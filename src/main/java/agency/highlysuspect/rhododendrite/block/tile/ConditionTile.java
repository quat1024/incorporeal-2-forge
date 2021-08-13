package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.rhododendrite.item.ConditionCardItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.ItemStackHandler;
import vazkii.botania.api.internal.VanillaPacketDispatcher;

import javax.annotation.Nonnull;

public class ConditionTile extends RhodoNetworkTile implements ITickableTileEntity {
	public ConditionTile() {
		super(RhoTileTypes.CONDITION);
	}
	
	int signal = 0;
	
	public int getComparator() {
		return signal;
	}
	
	@Override
	public void tick() {
		CoreTile core = findCore();
		if(core == null) {
			setSignalTo(0);
			return;
		}
		
		ItemStack conditionCard = inventory.getStackInSlot(0);
		if(conditionCard.getItem() instanceof ConditionCardItem) {
			int x = ((ConditionCardItem) conditionCard.getItem()).predicate.test(core) ? 15 : 0;
			setSignalTo(MathHelper.clamp(x, 0, 15));
		} else {
			setSignalTo(0);
		}
	}
	
	private void setSignalTo(int newSignal) {
		int oldSignal = signal;
		signal = newSignal;
		if(oldSignal != newSignal) {
			markDirty(); //schedules comparator updates
		}
	}
	
	//yeah im lazy
	public final ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
			return stack.getItem() instanceof ConditionCardItem;
		}
		
		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}
		
		@Override
		protected void onContentsChanged(int slot) {
			//ConditionTile.this.whenCoreChanged(ConditionTile.this.findCore());
			
			ConditionTile.this.markDirty();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(ConditionTile.this);
		}
	};
	
	@Override
	public void writePacketNBT(CompoundNBT nbt) {
		super.writePacketNBT(nbt);
		nbt.putInt("Signal", signal);
		nbt.put("Inventory", inventory.serializeNBT());
	}
	
	@Override
	public void readPacketNBT(CompoundNBT nbt) {
		super.readPacketNBT(nbt);
		signal = nbt.getInt("Signal");
		inventory.deserializeNBT(nbt.getCompound("Inventory"));
	}
}
