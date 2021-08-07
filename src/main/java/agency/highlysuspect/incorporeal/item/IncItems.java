package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.Init;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class IncItems {
	public static final CorporeaTicketItem CORPOREA_TICKET = new CorporeaTicketItem(defaultProps());
	
	public static final BlockItem CORPOREA_SOLIDIFIER = new BlockItem(IncBlocks.CORPOREA_SOLIDIFIER, defaultProps());
	
	private static Item.Properties defaultProps() {
		return new Item.Properties().group(Tab.INSTANCE);
	}
	
	public static void register(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();
		
		Init.reg(r, "corporea_ticket", CORPOREA_TICKET);
		
		Init.regBlockItem(r, CORPOREA_SOLIDIFIER);
	}
	
	private static class Tab extends ItemGroup {
		public static final Tab INSTANCE = new Tab();
		
		public Tab() {
			super(Init.MODID);
		}
		
		@Override
		public ItemStack createIcon() {
			return new ItemStack(CORPOREA_TICKET);
		}
		
		@Override
		public boolean hasSearchBar() {
			return true;
		}
	}
}
