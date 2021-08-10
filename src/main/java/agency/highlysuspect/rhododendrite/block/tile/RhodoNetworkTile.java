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
import net.minecraft.util.registry.Registry;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.wand.IWandBindable;
import vazkii.botania.common.block.tile.TileMod;
import vazkii.botania.common.core.handler.ModSounds;
import vazkii.botania.common.entity.EntitySpark;
import vazkii.botania.common.item.ItemTwigWand;
import vazkii.botania.common.network.PacketHandler;

import javax.annotation.Nullable;

public class RhodoNetworkTile extends TileMod implements IWandBindable {
	public RhodoNetworkTile(TileEntityType<?> type) {
		super(type);
	}
	
	public BlockPos uplink;
	
	public @Nullable CoreTile findCore() {
		if(world == null || uplink == null) return null;
		
		BlockPos cursor = uplink;
		while(cursor != null) {
			TileEntity tile = world.getTileEntity(cursor);
			if(tile instanceof CoreTile) return (CoreTile) tile;
			else if(tile instanceof RhodoNetworkTile) cursor = ((RhodoNetworkTile) tile).uplink;
			else return null;
		}
		
		throw new IllegalStateException("unreachable");
	}
	
	public void sparkle() {
		if(world == null || uplink == null) return;
		
		BlockPos start = pos;
		BlockPos end = uplink;
		while(true) {
			TileEntity tile = world.getTileEntity(end);
			if(tile instanceof CoreTile || tile instanceof RhodoNetworkTile)
				//TODO make a brighter sparkle-line packet. This is very small
				IncNetwork.sendToNearby(world, pos, new IncNetwork.SparkleLine(Vector3d.copyCentered(start), Vector3d.copyCentered(end), 30));
				//ItemTwigWand.doParticleBeamWithOffset(world, start, end);
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
		
		if(!CorePathTracing.withinWirelessRange(this.pos, pos)) return false;
		
		TileEntity tileAt = player.world.getTileEntity(pos);
		if(tileAt != this && (tileAt instanceof CoreTile || tileAt instanceof RhodoNetworkTile)){
			uplink = pos;
			
			player.playSound(ModSounds.ding, 0.1F, 1F);
			
			markDirty();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
			sparkle();
			
			return true;
		} else return false;
	}
	
	@Nullable
	@Override
	public BlockPos getBinding() {
		return uplink;
	}
}
