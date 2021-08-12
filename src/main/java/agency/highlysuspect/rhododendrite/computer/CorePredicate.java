package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.rhododendrite.block.tile.CoreTile;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface CorePredicate {
	boolean test(World world, BlockPos pos, BlockState state, CoreTile core);
	
	static CorePredicate alwaysFalse() {
		return (world, pos, state, core) -> false;
	}
	
	//"head then tail"
	static CorePredicate biStackPeek(BiPredicate<Fragment<?>, Fragment<?>> a) {
		return (world, pos, state, core) -> {
			StackOps ops = StackOps.read(core);
			return a.test(ops.peek(0), ops.peek(1));
		};
	}
}
