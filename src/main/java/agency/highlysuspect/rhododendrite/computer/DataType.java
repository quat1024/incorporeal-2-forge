package agency.highlysuspect.rhododendrite.computer;

import net.minecraft.nbt.CompoundNBT;

import java.util.Optional;

/**
 * All the information needed about how to wrap type T into the dendrite world.
 * It's the Item to Fragment's ItemStack.
 */
public interface DataType<T> {
	Optional<T> fromNbt(CompoundNBT nbt);
	CompoundNBT toNbt(T thing);
	Optional<String> validate(T thing);
	
	/**
	 * Whether this is of the unit type.
	 */
	default boolean isUnit() {
		return false;
	}
	
	/**
	 * Whether this is "zero", i.e. the number 0, an empty item stack, etc.
	 */
	default boolean isZero(T thing) {
		return true;
	}
	
	//Does not perform validation on the data.
	default Fragment<T> uncheckedInstantiate(T data) {
		return Fragment.create(this, data);
	}
}
