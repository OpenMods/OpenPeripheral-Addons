package openperipheral.addons.glasses;

import java.util.Arrays;
import java.util.Comparator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import openmods.access.ApiSingleton;
import openperipheral.addons.api.*;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;

@ApiSingleton
public class TerminalIdAccess implements ITerminalIdAccess {

	abstract static class HelmetGetterAdapter implements ITerminalIdGetter {
		@Override
		public Optional<Long> getFor(EntityPlayer player) {
			final ItemStack stack = TerminalUtils.getHeadSlot(player);
			return stack != null? getFor(stack) : Optional.<Long> absent();
		}

		protected abstract Optional<Long> getFor(ItemStack helmet);

		@Override
		public int getPriority() {
			return 0;
		}
	}

	abstract static class HandSetterAdapter implements ITerminalIdSetter {
		@Override
		public boolean setFor(EntityPlayer player, long guid) {
			final ItemStack stack = player.getHeldItem();
			return stack != null? setFor(stack, guid) : false;
		}

		protected abstract boolean setFor(ItemStack helmet, long guid);

		@Override
		public int getPriority() {
			return 0;
		}
	}

	public static class InterfaceGetter extends HelmetGetterAdapter {
		@Override
		public Optional<Long> getFor(ItemStack stack) {
			final Item item = stack.getItem();
			if (item instanceof ITerminalItem) {
				final ITerminalItem terminalItem = (ITerminalItem)item;
				return Optional.fromNullable(terminalItem.getTerminalGuid(stack));
			}

			return Optional.absent();
		}
	}

	public static class InterfaceSetter extends HandSetterAdapter {
		@Override
		public boolean setFor(ItemStack stack, long guid) {
			final Item item = stack.getItem();
			if (item instanceof ITerminalItem) {
				final ITerminalItem terminalItem = (ITerminalItem)item;
				terminalItem.bindToTerminal(stack, guid);
				return true;
			}

			return false;
		}
	}

	public static final TerminalIdAccess instance = new TerminalIdAccess();

	private ITerminalIdGetter[] getters = new ITerminalIdGetter[0];

	private ITerminalIdSetter[] setters = new ITerminalIdSetter[0];

	private TerminalIdAccess() {}

	@Override
	public Optional<Long> getIdFrom(EntityPlayer player) {
		for (ITerminalIdGetter getter : getters) {
			final Optional<Long> result = getter.getFor(player);
			if (result.isPresent()) return result;
		}

		return Optional.absent();
	}

	@Override
	public void register(ITerminalIdGetter getter) {
		Preconditions.checkNotNull(getter);
		getters = ArrayUtils.add(getters, getter);
		Arrays.sort(getters, new Comparator<ITerminalIdGetter>() {
			@Override
			public int compare(ITerminalIdGetter o1, ITerminalIdGetter o2) {
				return Ints.compare(o1.getPriority(), o2.getPriority());
			}
		});
	}

	@Override
	public boolean setIdFor(EntityPlayer player, long guid) {
		for (ITerminalIdSetter setter : setters)
			if (setter.setFor(player, guid)) return true;

		return false;
	}

	@Override
	public void register(ITerminalIdSetter setter) {
		Preconditions.checkNotNull(setter);
		setters = ArrayUtils.add(setters, setter);

		Arrays.sort(setters, new Comparator<ITerminalIdSetter>() {
			@Override
			public int compare(ITerminalIdSetter o1, ITerminalIdSetter o2) {
				return Ints.compare(o1.getPriority(), o2.getPriority());
			}
		});
	}

}
