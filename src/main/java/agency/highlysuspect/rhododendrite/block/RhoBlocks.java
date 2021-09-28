package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.WoodFamily;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import vazkii.botania.common.block.ModBlocks;

public class RhoBlocks {
	public static final WoodFamily RHODODENDRITE = Util.make(new WoodFamily("rhododendrite", MaterialColor.PINK, MaterialColor.OBSIDIAN), family -> {
		//TODO remove this, if i don't need to replace any of the blocks with funky impls anymore.
		family.okDoTheTreeNow();
	});
	
	public static final RhodoCellBlock CELL = new RhodoCellBlock(Properties.create(Material.WOOD, MaterialColor.RED)
		.hardnessAndResistance(2, 3)
		.sound(SoundType.WOOD)
		.notSolid());
	
	public static final RhodoOpBlock OP = new RhodoOpBlock(Properties.create(Material.WOOD, MaterialColor.RED)
		.hardnessAndResistance(2, 3)
		.sound(SoundType.WOOD)
		.notSolid());
	
	public static final RhodoFunnelBlock FUNNEL = new RhodoFunnelBlock(Properties.create(Material.WOOD, MaterialColor.RED)
		.hardnessAndResistance(2, 3)
		.sound(SoundType.WOOD)
		.notSolid());
	
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();
		
		RHODODENDRITE.registerBlocks(r);
		

		
		Rho.reg(r, "cell", CELL);
		Rho.reg(r, "op", OP);
		Rho.reg(r, "funnel2", FUNNEL); //TODO rename to `funnel`
	}
}
