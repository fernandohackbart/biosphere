package org.biosphere.tissue.blockchain;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.bouncycastle.util.encoders.Base64;

public class FlatBlock {
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
	 * Payload of the Block
	 */
	private String payload;

	/**
	 * Hash of this block prevHash+":"+payload
	 */
	private String blockHash;

	/**
	 * Position of this block in the chain
	 */
	private int chainPosition;

	public final String getBlockID() {
		return blockID;
	}

	public final void setBlockID(String blockID) {
		this.blockID = blockID;
	}

	public final Date getTimestamp() {
		return timestamp;
	}

	public final void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public final String getCellID() {
		return cellID;
	}

	public final void setCellID(String cellID) {
		this.cellID = cellID;
	}

	public final String getCellSignature() {
		return cellSignature;
	}

	public final void setCellSignature(String cellSignature) {
		this.cellSignature = cellSignature;
	}

	public final String getPrevHash() {
		return prevHash;
	}

	public final void setPrevHash(String prevHash) {
		this.prevHash = prevHash;
	}

	public final String getPrevBlockID() {
		return prevBlockID;
	}

	public final void setPrevBlockID(String prevBlockID) {
		this.prevBlockID = prevBlockID;
	}

	public final String getPayload() {
		return payload;
	}

	public final void setPayload(String payload) {
		this.payload = payload;
	}

	public final String getBlockHash() {
		return blockHash;
	}

	public final void setBlockHash(String blockHash) {
		this.blockHash = blockHash;
	}

	public final int getChainPosition() {
		return chainPosition;
	}

	public final void setChainPosition(int chainPosition) {
		this.chainPosition = chainPosition;
	}

	/**
	 * Exposes the block in flat (JSON or XML) format
	 * 
	 * @return flat representation of the block
	 */
	public final String toColonString() {
		return getChainPosition() + ":" + getTimestamp().getTime() + ":" + getCellID() + ":" + getPrevBlockID() + ":"
				+ getBlockID() + ":" + getPrevHash() + ":" + getBlockHash() + ":"
				+ Base64.toBase64String(getPayload().getBytes(StandardCharsets.UTF_8)) + ":" + getCellSignature();
	}
}
