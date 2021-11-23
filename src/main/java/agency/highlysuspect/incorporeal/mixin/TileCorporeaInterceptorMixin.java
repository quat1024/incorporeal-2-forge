package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.block.CorporeaSolidifierBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;
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
		remap = false,
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/level/Level;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)Z"
		)
	)
	private void whenIntercepting(ICorporeaRequestMatcher request, int count, ICorporeaSpark spark, ICorporeaSpark source, List<ItemStack> stacks, List<ICorporeaNode> nodes, boolean doit, CallbackInfo ci) {
		Level level = ((TileCorporeaInterceptor) (Object) this).getLevel(); assert level != null;
		BlockPos pos = ((TileCorporeaInterceptor) (Object) this).getBlockPos();
		
		for(Direction dir : Direction.values()) {
			BlockPos solidifierPos = pos.relative(dir);
			BlockState state = level.getBlockState(solidifierPos);
			if(state.getBlock() instanceof CorporeaSolidifierBlock) {
				((CorporeaSolidifierBlock) state.getBlock()).receiveRequest(level, solidifierPos, state, request, count);
			}
		}
	}
}
