package de.andrena.tools.nopackagecycles;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;

import org.apache.maven.enforcer.rule.api.EnforcerRule;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

public class NoPackageCyclesRule implements EnforcerRule {

	public static final String MAVEN_CLASSES_DIR = "classes";
	public static final String MAVEN_PROJECT_BUILD_DIRECTORY_VAR = "${project.build.directory}";

	public void execute(EnforcerRuleHelper helper) throws EnforcerRuleException {
		try {
			executePackageCycleCheckIfNecessary(helper);
		} catch (ExpressionEvaluationException e) {
			throw new EnforcerRuleException("Unable to lookup an expression " + e.getLocalizedMessage(), e);
		} catch (IOException e) {
			throw new EnforcerRuleException("Unable to access target directory " + e.getLocalizedMessage(), e);
		}
	}

	private void executePackageCycleCheckIfNecessary(EnforcerRuleHelper helper) throws ExpressionEvaluationException,
			IOException, EnforcerRuleException {
		File targetDir = new File((String) helper.evaluate(MAVEN_PROJECT_BUILD_DIRECTORY_VAR));
		File classesDir = new File(targetDir, MAVEN_CLASSES_DIR);
		helper.getLog().info("Searching directory " + classesDir.getAbsolutePath() + " for package cycles.");
		if (checkIsNecessary(classesDir)) {
			executePackageCycleCheck(classesDir);
		} else {
			helper.getLog().info("Directory " + classesDir.getAbsolutePath() + " could not be found.");
		}
	}

	private void executePackageCycleCheck(File classesDir) throws IOException, EnforcerRuleException {
		JDepend jdepend = createJDepend();
		jdepend.addDirectory(classesDir.getAbsolutePath());
		jdepend.analyze();
		if (jdepend.containsCycles()) {
			throw new EnforcerRuleException("There are package cycles:" + getPackageCycles(jdepend));
		}
	}

	protected JDepend createJDepend() {
		return new JDepend();
	}

	private String getPackageCycles(JDepend jdepend) {
		@SuppressWarnings("unchecked")
		Collection<JavaPackage> packages = jdepend.getPackages();
		return new PackageCycleOutput(new ArrayList<JavaPackage>(packages)).getOutput();
	}

	private boolean checkIsNecessary(File classesDir) {
		return classesDir.exists();
	}

	public String getCacheId() {
		return "";
	}

	public boolean isCacheable() {
		return false;
	}

	public boolean isResultValid(EnforcerRule arg0) {
		return false;
	}
}