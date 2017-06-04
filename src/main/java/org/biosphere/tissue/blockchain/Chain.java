package org.biosphere.tissue.blockchain;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.protocol.BlockAddRequest;
import org.biosphere.tissue.protocol.BlockAddResponse;
import org.biosphere.tissue.protocol.CellInterface;
import org.biosphere.tissue.protocol.BlockAppendRequest;
import org.biosphere.tissue.protocol.FlatBlock;
import org.biosphere.tissue.protocol.FlatChain;
import org.biosphere.tissue.tissue.TissueManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Chain {
	/**
	 * Create one new instance of the Chain class
	 *
	 * @param cellID
	 *            the current Cell where this Chain is being instantiated
	 */
	public Chain(String cellID, Cell cell) throws BlockException {
		super();
		logger = LoggerFactory.getLogger(Chain.class);
		setCell(cell);
		chain = new Hashtable<String, Block>();
		Block genesisBlock = new Block(getCell(), "GENESIS", "GENESIS", "GENESIS", this, true);
		addBlockToChain(genesisBlock);
	}

	/**
	 * Create one instance of the Chain class
	 *
	 * @param cellID
	 *            the current Cell where this Chain is being instantiated
	 * @param existingChain
	 *            the String that represents the Chain being joined to (JSON or
	 *            XML)
	 * @param currentBlockID
	 *            the current Block ID in the Chain being joined to
	 */
	public Chain(String cellID, Cell cell, FlatChain flatChain) throws BlockException {
		super();
		logger = LoggerFactory.getLogger(Chain.class);
		setCell(cell);
		chain = new Hashtable<String, Block>();
		parseChain(flatChain);
	}

	/**
	 * The cell which the Chain belongs to
	 */
	private Cell cell;

	/**
	 * The hash table that holds the chain
	 */
	private Hashtable<String, Block> chain;

	/**
	 * Block logger
	 *
	 */
	private Logger logger;

	/**
	 * Return the Block identified by the BlockID parameter
	 *
	 * @param BlockID
	 * @return the Block with the provided BlockID
	 */
	Block getBlock(String blockID) {
		return chain.get(blockID);
	}

	/**
	 * Remove the provided block from the chain
	 * 
	 * @param blockID
	 */
	void removeBlock(String blockID) {
		chain.remove(blockID);
	}

	/**
	 * Set the cell this Chain belongs to
	 *
	 * @param cell
	 *            The cell this Chain belongs to
	 */
	protected final void setCell(Cell cell) {
		this.cell = cell;
	}

	/**
	 * Return the cell this chain belongs to
	 *
	 * @return Cell the cell this Chain belongs to
	 */
	protected final Cell getCell() {
		return cell;
	}

	/**
	 * Return the ID of the current block
	 *
	 * @return String the current block ID
	 */
	public final String getNextBlockID() {
		int tissueSize = getCell().getDna().getTissueSize();
		int highestPosition = getBlock("GENESIS").getChainPosition();
		String nextBlockID = "GENESIS";
		ArrayList<String> candidates = new ArrayList<String>();

		// iterate over the chain and search the accepted block with the highest
		// position
		// keep the high position ?
		Enumeration<String> blockKeys = chain.keys();
		while (blockKeys.hasMoreElements()) {
			Block block = getBlock((String) blockKeys.nextElement());
			logger.trace("Chain.getNextBlockID() Checking block ID(" + block.getBlockID() + ") POSITION(" + block.getChainPosition() + ")");
			if (block.isAccepted(tissueSize)) {
				if ((block.getChainPosition() > highestPosition)) {
					logger.trace("Chain.getNextBlockID() Found new high position (" + block.getChainPosition() + ") block ID(" + block.getBlockID() + ")");
					candidates = new ArrayList<String>();
					candidates.add(block.getBlockID());
					highestPosition = block.getChainPosition();
				}
				if ((block.getChainPosition() == highestPosition)) {
					logger.trace("Chain.getNextBlockID() Found another high position (" + block.getChainPosition() + ") block ID(" + block.getBlockID() + ")");
					candidates.add(block.getBlockID());
				}
			}
		}
		if (candidates.size() > 1) {
			boolean nextBlockIDSet = false;
			for (String candidate : candidates) {
				if (!nextBlockIDSet) {
					logger.trace("Chain.getNextBlockID() Choosing first block ID(" + candidate + ")");
					nextBlockID = candidate;
					nextBlockIDSet = true;
				} else {
					if (getBlock(candidate).getTimestamp().before(getBlock(nextBlockID).getTimestamp())) {
						logger.trace("Chain.getNextBlockID() Block ID(" + candidate + ") is older than ID(" + candidate + "), replacing.");
						nextBlockID = candidate;
					}
				}
			}
		} else {
			nextBlockID = candidates.get(0);
		}
		logger.trace("Chain.getNextBlockID() NEXT BLOCK ID (" + nextBlockID + ")");
		return nextBlockID;
	}

	/**
	 * Returns the current block for this ChainManager
	 *
	 * @return the instance of the current block
	 */
	public Block getNextBlock() {
		return chain.get(getNextBlockID());
	}

	/**
	 * Add a block to the chain, to be called by clients
	 *
	 * @param payload
	 *            the payload of the block to be added
	 */
	public synchronized BlockAddResponse addBlock(BlockAddRequest blockAddRequest) throws BlockException {
		logger.debug("Chain.addBlock() Adding block with payload:" + blockAddRequest.getPayload());
		Block nextBlock = getNextBlock();
		logger.debug("Chain.addBlock() Adding block with nextBlockID: (" + nextBlock.getBlockID() + ")");
		Block newBlock = new Block(getCell(), blockAddRequest.getTitle(), blockAddRequest.getPayload(), nextBlock.getBlockID(), this, false);
		logger.debug("Chain.addBlock() Block (" + newBlock.getBlockID() + ") extending block (" + nextBlock.getBlockID() + ")!");
		boolean accepted = appendBlock(newBlock, getCell().getCellName(), true, blockAddRequest.isEnsureAcceptance());
		// newBlock.executePayload(cell);
		BlockAddResponse bar = new BlockAddResponse();
		bar.setAccepted(accepted);
		bar.setCellName(getCell().getCellName());
		bar.setBlockID(newBlock.getBlockID());
		return bar;
	}

	/**
	 * Add a block to the local Hashtable<String, Block> chain
	 *
	 * @param block
	 *            the block to be added
	 * @return true if the block was added (not already existing in the
	 *         Hashtable)
	 */
	private synchronized boolean addBlockToChain(Block block) {
		boolean blockAdded = false;
		if (!chain.containsKey(block.getBlockID())) {
			if ((chain.containsKey(block.getPrevBlockID()))) {
				logger.debug("Chain.addBlockToChain() Adding block (" + block.getBlockID() + ") to chain");
				chain.put(block.getBlockID(), block);
				blockAdded = true;
			} else {
				if (block.getBlockID().equals("GENESIS")) {
					logger.debug("Chain.addBlockToChain() Adding GENESIS block (" + block.getBlockID() + ") to chain");
					chain.put(block.getBlockID(), block);
					blockAdded = true;
				} else {
					logger.error("Chain.addBlockToChain() Block (" + block.getBlockID() + ") previous block (" + block.getPrevBlockID() + ") not present in the chain!");
				}
			}
		} else {
			logger.debug("Chain.addBlockToChain() Block (" + block.getBlockID() + ") already exists in the chain!");
		}
		return blockAdded;
	}

	/**
	 * Appends a block to the chain
	 *
	 * @param flatBlock
	 *            the block to be appended to the chain
	 * @return boolean true if the block was accepted
	 */
	public synchronized boolean appendBlock(BlockAppendRequest flatBlock) throws ChainException {
		boolean accepted = false;
		try {
			Block block = null;
			if (!chain.containsKey(flatBlock.getBlockID())) {
				logger.debug("Chain.appendBlock(BlockAppendRequest) Block (" + flatBlock.getBlockID() + ") does not exists in the chain, creating!");
				block = new Block(flatBlock, this, getCell());
			} else {
				logger.debug("Chain.appendBlock(BlockAppendRequest) Block (" + flatBlock.getBlockID() + ") already exists in the chain, adding votes!");
				block = getBlock(flatBlock.getBlockID());
			}
			accepted = appendBlock(block, flatBlock.getNotifyingCell(), flatBlock.isAccepted(), flatBlock.isEnsureAcceptance());
		} catch (BlockException e) {
			ChainExceptionHandler.handleGenericException(e, "Chain.appendBlock(BlockAppendRequest)", "Failed to create new block instance.");
		}
		return accepted;
	}

	/**
	 * Append a block to the chain, to be called remotely from one cell to
	 * another Requires the block serialization using String (JSON or XML)
	 *
	 * @param block
	 *            String representing the block in String (JSON or XML) format
	 * @return boolean value if the block was accepted or not to be pending in
	 *         the chain
	 */
	private boolean appendBlock(Block block, String notifyingCell, boolean notifyingCellAccepted, boolean ensureAcceptance) {
		logger.trace("Chain.appendBlock(Block) block(" + block.getBlockID() + ")");
		boolean accepted = true;
		try {
			if (block.isValid(getCell())) {
				logger.trace("Chain.appendBlock(Block) Block (" + block.getBlockID() + ") is valid, adding to the chain!");
				if (addBlockToChain(block)) {
					logger.debug("Chain.appendBlock(Block) Block (" + block.getBlockID() + ") added to the chain!");
				} else {
					logger.debug("Chain.appendBlock(Block) Block (" + block.getBlockID() + ") already present in the chain!");
				}
				requestVotes(block, accepted, notifyingCell, notifyingCellAccepted, ensureAcceptance);
				if (ensureAcceptance) {
					logger.debug("Chain.appendBlock(Block) ensureAcceptance = " + ensureAcceptance + " waiting for acceptance!");
					boolean keepWaiting = true;
					long timeout = System.currentTimeMillis() + TissueManager.acceptanceTimeout;
					logger.trace("Chain.appendBlock(Block) Will wait until " + new Date(timeout) + " for acceptance of Block (" + block.getBlockID() + ")");
					while (keepWaiting) {
						if (block.getVotesCount() > (getCell().getDna().getTissueSize() / 2)) {
							accepted = block.isAccepted(getCell().getDna().getTissueSize());
							logger.debug("Chain.appendBlock(Block) Acceptance for Block (" + block.getBlockID() + ") = " + accepted);
							keepWaiting = false;
						}
						if (System.currentTimeMillis() > timeout) {
							logger.warn("Chain.appendBlock(Block) Timeout waiting for acceptance for Block (" + block.getBlockID() + ")");
							keepWaiting = false;
						}
						if (keepWaiting) {
							logger.trace("Chain.appendBlock(Block) Waiting for acceptance for Block (" + block.getBlockID() + ") for more " + TissueManager.acceptanceInterval + " seconds");
							Thread.sleep(TissueManager.acceptanceInterval);
						}
					}
				} else {
					logger.trace("Chain.appendBlock(Block) ensureAcceptance = " + ensureAcceptance + ", NOT waiting for acceptance!");
				}
				if (accepted) {
					logger.trace("Chain.appendBlock(Block) Block (" + block.getBlockID() + ") accepted, executing payload!");
					block.executePayload(cell);
				}

			} else {
				logger.debug("Chain.appendBlock(Block) Block ID(" + block.getBlockID() + ") not valid, rejecting...");
				accepted = false;
			}
		} catch (BlockException e) {
			accepted = false;
			ChainExceptionHandler.handleGenericException(e, "Chain.appendBlock(Block)", "Failed to create new block instance (BlockException).");
		} catch (Exception e) {
			accepted = false;
			ChainExceptionHandler.handleGenericException(e, "Chain.appendBlock(Block)", "Failed to create new block instance (Exception).");
		}
		return accepted;
	}

	/**
	 * Notify all the cells in the tissue calling /appendBlock
	 *
	 * @param block
	 *            The block to be appended to the chain
	 * @return accepted Boolean in case more than 50% of the contacted cells
	 *         accept the block
	 */
	private void requestVotes(Block block, boolean accepted, String notifyingCell, boolean notifyingCellAccepted, boolean ensureAcceptance) {
		if (!block.cellVoted(getCell().getCellName())) {
			logger.debug("Chain.requestVotes() Adding local cell (" + getCell().getCellName() + ") vote (" + accepted + ") for block (" + block.getBlockID() + ")");
			block.addVote(new Vote(getCell().getCellName(), accepted));
		}
		if (!block.cellVoted(notifyingCell)) {
			logger.debug("Chain.requestVotes() Adding remote cell (" + notifyingCell + ") vote (" + notifyingCellAccepted + ") for block " + block.getBlockID() + ")");
			block.addVote(new Vote(notifyingCell, notifyingCellAccepted));
		}
		if (block.getCellID().equals(notifyingCell)) {
			logger.debug("Chain.requestVotes() Sending consensus vote (" + accepted + ") for block (" + block.getBlockID() + ") to the tissue");
			logger.debug("Chain.requestVotes() Getting the list of the cells from the DNA");
			List<CellInterface> cellIFs = getCell().getDna().getTissueCellsInterfaces();
			Iterator<CellInterface> cellIFIterator = cellIFs.iterator();
			while (cellIFIterator.hasNext()) {
				CellInterface cellInterface = cellIFIterator.next();
				if ((!cellInterface.getCellName().equals(getCell().getCellName())) && (!cellInterface.getCellName().equals(block.getCellID()))) {
					logger.debug("Chain.requestVotes() Cell (" + cellInterface.getCellName() + ") elegible for notification ");
					if (!block.cellVoted(cellInterface.getCellName())) {
						logger.debug("Chain.requestVotes() Notifying cell (" + cellInterface.getCellName() + ") " + cellInterface.getCellNetworkName() + ":" + cellInterface.getPort());
						ChainNotifier cnc = new ChainNotifier(cellInterface.getCellNetworkName(), cellInterface.getPort(), block, cellInterface.getCellName(), getCell().getCellName(), accepted, ensureAcceptance);
						Thread thread = new Thread(cnc);
						thread.setName("ChainNotifyCell-Block(" + block.getBlockID() + ")-Cell(" + cellInterface.getCellName() + ")");
						thread.start();
					} else {
						logger.debug("Chain.requestVotes() Cell (" + cellInterface.getCellName() + ") already voted, skipping...");
					}
				} else {
					if ((cellInterface.getCellName().equals(getCell().getCellName()))) {
						logger.debug("Chain.requestVotes() Cell " + cellInterface.getCellName() + " NOT elegible for notification as it is local (" + getCell().getCellName() + ")");
					}
					if ((cellInterface.getCellName().equals(block.getCellID()))) {
						logger.debug("Chain.requestVotes() Cell " + cellInterface.getCellName() + " NOT elegible for notification as it is creator (" + getCell().getCellName() + ")");
					}
					if ((cellInterface.getCellName().equals(notifyingCell))) {
						logger.debug("Chain.requestVotes() Cell " + cellInterface.getCellName() + " NOT elegible for notification as it is notifyingCell (" + notifyingCell + ")");
					}
				}
			}
		}
		// TODO enable the cell death detection based on the timeout
		// TODO the cell death algorithm should use also consensus to ensure one
		// cell is dead
	}

	/**
	 * Create a chain from a String (JSON or XML) pay load
	 *
	 * @param flatChain
	 *            the String (JSON or XML) to be converted in to a Hash table
	 * @return the chain hash table
	 */
	private void parseChain(FlatChain flatChain) throws BlockException {
		logger.debug("Chain.parseChain() Parsing flatChain");
		for (FlatBlock flatBlock : flatChain.getBlocks()) {
			Block tmpBlock = new Block(flatBlock, this, getCell());
			logger.debug("Chain.parseChain() Adding block ID: " + tmpBlock.getBlockID());
			if (addBlockToChain(tmpBlock)) {
				tmpBlock.executePayload(cell);
			}
		}
		logger.debug("Chain.parseChain() chain size after parse: " + chain.size());
	}

	/**
	 * Return a flat (JSON or XML) representation of the chain in its current
	 * state
	 *
	 * @return flat String of the Chain
	 * @throws JsonProcessingException
	 */
	public String toJSON() throws JsonProcessingException {
		logger.trace("Chain.toJSON() Returning flat chain!");
		FlatChain flatChain = new FlatChain();
		blockToJSON("GENESIS", flatChain);
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(flatChain);
	}

	/**
	 * Get the proVided block ID's as flat and append to the StringBuffer output
	 * 
	 * @param blockID
	 *            the ID of the block to the converted to flat and added to the
	 *            output
	 * @param output
	 *            the output StringBuffer
	 */
	private void blockToJSON(String blockID, FlatChain flatChain) {
		if (getBlock(blockID) != null) {
			flatChain.addBlock(getBlock(blockID).getFlatBlock());
			logger.trace("Chain.blockToJSON() Adding block POS(" + getBlock(blockID).getChainPosition() + ") ID(" + blockID + ") TITLE(" + getBlock(blockID).getTitle() + ") to the flat chain!");
		} else {
			// TODO decide whenever to raise a exception
			logger.trace("Chain.blockToJSON() Block (" + blockID + ") not found in the chain!");
		}
		for (String nextBlockID : getBlock(blockID).getNextBlockIDs()) {
			blockToJSON(nextBlockID, flatChain);
		}
	}

	/**
	 * Returns a String with all blocks in the chain one by line
	 * 
	 * @return String with all blocks in the chain one by line
	 */
	public String dumpChain() {
		StringBuffer dumpChain = new StringBuffer();
		Enumeration<String> blockKeys = chain.keys();
		while (blockKeys.hasMoreElements()) {
			String blockID = blockKeys.nextElement();
			dumpChain.append("\nChain.dumpChain(" + getCell().getCellName() + ")  POS(" + getBlock(blockID).getChainPosition() + ") PREV(" + getBlock(blockID).getPrevBlockID() + ") ID(" + getBlock(blockID).getBlockID() + ") TITLE("
					+ getBlock(blockID).getTitle() + ")");
		}
		return dumpChain.toString();
	}
}
