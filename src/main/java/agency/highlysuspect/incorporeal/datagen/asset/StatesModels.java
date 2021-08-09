package agency.highlysuspect.incorporeal.datagen.asset;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.block.SoulCoreBlock;
import agency.highlysuspect.incorporeal.block.UnstableCubeBlock;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import javax.annotation.Nonnull;

public class StatesModels extends BlockStateProvider {
	public StatesModels(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, Inc.MODID, exFileHelper);
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
				.cubeColumn(n(IncBlocks.CORPOREA_SOLIDIFIER), Inc.id("block/corporea_solidifier/side"), Inc.id("block/corporea_solidifier/top_bottom"))));
		
		redStringBlock(IncBlocks.RED_STRING_LIAR, Inc.id("block/red_string_liar/side"));
		
		getVariantBuilder(IncBlocks.FRAME_TINKERER)
			.partialState()
			.setModels(new ConfiguredModel(models()
				.withExistingParent(n(IncBlocks.FRAME_TINKERER), Inc.botaniaId("block/shapes/three_high_bottom_top"))
				.texture("bottom", Inc.id("block/frame_tinkerer/bottom"))
				.texture("top", Inc.id("block/frame_tinkerer/top"))
				.texture("side", Inc.id("block/frame_tinkerer/side"))));
		
		getVariantBuilder(IncBlocks.CORPOREA_RETAINER_EVAPORATOR)
			.partialState()
			.setModels(new ConfiguredModel(models()
				.cubeColumn(n(IncBlocks.CORPOREA_RETAINER_EVAPORATOR), Inc.id("block/corporea_retainer_evaporator/side"), Inc.id("block/corporea_retainer_evaporator/top_bottom"))));
		
		particleOnly(IncBlocks.ENDER_SOUL_CORE, Inc.id("entity/ender_soul_core"));
		particleOnly(IncBlocks.CORPOREA_SOUL_CORE, Inc.id("entity/corporea_soul_core"));
		
		for(UnstableCubeBlock u : IncBlocks.UNSTABLE_CUBES.values()) particleOnly(u, Inc.id("entity/unstable_cube"));
		
		flowerBlock(IncBlocks.SANVOCALIA, Inc.id("block/sanvocalia/big"));
		flowerBlock(IncBlocks.SMALL_SANVOCALIA, Inc.id("block/sanvocalia/small"));
		//Floating flower models are done in regular json because forge datagens are awful
		//I'd love to literally just provide a ResourceLocation to paste into the "loader" field of the json file. Nope!
		simpleBlock(IncBlocks.FLOATING_SANVOCALIA, new ModelFile.UncheckedModelFile(Inc.id("block/floating_sanvocalia")));
		simpleBlock(IncBlocks.SMALL_FLOATING_SANVOCALIA, new ModelFile.UncheckedModelFile(Inc.id("block/floating_sanvocalia_chibi")));
		
		flowerBlock(IncBlocks.FUNNY, Inc.id("block/funny/thisissosad"));
		flowerBlock(IncBlocks.SMALL_FUNNY, Inc.id("block/funny/alexaplaydespacito"));
		simpleBlock(IncBlocks.FLOATING_FUNNY, new ModelFile.UncheckedModelFile(Inc.id("block/floating_funny")));
		simpleBlock(IncBlocks.SMALL_FLOATING_FUNNY, new ModelFile.UncheckedModelFile(Inc.id("block/floating_funny_chibi")));
		
		///////////////uhhhh items
		
		//do the block items
		//this is somewhat miserable
		//noinspection deprecation,ConstantConditions
		Registry.ITEM.stream().filter(i -> i instanceof BlockItem && Inc.MODID.equals(i.getRegistryName().getNamespace())).map(i -> (BlockItem) i).forEach(i -> {
			//noinspection ConstantConditions
			if(i instanceof ItemBlockSpecialFlower && !i.getRegistryName().getPath().contains("floating")) return;
			
			if(i.getBlock() instanceof SoulCoreBlock || i.getBlock() instanceof UnstableCubeBlock) {
				builtinEntity(i);
				return;
			}
			
			itemModels().withExistingParent(n(i), Inc.id("block/" + n(i)));
		});
		
		//item/generated items
		
		//IncItems.CORPOREA_TICKET - handwritten
		itemGenerated(IncItems.TICKET_CONJURER, Inc.id("item/ticket_conjurer/tex"));
		itemGenerated(IncItems.FRACTURED_SPACE_ROD, Inc.id("item/fractured_space_rod/tex"));
		builtinEntity(IncItems.SOUL_CORE_FRAME);
	}
	
	//copypaste from botania kinda
	@SuppressWarnings("SameParameterValue")
	private void redStringBlock(Block b, ResourceLocation funny) {
		String name = Registry.BLOCK.getKey(b).getPath();
		ResourceLocation front = Inc.botaniaId("block/red_string_sender");
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
		simpleBlock(b, models().withExistingParent(n(b), Inc.botaniaId("block/shapes/cross")).texture("cross", flowerTexture));
		itemGenerated(b, flowerTexture);
	}
	
	private void particleOnly(Block b, ResourceLocation particle) {
		simpleBlock(b, models().getBuilder(n(b)).texture("particle", particle));
	}
	
	private void itemGenerated(IForgeRegistryEntry<?> thingie, ResourceLocation texture) {
		itemModels().withExistingParent(n(thingie), new ResourceLocation("item/generated")).texture("layer0", texture);
	}
	
	//copy from botania, modified a bit
	private void builtinEntity(Item i) {
		itemModels().getBuilder(n(i)).parent(new ModelFile.UncheckedModelFile("builtin/entity"))
			.transforms()
			.transform(ModelBuilder.Perspective.GUI)
			.rotation(30, 45, 0)
			.scale(0.625F)
			.end()
			.transform(ModelBuilder.Perspective.GROUND)
			.translation(0, 3, 0)
			.scale(0.25F)
			.end()
			.transform(ModelBuilder.Perspective.HEAD)
			.rotation(0, 180, 0)
			.end()
			.transform(ModelBuilder.Perspective.FIXED)
			.rotation(0, 180, 0)
			.scale(0.5F)
			.end()
			.transform(ModelBuilder.Perspective.THIRDPERSON_RIGHT)
			.rotation(75, 315, 0)
			.translation(0, 2.5F, 0)
			.scale(0.375F)
			.end()
			.transform(ModelBuilder.Perspective.FIRSTPERSON_RIGHT)
			.rotation(0, 315, 0)
			.scale(0.4F)
			.end()
			.end();
	}
}
