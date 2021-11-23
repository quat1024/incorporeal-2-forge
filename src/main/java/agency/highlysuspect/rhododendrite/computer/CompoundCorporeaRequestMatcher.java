package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.incorporeal.corporea.EmptyCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.MatcherUtils;
import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

import java.util.*;
import java.util.stream.Collectors;

public class CompoundCorporeaRequestMatcher implements ICorporeaRequestMatcher {
	private CompoundCorporeaRequestMatcher(List<ICorporeaRequestMatcher> others) {
		Preconditions.checkArgument(others.stream().noneMatch(other -> other instanceof CompoundCorporeaRequestMatcher), "Tried to nest compound requests");
		Preconditions.checkArgument(others.stream().noneMatch(other -> other == EmptyCorporeaRequestMatcher.INSTANCE), "Empty corporea request matcher snuck into a compound request");
		
		this.others = others;
	}
	
	public static ICorporeaRequestMatcher create(Collection<ICorporeaRequestMatcher> others) {
		if(others.size() == 0) return EmptyCorporeaRequestMatcher.INSTANCE;
		if(others.size() == 1) return others.iterator().next();
		else return new CompoundCorporeaRequestMatcher(new ArrayList<>(others));
	}
	
	public static ICorporeaRequestMatcher union(ICorporeaRequestMatcher a, ICorporeaRequestMatcher b) {
		if(a == EmptyCorporeaRequestMatcher.INSTANCE) return b;
		if(b == EmptyCorporeaRequestMatcher.INSTANCE) return a;
		
		Set<ICorporeaRequestMatcher> matcherPool = new HashSet<>();
		if(a instanceof CompoundCorporeaRequestMatcher) matcherPool.addAll(((CompoundCorporeaRequestMatcher) a).others);
		else matcherPool.add(a);
		
		if(b instanceof CompoundCorporeaRequestMatcher) matcherPool.addAll(((CompoundCorporeaRequestMatcher) b).others);
		else matcherPool.add(b);
		
		return create(matcherPool);
	}
	
	//i think the actual set theory term is "relative compliment" but i can't wrap my head around which is the complement and which is being complemented
	//So im calling it minus()
	public static ICorporeaRequestMatcher minus(ICorporeaRequestMatcher a, ICorporeaRequestMatcher b) {
		if(a == EmptyCorporeaRequestMatcher.INSTANCE) return b;
		if(b == EmptyCorporeaRequestMatcher.INSTANCE) return a;
		
		Set<ICorporeaRequestMatcher> matcherPool = new HashSet<>();
		if(a instanceof CompoundCorporeaRequestMatcher) matcherPool.addAll(((CompoundCorporeaRequestMatcher) a).others);
		else matcherPool.add(a);
		
		//intellij told me not to use a removeAll here because it's slow, ok, sure, i can do that
		if(b instanceof CompoundCorporeaRequestMatcher)
			((CompoundCorporeaRequestMatcher) b).others.forEach(matcherPool::remove);
		else matcherPool.remove(b);
		
		return create(matcherPool);
	}
	
	//Immutable, please. Also, do not nest these inside each other.
	private final List<ICorporeaRequestMatcher> others;
	
	public List<ICorporeaRequestMatcher> getChildren() {
		return others;
	}
	
	@Override
	public boolean test(ItemStack stack) {
		for(ICorporeaRequestMatcher matcher : others) {
			if(matcher.test(stack)) return true;
		}
		return false;
	}
	
	@Override
	public void writeToNBT(CompoundNBT tag) {
		ListNBT list = new ListNBT();
		for(ICorporeaRequestMatcher matcher : others) list.add(MatcherUtils.toTag(matcher));
		tag.put("CompoundRequest", list);
	}
	
	public static CompoundCorporeaRequestMatcher fromTag(CompoundNBT tag) {
		return new CompoundCorporeaRequestMatcher(tag.getList("CompoundRequest", 10).stream()
			.filter(inbt -> inbt instanceof CompoundNBT)
			.map(inbt -> (CompoundNBT) inbt)
			.map(MatcherUtils::tryFromTag)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toList()));
	}
	
	@Override
	public ITextComponent getRequestName() {
		//TODO this just comma-separates them, is that cool
		return TextComponentUtils.formatList(others, ICorporeaRequestMatcher::getRequestName);
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		
		CompoundCorporeaRequestMatcher that = (CompoundCorporeaRequestMatcher) o;
		
		return others.equals(that.others);
	}
	
	@Override
	public int hashCode() {
		return others.hashCode();
	}
}
