package openperipheral.addons.glasses.utils;

import openmods.serializable.cls.SerializableClass;
import openmods.serializable.cls.Serialize;
import openperipheral.api.struct.ScriptStruct;
import openperipheral.api.struct.StructField;

@ScriptStruct
@SerializableClass
public class ColorPoint2d {

	public static final ColorPoint2d NULL = new ColorPoint2d(0, 0, 0, 0.0f);

	public ColorPoint2d() {}

	public ColorPoint2d(float x, float y, int rgb, float opacity) {
		this.x = x;
		this.y = y;
		this.rgb = rgb;
		this.opacity = opacity;
	}

	@Serialize
	@StructField(index = 0)
	public float x;

	@Serialize
	@StructField(index = 1)
	public float y;

	@Serialize
	@StructField(index = 2, optional = true)
	public int rgb = 0xFFFFFF;

	@Serialize
	@StructField(index = 3, optional = true)
	public float opacity = 1;

	@Override
	public String toString() {
		return String.format("(%f,%f %06X:%.2f", x, y, rgb, opacity);
	}

}
