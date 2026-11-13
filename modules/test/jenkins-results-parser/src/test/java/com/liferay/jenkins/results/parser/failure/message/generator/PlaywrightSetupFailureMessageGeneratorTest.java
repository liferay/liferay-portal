/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.failure.message.generator;

import com.liferay.jenkins.results.parser.Build;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;

import java.util.Properties;

import org.dom4j.Element;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class PlaywrightSetupFailureMessageGeneratorTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"liferay.jenkins.plugin.op.connect.ignored.values", "");

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);
	}

	@Test
	public void testGetMessageElement() {
		_testGetMessageElement(
			JenkinsResultsParserUtil.combine(
				"Duplicate OSGi modules found for ",
				RandomTestUtil.randomString(), ":"));

		String osgiModulesListFile = RandomTestUtil.randomString();

		_testGetMessageElement(
			JenkinsResultsParserUtil.combine(
				"Unable to deploy OSGi module ", RandomTestUtil.randomString(),
				" in ", osgiModulesListFile, "."));
		_testGetMessageElement(
			JenkinsResultsParserUtil.combine(
				"Unable to find OSGi module ", RandomTestUtil.randomString(),
				" in ", osgiModulesListFile, "."));

		Assert.assertNull(
			_playwrightSetupFailureMessageGenerator.getMessageElement(""));
		Assert.assertNull(
			_playwrightSetupFailureMessageGenerator.getMessageElement(
				(String)null));
		Assert.assertNull(
			_playwrightSetupFailureMessageGenerator.getMessageElement(
				JenkinsResultsParserUtil.combine(
					"  [exec] Deploying OSGi modules in ", osgiModulesListFile,
					".\n  [exec] > Task :", RandomTestUtil.randomString(),
					":compileJava FAILED\n")));
	}

	@Test
	public void testGetMessageElementBuild() {
		Build build = Mockito.mock(Build.class);

		String message = JenkinsResultsParserUtil.combine(
			"Unable to deploy OSGi module ", RandomTestUtil.randomString(),
			" in ", RandomTestUtil.randomString(), ".");

		Mockito.when(
			build.getConsoleText()
		).thenReturn(
			"  [exec] " + message + "\n"
		);

		Mockito.when(
			build.getJobVariant()
		).thenReturn(
			RandomTestUtil.randomString()
		);

		Assert.assertNull(
			_playwrightSetupFailureMessageGenerator.getMessageElement(build));

		Mockito.when(
			build.getJobVariant()
		).thenReturn(
			"playwright-js-tomcat101-postgresql163"
		);

		Element messageElement =
			_playwrightSetupFailureMessageGenerator.getMessageElement(build);

		Element pElement = messageElement.element("p");

		Element strongElement = pElement.element("strong");

		Assert.assertEquals(message, strongElement.getText());
	}

	@Test
	public void testGetMessageElementDuplicateOSGiModules() {
		_testGetMessageElementDuplicateOSGiModules("     [exec] ");
		_testGetMessageElementDuplicateOSGiModules("");
	}

	private void _testGetMessageElement(String message) {
		String taskFailedLine = JenkinsResultsParserUtil.combine(
			"  [exec] > Task :", RandomTestUtil.randomString(),
			":compileJava FAILED");
		String trailingLine = RandomTestUtil.randomString();

		String consoleText = JenkinsResultsParserUtil.combine(
			RandomTestUtil.randomString(), "\n", taskFailedLine, "\n  [exec] ",
			message, "\n", trailingLine, "\n");

		Assert.assertEquals(
			message,
			_playwrightSetupFailureMessageGenerator.getMessage(consoleText));

		Element messageElement =
			_playwrightSetupFailureMessageGenerator.getMessageElement(
				consoleText);

		Element preElement = messageElement.element("pre");

		String text = preElement.getText();

		Assert.assertTrue(text.endsWith(message));
		Assert.assertTrue(text.contains(taskFailedLine.trim()));
		Assert.assertFalse(text.contains(trailingLine));

		Element pElement = messageElement.element("p");

		Element strongElement = pElement.element("strong");

		Assert.assertEquals(message, strongElement.getText());
	}

	private void _testGetMessageElementDuplicateOSGiModules(String linePrefix) {
		String osgiModuleName = RandomTestUtil.randomString();

		String appsOSGiModuleDirName = JenkinsResultsParserUtil.combine(
			"apps/", RandomTestUtil.randomString(), "/", osgiModuleName);
		String dxpAppsOSGiModuleDirName = JenkinsResultsParserUtil.combine(
			"dxp/apps/", RandomTestUtil.randomString(), "/", osgiModuleName);
		String message = JenkinsResultsParserUtil.combine(
			"Duplicate OSGi modules found for ", osgiModuleName, ":");
		String replaceLine = JenkinsResultsParserUtil.combine(
			"Replace \"", osgiModuleName, "\" in ",
			RandomTestUtil.randomString(), " with one of the following:");

		String trailingLine = RandomTestUtil.randomString();

		Element messageElement =
			_playwrightSetupFailureMessageGenerator.getMessageElement(
				JenkinsResultsParserUtil.combine(
					RandomTestUtil.randomString(), "\n", linePrefix, message,
					"\n", linePrefix, "/", RandomTestUtil.randomString(), "/",
					appsOSGiModuleDirName, "\n", linePrefix, "/",
					RandomTestUtil.randomString(), "/",
					dxpAppsOSGiModuleDirName, "\n", linePrefix, replaceLine,
					"\n", linePrefix, appsOSGiModuleDirName, "\n", linePrefix,
					dxpAppsOSGiModuleDirName, "\n", linePrefix, trailingLine,
					"\n"));

		Element preElement = messageElement.element("pre");

		String text = preElement.getText();

		Assert.assertTrue(
			text.endsWith(
				JenkinsResultsParserUtil.combine(
					replaceLine, "\n", linePrefix, appsOSGiModuleDirName, "\n",
					linePrefix, dxpAppsOSGiModuleDirName)));
		Assert.assertFalse(text.contains(trailingLine));

		Element pElement = messageElement.element("p");

		Element strongElement = pElement.element("strong");

		Assert.assertEquals(message, strongElement.getText());
	}

	private final PlaywrightSetupFailureMessageGenerator
		_playwrightSetupFailureMessageGenerator =
			new PlaywrightSetupFailureMessageGenerator();

}