package edu.kit.ipd.sdq.kamp.core.derivation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.uka.ipd.sdq.pcm.repository.ProvidedRole;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import de.uka.ipd.sdq.pcm.repository.Signature;
import edu.kit.ipd.sdq.kamp.core.Activity;
import edu.kit.ipd.sdq.kamp.core.ActivityElementType;
import edu.kit.ipd.sdq.kamp.core.ActivityType;
import edu.kit.ipd.sdq.kamp.core.ArchitectureModelLookup;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersion;
import edu.kit.ipd.sdq.kamp.core.BasicActivity;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyComponent;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyProvidedRole;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifySignature;

public class InternalModificationDerivation {

	public static List<Activity> deriveInternalModifications(
			ArchitectureVersion targetVersion) {
		
		List<Activity> activityList = new ArrayList<Activity>();
		
		// avoid duplicates!
		Map<RepositoryComponent, Activity> component2activityMap = new HashMap<RepositoryComponent, Activity>();
		
		// ignore "abgelehnte markierungen"
		
		
		if (targetVersion.getModificationMarkRepository() != null) {
			List<ModifyComponent> modifyComponents = ArchitectureModelLookup.lookUpAllComponentModificationMarks(targetVersion);
			for (ModifyComponent modifyComponent : modifyComponents) {

				RepositoryComponent repositoryComponent = modifyComponent.getComponent();
				if (repositoryComponent!=null) {
					Activity componentActivity = component2activityMap.get(repositoryComponent);
					if (componentActivity==null) {
					   componentActivity = new Activity(ActivityType.INTERNALMODIFICATIONMARK, ActivityElementType.BASICCOMPONENT, modifyComponent.getComponent().getEntityName(), BasicActivity.MODIFY, 
							"Modifiziere Komponente "+repositoryComponent.getEntityName()+".", repositoryComponent);
						activityList.add(componentActivity);
						component2activityMap.put(repositoryComponent, componentActivity);
					}
	
					deriveProvidedRoleModifications(modifyComponent, componentActivity);
				}
			}
		}
		
		return activityList;
	}

	private static void deriveProvidedRoleModifications(
			ModifyComponent modifyComponent, Activity componentActivity) {
		for (ModifyProvidedRole modifyProvidedRole : modifyComponent.getProvidedroleModifications()) {
			
			ProvidedRole providedRole = modifyProvidedRole.getProvidedrole();
			
		    Activity providedRoleActivity = tryToGetExistingProvidedRoleActivity(componentActivity, providedRole);
			
			if (providedRoleActivity == null) {
				providedRoleActivity = new Activity(ActivityType.INTERNALMODIFICATIONMARK, ActivityElementType.PROVIDEDROLE, modifyProvidedRole.getProvidedrole().getEntityName(), BasicActivity.MODIFY, 
					"Modifiziere Schnittstellenangebot "+ providedRole.getEntityName()+".", providedRole);
				componentActivity.addSubactivity(providedRoleActivity);
			}

			deriveSignatureModifications(modifyProvidedRole, providedRoleActivity);
		}
	}

	private static Activity tryToGetExistingProvidedRoleActivity(
			Activity componentActivity, ProvidedRole providedRole) {
		for (Activity subactivity : componentActivity.getSubactivities()) {
			if ((subactivity.getElementType()==ActivityElementType.PROVIDEDROLE)&&(subactivity.getElement()==providedRole)) {
				return subactivity;
				
			}
		}
		return null;
	}

	private static void deriveSignatureModifications(
			ModifyProvidedRole modifyProvidedRole, Activity providedRoleActivity) {
		for (ModifySignature modifySignature : modifyProvidedRole.getSignatureModifications()) {
			
			Signature signature = modifySignature.getSignature();
						
			Activity providedOperationActivity = tryToGetExistingSignatureActivity(providedRoleActivity, signature);
			
			if (providedOperationActivity != null) {
				providedOperationActivity = new Activity(ActivityType.INTERNALMODIFICATIONMARK, ActivityElementType.PROVIDEDOPERATION, 
						modifySignature.getSignature().getEntityName(), BasicActivity.MODIFY, 
						"Modifiziere angebotene Operation "+ signature.getEntityName() + ".", signature);
				providedRoleActivity.addSubactivity(providedOperationActivity);
			}
		}
	}

	private static Activity tryToGetExistingSignatureActivity(
			Activity providedRoleActivity, Signature signature) {
		for (Activity subactivity : providedRoleActivity.getSubactivities()) {
			if ((subactivity.getElementType()==ActivityElementType.PROVIDEDOPERATION)&&(subactivity.getElement()==signature)) {
				return subactivity;
			}
		}
		return null;
	}


}
