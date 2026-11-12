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
			"modules-compile"
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

		Element strongElement = (Element)messageElement.selectSingleNode(
			"p/strong");

		Assert.assertEquals(message, strongElement.getText());
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

		Element preElement = (Element)messageElement.selectSingleNode("pre");

		String text = preElement.getText();

		Assert.assertTrue(text.endsWith(message));
		Assert.assertTrue(text.contains(taskFailedLine.trim()));
		Assert.assertFalse(text.contains(trailingLine));

		Element strongElement = (Element)messageElement.selectSingleNode(
			"p/strong");

		Assert.assertEquals(message, strongElement.getText());
	}

	private final PlaywrightSetupFailureMessageGenerator
		_playwrightSetupFailureMessageGenerator =
			new PlaywrightSetupFailureMessageGenerator();

}