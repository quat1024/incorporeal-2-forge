package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.rhododendrite.item.RhoCardItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import vazkii.botania.api.internal.VanillaPacketDispatcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RhodoOpTile extends AbstractComputerTile implements TickableBlockEntity {
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
			RhodoOpTile.this.setChanged();
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
		assert level != null;
		BlockEntity tile = level.getBlockEntity(binding.root);
		return tile instanceof RhodoCellTile ? (RhodoCellTile) tile : null;
	}
	
	@Override
	public void tick() {
		if(level == null) return;
		
		binding = rootExtractingChainBind(
			getBlockState().getValue(BlockStateProperties.FACING),
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
			setChanged();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}
	
	public int getComparatorSignal() {
		return comparatorSignal;
	}
	
	public ItemStack getCard() {
		return inventory.getStackInSlot(0);
	}
	
	@Override
	public AABB getRenderBoundingBox() {
		return binding == null ? new AABB(worldPosition) : new AABB(worldPosition, binding.direct);
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction dir) {
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return LazyOptional.of(() -> inventory).cast();
		else return super.getCapability(cap, dir);
	}
	
	@Override
	public void writePacketNBT(CompoundTag nbt) {
		super.writePacketNBT(nbt);
		nbt.put("Inventory", inventory.serializeNBT());
		nbt.putInt("Signal", comparatorSignal);
	}
	
	@Override
	public void readPacketNBT(CompoundTag nbt) {
		super.readPacketNBT(nbt);
		inventory.deserializeNBT(nbt.getCompound("Inventory"));
		comparatorSignal = nbt.getInt("Signal");
	}
}
