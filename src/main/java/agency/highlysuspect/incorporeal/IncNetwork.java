package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.client.ClientPacketHandler;
import com.google.common.base.Preconditions;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

public class IncNetwork {
	private IncNetwork() {}
	
	private static final String PROTOCOL = "0";
	public static final SimpleChannel HANDLER = NetworkRegistry.newSimpleChannel(Init.id("channel"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
	
	public static void setup() {
		HANDLER.registerMessage(0, SparkleLine.class, SparkleLine::encode, SparkleLine::decode, ClientPacketHandler::handleSparkleLine, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		HANDLER.registerMessage(1, FunnyFlower.class, FunnyFlower::encode, FunnyFlower::decode, ClientPacketHandler::handleFunnyFlower, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}
	
	//Copypaste from Botania
	public static void sendToNearby(World world, BlockPos pos, Object toSend) {
		if (world instanceof ServerWorld) {
			ServerWorld ws = (ServerWorld) world;
			
			ws.getChunkProvider().chunkManager.getTrackingPlayers(new ChunkPos(pos), false)
				.filter(p -> p.getDistanceSq(pos.getX(), pos.getY(), pos.getZ()) < 64 * 64)
				.forEach(p -> HANDLER.send(PacketDistributor.PLAYER.with(() -> p), toSend));
		}
	}
	
	public static void writeVec3d(PacketBuffer buf, Vector3d vec) {
		buf.writeDouble(vec.x);
		buf.writeDouble(vec.y);
		buf.writeDouble(vec.z);
	}
	
	public static Vector3d readVec3d(PacketBuffer buf) {
		return new Vector3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}
	
	public static class SparkleLine {
		public SparkleLine(Vector3d start, Vector3d end, int decay) {
			this.start = start;
			this.end = end;
			this.decay = decay;
		}
		
		public final Vector3d start;
		public final Vector3d end;
		public final int decay;
		
		public void encode(PacketBuffer buf) {
			writeVec3d(buf, start);
			writeVec3d(buf, end);
			buf.writeInt(decay);
		}
		
		public static SparkleLine decode(PacketBuffer buf) {
			return new SparkleLine(readVec3d(buf), readVec3d(buf), buf.readInt());
		}
	}
	
	public static class FunnyFlower {
		public FunnyFlower(Vector3d start, Vector3d end, int decay, int[] notes) {
			this(new SparkleLine(start, end, decay), notes);
		}
		
		public FunnyFlower(SparkleLine sparkleLine, int[] notes) {
			Preconditions.checkNotNull(notes);
			Preconditions.checkArgument(notes.length <= 2);
			
			this.sparkleLine = sparkleLine;
			this.notes = notes;
		}
		
		public final SparkleLine sparkleLine;
		public final int[] notes;
		
		public void encode(PacketBuffer buf) {
			sparkleLine.encode(buf);
			buf.writeVarIntArray(notes);
		}
		
		public static FunnyFlower decode(PacketBuffer buf) {
			return new FunnyFlower(SparkleLine.decode(buf), buf.readVarIntArray(2));
		}
	}
}
