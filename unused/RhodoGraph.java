package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.rhododendrite.block.CoreBlock;
import agency.highlysuspect.rhododendrite.block.tile.CoreTile;
import agency.highlysuspect.rhododendrite.block.tile.RhodoNetworkTile;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;

import java.util.*;

public class RhodoGraph {
	public RhodoGraph(RhodoGraphData owner, UUID uuid, List<BlockPos> members) {
		this.owner = owner;
		this.uuid = uuid;
		this.members = members;
	}
	
	public static RhodoGraph empty(RhodoGraphData owner, UUID uuid, BlockPos core) {
		List<BlockPos> members = new ArrayList<>();
		members.add(core);
		return new RhodoGraph(owner, uuid, members);
	}
	
	public static final int WIRELESS_DISTANCE = 8;
	public static final int WIRELESS_DISTANCE_SQUARED = WIRELESS_DISTANCE * WIRELESS_DISTANCE;
	
	public final RhodoGraphData owner;
	public final UUID uuid;
	//The first element is the "core", other elements are other members.
	public List<BlockPos> members;
	
	public boolean containsMember(BlockPos pos) {
		//TODO: linear scan
		return members.contains(pos);
	}
	
	public BlockPos corePos() {
		return members.get(0);
	}
	
	public static boolean withinRange(BlockPos a, BlockPos b) {
		return a.distanceSq(b) <= WIRELESS_DISTANCE_SQUARED;
	}
	
	public boolean isNear(BlockPos pos) {
		return members.stream().anyMatch(p -> withinRange(pos, p));
	}
	
	public void add(BlockPos pos) {
		members.add(pos);
		owner.graphsByMemberPos.put(pos, this);
	}
	
	//Returns 'true' if the whole graph is invalid.
	public boolean upkeep(ServerWorld world) {
		Set<BlockPos> toRemove = new HashSet<>();
		
		for(int i = 0; i < members.size(); i++) {
			BlockPos pos = members.get(i);
			
			if(i == 0) { //Core
				if(world.getChunkProvider().isChunkLoaded(new ChunkPos(pos)) && !(world.getTileEntity(pos) instanceof CoreTile)) {
					//The whole graph can be thrown out.
					return true;
				}
			} else { //Not-core
				if(world.getChunkProvider().isChunkLoaded(new ChunkPos(pos)) && !(world.getTileEntity(pos) instanceof RhodoNetworkTile)) {
					//Remove this block from the graph.
					toRemove.add(pos);
				}
			}
		}
		
		removeAll(toRemove);
		return false;
	}
	
	public void remove(BlockPos pos) {
		if(members.remove(pos))	{
			owner.graphsByMemberPos.remove(pos);
			recheckConnectivity();
		}
	}
	
	public void removeAll(Collection<BlockPos> removals) {
		if(members.removeAll(removals))	{
			for(BlockPos p : removals) owner.graphsByMemberPos.remove(p);
			recheckConnectivity();
		}
	}
	
	private void recheckConnectivity() {
		BlockPos core = members.get(0);
		Set<BlockPos> reachable = new HashSet<>();
		reachable.add(core);
		floodFill(core, reachable);
		
		members = new ArrayList<>(reachable);
	}
	
	private void floodFill(BlockPos checkFrom, Set<BlockPos> reachable) {
		//Find all nodes that are:
		// - immediately reachable from this one,
		// - not already confirmed reachable.
		Set<BlockPos> newlyReachable = new HashSet<>();
		for(BlockPos member : members) {
			if(!reachable.contains(member) && withinRange(checkFrom, member)) newlyReachable.add(member);
		}
		reachable.addAll(newlyReachable);
		
		//Recurse into each of the newly reachable nodes, do the same.
		newlyReachable.forEach(pos -> floodFill(pos, reachable));
	}
	
	public CompoundNBT toNbt() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putUniqueId("Uuid", uuid);
		
		ListNBT l = new ListNBT();
		for(BlockPos member : members) l.add(NBTUtil.writeBlockPos(member));
		nbt.put("Members", l);
		
		return nbt;
	}
	
	public static RhodoGraph fromNbt(RhodoGraphData owner, CompoundNBT nbt) {
		UUID uuid = nbt.getUniqueId("Uuid");
		
		ListNBT l = nbt.getList("Members", Constants.NBT.TAG_COMPOUND); //10
		List<BlockPos> members = new ArrayList<>();
		l.forEach(n -> {
			if(n instanceof CompoundNBT) members.add(NBTUtil.readBlockPos((CompoundNBT) n));
		});
		
		return new RhodoGraph(owner, uuid, members);
	}
}
