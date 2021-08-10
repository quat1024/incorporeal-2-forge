package agency.highlysuspect.rhododendrite.computer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import org.apache.logging.log4j.core.util.Integers;
import vazkii.botania.api.corporea.CorporeaHelper;

import java.util.Comparator;
import java.util.Optional;

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
	public Optional<String> validate(ItemStack thing) {
		return Optional.empty();
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
