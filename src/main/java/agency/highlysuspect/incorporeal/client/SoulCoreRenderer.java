package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.tile.AbstractSoulCoreTile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.model.GenericHeadModel;
import net.minecraft.client.renderer.entity.model.HumanoidHeadModel;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import vazkii.botania.client.core.handler.ClientTickHandler;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;

public class SoulCoreRenderer extends TileEntityRenderer<AbstractSoulCoreTile> {
	public SoulCoreRenderer(TileEntityRendererDispatcher dispatcher, ResourceLocation cubesTexture) {
		super(dispatcher);
		this.cubesTexture = cubesTexture;
	}
	
	public SoulCoreRenderer(ResourceLocation cubesTexture) {
		//cheeky way to allow creating one in a ISTER
		this(null, cubesTexture);
	}
	
	private static final GenericHeadModel headModel = new HumanoidHeadModel(); 
	private final CubeModel cubeModel = new CubeModel();
	
	private final ResourceLocation cubesTexture;
	
	@Override
	public void render(@Nullable AbstractSoulCoreTile tile, float partialTicks, MatrixStack ms, IRenderTypeBuffer buf, int combinedLight, int combinedOverlay) {
		int hash = positionalHash(tile);
		//Using getRenderPartialTicks instead of trusting the partialTicks parameter - Botania's "TEISR" utility passes 0 always. I should PR that.
		//TODO: I did PR that! Not released rn though.
		float ticks = ClientTickHandler.total;
		
		ms.pushPose();
		initialWobble(ms, hash, ticks);
		
		if(tile == null) {
			//used for TEISR
			ms.scale(.9f, .9f, .9f);
		} else {
			//real tile entity
			if(tile.hasOwnerProfile()) {
				ms.pushPose(); //again
				wobbleSkull(ms, hash, ticks);
				
				IVertexBuilder builder = buf.getBuffer(getSkullRenderType(tile.getOwnerProfile()));
				headModel.renderToBuffer(ms, builder, combinedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);
				
				ms.popPose();
			}
		}
		
		wobbleCubes(ms, hash, ticks);
		
		//idk
		IVertexBuilder builder = buf.getBuffer(RenderType.entityTranslucentCull(cubesTexture));
		cubeModel.expand = Inc.rangeRemap(Inc.sinDegrees(hash + 800 + ticks * 3.4f), -1, 1, 1.5f / 16f, 1.9f / 16f);
		cubeModel.renderToBuffer(ms, builder, combinedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, Inc.rangeRemap(Inc.sinDegrees(hash + ticks * 4), -1, 1, .7f, .95f));
		
		ms.popPose();
	}
	
	private static class CubeModel extends Model {
		public CubeModel() {
			super(RenderType::entityTranslucentCull);
			texWidth = 64;
			texHeight = 32;
			
			cube = new ModelRenderer(this, 0, 0);
			cube.setTexSize(64, 32);
			cube.addBox(1.6f, 1.6f, 1.6f, 16, 16, 16);
			cube.setPos(0, 0, 0);
		}
		
		private final ModelRenderer cube;
		private float expand = 1.6f;
		
		@Override
		public void renderToBuffer(MatrixStack ms, IVertexBuilder buf, int light, int overlay, float red, float green, float blue, float alpha) {
//			
//			ModelRenderer cube;
//			cube = new ModelRenderer(this, 0, 0);
//			cube.setTextureSize(64, 64);
//			cube.addBox(1.6f, 1.6f, 1.6f, 16, 16, 16);
//			cube.setRotationPoint(0, 0, 0);
			
			//I have no idea how to port this immediate-mode stuff from 1.12 lol
			ms.pushPose();
			ms.scale(0.45f, 0.45f, 0.45f);
			
			Quaternion rotater = Vector3f.YP.rotationDegrees(90);
			
			//JUST FUCK ME UP FAM!!!!!!!!!!!!!!!
			for(int i = 0; i < 8; i++) {
				ms.translate(expand, expand, expand);
				cube.render(ms, buf, light, overlay, red, green, blue, alpha);
				ms.translate(-expand, -expand, -expand);
				ms.scale(1, -1, -1);
				if(i % 2 == 0) ms.mulPose(rotater);
			}
			
			ms.popPose();
		}
	}
	
	private static int positionalHash(@Nullable AbstractSoulCoreTile tile) {
		return tile == null ? 0 : MathHelper.murmurHash3Mixer(MathHelper.murmurHash3Mixer(tile.getBlockPos().hashCode())) % 150000;
	}
	
	//Much of this has been lifted straight from the 1.12 version of the mod - I don't know exactly how it works.
	//I remember spending a lot of time tweaking the numbers until it looked decent, haha.
	
	private static void initialWobble(MatrixStack ms, int hash, float ticks) {
		ms.translate(.5, .5, .5);
		ms.mulPose(Vector3f.YP.rotationDegrees((hash + ticks) * 2 % 360));
		ms.translate(0, 0.1 * Inc.sinDegrees((hash + ticks) * 4), 0);
	}
	
	private static void wobbleSkull(MatrixStack ms, int hash, float ticks) {
		float wobble = (hash + ticks) * 5;
		float wobbleSin = Inc.sinDegrees(wobble);
		float wobbleCos = Inc.cosDegrees(wobble);
		float wobbleAmountDegrees = 10f;
		
		ms.mulPose(Vector3f.XP.rotationDegrees(wobbleCos * wobbleAmountDegrees));
		ms.mulPose(Vector3f.XP.rotationDegrees(wobbleSin * wobbleAmountDegrees));
		ms.mulPose(Vector3f.ZP.rotationDegrees(-wobbleCos * wobbleAmountDegrees));
		ms.mulPose(Vector3f.ZP.rotationDegrees(-wobbleSin * wobbleAmountDegrees));
		
		ms.translate(0, -1/4f, 0);
		ms.scale(-1f, -1f, 1f);
	}
	
	private static void wobbleCubes(MatrixStack ms, int hash, float ticks) {
		ms.mulPose(Vector3f.YP.rotationDegrees(((-ticks + hash) / 5f) % 360));
		ms.mulPose(Vector3f.YP.rotationDegrees(MathHelper.sin((ticks + hash) / 50f) * 40));
		
		float wobble2 = (hash + ticks) * 3;
		float wobble2Sin = Inc.sinDegrees(wobble2);
		float wobble2Cos = Inc.cosDegrees(wobble2);
		float wobble2AmountDegrees = 10f;
		ms.mulPose(Vector3f.XP.rotationDegrees(-wobble2Cos * wobble2AmountDegrees));
		ms.mulPose(Vector3f.XP.rotationDegrees(-wobble2Sin * wobble2AmountDegrees));
		ms.mulPose(Vector3f.ZP.rotationDegrees(wobble2Cos * wobble2AmountDegrees));
		ms.mulPose(Vector3f.ZP.rotationDegrees(wobble2Sin * wobble2AmountDegrees));
	}
	
	//based on SkullTileEntityRenderer
	private static final UUID UNKNOWN_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");
	private static RenderType getSkullRenderType(@Nullable GameProfile profile) {
		if(profile == null) {
			return RenderType.entityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin(UNKNOWN_UUID));
		}
		
		Minecraft minecraft = Minecraft.getInstance();
		Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(profile);
		return map.containsKey(MinecraftProfileTexture.Type.SKIN) ?
			RenderType.entityTranslucent(minecraft.getSkinManager().registerTexture(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN)) :
			RenderType.entityCutoutNoCull(DefaultPlayerSkin.getDefaultSkin(PlayerEntity.createPlayerUUID(profile)));
	}
}
