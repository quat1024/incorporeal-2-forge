package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.WoodBlockFamily;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class RhoBlocks {
	public static final WoodBlockFamily RHODODENDRITE = new WoodBlockFamily("rhododendrite", MaterialColor.PINK, MaterialColor.OBSIDIAN);
	
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();
		
		RHODODENDRITE.registerBlocks(r);
	}
}
