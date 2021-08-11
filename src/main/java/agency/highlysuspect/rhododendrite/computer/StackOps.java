package agency.highlysuspect.rhododendrite.computer;

import agency.highlysuspect.rhododendrite.block.CoreBlock;
import agency.highlysuspect.rhododendrite.block.tile.CoreTile;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StackOps {
	public StackOps(Fragment<?>[] fragments, Fragment.Holder[] holders) {
		this.fragments = fragments;
		this.holders = holders;
	}
	
	public StackOps() {
		this(new Fragment<?>[0], new Fragment.Holder[0]);
	}
	
	public static StackOps read(CoreTile tile) {
		return read(tile.getWorld(), tile.getPos(), tile.getBlockState());
	}
	
	public static StackOps read(World world, BlockPos corePos, BlockState coreState) {
		if(!(coreState.getBlock() instanceof CoreBlock)) return new StackOps();
		
		Direction coreFacing = coreState.get(CoreBlock.FACING);
		
		List<Fragment<?>> fragments = new ArrayList<>();
		List<Fragment.Holder> holders = new ArrayList<>();
		
		BlockPos.Mutable cursor = corePos.toMutable();
		for(int i = 0; i <= CorePathTracing.MAX_RANGE; i++) { // <=: the actual max size of a conga line is MAX_RANGE + 1, counting the Core itself
			TileEntity tile = world.getTileEntity(cursor);
			if(tile == null) break;
			
			Optional<Fragment.Holder> holder = tile.getCapability(FragmentCapability.INSTANCE).resolve();
			if(holder.isPresent()) {
				fragments.add(holder.get().getFragment());
				holders.add(holder.get());
				//offsetting at the end of the loop btw, so the core itself gets included in the conga line
				cursor.move(coreFacing);
			} else {
				break;
			}
		}
		
		return new StackOps(fragments.toArray(new Fragment<?>[0]), holders.toArray(new Fragment.Holder[0]));
	}
	
	private final Fragment<?>[] fragments;
	private final Fragment.Holder[] holders;
	
	/**
	 * Apply the changes to the Fragment.Holders. Without this, you're just working off a copy
	 */
	public void commit() {
		for(int i = 0; i < fragments.length; i++) {
			holders[i].setFragment(fragments[i]);
		}
	}
	
	public void push() {
		//[A][B][C] -> [ ][A][B]. C is lost
		push(Fragment.EMPTY);
	}
	
	public void push(Fragment<?> replacement) {
		//[A][B][C] -> [r][A][B]
		if(fragments.length == 0) return;
		if(fragments.length != 1)	System.arraycopy(fragments, 0, fragments, 1, fragments.length - 1);
		fragments[0] = replacement;
	}
	
	public Fragment<?> pull() {
		//[A][B][C] -> [B][C][ ], and A is returned
		if(fragments.length == 0) return Fragment.EMPTY;
		Fragment<?> first = fragments[0];
		destroy(1);
		return first;
	}
	
	public void destroy(int howMany) {
		System.arraycopy(fragments, howMany, fragments, 0, fragments.length - howMany);
	}
	
	public Fragment<?> peek() {
		return peek(0);
	}
	
	public Fragment<?> peek(int depth) {
		if(depth < fragments.length) return fragments[depth];
		return Fragment.EMPTY;
	}
	
	@Nullable public <T> Fragment<T> peekMatching(DataType<T> type) {
		return peekMatching(0, type);
	}
	
	//returns null instead of empty on type mismatch b/c empty fragments are Fragment<Unit> and bla bla heap pollution
	@Nullable public <T> Fragment<T> peekMatching(int depth, DataType<T> type) {
		Fragment<?> unk = peek(depth);
		if(unk.getType() == type) //noinspection unchecked
			return (Fragment<T>) unk;
		else return null;
	}
}
