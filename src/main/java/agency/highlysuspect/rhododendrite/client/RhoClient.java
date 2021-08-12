package agency.highlysuspect.rhododendrite.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.rhododendrite.RhoProxy;
import agency.highlysuspect.rhododendrite.block.AwakenedLogBlock;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import agency.highlysuspect.rhododendrite.computer.CorePathTracing;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class RhoClient implements RhoProxy {
	@Override
	public void setup() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modBus.addListener((FMLClientSetupEvent e) -> {
			//TODO uncomment when the sign is readded
//			e.enqueueWork(() -> {
//				Atlases.addWoodType(RhoBlocks.RHODODENDRITE.woodType);
//			});
			
			RenderTypeLookup.setRenderLayer(RhoBlocks.CORE, RenderType.getTranslucent());
			RenderTypeLookup.setRenderLayer(RhoBlocks.AWAKENED_LOG, RenderType.getCutout());
			
			RenderTypeLookup.setRenderLayer(RhoBlocks.RHODODENDRITE.leaves, RenderType.getCutout());
			RenderTypeLookup.setRenderLayer(RhoBlocks.RHODODENDRITE.sapling, RenderType.getCutout());
		});
		
		modBus.addListener((ColorHandlerEvent.Block event) -> {
			BlockColors colors = event.getBlockColors();
			
			colors.register((state, world, pos, what) -> {
				int distance = state.get(AwakenedLogBlock.DISTANCE);
				int factor = MathHelper.floor(Inc.rangeRemap(distance, 1, CorePathTracing.MAX_RANGE, 0xFF, 0x99));
				return (factor << 16) | (factor << 8) | factor;
			}, RhoBlocks.AWAKENED_LOG);
			
			//This looked like GARBAGE so i just colorized them in an image editor, sue me lol
			//colors.register((state, world, pos, what) -> 0xfe7acc, RhoBlocks.RHODODENDRITE.leaves);
		});
		
		modBus.addListener((ColorHandlerEvent.Item event) -> {
			ItemColors colors = event.getItemColors();
			
			//colors.register((p_getColor_1_, p_getColor_2_) -> 0xfe7acc, RhoBlocks.RHODODENDRITE.leaves);
		});
	}
}
