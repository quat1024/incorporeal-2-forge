package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.rhododendrite.item.RhoCardItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
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
		super(RhoTileTypes.OP);
	}
	
	protected transient @Nullable ChainBindResult binding;
	public int comparatorSignal;
	public final ItemStackHandler inventory = new ItemStackHandler(1) {
		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
			return stack.getItem() instanceof RhoCardItem;
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
	
	public @Nullable BlockPos getDirectBind() {
		return binding == null ? null : binding.direct;
	}
	
	public @Nullable BlockPos getRootBind() {
		return binding == null ? null : binding.root;
	}
	
	public @Nullable RhodoCellTile getBoundCell() {
		if(binding == null) return null;
		assert world != null;
		TileEntity tile = world.getTileEntity(binding.root);
		return tile instanceof RhodoCellTile ? (RhodoCellTile) tile : null;
	}
	
	@Override
	public void tick() {
		if(world == null) return;
		
		binding = rootExtractingChainBind(
			getBlockState().get(BlockStateProperties.FACING),
			(cursor, tile) -> {
				if(tile instanceof RhodoCellTile) return cursor;
				else if(tile instanceof RhodoOpTile) return ((RhodoOpTile) tile).getRootBind();
				else return null;
			}
		);
		
		doIt(true);
	}
	
	public void onRedstonePower() {
		doIt(false);
	}
	
	protected void doIt(boolean isCondition) {
		if(binding == null) return;
		
		ItemStack stack = getCard();
		if(stack.isEmpty()) {
			setComparatorSignal(0);
			return;
		}
		
		RhoCardItem item = RhoCardItem.extract(stack);
		if(item == null || item.isCondition != isCondition) return;
		
		RhodoCellTile cell = getBoundCell();
		if(cell == null) return;
		
		item.action.run(cell, this);
	}
	
	//opcode fail
	public void fail() {
		setComparatorSignal(15);
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
