package openperipheral.addons.glasses.drawable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.ScaledResolution;
import openmods.structured.ElementField;
import openmods.structured.IStructureContainer;
import openmods.structured.IStructureElement;
import openperipheral.addons.glasses.SurfaceServer;
import openperipheral.api.adapter.*;
import openperipheral.api.adapter.method.Arg;
import openperipheral.api.adapter.method.ReturnType;
import openperipheral.api.adapter.method.ScriptCallable;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Asynchronous
@AdapterSourceName("glasses_drawable")
public abstract class Drawable implements IPropertyCallback, IStructureContainer<IStructureElement> {

	enum Type {
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

	@CallbackProperty
	public boolean visible = true;

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

	public abstract int getWidth();

	public abstract int getHeight();

	protected abstract boolean isVisible();

	public final boolean shouldRender() {
		return visible && isVisible();
	}

	@Override
	public int getType() {
		return getTypeEnum().ordinal();
	}

	public static Drawable createFromTypeId(int containerId, int typeId) {
		Type type = Type.TYPES[typeId];
		final Drawable container = type.create();
		container.containerId = containerId;
		return container;
	}

	@ScriptCallable(returnTypes = ReturnType
			.STRING, name = "getType", description = "Get object type")
	public String getTypeName() {
		return getTypeEnum().name().toLowerCase();
	}

	@ScriptCallable
	public void delete() {
		Preconditions.checkState(!deleted, "Object is already deleted");
		Preconditions.checkState(owner != null, "Invalid side");
		owner.removeContainer(containerId);
		deleted = true;
	}

	@ScriptCallable(returnTypes = ReturnType
			.NUMBER, name = "getId")
	public int getId() {
		Preconditions.checkState(!deleted, "Object is already deleted");
		return containerId + 1;
	}

	public VerticalAlignment getScreenVerticalAnchor() {
		return alignment.screenVerticalAnchor;
	}

	@ScriptCallable
	public void setScreenAnchor(@Arg(name = "horizontal") HorizontalAlignment horizontal, @Arg(name = "vertical") VerticalAlignment vertical) {
		alignment.screenVerticalAnchor = vertical;
		alignment.screenHorizontalAnchor = horizontal;
		owner.markElementModified(alignment);
	}

	@ScriptCallable
	public void setObjectAnchor(@Arg(name = "horizontal") HorizontalAlignment horizontal, @Arg(name = "vertical") VerticalAlignment vertical) {
		alignment.objectVerticalAnchor = vertical;
		alignment.objectHorizontalAnchor = horizontal;
		owner.markElementModified(alignment);
	}

	@ScriptCallable
	public void setAlignment(@Arg(name = "horizontal") HorizontalAlignment horizontal, @Arg(name = "vertical") VerticalAlignment vertical) {
		alignment.objectVerticalAnchor = alignment.screenVerticalAnchor = vertical;
		alignment.objectHorizontalAnchor = alignment.screenHorizontalAnchor = horizontal;
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
