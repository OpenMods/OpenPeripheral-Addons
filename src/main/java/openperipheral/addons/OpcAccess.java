package openperipheral.addons;

import com.google.common.base.Preconditions;
import openperipheral.api.ApiHolder;
import openperipheral.api.adapter.IPeripheralAdapterRegistry;
import openperipheral.api.meta.IEntityPartialMetaBuilder;
import openperipheral.api.meta.IItemStackMetaBuilder;

public class OpcAccess {

	@ApiHolder
	public static IPeripheralAdapterRegistry adapterRegistry;

	@ApiHolder
	public static IItemStackMetaBuilder itemStackMetaBuilder;

	@ApiHolder
	public static IEntityPartialMetaBuilder entityMetaBuilder;

	public static void checkApiPresent() {
		Preconditions.checkState(adapterRegistry != null, "Adapter Registry not present");
		Preconditions.checkState(itemStackMetaBuilder != null, "Item stack metadata provider not present");
		Preconditions.checkState(entityMetaBuilder != null, "Entity metadata provider not present");
	}

}
