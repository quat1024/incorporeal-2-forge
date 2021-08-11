package agency.highlysuspect.rhododendrite.item;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import agency.highlysuspect.rhododendrite.computer.DataTypes;
import agency.highlysuspect.rhododendrite.computer.StackOps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import vazkii.botania.common.impl.corporea.CorporeaItemStackMatcher;

import java.math.BigInteger;
import java.util.Optional;

import static agency.highlysuspect.rhododendrite.item.OpcodeCardItem.CoreAction.Result.*;

public class RhoItems {
	public static final OpcodeCardItem NOP = new OpcodeCardItem(defaultProps(), (world, pos, state, core) -> SUCCESS);
	public static final OpcodeCardItem TEST1 = new OpcodeCardItem(defaultProps(), (world, pos, state, core) -> {
		Rho.LOGGER.info(core.getFragment().data);
		return SUCCESS;
	});
	public static final OpcodeCardItem TEST2 = new OpcodeCardItem(defaultProps(), (world, pos, state, core) -> {
		core.setFragment(DataTypes.SOLIDIFIED_REQUEST.uncheckedInstantiate(new SolidifiedRequest(new CorporeaItemStackMatcher(new ItemStack(Items.STONE), false), 69)));
		return SUCCESS;
	});
	public static final OpcodeCardItem PUSH = new OpcodeCardItem(defaultProps(), (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		ops.push();
		ops.commit();
		return SUCCESS;
	});
	public static final OpcodeCardItem PULL = new OpcodeCardItem(defaultProps(), (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		ops.pull();
		ops.commit();
		return SUCCESS;
	});
	public static final OpcodeCardItem DUP = new OpcodeCardItem(defaultProps(), (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		ops.push(ops.peek().unlink());
		ops.commit();
		return SUCCESS;
	});
	public static final OpcodeCardItem PUSH_ZERO = new OpcodeCardItem(defaultProps(), (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		ops.push(DataTypes.BIG_INTEGER.uncheckedInstantiate(BigInteger.ZERO));
		ops.commit();
		return SUCCESS;
	});
	public static final OpcodeCardItem PUSH_ONE = new OpcodeCardItem(defaultProps(), (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		ops.push(DataTypes.BIG_INTEGER.uncheckedInstantiate(BigInteger.ONE));
		ops.commit();
		return SUCCESS;
	});
	public static final OpcodeCardItem ADD = new OpcodeCardItem(defaultProps(), OpcodeCardItem.CoreAction.binNumeric(BigInteger::add));
	public static final OpcodeCardItem SUBTRACT = new OpcodeCardItem(defaultProps(), OpcodeCardItem.CoreAction.binNumeric(BigInteger::subtract));
	public static final OpcodeCardItem MULTIPLY = new OpcodeCardItem(defaultProps(), OpcodeCardItem.CoreAction.binNumeric(BigInteger::multiply));
	public static final OpcodeCardItem DIVIDE = new OpcodeCardItem(defaultProps(), OpcodeCardItem.CoreAction.binNumericOpt((x, y) -> y.equals(BigInteger.ZERO) ? Optional.empty() : Optional.of(x.divide(y))));
	public static final OpcodeCardItem REMAINDER = new OpcodeCardItem(defaultProps(), OpcodeCardItem.CoreAction.binNumericOpt((x, y) -> y.equals(BigInteger.ZERO) ? Optional.empty() : Optional.of(x.remainder(y))));
	
	public static Item.Properties defaultProps() {
		return new Item.Properties().group(Tab.INSTANCE);
	}
	
	public static void register(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();
		
		RhoBlocks.RHODODENDRITE.registerItems(r);
		
		Rho.reg(r, "opcode_blank", NOP);
		Rho.reg(r, "opcode_test_1", TEST1);
		Rho.reg(r, "opcode_test_2", TEST2);
		Rho.reg(r, "opcode_push", PUSH);
		Rho.reg(r, "opcode_pull", PULL);
		Rho.reg(r, "opcode_dup", DUP);
		Rho.reg(r, "opcode_push_zero", PUSH_ZERO);
		Rho.reg(r, "opcode_push_one", PUSH_ONE);
		Rho.reg(r, "opcode_add", ADD);
		Rho.reg(r, "opcode_subtract", SUBTRACT);
		Rho.reg(r, "opcode_multiply", MULTIPLY);
		Rho.reg(r, "opcode_divide", DIVIDE);
		Rho.reg(r, "opcode_remainder", REMAINDER);
		
		Rho.simpleBlockItems(r,
			RhoBlocks.CORE,
			RhoBlocks.AWAKENED_LOG,
			RhoBlocks.OPCODE,
			RhoBlocks.CORE_TUMBLER
		);
	}
	
	private static class Tab extends ItemGroup {
		public static final Tab INSTANCE = new Tab();
		
		public Tab() { super(Rho.MODID); }
		
		@Override
		public ItemStack createIcon() {
			return new ItemStack(RhoBlocks.RHODODENDRITE.planks);
		}
		
		@Override
		public boolean hasSearchBar() {
			return true;
		}
	}
}
