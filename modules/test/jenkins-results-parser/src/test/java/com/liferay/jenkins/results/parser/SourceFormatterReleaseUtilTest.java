/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class SourceFormatterReleaseUtilTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		_artifactPropertiesFile = new File(
			temporaryFolder.getRoot(), _PATH_ARTIFACT_PROPERTIES);

		JenkinsResultsParserUtil.write(
			_artifactPropertiesFile,
			JenkinsResultsParserUtil.combine(
				"artifact.git.id=", _ARTIFACT_SHA, "\nartifact.url=",
				_getArtifactURL()));

		_ivyXMLFile = new File(temporaryFolder.getRoot(), _PATH_IVY_XML);

		JenkinsResultsParserUtil.write(
			_ivyXMLFile,
			_getIvyXML(
				"<dependency name=\"com.liferay.portal.kernel\" rev=\"" +
					RandomTestUtil.randomString() + "\" />",
				"<dependency name=\"com.liferay.source.formatter\" rev=\"" +
					_VERSION + "\" />"));
	}

	@Test
	public void testGetIvyVersion() throws Exception {
		Assert.assertEquals(
			_VERSION,
			SourceFormatterReleaseUtil.getIvyVersion(
				temporaryFolder.getRoot()));
	}

	@Test
	public void testGetIvyVersionWithInvalidIvyXML() throws Exception {
		JenkinsResultsParserUtil.write(_ivyXMLFile, "<ivy-module>");

		Assert.assertNull(
			SourceFormatterReleaseUtil.getIvyVersion(
				temporaryFolder.getRoot()));

		JenkinsResultsParserUtil.write(_ivyXMLFile, "<ivy-module />");

		Assert.assertNull(
			SourceFormatterReleaseUtil.getIvyVersion(
				temporaryFolder.getRoot()));
	}

	@Test
	public void testGetIvyVersionWithoutIvyXML() {
		_ivyXMLFile.delete();

		Assert.assertNull(
			SourceFormatterReleaseUtil.getIvyVersion(
				temporaryFolder.getRoot()));
	}

	@Test
	public void testGetIvyVersionWithoutSourceFormatterDependency()
		throws Exception {

		JenkinsResultsParserUtil.write(
			_ivyXMLFile,
			_getIvyXML(
				"<dependency name=\"com.liferay.portal.kernel\" rev=\"" +
					RandomTestUtil.randomString() + "\" />"));

		Assert.assertNull(
			SourceFormatterReleaseUtil.getIvyVersion(
				temporaryFolder.getRoot()));
	}

	@Test
	public void testIsReleasedSourceMatch() throws Exception {
		GitUtil.ExecutionResult shaExecutionResult = _getExecutionResult(
			0, _ARTIFACT_PROPERTIES_SHA);

		_testIsReleasedSourceMatch(
			_getExecutionResult(
				0,
				"modules/util/source-formatter/" +
					RandomTestUtil.randomString()),
			false, shaExecutionResult, shaExecutionResult);
		_testIsReleasedSourceMatch(
			_getExecutionResult(1, ""), false, shaExecutionResult,
			shaExecutionResult);

		GitUtil.ExecutionResult emptyExecutionResult = _getExecutionResult(
			0, "");

		_testIsReleasedSourceMatch(
			emptyExecutionResult, false,
			_getExecutionResult(1, _ARTIFACT_PROPERTIES_SHA),
			shaExecutionResult);
		_testIsReleasedSourceMatch(
			emptyExecutionResult, false, emptyExecutionResult,
			shaExecutionResult);
		_testIsReleasedSourceMatch(
			emptyExecutionResult, false, shaExecutionResult,
			_getExecutionResult(0, RandomTestUtil.randomSHA()));
		_testIsReleasedSourceMatch(
			emptyExecutionResult, false, shaExecutionResult,
			_getExecutionResult(
				0,
				_ARTIFACT_PROPERTIES_SHA + "\n" + RandomTestUtil.randomSHA()));
		_testIsReleasedSourceMatch(
			emptyExecutionResult, false, shaExecutionResult,
			_getExecutionResult(1, _ARTIFACT_PROPERTIES_SHA));
		_testIsReleasedSourceMatch(
			emptyExecutionResult, false, shaExecutionResult,
			emptyExecutionResult);
		_testIsReleasedSourceMatch(
			emptyExecutionResult, true, shaExecutionResult, shaExecutionResult);
	}

	@Test
	public void testIsReleasedSourceMatchWithException() {
		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		Mockito.when(
			gitWorkingDirectory.getWorkingDirectory()
		).thenThrow(
			new RuntimeException()
		);

		Assert.assertFalse(
			SourceFormatterReleaseUtil.isReleasedSourceMatch(
				gitWorkingDirectory, RandomTestUtil.randomString()));
	}

	@Test
	public void testIsReleasedSourceMatchWithInvalidArtifactURL()
		throws Exception {

		JenkinsResultsParserUtil.write(
			_artifactPropertiesFile,
			JenkinsResultsParserUtil.combine(
				"artifact.git.id=", _ARTIFACT_SHA, "\nartifact.url=",
				"https://repository-cdn.liferay.com/nexus/content",
				"/repositories/liferay-public-releases/com/liferay",
				"/com.liferay.source.formatter/", _VERSION,
				"/com.liferay.source.formatter-", _VERSION, ".zip"));

		_assertNotReleasedSourceMatch();

		JenkinsResultsParserUtil.write(
			_artifactPropertiesFile, "artifact.git.id=" + _ARTIFACT_SHA);

		_assertNotReleasedSourceMatch();
	}

	@Test
	public void testIsReleasedSourceMatchWithIvyVersionMismatch()
		throws Exception {

		JenkinsResultsParserUtil.write(
			_ivyXMLFile,
			_getIvyXML(
				"<dependency name=\"com.liferay.source.formatter\" rev=\"" +
					RandomTestUtil.randomString() + "\" />"));

		_assertNotReleasedSourceMatch();

		_ivyXMLFile.delete();

		_assertNotReleasedSourceMatch();
	}

	@Test
	public void testIsReleasedSourceMatchWithMissingArtifactGitId()
		throws Exception {

		JenkinsResultsParserUtil.write(
			_artifactPropertiesFile, "artifact.url=" + _getArtifactURL());

		_assertNotReleasedSourceMatch();

		JenkinsResultsParserUtil.write(
			_artifactPropertiesFile,
			"artifact.git.id=\nartifact.url=" + _getArtifactURL());

		_assertNotReleasedSourceMatch();
	}

	@Test
	public void testIsReleasedSourceMatchWithoutArtifactProperties()
		throws Exception {

		_artifactPropertiesFile.delete();

		_assertNotReleasedSourceMatch();
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private void _assertNotReleasedSourceMatch() throws Exception {
		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		Mockito.when(
			gitWorkingDirectory.getWorkingDirectory()
		).thenReturn(
			temporaryFolder.getRoot()
		);

		Assert.assertFalse(
			SourceFormatterReleaseUtil.isReleasedSourceMatch(
				gitWorkingDirectory, RandomTestUtil.randomString()));

		Mockito.verify(
			gitWorkingDirectory, Mockito.never()
		).executeBashCommands(
			Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong(),
			Mockito.anyString()
		);
	}

	private String _getArtifactURL() {
		return JenkinsResultsParserUtil.combine(
			"https://repository-cdn.liferay.com/nexus/content/repositories",
			"/liferay-public-releases/com/liferay/com.liferay.source.formatter",
			"/", _VERSION, "/com.liferay.source.formatter-", _VERSION, ".jar");
	}

	private GitUtil.ExecutionResult _getExecutionResult(
		int exitValue, String standardOut) {

		return new GitUtil.ExecutionResult(
			exitValue, RandomTestUtil.randomString(), standardOut);
	}

	private String _getIvyXML(String... dependencyXMLs) {
		return JenkinsResultsParserUtil.combine(
			"<ivy-module><dependencies>", String.join("", dependencyXMLs),
			"</dependencies></ivy-module>");
	}

	private void _testIsReleasedSourceMatch(
			GitUtil.ExecutionResult diffExecutionResult, boolean expected,
			GitUtil.ExecutionResult logExecutionResult,
			GitUtil.ExecutionResult revListExecutionResult)
		throws Exception {

		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		String ref = RandomTestUtil.randomString();

		Mockito.when(
			gitWorkingDirectory.executeBashCommands(
				Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.eq(
					JenkinsResultsParserUtil.combine(
						"git diff --name-only ", _ARTIFACT_PROPERTIES_SHA, " ",
						ref, " -- modules/util/source-formatter")))
		).thenReturn(
			diffExecutionResult
		);

		Mockito.when(
			gitWorkingDirectory.executeBashCommands(
				Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.eq(
					JenkinsResultsParserUtil.combine(
						"git log --format=%H --max-count=1 ", ref, " -- ",
						_PATH_ARTIFACT_PROPERTIES)))
		).thenReturn(
			logExecutionResult
		);

		Mockito.when(
			gitWorkingDirectory.executeBashCommands(
				Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.eq(
					JenkinsResultsParserUtil.combine(
						"git rev-list ", _ARTIFACT_SHA, "..",
						_ARTIFACT_PROPERTIES_SHA,
						" -- modules/util/source-formatter")))
		).thenReturn(
			revListExecutionResult
		);

		Mockito.when(
			gitWorkingDirectory.getWorkingDirectory()
		).thenReturn(
			temporaryFolder.getRoot()
		);

		Assert.assertEquals(
			expected,
			SourceFormatterReleaseUtil.isReleasedSourceMatch(
				gitWorkingDirectory, ref));
	}

	private static final String _ARTIFACT_PROPERTIES_SHA =
		RandomTestUtil.randomSHA();

	private static final String _ARTIFACT_SHA = RandomTestUtil.randomSHA();

	private static final String _PATH_ARTIFACT_PROPERTIES =
		"modules/.releng/util/source-formatter/artifact.properties";

	private static final String _PATH_IVY_XML =
		"tools/sdk/dependencies/com.liferay.source.formatter/ivy.xml";

	private static final String _VERSION =
		"1.0." + String.valueOf(Math.abs(RandomTestUtil.randomLong() % 10000));

	private File _artifactPropertiesFile;
	private File _ivyXMLFile;

}