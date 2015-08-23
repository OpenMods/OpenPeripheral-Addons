package openperipheral.addons.glasses;

import java.util.Map;
import java.util.Set;

import openperipheral.api.Constants;
import openperipheral.api.adapter.method.*;
import openperipheral.api.architecture.IArchitecture;
import openperipheral.api.helpers.Index;

public interface IMappedContainer<E> {

	@Alias("getObjectById")
	@ScriptCallable(returnTypes = ReturnType.OBJECT, description = "Get object by id")
	public E getById(@Arg(name = "id", description = "Id of drawed object") Index id);

	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Get the Ids of all the objects on the screen")
	public Set<Index> getAllIds(@Env(Constants.ARG_ARCHITECTURE) IArchitecture access);

	@ScriptCallable(returnTypes = ReturnType.TABLE, description = "Get all objects on the screen")
	public Map<Index, E> getAllObjects(@Env(Constants.ARG_ARCHITECTURE) IArchitecture access);
}
