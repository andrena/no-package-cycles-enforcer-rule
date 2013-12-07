package de.andrena.tools.nopackagecycles;

import static java.util.Collections.unmodifiableList;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.enforcer.rule.api.EnforcerRuleHelper;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;

public class DirectoriesWithClasses implements Iterable<File>{

	public static final String MAVEN_PROJECT_BUILD_OUTPUT_DIRECTORY_VAR = "${project.build.outputDirectory}";
	public static final String MAVEN_PROJECT_BUILD_TEST_OUTPUT_DIRECTORY_VAR = "${project.build.testOutputDirectory}";

	private final List<File> directories = new LinkedList<File>();
	
	public DirectoriesWithClasses(EnforcerRuleHelper helper) throws ExpressionEvaluationException {
		addDirectoryIfExists(helper, MAVEN_PROJECT_BUILD_OUTPUT_DIRECTORY_VAR);
		addDirectoryIfExists(helper, MAVEN_PROJECT_BUILD_TEST_OUTPUT_DIRECTORY_VAR);
	}

	private void addDirectoryIfExists(EnforcerRuleHelper helper, String variable)
			throws ExpressionEvaluationException {
		File directory = new File((String) helper.evaluate(variable));
		if(directory.exists()) {
			helper.getLog().info("Adding directory " + directory.getAbsolutePath() + " for package cycles search.");
			directories.add(directory);
		} else {			
			helper.getLog().info("Directory " + directory.getAbsolutePath() + " could not be found.");
		}
	}

	public boolean directoriesWithClassesFound() {
		return !directories.isEmpty();
	}

	public Iterator<File> iterator() {
		return unmodifiableList(directories).iterator();
	}
}
