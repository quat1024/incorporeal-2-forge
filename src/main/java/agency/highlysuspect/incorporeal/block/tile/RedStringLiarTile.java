package agency.highlysuspect.incorporeal.block.tile;

import agency.highlysuspect.incorporeal.corporea.FrameReader;
import agency.highlysuspect.incorporeal.corporea.LyingCorporeaNode;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import vazkii.botania.api.corporea.ICorporeaNode;
import vazkii.botania.api.corporea.ICorporeaNodeDetector;
import vazkii.botania.api.corporea.ICorporeaSpark;
import vazkii.botania.common.block.tile.string.TileRedString;
import vazkii.botania.common.impl.corporea.DummyCorporeaNode;
import vazkii.botania.common.impl.corporea.ForgeCapCorporeaNode;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class RedStringLiarTile extends TileRedString {
	public RedStringLiarTile() {
		super(IncTileTypes.RED_STRING_LIAR);
	}
	
	@Override
	public boolean acceptBlock(BlockPos pos) {
		assert world != null;
		
		TileEntity tile = world.getTileEntity(pos);
		if(tile == null) return false;
		else return tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getOrientation().getOpposite()).isPresent();
	}
	
	public ICorporeaNode getNode(World world, ICorporeaSpark spark) {
		TileEntity bound = getTileAtBinding();
		if(bound != null) {
			//todo: this is kinda messy capabilities code, forge caps in 1.16 are wild
			// also there's no reason to limit myself to only ForgeCapCorporeaNodes
			// the new implementation of LyingCorporeaNode is generic over all corporea nodes. might be something to look at.
			LazyOptional<IItemHandler> handler = bound.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, getOrientation().getOpposite());
			if(handler.isPresent()) {
				return new LyingCorporeaNode(
					world, spark.getAttachPos(), spark,
					new ForgeCapCorporeaNode(world, spark.getAttachPos(), handler.resolve().get(), spark),
					getSpoofedStacks()
				);
			}
		}
		
		return new DummyCorporeaNode(world, spark.getAttachPos(), spark);
	}
	
	public List<ItemStack> getSpoofedStacks() {
		return FrameReader.readFramesAround(world, pos);
	}
	
	public static class NodeDetector implements ICorporeaNodeDetector {
		@Nullable
		@Override
		public ICorporeaNode getNode(World world, ICorporeaSpark spark) {
			TileEntity tile = world.getTileEntity(spark.getAttachPos());
			if(tile instanceof RedStringLiarTile) return ((RedStringLiarTile) tile).getNode(world, spark);
			else return null;
		}
	}
}
