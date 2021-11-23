package agency.highlysuspect.incorporeal.item;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import vazkii.botania.api.corporea.CorporeaHelper;
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.item.Item.Properties;

public class TicketConjurerItem extends Item {
	public TicketConjurerItem(Properties properties) {
		super(properties);
	}
	
	public static final Map<Pattern, TileCorporeaIndex.IRegexStacker> patterns;
	static {
		Map<Pattern, TileCorporeaIndex.IRegexStacker> patterns_;
		try {
			Field f = TileCorporeaIndex.class.getDeclaredField("patterns");
			f.setAccessible(true);
			//noinspection unchecked
			patterns_ = (Map<Pattern, TileCorporeaIndex.IRegexStacker>) f.get(null);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
		
		patterns = patterns_;
	}
	
	public static void chatEvent(ServerChatEvent e) {
		//"based on code from TileCorporeaIndex" is what the 1.12 comment says, haven't really checked if the exact logic still matches too closely
		ServerPlayerEntity player = e.getPlayer();
		ItemStack thisOrderStack; //when you say "2 of this", what are you referring to
		
		if(player.getItemInHand(Hand.MAIN_HAND).getItem() instanceof TicketConjurerItem) {
			thisOrderStack = player.getItemInHand(Hand.OFF_HAND);
		} else if(player.getItemInHand(Hand.OFF_HAND).getItem() instanceof TicketConjurerItem) {
			thisOrderStack = player.getItemInHand(Hand.MAIN_HAND);
		} else return;
		
		String chatMessage = e.getMessage().toLowerCase(Locale.ROOT).trim();
		
		String itemName = "";
		int itemCount = 0;
		boolean foundMatch = false;
		
		for(Map.Entry<Pattern, TileCorporeaIndex.IRegexStacker> pair : patterns.entrySet()) {
			Pattern pattern = pair.getKey();
			Matcher matcher = pattern.matcher(chatMessage);
			if(matcher.matches()) {
				TileCorporeaIndex.IRegexStacker stacker = pair.getValue();
				itemCount = stacker.getCount(matcher);
				itemName = stacker.getName(matcher);
				foundMatch = true;
				//no 'break' - there also isn't one in Botania, idk why actually.
			}
		}
		
		itemName = itemName.toLowerCase(Locale.ROOT).trim();
		
		if(itemName.equals("this") || itemName.equals("these")) {
			if(thisOrderStack.isEmpty()) foundMatch = false; //you said "2 of this", but weren't actually holding anything else.
			else itemName = thisOrderStack.getHoverName().getString().toLowerCase(Locale.ROOT).trim();
		}
		
		if(foundMatch) {
			//conjure up a ticket!
			ItemStack ticket = IncItems.CORPOREA_TICKET.produceForRequest(CorporeaHelper.instance().createMatcher(itemName), itemCount);
			ItemHandlerHelper.giveItemToPlayer(player, ticket);
			
			e.setCanceled(true);
		}
	}
}
