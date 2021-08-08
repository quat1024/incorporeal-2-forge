package agency.highlysuspect.incorporeal.block.tile;

import agency.highlysuspect.incorporeal.IncConfig;
import agency.highlysuspect.incorporeal.Init;
import agency.highlysuspect.incorporeal.corporea.IndexRequestFaker;
import agency.highlysuspect.incorporeal.corporea.NearbyIndicesFinder;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import vazkii.botania.api.subtile.RadiusDescriptor;
import vazkii.botania.api.subtile.TileEntityFunctionalFlower;
import vazkii.botania.common.block.tile.corporea.TileCorporeaIndex;

import javax.annotation.Nullable;
import java.util.*;

public class SanvocaliaSubTile extends TileEntityFunctionalFlower {
	public SanvocaliaSubTile(int radius, TileEntityType<?> type) {
		super(type);
		this.radius = radius;
	}
	
	public static SanvocaliaSubTile big() {
		return new SanvocaliaSubTile(3, IncTileTypes.SANVOCALIA_BIG);
	}
	
	public static SanvocaliaSubTile small() {
		return new SanvocaliaSubTile(1, IncTileTypes.SANVOCALIA_SMALL);
	}
	
	private static final UUID CHAT_SEND_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private final int radius;
	
	private int cooldown;
	
	private UUID placerUuid = null;
	private ITextComponent displayName = null;
	
	@Override
	public void tickFlower() {
		super.tickFlower();
		if(world == null || world.isRemote) return;
		
		if(cooldown > 0) {
			cooldown--;
			return;
		}
		
		BlockPos pos = getEffectivePos(); //name shadow
		
		AxisAlignedBB itemDetectionBox = new AxisAlignedBB(pos.add(-radius, 0, -radius), pos.add(radius + 1, 1, radius + 1));
		List<ItemEntity> nearbyTicketEnts = world.getEntitiesWithinAABB(ItemEntity.class, itemDetectionBox, ent -> {
			if(ent == null) return false;
			ItemStack stack = ent.getItem();
			return stack.getItem() == IncItems.CORPOREA_TICKET && IncItems.CORPOREA_TICKET.getRequest(stack).isPresent();
		});
		if(nearbyTicketEnts.isEmpty()) return;
		
		//Pick one at random and get its request
		ItemEntity ticketEnt = nearbyTicketEnts.get(world.rand.nextInt(nearbyTicketEnts.size()));
		
		@SuppressWarnings("OptionalGetWithoutIsPresent") //i checked above
		SolidifiedRequest request = IncItems.CORPOREA_TICKET.getRequest(ticketEnt.getItem()).get();
		
		List<TileCorporeaIndex> nearbyIndices = NearbyIndicesFinder.findNearbyIndicesReflect(world, pos, radius);
		if(nearbyIndices.isEmpty()) {
			//A nod to when people write in chat while accidentally standing too far from the corporea index
			MinecraftServer server = world.getServer();
			if(server != null && getMana() >= 100) {
				TranslationTextComponent msg = new TranslationTextComponent("chat.type.text", displayName == null ? new TranslationTextComponent("block.incorporeal.sanvocalia") : displayName, request.toText());
				
				Init.LOG.info("Sanvocalia chat message triggered at " + pos.getCoordinatesAsString() + " in dimension " + world.getDimensionKey().getLocation());
				if(IncConfig.everyoneHearsSanvocalia) {
					server.getPlayerList().func_232641_a_(msg, ChatType.CHAT, CHAT_SEND_UUID);
				} else {
					ServerPlayerEntity placer = server.getPlayerList().getPlayerByUUID(placerUuid); 
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
				
				indexPositions.add(index.getPos());
				IndexRequestFaker.requestAtIndex(world, request, index.getSpark(), index.getPos());
				
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
		assert world != null;
		
		//TODO: Sparkle line packet
		//TODO: Eating particles (cannot be done serverside anymore)
		//probably could make a dedicated "sanvocalia effect" packet for this.
		
		//Burp
		SoundEvent sound = world.rand.nextFloat() < 0.1 ? SoundEvents.ENTITY_PLAYER_BURP : SoundEvents.ENTITY_GENERIC_EAT;
		world.playSound(null, pos, sound, SoundCategory.BLOCKS, .5f, 1);
		
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
		return new RadiusDescriptor.Square(pos, radius);
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
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, @Nullable LivingEntity entity, ItemStack stack) {
		placerUuid = entity == null ? null : entity.getUniqueID();
		if(stack.hasDisplayName()) displayName = stack.getDisplayName();
	}
	
	@Override
	public void readFromPacketNBT(CompoundNBT tag) {
		super.readFromPacketNBT(tag);
		
		if(tag.contains("Placer")) placerUuid = tag.getUniqueId("Placer");
		else placerUuid = null;
		
		if(tag.contains("Name")) displayName = ITextComponent.Serializer.getComponentFromJson(tag.getString("Name"));
		else displayName = null;
		
		cooldown = tag.getInt("Cooldown");
	}
	
	@Override
	public void writeToPacketNBT(CompoundNBT tag) {
		super.writeToPacketNBT(tag);
		
		if(placerUuid != null) tag.putUniqueId("Placer", placerUuid);
		if(displayName != null) tag.putString("Name", ITextComponent.Serializer.toJson(displayName));
		tag.putInt("Cooldown", cooldown);
	}
}
