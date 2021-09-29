package agency.highlysuspect.rhododendrite.item;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import agency.highlysuspect.rhododendrite.computer.CompoundCorporeaRequestMatcher;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class RhoItems {
	//Always succeeds
	public static final RhoCardItem OPCODE_BLANK = RhoCardItem.op(defaultProps(), (dir, core) -> {});
	
	//test opcodes, log to console, there's 2 so i can use hotswap lol
	public static final RhoCardItem OPCODE_TEST_1 = RhoCardItem.op(defaultProps(), (dir, core) -> {
		
	});
	public static final RhoCardItem OPCODE_TEST_2 = RhoCardItem.op(defaultProps(), (dir, core) -> {
		
	});
	
	//Pushes an empty corporea request to the stack
	public static final RhoCardItem OPCODE_PUSH = RhoCardItem.op(defaultProps(), (cell, op) -> cell.push(SolidifiedRequest.EMPTY));
	
	//Pulls data closer to the core, erasing whatever's inside
	public static final RhoCardItem OPCODE_PULL = RhoCardItem.op(defaultProps(), (cell, op) -> cell.pull());
	
	//Pushes a copy of the contents of the core onto the stack
	public static final RhoCardItem OPCODE_DUP = RhoCardItem.op(defaultProps(), (cell, op) -> cell.push(cell.peek()));
	
	//Rotates the cell. Why not?
	public static final RhoCardItem OPCODE_TUMBLE = RhoCardItem.op(defaultProps(), (cell, op) -> {
		BlockState cellState = cell.getBlockState();
		BlockState opState = op.getBlockState();
		
		assert cell.getWorld() != null;
		cell.getWorld().setBlockState(
			cell.getPos(),
			cellState.with(DirectionalBlock.FACING, opState.get(DirectionalBlock.FACING))
		);
	});
	
	//Mathematical operations! The fun part.
	//Add unions the matchers of corporea requests and sums their counts.
	public static final RhoCardItem OPCODE_ADD = RhoCardItem.op(defaultProps(), (cell, op) -> {
		SolidifiedRequest head = cell.peek();
		SolidifiedRequest tail = cell.peekNext();
		cell.push(new SolidifiedRequest(
			CompoundCorporeaRequestMatcher.union(head.matcher, tail.matcher),
			head.count + tail.count
		));
	});
	
	//Subtract removes all of B's matchers that exist in A and finds the difference of their counts.
	public static final RhoCardItem OPCODE_SUBTRACT = RhoCardItem.op(defaultProps(), (cell, op) -> {
		SolidifiedRequest head = cell.peek();
		SolidifiedRequest tail = cell.peekNext();
		cell.push(new SolidifiedRequest(
			CompoundCorporeaRequestMatcher.minus(head.matcher, tail.matcher),
			head.count - tail.count
		));
	});
	
	//Multiply multiplies the counts and uhh, it just finds the union of matchers again, can't think of much else lol
	public static final RhoCardItem OPCODE_MULTIPLY = RhoCardItem.op(defaultProps(), (cell, op) -> {
		SolidifiedRequest head = cell.peek();
		SolidifiedRequest tail = cell.peekNext();
		cell.push(new SolidifiedRequest(
			CompoundCorporeaRequestMatcher.union(head.matcher, tail.matcher),
			head.count * tail.count
		));
	});
	
	//Divide and Remainder both take the set differences again, although they can fail when the divisor is 0
	public static final RhoCardItem OPCODE_DIVIDE = RhoCardItem.op(defaultProps(), (cell, op) -> {
		SolidifiedRequest tail = cell.peekNext();
		if(tail.count == 0) op.fail();
		else {
			SolidifiedRequest head = cell.peek();
			cell.push(new SolidifiedRequest(
				CompoundCorporeaRequestMatcher.minus(head.matcher, tail.matcher),
				head.count / tail.count
			));
		}
	});
	
	public static final RhoCardItem OPCODE_REMAINDER = RhoCardItem.op(defaultProps(), (cell, op) -> {
		SolidifiedRequest tail = cell.peekNext();
		if(tail.count == 0) op.fail();
		else {
			SolidifiedRequest head = cell.peek();
			cell.push(new SolidifiedRequest(
				CompoundCorporeaRequestMatcher.minus(head.matcher, tail.matcher),
				head.count % tail.count
			));
		}
	});
	
	////// Conditions!!
	
	public static final RhoCardItem CONDITION_BLANK = RhoCardItem.cond(defaultProps(), (cell, op) -> false);
	public static final RhoCardItem CONDITION_IS_EMPTY = RhoCardItem.cond(defaultProps(), (cell, op) -> cell.peek().isEmpty());
	public static final RhoCardItem CONDITION_EQUAL = RhoCardItem.cond(defaultProps(), (cell, op) -> cell.peek().equals(cell.peekNext()));
	public static final RhoCardItem CONDITION_COUNT_EQUAL = RhoCardItem.cond(defaultProps(), (cell, op) -> cell.peek().count == cell.peekNext().count);
	public static final RhoCardItem CONDITION_LESS_THAN = RhoCardItem.cond(defaultProps(), (cell, op) -> cell.peek().count < cell.peekNext().count);
	public static final RhoCardItem CONDITION_GREATER_THAN = RhoCardItem.cond(defaultProps(), (cell, op) -> cell.peek().count > cell.peekNext().count);
	
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
		Rho.reg(r, "opcode_tumble", OPCODE_TUMBLE);
		Rho.reg(r, "opcode_add", OPCODE_ADD);
		Rho.reg(r, "opcode_subtract", OPCODE_SUBTRACT);
		Rho.reg(r, "opcode_multiply", OPCODE_MULTIPLY);
		Rho.reg(r, "opcode_divide", OPCODE_DIVIDE);
		Rho.reg(r, "opcode_remainder", OPCODE_REMAINDER);
		
		Rho.reg(r, "condition_blank", CONDITION_BLANK);
		Rho.reg(r, "condition_is_empty", CONDITION_IS_EMPTY);
		Rho.reg(r, "condition_equal", CONDITION_EQUAL);
		Rho.reg(r, "condition_count_equal", CONDITION_COUNT_EQUAL);
		Rho.reg(r, "condition_less_than", CONDITION_LESS_THAN);
		Rho.reg(r, "condition_greater_than", CONDITION_GREATER_THAN);
		
		Rho.simpleBlockItems(r,
			RhoBlocks.CELL,
			RhoBlocks.OP,
			RhoBlocks.FUNNEL
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
