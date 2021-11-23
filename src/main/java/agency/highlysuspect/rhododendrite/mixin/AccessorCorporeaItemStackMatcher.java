package agency.highlysuspect.rhododendrite.mixin;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vazkii.botania.common.impl.corporea.CorporeaItemStackMatcher;

@Mixin(CorporeaItemStackMatcher.class)
public interface AccessorCorporeaItemStackMatcher {
	@Accessor(remap = false, value = "match")
	ItemStack rho$getMatcher();
}
