package agency.highlysuspect.rhododendrite.client;

import agency.highlysuspect.rhododendrite.RhoProxy;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class RhoClient implements RhoProxy {
	@Override
	public void setup() {
		MatcherColors.registerBuiltinColors();
		
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
		
//		modBus.addListener((ColorHandlerEvent.Block event) -> {
//			BlockColors colors = event.getBlockColors();
//			//This looked like GARBAGE so i just colorized them in an image editor, sue me lol
//			//colors.register((state, world, pos, what) -> 0xfe7acc, RhoBlocks.RHODODENDRITE.leaves);
//		});
		
//		modBus.addListener((ColorHandlerEvent.Item event) -> {
//			ItemColors colors = event.getItemColors();
//			
//			//colors.register((p_getColor_1_, p_getColor_2_) -> 0xfe7acc, RhoBlocks.RHODODENDRITE.leaves);
//		});
	}
}
