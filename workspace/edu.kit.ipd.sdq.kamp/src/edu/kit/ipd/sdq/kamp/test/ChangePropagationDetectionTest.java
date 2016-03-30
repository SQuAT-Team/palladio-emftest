package edu.kit.ipd.sdq.kamp.test;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uka.ipd.sdq.componentInternalDependencies.RoleToRoleDependency;
import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.CompositeDataType;
import de.uka.ipd.sdq.pcm.repository.DataType;
import de.uka.ipd.sdq.pcm.repository.OperationInterface;
import de.uka.ipd.sdq.pcm.repository.OperationProvidedRole;
import de.uka.ipd.sdq.pcm.repository.OperationRequiredRole;
import de.uka.ipd.sdq.pcm.repository.OperationSignature;
import de.uka.ipd.sdq.pcm.repository.ProvidedRole;
import de.uka.ipd.sdq.pcm.repository.RequiredRole;
import edu.kit.ipd.sdq.kamp.core.ArchitectureModelFactoryFacade;
import edu.kit.ipd.sdq.kamp.core.ArchitectureModelLookup;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersion;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersionPersistency;

public class ChangePropagationDetectionTest {

	public static final String TESTNAME = "ChangePropagationDetectionTest";

	ArchitectureVersion baseArchitectureVersion;
	ArchitectureVersion targetArchitectureVersion;
	
	@Before
	public void setUp() throws Exception {
		TestPathProvider.resetTestProject(TESTNAME);
		baseArchitectureVersion = setupBasePCMModel("basemodel");
		ArchitectureVersionPersistency.save(TestPathProvider.getTestPath(TESTNAME), baseArchitectureVersion.getName(), baseArchitectureVersion);
		targetArchitectureVersion = ArchitectureVersionPersistency.saveAsAndReload(baseArchitectureVersion, TestPathProvider.getTestPath(TESTNAME), "targetmodel");
	}
	
	private static ArchitectureVersion setupBasePCMModel(String name) {
		ArchitectureVersion baseversion = ArchitectureModelFactoryFacade.createEmptyModel(name);

		BasicComponent client = ArchitectureModelFactoryFacade.createBasicComponent(baseversion, "Client");
		BasicComponent server = ArchitectureModelFactoryFacade.createBasicComponent(baseversion, "Server");
		BasicComponent database = ArchitectureModelFactoryFacade.createBasicComponent(baseversion, "Database");
		
		OperationInterface clientInterface = ArchitectureModelFactoryFacade.createInterface(baseversion, "ClientInterface");
		OperationInterface serverInterface = ArchitectureModelFactoryFacade.createInterface(baseversion, "ServerInterface");
		OperationInterface databaseInterface = ArchitectureModelFactoryFacade.createInterface(baseversion, "DatabaseInterface");
		
		ArchitectureModelFactoryFacade.createProvidedRole(client, clientInterface);
		ArchitectureModelFactoryFacade.createRequiredRole(client, serverInterface);

		ArchitectureModelFactoryFacade.createProvidedRole(server, serverInterface);
		ArchitectureModelFactoryFacade.createRequiredRole(server, databaseInterface);

		ArchitectureModelFactoryFacade.createProvidedRole(database, databaseInterface);

		ArchitectureModelFactoryFacade.createAssemblyContext(client, baseversion);
		ArchitectureModelFactoryFacade.createAssemblyContext(server, baseversion);
		ArchitectureModelFactoryFacade.createAssemblyContext(database, baseversion);
		
		ArchitectureModelFactoryFacade.createAssemblyConnector(client, server, baseversion);
		ArchitectureModelFactoryFacade.createAssemblyConnector(server, database, baseversion);
		
		CompositeDataType dataType = ArchitectureModelFactoryFacade.createCompositeDatatype(baseversion, "mydatatype");
		
		OperationSignature signature = ArchitectureModelFactoryFacade.createSignatureForInterface(clientInterface, "myOperation");
		ArchitectureModelFactoryFacade.createParameterForSignature(signature, "myParameter", dataType);
		
		return baseversion;
	}
	
	@After
	public void tearDown() {
		baseArchitectureVersion.delete();
		targetArchitectureVersion.delete();
		baseArchitectureVersion = null;
		targetArchitectureVersion = null;
	}
	
	@Test 
	public void testComponentInternalDependencies_Creation() {
		BasicComponent client = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(baseArchitectureVersion, "Client");
	
		ProvidedRole providedRole = client.getProvidedRoles_InterfaceProvidingEntity().get(0);

		RequiredRole requiredRole = client.getRequiredRoles_InterfaceRequiringEntity().get(0);

		ArchitectureModelFactoryFacade.createComponentInternalDependency(baseArchitectureVersion, providedRole, requiredRole);
		
		List<RoleToRoleDependency> dependencies = ArchitectureModelLookup.lookUpComponentInternalDependenciesForComponent(baseArchitectureVersion, client);
		
		assertTrue("Created Component Internal Dependency not found.", !dependencies.isEmpty());	
	}
	
	@Test 
	public void testComponentInternalDependencies_LookUpDependentProvidedRolesForRequiredRole() {
		BasicComponent client = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(baseArchitectureVersion, "Client");
	
		OperationInterface secondInterf = ArchitectureModelFactoryFacade.createInterface(baseArchitectureVersion, "SecondInterface");
		OperationInterface thirdInterf = ArchitectureModelFactoryFacade.createInterface(baseArchitectureVersion, "ThirdInterface");
		
		ArchitectureModelFactoryFacade.createProvidedRole(client, secondInterf);
		ArchitectureModelFactoryFacade.createProvidedRole(client, thirdInterf);
		
		ProvidedRole firstProvidedRole = client.getProvidedRoles_InterfaceProvidingEntity().get(0);
		// secondProvidedRole is omitted on purpose
		ProvidedRole thirdProvidedRole = client.getProvidedRoles_InterfaceProvidingEntity().get(2);
		RequiredRole requiredRole = client.getRequiredRoles_InterfaceRequiringEntity().get(0);

		ArchitectureModelFactoryFacade.createComponentInternalDependency(baseArchitectureVersion, firstProvidedRole, requiredRole);
		ArchitectureModelFactoryFacade.createComponentInternalDependency(baseArchitectureVersion, thirdProvidedRole, requiredRole);
		
		List<ProvidedRole> dependentProvidedRoles = ArchitectureModelLookup.lookUpDependentProvidedRolesForRequiredRole(baseArchitectureVersion, requiredRole);
		
		assertTrue("Dependent ProvidedRoles not detected properly.", 
				dependentProvidedRoles.size()==2 && 
				dependentProvidedRoles.contains(firstProvidedRole) && 
				dependentProvidedRoles.contains(thirdProvidedRole));	
	}
	
	@Test
	public void testDatatypeDependencyRetrieval() {
		DataType datatype = ArchitectureModelLookup.lookUpDatatypeByName(targetArchitectureVersion, "mydatatype");
		
		assertTrue("Datatype not found", datatype!=null);
		
		List<OperationInterface> dependentInterfaces = ArchitectureModelLookup.lookUpInterfacesWithParameterOfType(targetArchitectureVersion, datatype);
		
		OperationInterface clientInterface = (OperationInterface) ArchitectureModelLookup.lookUpInterfaceByName(targetArchitectureVersion, "ClientInterface");
		
		assertTrue("Dependent ClientInterface not retrieved properly", dependentInterfaces.contains(clientInterface));
		
	}
	
	@Test
	public void testInterfaceDependencyRetrieval() {
		OperationInterface serverInterface = (OperationInterface) ArchitectureModelLookup.lookUpInterfaceByName(targetArchitectureVersion, "ServerInterface");

		List<ProvidedRole> providedRoles = ArchitectureModelLookup.lookUpProvidedRolesWithInterface(targetArchitectureVersion, serverInterface);

		assertTrue(providedRoles.size()==1 && ((OperationProvidedRole)providedRoles.get(0)).getProvidedInterface__OperationProvidedRole()==serverInterface);
		
		List<RequiredRole> requiredRoles = ArchitectureModelLookup.lookUpRequiredRolesWithInterface(targetArchitectureVersion, serverInterface);

		assertTrue(requiredRoles.size()==1 && ((OperationRequiredRole)requiredRoles.get(0)).getRequiredInterface__OperationRequiredRole()==serverInterface);
		
	}

}
