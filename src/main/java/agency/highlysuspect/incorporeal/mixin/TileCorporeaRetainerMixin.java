package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.block.CorporeaRetainerEvaporatorBlock;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.common.block.tile.corporea.TileCorporeaRetainer;

import javax.annotation.Nullable;

@Mixin(TileCorporeaRetainer.class)
public class TileCorporeaRetainerMixin implements CorporeaRetainerEvaporatorBlock.Duck {
	@Shadow(remap = false) private BlockPos requestPos;
	@Shadow(remap = false) @Nullable private ICorporeaRequestMatcher request;
	@Shadow(remap = false) private int requestCount;
	@Shadow(remap = false) private int compValue;
	
	@Override
	public int inc$getRequestCount() {
		return requestCount;
	}
	
	@Override
	public void inc$setRequestCount(int newCount) {
		this.requestCount = newCount;
		((TileCorporeaRetainer) (Object) this).markDirty();
	}
	
	@Override
	public void inc$clearRequest() {
		request = null;
		requestCount = 0;
		compValue = 0;
		//Botania doesn't clear requestPos either. Kinda odd.
		((TileCorporeaRetainer) (Object) this).markDirty();
	}
	
	@Override
	public boolean inc$updateComparator() {
		int oldCompValue = compValue;
		compValue = CorporeaHelper.instance().signalStrengthForRequestSize(requestCount);
		
		if(oldCompValue != compValue) {
			((TileCorporeaRetainer) (Object) this).markDirty();
			return true;
		} else return false;
	}
}
