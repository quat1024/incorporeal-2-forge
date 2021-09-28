package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.rhododendrite.computer.RhodoFunnelable;
import agency.highlysuspect.rhododendrite.computer.RhodoFunnelableCapability;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public class RhodoFunnelTile extends AbstractComputerTile implements ITickableTileEntity {
	public RhodoFunnelTile() {
		super(RhoTileTypes.FUNNEL);
	}
	
	protected transient @Nullable BlockPos foreBinding;
	protected transient @Nullable BlockPos aftBinding;
	
	@Override
	public void tick() {
		Direction dir = getBlockState().get(DirectionalBlock.FACING);
		
		foreBinding = rootExtractingChainBind(dir,
			(cursor, tile) -> {
				if(tile == null) return null;
				else if(tile.getCapability(RhodoFunnelableCapability.INSTANCE).map(RhodoFunnelable::canRhodoInsert).isPresent()) return cursor;
				else if(tile instanceof RhodoFunnelTile) return ((RhodoFunnelTile) tile).foreBinding;
				else return null;
			});
		
		aftBinding = rootExtractingChainBind(dir.getOpposite(),
			(cursor, tile) -> {
				if(tile == null) return null;
				else if(tile.getCapability(RhodoFunnelableCapability.INSTANCE).map(RhodoFunnelable::canRhodoExtract).isPresent()) return cursor;
				else if(tile instanceof RhodoFunnelTile) return ((RhodoFunnelTile) tile).foreBinding;
				else return null;
			});
	}
	
	//todo: something like FunnelBinding(supplier<funnelable>, supplier<vec3d>) so i can bind to entities etc.
}
