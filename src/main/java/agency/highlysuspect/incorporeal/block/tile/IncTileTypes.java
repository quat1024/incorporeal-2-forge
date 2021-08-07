package agency.highlysuspect.incorporeal.block.tile;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

//@SuppressWarnings("ConstantConditions")
public class IncTileTypes {
	//public static final TileEntityType<CorporeaSolidifierTile> CORPOREA_SOLIDIFIER = TileEntityType.Builder.create(CorporeaSolidifierTile::new, IncBlocks.CORPOREA_SOLIDIFIER).build(null); 
	
	public static void register(RegistryEvent.Register<TileEntityType<?>> event) {
		IForgeRegistry<TileEntityType<?>> r = event.getRegistry();
		
		//Init.reg(r, "corporea_solidifier", CORPOREA_SOLIDIFIER);
	}
}
