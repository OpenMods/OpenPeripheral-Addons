package openperipheral.addons.glasses.drawable;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openmods.geometry.Box2d;
import openmods.structured.IStructureElement;
import openmods.structured.StructureField;
import openperipheral.addons.glasses.StructuredObjectBase;
import openperipheral.addons.glasses.utils.Point2d;
import openperipheral.addons.glasses.utils.RenderState;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.*;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Preconditions;

@Asynchronous
@AdapterSourceName("glasses_drawable")
public abstract class Drawable extends StructuredObjectBase {

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
		public void readFromStream(PacketBuffer input) {
			byte value = input.readByte();
			screenVerticalAnchor = VerticalAlignment.VALUES[(value >> 0) & MASK];
			screenHorizontalAnchor = HorizontalAlignment.VALUES[(value >> 2) & MASK];

			objectVerticalAnchor = VerticalAlignment.VALUES[(value >> 4) & MASK];
			objectHorizontalAnchor = HorizontalAlignment.VALUES[(value >> 6) & MASK];
		}

		@Override
		public void writeToStream(PacketBuffer output) {
			byte value = (byte)((screenVerticalAnchor.ordinal() << 0) |
					(screenHorizontalAnchor.ordinal() << 2) |
					(objectVerticalAnchor.ordinal() << 4) |
					(objectHorizontalAnchor.ordinal() << 6));
			output.writeByte(value);
		}

		public float getScreenAnchorX(float screenWidth) {
			switch (screenHorizontalAnchor) {
				case MIDDLE:
					return screenWidth / 2.0f;
				case RIGHT:
					return screenWidth;
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

		public float getScreenAnchorY(float screenHeight) {
			switch (screenVerticalAnchor) {
				case BOTTOM:
					return screenHeight;
				case MIDDLE:
					return screenHeight / 2.0f;
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

	private float rotationSin;

	private float rotationCos;

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

	@Property(type = ArgType.OBJECT,
			getterDesc = "Get userdata",
			setterDesc = "Set userdata (no restrictions, not sent to clients)",
			nullable = true)
	public Object userdata;

	protected Drawable() {}

	public void onUpdate() {
		final double rotationRad = rotation / 180 * Math.PI;
		rotationCos = (float)Math.cos(rotationRad);
		rotationSin = (float)Math.sin(rotationRad);
	}

	public Point2d transformToLocal(float worldX, float worldY, float screenWidth, float screenHeight) {
		final float worldLeft = alignment.getScreenAnchorX(screenWidth) + boundingBox.left;
		final float worldTop = alignment.getScreenAnchorY(screenHeight) + boundingBox.top;
		worldX -= worldLeft;
		worldY -= worldTop;

		float localX = worldX * +rotationCos + worldY * +rotationSin;
		float localY = worldX * -rotationSin + worldY * +rotationCos;

		localX -= alignment.getObjectAnchorX(boundingBox);
		localY -= alignment.getObjectAnchorY(boundingBox);

		return new Point2d(localX, localY);
	}

	@SideOnly(Side.CLIENT)
	public void draw(ScaledResolution resolution, RenderState renderState, float partialTicks) {
		final float screenX = alignment.getScreenAnchorX(resolution.getScaledWidth()) + boundingBox.left;
		final float screenY = alignment.getScreenAnchorY(resolution.getScaledHeight()) + boundingBox.top;

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

	protected abstract DrawableType getTypeEnum();

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
