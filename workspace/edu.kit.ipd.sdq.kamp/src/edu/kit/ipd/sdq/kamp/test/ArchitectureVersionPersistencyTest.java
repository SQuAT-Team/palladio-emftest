package edu.kit.ipd.sdq.kamp.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.Test;

import edu.kit.ipd.sdq.kamp.activator.Activator;
import edu.kit.ipd.sdq.kamp.core.ArchitectureModelFactoryFacade;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersion;
import edu.kit.ipd.sdq.kamp.core.ArchitectureVersionPersistency;

public class ArchitectureVersionPersistencyTest {

	public static final String TESTNAME = "ArchitectureVersionPersistencyTest";

	
	@Before
	public void setUp() throws Exception {
		TestPathProvider.resetTestProject(TESTNAME);
	}

	
	@Test
	public void testEmptyModelCreation() {
		String name = "basemodel";
		ArchitectureVersion version = ArchitectureModelFactoryFacade.createEmptyModel(name);
		
		assertTrue("Version null", version != null);
		assertTrue("Name not set properly", name.equals(version.getName()));
		assertTrue("Repository not set properly", version.getRepository()!=null);
		assertTrue("System not set properly", version.getSystem()!=null);
		assertTrue("InternalModificationModel not set properly", version.getModificationMarkRepository()!=null);
		assertTrue("ComponentInternalDepModel not set properly", version.getComponentInternalDependencyRepository()!=null);
		assertTrue("FieldOfActivityModel not set properly", version.getFieldOfActivityRepository()!=null);
	}

	@Test
	public void testModelStoreAndLoad() {
		String baseName = "basemodel";
		ArchitectureVersion saveVersion = ArchitectureModelFactoryFacade.createEmptyModel(baseName);
		
		try {
			ArchitectureVersionPersistency.save(TestPathProvider.getTestPath(TESTNAME).toString(), "dateitest", saveVersion);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		ArchitectureVersion loadVersion = null;
		try {
			loadVersion = ArchitectureVersionPersistency.load(TestPathProvider.getTestPath(TESTNAME).toString(), "dateitest", baseName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		assertTrue("Version null", loadVersion != null);
		assertTrue("Name not loaded properly", baseName.equals(loadVersion.getName()));
		assertTrue("Repository not loaded properly", loadVersion.getRepository()!=null);
		assertTrue("System not set properly", loadVersion.getSystem()!=null);
		assertTrue("InternalModificationModel not loaded properly", loadVersion.getModificationMarkRepository()!=null);
		assertTrue("ComponentInternalDepModel not loaded properly", loadVersion.getComponentInternalDependencyRepository()!=null);
		assertTrue("FieldOfActivityModel not loaded properly", loadVersion.getFieldOfActivityRepository()!=null);
	}
	
	

}
