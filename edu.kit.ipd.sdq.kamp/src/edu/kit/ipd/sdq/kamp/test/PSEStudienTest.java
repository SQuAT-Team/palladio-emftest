package edu.kit.ipd.sdq.kamp.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uka.ipd.sdq.pcm.repository.DataType;
import de.uka.ipd.sdq.pcm.repository.ProvidedRole;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import edu.kit.ipd.sdq.kamp.core.Activity;
import edu.kit.ipd.sdq.kamp.core.ArchitectureAnnotationFactory;
import edu.kit.ipd.sdq.kamp.core.ArchitectureModelFactoryFacade;
import edu.kit.ipd.sdq.kamp.core.ArchitectureModelLookup;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersion;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersionPersistency;
import edu.kit.ipd.sdq.kamp.core.ChangePropagationAnalysis;
import edu.kit.ipd.sdq.kamp.core.derivation.DifferenceCalculation;
import edu.kit.ipd.sdq.kamp.core.derivation.EnrichedWorkplanDerivation;

public class PSEStudienTest {

	private static String FOLDERNAME = "kamptestsPSEStudienTest";
	private static String FILENAME = "pse";

	private static ArchitectureVersion baseversion;
	
	@Before
	public void setUp() throws Exception {
		baseversion = ArchitectureVersionPersistency.load(FOLDERNAME, FILENAME, "basismodell");
		
	}

	public void testBaseModel() throws IOException {

		RepositoryComponent componentUserServiceApache = ArchitectureModelLookup.lookUpComponentByName(this.baseversion, "UserServiceApacheProxy");

		assertTrue("componentUserServiceApache not found",componentUserServiceApache != null);
		
		RepositoryComponent componentUserServiceTomcat = ArchitectureModelLookup.lookUpComponentByName(this.baseversion, "UserServiceTomcat");

		assertTrue("componentUserServiceTomcat not found",componentUserServiceTomcat != null);

		RepositoryComponent componentReporting = ArchitectureModelLookup.lookUpComponentByName(this.baseversion, "Reporting");

		assertTrue("Reporting component not found",componentReporting != null);

		RepositoryComponent componentAuthentication = ArchitectureModelLookup.lookUpComponentByName(this.baseversion, "Authentication");

		assertTrue("Authentication component not found",componentAuthentication != null);

		RepositoryComponent componentUserManagement = ArchitectureModelLookup.lookUpComponentByName(this.baseversion, "UserManagement");

		assertTrue("componentUserManagement not found",componentUserManagement != null);

		RepositoryComponent componentUserDAO = ArchitectureModelLookup.lookUpComponentByName(this.baseversion, "UserDAO");

		assertTrue("componentUserDAO not found",componentUserDAO != null);
		
		RepositoryComponent componentHibernate = ArchitectureModelLookup.lookUpComponentByName(this.baseversion, "DatabaseAccess/Hibernate");

		assertTrue("componentHibernate not found",componentHibernate != null);

		RepositoryComponent componentUserDatabase = ArchitectureModelLookup.lookUpComponentByName(this.baseversion, "UserDatabase");

		assertTrue("componentUserDatabase not found",componentUserDatabase != null);

		// only needs to be available in sub-version (not basemodell)
		
		//assertTrue("FieldOfActivityAnnotation Repository not found", baseversion.getFieldOfActivityRepository() != null);
		
		// initialize annotation repository
		
		baseversion.setFieldOfActivityRepository(ArchitectureModelFactoryFacade.createFieldOfActivityAnnotationsRepository());
		
		// setup field of activity annotations

		// source files
		ArchitectureAnnotationFactory.createSourceFileAnnotation(baseversion, componentUserServiceTomcat, "UserRest.java", "Java");
		ArchitectureAnnotationFactory.createSourceFileAnnotation(baseversion, componentReporting, "Reporting.java", "Java");
		ArchitectureAnnotationFactory.createSourceFileAnnotation(baseversion, componentUserManagement, "UserService.java", "Java");
		ArchitectureAnnotationFactory.createSourceFileAnnotation(baseversion, componentUserDAO, "UserDAO.java", "Java");
		ArchitectureAnnotationFactory.createSourceFileAnnotation(baseversion, componentUserDAO, "HibernateUtil.java", "Java");
		// metadata files
		ArchitectureAnnotationFactory.createMetadataFileAnnotation(baseversion, componentHibernate, "hibernate.cfg.xml", "Hibernate-Konfigurationsdatei");
		ArchitectureAnnotationFactory.createMetadataFileAnnotation(baseversion, componentHibernate, "User.hbm.xml", "Hibernate-Mapping-Datei");
		ArchitectureAnnotationFactory.createMetadataFileAnnotation(baseversion, componentUserDatabase, "Userdatabase.sql", "Datenbank-Schema");

		RepositoryComponent[] allComponentsOfSystem = new RepositoryComponent[] {
				componentUserServiceApache, componentUserServiceTomcat, componentUserManagement, componentReporting, 
				componentAuthentication, componentUserDAO, componentHibernate, componentUserDatabase};

		// build config
		ArchitectureAnnotationFactory.createBuildConfiguration(baseversion, allComponentsOfSystem, 
				"Eclipse-Projekt-Einstellungen", "Eclipse-Projekt-Einstellungen");

		// test cases
		ArchitectureAnnotationFactory.createUnitTestAggregation(baseversion, componentUserServiceTomcat.getProvidedRoles_InterfaceProvidingEntity().get(0), 
				7, "UserServiceTomcatTest.java");
		ArchitectureAnnotationFactory.createUnitTestAggregation(baseversion, componentUserManagement.getProvidedRoles_InterfaceProvidingEntity().get(0), 
				5, "UserManagementTest.java");
		ArchitectureAnnotationFactory.createUnitTestAggregation(baseversion, componentAuthentication.getProvidedRoles_InterfaceProvidingEntity().get(0), 
				1, "AuthenticationTest.java");
		
		// library / third party components
		ArchitectureAnnotationFactory.createThirdPartyOrLibraryMarker(baseversion, componentUserServiceApache, "Apachy-Server", "Apachy-Server");
		ArchitectureAnnotationFactory.createThirdPartyOrLibraryMarker(baseversion, componentHibernate, "Hibernate.jar", "OR-Mapper");
		ArchitectureAnnotationFactory.createThirdPartyOrLibraryMarker(baseversion, componentUserDatabase, "MySQL-Database", "MySQL-Database");
		ArchitectureAnnotationFactory.createThirdPartyOrLibraryMarker(baseversion, componentAuthentication, "External Authentication Service", "External Authentication Service");
		
		RepositoryComponent[] componentsToBeDeployedWithinSystemContext = new RepositoryComponent[] {
				componentUserServiceApache, componentUserServiceTomcat, componentUserManagement, componentReporting, 
		        componentUserDAO, componentHibernate, componentUserDatabase};

		// release
		ArchitectureAnnotationFactory.createReleaseConfiguration(baseversion, componentsToBeDeployedWithinSystemContext, "fileserver/usermanagement", "");
		
		// deployment
		ArchitectureAnnotationFactory.createRuntimeInstanceAggregation(baseversion, componentsToBeDeployedWithinSystemContext, 1, "ATIS-Server");
		
		ArchitectureVersionPersistency.saveFieldOfActivityRepository(FOLDERNAME, FILENAME, baseversion);
		
		assertTrue("Internal Dependency Repository not found", baseversion.getComponentInternalDependencyRepository() != null);

		baseversion.setComponentInternalDependencyRepository(ArchitectureModelFactoryFacade.createComponentInternalDependencyRepository());
		ArchitectureModelFactoryFacade.setupComponentInternalDependenciesPessimistic(baseversion);
		ArchitectureVersionPersistency.saveComponentInternalDependencyModel(FOLDERNAME, FILENAME, baseversion);
		
		// setup component internal dependencies
		
		// TODO: check presence of annotations by lookup!
	}

	@Test
	public void testScenarioModificationOfDatabaseProvidedRole() throws IOException {
		
		testBaseModel();
		
		ArchitectureVersion subVersion = ArchitectureVersionPersistency.load(FOLDERNAME+"/subversion1", FILENAME, "SubversionScenarioMarkUserDatatype");

		// init modificationmark repository
		subVersion.setInternalModificationMarkRepository(ArchitectureModelFactoryFacade.createModificationMarkRepository());
		
		RepositoryComponent componentUserDatabase = ArchitectureModelLookup.lookUpComponentByName(subVersion, "UserDatabase");

		assertTrue("componentUserDatabase not found", componentUserDatabase != null);
		
		assertTrue("Number of provided roles of componentUserDatabase not equals 1", componentUserDatabase.getProvidedRoles_InterfaceProvidingEntity().size()==1);
		
		ProvidedRole providedRoleOfUserDatabase = componentUserDatabase.getProvidedRoles_InterfaceProvidingEntity().get(0);

		DataType userDataType = ArchitectureModelLookup.lookUpDatatypeByName(subVersion, "User");

		assertTrue("userDataType not found", userDataType != null);
		
		// CHANGE REQUEST MODELLING
		
		ArchitectureModelFactoryFacade.assignInternalModificationMarkToDataType(subVersion, userDataType);
		
		ChangePropagationAnalysis changePropagationAnalysis = new ChangePropagationAnalysis();
		changePropagationAnalysis.runChangePropagationAnalysis(subVersion);

		ArchitectureVersionPersistency.saveModificationMarkFile(FOLDERNAME+"/subversion1", FILENAME, subVersion);
		
		List<Activity> baseActivityListAfterChangePropagationAnalysis = DifferenceCalculation.deriveWorkplan(baseversion, subVersion);
		
		assertTrue("No activities detected", !baseActivityListAfterChangePropagationAnalysis.isEmpty());

		ArchitectureVersionPersistency.saveActivityListToExcelFile(FOLDERNAME+"/subversion1", "workplan_archbased_afterpropagationanalysis", baseActivityListAfterChangePropagationAnalysis);

		List<Activity> enrichedActivityList = EnrichedWorkplanDerivation.deriveEnrichedWorkplan(baseversion, subVersion, baseActivityListAfterChangePropagationAnalysis);
		
		assertTrue("No enriched activities detected", !enrichedActivityList.isEmpty());
		assertTrue("No difference to base activity list", enrichedActivityList.size()==baseActivityListAfterChangePropagationAnalysis.size());
		
		ArchitectureVersionPersistency.saveActivityListToExcelFile(FOLDERNAME+"/subversion1", "workplan_enriched", enrichedActivityList);
	}

	
	@After
	public void tearDown() {
	}

}
