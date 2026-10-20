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
		String version = RandomTestUtil.randomString();

		_writeIvyXML(version);

		Assert.assertEquals(
			version,
			SourceFormatterReleaseUtil.getIvyVersion(
				temporaryFolder.getRoot()));
	}

	@Test
	public void testIsReleasedSourceMatchWithDiffFailure() throws Exception {
		String artifactGitId = RandomTestUtil.randomSHA();
		String version = RandomTestUtil.randomString();

		_writeArtifactProperties(artifactGitId, version);
		_writeIvyXML(version);

		Assert.assertFalse(_isReleasedSourceMatch(artifactGitId, 128, ""));
	}

	@Test
	public void testIsReleasedSourceMatchWithEmptyDiff() throws Exception {
		String artifactGitId = RandomTestUtil.randomSHA();
		String version = RandomTestUtil.randomString();

		_writeArtifactProperties(artifactGitId, version);
		_writeIvyXML(version);

		Assert.assertTrue(_isReleasedSourceMatch(artifactGitId, 0, ""));
	}

	@Test
	public void testIsReleasedSourceMatchWithIvyVersionMismatch()
		throws Exception {

		String artifactGitId = RandomTestUtil.randomSHA();

		_writeArtifactProperties(artifactGitId, RandomTestUtil.randomString());

		_writeIvyXML(RandomTestUtil.randomString());

		Assert.assertFalse(_isReleasedSourceMatch(artifactGitId, 0, ""));
	}

	@Test
	public void testIsReleasedSourceMatchWithMissingArtifactGitId()
		throws Exception {

		String version = RandomTestUtil.randomString();

		JenkinsResultsParserUtil.write(
			new File(
				temporaryFolder.getRoot(),
				"modules/.releng/util/source-formatter/artifact.properties"),
			"artifact.url=" + _getArtifactURL(version));

		_writeIvyXML(version);

		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		Mockito.when(
			gitWorkingDirectory.getWorkingDirectory()
		).thenReturn(
			temporaryFolder.getRoot()
		);

		Assert.assertFalse(
			SourceFormatterReleaseUtil.isReleasedSourceMatch(
				gitWorkingDirectory, "HEAD"));

		Mockito.verify(
			gitWorkingDirectory, Mockito.never()
		).executeBashCommands(
			Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong(),
			Mockito.anyString()
		);
	}

	@Test
	public void testIsReleasedSourceMatchWithSourceChangeAndVersionBump()
		throws Exception {

		String artifactGitId = RandomTestUtil.randomSHA();
		String version = RandomTestUtil.randomString();

		_writeArtifactProperties(artifactGitId, version);
		_writeIvyXML(version);

		Assert.assertFalse(
			_isReleasedSourceMatch(
				artifactGitId, 0,
				JenkinsResultsParserUtil.combine(
					_getGitDiff(
						"Bundle-Version: 1.0.1644",
						"modules/util/source-formatter/bnd.bnd",
						"Bundle-Version: 1.0.1643"),
					_getGitDiff(
						"\t_VERSION = \"1.0.2\";",
						"modules/util/source-formatter/src/main/java/com" +
							"/liferay/source/formatter/SourceFormatter.java",
						"\t_VERSION = \"1.0.1\";"))));
	}

	@Test
	public void testIsReleasedSourceMatchWithVersionBump() throws Exception {
		String artifactGitId = RandomTestUtil.randomSHA();
		String version = RandomTestUtil.randomString();

		_writeArtifactProperties(artifactGitId, version);
		_writeIvyXML(version);

		String mavenDirPath =
			"modules/util/source-formatter/src/main/resources/META-INF/maven";

		Assert.assertTrue(
			_isReleasedSourceMatch(
				artifactGitId, 0,
				JenkinsResultsParserUtil.combine(
					_getGitDiff(
						"Bundle-Version: 1.0.1644",
						"modules/util/source-formatter/bnd.bnd",
						"Bundle-Version: 1.0.1643"),
					_getGitDiff(
						"\t\t\t\t<version>1.0.1643</version>",
						"modules/util/source-formatter/samples/pom.xml",
						"\t\t\t\t<version>1.0.1642</version>"),
					_getGitDiff(
						"\t<version>1.0.1643</version>",
						mavenDirPath +
							"/com.liferay/com.liferay.source.formatter" +
								"/plugin-help.xml",
						"\t<version>1.0.1642</version>"),
					_getGitDiff(
						"\t<version>1.0.1643</version>",
						mavenDirPath + "/plugin.xml",
						"\t<version>1.0.1642</version>"))));
	}

	@Test
	public void testIsReleasedSourceMatchWithVersionFileChange()
		throws Exception {

		String artifactGitId = RandomTestUtil.randomSHA();
		String version = RandomTestUtil.randomString();

		_writeArtifactProperties(artifactGitId, version);
		_writeIvyXML(version);

		Assert.assertFalse(
			_isReleasedSourceMatch(
				artifactGitId, 0,
				_getGitDiff(
					"Bundle-Name: Liferay Source Formatter 1.0.1644",
					"modules/util/source-formatter/bnd.bnd",
					"Bundle-Version: 1.0.1643")));
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private String _getArtifactURL(String version) {
		return JenkinsResultsParserUtil.combine(
			"https://repository-cdn.liferay.com/nexus/content/repositories",
			"/liferay-public-releases/com/liferay/com.liferay.source.formatter",
			"/", version, "/com.liferay.source.formatter-", version, ".jar");
	}

	private String _getGitDiff(
		String addedLine, String filePath, String removedLine) {

		return JenkinsResultsParserUtil.combine(
			"diff --git a/", filePath, " b/", filePath, "\n",
			"index 1111111..2222222 100644\n", "--- a/", filePath, "\n",
			"+++ b/", filePath, "\n", "@@ -3 +3 @@\n", "-", removedLine, "\n",
			"+", addedLine, "\n");
	}

	private boolean _isReleasedSourceMatch(
			String artifactGitId, int exitValue, String standardOut)
		throws Exception {

		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		Mockito.when(
			gitWorkingDirectory.executeBashCommands(
				Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.eq(
					JenkinsResultsParserUtil.combine(
						"git diff --no-color --unified=0 ", artifactGitId,
						" HEAD -- modules/util/source-formatter")))
		).thenReturn(
			new GitUtil.ExecutionResult(exitValue, "", standardOut)
		);

		Mockito.when(
			gitWorkingDirectory.getWorkingDirectory()
		).thenReturn(
			temporaryFolder.getRoot()
		);

		return SourceFormatterReleaseUtil.isReleasedSourceMatch(
			gitWorkingDirectory, "HEAD");
	}

	private void _writeArtifactProperties(String artifactGitId, String version)
		throws Exception {

		JenkinsResultsParserUtil.write(
			new File(
				temporaryFolder.getRoot(),
				"modules/.releng/util/source-formatter/artifact.properties"),
			JenkinsResultsParserUtil.combine(
				"artifact.git.id=", artifactGitId, "\n", "artifact.url=",
				_getArtifactURL(version)));
	}

	private void _writeIvyXML(String version) throws Exception {
		JenkinsResultsParserUtil.write(
			new File(
				temporaryFolder.getRoot(),
				"tools/sdk/dependencies/com.liferay.source.formatter/ivy.xml"),
			JenkinsResultsParserUtil.combine(
				"<ivy-module><dependencies><dependency ",
				"name=\"com.liferay.portal.kernel\" rev=\"",
				RandomTestUtil.randomString(), "\" /><dependency ",
				"name=\"com.liferay.source.formatter\" rev=\"", version,
				"\" /></dependencies></ivy-module>"));
	}

}