package openperipheral.addons.sensors;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import openmods.block.OpenBlock;
import openperipheral.addons.Config;
import openperipheral.addons.OpenPeripheralAddons;
import openperipheral.addons.utils.CCUtils;

public class BlockSensor extends OpenBlock {

	public Icon turtleIcon;

	public BlockSensor() {
		super(Config.sensorBlockId, Material.ground);
		setHardness(0.5F);
		setCreativeTab(OpenPeripheralAddons.tabOpenPeripheralAddons);
	}

	@Override
	public boolean canCollideCheck(int par1, boolean par2) {
		return true;
	}

	@Override
	public void registerIcons(IconRegister iconRegister) {
		super.registerIcons(iconRegister);
		turtleIcon = iconRegister.registerIcon("openperipheraladdons:sensorturtle");
	}

	@Override
	public int getFlammability(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
		return 0;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
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
	public boolean canBeReplacedByLeaves(World world, int x, int y, int z) {
		return false;
	}

	@Override
	public boolean isFlammable(IBlockAccess world, int x, int y, int z, int metadata, ForgeDirection face) {
		return false;
	}

	@Override
	public boolean shouldRenderBlock() {
		return false;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getSubBlocks(int id, CreativeTabs tab, List result) {
		super.getSubBlocks(id, tab, result);
		if (Config.addTurtlesToCreative) CCUtils.addUpgradedTurtles(result, OpenPeripheralAddons.sensorUpgrade);
	}

}
