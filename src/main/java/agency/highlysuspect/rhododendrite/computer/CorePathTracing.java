package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.AwakenedLogBlock;
import agency.highlysuspect.rhododendrite.block.CoreBlock;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class CorePathTracing {
	///////////////////////// logs ///////////////////////// 
	
	public static final int MAX_RANGE = 16;
	
	public static Optional<Result> scanForCore(IWorldReader world, BlockPos pos, Direction.Axis axis) {
		Optional<Result> first = scanForCore(world, pos, Rho.positive(axis));
		//so there's no flatOrElse, huh
		return first.isPresent() ? first : scanForCore(world, pos, Rho.negative(axis));
	}
	
	public static Optional<Result> scanForCore(IWorldReader world, BlockPos pos, Direction scanDir) {
		//skip the log itself by alreading moving 1 time before the loop
		BlockPos.Mutable cursor = pos.toMutable().move(scanDir);
		BlockState stateThere = world.getBlockState(cursor);
		
		//If we're right next to a core? Well hot damn that's great
		if(stateThere.getBlock() instanceof CoreBlock) return Optional.of(new Result(scanDir, 1));
		
		//If we're one block away from an already awakened log, note that too
		if(stateThere.getBlock() instanceof AwakenedLogBlock) {
			Direction theirFacing = stateThere.get(AwakenedLogBlock.FACING);
			if(theirFacing != scanDir) return Optional.empty();
			
			int nextDistance = stateThere.get(AwakenedLogBlock.DISTANCE) + 1;
			if(nextDistance > MAX_RANGE) return Optional.empty();
			
			return Optional.of(new Result(scanDir, nextDistance));
		}
		
		//Otherwise we're not next to a core at all
		return Optional.empty();
	}
	
	public static StackOps readStackOps(World world, BlockPos corePos, BlockState coreState) {
		if(!(coreState.getBlock() instanceof CoreBlock)) return new StackOps();
		
		Direction coreFacing = coreState.get(CoreBlock.FACING);
		
		List<SolidifiedRequest> requests = new ArrayList<>();
		List<SolidifiedRequest.Holder> holders = new ArrayList<>();
		
		BlockPos.Mutable cursor = corePos.toMutable();
		for(int i = 0; i <= MAX_RANGE; i++, cursor.move(coreFacing)) { // <=: the actual max size of a conga line is MAX_RANGE + 1, counting the Core itself
			TileEntity tile = world.getTileEntity(cursor);
			if(tile == null) break;
			
			Optional<SolidifiedRequest.Holder> holder = tile.getCapability(SolidifiedRequest.Cap.INSTANCE).resolve();
			if(holder.isPresent()) {
				requests.add(holder.get().getRequest());
				holders.add(holder.get());
			} else break;
		}
		
		return new StackOps(requests, holders);
	}
	
	public static boolean stillValid(IWorldReader world, BlockPos pos, Direction coreDir, int distance) {
		//Well if there's no core there, it's definitely not still valid
		if(!(world.getBlockState(pos.offset(coreDir, distance)).getBlock() instanceof CoreBlock)) return false;
		
		//Otherwise do the more expensive check
		Optional<Result> opR = scanForCore(world, pos, coreDir);
		return opR.isPresent() && opR.get().facing == coreDir && opR.get().distance == distance;
	}
	
	public static class Result {
		public Result(Direction facing, int distance) {
			this.facing = facing;
			this.distance = distance;
		}
		
		public final Direction facing;
		public final int distance;
		
		public BlockState toAwakenedLogState(AwakenedLogBlock log) {
			return log.getDefaultState().with(AwakenedLogBlock.FACING, facing).with(AwakenedLogBlock.DISTANCE, distance);
		}
	}
	
	///////////////////////// wireless network /////////////////////////
	
	public static final int WIRELESS_RANGE = 10; //just like flowers
	
	public static int blockPosDistSq(BlockPos a, BlockPos b) {
		//BlockPos#distanceSq seems to offset *one* of the positions by .5, so it has a directional bias.
		int x = a.getX() - b.getX();
		int y = a.getY() - b.getY();
		int z = a.getZ() - b.getZ();
		return x * x + y * y + z * z;
	}
	
	public static boolean withinWirelessRange(BlockPos a, BlockPos b) {
		//botania uses a cubical region for corporea sparks and a spherical region for flowers
		//since these bind with wand of the forest now, i think a spherical region is more appropriate
		return (blockPosDistSq(a, b) - 0.0001f) <= WIRELESS_RANGE * WIRELESS_RANGE; //subtract a bit to fudge ties in your favor
		
		//int x = Math.abs(a.getX() - b.getX());
		//int y = Math.abs(a.getY() - b.getY());
		//int z = Math.abs(a.getZ() - b.getZ());
		//return Math.max(Math.max(x, y), z) <= WIRELESS_RANGE;
	}
	
	/**
	 * Like this:
	 * 
	 * BlockPos.Mutable mut = pos.toMutable();
	 *   for(BlockPos a : CorePathTracing.RELATIVE_SCAN_OFFSETS) {
	 *     mut.func_243531_h(a); //it's "move" with a fucked up MCP name
	 *     //(do something with the block pos)
	 *   }
	 * }
	 * 
	 * This process moves the mutable BlockPos inwards-to-outwards in a sphere.
	 * The origin is not iterated over (i.e. the first vector is not 0, 0, 0)
	 */
	public static final List<BlockPos> RELATIVE_SCAN_OFFSETS = new ArrayList<>();
	
	static {
		List<BlockPos> ABSOLUTE_SCAN_OFFSETS = new ArrayList<>();
			
		for(int dx = -WIRELESS_RANGE; dx <= WIRELESS_RANGE; dx++) {
			for(int dy = -WIRELESS_RANGE; dy <= WIRELESS_RANGE; dy++) {
				for(int dz = -WIRELESS_RANGE; dz <= WIRELESS_RANGE; dz++) {
					BlockPos pos = new BlockPos(dx, dy, dz);
					if(pos.equals(BlockPos.ZERO)) continue;
					if(withinWirelessRange(BlockPos.ZERO, pos)) ABSOLUTE_SCAN_OFFSETS.add(pos);
				}
			}
		}
		
		ABSOLUTE_SCAN_OFFSETS.sort(Comparator.comparingDouble(pos -> blockPosDistSq(pos, BlockPos.ZERO)));
		
		Map<BlockPos, BlockPos> objectCache = new HashMap<>();
		
		BlockPos prev = null;
		for(BlockPos next : ABSOLUTE_SCAN_OFFSETS) {
			BlockPos add;
			if(prev == null) {
				add = next;
			} else {
				add = next.subtract(prev);
			}
			add = objectCache.computeIfAbsent(add, Function.identity());
			RELATIVE_SCAN_OFFSETS.add(add);
			prev = next;
		}
	}
	
	public static boolean iterateWirelessRange(BlockPos start, Predicate<? super BlockPos.Mutable> shouldBreak) {
		BlockPos.Mutable pos = start.toMutable();
		for(BlockPos d : RELATIVE_SCAN_OFFSETS) {
			pos.func_243531_h(d); //"move" that takes a blockpos instead of x,y,z
			
			if(shouldBreak.test(pos)) return true;
		}
		return false;
	}
}
