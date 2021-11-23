package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

@SuppressWarnings("ConstantConditions")
public class RhoTileTypes {
	public static final TileEntityType<RhodoCellTile> CELL = TileEntityType.Builder.of(RhodoCellTile::new, RhoBlocks.CELL).build(null);
	public static final TileEntityType<RhodoOpTile> OP = TileEntityType.Builder.of(RhodoOpTile::new, RhoBlocks.OP).build(null);
	public static final TileEntityType<RhodoFunnelTile> FUNNEL = TileEntityType.Builder.of(RhodoFunnelTile::new, RhoBlocks.FUNNEL).build(null);
	
	public static void register(RegistryEvent.Register<TileEntityType<?>> event) {
		IForgeRegistry<TileEntityType<?>> r = event.getRegistry();
		
		Rho.reg(r, "cell", CELL);
		Rho.reg(r, "op", OP);
		Rho.reg(r, "funnel", FUNNEL);
	}
}
