package agency.highlysuspect.incorporeal.entity;

import agency.highlysuspect.incorporeal.Inc;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;

public class IncEntityTypes {
	public static final EntityType<FracturedSpaceCollectorEntity> FRACTURED_SPACE_COLLECTOR = EntityType.Builder.<FracturedSpaceCollectorEntity>of(FracturedSpaceCollectorEntity::new, MobCategory.MISC)
		.sized(0, 0)
		.fireImmune()
		.clientTrackingRange(10)
		.updateInterval(5)
		.build("");
	
	public static final EntityType<PotionSoulCoreCollectorEntity> POTION_SOUL_CORE_COLLECTOR = EntityType.Builder.<PotionSoulCoreCollectorEntity>of(PotionSoulCoreCollectorEntity::new, MobCategory.MISC)
		.sized(0.99f, 0.99f)
		.fireImmune()
		.clientTrackingRange(10)
		.updateInterval(40)
		.build("");
	
	public static void register() {
		Registry<EntityType<?>> r = Registry.ENTITY_TYPE;
		
		Inc.reg(r, "fractured_space_collector", FRACTURED_SPACE_COLLECTOR);
		Inc.reg(r, "potion_soul_core_collector", POTION_SOUL_CORE_COLLECTOR);
	}
}
