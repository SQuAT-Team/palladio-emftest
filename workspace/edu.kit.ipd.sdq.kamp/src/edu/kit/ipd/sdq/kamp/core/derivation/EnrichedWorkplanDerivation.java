package edu.kit.ipd.sdq.kamp.core.derivation;

import java.util.ArrayList;
import java.util.List;

import de.uka.ipd.sdq.pcm.repository.ProvidedRole;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import de.uka.ipd.sdq.pcm.repository.RequiredRole;

import edu.kit.ipd.sdq.kamp.core.Activity;
import edu.kit.ipd.sdq.kamp.core.ActivityElementType;
import edu.kit.ipd.sdq.kamp.core.ActivityType;
import edu.kit.ipd.sdq.kamp.core.ArchitectureAnnotationLookup;
import edu.kit.ipd.sdq.kamp.core.ArchitectureModelLookup;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersion;
import edu.kit.ipd.sdq.kamp.core.BasicActivity;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.BuildConfiguration;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.MetadataFile;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.MetadataFileAggregation;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.SourceFile;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.SourceFileAggregation;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.UnitTestCase;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.UnitTestCaseAggregation;

public class EnrichedWorkplanDerivation {
	public static List<Activity> deriveEnrichedWorkplan(
			ArchitectureVersion baseArchitectureVersion,
			ArchitectureVersion subVersion, List<Activity> baseActivityList) {

		// derive coding activities
		deriveCodingActivities(baseArchitectureVersion, subVersion, baseActivityList);

		// derive metadata activities
		deriveMetadataActivities(baseArchitectureVersion, subVersion, baseActivityList);

		// derive build configuration activities
		deriveBuildConfigurationActivities(baseArchitectureVersion, subVersion, baseActivityList);

		// derive build execution activities
		deriveBuildExecutionActivities(subVersion, baseActivityList);
		
		// component interface tests
		// derive test development activities 
		deriveTestDevelopmentActivities(baseArchitectureVersion, subVersion, baseActivityList);

		// derive test execution activities
		deriveTestExecutionActivities(subVersion, baseActivityList);

		// integration tests
		// acceptance tests
		
		// derive release configuration activities 
//		deriveReleaseConfigurationActivities(calculateFlattenendActivityList(baseActivityList));		

		// derive release configuration activities 
		deriveReleaseExecutionActivities(subVersion, baseActivityList);		
//
//		// derive deployment configuration activities
//		deriveDeploymentConfigurationActivities(calculateFlattenendActivityList(baseActivityList));		
//
		// derive deployment execution activities
		deriveDeploymentExecutionActivities(subVersion, baseActivityList);		

		return baseActivityList;
	}

	public static List<Activity> calculateFlattenendActivityList(List<Activity> activityList) {
		List<Activity> flatActivityList = new ArrayList<Activity>();
		
		for (Activity activity : activityList) {
			flatActivityList.add(activity);
			if (!activity.getSubactivities().isEmpty()) {
				flatActivityList.addAll(calculateFlattenendActivityList(activity.getSubactivities()));
			}
			if (!activity.getFollowupActivities().isEmpty()) {
				flatActivityList.addAll(calculateFlattenendActivityList(activity.getFollowupActivities()));
			}
		}
		
		return flatActivityList;
	}

	private static void deriveCodingActivities(ArchitectureVersion baseVersion, ArchitectureVersion targetVersion, List<Activity> baseActivityList) {
		for (Activity activity : baseActivityList) {
			if (activityElementIsComponent(activity) && activity.getBasicActivity()==BasicActivity.ADD) {
				int numberOfSourceFiles = determineNumberOfSourceFiles(targetVersion, activity);
				
				if (numberOfSourceFiles > 0) {
					Activity newActivity = new Activity(ActivityType.IMPLEMENTATION_SOURCECODE, ActivityElementType.SOURCECODEFILES, numberOfSourceFiles+" Quelltextdatei(en)", BasicActivity.ADD, 
							"Quelltextbearbeitung: Erstelle Quelltextdateien für Komponente "+activity.getElementName()+".", "Komponente "+activity.getElementName());
					activity.addFollowupactivity(newActivity);
				}
			} else if (activityElementIsComponent(activity) && activity.getBasicActivity()==BasicActivity.REMOVE) {
				int numberOfSourceFiles = determineNumberOfSourceFiles(baseVersion, activity);

				if (numberOfSourceFiles > 0) {
					Activity newActivity = new Activity(ActivityType.IMPLEMENTATION_SOURCECODE, ActivityElementType.SOURCECODEFILES, numberOfSourceFiles+" Quelltextdatei(en)", BasicActivity.REMOVE, 
							"Quelltextbearbeitung: Entferne Quelltextdateien der Komponente "+activity.getElementName()+".","Komponente "+activity.getElementName());
					activity.addFollowupactivity(newActivity);
				}
			} else if (activityElementIsComponent(activity) && activity.getBasicActivity()==BasicActivity.MODIFY) {
				int numberOfSourceFiles = determineNumberOfSourceFiles(targetVersion, activity);
				if (numberOfSourceFiles > 0) {
					Activity newActivity = new Activity(ActivityType.IMPLEMENTATION_SOURCECODE, ActivityElementType.SOURCECODEFILES, numberOfSourceFiles+" Quelltextdatei(en)", BasicActivity.MODIFY, 
							"Quelltextbearbeitung: Modifiziere Quelltextdateien der Komponente "+activity.getElementName()+".", "Komponente "+activity.getElementName());
					activity.addFollowupactivity(newActivity);
				}
			} 
			deriveCodingActivities(baseVersion, targetVersion, activity.getSubactivities());
		}
	}

	private static boolean activityElementIsComponent(Activity activity) {
		return activity.getElementType() == ActivityElementType.BASICCOMPONENT || activity.getElementType() == ActivityElementType.COMPOSITECOMPONENT;
	}

	private static int determineNumberOfSourceFiles(
			ArchitectureVersion version, Activity activity) {
		int numberOfSourceFiles = 0;
		if (activity.getElement() instanceof RepositoryComponent) {
			RepositoryComponent component = (RepositoryComponent)activity.getElement();
			List<SourceFile> sourceFiles = ArchitectureAnnotationLookup.lookUpSourceFilesForComponent(version, component);
			numberOfSourceFiles += sourceFiles.size();
			SourceFileAggregation sourceFileAggregation = ArchitectureAnnotationLookup.lookUpSourceFileAggregationForComponent(version, component);
			if (sourceFileAggregation!=null)
				numberOfSourceFiles += sourceFileAggregation.getNumberOfFiles(); 
		}
		return numberOfSourceFiles;
	}

	private static void deriveMetadataActivities(ArchitectureVersion baseVersion, ArchitectureVersion targetVersion, List<Activity> baseActivityList) {
		for (Activity activity : baseActivityList) {
			if (activityElementIsComponent(activity) && activity.getBasicActivity()==BasicActivity.ADD) {
				int numberOfMetadataFiles = determineNumberOfMetadataFiles(targetVersion, activity);
				if (numberOfMetadataFiles > 0) {
					Activity newActivity = new Activity(ActivityType.IMPLEMENTATION_METADATA, ActivityElementType.METADATAFILES, numberOfMetadataFiles+" Metadatendatei(en)", BasicActivity.ADD, 
							"Metadatenbearbeitung: Erstelle Metadatendateien ("+numberOfMetadataFiles+" Dateien) für Komponente "+activity.getElementName()+".", "Komponente "+activity.getElementName());
					activity.addFollowupactivity(newActivity);
				}
			} else if (activityElementIsComponent(activity) && activity.getBasicActivity()==BasicActivity.REMOVE) {
				int numberOfMetadataFiles = determineNumberOfMetadataFiles(baseVersion, activity);
				if (numberOfMetadataFiles > 0) {
					Activity newActivity = new Activity(ActivityType.IMPLEMENTATION_METADATA, ActivityElementType.METADATAFILES, numberOfMetadataFiles+" Metadatendatei(en)", BasicActivity.REMOVE, 
							"Metadatenbearbeitung: Entferne Metadatendateien ("+numberOfMetadataFiles+" Dateien) der Komponente "+activity.getElementName()+".", "Komponente "+activity.getElementName());
					activity.addFollowupactivity(newActivity);
				}
			} else if (activityElementIsComponent(activity) && activity.getBasicActivity()==BasicActivity.MODIFY) {
				int numberOfMetadataFiles = determineNumberOfMetadataFiles(targetVersion, activity);
				if (numberOfMetadataFiles > 0) {
					Activity newActivity = new Activity(ActivityType.IMPLEMENTATION_METADATA, ActivityElementType.METADATAFILES, numberOfMetadataFiles+" Metadatendatei(en)", BasicActivity.MODIFY, 
							"Metadatenbearbeitung: Modifiziere Metadatendateien ("+numberOfMetadataFiles+" Dateien) der Komponente "+activity.getElementName()+".", "Komponente "+activity.getElementName());
					activity.addFollowupactivity(newActivity);
				}
			} 
			deriveMetadataActivities(baseVersion, targetVersion, activity.getSubactivities());
		}
	}

	private static int determineNumberOfMetadataFiles(
			ArchitectureVersion version, Activity activity) {
		int numberOfMetadataFiles = 0;
		if (activity.getElement() instanceof RepositoryComponent) {
			RepositoryComponent component = (RepositoryComponent)activity.getElement();
			List<MetadataFile> metadataFiles = ArchitectureAnnotationLookup.lookUpMetadataFilesForComponent(version, component);
			numberOfMetadataFiles += metadataFiles.size();
			MetadataFileAggregation metadataFileAggregation = ArchitectureAnnotationLookup.lookUpMetadataFileAggregationForComponent(version, component);
			if (metadataFileAggregation!=null)
				numberOfMetadataFiles += metadataFileAggregation.getNumberOfFiles(); 
		}
		return numberOfMetadataFiles;
	}
	
	private static void deriveBuildConfigurationActivities(
			ArchitectureVersion base, ArchitectureVersion target, List<Activity> activityList) {
		// TODO: update build annotations
		for (Activity activity : activityList) {
			if (activityElementIsComponent(activity) && activity.getBasicActivity()==BasicActivity.ADD) {
				if (componentIsNotThirdPartyOrLibrary(target, (RepositoryComponent)activity.getElement())) {
					BuildConfiguration buildConfiguration = ArchitectureAnnotationLookup.lookUpBuildConfigurationForComponent(target, (RepositoryComponent)activity.getElement());
					Activity newActivity = new Activity(ActivityType.BUILDCONFIGURATION, ActivityElementType.BUILDCONFIGURATION, 
							(buildConfiguration != null) ? buildConfiguration.getFilename() : "", BasicActivity.MODIFY, 
							"Baukonfiguration: Komponente "+activity.getElementName()+" in Baukonfiguration registrieren.", "Komponente "+activity.getElementName());
					activity.addFollowupactivity(newActivity);
				}
			} else if (activityElementIsComponent(activity) && activity.getBasicActivity()==BasicActivity.REMOVE) {
				if (componentIsNotThirdPartyOrLibrary(base, (RepositoryComponent)activity.getElement())) {
					BuildConfiguration buildConfiguration = ArchitectureAnnotationLookup.lookUpBuildConfigurationForComponent(base, (RepositoryComponent)activity.getElement());
					Activity newActivity = new Activity(ActivityType.BUILDCONFIGURATION, ActivityElementType.BUILDCONFIGURATION, 
							(buildConfiguration != null) ? buildConfiguration.getFilename() : "", BasicActivity.MODIFY, 
							"Baukonfiguration: Komponente "+activity.getElementName()+" aus Baukonfiguration entfernen.", "Komponente "+activity.getElementName());
					activity.addFollowupactivity(newActivity);
				}
			} else if (activity.getElementType() == ActivityElementType.REQUIREDROLE && activity.getBasicActivity()==BasicActivity.ADD) {
				RepositoryComponent component = null;
				component = getComponentOfRequiredRole(activity);
				if (component != null && componentIsNotThirdPartyOrLibrary(target, component)) {
					BuildConfiguration buildConfiguration = ArchitectureAnnotationLookup.lookUpBuildConfigurationForComponent(target, component);
					Activity newActivity = new Activity(ActivityType.BUILDCONFIGURATION, ActivityElementType.BUILDCONFIGURATION, 
							(buildConfiguration != null) ? buildConfiguration.getFilename() : "", BasicActivity.MODIFY, 
							"Baukonfiguration: Hinzugekommene Abhängigkeit für Schnittstellennachfrage "+activity.getElementName()+" in Baukonfiguration ergänzen.", 
							"Schnittstellennachfrage "+activity.getElementName());
					activity.addFollowupactivity(newActivity);
					
				}
			} else if (activity.getElementType() == ActivityElementType.REQUIREDROLE && activity.getBasicActivity()==BasicActivity.REMOVE) {
				RepositoryComponent component = null;
				component = getComponentOfRequiredRole(activity);
				if (component != null && componentIsNotThirdPartyOrLibrary(target, component)) {
					BuildConfiguration buildConfiguration = ArchitectureAnnotationLookup.lookUpBuildConfigurationForComponent(target, component);
					Activity newActivity = new Activity(ActivityType.BUILDCONFIGURATION, ActivityElementType.BUILDCONFIGURATION, 
							(buildConfiguration != null) ? buildConfiguration.getFilename() : "", BasicActivity.MODIFY, 
							"Baukonfiguration: Weggefallene Abhängigkeit für Schnittstellennachfrage "+activity.getElementName()+" in Baukonfiguration entfernen.", 
							"Schnittstellennachfrage "+activity.getElementName());
					activity.addFollowupactivity(newActivity);
				}
			}
			deriveBuildConfigurationActivities(base, target, activity.getSubactivities());
		}
		
	}

	private static RepositoryComponent getComponentOfRequiredRole(
			Activity activity) {
		RepositoryComponent component = null;
		if (activity.getElement()!=null) {
			if (((RequiredRole)(activity.getElement())).getRequiringEntity_RequiredRole() instanceof RepositoryComponent) {
				component = (RepositoryComponent) ((RequiredRole)(activity.getElement())).getRequiringEntity_RequiredRole();
			}
		}
		return component;
	}

	
	private static boolean componentIsNotThirdPartyOrLibrary(
			ArchitectureVersion version, RepositoryComponent component) {
		return ArchitectureAnnotationLookup.lookUpThirdPartyOrLibraryAnnotationForComponent(version, component)!=null;
	}

	private static void deriveBuildExecutionActivities(
			ArchitectureVersion target, List<Activity> activityList) {
		
		for (Activity activity : activityList) {
			if (activityElementIsComponent(activity) && activityIsADDorMODIFY(activity)) {
				if (componentIsNotThirdPartyOrLibrary(target, (RepositoryComponent)activity.getElement())) {
					// verfügbarkeit von quelltext oder metadaten egal
					// TODO: build configuration definiert die einheit
					// TODO: für jede build configuration wird insgesamt nur eine durchführung geplant
					BuildConfiguration buildConfig = ArchitectureAnnotationLookup.lookUpBuildConfigurationForComponent(target, (RepositoryComponent)activity.getElement());
					Activity newActivity = new Activity(ActivityType.BUILDEXECUTION, ActivityElementType.BUILDCONFIGURATION, 
							(buildConfig != null) ? buildConfig.getFilename() : "" , BasicActivity.EXECUTE, "Baudurchführung: Baue Komponente "+activity.getElementName(), "Komponente "+activity.getElementName());
					activity.addFollowupactivity(newActivity);
				}
			}
			deriveBuildExecutionActivities(target, activity.getSubactivities());
		}
	}

	private static boolean activityIsADDorMODIFY(Activity activity) {
		return (activity.getBasicActivity()==BasicActivity.ADD) || (activity.getBasicActivity()==BasicActivity.MODIFY);
	}
	
	private static void deriveTestDevelopmentActivities(
			ArchitectureVersion base, ArchitectureVersion target, List<Activity> baseActivityList) {

		// nur entfernen von vorhandenen tests
		
		// nur prüfen und aktualisieren von vorhandenen tests
		
		for (Activity activity : baseActivityList) {
			if (activity.getElementType() == ActivityElementType.PROVIDEDROLE && activity.getBasicActivity()==BasicActivity.ADD) {
				Activity newActivity = new Activity(ActivityType.TESTDEVELOPMENT, ActivityElementType.TESTCASE, 
						"", BasicActivity.ADD, "Testentwicklung: Entwickle Unit-Testfälle für Schnittstellenangebot.", 
						"Schnittstellenangebot "+activity.getElementName());
				activity.addFollowupactivity(newActivity);
			} else if (activity.getElementType() == ActivityElementType.PROVIDEDROLE && activity.getBasicActivity()==BasicActivity.REMOVE) {
				int numberOfUnitTests = numberOfAvailableUnitTests(base, activity);
				if (numberOfUnitTests>0) {
					Activity newActivity = new Activity(ActivityType.TESTDEVELOPMENT, ActivityElementType.TESTCASE, 
							numberOfUnitTests+ " Tests", BasicActivity.REMOVE, "Testentwicklung: Entferne Unit-Testfälle für Schnittstellenangebot.", 
							"Schnittstellenangebot "+activity.getElementName());
					activity.addFollowupactivity(newActivity);
				}
			} else if (activity.getElementType() == ActivityElementType.PROVIDEDROLE && activity.getBasicActivity()==BasicActivity.MODIFY) {
				int numberOfUnitTests = numberOfAvailableUnitTests(target, activity);
				if (numberOfUnitTests>0) {
					Activity newActivity = new Activity(ActivityType.TESTUPDATE, ActivityElementType.TESTCASE, 
						numberOfUnitTests+ " Tests", BasicActivity.CHECKANDUPDATE, "Testaktualisierung: Prüfe und aktualisiere Testfälle für Schnittstellenangebot.", 
						"Schnittstellenangebot "+activity.getElementName());
					activity.addFollowupactivity(newActivity);
				}
			}
			deriveTestDevelopmentActivities(base, target, activity.getSubactivities());
		}
	}

	private static int numberOfAvailableUnitTests(ArchitectureVersion version,
			Activity activity) {
		int numberOfTests = 0;
		if (activity.getElement() instanceof ProvidedRole) {
			UnitTestCaseAggregation testAggregation = ArchitectureAnnotationLookup.lookUpUnitTestAggregationForProvidedRole(version, (ProvidedRole)activity.getElement());
			List<UnitTestCase> testCases = ArchitectureAnnotationLookup.lookUpUnitTestCasesForProvidedRole(version, (ProvidedRole)activity.getElement());
			if (testAggregation!=null)
				numberOfTests += testAggregation.getNumberOfTestcases();
			numberOfTests += testCases.size();
		}
		return numberOfTests;
	}
	
	private static void deriveTestExecutionActivities(ArchitectureVersion target,
			List<Activity> baseActivityList) {
		
		for (Activity activity : baseActivityList) {
			if (activity.getElementType() == ActivityElementType.PROVIDEDROLE && activity.getBasicActivity()==BasicActivity.ADD) {
				Activity newActivity = new Activity(ActivityType.TESTEXECUTION, ActivityElementType.TESTCASE, "Neue Testfälle", BasicActivity.EXECUTE, 
						"Testdurchführung (Unit-Tests): Führe Testfälle aus.", "Schnittstellenangebot "+activity.getElementName());
				activity.addFollowupactivity(newActivity);
			} else if (activity.getElementType() == ActivityElementType.PROVIDEDROLE && activity.getBasicActivity()==BasicActivity.MODIFY) {
				int numberOfUnitTests = numberOfAvailableUnitTests(target, activity);
				if (numberOfUnitTests>0) {
					Activity newActivity = new Activity(ActivityType.TESTEXECUTION, ActivityElementType.TESTCASE, numberOfUnitTests+ " Tests", BasicActivity.EXECUTE, 
							"Testdurchführung (Unit-Tests): Führe Testfälle aus.", "Schnittstellenangebot "+activity.getElementName());
					activity.addFollowupactivity(newActivity);
				}
			}
			deriveTestExecutionActivities(target, activity.getSubactivities());
		}
	}

//	private static void deriveReleaseConfigurationActivities(
//			List<Activity> baseActivityList) {
//		
//		for (Activity activity : baseActivityList) {
//			if (activity.getElementType() == ActivityElementType.BUILDCONFIGURATION && activity.getBasicActivity()==BasicActivity.MODIFY) {
//				Activity newActivity = new Activity(ActivityType.RELEASECONFIGURATION, ActivityElementType.RELEASECONFIGURATION, "release configuration", BasicActivity.MODIFY, 
//						"Bearbeite Bereitstellungskonfiguration.");
//				activity.addFollowupactivity(newActivity);
//			} 
//		}
//	}

	private static void deriveReleaseExecutionActivities(ArchitectureVersion target,
			List<Activity> baseActivityList) {

		//ArchitectureAnnotationLookup.lookUpReleaseConfigurationForComponent(target, component)

		for (Activity activity : baseActivityList) {
			if (activity.getElementType() == ActivityElementType.BUILDCONFIGURATION && activity.getBasicActivity()==BasicActivity.EXECUTE) {
				Activity newActivity = new Activity(ActivityType.RELEASEEXECUTION, ActivityElementType.RELEASECONFIGURATION, "UserManagement.war", BasicActivity.EXECUTE, 
						"Bereitstellung: Führe Bereitstellung durch.");
				activity.addFollowupactivity(newActivity);
			} 
			deriveReleaseExecutionActivities(target, activity.getFollowupActivities());
		}
	}
	
//	private static void deriveDeploymentConfigurationActivities(
//			List<Activity> baseActivityList) {
//		for (Activity activity : baseActivityList) {
//			if (activity.getType()==ActivityType.RELEASEEXECUTION && activity.getBasicActivity()==BasicActivity.EXECUTE) {
//				Activity newActivity = new Activity(ActivityType.DEPLOYMENTCONFIGURATION, ActivityElementType.DEPLOYMENTCONFIGURATION, "deployment configuration", BasicActivity.MODIFY, 
//						"Inbetriebnahmekonfiguration: Bearbeite Inbetriebnahmekonfiguration.");
//				activity.addFollowupactivity(newActivity);
//			} 
//		}
//	}

	private static void deriveDeploymentExecutionActivities(
			ArchitectureVersion target, List<Activity> baseActivityList) {
		
		// runtime instance annotations !
		
		for (Activity activity : baseActivityList) {
			if (activity.getType()==ActivityType.RELEASEEXECUTION && activity.getBasicActivity()==BasicActivity.EXECUTE) {
				Activity newActivity = new Activity(ActivityType.DEPLOYMENTEXECUTION, ActivityElementType.DEPLOYMENTCONFIGURATION, "1 Laufzeitinstanz", BasicActivity.EXECUTE, 
						"Inbetriebnahmedurchführung: Führe Inbetriebnahme durch.");
				activity.addFollowupactivity(newActivity);
			} 
			deriveDeploymentExecutionActivities(target, activity.getFollowupActivities());
		}
	}
}
