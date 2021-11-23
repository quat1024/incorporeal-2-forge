package agency.highlysuspect.incorporeal.block.tile;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.Util;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.EnumMap;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class IncTileTypes {
	public static final BlockEntityType<RedStringLiarTile> RED_STRING_LIAR = BlockEntityType.Builder.of(RedStringLiarTile::new, IncBlocks.RED_STRING_LIAR).build(null);
	
	//forgive me for this - I can only assign one renderer per tileentitytype
	public static final Map<DyeColor, BlockEntityType<UnstableCubeTile>> UNSTABLE_CUBES = Util.make(new EnumMap<>(DyeColor.class), m -> {
		for(DyeColor color : DyeColor.values()) m.put(color, BlockEntityType.Builder.of(() -> new UnstableCubeTile(color), IncBlocks.UNSTABLE_CUBES.get(color)).build(null));
	});
	
	public static final BlockEntityType<EnderSoulCoreTile> ENDER_SOUL_CORE = BlockEntityType.Builder.of(EnderSoulCoreTile::new, IncBlocks.ENDER_SOUL_CORE).build(null);
	public static final BlockEntityType<CorporeaSoulCoreTile> CORPOREA_SOUL_CORE = BlockEntityType.Builder.of(CorporeaSoulCoreTile::new, IncBlocks.CORPOREA_SOUL_CORE).build(null);
	public static final BlockEntityType<PotionSoulCoreTile> POTION_SOUL_CORE = BlockEntityType.Builder.of(PotionSoulCoreTile::new, IncBlocks.POTION_SOUL_CORE).build(null);
	
	public static final BlockEntityType<SanvocaliaSubTile> SANVOCALIA_BIG = BlockEntityType.Builder.of(SanvocaliaSubTile::big, IncBlocks.SANVOCALIA, IncBlocks.FLOATING_SANVOCALIA).build(null);
	public static final BlockEntityType<SanvocaliaSubTile> SANVOCALIA_SMALL = BlockEntityType.Builder.of(SanvocaliaSubTile::small, IncBlocks.SMALL_SANVOCALIA, IncBlocks.SMALL_FLOATING_SANVOCALIA).build(null);
	
	public static final BlockEntityType<FunnySubTile> FUNNY_BIG = BlockEntityType.Builder.of(FunnySubTile::big, IncBlocks.FUNNY, IncBlocks.FLOATING_FUNNY).build(null);
	public static final BlockEntityType<FunnySubTile> FUNNY_SMALL = BlockEntityType.Builder.of(FunnySubTile::small, IncBlocks.SMALL_FUNNY, IncBlocks.SMALL_FLOATING_FUNNY).build(null);
	
	public static void register(RegistryEvent.Register<BlockEntityType<?>> event) {
		IForgeRegistry<BlockEntityType<?>> r = event.getRegistry();
		
		Inc.reg(r, "red_string_liar", RED_STRING_LIAR);
		UNSTABLE_CUBES.forEach((color, type) -> Inc.reg(r, color.getSerializedName() + "_unstable_cube", type));
		
		Inc.reg(r, "ender_soul_core", ENDER_SOUL_CORE);
		Inc.reg(r, "corporea_soul_core", CORPOREA_SOUL_CORE);
		Inc.reg(r, "potion_soul_core", POTION_SOUL_CORE);
		
		Inc.reg(r, "sanvocalia", SANVOCALIA_BIG);
		Inc.reg(r, "sanvocalia_small", SANVOCALIA_SMALL);
		
		Inc.reg(r, "funny", FUNNY_BIG);
		Inc.reg(r, "funny_small", FUNNY_SMALL);
	}
}
