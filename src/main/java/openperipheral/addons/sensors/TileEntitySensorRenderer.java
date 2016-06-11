package openperipheral.addons.sensors;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntitySensorRenderer extends TileEntitySpecialRenderer {

	private ModelSensor modelSensor = new ModelSensor();
	private static final ResourceLocation texture = new ResourceLocation("openperipheraladdons", "textures/models/sensor.png");

	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTick) {
		TileEntitySensor sensor = (TileEntitySensor)tileEntity;
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
		float rotation = sensor.getRotation() - 1 + partialTick;
		bindTexture(texture);
		modelSensor.renderSensor(rotation);
		GL11.glPopMatrix();

	}
}
