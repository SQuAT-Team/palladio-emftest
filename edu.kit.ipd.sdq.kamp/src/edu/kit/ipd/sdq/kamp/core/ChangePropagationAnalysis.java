package edu.kit.ipd.sdq.kamp.core;

import java.util.ArrayList;
import java.util.List;

import de.uka.ipd.sdq.componentInternalDependencies.RoleToRoleDependency;
import de.uka.ipd.sdq.pcm.core.composition.AssemblyConnector;
import de.uka.ipd.sdq.pcm.core.composition.AssemblyContext;
import de.uka.ipd.sdq.pcm.core.composition.Connector;
import de.uka.ipd.sdq.pcm.repository.DataType;
import de.uka.ipd.sdq.pcm.repository.OperationInterface;
import de.uka.ipd.sdq.pcm.repository.ProvidedRole;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import de.uka.ipd.sdq.pcm.repository.RequiredRole;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ChangePropagationDueToDataDependencies;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ChangePropagationDueToInterfaceDependencies;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.IntercomponentPropagation;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.IntracomponentPropagation;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyAssemblyConnector;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyComponent;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyInterface;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyProvidedRole;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyRequiredRole;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.modificationmarksFactory;

/**
 * The change propagation analysis 
 * 1. determines a seed population of affected components (resp. provided roles)
 * 2. calculates in iterations:
 *    a) inter-component propagation
 *    b) intra-component propagation
 * 3. generates internal modification marks for affected elements
 * 
 * - elements which were already part of a seed population are not further investigated
 * 
 * 
 * @author stammel
 *
 */
public class ChangePropagationAnalysis {

	private List<RepositoryComponent> visitedComponents;
	private List<ProvidedRole> visitedProvidedRoles;
	private List<RequiredRole> visitedRequiredRoles;
	private List<AssemblyConnector> visitedAssemblyConnectors;

	//	private List<RepositoryComponent> acceptedPropagationComponents;
	//	private List<RepositoryComponent> excludedPropagationComponents;
	//	private List<ProvidedRole> acceptedProvidedRoles;
	//	private List<ProvidedRole> excludedProvidedRoles;

	public ChangePropagationAnalysis() {
		this.visitedComponents = new ArrayList<RepositoryComponent>();
		this.visitedProvidedRoles = new ArrayList<ProvidedRole>();
		this.visitedRequiredRoles = new ArrayList<RequiredRole>();
		this.visitedAssemblyConnectors = new ArrayList<AssemblyConnector>();
	}
	
	public void runChangePropagationAnalysis(ArchitectureVersion version) {
		
		// I. datatype -> interface 
		calculateAndMarkDataTypeToInterfacePropagation(version);

		// II. interface -> roles
		calculateAndMarkInterfaceToRolePropagation(version);

		List<RequiredRole> markedRequiredRoles = ArchitectureModelLookup.lookUpMarkedRequiredRoles(version);
		List<ProvidedRole> markedProvidedRoles = ArchitectureModelLookup.lookUpMarkedProvidedRoles(version);
		this.visitedProvidedRoles = markedProvidedRoles;

		// first iteration of intra component propagation 
		//List<RequiredRole> seedRequiredRoles = new ArrayList<RequiredRole>();
		// erste iteration von required roles -> provided roles
		calculateIntraComponentPropagation(markedRequiredRoles, version);
		this.visitedRequiredRoles.addAll(markedRequiredRoles);
		
		List<ProvidedRole> providedRolesToBeAnalysedInNextIteration = ArchitectureModelLookup.lookUpMarkedProvidedRoles(version);
		List<RequiredRole> requiredRolesToBeAnalysedInNextIteration = new ArrayList<RequiredRole>();
		do {
			// schritt: provided roles -> assembly connector
			requiredRolesToBeAnalysedInNextIteration = calculateInterComponentPropagation(providedRolesToBeAnalysedInNextIteration, version);		
			// schritt: assemblierungs connector -> required role -> 
			providedRolesToBeAnalysedInNextIteration = calculateIntraComponentPropagation(requiredRolesToBeAnalysedInNextIteration, version);
		} while (!providedRolesToBeAnalysedInNextIteration.isEmpty());
		
	}

	private void calculateAndMarkDataTypeToInterfacePropagation(ArchitectureVersion version) {
		
		// TODO: datatype modifications in repository-model not considered, only modificationmarks!

		List<DataType> initialMarkedDatatypes = new ArrayList<DataType>();
		initialMarkedDatatypes.addAll(ArchitectureModelLookup.lookUpMarkedDatatypes(version));

		List<OperationInterface> initialMarkedInterfaces = new ArrayList<OperationInterface>();
		initialMarkedInterfaces.addAll(ArchitectureModelLookup.lookUpMarkedInterfaces(version));
		
		for (DataType datatype : initialMarkedDatatypes) {
			List<OperationInterface> operationInterfaces = ArchitectureModelLookup.lookUpInterfacesWithParameterOfType(version, datatype);
			
			List<OperationInterface> interfacesToBeMarked = new ArrayList<OperationInterface>();
			
			for (OperationInterface operationInterface : operationInterfaces) {
				if (!initialMarkedInterfaces.contains(operationInterface)) {
					interfacesToBeMarked.add(operationInterface);
				}
			}

			if (!interfacesToBeMarked.isEmpty()) {
				ChangePropagationDueToDataDependencies cp = modificationmarksFactory.eINSTANCE.createChangePropagationDueToDataDependencies();
				cp.setToolderived(true);
				version.getModificationMarkRepository().getChangePropagationSteps().add(cp);

				for (OperationInterface operationInterface : interfacesToBeMarked) {
					ModifyInterface modifyInterface = modificationmarksFactory.eINSTANCE.createModifyInterface();
					modifyInterface.setToolderived(true);
					modifyInterface.setOperationInterface(operationInterface);
					cp.getInterfaceModifications().add(modifyInterface);
				}
			}
		}
	}
	
	private void calculateAndMarkInterfaceToRolePropagation(
			ArchitectureVersion version) {
		
		// TODO: interface modifications in repository-model not considered, only modificationsmarks!
		
		List<OperationInterface> initialMarkedInterfaces = new ArrayList<OperationInterface>();
		initialMarkedInterfaces.addAll(ArchitectureModelLookup.lookUpMarkedInterfaces(version));

		List<ProvidedRole> initialMarkedProvidedRoles = new ArrayList<ProvidedRole>();
		initialMarkedProvidedRoles.addAll(ArchitectureModelLookup.lookUpMarkedProvidedRoles(version));

		List<RequiredRole> initialMarkedRequiredRoles = new ArrayList<RequiredRole>();
		initialMarkedRequiredRoles.addAll(ArchitectureModelLookup.lookUpMarkedRequiredRoles(version));

		for (OperationInterface operationInterface : initialMarkedInterfaces) {
			// determine provided roles to be marked
			List<ProvidedRole> providedRolesToBeMarked = determineProvidedRolesToBeMarked(
					version, initialMarkedProvidedRoles, operationInterface);
			// determine required roles to be marked
			List<RequiredRole> requiredRolesToBeMarked = determineRequiredRoleToBeMarked(
					version, initialMarkedRequiredRoles, operationInterface);
			
			markRoles(version, providedRolesToBeMarked, requiredRolesToBeMarked);
		}
	}

	private List<ProvidedRole> determineProvidedRolesToBeMarked(
			ArchitectureVersion version,
			List<ProvidedRole> initialMarkedProvidedRoles,
			OperationInterface operationInterface) {
		List<ProvidedRole> providedRolesWithInterface = ArchitectureModelLookup.lookUpProvidedRolesWithInterface(version, operationInterface);
		List<ProvidedRole> providedRolesToBeMarked = new ArrayList<ProvidedRole>();
		for (ProvidedRole providedRole : providedRolesWithInterface) {
			if (!initialMarkedProvidedRoles.contains(providedRole)) {
				providedRolesToBeMarked.add(providedRole);
			}
		}
		return providedRolesToBeMarked;
	}

	private List<RequiredRole> determineRequiredRoleToBeMarked(
			ArchitectureVersion version,
			List<RequiredRole> initialMarkedRequiredRoles,
			OperationInterface operationInterface) {
		List<RequiredRole> requiredRolesWithInterface = ArchitectureModelLookup.lookUpRequiredRolesWithInterface(version, operationInterface);
		List<RequiredRole> requiredRolesToBeMarked = new ArrayList<RequiredRole>();
		for (RequiredRole requiredRole : requiredRolesWithInterface) {
			if (!initialMarkedRequiredRoles.contains(requiredRole)) {
				requiredRolesToBeMarked.add(requiredRole);
			}
		}
		return requiredRolesToBeMarked;
	}
	
	private void markRoles(ArchitectureVersion version, List<ProvidedRole> providedRolesToBeMarked,
			List<RequiredRole> requiredRolesToBeMarked) {
		if (!providedRolesToBeMarked.isEmpty()||!requiredRolesToBeMarked.isEmpty()) {
			ChangePropagationDueToInterfaceDependencies cp = modificationmarksFactory.eINSTANCE.createChangePropagationDueToInterfaceDependencies();
			cp.setToolderived(true);
			version.getModificationMarkRepository().getChangePropagationSteps().add(cp);
			
			for (ProvidedRole providedRole : providedRolesToBeMarked) {
				ModifyProvidedRole modifyProvidedRole = modificationmarksFactory.eINSTANCE.createModifyProvidedRole();
				modifyProvidedRole.setToolderived(true);
				modifyProvidedRole.setProvidedrole(providedRole);

				if (providedRole.getProvidingEntity_ProvidedRole() instanceof RepositoryComponent) {
					ModifyComponent modifyComponent = modificationmarksFactory.eINSTANCE.createModifyComponent();
					modifyComponent.setToolderived(true);
					modifyComponent.setComponent((RepositoryComponent)providedRole.getProvidingEntity_ProvidedRole());
					cp.getComponentModifications().add(modifyComponent);
					modifyComponent.getProvidedroleModifications().add(modifyProvidedRole);
				}
			}

			for (RequiredRole requiredRole : requiredRolesToBeMarked) {
				ModifyRequiredRole modifyRequiredRole = modificationmarksFactory.eINSTANCE.createModifyRequiredRole();
				modifyRequiredRole.setToolderived(true);
				modifyRequiredRole.setRequiredrole(requiredRole);

				if (requiredRole.getRequiringEntity_RequiredRole() instanceof RepositoryComponent) {
					ModifyComponent modifyComponent = modificationmarksFactory.eINSTANCE.createModifyComponent();
					modifyComponent.setToolderived(true);
					modifyComponent.setComponent((RepositoryComponent)requiredRole.getRequiringEntity_RequiredRole());
					cp.getComponentModifications().add(modifyComponent);
					modifyComponent.getRequiredroleModifications().add(modifyRequiredRole);
				}
			}
		}
	}

	/**
	 * affected required roles + component internal dependencies => affected provided roles
	 */
	public List<ProvidedRole> calculateIntraComponentPropagation(List<RequiredRole> requiredRoles, ArchitectureVersion version) {
		
		List<ProvidedRole> providedRolesToBeAnalysedInNextIteration = new ArrayList<ProvidedRole>();

		List<ProvidedRole> markedProvidedRoles = ArchitectureModelLookup.lookUpMarkedProvidedRoles(version);
		
		if (version.getComponentInternalDependencyRepository() != null) {
			for (RequiredRole requiredRole : requiredRoles) {
				if (!visitedRequiredRoles.contains(requiredRole)) {
					IntracomponentPropagation intracomponentPropagation = modificationmarksFactory.eINSTANCE.createIntracomponentPropagation();
					intracomponentPropagation.setToolderived(true);
					version.getModificationMarkRepository().getChangePropagationSteps().add(intracomponentPropagation);
	
					ModifyComponent modifyComponent = modificationmarksFactory.eINSTANCE.createModifyComponent();
					modifyComponent.setToolderived(true);
					if (requiredRole.getRequiringEntity_RequiredRole() instanceof RepositoryComponent)
						modifyComponent.setComponent((RepositoryComponent)requiredRole.getRequiringEntity_RequiredRole());
					intracomponentPropagation.getComponentModifications().add(modifyComponent);
					
					ModifyRequiredRole modifyRequiredRole = modificationmarksFactory.eINSTANCE.createModifyRequiredRole();
					modifyRequiredRole.setToolderived(true);
					modifyRequiredRole.setRequiredrole(requiredRole);
					modifyComponent.getRequiredroleModifications().add(modifyRequiredRole);
					
						for (RoleToRoleDependency dependency : version.getComponentInternalDependencyRepository().getDependencies()) {
							if (dependency.getRequiredRole()==requiredRole) {
								ProvidedRole providedRole = dependency.getProvidedRole();
								
								ModifyProvidedRole modifyProvidedRole = modificationmarksFactory.eINSTANCE.createModifyProvidedRole();
								modifyProvidedRole.setToolderived(true);
								modifyProvidedRole.setProvidedrole(providedRole);
								
								modifyComponent.getProvidedroleModifications().add(modifyProvidedRole);
								if (!markedProvidedRoles.contains(providedRole)) {
									providedRolesToBeAnalysedInNextIteration.add(providedRole);
								}
							}
						}
					}
					visitedRequiredRoles.add(requiredRole);
				}
			}
		
		return providedRolesToBeAnalysedInNextIteration;
	}

	/**
	 *  affected provided roles (of component A) + connectors => affected required roles of dependent components (B, C, ...)
	 * @param newProvidedRolesMarked 
	 */
	public List<RequiredRole> calculateInterComponentPropagation(List<ProvidedRole> newProvidedRolesMarked, ArchitectureVersion version) {

		List<RequiredRole> requiredRolesToBeAnalysedInNextIteration = new ArrayList<RequiredRole>();
		
		List<RequiredRole> markedRequiredRoles = ArchitectureModelLookup.lookUpMarkedRequiredRoles(version);

		for (ProvidedRole providedRole : newProvidedRolesMarked) {
			List<AssemblyConnector> assemblyConnectors = lookUpAssemblyConnectorsAttachedToProvidedRole(version, providedRole);

			for (AssemblyConnector assemblyConnector : assemblyConnectors) {
				if (!this.visitedAssemblyConnectors.contains(assemblyConnector)) {

					IntercomponentPropagation intercomponentPropagation = modificationmarksFactory.eINSTANCE.createIntercomponentPropagation();
					intercomponentPropagation.setToolderived(true);
					
					ModifyAssemblyConnector modifyAssemblyConnector = modificationmarksFactory.eINSTANCE.createModifyAssemblyConnector();
					modifyAssemblyConnector.setAssemblyconnector(assemblyConnector);
					modifyAssemblyConnector.setToolderived(true);
					
					intercomponentPropagation.getAssemblyConnectorModifications().add(modifyAssemblyConnector);
					version.getModificationMarkRepository().getChangePropagationSteps().add(intercomponentPropagation);
					
					this.visitedAssemblyConnectors.add(assemblyConnector);
				}
			}
			
			// required roles nur zurückgeben, wenn noch nicht markiert
			List<RequiredRole> requiredRoles = lookUpRequiredRolesForAssemblyConnectors(assemblyConnectors);
			for (RequiredRole requireRole : requiredRoles) {
				if (!markedRequiredRoles.contains(requireRole)) {
					requiredRolesToBeAnalysedInNextIteration.add(requireRole);
				}
			}
		}
		
		return requiredRolesToBeAnalysedInNextIteration;
	}

	private static List<AssemblyConnector> lookUpAssemblyConnectorsAttachedToProvidedRole(
			ArchitectureVersion version, ProvidedRole providedRole) {

		List<AssemblyConnector> assemblyconnectors = new ArrayList<AssemblyConnector>();

		RepositoryComponent component = (RepositoryComponent)providedRole.getProvidingEntity_ProvidedRole();

		List<AssemblyContext> assemblycontexts = ArchitectureModelLookup.lookUpAssemblyContextsForRepositoryComponent(version, component);

		for (AssemblyContext assemblycontext : assemblycontexts) {
			for (Connector connector : assemblycontext.getParentStructure__AssemblyContext().getConnectors__ComposedStructure()) {
				if (connector instanceof AssemblyConnector) {
					if (((AssemblyConnector)connector).getProvidedRole_AssemblyConnector()==providedRole) 
						assemblyconnectors.add((AssemblyConnector) connector);
				}
			}
		}
	 
		return assemblyconnectors;
	}

	private static List<RequiredRole> lookUpRequiredRolesForAssemblyConnectors(
			List<AssemblyConnector> assemblyconnectors) {

		List<RequiredRole> results = new ArrayList<RequiredRole>();

		for (AssemblyConnector assemblyconnector : assemblyconnectors) {
			results.add(assemblyconnector.getRequiredRole_AssemblyConnector());
		}
	 
		return results;
	}

}
