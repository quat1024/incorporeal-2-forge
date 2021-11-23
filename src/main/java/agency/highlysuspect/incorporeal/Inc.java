package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.block.tile.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.block.tile.RedStringLiarTile;
import agency.highlysuspect.incorporeal.corporea.EmptyCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.WildcardCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.entity.IncEntityTypes;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.common.lib.LibMisc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Inc implements ModInitializer {
	public static final String MODID = "incorporeal";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	@Override
	public void onInitialize() {
		IncBlocks.register();
		IncItems.register();
		IncBlockEntityTypes.register();
		IncEntityTypes.register();
		IncSoundEvents.register();
		
		BotaniaAPI botania = BotaniaAPI.instance();
		botania.registerCorporeaNodeDetector(new RedStringLiarTile.NodeDetector());
		
		CorporeaHelper corporeaHelper = CorporeaHelper.instance();
		corporeaHelper.registerRequestMatcher(id("wildcard"), WildcardCorporeaRequestMatcher.class, nbt -> WildcardCorporeaRequestMatcher.INSTANCE);
		corporeaHelper.registerRequestMatcher(id("empty"), EmptyCorporeaRequestMatcher.class, nbt -> EmptyCorporeaRequestMatcher.INSTANCE);
	}
	
	//making a util class is for cowards
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MODID, path);
	}
	
	public static ResourceLocation botaniaId(String path) {
		return new ResourceLocation(LibMisc.MOD_ID, path);
	}
	
	public static ResourceLocation forgeId(String path) {
		return new ResourceLocation("forge", path);
	}
	
	public static <T> void reg(Registry<T> r, String name, T thing) {
		Registry.register(r, id(name), thing);
	}
	
	public static <T> T choose(Collection<T> things, Random random) {
		if(things instanceof List<?>) return ((List<T>) things).get(random.nextInt(things.size()));
		else {
			//This is shitty im sorry.
			List<T> thingsList = new ArrayList<>(things);
			return thingsList.get(random.nextInt(thingsList.size()));
		}
	}
	
	public static float rangeRemap(float value, float low1, float high1, float low2, float high2) {
		//The value goes from low1..high1 -> remap that range to 0..1
		float x = (value - low1) / (high1 - low1);
		//The value goes from 0..1 -> remap that range to low2..high2
		return x * (high2 - low2) + low2;
	}
	
	public static float sinDegrees(float in) {
		return Mth.sin((in % 360) * (float) (Math.PI / 180));
	}
	
	public static float cosDegrees(float in) {
		return Mth.cos((in % 360) * (float) (Math.PI / 180));
	}
}
