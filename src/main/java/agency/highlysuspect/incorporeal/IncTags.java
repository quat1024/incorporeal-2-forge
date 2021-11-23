package agency.highlysuspect.incorporeal;

import net.minecraft.world.level.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;

public class IncTags {
	public static final class Blocks {
		public static final Tag.Named<Block> OPEN_CRATES = BlockTags.bind(Inc.id("open_crates").toString());
	}
}
