package agency.highlysuspect.incorporeal;

import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;

public interface IncProxy {
	default void setup() {}
	
	default Item.Properties soulCoreFrameIster(Item.Properties in) {
		return in;
	}
	
	default Item.Properties unstableCubeIster(Item.Properties in, DyeColor color) {
		return in;
	}
	
	class Server implements IncProxy {}
}
