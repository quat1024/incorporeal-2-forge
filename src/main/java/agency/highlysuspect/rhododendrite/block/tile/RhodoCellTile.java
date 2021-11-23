package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.computer.RhodoFunnelable;
import agency.highlysuspect.rhododendrite.computer.RhodoFunnelableCapability;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import vazkii.botania.api.internal.VanillaPacketDispatcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class RhodoCellTile extends AbstractComputerTile implements ITickableTileEntity {
	public RhodoCellTile() {
		super(RhoTileTypes.CELL);
	}
	
	protected transient @Nullable BlockPos binding; //Not serialized.
	protected SolidifiedRequest request = SolidifiedRequest.EMPTY;
	protected final RhodoCellTile.Funnelable funnelable = this.new Funnelable(); //BRING OUT THE CURSED SYNTAX
	
	@Override
	public void tick() {
		binding = directBind(getBlockState().getValue(BlockStateProperties.FACING), (cursor, tile) -> tile instanceof RhodoCellTile);
	}
	
	public @Nullable BlockPos getBind() {
		return binding;
	}
	
	public @Nullable
	RhodoCellTile getBoundCell() {
		if(binding == null) return null;
		assert level != null;
		TileEntity tile = level.getBlockEntity(binding);
		return tile instanceof RhodoCellTile ? (RhodoCellTile) tile : null;
	}
	
	public void setRequest(SolidifiedRequest request) {
		boolean changed = !Objects.equals(this.request, request);
		this.request = request;
		if(changed) {
			setChanged();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}
	
	//Accept this SolidifiedRequest into myself, punting the previous contents to the next cell.
	public void push(SolidifiedRequest head) {
		push(head, new HashSet<>());
	}
	
	private void push(SolidifiedRequest head, Set<RhodoCellTile> visited) {
		SolidifiedRequest old = peek();
		setRequest(head);
		
		RhodoCellTile nextCell = visited.contains(this) ? null : getBoundCell();
		visited.add(this);
		if(nextCell != null) nextCell.push(old, visited);
	}
	
	//Sets my contents to the next cell's contents (or EMPTY if there isn't a next cell), returning my previous contents.
	//Um, it's like the opposite of push().
	public SolidifiedRequest pull() {
		return pull(new HashSet<>());
	}
	
	//each step of the process returns the value that should be put in the previous cell
	private SolidifiedRequest pull(Set<RhodoCellTile> visited) {
		if(visited.contains(this)) return peek();
		visited.add(this);
		
		RhodoCellTile nextCell = getBoundCell();
		SolidifiedRequest next = nextCell == null ? SolidifiedRequest.EMPTY : nextCell.pull(visited);
		SolidifiedRequest old = peek();
		setRequest(next);
		return old;
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
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction dir) {
		if(cap == RhodoFunnelableCapability.INSTANCE) return LazyOptional.of(() -> funnelable).cast();
		else return super.getCapability(cap, dir);
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return binding == null ? new AxisAlignedBB(levelPosition) : new AxisAlignedBB(levelPosition, binding);
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
