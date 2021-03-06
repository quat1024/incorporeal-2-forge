package agency.highlysuspect.incorporeal.entity;

import agency.highlysuspect.incorporeal.Inc;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class IncEntityTypes {
	public static final EntityType<FracturedSpaceCollectorEntity> FRACTURED_SPACE_COLLECTOR = EntityType.Builder.<FracturedSpaceCollectorEntity>create(FracturedSpaceCollectorEntity::new, EntityClassification.MISC)
		.size(0, 0)
		.immuneToFire()
		.trackingRange(10)
		.setUpdateInterval(5)
		.setShouldReceiveVelocityUpdates(false)
		.build("");
	
	public static final EntityType<PotionSoulCoreCollectorEntity> POTION_SOUL_CORE_COLLECTOR = EntityType.Builder.<PotionSoulCoreCollectorEntity>create(PotionSoulCoreCollectorEntity::new, EntityClassification.MISC)
		.size(0.99f, 0.99f)
		.immuneToFire()
		.trackingRange(10)
		.setUpdateInterval(40)
		.setShouldReceiveVelocityUpdates(false)
		.build("");
	
	public static void register(RegistryEvent.Register<EntityType<?>> event) {
		IForgeRegistry<EntityType<?>> r = event.getRegistry();
		
		Inc.reg(r, "fractured_space_collector", FRACTURED_SPACE_COLLECTOR);
		Inc.reg(r, "potion_soul_core_collector", POTION_SOUL_CORE_COLLECTOR);
	}
}
