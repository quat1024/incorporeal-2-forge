package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

public class FrameReader {	
	public static final BiPredicate<ItemFrame, Direction> NON_EMPTY = (frame, dir) -> !frame.getItem().isEmpty();
	public static final BiPredicate<ItemFrame, Direction> RESTING_ON = (frame, dir) -> frame.getDirection() == dir;
	
	//near: The item frame is anywhere in the 6 blocks surrounding the provided blockpos. (e.g. "frame tinkerer")
	//resting on: The item frame is resting on the provided blockpos. (e.g. "corporea funnel")
	
	public static List<ItemStack> itemsNear(Level world, BlockPos pos) {
		return itemsAround(world, pos, null);
	}
	
	public static List<ItemStack> nonEmptyItemsNear(Level world, BlockPos pos) {
		return itemsAround(world, pos, NON_EMPTY);
	}
	
	public static List<ItemStack> itemsRestingOn(Level world, BlockPos pos) {
		return itemsAround(world, pos, RESTING_ON);
	}
	
	public static List<ItemStack> nonEmptyItemsRestingOn(Level world, BlockPos pos) {
		return itemsAround(world, pos, NON_EMPTY.and(RESTING_ON));
	}
	
	public static List<ItemFrame> near(Level world, BlockPos pos) {
		return framesAround(world, pos, null);
	}
	
	public static List<ItemFrame> nonEmptyNear(Level world, BlockPos pos) {
		return framesAround(world, pos, NON_EMPTY);
	}
	
	public static List<ItemFrame> restingOn(Level world, BlockPos pos) {
		return framesAround(world, pos, RESTING_ON);
	}
	
	public static List<ItemFrame> nonEmptyRestingOn(Level world, BlockPos pos) {
		return framesAround(world, pos, NON_EMPTY.and(RESTING_ON));
	}
	
	//directional: The item frame resting on the north side of the block, is returned in the north slot of the map.
	// Only frames resting on the block can be returned, there can only be one in each direction.
	
	public static Map<Direction, ItemFrame> directionalRestingOn(Level world, BlockPos pos) {
		return directionalFramesRestingOnAround(world, pos, null);
	}
	
	public static Map<Direction, ItemFrame> directionalNonEmptyRestingOn(Level world, BlockPos pos) {
		return directionalFramesRestingOnAround(world, pos, NON_EMPTY);
	}
	
	//i dont wanna fuckin microbenchmark this but like... how hot is this code? probably not that hot
	// might be worth it to invest in blockpos.mutable & friends, though... use a similar approach to TE scanning in rhodo
	// actually a better approach would probably be doing a single getEntitiesWithinAABB call lol
	
	public static List<ItemStack> itemsAround(Level world, BlockPos pos, @Nullable BiPredicate<ItemFrame, Direction> test) {
		List<ItemStack> items = new ArrayList<>(6);
		
		for(Direction dir : Direction.values()) {
			BlockPos off = pos.relative(dir);
			for(ItemFrame frame : world.getEntitiesOfClass(ItemFrame.class, new AABB(off, off.offset(1, 1, 1)))) {
				if(frame.isAlive() && (test == null || test.test(frame, dir))) {
					items.add(frame.getItem());
				}
			}
		}
		
		return items;
	}
	
	public static List<ItemFrame> framesAround(Level world, BlockPos pos, @Nullable BiPredicate<ItemFrame, Direction> test) {
		List<ItemFrame> frames = new ArrayList<>(6);
		
		for(Direction dir : Direction.values()) {
			BlockPos off = pos.relative(dir);
			for(ItemFrame frame : world.getEntitiesOfClass(ItemFrame.class, new AABB(off, off.offset(1, 1, 1)))) {
				if(frame.isAlive() && (test == null || test.test(frame, dir))) {
					frames.add(frame);
				}
			}
		}
		
		return frames;
	}
	
	public static Map<Direction, ItemFrame> directionalFramesRestingOnAround(Level world, BlockPos pos, @Nullable BiPredicate<ItemFrame, Direction> test) {
		Map<Direction, ItemFrame> frames = new EnumMap<>(Direction.class);
		
		for(Direction dir : Direction.values()) {
			BlockPos off = pos.relative(dir);
			for(ItemFrame frame : world.getEntitiesOfClass(ItemFrame.class, new AABB(off, off.offset(1, 1, 1)))) {
				if(frame.isAlive() && frame.getDirection() == dir && (test == null || test.test(frame, dir))) {
					frames.put(dir, frame);
				}
			}
		}
		
		return frames;
	}
}
