package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.block.tile.CoreTile;
import agency.highlysuspect.rhododendrite.block.tile.FunnelTile;
import agency.highlysuspect.rhododendrite.block.tile.RhoTileTypes;
import agency.highlysuspect.rhododendrite.block.tile.RhodoNetworkTile;
import agency.highlysuspect.rhododendrite.computer.RhodoFunnelable;
import agency.highlysuspect.rhododendrite.computer.RhodoFunnelableCapability;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import vazkii.botania.api.wand.IWandable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class FunnelBlock extends DirectionalBlockButBetter.PlacesLikeLogs implements IWandable {
	public FunnelBlock(Properties builder) {
		super(builder);
		
		setDefaultState(getDefaultState()
			.with(POWERED, false)
			.with(BACK, false)
			.with(FRONT, false));
	}
	
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty BACK = BooleanProperty.create("back");
	public static final BooleanProperty FRONT = BooleanProperty.create("front");
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder.add(POWERED, BACK, FRONT));
	}
	
	public void updateArrowStatus(World world, BlockPos pos, BlockState state) {
		Direction facing = state.get(FACING);
		
		RhodoFunnelable source = RhodoFunnelableCapability.findFunnelable(world, pos.offset(facing.getOpposite()), facing);
		boolean sourceCanExtract = source != null && source.canRhodoExtract();
		
		RhodoFunnelable sink = RhodoFunnelableCapability.findFunnelable(world, pos.offset(facing), facing.getOpposite());
		boolean sinkCanInsert = sink != null && sink.canRhodoInsert();
		
		world.setBlockState(pos, state.with(BACK, sourceCanExtract).with(FRONT, sinkCanInsert), 2);
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block fromBlock, BlockPos fromPos, boolean isMoving) {
		boolean isPowered = state.get(POWERED);
		boolean shouldPower = world.isBlockPowered(pos);
		if(isPowered != shouldPower) {
			Direction facing = state.get(FACING);
			
			RhodoFunnelable source = RhodoFunnelableCapability.findFunnelable(world, pos.offset(facing.getOpposite()), facing);
			boolean sourceCanExtract = source != null && source.canRhodoExtract();
			
			RhodoFunnelable sink = RhodoFunnelableCapability.findFunnelable(world, pos.offset(facing), facing.getOpposite());
			boolean sinkCanInsert = sink != null && sink.canRhodoInsert();
			
			world.setBlockState(pos, state.with(POWERED, shouldPower).with(BACK, sourceCanExtract).with(FRONT, sinkCanInsert));
			
			if(shouldPower) {
				//if neither can do it, not much else i can do
				if(!sourceCanExtract && !sinkCanInsert) return;
				
				//if both can do it, don't involve the core at all
				if(sourceCanExtract && sinkCanInsert) {
					transfer(source, sink);
					return;
				}
				
				FunnelTile tile = RhoTileTypes.FUNNEL.getIfExists(world, pos);
				CoreTile core = tile == null ? null : tile.findCore();
				if(core == null) return;
				
				//only one of these booleans is true
				if(!sourceCanExtract) source = core;
				if(!sinkCanInsert) sink = core;
				
				transfer(source, sink);
			}
		}
	}
	
	public static void transfer(@Nonnull RhodoFunnelable source, @Nonnull RhodoFunnelable sink) {
		assert source.canRhodoExtract();
		assert sink.canRhodoInsert();
		
		Optional<SolidifiedRequest> tryExtr = source.rhodoExtract(true);
		if(tryExtr.isPresent()) {
			boolean tryFit = sink.tryRhodoInsert(tryExtr.get(), true);
			if(tryFit) {
				@SuppressWarnings("OptionalGetWithoutIsPresent") //gottem
				SolidifiedRequest extr = source.rhodoExtract(false).get();
				
				boolean itFit = sink.tryRhodoInsert(extr, false);
				assert itFit;
			}
		}
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return RhoTileTypes.FUNNEL.create();
	}
	
	//TODO make it so i dont have to paste this into every rhodo block... thatd be nice lOl
	@Override
	public boolean onUsedByWand(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Direction side) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof RhodoNetworkTile) {
			((RhodoNetworkTile) tile).whenWanded();
			return true;
		} else return false;
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof RhodoNetworkTile) ((RhodoNetworkTile) tile).whenPlaced();
	}
}
