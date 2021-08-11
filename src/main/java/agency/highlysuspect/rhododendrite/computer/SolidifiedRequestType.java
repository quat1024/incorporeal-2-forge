package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.nbt.CompoundNBT;
import vazkii.botania.api.corporea.CorporeaHelper;

import java.math.BigInteger;
import java.util.Optional;

public class SolidifiedRequestType implements DataType<SolidifiedRequest> {
	@Override
	public Optional<SolidifiedRequest> fromNbt(CompoundNBT nbt) {
		return SolidifiedRequest.tryFromTag(nbt);
	}
	
	@Override
	public CompoundNBT toNbt(SolidifiedRequest thing) {
		return thing.toTag();
	}
	
	@Override
	public boolean validate(SolidifiedRequest thing) {
		return true; //I guess??
	}
	
	@Override
	public boolean isZero(SolidifiedRequest thing) {
		return thing.count == 0;
	}
	
	@Override
	public int signalStrength(SolidifiedRequest thing) {
		return CorporeaHelper.instance().signalStrengthForRequestSize(thing.count);
	}
	
	@Override
	public SolidifiedRequest unlink(SolidifiedRequest thing) {
		return thing; //it's immutable
	}
	
	@Override
	public boolean dataEquals(SolidifiedRequest a, SolidifiedRequest b) {
		return a.equals(b);
	}
	
	@Override
	public Optional<BigInteger> asNumber(SolidifiedRequest thing) {
		return Optional.of(BigInteger.valueOf(thing.count));
	}
	
	@Override
	public Optional<SolidifiedRequest> injectNumber(SolidifiedRequest thing, BigInteger number) {
		try {
			int exactValue = number.intValueExact();
			return Optional.of(thing.withCount(exactValue));
		} catch (ArithmeticException e) {
			return Optional.empty();
		}
	}
	
	@Override
	public int dataCompareTo(SolidifiedRequest a, SolidifiedRequest b) {
		return Integer.compare(a.count, b.count);
	}
}
