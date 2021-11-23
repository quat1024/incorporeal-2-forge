package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.computer.RhodoFunnelable;
import agency.highlysuspect.rhododendrite.computer.RhodoFunnelableCapability;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import vazkii.botania.common.core.helper.Vector3;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class RhodoFunnelTile extends AbstractComputerTile implements ITickableTileEntity {
	public RhodoFunnelTile() {
		super(RhoTileTypes.FUNNEL);
	}
	
	protected transient @Nullable FunnelBindResult foreBinding;
	protected transient @Nullable FunnelBindResult aftBinding;
	
	public Vector3 getForeDirectBind() {
		return foreBinding == null ? null : foreBinding.direct;
	}
	
	public Vector3 getForeRootBind() {
		return foreBinding == null ? null : foreBinding.root;
	}
	
	public Vector3 getAftDirectBind() {
		return aftBinding == null ? null : aftBinding.direct;
	}
	
	public Vector3 getAftRootBind() {
		return aftBinding == null ? null : aftBinding.root;
	}
	
	@Override
	public void tick() {
		Direction dir = getBlockState().getValue(DirectionalBlock.FACING);
		
		foreBinding = rootExtractingChainBindFunnelable(dir, true);
		aftBinding = rootExtractingChainBindFunnelable(dir.getOpposite(), false);
	}
	
	public void onRedstonePower() {
		if(foreBinding == null || aftBinding == null) return; //can't do anything in this case.
		
		RhodoFunnelable source = aftBinding.funnelableProvider.get();
		RhodoFunnelable sink = foreBinding.funnelableProvider.get();
		
		if(source == null || sink == null) return;
		
		//these cases shouldn't get bound to anyways, but hey, you never know
		if(!source.canRhodoExtract()) return;
		if(!sink.canRhodoInsert()) return;
		
		Optional<SolidifiedRequest> tryExtr = source.rhodoExtract(true);
		if(tryExtr.isPresent()) {
			boolean tryFit = sink.tryRhodoInsert(tryExtr.get(), true);
			if(tryFit) {
				@SuppressWarnings("OptionalGetWithoutIsPresent") //gottem
				SolidifiedRequest extr = source.rhodoExtract(false).get();
				
				boolean itFit = sink.tryRhodoInsert(extr, false);
				assert itFit;
			}
		}
	}
	
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		if(foreBinding == null && aftBinding == null) return new AxisAlignedBB(levelPosition);
		if(foreBinding == null) return new AxisAlignedBB(levelPosition, conv(aftBinding.direct));
		if(aftBinding == null) return new AxisAlignedBB(levelPosition, conv(foreBinding.direct));
		else return new AxisAlignedBB(conv(foreBinding.direct), conv(aftBinding.direct));
	}
	
	//convert Botania Vec3 to a blockpos in one step.
	private static BlockPos conv(Vector3 a) {
		return new BlockPos(a.x, a.y, a.z);
	}
	
	//Similar to the one in AbstractComputerTile but adapted to how funnels work specifically.
	private @Nullable
	FunnelBindResult rootExtractingChainBindFunnelable(Direction dir, boolean fore) {
		assert level != null;
		
		Predicate<RhodoFunnelable> funnelableCond = fore ? RhodoFunnelable::canRhodoInsert : RhodoFunnelable::canRhodoExtract;
		
		BlockPos.Mutable cursor = levelPosition.mutable();
		for(int i = 0; i < RANGE; i++) {
			cursor.move(dir);
			
			TileEntity t = level.getBlockEntity(cursor);
			
			//Chain through other rhodo funnels
			if(t instanceof RhodoFunnelTile) {
				FunnelBindResult otherBind = fore ? ((RhodoFunnelTile) t).foreBinding : ((RhodoFunnelTile) t).aftBinding;
				if(otherBind != null)
					return new FunnelBindResult(Vector3.fromTileEntityCenter(t), otherBind.root, otherBind.funnelableProvider);
			}
			
			//Bind directly to entities
			List<Entity> nearbyEntities = level.getEntitiesOfClass(Entity.class, new AxisAlignedBB(cursor));
			Collections.shuffle(nearbyEntities, level.random);
			for(Entity e : nearbyEntities) {
				LazyOptional<RhodoFunnelable> cap = e.getCapability(RhodoFunnelableCapability.INSTANCE);
				//noinspection OptionalGetWithoutIsPresent
				if(cap.isPresent() && funnelableCond.test(cap.resolve().get())) {
					Vector3 p = Vector3.fromEntityCenter(e).add(0, -0.25, 0);
					return new FunnelBindResult(p, p, fromLazyOpt(cap));
				}
			}
			
			//Bind directly to tile entities
			if(t != null) {
				LazyOptional<RhodoFunnelable> cap = t.getCapability(RhodoFunnelableCapability.INSTANCE);
				//noinspection OptionalGetWithoutIsPresent
				if(cap.isPresent() && funnelableCond.test(cap.resolve().get())) {
					Vector3 p = Vector3.fromTileEntityCenter(t);
					return new FunnelBindResult(p, p, fromLazyOpt(cap));
				}
			}
			
			//Try loose funnelables
			BlockState state = level.getBlockState(cursor);
			for(RhodoFunnelable.Loose loose : RhodoFunnelableCapability.LOOSE_FUNNELABLES) {
				RhodoFunnelable yes = loose.getFunnelable(level, cursor, state, dir);
				if(yes != null && funnelableCond.test(yes)) {
					Vector3 p = loose.bindPosition(cursor);
					return new FunnelBindResult(p, p, fromLoose(level, cursor.immutable(), state, dir, loose));
				}
			}
		}
		
		return null;
	}
	
	private static Supplier<RhodoFunnelable> fromLazyOpt(LazyOptional<RhodoFunnelable> op) {
		return () -> op.resolve().orElse(null);
	}
	
	private static Supplier<RhodoFunnelable> fromLoose(Level level, BlockPos pos, BlockState state, Direction face, RhodoFunnelable.Loose looseFunnelable) {
		WeakReference<Level> levelWeak = new WeakReference<>(level);
		return () -> {
			Level w = levelWeak.get();
			return w == null ? null : looseFunnelable.getFunnelable(w, pos, state, face);
		};
	}
	
	public static class FunnelBindResult {
		public FunnelBindResult(Vector3 direct, Vector3 root, Supplier<RhodoFunnelable> funnelableProvider) {
			this.direct = direct;
			this.root = root;
			this.funnelableProvider = funnelableProvider;
		}
		
		public final Vector3 direct;
		public final Vector3 root;
		public final Supplier<RhodoFunnelable> funnelableProvider;
	}
}
