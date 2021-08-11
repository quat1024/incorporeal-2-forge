package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class FrameReader {
	public static List<ItemStack> readFramesRestingOn(World world, BlockPos pos) {
		return readFramesRestingOnAndMatching(world, pos, stack -> true);
	}
	
	//Pretty much copied from TileCorporeaFunnel.
	public static List<ItemStack> readFramesRestingOnAndMatching(World world, BlockPos pos, Predicate<ItemStack> test) {
		List<ItemStack> stacks = new ArrayList<>();
		
		for (Direction dir : Direction.values()) {
			List<ItemFrameEntity> frames = world.getEntitiesWithinAABB(ItemFrameEntity.class, new AxisAlignedBB(pos.offset(dir), pos.offset(dir).add(1, 1, 1)));
			for (ItemFrameEntity frame : frames) {
				Direction orientation = frame.getHorizontalFacing();
				if (orientation == dir) {
					ItemStack stack = frame.getDisplayedItem();
					if (!stack.isEmpty() && test.test(stack)) {
						stacks.add(stack.copy());
					}
				}
			}
		}
		
		return stacks;
	}
}
