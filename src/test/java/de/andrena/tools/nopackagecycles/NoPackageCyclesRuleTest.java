package de.andrena.tools.nopackagecycles;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
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

	private static final String MAVEN_WAR_PACKAGING = "war";

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
		temporaryFolder.newFolder(NoPackageCyclesRule.MAVEN_CLASSES_DIR);
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
		jdependMock.setContainsCycles(true);
		helper.setTargetDir(temporaryFolder.newFolder());
		rule.execute(helper);
	}

	@Test
	public void execute_checkNotNecessary_PackagingNotJar() throws Exception {
		jdependMock.setContainsCycles(true);
		helper.setPackaging(MAVEN_WAR_PACKAGING);
		expectedException.expect(EnforcerRuleException.class);
		expectedException.expectMessage(containsString("There are package cycles"));
		rule.execute(helper);
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
	}

	@Test
	public void execute_ContainsCycles() throws Exception {
		jdependMock.setContainsCycles(true);
		expectedException.expect(EnforcerRuleException.class);
		expectedException.expectMessage(containsString("There are package cycles"));
		rule.execute(helper);
	}
}
