package agency.highlysuspect.rhododendrite.block.tile;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import vazkii.botania.common.block.tile.TileMod;

import javax.annotation.Nullable;

//Basically this is just spicy red string
public abstract class AbstractComputerTile extends TileMod {
	public AbstractComputerTile(TileEntityType<?> type) {
		super(type);
	}
	
	public static final int RANGE = 8;
	
	//Perform a bind with a "direct" algorithm.
	//I scan forwards and look for the first block that passes the predicate.
	protected @Nullable BlockPos directBind(Direction dir, DirectBindPredicate directBind) {
		//return rootExtractingChainBind(dir, (cursor, tile) -> directBind.bindsTo(poss, tile) ? poss : null); //Wasteful
		assert level != null;
		
		BlockPos.Mutable cursor = worldPosition.mutable();
		for(int i = 0; i < RANGE; i++) {
			cursor.move(dir);
			@Nullable TileEntity tile = level.getBlockEntity(cursor);
			if(directBind.bindsTo(cursor, tile)) return cursor.immutable();
		}
		
		return null;
	}
	
	//Perform a bind with a "root-extraction" algorithm.
	//I scan forwards and at every position, ask you "so, if I bound to this block, what would the root be?"
	//You may return null (so i won't bind to that block), or return *any* blockpos, which i will bind to.
	//You can bind to "this block" by passing in the same blockpos I passed you.
	//
	//The idea here - if you are a RhodoOp, and want to bind to blocks of type RhodoCell while allowing
	//the bind to "chain" through more RhodoOp blocks; if you find an already-bound RhodoOp, there's no need
	//to perform more expensive block-by-block scanning, because you can just copy-paste its bind position.
	protected @Nullable ChainBindResult rootExtractingChainBind(Direction dir, ChainBindRootExtactor chainRoot) {
		assert level != null;
		
		BlockPos.Mutable cursor = worldPosition.mutable();
		for(int i = 0; i < RANGE; i++) { //same as red string
			cursor.move(dir);
			@Nullable TileEntity tile = level.getBlockEntity(cursor);
			@Nullable BlockPos root = chainRoot.getRootBind(cursor, tile);
			if(root != null) return new ChainBindResult(cursor.immutable(), root.immutable());
		}
		return null;
	}
	
	public static class ChainBindResult {
		public ChainBindResult(BlockPos direct, BlockPos root) {
			this.direct = direct;
			this.root = root;
		}
		
		public final BlockPos direct;
		public final BlockPos root;
	}
	
	protected interface DirectBindPredicate {
		boolean bindsTo(BlockPos.Mutable cursor, @Nullable TileEntity tile);
	}
	
	protected interface ChainBindRootExtactor {
		@Nullable
		BlockPos getRootBind(BlockPos.Mutable cursor, @Nullable TileEntity tile);
	}
}
