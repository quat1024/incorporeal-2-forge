package agency.highlysuspect.incorporeal.block.tile;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.Util;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.EnumMap;
import java.util.Map;

@SuppressWarnings("ConstantConditions")
public class IncBlockEntityTypes {
	public static final BlockEntityType<RedStringLiarTile> RED_STRING_LIAR = FabricBlockEntityTypeBuilder.create(RedStringLiarTile::new, IncBlocks.RED_STRING_LIAR).build();
	
	//forgive me for this - I can only assign one renderer per tileentitytype
	public static final Map<DyeColor, BlockEntityType<UnstableCubeTile>> UNSTABLE_CUBES = Util.make(new EnumMap<>(DyeColor.class), m -> {
		for(DyeColor color : DyeColor.values()) m.put(color, FabricBlockEntityTypeBuilder.create(() -> new UnstableCubeTile(color), IncBlocks.UNSTABLE_CUBES.get(color)).build());
	});
	
	public static final BlockEntityType<EnderSoulCoreTile> ENDER_SOUL_CORE = FabricBlockEntityTypeBuilder.create(EnderSoulCoreTile::new, IncBlocks.ENDER_SOUL_CORE).build();
	public static final BlockEntityType<CorporeaSoulCoreTile> CORPOREA_SOUL_CORE = FabricBlockEntityTypeBuilder.create(CorporeaSoulCoreTile::new, IncBlocks.CORPOREA_SOUL_CORE).build();
	public static final BlockEntityType<PotionSoulCoreTile> POTION_SOUL_CORE = FabricBlockEntityTypeBuilder.create(PotionSoulCoreTile::new, IncBlocks.POTION_SOUL_CORE).build();
	
	public static final BlockEntityType<SanvocaliaSubTile> SANVOCALIA_BIG = FabricBlockEntityTypeBuilder.create(SanvocaliaSubTile::big, IncBlocks.SANVOCALIA, IncBlocks.FLOATING_SANVOCALIA).build();
	public static final BlockEntityType<SanvocaliaSubTile> SANVOCALIA_SMALL = FabricBlockEntityTypeBuilder.create(SanvocaliaSubTile::small, IncBlocks.SMALL_SANVOCALIA, IncBlocks.SMALL_FLOATING_SANVOCALIA).build();
	
	public static final BlockEntityType<FunnySubTile> FUNNY_BIG = FabricBlockEntityTypeBuilder.create(FunnySubTile::big, IncBlocks.FUNNY, IncBlocks.FLOATING_FUNNY).build();
	public static final BlockEntityType<FunnySubTile> FUNNY_SMALL = FabricBlockEntityTypeBuilder.create(FunnySubTile::small, IncBlocks.SMALL_FUNNY, IncBlocks.SMALL_FLOATING_FUNNY).build();
	
	public static void register() {
		Registry<BlockEntityType<?>> r = Registry.BLOCK_ENTITY_TYPE;
		
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
