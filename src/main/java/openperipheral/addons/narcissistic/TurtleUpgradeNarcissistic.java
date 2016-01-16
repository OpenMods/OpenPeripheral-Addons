package openperipheral.addons.narcissistic;

import javax.vecmath.Matrix4f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import openperipheral.addons.MetasGeneric;
import openperipheral.addons.ModuleComputerCraft;
import openperipheral.addons.OpenPeripheralAddons;
import openperipheral.api.ApiAccess;
import openperipheral.api.architecture.cc.IComputerCraftObjectsFactory;

import org.apache.commons.lang3.tuple.Pair;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.turtle.*;

public class TurtleUpgradeNarcissistic implements ITurtleUpgrade {

	@Override
	public ResourceLocation getUpgradeID() {
		return OpenPeripheralAddons.location("narcissistic");
	}

	@Override
	public int getLegacyUpgradeID() {
		return 181;
	}

	@Override
	public String getUnlocalisedAdjective() {
		return "openperipheral.turtle.narcissistic.adjective";
	}

	@Override
	public TurtleUpgradeType getType() {
		return TurtleUpgradeType.Peripheral;
	}

	@Override
	public ItemStack getCraftingItem() {
		return MetasGeneric.duckAntenna.newItemStack();
	}

	@Override
	public IPeripheral createPeripheral(ITurtleAccess turtle, TurtleSide side) {
		return ApiAccess.getApi(IComputerCraftObjectsFactory.class).createPeripheral(new TurtleInventoryDelegate(turtle));
	}

	@Override
	public void update(ITurtleAccess turtle, TurtleSide side) {}

	@Override
	public TurtleCommandResult useTool(ITurtleAccess turtle, TurtleSide side, TurtleVerb verb, EnumFacing direction) {
		return null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Pair<IBakedModel, Matrix4f> getModel(ITurtleAccess turtle, TurtleSide side) {
		final Minecraft mc = Minecraft.getMinecraft();
		final ModelManager modelManager = mc.getRenderItem().getItemModelMesher().getModelManager();

		ModelResourceLocation location = new ModelResourceLocation(ModuleComputerCraft.getNarcissisticTurtleModel(side), "inventory");
		return Pair.of(modelManager.getModel(location), null);
	}
}
