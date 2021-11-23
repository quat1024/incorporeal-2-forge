package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.block.tile.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.block.tile.UnstableCubeTile;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import vazkii.botania.api.block.IWandable;
import vazkii.botania.common.block.BlockMod;
import vazkii.botania.common.block.BlockModWaterloggable;
import vazkii.botania.common.block.tile.string.TileRedString;

import javax.annotation.Nullable;

public class UnstableCubeBlock extends BlockModWaterloggable implements /*IWandable,*/ EntityBlock {
	public UnstableCubeBlock(Properties properties, DyeColor color) {
		super(properties);
		this.color = color;
	}
	
	public final DyeColor color;
	public static final VoxelShape BOX = Shapes.box(3/16d, 3/16d, 3/16d, 1 - 3/16d, 1 - 3/16d, 1 - 3/16d);
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return BOX;
	}
	
	@Override
	public void attack(BlockState state, Level world, BlockPos pos, Player player) {
		punch(world, pos);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		punch(world, pos);
		return InteractionResult.SUCCESS;
	}
	
	//TODO fix IWandable (no pos?)
	//@Override
	public boolean onUsedByWand(Player player, ItemStack stack, Level world, BlockPos pos, Direction side) {
		punch(world, pos);
		return true;
	}
	
	private void punch(Level world, BlockPos pos) {
		BlockEntity tile = world.getBlockEntity(pos);
		if(tile instanceof UnstableCubeTile) ((UnstableCubeTile) tile).punch();
	}
	
	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}
	
	@Override
	public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		return getDirectSignal(state, world, pos, side);
	}
	
	@Override
	public int getDirectSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
		BlockEntity tile = world.getBlockEntity(pos);
		if(tile instanceof UnstableCubeTile) return ((UnstableCubeTile) tile).getPower();
		else return 0;
	}
	
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return IncBlockEntityTypes.UNSTABLE_CUBES.get(color).create(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return BlockMod.createTickerHelper(blockEntityType, IncBlockEntityTypes.UNSTABLE_CUBES.get(color), UnstableCubeTile::commonTick);
	}
}
