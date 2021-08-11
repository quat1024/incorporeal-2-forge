package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.rhododendrite.block.CoreBlock;
import agency.highlysuspect.rhododendrite.block.CoreTumblerBlock;
import agency.highlysuspect.rhododendrite.block.DirectionalBlockButBetter;
import net.minecraft.block.BlockState;

public class CoreTumblerTile extends RhodoNetworkTile {
	public CoreTumblerTile() {
		super(RhoTileTypes.CORE_TUMBLER);
	}
	
	public void whenPowered() {
		if(world == null) return; //smh
		
		CoreTile core = findCore();
		if(core != null) {
			BlockState coreState = core.getBlockState();
			if(coreState.getBlock() instanceof CoreBlock && getBlockState().getBlock() instanceof CoreTumblerBlock) { //u never know????
				sparkle();
				world.setBlockState(core.getPos(), coreState.with(CoreBlock.FACING, getBlockState().get(DirectionalBlockButBetter.FACING)));
			}
		}
	}
}
