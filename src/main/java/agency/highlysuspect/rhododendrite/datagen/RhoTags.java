package agency.highlysuspect.rhododendrite.datagen;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.WoodFamily;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
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
		protected void registerTags() {
			handleWoodFamily(RhoBlocks.RHODODENDRITE);
		}
		
		@SuppressWarnings("SameParameterValue")
		protected void handleWoodFamily(WoodFamily family) {
			getOrCreateBuilder(BlockTags.LOGS_THAT_BURN).add(family.log);
			getOrCreateBuilder(BlockTags.PLANKS).add(family.planks);
			getOrCreateBuilder(BlockTags.WOODEN_FENCES).add(family.fence);
			getOrCreateBuilder(BlockTags.WOODEN_BUTTONS).add(family.button);
			getOrCreateBuilder(BlockTags.WOODEN_DOORS).add(family.door);
			getOrCreateBuilder(BlockTags.WOODEN_PRESSURE_PLATES).add(family.pressurePlate);
			getOrCreateBuilder(BlockTags.WOODEN_SLABS).add(family.slab);
			getOrCreateBuilder(BlockTags.WOODEN_STAIRS).add(family.stairs);
			getOrCreateBuilder(BlockTags.WOODEN_TRAPDOORS).add(family.trapdoor);
		}
	}
	
	public static class ItemProvider extends ItemTagsProvider {
		public ItemProvider(DataGenerator generatorIn, BlockTagsProvider block, @Nullable ExistingFileHelper existingFileHelper) {
			super(generatorIn, block, Rho.MODID, existingFileHelper);
		}
		
		@Override
		protected void registerTags() {
			copy(BlockTags.LOGS_THAT_BURN, ItemTags.LOGS_THAT_BURN);
			copy(BlockTags.PLANKS, ItemTags.PLANKS);
			copy(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
			copy(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
			copy(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
			copy(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
			copy(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
			copy(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
			copy(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
		}
	}
}
