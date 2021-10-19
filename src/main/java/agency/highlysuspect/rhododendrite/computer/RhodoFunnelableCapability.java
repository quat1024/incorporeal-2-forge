package agency.highlysuspect.rhododendrite.computer;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class RhodoFunnelableCapability {
	@CapabilityInject(RhodoFunnelable.class) public static Capability<RhodoFunnelable> INSTANCE;
	
	public static final ArrayList<RhodoFunnelable.Loose> LOOSE_FUNNELABLES = new ArrayList<>();
	
	public static void initialize() {
		CapabilityManager.INSTANCE.register(RhodoFunnelable.class, Bwbwbwbw.INSTANCE, () -> Bwbwbwbw.INSTANCE);
	}
	
	public static void registerLooseFunnelable(RhodoFunnelable.Loose loose) {
		//yeah i dont fuckin know how multithreaded modloading works
		//i think you just slap synchronized on things and then its good
		//and it probably works
		//and if it doesnt work, just add more synchronized until it works
		//not like anyone's gonna make an addon for this mod anyways :megajoy:
		synchronized(LOOSE_FUNNELABLES) {
			LOOSE_FUNNELABLES.add(loose);
		}
	}
	
	public static void registerBuiltinLooseFunnelables() {
		registerLooseFunnelable(MiscFunnelables.DUST);
		registerLooseFunnelable(MiscFunnelables.REPEATER);
		registerLooseFunnelable(MiscFunnelables.CORPOREA_SOLIDIFIER);
	}
	
	public static class Bwbwbwbw implements RhodoFunnelable, Capability.IStorage<RhodoFunnelable> {
		public static final Bwbwbwbw INSTANCE = new Bwbwbwbw();
		
		@Nullable
		@Override
		public INBT writeNBT(Capability<RhodoFunnelable> capability, RhodoFunnelable instance, Direction side) {
			return new CompoundNBT();
		}
		
		@Override
		public void readNBT(Capability<RhodoFunnelable> capability, RhodoFunnelable instance, Direction side, INBT nbt) {
			//No
		}
	}
}
