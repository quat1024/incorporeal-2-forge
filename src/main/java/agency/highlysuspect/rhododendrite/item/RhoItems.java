package agency.highlysuspect.rhododendrite.item;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.CoreBlock;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import agency.highlysuspect.rhododendrite.block.tile.CoreTile;
import agency.highlysuspect.rhododendrite.computer.CompoundCorporeaRequestMatcher;
import agency.highlysuspect.rhododendrite.computer.CoreAction;
import agency.highlysuspect.rhododendrite.computer.StackOps;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

import static agency.highlysuspect.rhododendrite.computer.CoreAction.Result.*;

public class RhoItems {
	//Always succeeds
	public static final OpcodeCardItem OPCODE_BLANK = new OpcodeCardItem(defaultProps(), (dir, core) -> SUCCESS);
	
	//test opcodes, log to console, there's 2 so i can use hotswap lol
	public static final OpcodeCardItem OPCODE_TEST_1 = new OpcodeCardItem(defaultProps(), (dir, core) -> {
		Rho.LOGGER.info(core.getRequest());
		return SUCCESS;
	});
	public static final OpcodeCardItem OPCODE_TEST_2 = new OpcodeCardItem(defaultProps(), (dir, core) -> {
		Rho.LOGGER.info(core.getRequest());
		return SUCCESS;
	});
	
	//Pushes an empty corporea request to the stack
	public static final OpcodeCardItem OPCODE_PUSH = new OpcodeCardItem(defaultProps(), CoreAction.alwaysSucceeds((dir, core) -> StackOps.read(core).push().commit()));
	
	//Pulls data closer to the core, erasing whatever's inside
	public static final OpcodeCardItem OPCODE_PULL = new OpcodeCardItem(defaultProps(), CoreAction.alwaysSucceeds((dir, core) -> StackOps.read(core).destroy(1).commit()));
	
	//Pushes a copy of the contents of the core onto the stack
	public static final OpcodeCardItem OPCODE_DUP = new OpcodeCardItem(defaultProps(), CoreAction.alwaysSucceeds((dir, core) -> {
		StackOps ops = StackOps.read(core);
		ops.push(ops.peek());
		ops.commit();
	}));
	
	//Rotates the core. Aka the reason i've been passing direction arguments around the whole time
	public static final OpcodeCardItem OPCODE_TUMBLE = new OpcodeCardItem(defaultProps(), (dir, core) -> {
		BlockState state = core.getBlockState();
		if(state.getBlock() instanceof CoreBlock && core.getWorld() != null) { //hey, you never know
			core.getWorld().setBlockState(core.getPos(), state.with(CoreBlock.FACING, dir));
			return SUCCESS;
		} else return FAILURE;
	});
	
	//Mathematical operations! The fun part.
	//Add unions the matchers of corporea requests and sums their counts.
	public static final OpcodeCardItem OPCODE_ADD = new OpcodeCardItem(defaultProps(), CoreAction.alwaysSucceeds((dir, core) -> 
		StackOps.read(core).biPullPush((head, tail) -> new SolidifiedRequest(
			CompoundCorporeaRequestMatcher.union(head.matcher, tail.matcher), 
			head.count + tail.count
		)).commit()));
	
	//Subtract removes all of B's matchers that exist in A and finds the difference of their counts.
	public static final OpcodeCardItem OPCODE_SUBTRACT = new OpcodeCardItem(defaultProps(), CoreAction.alwaysSucceeds((dir, core) ->
		StackOps.read(core).biPullPush((head, tail) -> new SolidifiedRequest(
			CompoundCorporeaRequestMatcher.minus(head.matcher, tail.matcher),
			head.count - tail.count
		)).commit()));
	
	//Multiply multiplies the counts and uhh, it just finds the union of matchers again, can't think of much else lol
	public static final OpcodeCardItem OPCODE_MULTIPLY = new OpcodeCardItem(defaultProps(), CoreAction.alwaysSucceeds((dir, core) ->
		StackOps.read(core).biPullPush((head, tail) -> new SolidifiedRequest(
			CompoundCorporeaRequestMatcher.union(head.matcher, tail.matcher),
			head.count * tail.count
		)).commit()));
	
	//Divide and Remainder both take the set differences again, although they can fail when the divisor is 0
	public static final OpcodeCardItem OPCODE_DIVIDE = new OpcodeCardItem(defaultProps(), (dir, core) -> {
		StackOps ops = StackOps.read(core);
		if(ops.peek(1).count == 0) return FAILURE;
		else ops.biPullPush((head, tail) -> new SolidifiedRequest(
			CompoundCorporeaRequestMatcher.minus(head.matcher, tail.matcher),
			head.count / tail.count
		)).commit();
		return SUCCESS;
	});
	
	public static final OpcodeCardItem OPCODE_REMAINDER = new OpcodeCardItem(defaultProps(), (dir, core) -> {
		StackOps ops = StackOps.read(core);
		if(ops.peek(1).count == 0) return FAILURE;
		else ops.biPullPush((head, tail) -> new SolidifiedRequest(
			CompoundCorporeaRequestMatcher.minus(head.matcher, tail.matcher),
			head.count % tail.count
		)).commit();
		return SUCCESS;
	});
	
	////// Conditions!!
	
	//Always false.
	public static final ConditionCardItem CONDITION_BLANK = new ConditionCardItem(defaultProps(), core ->
		false);
	
	public static final ConditionCardItem CONDITION_IS_EMPTY = new ConditionCardItem(defaultProps(), core ->
		StackOps.read(core).peek().isEmpty());
	
	public static final ConditionCardItem CONDITION_EQUAL = new ConditionCardItem(defaultProps(), core ->
		StackOps.read(core).biPeekMatches(SolidifiedRequest::equals));
	
	public static final ConditionCardItem CONDITION_COUNT_EQUAL = new ConditionCardItem(defaultProps(), core ->
		StackOps.read(core).biPeekMatches((head, tail) -> head.count == tail.count));
	
	public static final ConditionCardItem CONDITION_LESS_THAN = new ConditionCardItem(defaultProps(), core ->
		StackOps.read(core).biPeekMatches((head, tail) -> head.count < tail.count));
	
	public static final ConditionCardItem CONDITION_GREATER_THAN = new ConditionCardItem(defaultProps(), core ->
		StackOps.read(core).biPeekMatches((head, tail) -> head.count > tail.count));
	
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
			RhoBlocks.CORE,
			RhoBlocks.AWAKENED_LOG,
			RhoBlocks.OPCODE,
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
