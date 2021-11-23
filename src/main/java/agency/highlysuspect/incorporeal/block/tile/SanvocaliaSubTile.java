package agency.highlysuspect.incorporeal.block.tile;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncConfig;
import agency.highlysuspect.incorporeal.IncNetwork;
import agency.highlysuspect.incorporeal.corporea.IndexRequestFaker;
import agency.highlysuspect.incorporeal.corporea.NearbyIndicesFinder;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex;

import javax.annotation.Nullable;
import java.util.*;

public class SanvocaliaSubTile extends TileEntityFunctionalFlower {
	public SanvocaliaSubTile(int radius, BlockEntityType<?> type) {
		super(type);
		this.radius = radius;
	}
	
	public static SanvocaliaSubTile big() {
		return new SanvocaliaSubTile(3, IncBlockEntityTypes.SANVOCALIA_BIG);
	}
	
	public static SanvocaliaSubTile small() {
		return new SanvocaliaSubTile(1, IncBlockEntityTypes.SANVOCALIA_SMALL);
	}
	
	private static final UUID CHAT_SEND_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private final int radius;
	
	private int cooldown;
	
	private UUID placerUuid = null;
	private Component displayName = null;
	
	@Override
	public void tickFlower() {
		super.tickFlower();
		if(level == null || level.isClientSide) return;
		
		if(cooldown > 0) {
			cooldown--;
			return;
		}
		
		BlockPos pos = getEffectivePos(); //name shadow
		
		AABB itemDetectionBox = new AABB(pos.offset(-radius, 0, -radius), pos.offset(radius + 1, 1, radius + 1));
		List<ItemEntity> nearbyTicketEnts = level.getEntitiesOfClass(ItemEntity.class, itemDetectionBox, ent -> {
			if(ent == null || !ent.isAlive()) return false;
			ItemStack stack = ent.getItem();
			return stack.getItem() == IncItems.CORPOREA_TICKET && IncItems.CORPOREA_TICKET.getRequest(stack).isPresent();
		});
		if(nearbyTicketEnts.isEmpty()) return;
		
		//Pick one at random and get its request
		ItemEntity ticketEnt = nearbyTicketEnts.get(level.random.nextInt(nearbyTicketEnts.size()));
		
		@SuppressWarnings("OptionalGetWithoutIsPresent") //i checked above
		SolidifiedRequest request = IncItems.CORPOREA_TICKET.getRequest(ticketEnt.getItem()).get();
		
		List<TileCorporeaIndex> nearbyIndices = NearbyIndicesFinder.findNearbyIndicesReflect(level, pos, radius);
		if(nearbyIndices.isEmpty()) {
			//A nod to when people write in chat while accidentally standing too far from the corporea index
			MinecraftServer server = level.getServer();
			if(server != null && getMana() >= 100) {
				TranslatableComponent msg = new TranslatableComponent("chat.type.text", displayName == null ? new TranslatableComponent("block.incorporeal.sanvocalia") : displayName, request.toText());
				
				Inc.LOGGER.info("Sanvocalia chat message triggered at {} in dimension {}", pos.toShortString(), level.dimension().location());
				if(IncConfig.INST.everyoneHearsSanvocalia.get()) {
					server.getPlayerList().broadcastMessage(msg, ChatType.CHAT, CHAT_SEND_UUID);
				} else {
					ServerPlayer placer = server.getPlayerList().getPlayer(placerUuid); 
					if(placer != null) placer.sendMessage(msg, CHAT_SEND_UUID);
				}
				
				addMana(-100);
				consumeTicket(ticketEnt, null);
				cooldown = 3;
				sync();
			}
		} else {
			boolean didAnything = false;
			Set<BlockPos> indexPositions = new HashSet<>();
			
			for(TileCorporeaIndex index : nearbyIndices) {
				if(getMana() < 20) break;
				
				indexPositions.add(index.getBlockPos());
				IndexRequestFaker.requestAtIndex(level, request, index.getSpark(), index.getBlockPos());
				
				addMana(-20);
				didAnything = true;
			}
			
			if(didAnything) {
				consumeTicket(ticketEnt, indexPositions);
				cooldown = 3;
				sync();
			}
		}
	}
	
	private void consumeTicket(ItemEntity ticket, @Nullable Collection<BlockPos> indexPositions) {
		assert level != null;
		
		//Burp
		SoundEvent sound = level.random.nextFloat() < 0.1 ? SoundEvents.PLAYER_BURP : SoundEvents.GENERIC_EAT;
		level.playSound(null, worldPosition, sound, SoundSource.BLOCKS, .5f, 1);
		
		//Show eating particles
		if(level instanceof ServerLevel) {
			((ServerLevel) level).sendParticles(new ItemParticleOption(ParticleTypes.ITEM, ticket.getItem()), ticket.getX(), ticket.getY(), ticket.getZ(), 10, 0.1, 0.1, 0.1, 0.03);
		}
		
		//Show sparkle lines
		if(indexPositions != null) {
			Vec3 here = ticket.position();
			for(BlockPos pos : indexPositions) {
				Vec3 there = Vec3.atCenterOf(pos);
				IncNetwork.sendToNearby(level, pos, new IncNetwork.SparkleLine(here, there, 4, 1f));
			}
		}
		
		//Shrink the item
		if(ticket.getItem().getCount() > 1) {
			ticket.getItem().shrink(1);
			ticket.setItem(ticket.getItem()); //forces a sync?
		} else {
			ticket.remove();
		}
	}
	
	@Nullable
	@Override
	public RadiusDescriptor getRadius() {
		return new RadiusDescriptor.Square(worldPosition, radius);
	}
	
	@Override
	public int getMaxMana() {
		return 200;
	}
	
	@Override
	public int getColor() {
		return 0xed9625;
	}
	
	@Override
	public void onBlockPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		placerUuid = entity == null ? null : entity.getUUID();
		if(stack.hasCustomHoverName()) displayName = stack.getHoverName();
	}
	
	@Override
	public void readFromPacketNBT(CompoundTag tag) {
		super.readFromPacketNBT(tag);
		
		if(tag.contains("Placer")) placerUuid = tag.getUUID("Placer");
		else placerUuid = null;
		
		if(tag.contains("Name")) displayName = Component.Serializer.fromJson(tag.getString("Name"));
		else displayName = null;
		
		cooldown = tag.getInt("Cooldown");
	}
	
	@Override
	public void writeToPacketNBT(CompoundTag tag) {
		super.writeToPacketNBT(tag);
		
		if(placerUuid != null) tag.putUUID("Placer", placerUuid);
		if(displayName != null) tag.putString("Name", Component.Serializer.toJson(displayName));
		tag.putInt("Cooldown", cooldown);
	}
}
