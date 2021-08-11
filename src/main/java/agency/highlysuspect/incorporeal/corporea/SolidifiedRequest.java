package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.common.block.tile.corporea.TileCorporeaRetainer;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class SolidifiedRequest {
	public SolidifiedRequest(ICorporeaRequestMatcher matcher, int count) {
		this.matcher = matcher;
		this.count = count;
	}
	
	public final ICorporeaRequestMatcher matcher;
	public final int count;
	
	public CompoundNBT toTag() {
		CompoundNBT tag = new CompoundNBT();
		
		//type
		tag.putString("type", getSerializers().get(matcher.getClass()).toString());
		
		//properties (technically this writes to the same namespace as type/count, but it's okay)
		matcher.writeToNBT(tag);
		
		//count
		tag.putInt("count", count);
		return tag;
	}
	
	public ITextComponent toText() {
		return new TranslationTextComponent("incorporeal.solidified_request", count, matcher.getRequestName());
	}
	
	public static Optional<SolidifiedRequest> tryFromTag(CompoundNBT tag) {
		int count = tag.getInt("count");
		if(count == 0) return Optional.empty();
		
		return Optional.ofNullable(ResourceLocation.tryCreate(tag.getString("type")))
			.flatMap(type -> Optional.ofNullable(getDeserializers().get(type))) //deserializers aren't as guaranteed to exist (e.g. uninstalling an addon)
			.map(de -> new SolidifiedRequest(de.apply(tag), count));
	}
	
	//TODO: I'd love to use not-Reflection for this, but... yeah.
	// An Accessor was my first choice, but since they're static fields, I need a TileCorporeaRetainer *instance* in order to call methods on the accessor.
	// Normally for things like Blocks this isn't too painful since you can just use Blocks.AIR or whatever but this is not safe to randomly construct.
	// ...
	// Can I codecpill the botania devs abt this, it will really be handy
	
	private static Map<ResourceLocation, Function<CompoundNBT, ? extends ICorporeaRequestMatcher>> getDeserializers() {
		try {
			Field f = TileCorporeaRetainer.class.getDeclaredField("corporeaMatcherDeserializers");
			f.setAccessible(true);
			//noinspection unchecked
			return (Map<ResourceLocation, Function<CompoundNBT, ? extends ICorporeaRequestMatcher>>) f.get(null);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Problem reflecting!", e);
		}
	}
	
	private static Map<Class<? extends ICorporeaRequestMatcher>, ResourceLocation> getSerializers() {
		try {
			Field f = TileCorporeaRetainer.class.getDeclaredField("corporeaMatcherSerializers");
			f.setAccessible(true);
			//noinspection unchecked
			return (Map<Class<? extends ICorporeaRequestMatcher>, ResourceLocation>) f.get(null);
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException("Problem reflecting!", e);
		}
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
		return compareRequestMatcher(matcher, that.matcher);
	}
	
	public static boolean compareRequestMatcher(ICorporeaRequestMatcher a, ICorporeaRequestMatcher b) {
		if(a == null && b == null) return true;
		if((a == null) != (b == null)) return false;
		if(!a.getClass().equals(b.getClass())) return false;
		
		//So I should probably pester Botania devs to implement equals and hashcode for these, huh...
		CompoundNBT aNbt = new CompoundNBT(); a.writeToNBT(aNbt);
		CompoundNBT bNbt = new CompoundNBT(); b.writeToNBT(bNbt);
		return aNbt.equals(bNbt);
	}
}
