package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

//Implementation detail of LyingCorporeaNode - requesting with this matcher will just dump the entire contents of the corporea node into your claws.
//(I think there's been discussion about how real "wildcard corporea funnels" aren't that much fun, so, probably will remain an implementation detail.)
public class WildcardCorporeaRequestMatcher implements ICorporeaRequestMatcher {
	public static final WildcardCorporeaRequestMatcher INSTANCE = new WildcardCorporeaRequestMatcher();
	
	@Override
	public boolean test(ItemStack stack) {
		return !stack.isEmpty();
	}
	
	@Override
	public void writeToNBT(CompoundTag tag) {
		//Nothing to do!
	}
	
	@Override
	public Component getRequestName() {
		return new TranslatableComponent("incorporeal.request.anything");
	}
	
	//no equals/hashcode since it's a singleton
}
