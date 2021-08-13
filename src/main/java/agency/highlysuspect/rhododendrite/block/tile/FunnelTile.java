package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import net.minecraft.tileentity.ITickableTileEntity;

public class FunnelTile extends RhodoNetworkTile implements ITickableTileEntity {
	public FunnelTile() {
		super(RhoTileTypes.FUNNEL);
	}
	
	@Override
	public void tick() {
		if(world != null && world.getGameTime() % 20 == 0) {
			RhoBlocks.FUNNEL.updateArrowStatus(world, pos, getBlockState());
		}
	}
}
