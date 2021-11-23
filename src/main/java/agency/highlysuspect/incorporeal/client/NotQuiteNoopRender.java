package agency.highlysuspect.incorporeal.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

//Botania has something called RenderNoop that overrides shouldRender() to return false().
//Unfortunately that also hides F3+B hitboxes, so it's hard to debug where the entities are.
public class NotQuiteNoopRender<T extends Entity> extends EntityRenderer<T> {
	public NotQuiteNoopRender(EntityRenderDispatcher renderManager) {
		super(renderManager);
	}
	
	@Override
	public void render(T entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		//Nope
	}
	
	@Override
	protected boolean shouldShowName(T entity) {
		return false;
	}
	
	@Override
	protected void renderNameTag(T entityIn, Component displayNameIn, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
		//Nope
	}
	
	@Override
	public ResourceLocation getTextureLocation(T entity) {
		return TextureAtlas.LOCATION_BLOCKS;
	}
}
