package de.andrena.tools.nopackagecycles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.util.List;

import jdepend.framework.JDepend;

import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import de.andrena.tools.nopackagecycles.mock.EnforcerRuleHelperMock;
import de.andrena.tools.nopackagecycles.mock.JDependMock;

public class NoPackageCyclesRuleTest {

	private class NoPackageCyclesRuleMock extends NoPackageCyclesRule {
		@Override
		protected JDepend createJDepend() {
			return jdependMock;
		}
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private NoPackageCyclesRule rule;
	private JDependMock jdependMock;
	private EnforcerRuleHelperMock helper;

	@Before
	public void setUp() throws Exception {
		jdependMock = new JDependMock();
		rule = new NoPackageCyclesRuleMock();
		helper = new EnforcerRuleHelperMock();
		helper.setTargetDir(temporaryFolder.getRoot());
	}

	@Test
	public void cacheId_IsEmpty() throws Exception {
		assertThat(rule.getCacheId(), is(""));
	}

	@Test
	public void isNot_cacheable() {
		assertThat(rule.isCacheable(), is(false));
	}

	@Test
	public void result_IsNotValid() {
		assertThat(rule.isResultValid(null), is(false));
	}

	@Test
	public void execute_checkNotNecessary_ClassesDirNotFound() throws Exception {
		File nonExistentFolder = new File(temporaryFolder.getRoot(), "non-existent-classes-dir");
		helper.setTargetDir(nonExistentFolder);
		rule.execute(helper);
		List<String> infoLogs = helper.getLogMock().getInfo();
		assertThat(infoLogs, hasSize(2));
		assertSearchingInfo(nonExistentFolder, infoLogs);
		assertThat(infoLogs.get(1), is("Directory " + nonExistentFolder.getAbsolutePath() + " could not be found."));
	}

	@Test
	public void execute_MavenEvaluationFailed_ThrowsException() throws Exception {
		helper.setEvaluateThrowsException(true);
		expectedException.expect(EnforcerRuleException.class);
		expectedException.expectMessage(containsString("Unable to lookup an expression"));
		rule.execute(helper);
	}

	@Test
	public void execute_JdependAddDirectoryFailed_ThrowsException() throws Exception {
		jdependMock.setAddDirectoryThrowsException(true);
		expectedException.expect(EnforcerRuleException.class);
		expectedException.expectMessage(containsString("Unable to access target directory"));
		rule.execute(helper);
	}

	@Test
	public void execute_ContainsNoCycles() throws Exception {
		rule.execute(helper);
		List<String> infoLogs = helper.getLogMock().getInfo();
		assertThat(infoLogs, hasSize(1));
		assertSearchingInfo(temporaryFolder.getRoot(), infoLogs);
	}

	@Test
	public void execute_ContainsCycles() throws Exception {
		jdependMock.setContainsCycles(true);
		expectedException.expect(EnforcerRuleException.class);
		expectedException.expectMessage(containsString("There are package cycles"));
		rule.execute(helper);
	}

	private void assertSearchingInfo(File projectDirectory, List<String> infoLogs) {
		assertThat(infoLogs.get(0), is("Searching directory " + projectDirectory.getAbsolutePath()
				+ " for package cycles."));
	}
}
