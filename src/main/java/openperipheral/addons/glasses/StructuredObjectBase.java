package openperipheral.addons.glasses;

import java.lang.reflect.Field;

import openmods.structured.FieldContainer;
import openperipheral.api.Constants;
import openperipheral.api.adapter.Asynchronous;
import openperipheral.api.adapter.method.Env;
import openperipheral.api.adapter.method.ReturnType;
import openperipheral.api.adapter.method.ScriptCallable;
import openperipheral.api.architecture.IArchitecture;
import openperipheral.api.helpers.Index;
import openperipheral.api.property.IIndexedPropertyListener;
import openperipheral.api.property.ISinglePropertyListener;

import com.google.common.base.Preconditions;

@Asynchronous
public abstract class StructuredObjectBase extends FieldContainer implements ISinglePropertyListener, IIndexedPropertyListener {

	private boolean deleted;

	private int id;

	private IOwnerProxy owner = IOwnerProxy.DUMMY;

	@ScriptCallable
	public void delete() {
		checkState();
		owner.removeContainer(id);
		setDeleted();
	}

	@ScriptCallable(returnTypes = ReturnType.NUMBER, name = "getId")
	public Index getId(@Env(Constants.ARG_ARCHITECTURE) IArchitecture access) {
		checkState();
		return access.createIndex(id);
	}

	public void setOwner(IOwnerProxy owner) {
		this.owner = owner;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	private void handleFieldSet(Field field) {
		Preconditions.checkState(!deleted, "Object is already deleted");

		final Integer elementId = getElementIdForField(field);
		if (elementId != null) owner.markElementModified(elementId);
	}

	private void checkState() {
		Preconditions.checkState(!deleted, "Object is already deleted");
	}

	@Override
	public void onPropertySet(Field field, Object value) {
		handleFieldSet(field);
	}

	@Override
	public void onPropertySet(Field field, Object key, Object value) {
		handleFieldSet(field);
	}

	@Override
	public void onPropertyGet(Field field) {
		checkState();
	}

	@Override
	public void onPropertyGet(Field field, Object key) {
		checkState();
	}

	public void setDeleted() {
		this.deleted = true;
	}

	protected void markModified(int id) {
		owner.markElementModified(id);
	}
}
