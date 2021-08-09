package agency.highlysuspect.rhododendrite.datagen;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.WoodFamily;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

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
	}
	
	//LITERALLY copy paste from Forge code. Including the PRIVATE modifier. WHAT
	private ResourceLocation extend(ResourceLocation rl, String suffix) {
		return new ResourceLocation(rl.getNamespace(), rl.getPath() + suffix);
	}
	
	@SuppressWarnings("SameParameterValue")
	private void handleWoodBlockFamily(WoodFamily family) {
		ResourceLocation planks = blockTexture(family.planks);
		ResourceLocation log = blockTexture(family.log);
		
		simpleBlock(family.planks);
		blockItemParent(family.planks.asItem());
		
		//todo sapling
		
		logBlock(family.log);
		blockItemParent(family.log.asItem());
		logBlock(family.strippedLog); 
		blockItemParent(family.strippedLog.asItem());
		axisBlock(family.wood, blockTexture(family.log), extend(blockTexture(family.log), "_top"));
		blockItemParent(family.wood.asItem());
		axisBlock(family.strippedWood, blockTexture(family.strippedLog), extend(blockTexture(family.strippedLog), "_top"));
		blockItemParent(family.strippedWood.asItem());
		
		//todo leaves
		blockItemParent(family.leaves.asItem());
		
		stairsBlock(family.stairs, planks);
		itemModels().stairs(n(family.stairs), planks, planks, planks);
		
		//todo sign
		
		doorBlock(family.door, Rho.id("block/" + family.name + "_door_bottom"), Rho.id("block/" + family.name + "_door_top"));
		itemGenerated(family.door.asItem());
		
		//todo wall sign
		
		//todo pressure plate
		
		fenceBlock(family.fence, planks);
		itemModels().fenceInventory(n(family.fence), planks);
		
		trapdoorBlock(family.trapdoor, Rho.id("block/" + family.name + "_trapdoor"), true);
		blockItemParent(family.trapdoor.asItem());
		
		fenceGateBlock(family.fenceGate, planks);
		itemModels().fenceGate(n(family.fenceGate), planks);
		
		//todo potted sapling
		
		//todo button
		
		slabBlock(family.slab, planks, planks);
		itemModels().slab(n(family.slab), planks, planks, planks);
	}
	
	private void blockItemParent(Item i) {
		itemModels().withExistingParent(n(i), Rho.id("block/" + n(i)));
	}
	
	private void itemGenerated(IForgeRegistryEntry<?> thingie) {
		assert thingie.getRegistryName() != null; //no u
		ResourceLocation item = new ResourceLocation(thingie.getRegistryName().getNamespace(), "item/" + thingie.getRegistryName().getPath());
		itemGenerated(thingie, item);
	}
	
	private void itemGenerated(IForgeRegistryEntry<?> thingie, ResourceLocation texture) {
		itemModels().withExistingParent(n(thingie), new ResourceLocation("item/generated")).texture("layer0", texture);
	}
}
