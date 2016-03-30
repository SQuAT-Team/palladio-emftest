package edu.kit.ipd.sdq.kamp.core;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.compare.util.ModelUtils;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLParserPoolImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceImpl;

import de.uka.ipd.sdq.componentInternalDependencies.ComponentInternalDependenciesPackage;
import de.uka.ipd.sdq.componentInternalDependencies.ComponentInternalDependencyRepository;
import de.uka.ipd.sdq.pcm.repository.Repository;
import de.uka.ipd.sdq.pcm.repository.RepositoryPackage;
import de.uka.ipd.sdq.pcm.system.System;
import de.uka.ipd.sdq.pcm.system.SystemPackage;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.FieldOfActivityAnnotationRepository;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.FieldofactivityannotationsPackage;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModificationRepository;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.modificationmarksPackage;
import edu.kit.ipd.sdq.kamp.ui.FileAndFolderManagement;

public class ArchitectureVersionPersistency {

	public static final String FILEEXTENSION_REPOSITORY = "repository";
	public static final String FILEEXTENSION_SYSTEM = "system";
	public static final String FILEEXTENSION_FIELDOFACTIVITYANNOTATIONS = "fieldofactivityannotations";
	public static final String FILEEXTENSION_INTERNALMODIFICATIONMARK = "modificationmarks";
	public static final String FILEEXTENSION_COMPONENTINTERNALDEPENDENCIES = "componentinternaldependencies";
	public static final String FILEEXTENSION_ACTIVITYLIST = "activitylist";
	public static final String FILEEXTENSION_EXCEL = "xls";
	
	
	public static void save(String targetDirectoryPath, String filename, ArchitectureVersion version) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();

		String repositoryfilePath = filename + "."+FILEEXTENSION_REPOSITORY;
		String systemfilePath = filename + "."+FILEEXTENSION_SYSTEM;
		String internalModFilePath = filename + "." + FILEEXTENSION_INTERNALMODIFICATIONMARK;
		
		if (version.getRepository()!=null)
			ArchitectureVersionPersistency.saveEmfModelToResource(version.getRepository(), targetDirectoryPath, repositoryfilePath, resourceSet);		
		if (version.getSystem()!=null)
			ArchitectureVersionPersistency.saveEmfModelToResource(version.getSystem(), targetDirectoryPath, systemfilePath, resourceSet);		
		if (version.getModificationMarkRepository()!=null)
			ArchitectureVersionPersistency.saveEmfModelToResource(version.getModificationMarkRepository(), targetDirectoryPath, internalModFilePath, resourceSet);		

		saveFieldOfActivityRepository(targetDirectoryPath, filename, version);
		saveComponentInternalDependencyModel(targetDirectoryPath, filename, version);
	}

	public static void saveFieldOfActivityRepository(String targetDirectoryPath, String filename, ArchitectureVersion version) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();

		String fieldOfActivityRepositoryFilePath = filename + "."+FILEEXTENSION_FIELDOFACTIVITYANNOTATIONS;
		
		if (version.getFieldOfActivityRepository()!=null)
			ArchitectureVersionPersistency.saveEmfModelToResource(version.getFieldOfActivityRepository(), targetDirectoryPath, fieldOfActivityRepositoryFilePath, resourceSet);		
	}

	public static void saveComponentInternalDependencyModel(String targetDirectoryPath, String filename, ArchitectureVersion version) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();

		String cidepfilePath = filename + "." + FILEEXTENSION_COMPONENTINTERNALDEPENDENCIES;
		
		if (version.getComponentInternalDependencyRepository()!=null)
			ArchitectureVersionPersistency.saveEmfModelToResource(version.getComponentInternalDependencyRepository(), targetDirectoryPath, cidepfilePath, resourceSet);		
	}

	public static void saveModificationMarkFile(String targetDirectoryPath, String filename, ArchitectureVersion version) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();

		String internalModFilePath = filename + "." + FILEEXTENSION_INTERNALMODIFICATIONMARK;
		
		if (version.getModificationMarkRepository()!=null)
			ArchitectureVersionPersistency.saveEmfModelToResource(version.getModificationMarkRepository(), targetDirectoryPath, internalModFilePath, resourceSet);		
	}

	
	public static void saveActivityList(String targetDirectoryPath, String filename, List<Activity> activityList) {
		String activityListFilePath = filename + "." + FILEEXTENSION_ACTIVITYLIST;
		String workspaceURI = ResourcesPlugin.getWorkspace().getRoot().getLocationURI().getPath().toString();
//		URI saveURI = URI.createPlatformResourceURI(targetDirectoryPath, true);
//		saveURI = saveURI.appendSegment(activityListFilePath);
		saveActivitiesToFile(workspaceURI+"/"+targetDirectoryPath+"/"+activityListFilePath, activityList, "");
	}
	
	public static void saveActivityListToExcelFile(String targetDirectoryPath, String filename, List<Activity> activityList) {
		String activityListFilePath = filename + "." + FILEEXTENSION_EXCEL;
		String workspaceURI = ResourcesPlugin.getWorkspace().getRoot().getLocationURI().getPath().toString();
		
		ExcelWriter excelWriter = new ExcelWriter();
		
		excelWriter.saveActivitiesToExcelFile(workspaceURI+"/"+targetDirectoryPath+"/"+activityListFilePath, activityList, "");
	}
	
	public static void saveFromExistingResource(String targetDirectoryPath, ArchitectureVersion version) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		if (version.getRepository()!=null)
			ArchitectureVersionPersistency.saveEmfModelToResource(version.getRepository(), targetDirectoryPath, null, resourceSet);		
		if (version.getSystem()!=null)
			ArchitectureVersionPersistency.saveEmfModelToResource(version.getSystem(), targetDirectoryPath, null, resourceSet);		
		if (version.getFieldOfActivityRepository()!=null)
			ArchitectureVersionPersistency.saveEmfModelToResource(version.getFieldOfActivityRepository(), targetDirectoryPath, null, resourceSet);		
		if (version.getModificationMarkRepository()!=null)
			ArchitectureVersionPersistency.saveEmfModelToResource(version.getModificationMarkRepository(), targetDirectoryPath, null, resourceSet);		
		if (version.getComponentInternalDependencyRepository()!=null)
			ArchitectureVersionPersistency.saveEmfModelToResource(version.getComponentInternalDependencyRepository(), targetDirectoryPath, null, resourceSet);		
	}
	
	public static ArchitectureVersion load(String folderpath, String filename, String versionname) throws IOException {
		ResourceSet loadResourceSet = new ResourceSetImpl();
		
		String repositoryfilePath = filename + "."+FILEEXTENSION_REPOSITORY;
		String systemfilePath = filename + "."+FILEEXTENSION_SYSTEM;
		String fieldOfActivityRepositoryFilePath = filename + "."+FILEEXTENSION_FIELDOFACTIVITYANNOTATIONS;
		String internalModFilePath = filename + "." + FILEEXTENSION_INTERNALMODIFICATIONMARK;
		String cidepfilePath = filename + "." + FILEEXTENSION_COMPONENTINTERNALDEPENDENCIES;

		Repository repository = (Repository)loadEmfModelFromResource(folderpath, repositoryfilePath, loadResourceSet);
		System system = (System)loadEmfModelFromResource(folderpath, systemfilePath, loadResourceSet);
		FieldOfActivityAnnotationRepository fieldOfActivityRepository = (FieldOfActivityAnnotationRepository)loadEmfModelFromResource(folderpath, fieldOfActivityRepositoryFilePath, loadResourceSet);
		ModificationRepository internalModificationMarkRepository = (ModificationRepository)loadEmfModelFromResource(folderpath, internalModFilePath, loadResourceSet);
		ComponentInternalDependencyRepository componentInternalDependencyRepository = (ComponentInternalDependencyRepository)loadEmfModelFromResource(folderpath, cidepfilePath, loadResourceSet);
		
		return new ArchitectureVersion(versionname, repository, system, fieldOfActivityRepository, internalModificationMarkRepository, componentInternalDependencyRepository);
	}	
	
	public static ArchitectureVersion load(IContainer folder, String versionname) throws IOException {
		ResourceSet loadResourceSet = new ResourceSetImpl();
		
		IFile repositoryfile = FileAndFolderManagement.retrieveFileWithExtension(folder, FILEEXTENSION_REPOSITORY);
		IFile systemfile = FileAndFolderManagement.retrieveFileWithExtension(folder, FILEEXTENSION_SYSTEM);
		IFile fieldOfActivityRepositoryFile = FileAndFolderManagement.retrieveFileWithExtension(folder, FILEEXTENSION_FIELDOFACTIVITYANNOTATIONS);
		IFile internalModFile = FileAndFolderManagement.retrieveFileWithExtension(folder, FILEEXTENSION_INTERNALMODIFICATIONMARK);
		IFile cidepfile = FileAndFolderManagement.retrieveFileWithExtension(folder, FILEEXTENSION_COMPONENTINTERNALDEPENDENCIES);

		Repository repository = null;
		System system = null;
		FieldOfActivityAnnotationRepository fieldOfActivityRepository = null;
		ModificationRepository internalModificationMarkRepository = null;
		ComponentInternalDependencyRepository componentInternalDependencyRepository = null;
		
		if (repositoryfile != null && repositoryfile.exists())
			repository = (Repository)loadEmfModelFromResource(repositoryfile.getFullPath().toString(), null, loadResourceSet);
		if (systemfile != null && systemfile.exists())
			system = (System)loadEmfModelFromResource(systemfile.getFullPath().toString(), null, loadResourceSet);
		if (fieldOfActivityRepositoryFile != null && fieldOfActivityRepositoryFile.exists())
			fieldOfActivityRepository = (FieldOfActivityAnnotationRepository)loadEmfModelFromResource(fieldOfActivityRepositoryFile.getFullPath().toString(), null, loadResourceSet);
		if (internalModFile != null && internalModFile.exists())
			internalModificationMarkRepository = (ModificationRepository)loadEmfModelFromResource(internalModFile.getFullPath().toString(), null, loadResourceSet);
		if (cidepfile != null && cidepfile.exists())
			componentInternalDependencyRepository = (ComponentInternalDependencyRepository)loadEmfModelFromResource(cidepfile.getFullPath().toString(), null, loadResourceSet);
		
		return new ArchitectureVersion(versionname, repository, system, fieldOfActivityRepository, internalModificationMarkRepository, componentInternalDependencyRepository);
	}	
	
	private static void saveEmfModelToResource(EObject model, String directoryPath, String fileName, ResourceSet resourceSet) {
        if (directoryPath != null) {
        	URI saveURI = URI.createPlatformResourceURI(directoryPath, true);

        	if (fileName != null) {
        		saveURI = saveURI.appendSegment(fileName);
        	} else if (model.eResource()!=null) {
        		String segment = model.eResource().getURI().lastSegment();
        		saveURI = saveURI.appendSegment(segment);
        	}
        	
            Resource resource = resourceSet.createResource(saveURI);

            Map saveOptions = setupLoadOptions(resource);
            
            resource.getContents().add(model);
            
            try {
                resource.save(saveOptions);
            } catch (IOException e) {
                throw new RuntimeException("Saving of resource failed", e);
            } finally {

            }
        }
    }
	
	private static EObject loadEmfModelFromResource(String folderPath, String filePath, ResourceSet resourceSet) {
    	resourceSet.getPackageRegistry().put(RepositoryPackage.eNS_URI, RepositoryPackage.eINSTANCE);
        resourceSet.getPackageRegistry().put(SystemPackage.eNS_URI, SystemPackage.eINSTANCE);
        resourceSet.getPackageRegistry().put(FieldofactivityannotationsPackage.eNS_URI, FieldofactivityannotationsPackage.eINSTANCE);
        resourceSet.getPackageRegistry().put(ComponentInternalDependenciesPackage.eNS_URI, ComponentInternalDependenciesPackage.eINSTANCE);
        resourceSet.getPackageRegistry().put(modificationmarksPackage.eNS_URI, modificationmarksPackage.eINSTANCE);
    	
    	URI loadURI = URI.createPlatformResourceURI(folderPath, true);
    	if (filePath!=null)
    		loadURI = loadURI.appendSegment(filePath);	
        
        try {

        	Resource resource = ModelUtils.createResource(loadURI, resourceSet);
        	
        	((ResourceImpl) resource).setIntrinsicIDToEObjectMap(new HashMap());
        	
        	Map loadOptions = setupLoadOptions(resource);

        	resource.load(loadOptions);
        	
        	if (!resource.getContents().isEmpty())
        		return resource.getContents().get(0);
		} catch (IOException e) {
			//throw new RuntimeException(e);
			return null;
		}
        
        return null;
    }
	
	private static Map setupLoadOptions(Resource resource) {
		Map loadOptions = ((XMLResourceImpl)resource).getDefaultLoadOptions();
        loadOptions.put(XMLResource.OPTION_DEFER_ATTACHMENT, Boolean.TRUE);
        loadOptions.put(XMLResource.OPTION_DEFER_IDREF_RESOLUTION, Boolean.TRUE);
        loadOptions.put(XMLResource.OPTION_USE_DEPRECATED_METHODS, Boolean.TRUE);
        loadOptions.put(XMLResource.OPTION_USE_PARSER_POOL, new XMLParserPoolImpl());
        loadOptions.put(XMLResource.OPTION_USE_XML_NAME_TO_FEATURE_MAP, new HashMap());
        return loadOptions;
	}
	
	public static ArchitectureVersion saveAsAndReload(
			ArchitectureVersion saveVersion, String targetfolderpath, String filename) {
		try {
			ArchitectureVersionPersistency.save(targetfolderpath, filename, saveVersion);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		ArchitectureVersion loadVersion = null;
		try {
			loadVersion = ArchitectureVersionPersistency.load(targetfolderpath, filename, filename);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
		
		return loadVersion;
	}
	
	public static ArchitectureVersion createArchitectureVersionClone(
			ArchitectureVersion baseversion, String targetfolderpath, String targetfilename) {
		
		ArchitectureVersion targetversion = ArchitectureVersionPersistency.saveAsAndReload(baseversion, targetfolderpath, targetfilename);
		
		return targetversion;
	}

	private static void saveActivitiesToFile(String filename, List<Activity> activityList, String prefix) {
		java.io.File file = new java.io.File(filename);
		
		if (file.getParentFile() != null && !file.getParentFile().mkdirs()) {
		    /* handle permission problems here */
		}
		
		try {
			boolean answer = file.createNewFile();
			java.lang.System.out.println(answer);
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		
    	FileWriter  writer = null;

    	try {
			writer = new FileWriter(file);
			if (writer != null) {
				saveActivitiesToFile(writer, activityList, prefix);
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
    private static void saveActivitiesToFile(FileWriter writer, List<Activity> activityList, String prefix) {
    	try {
	    	if (writer != null) {
	    		if (prefix==null)
	    			prefix="";
	    		
	    		for (Activity activity : activityList) {
	    			writer.write(prefix + " " + activity.getBasicActivity() + " " + activity.getElementType() + " " + activity.getElementName());
	    			writer.write("\n");
	    			if (!activity.getSubactivities().isEmpty()) {
	    				saveActivitiesToFile(writer, activity.getSubactivities(), prefix + "=");
	    			}
	    			if (!activity.getFollowupActivities().isEmpty()) {
	    				saveActivitiesToFile(writer, activity.getFollowupActivities(), prefix+"->");
	    			}
	    		}
	    		
	    		writer.flush();
	    	}
    	} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
    
    

	
}
