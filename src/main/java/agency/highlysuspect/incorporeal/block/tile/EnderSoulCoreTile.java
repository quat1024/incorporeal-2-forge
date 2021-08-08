package agency.highlysuspect.incorporeal.block.tile;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.EmptyHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class EnderSoulCoreTile extends AbstractSoulCoreTile {
	public EnderSoulCoreTile() {
		super(IncTileTypes.ENDER_SOUL_CORE);
	}
	
	@Override
	protected int getMaxMana() {
		return 5000;
	}
	
	private IItemHandler handler = EmptyHandler.INSTANCE;
	
	@Override
	public void tick() {
		super.tick();
		if(world == null || world.isRemote) return;
		
		Optional<ServerPlayerEntity> player = findPlayer();
		boolean isHere = player.isPresent();
		
		if(!isHere) {
			handler = EmptyHandler.INSTANCE;
		} else if(handler == EmptyHandler.INSTANCE) {
			handler = new ManaDrainingInvWrapper(player.get().getInventoryEnderChest());
		}
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return LazyOptional.of(() -> handler).cast();
		else return super.getCapability(cap, side);
	}
	
	//non-static inner class
	public class ManaDrainingInvWrapper extends InvWrapper {
		public ManaDrainingInvWrapper(IInventory inv) {
			super(inv);
		}
		
		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			ItemStack sup = super.extractItem(slot, amount, simulate);
			if(!simulate) drainMana(5 * sup.getCount());
			return sup;
		}
		
		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			ItemStack sup = super.insertItem(slot, stack, simulate);
			if(!simulate) drainMana(5 * (stack.getCount() - sup.getCount()));
			return sup;
		}
	}
}
