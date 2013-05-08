package de.andrena.tools.nopackagecycles.mock;

import java.io.IOException;

import jdepend.framework.JDepend;

public class JDependMock extends JDepend {
	private boolean addDirectoryThrowsException;
	private boolean containsCycles;

	public void setAddDirectoryThrowsException(boolean addDirectoryThrowsException) {
		this.addDirectoryThrowsException = addDirectoryThrowsException;
	}

	@Override
	public void addDirectory(String name) throws IOException {
		if (addDirectoryThrowsException) {
			throw new IOException();
		}
		super.addDirectory(name);
	}

	@Override
	public boolean containsCycles() {
		return containsCycles;
	}

	public void setContainsCycles(boolean containsCycles) {
		this.containsCycles = containsCycles;
	}
}
