/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.FileOutputStream;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.mockito.Mockito;

/**
 * @author Kenji Heigel
 */
public class JenkinsGitRepositoryJobTest
	extends com.liferay.jenkins.results.parser.Test {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_gitRepositoryDir = temporaryFolder.newFolder();
		_gitWorkingDirectory = Mockito.mock(GitWorkingDirectory.class);

		JenkinsResultsParserUtil.setBuildProperties(_getBuildProperties());

		mockEnvironment(
			Collections.singletonMap(
				"GITHUB_UPSTREAM_BRANCH_NAME", RandomTestUtil.randomString()));

		File upstreamJarFile = temporaryFolder.newFile();

		_writeJarFile(upstreamJarFile, null);

		Mockito.doAnswer(
			invocation -> {
				String command = invocation.getArgument(3);

				Files.copy(
					upstreamJarFile.toPath(),
					Paths.get(command.substring(command.indexOf(" > ") + 3)),
					StandardCopyOption.REPLACE_EXISTING);

				return new GitUtil.ExecutionResult(0, "", "");
			}
		).when(
			_gitWorkingDirectory
		).executeBashCommands(
			Mockito.anyInt(), Mockito.anyLong(), Mockito.anyLong(),
			Mockito.anyString()
		);
	}

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		JenkinsResultsParserUtil.setBuildProperties(new Properties());
	}

	@Test
	public void testGetInvokedJobNames() throws Exception {
		_testGetInvokedJobNames(
			Arrays.asList(
				"root-cause-analysis-tool",
				"test-portal-acceptance-pullrequest(master)/dummy",
				"test-portal-source-format"),
			null, "relevant", "commands/build-common.xml",
			"template/jobs/root-cause-analysis-tool-1/config.xml");
		_testGetInvokedJobNames(
			Arrays.asList("test-portal-acceptance-pullrequest(master)"), null,
			"default", "commands/build-common.xml");
		_testGetInvokedJobNames(
			Arrays.asList("test-portal-release"), _ENTRY_NAME_PORTAL_RELEASE,
			"relevant", _JAR_FILE_NAME);
		_testGetInvokedJobNames(
			Arrays.asList("test-portal-source-format"), _ENTRY_NAME_MANIFEST,
			"relevant", _JAR_FILE_NAME);
	}

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private Properties _getBuildProperties() {
		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"jenkins.pull.request.job.portal.test.suite" +
				"[test-portal-acceptance-pullrequest(*)]",
			"dummy");
		buildProperties.setProperty(
			"jenkins.pull.request.jobs[build-common-rule][relevant]",
			"test-portal-source-format," +
				"test-portal-acceptance-pullrequest(master)");
		buildProperties.setProperty(
			"jenkins.pull.request.jobs[default]",
			"test-portal-acceptance-pullrequest(master)");
		buildProperties.setProperty(
			"jenkins.pull.request.jobs[portal-release-rule][relevant]",
			"test-portal-release");
		buildProperties.setProperty(
			"jenkins.pull.request.jobs[relevant]", "test-portal-source-format");
		buildProperties.setProperty(
			"jenkins.pull.request.jobs" +
				"[root-cause-analysis-tool-rule][relevant]",
			"root-cause-analysis-tool,test-portal-source-format");
		buildProperties.setProperty(
			"modified.files.includes[build-common-rule][relevant]",
			"commands/build-common.xml,lib/jenkins/*");
		buildProperties.setProperty(
			"modified.files.includes[root-cause-analysis-tool-rule][relevant]",
			"template/jobs/root-cause-analysis-tool*/**");
		buildProperties.setProperty(
			"modified.jar.entries.includes[portal-release-rule][relevant]",
			"com/liferay/jenkins/results/parser/PortalRelease.class");
		buildProperties.setProperty(
			"modified.jar.entries.includes" +
				"[root-cause-analysis-tool-rule][relevant]",
			"META-INF/**");

		return buildProperties;
	}

	private void _testGetInvokedJobNames(
			List<String> expectedInvokedJobNames, String modifiedEntryName,
			String testSuiteName, String... modifiedFileNames)
		throws Exception {

		_writeJarFile(
			new File(_gitRepositoryDir, _JAR_FILE_NAME), modifiedEntryName);

		List<File> modifiedFiles = new ArrayList<>();

		for (String modifiedFileName : modifiedFileNames) {
			modifiedFiles.add(new File(_gitRepositoryDir, modifiedFileName));
		}

		Mockito.doReturn(
			modifiedFiles
		).when(
			_gitWorkingDirectory
		).getModifiedFilesList();

		JenkinsGitRepositoryJob jenkinsGitRepositoryJob = Mockito.mock(
			JenkinsGitRepositoryJob.class);

		ReflectionTestUtil.setFieldValue(
			jenkinsGitRepositoryJob, "gitRepositoryDir", _gitRepositoryDir);
		ReflectionTestUtil.setFieldValue(
			jenkinsGitRepositoryJob, "gitWorkingDirectory",
			_gitWorkingDirectory);

		Mockito.doCallRealMethod(
		).when(
			jenkinsGitRepositoryJob
		).getInvokedJobNames();

		Mockito.doReturn(
			testSuiteName
		).when(
			jenkinsGitRepositoryJob
		).getTestSuiteName();

		testEquals(
			expectedInvokedJobNames,
			jenkinsGitRepositoryJob.getInvokedJobNames());
	}

	private void _writeJarFile(File file, String modifiedEntryName)
		throws Exception {

		File parentFile = file.getParentFile();

		parentFile.mkdirs();

		try (ZipOutputStream zipOutputStream = new ZipOutputStream(
				new FileOutputStream(file))) {

			for (String entryName : _entryNames) {
				zipOutputStream.putNextEntry(new ZipEntry(entryName));

				if (entryName.equals(modifiedEntryName)) {
					zipOutputStream.write("modified".getBytes());
				}
				else {
					zipOutputStream.write(entryName.getBytes());
				}

				zipOutputStream.closeEntry();
			}
		}
	}

	private static final String _ENTRY_NAME_MANIFEST = "META-INF/MANIFEST.MF";

	private static final String _ENTRY_NAME_PORTAL_RELEASE =
		"com/liferay/jenkins/results/parser/PortalRelease$1.class";

	private static final String _JAR_FILE_NAME =
		"lib/jenkins/com.liferay.jenkins.results.parser.jar";

	private static final List<String> _entryNames = Arrays.asList(
		_ENTRY_NAME_MANIFEST, _ENTRY_NAME_PORTAL_RELEASE);

	private File _gitRepositoryDir;
	private GitWorkingDirectory _gitWorkingDirectory;

}