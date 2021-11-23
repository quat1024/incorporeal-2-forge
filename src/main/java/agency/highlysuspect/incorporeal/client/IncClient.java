package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.IncProxy;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.block.UnstableCubeBlock;
import agency.highlysuspect.incorporeal.block.tile.IncTileTypes;
import agency.highlysuspect.incorporeal.entity.IncEntityTypes;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import vazkii.botania.client.render.tile.RenderTileRedString;
import vazkii.botania.client.render.tile.RenderTileSpecialFlower;

public class IncClient implements IncProxy {
	@Override
	public void setup() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modBus.addListener((FMLClientSetupEvent e) -> {
			e.enqueueWork(() -> {
				ItemProperties.register(IncItems.CORPOREA_TICKET, Inc.id("written_ticket"), (stack, world, ent) -> IncItems.CORPOREA_TICKET.hasRequest(stack) ? 1 : 0);
			});
			
			ItemBlockRenderTypes.setRenderLayer(IncBlocks.ENDER_SOUL_CORE, RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(IncBlocks.CORPOREA_SOUL_CORE, RenderType.translucent());
			ItemBlockRenderTypes.setRenderLayer(IncBlocks.POTION_SOUL_CORE, RenderType.translucent());
			
			ItemBlockRenderTypes.setRenderLayer(IncBlocks.NATURAL_REPEATER, RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(IncBlocks.NATURAL_COMPARATOR, RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(IncBlocks.REDSTONE_ROOT_CROP, RenderType.cutout());
			
			ItemBlockRenderTypes.setRenderLayer(IncBlocks.SANVOCALIA, RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(IncBlocks.SMALL_SANVOCALIA, RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(IncBlocks.FLOATING_SANVOCALIA, RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(IncBlocks.SMALL_FLOATING_SANVOCALIA, RenderType.cutout());
			
			ItemBlockRenderTypes.setRenderLayer(IncBlocks.FUNNY, RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(IncBlocks.SMALL_FUNNY, RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(IncBlocks.FLOATING_FUNNY, RenderType.cutout());
			ItemBlockRenderTypes.setRenderLayer(IncBlocks.SMALL_FLOATING_FUNNY, RenderType.cutout());
			
			RenderingRegistry.registerEntityRenderingHandler(IncEntityTypes.FRACTURED_SPACE_COLLECTOR, NotQuiteNoopRender::new);
			RenderingRegistry.registerEntityRenderingHandler(IncEntityTypes.POTION_SOUL_CORE_COLLECTOR, NotQuiteNoopRender::new);
		});
		
		modBus.addListener((ModelRegistryEvent e) -> {
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.RED_STRING_LIAR, RenderTileRedString::new);
			
			IncTileTypes.UNSTABLE_CUBES.forEach((color, type) -> ClientRegistry.bindTileEntityRenderer(type, d -> new UnstableCubeRenderer(d, color)));
			
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.ENDER_SOUL_CORE, d -> new SoulCoreRenderer(d, Inc.id("textures/entity/ender_soul_core.png")));
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.CORPOREA_SOUL_CORE, d -> new SoulCoreRenderer(d, Inc.id("textures/entity/corporea_soul_core.png")));
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.POTION_SOUL_CORE, d -> new SoulCoreRenderer(d, Inc.id("textures/entity/potion_soul_core.png")));
			
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.SANVOCALIA_BIG, RenderTileSpecialFlower::new);
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.SANVOCALIA_SMALL, RenderTileSpecialFlower::new);
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.FUNNY_BIG, RenderTileSpecialFlower::new);
			ClientRegistry.bindTileEntityRenderer(IncTileTypes.FUNNY_SMALL, RenderTileSpecialFlower::new);
		});
		
		modBus.addListener((ColorHandlerEvent.Block event) -> {
			BlockColors colors = event.getBlockColors();
			
			colors.register((state, world, pos, layer) -> ((UnstableCubeBlock) state.getBlock()).color.getColorValue(),
				IncBlocks.UNSTABLE_CUBES.values().toArray(new Block[0]));
		});
		
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		forgeBus.addListener(EventPriority.LOW, IncHudHandler::onDrawScreenPost); //make sure we're after Botania
	}
	
	//Forge what the hell is this>?????????? What is this shit??>>?/
	@Override
	public Item.Properties soulCoreFrameIster(Item.Properties in) {
		return in.setISTER(() -> SoulCoreFrameIster::new);
	}
	
	@Override
	public Item.Properties unstableCubeIster(Item.Properties in, DyeColor color) {
		return in.setISTER(() -> () -> new UnstableCubeIster(color));
	}
}
