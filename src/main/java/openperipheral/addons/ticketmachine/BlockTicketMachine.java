package openperipheral.addons.ticketmachine;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import openmods.block.BlockRotationMode;
import openmods.block.OpenBlock;
import openmods.geometry.Orientation;

public class BlockTicketMachine extends OpenBlock {

	public static final PropertyBool HAS_TICKET = PropertyBool.create("ticket");

	public BlockTicketMachine() {
		super(Material.iron);
		setDefaultState(blockState.getBaseState().withProperty(HAS_TICKET, false));
	}

	@Override
	public BlockRotationMode getRotationMode() {
		return BlockRotationMode.FOUR_DIRECTIONS;
	}

	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, getRotationMode().property, HAS_TICKET);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		final BlockRotationMode rotationMode = getRotationMode();
		return getDefaultState()
				.withProperty(getOrientationProperty(), rotationMode.fromValue(meta & rotationMode.mask))
				.withProperty(HAS_TICKET, ((meta & ~rotationMode.mask) != 0));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		final BlockRotationMode rotationMode = getRotationMode();
		final Orientation orientation = state.getValue(rotationMode.property);
		final int hasTicket = (state.getValue(HAS_TICKET)? 1 : 0) << rotationMode.bitCount;
		final int meta = getRotationMode().toValue(orientation) | hasTicket;
		return meta;
	}
}
