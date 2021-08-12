package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;

//mainly used in Rhododendrite, put in here so i have something to assign to SolidifiedRequest.EMPTY
public class EmptyCorporeaRequestMatcher implements ICorporeaRequestMatcher {
	public static final EmptyCorporeaRequestMatcher INSTANCE = new EmptyCorporeaRequestMatcher();
	
	@Override
	public boolean test(ItemStack stack) {
		return false;
	}
	
	@Override
	public void writeToNBT(CompoundNBT tag) {
		//Nope!
	}
	
	@Override
	public ITextComponent getRequestName() {
		return new TranslationTextComponent("incorporeal.empty_matcher");
	}
}
