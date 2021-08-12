package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class SolidifiedRequest {
	public SolidifiedRequest(ICorporeaRequestMatcher matcher, int count) {
		this.matcher = matcher;
		this.count = count;
	}
	
	public final ICorporeaRequestMatcher matcher;
	public final int count;
	
	public static final SolidifiedRequest EMPTY = new SolidifiedRequest(EmptyCorporeaRequestMatcher.INSTANCE, 0);
	
	public CompoundNBT toTag() {
		CompoundNBT tag = MatcherUtils.toTag(matcher);
		
		//count
		tag.putInt("count", count);
		return tag;
	}
	
	public static Optional<SolidifiedRequest> tryFromTag(CompoundNBT tag) {
		return MatcherUtils.tryFromTag(tag).map(matcher -> {
			int count = tag.getInt("count");
			return new SolidifiedRequest(matcher, count);
		});
	}
	
	public static SolidifiedRequest fromNbtOrEmpty(CompoundNBT tag) {
		return tryFromTag(tag).orElse(EMPTY);
	}
	
	//Slightly different semantics from ItemStack#isEmpty; it's totally valid for the count to be 0, for example
	public boolean isEmpty() {
		return matcher == EmptyCorporeaRequestMatcher.INSTANCE && count == 0;
	}
	
	public ITextComponent toText() {
		return new TranslationTextComponent("incorporeal.solidified_request", count, matcher.getRequestName());
	}
	
	public int signalStrength() {
		return CorporeaHelper.instance().signalStrengthForRequestSize(count);
	}
	
	public SolidifiedRequest withCount(int newCount) {
		return new SolidifiedRequest(matcher, newCount);
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		
		SolidifiedRequest that = (SolidifiedRequest) o;
		
		if(count != that.count) return false;
		return Objects.equals(matcher, that.matcher);
	}
	
	@Override
	public String toString() {
		return "SolidifiedRequest[" + count + "x " + matcher.getRequestName().getString() + "]";
	}
	
	public interface Holder {
		@Nonnull SolidifiedRequest getRequest();
		void setRequest(@Nonnull SolidifiedRequest newRequest);
	}
	
	//TODO: This is mainly used in Rhododendrite, might be beneficial to use this capability to interact with Botania too though.
	// e.g. stuff like the retainer evaporator could benefit from this.
	// Especially the NonNull semantics - in botania, nullable requests are all over the place
	public static class Cap {
		@CapabilityInject(SolidifiedRequest.Holder.class)
		public static Capability<SolidifiedRequest.Holder> INSTANCE;
		
		public static void initialize() {
			CapabilityManager.INSTANCE.register(SolidifiedRequest.Holder.class, Default.INSTANCE, () -> Default.INSTANCE);
		}
		
		//I'm hearing that the "default capability" stuff is getting removed in Forge 1.17 anyways, so I'm not gonna miss it too badly
		public static class Default implements SolidifiedRequest.Holder, Capability.IStorage<SolidifiedRequest.Holder> {
			public static final Default INSTANCE = new Default();
			
			@Nonnull
			@Override
			public SolidifiedRequest getRequest() {
				return SolidifiedRequest.EMPTY;
			}
			
			@Override
			public void setRequest(@Nonnull SolidifiedRequest fragment) {
				//No
			}
			
			@Nullable
			@Override
			public INBT writeNBT(Capability<Holder> capability, Holder instance, Direction side) {
				//No
				return null;
			}
			
			@Override
			public void readNBT(Capability<Holder> capability, Holder instance, Direction side, INBT nbt) {
				//No
			}
		}
	}
}
