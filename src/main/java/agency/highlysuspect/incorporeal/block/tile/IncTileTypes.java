package agency.highlysuspect.incorporeal.block.tile;

import agency.highlysuspect.incorporeal.Init;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

@SuppressWarnings("ConstantConditions")
public class IncTileTypes {
	public static final TileEntityType<SanvocaliaSubTile> SANVOCALIA_BIG = TileEntityType.Builder.create(SanvocaliaSubTile::big, IncBlocks.SANVOCALIA, IncBlocks.FLOATING_SANVOCALIA).build(null);
	public static final TileEntityType<SanvocaliaSubTile> SANVOCALIA_SMALL = TileEntityType.Builder.create(SanvocaliaSubTile::small, IncBlocks.SMALL_SANVOCALIA, IncBlocks.SMALL_FLOATING_SANVOCALIA).build(null);
	
	public static void register(RegistryEvent.Register<TileEntityType<?>> event) {
		IForgeRegistry<TileEntityType<?>> r = event.getRegistry();
		
		Init.reg(r, "sanvocalia", SANVOCALIA_BIG);
		Init.reg(r, "sanvocalia_small", SANVOCALIA_SMALL);
	}
}
