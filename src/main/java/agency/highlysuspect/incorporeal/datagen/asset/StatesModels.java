package agency.highlysuspect.incorporeal.datagen.asset;

import agency.highlysuspect.incorporeal.Init;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import javax.annotation.Nonnull;

public class StatesModels extends BlockStateProvider {
	public StatesModels(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, Init.MODID, exFileHelper);
	}
	
	@Nonnull
	@Override
	public String getName() {
		return "incorporeal blockstates and item models";
	}
	
	@SuppressWarnings("ConstantConditions") //shit has registry names
	private String n(IForgeRegistryEntry<?> b) {
		return b.getRegistryName().getPath();
	}
	
	@Override
	protected void registerStatesAndModels() {
		getVariantBuilder(IncBlocks.CORPOREA_SOLIDIFIER)
			.partialState()
			.setModels(new ConfiguredModel(models()
				.cubeColumn(n(IncBlocks.CORPOREA_SOLIDIFIER), Init.id("block/corporea_solidifier/side"), Init.id("block/corporea_solidifier/top_bottom"))));
		
		redStringBlock(IncBlocks.RED_STRING_LIAR, Init.id("block/red_string_liar/side"));
		
		getVariantBuilder(IncBlocks.FRAME_TINKERER)
			.partialState()
			.setModels(new ConfiguredModel(models()
				.withExistingParent(n(IncBlocks.FRAME_TINKERER), Init.botaniaId("block/shapes/three_high_bottom_top"))
				.texture("bottom", Init.id("block/frame_tinkerer/bottom"))
				.texture("top", Init.id("block/frame_tinkerer/top"))
				.texture("side", Init.id("block/frame_tinkerer/side"))));
		
		flowerBlock(IncBlocks.SANVOCALIA, Init.id("block/sanvocalia/big"));
		flowerBlock(IncBlocks.SMALL_SANVOCALIA, Init.id("block/sanvocalia/small"));
		//Floating flower models are done in regular json because forge datagens are awful
		//I'd love to literally just provide a ResourceLocation to paste into the "loader" field of the json file. Nope!
		simpleBlock(IncBlocks.FLOATING_SANVOCALIA, new ModelFile.UncheckedModelFile(Init.id("block/floating_sanvocalia")));
		simpleBlock(IncBlocks.SMALL_FLOATING_SANVOCALIA, new ModelFile.UncheckedModelFile(Init.id("block/floating_sanvocalia_chibi")));
		
		flowerBlock(IncBlocks.FUNNY, Init.id("block/funny/thisissosad"));
		flowerBlock(IncBlocks.SMALL_FUNNY, Init.id("block/funny/alexaplaydespacito"));
		simpleBlock(IncBlocks.FLOATING_FUNNY, new ModelFile.UncheckedModelFile(Init.id("block/floating_funny")));
		simpleBlock(IncBlocks.SMALL_FLOATING_FUNNY, new ModelFile.UncheckedModelFile(Init.id("block/floating_funny_chibi")));
		
		//this is somewhat miserable
		//noinspection deprecation,ConstantConditions
		Registry.ITEM.stream().filter(i -> i instanceof BlockItem && Init.MODID.equals(i.getRegistryName().getNamespace())).map(i -> (BlockItem) i).forEach(i -> {
			//noinspection ConstantConditions
			if(i instanceof ItemBlockSpecialFlower && !i.getRegistryName().getPath().contains("floating")) return;
			
			itemModels().withExistingParent(n(i), Init.id("block/" + n(i)));
		});
	}
	
	//copypaste from botania kinda
	@SuppressWarnings("SameParameterValue")
	private void redStringBlock(Block b, ResourceLocation funny) {
		String name = Registry.BLOCK.getKey(b).getPath();
		ResourceLocation front = Init.botaniaId("block/red_string_sender");
		ModelFile file = models().orientable(name, funny, front, funny);
		getVariantBuilder(b)
			.partialState().with(BlockStateProperties.FACING, Direction.NORTH).setModels(new ConfiguredModel(file))
			.partialState().with(BlockStateProperties.FACING, Direction.SOUTH).setModels(new ConfiguredModel(file, 0, 180, false))
			.partialState().with(BlockStateProperties.FACING, Direction.WEST).setModels(new ConfiguredModel(file, 0, 270, false))
			.partialState().with(BlockStateProperties.FACING, Direction.EAST).setModels(new ConfiguredModel(file, 0, 90, false))
			.partialState().with(BlockStateProperties.FACING, Direction.DOWN).setModels(new ConfiguredModel(file, 90, 0, false))
			.partialState().with(BlockStateProperties.FACING, Direction.UP).setModels(new ConfiguredModel(file, 270, 0, false));
	}
	
	private void flowerBlock(Block b, ResourceLocation flowerTexture) {
		simpleBlock(b, models().withExistingParent(n(b), Init.botaniaId("block/shapes/cross")).texture("cross", flowerTexture));
		itemModels().withExistingParent(n(b), new ResourceLocation("item/generated")).texture("layer0", flowerTexture);
	}
}
