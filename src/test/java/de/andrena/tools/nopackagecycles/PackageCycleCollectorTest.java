package de.andrena.tools.nopackagecycles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import jdepend.framework.JavaPackage;

import org.junit.Before;
import org.junit.Test;

public class PackageCycleCollectorTest {

	private JavaPackage packageA;
	private JavaPackage packageB;
	private JavaPackage packageC;
	private JavaPackage packageD;
	private JavaPackage packageE;
	private PackageCycleCollector collector;

	@Before
	public void setUp() {
		packageA = new JavaPackage("packageA");
		packageB = new JavaPackage("packageB");
		packageC = new JavaPackage("packageC");
		packageD = new JavaPackage("packageD");
		packageE = new JavaPackage("packageE");
		collector = new PackageCycleCollector();
	}

	@Test
	public void collectCycles_NoCycle() throws Exception {
		packageA.dependsUpon(packageB);
		assertThat(collector.collectCycles(packageA), is(empty()));
	}

	@Test
	public void collectCycles_HasCycle() throws Exception {
		packageA.dependsUpon(packageB);
		packageB.dependsUpon(packageA);
		assertThat(collector.collectCycles(packageA), containsInAnyOrder(packageA, packageB));
	}

	@Test
	public void collectCycles_HasCycleWithThreePackages() throws Exception {
		packageA.dependsUpon(packageB);
		packageB.dependsUpon(packageC);
		packageC.dependsUpon(packageA);
		assertThat(collector.collectCycles(packageA), containsInAnyOrder(packageA, packageB, packageC));
	}

	@Test
	public void collectCycles_HasCycleWithMultiplePaths() throws Exception {
		packageA.dependsUpon(packageB);
		packageB.dependsUpon(packageC);
		packageC.dependsUpon(packageA);
		packageD.dependsUpon(packageB);
		packageB.dependsUpon(packageD);
		packageB.dependsUpon(packageE);
		packageE.dependsUpon(packageC);
		assertThat(collector.collectCycles(packageA),
				containsInAnyOrder(packageA, packageB, packageC, packageD, packageE));
	}

	@Test
	public void collectCycles_HasCycleWithDependendPackageNotInCycle() throws Exception {
		packageA.dependsUpon(packageB);
		packageB.dependsUpon(packageA);
		packageC.dependsUpon(packageA);
		assertThat(collector.collectCycles(packageC), is(empty()));
	}

}
