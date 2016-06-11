package openperipheral.addons.glasses;

import java.util.Map;
import java.util.Set;
import openperipheral.api.Constants;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.method.Alias;
import openperipheral.api.adapter.method.Arg;
import openperipheral.api.adapter.method.Env;
import openperipheral.api.adapter.method.ReturnType;
import openperipheral.api.adapter.method.ScriptCallable;
import openperipheral.api.architecture.IArchitecture;
import openperipheral.api.helpers.Index;

@Asynchronous
@AdapterSourceName("glasses_container")
public interface IContainer<E> extends IClearable {

	public E addObject(E drawable);

	@Alias("getObjectById")
	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Get object by id")
	public E getById(@Arg(name = "id", description = "Id of drawed object") Index id);

	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Get the ids of all the objects")
	public Set<Index> getAllIds(@Env(Constants.ARG_ARCHITECTURE) IArchitecture access);

	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Get all objects")
	public Map<Index, E> getAllObjects(@Env(Constants.ARG_ARCHITECTURE) IArchitecture access);
}
