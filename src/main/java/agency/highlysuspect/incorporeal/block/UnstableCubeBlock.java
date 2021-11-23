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
import net.minecraft.level.IBlockReader;
import net.minecraft.level.Level;
import vazkii.botania.api.wand.IWandable;
import vazkii.botania.common.block.BlockModWaterloggable;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public class UnstableCubeBlock extends BlockModWaterloggable implements IWandable {
	public UnstableCubeBlock(Properties properties, DyeColor color) {
		super(properties);
		this.color = color;
	}
	
	public final DyeColor color;
	public static final VoxelShape BOX = VoxelShapes.box(3/16d, 3/16d, 3/16d, 1 - 3/16d, 1 - 3/16d, 1 - 3/16d);
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader levelIn, BlockPos pos, ISelectionContext context) {
		return BOX;
	}
	
	@Override
	public void attack(BlockState state, Level level, BlockPos pos, PlayerEntity player) {
		punch(level, pos);
	}
	
	@Override
	public ActionResultType use(BlockState state, Level level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		punch(level, pos);
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public boolean onUsedByWand(PlayerEntity player, ItemStack stack, Level level, BlockPos pos, Direction side) {
		punch(level, pos);
		return true;
	}
	
	private void punch(Level level, BlockPos pos) {
		TileEntity tile = level.getBlockEntity(pos);
		if(tile instanceof UnstableCubeTile) ((UnstableCubeTile) tile).punch();
	}
	
	@Override
	public boolean isSignalSource(BlockState state) {
		return true;
	}
	
	@Override
	public int getSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		return getDirectSignal(state, level, pos, side);
	}
	
	@Override
	public int getDirectSignal(BlockState state, IBlockReader level, BlockPos pos, Direction side) {
		TileEntity tile = level.getBlockEntity(pos);
		if(tile instanceof UnstableCubeTile) return ((UnstableCubeTile) tile).getPower();
		else return 0;
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return IncTileTypes.UNSTABLE_CUBES.get(color).create();
	}
}
