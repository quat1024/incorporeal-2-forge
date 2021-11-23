package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

//mainly used in Rhododendrite, put in here so i have something to assign to SolidifiedRequest.EMPTY
public class EmptyCorporeaRequestMatcher implements ICorporeaRequestMatcher {
	public static final EmptyCorporeaRequestMatcher INSTANCE = new EmptyCorporeaRequestMatcher();
	
	@Override
	public boolean test(ItemStack stack) {
		return false;
	}
	
	@Override
	public void writeToNBT(CompoundTag tag) {
		//Nope!
	}
	
	@Override
	public Component getRequestName() {
		return new TranslatableComponent("incorporeal.empty_matcher");
	}
}
