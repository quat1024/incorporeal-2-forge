package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;
import vazkii.botania.common.core.helper.Vector3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface RhodoFunnelable {
	default boolean canRhodoExtract() {
		return false;
	}
	
	default Optional<SolidifiedRequest> rhodoExtract(boolean simulate) {
		return Optional.empty();
	}
	
	default boolean canRhodoInsert() {
		return false;
	}
	
	default boolean tryRhodoInsert(@Nonnull SolidifiedRequest request, boolean simulate) {
		return false;
	}
	
	interface Loose {
		@Nullable RhodoFunnelable getFunnelable(Level level, BlockPos pos, BlockState state, Direction face);
		
		default Vector3 bindPosition(BlockPos pos) {
			return Vector3.fromBlockPos(pos).add(.5, .5, .5);
		}
	}
}
