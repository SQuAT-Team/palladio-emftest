package edu.kit.ipd.sdq.kamp.ui;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;

import edu.kit.ipd.sdq.kamp.core.Activity;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersion;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersionPersistency;
import edu.kit.ipd.sdq.kamp.core.derivation.DifferenceCalculation;
import edu.kit.ipd.sdq.kamp.core.derivation.EnrichedWorkplanDerivation;

public class DeriveWorkplanAction implements IActionDelegate {

	public DeriveWorkplanAction() {
	}
	
	private ISelection selection;

	@Override
	public void run(IAction action) {
		
		IContainer selectedFolder = FileAndFolderManagement.retrieveSelectedFolder(selection);
		
		ArchitectureVersion targetversion = null;
		if (selectedFolder != null) {
			try {
				targetversion = ArchitectureVersionPersistency.load(selectedFolder, "target");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		if (selectedFolder.getParent() == null) {
			throw new RuntimeException("Parent container of selection is null.");
		}

		if (!(selectedFolder.getParent() instanceof IContainer)) {
			throw new RuntimeException("Parent container of selection is not an IContainer (IFolder or IProject).");
		}

		
		ArchitectureVersion baseversion = null;
		if (selectedFolder.getParent() != null && selectedFolder.getParent() instanceof IContainer) {
			try {
				baseversion = ArchitectureVersionPersistency.load((IContainer)selectedFolder.getParent(), "base");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		if ((baseversion != null)&&(targetversion != null)) {
			List<Activity> baseActivityList = DifferenceCalculation.deriveWorkplan(baseversion, targetversion);
			
			String username = System.getProperty("user.name");
			String usernameSuffix = (username != null) ? "-" + username : "";
			
			ArchitectureVersionPersistency.saveActivityListToExcelFile(selectedFolder.getFullPath().toString(), "workplan-base"+usernameSuffix, baseActivityList);

			List<Activity> enrichedActivityList = EnrichedWorkplanDerivation.deriveEnrichedWorkplan(baseversion, targetversion, baseActivityList);
		
			ArchitectureVersionPersistency.saveActivityListToExcelFile(selectedFolder.getFullPath().toString(), "workplan-enriched"+usernameSuffix, enrichedActivityList);
		} 
		
		if (baseversion == null) {
			throw new RuntimeException("Baseversion was null");
		}

		if (targetversion == null) {
			throw new RuntimeException("Target version was null");
		}

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

}
