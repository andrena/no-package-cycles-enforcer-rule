package de.andrena.tools.nopackagecycles.comparator;

import java.util.Comparator;

import jdepend.framework.JavaClass;

public enum JavaClassNameComparator implements Comparator<JavaClass> {
	INSTANCE;

	public int compare(JavaClass class1, JavaClass class2) {
		return class1.getName().compareTo(class2.getName());
	}
}