package agency.highlysuspect.rhododendrite.computer;

import com.mojang.datafixers.util.Unit;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nonnull;
import java.math.BigInteger;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * A piece of data (of type T) in the dendrite world.
 */
public class Fragment<T> {
	@SuppressWarnings("unchecked")
	private Fragment(DataType<?> type, Object data) {
		this.type = (DataType<T>) type;
		this.data = (T) data;
	}
	
	public static final Fragment<Unit> EMPTY = DataTypes.EMPTY.uncheckedInstantiate(Unit.INSTANCE);
	
	public static <T> Fragment<T> create(DataType<T> type, T data) {
		return new Fragment<>(type, data);
	}
	
	//when you try to deserialize fucking anything with a complex type, and the java type system just craps out on you
	public static Fragment<?> unchecked(DataType<?> type, Object data) {
		return new Fragment<>(type, data);
	}
	
	public final DataType<T> type;
	public final T data;
	
	public DataType<T> getType() {
		return type;
	}
	
	public T getData() {
		return data;
	}
	
	public boolean validate() {
		return type.validate(data);
	}
	
	//Performs a mapping operation without calling validate().
	public Fragment<T> uncheckedMap(UnaryOperator<T> op) {
		return create(type, op.apply(data));
	}
	
	public <X> Fragment<X> uncheckedMap(DataType<X> newType, Function<T, X> op) {
		return create(newType, op.apply(data));
	}
	
	public Optional<Fragment<T>> map(UnaryOperator<T> op) {
		T newData = op.apply(data);
		return type.validate(newData) ? Optional.of(create(type, newData)) : Optional.empty();
	}
	
	public <X> Optional<Fragment<X>> map(DataType<X> newType, Function<T, X> op) {
		X newData = op.apply(data);
		return newType.validate(newData) ? Optional.of(create(newType, newData)) : Optional.empty();
	}
	
	public Optional<Fragment<T>> flatMap(Function<T, Optional<T>> op) {
		return op.apply(data).filter(type::validate).map(t -> create(type, t));
	}
	
	public <X> Optional<Fragment<X>> flatMap(DataType<X> newType, Function<T, Optional<X>> op) {
		return op.apply(data).filter(newType::validate).map(x -> create(newType, x));
	}
	
	public boolean isUnit() {
		return type.isUnit();
	}
	
	public boolean isZero() {
		return type.isZero(data);
	}
	
	public int signalStrength() {
		return type.signalStrength(data);
	}
	
	public Fragment<T> unlink() {
		return create(type, type.unlink(data));
	}
	
	public Optional<BigInteger> asNumber() {
		return type.asNumber(data);
	}
	
	public Optional<Fragment<T>> injectNumber(BigInteger number) {
		return flatMap(d -> type.injectNumber(d, number));
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		
		Fragment<?> other = (Fragment<?>) o;
		
		if(!type.equals(other.type)) return false;
		
		//noinspection unchecked
		return type.dataEquals(data, (T) other.data); //changed from typical implementations of equals(). cast is safe because type.equals() was checked
	}
	
	public CompoundNBT toNbt(Registry<DataType<?>> types) {
		CompoundNBT nbt = type.toNbt(data);
		
		ResourceLocation typeName = types.getKey(type);
		if(typeName == null) throw new IllegalStateException("Type " + type.getClass().getSimpleName() + " not found in registry " + types);
		nbt.putString("FragType", typeName.toString());
		return nbt;
	}
	
	public static Optional<Fragment<?>> fromNbt(Registry<DataType<?>> types, CompoundNBT nbt) {
		return types.getOptional(ResourceLocation.tryCreate(nbt.getString("FragType")))
			.flatMap(type -> type.fromNbt(nbt).map(data -> unchecked(type, data)));
	}
	
	public static Fragment<?> fromNbtOrEmpty(Registry<DataType<?>> types, CompoundNBT nbt) {
		return fromNbt(types, nbt).orElse(Fragment.EMPTY);
	}
	
	//capability target
	public interface Holder {
		@Nonnull Fragment<?> getFragment();
		void setFragment(@Nonnull Fragment<?> fragment);
	}
}
