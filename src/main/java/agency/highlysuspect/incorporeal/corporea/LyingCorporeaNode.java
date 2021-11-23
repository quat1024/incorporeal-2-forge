package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;
import vazkii.botania.api.corporea.ICorporeaNode;
import vazkii.botania.api.corporea.ICorporeaRequest;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.api.corporea.ICorporeaSpark;
import vazkii.botania.common.impl.corporea.AbstractCorporeaNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//hey if this mod has a dupe bug, it's probably somewhere in this class
public class LyingCorporeaNode extends AbstractCorporeaNode {
	public LyingCorporeaNode(Level level, BlockPos pos, ICorporeaSpark spark, ICorporeaNode wrapped, List<ItemStack> spoofStacks) {
		super(level, pos, spark);
		
		this.wrapped = wrapped;
		this.spoofStacks = spoofStacks;
	}
	
	private final ICorporeaNode wrapped;
	private final List<ItemStack> spoofStacks;
	
	//"mocked request"
	@Override
	public List<ItemStack> countItems(ICorporeaRequest request) {
		//1. Filter the list of spoof items to the ones that match this corporea request,
		List<ItemStack> matchingSpoofStacks = new ArrayList<>();
		for(ItemStack spoof : spoofStacks) {
			if(request.getMatcher().test(spoof)) matchingSpoofStacks.add(spoof); //without copying them.
		}
		
		if(matchingSpoofStacks.isEmpty()) return Collections.emptyList();
		
		//2. Count how many items are in the wrapped corporea node.
		List<ItemStack> realContents = wrapped.countItems(new UntrackedCorporeaRequest(WildcardCorporeaRequestMatcher.INSTANCE));
		int howMany = 0;
		for(ItemStack stack : realContents) howMany += stack.getCount();
		
		if(howMany == 0) return Collections.emptyList();
		
		//3. Report the results as big stacks of each matching spoof item. these will probably be "overstacked".
		List<ItemStack> result = new ArrayList<>();
		for(ItemStack spoof : matchingSpoofStacks) {
			request.trackFound(howMany);
			request.trackSatisfied(howMany);
			
			ItemStack copy = spoof.copy();
			copy.setCount(howMany);
			result.add(copy);
		}
		
		return result;
	}
	
	//"real request"
	@Override
	public List<ItemStack> extractItems(ICorporeaRequest request) {
		List<ItemStack> result = new ArrayList<>();
		ICorporeaRequest wildcardRequest = new DifferentMatcherCorporeaRequest(WildcardCorporeaRequestMatcher.INSTANCE, request); //just lifting this out of the loop
		
		for(ItemStack spoof : spoofStacks) {
			if(!request.getMatcher().test(spoof)) continue;
			
			//Just indiscriminately dump the contents of the inventory into the corporea request.
			//Btw, DifferentMatcherCorporeaRequest updates things like trackSatisfied/trackFound in the request it's delegating to.
			//So I don't need to update them here.
			for(ItemStack real : wrapped.extractItems(wildcardRequest)) {
				if(real.isEmpty()) continue;
				result.add(real); //No copy; assuming it was already copied enough times by the wrapped node.
			}
		}
		
		return result;
	}
	
	public static class UntrackedCorporeaRequest implements ICorporeaRequest {
		public UntrackedCorporeaRequest(ICorporeaRequestMatcher matcher) {
			this.matcher = matcher;
		}
		
		private final ICorporeaRequestMatcher matcher;
		
		@Override
		public ICorporeaRequestMatcher getMatcher() {
			return matcher;
		}
		
		@Override
		public int getStillNeeded() {
			return -1;
		}
		
		@Override
		public int getFound() {
			return 0;
		}
		
		@Override
		public int getExtracted() {
			return 0;
		}
		
		@Override
		public void trackSatisfied(int count) {
			//Nothing.
		}
		
		@Override
		public void trackFound(int count) {
			//Nothing.
		}
		
		@Override
		public void trackExtracted(int count) {
			//Nothing.
		}
	}
	
	public static class DifferentMatcherCorporeaRequest implements ICorporeaRequest {
		public DifferentMatcherCorporeaRequest(ICorporeaRequestMatcher newMatcher, ICorporeaRequest delegate) {
			this.newMatcher = newMatcher;
			this.delegate = delegate;
		}
		
		private final ICorporeaRequestMatcher newMatcher;
		private final ICorporeaRequest delegate;
		
		@Override
		public ICorporeaRequestMatcher getMatcher() {
			return newMatcher;
		}
		
		@Override
		public int getStillNeeded() {
			return delegate.getStillNeeded();
		}
		
		@Override
		public int getFound() {
			return delegate.getFound();
		}
		
		@Override
		public int getExtracted() {
			return delegate.getExtracted();
		}
		
		@Override
		public void trackSatisfied(int count) {
			delegate.trackSatisfied(count);
		}
		
		@Override
		public void trackFound(int count) {
			delegate.trackFound(count);
		}
		
		@Override
		public void trackExtracted(int count) {
			delegate.trackExtracted(count);
		}
	}
}
