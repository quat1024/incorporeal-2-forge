package agency.highlysuspect.rhododendrite.item;

import agency.highlysuspect.rhododendrite.block.tile.CoreTile;
import agency.highlysuspect.rhododendrite.computer.Fragment;
import agency.highlysuspect.rhododendrite.computer.StackOps;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.math.BigInteger;
import java.util.Optional;
import java.util.function.BiFunction;

public class OpcodeCardItem extends Item {
	public OpcodeCardItem(Properties properties, CoreAction action) {
		super(properties);
		this.action = action;
	}
	
	public final CoreAction action;
	
	public interface CoreAction {
		Result act(World world, BlockPos opcodePos, BlockState opcodeState, CoreTile core);
		
		enum Result {
			//The action completed successfully and probably mutated something in the world.
			SUCCESS,
			//The action was not able to complete due to a data-related error, like mismatched types.
			FAILURE,
			//The action was not able to complete due to something like "the opcode isn't bound to a core"
			NOT_APPLICABLE
		}
		
		static CoreAction binNumeric(BiFunction<BigInteger, BigInteger, BigInteger> binOp) {
			return binNumericOpt((x, y) -> Optional.of(binOp.apply(x, y)));
		}
		
		static CoreAction binNumericOpt(BiFunction<BigInteger, BigInteger, Optional<BigInteger>> binOp) {
			return (world, pos, state, core) -> {
				StackOps ops = StackOps.read(core);
				
				//grab the top two elements off the stack and try to convert them into numbers
				Fragment<?> head = ops.pull();
				Fragment<?> tail = ops.pull();
				Optional<BigInteger> headN = head.asNumber();
				Optional<BigInteger> tailN = tail.asNumber();
				
				if(headN.isPresent() && tailN.isPresent()) {
					//perform the binary operation, try to inject it back into the head's type
					Optional<? extends Fragment<?>> injResult = binOp.apply(headN.get(), tailN.get()).flatMap(head::injectNumber);
					if(injResult.isPresent()) {
						ops.push(injResult.get());
						ops.commit();
						return Result.SUCCESS;
					}
				}
				
				return Result.FAILURE;
			};
		}
	}
}
