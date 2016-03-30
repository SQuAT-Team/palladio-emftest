package edu.kit.ipd.sdq.kamp.test;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;

public class TestPathProvider {

	public static final String PROJECTNAME = "kamptests";

	public static String getTestPath(String testname) {
		return PROJECTNAME+testname;
	}
	
	public static void resetTestProject(String testname) {
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(PROJECTNAME+testname);
		
		if (project != null) {
			try {
				for (IResource resource : project.members()) {
					if ((resource instanceof IFile)&&(!resource.getName().contains(".project"))) {
						resource.delete(true, null);
					}
				}
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}
	}

}
