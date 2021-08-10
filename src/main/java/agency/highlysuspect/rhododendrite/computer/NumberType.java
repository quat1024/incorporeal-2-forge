package agency.highlysuspect.rhododendrite.computer;

import net.minecraft.nbt.CompoundNBT;
import vazkii.botania.api.corporea.CorporeaHelper;

import java.math.BigInteger;
import java.util.Optional;

public class NumberType implements DataType<BigInteger> {
	public static final BigInteger MAX = BigInteger.valueOf(10).pow(40);
	public static final BigInteger MIN = MAX.negate();
	
	@Override
	public Optional<BigInteger> fromNbt(CompoundNBT nbt) {
		if(nbt.contains("Number")) return Optional.of(BigInteger.valueOf(nbt.getLong("Number")));
		else if(nbt.contains("BigNumber")) return Optional.of(new BigInteger(nbt.getByteArray("BigNumber")));
		else return Optional.empty();
	}
	
	@Override
	public CompoundNBT toNbt(BigInteger thing) {
		CompoundNBT nbt = new CompoundNBT();
		
		long longValue = thing.longValue();
		if(thing.equals(BigInteger.valueOf(longValue))) {
			nbt.putLong("Number", longValue);
		} else {
			nbt.putByteArray("BigNumber", thing.toByteArray());
		}
		
		return nbt;
	}
	
	@Override
	public Optional<String> validate(BigInteger thing) {
		if(MAX.compareTo(thing) < 0) return Optional.of("Too big!");
		else if(MIN.compareTo(thing) > 0) return Optional.of("Too small!");
		else return Optional.empty();
	}
	
	@Override
	public boolean isZero(BigInteger thing) {
		return BigInteger.ZERO.equals(thing);
	}
	
	public static final BigInteger COMPARATOR_MIN = BigInteger.ZERO;
	public static final BigInteger COMPARATOR_MAX = BigInteger.valueOf(16384); // log2(16384) == 15
	
	@Override
	public int signalStrength(BigInteger thing) {
		if(COMPARATOR_MAX.compareTo(thing) <= 0) return 15;
		else if(COMPARATOR_MIN.compareTo(thing) > 0) return 0;
		else return CorporeaHelper.instance().signalStrengthForRequestSize(thing.intValue());
	}
	
	@Override
	public boolean dataEquals(BigInteger a, BigInteger b) {
		return a.equals(b);
	}
	
	@Override
	public int dataCompareTo(BigInteger a, BigInteger b) {
		return a.compareTo(b);
	}
	
	@Override
	public int dataHash(BigInteger thing) {
		return thing.hashCode();
	}
}