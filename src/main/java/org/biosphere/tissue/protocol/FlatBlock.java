package org.biosphere.tissue.protocol;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.bouncycastle.util.encoders.Base64;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FlatBlock {
	/**
	 * ID of this Block in this JVM generated using UUID
	 */
	@JsonProperty("blockID")
	private String blockID;

	/**
	 * Timestamp of the Block creation in the local JVM
	 */
	@JsonProperty("timestamp")
	private Date timestamp;

	/**
	 * ID of the cell that created the Block
	 */
	@JsonProperty("cellID")
	private String cellID;

	/**
	 * Signature of the cell that created the Block
	 */
	@JsonProperty("cellSignature")
	private String cellSignature;

	/**
	 * Previous Block SHA-256 hash
	 */
	@JsonProperty("prevHash")
	private String prevHash;

	/**
	 * Previous block ID
	 */
	@JsonProperty("prevBlockID")
	private String prevBlockID;

	/**
	 * Payload of the Block
	 */
	@JsonProperty("payload")
	private String payload;

	/**
	 * Hash of this block prevHash+":"+payload
	 */
	@JsonProperty("blockHash")
	private String blockHash;

	/**
	 * Position of this block in the chain
	 */
	@JsonProperty("chainPosition")
	private int chainPosition;

	@JsonProperty("blockID")
	public final String getBlockID() {
		return blockID;
	}

	@JsonProperty("blockID")
	public final void setBlockID(String blockID) {
		this.blockID = blockID;
	}

	@JsonProperty("timestamp")
	public final Date getTimestamp() {
		return timestamp;
	}

	@JsonProperty("timestamp")
	public final void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@JsonProperty("cellID")
	public final String getCellID() {
		return cellID;
	}

	@JsonProperty("cellID")
	public final void setCellID(String cellID) {
		this.cellID = cellID;
	}

	@JsonProperty("cellSignature")
	public final String getCellSignature() {
		return cellSignature;
	}

	@JsonProperty("cellSignature")
	public final void setCellSignature(String cellSignature) {
		this.cellSignature = cellSignature;
	}

	@JsonProperty("prevHash")
	public final String getPrevHash() {
		return prevHash;
	}

	@JsonProperty("prevHash")
	public final void setPrevHash(String prevHash) {
		this.prevHash = prevHash;
	}

	@JsonProperty("prevBlockID")
	public final String getPrevBlockID() {
		return prevBlockID;
	}

	@JsonProperty("prevBlockID")
	public final void setPrevBlockID(String prevBlockID) {
		this.prevBlockID = prevBlockID;
	}

	@JsonProperty("payload")
	public final String getPayload() {
		return payload;
	}

	@JsonProperty("payload")
	public final void setPayload(String payload) {
		this.payload = payload;
	}

	@JsonProperty("blockHash")
	public final String getBlockHash() {
		return blockHash;
	}

	@JsonProperty("blockHash")
	public final void setBlockHash(String blockHash) {
		this.blockHash = blockHash;
	}

	@JsonProperty("chainPosition")
	public final int getChainPosition() {
		return chainPosition;
	}

	@JsonProperty("chainPosition")
	public final void setChainPosition(int chainPosition) {
		this.chainPosition = chainPosition;
	}

	public final String toJSON() throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(this);
	}
}
