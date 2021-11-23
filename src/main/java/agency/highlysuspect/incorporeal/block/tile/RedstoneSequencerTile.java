package agency.highlysuspect.incorporeal.block.tile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.util.Constants;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.wand.IWandBindable;
import vazkii.botania.common.block.tile.TileMod;

import javax.annotation.Nullable;
import java.util.*;

public class RedstoneSequencerTile extends TileMod implements IWandBindable, ITickableTileEntity {
	public RedstoneSequencerTile() {
		super(IncTileTypes.REDSTONE_SEQUENCER);
	}
	
	private @Nullable BlockPos binding = null;
	
	private int myPowerLast;
	
	private final Set<Action> messages = new HashSet<>();
	private final HashMap<BlockPos, Integer> remotePowerContributions = new HashMap<>();
	
	@Override
	public void tick() {
		if(world == null || world.isRemote) return;
		boolean dirty = false;
		
		//Compute the redstone power at my location and ping my binding if it changed
		int myPower = world.getRedstonePowerFromNeighbors(pos);
		if(myPower != myPowerLast) {
			dirty = true;
			if(getNextSequencer() != null) getNextSequencer().pingFrom(pos, myPower);
		}
		myPowerLast = myPower;
		
		//Locate all sources that don't exist anymore and remove contributions from them, in two ticks.
		for(BlockPos source : remotePowerContributions.keySet()) {
			//...unless it's merely unloaded. look the other way for now
			boolean loaded = world.getChunkProvider().isChunkLoaded(new ChunkPos(source));
			if(!loaded) continue;
			
			boolean wrong;
			TileEntity there = world.getTileEntity(source);
			if(there instanceof RedstoneSequencerTile) {
				wrong = !Objects.equals(pos, ((RedstoneSequencerTile) there).binding);
			} else {
				wrong = true;
			}
			
			if(wrong) {
				messages.add(new Action.RemoveContributionsFrom(source, world.getGameTime(), delayFor(source)));
				dirty = true;
			}
		}
		
		//Process incoming messages
		for(Action a : messages) {
			if(a.sentAt + a.delay <= world.getGameTime()) {
				a.act(this);
				a.toRemove = true;
			}
		}
		messages.removeIf(a -> a.toRemove);
		
		//Compute the maximum signal from all sources.
		int signal = 0;
		for(Integer s : remotePowerContributions.values()) {
			if(s > signal) signal = s;
		}
		
		//Emit the signal.
		int currentSignal = getBlockState().get(BlockStateProperties.POWER_0_15);
		if(signal != currentSignal) {
			world.setBlockState(pos, getBlockState().with(BlockStateProperties.POWER_0_15, signal));
			
			if(getNextSequencer() != null) {
				getNextSequencer().pingFrom(pos, signal);
			}
			
			dirty = true;
		}
		
		if(dirty) {
			markDirty();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}
	
	public int delayFor(BlockPos other) {
		long dx = this.pos.getX() - other.getX();
		long dy = this.pos.getY() - other.getY();
		long dz = this.pos.getZ() - other.getZ();
		long distanceSquared = dx * dx + dy * dy + dz * dz;
		return MathHelper.floor(Math.sqrt(distanceSquared)) * 2;
	}
	
	public void pingFrom(BlockPos from, int signal) {
		if(world == null) return;
		messages.add(new Action.UpdateSignalTo(from, world.getGameTime(), delayFor(from), signal));
	}
	
	//IWandBindable
	@Override
	public boolean canSelect(PlayerEntity player, ItemStack wand, BlockPos pos, Direction side) {
		return true;
	}
	
	@Override
	public boolean bindTo(PlayerEntity player, ItemStack wand, BlockPos pos, Direction side) {
		//Inlined MathHelper#distSq from botania-fabric
		//Longs to avoid overflow while squaring
		long dx = this.pos.getX() - pos.getX();
		long dy = this.pos.getY() - pos.getY();
		long dz = this.pos.getZ() - pos.getZ();
		if(dx * dx + dy * dy + dz * dz <= 10 * 10) {
			setBinding(pos);
			return true;
		} else return false;
	}
	
	//ITileBound
	@Nullable
	@Override
	public BlockPos getBinding() {
		return binding;
	}
	
	//convenience
	public @Nullable RedstoneSequencerTile getNextSequencer() {
		if(world == null || binding == null) return null;
		TileEntity tileAtBinding = world.getTileEntity(binding);
		return tileAtBinding instanceof RedstoneSequencerTile ? (RedstoneSequencerTile) tileAtBinding : null;
	}
	
	public void setBinding(@Nullable BlockPos newBinding) {
		boolean changed = !Objects.equals(binding, newBinding);
		binding = newBinding;
		if(changed) {
			markDirty();
			VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
		}
	}
	
	@Override
	public void writePacketNBT(CompoundNBT cmp) {
		super.writePacketNBT(cmp);
		
		cmp.putInt("my", myPowerLast);
		
		if(binding != null) {
			cmp.put("Binding", NBTUtil.writeBlockPos(binding));
		}
		
		//I HATE WRITING THIS CODE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		
		ListNBT pissOff = new ListNBT();
		for(Action a : messages) {
			pissOff.add(a.toTag(new CompoundNBT()));
		}
		cmp.put("messages", pissOff);
		
		ListNBT gotDamn = new ListNBT();
		remotePowerContributions.forEach((pos, signal) -> {
			CompoundNBT entry = new CompoundNBT();
			entry.put("who", NBTUtil.writeBlockPos(pos));
			entry.putInt("signal", signal);
			gotDamn.add(entry);
		});
		cmp.put("contributions", gotDamn);
	}
	
	@Override
	public void readPacketNBT(CompoundNBT cmp) {
		super.readPacketNBT(cmp);
		
		myPowerLast = cmp.getInt("my");
		
		if(cmp.contains("Binding", Constants.NBT.TAG_COMPOUND)) { //10
			binding = NBTUtil.readBlockPos(cmp.getCompound("Binding"));
		} else binding = null;
		
		messages.clear();
		for(INBT a : cmp.getList("messages", Constants.NBT.TAG_COMPOUND)) { //10
			if(a instanceof CompoundNBT) {
				messages.add(Action.fromNbt((CompoundNBT) a));
			}
		}
		
		remotePowerContributions.clear();
		for(INBT asdklasjkldsad : cmp.getList("contributions", Constants.NBT.TAG_COMPOUND)) { //10
			if(asdklasjkldsad instanceof CompoundNBT) {
				CompoundNBT urmom = (CompoundNBT) asdklasjkldsad;
				remotePowerContributions.put(NBTUtil.readBlockPos(urmom.getCompound("who")), urmom.getInt("signal"));
			}
		}
	}
	
	//TODO 1.17: records
	private static abstract class Action {
		public Action(BlockPos who, long sentAt, int delay) {
			this.who = who;
			this.sentAt = sentAt;
			this.delay = delay;
		}
		
		BlockPos who;
		long sentAt;
		int delay;
		transient boolean toRemove = false;
		
		abstract void act(RedstoneSequencerTile tile);
		@Override public abstract boolean equals(Object obj);
		
		public CompoundNBT toTag(CompoundNBT cmp) {
			cmp.put("who", NBTUtil.writeBlockPos(who));
			cmp.putLong("sentAt", sentAt);
			cmp.putInt("delay", delay);
			return cmp;
		}
		
		private static class RemoveContributionsFrom extends Action {
			public RemoveContributionsFrom(BlockPos who, long sentAt, int delay) {
				super(who, sentAt, delay);
			}
			
			@Override
			void act(RedstoneSequencerTile tile) {
				tile.remotePowerContributions.remove(who);
				tile.messages.forEach(act -> {
					if(act.who.equals(who)) act.toRemove = true;
				});
			}
			
			@Override
			public CompoundNBT toTag(CompoundNBT cmp) {
				cmp.putInt("type", 0);
				return super.toTag(cmp);
			}
			
			@Override
			public boolean equals(Object obj) {
				if(obj == null || obj.getClass() != RemoveContributionsFrom.class) return false;
				RemoveContributionsFrom other = (RemoveContributionsFrom) obj;
				
				return Objects.equals(who, other.who) && sentAt == other.sentAt;
			}
		}
		
		private static class UpdateSignalTo extends Action {
			public UpdateSignalTo(BlockPos who, long sentAt, int delay, int signal) {
				super(who, sentAt, delay);
				this.signal = signal;
			}
			
			int signal;
			
			@Override
			void act(RedstoneSequencerTile tile) {
				tile.remotePowerContributions.put(who, signal);
			}
			
			@Override
			public CompoundNBT toTag(CompoundNBT cmp) {
				cmp.putInt("type", 1);
				cmp.putInt("signal", signal);
				return super.toTag(cmp);
			}
			
			@Override
			public boolean equals(Object obj) {
				if(obj == null || obj.getClass() != UpdateSignalTo.class) return false;
				UpdateSignalTo other = (UpdateSignalTo) obj;
				
				return Objects.equals(who, other.who) && sentAt == other.sentAt && signal == other.signal;
			}
		}
		
		public static Action fromNbt(CompoundNBT nbt) {
			BlockPos who = NBTUtil.readBlockPos(nbt.getCompound("who"));
			long sentAt = nbt.getLong("sentAt");
			int delay = nbt.getInt("delay");
			switch(nbt.getInt("type")) {
				case 0: default: return new Action.RemoveContributionsFrom(who, sentAt, delay);
				case 1: return new Action.UpdateSignalTo(who, sentAt, delay, nbt.getInt("signal"));
			}
		}
	}
}
