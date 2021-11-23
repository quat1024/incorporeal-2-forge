package agency.highlysuspect.incorporeal;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class IncSoundEvents {
	public static final SoundEvent UNSTABLE = cbt(Inc.id("unstable"));
	
	private static SoundEvent cbt(ResourceLocation id) {
		//Vanilla takes an Identifier and, inexplicably, Forge also takes an identifier...?
		//The Forge Registry system is incredibly well thought out and I'm glad to use it in my mods:))))))
		//Also vanilla only exposes the id getter on the clientside lol
		return new SoundEvent(id).setRegistryName(id);
	}
	
	public static void register(RegistryEvent.Register<SoundEvent> event) {
		IForgeRegistry<SoundEvent> r = event.getRegistry();
		
		r.register(UNSTABLE);
	}
}
