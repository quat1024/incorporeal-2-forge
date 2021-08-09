package agency.highlysuspect.rhododendrite.block.tile;

import agency.highlysuspect.rhododendrite.computer.DataTypes;
import agency.highlysuspect.rhododendrite.computer.Fragment;
import net.minecraft.nbt.CompoundNBT;
import vazkii.botania.common.block.tile.TileMod;

public class CoreTile extends TileMod {
	public CoreTile() {
		super(RhoTileTypes.CORE);
	}
	
	private Fragment<?> fragment = Fragment.EMPTY;
	
	@Override
	public void writePacketNBT(CompoundNBT cmp) {
		super.writePacketNBT(cmp);
		cmp.put("Fragment", fragment.toNbt(DataTypes.REGISTRY));
	}
	
	@Override
	public void readPacketNBT(CompoundNBT cmp) {
		super.readPacketNBT(cmp);
		fragment = Fragment.fromNbtOrEmpty(DataTypes.REGISTRY, cmp.getCompound("Fragment"));
	}
}
