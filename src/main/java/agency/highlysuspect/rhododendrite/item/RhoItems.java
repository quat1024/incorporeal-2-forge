package agency.highlysuspect.rhododendrite.item;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import agency.highlysuspect.rhododendrite.computer.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import vazkii.botania.common.impl.corporea.CorporeaItemStackMatcher;

import java.math.BigInteger;
import java.util.Optional;

import static agency.highlysuspect.rhododendrite.computer.CoreAction.Result.*;

public class RhoItems {
	//Always succeeds
	public static final OpcodeCardItem OPCODE_BLANK = new OpcodeCardItem(defaultProps(), (world, pos, state, core) -> SUCCESS);
	//test opcode, logs to console
	public static final OpcodeCardItem OPCODE_TEST_1 = new OpcodeCardItem(defaultProps(), (world, pos, state, core) -> {
		Rho.LOGGER.info(core.getFragment().data);
		return SUCCESS;
	});
	//test opcode to try out solidified_request type
	public static final OpcodeCardItem OPCODE_TEST_2 = new OpcodeCardItem(defaultProps(), (world, pos, state, core) -> {
		core.setFragment(DataTypes.SOLIDIFIED_REQUEST.uncheckedInstantiate(new SolidifiedRequest(new CorporeaItemStackMatcher(new ItemStack(Items.STONE), false), 69)));
		return SUCCESS;
	});
	//Pushes an empty fragment to the stack
	public static final OpcodeCardItem OPCODE_PUSH = new OpcodeCardItem(defaultProps(), (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		ops.push();
		ops.commit();
		return SUCCESS;
	});
	//Pulls data closer to the core, erasing whatever's inside
	public static final OpcodeCardItem OPCODE_PULL = new OpcodeCardItem(defaultProps(), (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		ops.pull();
		ops.commit();
		return SUCCESS;
	});
	//Pushes a copy of the contents of the core onto the stack
	public static final OpcodeCardItem OPCODE_DUP = new OpcodeCardItem(defaultProps(), (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		ops.push(ops.peek().unlink());
		ops.commit();
		return SUCCESS;
	});
	//Pushes a 0
	public static final OpcodeCardItem OPCODE_PUSH_ZERO = new OpcodeCardItem(defaultProps(), (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		ops.push(DataTypes.BIG_INTEGER.uncheckedInstantiate(BigInteger.ZERO));
		ops.commit();
		return SUCCESS;
	});
	//Pushes a 1
	public static final OpcodeCardItem OPCODE_PUSH_ONE = new OpcodeCardItem(defaultProps(), (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		ops.push(DataTypes.BIG_INTEGER.uncheckedInstantiate(BigInteger.ONE));
		ops.commit();
		return SUCCESS;
	});
	//Mathematical operations. The type of the result is the type of whatever's in the core.
	public static final OpcodeCardItem OPCODE_ADD = new OpcodeCardItem(defaultProps(), CoreAction.binNumeric(BigInteger::add));
	public static final OpcodeCardItem OPCODE_SUBTRACT = new OpcodeCardItem(defaultProps(), CoreAction.binNumeric(BigInteger::subtract));
	public static final OpcodeCardItem OPCODE_MULTIPLY = new OpcodeCardItem(defaultProps(), CoreAction.binNumeric(BigInteger::multiply));
	public static final OpcodeCardItem OPCODE_DIVIDE = new OpcodeCardItem(defaultProps(), CoreAction.binNumericOpt((x, y) -> y.equals(BigInteger.ZERO) ? Optional.empty() : Optional.of(x.divide(y))));
	public static final OpcodeCardItem OPCODE_REMAINDER = new OpcodeCardItem(defaultProps(), CoreAction.binNumericOpt((x, y) -> y.equals(BigInteger.ZERO) ? Optional.empty() : Optional.of(x.remainder(y))));
	//Copies the numeric value of the thing and pushes it onto the core.
	//This lets you convert from corporea request -> number if you want.
	public static final OpcodeCardItem OPCODE_EXTRACT_NUMBER = new OpcodeCardItem(defaultProps(), (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		Optional<BigInteger> value = ops.pull().asNumber();
		if(value.isPresent()) {
			Optional<Fragment<BigInteger>> hahaYes = DataTypes.BIG_INTEGER.checkedInstantiate(value.get());
			if(hahaYes.isPresent()) {
				ops.push(hahaYes.get());
				ops.commit();
				return SUCCESS;
			}
		}
		
		return FAILURE;
	});
	
	//Always false.
	public static final ConditionCardItem CONDITION_BLANK = new ConditionCardItem(defaultProps(), (world, pos, state, core) -> false);
	//True if the core contains an empty item (unit type).
	public static final ConditionCardItem CONDITION_IS_EMPTY = new ConditionCardItem(defaultProps(), (world, pos, state, core) -> StackOps.read(core).peek().isUnit());
	//True if the thing inside the core is the same type as the thing outside the core.
	public static final ConditionCardItem CONDITION_EQUAL_TYPE = new ConditionCardItem(defaultProps(), (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		Fragment<?> a = ops.peek(0);
		Fragment<?> b = ops.peek(1);
		return a.type != DataTypes.EMPTY && a.type == b.type;
	});
	//True if the thing inside the core is the same type as the thing outside the core, and their values are equal too.
	public static final ConditionCardItem CONDITION_EQUAL = new ConditionCardItem(defaultProps(), (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		Fragment<?> a = ops.peek(0);
		Fragment<?> b = ops.peek(1);
		return a.type != DataTypes.EMPTY && a.equals(b);
	});
	//True if the numeric value of the thing inside the core is less than the numeric value of the thing outside the core.
	//False if either are nonnumeric.
	public static final ConditionCardItem CONDITION_LESS_THAN = new ConditionCardItem(defaultProps(), (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		Optional<BigInteger> head = ops.peek(0).asNumber();
		Optional<BigInteger> tail = ops.peek(1).asNumber();
		if(head.isPresent() && tail.isPresent()) return head.get().compareTo(tail.get()) < 0;
		else return false;
	});
	//Same thing but does "greater than", false if either are nonnumeric.
	//There's no "numeric equals" that would check e.g. (5xStone == 5), but I think you can make it with "not less than" & "not greater than"?
	//This is a bad design decision lol
	public static final ConditionCardItem CONDITION_GREATER_THAN = new ConditionCardItem(defaultProps(), (world, pos, state, core) -> {
		StackOps ops = StackOps.read(core);
		Optional<BigInteger> head = ops.peek(0).asNumber();
		Optional<BigInteger> tail = ops.peek(1).asNumber();
		if(head.isPresent() && tail.isPresent()) return head.get().compareTo(tail.get()) > 0;
		else return false;
	});
	
	public static Item.Properties defaultProps() {
		return new Item.Properties().group(Tab.INSTANCE);
	}
	
	public static void register(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();
		
		RhoBlocks.RHODODENDRITE.registerItems(r);
		
		Rho.reg(r, "opcode_blank", OPCODE_BLANK);
		Rho.reg(r, "opcode_test_1", OPCODE_TEST_1);
		Rho.reg(r, "opcode_test_2", OPCODE_TEST_2);
		Rho.reg(r, "opcode_push", OPCODE_PUSH);
		Rho.reg(r, "opcode_pull", OPCODE_PULL);
		Rho.reg(r, "opcode_dup", OPCODE_DUP);
		Rho.reg(r, "opcode_push_zero", OPCODE_PUSH_ZERO);
		Rho.reg(r, "opcode_push_one", OPCODE_PUSH_ONE);
		Rho.reg(r, "opcode_add", OPCODE_ADD);
		Rho.reg(r, "opcode_subtract", OPCODE_SUBTRACT);
		Rho.reg(r, "opcode_multiply", OPCODE_MULTIPLY);
		Rho.reg(r, "opcode_divide", OPCODE_DIVIDE);
		Rho.reg(r, "opcode_remainder", OPCODE_REMAINDER);
		
		Rho.reg(r, "condition_blank", CONDITION_BLANK);
		Rho.reg(r, "condition_is_empty", CONDITION_IS_EMPTY);
		Rho.reg(r, "condition_equal_type", CONDITION_EQUAL_TYPE);
		Rho.reg(r, "condition_equal", CONDITION_EQUAL);
		Rho.reg(r, "condition_less_than", CONDITION_LESS_THAN);
		Rho.reg(r, "condition_greater_than", CONDITION_GREATER_THAN);
		
		Rho.simpleBlockItems(r,
			RhoBlocks.CORE,
			RhoBlocks.AWAKENED_LOG,
			RhoBlocks.OPCODE,
			RhoBlocks.CORE_TUMBLER,
			RhoBlocks.CONDITION
		);
	}
	
	private static class Tab extends ItemGroup {
		public static final Tab INSTANCE = new Tab();
		
		public Tab() {
			super(Rho.MODID);
			setNoTitle();
			//noinspection deprecation (forge extends this, but this works fine too)
			setBackgroundImageName(Rho.MODID + ".png");
		}
		
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
