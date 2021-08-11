package agency.highlysuspect.rhododendrite.computer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import vazkii.botania.api.corporea.CorporeaHelper;

import java.math.BigInteger;
import java.util.Optional;

//Goes unused since I forgot Cygnus worked on corporea requests, which aren't quite ItemStacks
public class ItemStackType implements DataType<ItemStack> {
	@Override
	public Optional<ItemStack> fromNbt(CompoundNBT nbt) {
		//ItemStack#read(CompoundNBT) just logs errors and returns EMPTY. This actually errors on errors
		return ItemStack.CODEC.parse(NBTDynamicOps.INSTANCE, nbt).result();
	}
	
	@Override
	public CompoundNBT toNbt(ItemStack thing) {
		return thing.serializeNBT(); //Forge extension
	}
	
	@Override
	public boolean validate(ItemStack thing) {
		return true;
	}
	
	@Override
	public boolean isZero(ItemStack thing) {
		return thing.isEmpty();
	}
	
	@Override
	public int signalStrength(ItemStack thing) {
		return CorporeaHelper.instance().signalStrengthForRequestSize(thing.getCount());
	}
	
	@Override
	public ItemStack unlink(ItemStack thing) {
		return thing.copy();
	}
	
	@Override
	public Optional<BigInteger> asNumber(ItemStack thing) {
		return Optional.of(BigInteger.valueOf(thing.getCount()));
	}
	
	@Override
	public Optional<ItemStack> injectNumber(ItemStack thing, BigInteger number) {
		//Only works if the number fits inside an int. Yeah, not great. Some kind of BigItemStack would be better.
		try {
			int asInt = number.intValueExact();
			ItemStack copy = thing.copy();
			copy.setCount(asInt);
			return Optional.of(copy);
		} catch (ArithmeticException e) {
			return Optional.empty();
		}
	}
	
	@Override
	public boolean dataEquals(ItemStack a, ItemStack b) {
		return ItemStack.areItemStacksEqual(a, b);
	}
	
	@Override
	public int dataCompareTo(ItemStack a, ItemStack b) {
		return Integer.compare(a.getCount(), b.getCount());
	}
	
	@Override
	public int dataHash(ItemStack thing) {
		return DataType.super.dataHash(thing);
	}
}
