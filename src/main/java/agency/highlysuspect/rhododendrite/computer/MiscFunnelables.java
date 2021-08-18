package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.MathHelper;
import vazkii.botania.api.corporea.CorporeaHelper;

import javax.annotation.Nonnull;
import java.util.Optional;

public class MiscFunnelables {
	public static final RhodoFunnelable.Loose DUST = (world, pos, state, face) -> state.getBlock() == Blocks.REDSTONE_WIRE ? new RhodoFunnelable() {
		@Override
		public boolean canRhodoExtract() {
			return true;
		}
		
		@Override
		public Optional<SolidifiedRequest> rhodoExtract(boolean simulate) {
			return Optional.of(new SolidifiedRequest(
				CorporeaHelper.instance().createMatcher(new ItemStack(Items.REDSTONE), true),
				state.get(RedstoneWireBlock.POWER)));
		}
	} : null;
	
	public static final RhodoFunnelable.Loose REPEATER = (world, pos, state, face) -> state.getBlock() == Blocks.REPEATER ? new RhodoFunnelable() {
		@Override
		public boolean canRhodoExtract() {
			return true;
		}
		
		@Override
		public Optional<SolidifiedRequest> rhodoExtract(boolean simulate) {
			return Optional.of(new SolidifiedRequest(
				CorporeaHelper.instance().createMatcher(new ItemStack(Blocks.REPEATER), true),
				state.get(RepeaterBlock.DELAY)));
		}
		
		@Override
		public boolean canRhodoInsert() {
			return true;
		}
		
		@Override
		public boolean tryRhodoInsert(@Nonnull SolidifiedRequest request, boolean simulate) {
			int clampCount = MathHelper.clamp(request.count, 1, 4);
			if(clampCount == request.count) {
				if(!simulate) {
					world.setBlockState(pos, state.with(RepeaterBlock.DELAY, clampCount));
				}
				return true;
			} else return false;
		}
	} : null;
}
