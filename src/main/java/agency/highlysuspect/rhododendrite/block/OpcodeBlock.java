package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.block.tile.CoreTile;
import agency.highlysuspect.rhododendrite.block.tile.OpcodeTile;
import agency.highlysuspect.rhododendrite.block.tile.RhoTileTypes;
import agency.highlysuspect.rhododendrite.block.tile.RhodoNetworkTile;
import agency.highlysuspect.rhododendrite.computer.Fragment;
import agency.highlysuspect.rhododendrite.computer.StackOps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import vazkii.botania.api.wand.IWandable;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.Optional;
import java.util.function.BiFunction;

public class OpcodeBlock extends Block implements IWandable {
	public OpcodeBlock(Properties properties, CoreAction action) {
		super(properties);
		this.action = action;
		
		setDefaultState(getDefaultState().with(BlockStateProperties.POWERED, false));
	}
	
	public static CoreAction binNumeric(BiFunction<BigInteger, BigInteger, BigInteger> binOp) {
		return binNumericOpt((x, y) -> Optional.of(binOp.apply(x, y)));
	}
	
	public static CoreAction binNumericOpt(BiFunction<BigInteger, BigInteger, Optional<BigInteger>> binOp) {
		return (world, pos, state, core) -> {
			StackOps ops = StackOps.read(core);
			
			//grab the top two elements off the stack and try to convert them into numbers
			Fragment<?> head = ops.pull();
			Fragment<?> tail = ops.pull();
			Optional<BigInteger> headN = head.asNumber();
			Optional<BigInteger> tailN = tail.asNumber();
			
			if(headN.isPresent() && tailN.isPresent())
				//perform the binary operation, try to inject it back into the head's type
				binOp.apply(headN.get(), tailN.get()).flatMap(head::injectNumber).ifPresent(injResult -> {
					ops.push(injResult);
					ops.commit();
				});
		};
	}
	
	public final CoreAction action;
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder.add(BlockStateProperties.POWERED));
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
		setDefaultState(getDefaultState().with(BlockStateProperties.POWERED, false));
		
		boolean shouldPower = world.getRedstonePowerFromNeighbors(pos) > 0;
		boolean isPowered = state.get(BlockStateProperties.POWERED);
		if(isPowered != shouldPower) {
			world.setBlockState(pos, state.with(BlockStateProperties.POWERED, shouldPower));
			
			if(!world.isRemote && shouldPower) {
				OpcodeTile tile = RhoTileTypes.OPCODE.getIfExists(world, pos);
				if(tile != null) {
					CoreTile core = tile.findCore();
					if(core != null) {
						action.apply(world, pos, state, core);
					}
				}
			}
		}
	}
	
	//TODO copy to other rhodo network tiles/mess about so the hierarchy is more clear
	@Override
	public boolean onUsedByWand(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Direction side) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof RhodoNetworkTile) {
			((RhodoNetworkTile) tile).sparkle();
			return true;
		} else return false;
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof RhodoNetworkTile) ((RhodoNetworkTile) tile).tryAutobind();
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return RhoTileTypes.OPCODE.create();
	}
	
	public interface CoreAction {
		void apply(World world, BlockPos pos, BlockState state, CoreTile core);
	}
	
	public static class Directional extends OpcodeBlock {
		public Directional(Properties properties, CoreAction action) {
			super(properties, action);
			setDefaultState(getDefaultState().with(FACING, Direction.UP));
		}
		
		public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;
		
		@Override
		protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
			super.fillStateContainer(builder.add(FACING));
		}
		
		@Nullable
		@Override
		public BlockState getStateForPlacement(BlockItemUseContext context) {
			return getDefaultState().with(FACING, context.getFace().getOpposite());
		}
	}
}
