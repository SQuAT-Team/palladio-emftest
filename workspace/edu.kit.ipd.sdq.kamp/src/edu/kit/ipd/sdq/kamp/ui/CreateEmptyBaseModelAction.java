package edu.kit.ipd.sdq.kamp.ui;

import java.io.IOException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;

import edu.kit.ipd.sdq.kamp.core.ArchitectureModelFactoryFacade;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersion;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersionPersistency;

public class CreateEmptyBaseModelAction  implements IActionDelegate {

	public CreateEmptyBaseModelAction() {
	
	}

	private ISelection selection;
	
	@Override
	public void run(IAction action) {
		
		IContainer selectedFolder = FileAndFolderManagement.retrieveSelectedFolder(selection);
		
		if (selectedFolder != null) {
			ArchitectureVersion version = ArchitectureModelFactoryFacade.createEmptyModel("architecturemodel");
			try {
				ArchitectureVersionPersistency.save(selectedFolder.getFullPath().toString(), "architecturemodel", version);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
	}

	
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}

