package edu.kit.ipd.sdq.kamp.test;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import de.uka.ipd.sdq.pcm.repository.CompositeComponent;

import edu.kit.ipd.sdq.kamp.core.ArchitectureModelFactoryFacade;
import edu.kit.ipd.sdq.kamp.core.ArchitectureModelLookup;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersion;

public class ArchitectureModelLookupTest {
	
	@Test 
	public void testLookUpCompositeComponents() {
		ArchitectureVersion version = ArchitectureModelFactoryFacade.createEmptyModel("testmodel");
		ArchitectureModelFactoryFacade.createBasicComponent(version, "Fake1");
		CompositeComponent component = ArchitectureModelFactoryFacade.createCompositeComponent(version, "MyCompositeComponent");
		ArchitectureModelFactoryFacade.createBasicComponent(version, "Fake2");
		List<CompositeComponent> compositeComponents = ArchitectureModelLookup.lookUpCompositeComponents(version);
		assertTrue("Composite component not found during look up", compositeComponents.size()==1 && 
				compositeComponents.get(0)==component);
	}
}
