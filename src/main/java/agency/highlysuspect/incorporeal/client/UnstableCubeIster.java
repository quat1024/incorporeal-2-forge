package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.block.UnstableCubeBlock;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class UnstableCubeIster extends BlockEntityWithoutLevelRenderer {
	public UnstableCubeIster(DyeColor color) {
		renderer = new UnstableCubeRenderer(color);
	}
	
	private final UnstableCubeRenderer renderer;
	
	@Override
	public void renderByItem(ItemStack stack, ItemTransforms.TransformType p_239207_2_, PoseStack ms, MultiBufferSource buffers, int light, int overlay) {
		if(stack.getItem() instanceof BlockItem && (((BlockItem) stack.getItem()).getBlock()) instanceof UnstableCubeBlock) {
			renderer.render(null, 0, ms, buffers, light, overlay);
		}
	}
}
