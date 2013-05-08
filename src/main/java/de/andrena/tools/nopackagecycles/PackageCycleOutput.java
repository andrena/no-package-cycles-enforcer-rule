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

public class PackageCycleOutput {

	private final Collection<JavaPackage> packages;
	private StringBuilder output;

	public PackageCycleOutput(Collection<JavaPackage> packages) {
		this.packages = packages;
	}

	public String getOutput() {
		output = new StringBuilder();
		for (JavaPackage javaPackage : packages) {
			if (javaPackage.containsCycle()) {
				appendOutputForPackage(javaPackage);
			}
		}
		return output.toString();
	}

	private void appendOutputForPackage(final JavaPackage javaPackage) {
		output.append("\n").append(javaPackage.getName()).append(" has cyclic dependency to: ");
		joinCollection(getCyclicPackages(javaPackage), output, new Appender<JavaPackage>() {
			public void append(JavaPackage cyclicPackage) {
				appendOutputForCyclicPackage(javaPackage, cyclicPackage);
			}
		});
	}

	private void appendOutputForCyclicPackage(JavaPackage javaPackage, JavaPackage cyclicPackage) {
		output.append(cyclicPackage.getName()).append(" (");
		appendOutputForCyclicPackageClasses(javaPackage, cyclicPackage);
		output.append(")");
	}

	private void appendOutputForCyclicPackageClasses(JavaPackage javaPackage, final JavaPackage cyclicPackage) {
		joinCollection(getOrderedPackageClasses(javaPackage, cyclicPackage), output, new Appender<JavaClass>() {
			public void append(JavaClass packageClass) {
				output.append(packageClass.getName().substring(cyclicPackage.getName().length() + 1));
			}
		});
	}

	private Collection<JavaClass> getOrderedPackageClasses(JavaPackage javaPackage, JavaPackage cyclicPackage) {
		List<JavaClass> classes = getClassesDependentOnPackage(javaPackage, cyclicPackage);
		Collections.sort(classes, new Comparator<JavaClass>() {
			public int compare(JavaClass class1, JavaClass class2) {
				return class1.getName().compareTo(class2.getName());
			}
		});
		return classes;
	}

	private List<JavaClass> getClassesDependentOnPackage(JavaPackage javaPackage, JavaPackage cyclicPackage) {
		@SuppressWarnings("unchecked")
		Collection<JavaClass> allClasses = cyclicPackage.getClasses();
		List<JavaClass> dependentClasses = new ArrayList<JavaClass>();
		for (JavaClass clazz : allClasses) {
			if (clazz.getImportedPackages().contains(javaPackage)) {
				dependentClasses.add(clazz);
			}
		}
		return dependentClasses;
	}

	private List<JavaPackage> getCyclicPackages(JavaPackage javaPackage) {
		List<JavaPackage> cyclicPackages = new ArrayList<JavaPackage>();
		javaPackage.collectAllCycles(cyclicPackages);
		removeSelfAndDuplications(javaPackage, cyclicPackages);
		return orderByPackageName(cyclicPackages);
	}

	private List<JavaPackage> orderByPackageName(List<JavaPackage> cyclicPackages) {
		Collections.sort(cyclicPackages, new Comparator<JavaPackage>() {
			public int compare(JavaPackage package1, JavaPackage package2) {
				return package1.getName().compareTo(package2.getName());
			}
		});
		return cyclicPackages;
	}

	private void removeSelfAndDuplications(JavaPackage javaPackage, List<JavaPackage> cyclicPackages) {
		Set<JavaPackage> uniquePackages = new HashSet<JavaPackage>(cyclicPackages);
		uniquePackages.remove(javaPackage);
		cyclicPackages.clear();
		cyclicPackages.addAll(uniquePackages);
	}

}
