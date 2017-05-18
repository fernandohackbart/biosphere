package org.biosphere.tissue.blockchain;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import org.biosphere.tissue.Cell;
import org.biosphere.tissue.protocol.FlatBlock;
import org.biosphere.tissue.protocol.ServiceEnableRequest;
import org.biosphere.tissue.protocol.TissueAddCellPayload;
import org.biosphere.tissue.protocol.TissueOperationPayload;
import org.biosphere.tissue.protocol.TissueRemoveCellPayload;
import org.biosphere.tissue.tissue.TissueManager;
import org.biosphere.tissue.utils.CellSigner;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.encoders.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private String cellSignature;

	/**
	 * Previous Block SHA-256 hash
	 */
	private String prevHash;

	/**
	 * Previous block ID
	 */
	private String prevBlockID;

	/**
	 * Title of the Block
	 */
	private String title;

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
	// private Chain chain;

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
	Block(FlatBlock flatBlock, Chain chain, Cell cell) throws BlockException {
		super();
		logger = LoggerFactory.getLogger(Block.class);
		logger.debug("Block.Block() Creating new Block (flatBlock): cell(" + flatBlock.getCellID() + ") prev block ID(" + flatBlock.getPrevBlockID() + ") block ID(" + flatBlock.getBlockID() + ") TITLE(" + flatBlock.getTitle() + ")");
		setChainPosition(flatBlock.getChainPosition());
		setTimestamp(flatBlock.getTimestamp());
		setCellID(flatBlock.getCellID());
		setPrevBlockID(flatBlock.getPrevBlockID());
		setBlockID(flatBlock.getBlockID());
		setPrevHash(flatBlock.getPrevHash());
		setBlockHash(flatBlock.getBlockHash());
		setTitle(flatBlock.getTitle());
		setPayload(new String(Base64.decode(flatBlock.getPayload())));
		setCellSignature(flatBlock.getCellSignature());
		// setChain(chain);
		if (flatBlock.getAcceptanceVotes() != null) {
			logger.trace("Block.Block() Acceptance votes from flatBlock: " + flatBlock.getAcceptanceVotes().size());
			setAcceptanceVotes(flatBlock.getAcceptanceVotes());
		} else {
			logger.trace("Block.Block() No acceptance votes from flatBlock");
			setAcceptanceVotes(new ArrayList<Vote>());
		}
		setNextBlockIDs(new ArrayList<String>());
		if (!getBlockID().equals("GENESIS")) {
			chain.getBlock(getPrevBlockID()).addNextBlockID(getBlockID());
		}
		if (!isValid(cell)) {
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
	Block(Cell cell, String title, String payload, String prevBlockID, Chain chain, boolean genesis) throws BlockException {
		super();
		logger = LoggerFactory.getLogger(Block.class);
		String blockID = UUID.randomUUID().toString();
		logger.debug("Block.Block() Creating new Block (parameters): cell(" + cell.getCellName() + ") prev block ID(" + prevBlockID + ") block ID(" + blockID + ") TITLE(" + title + ") GENESIS(" + genesis + ")");
		// setChain(chain);
		setAcceptanceVotes(new ArrayList<Vote>());
		setNextBlockIDs(new ArrayList<String>());
		setCellID(cell.getCellName());
		setPrevBlockID(prevBlockID);
		if (genesis) {
			setBlockID("GENESIS");
			setPrevHash("GENESIS");
			setChainPosition(0);
		} else {
			setBlockID(blockID);
			setPrevHash(chain.getBlock(prevBlockID).getBlockHash());
			setChainPosition(chain.getBlock(prevBlockID).getChainPosition() + 1);
			chain.getBlock(getPrevBlockID()).addNextBlockID(getBlockID());
		}
		setTitle(title);
		setPayload(payload);
		generateCellSignature(cell);
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
		if (!cellVoted(vote.getCellID())) {
			logger.debug("Block.addVote() Adding vote block (" + getBlockID() + ") cell (" + vote.getCellID() + ") voted: " + vote.isAccepted() + " for block (" + getBlockID() + ")");
			acceptanceVotes.add(vote);
		} else {
			logger.debug("Block.addVote() Cell (" + vote.getCellID() + ") already voted for block (" + getBlockID() + ")");
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
		logger.trace("Block.cellVoted() Block (" + getBlockID() + ") vote count=" + getAcceptanceVotes().size());
		showVotes();
		for (Vote vote : getAcceptanceVotes()) {
			if (vote.getCellID().equals(cellName)) {
				voted = true;
				break;
			}
		}
		if (!voted) {
			logger.trace("Block.cellVoted() Cell (" + cellName + ") NOT voted for block (" + getBlockID() + ")");
		} else {
			logger.trace("Block.cellVoted() Cell (" + cellName + ") already voted for block (" + getBlockID() + ")");
		}
		return voted;
	}

	public void showVotes() {
		logger.trace("Block.showVotes() Block (" + getBlockID() + ") vote count=" + getAcceptanceVotes().size());
		for (Vote vote : getAcceptanceVotes()) {
			logger.trace("Block.showVotes() Cell (" + vote.getCellID() + ") vote (" + vote.isAccepted() + ")");
		}
	}

	/**
	 * Return the acceptance of the block base on the votes
	 * 
	 * @return
	 */
	public boolean isAccepted(int tissueSize) {
		int totalVotes = acceptanceVotes.size();
		logger.trace("Block.isAccepted() Block: " + getBlockID() + " total votes " + totalVotes + " tissue size " + tissueSize);
		int totalAccepted = 0;
		boolean acceptance = false;
		if (!getBlockID().equals("GENESIS")) {
			if (totalVotes >= (tissueSize / 2)) {
				Iterator<Vote> itAccept = acceptanceVotes.iterator();
				while (itAccept.hasNext()) {
					Vote tmpVote = itAccept.next();
					if (tmpVote.isAccepted()) {
						totalAccepted++;
					}
				}
				logger.trace("Block.isAccepted() Block: " + getBlockID() + " total accepted votes " + totalAccepted);
				if (totalAccepted > (totalVotes / 2)) {
					acceptance = true;
				}
			} else {
				logger.trace("Block.isAccepted() Block: " + getBlockID() + " no enought votes " + totalVotes);
			}
		} else {
			logger.trace("Block.isAccepted() Block: " + getBlockID() + " always accepted.");
			acceptance = true;
		}
		logger.trace("Block.isAccepted() Block: " + getBlockID() + " ACCEPTED: " + acceptance);
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
		logger.debug("Block.acceptanceRate() Block: " + getBlockID() + " total votes " + totalVotes);
		int totalAccepted = 0;
		Iterator<Vote> itAccept = acceptanceVotes.iterator();
		while (itAccept.hasNext()) {
			Vote tmpVote = itAccept.next();
			if (tmpVote.isAccepted()) {
				totalAccepted++;
			}
		}
		logger.debug("Block.acceptanceRate() Block: " + getBlockID() + " total accepted votes " + totalAccepted);
		acceptangeRate = ((totalAccepted * 100) / totalVotes);
		logger.debug("Block.acceptanceRate() Block: " + getBlockID() + " accepted rate " + acceptangeRate + " %");
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
		if (payload != null) {
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
	 * Generates a FlatBlock representation of the block
	 * 
	 * @return a FltBlock instance of the block
	 */
	public final FlatBlock getFlatBlock() {
		FlatBlock fb = new FlatBlock();
		fb.setBlockHash(getBlockHash());
		fb.setBlockID(getBlockID());
		fb.setCellID(getCellID());
		fb.setCellSignature(getCellSignature());
		fb.setChainPosition(getChainPosition());
		fb.setTitle(getTitle());
		fb.setPayload(Base64.toBase64String(getPayload().getBytes(StandardCharsets.UTF_8)));
		fb.setPrevBlockID(getPrevBlockID());
		fb.setPrevHash(getPrevHash());
		fb.setTimestamp(getTimestamp());
		fb.setAcceptanceVotes(getAcceptanceVotes());
		return fb;
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
				String hashInput = getPrevHash() + ":" + getPayload() + ":" + getCellSignature();
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				md.update(hashInput.getBytes(StandardCharsets.UTF_8));
				byte[] digest = md.digest();
				digestHexa = String.format("%064x", new java.math.BigInteger(1, digest));
			} else {
				logger.error("Block.calculateBlockHash() Previous hash is empty");
			}
		} catch (NoSuchAlgorithmException e) {
			ChainExceptionHandler.handleUnrecoverableGenericException(e, "Block.calculateBlockHash()", "NoSuchAlgorithmException.");
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
	 * Executes the commands into the payload
	 * 
	 * 
	 * @param cell
	 * @throws BlockException
	 */
	void executePayload(Cell cell) throws BlockException {
		try {
			logger.debug("Block.executePayload() Executing payload of block (" + getBlockID() + ")");
			ObjectMapper mapper = new ObjectMapper();
			if (!payload.equals("GENESIS")) {
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				TissueOperationPayload topl = mapper.readValue(Base64.decode(payload), TissueOperationPayload.class);
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
				switch (topl.getOperation()) {
				case TissueManager.TissueCellAddOperation:
					TissueAddCellPayload tacp = mapper.readValue(Base64.decode(payload), TissueAddCellPayload.class);
					logger.trace("Block.executePayload() Executing payload of block (" + getBlockID() + ") " + TissueManager.TissueCellAddOperation + " (" + tacp.getCell().getName() + ")");
					cell.getDna().appendCell(cell.getDna().getCellInstance(tacp.getCell().getName(), tacp.getCell().getPublicKey(), tacp.getCell().getInterfaces(), tacp.getCell().getTissuePort()), cell);
					break;
				case TissueManager.TissueCellRemoveOperation:
					TissueRemoveCellPayload trcp = mapper.readValue(Base64.decode(payload), TissueRemoveCellPayload.class);
					logger.trace("Block.executePayload() Executing payload of block (" + getBlockID() + ") " + TissueManager.TissueCellRemoveOperation + " (" + trcp.getToRemoveCell().getName() + ")");
					cell.getDna().deleteCell(trcp.getToRemoveCell());
					break;
				case TissueManager.TissueServiceEnableOperation:
					ServiceEnableRequest ser = mapper.readValue(Base64.decode(payload), ServiceEnableRequest.class);
					logger.trace("Block.executePayload() Executing payload of block (" + getBlockID() + ") " + TissueManager.TissueServiceEnableOperation + " service (" + ser.getServiceName() + ") enable (" + ser.isEnableService() + ")");
					if (ser.isEnableService()) {
						cell.getDna().enableService(ser, cell);
					} else {
						cell.getDna().disableService(ser, cell);
					}
					break;
				}
			}
		} catch (IOException e) {
			throw new BlockException("Failed to execute block payload: ", e);
		}
	}

	/**
	 * Validates the Block if it is consistent
	 * 
	 * @return boolean value for the question "block validated?"
	 */
	boolean isValid(Cell cell) throws BlockException {
		boolean valid = true;
		if (!calculateBlockHash().equals(getBlockHash())) {
			// throw new BlockException("The block hash does not match the
			// prevBlockHash+payload calculation");
			logger.debug("Block.isValid() Block: " + getBlockID() + " hashes does not match");
			valid = false;
		}
		try {
			if (!CellSigner.verify(getCellID(), cell, getCellSignature())) {
				logger.debug("Block.isValid() Block: " + getBlockID() + " signature does not match");
				valid = false;
			}
		} catch (CertificateException | OperatorCreationException | CMSException e) {
			throw new BlockException("Cell signature is not valid", e);
		}
		// BlockPayloadValidator bpv =
		// Class.forName("org.biosphere.tissue.blockchain.DefaultBlockPayloadValidator");
		// bpv.validate(nextBlock, chain)
		// TODO check for other required validations
		return valid;
	}

	/**
	 * Set the chain that this block belongs to
	 * 
	 * @param chain
	 *            The chain that this block belongs to
	 */
	/*
	 * private final void setChain(Chain chain) { this.chain = chain; }
	 */

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
	private final void setCellSignature(String cellSignature) {
		this.cellSignature = cellSignature;
	}

	/**
	 * Check if the signature is valid for the known provided public key
	 * 
	 * @throws BlockException
	 * 
	 */
	final void generateCellSignature(Cell cell) throws BlockException {
		try {
			setCellSignature(CellSigner.sign(cell));
		} catch (UnrecoverableKeyException | InvalidKeyException | CertificateEncodingException | KeyStoreException | NoSuchAlgorithmException | NoSuchProviderException | SignatureException | OperatorCreationException | CMSException
				| IOException e) {
			throw new BlockException("Execption generation cell signature ", e);
		}
	}

	/**
	 * Returns the cell signature for this block
	 *
	 * @return
	 */
	public final String getCellSignature() {
		return cellSignature;
	}

	/**
	 * Check if the signature is valid for the known provided public key
	 * 
	 * @return
	 */
	public final String checkCellSignature(String cellPublicKey) {
		return cellSignature;
	}

	/**
	 * Return the number of votes
	 * 
	 * @return number of votes
	 */
	public final int getVotesCount() {
		return acceptanceVotes.size();
	}

	public final String getTitle() {
		return title;
	}

	final void setTitle(String title) throws BlockException {
		if (title == null) {
			throw new BlockException("Block title could not be null!");
		} else {
			this.title = title;
		}

	}
}
