package openperipheral.addons.glasses;

import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.method.ScriptCallable;

@Asynchronous
@AdapterSourceName("glasses_clearable")
public interface IClearable {

	@ScriptCallable(description = "Clear all children from this object")
	public void clear();

}
