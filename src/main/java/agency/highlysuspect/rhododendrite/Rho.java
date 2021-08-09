package agency.highlysuspect.rhododendrite;

import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import agency.highlysuspect.rhododendrite.client.RhoClient;
import agency.highlysuspect.rhododendrite.datagen.RhoDatagen;
import agency.highlysuspect.rhododendrite.item.RhoItems;
import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;

@Mod("rhododendrite")
public class Rho {
	public static final String MODID = "rhododendrite";
	
	public static final RhoProxy proxy = DistExecutor.safeRunForDist(() -> RhoClient::new, () -> RhoProxy.Server::new);
	
	public Rho() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modBus.addListener(RhoDatagen::gatherData);
		
		modBus.addGenericListener(Block.class, RhoBlocks::register);
		modBus.addGenericListener(Item.class, RhoItems::register);
		
		proxy.setup();
	}
	
	public static ResourceLocation id(String path) {
		return new ResourceLocation(MODID, path);
	}
	
	public static <T extends ForgeRegistryEntry<T>> void reg(IForgeRegistry<T> r, String name, T thing) {
		thing.setRegistryName(id(name));
		r.register(thing);
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
