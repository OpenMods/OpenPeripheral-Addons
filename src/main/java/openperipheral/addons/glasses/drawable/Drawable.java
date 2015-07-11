package openperipheral.addons.glasses.drawable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.client.gui.ScaledResolution;
import openmods.structured.FieldContainer;
import openmods.structured.IStructureElement;
import openmods.structured.StructureField;
import openperipheral.addons.glasses.SurfaceServer;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.*;
import openperipheral.api.property.ISinglePropertyListener;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Preconditions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Asynchronous
@AdapterSourceName("glasses_drawable")
public abstract class Drawable extends FieldContainer implements ISinglePropertyListener {

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

		public float getScreenAnchorX(ScaledResolution resolution, Drawable drawable) {
			switch (screenHorizontalAnchor) {
				case MIDDLE:
					return resolution.getScaledWidth() / 2.0f;
				case RIGHT:
					return resolution.getScaledWidth();
				case LEFT:
				default:
					return 0;

			}
		}

		public float getObjectAnchorX(Drawable drawable) {
			switch (objectHorizontalAnchor) {
				case MIDDLE:
					return -drawable.getWidth() / 2.0f;
				case RIGHT:
					return -drawable.getWidth();
				case LEFT:
				default:
					return 0;

			}
		}

		public float getScreenAnchorY(ScaledResolution resolution, Drawable drawable) {
			switch (screenVerticalAnchor) {
				case BOTTOM:
					return resolution.getScaledHeight();
				case MIDDLE:
					return resolution.getScaledHeight() / 2.0f;
				default:
				case TOP:
					return 0;
			}
		}

		public float getObjectAnchorY(Drawable drawable) {
			switch (objectVerticalAnchor) {
				case BOTTOM:
					return -drawable.getHeight();
				case MIDDLE:
					return -drawable.getHeight() / 2.0f;
				default:
				case TOP:
					return 0;
			}
		}
	}

	private boolean deleted;

	private int containerId;

	private SurfaceServer owner;

	private final Alignment alignment = new Alignment();

	@Property
	@StructureField
	public short x;

	@Property
	@StructureField
	public short y;

	@Property
	@StructureField
	public short z;

	@Property
	@StructureField
	public boolean visible = true;

	@Property
	@StructureField
	public float rotation = 0;

	@Property(type = ArgType.OBJECT,
			getterDesc = "Get userdata",
			setterDesc = "Set userdata (no restrictions, not sent to clients)",
			nullable = true)
	public Object userdata;

	protected Drawable() {}

	protected Drawable(short x, short y) {
		this.x = x;
		this.y = y;
	}

	@SideOnly(Side.CLIENT)
	public double getX(ScaledResolution resolution) {
		return alignment.getScreenAnchorX(resolution, this) + alignment.getObjectAnchorX(this) + x;
	}

	@SideOnly(Side.CLIENT)
	public double getY(ScaledResolution resolution) {
		return alignment.getScreenAnchorY(resolution, this) + alignment.getObjectAnchorY(this) + y;
	}

	@SideOnly(Side.CLIENT)
	public void draw(ScaledResolution resolution, float partialTicks) {
		final float globalX = alignment.getScreenAnchorX(resolution, this) + x;
		final float globalY = alignment.getScreenAnchorY(resolution, this) + y;

		final float localX = alignment.getObjectAnchorX(this);
		final float localY = alignment.getObjectAnchorY(this);

		GL11.glPushMatrix();
		if (rotation != 0) {
			GL11.glTranslatef(globalX, globalY, z);
			GL11.glRotated(rotation, 0, 0, 1);
			GL11.glTranslatef(localX, localY, 0);
		} else {
			GL11.glTranslatef(globalX + localX, globalY + localY, z);
		}

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

	@ScriptCallable(returnTypes = ReturnType.STRING, name = "getType", description = "Get object type")
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

	@ScriptCallable(returnTypes = ReturnType.NUMBER, name = "getId")
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
	public void onPropertySet(Field field, Object value) {
		Preconditions.checkState(!deleted, "Object is already deleted");
		Preconditions.checkState(owner != null, "Invalid side");

		final IStructureElement fieldWrapper = getElementForField(field);
		if (fieldWrapper != null) owner.markElementModified(fieldWrapper);
	}

	@Override
	public void onPropertyGet(Field field) {
		Preconditions.checkState(!deleted, "Object is already deleted");
	}

	@Override
	public List<IStructureElement> createElements() {
		final List<IStructureElement> result = super.createElements();
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
	public void onElementAdded(IStructureElement element) {}

	@Override
	public final void onElementUpdated(IStructureElement element) {
		onUpdate();
	}

	protected abstract void onUpdate();

	public void onAdded(SurfaceServer owner, int containerId) {
		this.containerId = containerId;
		this.owner = owner;
	}

	public static void setupFlatRenderState() {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
}
