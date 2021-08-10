package agency.highlysuspect.rhododendrite.computer;

import com.mojang.datafixers.util.Unit;
import net.minecraft.nbt.CompoundNBT;

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
	 * Exactly what this means depends on what gameplay restrictions you want to place on the datatype.
	 * If the type passes validation, return Optional.empty().
	 * If the type fails validation, return a string representing what is wrong (e.g. "number too large")
	 * TODO it probably shouldnt be a string lol
	 */
	Optional<String> validate(T thing);
	
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
	 * Whether these two things are equal.
	 */
	default boolean dataEquals(T a, T b) {
		return a.equals(b);
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
	 * The default implementation compares items more-or-less randomly; literally just comparing dataHash.
	 * Please override this, lol.
	 */
	default int dataCompareTo(T a, T b) {
		return Integer.compare(dataHash(a), dataHash(b));
	}
	
	/**
	 * Hashes the thing. Might remove this one if it turns out to be unneeded.
	 */
	default int dataHash(T thing) {
		return thing.hashCode();
	}
	
	/**
	 * Instantiates a Fragment without performing any validation.
	 */
	default Fragment<T> uncheckedInstantiate(T data) {
		return Fragment.create(this, data);
	}
	
	//TODO this probably shouldn't be an Optional, but should return the error
	default Optional<Fragment<T>> checkedInstantiate(T data) {
		Optional<String> error = validate(data);
		if(error.isPresent()) return Optional.empty();
		else return Optional.of(uncheckedInstantiate(data));
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
		public Optional<String> validate(Unit thing) {
			//Always succeeds, nothing to validate!
			return Optional.empty();
		}
	}
}
