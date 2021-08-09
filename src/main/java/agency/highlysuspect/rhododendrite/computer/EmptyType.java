package agency.highlysuspect.rhododendrite.computer;

import com.mojang.datafixers.util.Unit;
import net.minecraft.nbt.CompoundNBT;

import java.util.Optional;

public class EmptyType implements DataType<Unit> {
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
		return Optional.empty();
	}
	
	@Override
	public boolean isUnit() {
		return true;
	}
	
	@Override
	public boolean isZero(Unit thing) {
		return true;
	}
}
