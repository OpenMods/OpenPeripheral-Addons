package openperipheral.addons.glasses.utils;

import openmods.serializable.cls.SerializableClass;
import openmods.serializable.cls.Serialize;
import openperipheral.api.struct.ScriptStruct;
import openperipheral.api.struct.StructField;

@ScriptStruct
@SerializableClass
public class Point2d {

	public static final Point2d NULL = new Point2d(0, 0);

	public Point2d() {}

	public Point2d(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Serialize
	@StructField(index = 0)
	public int x;

	@Serialize
	@StructField(index = 1)
	public int y;

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}

}
