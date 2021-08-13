package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.block.tile.IncTileTypes;
import agency.highlysuspect.incorporeal.block.tile.UnstableCubeTile;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import vazkii.botania.api.wand.IWandable;
import vazkii.botania.common.block.BlockModWaterloggable;

import javax.annotation.Nullable;

public class UnstableCubeBlock extends BlockModWaterloggable implements IWandable {
	public UnstableCubeBlock(Properties properties, DyeColor color) {
		super(properties);
		this.color = color;
	}
	
	public final DyeColor color;
	public static final VoxelShape BOX = VoxelShapes.create(3/16d, 3/16d, 3/16d, 1 - 3/16d, 1 - 3/16d, 1 - 3/16d);
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return BOX;
	}
	
	@Override
	public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		punch(world, pos);
	}
	
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		punch(world, pos);
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public boolean onUsedByWand(PlayerEntity player, ItemStack stack, World world, BlockPos pos, Direction side) {
		punch(world, pos);
		return true;
	}
	
	private void punch(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof UnstableCubeTile) ((UnstableCubeTile) tile).punch();
	}
	
	@Override
	public boolean canProvidePower(BlockState state) {
		return true;
	}
	
	@Override
	public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return getStrongPower(state, world, pos, side);
	}
	
	@Override
	public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof UnstableCubeTile) return ((UnstableCubeTile) tile).getPower();
		else return 0;
	}
	
	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return IncTileTypes.UNSTABLE_CUBES.get(color).create();
	}
}
