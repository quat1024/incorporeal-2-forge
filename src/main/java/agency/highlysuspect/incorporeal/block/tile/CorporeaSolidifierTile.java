package agency.highlysuspect.incorporeal.block.tile;

import net.minecraft.tileentity.TileEntity;

public class CorporeaSolidifierTile extends TileEntity {
	//rrrrreh okay, yeah
	//so TileCorporeaInterceptor checks "instanceof TileCorporeaRetainer" to send requests to be stored
	//and in 1.12 i just teehee subclassed that
	//but with the new BlockEntityType system, i cannot easily subclass other tile entities that don't allow me to inject a blockentitytype in their constructor
	//(which is like, all of them)
	
	//one block in and i already need a mixin ;)
	
	public CorporeaSolidifierTile() {
		super(IncTileTypes.CORPOREA_SOLIDIFIER);
	}
}
