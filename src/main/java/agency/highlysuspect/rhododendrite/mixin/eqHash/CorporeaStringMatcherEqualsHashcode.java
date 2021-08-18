package agency.highlysuspect.rhododendrite.mixin.eqHash;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import vazkii.botania.common.impl.corporea.CorporeaStringMatcher;

import java.util.Arrays;

@Mixin(CorporeaStringMatcher.class)
public class CorporeaStringMatcherEqualsHashcode {
	@Shadow(remap = false) @Final private String[] expression;
	
	@Override
	@Intrinsic
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		
		CorporeaStringMatcherEqualsHashcode that = (CorporeaStringMatcherEqualsHashcode) o;
		
		return Arrays.equals(expression, that.expression);
	}
	
	@Override
	@Intrinsic
	public int hashCode() {
		return Arrays.hashCode(expression);
	}
}
