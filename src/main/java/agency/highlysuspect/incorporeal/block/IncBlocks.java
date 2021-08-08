package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.Init;
import agency.highlysuspect.incorporeal.block.tile.FunnySubTile;
import agency.highlysuspect.incorporeal.block.tile.SanvocaliaSubTile;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import vazkii.botania.api.subtile.TileEntitySpecialFlower;
import vazkii.botania.common.block.BlockFloatingSpecialFlower;
import vazkii.botania.common.block.BlockSpecialFlower;
import vazkii.botania.common.block.ModBlocks;

import java.util.function.Supplier;

public class IncBlocks {
	public static final Block CORPOREA_SOLIDIFIER = new CorporeaSolidifierBlock(AbstractBlock.Properties.from(ModBlocks.corporeaRetainer));
	public static final Block RED_STRING_LIAR = new RedStringLiarBlock(AbstractBlock.Properties.from(ModBlocks.redStringContainer));
	
	private static final Block.Properties FLOWER_PROPS = AbstractBlock.Properties.from(Blocks.POPPY);
	private static final Block.Properties FLOATING_PROPS = ModBlocks.FLOATING_PROPS;
	
	public static final BlockSpecialFlower SANVOCALIA = new NotBlockSpecialFlower(Effects.GLOWING, 20, FLOWER_PROPS, SanvocaliaSubTile::big);
	public static final BlockSpecialFlower SMALL_SANVOCALIA = new NotBlockSpecialFlower(Effects.GLOWING, 5, FLOWER_PROPS, SanvocaliaSubTile::small);
	public static final BlockFloatingSpecialFlower FLOATING_SANVOCALIA = new NotBlockFloatingSpecialFlower(FLOATING_PROPS, SanvocaliaSubTile::big);
	public static final BlockFloatingSpecialFlower SMALL_FLOATING_SANVOCALIA = new NotBlockFloatingSpecialFlower(FLOATING_PROPS, SanvocaliaSubTile::small);
	
	public static final BlockSpecialFlower FUNNY = new NotBlockSpecialFlower(Effects.BAD_OMEN, 20, FLOWER_PROPS, FunnySubTile::big);
	public static final BlockSpecialFlower SMALL_FUNNY = new NotBlockSpecialFlower(Effects.BAD_OMEN, 5, FLOWER_PROPS, FunnySubTile::small);
	public static final BlockFloatingSpecialFlower FLOATING_FUNNY = new NotBlockFloatingSpecialFlower(FLOATING_PROPS, FunnySubTile::big);
	public static final BlockFloatingSpecialFlower SMALL_FLOATING_FUNNY = new NotBlockFloatingSpecialFlower(FLOATING_PROPS, FunnySubTile::small);
	
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();
		
		//misc blocks!
		Init.reg(r, "corporea_solidifier", CORPOREA_SOLIDIFIER);
		Init.reg(r, "red_string_liar", RED_STRING_LIAR);
		
		//flowers
		//this is the block ID scheme that botania uses
		Init.reg(r, "sanvocalia", SANVOCALIA);
		Init.reg(r, "sanvocalia_chibi", SMALL_SANVOCALIA);
		Init.reg(r, "floating_sanvocalia", FLOATING_SANVOCALIA);
		Init.reg(r, "floating_sanvocalia_chibi", SMALL_FLOATING_SANVOCALIA);
		
		Init.reg(r, "funny", FUNNY);
		Init.reg(r, "funny_chibi", SMALL_FUNNY);
		Init.reg(r, "floating_funny", FLOATING_FUNNY);
		Init.reg(r, "floating_funny_chibi", SMALL_FLOATING_FUNNY);
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
