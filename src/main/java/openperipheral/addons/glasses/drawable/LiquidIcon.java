package openperipheral.addons.glasses.drawable;

import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import openperipheral.addons.glasses.GlassesRenderingUtils;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.CallbackProperty;
import openperipheral.api.adapter.method.ScriptObject;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@ScriptObject
@AdapterSourceName("glasses_liquid")
public class LiquidIcon extends Drawable {
	@CallbackProperty
	public short width;

	@CallbackProperty
	public short height;

	@CallbackProperty
	public String fluid;

	@CallbackProperty
	public float alpha = 1;

	LiquidIcon() {}

	public LiquidIcon(short x, short y, short width, short height, String fluid) {
		super(x, y);
		this.width = width;
		this.height = height;
		this.fluid = fluid;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void drawContents(float partialTicks) {
		Fluid drawLiquid = FluidRegistry.getFluid(fluid);

		if (drawLiquid == null) return;

		IIcon fluidIcon = drawLiquid.getFlowingIcon();
		if (fluidIcon == null) return;

		final int iconWidth = fluidIcon.getIconWidth();
		final int iconHeight = fluidIcon.getIconHeight();

		if (iconWidth <= 0 || iconHeight <= 0) return;

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
						yDrawSize,
						alpha);
			}
		}

	}

	@Override
	public Type getTypeEnum() {
		return Type.LIQUID;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public boolean isVisible() {
		return alpha > 0;
	}

}