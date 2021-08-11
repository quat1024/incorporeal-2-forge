package agency.highlysuspect.rhododendrite;

import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import agency.highlysuspect.rhododendrite.block.tile.RhoTileTypes;
import agency.highlysuspect.rhododendrite.client.RhoClient;
import agency.highlysuspect.rhododendrite.computer.DataTypes;
import agency.highlysuspect.rhododendrite.computer.FragmentCapability;
import agency.highlysuspect.rhododendrite.datagen.RhoDatagen;
import agency.highlysuspect.rhododendrite.item.RhoItems;
import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
		
		modBus.addListener((FMLCommonSetupEvent e) -> {
			FragmentCapability.initialize();
			
			DataTypes.initialize();
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
	
	public static Direction positive(Direction.Axis axis) {
		if(axis == Direction.Axis.X) return Direction.EAST;
		else if(axis == Direction.Axis.Y) return Direction.UP;
		else return Direction.SOUTH; 
	}
	
	public static Direction negative(Direction.Axis axis) {
		if(axis == Direction.Axis.X) return Direction.WEST;
		else if(axis == Direction.Axis.Y) return Direction.DOWN;
		else return Direction.NORTH;
	}
}
