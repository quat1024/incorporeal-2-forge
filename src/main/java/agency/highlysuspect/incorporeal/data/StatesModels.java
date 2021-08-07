package agency.highlysuspect.incorporeal.data;

import agency.highlysuspect.incorporeal.Init;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nonnull;

public class IncStates extends BlockStateProvider {
	public IncStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
		super(gen, Init.MODID, exFileHelper);
	}
	
	@Nonnull
	@Override
	public String getName() {
		return "incorporeal blockstates";
	}
	
	@Override
	protected void registerStatesAndModels() {
		models().cubeColumn("a", )
	}
}
