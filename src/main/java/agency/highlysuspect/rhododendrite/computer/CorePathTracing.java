package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.AwakenedLogBlock;
import agency.highlysuspect.rhododendrite.block.CoreBlock;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.Optional;

public class CorePathTracing {
	public static final int MAX_RANGE = 16;
	
	public static Optional<Result> scanForCore(IWorldReader world, BlockPos pos, Direction.Axis axis) {
		Optional<Result> first = scanForCore(world, pos, Rho.positive(axis));
		//so there's no flatOrElse, huh
		return first.isPresent() ? first : scanForCore(world, pos, Rho.negative(axis));
	}
	
	public static Optional<Result> scanForCore(IWorldReader world, BlockPos pos, Direction scanDir) {
		//TODO okay so the original plan was to have leaves have paths continue "through" them
		// turns out to be Really Hard!, and kind of weird performance-wise
		// since like, whenever a leaf block moves you have to update a bazillion blocks
		
		BlockState stateThere = world.getBlockState(pos.offset(scanDir));
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
	
//	
//	public static Optional<Result> scanForCore(World world, BlockPos pos, Direction scanDir) {
//		//search "out" from myself, in the direction of scanDir, for a core
//		BlockPos.Mutable scanPos = pos.toMutable();
//		for(int distance = 1; distance < MAX_RANGE; distance++) {
//			scanPos.move(scanDir);
//			BlockState stateThere = world.getBlockState(scanPos);
//			
//			//skip over blocks that are okay to have in the path of a rhododendrite stack
//			if(stateThere.getBlock() instanceof AwakenedLogBlock) continue;
//			
//			if(stateThere.getBlock() instanceof CoreBlock) return Optional.of(new Result(scanDir, distance));
//			else return Optional.empty(); //no other unapproved blocks allowed between this and the core.
//		}
//		
//		//managed to scan MAX_RANGE blocks towards the core and they were all valid blocks, but still no core.
//		//here, MAX_RANGE has its effect.
//		return Optional.empty();
//	}
	
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
}
