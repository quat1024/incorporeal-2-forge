package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.item.IncItems;
import agency.highlysuspect.incorporeal.item.TicketConjurerItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.ChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class IncHudHandler {
	//Set to true via mixin on every frame Botania draws the "standing near corporea index!" overlay, false otherwise
	//Used to hide the ticket conjurer overlay because it doesn't actually work, botania cancels the event first :)
	//Maybe i could fix that instead LOL
	public static boolean botaniaDrewCorporeaIndexOverlay = false;
	
	public static void onDrawScreenPost(RenderGameOverlayEvent.Post event) {
		if(event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
			Minecraft mc = Minecraft.getInstance();
			ProfilerFiller profiler = mc.getProfiler();
			profiler.push("incorporeal-hud");
			
			if(mc.player == null) return; //idk
			
			ItemStack main = mc.player.getMainHandItem();
			ItemStack offhand = mc.player.getOffhandItem();
			PoseStack ms = event.getMatrixStack();
			
			if(!botaniaDrewCorporeaIndexOverlay &&
				mc.screen instanceof ChatScreen &&
				main.getItem() instanceof TicketConjurerItem || offhand.getItem() instanceof TicketConjurerItem) {
				renderConjurerOverlay(ms);
			}
			
			profiler.pop();
		}
	}
	
	//Copypaste of renderNearIndexDisplay in Botania, small changes ofc
	private static void renderConjurerOverlay(PoseStack ms) {
		Minecraft mc = Minecraft.getInstance();
		String txt0 = I18n.get("incorporeal.ticket_conjurer.hold0");
		String txt1 = ChatFormatting.GRAY + I18n.get("incorporeal.ticket_conjurer.hold1");
		String txt2 = ChatFormatting.GRAY + I18n.get("incorporeal.ticket_conjurer.hold2");
		
		int l = Math.max(mc.font.width(txt0), Math.max(mc.font.width(txt1), mc.font.width(txt2))) + 20;
		int x = mc.getWindow().getGuiScaledWidth() - l - 20;
		int y = mc.getWindow().getGuiScaledHeight() - 60;
		
		GuiComponent.fill(ms, x - 6, y - 6, x + l + 6, y + 37, 0x44000000);
		GuiComponent.fill(ms, x - 4, y - 4, x + l + 4, y + 35, 0x44000000);
		RenderSystem.enableRescaleNormal();
		mc.getItemRenderer().renderAndDecorateItem(new ItemStack(IncItems.TICKET_CONJURER), x, y + 10);
		
		mc.font.drawShadow(ms, txt0, x + 20, y, 0xFFFFFF);
		mc.font.drawShadow(ms, txt1, x + 20, y + 14, 0xFFFFFF);
		mc.font.drawShadow(ms, txt2, x + 20, y + 24, 0xFFFFFF);
	}
}
