package openperipheral.addons.drawable;

import java.io.DataInput;
import java.io.DataOutput;

import dan200.computer.api.ILuaObject;

public interface IDrawable extends ILuaObject {

	public int getX();

	public int getY();

	public int getZIndex();

	public int setZIndex(byte z);

	public void writeTo(DataOutput stream, Short changeMask);

	public void readFrom(DataInput stream, Short changeMask);

	public void draw(float partialTicks);

}
