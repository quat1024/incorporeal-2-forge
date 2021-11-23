package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.IncNetwork;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import vazkii.botania.client.fx.SparkleParticleData;

import java.util.function.Supplier;

//Because Forge's packet handling API has been REALLY WELL THOUGHT OUT,
//yeah. This is a thing. Botania does too, with the Runnables
//Astonishing that you don't register client and server sides separately but hey that's Forge for you
//The land of Proxies :sparkles:
@SuppressWarnings("Convert2Lambda")
public class ClientPacketHandler {
	public static void handleSparkleLine(IncNetwork.SparkleLine sparkle, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().setPacketHandled(true);
		ctx.get().enqueueWork(new Runnable() {
			@Override
			public void run() {
				Minecraft client = Minecraft.getInstance();
				ClientWorld world = client.level;
				if(world == null) return;
				
				showSparkleLine(world, sparkle);
			}
		});
	}
	
	public static void handleFunnyFlower(IncNetwork.FunnyFlower funny, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().setPacketHandled(true);
		ctx.get().enqueueWork(new Runnable() {
			@Override
			public void run() {
				Minecraft client = Minecraft.getInstance();
				ClientWorld world = client.level;
				if(world == null) return;
				
				for(Pair<IncNetwork.SparkleLine, byte[]> item : funny.data) {
					showSparkleLine(world, item.getFirst());
					
					Vector3d end = item.getFirst().end;
					byte[] notes = item.getSecond();
					
					if(notes.length == 1) {
						world.addParticle(ParticleTypes.NOTE, end.x, end.y + 0.7, end.z, notes[0] / 24d, 0.0D, 0.0D);
					} else if(notes.length == 2) {
						world.addParticle(ParticleTypes.NOTE, end.x - 0.2, end.y + 0.7, end.z, notes[0] / 24d, 0.0D, 0.0D);
						world.addParticle(ParticleTypes.NOTE, end.x + 0.2, end.y + 0.7, end.z, notes[1] / 24d, 0.0D, 0.0D);
					}
				}
			}
		});
	}
	
	private static void showSparkleLine(World world, IncNetwork.SparkleLine sparkle) {
		//Loosely based on PacketBotaniaEffect's SPARK_NET_INDICATOR (...is the comment i wrote back in 2018)
		Vector3d diff = sparkle.end.subtract(sparkle.start);
		Vector3d movement = diff.normalize().scale(.2);
		int iters = (int) (diff.length() / movement.length());
		float huePer = 1F / iters;
		float hueSum = (float) Math.random();
		
		Vector3d currentPos = sparkle.start;
		
		for(int i = 0; i < iters; i++) {
			float hue = i * huePer + hueSum;
			
			int color = hsbToRgb(hue, 1f, 1f);
			
			float r = Math.min(1F, ((color & 0xFF0000) >> 16) / 255F + 0.4F);
			float g = Math.min(1F, ((color & 0x00FF00) >> 8) / 255F + 0.4F);
			float b = Math.min(1F, (color & 0x0000FF) / 255F + 0.4F);
			
			world.addParticle(SparkleParticleData.noClip(sparkle.size, r, g, b, sparkle.decay), currentPos.x, currentPos.y, currentPos.z, 0, 0, 0);
			
			currentPos = currentPos.add(movement);
		}
	}
	
	//Copy-pasted from Java AWT, because you can't use that on Macs these days, or something
	@SuppressWarnings({"PointlessBitwiseExpression", "SameParameterValue"}) //Wasn't me!
	private static int hsbToRgb(float hue, float saturation, float brightness) {
		int r = 0, g = 0, b = 0;
		if (saturation == 0) {
			r = g = b = (int) (brightness * 255.0f + 0.5f);
		} else {
			float h = (hue - (float)Math.floor(hue)) * 6.0f;
			float f = h - (float)java.lang.Math.floor(h);
			float p = brightness * (1.0f - saturation);
			float q = brightness * (1.0f - saturation * f);
			float t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch ((int) h) {
				case 0:
					r = (int) (brightness * 255.0f + 0.5f);
					g = (int) (t * 255.0f + 0.5f);
					b = (int) (p * 255.0f + 0.5f);
					break;
				case 1:
					r = (int) (q * 255.0f + 0.5f);
					g = (int) (brightness * 255.0f + 0.5f);
					b = (int) (p * 255.0f + 0.5f);
					break;
				case 2:
					r = (int) (p * 255.0f + 0.5f);
					g = (int) (brightness * 255.0f + 0.5f);
					b = (int) (t * 255.0f + 0.5f);
					break;
				case 3:
					r = (int) (p * 255.0f + 0.5f);
					g = (int) (q * 255.0f + 0.5f);
					b = (int) (brightness * 255.0f + 0.5f);
					break;
				case 4:
					r = (int) (t * 255.0f + 0.5f);
					g = (int) (p * 255.0f + 0.5f);
					b = (int) (brightness * 255.0f + 0.5f);
					break;
				case 5:
					r = (int) (brightness * 255.0f + 0.5f);
					g = (int) (p * 255.0f + 0.5f);
					b = (int) (q * 255.0f + 0.5f);
					break;
			}
		}
		return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
	}
}
