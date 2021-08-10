package agency.highlysuspect.rhododendrite.item;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class RhoItems {
	public static BlockItem CORE;
	
	public static Item.Properties defaultProps() {
		return new Item.Properties().group(Tab.INSTANCE);
	}
	
	public static void register(RegistryEvent.Register<Item> event) {
		//TODO clean this shit up.
		// used to be in `final` fields but like, classloading order threw a wrench in that.
		// I should fix it in Incorporeal too
		
		CORE = new BlockItem(RhoBlocks.CORE, defaultProps());
		
		IForgeRegistry<Item> r = event.getRegistry();
		
		RhoBlocks.RHODODENDRITE.registerItems(r);
		
		regBlockItem(r, CORE);
	}
	
	public static void regBlockItem(IForgeRegistry<Item> r, BlockItem bi) {
		assert bi.getBlock().getRegistryName() != null;
		bi.setRegistryName(bi.getBlock().getRegistryName());
		r.register(bi);
	}
	
	private static class Tab extends ItemGroup {
		public static final Tab INSTANCE = new Tab();
		
		public Tab() { super(Rho.MODID); }
		
		@Override
		public ItemStack createIcon() {
			return new ItemStack(RhoBlocks.RHODODENDRITE.planks);
		}
		
		@Override
		public boolean hasSearchBar() {
			return true;
		}
	}
}
