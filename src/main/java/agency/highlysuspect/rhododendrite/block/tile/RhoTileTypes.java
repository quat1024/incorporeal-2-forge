package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

@SuppressWarnings("ConstantConditions")
public class RhoTileTypes {
	public static final BlockEntityType<RhodoCellTile> CELL = BlockEntityType.Builder.of(RhodoCellTile::new, RhoBlocks.CELL).build(null);
	public static final BlockEntityType<RhodoOpTile> OP = BlockEntityType.Builder.of(RhodoOpTile::new, RhoBlocks.OP).build(null);
	public static final BlockEntityType<RhodoFunnelTile> FUNNEL = BlockEntityType.Builder.of(RhodoFunnelTile::new, RhoBlocks.FUNNEL).build(null);
	
	public static void register(RegistryEvent.Register<BlockEntityType<?>> event) {
		IForgeRegistry<BlockEntityType<?>> r = event.getRegistry();
		
		Rho.reg(r, "cell", CELL);
		Rho.reg(r, "op", OP);
		Rho.reg(r, "funnel", FUNNEL);
	}
}
