package agency.highlysuspect.incorporeal.mixin;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.common.block.tile.corporea.TileCorporeaRetainer;

import java.util.Map;
import java.util.function.Function;

@Mixin(TileCorporeaRetainer.class)
public interface TileCorporeaRetainerAccessor {
	@Accessor(value = "corporeaMatcherDeserializers", remap = false)
	static Map<ResourceLocation, Function<CompoundNBT, ? extends ICorporeaRequestMatcher>> inc$getDeserializers() {
		throw new IllegalStateException("oh no");
	}
	
	@Accessor(value = "corporeaMatcherSerializers", remap = false)
	static Map<Class<? extends ICorporeaRequestMatcher>, ResourceLocation> inc$getSerializers() {
		throw new IllegalStateException("uh oh");
	}
}
