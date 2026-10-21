/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;

import java.util.Arrays;
import java.util.List;

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
	public void setUp() throws Exception {
		_artifactGitId = RandomTestUtil.randomSHA();
		_artifactPropertiesFile = new File(
			temporaryFolder.getRoot(),
			"modules/.releng/util/source-formatter/artifact.properties");
		_ivyXMLFile = new File(
			temporaryFolder.getRoot(),
			"tools/sdk/dependencies/com.liferay.source.formatter/ivy.xml");

		_version = JenkinsResultsParserUtil.combine(
			"1.0.",
			String.valueOf(Math.abs(RandomTestUtil.randomLong() % 10000)));

		JenkinsResultsParserUtil.write(
			_artifactPropertiesFile,
			JenkinsResultsParserUtil.combine(
				"artifact.git.id=", _artifactGitId, "\nartifact.url=",
				_getArtifactURL(_version)));

		JenkinsResultsParserUtil.write(
			_ivyXMLFile,
			_getIvyXML(
				"<dependency name=\"com.liferay.portal.kernel\" rev=\"" +
					RandomTestUtil.randomString() + "\" />",
				"<dependency name=\"com.liferay.source.formatter\" rev=\"" +
					_version + "\" />"));
	}

	@Test
	public void testGetIvyVersion() throws Exception {
		Assert.assertEquals(
			_version,
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
					_version + "\" />"));

		Assert.assertNull(
			SourceFormatterReleaseUtil.getIvyVersion(
				temporaryFolder.getRoot()));
	}

	@Test
	public void testIsReleasedSourceMatchWithAddedOrDeletedVersionFile()
		throws Exception {

		Assert.assertFalse(
			_isReleasedSourceMatch(
				0,
				JenkinsResultsParserUtil.combine(
					"diff --git a/", _BND_PATH, " b/", _BND_PATH, "\n",
					"deleted file mode 100644\n", "index 1111111..0000000\n",
					"--- a/", _BND_PATH, "\n", "+++ /dev/null\n",
					"@@ -1 +0,0 @@\n", "-Bundle-Version: 1.0.1643\n")));
		Assert.assertFalse(
			_isReleasedSourceMatch(
				0,
				JenkinsResultsParserUtil.combine(
					"diff --git a/", _BND_PATH, " b/", _BND_PATH, "\n",
					"new file mode 100644\n", "index 0000000..1111111\n",
					"--- /dev/null\n", "+++ b/", _BND_PATH, "\n",
					"@@ -0,0 +1 @@\n", "+Bundle-Version: 1.0.1644\n")));
	}

	@Test
	public void testIsReleasedSourceMatchWithDiffFailure() throws Exception {
		Assert.assertFalse(_isReleasedSourceMatch(128, ""));
	}

	@Test
	public void testIsReleasedSourceMatchWithEmptyDiff() throws Exception {
		Assert.assertTrue(_isReleasedSourceMatch(0, ""));
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
				"artifact.git.id=", _artifactGitId, "\nartifact.url=",
				"https://repository-cdn.liferay.com/nexus/content",
				"/repositories/liferay-public-releases/com/liferay",
				"/com.liferay.source.formatter/", _version,
				"/com.liferay.source.formatter-", _version, ".zip"));

		_assertNotReleasedSourceMatch();

		JenkinsResultsParserUtil.write(
			_artifactPropertiesFile, "artifact.git.id=" + _artifactGitId);

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
			_artifactPropertiesFile,
			"artifact.url=" + _getArtifactURL(_version));

		_assertNotReleasedSourceMatch();

		JenkinsResultsParserUtil.write(
			_artifactPropertiesFile,
			"artifact.git.id=\nartifact.url=" + _getArtifactURL(_version));

		_assertNotReleasedSourceMatch();
	}

	@Test
	public void testIsReleasedSourceMatchWithNoNewlineMarker()
		throws Exception {

		Assert.assertTrue(
			_isReleasedSourceMatch(
				0,
				JenkinsResultsParserUtil.combine(
					_getVersionBumpGitDiff(),
					"\\ No newline at end of file\n")));
	}

	@Test
	public void testIsReleasedSourceMatchWithoutArtifactProperties()
		throws Exception {

		_artifactPropertiesFile.delete();

		_assertNotReleasedSourceMatch();
	}

	@Test
	public void testIsReleasedSourceMatchWithReorderedLines() throws Exception {
		Assert.assertFalse(
			_isReleasedSourceMatch(
				0,
				JenkinsResultsParserUtil.combine(
					"diff --git a/", _BND_PATH, " b/", _BND_PATH, "\n",
					"index 1111111..2222222 100644\n", "--- a/", _BND_PATH,
					"\n", "+++ b/", _BND_PATH, "\n", "@@ -1,2 +1,2 @@\n",
					"-Bundle-Name: Liferay Source Formatter\n",
					"-Bundle-SymbolicName: com.liferay.source.formatter\n",
					"+Bundle-SymbolicName: com.liferay.source.formatter\n",
					"+Bundle-Name: Liferay Source Formatter\n")));
	}

	@Test
	public void testIsReleasedSourceMatchWithSourceChangeAndVersionBump()
		throws Exception {

		Assert.assertFalse(
			_isReleasedSourceMatch(
				0,
				JenkinsResultsParserUtil.combine(
					_getVersionBumpGitDiff(),
					_getGitDiff(
						"\t_VERSION = \"1.0.2\";",
						"modules/util/source-formatter/src/main/java/com" +
							"/liferay/source/formatter/SourceFormatter.java",
						"\t_VERSION = \"1.0.1\";"))));
	}

	@Test
	public void testIsReleasedSourceMatchWithUndottedVersion()
		throws Exception {

		String version = String.valueOf(
			Math.abs(RandomTestUtil.randomLong() % 10000));

		JenkinsResultsParserUtil.write(
			_artifactPropertiesFile,
			JenkinsResultsParserUtil.combine(
				"artifact.git.id=", _artifactGitId, "\nartifact.url=",
				_getArtifactURL(version)));

		JenkinsResultsParserUtil.write(
			_ivyXMLFile,
			_getIvyXML(
				"<dependency name=\"com.liferay.source.formatter\" rev=\"" +
					version + "\" />"));

		Assert.assertFalse(_isReleasedSourceMatch(0, ""));
	}

	@Test
	public void testIsReleasedSourceMatchWithUnreleasedVersionChange()
		throws Exception {

		Assert.assertFalse(
			_isReleasedSourceMatch(
				0,
				JenkinsResultsParserUtil.combine(
					_getVersionBumpGitDiff(),
					_getGitDiff(
						"Import-Package: org.example;version=\"[1.5,2)\"",
						_BND_PATH,
						"Import-Package: org.example;version=\"[1.0,2)\""))));
	}

	@Test
	public void testIsReleasedSourceMatchWithVersionBump() throws Exception {
		Assert.assertTrue(_isReleasedSourceMatch(0, _getVersionBumpGitDiff()));
	}

	@Test
	public void testIsReleasedSourceMatchWithVersionFileChange()
		throws Exception {

		List<String> versionFilePaths = Arrays.asList(
			_BND_PATH, _PLUGIN_HELP_PATH, _PLUGIN_PATH, _POM_PATH);

		for (String versionFilePath : versionFilePaths) {
			Assert.assertFalse(
				versionFilePath,
				_isReleasedSourceMatch(
					0,
					_getGitDiff(
						"<name>Liferay Source Formatter 1.0.1644</name>",
						versionFilePath, "<version>1.0.1643</version>")));
		}
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

	private String _getIvyXML(String... dependencyXMLs) {
		return JenkinsResultsParserUtil.combine(
			"<ivy-module><dependencies>", String.join("", dependencyXMLs),
			"</dependencies></ivy-module>");
	}

	private String _getVersionBumpGitDiff() {
		return JenkinsResultsParserUtil.combine(
			_getGitDiff(
				"Bundle-Version: 1.0.1644", _BND_PATH,
				"Bundle-Version: 1.0.1643"),
			_getGitDiff(
				"\t\t\t\t<version>1.0.1643</version>", _POM_PATH,
				"\t\t\t\t<version>1.0.1642</version>"),
			_getGitDiff(
				"\t<version>1.0.1643</version>", _PLUGIN_HELP_PATH,
				"\t<version>1.0.1642</version>"),
			_getGitDiff(
				"\t<version>1.0.1643</version>", _PLUGIN_PATH,
				"\t<version>1.0.1642</version>"));
	}

	private boolean _isReleasedSourceMatch(int exitValue, String standardOut)
		throws Exception {

		GitWorkingDirectory gitWorkingDirectory = Mockito.mock(
			GitWorkingDirectory.class);

		String ref = RandomTestUtil.randomString();

		Mockito.when(
			gitWorkingDirectory.executeBashCommands(
				Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong(),
				Mockito.eq(
					JenkinsResultsParserUtil.combine(
						"git diff --dst-prefix=b/ --no-color --src-prefix=a/ ",
						"--unified=0 ", _artifactGitId, " ", ref,
						" -- modules/util/source-formatter")))
		).thenReturn(
			new GitUtil.ExecutionResult(exitValue, "", standardOut)
		);

		Mockito.when(
			gitWorkingDirectory.getWorkingDirectory()
		).thenReturn(
			temporaryFolder.getRoot()
		);

		return SourceFormatterReleaseUtil.isReleasedSourceMatch(
			gitWorkingDirectory, ref);
	}

	private static final String _BND_PATH =
		"modules/util/source-formatter/bnd.bnd";

	private static final String _PLUGIN_HELP_PATH =
		JenkinsResultsParserUtil.combine(
			"modules/util/source-formatter/src/main/resources/META-INF/maven",
			"/com.liferay/com.liferay.source.formatter/plugin-help.xml");

	private static final String _PLUGIN_PATH =
		"modules/util/source-formatter/src/main/resources/META-INF/maven" +
			"/plugin.xml";

	private static final String _POM_PATH =
		"modules/util/source-formatter/samples/pom.xml";

	private String _artifactGitId;
	private File _artifactPropertiesFile;
	private File _ivyXMLFile;
	private String _version;

}