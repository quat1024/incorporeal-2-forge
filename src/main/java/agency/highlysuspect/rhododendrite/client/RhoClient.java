package agency.highlysuspect.rhododendrite.client;

import agency.highlysuspect.rhododendrite.RhoProxy;
import agency.highlysuspect.rhododendrite.block.RhoBlocks;
import net.minecraft.client.renderer.Atlases;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class RhoClient implements RhoProxy {
	@Override
	public void setup() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modBus.addListener((FMLClientSetupEvent e) -> {
			e.enqueueWork(() -> {
				Atlases.addWoodType(RhoBlocks.RHODODENDRITE.woodType);
			});
		});
	}
}
