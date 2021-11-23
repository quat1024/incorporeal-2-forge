package agency.highlysuspect.incorporeal.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneDiodeBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.IBlockReader;
import net.minecraft.level.ILevel;
import net.minecraft.level.ILevelReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import net.minecraft.block.AbstractBlock.Properties;

public abstract class CrappyRedstoneDiodeBlock extends RedstoneDiodeBlock implements IPlantable {
	public CrappyRedstoneDiodeBlock(Properties builder) {
		super(builder);
	}
	
	@Override
	public boolean canSurvive(BlockState state, ILevelReader level, BlockPos pos) {
		return level.getBlockState(pos.below()).canSustainPlant(level, pos, Direction.UP, this);
	}
	
	//Copy paste from several blocks that are attached to things, like BushBlock and TorchBlock
	@Override
	public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, ILevel levelIn, BlockPos currentPos, BlockPos facingPos) {
		return facing == Direction.DOWN && !this.canSurvive(stateIn, levelIn, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(stateIn, facing, facingState, levelIn, currentPos, facingPos);
	}
	
	@Override
	public BlockState getPlant(IBlockReader level, BlockPos pos) {
		//Copied from BushBlock. Idk what this is really for
		BlockState state = level.getBlockState(pos);
		if (state.getBlock() != this) return defaultBlockState();
		return state;
	}
	
	@Override
	public PlantType getPlantType(IBlockReader level, BlockPos pos) {
		return PlantType.PLAINS;
	}
}
