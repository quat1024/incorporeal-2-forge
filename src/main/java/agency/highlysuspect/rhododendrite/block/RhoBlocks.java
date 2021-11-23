package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.WoodFamily;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class RhoBlocks {
	public static final WoodFamily RHODODENDRITE = new WoodFamily("rhododendrite", MaterialColor.COLOR_PINK, MaterialColor.PODZOL).defaults();
	
	public static final RhodoCellBlock CELL = new RhodoCellBlock(Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
		.strength(2, 3)
		.sound(SoundType.WOOD)
		.noOcclusion());
	
	public static final RhodoOpBlock OP = new RhodoOpBlock(Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
		.strength(2, 3)
		.sound(SoundType.WOOD)
		.noOcclusion());
	
	public static final RhodoFunnelBlock FUNNEL = new RhodoFunnelBlock(Properties.of(Material.WOOD, MaterialColor.COLOR_RED)
		.strength(2, 3)
		.sound(SoundType.WOOD)
		.noOcclusion());
	
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();
		
		RHODODENDRITE.registerBlocks(r);
		
		
		Rho.reg(r, "cell", CELL);
		Rho.reg(r, "op", OP);
		Rho.reg(r, "funnel2", FUNNEL); //TODO rename to `funnel`
	}
}
