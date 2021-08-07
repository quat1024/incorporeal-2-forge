package agency.highlysuspect.incorporeal.mixin;

import agency.highlysuspect.incorporeal.block.CorporeaSolidifierBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "Lnet/minecraft/world/World;getTileEntity(Lnet/minecraft/util/math/BlockPos;)Lnet/minecraft/tileentity/TileEntity;"
		),
		//TODO: Well, interceptRequestLast doesn't need to get remapped, but getTileEntity most certainly does.
		// Does setting remap = false give the correct behavior?
		remap = false
	)
	private void whenIntercepting(ICorporeaRequestMatcher request, int count, ICorporeaSpark spark, ICorporeaSpark source, List<ItemStack> stacks, List<ICorporeaNode> nodes, boolean doit, CallbackInfo ci) {
		World world = ((TileCorporeaInterceptor) (Object) this).getWorld(); assert world != null;
		BlockPos pos = ((TileCorporeaInterceptor) (Object) this).getPos();
		
		for(Direction dir : Direction.values()) {
			BlockState state = world.getBlockState(pos.offset(dir));
			if(state.getBlock() instanceof CorporeaSolidifierBlock) {
				((CorporeaSolidifierBlock) state.getBlock()).receiveRequest(world, pos, state, request, count);
			}
		}
	}
}
