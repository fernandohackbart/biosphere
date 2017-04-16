package org.biosphere.tissue.protocol;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlatChain {

	public FlatChain() {
		super();
		blocks = new ArrayList<FlatBlock>();
	}

	@JsonProperty("blocks")
	ArrayList<FlatBlock> blocks;
	
	@JsonProperty("blocks")
	public final ArrayList<FlatBlock> getBlocks() {
		return blocks;
	}
	@JsonProperty("blocks")
	public final void setBlocks(ArrayList<FlatBlock> blocks) {
		this.blocks = blocks;
	}

	public final void addBlock(FlatBlock block) {
		this.blocks.add(block);
	}
}
