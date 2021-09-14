package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncTags;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.block.Block;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import vazkii.botania.common.block.ModBlocks;
import vazkii.botania.common.lib.ModTags;

import javax.annotation.Nullable;

public class IncTagProvider {
	public static class BlockProvider extends BlockTagsProvider {
		public BlockProvider(DataGenerator generatorIn, @Nullable ExistingFileHelper existingFileHelper) {
			super(generatorIn, Inc.MODID, existingFileHelper);
		}
		
		@Override
		protected void registerTags() {
			getOrCreateBuilder(ModTags.Blocks.CORPOREA_SPARK_OVERRIDE).add(IncBlocks.RED_STRING_LIAR, IncBlocks.CORPOREA_SOUL_CORE);
			
			Block[] functionalNotFloating = new Block[]{IncBlocks.FUNNY, IncBlocks.SMALL_FUNNY, IncBlocks.SANVOCALIA, IncBlocks.SMALL_SANVOCALIA};
			Block[] functionalFloating = new Block[]{IncBlocks.FLOATING_FUNNY, IncBlocks.SMALL_FLOATING_FUNNY, IncBlocks.FLOATING_SANVOCALIA, IncBlocks.SMALL_FLOATING_SANVOCALIA};
			Block[] smallNotFloating = new Block[]{IncBlocks.SMALL_SANVOCALIA, IncBlocks.SMALL_FUNNY};
			
			getOrCreateBuilder(ModTags.Blocks.FUNCTIONAL_SPECIAL_FLOWERS).add(functionalNotFloating);
			getOrCreateBuilder(ModTags.Blocks.SPECIAL_FLOATING_FLOWERS).add(functionalFloating);
			getOrCreateBuilder(ModTags.Blocks.MINI_FLOWERS).add(smallNotFloating); //Looks like an unused tag?
			
			getOrCreateBuilder(IncTags.Blocks.OPEN_CRATES).add(ModBlocks.openCrate);
		}
	}
	
	public static class ItemProvider extends ItemTagsProvider {
		public ItemProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
			super(dataGenerator, blockTagProvider, Inc.MODID, existingFileHelper);
		}
		
		@Override
		protected void registerTags() {
			copy(ModTags.Blocks.FUNCTIONAL_SPECIAL_FLOWERS, ModTags.Items.FUNCTIONAL_SPECIAL_FLOWERS);
			copy(ModTags.Blocks.SPECIAL_FLOATING_FLOWERS, ModTags.Items.SPECIAL_FLOATING_FLOWERS);
			copy(ModTags.Blocks.MINI_FLOWERS, ModTags.Items.MINI_FLOWERS);
			
			getOrCreateBuilder(ModTags.Items.RODS).add(IncItems.FRACTURED_SPACE_ROD);
		}
	}
}
