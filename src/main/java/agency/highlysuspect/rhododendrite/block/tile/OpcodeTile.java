package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.corporea.FrameReader;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.computer.StackOps;
import agency.highlysuspect.rhododendrite.item.OpcodeCardItem;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.common.block.tile.TileAnimatedTorch;

import java.util.ArrayList;
import java.util.Map;

import static agency.highlysuspect.rhododendrite.computer.CoreAction.Result;

public class OpcodeTile extends RhodoNetworkTile {
	public OpcodeTile() {
		super(RhoTileTypes.OPCODE);
	}
	
	public Result runAction() {
		if(world == null) return Result.NOT_APPLICABLE;
		
		CoreTile core = findCore();
		if(core == null) return Result.NOT_APPLICABLE;
		
		//Locate nearby item frames.
		Map<Direction, ItemFrameEntity> frames = FrameReader.directionalNonEmptyRestingOn(world, pos);
		if(frames.isEmpty()) return Result.NOT_APPLICABLE;
		
		Direction dir;
		
		//If there's an animated torch above, choose the item frame it's pointing at.
		TileEntity above = world.getTileEntity(pos.up());
		if(above instanceof TileAnimatedTorch) {
			TileAnimatedTorch anim = (TileAnimatedTorch) above; //java 16 my beloved... i miss u.... <3
			dir = TileAnimatedTorch.SIDES[anim.side].getOpposite();
			if(!frames.containsKey(dir)) return Result.NOT_APPLICABLE;
		} else {
			//Otherwise just pick a random item frame.
			dir = Inc.choose(new ArrayList<>(frames.keySet()), world.rand);
		}
		
		ItemFrameEntity frame = frames.get(dir); //must be non-null; containsKey is checked in the only path that can return directions not already in the set
		ItemStack displayed = frame.getDisplayedItem();
		
		//Display particles to show things are working
		whenWanded(); //used to be called sparkles() until i made it do more in one block - i should split that out into its own method tbh
		
		if(displayed.getItem() instanceof OpcodeCardItem) {
			//If its an opcode card just do the thing
			return ((OpcodeCardItem) displayed.getItem()).action.apply(dir, core);
		} else {
			//If it's not an opcode card act like we're a corporea funnel, push a request for that item onto the stack
			int count = new int[] {1, 2, 4, 8, 16, 32, 48, 64}[frame.getRotation()];
			StackOps ops = StackOps.read(core);
			ops.push(new SolidifiedRequest(CorporeaHelper.instance().createMatcher(displayed, true), count));
			ops.commit();
			return Result.SUCCESS;
		}
	}
}
