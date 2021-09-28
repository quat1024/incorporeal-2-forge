package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.computer.RhodoFunnelable;
import agency.highlysuspect.rhododendrite.computer.RhodoFunnelableCapability;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import vazkii.botania.api.internal.VanillaPacketDispatcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class RhodoCellTile extends AbstractComputerTile implements ITickableTileEntity {
	public RhodoCellTile() {
		super(null);
	}
	
	protected transient @Nullable BlockPos binding; //Not serialized.
	protected SolidifiedRequest request = SolidifiedRequest.EMPTY;
	protected RhodoCellTile.Funnelable funnelable = this.new Funnelable(); //BRING OUT THE CURSED SYNTAX
	
	@Override
	public void tick() {
		binding = directBind(getBlockState().get(BlockStateProperties.FACING), (cursor, tile) -> tile instanceof RhodoCellTile);
	}
	
	public @Nullable RhodoCellTile getBoundCell() {
		if(binding == null) return null;
		
		assert world != null;
		TileEntity tile = world.getTileEntity(binding);
		return tile instanceof RhodoCellTile ? (RhodoCellTile) tile : null;
	}
	
	public void setRequest(SolidifiedRequest request) {
		boolean changed = !Objects.equals(this.request, request);
		this.request = request;
		if(changed) {
			markDirty();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}
	
	//Accept this SolidifiedRequest into myself, punting the previous contents to the next rhodo cell.
	//Written in a weird way to use constant stack space and not blow up if you form a cycle.
	public void push(SolidifiedRequest head) {
		Set<RhodoCellTile> visited = new HashSet<>();
		RhodoCellTile visiting = this;
		do {
			//swap "head" for whatever's inside the visiting node
			SolidifiedRequest a = head;
			head = request;
			visiting.setRequest(a);
			
			//visit the next node
			visited.add(visiting);
			visiting = getBoundCell();
		} while(visiting != null && !visited.contains(visiting));
	}
	
	public SolidifiedRequest pull() {
		//Build the chain structure.
		//Look at my binding, and my binding's binding, etc, until finding the end (or a cycle).
		List<RhodoCellTile> fullChain = new ArrayList<>();
		Set<RhodoCellTile> chain_ = new HashSet<>(); //Avoid calling list.contains, and no, i didn't profile this
		RhodoCellTile visiting = this;
		do {
			fullChain.add(visiting);
			chain_.add(visiting);
			visiting = getBoundCell();
		} while(visiting != null && !chain_.contains(visiting));
		
		//Pull each request backwards one step, leaving an empty request in the remaining cell.
		SolidifiedRequest cursor = SolidifiedRequest.EMPTY;
		for(int i = fullChain.size() - 1; i >= 0; i--) {
			SolidifiedRequest a = fullChain.get(i).request;
			fullChain.get(i).setRequest(cursor);
			cursor = a;
		}
		
		//Return the last element to be removed, which was from this cell.
		return cursor;
	}
	
	public SolidifiedRequest peek() {
		return request;
	}
	
	public SolidifiedRequest peekNext() {
		RhodoCellTile next = getBoundCell();
		return next == null ? SolidifiedRequest.EMPTY : next.peek();
	}
	
	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
		if(cap == RhodoFunnelableCapability.INSTANCE) return LazyOptional.of(() -> funnelable).cast();
		else return super.getCapability(cap);
	}
	
	@Override
	public void writePacketNBT(CompoundNBT nbt) {
		super.writePacketNBT(nbt);
		nbt.put("Request", request.toTag());
	}
	
	@Override
	public void readPacketNBT(CompoundNBT nbt) {
		super.readPacketNBT(nbt);
		request = SolidifiedRequest.fromNbtOrEmpty(nbt.getCompound("Request"));
	}
	
	/* non-static */ class Funnelable implements RhodoFunnelable {
		@Override
		public boolean canRhodoExtract() {
			return true;
		}
		
		@Override
		public Optional<SolidifiedRequest> rhodoExtract(boolean simulate) {
			return Optional.of(request);
		}
		
		@Override
		public boolean canRhodoInsert() {
			return true;
		}
		
		@Override
		public boolean tryRhodoInsert(@Nonnull SolidifiedRequest request, boolean simulate) {
			if(!simulate) setRequest(request);
			return true;
		}
	}
}
