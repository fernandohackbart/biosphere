package org.biosphere.tissue.protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.biosphere.tissue.protocol.FlatBlock;

public class BlockAppendRequest extends FlatBlock{
	
	@JsonProperty("notifyingCell")
	String notifyingCell;
	@JsonProperty("accepted")
	boolean accepted;
	@JsonProperty("ensureAcceptance")
	boolean ensureAcceptance;
	@JsonProperty("accepted")
	public final boolean isAccepted() {
		return accepted;
	}
	@JsonProperty("accepted")
	public final void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
	@JsonProperty("notifyingCell")
	public final String getNotifyingCell() {
		return notifyingCell;
	}
	@JsonProperty("notifyingCell")
	public final void setNotifyingCell(String notifyingCell) {
		this.notifyingCell = notifyingCell;
	}
	@JsonIgnore
	public final void setFlatBlock(FlatBlock flatBlock)
	{
		setBlockID(flatBlock.getBlockID());
		setTimestamp(flatBlock.getTimestamp());
        setCellID(flatBlock.getCellID());
        setCellSignature(flatBlock.getCellSignature());
        setPrevHash(flatBlock.getPrevHash());
        setPrevBlockID(flatBlock.getPrevBlockID());
        setTitle(flatBlock.getTitle());
        setPayload(flatBlock.getPayload());
        setBlockHash(flatBlock.getBlockHash());
        setChainPosition(flatBlock.getChainPosition());
	}
	@JsonProperty("ensureAcceptance")
	public final boolean isEnsureAcceptance() {
		return ensureAcceptance;
	}
	@JsonProperty("ensureAcceptance")
	public final void setEnsureAcceptance(boolean ensureAcceptance) {
		this.ensureAcceptance = ensureAcceptance;
	}

}
