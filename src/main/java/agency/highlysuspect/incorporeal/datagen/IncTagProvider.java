package agency.highlysuspect.incorporeal.datagen;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncTags;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.item.IncItems;
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
			getOrCreateBuilder(ModTags.Blocks.SPECIAL_FLOATING_FLOWERS).add(IncBlocks.FLOATING_FUNNY, IncBlocks.FLOATING_SANVOCALIA, IncBlocks.SMALL_FLOATING_FUNNY, IncBlocks.SMALL_FLOATING_SANVOCALIA);
			
			getOrCreateBuilder(IncTags.Blocks.OPEN_CRATES).add(ModBlocks.openCrate);
		}
	}
	
	public static class ItemProvider extends ItemTagsProvider {
		public ItemProvider(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, @Nullable ExistingFileHelper existingFileHelper) {
			super(dataGenerator, blockTagProvider, Inc.MODID, existingFileHelper);
		}
		
		@Override
		protected void registerTags() {
			copy(ModTags.Blocks.SPECIAL_FLOATING_FLOWERS, ModTags.Items.SPECIAL_FLOATING_FLOWERS);
			
			getOrCreateBuilder(ModTags.Items.RODS).add(IncItems.FRACTURED_SPACE_ROD);
		}
	}
}
