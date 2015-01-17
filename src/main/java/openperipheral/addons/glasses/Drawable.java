package openperipheral.addons.glasses;

import java.lang.reflect.Field;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import openperipheral.addons.glasses.SurfaceServer.DrawableWrapper;
import openperipheral.api.*;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@AdapterSourceName("glasses_drawable")
public abstract class Drawable implements IPropertyCallback {

	private enum Type {
		GRADIENT {
			@Override
			public Drawable create() {
				return new GradientBox();
			}
		},
		BOX {
			@Override
			public Drawable create() {
				return new SolidBox();
			}
		},
		TEXT {
			@Override
			public Drawable create() {
				return new Text();
			}
		},
		LIQUID {
			@Override
			public Drawable create() {
				return new LiquidIcon();
			}
		},
		ITEM {
			@Override
			public Drawable create() {
				return new ItemIcon();
			}
		};

		public abstract Drawable create();

		public static final Type[] TYPES = values();
	}

	DrawableWrapper wrapper;

	@CallbackProperty
	public short x;

	@CallbackProperty
	public short y;

	@CallbackProperty
	public short z;

	protected Drawable() {}

	protected Drawable(short x, short y) {
		this.x = x;
		this.y = y;
	}

	@SideOnly(Side.CLIENT)
	public void draw(float partialTicks) {
		GL11.glPushMatrix();
		GL11.glTranslated(x, y, z);
		drawContents(partialTicks);
		GL11.glPopMatrix();
	}

	@SideOnly(Side.CLIENT)
	protected abstract void drawContents(float partialTicks);

	protected abstract Type getType();

	@LuaObject
	@AdapterSourceName("glasses_box")
	public static class SolidBox extends Drawable {
		@CallbackProperty
		public short width;

		@CallbackProperty
		public short height;

		@CallbackProperty
		public int color;

		@CallbackProperty
		public float opacity;

		private SolidBox() {}

		public SolidBox(short x, short y, short width, short height, int color, float opacity) {
			super(x, y);
			this.width = width;
			this.height = height;
			this.color = color;
			this.opacity = opacity;
		}

		@Override
		@SideOnly(Side.CLIENT)
		protected void drawContents(float partialTicks) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			Tessellator tessellator = Tessellator.instance;
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(color, (int)(opacity * 255));

			tessellator.addVertex(0, 0, 0);
			tessellator.addVertex(0, height, 0);

			tessellator.addVertex(width, height, 0);
			tessellator.addVertex(width, 0, 0);

			tessellator.draw();
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_ALPHA_TEST);
		}

		@Override
		public Type getType() {
			return Type.BOX;
		}

	}

	@LuaObject
	@AdapterSourceName("glasses_gradient")
	public static class GradientBox extends Drawable {
		@CallbackProperty
		public short width;

		@CallbackProperty
		public short height;

		@CallbackProperty
		public int color1;

		@CallbackProperty
		public float opacity1;

		@CallbackProperty
		public int color2;

		@CallbackProperty
		public float opacity2;

		@CallbackProperty
		public int gradient;

		private GradientBox() {}

		public GradientBox(short x, short y, short width, short height, int color1, float opacity1, int color2, float opacity2, int gradient) {
			super(x, y);
			this.width = width;
			this.height = height;
			this.color1 = color1;
			this.opacity1 = opacity1;
			// compat hack
			if (gradient == 0) {
				this.color2 = color1;
				this.opacity2 = opacity1;
			} else {
				this.color2 = color2;
				this.opacity2 = opacity2;
			}
			this.gradient = gradient;
		}

		@Override
		@SideOnly(Side.CLIENT)
		protected void drawContents(float partialTicks) {
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_ALPHA_TEST);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glShadeModel(GL11.GL_SMOOTH);

			Tessellator tessellator = Tessellator.instance;

			tessellator.startDrawingQuads();
			tessellator.setColorRGBA_I(color1, (int)(opacity1 * 255));

			if (gradient == 1) {
				tessellator.addVertex(0, height, 0);
				tessellator.addVertex(width, height, 0);
			} else {
				tessellator.addVertex(width, height, 0);
				tessellator.addVertex(width, 0, 0);

			}

			tessellator.setColorRGBA_I(color2, (int)(opacity2 * 255));

			if (gradient == 1) {
				tessellator.addVertex(width, 0, 0);
				tessellator.addVertex(0, 0, 0);
			} else {
				tessellator.addVertex(0, 0, 0);
				tessellator.addVertex(0, height, 0);
			}

			tessellator.draw();
			GL11.glShadeModel(GL11.GL_FLAT);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_ALPHA_TEST);

		}

		@Override
		public Type getType() {
			return Type.GRADIENT;
		}

	}

	@LuaObject
	@AdapterSourceName("glasses_icon")
	public static class ItemIcon extends Drawable {
		@SideOnly(Side.CLIENT)
		private RenderItem renderItem;

		@CallbackProperty
		public float scale = 1;

		@CallbackProperty
		public String itemId;

		@CallbackProperty
		public int meta;

		private ItemIcon() {}

		public ItemIcon(short x, short y, String itemId, int meta) {
			super(x, y);
			this.itemId = itemId;
			this.meta = meta;
		}

		@SideOnly(Side.CLIENT)
		private RenderItem getRenderItem() {
			if (renderItem == null) renderItem = new RenderItem();
			return renderItem;
		}

		@Override
		@SideOnly(Side.CLIENT)
		protected void drawContents(float partialTicks) {
			if (Strings.isNullOrEmpty(itemId)) return;
			String[] itemSplit = itemId.split(":");
			if (itemSplit.length != 2) return;

			Item item = GameRegistry.findItem(itemSplit[0], itemSplit[1]);
			if (item == null) return;

			ItemStack drawStack = new ItemStack(item, 1, meta);
			GL11.glScalef(scale, scale, scale);
			getRenderItem().renderItemAndEffectIntoGUI(null, Minecraft.getMinecraft().getTextureManager(), drawStack, 0, 0);
			GL11.glDisable(GL11.GL_LIGHTING);
		}

		@Override
		public Type getType() {
			return Type.ITEM;
		}

	}

	@LuaObject
	@AdapterSourceName("glasses_liquid")
	public static class LiquidIcon extends Drawable {
		@CallbackProperty
		public short width;

		@CallbackProperty
		public short height;

		@CallbackProperty
		public String fluid;

		@CallbackProperty
		public float alpha = 1;

		private LiquidIcon() {}

		public LiquidIcon(short x, short y, short width, short height, String fluid) {
			super(x, y);
			this.width = width;
			this.height = height;
			this.fluid = fluid;
		}

		@Override
		@SideOnly(Side.CLIENT)
		protected void drawContents(float partialTicks) {
			Fluid drawLiquid = FluidRegistry.getFluid(fluid);

			if (drawLiquid == null) return;

			IIcon fluidIcon = drawLiquid.getFlowingIcon();
			if (fluidIcon == null) return;

			final int iconWidth = fluidIcon.getIconWidth();
			final int iconHeight = fluidIcon.getIconHeight();

			if (iconWidth <= 0 || iconHeight <= 0) return;

			TextureManager render = FMLClientHandler.instance().getClient().renderEngine;
			render.bindTexture(TextureMap.locationBlocksTexture);
			float xIterations = (float)width / iconWidth;
			float yIterations = (float)height / iconHeight;

			for (float xIteration = 0; xIteration < xIterations; xIteration += 1) {
				for (float yIteration = 0; yIteration < yIterations; yIteration += 1) {
					// Draw whole or partial
					final float xDrawSize = Math.min(xIterations - xIteration, 1);
					final float yDrawSize = Math.min(yIterations - yIteration, 1);

					GlassesRenderingUtils.drawTexturedQuad(
							xIteration * iconWidth,
							yIteration * iconHeight,
							fluidIcon,
							xDrawSize * iconWidth,
							yDrawSize * iconHeight,
							xDrawSize,
							yDrawSize,
							alpha);
				}
			}

		}

		@Override
		public Type getType() {
			return Type.LIQUID;
		}

	}

	@LuaObject
	@AdapterSourceName("glasses_text")
	public static class Text extends Drawable {
		@CallbackProperty
		public String text;

		@CallbackProperty
		public int color;

		@CallbackProperty
		public double alpha = 1;

		@CallbackProperty
		public float scale = 1;

		private Text() {}

		public Text(short x, short y, String text, int color) {
			super(x, y);
			this.text = text;
			this.color = color;
		}

		@Override
		@SideOnly(Side.CLIENT)
		protected void drawContents(float partialTicks) {
			FontRenderer fontRenderer = FMLClientHandler.instance().getClient().fontRenderer;
			GL11.glScalef(scale, scale, scale);
			fontRenderer.drawString(text, 0, 0, ((int)(alpha * 255) << 24 | color));
		}

		@Override
		public Type getType() {
			return Type.TEXT;
		}
	}

	public int getTypeId() {
		return getType().ordinal();
	}

	public static Drawable createFromTypeId(int containerId, int typeId) {
		Type type = Type.TYPES[typeId];
		return type.create();
	}

	@LuaCallable(returnTypes = LuaReturnType.STRING, name = "getType", description = "Get object type")
	public String getTypeName() {
		return getType().name().toLowerCase();
	}

	@LuaCallable
	public void delete() {
		Preconditions.checkNotNull(wrapper, "Object is already deleted");
		wrapper.delete();
		wrapper = null;
	}

	@LuaCallable(returnTypes = LuaReturnType.NUMBER, name = "getId")
	public int getId() {
		Preconditions.checkNotNull(wrapper, "Object is deleted");
		return wrapper.containerId + 1;
	}

	@Override
	public void setField(Field field, Object value) {
		Preconditions.checkNotNull(wrapper, "Object is deleted");
		wrapper.setField(field, value);
	}

	@Override
	public Object getField(Field field) {
		Preconditions.checkNotNull(wrapper, "Object is deleted");
		return wrapper.getField(field);
	}
}
