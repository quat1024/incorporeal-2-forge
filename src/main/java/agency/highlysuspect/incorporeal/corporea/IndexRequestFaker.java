package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.ICorporeaResult;
import vazkii.botania.api.corporea.ICorporeaSpark;

import java.util.List;

public class IndexRequestFaker {
	public static void requestAtIndex(Level level, SolidifiedRequest request, ICorporeaSpark indexSpark, BlockPos indexPos) {
		if(indexSpark == null) return;
		
		ICorporeaResult result = CorporeaHelper.instance().requestItem(request.matcher, request.count, indexSpark, true);
		List<ItemStack> stacks = result.getStacks();
		indexSpark.onItemsRequested(stacks);
		for(ItemStack stack : stacks) {
			if(!stack.isEmpty()) {
				ItemEntity item = new ItemEntity(level, indexPos.getX() + 0.5, indexPos.getY() + 1.5, indexPos.getZ() + 0.5, stack);
				level.addFreshEntity(item);
			}
		}
	}
}
