package de.andrena.tools.nopackagecycles;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jdepend.framework.JavaPackage;

public class PackageCycleCollector {

	public Set<JavaPackage> collectCycles(JavaPackage javaPackage) {
		return collectCycles(javaPackage, new ArrayList<JavaPackage>());
	}

	private Set<JavaPackage> collectCycles(JavaPackage javaPackage, List<JavaPackage> visitedPackages) {
		if (visitedPackages.contains(javaPackage)) {
			return getPackageCycle(javaPackage, visitedPackages);
		}
		visitedPackages.add(javaPackage);
		return collectAllDependencyCycles(javaPackage, visitedPackages);
	}

	private Set<JavaPackage> collectAllDependencyCycles(JavaPackage javaPackage, List<JavaPackage> visitedPackages) {
		Set<JavaPackage> allCycles = new HashSet<JavaPackage>();
		for (JavaPackage dependencyPackage : getEfferents(javaPackage)) {
			Set<JavaPackage> dependencyPackageCycle = collectCycles(dependencyPackage, new ArrayList<JavaPackage>(
					visitedPackages));
			if (dependencyPackageCycle.contains(javaPackage)) {
				allCycles.addAll(dependencyPackageCycle);
				visitedPackages.addAll(dependencyPackageCycle);
			}
		}
		return allCycles;
	}

	@SuppressWarnings("unchecked")
	private Collection<JavaPackage> getEfferents(JavaPackage javaPackage) {
		return javaPackage.getEfferents();
	}

	private Set<JavaPackage> getPackageCycle(JavaPackage javaPackage, List<JavaPackage> visitedPackages) {
		Set<JavaPackage> packageCycle = new HashSet<JavaPackage>();
		boolean includeFollowingPackages = false;
		for (JavaPackage visitedPackage : visitedPackages) {
			if (visitedPackage.equals(javaPackage)) {
				includeFollowingPackages = true;
			}
			if (includeFollowingPackages) {
				packageCycle.add(visitedPackage);
			}
		}
		return packageCycle;
	}

}
