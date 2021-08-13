package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;

public class FrameReader {	
	public static final BiPredicate<ItemFrameEntity, Direction> NON_EMPTY = (frame, dir) -> !frame.getDisplayedItem().isEmpty();
	public static final BiPredicate<ItemFrameEntity, Direction> RESTING_ON = (frame, dir) -> frame.getHorizontalFacing() == dir;
	
	//near: The item frame is anywhere in the 6 blocks surrounding the provided blockpos. (e.g. "frame tinkerer")
	//resting on: The item frame is resting on the provided blockpos. (e.g. "corporea funnel")
	
	public static List<ItemStack> itemsNear(World world, BlockPos pos) {
		return itemsAround(world, pos, null);
	}
	
	public static List<ItemStack> nonEmptyItemsNear(World world, BlockPos pos) {
		return itemsAround(world, pos, NON_EMPTY);
	}
	
	public static List<ItemStack> itemsRestingOn(World world, BlockPos pos) {
		return itemsAround(world, pos, RESTING_ON);
	}
	
	public static List<ItemStack> nonEmptyItemsRestingOn(World world, BlockPos pos) {
		return itemsAround(world, pos, NON_EMPTY.and(RESTING_ON));
	}
	
	public static List<ItemFrameEntity> near(World world, BlockPos pos) {
		return framesAround(world, pos, null);
	}
	
	public static List<ItemFrameEntity> nonEmptyNear(World world, BlockPos pos) {
		return framesAround(world, pos, NON_EMPTY);
	}
	
	public static List<ItemFrameEntity> restingOn(World world, BlockPos pos) {
		return framesAround(world, pos, RESTING_ON);
	}
	
	public static List<ItemFrameEntity> nonEmptyRestingOn(World world, BlockPos pos) {
		return framesAround(world, pos, NON_EMPTY.and(RESTING_ON));
	}
	
	//directional: The item frame resting on the north side of the block, is returned in the north slot of the map.
	// Only frames resting on the block can be returned, there can only be one in each direction.
	
	public static Map<Direction, ItemFrameEntity> directionalRestingOn(World world, BlockPos pos) {
		return directionalFramesRestingOnAround(world, pos, null);
	}
	
	public static Map<Direction, ItemFrameEntity> directionalNonEmptyRestingOn(World world, BlockPos pos) {
		return directionalFramesRestingOnAround(world, pos, NON_EMPTY);
	}
	
	//i dont wanna fuckin microbenchmark this but like... how hot is this code? probably not that hot
	// might be worth it to invest in blockpos.mutable & friends, though... use a similar approach to TE scanning in rhodo
	// actually a better approach would probably be doing a single getEntitiesWithinAABB call lol
	
	public static List<ItemStack> itemsAround(World world, BlockPos pos, @Nullable BiPredicate<ItemFrameEntity, Direction> test) {
		List<ItemStack> items = new ArrayList<>(6);
		
		for(Direction dir : Direction.values()) {
			BlockPos off = pos.offset(dir);
			for(ItemFrameEntity frame : world.getEntitiesWithinAABB(ItemFrameEntity.class, new AxisAlignedBB(off, off.add(1, 1, 1)))) {
				if(frame.isAlive() && (test == null || test.test(frame, dir))) {
					items.add(frame.getDisplayedItem());
				}
			}
		}
		
		return items;
	}
	
	public static List<ItemFrameEntity> framesAround(World world, BlockPos pos, @Nullable BiPredicate<ItemFrameEntity, Direction> test) {
		List<ItemFrameEntity> frames = new ArrayList<>(6);
		
		for(Direction dir : Direction.values()) {
			BlockPos off = pos.offset(dir);
			for(ItemFrameEntity frame : world.getEntitiesWithinAABB(ItemFrameEntity.class, new AxisAlignedBB(off, off.add(1, 1, 1)))) {
				if(frame.isAlive() && (test == null || test.test(frame, dir))) {
					frames.add(frame);
				}
			}
		}
		
		return frames;
	}
	
	public static Map<Direction, ItemFrameEntity> directionalFramesRestingOnAround(World world, BlockPos pos, @Nullable BiPredicate<ItemFrameEntity, Direction> test) {
		Map<Direction, ItemFrameEntity> frames = new EnumMap<>(Direction.class);
		
		for(Direction dir : Direction.values()) {
			BlockPos off = pos.offset(dir);
			for(ItemFrameEntity frame : world.getEntitiesWithinAABB(ItemFrameEntity.class, new AxisAlignedBB(off, off.add(1, 1, 1)))) {
				if(frame.isAlive() && frame.getHorizontalFacing() == dir && (test == null || test.test(frame, dir))) {
					frames.put(dir, frame);
				}
			}
		}
		
		return frames;
	}
}
