package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.block.tile.RedStringLiarTile;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.level.IBlockReader;
import vazkii.botania.common.block.string.BlockRedString;

import javax.annotation.Nullable;

import net.minecraft.block.AbstractBlock.Properties;

public class RedStringLiarBlock extends BlockRedString {
	public RedStringLiarBlock(Properties props) {
		super(props);
	}
	
	@Nullable
	@Override
	public TileEntity newBlockEntity(IBlockReader level) {
		return new RedStringLiarTile();
	}
}
