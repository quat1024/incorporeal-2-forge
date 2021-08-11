package agency.highlysuspect.rhododendrite.block;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.WoodFamily;
import agency.highlysuspect.rhododendrite.computer.DataTypes;
import agency.highlysuspect.rhododendrite.computer.StackOps;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import vazkii.botania.common.block.ModBlocks;

import java.math.BigInteger;
import java.util.Optional;

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
	
	//TODO remove
	public static final OpcodeBlock TEST1 = new OpcodeBlock(opcodeProps, (world, pos, state, core) -> {
		Rho.LOGGER.info(core.getFragment().data);
		return true;
	});
	
	//TODO remove
	public static final OpcodeBlock TEST2 = new OpcodeBlock(opcodeProps, (world, pos, state, core) -> {
		int hilarious = world.rand.nextInt(500);
		Rho.LOGGER.info("setting core fragment to " + hilarious);
		core.setFragment(DataTypes.BIG_INTEGER.uncheckedInstantiate(BigInteger.valueOf(hilarious)));
		return true;
	});
	
	public static final OpcodeBlock PUSH = new OpcodeBlock(opcodeProps, (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		ops.push();
		ops.commit();
		return true;
	});
	
	public static final OpcodeBlock PULL = new OpcodeBlock(opcodeProps, (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		ops.pull();
		ops.commit();
		return true;
	});
	
	public static final OpcodeBlock REORIENT = new OpcodeBlock.Directional(opcodeProps, (world, pos, state, core) -> {
		BlockState coreState = core.getBlockState();
		if(coreState.getBlock() instanceof CoreBlock) { //u never know
			Direction opcodeFacing = state.get(OpcodeBlock.Directional.FACING);
			Direction coreFacing = coreState.get(CoreBlock.FACING);
			if(opcodeFacing != coreFacing) {
				world.setBlockState(core.getPos(), coreState.with(CoreBlock.FACING, opcodeFacing));
				return true;
			}
		}
		return false;
	});
	
	public static final OpcodeBlock DUP = new OpcodeBlock(opcodeProps, (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		ops.push(ops.peek().unlink());
		ops.commit();
		return true;
	});
	
	public static final OpcodeBlock PUSH_ZERO = new OpcodeBlock(opcodeProps, (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		ops.push(DataTypes.BIG_INTEGER.uncheckedInstantiate(BigInteger.ZERO));
		ops.commit();
		return true;
	});
	
	public static final OpcodeBlock PUSH_ONE = new OpcodeBlock(opcodeProps, (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		ops.push(DataTypes.BIG_INTEGER.uncheckedInstantiate(BigInteger.ONE));
		ops.commit();
		return true;
	});
	
	public static final OpcodeBlock ADD = new OpcodeBlock(opcodeProps, OpcodeBlock.binNumeric(BigInteger::add));
	public static final OpcodeBlock SUBTRACT = new OpcodeBlock(opcodeProps, OpcodeBlock.binNumeric(BigInteger::subtract));
	public static final OpcodeBlock MULTIPLY = new OpcodeBlock(opcodeProps, OpcodeBlock.binNumeric(BigInteger::multiply));
	public static final OpcodeBlock DIVIDE = new OpcodeBlock(opcodeProps, OpcodeBlock.binNumericOpt((x, y) -> y.equals(BigInteger.ZERO) ? Optional.empty() : Optional.of(x.divide(y))));
	public static final OpcodeBlock REMAINDER = new OpcodeBlock(opcodeProps, OpcodeBlock.binNumericOpt((x, y) -> y.equals(BigInteger.ZERO) ? Optional.empty() : Optional.of(x.remainder(y))));
	
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
		Rho.reg(r, "dup", DUP);
		Rho.reg(r, "push_zero", PUSH_ZERO);
		Rho.reg(r, "push_one", PUSH_ONE);
		Rho.reg(r, "add", ADD);
		Rho.reg(r, "subtract", SUBTRACT);
		Rho.reg(r, "multiply", MULTIPLY);
		Rho.reg(r, "divide", DIVIDE);
		Rho.reg(r, "remainder", REMAINDER);
	}
}
