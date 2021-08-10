package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class RhoTileTypes {
	public static final TileEntityType<CoreTile> CORE = TileEntityType.Builder.create(CoreTile::new, RhoBlocks.CORE).build(null);
	public static final TileEntityType<AwakenedLogTile> AWAKENED_LOG = TileEntityType.Builder.create(AwakenedLogTile::new, RhoBlocks.AWAKENED_LOG).build(null);
	
	public static void register(RegistryEvent.Register<TileEntityType<?>> event) {
		IForgeRegistry<TileEntityType<?>> r = event.getRegistry();
		
		Rho.reg(r, "core", CORE);
	}
}
