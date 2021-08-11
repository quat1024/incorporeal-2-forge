package agency.highlysuspect.rhododendrite.item;

import agency.highlysuspect.rhododendrite.Rho;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class RhoItems {
	//dont put item fields directly in here because classloading makes it blow up
	//todo i should fix that in incorporeal too... and probably like, half my mods...
	
	public static Item.Properties defaultProps() {
		return new Item.Properties().group(Tab.INSTANCE);
	}
	
	public static void register(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();
		
		RhoBlocks.RHODODENDRITE.registerItems(r);
		Rho.simpleBlockItems(r,
			RhoBlocks.CORE,
			RhoBlocks.AWAKENED_LOG,
			RhoBlocks.TEST1,
			RhoBlocks.TEST2,
			RhoBlocks.PUSH,
			RhoBlocks.PULL,
			RhoBlocks.REORIENT,
			RhoBlocks.DUP,
			RhoBlocks.PUSH_ZERO,
			RhoBlocks.PUSH_ONE,
			RhoBlocks.ADD,
			RhoBlocks.SUBTRACT,
			RhoBlocks.MULTIPLY,
			RhoBlocks.DIVIDE,
			RhoBlocks.REMAINDER
		);
	}
	
//	public static void regBlockItem(IForgeRegistry<Item> r, BlockItem bi) {
//		assert bi.getBlock().getRegistryName() != null;
//		bi.setRegistryName(bi.getBlock().getRegistryName());
//		r.register(bi);
//	}
	
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
