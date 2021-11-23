package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NearbyIndicesFinder {
	public static List<TileCorporeaIndex> findNearbyIndicesReflect(Level level, BlockPos pos, int radius) {
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
			.filter(tile -> tile.getLevel() != null && tile.getLevel().dimension().equals(level.dimension()) &&
				Math.abs(pos.getX() - tile.getBlockPos().getX()) <= radius &&
				tile.getBlockPos().getY() == pos.getY() &&
				Math.abs(pos.getZ() - tile.getBlockPos().getZ()) <= radius)
			.collect(Collectors.toList());
	}
}
