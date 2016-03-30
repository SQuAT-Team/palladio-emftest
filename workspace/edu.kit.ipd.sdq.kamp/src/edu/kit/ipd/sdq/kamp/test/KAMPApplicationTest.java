package edu.kit.ipd.sdq.kamp.test;

import static org.junit.Assert.assertTrue;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uka.ipd.sdq.pcm.core.composition.AssemblyConnector;
import de.uka.ipd.sdq.pcm.core.composition.AssemblyContext;
import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.OperationInterface;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import edu.kit.ipd.sdq.kamp.core.Activity;
import edu.kit.ipd.sdq.kamp.core.ArchitectureModelFactoryFacade;
import edu.kit.ipd.sdq.kamp.core.ArchitectureModelLookup;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersion;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersionPersistency;
import edu.kit.ipd.sdq.kamp.core.ChangePropagationAnalysis;
import edu.kit.ipd.sdq.kamp.core.derivation.DifferenceCalculation;
import edu.kit.ipd.sdq.kamp.core.derivation.EnrichedWorkplanDerivation;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyComponent;

public class KAMPApplicationTest {
	ArchitectureVersion baseArchitectureVersion;
	
	public static final String TESTNAME = "KAMPApplicationTest";
	
	@Before
	public void setUp() throws Exception {
		TestPathProvider.resetTestProject(TESTNAME);
		
		baseArchitectureVersion = setupBasePCMModel("basemodel");
		ArchitectureVersionPersistency.save(TestPathProvider.getTestPath(TESTNAME), baseArchitectureVersion.getName(), baseArchitectureVersion);
	}

	private static ArchitectureVersion setupBasePCMModel(String name) {
		ArchitectureVersion baseversion = ArchitectureModelFactoryFacade.createEmptyModel(name);

		// base model - repository 
		
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

		// base model - system
		
		ArchitectureModelFactoryFacade.createAssemblyContext(client, baseversion);
		ArchitectureModelFactoryFacade.createAssemblyContext(server, baseversion);
		ArchitectureModelFactoryFacade.createAssemblyContext(database, baseversion);
		
		ArchitectureModelFactoryFacade.createAssemblyConnector(client, server, baseversion);
		ArchitectureModelFactoryFacade.createAssemblyConnector(server, database, baseversion);
		
		// component internal dependencies
		
		// pessimistic modelling
		
		ArchitectureModelFactoryFacade.setupComponentInternalDependenciesPessimistic(baseversion);
		
		
		return baseversion;
	}

	@After
	public void tearDown() {
		baseArchitectureVersion.delete();
		baseArchitectureVersion = null;
	}

	@Test
	public void testWholeAnalysis() {
		
		// I. Preparation
		
		// base model creation 
		
		// component internal dependencies
		
		// annotation model creation
				
		// II. CR1

		// model clone
		
		ArchitectureVersion subVersion = ArchitectureVersionPersistency.createArchitectureVersionClone(baseArchitectureVersion, TestPathProvider.getTestPath(TESTNAME), "SubVersionInsertDatabaseCache");
		
		// change request modelling
		
		// add cache interface and component with roles
		BasicComponent cacheComponent = ArchitectureModelFactoryFacade.createBasicComponent(subVersion, "DatabaseCache");
		OperationInterface dbInterface = (OperationInterface) ArchitectureModelLookup.lookUpInterfaceByName(subVersion, "DatabaseInterface");
		ArchitectureModelFactoryFacade.createProvidedRole(cacheComponent, dbInterface);
		ArchitectureModelFactoryFacade.createRequiredRole(cacheComponent, dbInterface);
		
		RepositoryComponent server = ArchitectureModelLookup.lookUpComponentByName(subVersion, "Server");
		RepositoryComponent database = ArchitectureModelLookup.lookUpComponentByName(subVersion, "Database");
		
		AssemblyContext databaseContext = ArchitectureModelLookup.lookUpAssemblyContextsForRepositoryComponent(subVersion, database).get(0);
		assertTrue("Database Context not found", databaseContext != null);
		
		AssemblyContext serverContext = ArchitectureModelLookup.lookUpAssemblyContextsForRepositoryComponent(subVersion, server).get(0);
		assertTrue("Server Context not found", serverContext != null);

		List<AssemblyConnector> assemblyConnectors = ArchitectureModelLookup.lookUpAssemblyConnectorsBetweenAssemblyContexts(databaseContext, serverContext);
		assertTrue("Assembly connector not found", assemblyConnectors.size()==1);
	
		// delete old assembly connector
		
		ArchitectureModelFactoryFacade.deleteAssemblyConnector(assemblyConnectors.get(0));
		assertTrue("Assembly connectors not deleted properly", ArchitectureModelLookup.lookUpAssemblyConnectorsBetweenAssemblyContexts(databaseContext, serverContext).size()==0);
		
		// create new assembly contexts and connectors
		
		ArchitectureModelFactoryFacade.createAssemblyContext(cacheComponent, subVersion);
		assertTrue("Assembly context for cache not properly created", ArchitectureModelLookup.lookUpAssemblyContextsForRepositoryComponent(subVersion, cacheComponent).size()==1);

		AssemblyContext cacheAssemblyContext = ArchitectureModelLookup.lookUpAssemblyContextsForRepositoryComponent(subVersion, cacheComponent).get(0);

		ArchitectureModelFactoryFacade.createAssemblyConnector(server, cacheComponent, subVersion);
		assertTrue("Assembly connector between server and cache not created properly", ArchitectureModelLookup.lookUpAssemblyConnectorsBetweenAssemblyContexts(cacheAssemblyContext, serverContext).size()==1);

		ArchitectureModelFactoryFacade.createAssemblyConnector(cacheComponent, database, subVersion);
		assertTrue("Assembly connectors not deleted properly", ArchitectureModelLookup.lookUpAssemblyConnectorsBetweenAssemblyContexts(databaseContext, cacheAssemblyContext).size()==1);
		
		
		// mark internal modification on database
		
		ArchitectureModelFactoryFacade.assignInternalModificationMarkToComponent(subVersion, database);

		List<ModifyComponent> internalModificationMarks = ArchitectureModelLookup.lookUpModificationMarksForComponent(subVersion, database);
		
		assertTrue("InternalModificationMark not found", internalModificationMarks.size()==1);
		
		// workplan derivation - plain

		List<Activity> baseActivityList = DifferenceCalculation.deriveWorkplan(baseArchitectureVersion, subVersion);
		
		assertTrue("No activities detected", !baseActivityList.isEmpty());

		System.out.println("Base Activity List:");
		printActivities(baseActivityList, "Top:");

//		assertTrue("Different number of base activities expected", baseActivityList.size()==2);

		// change propagation analysis
		
//		ChangePropagationAnalysis.calculateIntraComponentPropagation();
//		
//		ChangePropagationAnalysis.calculateInterComponentPropagation();
		
		
		
		// workplan derivation - enriched
		
		List<Activity> enrichedActivityList = EnrichedWorkplanDerivation.deriveEnrichedWorkplan(baseArchitectureVersion, subVersion, baseActivityList);
		
		assertTrue("No enriched activities detected", !enrichedActivityList.isEmpty());
		assertTrue("No difference to base activity list", enrichedActivityList.size()==baseActivityList.size());
		
		System.out.println("Enriched Activity List:");
		printActivities(baseActivityList, "Top:");
		
		try {
			ArchitectureVersionPersistency.save(TestPathProvider.getTestPath(TESTNAME), "subversion", subVersion);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		ArchitectureVersionPersistency.saveActivityList(TestPathProvider.getTestPath(TESTNAME), "SubVersionInsertDatabaseCache", baseActivityList);

		ArchitectureVersionPersistency.saveActivityListToExcelFile(TestPathProvider.getTestPath(TESTNAME), "workplan", baseActivityList);
		
		// annotation update
		
		
	}

	private void printActivities(List<Activity> activityList, String prefix) {
		if (prefix==null)
			prefix="";
		
		for (Activity activity : activityList) {
			System.out.println(prefix + " " + activity.getBasicActivity() + " " + activity.getElementType() + " " + activity.getElementName());
			if (!activity.getSubactivities().isEmpty()) {
				printActivities(activity.getSubactivities(), prefix + "=");
			}
			if (!activity.getFollowupActivities().isEmpty()) {
				printActivities(activity.getFollowupActivities(), prefix+"->");
			}
		}
	}
	

}
