package edu.kit.ipd.sdq.kamp.core.derivation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.compare.diff.metamodel.DiffElement;
import org.eclipse.emf.compare.diff.metamodel.DiffModel;
import org.eclipse.emf.compare.diff.metamodel.DifferenceKind;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeLeftTarget;
import org.eclipse.emf.compare.diff.metamodel.ModelElementChangeRightTarget;
import org.eclipse.emf.compare.diff.metamodel.UpdateReference;
import org.eclipse.emf.compare.diff.service.DiffService;
import org.eclipse.emf.compare.match.metamodel.MatchModel;
import org.eclipse.emf.compare.match.service.MatchService;
import org.eclipse.emf.ecore.EObject;

import de.uka.ipd.sdq.pcm.core.composition.AssemblyConnector;
import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.CollectionDataType;
import de.uka.ipd.sdq.pcm.repository.CompositeComponent;
import de.uka.ipd.sdq.pcm.repository.CompositeDataType;
import de.uka.ipd.sdq.pcm.repository.DataType;
import de.uka.ipd.sdq.pcm.repository.InnerDeclaration;
import de.uka.ipd.sdq.pcm.repository.OperationInterface;
import de.uka.ipd.sdq.pcm.repository.OperationProvidedRole;
import de.uka.ipd.sdq.pcm.repository.OperationRequiredRole;
import de.uka.ipd.sdq.pcm.repository.OperationSignature;
import de.uka.ipd.sdq.pcm.repository.Parameter;
import de.uka.ipd.sdq.pcm.repository.ProvidedRole;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import de.uka.ipd.sdq.pcm.repository.RequiredRole;
import de.uka.ipd.sdq.pcm.repository.Signature;
import edu.kit.ipd.sdq.kamp.core.Activity;
import edu.kit.ipd.sdq.kamp.core.ActivityElementType;
import edu.kit.ipd.sdq.kamp.core.ActivityType;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersion;
import edu.kit.ipd.sdq.kamp.core.BasicActivity;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyComponent;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyProvidedRole;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifySignature;

public class DifferenceCalculation {
	public static DiffModel calculateDiffModel(EObject source, EObject target) {
		MatchModel matchModel = null;
		try {
			matchModel = MatchService.doMatch(target, source, null);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		DiffModel diff = null;
		if (matchModel != null) {
			diff = DiffService.doDiff(matchModel);
		}
		
		return diff;
	}

	public static boolean checkDiffElement(DiffElement diffElement, Class diffElementType, DifferenceKind kind) {
		if ((diffElementType.isInstance(diffElement))&&(diffElement.getKind() == kind)) 
			return true;
		
		return false;
	}
	
	public static boolean checkUpdateReference(UpdateReference diffElement, Class leftElementType, Class leftTargetType) {
		if ((leftElementType.isInstance(diffElement.getLeftElement()))&&(leftTargetType.isInstance(diffElement.getLeftTarget()))) 
			return true;
		
		return false;
	}
	
	public static boolean checkModelElementChangeLeftTarget(ModelElementChangeLeftTarget diffElement, Class leftElementType) {
		if (leftElementType.isInstance(diffElement.getLeftElement())) 
			return true;
		
		return false;
	}
	
	public static boolean checkModelElementChangeRightTarget(ModelElementChangeRightTarget diffElement, Class leftElementType) {
		if (leftElementType.isInstance(diffElement.getRightElement())) 
			return true;
		
		return false;
	}
	
	
	public static boolean detectionRuleAddedBasicComponent(DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeLeftTarget.class, DifferenceKind.ADDITION)
				&&checkModelElementChangeLeftTarget((ModelElementChangeLeftTarget)diffElement, BasicComponent.class))
			return true;
		
		return false;
	}
	
	public static boolean detectionRuleDeletedBasicComponent(DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeRightTarget.class, DifferenceKind.DELETION)
				&&checkModelElementChangeRightTarget((ModelElementChangeRightTarget)diffElement, BasicComponent.class))
			return true;
		
		return false;
	}
	
	public static boolean detectionRuleAddedInterface(DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeLeftTarget.class, DifferenceKind.ADDITION)
				&&checkModelElementChangeLeftTarget((ModelElementChangeLeftTarget)diffElement, OperationInterface.class))
			return true;
		
		return false;
	}
	
	public static boolean detectionRuleDeletedInterface(DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeRightTarget.class, DifferenceKind.DELETION)
				&&checkModelElementChangeRightTarget((ModelElementChangeRightTarget)diffElement, OperationInterface.class))
			return true;
		
		return false;
	}

	public static boolean detectionRuleAddedSignature(DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeLeftTarget.class, DifferenceKind.ADDITION)
				&&checkModelElementChangeLeftTarget((ModelElementChangeLeftTarget)diffElement, OperationSignature.class))
			return true;
		
		return false;
	}

	public static boolean detectionRuleDeletedSignature(DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeRightTarget.class, DifferenceKind.DELETION)
				&&checkModelElementChangeRightTarget((ModelElementChangeRightTarget)diffElement, OperationSignature.class))
			return true;
		
		return false;
	}

	public static boolean detectionRuleAddedParameter(DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeLeftTarget.class, DifferenceKind.ADDITION)
				&&checkModelElementChangeLeftTarget((ModelElementChangeLeftTarget)diffElement, Parameter.class))
			return true;
		
		return false;
	}

	public static boolean detectionRuleDeletedParameter(DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeRightTarget.class, DifferenceKind.DELETION)
				&&checkModelElementChangeRightTarget((ModelElementChangeRightTarget)diffElement, Parameter.class))
			return true;
		
		return false;
	}

	public static boolean detectionRuleDeletedInterfaceForProvidedRole(DiffElement diffElement) {
		if (checkDiffElement(diffElement, UpdateReference.class, DifferenceKind.CHANGE)
				&&checkUpdateReference((UpdateReference)diffElement, ProvidedRole.class, OperationInterface.class))
			return true;
		
		return false;
	}

	public static boolean detectionRuleDeletedInterfaceForRequiredRole(DiffElement diffElement) {
		if (checkDiffElement(diffElement, UpdateReference.class, DifferenceKind.CHANGE)
				&&checkUpdateReference((UpdateReference)diffElement, RequiredRole.class, OperationInterface.class))
			return true;
		
		return false;
	}

	public static boolean detectionRuleAddedProvidedRole(DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeLeftTarget.class, DifferenceKind.ADDITION)
				&&checkModelElementChangeLeftTarget((ModelElementChangeLeftTarget)diffElement, OperationProvidedRole.class))
			return true;
		
		return false;
	}
	
	public static boolean detectionRuleAddedRequiredRole(DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeLeftTarget.class, DifferenceKind.ADDITION)
				&&checkModelElementChangeLeftTarget((ModelElementChangeLeftTarget)diffElement, OperationRequiredRole.class))
			return true;
		
		return false;
	}

	public static boolean detectionRuleDeletedProvidedRole(DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeRightTarget.class, DifferenceKind.DELETION)
				&&checkModelElementChangeRightTarget((ModelElementChangeRightTarget)diffElement, OperationProvidedRole.class))
			return true;
		
		return false;
	}
	
	public static boolean detectionRuleDeletedRequiredRole(DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeRightTarget.class, DifferenceKind.DELETION)
				&&checkModelElementChangeRightTarget((ModelElementChangeRightTarget)diffElement, OperationRequiredRole.class))
			return true;
		
		return false;
	}
	
	public static EObject retrieveArchitectureElement(DiffElement diffElement) {
		if (diffElement instanceof ModelElementChangeRightTarget) {
			return ((ModelElementChangeRightTarget)diffElement).getRightElement();
		} else if (diffElement instanceof ModelElementChangeLeftTarget) {
			return ((ModelElementChangeLeftTarget)diffElement).getLeftElement();
		} else if (diffElement instanceof UpdateReference) {
			return ((UpdateReference)diffElement).getLeftElement();
		} else {
			return null;
		}
	}
	
	public static List<DiffElement> foundAddedBasicComponent(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleAddedBasicComponent(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}
	
	public static List<DiffElement> foundAddedInterface(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleAddedInterface(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}
	
	public static List<DiffElement> foundDeletedBasicComponent(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleDeletedBasicComponent(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}

	public static List<DiffElement> foundDeletedInterface(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleDeletedInterface(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}

	public static List<DiffElement> foundAddedSignature(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleAddedSignature(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}

	public static List<DiffElement> foundDeletedSignature(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleDeletedSignature(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}
	
	public static List<DiffElement> foundAddedParameter(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleAddedParameter(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}

	public static List<DiffElement> foundDeletedParameter(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleDeletedParameter(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}


	public static List<DiffElement> foundDeletedInterfaceForProvidedRole(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleDeletedInterfaceForProvidedRole(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}

	public static List<DiffElement> foundDeletedInterfaceForRequiredRole(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleDeletedInterfaceForRequiredRole(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}

	public static List<DiffElement> foundAddedProvidedRole(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleAddedProvidedRole(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}
	
	public static List<DiffElement> foundAddedRequiredRole(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleAddedRequiredRole(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}
	
	public static List<DiffElement> foundDeletedProvidedRole(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleDeletedProvidedRole(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}

	public static List<DiffElement> foundDeletedRequiredRole(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleDeletedRequiredRole(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}

	public static List<Activity> deriveBaseWorkPlan(DiffModel diff) {
		List<Activity> workplan = new ArrayList<Activity>();
		
		for (DiffElement diffElement : diff.getDifferences()) {
			
				// INTERFACE + 
			if (detectionRuleAddedInterface(diffElement)) {
				OperationInterface architectureElement = (OperationInterface)retrieveArchitectureElement(diffElement);
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.INTERFACE, architectureElement.getEntityName(), BasicActivity.ADD, 
						"FŸge Schnittstelle "+architectureElement.getEntityName()+" hinzu.");
				workplan.add(newActivity);
				newActivity.addSubactivities(SubactivityDerivation.deriveSubactivities(architectureElement, BasicActivity.ADD));
			
				// INTERFACE -
			} else if (detectionRuleDeletedInterface(diffElement)) {
				OperationInterface architectureElement = (OperationInterface)retrieveArchitectureElement(diffElement);
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.INTERFACE, architectureElement.getEntityName(), BasicActivity.REMOVE, 
						"Entferne Schnittstelle "+architectureElement.getEntityName()+".");
				workplan.add(newActivity);
				newActivity.addSubactivities(SubactivityDerivation.deriveSubactivities(architectureElement, BasicActivity.REMOVE));

				// SIGNATURE +
			} else if (detectionRuleAddedSignature(diffElement)) {

				// SIGNATURE -
			} else if (detectionRuleDeletedSignature(diffElement)) {

				// PARAMETER +
			} else if (detectionRuleAddedParameter(diffElement)) {
				
				// PARAMETER -
			} else if (detectionRuleDeletedParameter(diffElement)) {
		    
			    // COLLECTION-DATATYPE +
			} else if (detectionRuleAddedCollectionDatatype(diffElement)) {
				CollectionDataType architectureElement = (CollectionDataType)retrieveArchitectureElement(diffElement);
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.COLLECTIONDATATYPE, architectureElement.getEntityName(), BasicActivity.ADD, 
						"FŸge Kollektions-Datentyp "+architectureElement.getEntityName()+" hinzu.");
				workplan.add(newActivity);

				// COLLECTION-DATATYPE -
			} else if (detectionRuleRemovedCollectionDatatype(diffElement)) {
				CollectionDataType architectureElement = (CollectionDataType)retrieveArchitectureElement(diffElement);
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.COLLECTIONDATATYPE, architectureElement.getEntityName(), BasicActivity.REMOVE, 
						"Entferne Kollektions-Datentyp "+architectureElement.getEntityName()+".");
				workplan.add(newActivity);
				
				// COMPOSITE-DATATYPE +
			} else if (detectionRuleAddedCompositeDatatype(diffElement)) {
				CompositeDataType architectureElement = (CompositeDataType)retrieveArchitectureElement(diffElement);
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.COMPOSITEDATATYPE, architectureElement.getEntityName(), BasicActivity.ADD, 
						"FŸge Komposit-Datentyp "+architectureElement.getEntityName()+" hinzu.");
				workplan.add(newActivity);

				// COMPOSITE-DATATYPE -
			} else if (detectionRuleRemovedCompositeDatatype(diffElement)) {
				CompositeDataType architectureElement = (CompositeDataType)retrieveArchitectureElement(diffElement);
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.COMPOSITEDATATYPE, architectureElement.getEntityName(), BasicActivity.REMOVE, 
						"Entferne Komposit-Datentyp "+architectureElement.getEntityName()+".");
				workplan.add(newActivity);
				
				// INNERDECLARATION +
			} else if (detectionRuleAddedInnerDeclaration(diffElement)) {
				InnerDeclaration architectureElement = (InnerDeclaration) retrieveArchitectureElement(diffElement);
				CompositeDataType datatype = architectureElement.getCompositeDataType_InnerDeclaration();
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.COMPOSITECOMPONENT, datatype.getEntityName(), BasicActivity.MODIFY, 
						"FŸge innere Deklaration zu Komposit-Datentyp "+datatype.getEntityName()+" hinzu.");
				workplan.add(newActivity);
				
				// INNERDECLARATION -
			} else if (detectionRuleRemovedInnerDeclaration(diffElement)) {
				InnerDeclaration architectureElement = (InnerDeclaration) retrieveArchitectureElement(diffElement);
				CompositeDataType datatype = architectureElement.getCompositeDataType_InnerDeclaration();
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.COMPOSITECOMPONENT, datatype.getEntityName(), BasicActivity.MODIFY, 
						"Entferne innere Deklaration von Komposit-Datentyp "+datatype.getEntityName()+".");
				workplan.add(newActivity);
				
				// BASIC-COMPONENT +
			} else if (detectionRuleAddedBasicComponent(diffElement)) {
				BasicComponent architectureElement = (BasicComponent) retrieveArchitectureElement(diffElement);
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.BASICCOMPONENT, architectureElement.getEntityName(), BasicActivity.ADD, 
						"FŸge Basis-Komponente "+architectureElement.getEntityName()+" hinzu.");
				workplan.add(newActivity);
				newActivity.addSubactivities(SubactivityDerivation.deriveSubactivities(architectureElement, BasicActivity.ADD));
				
				// BASIC-COMPONENT -
			} else if (detectionRuleDeletedBasicComponent(diffElement)) {
				BasicComponent architectureElement = (BasicComponent) retrieveArchitectureElement(diffElement);
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.BASICCOMPONENT, architectureElement.getEntityName(), BasicActivity.REMOVE, 
						"Entferne Basis-Komponente "+architectureElement.getEntityName()+".");
				workplan.add(newActivity);
				newActivity.addSubactivities(SubactivityDerivation.deriveSubactivities(architectureElement, BasicActivity.REMOVE));
				
				// COMPOSITE-COMPONENT +
			} else if (detectionRuleAddedCompositeComponent(diffElement)) {
				CompositeComponent architectureElement = (CompositeComponent) retrieveArchitectureElement(diffElement);
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.COMPOSITECOMPONENT, architectureElement.getEntityName(), BasicActivity.ADD, 
						"FŸge Komposit-Komponente "+architectureElement.getEntityName()+" hinzu.");
				workplan.add(newActivity);
				newActivity.addSubactivities(SubactivityDerivation.deriveSubactivities(architectureElement, BasicActivity.ADD));
				
				// COMPOSITE-COMPONENT -
			} else if (detectionRuleDeletedCompositeComponent(diffElement)) {
				CompositeComponent architectureElement = (CompositeComponent) retrieveArchitectureElement(diffElement);
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.COMPOSITECOMPONENT, architectureElement.getEntityName(), BasicActivity.REMOVE, 
						"Entferne Komposit-Komponente "+architectureElement.getEntityName()+".");
				workplan.add(newActivity);
				newActivity.addSubactivities(SubactivityDerivation.deriveSubactivities(architectureElement, BasicActivity.REMOVE));
			
				// PROVIDED-ROLE +
			} else if (detectionRuleAddedProvidedRole(diffElement)) {
				OperationProvidedRole architectureElement = (OperationProvidedRole)retrieveArchitectureElement(diffElement);
				
				String componentName = architectureElement.getProvidingEntity_ProvidedRole().getEntityName();
				
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.PROVIDEDROLE, architectureElement.getEntityName(), BasicActivity.ADD, 
						"FŸge Schnittstellenangebot "+architectureElement.getEntityName()+" zu Komponente "+componentName+" hinzu.");
				workplan.add(newActivity);
				newActivity.addSubactivities(SubactivityDerivation.deriveSubactivities(architectureElement, BasicActivity.ADD));

				// PROVIDED-ROLE -
			} else if (detectionRuleDeletedProvidedRole(diffElement)) {
				OperationProvidedRole architectureElement = (OperationProvidedRole)retrieveArchitectureElement(diffElement);
				String componentName = architectureElement.getProvidingEntity_ProvidedRole().getEntityName();
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.PROVIDEDROLE, architectureElement.getEntityName(), BasicActivity.REMOVE, 
						"Entferne Schnittstellenangebot "+architectureElement.getEntityName()+" von Komponente "+componentName+".");
				workplan.add(newActivity);
				newActivity.addSubactivities(SubactivityDerivation.deriveSubactivities(architectureElement, BasicActivity.REMOVE));

				// REQUIRED-ROLE + 
			} else if (detectionRuleAddedRequiredRole(diffElement)) {
				OperationRequiredRole architectureElement = (OperationRequiredRole)retrieveArchitectureElement(diffElement);
				String componentName = architectureElement.getRequiringEntity_RequiredRole().getEntityName();
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.REQUIREDROLE, architectureElement.getEntityName(), BasicActivity.ADD, 
						"FŸge Schnittstellennachfrage "+architectureElement.getEntityName()+" zu Komponente "+componentName+" hinzu.");
				workplan.add(newActivity);
				newActivity.addSubactivities(SubactivityDerivation.deriveSubactivities(architectureElement, BasicActivity.REMOVE));

				// REQUIRED-ROLE - 
			} else if (detectionRuleDeletedRequiredRole(diffElement)) {
				OperationRequiredRole architectureElement = (OperationRequiredRole)retrieveArchitectureElement(diffElement);
				String componentName = architectureElement.getRequiringEntity_RequiredRole().getEntityName();
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.REQUIREDROLE, architectureElement.getEntityName(), BasicActivity.REMOVE, 
						"Entferne Schnittstellennachfrage "+architectureElement.getEntityName()+" von Komponente "+componentName+".");
				workplan.add(newActivity);
				newActivity.addSubactivities(SubactivityDerivation.deriveSubactivities(architectureElement, BasicActivity.REMOVE));

				// ASSEMBLY-CONNECTOR +
			} else if (detectionRuleAddedAssemblyConnector(diffElement)) {
				AssemblyConnector architectureElement = (AssemblyConnector)retrieveArchitectureElement(diffElement);
				
				String desc = calculateDescriptionForAssemblyConnector(architectureElement);
				
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.ASSEMBLYCONNECTOR, architectureElement.getEntityName(), BasicActivity.ADD, 
						"FŸge Assemblierungskonnektor ("+desc+") hinzu.");
				workplan.add(newActivity);
			    
				// ASSEMBLY-CONNECTOR -
			} else if (detectionRuleRemovedAssemblyConnector(diffElement)) {
				AssemblyConnector architectureElement = (AssemblyConnector)retrieveArchitectureElement(diffElement);
				String desc = calculateDescriptionForAssemblyConnector(architectureElement);
				Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.ASSEMBLYCONNECTOR, architectureElement.getEntityName(), BasicActivity.REMOVE, 
						"Entferne Assemblierungskonnektor ("+desc+").");
				workplan.add(newActivity);
			}
		} 
	
		return workplan;
	}

	private static String calculateDescriptionForAssemblyConnector(
			AssemblyConnector architectureElement) {
		String desc = architectureElement.getProvidingAssemblyContext_AssemblyConnector().getEntityName() + "." +
		architectureElement.getProvidedRole_AssemblyConnector().getEntityName() + " -> "+
		architectureElement.getRequiringAssemblyContext_AssemblyConnector().getEntityName() + "." +
		architectureElement.getRequiredRole_AssemblyConnector().getEntityName();
		return desc;
	}
	
	
//	private static EObject getReferenceTarget(DiffElement diffElement) {
//		
//		if (diffElement instanceof UpdateReference) {
//			return ((UpdateReference)diffElement).getLeftTarget();
//		} else if (diffElement instanceof ModelElementChangeLeftTarget) {
//			return ((ModelElementChangeLeftTarget)diffElement).getLeftElement();
//		}
//		
//		return null;
//	}

	public static List<DiffElement> foundAddedAssemblyConnector(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleAddedAssemblyConnector(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}

	public static List<DiffElement> foundRemovedAssemblyConnector(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleRemovedAssemblyConnector(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}

	public static boolean detectionRuleAddedAssemblyConnector(
			DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeLeftTarget.class, DifferenceKind.ADDITION)
				&&checkModelElementChangeLeftTarget((ModelElementChangeLeftTarget)diffElement, AssemblyConnector.class))
			return true;
		
		return false;
	}

	public static boolean detectionRuleRemovedAssemblyConnector(
			DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeRightTarget.class, DifferenceKind.DELETION)
				&&checkModelElementChangeRightTarget((ModelElementChangeRightTarget)diffElement, AssemblyConnector.class))
			return true;
		
		return false;
	}

	public static List<Activity> deriveWorkplan(
			ArchitectureVersion baseVersion,
			ArchitectureVersion targetVersion) {
		List<Activity> activityList = new ArrayList<Activity>();
		
		DiffModel repositoryDiff = calculateDiffModel(baseVersion.getRepository(), targetVersion.getRepository());
		DiffModel systemDiff = calculateDiffModel(baseVersion.getSystem(), targetVersion.getSystem());

		List<Activity> repositoryActivities = deriveBaseWorkPlan(repositoryDiff);
		activityList.addAll(repositoryActivities);

		List<Activity> systemActivities = deriveBaseWorkPlan(systemDiff);
		activityList.addAll(systemActivities);

		List<Activity> internalModificationActivities = InternalModificationDerivation.deriveInternalModifications(targetVersion);
		activityList.addAll(internalModificationActivities);
		
		return activityList;
	}

	
	public static List<DiffElement> foundAddedCollectionDatatype(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleAddedCollectionDatatype(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;	}

	private static boolean detectionRuleAddedCollectionDatatype(
			DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeLeftTarget.class, DifferenceKind.ADDITION)
				&&checkModelElementChangeLeftTarget((ModelElementChangeLeftTarget)diffElement, CollectionDataType.class))
			return true;
		
		return false;
	}

	public static List<DiffElement> foundAddedCompositeDatatype(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleAddedCompositeDatatype(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;	}

	private static boolean detectionRuleAddedCompositeDatatype(
			DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeLeftTarget.class, DifferenceKind.ADDITION)
				&&checkModelElementChangeLeftTarget((ModelElementChangeLeftTarget)diffElement, CompositeDataType.class))
			return true;
		
		return false;
	}

	public static List<DiffElement> foundDeletedCollectionDatatype(
			DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleRemovedCollectionDatatype(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}

	public static boolean detectionRuleRemovedCollectionDatatype(
			DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeRightTarget.class, DifferenceKind.DELETION)
				&&checkModelElementChangeRightTarget((ModelElementChangeRightTarget)diffElement, CollectionDataType.class))
			return true;
		
		return false;
	}

	
	public static List<DiffElement> foundDeletedCompositeDatatype(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleRemovedCompositeDatatype(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}

	public static boolean detectionRuleRemovedCompositeDatatype(
			DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeRightTarget.class, DifferenceKind.DELETION)
				&&checkModelElementChangeRightTarget((ModelElementChangeRightTarget)diffElement, CompositeDataType.class))
			return true;
		
		return false;
	}

	public static List<DiffElement> foundAddedInnerDeclarationOfCompositeDatatype(
			DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleAddedInnerDeclaration(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}

	public static List<DiffElement> foundDeletedInnerDeclarationOfCompositeDatatype(
			DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleRemovedInnerDeclaration(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}
	
	private static boolean detectionRuleAddedInnerDeclaration(
			DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeLeftTarget.class, DifferenceKind.ADDITION)
				&&checkModelElementChangeLeftTarget((ModelElementChangeLeftTarget)diffElement, InnerDeclaration.class))
			return true;
		
		return false;
	}

	public static boolean detectionRuleRemovedInnerDeclaration(
			DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeRightTarget.class, DifferenceKind.DELETION)
				&&checkModelElementChangeRightTarget((ModelElementChangeRightTarget)diffElement, InnerDeclaration.class))
			return true;
		
		return false;
	}

	public static List<DiffElement> foundAddedCompositeComponent(DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleAddedCompositeComponent(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}


	private static boolean detectionRuleAddedCompositeComponent(
			DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeLeftTarget.class, DifferenceKind.ADDITION)
				&&checkModelElementChangeLeftTarget((ModelElementChangeLeftTarget)diffElement, CompositeComponent.class))
			return true;
		
		return false;
	}

	public static List<DiffElement> foundDeletedCompositeComponent(
			DiffModel diff) {
		List<DiffElement> result = new ArrayList<DiffElement>();
		for (DiffElement diffElement : diff.getDifferences()) {
			if (detectionRuleDeletedCompositeComponent(diffElement)) {
				result.add(diffElement);
			}
		} 
		return result;
	}

	private static boolean detectionRuleDeletedCompositeComponent(
			DiffElement diffElement) {
		if (checkDiffElement(diffElement, ModelElementChangeRightTarget.class, DifferenceKind.DELETION)
				&&checkModelElementChangeRightTarget((ModelElementChangeRightTarget)diffElement, CompositeComponent.class))
			return true;
		
		return false;
	}


}
