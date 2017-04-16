package org.biosphere.tissue.blockchain;

import org.biosphere.tissue.protocol.FlatBlock;

public interface BlockPayloadValidator {
	public boolean validate(FlatBlock nextBlock, FlatBlock[] chain);

}
