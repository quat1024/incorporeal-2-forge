package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.client.IncClient;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.botania.common.core.helper.MathHelper;
import vazkii.botania.common.lib.LibMisc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Inc implements ModInitializer {
	public static final String MODID = "incorporeal";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	//"WE HAVE CLIENT ENTRYPOINTS AT HOME"
	//public static final IncProxy proxy = DistExecutor.safeRunForDist(() -> IncClient::new, () -> IncProxy.Server::new);
	
	@Override
	public void onInitialize() {
//		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
//		
//		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, IncConfig.SPEC);
//		
//		modBus.addListener(IncDatagen::gatherData);
//		
//		modBus.addGenericListener(Block.class, IncBlocks::register);
//		modBus.addGenericListener(Item.class, IncItems::register);
//		modBus.addGenericListener(TileEntityType.class, IncTileTypes::register);
//		modBus.addGenericListener(EntityType.class, IncEntityTypes::register);
//		modBus.addGenericListener(SoundEvent.class, IncSoundEvents::register);
//		
//		modBus.addListener(PotionSoulCoreCollectorEntity::attributeEvent);
//		
//		modBus.addListener((FMLCommonSetupEvent event) -> {
//			SolidifiedRequest.Cap.initialize();
//			
//			BotaniaAPI botania = BotaniaAPI.instance();
//			botania.registerCorporeaNodeDetector(new RedStringLiarTile.NodeDetector());
//			
//			CorporeaHelper corporeaHelper = CorporeaHelper.instance();
//			corporeaHelper.registerRequestMatcher(id("wildcard"), WildcardCorporeaRequestMatcher.class, nbt -> WildcardCorporeaRequestMatcher.INSTANCE);
//			corporeaHelper.registerRequestMatcher(id("empty"), EmptyCorporeaRequestMatcher.class, nbt -> EmptyCorporeaRequestMatcher.INSTANCE);
//			
//			MinecraftForge.EVENT_BUS.addListener(CorporeaSoulCoreTile::corporeaIndexRequestEvent);
//			MinecraftForge.EVENT_BUS.addListener(TicketConjurerItem::chatEvent);
//			
//			MinecraftForge.EVENT_BUS.addGenericListener(TileEntity.class, IncCapEvents::attachTileCapabilities);
//			
//			MinecraftForge.EVENT_BUS.addListener(PotionSoulCoreCollectorEntity::healEvent);
//			MinecraftForge.EVENT_BUS.addListener(PotionSoulCoreCollectorEntity::attackEvent);
//			
//			MinecraftForge.EVENT_BUS.addListener(RedstoneRootCropBlock::interactEvent);
//			
//			IncNetwork.setup();
//		});
//		
//		proxy.setup();
	}
	
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
