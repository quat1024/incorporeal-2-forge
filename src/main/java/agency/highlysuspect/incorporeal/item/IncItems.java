package agency.highlysuspect.incorporeal.item;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import net.minecraft.item.*;
import net.minecraft.util.Util;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import vazkii.botania.common.Botania;
import vazkii.botania.common.item.block.ItemBlockSpecialFlower;

import java.util.EnumMap;
import java.util.Map;

public class IncItems {
	public static final CorporeaTicketItem CORPOREA_TICKET = new CorporeaTicketItem(defaultProps());
	public static final TicketConjurerItem TICKET_CONJURER = new TicketConjurerItem(defaultProps().stacksTo(1));
	public static final FracturedSpaceRodItem FRACTURED_SPACE_ROD = new FracturedSpaceRodItem(defaultProps().stacksTo(1));
	
	public static final Item SOUL_CORE_FRAME = new Item(Inc.proxy.soulCoreFrameIster(defaultProps()));
	
	public static final BlockItem CORPOREA_SOLIDIFIER = new BlockItem(IncBlocks.CORPOREA_SOLIDIFIER, defaultProps());
	public static final BlockItem RED_STRING_LIAR = new BlockItem(IncBlocks.RED_STRING_LIAR, defaultProps());
	public static final BlockItem FRAME_TINKERER = new BlockItem(IncBlocks.FRAME_TINKERER, defaultProps());
	public static final BlockItem CORPOREA_RETAINER_EVAPORATOR = new BlockItem(IncBlocks.CORPOREA_RETAINER_EVAPORATOR, defaultProps());
	
	public static final Map<DyeColor, BlockItem> UNSTABLE_CUBES = Util.make(new EnumMap<>(DyeColor.class), m ->
		IncBlocks.UNSTABLE_CUBES.forEach((color, block) ->
			m.put(color, new BlockItem(block, Inc.proxy.unstableCubeIster(defaultProps(), color)))));
	
	//What the fuck is this Forge......
	//thanks botania for having this little utility, Lol
	public static final BlockItem ENDER_SOUL_CORE = new BlockItem(IncBlocks.ENDER_SOUL_CORE, Botania.proxy.propertiesWithRenderer(defaultProps(), IncBlocks.ENDER_SOUL_CORE));
	public static final BlockItem CORPOREA_SOUL_CORE = new BlockItem(IncBlocks.CORPOREA_SOUL_CORE, Botania.proxy.propertiesWithRenderer(defaultProps(), IncBlocks.CORPOREA_SOUL_CORE));
	public static final BlockItem POTION_SOUL_CORE = new BlockItem(IncBlocks.POTION_SOUL_CORE, Botania.proxy.propertiesWithRenderer(defaultProps(), IncBlocks.POTION_SOUL_CORE));
	
	public static final BlockItem NATURAL_REPEATER = new BlockItem(IncBlocks.NATURAL_REPEATER, defaultProps());
	public static final BlockItem NATURAL_COMPARATOR = new BlockItem(IncBlocks.NATURAL_COMPARATOR, defaultProps());
	
	public static final ItemBlockSpecialFlower SANVOCALIA = new ItemBlockSpecialFlower(IncBlocks.SANVOCALIA, defaultProps());
	public static final ItemBlockSpecialFlower SMALL_SANVOCALIA = new ItemBlockSpecialFlower(IncBlocks.SMALL_SANVOCALIA, defaultProps());
	public static final ItemBlockSpecialFlower FLOATING_SANVOCALIA = new ItemBlockSpecialFlower(IncBlocks.FLOATING_SANVOCALIA, defaultProps());
	public static final ItemBlockSpecialFlower SMALL_FLOATING_SANVOCALIA = new ItemBlockSpecialFlower(IncBlocks.SMALL_FLOATING_SANVOCALIA, defaultProps());
	
	public static final ItemBlockSpecialFlower FUNNY = new ItemBlockSpecialFlower(IncBlocks.FUNNY, defaultProps());
	public static final ItemBlockSpecialFlower SMALL_FUNNY = new ItemBlockSpecialFlower(IncBlocks.SMALL_FUNNY, defaultProps());
	public static final ItemBlockSpecialFlower FLOATING_FUNNY = new ItemBlockSpecialFlower(IncBlocks.FLOATING_FUNNY, defaultProps());
	public static final ItemBlockSpecialFlower SMALL_FLOATING_FUNNY = new ItemBlockSpecialFlower(IncBlocks.SMALL_FLOATING_FUNNY, defaultProps());
	
	private static Item.Properties defaultProps() {
		return new Item.Properties().tab(Tab.INSTANCE);
	}
	
	public static void register(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> r = event.getRegistry();
		
		//items
		Inc.reg(r, "corporea_ticket", CORPOREA_TICKET);
		Inc.reg(r, "ticket_conjurer", TICKET_CONJURER);
		Inc.reg(r, "fractured_space_rod", FRACTURED_SPACE_ROD);
		Inc.reg(r, "soul_core_frame", SOUL_CORE_FRAME);
		
		//item blocks
		regBlockItem(r, CORPOREA_SOLIDIFIER);
		regBlockItem(r, RED_STRING_LIAR);
		regBlockItem(r, FRAME_TINKERER);
		regBlockItem(r, CORPOREA_RETAINER_EVAPORATOR);
		
		UNSTABLE_CUBES.values().forEach(i -> regBlockItem(r, i));
		
		regBlockItem(r, ENDER_SOUL_CORE);
		regBlockItem(r, CORPOREA_SOUL_CORE);
		regBlockItem(r, POTION_SOUL_CORE);
		
		regBlockItem(r, NATURAL_REPEATER);
		regBlockItem(r, NATURAL_COMPARATOR);
		
		//flowers
		regBlockItem(r, SANVOCALIA);
		regBlockItem(r, SMALL_SANVOCALIA);
		regBlockItem(r, FLOATING_SANVOCALIA);
		regBlockItem(r, SMALL_FLOATING_SANVOCALIA);
		
		regBlockItem(r, FUNNY);
		regBlockItem(r, SMALL_FUNNY);
		regBlockItem(r, FLOATING_FUNNY);
		regBlockItem(r, SMALL_FLOATING_FUNNY);
	}
	
	public static void regBlockItem(IForgeRegistry<Item> r, BlockItem bi) {
		assert bi.getBlock().getRegistryName() != null; //i dont know what the fuck "registry delegates" are, and i never will
		bi.setRegistryName(bi.getBlock().getRegistryName());
		r.register(bi);
	}
	
	private static class Tab extends ItemGroup {
		public static final Tab INSTANCE = new Tab();
		
		public Tab() {
			super(Inc.MODID);
			hideTitle();
			//noinspection deprecation (forge extends this, but this works fine too)
			setBackgroundSuffix(Inc.MODID + ".png");
		}
		
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(SOUL_CORE_FRAME);
		}
		
		@Override
		public boolean hasSearchBar() {
			return true;
		}
	}
}
