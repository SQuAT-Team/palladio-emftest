package edu.kit.ipd.sdq.kamp.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import de.uka.ipd.sdq.pcm.core.composition.AssemblyConnector;
import de.uka.ipd.sdq.pcm.core.composition.AssemblyContext;
import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.OperationInterface;
import de.uka.ipd.sdq.pcm.repository.OperationProvidedRole;
import de.uka.ipd.sdq.pcm.repository.OperationRequiredRole;
import edu.kit.ipd.sdq.kamp.core.ArchitectureModelFactoryFacade;
import edu.kit.ipd.sdq.kamp.core.ArchitectureModelLookup;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersion;
import edu.kit.ipd.sdq.kamp.core.ProvidingRequiredRolePair;

public class ArchitectureModelFactoryTest {

	@Test
	public void testAssemblyConnectorCreation() {
		ArchitectureVersion version = ArchitectureModelFactoryFacade.createEmptyModel("test");
		
		BasicComponent provider = ArchitectureModelFactoryFacade.createBasicComponent(version, "provider");
		
		assertTrue("Provider component not properly created", provider != null && provider.getEntityName().equals("provider"));
		
		BasicComponent user = ArchitectureModelFactoryFacade.createBasicComponent(version, "user");

		assertTrue("User component not properly created", user != null && user.getEntityName().equals("user"));
		
		OperationInterface providerInterface = ArchitectureModelFactoryFacade.createInterface(version, "providerInterface");

		assertTrue("Provider interface not properly created", providerInterface != null && providerInterface.getEntityName().equals("providerInterface"));
		
		ArchitectureModelFactoryFacade.createProvidedRole(provider, providerInterface);

		assertTrue("Provided role not properly created", 
				provider.getProvidedRoles_InterfaceProvidingEntity().size()==1 && 
				((OperationProvidedRole)provider.getProvidedRoles_InterfaceProvidingEntity().get(0)).getProvidedInterface__OperationProvidedRole()==providerInterface);
		
		ArchitectureModelFactoryFacade.createRequiredRole(user, providerInterface);

		assertTrue("Required role not properly created", 
				user.getRequiredRoles_InterfaceRequiringEntity().size()==1 && 
				((OperationRequiredRole)user.getRequiredRoles_InterfaceRequiringEntity().get(0)).getRequiredInterface__OperationRequiredRole()==providerInterface);

		ArchitectureModelFactoryFacade.createAssemblyContext(provider, version);

		List<AssemblyContext> assemblyContextsForProvider = ArchitectureModelLookup.lookUpAssemblyContextsForRepositoryComponent(version, provider);
		
		assertTrue("Assembly context for provider not properly created", assemblyContextsForProvider.size()==1 && assemblyContextsForProvider.get(0).getEncapsulatedComponent__AssemblyContext()==provider);
		
		ArchitectureModelFactoryFacade.createAssemblyContext(user, version);

		List<AssemblyContext> assemblyContextsForUser = ArchitectureModelLookup.lookUpAssemblyContextsForRepositoryComponent(version, user);

		assertTrue("Assembly context for user not properly created", assemblyContextsForUser.size()==1 && assemblyContextsForUser.get(0).getEncapsulatedComponent__AssemblyContext()==user);
		
		List<ProvidingRequiredRolePair> matchingRolePairs = ArchitectureModelLookup.lookUpMatchingRolePairs(provider, user);
		
		assertTrue("Matching roles not found", matchingRolePairs.size()==1);
		assertTrue("Found providing role not from provider", matchingRolePairs.get(0).getProvidedRole()==provider.getProvidedRoles_InterfaceProvidingEntity().get(0));
		assertTrue("Found requiring role not from user", matchingRolePairs.get(0).getRequiredRole()==user.getRequiredRoles_InterfaceRequiringEntity().get(0));
		assertTrue("Found matching role do not have similar interface types", ((OperationRequiredRole)matchingRolePairs.get(0).getRequiredRole()).getRequiredInterface__OperationRequiredRole()==((OperationProvidedRole)matchingRolePairs.get(0).getProvidedRole()).getProvidedInterface__OperationProvidedRole());

		AssemblyConnector connector = ArchitectureModelFactoryFacade.createAssemblyConnector(user, provider, version);
		
		assertTrue("AssemblyConnector not found in System", version.getSystem().getConnectors__ComposedStructure().size()==1 && version.getSystem().getConnectors__ComposedStructure().get(0) instanceof AssemblyConnector);

		List<AssemblyConnector> assemblyConnectors = ArchitectureModelLookup.lookUpAssemblyConnectors(version); 
		
		assertTrue("Assembly connectors not looked up properly", assemblyConnectors.size()==1 && assemblyConnectors.get(0) == connector);		
				
		assertTrue("Provided role of Assembly connector not properly created", connector.getProvidedRole_AssemblyConnector()!=null);
		assertTrue("Required role of Assembly connector not properly created", connector.getRequiredRole_AssemblyConnector()!=null);
		assertTrue("Provided assembly context of Assembly connector not properly created", connector.getProvidingAssemblyContext_AssemblyConnector()!=null);
		assertTrue("Required assembly context of Assembly connector not properly created", connector.getRequiringAssemblyContext_AssemblyConnector()!=null);
		
	}

}
