/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.failure.message.generator;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.RandomTestUtil;

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
	public void testGetMessageElement() {
		String javaErrorLine = _getJavaErrorLine();

		_testGetMessageElement(
			JenkinsResultsParserUtil.combine(
				"\n[####    ] 50%\r", javaErrorLine, "\n1 error\n"),
			"[####    ] 50%\r" + javaErrorLine);
		_testGetMessageElement(
			JenkinsResultsParserUtil.combine(javaErrorLine, "\n1 error\n"),
			javaErrorLine);

		String taskFailedLine = _getTaskFailedLine();

		_testGetMessageElement(
			JenkinsResultsParserUtil.combine(
				RandomTestUtil.randomString(), "\n", taskFailedLine, "\n"),
			taskFailedLine);

		String whatWentWrongBlock = _getWhatWentWrongBlock();

		_testGetMessageElement(
			JenkinsResultsParserUtil.combine(
				"FAILURE: Build failed with an exception.\n\n",
				whatWentWrongBlock, "\n* Try:\n", RandomTestUtil.randomString(),
				"\n"),
			whatWentWrongBlock.trim());

		String whereBlock = JenkinsResultsParserUtil.combine(
			"* Where:\nBuild file '/opt/dev/", RandomTestUtil.randomString(),
			"/build.gradle' line: 12\n");

		_testGetMessageElement(
			JenkinsResultsParserUtil.combine(
				"FAILURE: Build failed with an exception.\n\n", whereBlock,
				"\n", whatWentWrongBlock),
			whereBlock.trim(), whatWentWrongBlock.trim());

		String lfConsoleText = JenkinsResultsParserUtil.combine(
			RandomTestUtil.randomString(), "\n", javaErrorLine, "\n1 error\n",
			taskFailedLine, "\n");

		String crlfConsoleText = lfConsoleText.replace("\n", "\r\n");

		String crlfText = _getText(crlfConsoleText);

		Assert.assertEquals(
			_getText(lfConsoleText), crlfText.replace("\r", ""));

		_testGetMessageElement("");
		_testGetMessageElement("BUILD FAILED\n/opt/dev/build.xml:12: x\n");
		_testGetMessageElement("BUILD SUCCESSFUL in 3s\n");
		_testGetMessageElement(
			JenkinsResultsParserUtil.combine(
				"\n  [exec] > Task :", RandomTestUtil.randomString(),
				" UP-TO-DATE\n  [exec] ", RandomTestUtil.randomString(),
				" FAILED\n"));
		_testGetMessageElement(
			JenkinsResultsParserUtil.combine(
				"\n> Task :", RandomTestUtil.randomString(), " FAILED\n"));
		_testGetMessageElement(
			JenkinsResultsParserUtil.combine(
				"\n[####    ] 50%\r", taskFailedLine, "\n"));
		_testGetMessageElement(
			JenkinsResultsParserUtil.combine(taskFailedLine, "\n"));
		_testGetMessageElement(RandomTestUtil.randomString());
		_testGetMessageElement(null);
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

	@Test(timeout = 30000)
	public void testGetMessageElementLargeConsole() {
		int length = _MAXIMUM_REGION_SIZE * 5;

		_testGetMessageElement(
			_repeat(
				length / (1024 * 1024),
				"\n  [exec] > Task :" + _repeat(1024 * 1024, "y")));
		_testGetMessageElement(_repeat(length / 15, "[####    ] 50%\r"));
		_testGetMessageElement(
			_repeat(length / 20001, _repeat(20000, " ") + "\n"));
		_testGetMessageElement(
			_repeat(length / 31, "/opt/dev/Foo.java:12: warning: x\n"));
		_testGetMessageElement(
			_repeat(
				length / 42, "  [exec] > Task :foo:compileJava UP-TO-DATE\n"));
	}

	@Test(timeout = 10000)
	public void testGetMessageElementLongLine() {
		Assert.assertNull(
			_gradleTaskFailureMessageGenerator.getMessageElement(
				_getLongLine()));
	}

	@Test
	public void testGetMessageElementMaximumRegionSize() {
		String javaErrorLine = _getJavaErrorLine();

		String javaErrorConsoleText = JenkinsResultsParserUtil.combine(
			javaErrorLine, "\n1 error\n");

		String javaErrorText = _getText(javaErrorConsoleText);

		Assert.assertEquals(
			javaErrorText,
			_getText(
				JenkinsResultsParserUtil.combine(
					_getLines(_MAXIMUM_REGION_SIZE), javaErrorConsoleText)));

		for (int length :
				new int[] {_MAXIMUM_REGION_SIZE, _MAXIMUM_REGION_SIZE + 1}) {

			Assert.assertEquals(
				javaErrorText,
				_getText(
					JenkinsResultsParserUtil.combine(
						_getExactLines(length - javaErrorConsoleText.length()),
						javaErrorConsoleText)));
		}

		String text = _getText(
			JenkinsResultsParserUtil.combine(
				javaErrorConsoleText,
				_getExactLines(
					_MAXIMUM_REGION_SIZE - javaErrorConsoleText.length())));

		Assert.assertEquals(
			javaErrorText.substring(0, javaErrorLine.length()),
			text.substring(0, javaErrorLine.length()));

		String whatWentWrongBlock = _getWhatWentWrongBlock();

		_testGetMessageElement(
			JenkinsResultsParserUtil.combine(
				whatWentWrongBlock, _getLines(_MAXIMUM_REGION_SIZE + 1)),
			whatWentWrongBlock.trim());

		String taskFailedConsoleText = JenkinsResultsParserUtil.combine(
			"\n", _getTaskFailedLine(), "\n");

		Assert.assertEquals(
			_getText(taskFailedConsoleText),
			_getText(
				JenkinsResultsParserUtil.combine(
					_getLines(_MAXIMUM_REGION_SIZE), taskFailedConsoleText)));

		_testGetMessageElement(
			JenkinsResultsParserUtil.combine(
				javaErrorConsoleText, _repeat(_MAXIMUM_REGION_SIZE + 1, "z")));
		_testGetMessageElement(
			JenkinsResultsParserUtil.combine(
				javaErrorLine, "\n",
				_getExactLines(
					_MAXIMUM_REGION_SIZE - (javaErrorLine.length() / 2))));
		_testGetMessageElement(
			JenkinsResultsParserUtil.combine(
				taskFailedConsoleText, _getLines(_MAXIMUM_REGION_SIZE + 1)));

		String consoleText = JenkinsResultsParserUtil.combine(
			RandomTestUtil.randomString(), "\n", javaErrorLine,
			_repeat(_MAXIMUM_REGION_SIZE - javaErrorLine.length() + 5, "z"));

		text = _getText(consoleText);

		Assert.assertTrue(text, text.startsWith(javaErrorLine.substring(5)));
	}

	@Test
	public void testGetMessageElementMultipleFailures() {
		String javaErrorLine = _getJavaErrorLine();
		String taskFailedLine1 = _getTaskFailedLine();
		String taskFailedLine2 = _getTaskFailedLine();
		String whatWentWrongBlock1 = _getWhatWentWrongBlock();
		String whatWentWrongBlock2 = _getWhatWentWrongBlock();

		_testGetMessageElementMultipleFailures(
			JenkinsResultsParserUtil.combine(
				"\n", taskFailedLine1, "\n", _getLines(5000), javaErrorLine,
				"\n", _getLines(5000), taskFailedLine2, "\n", _getLines(5000),
				whatWentWrongBlock1, _getLines(5000), whatWentWrongBlock2),
			taskFailedLine1, whatWentWrongBlock2.trim(), javaErrorLine,
			taskFailedLine2, whatWentWrongBlock1.trim());
		_testGetMessageElementMultipleFailures(
			JenkinsResultsParserUtil.combine(
				javaErrorLine, "\n", _getLines(5000), taskFailedLine1, "\n",
				_getLines(5000), whatWentWrongBlock1, _getLines(5000),
				whatWentWrongBlock2),
			javaErrorLine, whatWentWrongBlock2.trim(), taskFailedLine1,
			whatWentWrongBlock1.trim());
	}

	@Test
	public void testGetMessageElementSnippetSize() {
		String javaErrorLine = _getJavaErrorLine();

		_testGetMessageElementSnippetSize(
			JenkinsResultsParserUtil.combine(
				RandomTestUtil.randomString(), "\n", javaErrorLine,
				_repeat(5000, "z"), "\n"),
			javaErrorLine);
		_testGetMessageElementSnippetSize(
			JenkinsResultsParserUtil.combine(
				javaErrorLine, "\n", _getLines(5000)),
			javaErrorLine);

		String taskFailedLine = _getTaskFailedLine();

		_testGetMessageElementSnippetSize(
			JenkinsResultsParserUtil.combine(
				RandomTestUtil.randomString(), "\n", taskFailedLine,
				_repeat(5000, "z"), "\n"),
			taskFailedLine);

		String whatWentWrongBlock = _getWhatWentWrongBlock();

		_testGetMessageElementSnippetSize(
			JenkinsResultsParserUtil.combine(
				RandomTestUtil.randomString(), "\n", whatWentWrongBlock,
				_repeat(5000, "z"), "\n"),
			"* What went wrong:");
		_testGetMessageElementSnippetSize(
			JenkinsResultsParserUtil.combine(
				whatWentWrongBlock, _getLines(5000)),
			whatWentWrongBlock.trim());
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

	private String _getExactLines(int length) {
		String lines = _getLines(length);

		return lines.substring(0, length - 1) + "\n";
	}

	private String _getJavaErrorLine() {
		return JenkinsResultsParserUtil.combine(
			"/opt/dev/", RandomTestUtil.randomString(), "/Foo.java:",
			String.valueOf(RandomTestUtil.randomInt() & Integer.MAX_VALUE),
			": error: ", RandomTestUtil.randomString());
	}

	private String _getLines(int length) {
		String line = _repeat(99, "y") + "\n";

		return _repeat((length / 100) + 1, line);
	}

	private String _getLongLine() {
		return _repeat(300000, "[####    ] 50%\r");
	}

	private String _getTaskFailedLine() {
		return JenkinsResultsParserUtil.combine(
			"  [exec] > Task :", RandomTestUtil.randomString(),
			":compileJava FAILED");
	}

	private String _getText(String consoleText) {
		Element messageElement =
			_gradleTaskFailureMessageGenerator.getMessageElement(consoleText);

		Assert.assertNotNull(messageElement);

		return messageElement.getText();
	}

	private String _getWhatWentWrongBlock() {
		return JenkinsResultsParserUtil.combine(
			"* What went wrong:\nExecution failed for task ':",
			RandomTestUtil.randomString(), ":compileJava'.\n");
	}

	private String _repeat(int count, String string) {
		StringBuilder sb = new StringBuilder(string.length() * count);

		for (int i = 0; i < count; i++) {
			sb.append(string);
		}

		return sb.toString();
	}

	private void _testGetMessageElement(
		String consoleText, String... expectedTexts) {

		Element messageElement =
			_gradleTaskFailureMessageGenerator.getMessageElement(consoleText);

		String message = _gradleTaskFailureMessageGenerator.getMessage(
			consoleText);

		if (expectedTexts.length == 0) {
			Assert.assertNull(message);
			Assert.assertNull(messageElement);

			return;
		}

		Assert.assertNotNull(message);
		Assert.assertNotNull(messageElement);

		String text = messageElement.getText();

		Assert.assertTrue(text, text.startsWith(expectedTexts[0]));

		for (String expectedText : expectedTexts) {
			Assert.assertTrue(
				message, message.contains(expectedText.replace(">", "&gt;")));
			Assert.assertTrue(text, text.contains(expectedText));
		}
	}

	private void _testGetMessageElementMultipleFailures(
		String consoleText, String expectedFirstText, String expectedLastText,
		String... unexpectedTexts) {

		String text = _getText(consoleText);

		Assert.assertTrue(text, text.contains(expectedLastText));
		Assert.assertTrue(text, text.startsWith(expectedFirstText));

		for (String unexpectedText : unexpectedTexts) {
			Assert.assertFalse(text, text.contains(unexpectedText));
		}
	}

	private void _testGetMessageElementSnippetSize(
		String consoleText, String expectedText) {

		String text = _getText(consoleText);

		Assert.assertTrue(text, text.startsWith(expectedText));

		String trimmedText = text.trim();

		int textLength = trimmedText.length();

		Assert.assertTrue(
			String.valueOf(textLength),
			textLength <=
				BaseFailureMessageGenerator.
					CHARS_CONSOLE_TEXT_SNIPPET_SIZE_MAX);
	}

	private static final String _JAVA_ERROR_LINE =
		"/opt/dev/Foo.java:12: error: cannot find symbol";

	private static final int _MAXIMUM_REGION_SIZE =
		GradleTaskFailureMessageGenerator.MAXIMUM_REGION_SIZE;

	private final GradleTaskFailureMessageGenerator
		_gradleTaskFailureMessageGenerator =
			new GradleTaskFailureMessageGenerator();

}