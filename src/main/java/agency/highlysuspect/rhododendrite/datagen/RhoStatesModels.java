package agency.highlysuspect.rhododendrite.datagen;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.WoodBlockFamily;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

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
	
	@Override
	protected void registerStatesAndModels() {
		handleWoodBlockFamily(RhoBlocks.RHODODENDRITE);
	}
	@SuppressWarnings("SameParameterValue")
	private void handleWoodBlockFamily(WoodBlockFamily family) {
		simpleBlock(family.planks);
		//todo sapling
		logBlock(family.log);
		logBlock(family.strippedLog);
		logBlock(family.wood);
		logBlock(family.strippedWood);
		//todo leaves
		stairsBlock(family.stairs, family.planks.getRegistryName());
		//todo sign
		doorBlock(family.door, Rho.id(family.name + "_door_bottom"), Rho.id(family.name + "_door_top"));
		//todo wall sign
		//todo pressure plate
		fenceBlock(family.fence, family.planks.getRegistryName());
		trapdoorBlock(family.trapdoor, Rho.id(family.name + "_trapdoor"), true);
		fenceGateBlock(family.fenceGate, family.planks.getRegistryName());
		//todo potted sapling
		//todo button
		slabBlock(family.slab, family.planks.getRegistryName(), family.planks.getRegistryName());
	}
}
