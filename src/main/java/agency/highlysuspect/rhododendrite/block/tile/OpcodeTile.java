package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.corporea.FrameReader;
import agency.highlysuspect.rhododendrite.item.OpcodeCardItem;
import net.minecraft.item.ItemStack;
import java.util.List;

import static agency.highlysuspect.rhododendrite.item.OpcodeCardItem.CoreAction.Result;

public class OpcodeTile extends RhodoNetworkTile {
	public OpcodeTile() {
		super(RhoTileTypes.OPCODE);
	}
	
	public Result runAction() {
		if(world == null) return Result.NOT_APPLICABLE;
		
		List<ItemStack> choices = FrameReader.readFramesRestingOnAndMatching(world, pos, stack -> stack.getItem() instanceof OpcodeCardItem);
		if(choices.isEmpty()) return Result.NOT_APPLICABLE;
		else {
			CoreTile core = findCore();
			if(core == null) return Result.NOT_APPLICABLE;
			
			sparkle();
			
			OpcodeCardItem choice = (OpcodeCardItem) Inc.choose(choices, world.rand).getItem();
			return choice.action.act(world, pos, getBlockState(), core);
		}
	}
}
