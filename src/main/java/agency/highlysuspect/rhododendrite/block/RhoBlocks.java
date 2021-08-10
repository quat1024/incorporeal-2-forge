package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.WoodFamily;
import agency.highlysuspect.rhododendrite.block.tile.RhoTileTypes;
import agency.highlysuspect.rhododendrite.computer.DataTypes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import vazkii.botania.common.block.ModBlocks;

import java.math.BigInteger;

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
	
	private static final AbstractBlock.Properties opcodeProps = AbstractBlock.Properties.from(ModBlocks.corporeaFunnel);
	
	public static final OpcodeBlock TEST1 = new OpcodeBlock(opcodeProps, (world, pos, state, core) -> {
		Rho.LOGGER.info(core.getFragment().data);
	});
	
	public static final OpcodeBlock TEST2 = new OpcodeBlock(opcodeProps, (world, pos, state, core) -> {
		int hilarious = world.rand.nextInt(500);
		Rho.LOGGER.info("setting core fragment to " + hilarious);
		core.setFragment(DataTypes.NUMBER.uncheckedInstantiate(BigInteger.valueOf(hilarious)));
	});
	
	public static final OpcodeBlock PUSH = new OpcodeBlock(opcodeProps, (world, pos, state, core) -> core.push());
	public static final OpcodeBlock PULL = new OpcodeBlock(opcodeProps, (world, pos, state, core) -> core.pull());
	public static final OpcodeBlock REORIENT = new OpcodeBlock.Directional(opcodeProps, (world, pos, state, core) -> core.reorient(state.get(OpcodeBlock.Directional.FACING)));
	
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> r = event.getRegistry();
		
		RHODODENDRITE.registerBlocks(r);
		
		Rho.reg(r, "core", CORE);
		Rho.reg(r, "awakened_log", AWAKENED_LOG);
		
		Rho.reg(r, "test1", TEST1);
		Rho.reg(r, "test2", TEST2);
		
		Rho.reg(r, "push", PUSH);
		Rho.reg(r, "pull", PULL);
		Rho.reg(r, "reorient", REORIENT);
	}
}
