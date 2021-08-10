package agency.highlysuspect.rhododendrite.computer;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FragmentCapability {
	@CapabilityInject(Fragment.Holder.class)
	public static Capability<Fragment.Holder> INSTANCE = null;
	
	public static void initialize() {
		CapabilityManager.INSTANCE.register(Fragment.Holder.class, Default.INSTANCE, () -> Default.INSTANCE);
	}
	
	//I'm hearing that the "default capability" stuff is getting removed in Forge 1.17 anyways, so I'm not gonna miss it too badly
	public static class Default implements Fragment.Holder, Capability.IStorage<Fragment.Holder> {
		public static final Default INSTANCE = new Default();
		
		@Nonnull
		@Override
		public Fragment<?> getFragment() {
			return Fragment.EMPTY;
		}
		
		@Override
		public void setFragment(@Nonnull Fragment<?> fragment) {
			//No
		}
		
		@Nullable
		@Override
		public INBT writeNBT(Capability<Fragment.Holder> capability, Fragment.Holder instance, Direction side) {
			//No
			return null;
		}
		
		@Override
		public void readNBT(Capability<Fragment.Holder> capability, Fragment.Holder instance, Direction side, INBT nbt) {
			//No
		}
	}
}
