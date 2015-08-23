package openperipheral.addons.glasses.server;

import java.util.Map;
import java.util.Set;

import openperipheral.addons.glasses.IMappedContainer;
import openperipheral.api.architecture.IArchitecture;
import openperipheral.api.helpers.Index;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MappedContainerAdapter<C> implements IMappedContainer<C> {

	private final Map<Integer, C> containers;

	public MappedContainerAdapter(Map<Integer, C> containers) {
		this.containers = containers;
	}

	@Override
	public synchronized C getById(Index id) {
		return containers.get(id.value);
	}

	@Override
	public synchronized Set<Index> getAllIds(IArchitecture access) {
		final Set<Index> indices = Sets.newHashSet();
		for (int value : containers.keySet())
			indices.add(access.createIndex(value));

		return indices;
	}

	@Override
	public synchronized Map<Index, C> getAllObjects(IArchitecture access) {
		final Map<Index, C> result = Maps.newHashMap();
		for (Map.Entry<Integer, C> e : containers.entrySet())
			result.put(access.createIndex(e.getKey()), e.getValue());

		return result;
	}

}
