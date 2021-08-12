package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.block.tile.CoreTile;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class StackOps {
	public StackOps(Collection<SolidifiedRequest> requests, Collection<SolidifiedRequest.Holder> holders) {
		this.requests = requests.toArray(new SolidifiedRequest[0]);
		this.holders = holders.toArray(new SolidifiedRequest.Holder[0]);
	}
	
	public StackOps() {
		this(Collections.emptyList(), Collections.emptyList());
	}
	
	public static StackOps read(CoreTile tile) {
		return CorePathTracing.readStackOps(tile.getWorld(), tile.getPos(), tile.getBlockState());
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
		if(howMany == 0 || requests.length == 0) return this;
		if(requests.length == 1) requests[0] = SolidifiedRequest.EMPTY;
		else {
			//hurrr durrrr, its fast because it uses arraycopy, the "code go fast" method
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
