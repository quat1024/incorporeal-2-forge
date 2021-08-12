package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.block.CoreBlock;
import agency.highlysuspect.rhododendrite.block.tile.CoreTile;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class StackOps {
	public StackOps(SolidifiedRequest[] requests, SolidifiedRequest.Holder[] holders) {
		this.requests = requests;
		this.holders = holders;
	}
	
	public StackOps() {
		this(new SolidifiedRequest[0], new SolidifiedRequest.Holder[0]);
	}
	
	public static StackOps read(CoreTile tile) {
		return read(tile.getWorld(), tile.getPos(), tile.getBlockState());
	}
	
	public static StackOps read(World world, BlockPos corePos, BlockState coreState) {
		if(!(coreState.getBlock() instanceof CoreBlock)) return new StackOps();
		
		Direction coreFacing = coreState.get(CoreBlock.FACING);
		
		List<SolidifiedRequest> requests = new ArrayList<>();
		List<SolidifiedRequest.Holder> holders = new ArrayList<>();
		
		BlockPos.Mutable cursor = corePos.toMutable();
		for(int i = 0; i <= CorePathTracing.MAX_RANGE; i++) { // <=: the actual max size of a conga line is MAX_RANGE + 1, counting the Core itself
			TileEntity tile = world.getTileEntity(cursor);
			if(tile == null) break;
			
			Optional<SolidifiedRequest.Holder> holder = tile.getCapability(SolidifiedRequest.Cap.INSTANCE).resolve();
			if(holder.isPresent()) {
				requests.add(holder.get().getRequest());
				holders.add(holder.get());
				//offsetting at the end of the loop btw, so the core itself gets included in the conga line
				cursor.move(coreFacing);
			} else {
				break;
			}
		}
		
		return new StackOps(requests.toArray(new SolidifiedRequest[0]), holders.toArray(new SolidifiedRequest.Holder[0]));
	}
	
	private static final SolidifiedRequest[] BUNCHA_EMPTIES = new SolidifiedRequest[CorePathTracing.MAX_RANGE];
	static {
		Arrays.fill(BUNCHA_EMPTIES, SolidifiedRequest.EMPTY);
	} 
	
	private final SolidifiedRequest[] requests;
	private final SolidifiedRequest.Holder[] holders;
	
	/**
	 * Apply the changes to the SolidifiedRequest.Holders. Without this, you're just working off a copy
	 */
	public void commit() {
		for(int i = 0; i < requests.length; i++) {
			holders[i].setRequest(requests[i]);
		}
	}
	
	public StackOps push() {
		//[A][B][C] -> [ ][A][B]. C is lost
		push(SolidifiedRequest.EMPTY);
		return this;
	}
	
	public StackOps push(SolidifiedRequest replacement) {
		//[A][B][C] -> [r][A][B]
		if(requests.length == 0) return this;
		if(requests.length != 1) System.arraycopy(requests, 0, requests, 1, requests.length - 1);
		requests[0] = replacement;
		return this;
	}
	
	public SolidifiedRequest pull() {
		//[A][B][C] -> [B][C][ ], and A is returned
		if(requests.length == 0) return SolidifiedRequest.EMPTY;
		SolidifiedRequest first = requests[0];
		destroy(1);
		return first;
	}
	
	public StackOps destroy(int howMany) {
		//hurrr durrrr, its fast because it uses arraycopy, the "code go fast" method
		//yeah theres definitely nicer ways to do this
		if(howMany == 0 || requests.length == 0) return this;
		if(requests.length == 1) requests[0] = SolidifiedRequest.EMPTY;
		else {
			System.arraycopy(requests, howMany, requests, 0, requests.length - howMany);
			System.arraycopy(BUNCHA_EMPTIES, 0, requests, requests.length - howMany, howMany);
		}
		return this;
	}
	
	public SolidifiedRequest peek() {
		return peek(0);
	}
	
	public SolidifiedRequest peek(int depth) {
		if(depth < requests.length) return requests[depth];
		return SolidifiedRequest.EMPTY;
	}
	
	public boolean biPeekMatches(BiPredicate<SolidifiedRequest, SolidifiedRequest> peeker) {
		return peeker.test(peek(0), peek(1));
	}
	
	public StackOps biPullPush(BiFunction<SolidifiedRequest, SolidifiedRequest, SolidifiedRequest> mapper) {
		push(mapper.apply(pull(), pull()));
		return this;
	}
}
