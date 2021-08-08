package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.IncProxy;
import agency.highlysuspect.incorporeal.Init;
import agency.highlysuspect.incorporeal.block.tile.IncTileTypes;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.item.ItemModelsProperties;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.botania.client.render.tile.RenderTileRedString;
import vazkii.botania.client.render.tile.RenderTileSpecialFlower;

public class IncClient implements IncProxy {
	@Override
	public void setup() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modBus.addListener((FMLClientSetupEvent e) -> {
			e.enqueueWork(() -> {
				ItemModelsProperties.registerProperty(IncItems.CORPOREA_TICKET, Init.id("written_ticket"), (stack, world, ent) -> IncItems.CORPOREA_TICKET.hasRequest(stack) ? 1 : 0);
			});
		});
		
		modBus.addListener((ModelRegistryEvent e) -> {
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.RED_STRING_LIAR, RenderTileRedString::new);
			
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.SANVOCALIA_BIG, RenderTileSpecialFlower::new);
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.SANVOCALIA_SMALL, RenderTileSpecialFlower::new);
		});
	}
}
