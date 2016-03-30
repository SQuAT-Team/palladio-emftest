package edu.kit.ipd.sdq.kamp.core;

import de.uka.ipd.sdq.pcm.core.composition.AssemblyConnector;
import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.OperationProvidedRole;
import de.uka.ipd.sdq.pcm.repository.ProvidedRole;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.AcceptanceTestCase;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.AcceptanceTestCaseAggregation;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.BuildConfiguration;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.FieldofactivityannotationsFactory;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.IntegrationTestCase;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.IntegrationTestCaseAggregation;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.MetadataFile;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.MetadataFileAggregation;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.ReleaseConfiguration;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.RuntimeInstance;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.RuntimeInstanceAggregation;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.SourceFile;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.SourceFileAggregation;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.ThirdPartyComponentOrLibrary;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.UnitTestCase;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.UnitTestCaseAggregation;

public class ArchitectureAnnotationFactory {

	public static void createSourceFileAggregationAnnotation(ArchitectureVersion version, 
			RepositoryComponent component, int numberOfFiles, String technology) {
		
		SourceFileAggregation sourceFileAggregation = FieldofactivityannotationsFactory.eINSTANCE.createSourceFileAggregation();
		sourceFileAggregation.setNumberOfFiles(numberOfFiles);
		sourceFileAggregation.setComponent(component);
		
		// TODO: SETUP TECHNOLOGY
		sourceFileAggregation.setTechnology(technology);
		
		version.getFieldOfActivityRepository().getDevelopmentArtefactSpecification().getSourceFileAggregations().add(sourceFileAggregation);
	}

	public static void createSourceFileAnnotation(ArchitectureVersion version, 
			RepositoryComponent component, String nameOfFile, String technology) {
		
		SourceFile sourceFile = FieldofactivityannotationsFactory.eINSTANCE.createSourceFile();
		sourceFile.setFilename(nameOfFile);
		sourceFile.setComponent(component);
		
		// TODO: SETUP TECHNOLOGY
		sourceFile.setTechnology(technology);
		
		version.getFieldOfActivityRepository().getDevelopmentArtefactSpecification().getSourceFiles().add(sourceFile);
	}

	public static void createMetadataFileAnnotation(
			ArchitectureVersion version, RepositoryComponent component,
			String nameOfFile, String technology) {

		MetadataFile metadataFile = FieldofactivityannotationsFactory.eINSTANCE.createMetadataFile();
		metadataFile.setFilename(nameOfFile);
		metadataFile.setComponent(component);
		
		// TODO: SETUP TECHNOLOGY
		metadataFile.setTechnology(technology);
		
		version.getFieldOfActivityRepository().getDevelopmentArtefactSpecification().getMetadataFiles().add(metadataFile);
	}
	
	public static void createMetadataFileAggregationAnnotation(
			ArchitectureVersion version, RepositoryComponent component,
			int numberOfFiles, String technology) {

		MetadataFileAggregation metadataFile = FieldofactivityannotationsFactory.eINSTANCE.createMetadataFileAggregation();
		metadataFile.setNumberOfFiles(numberOfFiles);
		metadataFile.setComponent(component);
		
		// TODO: SETUP TECHNOLOGY
		metadataFile.setTechnology(technology);
		
		version.getFieldOfActivityRepository().getDevelopmentArtefactSpecification().getMetadataFileAggregations().add(metadataFile);
	}

	public static void createUnitTestAggregation(
			ArchitectureVersion version, ProvidedRole providedRole,
			int numberOfTests, String nameOfTestSuite) {

		UnitTestCaseAggregation unitTestCaseAggregation = FieldofactivityannotationsFactory.eINSTANCE.createUnitTestCaseAggregation();
		unitTestCaseAggregation.setNumberOfTestcases(numberOfTests);
		unitTestCaseAggregation.setNameOfTestSuite(nameOfTestSuite);
		unitTestCaseAggregation.setProvidedrole((OperationProvidedRole) providedRole);
		version.getFieldOfActivityRepository().getTestSpecification().getUnitTestCaseAggregations().add(unitTestCaseAggregation);
		
	}

	public static void createIntegrationTestAggregation(
			ArchitectureVersion version, AssemblyConnector connector, int numberOfTestCases, String nameOfTestSuite) {
		IntegrationTestCaseAggregation testcaseaggregation = FieldofactivityannotationsFactory.eINSTANCE.createIntegrationTestCaseAggregation();
		testcaseaggregation.setNumberOfTestcases(numberOfTestCases);
		testcaseaggregation.setNameOfTestSuite(nameOfTestSuite);
		testcaseaggregation.setAssemblyConnector(connector);
		version.getFieldOfActivityRepository().getTestSpecification().getIntegrationTestCaseAggregations().add(testcaseaggregation);
	}

	public static void createAcceptanceTestAggregation(
			ArchitectureVersion version, ProvidedRole providedRole, int numberOfTests, String nameOfTestSuite) {
		AcceptanceTestCaseAggregation testcaseaggregation = FieldofactivityannotationsFactory.eINSTANCE.createAcceptanceTestCaseAggregation();
		testcaseaggregation.setNumberOfTestcases(numberOfTests);
		testcaseaggregation.setNameOfTestSuite(nameOfTestSuite);
		testcaseaggregation.setProvidedrole((OperationProvidedRole) providedRole);
		version.getFieldOfActivityRepository().getTestSpecification().getAcceptanceTestCaseAggregations().add(testcaseaggregation);
	}

	public static void createBuildConfiguration(
			ArchitectureVersion version,
			RepositoryComponent[] repositoryComponents, String pathname, String technology) {
		BuildConfiguration buildConfiguration = FieldofactivityannotationsFactory.eINSTANCE.createBuildConfiguration();
		for (RepositoryComponent component : repositoryComponents) {
			buildConfiguration.getComponent().add(component);
		}
		buildConfiguration.setFilename(pathname);
		buildConfiguration.setTechnology(technology);
		version.getFieldOfActivityRepository().getBuildSpecification().getBuildConfigurations().add(buildConfiguration);
	}

	public static void createReleaseConfiguration(
			ArchitectureVersion version,
			RepositoryComponent[] repositoryComponents, String pathname,
			String description) {

		ReleaseConfiguration releaseConfiguration = FieldofactivityannotationsFactory.eINSTANCE.createReleaseConfiguration();
		for (RepositoryComponent component : repositoryComponents) {
			releaseConfiguration.getComponents().add(component);
		}
		releaseConfiguration.setPathname(pathname);
		releaseConfiguration.setDescription(description);
		version.getFieldOfActivityRepository().getReleaseSpecification().getReleaseConfigurations().add(releaseConfiguration);
	}

	public static void createRuntimeInstanceAggregation(
			ArchitectureVersion version, RepositoryComponent[] repositoryComponents,
			int numberOfInstances, String description) {
		RuntimeInstanceAggregation runtimeInstanceAggregation = FieldofactivityannotationsFactory.eINSTANCE.createRuntimeInstanceAggregation();
		runtimeInstanceAggregation.setNumberOfInstances(numberOfInstances);
		for (RepositoryComponent component : repositoryComponents) {
			runtimeInstanceAggregation.getComponents().add(component);
		}
		runtimeInstanceAggregation.setDescription(description);
		version.getFieldOfActivityRepository().getDeploymentSpecification().getRuntimeInstanceAggregations().add(runtimeInstanceAggregation);
	}

	public static void createThirdPartyOrLibraryMarker(
			ArchitectureVersion version, RepositoryComponent component,
			String nameOfFile, String technology) {
		
		ThirdPartyComponentOrLibrary tpcol = FieldofactivityannotationsFactory.eINSTANCE.createThirdPartyComponentOrLibrary();
		tpcol.setComponent(component);
		tpcol.setFilename(nameOfFile);
		tpcol.setTechnology(technology);
		version.getFieldOfActivityRepository().getBuildSpecification().getThirdPartyComponentOrLibraries().add(tpcol);
	}

	public static void createUnitTestCase(
			ArchitectureVersion version,
			OperationProvidedRole providedRole, String nameOfTest) {
		UnitTestCase testCase = FieldofactivityannotationsFactory.eINSTANCE.createUnitTestCase();
		testCase.setNameOfTest(nameOfTest);
		testCase.setProvidedrole(providedRole);
		version.getFieldOfActivityRepository().getTestSpecification().getUnitTestCases().add(testCase);
	}
	
	public static void createAcceptanceTestCase(
			ArchitectureVersion version,
			OperationProvidedRole providedRole, String nameOfTest) {
		AcceptanceTestCase testCase = FieldofactivityannotationsFactory.eINSTANCE.createAcceptanceTestCase();
		testCase.setNameOfTest(nameOfTest);
		testCase.setProvidedrole(providedRole);
		version.getFieldOfActivityRepository().getTestSpecification().getAcceptanceTestCases().add(testCase);
	}

	public static void createIntegrationTestCase(
			ArchitectureVersion version,
			AssemblyConnector connector, String nameOfTest) {
		IntegrationTestCase testCase = FieldofactivityannotationsFactory.eINSTANCE.createIntegrationTestCase();
		testCase.setNameOfTest(nameOfTest);
		testCase.setAssemblyConnector(connector);
		version.getFieldOfActivityRepository().getTestSpecification().getIntegrationTestCases().add(testCase);
	}

	public static void createRuntimeInstance(
			ArchitectureVersion version,
			RepositoryComponent[] repositoryComponents, String name,
			String description) {
		RuntimeInstance instance = FieldofactivityannotationsFactory.eINSTANCE.createRuntimeInstance();
		instance.setName(name);
		instance.setDescription(description);
		for (RepositoryComponent component : repositoryComponents) {
			instance.getComponents().add(component);
		}
		version.getFieldOfActivityRepository().getDeploymentSpecification().getRuntimeInstances().add(instance);
	}

	
}
