package agency.highlysuspect.rhododendrite.computer;

import net.minecraft.nbt.CompoundNBT;

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
}
