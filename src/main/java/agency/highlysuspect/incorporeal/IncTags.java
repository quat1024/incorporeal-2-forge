package agency.highlysuspect.incorporeal;

import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;

public class IncTags {
	public static final class Blocks {
		public static final ITag.INamedTag<Block> OPEN_CRATES = BlockTags.makeWrapperTag(Inc.id("open_crates").toString());
	}
}
