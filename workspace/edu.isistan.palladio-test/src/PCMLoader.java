import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import de.uka.ipd.sdq.pcm.allocation.Allocation;
import de.uka.ipd.sdq.pcm.allocation.AllocationContext;
import de.uka.ipd.sdq.pcm.allocation.AllocationPackage;
import de.uka.ipd.sdq.pcm.repository.Interface;
import de.uka.ipd.sdq.pcm.repository.ProvidedRole;
import de.uka.ipd.sdq.pcm.repository.Repository;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import de.uka.ipd.sdq.pcm.repository.RepositoryPackage;
import de.uka.ipd.sdq.pcm.resourceenvironment.LinkingResource;
import de.uka.ipd.sdq.pcm.resourceenvironment.ResourceContainer;
import de.uka.ipd.sdq.pcm.resourceenvironment.ResourceEnvironment;
import de.uka.ipd.sdq.pcm.resourceenvironment.ResourceenvironmentPackage;
import de.uka.ipd.sdq.pcm.system.SystemPackage;

public class PCMLoader {
	private static String dirPath = "file:///Users/alejandrorago/Documents/Implementacion/Repositorios/palladio-test/workspace/MediaStore3_Model/";
	private static String repositoryFile = dirPath + "ms.repository";
	private static String resourceEnvFile = dirPath + "ms.resourceenvironment";
	private static String baseSystemFile = dirPath + "ms_base.system";
	private static String baseAllocationFile = dirPath + "ms_base.allocation";
	
	static {
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		EPackage.Registry.INSTANCE.put(RepositoryPackage.eNS_URI, RepositoryPackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(ResourceenvironmentPackage.eNS_URI, ResourceenvironmentPackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(SystemPackage.eNS_URI, SystemPackage.eINSTANCE);
		EPackage.Registry.INSTANCE.put(AllocationPackage.eNS_URI, AllocationPackage.eINSTANCE);
	}
	
	private EObject load(String inputString) {
		URI resourceURI = URI.createURI(inputString);
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.getResource(resourceURI, true);
		EObject content = resource.getContents().get(0);
		return content;
	}
	
	public void loadRepository() {
		System.out.println();
		System.out.println("---Repository---");
		Repository repository = (Repository) this.load(repositoryFile);
		for(RepositoryComponent componentType : repository.getComponents__Repository())
			System.out.println(componentType.getEntityName());
		//for(DataType dataType : repository.getDataTypes__Repository())
			//;//
		for(Interface interfaceType : repository.getInterfaces__Repository())
			System.out.println(interfaceType.getEntityName());
	}
	
	public void loadResourceEnv() {
		System.out.println();
		System.out.println("---Resource Environment---");
		ResourceEnvironment resourceEnvironment = (ResourceEnvironment) this.load(resourceEnvFile);
		for(LinkingResource linkingResource : resourceEnvironment.getLinkingResources__ResourceEnvironment())
			System.out.println(linkingResource.getEntityName());
		for(ResourceContainer resourceContainer : resourceEnvironment.getResourceContainer_ResourceEnvironment())
			System.out.println(resourceContainer.getEntityName());
	}
	
	public void loadSystem() {
		System.out.println();
		System.out.println("---System---");
		de.uka.ipd.sdq.pcm.system.System baseSystem = (de.uka.ipd.sdq.pcm.system.System) this.load(baseSystemFile);
		for(ProvidedRole providedRole : baseSystem.getProvidedRoles_InterfaceProvidingEntity())
			System.out.println(providedRole.getEntityName());
	}
	
	public void loadAllocation() {
		System.out.println();
		System.out.println("---Allocation---");
		Allocation allocation = (Allocation) this.load(baseAllocationFile);
		for(AllocationContext allocationContext : allocation.getAllocationContexts_Allocation())
			System.out.println(allocationContext.getEntityName());
	}
	
	public PCMLoader() {
		
	}
	
	public static void main(String[] args) {
		System.out.println("---Model Processing---");
		PCMLoader loader = new PCMLoader();
		loader.loadRepository();
		loader.loadResourceEnv();
		loader.loadAllocation();
		loader.loadSystem();
	}
}
