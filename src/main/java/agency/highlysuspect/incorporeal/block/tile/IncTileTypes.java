package agency.highlysuspect.incorporeal.block.tile;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import net.minecraft.item.DyeColor;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Util;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.EnumMap;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class IncTileTypes {
	public static final TileEntityType<RedStringLiarTile> RED_STRING_LIAR = TileEntityType.Builder.create(RedStringLiarTile::new, IncBlocks.RED_STRING_LIAR).build(null);
	
	//forgive me for this - I can only assign one renderer per tileentitytype
	public static final Map<DyeColor, TileEntityType<UnstableCubeTile>> UNSTABLE_CUBES = Util.make(new EnumMap<>(DyeColor.class), m -> {
		for(DyeColor color : DyeColor.values()) m.put(color, TileEntityType.Builder.create(() -> new UnstableCubeTile(color), IncBlocks.UNSTABLE_CUBES.get(color)).build(null));
	});
	
	public static final TileEntityType<EnderSoulCoreTile> ENDER_SOUL_CORE = TileEntityType.Builder.create(EnderSoulCoreTile::new, IncBlocks.ENDER_SOUL_CORE).build(null);
	public static final TileEntityType<CorporeaSoulCoreTile> CORPOREA_SOUL_CORE = TileEntityType.Builder.create(CorporeaSoulCoreTile::new, IncBlocks.CORPOREA_SOUL_CORE).build(null);
	
	public static final TileEntityType<SanvocaliaSubTile> SANVOCALIA_BIG = TileEntityType.Builder.create(SanvocaliaSubTile::big, IncBlocks.SANVOCALIA, IncBlocks.FLOATING_SANVOCALIA).build(null);
	public static final TileEntityType<SanvocaliaSubTile> SANVOCALIA_SMALL = TileEntityType.Builder.create(SanvocaliaSubTile::small, IncBlocks.SMALL_SANVOCALIA, IncBlocks.SMALL_FLOATING_SANVOCALIA).build(null);
	
	public static final TileEntityType<FunnySubTile> FUNNY_BIG = TileEntityType.Builder.create(FunnySubTile::big, IncBlocks.FUNNY, IncBlocks.FLOATING_FUNNY).build(null);
	public static final TileEntityType<FunnySubTile> FUNNY_SMALL = TileEntityType.Builder.create(FunnySubTile::small, IncBlocks.SMALL_FUNNY, IncBlocks.SMALL_FLOATING_FUNNY).build(null);
	
	public static void register(RegistryEvent.Register<TileEntityType<?>> event) {
		IForgeRegistry<TileEntityType<?>> r = event.getRegistry();
		
		Inc.reg(r, "red_string_liar", RED_STRING_LIAR);
		UNSTABLE_CUBES.forEach((color, type) -> Inc.reg(r, color.getString() + "_unstable_cube", type));
		
		Inc.reg(r, "ender_soul_core", ENDER_SOUL_CORE);
		Inc.reg(r, "corporea_soul_core", CORPOREA_SOUL_CORE);
		
		Inc.reg(r, "sanvocalia", SANVOCALIA_BIG);
		Inc.reg(r, "sanvocalia_small", SANVOCALIA_SMALL);
		
		Inc.reg(r, "funny", FUNNY_BIG);
		Inc.reg(r, "funny_small", FUNNY_SMALL);
	}
}
