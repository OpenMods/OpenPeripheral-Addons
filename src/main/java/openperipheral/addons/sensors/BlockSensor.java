package openperipheral.addons.sensors;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.Loader;
import openmods.Mods;
import openmods.block.OpenBlock;
import openperipheral.addons.Config;
import openperipheral.addons.ModuleComputerCraft;

public class BlockSensor extends OpenBlock {

	public BlockSensor() {
		super(Material.iron);
		setHardness(0.5F);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, BlockPos blockPos, EnumFacing side) {
		return side == EnumFacing.DOWN;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubBlocks(Item item, CreativeTabs tab, List result) {
		super.getSubBlocks(item, tab, result);
		if (Config.addTurtlesToCreative && Loader.isModLoaded(Mods.COMPUTERCRAFT)) ModuleComputerCraft.listSensorTurtles(result);
	}

}
