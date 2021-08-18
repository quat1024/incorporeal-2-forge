package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.incorporeal.IncNetwork;
import agency.highlysuspect.rhododendrite.computer.CorePathTracing;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.wand.IWandBindable;
import vazkii.botania.common.block.tile.TileMod;
import vazkii.botania.common.core.handler.ModSounds;

import javax.annotation.Nullable;
import java.util.HashSet;

public class RhodoNetworkTile extends TileMod implements IWandBindable {
	public RhodoNetworkTile(TileEntityType<?> type) {
		super(type);
	}
	
	public BlockPos uplink;
	
	public boolean trySetUplink(TileEntity bindTo) {
		if(canBindTo(bindTo)) {
			this.uplink = bindTo.getPos();
			markDirty();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
			whenWanded();
			return true;
		} else return false;
	}
	
	@SuppressWarnings("ConditionCoveredByFurtherCondition") //makin it clear! check for null!
	protected boolean canBindTo(TileEntity tile) {
		return tile != null &&
			tile != this &&
			(tile instanceof CoreTile || tile instanceof RhodoNetworkTile) &&
			CorePathTracing.withinWirelessRange(this.pos, tile.getPos());
	}
	
	public void whenPlaced() {
		if(world != null && uplink == null) {
			//Try binding to cores first.
			if(CorePathTracing.iterateWirelessRange(pos, p -> {
				TileEntity tile = world.getTileEntity(p);
				return tile instanceof CoreTile && trySetUplink(tile);
			})) return;
			
			//Otherwise try binding to tiles that are already bound to a core.
			if(CorePathTracing.iterateWirelessRange(pos, p -> {
				TileEntity tile = world.getTileEntity(p);
				return tile instanceof RhodoNetworkTile && ((RhodoNetworkTile) tile).findCore() != null && trySetUplink(tile);
			})) return;
			
			//Otherwise just bind to whatever.
			CorePathTracing.iterateWirelessRange(pos, p -> {
				TileEntity tile = world.getTileEntity(p);
				return trySetUplink(tile);
			});
		}
	}
	
	public @Nullable CoreTile findCore() {
		if(world == null || uplink == null) return null;
		
		HashSet<BlockPos> cycleCheck = new HashSet<>();
		BlockPos cursor = uplink;
		
		while(true) {
			if(cursor == null || cycleCheck.contains(cursor)) return null;
			cycleCheck.add(cursor);
			
			TileEntity tile = world.getTileEntity(cursor);
			if(tile instanceof CoreTile) return (CoreTile) tile;
			else if(tile instanceof RhodoNetworkTile) cursor = ((RhodoNetworkTile) tile).uplink;
			else return null;
		}
	}
	
	public void whenWanded() {
		if(world == null || uplink == null) return;
		
		HashSet<BlockPos> cycleCheck = new HashSet<>();
		
		BlockPos start = pos;
		BlockPos end = uplink;
		while(true) {
			if(cycleCheck.contains(start)) return;
			cycleCheck.add(start);
			
			TileEntity tile = world.getTileEntity(end);
			if(tile instanceof CoreTile || tile instanceof RhodoNetworkTile)
				IncNetwork.sendToNearby(world, pos, new IncNetwork.SparkleLine(Vector3d.copyCentered(start), Vector3d.copyCentered(end), 30, 2f));
			
			if(tile instanceof RhodoNetworkTile) {
				start = end;
				end = ((RhodoNetworkTile) tile).uplink;
				if(end == null) break;
			} else break;
		}
	}
	
	@Override
	public void writePacketNBT(CompoundNBT nbt) {
		super.writePacketNBT(nbt);
		
		if(uplink != null) {
			nbt.put("Uplink", NBTUtil.writeBlockPos(uplink));
		}
	}
	
	@Override
	public void readPacketNBT(CompoundNBT nbt) {
		super.readPacketNBT(nbt);
		
		if(nbt.contains("Uplink")) {
			uplink = NBTUtil.readBlockPos(nbt.getCompound("Uplink"));
		} else uplink = null;
	}
	
	@Override
	public boolean canSelect(PlayerEntity player, ItemStack wand, BlockPos pos, Direction side) {
		return true;
	}
	
	@Override
	public boolean bindTo(PlayerEntity player, ItemStack wand, BlockPos pos, Direction side) {
		if(player == null || this.pos.equals(pos)) return false;
		
		if(trySetUplink(player.world.getTileEntity(pos))){
			player.playSound(ModSounds.ding, 0.1F, 1F);
			return true;
		} else return false;
	}
	
	@Nullable
	@Override
	public BlockPos getBinding() {
		return uplink;
	}
}
