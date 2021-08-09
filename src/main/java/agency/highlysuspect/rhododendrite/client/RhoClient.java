package agency.highlysuspect.rhododendrite.client;

import agency.highlysuspect.rhododendrite.RhoProxy;
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
	}
}
