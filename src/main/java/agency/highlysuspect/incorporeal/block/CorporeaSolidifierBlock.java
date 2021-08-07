package agency.highlysuspect.incorporeal.block;

import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

public class CorporeaSolidifierBlock extends Block {
	public CorporeaSolidifierBlock(Properties properties) {
		super(properties);
	}
	
	public void receiveRequest(World world, BlockPos pos, BlockState state, ICorporeaRequestMatcher request, int count) {
		if(world == null || world.isRemote) return;
		
		world.addEntity(new ItemEntity(world,
			pos.getX() + .5, pos.getY() + 1, pos.getZ() + .5,
			IncItems.CORPOREA_TICKET.produceForRequest(request, count))
		);
	}
}
