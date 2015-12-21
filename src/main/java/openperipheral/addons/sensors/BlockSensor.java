package openperipheral.addons.sensors;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;
import openmods.Mods;
import openperipheral.addons.*;
import cpw.mods.fml.common.Loader;

public class BlockSensor extends BlockOP {

	public BlockSensor() {
		super(Material.iron);
		setHardness(0.5F);
		setRenderMode(RenderMode.TESR_ONLY);
	}

	@Override
	public boolean canCollideCheck(int par1, boolean par2) {
		return true;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		return side == ForgeDirection.DOWN;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public int getRenderType() {
		return OpenPeripheralAddons.renderId;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubBlocks(Item item, CreativeTabs tab, List result) {
		super.getSubBlocks(item, tab, result);
		if (Config.addTurtlesToCreative && Loader.isModLoaded(Mods.COMPUTERCRAFT)) ModuleComputerCraft.listSensorTurtles(result);
	}

}
