package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.tile.FunnySubTile;
import agency.highlysuspect.incorporeal.block.tile.IncTileTypes;
import agency.highlysuspect.incorporeal.block.tile.SanvocaliaSubTile;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.DyeColor;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.Util;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import vazkii.botania.api.subtile.TileEntitySpecialFlower;
import vazkii.botania.common.block.BlockFloatingSpecialFlower;
import vazkii.botania.common.block.BlockSpecialFlower;
import vazkii.botania.common.block.ModBlocks;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class IncBlocks {
	public static final CorporeaSolidifierBlock CORPOREA_SOLIDIFIER = new CorporeaSolidifierBlock(AbstractBlock.Properties.from(ModBlocks.corporeaRetainer));
	public static final RedStringLiarBlock RED_STRING_LIAR = new RedStringLiarBlock(AbstractBlock.Properties.from(ModBlocks.redStringContainer));
	public static final FrameTinkererBlock FRAME_TINKERER = new FrameTinkererBlock(AbstractBlock.Properties.from(Blocks.OAK_PLANKS));
	public static final CorporeaRetainerEvaporatorBlock CORPOREA_RETAINER_EVAPORATOR = new CorporeaRetainerEvaporatorBlock(AbstractBlock.Properties.from(ModBlocks.corporeaRetainer));
	
	public static final Map<DyeColor, UnstableCubeBlock> UNSTABLE_CUBES = Util.make(new EnumMap<>(DyeColor.class), m -> {
		for(DyeColor color : DyeColor.values()) m.put(color, new UnstableCubeBlock(AbstractBlock.Properties.create(Material.IRON, color.getMapColor())
			.hardnessAndResistance(5f)
			.setOpaque((state, world, pos) -> false)
			.notSolid(), color));
	});
	
	public static final Block.Properties soulCoreProps = AbstractBlock.Properties.create(Material.ORGANIC)
		.hardnessAndResistance(1f)
		.setOpaque((state, world, pos) -> false)
		.notSolid();
	public static final Block ENDER_SOUL_CORE = new SoulCoreBlock(soulCoreProps, () -> IncTileTypes.ENDER_SOUL_CORE);
	public static final Block CORPOREA_SOUL_CORE = new SoulCoreBlock(soulCoreProps, () -> IncTileTypes.CORPOREA_SOUL_CORE);
	public static final Block POTION_SOUL_CORE = new SoulCoreBlock(soulCoreProps, () -> IncTileTypes.POTION_SOUL_CORE);
	
	public static final Block NATURAL_REPEATER = new CrappyRepeaterBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS).zeroHardnessAndResistance().sound(SoundType.WOOD).notSolid());
	public static final Block NATURAL_COMPARATOR = new CrappyComparatorBlock(AbstractBlock.Properties.create(Material.MISCELLANEOUS).zeroHardnessAndResistance().sound(SoundType.WOOD).notSolid());
	public static final NaturalDeviceCropBlock NATURAL_CROP = new NaturalDeviceCropBlock(AbstractBlock.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().zeroHardnessAndResistance().sound(SoundType.CROP).notSolid());
	
	public static final Block.Properties flowerProps = AbstractBlock.Properties.from(Blocks.POPPY);
	public static final Block.Properties floatingProps = ModBlocks.FLOATING_PROPS;
	
	public static final BlockSpecialFlower SANVOCALIA = new NotBlockSpecialFlower(Effects.GLOWING, 20, flowerProps, SanvocaliaSubTile::big);
	public static final BlockSpecialFlower SMALL_SANVOCALIA = new NotBlockSpecialFlower(Effects.GLOWING, 5, flowerProps, SanvocaliaSubTile::small);
	public static final BlockFloatingSpecialFlower FLOATING_SANVOCALIA = new NotBlockFloatingSpecialFlower(floatingProps, SanvocaliaSubTile::big);
	public static final BlockFloatingSpecialFlower SMALL_FLOATING_SANVOCALIA = new NotBlockFloatingSpecialFlower(floatingProps, SanvocaliaSubTile::small);
	
	public static final BlockSpecialFlower FUNNY = new NotBlockSpecialFlower(Effects.BAD_OMEN, 20, flowerProps, FunnySubTile::big);
	public static final BlockSpecialFlower SMALL_FUNNY = new NotBlockSpecialFlower(Effects.BAD_OMEN, 5, flowerProps, FunnySubTile::small);
	public static final BlockFloatingSpecialFlower FLOATING_FUNNY = new NotBlockFloatingSpecialFlower(floatingProps, FunnySubTile::big);
	public static final BlockFloatingSpecialFlower SMALL_FLOATING_FUNNY = new NotBlockFloatingSpecialFlower(floatingProps, FunnySubTile::small);
	
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();
		
		//misc blocks!
		Inc.reg(r, "corporea_solidifier", CORPOREA_SOLIDIFIER);
		Inc.reg(r, "red_string_liar", RED_STRING_LIAR);
		Inc.reg(r, "frame_tinkerer", FRAME_TINKERER);
		Inc.reg(r, "corporea_retainer_evaporator", CORPOREA_RETAINER_EVAPORATOR);
		
		UNSTABLE_CUBES.values().forEach(block -> Inc.reg(r, block.color.getString() + "_unstable_cube", block));
		
		Inc.reg(r, "ender_soul_core", ENDER_SOUL_CORE);
		Inc.reg(r, "corporea_soul_core", CORPOREA_SOUL_CORE);
		Inc.reg(r, "potion_soul_core", POTION_SOUL_CORE);
		
		Inc.reg(r, "natural_repeater", NATURAL_REPEATER);
		Inc.reg(r, "natural_comparator", NATURAL_COMPARATOR);
		Inc.reg(r, "natural_crop", NATURAL_CROP);
		
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
	
	private static class NotBlockSpecialFlower extends BlockSpecialFlower {
		public NotBlockSpecialFlower(Effect stewEffect, int stewDuration, Properties props, Supplier<? extends TileEntitySpecialFlower> teProvider) {
			super(stewEffect, stewDuration, props, teProvider);
		}
	}
	
	private static class NotBlockFloatingSpecialFlower extends BlockFloatingSpecialFlower {
		public NotBlockFloatingSpecialFlower(Properties props, Supplier<? extends TileEntitySpecialFlower> teProvider) {
			super(props, teProvider);
		}
	}
}
