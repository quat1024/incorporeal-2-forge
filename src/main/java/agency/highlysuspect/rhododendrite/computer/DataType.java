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
