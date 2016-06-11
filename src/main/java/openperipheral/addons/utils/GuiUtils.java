package openperipheral.addons.utils;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraftforge.client.GuiIngameForge;

public class GuiUtils {

	public static enum GuiElements {
		OVERLAY,
		PORTAL,
		HOTBAR,
		CROSSHAIRS,
		BOSS_HEALTH,
		HEALTH,
		ARMOR,
		FOOD,
		MOUNT_HEALTH,
		AIR,
		EXPERIENCE,
		JUMP_BAR,
		OBJECTIVES;
	}

	public static void setGuiElementState(GuiElements element, boolean isVisible) {
		switch (element) {
			case OVERLAY:
				GuiIngameForge.renderHelmet = isVisible;
				break;
			case PORTAL:
				GuiIngameForge.renderPortal = isVisible;
				break;
			case HOTBAR:
				GuiIngameForge.renderHotbar = isVisible;
				break;
			case CROSSHAIRS:
				GuiIngameForge.renderCrosshairs = isVisible;
				break;
			case BOSS_HEALTH:
				GuiIngameForge.renderBossHealth = isVisible;
				break;
			case HEALTH:
				GuiIngameForge.renderHealth = isVisible;
				break;
			case ARMOR:
				GuiIngameForge.renderArmor = isVisible;
				break;
			case FOOD:
				GuiIngameForge.renderFood = isVisible;
				break;
			case MOUNT_HEALTH:
				GuiIngameForge.renderHealthMount = isVisible;
				break;
			case AIR:
				GuiIngameForge.renderAir = isVisible;
				break;
			case EXPERIENCE:
				GuiIngameForge.renderExperiance = isVisible;
				break;
			case JUMP_BAR:
				GuiIngameForge.renderJumpBar = isVisible;
				break;
			case OBJECTIVES:
				GuiIngameForge.renderObjective = isVisible;
				break;
		}
	}

	public static boolean getGuiElementState(GuiElements element) {
		switch (element) {
			case OVERLAY:
				return GuiIngameForge.renderHelmet;
			case PORTAL:
				return GuiIngameForge.renderPortal;
			case HOTBAR:
				return GuiIngameForge.renderHotbar;
			case CROSSHAIRS:
				return GuiIngameForge.renderCrosshairs;
			case BOSS_HEALTH:
				return GuiIngameForge.renderBossHealth;
			case HEALTH:
				return GuiIngameForge.renderHealth;
			case ARMOR:
				return GuiIngameForge.renderArmor;
			case FOOD:
				return GuiIngameForge.renderFood;
			case MOUNT_HEALTH:
				return GuiIngameForge.renderHealthMount;
			case AIR:
				return GuiIngameForge.renderAir;
			case EXPERIENCE:
				return GuiIngameForge.renderExperiance;
			case JUMP_BAR:
				return GuiIngameForge.renderJumpBar;
			case OBJECTIVES:
				return GuiIngameForge.renderObjective;
			default:
				return false;
		}
	}

	public static Map<GuiElements, Boolean> storeGuiElementsState() {
		Map<GuiElements, Boolean> result = Maps.newEnumMap(GuiElements.class);
		for (GuiElements e : GuiElements.values())
			result.put(e, getGuiElementState(e));

		return result;
	}

	public static void loadGuiElementsState(Map<GuiElements, Boolean> state) {
		for (Map.Entry<GuiElements, Boolean> e : state.entrySet()) {
			final Boolean value = e.getValue();
			if (value != null) setGuiElementState(e.getKey(), value);
		}
	}
}
