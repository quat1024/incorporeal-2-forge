package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NearbyIndicesFinder {
	public static List<TileCorporeaIndex> findNearbyIndicesReflect(World world, BlockPos pos, int radius) {
		Set<TileCorporeaIndex> serverIndices;
		try {
			Field f = TileCorporeaIndex.class.getDeclaredField("serverIndexes");
			f.setAccessible(true);
			//noinspection unchecked
			serverIndices = (Set<TileCorporeaIndex>) f.get(null);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Problem reflecting", e);
		}
		
		return serverIndices.stream()
			.filter(tile -> tile.getWorld() != null && tile.getWorld().getDimensionKey().equals(world.getDimensionKey()) &&
				Math.abs(pos.getX() - tile.getPos().getX()) <= radius &&
				tile.getPos().getY() == pos.getY() &&
				Math.abs(pos.getZ() - tile.getPos().getZ()) <= radius)
			.collect(Collectors.toList());
	}
}
