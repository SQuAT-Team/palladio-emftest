package edu.kit.ipd.sdq.kamp.core;

import java.util.ArrayList;
import java.util.List;

import de.uka.ipd.sdq.pcm.core.composition.AssemblyConnector;
import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.ProvidedRole;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.AcceptanceTestCase;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.AcceptanceTestCaseAggregation;
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
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.ThirdPartyComponentOrLibrary;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.UnitTestCase;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.UnitTestCaseAggregation;

public class ArchitectureAnnotationLookup {

	public static List<SourceFile> lookUpSourceFilesForComponent(ArchitectureVersion version, 
			RepositoryComponent component) {
	
		List<SourceFile> sourceFiles = new ArrayList<SourceFile>();
		
		for (SourceFile sourceFile : version.getFieldOfActivityRepository().getDevelopmentArtefactSpecification().getSourceFiles()) {
			if (sourceFile.getComponent()==component) {
				sourceFiles.add(sourceFile);
			}
		}
		
		return sourceFiles;
	}

	public static SourceFileAggregation lookUpSourceFileAggregationForComponent(ArchitectureVersion version, 
			RepositoryComponent component) {
	
		for (SourceFileAggregation sourceFileAggregation : version.getFieldOfActivityRepository().getDevelopmentArtefactSpecification().getSourceFileAggregations()) {
			if (sourceFileAggregation.getComponent()==component) {
				return sourceFileAggregation;
			}
		}
		
		return null;
	}
	
	public static List<MetadataFile> lookUpMetadataFilesForComponent(ArchitectureVersion version, 
			RepositoryComponent component) {
	
		List<MetadataFile> metadataFiles = new ArrayList<MetadataFile>();
		
		for (MetadataFile metadataFile : version.getFieldOfActivityRepository().getDevelopmentArtefactSpecification().getMetadataFiles()) {
			if (metadataFile.getComponent()==component) {
				metadataFiles.add(metadataFile);
			}
		}
		
		return metadataFiles;
	}

	public static MetadataFileAggregation lookUpMetadataFileAggregationForComponent(ArchitectureVersion version, 
			RepositoryComponent component) {
	
		for (MetadataFileAggregation metadataFileAggregation : version.getFieldOfActivityRepository().getDevelopmentArtefactSpecification().getMetadataFileAggregations()) {
			if (metadataFileAggregation.getComponent()==component) {
				return metadataFileAggregation;
			}
		}
		
		return null;
	}

	public static UnitTestCaseAggregation lookUpUnitTestAggregationForProvidedRole(
			ArchitectureVersion version,
			ProvidedRole providedRole) {

		for (UnitTestCaseAggregation testAggregation : version.getFieldOfActivityRepository().getTestSpecification().getUnitTestCaseAggregations()) {
			if (testAggregation.getProvidedrole()==providedRole) {
				return testAggregation;
			}
		}
		
		return null;
	}

	public static List<UnitTestCase> lookUpUnitTestCasesForProvidedRole(
			ArchitectureVersion version,
			ProvidedRole providedRole) {

		List<UnitTestCase> testCases = new ArrayList<UnitTestCase>();
		
		for (UnitTestCase testcase : version.getFieldOfActivityRepository().getTestSpecification().getUnitTestCases()) {
			if (testcase.getProvidedrole()==providedRole) {
				testCases.add(testcase);
			}
		}
		
		return testCases;
	}

	public static IntegrationTestCaseAggregation lookUpIntegrationTestAggregationForAssemblyConnector(
			ArchitectureVersion version,
			AssemblyConnector assemblyConnector) {

		for (IntegrationTestCaseAggregation testAggregation : version.getFieldOfActivityRepository().getTestSpecification().getIntegrationTestCaseAggregations()) {
			if (testAggregation.getAssemblyConnector()==assemblyConnector) {
				return testAggregation;
			}
		}
		
		return null;
	}

	public static List<IntegrationTestCase> lookUpIntegrationTestCasesForAssemblyConnector(
			ArchitectureVersion version,
			AssemblyConnector assemblyConnector) {

		List<IntegrationTestCase> testCases = new ArrayList<IntegrationTestCase>();
		
		for (IntegrationTestCase testcase : version.getFieldOfActivityRepository().getTestSpecification().getIntegrationTestCases()) {
			if (testcase.getAssemblyConnector()==assemblyConnector) {
				testCases.add(testcase);
			}
		}
		
		return testCases;
	}

	public static AcceptanceTestCaseAggregation lookUpAcceptanceTestAggregationForProvidedRole(
			ArchitectureVersion version,
			ProvidedRole providedRole) {

		for (AcceptanceTestCaseAggregation testAggregation : version.getFieldOfActivityRepository().getTestSpecification().getAcceptanceTestCaseAggregations()) {
			if (testAggregation.getProvidedrole()==providedRole) {
				return testAggregation;
			}
		}
		
		return null;
	}

	public static List<AcceptanceTestCase> lookUpAcceptanceTestCasesForProvidedRole(
			ArchitectureVersion version,
			ProvidedRole providedRole) {

		List<AcceptanceTestCase> testCases = new ArrayList<AcceptanceTestCase>();
		
		for (AcceptanceTestCase testcase : version.getFieldOfActivityRepository().getTestSpecification().getAcceptanceTestCases()) {
			if (testcase.getProvidedrole()==providedRole) {
				testCases.add(testcase);
			}
		}
		return testCases;
	}

	public static ThirdPartyComponentOrLibrary lookUpThirdPartyOrLibraryAnnotationForComponent(
			ArchitectureVersion version,
			RepositoryComponent component) {
		
		for (ThirdPartyComponentOrLibrary annotation : version.getFieldOfActivityRepository().getBuildSpecification().getThirdPartyComponentOrLibraries()) {
			if (annotation.getComponent()==component) {
				return annotation;
			}
		}
		
		return null;
	}

	public static BuildConfiguration lookUpBuildConfigurationForComponent(
			ArchitectureVersion version, RepositoryComponent component) {

		for (BuildConfiguration annotation : version.getFieldOfActivityRepository().getBuildSpecification().getBuildConfigurations()) {
			if (annotation.getComponent().contains(component)) {
				return annotation;
			}
		}
		
		return null;
	}

	public static ReleaseConfiguration lookUpReleaseConfigurationForComponent(
			ArchitectureVersion version, RepositoryComponent component) {
		for (ReleaseConfiguration annotation : version.getFieldOfActivityRepository().getReleaseSpecification().getReleaseConfigurations()) {
			if (annotation.getComponents().contains(component)) {
				return annotation;
			}
		}
		
		return null;
	}

	public static RuntimeInstanceAggregation lookUpRuntimeInstanceAggregation(
			ArchitectureVersion version, RepositoryComponent component) {
		for (RuntimeInstanceAggregation annotation : version.getFieldOfActivityRepository().getDeploymentSpecification().getRuntimeInstanceAggregations()) {
			if (annotation.getComponents().contains(component)) {
				return annotation;
			}
		}
		return null;
	}

	public static List<RuntimeInstance> lookUpRuntimeInstances(
			ArchitectureVersion version, RepositoryComponent component) {
		
		List<RuntimeInstance> instances = new ArrayList<RuntimeInstance>();
		
		for (RuntimeInstance instance : version.getFieldOfActivityRepository().getDeploymentSpecification().getRuntimeInstances()) {
			if (instance.getComponents().contains(component)) {
				instances.add(instance);
			}
		}
		
		return instances;
	}

}
