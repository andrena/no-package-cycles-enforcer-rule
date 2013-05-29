package de.andrena.tools.nopackagecycles.comparator;

import java.util.Comparator;
import java.util.List;

import jdepend.framework.JavaPackage;

public enum JavaPackageListComparator implements Comparator<List<JavaPackage>> {
	INSTANCE;

	public int compare(List<JavaPackage> package1, List<JavaPackage> package2) {
		return package1.get(0).getName().compareTo(package2.get(0).getName());
	}
}