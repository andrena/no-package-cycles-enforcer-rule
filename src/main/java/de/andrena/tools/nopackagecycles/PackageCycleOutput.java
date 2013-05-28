package de.andrena.tools.nopackagecycles;

import static de.andrena.tools.nopackagecycles.CollectionOutput.joinCollection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jdepend.framework.JavaClass;
import jdepend.framework.JavaPackage;
import de.andrena.tools.nopackagecycles.CollectionOutput.Appender;
import de.andrena.tools.nopackagecycles.CollectionOutput.StringProvider;

public class PackageCycleOutput {

	private List<JavaPackage> packages;
	private StringBuilder output;

	public PackageCycleOutput(List<JavaPackage> packages) {
		this.packages = packages;
	}

	public String getOutput() {
		output = new StringBuilder();
		orderPackagesByName();
		while (!packages.isEmpty()) {
			JavaPackage javaPackage = packages.get(0);
			packages.remove(javaPackage);
			if (javaPackage.containsCycle()) {
				appendOutputForPackageCycle(javaPackage);
			}
		}
		return output.toString();
	}

	private void appendOutputForPackageCycle(JavaPackage javaPackage) {
		List<JavaPackage> cyclicPackages = getCyclicPackages(javaPackage);
		packages.removeAll(cyclicPackages);
		appendHeaderForPackageCycle(cyclicPackages);
		for (JavaPackage cyclicPackage : cyclicPackages) {
			appendOutputForPackage(cyclicPackage, cyclicPackages);
		}
	}

	private void appendHeaderForPackageCycle(List<JavaPackage> cyclicPackages) {
		output.append("\n\n").append("Package-cycle found involving ");
		output.append(joinCollection(cyclicPackages, new StringProvider<JavaPackage>() {

			public String provide(JavaPackage javaPackage) {
				return javaPackage.getName();
			}
		}, ", "));
		output.append(':');
	}

	private void orderPackagesByName() {
		List<JavaPackage> packageList = new ArrayList<JavaPackage>(packages);
		orderByPackageName(packageList);
		packages = packageList;
	}

	private void appendOutputForPackage(final JavaPackage javaPackage, List<JavaPackage> cyclicPackages) {
		output.append("\n    ").append(javaPackage.getName()).append(" depends on:");
		for (JavaPackage cyclicPackage : cyclicPackages) {
			appendOutputForCyclicPackage(javaPackage, cyclicPackage);
		}
	}

	private void appendOutputForCyclicPackage(final JavaPackage javaPackage, JavaPackage cyclicPackage) {
		if (javaPackage.equals(cyclicPackage)) {
			return;
		}
		List<JavaClass> dependentClasses = getOrderedDependentClasses(javaPackage, cyclicPackage);
		if (!dependentClasses.isEmpty()) {
			appendOutputForDependentCyclicPackage(javaPackage, cyclicPackage, dependentClasses);
		}
	}

	private List<JavaClass> getOrderedDependentClasses(JavaPackage javaPackage, JavaPackage cyclicPackage) {
		List<JavaClass> dependentClasses = new ArrayList<JavaClass>();
		@SuppressWarnings("unchecked")
		Collection<JavaClass> allClasses = javaPackage.getClasses();
		for (JavaClass javaClass : allClasses) {
			if (javaClass.getImportedPackages().contains(cyclicPackage)) {
				dependentClasses.add(javaClass);
			}
		}
		orderByClassName(dependentClasses);
		return dependentClasses;
	}

	private void appendOutputForDependentCyclicPackage(JavaPackage javaPackage, JavaPackage cyclicPackage,
			List<JavaClass> dependentClasses) {
		output.append("\n        ").append(cyclicPackage.getName()).append(" (");
		appendOutputForCyclicPackageClasses(javaPackage, cyclicPackage, dependentClasses);
		output.append(")");
	}

	private void appendOutputForCyclicPackageClasses(JavaPackage javaPackage, final JavaPackage cyclicPackage,
			List<JavaClass> dependentClasses) {
		joinCollection(dependentClasses, output, new Appender<JavaClass>() {
			public void append(JavaClass packageClass) {
				output.append(packageClass.getName().substring(packageClass.getPackageName().length() + 1));
			}
		}, ", ");
	}

	private void orderByClassName(List<JavaClass> dependentClasses) {
		Collections.sort(dependentClasses, new Comparator<JavaClass>() {
			public int compare(JavaClass class1, JavaClass class2) {
				return class1.getName().compareTo(class2.getName());
			}
		});
	}

	private List<JavaPackage> getCyclicPackages(JavaPackage javaPackage) {
		List<JavaPackage> cyclicPackages = new ArrayList<JavaPackage>();
		javaPackage.collectAllCycles(cyclicPackages);
		removeDuplications(javaPackage, cyclicPackages);
		orderByPackageName(cyclicPackages);
		return cyclicPackages;
	}

	private void orderByPackageName(List<JavaPackage> cyclicPackages) {
		Collections.sort(cyclicPackages, new Comparator<JavaPackage>() {
			public int compare(JavaPackage package1, JavaPackage package2) {
				return package1.getName().compareTo(package2.getName());
			}
		});
	}

	private void removeDuplications(JavaPackage javaPackage, List<JavaPackage> cyclicPackages) {
		Set<JavaPackage> uniquePackages = new HashSet<JavaPackage>(cyclicPackages);
		cyclicPackages.clear();
		cyclicPackages.addAll(uniquePackages);
	}

}
