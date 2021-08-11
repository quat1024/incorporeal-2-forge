package agency.highlysuspect.rhododendrite.computer;

import com.mojang.datafixers.util.Unit;
import net.minecraft.nbt.CompoundNBT;

import java.math.BigInteger;
import java.util.Optional;

/**
 * All the information needed about how to wrap type T into the dendrite world.
 * It's the Item to Fragment's ItemStack.
 */
public interface DataType<T> {
	/**
	 * Parses the thing out of an NBT tag.
	 * This operation may fail when the NBT tag is not well-formed.
	 * This operation may *not* fail when the deserialized object fails validate(T); that is a separate step.
	 */
	Optional<T> fromNbt(CompoundNBT nbt);
	
	/**
	 * Serializes the thing to an NBT tag. This operation may not fail.
	 */
	CompoundNBT toNbt(T thing);
	
	/**
	 * Check that the object is within some well-formedness parameters, like a number not being "too big".
	 * Return "true" if the object passes validation.
	 */
	boolean validate(T thing);
	
	/**
	 * Whether this is of the unit type. There's only one unit type so don't override this.
	 */
	default boolean isUnit() {
		return this == DataTypes.EMPTY;
	}
	
	/**
	 * Whether this "has zeroness", i.e. it's the number 0, an empty item stack, etc.
	 */
	default boolean isZero(T thing) {
		return isUnit();
	}
	
	/**
	 * Converts the thing to a comparator signal strength. Must be within 0 and 15.
	 */
	default int signalStrength(T thing) {
		return 0;
	}
	
	/**
	 * If this is mutable, return a copy of it.
	 */
	default T unlink(T thing) {
		return thing;
	}
	
	/**
	 * Whether these two things are equal.
	 */
	default boolean dataEquals(T a, T b) {
		return a.equals(b);
	}
	
	/**
	 * If this thing can be converted into a BigInteger, do that.
	 */
	default Optional<BigInteger> asNumber(T thing) {
		return Optional.empty();
	}
	
	/**
	 * Produce an object similar to this thing, but holding that number instead.
	 * Exactly what this means depends on what aspect of your item asNumber reads out.
	 */
	default Optional<T> injectNumber(T thing, BigInteger number) {
		return Optional.of(thing);
	}
	
	/**
	 * Compares some aspect of the items with compareTo semantics, i.e.
	 * - a negative number when A is less then B
	 * - zero when A equals B
	 * - a positive number when A is greater than B.
	 * 
	 * By "some aspect", I mean that dataCompareTo(a, b) == 0 doesn't imply that dataEquals(a, b).
	 * Item stack comparison compares the size of the stacks only, for example.
	 * If the stacks are of different items but the sizes are the same, it returns 0.
	 * 
	 * The default implementation allocates a bunch of BigIntegers.
	 */
	default int dataCompareTo(T a, T b) {
		Optional<BigInteger> x = asNumber(a);
		if(x.isPresent()) {
			Optional<BigInteger> y = asNumber(b);
			if(y.isPresent()) {
				return x.get().compareTo(y.get());
			}
		}
		return 0;
	}
	
	/**
	 * Instantiates a Fragment without performing any validation.
	 */
	default Fragment<T> uncheckedInstantiate(T data) {
		return Fragment.create(this, data);
	}
	
	default Optional<Fragment<T>> checkedInstantiate(T data) {
		return validate(data) ? Optional.of(uncheckedInstantiate(data)) : Optional.empty();
	}
	
	/**
	 * The empty data type.
	 */
	class Empty implements DataType<Unit> {
		@Override
		public Optional<Unit> fromNbt(CompoundNBT nbt) {
			return Optional.of(Unit.INSTANCE);
		}
		
		@Override
		public CompoundNBT toNbt(Unit thing) {
			return new CompoundNBT();
		}
		
		@Override
		public boolean validate(Unit thing) {
			//Always succeeds, nothing to validate!
			return true;
		}
	}
}
