package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.block.tile.CoreTile;

import java.util.function.BiPredicate;

public interface CorePredicate {
	boolean test(CoreTile core);
	
	//"head then tail"
	static CorePredicate biStackPeek(BiPredicate<SolidifiedRequest, SolidifiedRequest> a) {
		return core -> {
			StackOps ops = StackOps.read(core);
			return a.test(ops.peek(0), ops.peek(1));
		};
	}
}
