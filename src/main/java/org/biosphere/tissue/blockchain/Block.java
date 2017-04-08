package org.biosphere.tissue.blockchain;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import org.biosphere.tissue.utils.Logger;

import org.bouncycastle.util.encoders.Base64;

public class Block {
	/**
	 * ID of this Block in this JVM generated using UUID
	 */
	private String blockID;

	/**
	 * Timestamp of the Block creation in the local JVM
	 */
	private Date timestamp;

	/**
	 * ID of the cell that created the Block
	 */
	private String cellID;

	/**
	 * Signature of the cell that created the Block
	 */
	private byte[] cellSignature;

	/**
	 * Previous Block SHA-256 hash
	 */
	private String prevHash;

	/**
	 * Previous block ID
	 */
	private String prevBlockID;

	/**
	 * Payload of the Block
	 */
	private String payload;

	/**
	 * Hash of this block prevHash+":"+payload
	 */
	private String blockHash;

	/**
	 * ID of the next block in the chain
	 */
	private ArrayList<String> nextBlockIDs;

	/**
	 * Position of this block in the chain
	 */
	private int chainPosition;

	/**
	 * List of acceptance votes
	 */
	private ArrayList<Vote> acceptanceVotes;

	/**
	 * The chain that this Block belongs to
	 */
	private Chain chain;

	/**
	 * Block logger
	 * 
	 */
	private Logger logger;

	/**
	 * Creates one instance of e Block based on one cell identifier and a flat
	 * String
	 * <p>
	 * It is mainly intended to be used in the interactions between Cells, when
	 * propagating the local append of one block to the chain.
	 * 
	 * @param CellId
	 *            the unique identified of the Cell that added the Block to the
	 *            chain
	 * @param flatBlock
	 *            the flat String that represents the Block content
	 */
	Block(String flatBlock, Chain chain) throws BlockException {
		super();
		logger = new Logger();
		logger.debug("Block.Block()", "Creating new Block: cell(" + flatBlock.split(":")[1] + ") prev block ID("
				+ flatBlock.split(":")[2] + ") block ID(" + flatBlock.split(":")[3] + ")");
		setChainPosition(Integer.parseInt(flatBlock.split(":")[0]));
		setTimestamp(new Date(Long.parseLong(flatBlock.split(":")[1])));
		setCellID(flatBlock.split(":")[2]);
		setPrevBlockID(flatBlock.split(":")[3]);
		setBlockID(flatBlock.split(":")[4]);
		setPrevHash(flatBlock.split(":")[5]);
		setBlockHash(flatBlock.split(":")[6]);
		setPayload(new String(Base64.decode(flatBlock.split(":")[7])));
		setChain(chain);
		setAcceptanceVotes(new ArrayList<Vote>());
		setNextBlockIDs(new ArrayList<String>());
		if (!getBlockID().equals("GENESIS")) {
			chain.getBlock(getPrevBlockID()).addNextBlockID(getBlockID());
		}
		if (!isValid()) {
			throw new BlockException("Block ID(" + getBlockID() + ") is not valid!");
		}
	}

	/**
	 * Creates one empty instance of e Block
	 * <p>
	 * It is mainly intended to be used while adding a Cell to the chain
	 * locally.
	 * 
	 * @param CellId
	 *            the unique identified of the Cell that added the Block to the
	 *            chain, mostly the local Cell.
	 */
	Block(String cellID, String payload, String prevBlockID, Chain chain, boolean genesis) throws BlockException {
		super();
		logger = new Logger();
		logger.debug("Block.Block()",
				"Creating new Block:" + cellID + ":" + payload + ":" + prevBlockID + ":" + genesis);
		setChain(chain);
		setAcceptanceVotes(new ArrayList<Vote>());
		setNextBlockIDs(new ArrayList<String>());
		setCellID(cellID);
		setPrevBlockID(prevBlockID);
		if (genesis) {
			setBlockID("GENESIS");
			setPrevHash("GENESIS");
			setChainPosition(0);
		} else {
			setBlockID(UUID.randomUUID().toString());
			setPrevHash(chain.getBlock(prevBlockID).getBlockHash());
			setChainPosition(chain.getBlock(prevBlockID).getChainPosition() + 1);
			chain.getBlock(getPrevBlockID()).addNextBlockID(getBlockID());
		}
		setPayload(payload);
		setBlockHash(calculateBlockHash());
		setTimestamp(new Date());
	}

	/**
	 * Set the ArrayList<Vote> of the Block acceptance votes
	 * 
	 * @param acceptanceVotes
	 *            ArrayList<Vote>
	 */
	private final void setAcceptanceVotes(ArrayList<Vote> acceptanceVotes) {
		this.acceptanceVotes = acceptanceVotes;
	}

	/**
	 * Return the ArrayList<Vote> of acceptance votes
	 * 
	 * @return ArrayList<Vote> of acceptance votes
	 */
	final ArrayList<Vote> getAcceptanceVotes() {
		return acceptanceVotes;
	}

	/**
	 * Add a vote from a cell
	 * 
	 * @param vote
	 */
	public synchronized void addVote(Vote vote) {
		if (!acceptanceVotes.contains(vote)) {
			logger.debug("Block.addVote()", "Adding vote: " + vote.getCellID() + " voted: (" + vote.isAccepted()
					+ ") for block " + getBlockID());
			acceptanceVotes.add(vote);
		} else {
			logger.debug("Block.addVote()",
					"Adding vote: " + vote.getCellID() + " already voted for block " + getBlockID());
		}
	}

	/**
	 * Return true if the parameter cell has voted for this block
	 * 
	 * @param cellName
	 *            Name of the cell to be checked
	 * @return true if cell has voted
	 */
	public boolean cellVoted(String cellName) {
		boolean voted = false;
		Iterator accIt = acceptanceVotes.listIterator();
		while (accIt.hasNext()) {
			if (((Vote) accIt.next()).getCellID().equals(cellName)) {
				voted = true;
			}
			break;
		}
		logger.debug("Block.cellVoted()", "Cell: " + cellName + " vote present: " + voted);
		return voted;
	}

	/**
	 * Return the acceptance of the block base on the votes
	 * 
	 * @return
	 */
	public boolean isAccepted(int tissueSize) {
		int totalVotes = acceptanceVotes.size();
		logger.debug("Block.accepted()", "Block: " + getBlockID() + " total votes " + totalVotes);
		int totalAccepted = 0;
		boolean acceptance = false;
		if (!getBlockID().equals("GENESIS")) {
			if (totalVotes > (tissueSize / 2)) {
				Iterator itAccept = acceptanceVotes.iterator();
				while (itAccept.hasNext()) {
					Vote tmpVote = (Vote) itAccept.next();
					if (tmpVote.isAccepted()) {
						totalAccepted++;
					}
				}
				logger.debug("Block.accepted()", "Block: " + getBlockID() + " total accepted votes " + totalAccepted);
				if (totalAccepted > (totalVotes / 2)) {
					acceptance = true;
				}
			} else {
				logger.debug("Block.accepted()", "Block: " + getBlockID() + " no enought votes " + totalVotes);
			}
		} else {
			logger.debug("Block.accepted()", "Block: " + getBlockID() + " always accepted.");
			acceptance = true;
		}
		logger.debug("Block.accepted()", "Block: " + getBlockID() + " accepted: " + acceptance);
		return acceptance;
	}

	/**
	 * Return the acceptance rate of the block base on the votes
	 * 
	 * @return
	 */
	public long acceptanceRate() {
		long acceptangeRate = 0;
		int totalVotes = acceptanceVotes.size();
		logger.debug("Block.acceptanceRate()", "Block: " + getBlockID() + " total votes " + totalVotes);
		int totalAccepted = 0;
		Iterator itAccept = acceptanceVotes.iterator();
		while (itAccept.hasNext()) {
			Vote tmpVote = (Vote) itAccept.next();
			if (tmpVote.isAccepted()) {
				totalAccepted++;
			}
		}
		logger.debug("Block.acceptanceRate()", "Block: " + getBlockID() + " total accepted votes " + totalAccepted);
		acceptangeRate = ((totalAccepted * 100) / totalVotes);
		logger.debug("Block.acceptanceRate()", "Block: " + getBlockID() + " accepted rate " + acceptangeRate + " %");
		return acceptangeRate;
	}

	/**
	 * Returns the block ID assigned to the block when originally created
	 * 
	 * @return Block ID if this block
	 */
	final String getBlockID() {
		return blockID;
	}

	/**
	 * Defines the creation timestamp for this Block
	 * 
	 * @param timestamp
	 *            Date representing the Block creation timestamp
	 */
	final void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Returns the creation timestamp of this block
	 * 
	 * @return Creation timestamp of this block
	 */
	final Date getTimestamp() {
		return timestamp;
	}

	/**
	 * Defines the Cell ID of this Block, should only be called when creating
	 * the Block
	 * 
	 * @param cellId
	 */
	final void setCellID(String cellID) {
		this.cellID = cellID;
	}

	/**
	 * Returns the Cell ID of this Block
	 * 
	 * @return CellID of this Block
	 */
	final String getCellID() {
		return cellID;
	}

	/**
	 * Defines the prevHash of this Block
	 * 
	 * @param prevHash
	 *            The previouos Block Hash to be set in the prevHash attribute
	 */
	final void setPrevHash(String prevHash) {
		this.prevHash = prevHash;
	}

	/**
	 * Returns the prevHash of this Block
	 *
	 * @return PrevHash of this Block
	 */
	final String getPrevHash() {
		return prevHash;
	}

	/**
	 * Defines the payload of this block
	 * 
	 * @param payload
	 *            The payload to be defined
	 */
	final void setPayload(String payload) throws BlockException {
		if (payload != null || payload.equals("")) {
			this.payload = payload;
		} else {
			throw new BlockException("Payload cannot be null!");
		}

	}

	/**
	 * Return the payload of the block
	 * 
	 * @return payload of the block
	 */
	final String getPayload() {
		return payload;
	}

	/**
	 * Exposes the block in flat (JSON or XML) format
	 * 
	 * @return flat representation of the block
	 */
	String toFlat() {
		return getChainPosition() + ":" + getTimestamp().getTime() + ":" + getCellID() + ":" + getPrevBlockID() + ":"
				+ getBlockID() + ":" + getPrevHash() + ":" + getBlockHash() + ":"
				+ Base64.toBase64String(getPayload().getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Calculates the hash of this block and set the blockHash attribute
	 */
	final void setBlockHash(String digestHexa) {
		blockHash = digestHexa;
	}

	/**
	 * Return the hash of this block
	 *
	 * @return the hash of this block
	 */
	final String getBlockHash() {
		return blockHash;
	}

	/**
	 * Return the hash of the block
	 * 
	 * @return the hash of the block
	 */
	final String calculateBlockHash() throws BlockException {
		String digestHexa = null;
		try {
			if (prevHash != null) {
				String hashInput = prevHash + ":" + payload;
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				md.update(hashInput.getBytes(StandardCharsets.UTF_8));
				byte[] digest = digest = md.digest();
				digestHexa = String.format("%064x", new java.math.BigInteger(1, digest));
			} else {
				logger.error("Block.calculateBlockHash()", "Previous hash is empty");
			}
		} catch (NoSuchAlgorithmException e) {
			ChainExceptionHandler.handleUnrecoverableGenericException(e, "Block.calculateBlockHash()",
					"NoSuchAlgorithmException.");
		}
		return digestHexa;
	}

	/**
	 * Set the Previous Block ID for this block
	 * 
	 * @param prevBlockId
	 *            The ID of the block that is the previous for this block in the
	 *            chain
	 */
	final void setPrevBlockID(String prevBlockID) {
		this.prevBlockID = prevBlockID;
	}

	/**
	 * Return the previous block ID for this block
	 *
	 * @return the previous block ID for this block
	 */
	final String getPrevBlockID() {
		return this.prevBlockID;
	}

	/**
	 * Defines the Block ID for this Block
	 * 
	 * @param blockId
	 *            String representing this Block ID
	 */
	void setBlockID(String blockID) {
		this.blockID = blockID;
	}

	/**
	 * Validates the Block if it is consistent
	 * 
	 * @return boolean value fo rthe question "block validated?"
	 */
	boolean isValid() throws BlockException {
		boolean valid = true;
		if (!calculateBlockHash().equals(getBlockHash())) {
			// throw new BlockException("The block hash does not match the
			// prevBlockHash+payload calculation");
			valid = false;
		}
		// TODO check for other required validations
		return valid;
	}

	/**
	 * Set the chain that this block belongs to
	 * 
	 * @param chain
	 *            The chain that this block belongs to
	 */
	private final void setChain(Chain chain) {
		this.chain = chain;
	}

	/**
	 * Set the chain position for this block
	 * 
	 * @param chainPosition
	 */
	final void setChainPosition(int chainPosition) {
		this.chainPosition = chainPosition;
	}

	/**
	 * Return the chain position for this block
	 * 
	 * @return
	 */
	final int getChainPosition() {
		return chainPosition;
	}

	/**
	 * Set the ID of the next block for this block in the chain
	 * 
	 * @param nextBlockID
	 *            the id of the next block
	 */
	final void addNextBlockID(String nextBlockID) throws BlockException {
		if ((nextBlockID != null) && (!nextBlockID.equals("null"))) {
			if (!nextBlockID.equals(getBlockID())) {
				if (!nextBlockIDs.contains(nextBlockID)) {
					nextBlockIDs.add(nextBlockID);
				}
			}
		}
	}

	/**
	 * Set the next block IDs array list (used during the instantiation of a new
	 * block)
	 * 
	 * @param nextBlockIDs
	 */
	private final void setNextBlockIDs(ArrayList<String> nextBlockIDs) {
		this.nextBlockIDs = nextBlockIDs;
	}

	/**
	 * Return the array list of the next block IDs for this block
	 * 
	 * @return ArrayList<String> if the next block IDs
	 */
	final ArrayList<String> getNextBlockIDs() {
		return nextBlockIDs;
	}

	/**
	 * Sets the cell signature of this block
	 * 
	 * @param cellSignature
	 */
	private final void setCellSignature(byte[] cellSignature) {
		this.cellSignature = cellSignature;
	}

	/**
	 * Check if the signature is valid for the known provided public key
	 * 
	 */
	final void generateCellSignature(byte[] cellPrivKey) {
		setCellSignature("".getBytes());
	}

	/**
	 * Returns the cell signature for this block
	 *
	 * @return
	 */
	public final byte[] getCellSignature() {
		return cellSignature;
	}

	/**
	 * Check if the signature is valid for the known provided public key
	 * 
	 * @return
	 */
	public final byte[] checkCellSignature(String cellPublicKey) {
		return cellSignature;
	}
}
