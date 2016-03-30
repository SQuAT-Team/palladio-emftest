package edu.kit.ipd.sdq.kamp.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
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
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.uka.ipd.sdq.pcm.core.composition.AssemblyConnector;
import de.uka.ipd.sdq.pcm.core.composition.AssemblyContext;
import de.uka.ipd.sdq.pcm.repository.BasicComponent;
import de.uka.ipd.sdq.pcm.repository.CollectionDataType;
import de.uka.ipd.sdq.pcm.repository.CompositeComponent;
import de.uka.ipd.sdq.pcm.repository.CompositeDataType;
import de.uka.ipd.sdq.pcm.repository.InnerDeclaration;
import de.uka.ipd.sdq.pcm.repository.Interface;
import de.uka.ipd.sdq.pcm.repository.OperationInterface;
import de.uka.ipd.sdq.pcm.repository.OperationProvidedRole;
import de.uka.ipd.sdq.pcm.repository.OperationRequiredRole;
import de.uka.ipd.sdq.pcm.repository.OperationSignature;
import de.uka.ipd.sdq.pcm.repository.Parameter;
import de.uka.ipd.sdq.pcm.repository.PrimitiveDataType;
import de.uka.ipd.sdq.pcm.repository.ProvidedRole;
import de.uka.ipd.sdq.pcm.repository.RepositoryComponent;
import de.uka.ipd.sdq.pcm.repository.RequiredRole;
import edu.kit.ipd.sdq.kamp.core.Activity;
import edu.kit.ipd.sdq.kamp.core.ActivityElementType;
import edu.kit.ipd.sdq.kamp.core.ArchitectureModelFactoryFacade;
import edu.kit.ipd.sdq.kamp.core.ArchitectureModelLookup;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersion;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersionPersistency;
import edu.kit.ipd.sdq.kamp.core.BasicActivity;
import edu.kit.ipd.sdq.kamp.core.derivation.DifferenceCalculation;

public class DifferenceDetectionTest {

	public static final String TESTNAME = "DifferenceDetectionTest";

	ArchitectureVersion baseArchitectureVersion;
	ArchitectureVersion targetArchitectureVersion;
	
	@Before
	public void setUp() throws Exception {
		
		TestPathProvider.resetTestProject(TESTNAME);
		
		baseArchitectureVersion = setupBasePCMModel("basemodel");
		
		//ArchitectureVersionPersistency.save(TestPathProvider.getTestPath(), baseArchitectureVersion.getName(), baseArchitectureVersion);
		
		targetArchitectureVersion = ArchitectureVersionPersistency.saveAsAndReload(baseArchitectureVersion, TestPathProvider.getTestPath(TESTNAME), "targetmodel");
				
		// Test validiationExceptions
		
		// Test 
	}

	@After
	public void tearDown() {
		baseArchitectureVersion.delete();
		targetArchitectureVersion.delete();
		baseArchitectureVersion = null;
		targetArchitectureVersion = null;
	}

	private static ArchitectureVersion setupBasePCMModel(String name) {
		ArchitectureVersion baseversion = ArchitectureModelFactoryFacade.createEmptyModel(name);

		BasicComponent client = ArchitectureModelFactoryFacade.createBasicComponent(baseversion, "Client");
		BasicComponent server = ArchitectureModelFactoryFacade.createBasicComponent(baseversion, "Server");
		BasicComponent database = ArchitectureModelFactoryFacade.createBasicComponent(baseversion, "Database");
		CompositeComponent compositeComponent = ArchitectureModelFactoryFacade.createCompositeComponent(baseversion, "TestCompositeA");
		
		OperationInterface clientInterface = ArchitectureModelFactoryFacade.createInterface(baseversion, "ClientInterface");
		OperationInterface serverInterface = ArchitectureModelFactoryFacade.createInterface(baseversion, "ServerInterface");
		OperationInterface databaseInterface = ArchitectureModelFactoryFacade.createInterface(baseversion, "DatabaseInterface");

		OperationSignature signature = ArchitectureModelFactoryFacade.createSignatureForInterface(clientInterface, "superService");
		ArchitectureModelFactoryFacade.createParameterForSignature(signature, "parameterForRemoval", null);
		
		ArchitectureModelFactoryFacade.createCollectionDatatype(baseversion, "CollectionDatatypeForRemoval", null);
		CompositeDataType compositeDatatype = ArchitectureModelFactoryFacade.createCompositeDatatype(baseversion, "CompositeDatatypeForRemoval");
		ArchitectureModelFactoryFacade.createInnerdeclarationOfCompositeDatatype(compositeDatatype, "name", null);
		
		ArchitectureModelFactoryFacade.createProvidedRole(client, clientInterface);
		ArchitectureModelFactoryFacade.createRequiredRole(client, serverInterface);

		ArchitectureModelFactoryFacade.createProvidedRole(server, serverInterface);
		ArchitectureModelFactoryFacade.createRequiredRole(server, databaseInterface);

		ArchitectureModelFactoryFacade.createProvidedRole(database, databaseInterface);

		ArchitectureModelFactoryFacade.createAssemblyContext(client, baseversion);
		ArchitectureModelFactoryFacade.createAssemblyContext(server, baseversion);
		ArchitectureModelFactoryFacade.createAssemblyContext(database, baseversion);
		
		ArchitectureModelFactoryFacade.createAssemblyConnector(server, database, baseversion);
		
		return baseversion;
	}
	
	@Test
	public void testDeepClone() {
		assertTrue("TargetRepository is null", targetArchitectureVersion.getRepository() != null);
		System.out.println("baseModelId:  "+baseArchitectureVersion.getRepository().getId());
		System.out.println("targetModelId:"+targetArchitectureVersion.getRepository().getId());
		assertTrue("Repository-ID-Check failed", baseArchitectureVersion.getRepository().getId().equals(targetArchitectureVersion.getRepository().getId()));
		assertTrue("Number of Components different", baseArchitectureVersion.getRepository().getComponents__Repository().size()==targetArchitectureVersion.getRepository().getComponents__Repository().size());

		for (int i=0; i < baseArchitectureVersion.getRepository().getComponents__Repository().size(); i++) {
			RepositoryComponent sourceComponent = baseArchitectureVersion.getRepository().getComponents__Repository().get(i);
			RepositoryComponent targetComponent = targetArchitectureVersion.getRepository().getComponents__Repository().get(i);
			
			assertTrue("Components are identical", sourceComponent!=targetComponent);
			assertTrue("Component-ID-Check failed", sourceComponent.getId().equals(targetComponent.getId()));
			assertTrue("Component: number of provided roles different", sourceComponent.getProvidedRoles_InterfaceProvidingEntity().size()==targetComponent.getProvidedRoles_InterfaceProvidingEntity().size());
			assertTrue("Component: number of required roles different", sourceComponent.getRequiredRoles_InterfaceRequiringEntity().size()==targetComponent.getRequiredRoles_InterfaceRequiringEntity().size());
		}
		
		assertTrue("Interface for Provided Role is null", ((OperationProvidedRole)targetArchitectureVersion.getRepository().getComponents__Repository().get(0).getProvidedRoles_InterfaceProvidingEntity().get(0)).getProvidedInterface__OperationProvidedRole()!=null);
		
		assertTrue("Number of Interfaces in Repository different", baseArchitectureVersion.getRepository().getInterfaces__Repository().size()==targetArchitectureVersion.getRepository().getInterfaces__Repository().size());
		assertTrue("Number of Datatypes in Repository different", baseArchitectureVersion.getRepository().getDataTypes__Repository().size()==targetArchitectureVersion.getRepository().getDataTypes__Repository().size());
	}
	
	@Test 
	public void testEMFCompareDiffUsage() {
		
		// Do Modifications
		BasicComponent testComponent = ArchitectureModelFactoryFacade.createBasicComponent(targetArchitectureVersion, "AddedTestComponent");

		OperationInterface testInterface = ArchitectureModelFactoryFacade.createInterface(targetArchitectureVersion, "AddedTestInterface");
		
		ArchitectureModelFactoryFacade.createProvidedRole(testComponent, testInterface);

		// Step 1: Matching 
		
		// explanation: MatchService.doMatch(local, latestFromRepository, null=default options);
		MatchModel matchModel = null;
		try {
			matchModel = MatchService.doMatch(targetArchitectureVersion.getRepository(), baseArchitectureVersion.getRepository(), null);
		} catch (InterruptedException e) {
			fail("Model Matching interrupted, "+e.getMessage());
		}

		assertTrue("MatchModel null", matchModel!=null);
		
		if (matchModel != null) {
			DiffModel diff = DiffService.doDiff(matchModel);
			
			assertTrue("DiffModel null", diff!=null);
			assertTrue("DiffModel is empty (no contents)", !diff.eContents().isEmpty());

//			for (TreeIterator<EObject> iterator = diff.eAllContents(); iterator.hasNext();) {
//				EObject el = iterator.next();
//				System.out.println(el.eClass().getName()+": "+ el.toString());
//	        }
			
		}
		
	}
	
	// Die folgenden Tests sollen atomare Modellveränderungen testen
	
	@Test 
	public void testDifferenceCalculationOnRepository() {
//		// add component to repository
//		PCMFactory.createBasicComponent(targetArchitectureVersion.getRepository(), "AddedTestComponent");
//		boolean addedBasicComponentFound = false;
//
//		// add interface to repository
//		PCMFactory.createInterface(targetArchitectureVersion.getRepository(), "AddedTestInterface");
//		boolean addedInterfaceFound = false;

//		// remove component from repository
//		EcoreUtil.delete(targetArchitectureVersion.getRepository().getComponents__Repository().get(0));

//		// remove interface from repository
//		EcoreUtil.delete(targetArchitectureVersion.getRepository().getInterfaces__Repository().get(0));
//		EcoreUtil.delete(targetArchitectureVersion.getRepository().getInterfaces__Repository().get(0));
		
//		// add interface to repository
//		OperationInterface testInterface = PCMFactory.createInterface(targetArchitectureVersion.getRepository(), "AddedTestInterface");
//		
//		// add provided role
//		PCMFactory.createProvidedRole((BasicComponent)targetArchitectureVersion.getRepository().getComponents__Repository().get(0), testInterface);
//		// add required role
//		PCMFactory.createRequiredRole((BasicComponent)targetArchitectureVersion.getRepository().getComponents__Repository().get(0), testInterface);

		// delete provided role
		//EcoreUtil.delete(targetArchitectureVersion.getRepository().getComponents__Repository().get(0).getProvidedRoles_InterfaceProvidingEntity().get(0));

		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());
		
		System.out.println("elements from tree-iterator:");
		for (TreeIterator<EObject> iterator = diff.eAllContents(); iterator.hasNext();) {
			EObject el = iterator.next();
			System.out.println(el.eClass().getName()+": "+ el.toString());
    	}
		
		System.out.println("elements from differences-list:");
		for (DiffElement diffElement : diff.getDifferences()) {
			System.out.println(">>> "+diffElement.eClass().getName());
			System.out.println(diffElement.toString());
			System.out.println("kind: "+ diffElement.getKind().toString());
			System.out.println("has subdiffs: "+ !diffElement.getSubDiffElements().isEmpty());
		
			if (diffElement instanceof ModelElementChangeLeftTarget) {
				ModelElementChangeLeftTarget element = (ModelElementChangeLeftTarget) diffElement;
				System.out.println("Left Element: "+ element.getLeftElement());
				System.out.println("Right Parent: "+ element.getRightParent());
				
				if (element.getLeftElement() instanceof BasicComponent) {
					BasicComponent basicComponent = (BasicComponent) element.getLeftElement();
					System.out.println("Action: Basic Component "+basicComponent+" Added");
				} else if (element.getLeftElement() instanceof OperationInterface) {
					OperationInterface operationInterface = (OperationInterface) element.getLeftElement();
					System.out.println("Action: Interface "+operationInterface+" Added");
				} else if (element.getLeftElement() instanceof OperationRequiredRole) {
					OperationRequiredRole requiredRole = (OperationRequiredRole) element.getLeftElement();
					System.out.println("Action: OperationRequiredRole "+requiredRole+" Added");
				} else if (element.getLeftElement() instanceof OperationProvidedRole) {
					OperationProvidedRole providedRole = (OperationProvidedRole) element.getLeftElement();
					System.out.println("Action: OperationProvidedRole "+providedRole+" Added");
				} else {
					System.out.println("not matched");
				}
			} else if (diffElement instanceof ModelElementChangeRightTarget) {
				ModelElementChangeRightTarget element = (ModelElementChangeRightTarget) diffElement;
				System.out.println("Right Element: "+ element.getRightElement());
				System.out.println("Left Parent: "+ element.getLeftParent());

				if (element.getRightElement() instanceof BasicComponent) {
					BasicComponent basicComponent = (BasicComponent) element.getRightElement();
					System.out.println("Action: Basic Component "+basicComponent+" Deleted");
				} else if (element.getRightElement() instanceof OperationInterface) {
					OperationInterface operationInterface = (OperationInterface) element.getRightElement();
					System.out.println("Action: Interface "+operationInterface+" Deleted");
				} else {
					System.out.println("not matched");
				}
			} else if (diffElement instanceof UpdateReference) {
				UpdateReference element = (UpdateReference) diffElement;
				System.out.println("Left Element: "+element.getLeftElement());
				System.out.println("Left Target: "+element.getLeftTarget());
				System.out.println("Right Element: "+element.getRightElement());
				System.out.println("Right Target: "+element.getRightTarget());
				if ((element.getKind() == DifferenceKind.CHANGE)
						&& (element.getRightTarget() == null)
						&& (element.getLeftElement() instanceof ProvidedRole)
						&& (element.getLeftTarget() instanceof OperationInterface)) {
					System.out.println("Action: Interface for Provided Role deleted.");
				} else if ((element.getKind() == DifferenceKind.CHANGE)
						&& (element.getRightTarget() == null)
						&& (element.getLeftElement() instanceof RequiredRole)
						&& (element.getLeftTarget() instanceof OperationInterface)) {
					System.out.println("Action: Interface for Required Role deleted.");
				} else {
					System.out.println("not matched");
				}
			} else {
				System.out.println("not matched");
			}
			System.out.println("----");
		}
		
	}
	
	
	
	
	@Test 
	public void testDifferenceDetectionRules_AddedBasicComponent() {
		// add component to repository
		ArchitectureModelFactoryFacade.createBasicComponent(targetArchitectureVersion, "AddedTestComponent");
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());
		
		List<DiffElement> diffElements = DifferenceCalculation.foundAddedBasicComponent(diff);
		
		assertTrue("Added Basic Component Not Found", diffElements.size()==1);

		EObject component = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
				
		assertTrue("Added Basic Component Not Retrieved Properly", component!=null && component instanceof BasicComponent);
	}
	
	@Test 
	public void testDifferenceDetectionRules_RemovedBasicComponent() {
		// remove component from repository
		EcoreUtil.delete(targetArchitectureVersion.getRepository().getComponents__Repository().get(0));
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundDeletedBasicComponent(diff);
		
		assertTrue("Deleted Basic Component Not Found", diffElements.size()==1);

		EObject component = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Deleted Basic Component Not Retrieved Properly", component!=null && component instanceof BasicComponent);
	}

	@Test 
	public void testDifferenceDetectionRules_AddedCompositeComponent() {
		// add component to repository
		ArchitectureModelFactoryFacade.createCompositeComponent(targetArchitectureVersion, "AddedTestCompositeComponent");
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());
		
		List<DiffElement> diffElements = DifferenceCalculation.foundAddedCompositeComponent(diff);
		
		assertTrue("Added Composite Component Not Found", diffElements.size()==1);

		EObject component = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
				
		assertTrue("Added Composite Component Not Retrieved Properly", component!=null && component instanceof CompositeComponent);
	}
	
	@Test 
	public void testDifferenceDetectionRules_RemovedCompositeComponent() {
		// remove component from repository
		EcoreUtil.delete(targetArchitectureVersion.getRepository().getComponents__Repository().get(3));
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundDeletedCompositeComponent(diff);
		
		assertTrue("Deleted Composite Component Not Found", diffElements.size()==1);

		EObject component = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Deleted Composite Component Not Retrieved Properly", component!=null && component instanceof CompositeComponent);
	}
	
	@Test 
	public void testDifferenceDetectionRules_AddedCollectionDatatype() {
		ArchitectureModelFactoryFacade.createCollectionDatatype(targetArchitectureVersion, "TestCollectionDatatype", null);
		
		CollectionDataType collectionDatatype = ArchitectureModelLookup.lookUpCollectionDatatypeByName(targetArchitectureVersion, "TestCollectionDatatype");
		
		assertTrue("Added CollectionDataType not found (lookup fault)", collectionDatatype != null);
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundAddedCollectionDatatype(diff);

		assertTrue("Added CollectionDataType Not Found", diffElements.size()==1);

		EObject collectiondatatype = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Added CollectionDataType Not Retrieved Properly", collectiondatatype!=null && collectiondatatype instanceof CollectionDataType);
	}
	
	@Test 
	public void testDifferenceDetectionRules_RemovedCollectionDataType() {
		CollectionDataType collectionDatatype = ArchitectureModelLookup.lookUpCollectionDatatypeByName(targetArchitectureVersion, "CollectionDatatypeForRemoval");
		EcoreUtil.delete(collectionDatatype);
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundDeletedCollectionDatatype(diff);

		assertTrue("Deleted CollectionDataType Not Found", diffElements.size()==1);

		EObject datatype = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Deleted CollectionDataType Not Retrieved Properly", datatype!=null && datatype instanceof CollectionDataType);
	}

	@Test 
	public void testDifferenceDetectionRules_AddedCompositeDatatype() {
		ArchitectureModelFactoryFacade.createCompositeDatatype(targetArchitectureVersion, "TestCompositeDatatype");
		
		CompositeDataType collectionDatatype = ArchitectureModelLookup.lookUpCompositeDatatypeByName(targetArchitectureVersion, "TestCompositeDatatype");
		
		assertTrue("Added CollectionDataType not found (lookup fault)", collectionDatatype != null);
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundAddedCompositeDatatype(diff);

		assertTrue("Added CollectionDataType Not Found", diffElements.size()==1);

		EObject compositedatatype = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Added CollectionDataType Not Retrieved Properly", compositedatatype!=null && compositedatatype instanceof CompositeDataType);
	}

	@Test 
	public void testDifferenceDetectionRules_RemovedCompositeDataType() {
		CompositeDataType compositeDatatype = ArchitectureModelLookup.lookUpCompositeDatatypeByName(targetArchitectureVersion, "CompositeDatatypeForRemoval");
		EcoreUtil.delete(compositeDatatype);
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundDeletedCompositeDatatype(diff);

		assertTrue("Deleted CompositeDataType Not Found", diffElements.size()==1);

		EObject datatype = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Deleted CompositeDataType Not Retrieved Properly", datatype!=null && datatype instanceof CompositeDataType);
	}
	
	@Test 
	public void testDifferenceDetectionRules_AddedInnerDeclarationForCompositeDatatype() {
		CompositeDataType compositeDatatype = ArchitectureModelLookup.lookUpCompositeDatatypeByName(targetArchitectureVersion, "CompositeDatatypeForRemoval");
		ArchitectureModelFactoryFacade.createInnerdeclarationOfCompositeDatatype(compositeDatatype, "name", null);
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundAddedInnerDeclarationOfCompositeDatatype(diff);

		assertTrue("Added InnerDeclaration of CompositeDataType Not Found", diffElements.size()==1);

		EObject innerdeclaration = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Added InnerDeclaration Not Retrieved Properly", innerdeclaration!=null && innerdeclaration instanceof InnerDeclaration);
	}

	@Test 
	public void testDifferenceDetectionRules_RemovedInnerDeclarationForCompositeDatatype() {
		CompositeDataType compositeDatatype = ArchitectureModelLookup.lookUpCompositeDatatypeByName(targetArchitectureVersion, "CompositeDatatypeForRemoval");
		EcoreUtil.delete(compositeDatatype.getInnerDeclaration_CompositeDataType().get(0));
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundDeletedInnerDeclarationOfCompositeDatatype(diff);

		assertTrue("Deleted InnerDeclaration of CompositeDataType Not Found", diffElements.size()==1);

		EObject innerdeclaration = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Deleted InnerDeclaration Not Retrieved Properly", innerdeclaration!=null && innerdeclaration instanceof InnerDeclaration);
	}

	@Test 
	public void testDifferenceDetectionRules_AddedInterface() {
		// add interface to repository
		ArchitectureModelFactoryFacade.createInterface(targetArchitectureVersion, "AddedTestInterface");
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundAddedInterface(diff);

		assertTrue("Added Interface Not Found", diffElements.size()==1);

		EObject interf = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Added Interface Not Retrieved Properly", interf!=null && interf instanceof OperationInterface);
	}

	@Test 
	public void testDifferenceDetectionRules_RemovedInterface() {
		// remove interface
		EcoreUtil.delete(targetArchitectureVersion.getRepository().getInterfaces__Repository().get(0));
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundDeletedInterface(diff);

		assertTrue("Deleted Interface Not Found", diffElements.size()==1);

		EObject interf = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Deleted Interface Not Retrieved Properly", interf!=null && interf instanceof OperationInterface);
	}

	@Test 
	public void testDifferenceDetectionRules_RemovedSignatureFromInterface() {
		
		Interface interf = ArchitectureModelLookup.lookUpInterfaceByName(targetArchitectureVersion, "ClientInterface");

		assertTrue("ClientInterface not found or it is not an operation interface", interf!=null && interf instanceof OperationInterface);
		
		OperationInterface opInterface = (OperationInterface) interf;
		
		assertTrue("Client Interface has no Signatures for DeletionTest", opInterface.getSignatures__OperationInterface().size()==1);
		
		// delete Signature
		ArchitectureModelFactoryFacade.deleteSignatureForInterface(opInterface.getSignatures__OperationInterface().get(0));
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundDeletedSignature(diff);

		assertTrue("Deleted Signature Not Found", diffElements.size()==1);

		EObject signature = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Deleted Signature Not Retrieved Properly", signature!=null && signature instanceof OperationSignature);
	}

	@Test 
	public void testDifferenceDetectionRules_AddSignatureToInterface() {
		
		Interface interf = ArchitectureModelLookup.lookUpInterfaceByName(targetArchitectureVersion, "ClientInterface");

		assertTrue("ClientInterface not found or it is not an operation interface", interf!=null && interf instanceof OperationInterface);
		
		OperationInterface opInterface = (OperationInterface) interf;
		
		assertTrue("Client Interface has no Signatures for DeletionTest", opInterface.getSignatures__OperationInterface().size()==1);
		
		// delete Signature
		ArchitectureModelFactoryFacade.createSignatureForInterface(opInterface, "addedTestSignature");
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundAddedSignature(diff);

		assertTrue("Added Signature Not Found", diffElements.size()==1);

		EObject signature = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Added Signature Not Retrieved Properly", signature!=null && signature instanceof OperationSignature);
	}

	@Test 
	public void testDifferenceDetectionRules_AddParameterToSignature() {
		
		Interface interf = ArchitectureModelLookup.lookUpInterfaceByName(targetArchitectureVersion, "ClientInterface");

		assertTrue("ClientInterface not found or it is not an operation interface", interf!=null && interf instanceof OperationInterface);
		
		OperationInterface opInterface = (OperationInterface) interf;
		
		assertTrue("Client Interface has no Signatures for DeletionTest", opInterface.getSignatures__OperationInterface().size()==1);
		
		// delete Signature
		ArchitectureModelFactoryFacade.createParameterForSignature(opInterface.getSignatures__OperationInterface().get(0), "addedParameter", null);
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundAddedParameter(diff);

		assertTrue("Added Parameter Not Found", diffElements.size()==1);

		EObject parameter = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Added Parameter Not Retrieved Properly", parameter!=null && parameter instanceof Parameter && ((Parameter)parameter).getParameterName().equals("addedParameter"));
	}

	@Test 
	public void testDifferenceDetectionRules_RemoveParameterFromSignature() {
		
		Interface interf = ArchitectureModelLookup.lookUpInterfaceByName(targetArchitectureVersion, "ClientInterface");

		assertTrue("ClientInterface not found or it is not an operation interface", interf!=null && interf instanceof OperationInterface);
		
		OperationInterface opInterface = (OperationInterface) interf;
		
		assertTrue("Client Interface has no Signatures for DeletionTest", opInterface.getSignatures__OperationInterface().size()==1);

		assertTrue("First Signature of Client Interface has no Parameter for DeletionTest", opInterface.getSignatures__OperationInterface().get(0).getParameters__OperationSignature().size()==1);

		// delete Signature
		ArchitectureModelFactoryFacade.deleteParameterForSignature(opInterface.getSignatures__OperationInterface().get(0).getParameters__OperationSignature().get(0));
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundDeletedParameter(diff);

		assertTrue("Deleted Parameter Not Found", diffElements.size()==1);

		EObject parameter = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Deleted Parameter Not Retrieved Properly", parameter!=null && parameter instanceof Parameter && ((Parameter)parameter).getParameterName().equals("parameterForRemoval"));
	}

	
	@Test 
	public void testDifferenceDetectionRules_RemovedInterfaceForProvidedRole() {
		// remove interface
		EcoreUtil.delete(targetArchitectureVersion.getRepository().getInterfaces__Repository().get(0));
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundDeletedInterfaceForProvidedRole(diff);

		assertTrue("Deleted Interface For ProvidedRole Not Found", diffElements.size()==1);

		EObject role = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Provided Role with Deleted Interface Not Retrieved Properly", role!=null && role instanceof ProvidedRole);
	}
	
	@Test 
	public void testDifferenceDetectionRules_ModifyInterfaceForProvidedRole() {
		// set other interface for client component
		RepositoryComponent client = ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Client");
		OperationInterface serverInterface = (OperationInterface) ArchitectureModelLookup.lookUpInterfaceByName(targetArchitectureVersion, "ServerInterface");

		OperationProvidedRole clientRole = (OperationProvidedRole)(client.getProvidedRoles_InterfaceProvidingEntity().get(0));
		clientRole.setProvidedInterface__OperationProvidedRole(serverInterface);
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());
		
		List<DiffElement> diffElements = DifferenceCalculation.foundDeletedInterfaceForProvidedRole(diff);

		assertTrue("Modified Interface For ProvidedRole Not Found", diffElements.size()==1);

		EObject role = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Provided Role with Modified Interface Not Retrieved Properly", role!=null && role instanceof ProvidedRole);
	}

	
	@Test 
	public void testDifferenceDetectionRules_RemovedInterfaceForRequiredRole() {
		// remove interface
		EcoreUtil.delete(targetArchitectureVersion.getRepository().getInterfaces__Repository().get(1));
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundDeletedInterfaceForRequiredRole(diff);

		assertTrue("Deleted Interface For RequiredRole Not Found", diffElements.size()==1);

		EObject role = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Required Role with Deleted Interface Not Retrieved Properly", role!=null && role instanceof RequiredRole);
	}
	

	@Test 
	public void testDifferenceDetectionRules_AddProvidedRole() {
		// add interface to repository
		OperationInterface testInterface = ArchitectureModelFactoryFacade.createInterface(targetArchitectureVersion, "AddedTestInterface");
		// add provided role
		ArchitectureModelFactoryFacade.createProvidedRole((BasicComponent)targetArchitectureVersion.getRepository().getComponents__Repository().get(0), testInterface);		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundAddedProvidedRole(diff);
		
		assertTrue("Added Provided Role not found", diffElements.size()==1);

		EObject role = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Added Provided Role not Retrieved Properly", role!=null && role instanceof ProvidedRole);

	}

	@Test 
	public void testDifferenceDetectionRules_AddRequiredRole() {
		// add interface to repository
		OperationInterface testInterface = ArchitectureModelFactoryFacade.createInterface(targetArchitectureVersion, "AddedTestInterface");
		// add required role
		ArchitectureModelFactoryFacade.createRequiredRole((BasicComponent)targetArchitectureVersion.getRepository().getComponents__Repository().get(0), testInterface);	
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundAddedRequiredRole(diff);
		
		assertTrue("Added Required Role not found", diffElements.size()==1);

		EObject role = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Added Required Role not Retrieved Properly", role!=null && role instanceof RequiredRole);
	}
	
	@Test 
	public void testDifferenceDetectionRules_DeleteProvidedRole() {
		EcoreUtil.delete(targetArchitectureVersion.getRepository().getComponents__Repository().get(0).getProvidedRoles_InterfaceProvidingEntity().get(0));
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundDeletedProvidedRole(diff);

		assertTrue("Deleted Provided Role not found", diffElements.size()==1);

		EObject role = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Deleted Provided Role not Retrieved Properly", role!=null && role instanceof ProvidedRole);

	}

	@Test 
	public void testDifferenceDetectionRules_DeleteRequiredRole() {
		EcoreUtil.delete(targetArchitectureVersion.getRepository().getComponents__Repository().get(0).getRequiredRoles_InterfaceRequiringEntity().get(0));
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getRepository(), targetArchitectureVersion.getRepository());

		List<DiffElement> diffElements = DifferenceCalculation.foundDeletedRequiredRole(diff);

		assertTrue("Deleted Required Role not found", diffElements.size()==1);

		EObject role = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
		
		assertTrue("Deleted Provided Role not Retrieved Properly", role!=null && role instanceof RequiredRole);

	}
	
	
	@Test 
	public void testWorkplanGeneration_AddInterface() {
		ArchitectureModelFactoryFacade.createInterface(targetArchitectureVersion, "AddedTestInterface");

		List<Activity> workplan = DifferenceCalculation.deriveWorkplan(baseArchitectureVersion, targetArchitectureVersion);

		assertTrue("More than one activity derived.", workplan.size()==1);	

		assertTrue("Added Interface Activity Not Found", (!workplan.isEmpty())&&(workplan.get(0).getBasicActivity()==BasicActivity.ADD)&&(workplan.get(0).getElementType()==ActivityElementType.INTERFACE));	
	}

	@Test 
	public void testWorkplanGeneration_RemoveInterface() {
		EcoreUtil.delete(targetArchitectureVersion.getRepository().getInterfaces__Repository().get(0));

		List<Activity> workplan = DifferenceCalculation.deriveWorkplan(baseArchitectureVersion, targetArchitectureVersion);

		assertTrue("More than one activity derived.", workplan.size()==1);	

		assertTrue("Deleted Interface Activity Not Found", (!workplan.isEmpty())&&(workplan.get(0).getBasicActivity()==BasicActivity.REMOVE)&&(workplan.get(0).getElementType()==ActivityElementType.INTERFACE));	
	}

	
	
	
	@Test 
	public void testWorkplanGeneration_AddBasicComponent() {
		// add component to repository
		ArchitectureModelFactoryFacade.createBasicComponent(targetArchitectureVersion, "AddedTestComponent");
		
		List<Activity> workplan = DifferenceCalculation.deriveWorkplan(baseArchitectureVersion, targetArchitectureVersion);

		assertTrue("More than one activity derived.", workplan.size()==1);	

		assertTrue("Added Basic Component Not Found", (!workplan.isEmpty())&&(workplan.get(0).getBasicActivity()==BasicActivity.ADD)&&(workplan.get(0).getElementType()==ActivityElementType.BASICCOMPONENT));	
	}
	
	@Test 
	public void testWorkplanGeneration_RemoveBasicComponent() {
		EcoreUtil.delete(targetArchitectureVersion.getRepository().getComponents__Repository().get(0));
		
		List<Activity> workplan = DifferenceCalculation.deriveWorkplan(baseArchitectureVersion, targetArchitectureVersion);

		assertTrue("More than one activity derived.", workplan.size()==1);	

		assertTrue("Removed Basic Component Not Found", (!workplan.isEmpty())&&(workplan.get(0).getBasicActivity()==BasicActivity.REMOVE)&&(workplan.get(0).getElementType()==ActivityElementType.BASICCOMPONENT));	
	}


	@Test 
	public void testWorkplanGeneration_AddCompositeDatatype() {
		ArchitectureModelFactoryFacade.createCompositeDatatype(targetArchitectureVersion, "AddedCompositeDatatype");

		List<Activity> workplan = DifferenceCalculation.deriveWorkplan(baseArchitectureVersion, targetArchitectureVersion);

		assertTrue("More than one activity derived.", workplan.size()==1);	

		assertTrue("Added CompositeDatatype Activity Not Found", (!workplan.isEmpty())&&(workplan.get(0).getBasicActivity()==BasicActivity.ADD)&&(workplan.get(0).getElementType()==ActivityElementType.COMPOSITEDATATYPE));	
	}

	@Test 
	public void testWorkplanGeneration_RemoveCompositeDatatype() {
		CompositeDataType compositeDatatype = ArchitectureModelLookup.lookUpCompositeDatatypeByName(targetArchitectureVersion, "CompositeDatatypeForRemoval");
		EcoreUtil.delete(compositeDatatype);

		List<Activity> workplan = DifferenceCalculation.deriveWorkplan(baseArchitectureVersion, targetArchitectureVersion);

		assertTrue("More than one activity derived.", workplan.size()==1);	

		assertTrue("Deleted CompositeDatatype Activity Not Found", (!workplan.isEmpty())&&(workplan.get(0).getBasicActivity()==BasicActivity.REMOVE)&&(workplan.get(0).getElementType()==ActivityElementType.COMPOSITEDATATYPE));	
	}

	@Test 
	public void testWorkplanGeneration_AddCollectionDatatype() {
		ArchitectureModelFactoryFacade.createCollectionDatatype(targetArchitectureVersion, "AddedCollectionDatatype", null);

		List<Activity> workplan = DifferenceCalculation.deriveWorkplan(baseArchitectureVersion, targetArchitectureVersion);

		assertTrue("More than one activity derived.", workplan.size()==1);	

		assertTrue("Added CollectionDatatype Activity Not Found", (!workplan.isEmpty())&&(workplan.get(0).getBasicActivity()==BasicActivity.ADD)&&(workplan.get(0).getElementType()==ActivityElementType.COLLECTIONDATATYPE));	
	}

	@Test 
	public void testWorkplanGeneration_RemoveCollectionDatatype() {
		CollectionDataType collectionDatatype = ArchitectureModelLookup.lookUpCollectionDatatypeByName(targetArchitectureVersion, "CollectionDatatypeForRemoval");
		EcoreUtil.delete(collectionDatatype);

		List<Activity> workplan = DifferenceCalculation.deriveWorkplan(baseArchitectureVersion, targetArchitectureVersion);

		assertTrue("More than one activity derived.", workplan.size()==1);	

		assertTrue("Deleted CollectionDatatype Activity Not Found", (!workplan.isEmpty())&&(workplan.get(0).getBasicActivity()==BasicActivity.REMOVE)&&(workplan.get(0).getElementType()==ActivityElementType.COLLECTIONDATATYPE));	
	}

	@Test 
	public void testWorkplanGeneration_AddAssemblyConnector() {
		RepositoryComponent client = ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Client");
		RepositoryComponent server = ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		
		// assembly connector added
		ArchitectureModelFactoryFacade.createAssemblyConnector(client, server, targetArchitectureVersion);

		List<Activity> workplan = DifferenceCalculation.deriveWorkplan(baseArchitectureVersion, targetArchitectureVersion);

		assertTrue("More than one activity derived. Size:"+workplan.size(), workplan.size()==1);	

		assertTrue("Added AssemblyConnector Activity Not Found", (!workplan.isEmpty())&&(workplan.get(0).getBasicActivity()==BasicActivity.ADD)&&(workplan.get(0).getElementType()==ActivityElementType.ASSEMBLYCONNECTOR));	
	}

	@Test 
	public void testWorkplanGeneration_RemoveAssemblyConnector() {
		RepositoryComponent server = ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		RepositoryComponent serverBase = ArchitectureModelLookup.lookUpComponentByName(baseArchitectureVersion, "Server");
		RepositoryComponent database = ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Database");
		RepositoryComponent databaseBase = ArchitectureModelLookup.lookUpComponentByName(baseArchitectureVersion, "Database");

		assertTrue("server component not found", server!=null);
		assertTrue("database component not found", database!=null);

		AssemblyContext assemblyContextServer = ArchitectureModelLookup.lookUpAssemblyContextsForRepositoryComponent(targetArchitectureVersion, server).get(0);
		AssemblyContext assemblyContextDatabase = ArchitectureModelLookup.lookUpAssemblyContextsForRepositoryComponent(targetArchitectureVersion, database).get(0);

		assertTrue("Assembly context for server not found", assemblyContextServer!=null);
		assertTrue("Assembly context for database not found", assemblyContextDatabase!=null);

		AssemblyConnector connector = ArchitectureModelLookup.lookUpAssemblyConnectorsBetweenAssemblyContexts(assemblyContextDatabase, assemblyContextServer).get(0);
		
		assertTrue("Assembly Connector not found by lookup", connector!=null);
	
		EcoreUtil.delete(connector);
		
		List<Activity> workplan = DifferenceCalculation.deriveWorkplan(baseArchitectureVersion, targetArchitectureVersion);

		assertTrue("More than one activity derived. Size:"+workplan.size(), workplan.size()==1);	

		assertTrue("Removed AssemblyConnector Activity Not Found", (!workplan.isEmpty())&&(workplan.get(0).getBasicActivity()==BasicActivity.REMOVE)&&(workplan.get(0).getElementType()==ActivityElementType.ASSEMBLYCONNECTOR));	
	}
	
	
	@Test 
	public void testDifferenceCalculationOnSystem() {
		// assemblycontexts added
		
		RepositoryComponent client = ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Client");
		RepositoryComponent server = ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		
		// assembly connector added
		ArchitectureModelFactoryFacade.createAssemblyConnector(client, server, targetArchitectureVersion);
		
		// assembly connector removed

		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getSystem(), targetArchitectureVersion.getSystem());

		System.out.println("elements from tree-iterator:");
		for (TreeIterator<EObject> iterator = diff.eAllContents(); iterator.hasNext();) {
			EObject el = iterator.next();
			System.out.println(el.eClass().getName()+": "+ el.toString());
    	}
		
		System.out.println("elements from differences-list:");
		for (DiffElement diffElement : diff.getDifferences()) {
			System.out.println(">>> "+diffElement.eClass().getName());
			System.out.println(diffElement.toString());
			System.out.println("kind: "+ diffElement.getKind().toString());
			System.out.println("has subdiffs: "+ !diffElement.getSubDiffElements().isEmpty());
		}
	}
	
	@Test 
	public void testDifferenceDetectionRules_System_AssemblyConnectorAdded() {
		RepositoryComponent client = ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Client");
		RepositoryComponent server = ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		
		// assembly connector added
		ArchitectureModelFactoryFacade.createAssemblyConnector(client, server, targetArchitectureVersion);
	
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getSystem(), targetArchitectureVersion.getSystem());
	
		List<DiffElement> diffElements = DifferenceCalculation.foundAddedAssemblyConnector(diff);
		
		assertTrue("Added Assembly Connector Not Found", diffElements.size()==1);

		EObject connector = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
				
		assertTrue("Added Assembly Connector Not Retrieved Properly", connector!=null && connector instanceof AssemblyConnector);
		
		assertTrue("Provided Side of Connector Not Equals Server",((AssemblyConnector)connector).getProvidingAssemblyContext_AssemblyConnector().getEncapsulatedComponent__AssemblyContext()==server);

		assertTrue("Required Side of Connector Not Equals Client",((AssemblyConnector)connector).getRequiringAssemblyContext_AssemblyConnector().getEncapsulatedComponent__AssemblyContext()==client);
		
	}

	@Test 
	public void testDifferenceDetectionRules_System_AssemblyConnectorRemoved() {
		RepositoryComponent server = ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Server");
		RepositoryComponent serverBase = ArchitectureModelLookup.lookUpComponentByName(baseArchitectureVersion, "Server");
		RepositoryComponent database = ArchitectureModelLookup.lookUpComponentByName(targetArchitectureVersion, "Database");
		RepositoryComponent databaseBase = ArchitectureModelLookup.lookUpComponentByName(baseArchitectureVersion, "Database");

		assertTrue("server component not found", server!=null);
		assertTrue("database component not found", database!=null);

		AssemblyContext assemblyContextServer = ArchitectureModelLookup.lookUpAssemblyContextsForRepositoryComponent(targetArchitectureVersion, server).get(0);
		AssemblyContext assemblyContextDatabase = ArchitectureModelLookup.lookUpAssemblyContextsForRepositoryComponent(targetArchitectureVersion, database).get(0);

		assertTrue("Assembly context for server not found", assemblyContextServer!=null);
		assertTrue("Assembly context for database not found", assemblyContextDatabase!=null);

		AssemblyConnector connector = ArchitectureModelLookup.lookUpAssemblyConnectorsBetweenAssemblyContexts(assemblyContextDatabase, assemblyContextServer).get(0);
		
		assertTrue("Assembly Connector not found by lookup", connector!=null);
	
		EcoreUtil.delete(connector);
		
		DiffModel diff = DifferenceCalculation.calculateDiffModel(baseArchitectureVersion.getSystem(), targetArchitectureVersion.getSystem());
	
		List<DiffElement> diffElements = DifferenceCalculation.foundRemovedAssemblyConnector(diff);
		
		assertTrue("Removed Assembly Connector Not Found", diffElements.size()==1);

		EObject removedconnector = DifferenceCalculation.retrieveArchitectureElement(diffElements.get(0));
				
		assertTrue("Removed Assembly Connector Not Retrieved Properly", removedconnector!=null && removedconnector instanceof AssemblyConnector);
		
		assertTrue("Provided Side of Connector Not Equals Database",((AssemblyConnector)removedconnector).getProvidingAssemblyContext_AssemblyConnector().getEncapsulatedComponent__AssemblyContext()==databaseBase);

		assertTrue("Required Side of Connector Not Equals Server",((AssemblyConnector)removedconnector).getRequiringAssemblyContext_AssemblyConnector().getEncapsulatedComponent__AssemblyContext()==serverBase);
		
	}

	@Test 
	public void testArchitectureModelLookup_lookUpComponentByName() {
		String testcomponentName = "LookUpComponent";
		
		BasicComponent testComponent = ArchitectureModelFactoryFacade.createBasicComponent(baseArchitectureVersion, testcomponentName);
		BasicComponent foundComponent = (BasicComponent) ArchitectureModelLookup.lookUpComponentByName(baseArchitectureVersion, testcomponentName);
		
		assertTrue("Component not found", foundComponent!=null);
		assertTrue("Wrong Component found", foundComponent==testComponent);

		BasicComponent errorComponent = (BasicComponent) ArchitectureModelLookup.lookUpComponentByName(baseArchitectureVersion, null);
		assertTrue("LookUp of missing component not resulted in null", errorComponent==null);
	}	
	
}
