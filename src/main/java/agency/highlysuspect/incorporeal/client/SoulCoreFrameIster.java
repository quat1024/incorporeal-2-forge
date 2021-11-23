package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.item.IncItems;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.ItemStack;

public class SoulCoreFrameIster extends BlockEntityWithoutLevelRenderer {
	private final SoulCoreRenderer renderer = new SoulCoreRenderer(Inc.id("textures/entity/soul_core_frame.png"));
	
	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType p_239207_2_, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		if(stack.getItem() == IncItems.SOUL_CORE_FRAME) {
			renderer.render(null, 0, ms, buffers, light, overlay);
		}
	}
}
