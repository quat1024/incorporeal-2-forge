package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.IncProxy;
import agency.highlysuspect.incorporeal.Init;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.block.tile.IncTileTypes;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
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
			
			RenderTypeLookup.setRenderLayer(IncBlocks.SANVOCALIA, RenderType.getCutout());
			RenderTypeLookup.setRenderLayer(IncBlocks.SMALL_SANVOCALIA, RenderType.getCutout());
			RenderTypeLookup.setRenderLayer(IncBlocks.FLOATING_SANVOCALIA, RenderType.getCutout());
			RenderTypeLookup.setRenderLayer(IncBlocks.SMALL_FLOATING_SANVOCALIA, RenderType.getCutout());
			
			RenderTypeLookup.setRenderLayer(IncBlocks.FUNNY, RenderType.getCutout());
			RenderTypeLookup.setRenderLayer(IncBlocks.SMALL_FUNNY, RenderType.getCutout());
			RenderTypeLookup.setRenderLayer(IncBlocks.FLOATING_FUNNY, RenderType.getCutout());
			RenderTypeLookup.setRenderLayer(IncBlocks.SMALL_FLOATING_FUNNY, RenderType.getCutout());
		});
		
		modBus.addListener((ModelRegistryEvent e) -> {
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.RED_STRING_LIAR, RenderTileRedString::new);
			
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.ENDER_SOUL_CORE, d -> new SoulCoreRenderer(d, Init.id("textures/entity/ender_soul_core.png")));
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.CORPOREA_SOUL_CORE, d -> new SoulCoreRenderer(d, Init.id("textures/entity/corporea_soul_core.png")));
			
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.SANVOCALIA_BIG, RenderTileSpecialFlower::new);
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.SANVOCALIA_SMALL, RenderTileSpecialFlower::new);
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.FUNNY_BIG, RenderTileSpecialFlower::new);
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.FUNNY_SMALL, RenderTileSpecialFlower::new);
		});
	}
}
