package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface RhodoFunnelable {
	boolean canRhodoExtract();
	Optional<SolidifiedRequest> rhodoExtract(boolean simulate);
	
	boolean canRhodoInsert();
	boolean tryRhodoInsert(@Nonnull SolidifiedRequest request, boolean simulate);
	
	interface Loose {
		@Nullable RhodoFunnelable getFunnelable(World world, BlockPos pos, BlockState state, Direction face);
	}
	
	class ForRequestHolder implements RhodoFunnelable {
		public ForRequestHolder(SolidifiedRequest.Holder holder) {
			this.holder = holder;
		}
		
		final SolidifiedRequest.Holder holder;
		
		@Override
		public boolean canRhodoExtract() {
			return true;
		}
		
		@Override
		public Optional<SolidifiedRequest> rhodoExtract(boolean simulate) {
			return Optional.of(holder.getRequest());
		}
		
		@Override
		public boolean canRhodoInsert() {
			return true;
		}
		
		@Override
		public boolean tryRhodoInsert(@Nonnull SolidifiedRequest request, boolean simulate) {
			holder.setRequest(request);
			return true;
		}
	}
}
