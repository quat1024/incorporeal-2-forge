package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.block.tile.IncTileTypes;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vazkii.botania.common.lib.LibMisc;

@Mod("incorporeal")
public class Init {
	public static final String MODID = "incorporeal";
	public static final Logger LOG = LogManager.getLogger(MODID);
	
	public Init() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modBus.addGenericListener(Block.class, IncBlocks::register);
		modBus.addGenericListener(Item.class, IncItems::register);
		
		modBus.addGenericListener(TileEntityType.class, IncTileTypes::register);
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
}
