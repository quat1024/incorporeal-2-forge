package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.tile.UnstableCubeTile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import vazkii.botania.client.core.handler.ClientTickHandler;

import javax.annotation.Nullable;

public class UnstableCubeRenderer extends TileEntityRenderer<UnstableCubeTile> {
	public UnstableCubeRenderer(TileEntityRendererDispatcher disp, DyeColor color) {
		super(disp);
		this.color = color;
	}
	
	public UnstableCubeRenderer(DyeColor color) {
		this(TileEntityRendererDispatcher.instance, color);
	}
	
	private static final ResourceLocation texture = Inc.id("textures/entity/unstable_cube.png");
	private final DyeColor color;
	
	@Override
	public void render(@Nullable UnstableCubeTile te, float partialTicks, MatrixStack ms, IRenderTypeBuffer buf, int light, int overlay) {
		ms.push();
		
		partialTicks = Minecraft.getInstance().getRenderPartialTicks();
		
		if(te == null) {
			roll(ms, partialTicks);
		} else {
			
			roll(ms, partialTicks, te.rotationAngle, te.rotationSpeed, te.bump, te.bumpDecay, MathHelper.hash(MathHelper.hash(te.getPos().hashCode())) % 50000);
		}
		
		int colorPacked = color.getColorValue();
		float red = ((colorPacked & 0xFF0000) >> 16) / 255f;
		float green = ((colorPacked & 0x00FF00) >> 8) / 255f;
		float blue = (colorPacked & 0x0000FF) / 255f;
		
		//todo dont make a new one every frame, it's for debuging
		CubeModel poot = new CubeModel();
		IVertexBuilder builder = buf.getBuffer(RenderType.getEntityCutout(texture));
		poot.render(ms, builder, light, overlay, red, green, blue, 1f);
		
		ms.pop();
	}
	
	private static class CubeModel extends Model {
		public CubeModel() {
			super(RenderType::getEntityCutout);
			textureWidth = 64;
			textureHeight = 32;
			
			cube = new ModelRenderer(this, 0, 0);
			cube.addBox(-8, -8, -8, 16, 16, 16);
			cube.setRotationPoint(8, 8, 8);
		}
		
		private final ModelRenderer cube;
		
		@Override
		public void render(MatrixStack ms, IVertexBuilder builder, int light, int overlay, float red, float green, float blue, float alpha) {
			ms.push();
			cube.render(ms, builder, light, overlay, red, green, blue, alpha);
			ms.pop();
		}
	}
	
	public static void roll(MatrixStack ms, float partialTicks) {
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
	
	public static void roll(MatrixStack ms, float partialTicks, float angle, float speed, float bump, float bumpDecay, int hash) {
		float ticks = ClientTickHandler.ticksInGame + partialTicks;
		//i dont really know what im doing
		ms.scale(.5f, .5f, .5f);
		ms.translate(0.5, 0.5, 0.5);
		
		//I don't know what this shit is either. Copied it from the 1.12 version.
		
		float predictedAngle = angle + (speed * partialTicks);
		float predictedBump = bump * Inc.rangeRemap(partialTicks, 0, 1, bump, bump * bumpDecay);
		
		int flip = (hash % 2) == 0 ? -1 : 1;
		
		ms.translate(.5, .5, .5);
		ms.rotate(Vector3f.YP.rotationDegrees((flip * predictedAngle + hash) % 360));
		
		float wobble = ticks + hash;
		float wobbleSin = Inc.sinDegrees(wobble);
		float wobbleCos = Inc.cosDegrees(wobble);
		float wobbleAmountDegrees = 15 * flip;
		
		ms.rotate(XZP.rotationDegrees(MathHelper.sin(hash + ticks * 0.02f) * 40 * flip));
		ms.rotate(Vector3f.XP.rotationDegrees(wobbleCos * wobbleAmountDegrees));
		ms.rotate(Vector3f.XP.rotationDegrees(wobbleSin * wobbleAmountDegrees));
		ms.rotate(Vector3f.ZP.rotationDegrees(-wobbleSin * wobbleAmountDegrees));
		ms.rotate(Vector3f.ZP.rotationDegrees(-wobbleCos * wobbleAmountDegrees));
		
		float upscale = (predictedBump * 0.7f) + 1;
		ms.scale(upscale, upscale, upscale);
		
		ms.translate(-.5, -.5, -.5);
	}
}
