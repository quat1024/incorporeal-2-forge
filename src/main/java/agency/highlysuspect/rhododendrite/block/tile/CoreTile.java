package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.computer.RhodoFunnelable;
import agency.highlysuspect.rhododendrite.computer.StackOps;

import javax.annotation.Nonnull;
import java.util.Optional;

public class CoreTile extends RequestHolderTile implements RhodoFunnelable {
	public CoreTile() {
		super(RhoTileTypes.CORE);
	}
	
	@Override
	public boolean canRhodoExtract() {
		return true;
	}
	
	@Override
	public Optional<SolidifiedRequest> rhodoExtract(boolean simulate) {
		if(simulate) {
			return Optional.of(StackOps.read(this).peek());
		} else {
			StackOps ops = StackOps.read(this);
			SolidifiedRequest result = ops.pull();
			ops.commit();
			return Optional.of(result);
		}
	}
	
	@Override
	public boolean canRhodoInsert() {
		return true;
	}
	
	@Override
	public boolean tryRhodoInsert(@Nonnull SolidifiedRequest request, boolean simulate) {
		if(!simulate) {
			StackOps ops = StackOps.read(this);
			ops.push(request);
			ops.commit();
		}
		return true;
	}
	
	//TODO: Reimplement the Listener system. This is intended to allow Condition blocks to be nonticking. It had some problems (see #8)
	//private final Set<BlockPos> listeners = new HashSet<>();
	
//	@Override
//	public void setRequest(SolidifiedRequest newRequest) {
//		super.setRequest(newRequest);
//		
//		if(world == null) return;
//		
//		Set<BlockPos> removals = new HashSet<>();
//		for(BlockPos listener : listeners) {
//			//If it's not loaded just skip that one.
//			if(!world.getChunkProvider().isChunkLoaded(new ChunkPos(listener))) continue;
//			
//			TileEntity tile = world.getTileEntity(listener);
//			if(tile instanceof CoreTile.ChangeListener) ((ChangeListener) tile).whenCoreChanged(this);
//			else removals.add(listener);
//		}
//		
//		if(!removals.isEmpty()) {
//			listeners.removeAll(removals);
//			markDirty();
//		}
//	}
//	
//	public void registerListener(BlockPos who) {
//		listeners.add(who);
//		markDirty();
//	}
//	
//	public void unregisterListener(BlockPos who) {
//		listeners.remove(who);
//		markDirty();
//	}
	
//	@Override
//	public void writePacketNBT(CompoundNBT cmp) {
//		super.writePacketNBT(cmp);
//		
//		ListNBT pee = new ListNBT();
//		for(BlockPos pos : listeners) pee.add(NBTUtil.writeBlockPos(pos));
//		cmp.put("Listeners", pee);
//	}
//	
//	@Override
//	public void readPacketNBT(CompoundNBT cmp) {
//		super.readPacketNBT(cmp);
//		
//		listeners.clear();
//		cmp.getList("Listeners", Constants.NBT.TAG_COMPOUND) //10
//			.stream()
//			.filter(inbt -> inbt instanceof CompoundNBT)
//			.map(inbt -> (CompoundNBT) inbt)
//			.map(NBTUtil::readBlockPos)
//			.forEach(listeners::add);
//	}
}
