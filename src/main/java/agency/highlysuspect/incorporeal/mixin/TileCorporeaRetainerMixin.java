package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.corporea.EmptyCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.RetainerDuck;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.common.block.tile.corporea.TileCorporeaRetainer;

import javax.annotation.Nullable;

@Mixin(TileCorporeaRetainer.class)
public abstract class TileCorporeaRetainerMixin implements RetainerDuck {
	@Shadow(remap = false) private BlockPos requestPos;
	@Shadow(remap = false) @Nullable private ICorporeaRequestMatcher request;
	@Shadow(remap = false) private int requestCount;
	@Shadow(remap = false) private int compValue;
	
	@Shadow(remap = false) public abstract boolean hasPendingRequest();
	
	@Override
	public SolidifiedRequest inc$solidifyRequest() {
		if(!hasPendingRequest()) return SolidifiedRequest.EMPTY;
		else return new SolidifiedRequest(request, requestCount);
	}
	
	@Override
	public void inc$liquidateRequest(SolidifiedRequest solid) {
		this.request = solid.matcher;
		this.requestCount = solid.count;
		
		//A difference between the SolidifiedRequest world and the world of regular Botania.
		if(requestCount == 0 || solid.matcher == EmptyCorporeaRequestMatcher.INSTANCE) request = null;
		
		compValue = CorporeaHelper.instance().signalStrengthForRequestSize(requestCount);
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
}
