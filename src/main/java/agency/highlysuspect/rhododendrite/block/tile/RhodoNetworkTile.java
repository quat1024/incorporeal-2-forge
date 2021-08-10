package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.rhododendrite.computer.CorePathTracing;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import vazkii.botania.common.block.tile.TileMod;

import javax.annotation.Nullable;

public class RhodoNetworkTile extends TileMod {
	public RhodoNetworkTile(TileEntityType<?> type) {
		super(type);
	}
	
	public static class Connection {
		public Connection(BlockPos corePos, BlockPos uplink) {
			this.corePos = corePos;
			this.uplink = uplink;
		}
		
		//The location of my network's core.
		public BlockPos corePos;
		//The reason I'm able to connect to that core, even if the core itself is out of range.
		public BlockPos uplink;
	}
	
	private @Nullable Connection connection;
	private long lastUpkeep = 0; //simple check to avoid two tiles validateConnection-ing each other in a loop
	
	public @Nullable CoreTile findCore() {
		if(world == null) return null;
		
		validateConnection();
		if(connection == null) return null;
		
		else return RhoTileTypes.CORE.getIfExists(world, connection.corePos);
	}
	
	private void validateConnection() {
		if(world == null) return;
		if(!upkeepConnection()) rescan();
	}
	
	private boolean upkeepConnection() {
		if(world == null || connection == null) return false;
		
		//This is a bad hack and i should feel bad. Mom we have graph theory at home
		if(lastUpkeep == world.getGameTime()) return connection != null;
		lastUpkeep = world.getGameTime();
		
		//If my uplink is in an unloaded chunk: just assume the connection is valid.
		if(!world.getChunkProvider().isChunkLoaded(new ChunkPos(connection.uplink))) return true;
		TileEntity tile = world.getTileEntity(connection.uplink);
		
		//If my uplink is a core, the connection is valid.
		if(tile instanceof CoreTile) {
			connection.corePos = connection.uplink; //Just in case??? i should assert for this
			return true;
		}
		
		//If my uplink is another RhodoNetworkTile, my connection is only as valid as its is.
		if(tile instanceof RhodoNetworkTile && tile != this) {
			RhodoNetworkTile uplinkTile = (RhodoNetworkTile) tile;
			uplinkTile.validateConnection();
			
			if(uplinkTile.connection == null) {
				this.connection = null;
				return false;
			} else {
				connection.corePos = uplinkTile.connection.corePos;
				return true;
			}
		}
		
		//If I found something else, my connection is not valid.
		return false;
	}
	
	private void rescan() {
		connection = null;
		if(world == null) return;
		
		//Scan in a sphere around me
		BlockPos.Mutable pos = this.pos.toMutable();
		for(BlockPos offset : CorePathTracing.RELATIVE_SCAN_OFFSETS) {
			pos.func_243531_h(offset); //"add" with a fucked mcp name
			
			TileEntity tile = world.getTileEntity(pos);
			
			//TODO yes this is obvious testing code
			Minecraft.getInstance().world.addParticle(ParticleTypes.END_ROD, pos.getX() + .5, pos.getY() + 5, pos.getZ() + .5, 0, 0, 0);
			
			//If I find a core directly, that's great, connect to it.
			if(tile instanceof CoreTile) {
				BlockPos yes = pos.toImmutable();
				connection = new Connection(yes, yes);
				Minecraft.getInstance().world.addParticle(ParticleTypes.LARGE_SMOKE, pos.getX() + .5, pos.getY() + 5, pos.getZ() + .5, 0, 0, 0);
				return;
			}
			
			//If I find something else with a valid core uplink, connect to it.
			if(tile instanceof RhodoNetworkTile && tile != this) {
				RhodoNetworkTile other = (RhodoNetworkTile) tile;
				other.validateConnection();
				if(other.connection != null) {
					Minecraft.getInstance().world.addParticle(ParticleTypes.ANGRY_VILLAGER, pos.getX() + .5, pos.getY() + 5, pos.getZ() + .5, 0, 0, 0);
					Minecraft.getInstance().world.addParticle(ParticleTypes.NOTE, other.connection.corePos.getX() + .5, other.connection.corePos.getY() + 5, other.connection.corePos.getZ() + .5, 0, 0, 0);
					connection = new Connection(other.connection.corePos, pos.toImmutable());
					return;
				}
			}
		}
		
		//If I didn't find anything, the connection was set to `null` up top, which is correct
	}
	
	@Override
	public void writePacketNBT(CompoundNBT nbt) {
		super.writePacketNBT(nbt);
		
		if(connection != null) {
			nbt.put("CorePos", NBTUtil.writeBlockPos(connection.corePos));
			nbt.put("Uplink", NBTUtil.writeBlockPos(connection.uplink));
		}
	}
	
	@Override
	public void readPacketNBT(CompoundNBT nbt) {
		super.readPacketNBT(nbt);
		
		if(nbt.contains("CorePos")) {
			connection = new Connection(NBTUtil.readBlockPos(nbt.getCompound("CorePos")), NBTUtil.readBlockPos(nbt.getCompound("Uplink")));
		} else connection = null;
	}
}
