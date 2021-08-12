package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.rhododendrite.block.tile.CoreTile;
import net.minecraft.util.Direction;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface CoreAction extends BiFunction<Direction, CoreTile, CoreAction.Result> {
	static CoreAction alwaysSucceeds(BiConsumer<Direction, CoreTile> act) {
		return (direction, core) -> {
			act.accept(direction, core);
			return Result.SUCCESS;
		};
	}
	
	enum Result {
		//The action completed successfully and probably mutated something in the world.
		SUCCESS,
		//The action was not able to complete due to a data-related error, like mismatched types.
		FAILURE,
		//The action was not able to complete due to something like "the opcode isn't bound to a core"
		NOT_APPLICABLE
	}
}
