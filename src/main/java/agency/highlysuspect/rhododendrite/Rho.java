package agency.highlysuspect.rhododendrite;

import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import agency.highlysuspect.rhododendrite.block.tile.RhoTileTypes;
import agency.highlysuspect.rhododendrite.client.RhoClient;
import agency.highlysuspect.rhododendrite.computer.CompoundCorporeaRequestMatcher;
import agency.highlysuspect.rhododendrite.computer.RhoCapEvents;
import agency.highlysuspect.rhododendrite.computer.RhodoFunnelableCapability;
import agency.highlysuspect.rhododendrite.datagen.RhoDatagen;
import agency.highlysuspect.rhododendrite.item.RhoItems;
import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.botania.api.corporea.CorporeaHelper;

@Mod("rhododendrite")
public class Rho {
	public static final String MODID = "rhododendrite";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	public static final RhoProxy proxy = DistExecutor.safeRunForDist(() -> RhoClient::new, () -> RhoProxy.Server::new);
	
	public Rho() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modBus.addListener(RhoDatagen::gatherData);
		
		modBus.addGenericListener(Block.class, RhoBlocks::register);
		modBus.addGenericListener(Item.class, RhoItems::register);
		modBus.addGenericListener(TileEntityType.class, RhoTileTypes::register);
		modBus.addGenericListener(Feature.class, (RegistryEvent.Register<Feature<?>> e) -> RhoBlocks.RHODODENDRITE.registerFeature(e.getRegistry()));
		
		modBus.addListener((FMLCommonSetupEvent e) -> {
			CorporeaHelper helper = CorporeaHelper.instance();
			helper.registerRequestMatcher(Rho.id("compound"), CompoundCorporeaRequestMatcher.class, CompoundCorporeaRequestMatcher::fromTag);
			
			RhodoFunnelableCapability.initialize();
			RhodoFunnelableCapability.registerBuiltinLooseFunnelables();
			
			MinecraftForge.EVENT_BUS.addGenericListener(TileEntity.class, RhoCapEvents::tileCaps);
			MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, RhoCapEvents::entCaps);
		});
		
		proxy.setup();
	}
	
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MODID, path);
	}
	
	public static <T extends ForgeRegistryEntry<T>> T reg(IForgeRegistry<T> r, String name, T thing) {
		thing.setRegistryName(id(name));
		r.register(thing);
		return thing;
	}
	
	public static void simpleBlockItems(IForgeRegistry<Item> r, Block... bs) {
		for(Block b : bs) simpleBlockItem(r, b);
	}
	
	public static void simpleBlockItem(IForgeRegistry<Item> r, Block b) {
		Preconditions.checkNotNull(b.getRegistryName(), "register the block first");
		
		BlockItem yes = new BlockItem(b, RhoItems.defaultProps());
		yes.setRegistryName(b.getRegistryName());
		r.register(yes);
	}
}
