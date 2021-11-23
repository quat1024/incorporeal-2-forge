package agency.highlysuspect.incorporeal.block.tile;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncSoundEvents;
import agency.highlysuspect.incorporeal.block.UnstableCubeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.DyeColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.tile.TileMod;

public class UnstableCubeTile extends TileMod {
	public UnstableCubeTile(DyeColor color, BlockPos pos, BlockState state) {
		super(IncBlockEntityTypes.UNSTABLE_CUBES.get(color), pos, state);
		this.color = color;
	}
	
	public final DyeColor color;
	
	public float rotationAngle;
	public float rotationSpeed;
	public float bump;
	public float bumpDecay = 0.8f;
	
	private long nextLightningTick = 0;
	
	private int power;
	
	private final float[] basePitches = new float[] {1f, 1.1f, 1.15f, 1.2f, 1.25f, 1.3f, 1.35f, 1.4f, 0.9f, 0.85f, 0.8f, 0.75f, 0.7f, 0.65f, 0.6f, 0.55f};
	
	public static void commonTick(Level level, BlockPos blockPos, BlockState blockState, UnstableCubeTile e) {
		e.tick();
	}
	
	public void tick() {
		if(level == null || !(getBlockState().getBlock() instanceof UnstableCubeBlock)) return;
		
		setChanged();
		
		if(rotationSpeed == 0) rotationSpeed = 8;
		
		rotationAngle += rotationSpeed;
		rotationAngle %= 360f;
		if(rotationSpeed > 1f) rotationSpeed *= 0.96;
		
		bump *= bumpDecay;
		
		int newPower = Mth.clamp(Mth.floor(Inc.rangeRemap(rotationSpeed, 0, 90, 0, 15)), 0, 15);
		if(power != newPower) {
			power = newPower;
			level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
		}
		
		if(level.isClientSide) {
			if(level.getGameTime() >= nextLightningTick) {
				int colorPacked = color.getTextColor();
				int red = (colorPacked & 0xFF0000) >> 16;
				int green = (colorPacked & 0x00FF00) >> 8;
				int blue = (colorPacked & 0x0000FF);
				int colorDarker = ((red / 2) << 16) | ((green / 2) << 8) | (blue / 2);
				
				Vec3 start = Vec3.atCenterOf(worldPosition);
				Vec3 end = start.add(level.random.nextDouble() * 2 - 1, level.random.nextDouble() * 2 - 1, level.random.nextDouble() * 2 - 1);
				
				//TODO (issue #3): Why doesn't this produce any lightning
				Botania.proxy.lightningFX(start, end, 5f, colorDarker, colorPacked);
				
				if(rotationSpeed > 1.1) {
					nextLightningTick = level.getGameTime() + (int) (60 - Math.min(60, rotationSpeed)) + 3;
				} else {
					nextLightningTick = level.getGameTime() + level.random.nextInt(60) + 50;
				}
				
				float volume = rotationSpeed > 1.1 ? rotationSpeed / 170f : 0.1f;
				if(volume > 0.7f) volume = 0.7f;
				float basePitch = basePitches[color.getId()];
				float pitch = basePitch + (rotationSpeed / 600f);
				if(rotationSpeed > 83) pitch += 0.1;
				
				level.playLocalSound(worldPosition.getX() + .5, worldPosition.getY() + .5, worldPosition.getZ() + .5, IncSoundEvents.UNSTABLE, SoundSource.BLOCKS, volume, pitch, false);
			}
		}
	}
	
	public void punch() {
		if(level == null) return;
		
		rotationSpeed += 15;
		if(rotationSpeed > 200) rotationSpeed = 200;
		bump = 1;
		nextLightningTick = level.getGameTime();
		
		setChanged();
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}
	
	public int getPower() {
		return power;
	}
	
//	@Override
//	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
//		//On the client, ignore changes to the rotation angle (unless this is the first packet).
//		//helps prevent it jittering on servers, since the rotation is sorta controlled serverside.
//		float oldRotationAngle = rotationAngle;
//		super.onDataPacket(net, packet);
//		if(oldRotationAngle != 0 && oldRotationAngle != rotationAngle) {
//			rotationAngle = oldRotationAngle;
//		}
//	}
	
	@Override
	public void writePacketNBT(CompoundTag nbt) {
		super.writePacketNBT(nbt);
		
		nbt.putFloat("RotationAngle", rotationAngle);
		nbt.putFloat("RotationSpeed", rotationSpeed);
		nbt.putFloat("Bump", bump);
		nbt.putLong("NextLightingTick", nextLightningTick);
	}
	
	@Override
	public void readPacketNBT(CompoundTag nbt) {
		super.readPacketNBT(nbt);
		
		rotationAngle = nbt.getFloat("RotationAngle");
		rotationSpeed = nbt.getFloat("RotationSpeed");
		bump = nbt.getFloat("Bump");
		nextLightningTick = nbt.getLong("NextLightningTick");
	}
}
