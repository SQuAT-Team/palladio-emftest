package edu.kit.ipd.sdq.kamp.core;

import java.util.List;

import org.eclipse.emf.ecore.util.EcoreUtil;

import de.uka.ipd.sdq.componentInternalDependencies.ComponentInternalDependenciesFactory;
import de.uka.ipd.sdq.componentInternalDependencies.ComponentInternalDependencyRepository;
import de.uka.ipd.sdq.componentInternalDependencies.RoleToRoleDependency;
import de.uka.ipd.sdq.pcm.core.composition.AssemblyConnector;
import de.uka.ipd.sdq.pcm.core.composition.AssemblyContext;
import de.uka.ipd.sdq.pcm.core.composition.CompositionFactory;
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
import de.uka.ipd.sdq.pcm.repository.Repository;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import de.uka.ipd.sdq.pcm.repository.RepositoryFactory;
import de.uka.ipd.sdq.pcm.repository.RequiredRole;
import de.uka.ipd.sdq.pcm.system.System;
import de.uka.ipd.sdq.pcm.system.SystemFactory;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.BuildSpecification;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.DeploymentSpecification;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.DevelopmentArtefactSpecification;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.FieldOfActivityAnnotationRepository;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.FieldofactivityannotationsFactory;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.FieldofactivityannotationsPackage;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.ReleaseSpecification;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.StaffSpecification;
import edu.kit.ipd.sdq.kamp.model.fieldofactivityannotations.TestSpecification;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.IntercomponentPropagation;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModificationRepository;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyAssemblyConnector;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyComponent;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyDatatype;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyInterface;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyProvidedRole;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyRequiredRole;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.SeedModifications;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.modificationmarksFactory;

public class ArchitectureModelFactoryFacade {

	public static ArchitectureVersion createEmptyModel(String name) {
		Repository repository = ArchitectureModelFactoryFacade.createRepository(name);
		System system = ArchitectureModelFactoryFacade.createSystem();
		FieldOfActivityAnnotationRepository fieldOfActivityRepository = ArchitectureModelFactoryFacade.createFieldOfActivityAnnotationsRepository();
		ModificationRepository internalModificationMarkRepository = ArchitectureModelFactoryFacade.createModificationMarkRepository();
		ComponentInternalDependencyRepository componentInternalDependencyRepository = ArchitectureModelFactoryFacade.createComponentInternalDependencyRepository();
		return new ArchitectureVersion(name, repository, system, fieldOfActivityRepository, internalModificationMarkRepository, componentInternalDependencyRepository);
	}
	
	public static Repository createRepository(String name) {
		Repository repository = RepositoryFactory.eINSTANCE.createRepository();
		repository.setEntityName(name);
		return repository;
	}

	public static BasicComponent createBasicComponent(ArchitectureVersion version,
			String name) {
		BasicComponent component = RepositoryFactory.eINSTANCE.createBasicComponent();
		component.setEntityName(name);
		version.getRepository().getComponents__Repository().add(component);
		return component;
	}

	public static OperationInterface createInterface(ArchitectureVersion version, String name) {
		OperationInterface interf = RepositoryFactory.eINSTANCE.createOperationInterface();
		interf.setEntityName(name);
		version.getRepository().getInterfaces__Repository().add(interf);
		return interf;
	}

	public static OperationProvidedRole createProvidedRole(BasicComponent component,
			OperationInterface interf) {
		OperationProvidedRole providedRole = RepositoryFactory.eINSTANCE.createOperationProvidedRole();
		providedRole.setProvidedInterface__OperationProvidedRole(interf);
		providedRole.setEntityName(interf.getEntityName());
		component.getProvidedRoles_InterfaceProvidingEntity().add(providedRole);
		return providedRole;
	}

	public static OperationRequiredRole createRequiredRole(BasicComponent component,
			OperationInterface interf) {
		OperationRequiredRole requiredRole = RepositoryFactory.eINSTANCE.createOperationRequiredRole();
		requiredRole.setRequiredInterface__OperationRequiredRole(interf);
		requiredRole.setEntityName(interf.getEntityName());
		component.getRequiredRoles_InterfaceRequiringEntity().add(requiredRole);	
		return requiredRole;
	}
	
	public static de.uka.ipd.sdq.pcm.system.System createSystem() {
		return SystemFactory.eINSTANCE.createSystem();
	}

	public static ComponentInternalDependencyRepository createComponentInternalDependencyRepository() {
		return ComponentInternalDependenciesFactory.eINSTANCE.createComponentInternalDependencyRepository();
	}

	public static FieldOfActivityAnnotationRepository createFieldOfActivityAnnotationsRepository() {
		FieldOfActivityAnnotationRepository repository =  FieldofactivityannotationsFactory.eINSTANCE.createFieldOfActivityAnnotationRepository();
		
		DevelopmentArtefactSpecification devSpec = FieldofactivityannotationsFactory.eINSTANCE.createDevelopmentArtefactSpecification();
		repository.setDevelopmentArtefactSpecification(devSpec);
		DeploymentSpecification deploymentSpec = FieldofactivityannotationsFactory.eINSTANCE.createDeploymentSpecification();
		repository.setDeploymentSpecification(deploymentSpec);
		TestSpecification testSpec = FieldofactivityannotationsFactory.eINSTANCE.createTestSpecification();
		repository.setTestSpecification(testSpec);
		ReleaseSpecification releaseSpec = FieldofactivityannotationsFactory.eINSTANCE.createReleaseSpecification();
		repository.setReleaseSpecification(releaseSpec);
		BuildSpecification buildSpec = FieldofactivityannotationsFactory.eINSTANCE.createBuildSpecification();
		repository.setBuildSpecification(buildSpec);
		StaffSpecification staffSpec = FieldofactivityannotationsFactory.eINSTANCE.createStaffSpecification();
		repository.setStaffSpecification(staffSpec);
		staffSpec.setPersonList(FieldofactivityannotationsFactory.eINSTANCE.createPersonList());
		staffSpec.setRoleList(FieldofactivityannotationsFactory.eINSTANCE.createRoleList());
		
		return repository;
	}
	
	public static ModificationRepository createModificationMarkRepository() {
		ModificationRepository repository = modificationmarksFactory.eINSTANCE.createModificationRepository();
		
		SeedModifications seedModifications = modificationmarksFactory.eINSTANCE.createSeedModifications();
		repository.setSeedModifications(seedModifications);
		
		return repository;
	}

	public static void createAssemblyContext(RepositoryComponent component,
			ArchitectureVersion version) {
		AssemblyContext assemblyContext = CompositionFactory.eINSTANCE.createAssemblyContext();
		assemblyContext.setEntityName(component.getEntityName());
		assemblyContext.setEncapsulatedComponent__AssemblyContext(component);
		version.getSystem().getAssemblyContexts__ComposedStructure().add(assemblyContext);
	}
	
	public static AssemblyConnector createAssemblyConnector(RepositoryComponent requiringComponent, RepositoryComponent providingComponent, 
			ArchitectureVersion version) {
		AssemblyConnector assemblyConnector = CompositionFactory.eINSTANCE.createAssemblyConnector();
		assemblyConnector.setEntityName(requiringComponent.getEntityName()+ " -> " + providingComponent.getEntityName());
		
		AssemblyContext providingContext = ArchitectureModelLookup.lookUpAssemblyContextsForRepositoryComponent(version, providingComponent).get(0);
		AssemblyContext requiringContext = ArchitectureModelLookup.lookUpAssemblyContextsForRepositoryComponent(version, requiringComponent).get(0);
		assemblyConnector.setProvidingAssemblyContext_AssemblyConnector(providingContext);
		assemblyConnector.setRequiringAssemblyContext_AssemblyConnector(requiringContext);
		
		List<ProvidingRequiredRolePair> matchingRolePairs = ArchitectureModelLookup.lookUpMatchingRolePairs(providingComponent, requiringComponent);
		if (!matchingRolePairs.isEmpty()) {
			OperationProvidedRole providedRole = (OperationProvidedRole) matchingRolePairs.get(0).getProvidedRole();
			OperationRequiredRole requiredRole = (OperationRequiredRole) matchingRolePairs.get(0).getRequiredRole();
			assemblyConnector.setProvidedRole_AssemblyConnector(providedRole);
			assemblyConnector.setRequiredRole_AssemblyConnector(requiredRole);
		}
		
		providingContext.getParentStructure__AssemblyContext().getConnectors__ComposedStructure().add(assemblyConnector);
		
		return assemblyConnector;
	}

	public static ModifyComponent assignInternalModificationMarkToComponent(
			ArchitectureVersion version, RepositoryComponent component) {
		
		List<ModifyComponent> modificationMarksForComponent = ArchitectureModelLookup.lookUpModificationMarksForComponent(version, component);
		
		if (modificationMarksForComponent.isEmpty()) {
			ModifyComponent internalModificationMark = modificationmarksFactory.eINSTANCE.createModifyComponent();
			internalModificationMark.setComponent(component);
			version.getModificationMarkRepository().getSeedModifications().getComponentModifications().add(internalModificationMark);
			return internalModificationMark;
 		} else {
 			return modificationMarksForComponent.get(0);
 		}
	}

	public static void assignInternalModificationMarkToProvidedRoleOfComponent(
			ArchitectureVersion version, ProvidedRole providedRole) {
		
		if (ArchitectureModelLookup.lookUpInternalModificationMarkForProvidedRole(version, providedRole)==null) {
			ModifyComponent internalModificationMark = modificationmarksFactory.eINSTANCE.createModifyComponent();
			RepositoryComponent component = (RepositoryComponent) providedRole.getProvidingEntity_ProvidedRole();
			internalModificationMark.setComponent(component);
			ModifyProvidedRole modifyProvidedRole = modificationmarksFactory.eINSTANCE.createModifyProvidedRole();
			modifyProvidedRole.setProvidedrole(providedRole);
			internalModificationMark.getProvidedroleModifications().add(modifyProvidedRole);
			version.getModificationMarkRepository().getSeedModifications().getComponentModifications().add(internalModificationMark);
		}
	}

	public static void assignInternalModificationMarkToDataType(
			ArchitectureVersion version, DataType datatype) {
		ModifyDatatype modifyDataType = modificationmarksFactory.eINSTANCE.createModifyDatatype();
		modifyDataType.setDatatype(datatype);
		version.getModificationMarkRepository().getSeedModifications().getDatatypeModifications().add(modifyDataType);
	}

	public static void assignInternalModificationMarkToInterface(
			ArchitectureVersion version, OperationInterface operationInterface) {
		ModifyInterface modifyInterface = modificationmarksFactory.eINSTANCE.createModifyInterface();
		modifyInterface.setOperationInterface(operationInterface);
		version.getModificationMarkRepository().getSeedModifications().getInterfaceModifications().add(modifyInterface);
	}

	public static void createComponentInternalDependency(
			ArchitectureVersion version, ProvidedRole providedRole, RequiredRole requiredRole) {
		
		RoleToRoleDependency dependency = ComponentInternalDependenciesFactory.eINSTANCE.createRoleToRoleDependency();
		
		dependency.setProvidedRole(providedRole);
		dependency.setRequiredRole(requiredRole);		
		
		version.getComponentInternalDependencyRepository().getDependencies().add(dependency);		
	}

	/**
	 * Delegation to Persistency
	 * 
	 * @param baseversion
	 * @param targetfolderpath
	 * @param targetfilename
	 * @return
	 */
	public static ArchitectureVersion createArchitectureVersionClone(
			ArchitectureVersion baseversion, String targetfolderpath, String targetfilename) {
		
		ArchitectureVersion targetversion = ArchitectureVersionPersistency.createArchitectureVersionClone(baseversion, targetfolderpath, targetfilename);
		
		return targetversion;
	}

	public static void deleteAssemblyConnector(
			AssemblyConnector assemblyConnector) {
		EcoreUtil.delete(assemblyConnector);
	}

	public static void deleteComponentConnector(
			BasicComponent basicComponent) {
		EcoreUtil.delete(basicComponent);
	}

	public static void setupComponentInternalDependenciesPessimistic(
			ArchitectureVersion version) {
		
		for (RepositoryComponent component : version.getRepository().getComponents__Repository()) 
			for (ProvidedRole providedRole : component.getProvidedRoles_InterfaceProvidingEntity()) 
				for (RequiredRole requiredRole : component.getRequiredRoles_InterfaceRequiringEntity()) 
					createComponentInternalDependency(version, providedRole, requiredRole);
		
	}

	public static CollectionDataType createCollectionDatatype(
			ArchitectureVersion version, String name, DataType innerType) {
		CollectionDataType collectionDatatype = RepositoryFactory.eINSTANCE.createCollectionDataType();
		collectionDatatype.setEntityName(name);
		collectionDatatype.setInnerType_CollectionDataType(innerType);
		version.getRepository().getDataTypes__Repository().add(collectionDatatype);
		return collectionDatatype;
	}

	public static CompositeDataType createCompositeDatatype(
			ArchitectureVersion version, String name) {
		CompositeDataType compositeDatatype = RepositoryFactory.eINSTANCE.createCompositeDataType();
		compositeDatatype.setEntityName(name);
		version.getRepository().getDataTypes__Repository().add(compositeDatatype);
		return compositeDatatype;
	}
	
	public static void createInnerdeclarationOfCompositeDatatype(CompositeDataType compositeDataType, String nameInnerType, DataType innerType) {
		InnerDeclaration innerDeclaration = RepositoryFactory.eINSTANCE.createInnerDeclaration();
		innerDeclaration.setEntityName(nameInnerType);
		innerDeclaration.setDatatype_InnerDeclaration(innerType);
		compositeDataType.getInnerDeclaration_CompositeDataType().add(innerDeclaration);
	}

	public static CompositeComponent createCompositeComponent(
			ArchitectureVersion version, String name) {
		CompositeComponent component = RepositoryFactory.eINSTANCE.createCompositeComponent();
		component.setEntityName(name);
		version.getRepository().getComponents__Repository().add(component);
		return component;
	}

	public static OperationSignature createSignatureForInterface(
			OperationInterface operationInterface, String operationname) {
		OperationSignature signature =  RepositoryFactory.eINSTANCE.createOperationSignature();
		signature.setEntityName(operationname);
		operationInterface.getSignatures__OperationInterface().add(signature);
		return signature;
	}

	public static void deleteSignatureForInterface(OperationSignature signature) {
		EcoreUtil.delete(signature);
	}

	public static void createParameterForSignature(
			OperationSignature signature, String parametername, DataType datatype) {
		Parameter parameter = RepositoryFactory.eINSTANCE.createParameter();
		parameter.setParameterName(parametername);
		// set datatype
		parameter.setDataType__Parameter(datatype);
        signature.getParameters__OperationSignature().add(parameter);		
	}

	public static void deleteParameterForSignature(
			Parameter parameter) {
		EcoreUtil.delete(parameter);
	}

	public static void assignInternalModificationMarkToRequiredRole(
			ArchitectureVersion version, RequiredRole requiredRole) {

		if (requiredRole.getRequiringEntity_RequiredRole() instanceof RepositoryComponent) {
		
			RepositoryComponent component = (RepositoryComponent) requiredRole.getRequiringEntity_RequiredRole();

			// when already present then retrieve existing one
			ModifyComponent modifyComponent = ArchitectureModelFactoryFacade.assignInternalModificationMarkToComponent(version, component);

			if (modifyComponent != null) {
				modifyComponent.setComponent((RepositoryComponent) requiredRole.getRequiringEntity_RequiredRole());
				ModifyRequiredRole modifyRequiredRole = modificationmarksFactory.eINSTANCE.createModifyRequiredRole();
				modifyRequiredRole.setRequiredrole(requiredRole);
				modifyComponent.getRequiredroleModifications().add(modifyRequiredRole);
			}
		}
	}

	public static void assignInternalModificationMarkToAssemblyConnector(
			ArchitectureVersion version,
			AssemblyConnector assemblyConnector) {
		IntercomponentPropagation changeStep = modificationmarksFactory.eINSTANCE.createIntercomponentPropagation();
		ModifyAssemblyConnector modifyAssemblyConnector = modificationmarksFactory.eINSTANCE.createModifyAssemblyConnector();
		modifyAssemblyConnector.setAssemblyconnector(assemblyConnector);
		changeStep.getAssemblyConnectorModifications().add(modifyAssemblyConnector);
		version.getModificationMarkRepository().getChangePropagationSteps().add(changeStep);
	}

}
