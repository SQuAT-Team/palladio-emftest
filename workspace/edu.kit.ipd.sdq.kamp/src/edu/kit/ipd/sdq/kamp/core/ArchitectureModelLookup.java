package edu.kit.ipd.sdq.kamp.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;

import de.uka.ipd.sdq.componentInternalDependencies.RoleToRoleDependency;
import de.uka.ipd.sdq.pcm.core.composition.AssemblyConnector;
import de.uka.ipd.sdq.pcm.core.composition.AssemblyContext;
import de.uka.ipd.sdq.pcm.core.composition.ComposedStructure;
import de.uka.ipd.sdq.pcm.core.composition.Connector;
import de.uka.ipd.sdq.pcm.core.entity.NamedElement;
import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.CollectionDataType;
import de.uka.ipd.sdq.pcm.repository.CompositeComponent;
import de.uka.ipd.sdq.pcm.repository.CompositeDataType;
import de.uka.ipd.sdq.pcm.repository.DataType;
import de.uka.ipd.sdq.pcm.repository.Interface;
import de.uka.ipd.sdq.pcm.repository.OperationInterface;
import de.uka.ipd.sdq.pcm.repository.OperationProvidedRole;
import de.uka.ipd.sdq.pcm.repository.OperationRequiredRole;
import de.uka.ipd.sdq.pcm.repository.OperationSignature;
import de.uka.ipd.sdq.pcm.repository.Parameter;
import de.uka.ipd.sdq.pcm.repository.ProvidedRole;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import de.uka.ipd.sdq.pcm.repository.RequiredRole;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyAssemblyConnector;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyComponent;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyDatatype;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyInterface;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyProvidedRole;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.ModifyRequiredRole;
import edu.kit.ipd.sdq.kamp.model.modificationmarks.util.modificationmarksSwitch;

public class ArchitectureModelLookup {
	public static List<AssemblyContext> lookUpAssemblyContextsForRepositoryComponent(ArchitectureVersion version, RepositoryComponent component) {
		List<AssemblyContext> assemblyContexts = new ArrayList<AssemblyContext>();
		
		for (AssemblyContext assemblyContext : version.getSystem().getAssemblyContexts__ComposedStructure()) {
			if (assemblyContext.getEncapsulatedComponent__AssemblyContext()==component)
				assemblyContexts.add(assemblyContext);
		}

		assemblyContexts.addAll(lookUpAssemblyContextsInCompositeComponents(version, component));

		return assemblyContexts;
	}

	public static List<CompositeComponent> lookUpCompositeComponents(ArchitectureVersion version) {
		List<CompositeComponent> compositeComponents = new ArrayList<CompositeComponent>();
		
		for (RepositoryComponent component : version.getRepository().getComponents__Repository()) {
			if (component instanceof CompositeComponent) {
				compositeComponents.add((CompositeComponent) component);
			}
		}
		
		return compositeComponents;
	}
	
	public static List<AssemblyConnector> lookUpAssemblyConnectors(ArchitectureVersion version) {
		List<AssemblyConnector> assemblyConnectors = new ArrayList<AssemblyConnector>();
		
		assemblyConnectors.addAll(lookUpAssemblyConnectorsInComposedStructures(version.getSystem()));

		List<CompositeComponent> compositeComponents = lookUpCompositeComponents(version);
		for (CompositeComponent compositeComponent : compositeComponents) {
			assemblyConnectors.addAll(lookUpAssemblyConnectorsInComposedStructures(compositeComponent));
		}

		return assemblyConnectors;
	}

	public static List<AssemblyConnector> lookUpAssemblyConnectorsInComposedStructures(
			ComposedStructure composedStructure) {
		List<AssemblyConnector> assemblyConnectors = new ArrayList<AssemblyConnector>();
		for (Connector connector : composedStructure.getConnectors__ComposedStructure()) {
			if (connector instanceof AssemblyConnector) {
				assemblyConnectors.add((AssemblyConnector)connector);
			}
		}
		return assemblyConnectors;
	}

	public static List<AssemblyConnector> lookUpAssemblyConnectorsBetweenAssemblyContexts(AssemblyContext providingAssemblyContext, AssemblyContext requiringAssemblyContext) {
		List<AssemblyConnector> assemblyConnectors = new ArrayList<AssemblyConnector>();
		
		for (Connector connector : providingAssemblyContext.getParentStructure__AssemblyContext().getConnectors__ComposedStructure()) {
			if (connector instanceof AssemblyConnector) {
				AssemblyConnector assemblyConnector = (AssemblyConnector) connector;
				
				if (assemblyConnector.getProvidingAssemblyContext_AssemblyConnector()==providingAssemblyContext && 
						assemblyConnector.getRequiringAssemblyContext_AssemblyConnector()==requiringAssemblyContext) {
					
					assemblyConnectors.add(assemblyConnector);
				}
				
			}
		}

		return assemblyConnectors;
	}

	public static List<AssemblyConnector> lookUpAssemblyConnectorsOnProvidingAssemblyContext(AssemblyContext providingAssemblyContext) {
		List<AssemblyConnector> assemblyConnectors = new ArrayList<AssemblyConnector>();
		List<Connector> connectors = providingAssemblyContext.getParentStructure__AssemblyContext().getConnectors__ComposedStructure();
		for (Connector connector : connectors) {
			if (connector instanceof AssemblyConnector) {
				if (((AssemblyConnector)connector).getProvidingAssemblyContext_AssemblyConnector()==providingAssemblyContext) {
					assemblyConnectors.add((AssemblyConnector)connector);
				}
			}
		}
		return assemblyConnectors;
	}

	public static List<AssemblyConnector> lookUpAssemblyConnectorsOnRequiringAssemblyContext(AssemblyContext requiringassemblyContext) {
		List<AssemblyConnector> assemblyConnectors = new ArrayList<AssemblyConnector>();
		List<Connector> connectors = requiringassemblyContext.getParentStructure__AssemblyContext().getConnectors__ComposedStructure();
		for (Connector connector : connectors) {
			if (connector instanceof AssemblyConnector) {
				if (((AssemblyConnector)connector).getRequiringAssemblyContext_AssemblyConnector()==requiringassemblyContext) {
					assemblyConnectors.add((AssemblyConnector)connector);
				}
			}
		}
		return assemblyConnectors;
	}

	public static List<AssemblyConnector> selectAssemblyConnectorsOnProvidedRole(List<AssemblyConnector> assemblyConnectors, ProvidedRole providedRole) {
		List<AssemblyConnector> assemblyConnectorsSelected = new ArrayList<AssemblyConnector>();
		for (AssemblyConnector connector : assemblyConnectors) {
			if (connector.getProvidedRole_AssemblyConnector()==providedRole) {
				assemblyConnectorsSelected.add(connector);
			}
		}
		return assemblyConnectorsSelected;
	}
	
	private static List<AssemblyContext> lookUpAssemblyContextsInCompositeComponents(
			ArchitectureVersion version, RepositoryComponent component) {
		List<AssemblyContext> assemblyContexts = new ArrayList<AssemblyContext>();
		
		List<CompositeComponent> compositeComponents = lookUpCompositeComponents(version);
		
		for (CompositeComponent compositeComponent : compositeComponents) {
			for (AssemblyContext assemblyContext : compositeComponent.getAssemblyContexts__ComposedStructure()) {
				if (assemblyContext.getEncapsulatedComponent__AssemblyContext()==component)
					assemblyContexts.add(assemblyContext);
			}
		}

		return assemblyContexts; 
	}

	public static List<ProvidingRequiredRolePair> lookUpMatchingRolePairs(
			RepositoryComponent providingComponent,
			RepositoryComponent requiringComponent) {
		List<ProvidingRequiredRolePair> pairs = new ArrayList<ProvidingRequiredRolePair>();
		
		for (ProvidedRole providedRole : providingComponent.getProvidedRoles_InterfaceProvidingEntity()) {
			for (RequiredRole requiredRole : requiringComponent.getRequiredRoles_InterfaceRequiringEntity()) {
				if (providedRole instanceof OperationProvidedRole && requiredRole instanceof OperationRequiredRole)
					if (((OperationProvidedRole)providedRole).getProvidedInterface__OperationProvidedRole()==((OperationRequiredRole)requiredRole).getRequiredInterface__OperationRequiredRole()) {
						pairs.add(new ProvidingRequiredRolePair(providedRole, requiredRole));
					}
			}
		}
		
		return pairs;
	}

	public static RepositoryComponent lookUpComponentByName(
			ArchitectureVersion version, String name) {
		
		for (RepositoryComponent repcomponent : version.getRepository().getComponents__Repository()) {
			if (repcomponent.getEntityName().equals(name)) {
				return repcomponent;
			}
		}
		
		return null;
	}

	public static Interface lookUpInterfaceByName(
			ArchitectureVersion version, String name) {
		
		for (Interface interf : version.getRepository().getInterfaces__Repository()) {
			if (interf.getEntityName().equals(name)) {
				return interf;
			}
		}
		
		return null;
	}


	public static ModifyProvidedRole lookUpInternalModificationMarkForProvidedRole(
			ArchitectureVersion version, ProvidedRole providedRole) {

		modificationmarksSwitch<ModifyProvidedRole> modifyProvidedRoleSwitch = new modificationmarksSwitch<ModifyProvidedRole>() {
			public ModifyProvidedRole caseModifyProvidedRole(ModifyProvidedRole object) {
				return object;
			}
		};
		
		TreeIterator<EObject> itr =  version.getModificationMarkRepository().eAllContents();
		while (itr.hasNext()) {
			EObject object = itr.next();
			ModifyProvidedRole modifyProvidedRole = modifyProvidedRoleSwitch.doSwitch(object);
			if (modifyProvidedRole != null)
			if (modifyProvidedRole.getProvidedrole()==providedRole) {
				return modifyProvidedRole;
			}
		}
		
		return null;
	}

	public static ModifyAssemblyConnector lookUpModificationMarkForAssemblyConnector(
			ArchitectureVersion version, AssemblyConnector assemblyConnector) {

		modificationmarksSwitch<ModifyAssemblyConnector> modifyAssemblyConnectorSwitch = new modificationmarksSwitch<ModifyAssemblyConnector>() {
			public ModifyAssemblyConnector caseModifyAssemblyConnector(ModifyAssemblyConnector object) {
				return object;
			}
		};
		
		TreeIterator<EObject> itr =  version.getModificationMarkRepository().eAllContents();
		while (itr.hasNext()) {
			EObject object = itr.next();
			ModifyAssemblyConnector modifyAssemblyConnector = modifyAssemblyConnectorSwitch.doSwitch(object);
			if (modifyAssemblyConnector!=null)
			if (modifyAssemblyConnector.getAssemblyconnector()==assemblyConnector) {
				return modifyAssemblyConnector;
			}
		}
		
		return null;
	}

	public static List<AssemblyConnector> lookUpMarkedAssemblyConnectors(
			ArchitectureVersion version) {

		List<AssemblyConnector> results = new ArrayList<AssemblyConnector>();

		modificationmarksSwitch<ModifyAssemblyConnector> modifyAssemblyConnectorSwitch = new modificationmarksSwitch<ModifyAssemblyConnector>() {
			public ModifyAssemblyConnector caseModifyAssemblyConnector(ModifyAssemblyConnector object) {
				return object;
			}
		};
		
		TreeIterator<EObject> itr =  version.getModificationMarkRepository().eAllContents();
		while (itr.hasNext()) {
			EObject object = itr.next();
			ModifyAssemblyConnector modifyAssemblyConnector = modifyAssemblyConnectorSwitch.doSwitch(object);
			if (modifyAssemblyConnector!=null)
				results.add(modifyAssemblyConnector.getAssemblyconnector());
		}

		return results;
	}

	public static List<ProvidedRole> lookUpMarkedProvidedRoles(
			ArchitectureVersion version) {
		
		List<ProvidedRole> results = new ArrayList<ProvidedRole>();
		
		modificationmarksSwitch<ModifyProvidedRole> modifyProvidedRoleSwitch = new modificationmarksSwitch<ModifyProvidedRole>() {
			public ModifyProvidedRole caseModifyProvidedRole(ModifyProvidedRole object) {
				return object;
			}
		};
		
		TreeIterator<EObject> itr =  version.getModificationMarkRepository().eAllContents();
		while (itr.hasNext()) {
			EObject object = itr.next();
			ModifyProvidedRole modifyProvidedRole = modifyProvidedRoleSwitch.doSwitch(object);
			if (modifyProvidedRole!=null)
				results.add(modifyProvidedRole.getProvidedrole());
		}
		
		return results;
	}
	
	public static List<RequiredRole> lookUpMarkedRequiredRoles(
			ArchitectureVersion version) {
		
		List<RequiredRole> results = new ArrayList<RequiredRole>();
		
		modificationmarksSwitch<ModifyRequiredRole> modifyRequiredRoleSwitch = new modificationmarksSwitch<ModifyRequiredRole>() {
			public ModifyRequiredRole caseModifyRequiredRole(ModifyRequiredRole object) {
				return object;
			}
		};
		
		TreeIterator<EObject> itr =  version.getModificationMarkRepository().eAllContents();
		while (itr.hasNext()) {
			EObject object = itr.next();
			ModifyRequiredRole modifyRequiredRole = modifyRequiredRoleSwitch.doSwitch(object);
			if (modifyRequiredRole!=null)
				results.add(modifyRequiredRole.getRequiredrole());
		}
		
		return results;
	}
	
	public static List<DataType> lookUpMarkedDatatypes(
			ArchitectureVersion version) {
		
		List<DataType> results = new ArrayList<DataType>();
		
		modificationmarksSwitch<ModifyDatatype> modifyDatatypeSwitch = new modificationmarksSwitch<ModifyDatatype>() {
			public ModifyDatatype caseModifyDatatype(ModifyDatatype object) {
				return object;
			}
		};
		
		TreeIterator<EObject> itr =  version.getModificationMarkRepository().eAllContents();
		while (itr.hasNext()) {
			EObject object = itr.next();
			ModifyDatatype modifyDatatype = modifyDatatypeSwitch.doSwitch(object);
			if (modifyDatatype!=null)
				results.add(modifyDatatype.getDatatype());
		}
		
		return results;
	}

	public static List<OperationInterface> lookUpMarkedInterfaces(
			ArchitectureVersion version) {
		
		List<OperationInterface> results = new ArrayList<OperationInterface>();
		
		modificationmarksSwitch<ModifyInterface> modifyInterfaceSwitch = new modificationmarksSwitch<ModifyInterface>() {
			public ModifyInterface caseModifyInterface(ModifyInterface object) {
				return object;
			}
		};
		
		TreeIterator<EObject> itr =  version.getModificationMarkRepository().eAllContents();
		while (itr.hasNext()) {
			EObject object = itr.next();
			ModifyInterface modifyInterface = modifyInterfaceSwitch.doSwitch(object);
			if (modifyInterface!=null)
				results.add(modifyInterface.getOperationInterface());
		}
		
		return results;
	}

	public static List<RepositoryComponent> lookUpMarkedComponents(
			ArchitectureVersion version) {
		
		List<RepositoryComponent> results = new ArrayList<RepositoryComponent>();
		
		modificationmarksSwitch<ModifyComponent> modifyProvidedRoleSwitch = new modificationmarksSwitch<ModifyComponent>() {
			public ModifyComponent caseModifyComponent(ModifyComponent object) {
				return object;
			}
		};
		
		TreeIterator<EObject> itr =  version.getModificationMarkRepository().eAllContents();
		while (itr.hasNext()) {
			EObject object = itr.next();
			ModifyComponent modifyComponent = modifyProvidedRoleSwitch.doSwitch(object);
			if (modifyComponent!=null)
				results.add(modifyComponent.getComponent());
		}
		
		return results;
	}
	
	public static List<ModifyComponent> lookUpModificationMarksForComponent(
			ArchitectureVersion version, RepositoryComponent component) {
		
		List<ModifyComponent> results = new ArrayList<ModifyComponent>();
		
		modificationmarksSwitch<ModifyComponent> modifyComponentSwitch = new modificationmarksSwitch<ModifyComponent>() {
			public ModifyComponent caseModifyComponent(ModifyComponent object) {
				return object;
			}
		};
		
		TreeIterator<EObject> itr =  version.getModificationMarkRepository().eAllContents();
		while (itr.hasNext()) {
			EObject object = itr.next();
			ModifyComponent modifyComponent = modifyComponentSwitch.doSwitch(object);
			if (modifyComponent!=null)
				if (modifyComponent.getComponent()==component) {
				results.add(modifyComponent);
				
				}
		}
		
		return results;
	}

	public static List<ModifyComponent> lookUpAllComponentModificationMarks(
			ArchitectureVersion version) {
		
		List<ModifyComponent> results = new ArrayList<ModifyComponent>();
		
		modificationmarksSwitch<ModifyComponent> modifyComponentSwitch = new modificationmarksSwitch<ModifyComponent>() {
			public ModifyComponent caseModifyComponent(ModifyComponent object) {
				return object;
			}
		};
		
		TreeIterator<EObject> itr =  version.getModificationMarkRepository().eAllContents();
		while (itr.hasNext()) {
			EObject object = itr.next();
			ModifyComponent modifyComponent = modifyComponentSwitch.doSwitch(object);
			if (modifyComponent!=null)
				results.add(modifyComponent);
		}
		
		return results;
	}


	public static List<RoleToRoleDependency> lookUpComponentInternalDependenciesForComponent(
			ArchitectureVersion version, BasicComponent component) {
		
		List<RoleToRoleDependency> roleToRoleDependencies = new ArrayList<RoleToRoleDependency>();
		
		for (RoleToRoleDependency dep : version.getComponentInternalDependencyRepository().getDependencies()) {
			if (dep.getProvidedRole().getProvidingEntity_ProvidedRole()==component) {
				roleToRoleDependencies.add(dep);
			}
		}
		
		return roleToRoleDependencies;
	}

	public static List<ProvidedRole> lookUpDependentProvidedRolesForRequiredRole(
			ArchitectureVersion version,
			RequiredRole requiredRole) {
		
		List<ProvidedRole> providedRoles = new ArrayList<ProvidedRole>();
		
		for (RoleToRoleDependency dep : version.getComponentInternalDependencyRepository().getDependencies()) {
			if (dep.getRequiredRole()==requiredRole) {
				providedRoles.add(dep.getProvidedRole());
			}
		}
		
		return providedRoles;
	}

	public static CollectionDataType lookUpCollectionDatatypeByName(
			ArchitectureVersion version, String name) {
		
		for (DataType datatype : version.getRepository().getDataTypes__Repository()) {
			if (datatype instanceof CollectionDataType)
				if (((CollectionDataType)datatype).getEntityName().equals(name)) {
					return (CollectionDataType)datatype;
				}
		}
		
		return null;
	}

	public static CompositeDataType lookUpCompositeDatatypeByName(
			ArchitectureVersion version, String name) {
		
		for (DataType datatype : version.getRepository().getDataTypes__Repository()) {
			if (datatype instanceof CompositeDataType)
				if (((CompositeDataType)datatype).getEntityName().equals(name)) {
					return (CompositeDataType)datatype;
				}
		}
		
		return null;
	}

	public static DataType lookUpDatatypeByName(ArchitectureVersion version, String dataTypeName) {
		for (DataType datatype : version.getRepository().getDataTypes__Repository()) {
			if (datatype instanceof NamedElement) {
				if (((NamedElement)datatype).getEntityName().equals(dataTypeName)) {
					return datatype;
				}
			}
		}

		return null;
	}

	public static List<OperationInterface> lookUpInterfacesWithParameterOfType(
			ArchitectureVersion version, DataType datatype) {
	
		List<OperationInterface> results = new ArrayList<OperationInterface>();
		
		for (Interface interfac : version.getRepository().getInterfaces__Repository()) {
			collectInterfacesWithDatatype(datatype, results, interfac);
		}
		
		return results;
	}

	private static void collectInterfacesWithDatatype(DataType datatype,
			List<OperationInterface> results, Interface interfac) {
		if (interfac instanceof OperationInterface) {
			OperationInterface operationInterface = (OperationInterface)interfac;
			
			for (OperationSignature signature : operationInterface.getSignatures__OperationInterface()) {
				checkSignatureForDatatype(datatype, results, operationInterface,
						signature);
			}
		
		}
	}

	private static void checkSignatureForDatatype(DataType datatype,
			List<OperationInterface> results,
			OperationInterface operationInterface, OperationSignature signature) {
		for (Parameter parameter : signature.getParameters__OperationSignature()) {
			if (parameter.getDataType__Parameter()==datatype) {
				if (!results.contains(operationInterface))
					results.add(operationInterface);
			}
		}
		
		if (signature.getReturnType__OperationSignature()==datatype) {
			if (!results.contains(operationInterface))
				results.add(operationInterface);
		}
	}

	public static List<ProvidedRole> lookUpProvidedRolesWithInterface(
			ArchitectureVersion version,
			OperationInterface opInterface) {
		
		List<ProvidedRole> results = new ArrayList<ProvidedRole>();
		
		for (RepositoryComponent component : version.getRepository().getComponents__Repository()) {
			for (ProvidedRole providedRole : component.getProvidedRoles_InterfaceProvidingEntity()) {
				if (providedRole instanceof OperationProvidedRole) {
					if (((OperationProvidedRole)providedRole).getProvidedInterface__OperationProvidedRole()==opInterface) {
						if (!results.contains(providedRole))
							results.add(providedRole);
					}
				}
			}
		}
		
		return results;
	}
	
	public static List<RequiredRole> lookUpRequiredRolesWithInterface(
			ArchitectureVersion version,
			OperationInterface opInterface) {
		
		List<RequiredRole> results = new ArrayList<RequiredRole>();
		
		for (RepositoryComponent component : version.getRepository().getComponents__Repository()) {
			for (RequiredRole requiredRole : component.getRequiredRoles_InterfaceRequiringEntity()) {
				if (requiredRole instanceof OperationRequiredRole) {
					if (((OperationRequiredRole)requiredRole).getRequiredInterface__OperationRequiredRole()==opInterface) {
						if (!results.contains(requiredRole))
							results.add(requiredRole);
					}
				}
			}
		}
		
		return results;
	}

}
