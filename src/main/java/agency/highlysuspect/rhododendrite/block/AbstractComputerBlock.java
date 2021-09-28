package agency.highlysuspect.rhododendrite.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;

public abstract class AbstractComputerBlock extends Block {
	public AbstractComputerBlock(Properties properties) {
		super(properties);
		setDefaultState(getDefaultState().with(DirectionalBlock.FACING, Direction.UP));
	}
	
	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder.add(DirectionalBlock.FACING));
	}
}
