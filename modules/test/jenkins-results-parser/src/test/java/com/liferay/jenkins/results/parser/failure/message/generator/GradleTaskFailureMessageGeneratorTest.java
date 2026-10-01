/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.failure.message.generator;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.util.Properties;

import org.dom4j.Element;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Michael Hashimoto
 */
public class GradleTaskFailureMessageGeneratorTest
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
	public void testGetMessageElementJavaError() {
		Element messageElement =
			_gradleTaskFailureMessageGenerator.getMessageElement(
				JenkinsResultsParserUtil.combine(
					"Starting build\n", _JAVA_ERROR_LINE,
					"\n  symbol: class Bar\n1 error\n"));

		Assert.assertNotNull(messageElement);

		String text = messageElement.getText();

		Assert.assertTrue(text, text.startsWith(_JAVA_ERROR_LINE));
	}

	@Test(timeout = 10000)
	public void testGetMessageElementJavaErrorAfterLongLine() {
		Element messageElement =
			_gradleTaskFailureMessageGenerator.getMessageElement(
				JenkinsResultsParserUtil.combine(
					_getLongLine(), "\n", _JAVA_ERROR_LINE, "\n1 error\n"));

		Assert.assertNotNull(messageElement);

		String text = messageElement.getText();

		Assert.assertTrue(text, text.startsWith(_JAVA_ERROR_LINE));
	}

	@Test
	public void testGetMessageElementJavaErrorBeforeMaximumRegionSize() {
		Assert.assertNull(
			_gradleTaskFailureMessageGenerator.getMessageElement(
				JenkinsResultsParserUtil.combine(
					_JAVA_ERROR_LINE, "\n",
					_getLines(_MAXIMUM_REGION_SIZE + 1))));
	}

	@Test(timeout = 10000)
	public void testGetMessageElementLongLine() {
		Assert.assertNull(
			_gradleTaskFailureMessageGenerator.getMessageElement(
				_getLongLine()));
	}

	@Test(timeout = 10000)
	public void testGetMessageElementTaskFailedAfterLongWhitespace() {
		String taskFailedLine = "  [exec] > Task :apps:foo:compileJava FAILED";

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < 250; i++) {
			sb.append("\n");
			sb.append(_repeat(20000, " "));
		}

		sb.append("\n");
		sb.append(taskFailedLine);
		sb.append("\n");

		Element messageElement =
			_gradleTaskFailureMessageGenerator.getMessageElement(sb.toString());

		Assert.assertNotNull(messageElement);

		String text = messageElement.getText();

		Assert.assertTrue(text, text.contains(taskFailedLine.trim()));
	}

	private String _getLines(int length) {
		String line = _repeat(99, "y") + "\n";

		return _repeat((length / 100) + 1, line);
	}

	private String _getLongLine() {
		return _repeat(300000, "[####    ] 50%\r");
	}

	private String _repeat(int count, String string) {
		StringBuilder sb = new StringBuilder(string.length() * count);

		for (int i = 0; i < count; i++) {
			sb.append(string);
		}

		return sb.toString();
	}

	private static final String _JAVA_ERROR_LINE =
		"/opt/dev/Foo.java:12: error: cannot find symbol";

	private static final int _MAXIMUM_REGION_SIZE =
		GradleTaskFailureMessageGenerator.MAXIMUM_REGION_SIZE;

	private final GradleTaskFailureMessageGenerator
		_gradleTaskFailureMessageGenerator =
			new GradleTaskFailureMessageGenerator();

}