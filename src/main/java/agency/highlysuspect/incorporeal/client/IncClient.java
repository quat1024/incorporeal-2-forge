package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.Inc;
import agency.highlysuspect.incorporeal.block.IncBlocks;
import agency.highlysuspect.incorporeal.block.UnstableCubeBlock;
import agency.highlysuspect.incorporeal.block.tile.IncTileTypes;
import agency.highlysuspect.incorporeal.entity.IncEntityTypes;
import agency.highlysuspect.incorporeal.item.IncItems;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import vazkii.botania.client.render.tile.RenderTileRedString;
import vazkii.botania.client.render.tile.RenderTileSpecialFlower;

public class IncClient implements ClientModInitializer {
	@Override
	public void setup() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modBus.addListener((FMLClientSetupEvent e) -> {
			e.enqueueWork(() -> {
				ItemModelsProperties.register(IncItems.CORPOREA_TICKET, Inc.id("written_ticket"), (stack, level, ent) -> IncItems.CORPOREA_TICKET.hasRequest(stack) ? 1 : 0);
			});
			
			RenderTypeLookup.setRenderLayer(IncBlocks.ENDER_SOUL_CORE, RenderType.translucent());
			RenderTypeLookup.setRenderLayer(IncBlocks.CORPOREA_SOUL_CORE, RenderType.translucent());
			RenderTypeLookup.setRenderLayer(IncBlocks.POTION_SOUL_CORE, RenderType.translucent());
			
			RenderTypeLookup.setRenderLayer(IncBlocks.NATURAL_REPEATER, RenderType.cutout());
			RenderTypeLookup.setRenderLayer(IncBlocks.NATURAL_COMPARATOR, RenderType.cutout());
			RenderTypeLookup.setRenderLayer(IncBlocks.REDSTONE_ROOT_CROP, RenderType.cutout());
			
			RenderTypeLookup.setRenderLayer(IncBlocks.SANVOCALIA, RenderType.cutout());
			RenderTypeLookup.setRenderLayer(IncBlocks.SMALL_SANVOCALIA, RenderType.cutout());
			RenderTypeLookup.setRenderLayer(IncBlocks.FLOATING_SANVOCALIA, RenderType.cutout());
			RenderTypeLookup.setRenderLayer(IncBlocks.SMALL_FLOATING_SANVOCALIA, RenderType.cutout());
			
			RenderTypeLookup.setRenderLayer(IncBlocks.FUNNY, RenderType.cutout());
			RenderTypeLookup.setRenderLayer(IncBlocks.SMALL_FUNNY, RenderType.cutout());
			RenderTypeLookup.setRenderLayer(IncBlocks.FLOATING_FUNNY, RenderType.cutout());
			RenderTypeLookup.setRenderLayer(IncBlocks.SMALL_FLOATING_FUNNY, RenderType.cutout());
			
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
			
			colors.register((state, level, pos, layer) -> ((UnstableCubeBlock) state.getBlock()).color.getColorValue(),
				IncBlocks.UNSTABLE_CUBES.values().toArray(new Block[0]));
		});
		
		IEventBus forgeBus = MinecraftForge.EVENT_BUS;
		forgeBus.addListener(EventPriority.LOW, IncHudHandler::onDrawScreenPost); //make sure we're after Botania
	}
	
	//Forge what the hell is this>?????????? What is this shit??>>?/
	
	public Item.Properties soulCoreFrameIster(Item.Properties in) {
		return in.setISTER(() -> SoulCoreFrameIster::new);
	}
	
	
	public Item.Properties unstableCubeIster(Item.Properties in, DyeColor color) {
		return in.setISTER(() -> () -> new UnstableCubeIster(color));
	}
}
