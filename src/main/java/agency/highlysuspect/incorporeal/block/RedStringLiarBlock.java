package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.block.tile.RedStringLiarTile;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;
import vazkii.botania.common.block.string.BlockRedString;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class RedStringLiarBlock extends BlockRedString {
	public RedStringLiarBlock(Properties props) {
		super(props);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockGetter world) {
		return new RedStringLiarTile();
	}
}
