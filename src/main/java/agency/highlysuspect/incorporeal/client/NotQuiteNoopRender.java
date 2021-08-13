package agency.highlysuspect.incorporeal.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

//Botania has something called RenderNoop that overrides shouldRender() to return false().
//Unfortunately that also hides F3+B hitboxes, so it's hard to debug where the entities are.
public class NotQuiteNoopRender<T extends Entity> extends EntityRenderer<T> {
	public NotQuiteNoopRender(EntityRendererManager renderManager) {
		super(renderManager);
	}
	
	@Override
	public void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		//Nope
	}
	
	@Override
	protected boolean canRenderName(T entity) {
		return false;
	}
	
	@Override
	protected void renderName(T entityIn, ITextComponent displayNameIn, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
		//Nope
	}
	
	@Override
	public ResourceLocation getEntityTexture(T entity) {
		return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
	}
}
