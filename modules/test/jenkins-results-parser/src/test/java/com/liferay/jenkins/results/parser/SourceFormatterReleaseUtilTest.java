/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;

/**
 * @author Calum Ragan
 */
public class SourceFormatterReleaseUtilTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testGetIvyVersion() throws Exception {
		_writeIvyXML("1.0.1643");

		Assert.assertEquals(
			"1.0.1643",
			SourceFormatterReleaseUtil.getIvyVersion(
				temporaryFolder.getRoot()));
	}

	@Test
	public void testGetIvyVersionWithoutIvyXML() {
		Assert.assertNull(
			SourceFormatterReleaseUtil.getIvyVersion(
				temporaryFolder.getRoot()));
	}

	@Test
	public void testIsReleasedSourceMatchWithDiffFailure() throws Exception {
		_writeIvyXML("1.0.1643");
		_writeArtifactProperties("1.0.1643");

		Assert.assertFalse(_isReleasedSourceMatch(128, ""));
	}

	@Test
	public void testIsReleasedSourceMatchWithEmptyDiff() throws Exception {
		_writeIvyXML("1.0.1643");
		_writeArtifactProperties("1.0.1643");

		Assert.assertTrue(_isReleasedSourceMatch(0, ""));
	}

	@Test
	public void testIsReleasedSourceMatchWithIvyVersionMismatch()
		throws Exception {

		_writeIvyXML("1.0.1642");
		_writeArtifactProperties("1.0.1643");

		Assert.assertFalse(_isReleasedSourceMatch(0, ""));
	}

	@Test
	public void testIsReleasedSourceMatchWithMissingArtifactGitId()
		throws Exception {

		_writeIvyXML("1.0.1643");

		JenkinsResultsParserUtil.write(
			new File(
				temporaryFolder.getRoot(),
				"modules/.releng/util/source-formatter/artifact.properties"),
			JenkinsResultsParserUtil.combine(
				"artifact.url=", _getArtifactURL("1.0.1643")));

		Assert.assertFalse(_isReleasedSourceMatch(0, ""));
	}

	@Test
	public void testIsReleasedSourceMatchWithoutArtifactProperties()
		throws Exception {

		_writeIvyXML("1.0.1643");

		Assert.assertFalse(_isReleasedSourceMatch(0, ""));
	}

	@Test
	public void testIsReleasedSourceMatchWithSourceChange() throws Exception {
		_writeIvyXML("1.0.1643");
		_writeArtifactProperties("1.0.1643");

		Assert.assertFalse(
			_isReleasedSourceMatch(0, _SOURCE_CHANGE_PATH + "\n"));
	}

	@Test
	public void testIsReleasedSourceMatchWithSourceChangeAndVersionBump()
		throws Exception {

		_writeIvyXML("1.0.1643");
		_writeArtifactProperties("1.0.1643");

		Assert.assertFalse(
			_isReleasedSourceMatch(
				0,
				"modules/util/source-formatter/bnd.bnd\n" +
					_SOURCE_CHANGE_PATH + "\n"));
	}

	@Test
	public void testIsReleasedSourceMatchWithVersionBump() throws Exception {
		_writeIvyXML("1.0.1643");
		_writeArtifactProperties("1.0.1643");

		Assert.assertTrue(
			_isReleasedSourceMatch(
				0,
				JenkinsResultsParserUtil.combine(
					"modules/util/source-formatter/bnd.bnd\n",
					"modules/util/source-formatter/samples/pom.xml\n",
					_PLUGIN_HELP_PATH, "\n", _PLUGIN_PATH, "\n")));
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private String _getArtifactURL(String version) {
		return JenkinsResultsParserUtil.combine(
			"https://repository-cdn.liferay.com/nexus/content/repositories/",
			"liferay-public-releases/com/liferay/com.liferay.source.formatter/",
			version, "/com.liferay.source.formatter-", version, ".jar");
	}

	private boolean _isReleasedSourceMatch(int exitValue, String standardOut)
		throws Exception {

		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		Mockito.when(
			gitWorkingDirectory.getWorkingDirectory()
		).thenReturn(
			temporaryFolder.getRoot()
		);

		Mockito.when(
			gitWorkingDirectory.executeBashCommands(
				Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.eq(
					"git diff --name-only " + _ARTIFACT_GIT_ID +
						" HEAD -- modules/util/source-formatter"))
		).thenReturn(
			new GitUtil.ExecutionResult(exitValue, "", standardOut)
		);

		return SourceFormatterReleaseUtil.isReleasedSourceMatch(
			gitWorkingDirectory, "HEAD");
	}

	private void _writeArtifactProperties(String version) throws Exception {
		JenkinsResultsParserUtil.write(
			new File(
				temporaryFolder.getRoot(),
				"modules/.releng/util/source-formatter/artifact.properties"),
			JenkinsResultsParserUtil.combine(
				"artifact.git.id=", _ARTIFACT_GIT_ID, "\n", "artifact.url=",
				_getArtifactURL(version)));
	}

	private void _writeIvyXML(String version) throws Exception {
		File ivyXMLFile = new File(
			temporaryFolder.getRoot(),
			"tools/sdk/dependencies/com.liferay.source.formatter/ivy.xml");

		JenkinsResultsParserUtil.write(
			ivyXMLFile,
			JenkinsResultsParserUtil.combine(
				"<ivy-module><dependencies>",
				"<dependency name=\"com.liferay.source.formatter\" rev=\"",
				version, "\" /></dependencies></ivy-module>"));
	}

	private static final String _ARTIFACT_GIT_ID =
		"b5b78a7944818f3d87eb0f47ad79a26c2577dad8";

	private static final String _PLUGIN_HELP_PATH =
		JenkinsResultsParserUtil.combine(
			"modules/util/source-formatter/src/main/resources/META-INF/maven/",
			"com.liferay/com.liferay.source.formatter/plugin-help.xml");

	private static final String _PLUGIN_PATH = JenkinsResultsParserUtil.combine(
		"modules/util/source-formatter/src/main/resources/META-INF/maven/",
		"plugin.xml");

	private static final String _SOURCE_CHANGE_PATH =
		JenkinsResultsParserUtil.combine(
			"modules/util/source-formatter/src/main/java/com/liferay/source/",
			"formatter/SourceFormatter.java");

}