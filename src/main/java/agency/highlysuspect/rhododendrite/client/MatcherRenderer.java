package agency.highlysuspect.rhododendrite.client;

import agency.highlysuspect.incorporeal.corporea.EmptyCorporeaRequestMatcher;
import agency.highlysuspect.incorporeal.corporea.SolidifiedRequest;
import agency.highlysuspect.rhododendrite.block.tile.AwakenedLogTile;
import agency.highlysuspect.rhododendrite.block.tile.CoreTile;
import agency.highlysuspect.rhododendrite.computer.CompoundCorporeaRequestMatcher;
import agency.highlysuspect.rhododendrite.mixin.AccessorCorporeaItemStackMatcher;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import vazkii.botania.api.corporea.ICorporeaRequestMatcher;
import vazkii.botania.common.impl.corporea.CorporeaItemStackMatcher;
import vazkii.botania.common.impl.corporea.CorporeaStringMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//I could make this more extensible but im lazy
public class MatcherRenderer {
	public static final MatcherRenderer INSTANCE = new MatcherRenderer();
	
	public int matcherColor(ICorporeaRequestMatcher matcher) {
		if(matcher == EmptyCorporeaRequestMatcher.INSTANCE) return 0xaaaaaa;
		else if(matcher instanceof CorporeaStringMatcher) return 0x4090ff;
		else if(matcher instanceof CorporeaItemStackMatcher) return 0xff60a0;
		else if(matcher instanceof CompoundCorporeaRequestMatcher) return 0xff8000;
		else return 0xff00ff;
	}
	
	public Widget requestWidget(SolidifiedRequest request) {
		return new HorizontalStackWidget(
			new StringWidget(new TranslationTextComponent("incorporeal.solidified_request.count_only", request.count).getString(), matcherColor(request.matcher)),
			matcherWidget(request.matcher)
		);
	}
	
	public Widget matcherWidget(ICorporeaRequestMatcher matcher) {
		if(matcher == EmptyCorporeaRequestMatcher.INSTANCE || matcher instanceof CorporeaStringMatcher) {
			return new StringWidget(matcher.getRequestName().getString(), matcherColor(matcher));
		} else if(matcher instanceof CorporeaItemStackMatcher) {
			return new ItemStackWidget(((AccessorCorporeaItemStackMatcher) matcher).rho$getMatcher());
		} else if(matcher instanceof CompoundCorporeaRequestMatcher) {
			List<Widget> widgets = new ArrayList<>();
			((CompoundCorporeaRequestMatcher) matcher).getChildren().forEach(child -> widgets.add(matcherWidget(child)));
			return new VerticalStackWidget(widgets);
		}
		
		return new PaddingWidget(9, 9);
	}
	
	public void drawInGui(SolidifiedRequest request, Minecraft mc, MatrixStack ms, World world) {
		Widget widget = requestWidget(request);
		int width = widget.getWidth(mc);
		int height = widget.getHeight(mc);
		
		int x = mc.getMainWindow().getScaledWidth() / 2 - width / 2;
		int y = mc.getMainWindow().getScaledHeight() / 2 - height / 2;
		
		AbstractGui.fill(ms, x - 6, y - 6, x + width + 6, y + height + 6, 0x44000000);
		AbstractGui.fill(ms, x - 4, y - 4, x + width + 4, y + height + 4, 0x44000000);
		
		widget.draw(mc, ms, x, y, LightTexture.packLight(15, 15), OverlayTexture.NO_OVERLAY, null, Context.GUI);
	}
	
	public void drawOnLog(Minecraft mc, MatrixStack ms, World world, AwakenedLogTile tile, IRenderTypeBuffer buf, int light, int overlay) {
		
	}
	
	public void drawInCore(Minecraft mc, MatrixStack ms, World world, CoreTile tile, IRenderTypeBuffer buf, int light, int overlay) {
		
	}
	
	public enum Context {
		GUI,
		LOG,
		CORE
	}
	
	public static abstract class Widget {
		abstract int getWidth(Minecraft mc);
		abstract int getHeight(Minecraft mc);
		abstract void draw(Minecraft mc, MatrixStack ms, int x, int y, int light, int overlay, IRenderTypeBuffer bufs, Context ctx);
	}
	
	public static class StringWidget extends Widget {
		public StringWidget(String msg, int color) {
			this.msg = msg;
			this.color = color;
		}
		
		private final String msg;
		private final int color;
		
		@Override
		int getWidth(Minecraft mc) {
			return mc.fontRenderer.getStringWidth(msg);
		}
		
		@Override
		int getHeight(Minecraft mc) {
			return 9;
		}
		
		@Override
		void draw(Minecraft mc, MatrixStack ms, int x, int y, int light, int overlay, IRenderTypeBuffer bufs, Context ctx) {
			mc.fontRenderer.drawStringWithShadow(ms, msg, x, y, color);
		}
	}
	
	public static class ItemStackWidget extends Widget {
		public ItemStackWidget(ItemStack stack) {
			this.stack = stack;
		}
		
		private final ItemStack stack;
		
		@Override
		int getWidth(Minecraft mc) {
			return 16;
		}
		
		@Override
		int getHeight(Minecraft mc) {
			return 16;
		}
		
		@Override
		void draw(Minecraft mc, MatrixStack ms, int x, int y, int light, int overlay, IRenderTypeBuffer bufs, Context ctx) {
			if(ctx == Context.GUI) {
				mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, x, y);
			} else {
				mc.getItemRenderer().renderItem(stack, ItemCameraTransforms.TransformType.FIXED, light, overlay, ms, bufs);
			}
		}
	}
	
	////////////////////////////////////////////////////////
	//                                                    //
	//              we have flexbox at home               //
	//                                                    //
	////////////////////////////////////////////////////////
	
	public static class HorizontalStackWidget extends Widget {
		public HorizontalStackWidget(Widget... others) {
			this.others = Arrays.asList(others);
		}
		
		public HorizontalStackWidget(List<Widget> others) {
			this.others = others;
		}
		
		private final List<Widget> others;
		
		@Override
		int getWidth(Minecraft mc) {
			int width = 0;
			for(Widget w : others) width += w.getWidth(mc);
			return width;
		}
		
		@Override
		int getHeight(Minecraft mc) {
			int height = 0;
			for(Widget w : others) height = Math.max(height, w.getHeight(mc));
			return height;
		}
		
		@Override
		void draw(Minecraft mc, MatrixStack ms, int x, int y, int light, int overlay, IRenderTypeBuffer bufs, Context ctx) {
			int totalHeight = getHeight(mc);
			int runningOffset = 0;
			for(Widget w : others) {
				int heightDeficit = (totalHeight - w.getHeight(mc)) / 2;
				w.draw(mc, ms, x + runningOffset, y + heightDeficit, light, overlay, bufs, ctx);
				runningOffset += w.getWidth(mc);
			}
		}
	}
	
	public static class VerticalStackWidget extends Widget {
		public VerticalStackWidget(Widget... others) {
			this.others = Arrays.asList(others);
		}
		
		public VerticalStackWidget(List<Widget> others) {
			this.others = others;
		}
		
		private final List<Widget> others;
		
		@Override
		int getWidth(Minecraft mc) {
			int width = 0;
			for(Widget w : others) width = Math.max(width, w.getWidth(mc));
			return width;
		}
		
		@Override
		int getHeight(Minecraft mc) {
			int height = 0;
			for(Widget w : others) height += w.getHeight(mc);
			return height;
		}
		
		@Override
		void draw(Minecraft mc, MatrixStack ms, int x, int y, int light, int overlay, IRenderTypeBuffer bufs, Context ctx) {
			int runningOffset = 0;
			for(Widget w : others) {
				w.draw(mc, ms, x, y + runningOffset, light, overlay, bufs, ctx);
				runningOffset += w.getHeight(mc);
			}
		}
	}
	
	public static class PaddingWidget extends Widget {
		public PaddingWidget(int width, int height) {
			this.width = width;
			this.height = height;
		}
		
		private final int width;
		private final int height;
		
		@Override
		int getWidth(Minecraft mc) {
			return width;
		}
		
		@Override
		int getHeight(Minecraft mc) {
			return height;
		}
		
		@Override
		void draw(Minecraft mc, MatrixStack ms, int x, int y, int light, int overlay, IRenderTypeBuffer bufs, Context ctx) {
			//No
		}
	}
}
