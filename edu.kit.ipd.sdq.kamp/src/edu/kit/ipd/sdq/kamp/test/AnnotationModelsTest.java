package edu.kit.ipd.sdq.kamp.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uka.ipd.sdq.componentInternalDependencies.RoleToRoleDependency;
import de.uka.ipd.sdq.pcm.core.composition.AssemblyConnector;
import de.uka.ipd.sdq.pcm.core.composition.AssemblyContext;
import de.uka.ipd.sdq.pcm.core.composition.Connector;
import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.OperationInterface;
import de.uka.ipd.sdq.pcm.repository.OperationProvidedRole;
import de.uka.ipd.sdq.pcm.repository.ProvidedRole;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import de.uka.ipd.sdq.pcm.repository.RequiredRole;
import edu.kit.ipd.sdq.kamp.core.ArchitectureAnnotationFactory;
import edu.kit.ipd.sdq.kamp.core.ArchitectureAnnotationLookup;
import edu.kit.ipd.sdq.kamp.core.ArchitectureModelFactoryFacade;
import edu.kit.ipd.sdq.kamp.core.ArchitectureModelLookup;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersion;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersionPersistency;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.AcceptanceTestCase;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.BuildConfiguration;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.IntegrationTestCase;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.IntegrationTestCaseAggregation;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.MetadataFile;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.MetadataFileAggregation;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.ReleaseConfiguration;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.RuntimeInstance;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.RuntimeInstanceAggregation;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.SourceFile;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.SourceFileAggregation;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.TestCaseAggregation;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.ThirdPartyComponentOrLibrary;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.UnitTestCase;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.UnitTestCaseAggregation;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyComponent;

public class AnnotationModelsTest {

	public static final String TESTNAME = "AnnotationModelsTest";

	ArchitectureVersion baseArchitectureVersion;
	ArchitectureVersion targetArchitectureVersion;
	
	@Before
	public void setUp() throws Exception {
		TestPathProvider.resetTestProject(TESTNAME);

		baseArchitectureVersion = setupBasePCMModel("basemodel");
		//ArchitectureVersionPersistency.save(baseArchitectureVersion.getName(),  baseArchitectureVersion);
		
		targetArchitectureVersion = ArchitectureVersionPersistency.saveAsAndReload(baseArchitectureVersion, TestPathProvider.getTestPath(TESTNAME)+"/target", "targetmodel");
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
	public void testInternalModificationMark_CreationAndLookUp() {
		BasicComponent client = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(baseArchitectureVersion, "Client");
		
		ArchitectureModelFactoryFacade.assignInternalModificationMarkToComponent(baseArchitectureVersion, client);

		List<ModifyComponent> internalModificationMarks = ArchitectureModelLookup.lookUpModificationMarksForComponent(baseArchitectureVersion, client);
		
		assertTrue("Number of InternalModificationMarks not one", internalModificationMarks.size()==1);	
		assertTrue("InternalModificationMark not found", internalModificationMarks.get(0)!=null);	
		assertTrue("InternalModificationMark not assigned to expected component", internalModificationMarks.get(0).getComponent()==client);	
	
		BasicComponent server = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(baseArchitectureVersion, "Server");
		List<ModifyComponent> internalModificationMarkServer = ArchitectureModelLookup.lookUpModificationMarksForComponent(baseArchitectureVersion, server);
		assertTrue("Lookup of missing internal modification mark not resulted in empty list", internalModificationMarkServer.isEmpty());	
	}

	@Test 
	public void testInternalModificationMark_OnlyOneMarkPerComponent() {
		BasicComponent client = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(baseArchitectureVersion, "Client");
		ArchitectureModelFactoryFacade.assignInternalModificationMarkToComponent(baseArchitectureVersion, client);
		ArchitectureModelFactoryFacade.assignInternalModificationMarkToComponent(baseArchitectureVersion, client);
		List<ModifyComponent> internalModificationMarks = ArchitectureModelLookup.lookUpModificationMarksForComponent(baseArchitectureVersion, client);
		assertTrue("Number of InternalModificationMarks not one", internalModificationMarks.size()==1);	
	}

	@Test 
	public void testInternalModificationMark_ProvidedRoleMarking() {
		BasicComponent client = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(baseArchitectureVersion, "Client");
		ArchitectureModelFactoryFacade.assignInternalModificationMarkToProvidedRoleOfComponent(baseArchitectureVersion, client.getProvidedRoles_InterfaceProvidingEntity().get(0));
		List<ProvidedRole> providedRoles = ArchitectureModelLookup.lookUpMarkedProvidedRoles(baseArchitectureVersion);
		assertTrue("Number of marked provided roles not one", providedRoles.size()==1);	
		assertTrue("Provided role not retrieved properly", providedRoles.get(0)==client.getProvidedRoles_InterfaceProvidingEntity().get(0));	
	}

	@Test 
	public void testInternalModificationMark_AssemblyConnectorMarking() {
		BasicComponent client = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(baseArchitectureVersion, "Client");
		BasicComponent server = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(baseArchitectureVersion, "Server");

		List<AssemblyContext> assemblyContextsClient = ArchitectureModelLookup.lookUpAssemblyContextsForRepositoryComponent(baseArchitectureVersion, client);
		assertTrue("Not exactly ONE assembly context found for client component", assemblyContextsClient.size()==1);
		AssemblyContext assemblyContextClient = assemblyContextsClient.get(0);
		List<AssemblyContext> assemblyContextsServer = ArchitectureModelLookup.lookUpAssemblyContextsForRepositoryComponent(baseArchitectureVersion, server);
		assertTrue("Not exactly ONE assembly context found for server component", assemblyContextsServer.size()==1);
		AssemblyContext assemblyContextServer = assemblyContextsServer.get(0);

		List<AssemblyConnector> assemblyConnectors = ArchitectureModelLookup.lookUpAssemblyConnectorsBetweenAssemblyContexts(assemblyContextServer, assemblyContextClient);
		
		assertTrue("Not exactly ONE assembly connector found between assembly contexts of client und server component", assemblyConnectors.size()==1);
		
		ArchitectureModelFactoryFacade.assignInternalModificationMarkToAssemblyConnector(baseArchitectureVersion, assemblyConnectors.get(0));

		List<AssemblyConnector> markedAssemblyConnectors =  ArchitectureModelLookup.lookUpMarkedAssemblyConnectors(baseArchitectureVersion);
		assertTrue("Number of marked assembly connectors not one", markedAssemblyConnectors.size()==1);	
		assertTrue("Marked assembly connector not retrieved properly", assemblyConnectors.get(0)==markedAssemblyConnectors.get(0));	
	}
	
	@Test
	public void testCreationAndLookUpOfSourceFileAnnotations() {
		BasicComponent client = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Client");
		BasicComponent server = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		ArchitectureAnnotationFactory.createSourceFileAnnotation(targetArchitectureVersion, client, "client.java", "Java");
		ArchitectureAnnotationFactory.createSourceFileAnnotation(targetArchitectureVersion, server, "server.cpp", "C++");
		
		List<SourceFile> clientFiles = ArchitectureAnnotationLookup.lookUpSourceFilesForComponent(targetArchitectureVersion, client);
		
		assertTrue("Client-SourceFile not retrieved properly", clientFiles.size()==1 && clientFiles.get(0).getFilename().equals("client.java") && clientFiles.get(0).getTechnology().equals("Java"));
		
		List<SourceFile> serverFiles = ArchitectureAnnotationLookup.lookUpSourceFilesForComponent(targetArchitectureVersion, server);

		assertTrue("Server-SourceFile not retrieved properly", serverFiles.size()==1 && serverFiles.get(0).getFilename().equals("server.cpp") && serverFiles.get(0).getTechnology().equals("C++"));
	}

	@Test
	public void testCreationAndLookUpOfSourceFileAggregationAnnotations() {
		BasicComponent client = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Client");
		BasicComponent server = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		ArchitectureAnnotationFactory.createSourceFileAggregationAnnotation(targetArchitectureVersion, client, 10, "Java");
		ArchitectureAnnotationFactory.createSourceFileAggregationAnnotation(targetArchitectureVersion, server, 20, "C++");
		
		SourceFileAggregation clientFileAggregation = ArchitectureAnnotationLookup.lookUpSourceFileAggregationForComponent(targetArchitectureVersion, client);
		
		assertTrue("Client-SourceFileAggregation not retrieved properly", clientFileAggregation!=null && clientFileAggregation.getNumberOfFiles()==10 && clientFileAggregation.getTechnology().equals("Java"));
		
		SourceFileAggregation serverFileAggregation= ArchitectureAnnotationLookup.lookUpSourceFileAggregationForComponent(targetArchitectureVersion, server);

		assertTrue("Server-SourceFileAggregation not retrieved properly", serverFileAggregation!=null && serverFileAggregation.getNumberOfFiles()==20 && serverFileAggregation.getTechnology().equals("C++"));
	}

	@Test
	public void testCreationAndLookUpOfMetadataFileAnnotations() {
		BasicComponent client = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Client");
		BasicComponent server = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		ArchitectureAnnotationFactory.createMetadataFileAnnotation(targetArchitectureVersion, client, "schema.dat", "DBS");
		ArchitectureAnnotationFactory.createMetadataFileAnnotation(targetArchitectureVersion, server, "hibernate.map", "HBM");
		
		List<MetadataFile> clientFiles = ArchitectureAnnotationLookup.lookUpMetadataFilesForComponent(targetArchitectureVersion, client);
		
		assertTrue("Client-MetadataFile not retrieved properly", clientFiles.size()==1 && clientFiles.get(0).getFilename().equals("schema.dat") && clientFiles.get(0).getTechnology().equals("DBS"));
		
		List<MetadataFile> serverFiles = ArchitectureAnnotationLookup.lookUpMetadataFilesForComponent(targetArchitectureVersion, server);

		assertTrue("Server-MetadataFile not retrieved properly", serverFiles.size()==1 && serverFiles.get(0).getFilename().equals("hibernate.map") && serverFiles.get(0).getTechnology().equals("HBM"));
	}

	@Test
	public void testCreationAndLookUpOfMetadataFileAggregationAnnotations() {
		BasicComponent client = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Client");
		BasicComponent server = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		ArchitectureAnnotationFactory.createMetadataFileAggregationAnnotation(targetArchitectureVersion, client, 10, "DBS");
		ArchitectureAnnotationFactory.createMetadataFileAggregationAnnotation(targetArchitectureVersion, server, 20, "HBM");
		
		MetadataFileAggregation clientFileAggregation = ArchitectureAnnotationLookup.lookUpMetadataFileAggregationForComponent(targetArchitectureVersion, client);
		
		assertTrue("Client-MetadataFileAggregation not retrieved properly", clientFileAggregation!=null && clientFileAggregation.getNumberOfFiles()==10 && clientFileAggregation.getTechnology().equals("DBS"));
		
		MetadataFileAggregation serverFileAggregation= ArchitectureAnnotationLookup.lookUpMetadataFileAggregationForComponent(targetArchitectureVersion, server);

		assertTrue("Server-MetadataFileAggregation not retrieved properly", serverFileAggregation!=null && serverFileAggregation.getNumberOfFiles()==20 && serverFileAggregation.getTechnology().equals("HBM"));
	}

	@Test
	public void testCreationAndLookUpOfUnitTestAggregationAnnotations() {
		BasicComponent client = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Client");
		BasicComponent server = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		ArchitectureAnnotationFactory.createUnitTestAggregation(targetArchitectureVersion, client.getProvidedRoles_InterfaceProvidingEntity().get(0), 10, "ClientTest.java");
		ArchitectureAnnotationFactory.createUnitTestAggregation(targetArchitectureVersion, server.getProvidedRoles_InterfaceProvidingEntity().get(0), 20, "ServerTest.java");
		
		UnitTestCaseAggregation clientTestAggregation = ArchitectureAnnotationLookup.lookUpUnitTestAggregationForProvidedRole(targetArchitectureVersion, client.getProvidedRoles_InterfaceProvidingEntity().get(0));
		
		assertTrue("Client-UnitTestCaseAggregation not retrieved properly", clientTestAggregation!=null && clientTestAggregation.getNumberOfTestcases()==10 && clientTestAggregation.getNameOfTestSuite().equals("ClientTest.java"));
		
		UnitTestCaseAggregation serverTestAggregation = ArchitectureAnnotationLookup.lookUpUnitTestAggregationForProvidedRole(targetArchitectureVersion, server.getProvidedRoles_InterfaceProvidingEntity().get(0));

		assertTrue("Server-UnitTestCaseAggregation not retrieved properly", serverTestAggregation!=null && serverTestAggregation.getNumberOfTestcases()==20 && serverTestAggregation.getNameOfTestSuite().equals("ServerTest.java"));
	}
	
	@Test
	public void testCreationAndLookUpOfUnitTestCaseAnnotations() {
		BasicComponent client = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Client");
		BasicComponent server = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		ArchitectureAnnotationFactory.createUnitTestCase(targetArchitectureVersion, (OperationProvidedRole) client.getProvidedRoles_InterfaceProvidingEntity().get(0), "testClientA");
		ArchitectureAnnotationFactory.createUnitTestCase(targetArchitectureVersion, (OperationProvidedRole) client.getProvidedRoles_InterfaceProvidingEntity().get(0), "testClientB");
		ArchitectureAnnotationFactory.createUnitTestCase(targetArchitectureVersion, (OperationProvidedRole) server.getProvidedRoles_InterfaceProvidingEntity().get(0), "testServerA");
		ArchitectureAnnotationFactory.createUnitTestCase(targetArchitectureVersion, (OperationProvidedRole) server.getProvidedRoles_InterfaceProvidingEntity().get(0), "testServerB");
		ArchitectureAnnotationFactory.createUnitTestCase(targetArchitectureVersion, (OperationProvidedRole) server.getProvidedRoles_InterfaceProvidingEntity().get(0), "testServerC");
		
		List<UnitTestCase> clientTests = ArchitectureAnnotationLookup.lookUpUnitTestCasesForProvidedRole(targetArchitectureVersion, client.getProvidedRoles_InterfaceProvidingEntity().get(0));
		
		assertTrue("Client-UnitTestCases not retrieved properly", clientTests.size()==2 && clientTests.get(0).getNameOfTest().equals("testClientA") && clientTests.get(1).getNameOfTest().equals("testClientB"));
		
		List<UnitTestCase> serverTests = ArchitectureAnnotationLookup.lookUpUnitTestCasesForProvidedRole(targetArchitectureVersion, server.getProvidedRoles_InterfaceProvidingEntity().get(0));

		assertTrue("Client-UnitTestCases not retrieved properly", serverTests.size()==3 && serverTests.get(0).getNameOfTest().equals("testServerA") && serverTests.get(1).getNameOfTest().equals("testServerB") && serverTests.get(2).getNameOfTest().equals("testServerC"));
	}

	@Test
	public void testCreationAndLookUpOfAcceptanceTestCaseAnnotations() {
		BasicComponent client = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Client");
		BasicComponent server = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		ArchitectureAnnotationFactory.createAcceptanceTestCase(targetArchitectureVersion, (OperationProvidedRole) client.getProvidedRoles_InterfaceProvidingEntity().get(0), "testClientA");
		ArchitectureAnnotationFactory.createAcceptanceTestCase(targetArchitectureVersion, (OperationProvidedRole) client.getProvidedRoles_InterfaceProvidingEntity().get(0), "testClientB");
		ArchitectureAnnotationFactory.createAcceptanceTestCase(targetArchitectureVersion, (OperationProvidedRole) server.getProvidedRoles_InterfaceProvidingEntity().get(0), "testServerA");
		ArchitectureAnnotationFactory.createAcceptanceTestCase(targetArchitectureVersion, (OperationProvidedRole) server.getProvidedRoles_InterfaceProvidingEntity().get(0), "testServerB");
		ArchitectureAnnotationFactory.createAcceptanceTestCase(targetArchitectureVersion, (OperationProvidedRole) server.getProvidedRoles_InterfaceProvidingEntity().get(0), "testServerC");
		
		List<AcceptanceTestCase> clientTests = ArchitectureAnnotationLookup.lookUpAcceptanceTestCasesForProvidedRole(targetArchitectureVersion, client.getProvidedRoles_InterfaceProvidingEntity().get(0));
		
		assertTrue("Client-AcceptanceTestCases not retrieved properly", clientTests.size()==2 && clientTests.get(0).getNameOfTest().equals("testClientA") && clientTests.get(1).getNameOfTest().equals("testClientB"));
		
		List<AcceptanceTestCase> serverTests = ArchitectureAnnotationLookup.lookUpAcceptanceTestCasesForProvidedRole(targetArchitectureVersion, server.getProvidedRoles_InterfaceProvidingEntity().get(0));

		assertTrue("Client-AcceptanceTestCases not retrieved properly", serverTests.size()==3 && serverTests.get(0).getNameOfTest().equals("testServerA") && serverTests.get(1).getNameOfTest().equals("testServerB") && serverTests.get(2).getNameOfTest().equals("testServerC"));
	}
	
	@Test
	public void testCreationAndLookUpOfIntegrationTestAggregationAnnotations() {
		BasicComponent server = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		BasicComponent database = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Database");
		
		List<AssemblyConnector> assemblyConnectors = ArchitectureModelLookup.lookUpAssemblyConnectors(targetArchitectureVersion);
		List<AssemblyConnector> assemblyConnectorsClientServer = ArchitectureModelLookup.selectAssemblyConnectorsOnProvidedRole(assemblyConnectors, server.getProvidedRoles_InterfaceProvidingEntity().get(0));
		List<AssemblyConnector> assemblyConnectorsServerDatabase = ArchitectureModelLookup.selectAssemblyConnectorsOnProvidedRole(assemblyConnectors, database.getProvidedRoles_InterfaceProvidingEntity().get(0));
		
		AssemblyConnector assemblyConnectorClientServer = assemblyConnectorsClientServer.get(0);
		AssemblyConnector assemblyConnectorServerDatabase = assemblyConnectorsServerDatabase.get(0);
		
		ArchitectureAnnotationFactory.createIntegrationTestAggregation(targetArchitectureVersion, assemblyConnectorClientServer, 30, "ClientServerIntegrationTest.java");
		ArchitectureAnnotationFactory.createIntegrationTestAggregation(targetArchitectureVersion, assemblyConnectorServerDatabase, 40, "ServerDatabaseIntegrationTest.java");
		
		IntegrationTestCaseAggregation clientServerTestAggregation = ArchitectureAnnotationLookup.lookUpIntegrationTestAggregationForAssemblyConnector(targetArchitectureVersion, assemblyConnectorClientServer);
		
		assertTrue("ClientServer-IntegrationTestCaseAggregation not retrieved properly", clientServerTestAggregation!=null && clientServerTestAggregation.getNumberOfTestcases()==30 && clientServerTestAggregation.getNameOfTestSuite().equals("ClientServerIntegrationTest.java"));
		
		IntegrationTestCaseAggregation serverDatabaseTestAggregation = ArchitectureAnnotationLookup.lookUpIntegrationTestAggregationForAssemblyConnector(targetArchitectureVersion, assemblyConnectorServerDatabase);

		assertTrue("ServerDatabase-IntegrationTestCaseAggregation not retrieved properly", serverDatabaseTestAggregation!=null && serverDatabaseTestAggregation.getNumberOfTestcases()==40 && serverDatabaseTestAggregation.getNameOfTestSuite().equals("ServerDatabaseIntegrationTest.java"));
	}
	
	@Test
	public void testCreationAndLookUpOfIntegrationTestCaseAnnotations() {
		BasicComponent server = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		BasicComponent database = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Database");
		
		List<AssemblyConnector> assemblyConnectors = ArchitectureModelLookup.lookUpAssemblyConnectors(targetArchitectureVersion);
		List<AssemblyConnector> assemblyConnectorsClientServer = ArchitectureModelLookup.selectAssemblyConnectorsOnProvidedRole(assemblyConnectors, server.getProvidedRoles_InterfaceProvidingEntity().get(0));
		List<AssemblyConnector> assemblyConnectorsServerDatabase = ArchitectureModelLookup.selectAssemblyConnectorsOnProvidedRole(assemblyConnectors, database.getProvidedRoles_InterfaceProvidingEntity().get(0));
		
		AssemblyConnector assemblyConnectorClientServer = assemblyConnectorsClientServer.get(0);
		AssemblyConnector assemblyConnectorServerDatabase = assemblyConnectorsServerDatabase.get(0);
		
		ArchitectureAnnotationFactory.createIntegrationTestCase(targetArchitectureVersion, assemblyConnectorClientServer, "testClientA");
		ArchitectureAnnotationFactory.createIntegrationTestCase(targetArchitectureVersion, assemblyConnectorClientServer, "testClientB");
		ArchitectureAnnotationFactory.createIntegrationTestCase(targetArchitectureVersion, assemblyConnectorServerDatabase, "testServerA");
		ArchitectureAnnotationFactory.createIntegrationTestCase(targetArchitectureVersion, assemblyConnectorServerDatabase, "testServerB");
		ArchitectureAnnotationFactory.createIntegrationTestCase(targetArchitectureVersion, assemblyConnectorServerDatabase, "testServerC");
		
		List<IntegrationTestCase> clientServerTests = ArchitectureAnnotationLookup.lookUpIntegrationTestCasesForAssemblyConnector(targetArchitectureVersion, assemblyConnectorClientServer);
		
		assertTrue("Client-IntegrationTestCases not retrieved properly", clientServerTests.size()==2 && clientServerTests.get(0).getNameOfTest().equals("testClientA") && clientServerTests.get(1).getNameOfTest().equals("testClientB"));
		
		List<IntegrationTestCase> serverDatabaseTests = ArchitectureAnnotationLookup.lookUpIntegrationTestCasesForAssemblyConnector(targetArchitectureVersion, assemblyConnectorServerDatabase);

		assertTrue("Client-IntegrationTestCases not retrieved properly", serverDatabaseTests.size()==3 && serverDatabaseTests.get(0).getNameOfTest().equals("testServerA") && serverDatabaseTests.get(1).getNameOfTest().equals("testServerB") && serverDatabaseTests.get(2).getNameOfTest().equals("testServerC"));
	}
	
	@Test
	public void testCreationAndLookUpOfThirdPartyOrLibraryAnnotations() {
		BasicComponent database = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Database");

		ArchitectureAnnotationFactory.createThirdPartyOrLibraryMarker(targetArchitectureVersion, database, "MySQL.jar", "MySQL-Database");
		
		ThirdPartyComponentOrLibrary annotation = ArchitectureAnnotationLookup.lookUpThirdPartyOrLibraryAnnotationForComponent(targetArchitectureVersion, database);

		assertTrue("ThirdPartyComponentOrLibrary-Annotation not retrieved properly", annotation!=null && annotation.getComponent()==database && annotation.getFilename().equals("MySQL.jar") && annotation.getTechnology().equals("MySQL-Database"));
	}

	@Test
	public void testCreationAndLookUpOfBuildConfigurationAnnotations() {
		BasicComponent client = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Client");
		BasicComponent server = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		BasicComponent database = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Database");

		ArchitectureAnnotationFactory.createBuildConfiguration(targetArchitectureVersion, new RepositoryComponent[]{client, server}, "build.ant", "ant");
		
		assertTrue("Number of build configurations not 1", targetArchitectureVersion.getFieldOfActivityRepository().getBuildSpecification().getBuildConfigurations().size()==1);
		
		BuildConfiguration buildConfigurationClient = ArchitectureAnnotationLookup.lookUpBuildConfigurationForComponent(targetArchitectureVersion, client);

		assertTrue("BuildConfiguration-Annotation for Client not retrieved properly", buildConfigurationClient!=null && buildConfigurationClient.getComponent().contains(client) && buildConfigurationClient.getFilename().equals("build.ant") && buildConfigurationClient.getTechnology().equals("ant"));

		BuildConfiguration buildConfigurationServer = ArchitectureAnnotationLookup.lookUpBuildConfigurationForComponent(targetArchitectureVersion, server);

		assertTrue("BuildConfiguration-Annotation for Server not retrieved properly", buildConfigurationServer!=null && buildConfigurationServer.getComponent().contains(server) && buildConfigurationServer.getFilename().equals("build.ant") && buildConfigurationServer.getTechnology().equals("ant"));

		assertTrue("Build configurations are not identical", buildConfigurationClient==buildConfigurationServer);

		BuildConfiguration buildConfigurationDatabase = ArchitectureAnnotationLookup.lookUpBuildConfigurationForComponent(targetArchitectureVersion, database);

		assertTrue("Non-existing build configuration lookup not resulted in null.", buildConfigurationDatabase==null);

	}
	
	@Test
	public void testCreationAndLookUpOfReleaseConfigurationAnnotations() {
		BasicComponent client = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Client");
		BasicComponent server = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		BasicComponent database = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Database");

		ArchitectureAnnotationFactory.createReleaseConfiguration(targetArchitectureVersion, new RepositoryComponent[]{client}, "client.jar", "Client-Release");
		ArchitectureAnnotationFactory.createReleaseConfiguration(targetArchitectureVersion, new RepositoryComponent[]{server}, "server.jar", "Server-Release");
		
		assertTrue("Number of release configurations not 2", targetArchitectureVersion.getFieldOfActivityRepository().getReleaseSpecification().getReleaseConfigurations().size()==2);
		
		ReleaseConfiguration releaseConfigurationClient = ArchitectureAnnotationLookup.lookUpReleaseConfigurationForComponent(targetArchitectureVersion, client);

		assertTrue("ReleaseConfiguration-Annotation for Client not retrieved properly", releaseConfigurationClient!=null && releaseConfigurationClient.getComponents().contains(client) && releaseConfigurationClient.getPathname().equals("client.jar") && releaseConfigurationClient.getDescription().equals("Client-Release"));

		ReleaseConfiguration releaseConfigurationServer = ArchitectureAnnotationLookup.lookUpReleaseConfigurationForComponent(targetArchitectureVersion, server);

		assertTrue("ReleaseConfiguration-Annotation for Server not retrieved properly", releaseConfigurationServer!=null && releaseConfigurationServer.getComponents().contains(server) && releaseConfigurationServer.getPathname().equals("server.jar") && releaseConfigurationServer.getDescription().equals("Server-Release"));

		assertTrue("ReleaseConfigurations are identical", releaseConfigurationClient!=releaseConfigurationServer);

		ReleaseConfiguration releaseConfigurationDatabase = ArchitectureAnnotationLookup.lookUpReleaseConfigurationForComponent(targetArchitectureVersion, database);

		assertTrue("Non-existing release configuration lookup not resulted in null.", releaseConfigurationDatabase==null);
	}
	
	@Test
	public void testCreationAndLookUpOfRuntimeInstanceAggregationAnnotations() {
		BasicComponent client = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Client");
		BasicComponent server = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		
		ArchitectureAnnotationFactory.createRuntimeInstanceAggregation(targetArchitectureVersion, new RepositoryComponent[]{client}, 100, "Kunden-Clients");
		ArchitectureAnnotationFactory.createRuntimeInstanceAggregation(targetArchitectureVersion, new RepositoryComponent[]{server}, 1, "Zentralserver");
			
		RuntimeInstanceAggregation clientInstanceAggregation = ArchitectureAnnotationLookup.lookUpRuntimeInstanceAggregation(targetArchitectureVersion, client);
		
		assertTrue("Client-RuntimeInstanceAggregation not retrieved properly", clientInstanceAggregation!=null && clientInstanceAggregation.getComponents().contains(client) && clientInstanceAggregation.getDescription().equals("Kunden-Clients") && clientInstanceAggregation.getNumberOfInstances()==100);
		
		RuntimeInstanceAggregation serverInstanceAggregation = ArchitectureAnnotationLookup.lookUpRuntimeInstanceAggregation(targetArchitectureVersion, server);
		
		assertTrue("Server-RuntimeInstanceAggregation not retrieved properly", serverInstanceAggregation!=null && serverInstanceAggregation.getComponents().contains(server) && serverInstanceAggregation.getDescription().equals("Zentralserver") && serverInstanceAggregation.getNumberOfInstances()==1);
	}
	
	@Test
	public void testCreationAndLookUpOfRuntimeInstancesAnnotations() {
		BasicComponent client = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Client");
		BasicComponent server = (BasicComponent)ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		
		ArchitectureAnnotationFactory.createRuntimeInstance(targetArchitectureVersion, new RepositoryComponent[]{client}, "Kunden-Client 1", "Descrp1");
		ArchitectureAnnotationFactory.createRuntimeInstance(targetArchitectureVersion, new RepositoryComponent[]{client}, "Kunden-Client 2", "Descrp2");
		ArchitectureAnnotationFactory.createRuntimeInstance(targetArchitectureVersion, new RepositoryComponent[]{server}, "Zentralserver", "Descrp3");
			
		List<RuntimeInstance> clientInstances = ArchitectureAnnotationLookup.lookUpRuntimeInstances(targetArchitectureVersion, client);
		
		assertTrue("Client-RuntimeInstances not retrieved properly", clientInstances.size()==2 && clientInstances.get(0).getComponents().contains(client) && clientInstances.get(0).getName().equals("Kunden-Client 1"));
		
		List<RuntimeInstance> serverInstances = ArchitectureAnnotationLookup.lookUpRuntimeInstances(targetArchitectureVersion, server);
		
		assertTrue("Server-RuntimeInstances not retrieved properly", serverInstances.size()==1 && serverInstances.get(0).getComponents().contains(server) && serverInstances.get(0).getName().equals("Zentralserver") && serverInstances.get(0).getDescription().equals("Descrp3"));
	}


}
