package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.tile.CoreTile;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class RhodoGraphData extends WorldSavedData {
	public RhodoGraphData() {
		super(NAME);
	}
	
	private static final String NAME = Rho.MODID + "-graph";
	
	public static RhodoGraphData getFor(ServerWorld world) {
		return world.getSavedData().getOrCreate(RhodoGraphData::new, NAME);
	}
	
	final Map<UUID, RhodoGraph> graphsByUuid = new HashMap<>();
	final Map<BlockPos, RhodoGraph> graphsByMemberPos = new HashMap<>();
	
	public RhodoGraph getOrCreateCore(CoreTile tile) {
		BlockPos pos = tile.getPos();
		RhodoGraph graph = graphsByMemberPos.get(pos);
		
		if(graph != null) {
			BlockPos corePos = graph.corePos();
			if(corePos.equals(pos)) {
				return graph;
			} else {
				//a core is here, but the graph says a non-core is here. get out of here with that.
				removeGraph(graph);
			}
		}
		
		RhodoGraph newGraph = RhodoGraph.empty(this, UUID.randomUUID(), pos);
		addGraph(newGraph);
		return newGraph;
	}
	
	public Optional<RhodoGraph> findGraphNear(BlockPos pos) {
		//TODO use Point of Interest system.
		return graphsByUuid.values().stream().filter(g -> g.isNear(pos)).findFirst();
	}
	
	private void addGraph(RhodoGraph graph) {
		graphsByUuid.put(graph.uuid, graph);
		for(BlockPos member : graph.members) graphsByMemberPos.put(member, graph);
	}
	
	private void removeGraph(RhodoGraph graph) {
		graphsByUuid.remove(graph.uuid);
		for(BlockPos member : graph.members) graphsByMemberPos.remove(member);
	}
	
	@Override
	public CompoundNBT write(CompoundNBT nbt) {
		ListNBT l = new ListNBT();
		graphsByUuid.values().forEach(graph -> l.add(graph.toNbt()));
		nbt.put("Graphs", l);
		return nbt;
	}
	
	@Override
	public void read(CompoundNBT nbt) {
		graphsByUuid.clear();
		graphsByMemberPos.clear();
		
		ListNBT l = nbt.getList("Graphs", Constants.NBT.TAG_COMPOUND); //10
		l.forEach(n -> {
			if(n instanceof CompoundNBT) addGraph(RhodoGraph.fromNbt(this, (CompoundNBT) n));
		});
	}
}
