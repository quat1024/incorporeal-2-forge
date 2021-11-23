package agency.highlysuspect.incorporeal.entity;

import agency.highlysuspect.incorporeal.block.tile.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.block.tile.PotionSoulCoreTile;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;

import java.util.Collections;
import java.util.Optional;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;

public class PotionSoulCoreCollectorEntity extends LivingEntity {
	public PotionSoulCoreCollectorEntity(EntityType<? extends LivingEntity> type, Level world) {
		super(type, world);
		
		setInvulnerable(true);
		setNoGravity(true);
	}
	
	public PotionSoulCoreCollectorEntity(Level world, BlockPos pos) {
		this(IncEntityTypes.POTION_SOUL_CORE_COLLECTOR, world);
		setPos(pos.getX() + .5d, pos.getY() + 0.005d, pos.getZ() + .5d);
	}
	
	public static void attributeEvent(EntityAttributeCreationEvent e) {
		e.put(IncEntityTypes.POTION_SOUL_CORE_COLLECTOR, LivingEntity.createLivingAttributes()
			.add(Attributes.MAX_HEALTH, 200f)
			.build());
	}
	
	public static void healEvent(LivingHealEvent e) {
		if(e.getEntityLiving() instanceof PotionSoulCoreCollectorEntity) {
			boolean didItWork = ((PotionSoulCoreCollectorEntity) e.getEntityLiving()).onHeal(e.getAmount());
			//if(didItWork) e.setCanceled(true);
		}
	}
	
	public static void attackEvent(LivingAttackEvent e) {
		if(e.getEntityLiving() instanceof PotionSoulCoreCollectorEntity) {
			boolean didItWork = ((PotionSoulCoreCollectorEntity) e.getEntityLiving()).onAttack(e.getSource(), e.getAmount());
			//if(didItWork) e.setCanceled(true);
		}
	}
	
	@Override
	public MobType getMobType() {
		return MobType.UNDEAD; //With it being a soul and all, like, ...
	}
	
	private Optional<Pair<PotionSoulCoreTile, ServerPlayer>> find() {
		PotionSoulCoreTile tile = IncBlockEntityTypes.POTION_SOUL_CORE.getBlockEntity(level, blockPosition());
		if(tile == null) { remove(); return Optional.empty(); }
		
		Optional<ServerPlayer> player = tile.findPlayer();
		if(!player.isPresent()) { remove(); return Optional.empty(); }
		
		return Optional.of(Pair.of(tile, player.get()));
	}
	
	@Override
	public void tick() {
		//This entity doesn't call super.tick() so i post the event myself
		//Is this a bad idea???? Yes
		if(net.minecraftforge.common.ForgeHooks.onLivingUpdate(this)) return;
		if(level == null || level.isClientSide) return;
		
		setDeltaMovement(0, 0, 0);
		yRot = 0;
		xRot = 0;
		yHeadRot = 0;
		setHealth(getMaxHealth());
		setAirSupply(getMaxAirSupply());
		setPos(Math.floor(getX()) + .5d, Math.floor(getY()) + 0.005d, Math.floor(getZ()) + .5d);
		
		find().ifPresent(pair -> {
			//Transfer long-lasting potion effects to the player
			for(MobEffectInstance effect : getActiveEffects()) {
				pair.getSecond().addEffect(effect);
				pair.getFirst().drainMana(200);
			}
			
			//Clean them off myself
			removeAllEffects(); //the "right" way, fires events and removes attr modifiers and stuff
			getActiveEffectsMap().clear(); //if there's any left over, kill them the hard way
		});
	}
	
	private boolean onHeal(float howMuch) {
		if(level.isClientSide) return true;
		Optional<Pair<PotionSoulCoreTile, ServerPlayer>> found = find();
		
		found.ifPresent(pair -> {
			pair.getSecond().heal(howMuch);
			pair.getFirst().drainMana(200);
		});
		
		return found.isPresent();
	}
	
	private boolean onAttack(DamageSource source, float howMuch) {
		if(level.isClientSide) return true;
		Optional<Pair<PotionSoulCoreTile, ServerPlayer>> found = find();
		
		if(found.isPresent()) {
			boolean happened = found.get().getSecond().hurt(source, howMuch);
			if(happened) {
				found.get().getFirst().drainMana(200);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void aiStep() {
		//No-op
	}
	
	@Override
	public Iterable<ItemStack> getArmorSlots() {
		return Collections.emptyList();
	}
	
	@Override
	public ItemStack getItemBySlot(EquipmentSlot slot) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
		//Noh
	}
	
	@Override
	public boolean startRiding(Entity entityIn) {
		return false; //No
	}
	
	@Override
	public boolean startRiding(Entity entityIn, boolean force) {
		return false; //Absolutely not
	}
	
	@Override
	protected boolean canRide(Entity entityIn) {
		return false; //You may not
	}
	
	@Override
	public boolean isPushable() {
		return false;
	}
	
	@Override
	public HumanoidArm getMainArm() {
		return HumanoidArm.LEFT;
	}
}
