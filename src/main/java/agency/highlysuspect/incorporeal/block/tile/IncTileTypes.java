package agency.highlysuspect.incorporeal.block.tile;

import agency.highlysuspect.incorporeal.Init;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

@SuppressWarnings("ConstantConditions")
public class IncTileTypes {
	public static final TileEntityType<RedStringLiarTile> RED_STRING_LIAR = TileEntityType.Builder.create(RedStringLiarTile::new, IncBlocks.RED_STRING_LIAR).build(null);
	public static final TileEntityType<EnderSoulCoreTile> ENDER_SOUL_CORE = TileEntityType.Builder.create(EnderSoulCoreTile::new, IncBlocks.ENDER_SOUL_CORE).build(null);
	public static final TileEntityType<CorporeaSoulCoreTile> CORPOREA_SOUL_CORE = TileEntityType.Builder.create(CorporeaSoulCoreTile::new, IncBlocks.CORPOREA_SOUL_CORE).build(null);
	
	public static final TileEntityType<SanvocaliaSubTile> SANVOCALIA_BIG = TileEntityType.Builder.create(SanvocaliaSubTile::big, IncBlocks.SANVOCALIA, IncBlocks.FLOATING_SANVOCALIA).build(null);
	public static final TileEntityType<SanvocaliaSubTile> SANVOCALIA_SMALL = TileEntityType.Builder.create(SanvocaliaSubTile::small, IncBlocks.SMALL_SANVOCALIA, IncBlocks.SMALL_FLOATING_SANVOCALIA).build(null);
	
	public static final TileEntityType<FunnySubTile> FUNNY_BIG = TileEntityType.Builder.create(FunnySubTile::big, IncBlocks.FUNNY, IncBlocks.FLOATING_FUNNY).build(null);
	public static final TileEntityType<FunnySubTile> FUNNY_SMALL = TileEntityType.Builder.create(FunnySubTile::small, IncBlocks.SMALL_FUNNY, IncBlocks.SMALL_FLOATING_FUNNY).build(null);
	
	public static void register(RegistryEvent.Register<TileEntityType<?>> event) {
		IForgeRegistry<TileEntityType<?>> r = event.getRegistry();
		
		Init.reg(r, "red_string_liar", RED_STRING_LIAR);
		Init.reg(r, "ender_soul_core", ENDER_SOUL_CORE);
		Init.reg(r, "corporea_soul_core", CORPOREA_SOUL_CORE);
		
		Init.reg(r, "sanvocalia", SANVOCALIA_BIG);
		Init.reg(r, "sanvocalia_small", SANVOCALIA_SMALL);
		
		Init.reg(r, "funny", FUNNY_BIG);
		Init.reg(r, "funny_small", FUNNY_SMALL);
	}
}
