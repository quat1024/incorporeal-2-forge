package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.incorporeal.corporea.RetainerDuck;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.incorporeal.item.IncItems;
import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.mixin.AccessorCorporeaItemStackMatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.ICorporeaRequestor;
import vazkii.botania.api.corporea.ICorporeaSpark;
import vazkii.botania.common.block.tile.corporea.TileCorporeaCrystalCube;
import vazkii.botania.common.block.tile.corporea.TileCorporeaFunnel;
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex;
import vazkii.botania.common.block.tile.corporea.TileCorporeaRetainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class RhoCapEvents {
	public static void tileCaps(AttachCapabilitiesEvent<TileEntity> e) {
		if(e.getObject() instanceof TileCorporeaRetainer) {
			e.addCapability(RETAINER_CAP, new RetainerFunnelable((RetainerDuck) e.getObject()));
		}
		
		if(e.getObject() instanceof TileCorporeaCrystalCube) {
			e.addCapability(CRYSTAL_CUBE_CAP, new CrystalCubeFunnelable((TileCorporeaCrystalCube) e.getObject()));
		}
		
		if(e.getObject() instanceof TileCorporeaFunnel || e.getObject() instanceof TileCorporeaIndex) {
			e.addCapability(REQUESTOR_CAP, new RequestorFunnelable<>((TileEntity & ICorporeaRequestor) e.getObject()));
		}
	}
	
	public static void entCaps(AttachCapabilitiesEvent<Entity> e) {
		if(e.getObject() instanceof ItemEntity) {
			e.addCapability(ITEM_ENTITY_CAP, new ItemStackFunnelable.Ent((ItemEntity) e.getObject()));
		}
		
		if(e.getObject() instanceof ItemFrameEntity) {
			e.addCapability(ITEM_FRAME_CAP, new ItemStackFunnelable.Frame((ItemFrameEntity) e.getObject()));
		}
	}
	
	private static final ResourceLocation RETAINER_CAP = Rho.id("retainer_cap");
	private static final ResourceLocation CRYSTAL_CUBE_CAP = Rho.id("crystal_cube_cap");
	private static final ResourceLocation REQUESTOR_CAP = Rho.id("requestor_cap");
	
	private static final ResourceLocation ITEM_ENTITY_CAP = Rho.id("item_entity_cap");
	private static final ResourceLocation ITEM_FRAME_CAP = Rho.id("item_frame_cap");
	
	public static abstract class FunnelableBase implements RhodoFunnelable, ICapabilityProvider {
		@Nonnull
		@Override
		public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
			return RhodoFunnelableCapability.INSTANCE.orEmpty(cap, LazyOptional.of(() -> this));
		}
	}
	
	public static class RetainerFunnelable extends FunnelableBase {
		public RetainerFunnelable(RetainerDuck duck) {
			this.duck = duck;
		}
		
		protected final RetainerDuck duck;
		
		@Override
		public boolean canRhodoExtract() {
			return true;
		}
		
		@Override
		public Optional<SolidifiedRequest> rhodoExtract(boolean simulate) {
			return Optional.of(duck.inc$solidifyRequest());
		}
		
		@Override
		public boolean canRhodoInsert() {
			return true;
		}
		
		@Override
		public boolean tryRhodoInsert(@Nonnull SolidifiedRequest request, boolean simulate) {
			if(!simulate) duck.inc$liquidateRequest(request);
			return true;
		}
	}
	
	public static class CrystalCubeFunnelable extends FunnelableBase {
		public CrystalCubeFunnelable(TileCorporeaCrystalCube cube) {
			this.cube = cube;
		}
		
		protected final TileCorporeaCrystalCube cube;
		
		@Override
		public boolean canRhodoExtract() {
			return !cube.getRequestTarget().isEmpty();
		}
		
		@Override
		public Optional<SolidifiedRequest> rhodoExtract(boolean simulate) {
			ItemStack displayed = cube.getRequestTarget();
			
			ItemStack displayed1 = displayed.copy();
			displayed1.setCount(1);
			
			return Optional.of(new SolidifiedRequest(displayed1, cube.getItemCount()));
		}
		
		@Override
		public boolean canRhodoInsert() {
			return true;
		}
		
		@Override
		public boolean tryRhodoInsert(@Nonnull SolidifiedRequest request, boolean simulate) {
			if(request.matcher instanceof AccessorCorporeaItemStackMatcher) {
				if(!simulate)
					cube.setRequestTarget(((AccessorCorporeaItemStackMatcher) request.matcher).rho$getMatcher().copy());
				return true;
			} else return false;
		}
	}
	
	public static class RequestorFunnelable<T extends TileEntity & ICorporeaRequestor> extends FunnelableBase {
		public RequestorFunnelable(T tile) {
			this.tile = tile;
		}
		
		protected final T tile;
		
		@Override
		public boolean canRhodoInsert() {
			return CorporeaHelper.instance().getSparkForBlock(tile.getWorld(), tile.getPos()) != null;
		}
		
		@Override
		public boolean tryRhodoInsert(@Nonnull SolidifiedRequest request, boolean simulate) {
			ICorporeaSpark spork = CorporeaHelper.instance().getSparkForBlock(tile.getWorld(), tile.getPos());
			if(spork == null) return false;
			
			if(!simulate) {
				tile.doCorporeaRequest(request.matcher, request.count, spork);
			}
			
			return true;
		}
	}
	
	public interface ItemStackFunnelable extends RhodoFunnelable, ICapabilityProvider {
		ItemStack getStack();
		
		void setStack(ItemStack stack);
		
		default int requestSize() {
			return getStack().getCount();
		}
		
		default boolean isTicket() {
			ItemStack stack = getStack();
			return !stack.isEmpty() && stack.getItem() == IncItems.CORPOREA_TICKET;
		}
		
		@Override
		default boolean canRhodoExtract() {
			return true;
		}
		
		@Override
		default Optional<SolidifiedRequest> rhodoExtract(boolean simulate) {
			ItemStack stack = getStack();
			if(isTicket()) return IncItems.CORPOREA_TICKET.getRequest(stack);
			else {
				ItemStack copy = stack.copy();
				copy.setCount(1);
				return Optional.of(new SolidifiedRequest(copy, requestSize()));
			}
		}
		
		@Override
		default boolean canRhodoInsert() {
			return isTicket();
		}
		
		@Override
		default boolean tryRhodoInsert(@Nonnull SolidifiedRequest request, boolean simulate) {
			if(!isTicket()) return false;
			if(!simulate) setStack(IncItems.CORPOREA_TICKET.produce(request));
			return true;
		}
		
		@Nonnull
		@Override
		default <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
			return RhodoFunnelableCapability.INSTANCE.orEmpty(cap, LazyOptional.of(() -> this));
		}
		
		class Ent implements ItemStackFunnelable {
			public Ent(ItemEntity ent) {
				this.ent = ent;
			}
			
			protected final ItemEntity ent;
			
			@Override
			public ItemStack getStack() {
				return ent.getItem();
			}
			
			@Override
			public void setStack(ItemStack stack) {
				//inb4 dupe bug
				stack.setCount(ent.getItem().getCount());
				ent.setItem(stack);
			}
		}
		
		class Frame implements ItemStackFunnelable {
			public Frame(ItemFrameEntity frame) {
				this.frame = frame;
			}
			
			protected final ItemFrameEntity frame;
			
			@Override
			public ItemStack getStack() {
				return frame.getDisplayedItem();
			}
			
			@Override
			public void setStack(ItemStack stack) {
				frame.setDisplayedItem(stack);
			}
			
			@Override
			public int requestSize() {
				return new int[]{1, 2, 4, 8, 16, 32, 48, 64}[frame.getRotation()];
			}
		}
	}
}
