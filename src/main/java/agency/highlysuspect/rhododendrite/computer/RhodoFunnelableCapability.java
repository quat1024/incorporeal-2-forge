package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Optional;

public class RhodoFunnelableCapability {
	@CapabilityInject(RhodoFunnelable.class)
	public static Capability<RhodoFunnelable> INSTANCE;
	
	public static final ArrayList<RhodoFunnelable.Loose> LOOSE_FUNNELABLES = new ArrayList<>();
	
	public static void initialize() {
		CapabilityManager.INSTANCE.register(RhodoFunnelable.class, Hooooooooooooooooo.INSTANCE, () -> Hooooooooooooooooo.INSTANCE);
	}
	
	public static void registerLooseFunnelable(RhodoFunnelable.Loose loose) {
		//yeah i dont fuckin know how multithreaded modloading works
		//i think you just slap synchronized on things and then its good
		//and it probably works
		//and if it doesnt work, just add more synchronized until it works
		//not like anyone's gonna make an addon for this mod anyways :megajoy:
		synchronized(LOOSE_FUNNELABLES) {
			LOOSE_FUNNELABLES.add(loose);
		}
	}
	
	public static void registerBuiltinLooseFunnelables() {
		registerLooseFunnelable(MiscFunnelables.DUST);
		registerLooseFunnelable(MiscFunnelables.REPEATER);
	}
	
	public static @Nullable RhodoFunnelable findFunnelable(World world, BlockPos pos, Direction face) {
		//If the block implements RhodoFunnelable
		BlockState state = world.getBlockState(pos);
		if(state.getBlock() instanceof RhodoFunnelable) {
			return (RhodoFunnelable) state.getBlock();
		}
		
		//If the tile implements RhodoFunnelable
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof RhodoFunnelable) return (RhodoFunnelable) tile;
		
		//If the tile has a RhodoFunnelable capability
		if(tile != null) {
			LazyOptional<RhodoFunnelable> asd = tile.getCapability(RhodoFunnelableCapability.INSTANCE);
			if(asd.isPresent()) //noinspection OptionalGetWithoutIsPresent
				return asd.resolve().get();
		}
		
		//entity moment
		for(Entity e : world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
			//If the entity implements RhodoFunnelable
			if(e instanceof RhodoFunnelable) return (RhodoFunnelable) e;
			
			//If the entity has a RhodoFunnelable capability
			LazyOptional<RhodoFunnelable> asd = e.getCapability(RhodoFunnelableCapability.INSTANCE);
			if(asd.isPresent()) //noinspection OptionalGetWithoutIsPresent
				return asd.resolve().get();
		}
		
		//Try loose funnelables
		for(RhodoFunnelable.Loose pee : RhodoFunnelableCapability.LOOSE_FUNNELABLES) {
			RhodoFunnelable yes = pee.getFunnelable(world, pos, state, face);
			if(yes != null) return yes;
		}
		
		//Oh well
		return null;
	}
	
	public static class Hooooooooooooooooo implements RhodoFunnelable, Capability.IStorage<RhodoFunnelable> {
		public static final Hooooooooooooooooo INSTANCE = new Hooooooooooooooooo();
		
		@Nullable
		@Override
		public INBT writeNBT(Capability<RhodoFunnelable> capability, RhodoFunnelable instance, Direction side) {
			return new CompoundNBT();
		}
		
		@Override
		public void readNBT(Capability<RhodoFunnelable> capability, RhodoFunnelable instance, Direction side, INBT nbt) {
			//No
		}
	}
}
