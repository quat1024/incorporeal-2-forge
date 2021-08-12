package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.rhododendrite.computer.Fragment;
import agency.highlysuspect.rhododendrite.item.ConditionCardItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.ItemStackHandler;
import vazkii.botania.api.internal.VanillaPacketDispatcher;

import javax.annotation.Nonnull;

public class ConditionTile extends RhodoNetworkTile implements CoreTile.ChangeListener {
	public ConditionTile() {
		super(RhoTileTypes.CONDITION);
	}
	
	int signal = 0;
	
	@Override
	public void whenPlaced() {
		super.whenPlaced();
		CoreTile core = findCore();
		if(core != null) core.registerListener(pos);
	}
	
	@Override
	public void whenWanded() {
		super.whenWanded();
		
		//idk just in case.. if it gets broken this lets you fix it w/o breaking and replacing
		CoreTile core = findCore();
		if(core != null) core.registerListener(pos);
	}
	
	public int getComparator() {
		return signal;
	}
	
	@Override
	public void whenCoreChanged(Fragment<?> oldFragment, Fragment<?> newFragment, CoreTile core) {
		change(core);
	}
	
	private void change(CoreTile core) {
		if(core == null) return;
		
		int oldSignal = signal;
		
		ItemStack conditionCard = inventory.getStackInSlot(0);
		if(conditionCard.getItem() instanceof ConditionCardItem) {
			signal = ((ConditionCardItem) conditionCard.getItem()).predicate.test(world, pos, getBlockState(), core) ? 15 : 0;
			signal = MathHelper.clamp(signal, 0, 15);
		} else {
			signal = 0;
		}
		
		if(oldSignal != signal) {
			markDirty(); //schedules comparator updates as well
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
			ConditionTile.this.change(ConditionTile.this.findCore());
			
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
