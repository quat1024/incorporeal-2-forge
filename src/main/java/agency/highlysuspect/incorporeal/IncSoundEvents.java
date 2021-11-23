package agency.highlysuspect.incorporeal;

import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;

public class IncSoundEvents {
	public static final SoundEvent UNSTABLE = new SoundEvent(Inc.id("unstable"));
	
	public static void register() {
		registerSoundEvent(UNSTABLE);
	}
	
	private static void registerSoundEvent(SoundEvent s) {
		Registry.register(Registry.SOUND_EVENT, s.getLocation(), s);
	}
}
