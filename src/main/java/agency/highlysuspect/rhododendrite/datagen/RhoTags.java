package agency.highlysuspect.rhododendrite.datagen;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.WoodFamily;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class RhoTags {
	public static class BlockProvider extends BlockTagsProvider {
		public BlockProvider(DataGenerator generatorIn, @Nullable ExistingFileHelper existingFileHelper) {
			super(generatorIn, Rho.MODID, existingFileHelper);
		}
		
		@Override
		protected void addTags() {
			handleWoodFamily(RhoBlocks.RHODODENDRITE);
		}
		
		@SuppressWarnings("SameParameterValue")
		protected void handleWoodFamily(WoodFamily family) {
			tag(BlockTags.LOGS_THAT_BURN).add(family.log);
			tag(BlockTags.PLANKS).add(family.planks);
			tag(BlockTags.WOODEN_FENCES).add(family.fence);
			tag(BlockTags.WOODEN_BUTTONS).add(family.button);
			//getOrCreateBuilder(BlockTags.WOODEN_DOORS).add(family.door);
			tag(BlockTags.WOODEN_PRESSURE_PLATES).add(family.pressurePlate);
			tag(BlockTags.WOODEN_SLABS).add(family.slab);
			tag(BlockTags.WOODEN_STAIRS).add(family.stairs);
			//getOrCreateBuilder(BlockTags.WOODEN_TRAPDOORS).add(family.trapdoor);
			tag(BlockTags.LEAVES).add(family.leaves);
		}
	}
	
	public static class ItemProvider extends ItemTagsProvider {
		public ItemProvider(DataGenerator generatorIn, BlockTagsProvider block, @Nullable ExistingFileHelper existingFileHelper) {
			super(generatorIn, block, Rho.MODID, existingFileHelper);
		}
		
		@Override
		protected void addTags() {
			copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
			copy(BlockTags.PLANKS, ItemTags.PLANKS);
			copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
			copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
			//copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
			copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
			copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
			copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
			//copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
			copy(BlockTags.LEAVES, ItemTags.LEAVES);
		}
	}
}
