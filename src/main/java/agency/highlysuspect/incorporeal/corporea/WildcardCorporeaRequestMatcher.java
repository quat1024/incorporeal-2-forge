package agency.highlysuspect.incorporeal.corporea;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
	public void writeToNBT(CompoundNBT tag) {
		//Nothing to do!
	}
	
	@Override
	public ITextComponent getRequestName() {
		return new TranslationTextComponent("incorporeal.request.anything");
	}
}
