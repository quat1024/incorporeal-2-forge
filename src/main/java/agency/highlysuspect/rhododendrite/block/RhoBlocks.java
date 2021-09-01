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
		//replace some of the blocks with a different implementation
		//leaky abstractions! this is my fault! yay
		family.log = new RhododendriteLogBlock(Properties.from(family.log));
		family.leaves = new RhododendriteLeavesBlock(Properties.from(family.leaves));
		
		family.okDoTheTreeNow();
	});
	
	public static final CoreBlock CORE = new CoreBlock(Properties.create(Material.WOOD, MaterialColor.RED)
		.hardnessAndResistance(2, 3)
		.sound(SoundType.WOOD)
		.notSolid());
	
	//abstractblock.properties.from(family.log), fudged to change the lambda type
	//i wish i could just override that one thing with a builder parameter but mfw its final for no reason.
	public static final AwakenedLogBlock AWAKENED_LOG = new AwakenedLogBlock(Properties.create(Material.WOOD, state -> state.get(AwakenedLogBlock.FACING).getAxis() == Direction.Axis.Y ? MaterialColor.PINK : MaterialColor.OBSIDIAN)
		.hardnessAndResistance(2.0F)
		.sound(SoundType.WOOD)
		.lootFrom(() -> RHODODENDRITE.log)
		.notSolid());
	
	public static final OpcodeBlock OPCODE = new OpcodeBlock(Properties.from(ModBlocks.corporeaFunnel));
	public static final ConditionBlock CONDITION = new ConditionBlock(Properties.create(Material.IRON).hardnessAndResistance(5.5F).sound(SoundType.METAL).notSolid());
	public static final FunnelBlock FUNNEL = new FunnelBlock(Properties.create(Material.WOOD).hardnessAndResistance(3F).sound(SoundType.WOOD));
	
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();
		
		RHODODENDRITE.registerBlocks(r);
		
		Rho.reg(r, "core", CORE);
		Rho.reg(r, "awakened_log", AWAKENED_LOG);
		
		Rho.reg(r, "opcode", OPCODE);
		Rho.reg(r, "condition", CONDITION);
		Rho.reg(r, "funnel", FUNNEL);
	}
}
