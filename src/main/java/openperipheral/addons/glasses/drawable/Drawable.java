package openperipheral.addons.glasses.drawable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.client.gui.ScaledResolution;
import openmods.geometry.Box2d;
import openmods.structured.IStructureContainerFactory;
import openmods.structured.IStructureElement;
import openmods.structured.StructureField;
import openperipheral.addons.glasses.StructuredObjectBase;
import openperipheral.addons.glasses.utils.RenderState;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.*;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Preconditions;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Asynchronous
@AdapterSourceName("glasses_drawable")
public abstract class Drawable extends StructuredObjectBase {

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
		},
		POINT {
			@Override
			public Drawable create() {
				return new Point();
			}
		},
		LINE {
			@Override
			public Drawable create() {
				return new SolidLine();
			}
		},
		GRADIENT_LINE {
			@Override
			public Drawable create() {
				return new GradientLine();
			}
		},
		LINE_STRIP {
			@Override
			public Drawable create() {
				return new SolidLineStrip();
			}
		},
		GRADIENT_LINE_STRIP {
			@Override
			public Drawable create() {
				return new GradientLineStrip();
			}
		},
		TRIANGLE {
			@Override
			public Drawable create() {
				return new SolidTriangle();
			}
		},
		GRADIENT_TRIANGLE {
			@Override
			public Drawable create() {
				return new GradientTriangle();
			}
		},
		QUAD {
			@Override
			public Drawable create() {
				return new SolidQuad();
			}
		},
		GRADIENT_QUAD {
			@Override
			public Drawable create() {
				return new GradientQuad();
			}
		},
		POLYGON {
			@Override
			public Drawable create() {
				return new SolidPolygon();
			}
		},
		GRADIENT_POLYGON {
			@Override
			public Drawable create() {
				return new GradientPolygon();
			}
		};

		public abstract Drawable create();

		public static final Type[] TYPES = values();
	}

	public static final IStructureContainerFactory<Drawable> FACTORY = new IStructureContainerFactory<Drawable>() {
		@Override
		public Drawable createContainer(int typeId) {
			final Type type = Type.TYPES[typeId];
			return type.create();
		}
	};

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

		public float getScreenAnchorX(ScaledResolution resolution) {
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

		public float getObjectAnchorX(Box2d box) {
			switch (objectHorizontalAnchor) {
				case MIDDLE:
					return -box.width / 2.0f;
				case RIGHT:
					return -box.width;
				case LEFT:
				default:
					return 0;

			}
		}

		public float getScreenAnchorY(ScaledResolution resolution) {
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

		public float getObjectAnchorY(Box2d box) {
			switch (objectVerticalAnchor) {
				case BOTTOM:
					return -box.height;
				case MIDDLE:
					return -box.height / 2.0f;
				default:
				case TOP:
					return 0;
			}
		}
	}

	private Box2d boundingBox = Box2d.NULL;

	private final Alignment alignment = new Alignment();

	@Property
	@StructureField
	public short z;

	@Property
	@StructureField
	public boolean visible = true;

	@Property
	@StructureField
	public boolean clickable = true;

	@Property
	@StructureField
	public float rotation = 0;

	@StructureField
	public int windowId = 0;

	@Property(type = ArgType.OBJECT,
			getterDesc = "Get userdata",
			setterDesc = "Set userdata (no restrictions, not sent to clients)",
			nullable = true)
	public Object userdata;

	protected Drawable() {}

	@SideOnly(Side.CLIENT)
	public float getX(ScaledResolution resolution) {
		return alignment.getScreenAnchorX(resolution) + boundingBox.left + alignment.getObjectAnchorX(boundingBox);
	}

	@SideOnly(Side.CLIENT)
	public float getY(ScaledResolution resolution) {
		return alignment.getScreenAnchorY(resolution) + boundingBox.top + alignment.getObjectAnchorY(boundingBox);
	}
	
	public abstract void onUpdate();

	@SideOnly(Side.CLIENT)
	public void draw(ScaledResolution resolution, RenderState renderState, float partialTicks) {
		final float screenX = alignment.getScreenAnchorX(resolution) + boundingBox.left;
		final float screenY = alignment.getScreenAnchorY(resolution) + boundingBox.top;

		final float anchorX = alignment.getObjectAnchorX(boundingBox);
		final float anchorY = alignment.getObjectAnchorY(boundingBox);

		GL11.glPushMatrix();
		if (rotation != 0) {
			GL11.glTranslatef(screenX, screenY, z);
			GL11.glRotated(rotation, 0, 0, 1);
			GL11.glTranslatef(anchorX, anchorY, 0);
		} else {
			GL11.glTranslatef(screenX + anchorX, screenY + anchorY, z);
		}

		drawContents(renderState, partialTicks);
		GL11.glPopMatrix();
	}

	protected void setBoundingBox(Box2d boundingBox) {
		Preconditions.checkNotNull(boundingBox);
		this.boundingBox = boundingBox;
	}

	@SideOnly(Side.CLIENT)
	protected abstract void drawContents(RenderState renderState, float partialTicks);

	protected abstract Type getTypeEnum();

	protected abstract boolean isVisible();

	public final boolean shouldRender() {
		return visible && isVisible();
	}

	public boolean isClickable() {
		return clickable;
	}

	@Override
	public int getType() {
		return getTypeEnum().ordinal();
	}

	@ScriptCallable(returnTypes = ReturnType.STRING, name = "getType", description = "Get object type")
	public String getTypeName() {
		return getTypeEnum().name().toLowerCase();
	}

	public VerticalAlignment getScreenVerticalAnchor() {
		return alignment.screenVerticalAnchor;
	}

	@ScriptCallable
	public void setScreenAnchor(@Arg(name = "horizontal") HorizontalAlignment horizontal, @Arg(name = "vertical") VerticalAlignment vertical) {
		alignment.screenVerticalAnchor = vertical;
		alignment.screenHorizontalAnchor = horizontal;
		markModified(alignment.id);
	}

	@ScriptCallable
	public void setObjectAnchor(@Arg(name = "horizontal") HorizontalAlignment horizontal, @Arg(name = "vertical") VerticalAlignment vertical) {
		alignment.objectVerticalAnchor = vertical;
		alignment.objectHorizontalAnchor = horizontal;
		markModified(alignment.id);
	}

	@ScriptCallable
	public void setAlignment(@Arg(name = "horizontal") HorizontalAlignment horizontal, @Arg(name = "vertical") VerticalAlignment vertical) {
		alignment.objectVerticalAnchor = alignment.screenVerticalAnchor = vertical;
		alignment.objectHorizontalAnchor = alignment.screenHorizontalAnchor = horizontal;
		markModified(alignment.id);
	}

	@Override
	public void createElements(IElementAddCallback<IStructureElement> callback) {
		super.createElements(callback);
		alignment.id = callback.addElement(alignment);
	}

	public Box2d getBoundingBox() {
		return boundingBox;
	}
}
