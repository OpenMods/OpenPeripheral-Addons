package openperipheral.addons.glasses.drawable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openmods.geometry.Box2d;
import openmods.structured.StructureField;
import openperipheral.addons.glasses.utils.GlassesRenderingUtils;
import openperipheral.addons.glasses.utils.RenderState;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Property;
import openperipheral.api.adapter.method.ScriptObject;

import com.google.common.base.Strings;

@ScriptObject
@AdapterSourceName("glasses_liquid")
public class LiquidIcon extends Drawable {
	@Property
	@StructureField
	public float x;

	@Property
	@StructureField
	public float y;

	@Property
	@StructureField
	public float width;

	@Property
	@StructureField
	public float height;

	@Property
	@StructureField
	public String fluid;

	@Property
	@StructureField
	public float alpha = 1;

	private TextureAtlasSprite fluidIcon;

	private int iconWidth;

	private int iconHeight;

	LiquidIcon() {}

	public LiquidIcon(float x, float y, float width, float height, String fluid) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.fluid = fluid;
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void drawContents(RenderState renderState, float partialTicks) {
		if (fluidIcon == null || iconWidth <= 0 || iconHeight <= 0) return;

		renderState.setupTexturedRender();
		renderState.setColor(0xFFFFFF, alpha);

		TextureManager render = FMLClientHandler.instance().getClient().renderEngine;
		render.bindTexture(TextureMap.locationBlocksTexture);
		float xIterations = width / iconWidth;
		float yIterations = height / iconHeight;

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
	public DrawableType getTypeEnum() {
		return DrawableType.LIQUID;
	}

	@Override
	public boolean isVisible() {
		return alpha > 0 && fluidIcon != null && iconWidth > 0 && iconHeight > 0;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();

		fluidIcon = findFluidIcon(fluid);
		if (fluidIcon != null) {
			iconWidth = fluidIcon.getIconWidth();
			iconHeight = fluidIcon.getIconHeight();
		}

		setBoundingBox(Box2d.fromOriginAndSize(x, y, width, height));
	}

	private static TextureAtlasSprite findFluidIcon(String fluid) {
		if (!Strings.isNullOrEmpty(fluid)) {
			final Fluid drawLiquid = FluidRegistry.getFluid(fluid);
			if (drawLiquid != null) {
				final ResourceLocation textureLocation = drawLiquid.getStill();
				return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(textureLocation.toString());
			}
		}
		return null;
	}

}