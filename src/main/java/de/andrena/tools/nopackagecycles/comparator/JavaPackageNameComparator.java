package de.andrena.tools.nopackagecycles.comparator;

import java.util.Comparator;

import jdepend.framework.JavaPackage;

public enum JavaPackageNameComparator implements Comparator<JavaPackage> {
	INSTANCE;

	public int compare(JavaPackage package1, JavaPackage package2) {
		return package1.getName().compareTo(package2.getName());
	}
}