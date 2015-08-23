package openperipheral.addons.glasses;

import openmods.structured.IStructureContainer;
import openmods.structured.IStructureElement;

public class WindowData implements IStructureContainer<IStructureElement> {

	@Override
	public int getType() {
		return 0;
	}

	@Override
	public void createElements(IElementAddCallback<IStructureElement> callback) {}

	public void setDeleted() {
		// TODO Auto-generated method stub
		
	}

}
