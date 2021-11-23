package agency.highlysuspect.incorporeal;

import agency.highlysuspect.incorporeal.client.ClientPacketHandler;
import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Pair;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IncNetwork {
	private IncNetwork() {}
	
	private static final String PROTOCOL = "0";
	public static final SimpleChannel HANDLER = NetworkRegistry.newSimpleChannel(Inc.id("channel"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);
	
	public static void setup() {
		HANDLER.registerMessage(0, SparkleLine.class, SparkleLine::encode, SparkleLine::decode, ClientPacketHandler::handleSparkleLine, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		HANDLER.registerMessage(1, FunnyFlower.class, FunnyFlower::encode, FunnyFlower::decode, ClientPacketHandler::handleFunnyFlower, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
	}
	
	//Copypaste from Botania
	public static void sendToNearby(Level world, BlockPos pos, Object toSend) {
		if (world instanceof ServerLevel) {
			ServerLevel ws = (ServerLevel) world;
			
			ws.getChunkSource().chunkMap.getPlayers(new ChunkPos(pos), false)
				.filter(p -> p.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 64 * 64)
				.forEach(p -> HANDLER.send(PacketDistributor.PLAYER.with(() -> p), toSend));
		}
	}
	
	public static void writeVec3d(FriendlyByteBuf buf, Vec3 vec) {
		buf.writeDouble(vec.x);
		buf.writeDouble(vec.y);
		buf.writeDouble(vec.z);
	}
	
	public static Vec3 readVec3d(FriendlyByteBuf buf) {
		return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
	}
	
	public static class SparkleLine {
		public SparkleLine(Vec3 start, Vec3 end, int decay, float size) {
			this.start = start;
			this.end = end;
			this.decay = decay;
			this.size = size;
		}
		
		public final Vec3 start;
		public final Vec3 end;
		public final int decay;
		public final float size;
		
		public void encode(FriendlyByteBuf buf) {
			writeVec3d(buf, start);
			writeVec3d(buf, end);
			buf.writeInt(decay);
			buf.writeFloat(size);
		}
		
		public static SparkleLine decode(FriendlyByteBuf buf) {
			return new SparkleLine(readVec3d(buf), readVec3d(buf), buf.readInt(), buf.readFloat());
		}
	}
	
	public static class FunnyFlower {
		public FunnyFlower(List<Pair<SparkleLine, byte[]>> data) {
			this.data = data;
		}
		
		public final List<Pair<SparkleLine, byte[]>> data;
		
		public void encode(FriendlyByteBuf buf) {
			buf.writeByte(data.size());
			for(Pair<SparkleLine, byte[]> item : data) {
				item.getFirst().encode(buf);
				buf.writeByteArray(item.getSecond());
			}
		}
		
		public static FunnyFlower decode(FriendlyByteBuf buf) {
			int count = buf.readByte();
			List<Pair<SparkleLine, byte[]>> list = new ArrayList<>(count);
			for(int i = 0; i < count; i++) {
				list.add(Pair.of(SparkleLine.decode(buf), buf.readByteArray(2)));
			}
			return new FunnyFlower(list);
		}
	}
}
