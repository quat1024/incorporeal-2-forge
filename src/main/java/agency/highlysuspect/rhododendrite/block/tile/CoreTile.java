package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.rhododendrite.block.CoreBlock;
import agency.highlysuspect.rhododendrite.computer.CorePathTracing;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;

public class CoreTile extends FragmentContainerTile {
	public CoreTile() {
		super(RhoTileTypes.CORE);
	}
	
	public void push() {
		CorePathTracing.pushFragments(CorePathTracing.readFragmentHolderLine(world, pos, getBlockState()));
	}
	
	public void pull() {
		CorePathTracing.pullFragments(CorePathTracing.readFragmentHolderLine(world, pos, getBlockState()));
	}
	
	public void reorient(Direction facing) {
		if(world == null || !(getBlockState().getBlock() instanceof CoreBlock)) return;
		world.setBlockState(pos, getBlockState().with(CoreBlock.FACING, facing));
	}
}
