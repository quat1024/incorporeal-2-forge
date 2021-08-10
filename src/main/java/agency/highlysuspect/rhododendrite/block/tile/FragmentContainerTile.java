package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.rhododendrite.computer.DataTypes;
import agency.highlysuspect.rhododendrite.computer.Fragment;
import agency.highlysuspect.rhododendrite.computer.FragmentCapability;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.block.tile.TileMod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class FragmentContainerTile extends TileMod implements Fragment.Holder {
	public FragmentContainerTile(TileEntityType<?> type) {
		super(type);
	}
	
	protected Fragment<?> fragment = Fragment.EMPTY;
	
	@Override
	public void writePacketNBT(CompoundNBT cmp) {
		super.writePacketNBT(cmp);
		cmp.put("Fragment", fragment.toNbt(DataTypes.REGISTRY));
	}
	
	@Override
	public void readPacketNBT(CompoundNBT cmp) {
		super.readPacketNBT(cmp);
		fragment = Fragment.fromNbtOrEmpty(DataTypes.REGISTRY, cmp.getCompound("Fragment"));
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if(cap == FragmentCapability.INSTANCE) return LazyOptional.of(() -> this).cast();
		else return super.getCapability(cap, side);
	}
	
	public Fragment<?> getFragment() {
		return fragment;
	}
	
	public void setFragment(Fragment<?> newFragment) {
		if(!fragment.equals(newFragment)) {
			fragment = newFragment;
			
			markDirty(); //also calls updateComparator
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}
	
	public int signalStrength() {
		return fragment.signalStrength();
	}
}
