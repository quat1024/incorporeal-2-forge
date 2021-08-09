package agency.highlysuspect.incorporeal.block.tile;

import agency.highlysuspect.incorporeal.Inc;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.wand.IWandHUD;
import vazkii.botania.client.core.handler.HUDHandler;
import vazkii.botania.common.block.tile.TileMod;

import java.util.Optional;

public abstract class AbstractSoulCoreTile extends TileMod implements IWandHUD, ITickableTileEntity, IManaReceiver {
	public AbstractSoulCoreTile(TileEntityType<?> type) {
		super(type);
	}
	
	//idk where else to stick this
	public static final DamageSource SOUL = new DamageSource("incorporeal.soul").setMagicDamage();
	
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
		markDirty();
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}
	
	public Optional<ServerPlayerEntity> findPlayer() {
		assert world != null; //grumble grumble
		if(world.isRemote) throw new IllegalStateException("findPlayer on client world");
		
		if(!hasOwnerProfile()) return Optional.empty();
		
		MinecraftServer server = world.getServer(); assert server != null; //!isRemote
		ServerPlayerEntity player = server.getPlayerList().getPlayerByUUID(ownerProfile.getId());
		
		//Must be online & in the same dimension. ("same dimension" is not a technical limitation, but a gameplay one.)
		if(player == null || player.world != world) return Optional.empty();
		
		else return Optional.of(player);
	}
	
	public ActionResultType activate(PlayerEntity player, Hand hand) {
		assert world != null;
		
		if(!player.getGameProfile().equals(ownerProfile)) {
			//set the soul core to this player's profile
			setOwnerProfile(player.getGameProfile());
			
			if(!world.isRemote) {
				player.attackEntityFrom(SOUL, 5f);
				receiveInitialMana();
			}
			return ActionResultType.SUCCESS;
		}
		
		return ActionResultType.PASS;
	}
	
	public void receiveInitialMana() {
		int n = getMaxMana() / 2;
		if(mana < n) mana = n;
		
		markDirty();
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}
	
	public void drainMana(int howMuch) {
		if(howMuch < 0) howMuch = 0;
		mana -= howMuch;
		if(mana < 0) mana = 0;
		
		markDirty();
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}
	
	@Override
	public void tick() {
		if(world == null || world.isRemote || getMaxMana() == 0) return;
		
		if(mana <= 0 && hasOwnerProfile()) {
			//uh oh!
			findPlayer().ifPresent(p -> p.attackEntityFrom(SOUL, 5f));
			setOwnerProfile(null);
			world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, .5f, 1.2f);
		}
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
		markDirty();
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
	public void renderHUD(MatrixStack ms, Minecraft mc, World world, BlockPos pos) {
		//lol
		HUDHandler.drawSimpleManaHUD(ms, 0xee4444, mana, getMaxMana(), getBlockState().getBlock().getTranslatedName().getString()); 
	}
	
	@Override
	public void writePacketNBT(CompoundNBT cmp) {
		super.writePacketNBT(cmp);
		
		if(ownerProfile != null) cmp.put("OwnerProfile", NBTUtil.writeGameProfile(new CompoundNBT(), ownerProfile));
		cmp.putInt("Mana", mana);
	}
	
	@Override
	public void readPacketNBT(CompoundNBT cmp) {
		super.readPacketNBT(cmp);
		
		if(cmp.contains("OwnerProfile")) ownerProfile = NBTUtil.readGameProfile(cmp.getCompound("OwnerProfile"));
		else ownerProfile = null;
		
		mana = cmp.getInt("Mana");
	}
}
