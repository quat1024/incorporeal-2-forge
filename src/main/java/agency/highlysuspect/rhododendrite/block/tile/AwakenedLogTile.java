package agency.highlysuspect.rhododendrite.block.tile;

import net.minecraft.nbt.CompoundNBT;

public class AwakenedLogTile extends FragmentContainerTile {
	public AwakenedLogTile() {
		super(RhoTileTypes.AWAKENED_LOG);
	}
	
	@Override
	public void writePacketNBT(CompoundNBT cmp) {
		super.writePacketNBT(cmp);
	}
	
	@Override
	public void readPacketNBT(CompoundNBT cmp) {
		super.readPacketNBT(cmp);
	}
}
