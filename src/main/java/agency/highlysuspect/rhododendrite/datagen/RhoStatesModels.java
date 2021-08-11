package agency.highlysuspect.rhododendrite.datagen;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.WoodFamily;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import agency.highlysuspect.rhododendrite.item.RhoItems;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public class RhoStatesModels extends BlockStateProvider {
	public RhoStatesModels(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, Rho.MODID, exFileHelper);
	}
	
	@Nonnull
	@Override
	public String getName() {
		return "rhododendrite blockstates and item models";
	}
	
	@SuppressWarnings("ConstantConditions") //shit has registry names
	private String n(IForgeRegistryEntry<?> b) {
		return b.getRegistryName().getPath();
	}
	
	@Override
	protected void registerStatesAndModels() {
		handleWoodBlockFamily(RhoBlocks.RHODODENDRITE);
		
		//simpleBlock(RhoBlocks.CORE); //Detailed custom model & i just did the blockstate too
		blockItemParent(RhoBlocks.CORE);
		
		//Done manually since forge generates an awful blockstate for this, unfolding all 16 * 6 states into their own json objects
		//Again just USE the damn VANILLA system.....
		//(I also needed to flip every other orientation so it lines up with the regular log.. this has 6 states, the log has 3)
		//(so awakening a log would sometimes flip the texture and look weird)
		//directionalBlock(RhoBlocks.AWAKENED_LOG, new ModelFile.ExistingModelFile(Rho.id("block/awakened_log"), models().existingFileHelper));
		blockItemParent(RhoBlocks.AWAKENED_LOG);
		
		blockItemParent(RhoBlocks.OPCODE);
		
		doIt(this::itemGenerated,
			RhoItems.NOP,
			RhoItems.TEST1,
			RhoItems.TEST2,
			RhoItems.PUSH,
			RhoItems.PULL,
			RhoItems.DUP,
			RhoItems.PUSH_ZERO,
			RhoItems.PUSH_ONE,
			RhoItems.ADD,
			RhoItems.SUBTRACT,
			RhoItems.MULTIPLY,
			RhoItems.DIVIDE,
			RhoItems.REMAINDER);
		
		coreTumbler(RhoBlocks.CORE_TUMBLER);
	}
	
	protected <T> void doIt(Consumer<T> yes, T... blocks) {
		for(T b : blocks) yes.accept(b);
	}
	
	protected void coreTumbler(Block b) {
		ResourceLocation tex = blockTexture(b);
		directionalBlock(b, models().cubeBottomTop(n(b), tex, extend(tex, "_bottom"), extend(tex, "_top")));
		blockItemParent(b);
	}
	
	//LITERALLY copy paste from Forge code. Including the PRIVATE modifier. WHAT
	private ResourceLocation extend(ResourceLocation rl, String suffix) {
		return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
	}
	
	@SuppressWarnings("SameParameterValue")
	private void handleWoodBlockFamily(WoodFamily family) {
		ResourceLocation planksTex = blockTexture(family.planks);
		ResourceLocation logTex = blockTexture(family.log);
		ResourceLocation strippedLogTex = blockTexture(family.strippedLog);
		
		simpleBlock(family.planks);
		blockItemParent(family.planks);
		
		//todo sapling
		
		logBlock(family.log);
		blockItemParent(family.log);
		logBlock(family.strippedLog); 
		blockItemParent(family.strippedLog);
		axisBlock(family.wood, logTex, logTex);
		blockItemParent(family.wood);
		axisBlock(family.strippedWood, strippedLogTex, strippedLogTex);
		blockItemParent(family.strippedWood);
		
		simpleBlock(family.leaves);
		blockItemParent(family.leaves);
		
		stairsBlock(family.stairs, planksTex);
		itemModels().stairs(n(family.stairs), planksTex, planksTex, planksTex);
		
		//todo sign
		
		doorBlock(family.door, Rho.id("block/" + family.name + "_door_bottom"), Rho.id("block/" + family.name + "_door_top"));
		itemGenerated(family.door);
		
		//todo wall sign
		
		pressurePlate(family.pressurePlate, planksTex);
		blockItemParent(family.pressurePlate);
		
		fenceBlock(family.fence, planksTex);
		itemModels().fenceInventory(n(family.fence), planksTex);
		
		trapdoorBlock(family.trapdoor, Rho.id("block/" + family.name + "_trapdoor"), true);
		blockItemParent(family.trapdoor);
		
		fenceGateBlock(family.fenceGate, planksTex);
		itemModels().fenceGate(n(family.fenceGate), planksTex);
		
		//todo potted sapling
		
		//todo buttons are done with a manual JSON file copypasted from Quark
		// because forge datagen is kinda CBT and i cant figure out a way to do it nice
		// Its really easy with the vanilla stuff, why didnt they just expose that?????
		
		slabBlock(family.slab, planksTex, planksTex);
		itemModels().slab(n(family.slab), planksTex, planksTex, planksTex);
	}
	
	private void pressurePlate(Block b, ResourceLocation texture) {
		getVariantBuilder(b).partialState().with(BlockStateProperties.POWERED, false).setModels(new ConfiguredModel(models()
			.withExistingParent(n(b), new ResourceLocation("block/pressure_plate_down")).texture("texture", texture)));
		getVariantBuilder(b).partialState().with(BlockStateProperties.POWERED, true).setModels(new ConfiguredModel(models()
			.withExistingParent(n(b), new ResourceLocation("block/pressure_plate_up")).texture("texture", texture)));
	}
	
	//i thought forge datagen stuff was supposed to handle the common cases. guess im wrong?
	private void blockItemParent(IItemProvider i) {
		itemModels().withExistingParent(n(i.asItem()), Rho.id("block/" + n(i.asItem())));
	}
	
	//this is a mess im sorry. this is the wrong way to do it
	private void itemGenerated(IItemProvider asd) {
		IForgeRegistryEntry<?> thingie = asd.asItem();
		assert thingie.getRegistryName() != null; //no u
		ResourceLocation item = new ResourceLocation(thingie.getRegistryName().getNamespace(), "item/" + thingie.getRegistryName().getPath());
		itemGenerated(thingie, item);
	}
	
	private void itemGenerated(IForgeRegistryEntry<?> thingie, ResourceLocation texture) {
		itemModels().withExistingParent(n(thingie), new ResourceLocation("item/generated")).texture("layer0", texture);
	}
}
