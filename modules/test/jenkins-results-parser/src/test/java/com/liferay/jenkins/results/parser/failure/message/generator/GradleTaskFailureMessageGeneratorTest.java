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
				"Starting build\n" + _JAVA_ERROR_LINE +
					"\n  symbol: class Bar\n1 error\n");

		Assert.assertNotNull(messageElement);

		String text = messageElement.getText();

		Assert.assertTrue(text, text.startsWith(_JAVA_ERROR_LINE));
	}

	@Test(timeout = 10000)
	public void testGetMessageElementJavaErrorAfterLongLine() {
		Element messageElement =
			_gradleTaskFailureMessageGenerator.getMessageElement(
				_getLongLine() + "\n" + _JAVA_ERROR_LINE + "\n1 error\n");

		Assert.assertNotNull(messageElement);

		String text = messageElement.getText();

		Assert.assertTrue(text, text.startsWith(_JAVA_ERROR_LINE));
	}

	@Test
	public void testGetMessageElementJavaErrorBeforeScanSizeMax() {
		Assert.assertNull(
			_gradleTaskFailureMessageGenerator.getMessageElement(
				_JAVA_ERROR_LINE + "\n" +
					_getLines(_CHARS_CONSOLE_TEXT_SCAN_SIZE_MAX + 1)));
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
			sb.append(_repeat(" ", 20000));
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
		String line = _repeat("y", 99) + "\n";

		return _repeat(line, (length / 100) + 1);
	}

	private String _getLongLine() {
		return _repeat("[####    ] 50%\r", 300000);
	}

	private String _repeat(String string, int count) {
		StringBuilder sb = new StringBuilder(string.length() * count);

		for (int i = 0; i < count; i++) {
			sb.append(string);
		}

		return sb.toString();
	}

	private static final int _CHARS_CONSOLE_TEXT_SCAN_SIZE_MAX =
		1024 * 1024 * 5;

	private static final String _JAVA_ERROR_LINE =
		"/opt/dev/Foo.java:12: error: cannot find symbol";

	private final GradleTaskFailureMessageGenerator
		_gradleTaskFailureMessageGenerator =
			new GradleTaskFailureMessageGenerator();

}