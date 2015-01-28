package openperipheral.addons.glasses;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import openmods.structured.ElementField;
import openmods.structured.IStructureContainer;
import openmods.structured.IStructureElement;
import openperipheral.api.*;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@AdapterSourceName("glasses_drawable")
public abstract class Drawable implements IPropertyCallback, IStructureContainer<IStructureElement> {

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

	public static enum VerticalAlignment {
		TOP,
		MIDDLE,
		BOTTOM;

		public static final VerticalAlignment[] VALUES = values();
	}

	public static enum HorizontalAlignment {
		LEFT,
		MIDDLE,
		RIGHT;

		public static final HorizontalAlignment[] VALUES = values();
	}

	private static class Alignment implements IStructureElement {
		private static final int MASK = 0x3;

		private int id;

		public VerticalAlignment screenVerticalAnchor = VerticalAlignment.TOP;
		public HorizontalAlignment screenHorizontalAnchor = HorizontalAlignment.LEFT;

		public VerticalAlignment objectVerticalAnchor = VerticalAlignment.TOP;
		public HorizontalAlignment objectHorizontalAnchor = HorizontalAlignment.LEFT;

		@Override
		public void readFromStream(DataInput input) throws IOException {
			byte value = input.readByte();
			screenVerticalAnchor = VerticalAlignment.VALUES[(value >> 0) & MASK];
			screenHorizontalAnchor = HorizontalAlignment.VALUES[(value >> 2) & MASK];

			objectVerticalAnchor = VerticalAlignment.VALUES[(value >> 4) & MASK];
			objectHorizontalAnchor = HorizontalAlignment.VALUES[(value >> 6) & MASK];
		}

		@Override
		public void writeToStream(DataOutput output) throws IOException {
			byte value = (byte)((screenVerticalAnchor.ordinal() << 0) |
					(screenHorizontalAnchor.ordinal() << 2) |
					(objectVerticalAnchor.ordinal() << 4) |
					(objectHorizontalAnchor.ordinal() << 6));
			output.writeByte(value);
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public void setId(int id) {
			this.id = id;
		}

		protected int getScreenAnchorX(ScaledResolution resolution, Drawable drawable) {
			switch (screenHorizontalAnchor) {
				case MIDDLE:
					return resolution.getScaledWidth() / 2;
				case RIGHT:
					return resolution.getScaledWidth();
				case LEFT:
				default:
					return 0;

			}
		}

		protected int getObjectAnchorX(Drawable drawable) {
			switch (objectHorizontalAnchor) {
				case MIDDLE:
					return -drawable.getWidth() / 2;
				case RIGHT:
					return -drawable.getWidth();
				case LEFT:
				default:
					return 0;

			}
		}

		public int getX(ScaledResolution resolution, Drawable drawable) {
			return drawable.x + getScreenAnchorX(resolution, drawable) + getObjectAnchorX(drawable);
		}

		public int getScreenAnchorY(ScaledResolution resolution, Drawable drawable) {
			switch (screenVerticalAnchor) {
				case BOTTOM:
					return resolution.getScaledHeight();
				case MIDDLE:
					return resolution.getScaledHeight() / 2;
				default:
				case TOP:
					return 0;
			}
		}

		public int getObjectAnchorY(Drawable drawable) {
			switch (objectVerticalAnchor) {
				case BOTTOM:
					return -drawable.getHeight();
				case MIDDLE:
					return -drawable.getHeight() / 2;
				default:
				case TOP:
					return 0;
			}
		}

		public int getY(ScaledResolution resolution, Drawable drawable) {
			return drawable.y + getScreenAnchorY(resolution, drawable) + getObjectAnchorY(drawable);
		}
	}

	private boolean deleted;

	private int containerId;

	private SurfaceServer owner;

	private final Map<Field, ElementField> fields = Maps.newHashMap();

	private final Alignment alignment = new Alignment();

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
	public void draw(ScaledResolution resolution, float partialTicks) {
		GL11.glPushMatrix();

		final int x = alignment.getX(resolution, this);
		final int y = alignment.getY(resolution, this);

		GL11.glTranslated(x, y, z);
		drawContents(partialTicks);
		GL11.glPopMatrix();
	}

	@SideOnly(Side.CLIENT)
	protected abstract void drawContents(float partialTicks);

	protected abstract Type getTypeEnum();

	protected abstract int getWidth();

	protected abstract int getHeight();

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
		public Type getTypeEnum() {
			return Type.BOX;
		}

		@Override
		protected int getWidth() {
			return width;
		}

		@Override
		protected int getHeight() {
			return height;
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
		public Type getTypeEnum() {
			return Type.GRADIENT;
		}

		@Override
		protected int getWidth() {
			return width;
		}

		@Override
		protected int getHeight() {
			return height;
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
		public Type getTypeEnum() {
			return Type.ITEM;
		}

		@Override
		protected int getWidth() {
			return Math.round(16 * scale);
		}

		@Override
		protected int getHeight() {
			return Math.round(16 * scale);
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
		public Type getTypeEnum() {
			return Type.LIQUID;
		}

		@Override
		protected int getWidth() {
			return width;
		}

		@Override
		protected int getHeight() {
			return height;
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
		public Type getTypeEnum() {
			return Type.TEXT;
		}

		@Override
		protected int getWidth() {
			FontRenderer fontRenderer = FMLClientHandler.instance().getClient().fontRenderer;
			return Math.round(fontRenderer.getStringWidth(text) * scale);
		}

		@Override
		protected int getHeight() {
			return Math.round(8 * scale);
		}
	}

	@Override
	public int getType() {
		return getTypeEnum().ordinal();
	}

	public static Drawable createFromTypeId(int containerId, int typeId) {
		Type type = Type.TYPES[typeId];
		return type.create();
	}

	@LuaCallable(returnTypes = LuaReturnType.STRING, name = "getType", description = "Get object type")
	public String getTypeName() {
		return getTypeEnum().name().toLowerCase();
	}

	@LuaCallable
	public void delete() {
		Preconditions.checkState(!deleted, "Object is already deleted");
		Preconditions.checkState(owner != null, "Invalid side");
		owner.removeContainer(containerId);
		deleted = true;
	}

	@LuaCallable(returnTypes = LuaReturnType.NUMBER, name = "getId")
	public int getId() {
		Preconditions.checkState(!deleted, "Object is already deleted");
		return containerId + 1;
	}

	public VerticalAlignment getScreenVerticalAnchor() {
		return alignment.screenVerticalAnchor;
	}

	@LuaCallable
	public void setScreenAnchor(@Arg(name = "horizontal") HorizontalAlignment horizontal, @Arg(name = "vertical") VerticalAlignment vertical) {
		alignment.screenVerticalAnchor = vertical;
		alignment.screenHorizontalAnchor = horizontal;
		owner.markElementModified(alignment);
	}

	@LuaCallable
	public void setObjectAnchor(@Arg(name = "horizontal") HorizontalAlignment horizontal, @Arg(name = "vertical") VerticalAlignment vertical) {
		alignment.objectVerticalAnchor = vertical;
		alignment.objectHorizontalAnchor = horizontal;
		owner.markElementModified(alignment);
	}

	@Override
	public void setField(Field field, Object value) {
		Preconditions.checkState(!deleted, "Object is already deleted");
		Preconditions.checkState(owner != null, "Invalid side");

		ElementField fieldWrapper = fields.get(field);
		Preconditions.checkState(fieldWrapper != null, "LOGIC FAIL. BLAME MOD DEVS");
		owner.markElementModified(fieldWrapper);
		fieldWrapper.set(value);
	}

	@Override
	public Object getField(Field field) {
		Preconditions.checkState(!deleted, "Object is already deleted");

		ElementField fieldWrapper = fields.get(field);
		Preconditions.checkState(fieldWrapper != null, "LOGIC FAIL. BLAME MOD DEVS");
		return fieldWrapper.get();
	}

	@Override
	public List<IStructureElement> createElements() {
		List<IStructureElement> result = Lists.newArrayList();
		for (Field field : getClass().getFields()) {
			field.setAccessible(true);
			if (!field.isAnnotationPresent(CallbackProperty.class)) continue;

			ElementField fieldWrapper = new ElementField(this, field);
			result.add(fieldWrapper);
			fields.put(field, fieldWrapper);
		}

		result.add(alignment);
		return result;
	}

	public void setDeleted() {
		this.deleted = true;
	}

	public void setOwner(SurfaceServer owner) {
		this.owner = owner;
	}

	@Override
	public void onElementAdded(IStructureElement element, int index) {}

	public void onAdded(SurfaceServer owner, int containerId) {
		this.containerId = containerId;
		this.owner = owner;
	}
}
