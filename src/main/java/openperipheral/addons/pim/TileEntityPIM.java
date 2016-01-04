package openperipheral.addons.pim;

import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import openmods.tileentity.OpenTileEntity;
import openperipheral.api.architecture.IArchitectureAccess;
import openperipheral.api.architecture.IAttachable;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;

public class TileEntityPIM extends OpenTileEntity implements IInventory, IAttachable, ITickable {

	private WeakReference<EntityPlayer> player;

	private Set<IArchitectureAccess> computers = Sets.newIdentityHashSet();

	public EntityPlayer getPlayer() {
		return player != null? player.get() : null;
	}

	@Override
	public int getSizeInventory() {
		EntityPlayer player = getPlayer();
		return player != null? player.inventory.getSizeInventory() : 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		EntityPlayer player = getPlayer();
		return player != null? player.inventory.getStackInSlot(i) : null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		EntityPlayer player = getPlayer();
		return player != null? player.inventory.decrStackSize(i, j) : null;
	}

	@Override
	public ItemStack removeStackFromSlot(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		EntityPlayer player = getPlayer();
		if (player != null) player.inventory.setInventorySlotContents(i, itemstack);
	}

	@Override
	public String getName() {
		EntityPlayer player = getPlayer();
		return player != null? player.getName() : "pim";
	}

	@Override
	public IChatComponent getDisplayName() {
		EntityPlayer player = getPlayer();
		return player != null? player.getDisplayName() : new ChatComponentText("pim");
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		EntityPlayer player = getPlayer();
		return player != null? player.inventory.getInventoryStackLimit() : 0;
	}

	@Override
	public void clear() {
		EntityPlayer player = getPlayer();
		if (player != null) player.inventory.clear();
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return getPlayer() != null;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void addComputer(IArchitectureAccess computer) {
		synchronized (computers) {
			computers.add(computer);
		}
	}

	@Override
	public synchronized void removeComputer(IArchitectureAccess computer) {
		synchronized (computers) {
			computers.remove(computer);
		}
	}

	private void setPlayer(EntityPlayer newPlayer) {
		final BlockPos pos = getPos();
		worldObj.playSoundEffect(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D, "random.click", 0.3F, 0.6F);

		final IBlockState state = worldObj.getBlockState(pos).withProperty(BlockPIM.ACTIVE, newPlayer != null);
		worldObj.setBlockState(pos, state);

		if (player != null) {
			EntityPlayer prevPlayer = player.get();
			if (prevPlayer != null) {
				GameProfile profile = prevPlayer.getGameProfile();
				final UUID uuid = profile.getId();
				fireEvent("player_off", profile.getName(), uuid != null? uuid.toString() : "?");
			}
		}

		player = null;

		if (newPlayer != null) {
			player = new WeakReference<EntityPlayer>(newPlayer);
			GameProfile profile = newPlayer.getGameProfile();
			final UUID uuid = profile.getId();
			fireEvent("player_on", profile.getName(), uuid != null? uuid.toString() : "?");
		}
	}

	public void trySetPlayer(EntityPlayer newPlayer) {
		if (newPlayer == null) return;
		EntityPlayer current = getPlayer();
		if (current == null && isPlayerValid(newPlayer)) setPlayer(newPlayer);
	}

	private void fireEvent(String eventName, Object... args) {
		synchronized (computers) {
			for (IArchitectureAccess computer : computers) {
				Object[] extendedArgs = ArrayUtils.add(args, computer.peripheralName());
				computer.signal(eventName, extendedArgs);
			}
		}
	}

	private boolean isPlayerValid(EntityPlayer player) {
		if (player == null) return false;
		return player.getPosition().equals(getPos());
	}

	/**
	 * TODO: fix this. This doesnt seem.. efficient.
	 */
	@Override
	public void update() {
		if (!worldObj.isRemote) {
			EntityPlayer player = getPlayer();
			if (player != null && !isPlayerValid(player)) setPlayer(null);
		}
	}
}
