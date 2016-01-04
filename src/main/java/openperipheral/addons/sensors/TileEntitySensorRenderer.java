package openperipheral.addons.sensors;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class TileEntitySensorRenderer extends TileEntitySpecialRenderer<TileEntitySensor> {

	private ModelSensor modelSensor = new ModelSensor();
	private static final ResourceLocation texture = new ResourceLocation("openperipheral", "textures/models/sensor.png");

	@Override
	public void renderTileEntityAt(TileEntitySensor sensor, double x, double y, double z, float partialTick, int destroyStage) {
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
		float rotation = sensor != null? (sensor.getRotation() - 1 + partialTick) : 0;
		bindTexture(texture);
		modelSensor.renderSensor(rotation);
		GL11.glPopMatrix();

	}
}
