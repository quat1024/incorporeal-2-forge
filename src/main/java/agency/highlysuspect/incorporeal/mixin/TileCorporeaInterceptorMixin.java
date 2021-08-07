package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.Init;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import vazkii.botania.api.corporea.ICorporeaNode;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.api.corporea.ICorporeaSpark;
import vazkii.botania.common.block.tile.corporea.TileCorporeaInterceptor;

import java.util.List;

@Mixin(TileCorporeaInterceptor.class)
public class TileCorporeaInterceptorMixin {
	@Inject(
		method = "interceptRequestLast",
		at = @At("HEAD"),
		remap = false
	)
	private void whenIntercepting(ICorporeaRequestMatcher request, int count, ICorporeaSpark spark, ICorporeaSpark source, List<ItemStack> stacks, List<ICorporeaNode> nodes, boolean doit, CallbackInfo ci) {
		Init.LOG.info("Intercepting!!!!!");
	}
}
