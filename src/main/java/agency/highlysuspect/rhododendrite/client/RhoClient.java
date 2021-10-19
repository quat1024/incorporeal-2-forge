package agency.highlysuspect.rhododendrite.client;

import agency.highlysuspect.rhododendrite.RhoProxy;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import agency.highlysuspect.rhododendrite.block.tile.RhoTileTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class RhoClient implements RhoProxy {
	@Override
	public void setup() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modBus.addListener((FMLClientSetupEvent e) -> {
			//TODO (issue #1) uncomment when the sign is readded
//			e.enqueueWork(() -> {
//				Atlases.addWoodType(RhoBlocks.RHODODENDRITE.woodType);
//			});
			
			RenderTypeLookup.setRenderLayer(RhoBlocks.CELL, RenderType.getTranslucent());
			RenderTypeLookup.setRenderLayer(RhoBlocks.OP, RenderType.getTranslucent());
			RenderTypeLookup.setRenderLayer(RhoBlocks.FUNNEL, RenderType.getTranslucent());
			
			RenderTypeLookup.setRenderLayer(RhoBlocks.RHODODENDRITE.leaves, RenderType.getCutout());
			RenderTypeLookup.setRenderLayer(RhoBlocks.RHODODENDRITE.sapling, RenderType.getCutout());
		});
		
		modBus.addListener((ModelRegistryEvent e) -> {
			ClientRegistry.bindTileEntityRenderer(RhoTileTypes.CELL, CellTileRenderer::new);
			ClientRegistry.bindTileEntityRenderer(RhoTileTypes.OP, OpTileRenderer::new);
			ClientRegistry.bindTileEntityRenderer(RhoTileTypes.FUNNEL, FunnelTileRenderer::new);
		});
	}
}
