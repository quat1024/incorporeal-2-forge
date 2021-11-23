package agency.highlysuspect.incorporeal.corporea;

import agency.highlysuspect.incorporeal.Inc;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import vazkii.botania.common.block.tile.corporea.TileCorporeaRetainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class IncCapEvents {
	public static void attachTileCapabilities(AttachCapabilitiesEvent<BlockEntity> e) {
		BlockEntity tile = e.getObject();
		if(tile instanceof TileCorporeaRetainer) {
			e.addCapability(RETAINER_CAP, new RetainerProvider((TileCorporeaRetainer) tile));
		}
	}
	
	private static final ResourceLocation RETAINER_CAP = Inc.id("retainer_cap");
	
	public static final class RetainerProvider implements SolidifiedRequest.Holder, ICapabilityProvider {
		public RetainerProvider(TileCorporeaRetainer retainer) {
			this.duck = (RetainerDuck) retainer;
		}
		
		private final RetainerDuck duck;
		
		@Nonnull
		@Override
		public SolidifiedRequest getRequest() {
			return duck.inc$solidifyRequest();
		}
		
		@Override
		public void setRequest(@Nonnull SolidifiedRequest newRequest) {
			duck.inc$liquidateRequest(newRequest);
		}
		
		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
			return SolidifiedRequest.Cap.INSTANCE.orEmpty(cap, LazyOptional.of(() -> this));
		}
	}
}
