package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.computer.RhodoFunnelable;
import agency.highlysuspect.rhododendrite.computer.RhodoFunnelableCapability;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.block.tile.TileMod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class RequestHolderTile extends TileMod implements SolidifiedRequest.Holder {
	public RequestHolderTile(TileEntityType<?> type) {
		super(type);
	}
	
	protected SolidifiedRequest request = SolidifiedRequest.EMPTY;
	protected final RhodoFunnelable funnelable = new RhodoFunnelable.ForRequestHolder(this);
	
	@Override
	public void writePacketNBT(CompoundNBT cmp) {
		super.writePacketNBT(cmp);
		cmp.put("Request", request.toTag());
	}
	
	@Override
	public void readPacketNBT(CompoundNBT cmp) {
		super.readPacketNBT(cmp);
		request = SolidifiedRequest.fromNbtOrEmpty(cmp.getCompound("Request"));
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if(cap == SolidifiedRequest.Cap.INSTANCE) return LazyOptional.of(() -> this).cast();
		else if(cap == RhodoFunnelableCapability.INSTANCE) return LazyOptional.of(() -> funnelable).cast();
		else return super.getCapability(cap, side);
	}
	
	@Nonnull
	@Override
	public SolidifiedRequest getRequest() {
		return request;
	}
	
	@Override
	public void setRequest(@Nonnull SolidifiedRequest newRequest) {
		if(!newRequest.equals(request)) {
			this.request = newRequest;
			
			markDirty(); //also calls updateComparator
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}
	
	public int signalStrength() {
		return request.signalStrength();
	}
}
