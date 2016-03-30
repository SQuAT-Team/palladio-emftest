package edu.kit.ipd.sdq.kamp.core.derivation;

import java.util.ArrayList;
import java.util.List;

import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.OperationInterface;
import de.uka.ipd.sdq.pcm.repository.OperationProvidedRole;
import de.uka.ipd.sdq.pcm.repository.OperationRequiredRole;
import de.uka.ipd.sdq.pcm.repository.OperationSignature;
import de.uka.ipd.sdq.pcm.repository.ProvidedRole;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import de.uka.ipd.sdq.pcm.repository.RequiredRole;
import edu.kit.ipd.sdq.kamp.core.Activity;
import edu.kit.ipd.sdq.kamp.core.ActivityElementType;
import edu.kit.ipd.sdq.kamp.core.ActivityType;
import edu.kit.ipd.sdq.kamp.core.BasicActivity;

public class SubactivityDerivation {
	public static List<Activity> deriveSubactivities(RepositoryComponent basicComponent, BasicActivity basicActivity) {
		List<Activity> workplan = new ArrayList<Activity>();
		
		for (ProvidedRole providedRole : basicComponent.getProvidedRoles_InterfaceProvidingEntity()) {
			String description = "";
			if (basicActivity.equals(BasicActivity.ADD)) {
				description = "FŸge Schnittstellenangebot "+providedRole.getEntityName()+" zu Komponente "+basicComponent.getEntityName()+" hinzu.";
			} else if (basicActivity.equals(BasicActivity.MODIFY)) {
				description = "Modifiziere Schnittstellenangebot "+providedRole.getEntityName()+" der Komponente "+basicComponent.getEntityName()+".";
			} else if (basicActivity.equals(BasicActivity.REMOVE)) {
				description = "Entferne Schnittstellenangebot "+providedRole.getEntityName()+" von der Komponente "+basicComponent.getEntityName()+".";
			}
			
			Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.PROVIDEDROLE, providedRole.getEntityName(), basicActivity, description);
			workplan.add(newActivity);
			newActivity.addSubactivities(SubactivityDerivation.deriveSubactivities((OperationProvidedRole) providedRole, basicActivity));
		}

		for (RequiredRole requiredRole : basicComponent.getRequiredRoles_InterfaceRequiringEntity()) {
			String description = "";
			if (basicActivity.equals(BasicActivity.ADD)) {
				description = "FŸge Schnittstellennachfrage "+requiredRole.getEntityName()+" zu Komponente "+basicComponent.getEntityName()+" hinzu.";
			} else if (basicActivity.equals(BasicActivity.MODIFY)) {
				description = "Modifiziere Schnittstellennachfrage "+requiredRole.getEntityName()+" der Komponente "+basicComponent.getEntityName()+".";
			} else if (basicActivity.equals(BasicActivity.REMOVE)) {
				description = "Entferne Schnittstellennachfrage "+requiredRole.getEntityName()+" von der Komponente "+basicComponent.getEntityName()+".";
			}
			Activity newActivity = new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.REQUIREDROLE, requiredRole.getEntityName(), basicActivity, description);
			workplan.add(newActivity);
			newActivity.addSubactivities(SubactivityDerivation.deriveSubactivities((OperationRequiredRole) requiredRole, basicActivity));
		}

		return workplan;
	}

	public static List<Activity> deriveSubactivities(OperationInterface operationInterface, BasicActivity basicActivity) {
		List<Activity> workplan = new ArrayList<Activity>();
		
		for (OperationSignature operationSignature : operationInterface.getSignatures__OperationInterface()) {

			String description = "";
			if (basicActivity.equals(BasicActivity.ADD)) {
				description = "FŸge Schnittstellenoperation "+operationSignature.getEntityName()+" zur Schnittstelle "+operationInterface.getEntityName()+" hinzu.";
			} else if (basicActivity.equals(BasicActivity.MODIFY)) {
				description = "Modifiziere Schnittstellenoperation "+operationSignature.getEntityName()+" der Schnittstelle "+operationInterface.getEntityName()+".";
			} else if (basicActivity.equals(BasicActivity.REMOVE)) {
				description = "Entferne Schnittstellenoperation "+operationSignature.getEntityName()+" von der Schnittstelle "+operationInterface.getEntityName()+".";
			}

			workplan.add(new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.INTERFACESIGNATURE, operationSignature.getEntityName(), basicActivity, description));
		}

		return workplan;
	}

	public static List<Activity> deriveSubactivities(OperationProvidedRole providedRole, BasicActivity basicActivity) {
		List<Activity> workplan = new ArrayList<Activity>();
		
		for (OperationSignature operationSignature : providedRole.getProvidedInterface__OperationProvidedRole().getSignatures__OperationInterface()) {
			String description = "";
			if (basicActivity.equals(BasicActivity.ADD)) {
				description = "FŸge angebotene Operation "+operationSignature.getEntityName()+" zum Schnittstellenangebot "+providedRole.getEntityName()+" hinzu.";
			} else if (basicActivity.equals(BasicActivity.MODIFY)) {
				description = "Modifiziere angebotene Operation "+operationSignature.getEntityName()+" des Schnittstellenangebots "+providedRole.getEntityName()+".";
			} else if (basicActivity.equals(BasicActivity.REMOVE)) {
				description = "Entferne angebotene Operation "+operationSignature.getEntityName()+" vom Schnittstellenangebot "+providedRole.getEntityName()+".";
			}

			workplan.add(new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.PROVIDEDOPERATION, operationSignature.getEntityName(), basicActivity, description));
		}

		return workplan;
	}
	
	public static List<Activity> deriveSubactivities(OperationRequiredRole requiredRole, BasicActivity basicActivity) {
		List<Activity> workplan = new ArrayList<Activity>();
		
		for (OperationSignature operationSignature : requiredRole.getRequiredInterface__OperationRequiredRole().getSignatures__OperationInterface()) {
			String description = "";
			if (basicActivity.equals(BasicActivity.ADD)) {
				description = "FŸge nachgefragte Operation "+operationSignature.getEntityName()+" zur Schnittstellennachfrage "+requiredRole.getEntityName()+" hinzu.";
			} else if (basicActivity.equals(BasicActivity.MODIFY)) {
				description = "Modifiziere nachgefragte Operation "+operationSignature.getEntityName()+" der Schnittstellennachfrage "+requiredRole.getEntityName()+".";
			} else if (basicActivity.equals(BasicActivity.REMOVE)) {
				description = "Entferne nachgefragte Operation "+operationSignature.getEntityName()+" von der Schnittstellennachfrage "+requiredRole.getEntityName()+".";
			}
			workplan.add(new Activity(ActivityType.ARCHITECTUREMODELDIFF, ActivityElementType.REQUIREDOPERATION, operationSignature.getEntityName(), basicActivity, description));
		}

		return workplan;
	}

}
