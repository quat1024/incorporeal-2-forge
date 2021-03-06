package agency.highlysuspect.incorporeal.block.tile;

import com.mojang.authlib.GameProfile;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.api.corporea.CorporeaIndexRequestEvent;

import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class CorporeaSoulCoreTile extends AbstractSoulCoreTile {
	public CorporeaSoulCoreTile() {
		super(IncTileTypes.CORPOREA_SOUL_CORE);
	}
	
	@Override
	protected int getMaxMana() {
		return 0;
	}
	
	private static final UUID CHAT_SEND_UUID = UUID.fromString("00000000-0000-0000-0000-000000000001");
	public static void corporeaIndexRequestEvent(CorporeaIndexRequestEvent event) {
		if(event.getIndexSpark() == null) return;
		
		Set<UUID> coredUuids = CorporeaHelper.instance().getNodesOnNetwork(event.getIndexSpark()).stream()
			.map(node -> node.getWorld().getTileEntity(node.getPos()))
			.filter(t -> t instanceof CorporeaSoulCoreTile)
			.map(soul -> ((CorporeaSoulCoreTile) soul).getOwnerProfile())
			.filter(Objects::nonNull)
			.map(GameProfile::getId)
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
		
		if(!coredUuids.isEmpty() && !coredUuids.contains(event.getRequester().getUniqueID())) {
			event.getRequester().sendMessage(new TranslationTextComponent("incorporeal.no_soul_core").mergeStyle(TextFormatting.RED), CHAT_SEND_UUID);
			event.setCanceled(true);
		}
	}
}
