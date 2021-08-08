package agency.highlysuspect.incorporeal.client;

import agency.highlysuspect.incorporeal.item.IncItems;
import agency.highlysuspect.incorporeal.item.TicketConjurerItem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class IncHudHandler {
	//Set to true via mixin on every frame Botania draws the "standing near corporea index!" overlay, false otherwise
	//Used to hide the ticket conjurer overlay because it doesn't actually work, botania cancels the event first :)
	//Maybe i could fix that instead LOL
	public static boolean botaniaDrewCorporeaIndexOverlay = false;
	
	public static void onDrawScreenPost(RenderGameOverlayEvent.Post event) {
		if(event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
			Minecraft mc = Minecraft.getInstance();
			IProfiler profiler = mc.getProfiler();
			profiler.startSection("incorporeal-hud");
			
			if(mc.player == null) return; //idk
			
			ItemStack main = mc.player.getHeldItemMainhand();
			ItemStack offhand = mc.player.getHeldItemOffhand();
			MatrixStack ms = event.getMatrixStack();
			
			if(!botaniaDrewCorporeaIndexOverlay &&
				mc.currentScreen instanceof ChatScreen &&
				main.getItem() instanceof TicketConjurerItem || offhand.getItem() instanceof TicketConjurerItem) {
				renderConjurerOverlay(ms);
			}
			
			profiler.endSection();
		}
	}
	
	//Copypaste of renderNearIndexDisplay in Botania, small changes ofc
	private static void renderConjurerOverlay(MatrixStack ms) {
		Minecraft mc = Minecraft.getInstance();
		String txt0 = I18n.format("incorporeal.ticket_conjurer.hold0");
		String txt1 = TextFormatting.GRAY + I18n.format("incorporeal.ticket_conjurer.hold1");
		String txt2 = TextFormatting.GRAY + I18n.format("incorporeal.ticket_conjurer.hold2");
		
		int l = Math.max(mc.fontRenderer.getStringWidth(txt0), Math.max(mc.fontRenderer.getStringWidth(txt1), mc.fontRenderer.getStringWidth(txt2))) + 20;
		int x = mc.getMainWindow().getScaledWidth() - l - 20;
		int y = mc.getMainWindow().getScaledHeight() - 60;
		
		AbstractGui.fill(ms, x - 6, y - 6, x + l + 6, y + 37, 0x44000000);
		AbstractGui.fill(ms, x - 4, y - 4, x + l + 4, y + 35, 0x44000000);
		RenderSystem.enableRescaleNormal();
		mc.getItemRenderer().renderItemAndEffectIntoGUI(new ItemStack(IncItems.TICKET_CONJURER), x, y + 10);
		
		mc.fontRenderer.drawStringWithShadow(ms, txt0, x + 20, y, 0xFFFFFF);
		mc.fontRenderer.drawStringWithShadow(ms, txt1, x + 20, y + 14, 0xFFFFFF);
		mc.fontRenderer.drawStringWithShadow(ms, txt2, x + 20, y + 24, 0xFFFFFF);
	}
}
