package agency.highlysuspect.incorporeal;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class IncMixinConnector implements IMixinConnector {
	@Override
	public void connect() {
		Mixins.addConfigurations("incorporeal.mixins.json");
	}
}
