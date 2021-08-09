package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.block.UnstableCubeBlock;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;

public class UnstableCubeIster extends ItemStackTileEntityRenderer {
	public UnstableCubeIster(DyeColor color) {
		renderer = new UnstableCubeRenderer(color);
	}
	
	private final UnstableCubeRenderer renderer;
	
	@Override
	public void func_239207_a_(ItemStack stack, ItemCameraTransforms.TransformType p_239207_2_, MatrixStack ms, IRenderTypeBuffer buffers, int light, int overlay) {
		if(stack.getItem() instanceof BlockItem && (((BlockItem) stack.getItem()).getBlock()) instanceof UnstableCubeBlock) {
			renderer.render(null, 0, ms, buffers, light, overlay);
		}
	}
}
