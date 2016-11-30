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
		DirectoriesWithClasses directories = new DirectoriesWithClasses(helper);
		if (directories.directoriesWithClassesFound()) {
			executePackageCycleCheck(directories);
		} else {
			helper.getLog().info("No directories with classes to check for cycles found.");
		}
	}

	private void executePackageCycleCheck(Iterable<File> directories) throws IOException, EnforcerRuleException {
		JDepend jdepend = createJDepend();
		for (File directory : directories) {
			jdepend.addDirectory(directory.getAbsolutePath());
		}
		jdepend.analyze();
		if (jdepend.containsCycles()) {
			throw new EnforcerRuleException("There are package cycles:" + getPackageCycles(jdepend));
		}
	}

	protected JDepend createJDepend() {
		return new JDepend();
	}

	private String getPackageCycles(JDepend jdepend) {
		Collection<JavaPackage> packages = jdepend.getPackages();
		return new PackageCycleOutput(new ArrayList<JavaPackage>(packages)).getOutput();
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