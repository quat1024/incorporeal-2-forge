package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.tile.FunnySubTile;
import agency.highlysuspect.incorporeal.block.tile.IncTileTypes;
import agency.highlysuspect.incorporeal.block.tile.SanvocaliaSubTile;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.level.effect.MobEffects;
import net.minecraft.level.item.DyeColor;
import net.minecraft.level.level.block.Block;
import net.minecraft.level.level.block.Blocks;
import net.minecraft.level.level.block.SoundType;
import net.minecraft.level.level.block.state.BlockBehaviour;
import net.minecraft.level.level.material.Material;
import vazkii.botania.common.block.BlockFloatingSpecialFlower;
import vazkii.botania.common.block.BlockSpecialFlower;
import vazkii.botania.common.block.ModBlocks;

import java.util.EnumMap;
import java.util.Map;

public class IncBlocks {
	public static final CorporeaSolidifierBlock CORPOREA_SOLIDIFIER = new CorporeaSolidifierBlock(BlockBehaviour.Properties.copy(ModBlocks.corporeaRetainer));
	public static final RedStringLiarBlock RED_STRING_LIAR = new RedStringLiarBlock(BlockBehaviour.Properties.copy(ModBlocks.redStringContainer));
	public static final FrameTinkererBlock FRAME_TINKERER = new FrameTinkererBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS));
	public static final CorporeaRetainerEvaporatorBlock CORPOREA_RETAINER_EVAPORATOR = new CorporeaRetainerEvaporatorBlock(BlockBehaviour.Properties.copy(ModBlocks.corporeaRetainer));
	
	public static final Map<DyeColor, UnstableCubeBlock> UNSTABLE_CUBES = Util.make(new EnumMap<>(DyeColor.class), m -> {
		for(DyeColor color : DyeColor.values()) m.put(color, new UnstableCubeBlock(BlockBehaviour.Properties.of(Material.METAL, color.getMaterialColor())
			.strength(5f)
			.isRedstoneConductor((state, level, pos) -> false)
			.noOcclusion(), color));
	});
	
	public static final Block.Properties soulCoreProps = BlockBehaviour.Properties.of(Material.GRASS)
		.strength(1f)
		.isRedstoneConductor((state, level, pos) -> false)
		.noOcclusion();
	public static final Block ENDER_SOUL_CORE = new SoulCoreBlock(soulCoreProps, () -> IncTileTypes.ENDER_SOUL_CORE);
	public static final Block CORPOREA_SOUL_CORE = new SoulCoreBlock(soulCoreProps, () -> IncTileTypes.CORPOREA_SOUL_CORE);
	public static final Block POTION_SOUL_CORE = new SoulCoreBlock(soulCoreProps, () -> IncTileTypes.POTION_SOUL_CORE);
	
	public static final Block NATURAL_REPEATER = new CrappyRepeaterBlock(BlockBehaviour.Properties.of(Material.DECORATION).instabreak().sound(SoundType.CROP).noOcclusion());
	public static final Block NATURAL_COMPARATOR = new CrappyComparatorBlock(BlockBehaviour.Properties.of(Material.DECORATION).instabreak().sound(SoundType.CROP).noOcclusion());
	public static final RedstoneRootCropBlock REDSTONE_ROOT_CROP = new RedstoneRootCropBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP).noOcclusion());
	
	public static final Block.Properties flowerProps = BlockBehaviour.Properties.copy(Blocks.POPPY);
	public static final Block.Properties floatingProps = ModBlocks.FLOATING_PROPS;
	
	public static final BlockSpecialFlower SANVOCALIA = new BlockSpecialFlower(MobEffects.GLOWING, 20, flowerProps, SanvocaliaSubTile::big);
	public static final BlockSpecialFlower SMALL_SANVOCALIA = new BlockSpecialFlower(MobEffects.GLOWING, 5, flowerProps, SanvocaliaSubTile::small);
	public static final BlockFloatingSpecialFlower FLOATING_SANVOCALIA = new BlockFloatingSpecialFlower(floatingProps, SanvocaliaSubTile::big);
	public static final BlockFloatingSpecialFlower SMALL_FLOATING_SANVOCALIA = new BlockFloatingSpecialFlower(floatingProps, SanvocaliaSubTile::small);
	
	public static final BlockSpecialFlower FUNNY = new BlockSpecialFlower(MobEffects.BAD_OMEN, 20, flowerProps, FunnySubTile::big);
	public static final BlockSpecialFlower SMALL_FUNNY = new BlockSpecialFlower(MobEffects.BAD_OMEN, 5, flowerProps, FunnySubTile::small);
	public static final BlockFloatingSpecialFlower FLOATING_FUNNY = new BlockFloatingSpecialFlower(floatingProps, FunnySubTile::big);
	public static final BlockFloatingSpecialFlower SMALL_FLOATING_FUNNY = new BlockFloatingSpecialFlower(floatingProps, FunnySubTile::small);
	
	public static void register() {
		Registry<Block> r = Registry.BLOCK;
		
		//misc blocks!
		Inc.reg(r, "corporea_solidifier", CORPOREA_SOLIDIFIER);
		Inc.reg(r, "red_string_liar", RED_STRING_LIAR);
		Inc.reg(r, "frame_tinkerer", FRAME_TINKERER);
		Inc.reg(r, "corporea_retainer_evaporator", CORPOREA_RETAINER_EVAPORATOR);
		
		UNSTABLE_CUBES.values().forEach(block -> Inc.reg(r, block.color.getSerializedName() + "_unstable_cube", block));
		
		Inc.reg(r, "ender_soul_core", ENDER_SOUL_CORE);
		Inc.reg(r, "corporea_soul_core", CORPOREA_SOUL_CORE);
		Inc.reg(r, "potion_soul_core", POTION_SOUL_CORE);
		
		Inc.reg(r, "natural_repeater", NATURAL_REPEATER);
		Inc.reg(r, "natural_comparator", NATURAL_COMPARATOR);
		Inc.reg(r, "redstone_root_crop", REDSTONE_ROOT_CROP);
		
		//flowers
		//this is the block ID scheme that botania uses
		Inc.reg(r, "sanvocalia", SANVOCALIA);
		Inc.reg(r, "sanvocalia_chibi", SMALL_SANVOCALIA);
		Inc.reg(r, "floating_sanvocalia", FLOATING_SANVOCALIA);
		Inc.reg(r, "floating_sanvocalia_chibi", SMALL_FLOATING_SANVOCALIA);
		
		Inc.reg(r, "funny", FUNNY);
		Inc.reg(r, "funny_chibi", SMALL_FUNNY);
		Inc.reg(r, "floating_funny", FLOATING_FUNNY);
		Inc.reg(r, "floating_funny_chibi", SMALL_FLOATING_FUNNY);
	}
}
