package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.block.UnstableCubeBlock;
import agency.highlysuspect.incorporeal.block.tile.IncBlockEntityTypes;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.level.block.Block;
import vazkii.botania.client.render.tile.RenderTileRedString;
import vazkii.botania.client.render.tile.RenderTileSpecialFlower;

public class IncClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		//TODO AW this
		//ItemProperties.register(IncItems.CORPOREA_TICKET, Inc.id("written_ticket"), (stack, world, ent) -> IncItems.CORPOREA_TICKET.hasRequest(stack) ? 1 : 0);
		
		BlockRenderLayerMap.INSTANCE.putBlock(IncBlocks.ENDER_SOUL_CORE, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(IncBlocks.CORPOREA_SOUL_CORE, RenderType.translucent());
		BlockRenderLayerMap.INSTANCE.putBlock(IncBlocks.POTION_SOUL_CORE, RenderType.translucent());
		
		BlockRenderLayerMap.INSTANCE.putBlock(IncBlocks.NATURAL_REPEATER, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(IncBlocks.NATURAL_COMPARATOR, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(IncBlocks.REDSTONE_ROOT_CROP, RenderType.cutout());

		BlockRenderLayerMap.INSTANCE.putBlock(IncBlocks.SANVOCALIA, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(IncBlocks.SMALL_SANVOCALIA, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(IncBlocks.FLOATING_SANVOCALIA, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(IncBlocks.SMALL_FLOATING_SANVOCALIA, RenderType.cutout());

		BlockRenderLayerMap.INSTANCE.putBlock(IncBlocks.FUNNY, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(IncBlocks.SMALL_FUNNY, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(IncBlocks.FLOATING_FUNNY, RenderType.cutout());
		BlockRenderLayerMap.INSTANCE.putBlock(IncBlocks.SMALL_FLOATING_FUNNY, RenderType.cutout());
		
		//todo entity renderers
		
		//TODO FIX
		BlockEntityRendererRegistry.register(IncBlockEntityTypes.RED_STRING_LIAR, RenderTileRedString::new);

		IncBlockEntityTypes.UNSTABLE_CUBES.forEach((color, type) -> BlockEntityRendererRegistry.register(type, d -> new UnstableCubeRenderer(d, color)));

		BlockEntityRendererRegistry.register(IncBlockEntityTypes.ENDER_SOUL_CORE, d -> new SoulCoreRenderer(d, Inc.id("textures/entity/ender_soul_core.png")));
		BlockEntityRendererRegistry.register(IncBlockEntityTypes.CORPOREA_SOUL_CORE, d -> new SoulCoreRenderer(d, Inc.id("textures/entity/corporea_soul_core.png")));
		BlockEntityRendererRegistry.register(IncBlockEntityTypes.POTION_SOUL_CORE, d -> new SoulCoreRenderer(d, Inc.id("textures/entity/potion_soul_core.png")));

		BlockEntityRendererRegistry.register(IncBlockEntityTypes.SANVOCALIA_BIG, RenderTileSpecialFlower::new);
		BlockEntityRendererRegistry.register(IncBlockEntityTypes.SANVOCALIA_SMALL, RenderTileSpecialFlower::new);
		BlockEntityRendererRegistry.register(IncBlockEntityTypes.FUNNY_BIG, RenderTileSpecialFlower::new);
		BlockEntityRendererRegistry.register(IncBlockEntityTypes.FUNNY_SMALL, RenderTileSpecialFlower::new);
		
		ColorProviderRegistry.BLOCK.register((state, world, pos, layer) -> ((UnstableCubeBlock) state.getBlock()).color.getTextColor(),
				IncBlocks.UNSTABLE_CUBES.values().toArray(new Block[0]));
		
		//TODO: IncHudHandler::onDrawScreenPost
	}
}
