package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.rhododendrite.item.ConditionCardItem;
import agency.highlysuspect.rhododendrite.item.OpcodeCardItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import vazkii.botania.api.internal.VanillaPacketDispatcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RhodoOpTile extends AbstractComputerTile implements ITickableTileEntity {
	public RhodoOpTile() {
		super(null); //TODO
	}
	
	protected transient @Nullable BlockPos coreBinding;
	public int comparatorSignal;
	public final ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
			return stack.getItem() instanceof ConditionCardItem || stack.getItem() instanceof OpcodeCardItem;
		}
		
		@Override
		public int getSlotLimit(int slot) {
			return 1;
		}
		
		@Override
		protected void onContentsChanged(int slot) {
			RhodoOpTile.this.markDirty();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(RhodoOpTile.this);
		}
	};
	
	@Override
	public void tick() {
		if(world == null) return;
		
		coreBinding = rootExtractingChainBind(
			getBlockState().get(BlockStateProperties.FACING),
			(cursor, tile) -> {
				if(tile instanceof RhodoCellTile) return pos;
				else if(tile instanceof RhodoOpTile) return ((RhodoOpTile) tile).coreBinding;
				else if(tile instanceof RhodoFunnelTile) return ((RhodoFunnelTile) tile).foreBinding;
				else return null;
			}
		);
		
		if(coreBinding != null) {
			ItemStack card = getCard();
			if(card.isEmpty()) {
				setComparatorSignal(0);
			} else if(card.getItem() instanceof ConditionCardItem) {
				//TODO handle condition.
			}
		}
	}
	
	public void onRedstonePower() {
		if(coreBinding != null) {
			ItemStack card = getCard();
			if(!card.isEmpty() && card.getItem() instanceof OpcodeCardItem) {
				//TODO handle opcode. (and write comparator signal on failure.)
			}
		}
	}
	
	public void setComparatorSignal(int comparatorSignal) {
		boolean changed = this.comparatorSignal != comparatorSignal;
		this.comparatorSignal = comparatorSignal;
		if(changed) {
			markDirty();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}
	
	public int getComparatorSignal() {
		return comparatorSignal;
	}
	
	public ItemStack getCard() {
		return inventory.getStackInSlot(0);
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return LazyOptional.of(() -> inventory).cast();
		else return super.getCapability(cap);
	}
	
	@Override
	public void writePacketNBT(CompoundNBT nbt) {
		super.writePacketNBT(nbt);
		nbt.put("Inventory", inventory.serializeNBT());
		nbt.putInt("Signal", comparatorSignal);
	}
	
	@Override
	public void readPacketNBT(CompoundNBT nbt) {
		super.readPacketNBT(nbt);
		inventory.deserializeNBT(nbt.getCompound("Inventory"));
		comparatorSignal = nbt.getInt("Signal");
	}
}
