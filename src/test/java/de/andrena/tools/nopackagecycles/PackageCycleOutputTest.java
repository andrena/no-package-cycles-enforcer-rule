package de.andrena.tools.nopackagecycles;

import static de.andrena.tools.nopackagecycles.CollectionOutput.joinArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jdepend.framework.JavaClass;
import jdepend.framework.JavaPackage;

import org.junit.Before;
import org.junit.Test;

import de.andrena.tools.nopackagecycles.CollectionOutput.StringProvider;
import edu.emory.mathcs.backport.java.util.Arrays;

public class PackageCycleOutputTest {

	private static final String PACKAGE1_CLASS_NAME1 = "Package1Class1";
	private static final String PACKAGE1_CLASS_NAME2 = "Package1Class2";
	private static final String PACKAGE2_CLASS_NAME = "Package2Class";
	private static final String PACKAGE1_NAME = "sample.package1";
	private static final String PACKAGE2_NAME = "sample.package2";
	private Collection<JavaPackage> packages;
	private JavaPackage package1;
	private JavaPackage package2;

	@Before
	public void setUp() {
		packages = new ArrayList<JavaPackage>();
		package1 = createPackage(PACKAGE1_NAME);
		package2 = createPackage(PACKAGE2_NAME);
		package1.dependsUpon(package2);
		package2.dependsUpon(package1);
	}

	private JavaPackage createPackage(String package1Name) {
		JavaPackage newPackage = new JavaPackage(package1Name);
		packages.add(newPackage);
		return newPackage;
	}

	@Test
	public void outputFor_TwoPackagesWithCycle() throws Exception {
		assertOutput(package1, package2);
		assertOutput(package2, package1);
	}

	@Test
	public void outputFor_TwoPackagesWithCycleAndClasses() throws Exception {
		JavaClass package1Class = createClassInPackage(PACKAGE1_CLASS_NAME1, package1);
		package1Class.addImportedPackage(package2);
		JavaClass package2Class = createClassInPackage(PACKAGE2_CLASS_NAME, package2);
		package2Class.addImportedPackage(package1);
		assertOutputWithClasses(package1, package2, PACKAGE2_CLASS_NAME);
		assertOutputWithClasses(package2, package1, PACKAGE1_CLASS_NAME1);
	}

	@Test
	public void outputFor_TwoPackagesWithCycle_AndOnePackageWithoutCycle() throws Exception {
		String packageWithoutCycleName = "sample.package.without.cycle";
		createPackage(packageWithoutCycleName);
		assertOutput(package1, package2);
		assertOutput(package2, package1);
	}

	@Test
	public void outputFor_ThreePackagesWithCycle() throws Exception {
		String package3Name = "sample.package3";
		JavaPackage package3 = createPackage(package3Name);
		package1.dependsUpon(package3);
		package3.dependsUpon(package1);
		assertOutput(package1, package2, package3);
		assertOutput(package2, package1, package3);
		assertOutput(package3, package1, package2);
	}

	@Test
	public void outputFor_PackageWithCycleAndMultipleClasses() throws Exception {
		JavaClass package1Class1 = createClassInPackage(PACKAGE1_CLASS_NAME1, package1);
		package1Class1.addImportedPackage(package2);
		JavaClass package1Class2 = createClassInPackage(PACKAGE1_CLASS_NAME2, package1);
		package1Class2.addImportedPackage(package2);
		assertOutput(package1, package2);
		assertOutputWithClasses(package2, package1, PACKAGE1_CLASS_NAME1, PACKAGE1_CLASS_NAME2);
	}

	@Test
	public void outputFor_PackageWithCycleAndClassWithoutCycle() throws Exception {
		JavaClass package1Class1 = createClassInPackage(PACKAGE1_CLASS_NAME1, package1);
		package1Class1.addImportedPackage(package2);
		createClassInPackage(PACKAGE1_CLASS_NAME2, package1);
		assertOutput(package1, package2);
		assertOutputWithClasses(package2, package1, PACKAGE1_CLASS_NAME1);
	}

	private JavaClass createClassInPackage(String className, JavaPackage classPackage) {
		JavaClass package1Class = new JavaClass(classPackage.getName() + "." + className);
		classPackage.addClass(package1Class);
		return package1Class;
	}

	private void assertOutputWithClasses(JavaPackage javaPackage, JavaPackage dependencyPackage, String... classNames) {
		String joinedClassNames = joinArray(classNames, new StringProvider<String>() {
			public String provide(String value) {
				return value;
			}
		});
		assertThat(new PackageCycleOutput(packages).getOutput(), containsString(getPackageOutput(javaPackage.getName())
				+ getDependencyPackageOutput(dependencyPackage.getName(), joinedClassNames)));
	}

	private void assertOutput(JavaPackage javaPackage, JavaPackage... dependencyPackages) {
		@SuppressWarnings("unchecked")
		List<JavaPackage> dependencyPackageList = Arrays.asList(dependencyPackages);
		String dependencyOutput = CollectionOutput.joinCollection(dependencyPackageList,
				new StringProvider<JavaPackage>() {
					public String provide(JavaPackage dependencyPackage) {
						return getDependencyPackageOutput(dependencyPackage.getName(), "");
					}
				});
		assertThat(new PackageCycleOutput(packages).getOutput(), containsString(getPackageOutput(javaPackage.getName())
				+ dependencyOutput));
	}

	private String getPackageOutput(String packageName) {
		return "\n" + packageName + " has cyclic dependency to: ";
	}

	private String getDependencyPackageOutput(String dependencyPackageNames, String classNames) {
		return dependencyPackageNames + " (" + classNames + ")";
	}
}
