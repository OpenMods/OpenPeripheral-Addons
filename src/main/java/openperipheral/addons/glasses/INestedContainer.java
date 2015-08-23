package openperipheral.addons.glasses;

import java.util.Map;

import openperipheral.api.Constants;
import openperipheral.api.adapter.AdapterSourceName;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.method.Env;
import openperipheral.api.adapter.method.ReturnType;
import openperipheral.api.adapter.method.ScriptCallable;
import openperipheral.api.architecture.IArchitecture;
import openperipheral.api.helpers.Index;

@Asynchronous
@AdapterSourceName("nested_container")
public interface INestedContainer<E> extends IMappedContainer<E> {

	@ScriptCallable(returnTypes = ReturnType.OBJECT)
	public INestedContainer<E> getParent();

	@ScriptCallable(returnTypes = ReturnType.TABLE)
	public INestedContainer<E> getChild(Index id);

	@ScriptCallable(returnTypes = ReturnType.TABLE)
	public Map<Index, INestedContainer<E>> getChildren(@Env(Constants.ARG_ARCHITECTURE) IArchitecture access);

	@ScriptCallable(returnTypes = ReturnType.OBJECT)
	public INestedContainer<E> createChild();

}
