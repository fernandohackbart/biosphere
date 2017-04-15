package org.biosphere.tissue.blockchain;

public interface BlockPayloadValidator {
	public boolean validate(FlatBlock nextBlock, FlatBlock[] chain);

}
