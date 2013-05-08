package de.andrena.tools.nopackagecycles.mock;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;

import de.andrena.tools.nopackagecycles.NoPackageCyclesRule;

public class EnforcerRuleHelperMock implements EnforcerRuleHelper {

	protected String packaging = NoPackageCyclesRule.MAVEN_JAR_PACKAGING;
	private File targetDir;
	private boolean evaluateThrowsException;

	public void setEvaluateThrowsException(boolean evaluateThrowsException) {
		this.evaluateThrowsException = evaluateThrowsException;
	}

	public void setTargetDir(File targetDir) {
		this.targetDir = targetDir;
	}

	public void setPackaging(String packaging) {
		this.packaging = packaging;
	}

	public File alignToBaseDirectory(File arg0) {
		return null;
	}

	public Object evaluate(String variable) throws ExpressionEvaluationException {
		if (evaluateThrowsException) {
			throw new ExpressionEvaluationException("");
		}
		if (NoPackageCyclesRule.MAVEN_PROJECT_VAR.equals(variable)) {
			return new MavenProject() {
				@Override
				public String getPackaging() {
					return packaging;
				}
			};
		} else if (NoPackageCyclesRule.MAVEN_PROJECT_BUILD_DIRECTORY_VAR.equals(variable)) {
			return targetDir.getPath();
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
		return null;
	}

}
