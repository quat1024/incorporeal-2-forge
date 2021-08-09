package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.item.IncItems;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;

public class SoulCoreFrameIster extends ItemStackTileEntityRenderer {
	private final SoulCoreRenderer renderer = new SoulCoreRenderer(Inc.id("textures/entity/soul_core_frame.png"));
	
	@Override
	public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
		if(stack.getItem() == IncItems.SOUL_CORE_FRAME) {
			renderer.render(null, 0, ms, buffers, light, overlay);
		}
	}
}
