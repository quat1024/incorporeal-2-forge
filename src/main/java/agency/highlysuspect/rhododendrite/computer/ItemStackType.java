package agency.highlysuspect.rhododendrite.computer;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;

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
}
