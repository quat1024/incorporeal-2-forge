package agency.highlysuspect.incorporeal.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

public abstract class CrappyRedstoneDiodeBlock extends RedstoneDiodeBlock implements IPlantable {
	public CrappyRedstoneDiodeBlock(Properties builder) {
		super(builder);
	}
	
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos) {
		return world.getBlockState(pos.down()).canSustainPlant(world, pos, Direction.UP, this);
	}
	
	//Copy paste from several blocks that are attached to things, like BushBlock and TorchBlock
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		return facing == Direction.DOWN && !this.isValidPosition(stateIn, worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	@Override
	public BlockState getPlant(IBlockReader world, BlockPos pos) {
		//Copied from BushBlock. Idk what this is really for
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() != this) return getDefaultState();
		return state;
	}
	
	@Override
	public PlantType getPlantType(IBlockReader world, BlockPos pos) {
		return PlantType.PLAINS;
	}
}
