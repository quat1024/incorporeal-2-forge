package agency.highlysuspect.incorporeal.block.tile;

import agency.highlysuspect.incorporeal.Inc;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.util.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.block.tile.TileMod;

import java.util.Optional;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;

public abstract class AbstractSoulCoreTile extends TileMod implements IWandHUD, IManaReceiver {
	public AbstractSoulCoreTile(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}
	
	//TODO 1.17 AW this
	//public static final DamageSource SOUL = new DamageSource("incorporeal.soul").setMagic();
	public static final DamageSource SOUL = DamageSource.ANVIL;
	
	protected GameProfile ownerProfile;
	protected int mana;
	
	protected abstract int getMaxMana();
	
	public boolean hasOwnerProfile() {
		return ownerProfile != null;
	}
	
	public GameProfile getOwnerProfile() {
		return ownerProfile;
	}
	
	public void setOwnerProfile(GameProfile newProfile) {
		ownerProfile = newProfile;
		setChanged();
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}
	
	public Optional<ServerPlayer> findPlayer() {
		assert level != null; //grumble grumble
		if(level.isClientSide) throw new IllegalStateException("findPlayer on client world");
		
		if(!hasOwnerProfile()) return Optional.empty();
		
		MinecraftServer server = level.getServer(); assert server != null; //!isRemote
		ServerPlayer player = server.getPlayerList().getPlayer(ownerProfile.getId());
		
		//Must be online & in the same dimension. ("same dimension" is not a technical limitation, but a gameplay one.)
		if(player == null || player.level != level) return Optional.empty();
		
		else return Optional.of(player);
	}
	
	public InteractionResult activate(Player player, InteractionHand hand) {
		assert level != null;
		
		if(!player.getGameProfile().equals(ownerProfile)) {
			//set the soul core to this player's profile
			setOwnerProfile(player.getGameProfile());
			
			if(!level.isClientSide) {
				player.hurt(SOUL, 5f);
				receiveInitialMana();
			}
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.PASS;
	}
	
	public void receiveInitialMana() {
		int n = getMaxMana() / 2;
		if(mana < n) mana = n;
		
		setChanged();
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}
	
	public void drainMana(int howMuch) {
		if(howMuch < 0) howMuch = 0;
		mana -= howMuch;
		if(mana < 0) mana = 0;
		
		setChanged();
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}
	
	@Override
	public void tick() {
		if(level == null || level.isClientSide || getMaxMana() == 0) return;
		
		if(mana <= 0 && hasOwnerProfile()) {
			//uh oh!
			onExpire();
		}
	}
	
	public void onExpire() {
		findPlayer().ifPresent(p -> p.hurt(SOUL, 5f));
		setOwnerProfile(null);
		if(level != null) level.playSound(null, worldPosition, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, .5f, 1.2f);
	}
	
	public int getComparator() {
		if(getMaxMana() == 0) return 0;
		else return Math.round(Inc.rangeRemap(mana, 0, getMaxMana(), 0, 15));
	}
	
	@Override
	public boolean isFull() {
		if(getMaxMana() == 0) return true;
		else return mana >= getMaxMana();
	}
	
	@Override
	public void receiveMana(int moreMana) {
		mana = Math.min(mana + moreMana, getMaxMana());
		setChanged();
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}
	
	@Override
	public boolean canReceiveManaFromBursts() {
		return getMaxMana() != 0;
	}
	
	@Override
	public int getCurrentMana() {
		return mana;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void renderHUD(PoseStack ms, Minecraft mc, Level world, BlockPos pos) {
		//lol
		HUDHandler.drawSimpleManaHUD(ms, 0xee4444, mana, getMaxMana(), getBlockState().getBlock().getName().getString()); 
	}
	
	@Override
	public void writePacketNBT(CompoundTag cmp) {
		super.writePacketNBT(cmp);
		
		if(ownerProfile != null) cmp.put("OwnerProfile", NbtUtils.writeGameProfile(new CompoundTag(), ownerProfile));
		cmp.putInt("Mana", mana);
	}
	
	@Override
	public void readPacketNBT(CompoundTag cmp) {
		super.readPacketNBT(cmp);
		
		if(cmp.contains("OwnerProfile")) ownerProfile = NbtUtils.readGameProfile(cmp.getCompound("OwnerProfile"));
		else ownerProfile = null;
		
		mana = cmp.getInt("Mana");
	}
}
