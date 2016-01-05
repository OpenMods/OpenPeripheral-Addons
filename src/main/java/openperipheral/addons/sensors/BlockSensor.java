package openperipheral.addons.sensors;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
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

	public static final PropertyBool STATIC = PropertyBool.create("static");

	public BlockSensor() {
		super(Material.iron);
		setHardness(0.5F);
		setDefaultState(getDefaultState().withProperty(STATIC, true));
	}

	@Override
	public BlockState createBlockState() {
		return new BlockState(this, new IProperty[] { getRotationMode().property, STATIC });
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return state.withProperty(STATIC, true);
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
