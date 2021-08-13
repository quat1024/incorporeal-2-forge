package agency.highlysuspect.incorporeal.entity;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.tile.IncTileTypes;
import agency.highlysuspect.incorporeal.block.tile.PotionSoulCoreTile;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;

import java.util.Collections;
import java.util.Optional;

public class PotionSoulCoreCollectorEntity extends LivingEntity {
	public PotionSoulCoreCollectorEntity(EntityType<? extends LivingEntity> type, World world) {
		super(type, world);
		
		setInvulnerable(true);
		setNoGravity(true);
	}
	
	public PotionSoulCoreCollectorEntity(World world, BlockPos pos) {
		this(IncEntityTypes.POTION_SOUL_CORE_COLLECTOR, world);
		setPosition(pos.getX() + .5d, pos.getY() + 0.005d, pos.getZ() + .5d);
	}
	
	public static void attributeEvent(EntityAttributeCreationEvent e) {
		e.put(IncEntityTypes.POTION_SOUL_CORE_COLLECTOR, LivingEntity.registerAttributes()
			.createMutableAttribute(Attributes.MAX_HEALTH, 200f)
			.create());
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
	public CreatureAttribute getCreatureAttribute() {
		return CreatureAttribute.UNDEAD; //With it being a soul and all, like, ...
	}
	
	private Optional<Pair<PotionSoulCoreTile, ServerPlayerEntity>> find() {
		PotionSoulCoreTile tile = IncTileTypes.POTION_SOUL_CORE.getIfExists(world, getPosition());
		if(tile == null) { remove(); return Optional.empty(); }
		
		Optional<ServerPlayerEntity> player = tile.findPlayer();
		if(!player.isPresent()) { remove(); return Optional.empty(); }
		
		return Optional.of(Pair.of(tile, player.get()));
	}
	
	@Override
	public void tick() {
		//This entity doesn't call super.tick() so i post the event myself
		//Is this a bad idea???? Yes
		if(net.minecraftforge.common.ForgeHooks.onLivingUpdate(this)) return;
		if(world == null || world.isRemote) return;
		
		setMotion(0, 0, 0);
		rotationYaw = 0;
		rotationPitch = 0;
		rotationYawHead = 0;
		setHealth(getMaxHealth());
		setAir(getMaxAir());
		setPosition(Math.floor(getPosX()) + .5d, Math.floor(getPosY()) + 0.005d, Math.floor(getPosZ()) + .5d);
		
		find().ifPresent(pair -> {
			//Transfer long-lasting potion effects to the player
			for(EffectInstance effect : getActivePotionEffects()) {
				Inc.LOGGER.info("transferring potion effect " + effect);
				pair.getSecond().addPotionEffect(effect);
				pair.getFirst().drainMana(200);
			}
			
			//Clean them off myself
			clearActivePotions(); //the "right" way, fires events and removes attr modifiers and stuff
			getActivePotionMap().clear(); //if there's any left over, kill them the hard way
		});
	}
	
	private boolean onHeal(float howMuch) {
		if(world.isRemote) return true;
		Optional<Pair<PotionSoulCoreTile, ServerPlayerEntity>> found = find();
		
		found.ifPresent(pair -> {
			Inc.LOGGER.info("onHeal");
			pair.getSecond().heal(howMuch);
			pair.getFirst().drainMana(200);
		});
		
		return found.isPresent();
	}
	
	private boolean onAttack(DamageSource source, float howMuch) {
		if(world.isRemote) return true;
		Optional<Pair<PotionSoulCoreTile, ServerPlayerEntity>> found = find();
		
		if(found.isPresent()) {
			Inc.LOGGER.info("onAttack");
			boolean happened = found.get().getSecond().attackEntityFrom(source, howMuch);
			if(happened) {
				found.get().getFirst().drainMana(200);
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void livingTick() {
		//No-op
	}
	
	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		return Collections.emptyList();
	}
	
	@Override
	public ItemStack getItemStackFromSlot(EquipmentSlotType slot) {
		return ItemStack.EMPTY;
	}
	
	@Override
	public void setItemStackToSlot(EquipmentSlotType slot, ItemStack stack) {
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
	protected boolean canBeRidden(Entity entityIn) {
		return false; //You may not
	}
	
	@Override
	public boolean canBePushed() {
		return false;
	}
	
	@Override
	public HandSide getPrimaryHand() {
		return HandSide.LEFT;
	}
}
