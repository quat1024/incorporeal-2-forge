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
	
	public static final TileEntityType<OpcodeTile> OPCODE = TileEntityType.Builder.create(OpcodeTile::new,
		//TODO: It would be nice if people could add their own blocks to this tile entity.
		// also i should check every time I `instanceof` to see if i really need to do that.
		// maybe I can make it all capability based.
		RhoBlocks.TEST1, RhoBlocks.TEST2, RhoBlocks.PUSH, RhoBlocks.PULL, RhoBlocks.REORIENT
	).build(null);
	
	public static void register(RegistryEvent.Register<TileEntityType<?>> event) {
		IForgeRegistry<TileEntityType<?>> r = event.getRegistry();
		
		Rho.reg(r, "core", CORE);
		Rho.reg(r, "awakened_log", AWAKENED_LOG);
		
		Rho.reg(r, "opcode", OPCODE);
	}
}
