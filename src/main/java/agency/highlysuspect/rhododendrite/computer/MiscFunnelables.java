package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.incorporeal.block.CorporeaSolidifierBlock;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import vazkii.botania.common.core.helper.Vector3;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class MiscFunnelables {
	public static final RhodoFunnelable.Loose DUST = new RhodoFunnelable.Loose() {
		@Nullable
		@Override
		public RhodoFunnelable getFunnelable(Level world, BlockPos pos, BlockState state, Direction face) {
			return state.getBlock() == Blocks.REDSTONE_WIRE ? new RhodoFunnelable() {
				@Override
				public boolean canRhodoExtract() {
					return true;
				}
				
				@Override
				public Optional<SolidifiedRequest> rhodoExtract(boolean simulate) {
					return Optional.of(new SolidifiedRequest(new ItemStack(Items.REDSTONE), state.getValue(RedStoneWireBlock.POWER)));
				}
			} : null;
		}
		
		@Override
		public Vector3 bindPosition(BlockPos pos) {
			return Vector3.fromBlockPos(pos).add(0.5, 1 / 16d, 0.5);
		}
	};
	
	public static final RhodoFunnelable.Loose REPEATER = new RhodoFunnelable.Loose() {
		@Nullable
		@Override
		public RhodoFunnelable getFunnelable(Level world, BlockPos pos, BlockState state, Direction face) {
			return state.getBlock() == Blocks.REPEATER ? new RhodoFunnelable() {
				@Override
				public boolean canRhodoExtract() {
					return true;
				}
				
				@Override
				public Optional<SolidifiedRequest> rhodoExtract(boolean simulate) {
					return Optional.of(new SolidifiedRequest(new ItemStack(Blocks.REPEATER), state.getValue(RepeaterBlock.DELAY)));
				}
				
				@Override
				public boolean canRhodoInsert() {
					return true;
				}
				
				@Override
				public boolean tryRhodoInsert(@Nonnull SolidifiedRequest request, boolean simulate) {
					int clampCount = Mth.clamp(request.count, 1, 4);
					if(clampCount == request.count) {
						if(!simulate) {
							world.setBlockAndUpdate(pos, state.setValue(RepeaterBlock.DELAY, clampCount));
						}
						return true;
					} else return false;
				}
			} : null;
		}
		
		@Override
		public Vector3 bindPosition(BlockPos pos) {
			return Vector3.fromBlockPos(pos).add(0.5, 1 / 16d, 0.5);
		}
	};
	
	public static final RhodoFunnelable.Loose CORPOREA_SOLIDIFIER = (world, pos, state, face) -> state.getBlock() == IncBlocks.CORPOREA_SOLIDIFIER ? new RhodoFunnelable() {
		@Override
		public boolean canRhodoInsert() {
			return true;
		}
		
		@Override
		public boolean tryRhodoInsert(@Nonnull SolidifiedRequest request, boolean simulate) {
			if(!simulate)
				((CorporeaSolidifierBlock) state.getBlock()).receiveRequest(world, pos, state, request.matcher, request.count);
			return true;
		}
	} : null;
}
