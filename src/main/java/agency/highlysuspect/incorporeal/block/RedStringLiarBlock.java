package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.block.tile.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.block.tile.RedStringLiarTile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import vazkii.botania.common.block.BlockMod;
import vazkii.botania.common.block.string.BlockRedString;
import vazkii.botania.common.block.tile.string.TileRedString;

import javax.annotation.Nullable;

public class RedStringLiarBlock extends BlockRedString {
	public RedStringLiarBlock(Properties props) {
		super(props);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new RedStringLiarTile(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return BlockMod.createTickerHelper(blockEntityType, IncBlockEntityTypes.RED_STRING_LIAR, TileRedString::commonTick);
	}
}
