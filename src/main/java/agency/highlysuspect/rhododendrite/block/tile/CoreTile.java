package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.rhododendrite.computer.Fragment;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.util.Constants;

import java.util.HashSet;
import java.util.Set;

public class CoreTile extends FragmentContainerTile {
	public CoreTile() {
		super(RhoTileTypes.CORE);
	}
	
	//TODO: This is kind of a manky listener system, just keeping a list of blockpos
	private final Set<BlockPos> listeners = new HashSet<>();
	
	@Override
	public void setFragment(Fragment<?> newFragment) {
		Fragment<?> oldFragment = getFragment();
		super.setFragment(newFragment);
		
		if(world == null) return;
		
		Set<BlockPos> removals = new HashSet<>();
		for(BlockPos listener : listeners) {
			//If it's not loaded just skip that one.
			if(!world.getChunkProvider().isChunkLoaded(new ChunkPos(listener))) continue;
			
			TileEntity tile = world.getTileEntity(listener);
			if(tile instanceof CoreTile.ChangeListener) ((ChangeListener) tile).whenCoreChanged(oldFragment, newFragment, this);
			else removals.add(listener);
		}
		
		if(!removals.isEmpty()) {
			listeners.removeAll(removals);
			markDirty();
		}
	}
	
	public void registerListener(BlockPos who) {
		listeners.add(who);
		markDirty();
	}
	
	public void unregisterListener(BlockPos who) {
		listeners.remove(who);
		markDirty();
	}
	
	interface ChangeListener {
		void whenCoreChanged(Fragment<?> oldFragment, Fragment<?> newFragment, CoreTile core);
	}
	
	@Override
	public void writePacketNBT(CompoundNBT cmp) {
		super.writePacketNBT(cmp);
		
		ListNBT pee = new ListNBT();
		for(BlockPos pos : listeners) pee.add(NBTUtil.writeBlockPos(pos));
		cmp.put("Listeners", pee);
	}
	
	@Override
	public void readPacketNBT(CompoundNBT cmp) {
		super.readPacketNBT(cmp);
		
		listeners.clear();
		cmp.getList("Listeners", Constants.NBT.TAG_COMPOUND) //10
			.stream()
			.filter(inbt -> inbt instanceof CompoundNBT)
			.map(inbt -> (CompoundNBT) inbt)
			.map(NBTUtil::readBlockPos)
			.forEach(listeners::add);
	}
}
