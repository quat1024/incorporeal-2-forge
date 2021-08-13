package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

@SuppressWarnings("ConstantConditions")
public class RhoTileTypes {
	public static final TileEntityType<CoreTile> CORE = TileEntityType.Builder.create(CoreTile::new, RhoBlocks.CORE).build(null);
	public static final TileEntityType<AwakenedLogTile> AWAKENED_LOG = TileEntityType.Builder.create(AwakenedLogTile::new, RhoBlocks.AWAKENED_LOG).build(null);
	
	public static final TileEntityType<OpcodeTile> OPCODE = TileEntityType.Builder.create(OpcodeTile::new, RhoBlocks.OPCODE).build(null);
	public static final TileEntityType<ConditionTile> CONDITION = TileEntityType.Builder.create(ConditionTile::new, RhoBlocks.CONDITION).build(null);
	public static final TileEntityType<FunnelTile> FUNNEL = TileEntityType.Builder.create(FunnelTile::new, RhoBlocks.FUNNEL).build(null);
	
	public static void register(RegistryEvent.Register<TileEntityType<?>> event) {
		IForgeRegistry<TileEntityType<?>> r = event.getRegistry();
		
		Rho.reg(r, "core", CORE);
		Rho.reg(r, "awakened_log", AWAKENED_LOG);
		
		Rho.reg(r, "opcode", OPCODE);
		Rho.reg(r, "condition", CONDITION);
		Rho.reg(r, "funnel", FUNNEL);
	}
}
