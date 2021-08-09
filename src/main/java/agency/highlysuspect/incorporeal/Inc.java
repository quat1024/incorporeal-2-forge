package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.block.tile.CorporeaSoulCoreTile;
import agency.highlysuspect.incorporeal.block.tile.IncTileTypes;
import agency.highlysuspect.incorporeal.block.tile.RedStringLiarTile;
import agency.highlysuspect.incorporeal.client.IncClient;
import agency.highlysuspect.incorporeal.corporea.WildcardCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.datagen.IncDatagen;
import agency.highlysuspect.incorporeal.entity.IncEntityTypes;
import agency.highlysuspect.incorporeal.item.IncItems;
import agency.highlysuspect.incorporeal.item.TicketConjurerItem;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.common.lib.LibMisc;

import java.util.List;
import java.util.Random;

@Mod("incorporeal")
public class Inc {
	public static final String MODID = "incorporeal";
	public static final Logger LOG = LogManager.getLogger(MODID);
	
	//"WE HAVE CLIENT ENTRYPOINTS AT HOME"
	public static final IncProxy proxy = DistExecutor.safeRunForDist(() -> IncClient::new, () -> IncProxy.Server::new);
	
	public Inc() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modBus.addListener(IncDatagen::gatherData);
		
		modBus.addGenericListener(Block.class, IncBlocks::register);
		modBus.addGenericListener(Item.class, IncItems::register);
		modBus.addGenericListener(TileEntityType.class, IncTileTypes::register);
		modBus.addGenericListener(EntityType.class, IncEntityTypes::register);
		
		modBus.addListener((FMLCommonSetupEvent event) -> {
			BotaniaAPI botania = BotaniaAPI.instance();
			botania.registerCorporeaNodeDetector(new RedStringLiarTile.NodeDetector());
			
			CorporeaHelper corporeaHelper = CorporeaHelper.instance();
			corporeaHelper.registerRequestMatcher(id("wildcard"), WildcardCorporeaRequestMatcher.class, nbt -> WildcardCorporeaRequestMatcher.INSTANCE);
			
			MinecraftForge.EVENT_BUS.addListener(CorporeaSoulCoreTile::corporeaIndexRequestEvent);
			MinecraftForge.EVENT_BUS.addListener(TicketConjurerItem::chatEvent);
			
			IncNetwork.setup();
		});
		
		proxy.setup();
	}
	
	//making a util class is for cowards
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MODID, path);
	}
	
	public static ResourceLocation botaniaId(String path) {
		return new ResourceLocation(LibMisc.MOD_ID, path);
	}
	
	public static <T extends ForgeRegistryEntry<T>> void reg(IForgeRegistry<T> r, String name, T thing) {
		thing.setRegistryName(id(name));
		r.register(thing);
	}
	
	public static <T> T choose(List<T> things, Random random) {
		return things.get(random.nextInt(things.size()));
	}
	
	public static float rangeRemap(float value, float low1, float high1, float low2, float high2) {
		//The value goes from low1..high1 -> remap that range to 0..1
		float x = (value - low1) / (high1 - low1);
		//The value goes from 0..1 -> remap that range to low2..high2
		return x * (high2 - low2) + low2;
	}
	
	public static float sinDegrees(float in) {
		return MathHelper.sin((in % 360) * (float) (Math.PI / 180));
	}
	
	public static float cosDegrees(float in) {
		return MathHelper.cos((in % 360) * (float) (Math.PI / 180));
	}
}
