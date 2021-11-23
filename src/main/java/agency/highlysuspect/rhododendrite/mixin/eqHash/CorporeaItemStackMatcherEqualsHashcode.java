package agency.highlysuspect.rhododendrite.mixin.eqHash;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.botania.common.impl.corporea.CorporeaItemStackMatcher;

import java.util.Objects;

@Mixin(CorporeaItemStackMatcher.class)
public class CorporeaItemStackMatcherEqualsHashcode {
	@Shadow(remap = false) @Final private ItemStack match;
	@Shadow(remap = false) @Final private boolean checkNBT;
	
	@Override
	@Intrinsic
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		
		CorporeaItemStackMatcherEqualsHashcode that = (CorporeaItemStackMatcherEqualsHashcode) o;
		
		if(checkNBT != that.checkNBT) return false;
		//return ItemStack.areItemStacksEqual(match, that.match);
		
		//The count on this ItemStack has nothing to do with the stack size of the request, but I don't see anything in Botania that fixes the count to 1.
		//An alternative fix could be forcing the stack size to 1 via mixin, so areItemStacksEqual would be appropriate.
		//Methods like ItemHandlerHelper.canItemStacksStack aren't appropriate, since it thinks two empty ItemStacks are different, for some reason.
		return match.getItem() == that.match.getItem() && Objects.equals(match.getTag(), that.match.getTag()) && match.areCapsCompatible(that.match);
	}
	
	@Override
	@Intrinsic
	public int hashCode() {
		return Objects.hash(match.getItem().getRegistryName(), match.getTag(), checkNBT);
	}
}
