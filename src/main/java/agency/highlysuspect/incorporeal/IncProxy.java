package agency.highlysuspect.incorporeal;

import net.minecraft.item.Item;

public interface IncProxy {
	default void setup() {}
	
	default Item.Properties soulCoreFrameIster(Item.Properties in) {
		return in;
	}
	
	class Server implements IncProxy {}
}
