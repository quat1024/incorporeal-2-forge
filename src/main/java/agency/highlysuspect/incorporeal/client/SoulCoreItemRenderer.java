package agency.highlysuspect.incorporeal.client;

import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.util.ResourceLocation;

public class SoulCoreItemRenderer extends ItemStackTileEntityRenderer {
	public SoulCoreItemRenderer(ResourceLocation cubesLocation) {
		this.cubesLocation = cubesLocation;
	}
	
	private final ResourceLocation cubesLocation;
}
