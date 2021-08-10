package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.WoodFamily;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class RhoBlocks {
	public static final WoodFamily RHODODENDRITE = Util.make(new WoodFamily("rhododendrite", MaterialColor.PINK, MaterialColor.OBSIDIAN), family -> {
		//replace the log with a different implementation
		//leaky abstractions! this is my fault! yay
		family.log = new RhododendriteLogBlock(AbstractBlock.Properties.from(family.log));
	});
	
	public static final CoreBlock CORE = new CoreBlock(AbstractBlock.Properties.create(Material.WOOD, MaterialColor.RED)
		.hardnessAndResistance(2, 3)
		.sound(SoundType.WOOD)
		.notSolid());
	
	//abstractblock.properties.from(family.log), fudged to change the lambda type
	//i wish i could just override that one thing with a builder parameter but mfw its final for no reason.
	public static final AwakenedLogBlock AWAKENED_LOG = new AwakenedLogBlock(AbstractBlock.Properties.create(Material.WOOD, state -> state.get(AwakenedLogBlock.FACING).getAxis() == Direction.Axis.Y ? MaterialColor.PINK : MaterialColor.OBSIDIAN)
		.hardnessAndResistance(2.0F)
		.sound(SoundType.WOOD)
		.lootFrom(() -> RHODODENDRITE.log));
	
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();
		
		RHODODENDRITE.registerBlocks(r);
		
		Rho.reg(r, "core", CORE);
		Rho.reg(r, "awakened_log", AWAKENED_LOG);
	}
}