package agency.highlysuspect.incorporeal.corporea;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.rhododendrite.Rho;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.common.block.tile.corporea.TileCorporeaRetainer;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class MatcherUtils {
	//Yeah, the deserializer is not actually from Botania, so using a Botania id for this is probably wrong.
	//TODO: Is it better to just register a deserializer, or make my own empty type (is this intended to be used as an "empty" matcher), or what 
	public static final ResourceLocation DUMMY_ID = Inc.botaniaId("dummy");
	
	public static Optional<ICorporeaRequestMatcher> tryFromTag(CompoundNBT nbt) {
		ResourceLocation type = ResourceLocation.tryCreate(nbt.getString("type"));
		if(type == null) return Optional.empty();
		if(type.equals(DUMMY_ID)) return Optional.of(ICorporeaRequestMatcher.Dummy.INSTANCE);
		
		Map<ResourceLocation, Function<CompoundNBT, ? extends ICorporeaRequestMatcher>> des = getDeserializers();
		Function<CompoundNBT, ? extends ICorporeaRequestMatcher> de = des.get(type);
		
		if(de == null) {
			Rho.LOGGER.warn("Can't deserialize ICorporeaRequestMatcher of type " + type + " as it doesn't have a registered deserializer");
			return Optional.empty();
		}
		
		return Optional.of(de.apply(nbt));
	}
	
	public static CompoundNBT toTag(ICorporeaRequestMatcher matcher) {
		Map<Class<? extends ICorporeaRequestMatcher>, ResourceLocation> nameMap = getSerializers();
		ResourceLocation name = matcher.getClass().equals(ICorporeaRequestMatcher.Dummy.class) ? DUMMY_ID : nameMap.get(matcher.getClass());
		
		if(name == null) {
			Rho.LOGGER.warn("Can't serialize ICorporeaRequestMatcher of class " + matcher.getClass().getSimpleName() + " as it doesn't have a registered ID");
			return new CompoundNBT();
		} else {
			CompoundNBT tag = new CompoundNBT();
			tag.putString("type", name.toString());
			matcher.writeToNBT(tag);
			return tag;
		}
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
	
	//AVERT YOUR EYES!!!!!!!!!!!!!!!!!!!!!!! IM FUCKING SORRY!!!!!!!!!! LOOOOOOLLLL
	//YEAH SOOOoooo i should probably bug botania devs about this, or make it with Mixin
	
	public static boolean equals(ICorporeaRequestMatcher a, ICorporeaRequestMatcher b) {
		if(a == null && b == null) return true;
		if((a == null) != (b == null)) return false;
		if(!a.getClass().equals(b.getClass())) return false;
		
		CompoundNBT aNbt = new CompoundNBT(); a.writeToNBT(aNbt);
		CompoundNBT bNbt = new CompoundNBT(); b.writeToNBT(bNbt);
		return aNbt.equals(bNbt);
	}
	
	public static int hashCode(ICorporeaRequestMatcher o) {
		CompoundNBT nbt = new CompoundNBT(); o.writeToNBT(nbt);
		return nbt.hashCode();
	}
	
	public static final Hash.Strategy<ICorporeaRequestMatcher> HASH_STRATEGY = new Hash.Strategy<ICorporeaRequestMatcher>() {
		@Override
		public int hashCode(ICorporeaRequestMatcher o) {
			return MatcherUtils.hashCode(o);
		}
		
		@Override
		public boolean equals(ICorporeaRequestMatcher a, ICorporeaRequestMatcher b) {
			return MatcherUtils.equals(a, b);
		}
	};
}
