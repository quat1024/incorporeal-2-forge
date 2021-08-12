package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Predicate;

public class FrameReader {
	public static Map<Direction, ItemFrameEntity> readFramesRestingOn(World world, BlockPos pos) {
		return readFramesRestingOnAndMatching(world, pos, stack -> true);
	}
	
	public static Map<Direction, ItemFrameEntity> readFramesRestingOnAndMatching(World world, BlockPos pos, Predicate<ItemStack> test) {
		Map<Direction, ItemFrameEntity> frames = new EnumMap<>(Direction.class);
		for(Direction dir : Direction.values()) readFrameOffsetBy(world, pos, dir, test).ifPresent(frame -> frames.put(dir, frame));
		return frames;
	}
	
	//TODO yeah this assumes you want non-empty item frames
	// but for stuff like the frame tinkerer you might want empty item frames too
	public static Optional<ItemFrameEntity> readFrameOffsetBy(World world, BlockPos pos, Direction dir, Predicate<ItemStack> test) {
		BlockPos offset = pos.offset(dir);
		return world.getEntitiesWithinAABB(ItemFrameEntity.class, new AxisAlignedBB(offset, offset.add(1, 1, 1)))
			.stream()
			.filter(frame -> frame.isAlive() &&
				frame.getHorizontalFacing() == dir &&
				!frame.getDisplayedItem().isEmpty() && 
				test.test(frame.getDisplayedItem()))
			.findFirst();
	}
}
