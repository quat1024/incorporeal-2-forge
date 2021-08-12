package agency.highlysuspect.incorporeal;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class IncConfig {
	public IncConfig(ForgeConfigSpec.Builder builder) {
		builder.push("silly");
		everyoneHearsSanvocalia = builder.comment(
			"Ok so the Sanvocalia functional flower has an easter-egg, where if it tries to request an item but isn't next to any corporea indices,",
			"it just dumps the request in the chat. This is a nod to when players try to use a Corporea Index but are standing too far away from it.",
			"...I first made this mod in like 2019, I'm sure it was funny then.",
			"Anyway, if this is 'true', everyone will see the message. If it's 'false', only the player who placed the flower will see the message.",
			"(Regardless, a line will be posted to the game log every time a message is sent in this manner, including the coordinates of the flower.)"
		).define("everyoneHearsSanvocalia", true);
		builder.pop();
	}
	
	public static final IncConfig INST;
	public static final ForgeConfigSpec SPEC;
	
	static {
		final Pair<IncConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(IncConfig::new);
		INST = specPair.getLeft();
		SPEC = specPair.getRight();
	}
	
	public final ForgeConfigSpec.BooleanValue everyoneHearsSanvocalia;
}
