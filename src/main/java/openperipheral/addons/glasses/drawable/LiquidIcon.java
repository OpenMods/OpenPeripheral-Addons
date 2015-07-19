package openperipheral.addons.glasses.drawable;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import openmods.geometry.Box2d;
import openmods.structured.StructureField;
import openperipheral.addons.glasses.GlassesRenderingUtils;
import openperipheral.addons.glasses.RenderState;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.ScriptObject;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ScriptObject
@AdapterSourceName("glasses_liquid")
public class LiquidIcon extends Drawable {
	@Property
	@StructureField
	public short x;

	@Property
	@StructureField
	public short y;

	@Property
	@StructureField
	public short width;

	@Property
	@StructureField
	public short height;

	@Property
	@StructureField
	public String fluid;

	@Property
	@StructureField
	public float alpha = 1;

	private IIcon fluidIcon;

	private int iconWidth;

	private int iconHeight;

	LiquidIcon() {}

	public LiquidIcon(short x, short y, short width, short height, String fluid) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.fluid = fluid;

		setBoundingBox(Box2d.fromOriginAndSize(x, y, width, height));
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void drawContents(RenderState renderState, float partialTicks) {
		if (fluidIcon == null || iconWidth <= 0 || iconHeight <= 0) return;

		renderState.enableBlending();
		renderState.disableAlphaTest();
		renderState.disableLight();
		renderState.enableTexture();
		renderState.setColor(0xFFFFFF, alpha);

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
						yDrawSize);
			}
		}
	}

	@Override
	public Type getTypeEnum() {
		return Type.LIQUID;
	}

	@Override
	public boolean isVisible() {
		return alpha > 0 && fluidIcon != null && iconWidth > 0 && iconHeight > 0;
	}

	@Override
	protected void onUpdate() {
		fluidIcon = findFluidIcon(fluid);
		if (fluidIcon != null) {
			iconWidth = fluidIcon.getIconWidth();
			iconHeight = fluidIcon.getIconHeight();
		}

		setBoundingBox(Box2d.fromOriginAndSize(x, y, width, height));
	}

	private static IIcon findFluidIcon(String fluid) {
		Fluid drawLiquid = FluidRegistry.getFluid(fluid);
		return drawLiquid != null? drawLiquid.getFlowingIcon() : null;
	}

}