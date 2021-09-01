package agency.highlysuspect.rhododendrite.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.rhododendrite.Rho;
import net.minecraft.util.ResourceLocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MatcherColors {
	//N.B. only has to be concurrent because of forge modloading crap
	private static final Map<ResourceLocation, Integer> colors = new ConcurrentHashMap<>();
	
	public static void registerColor(ResourceLocation id, int color) {
		colors.put(id, color);
	}
	
	public static int getColor(ResourceLocation id) {
		return colors.getOrDefault(id, 0xFF00FF);
	}
	
	public static void registerBuiltinColors() {
		//incorporeal:wildcard is not registered since it's an implementation-detail matcher, not really exposed to retainers and such.
		registerColor(Inc.id("empty"), 0x777777);
		registerColor(Rho.id("compound"), 0xFF8D27);
		registerColor(Inc.botaniaId("string"), 0x4090FF);
		registerColor(Inc.botaniaId("item_stack"), 0xFF60A0);
	}
}
