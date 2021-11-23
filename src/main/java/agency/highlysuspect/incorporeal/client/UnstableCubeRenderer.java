package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.tile.UnstableCubeTile;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.DyeColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;
import vazkii.botania.client.core.handler.ClientTickHandler;

import javax.annotation.Nullable;

public class UnstableCubeRenderer extends BlockEntityRenderer<UnstableCubeTile> {
	public UnstableCubeRenderer(BlockEntityRenderDispatcher disp, DyeColor color) {
		super(disp);
		this.color = color;
	}
	
	public UnstableCubeRenderer(DyeColor color) {
		this(null, color);
	}
	
	private static final ResourceLocation texture = Inc.id("textures/entity/unstable_cube.png");
	private final DyeColor color;
	private final CubeModel model = new CubeModel();
	
	@Override
	public void render(@Nullable UnstableCubeTile te, float partialTicks, PoseStack ms, MultiBufferSource buf, int light, int overlay) {
		ms.pushPose();
		
		partialTicks = ClientTickHandler.partialTicks;
		
		if(te == null) {
			roll(ms, partialTicks);
		} else {
			roll(ms, partialTicks, te.rotationAngle, te.rotationSpeed, te.bump, te.bumpDecay, Mth.murmurHash3Mixer(Mth.murmurHash3Mixer(te.getBlockPos().hashCode())) % 50000);
		}
		
		int colorPacked = color.getColorValue();
		float red = ((colorPacked & 0xFF0000) >> 16) / 255f;
		float green = ((colorPacked & 0x00FF00) >> 8) / 255f;
		float blue = (colorPacked & 0x0000FF) / 255f;
		
		VertexConsumer builder = buf.getBuffer(RenderType.entityCutout(texture));
		model.renderToBuffer(ms, builder, light, overlay, red, green, blue, 1f);
		
		ms.popPose();
	}
	
	private static class CubeModel extends Model {
		public CubeModel() {
			super(RenderType::entityCutout);
			texWidth = 64;
			texHeight = 32;
			
			cube = new ModelPart(this, 0, 0);
			cube.addBox(-8, -8, -8, 16, 16, 16);
			cube.setPos(8, 8, 8);
		}
		
		private final ModelPart cube;
		
		@Override
		public void renderToBuffer(PoseStack ms, VertexConsumer builder, int light, int overlay, float red, float green, float blue, float alpha) {
			ms.pushPose();
			cube.render(ms, builder, light, overlay, red, green, blue, alpha);
			ms.popPose();
		}
	}
	
	public static void roll(PoseStack ms, float partialTicks) {
		roll(ms, partialTicks, 0, 0, 0, 0, 0);
	}
	
	//A rotation axis used in roll()
	private static final Vector3f XZP;
	static {
		Vector3f xp = Vector3f.XP.copy();
		xp.add(Vector3f.ZP);
		xp.normalize();
		XZP = xp;
	}
	
	public static void roll(PoseStack ms, float partialTicks, float angle, float speed, float bump, float bumpDecay, int hash) {
		float ticks = ClientTickHandler.ticksInGame + partialTicks;
		//i dont really know what im doing
		ms.scale(.5f, .5f, .5f);
		ms.translate(0.5, 0.5, 0.5);
		
		//I don't know what this shit is either. Copied it from the 1.12 version.
		
		float predictedAngle = angle + (speed * partialTicks);
		float predictedBump = bump * Inc.rangeRemap(partialTicks, 0, 1, bump, bump * bumpDecay);
		
		int flip = (hash % 2) == 0 ? -1 : 1;
		
		ms.translate(.5, .5, .5);
		ms.mulPose(Vector3f.YP.rotationDegrees((flip * predictedAngle + hash) % 360));
		
		float wobble = ticks + hash;
		float wobbleSin = Inc.sinDegrees(wobble);
		float wobbleCos = Inc.cosDegrees(wobble);
		float wobbleAmountDegrees = 15 * flip;
		
		ms.mulPose(XZP.rotationDegrees(Mth.sin(hash + ticks * 0.02f) * 40 * flip));
		ms.mulPose(Vector3f.XP.rotationDegrees(wobbleCos * wobbleAmountDegrees));
		ms.mulPose(Vector3f.XP.rotationDegrees(wobbleSin * wobbleAmountDegrees));
		ms.mulPose(Vector3f.ZP.rotationDegrees(-wobbleSin * wobbleAmountDegrees));
		ms.mulPose(Vector3f.ZP.rotationDegrees(-wobbleCos * wobbleAmountDegrees));
		
		float upscale = (predictedBump * 0.7f) + 1;
		ms.scale(upscale, upscale, upscale);
		
		ms.translate(-.5, -.5, -.5);
	}
}
