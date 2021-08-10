package agency.highlysuspect.rhododendrite.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.rhododendrite.RhoProxy;
import agency.highlysuspect.rhododendrite.block.AwakenedLogBlock;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import agency.highlysuspect.rhododendrite.computer.CorePathTracing;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class RhoClient implements RhoProxy {
	@Override
	public void setup() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		//TODO uncomment when the sign is readded
//		modBus.addListener((FMLClientSetupEvent e) -> {
//			e.enqueueWork(() -> {
//				Atlases.addWoodType(RhoBlocks.RHODODENDRITE.woodType);
//			});
//		});
		
		modBus.addListener((ColorHandlerEvent.Block event) -> {
			BlockColors colors = event.getBlockColors();
			
			colors.register((state, world, pos, what) -> {
				int distance = state.get(AwakenedLogBlock.DISTANCE);
				int factor = MathHelper.floor(Inc.rangeRemap(distance, 1, CorePathTracing.MAX_RANGE, 0xFF, 0x99));
				return (factor << 16) | (factor << 8) | factor;
			}, RhoBlocks.AWAKENED_LOG);
		});
	}
}
