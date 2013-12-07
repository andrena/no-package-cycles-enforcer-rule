package de.andrena.tools.nopackagecycles.mock;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import de.andrena.tools.nopackagecycles.DirectoriesWithClasses;

public class EnforcerRuleHelperMock implements EnforcerRuleHelper {

	private File classesDir;
	private File testClassesDir;
	private boolean evaluateThrowsException;
	private final LogMock logMock = new LogMock();

	public LogMock getLogMock() {
		return logMock;
	}

	public void setEvaluateThrowsException(boolean evaluateThrowsException) {
		this.evaluateThrowsException = evaluateThrowsException;
	}

	public void setClassesDir(File targetDir) {
		this.classesDir = targetDir;
	}

	public void setTestClassesDir(File testClassesDir) {
		this.testClassesDir = testClassesDir;
	}
	
	public File alignToBaseDirectory(File arg0) {
		return null;
	}

	public Object evaluate(String variable) throws ExpressionEvaluationException {
		if (evaluateThrowsException) {
			throw new ExpressionEvaluationException("");
		}
		if (DirectoriesWithClasses.MAVEN_PROJECT_BUILD_OUTPUT_DIRECTORY_VAR.equals(variable)) {
			return classesDir.getPath();
		}
		if (DirectoriesWithClasses.MAVEN_PROJECT_BUILD_TEST_OUTPUT_DIRECTORY_VAR.equals(variable)) {
			return testClassesDir.getPath();
		}
		return null;
	}

	public Object getComponent(@SuppressWarnings("rawtypes") Class arg0) throws ComponentLookupException {
		return null;
	}

	public Object getComponent(String arg0) throws ComponentLookupException {
		return null;
	}

	public Object getComponent(String arg0, String arg1) throws ComponentLookupException {
		return null;
	}

	public List<?> getComponentList(String arg0) throws ComponentLookupException {
		return null;
	}

	public Map<?, ?> getComponentMap(String arg0) throws ComponentLookupException {
		return null;
	}

	public PlexusContainer getContainer() {
		return null;
	}

	public Log getLog() {
		return logMock;
	}

}
