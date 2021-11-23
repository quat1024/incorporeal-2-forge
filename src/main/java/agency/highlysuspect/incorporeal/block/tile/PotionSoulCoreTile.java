package agency.highlysuspect.incorporeal.block.tile;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.entity.PotionSoulCoreCollectorEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Optional;

public class PotionSoulCoreTile extends AbstractSoulCoreTile {
	public PotionSoulCoreTile() {
		super(IncTileTypes.POTION_SOUL_CORE);
	}
	
	@Override
	protected int getMaxMana() {
		return 3000;
	}
	
	@Override
	public void tick() {
		super.tick();
		if(level == null || level.isClientSide) return;
		
		List<PotionSoulCoreCollectorEntity> collectors = level.getEntitiesOfClass(PotionSoulCoreCollectorEntity.class, new AABB(worldPosition));
		Optional<ServerPlayer> playerOp = findPlayer();
		
		if(collectors.size() >= 2 || !playerOp.isPresent()) {
			collectors.forEach(Entity::remove);
			collectors.clear();
		}
		
		if(collectors.isEmpty() && playerOp.isPresent()) {
			level.addFreshEntity(new PotionSoulCoreCollectorEntity(level, getBlockPos()));
		}
	}
	
	@Override
	public void onExpire() {
		super.onExpire();
		if(level != null)	level.getEntitiesOfClass(PotionSoulCoreCollectorEntity.class, new AABB(worldPosition)).forEach(Entity::remove);
	}
}
