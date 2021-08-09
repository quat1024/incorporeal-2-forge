package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.WoodFamily;
import net.minecraft.block.*;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class RhoBlocks {
	public static final WoodFamily RHODODENDRITE = new WoodFamily("rhododendrite", MaterialColor.PINK, MaterialColor.OBSIDIAN);
	
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();
		
		RHODODENDRITE.registerBlocks(r);
	}
}
