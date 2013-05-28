package de.andrena.tools.nopackagecycles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.maven.enforcer.rule.api.EnforcerRuleException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.andrena.tools.nopackagecycles.mock.EnforcerRuleHelperMock;

public class NoPackageCyclesRuleIntegrationTest {
	private static final URL TARGET_FOLDER = Thread.currentThread().getContextClassLoader().getResource("junit-target");
	private static final URL EXPECTED_OUTPUT = Thread.currentThread().getContextClassLoader()
			.getResource("junit-expected-output.txt");

	private NoPackageCyclesRule rule;
	private EnforcerRuleHelperMock helper;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Before
	public void setUp() throws Exception {
		rule = new NoPackageCyclesRule();
		helper = new EnforcerRuleHelperMock();
		helper.setTargetDir(new File(TARGET_FOLDER.toURI()));
	}

	@Test
	public void integrationTest() throws Exception {
		try {
			rule.execute(helper);
			fail("expected EnforcerRuleException");
		} catch (EnforcerRuleException e) {
			assertEquals(getExpectedOutput(), e.getMessage());
		}
	}

	private String getExpectedOutput() throws IOException {
		return IOUtils.toString(EXPECTED_OUTPUT.openStream()).replaceAll("\r", "");
	}
}
