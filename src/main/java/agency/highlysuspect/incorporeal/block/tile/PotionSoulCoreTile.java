package agency.highlysuspect.incorporeal.block.tile;

import agency.highlysuspect.incorporeal.entity.PotionSoulCoreCollectorEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

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
		if(world == null || world.isRemote) return;
		
		List<PotionSoulCoreCollectorEntity> collectors = world.getEntitiesWithinAABB(PotionSoulCoreCollectorEntity.class, new AxisAlignedBB(pos));
		Optional<ServerPlayerEntity> playerOp = findPlayer();
		
		if(collectors.size() >= 2 || !playerOp.isPresent()) {
			collectors.forEach(Entity::remove);
			collectors.clear();
		}
		
		if(collectors.isEmpty() && playerOp.isPresent()) {
			world.addEntity(new PotionSoulCoreCollectorEntity(world, getPos()));
		}
	}
	
	@Override
	public void onExpire() {
		super.onExpire();
		if(world != null)	world.getEntitiesWithinAABB(PotionSoulCoreCollectorEntity.class, new AxisAlignedBB(pos)).forEach(Entity::remove);
	}
}
