package agency.highlysuspect.incorporeal.block.tile;

import agency.highlysuspect.incorporeal.Init;
import agency.highlysuspect.incorporeal.block.UnstableCubeBlock;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.tile.TileMod;
import vazkii.botania.common.core.helper.Vector3;

public class UnstableCubeTile extends TileMod implements ITickableTileEntity {
	public UnstableCubeTile(DyeColor color) {
		super(IncTileTypes.UNSTABLE_CUBES.get(color));
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
	
	@Override
	public void tick() {
		if(world == null || !(getBlockState().getBlock() instanceof UnstableCubeBlock)) return;
		
		markDirty();
		
		if(rotationSpeed == 0) rotationSpeed = 8;
		
		rotationAngle += rotationSpeed;
		rotationAngle %= 360f;
		if(rotationSpeed > 1f) rotationSpeed *= 0.96;
		
		bump *= bumpDecay;
		
		int newPower = MathHelper.clamp(MathHelper.floor(Init.rangeRemap(rotationSpeed, 0, 90, 0, 15)), 0, 15);
		if(power != newPower) {
			power = newPower;
			world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
		}
		
		if(world.isRemote) {
			if(world.getGameTime() >= nextLightningTick) {
				int colorPacked = color.getColorValue();
				int red = (colorPacked & 0xFF0000) >> 16;
				int green = (colorPacked & 0x00FF00) >> 8;
				int blue = (colorPacked & 0x0000FF);
				int colorDarker = ((red / 2) << 16) | ((green / 2) << 8) | (blue / 2);
				
				Vector3 start = Vector3.fromBlockPos(pos);
				Vector3 end = start.add(world.rand.nextDouble() * 2 - 1, world.rand.nextDouble() * 2 - 1, world.rand.nextDouble() * 2 - 1);
				
				//TODO: Why doesn't this produce any lightning
				Botania.proxy.lightningFX(start, end, 5f, colorDarker, colorPacked);
				
				if(rotationSpeed > 1.1) {
					nextLightningTick = world.getGameTime() + (int) (60 - Math.min(60, rotationSpeed)) + 3;
				} else {
					nextLightningTick = world.getGameTime() + world.rand.nextInt(60) + 50;
				}
				
				float volume = rotationSpeed > 1.1 ? rotationSpeed / 170f : 0.1f;
				if(volume > 0.7f) volume = 0.7f;
				float basePitch = basePitches[color.getId()];
				float pitch = basePitch + (rotationSpeed / 600f);
				if(rotationSpeed > 83) pitch += 0.1;
				
				//world.playSound(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, SoulCoresSounds.UNSTABLE, SoundCategory.BLOCKS, volume, pitch, false);
			}
		}
	}
	
	public void punch() {
		if(world == null) return;
		
		rotationSpeed += 15;
		if(rotationSpeed > 200) rotationSpeed = 200;
		bump = 1;
		nextLightningTick = world.getGameTime();
		
		markDirty();
		VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
	}
	
	public int getPower() {
		return power;
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		//On the client, ignore changes to the rotation angle (unless this is the first packet).
		//helps prevent it jittering on servers, since the rotation is sorta controlled serverside.
		float oldRotationAngle = rotationAngle;
		super.onDataPacket(net, packet);
		if(oldRotationAngle != 0 && oldRotationAngle != rotationAngle) {
			rotationAngle = oldRotationAngle;
		}
	}
	
	@Override
	public void writePacketNBT(CompoundNBT nbt) {
		super.writePacketNBT(nbt);
		
		nbt.putFloat("RotationAngle", rotationAngle);
		nbt.putFloat("RotationSpeed", rotationSpeed);
		nbt.putFloat("Bump", bump);
		nbt.putLong("NextLightingTick", nextLightningTick);
	}
	
	@Override
	public void readPacketNBT(CompoundNBT nbt) {
		super.readPacketNBT(nbt);
		
		rotationAngle = nbt.getFloat("RotationAngle");
		rotationSpeed = nbt.getFloat("RotationSpeed");
		bump = nbt.getFloat("Bump");
		nextLightningTick = nbt.getLong("NextLightningTick");
	}
}
