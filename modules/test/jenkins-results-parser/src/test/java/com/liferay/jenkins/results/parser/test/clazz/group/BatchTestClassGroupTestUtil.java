/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.test.clazz.group;

import com.liferay.jenkins.results.parser.GitWorkingDirectoryFactory;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.JobFactory;
import com.liferay.jenkins.results.parser.PortalGitWorkingDirectory;
import com.liferay.jenkins.results.parser.PortalTestClassJob;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.mockito.Mockito;

/**
 * @author Kenji Heigel
 */
public class BatchTestClassGroupTestUtil {

	public static PortalTestClassJob getPortalTestClassJob() {
		return getPortalTestClassJob(null);
	}

	public static PortalTestClassJob getPortalTestClassJob(
		Properties jobProperties) {

		PortalTestClassJob portalTestClassJob = _getPortalTestClassJob();

		List<File> jobPropertiesFiles =
			portalTestClassJob.getJobPropertiesFiles();

		jobPropertiesFiles.clear();

		if (jobProperties != null) {
			jobPropertiesFiles.add(_writeJobPropertiesFile(jobProperties));
		}

		jobPropertiesFiles.add(
			new File(
				"src/test/resources/dependencies/test/clazz/group" +
					"/BatchTestClassGroupTestUtil/test.properties"));

		_setDefaults(portalTestClassJob.getPortalGitWorkingDirectory());

		return portalTestClassJob;
	}

	public static CompileModulesBatchTestClassGroup
		newCompileModulesBatchTestClassGroup(
			Properties jobProperties, File... modifiedModuleDirs) {

		PortalTestClassJob portalTestClassJob = getPortalTestClassJob(
			jobProperties);

		PortalGitWorkingDirectory portalGitWorkingDirectory =
			portalTestClassJob.getPortalGitWorkingDirectory();

		try {
			Mockito.doReturn(
				Arrays.asList(modifiedModuleDirs)
			).when(
				portalGitWorkingDirectory
			).getModifiedModuleDirsList(
				Mockito.anyList(), Mockito.anyList()
			);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return new CompileModulesBatchTestClassGroup(
			"modules-compile", portalTestClassJob);
	}

	public static ServiceBuilderModulesBatchTestClassGroup
		newServiceBuilderModulesBatchTestClassGroup(
			String... modifiedFilePaths) {

		List<File> modifiedFiles = new ArrayList<>();

		PortalTestClassJob portalTestClassJob = getPortalTestClassJob();

		PortalGitWorkingDirectory portalGitWorkingDirectory =
			portalTestClassJob.getPortalGitWorkingDirectory();

		File workingDirectory = portalGitWorkingDirectory.getWorkingDirectory();

		for (String modifiedFilePath : modifiedFilePaths) {
			modifiedFiles.add(new File(workingDirectory, modifiedFilePath));
		}

		Mockito.doReturn(
			modifiedFiles
		).when(
			portalGitWorkingDirectory
		).getModifiedFilesList();

		return new ServiceBuilderModulesBatchTestClassGroup(
			"service-builder-modules", portalTestClassJob);
	}

	public static File newTestClassFile(String className, File parentDir)
		throws IOException {

		File testClassFile = new File(parentDir, className + ".java");

		String testClassFileContent = _getTestClassFileContent(className);

		Files.write(
			testClassFile.toPath(), testClassFileContent.getBytes("UTF-8"));

		return testClassFile;
	}

	private static PortalTestClassJob _getPortalTestClassJob() {
		if (_portalTestClassJob != null) {
			return _portalTestClassJob;
		}

		String repositoryName = "liferay-portal";
		String upstreamBranchName = "master";

		PortalGitWorkingDirectory portalGitWorkingDirectory = Mockito.spy(
			(PortalGitWorkingDirectory)
				GitWorkingDirectoryFactory.newGitWorkingDirectory(
					upstreamBranchName,
					JenkinsResultsParserUtil.getGitWorkingDir(new File(".")),
					repositoryName));

		_portalTestClassJob = (PortalTestClassJob)JobFactory.newJob(
			Job.BuildProfile.DXP, "test-portal-acceptance-pullrequest(master)",
			null, portalGitWorkingDirectory, upstreamBranchName, null,
			repositoryName, "relevant", upstreamBranchName);

		return _portalTestClassJob;
	}

	private static String _getTestClassFileContent(String className) {
		return JenkinsResultsParserUtil.combine(
			"public class ", className, " {\n\n\t@Test\n\tpublic void ",
			"testSample() {\n\t}\n\n}");
	}

	private static void _setDefaults(
		PortalGitWorkingDirectory portalGitWorkingDirectory) {

		try {
			Mockito.doReturn(
				Collections.emptyList()
			).when(
				portalGitWorkingDirectory
			).getModifiedFilesList();

			Mockito.doReturn(
				Collections.emptyList()
			).when(
				portalGitWorkingDirectory
			).getModifiedModuleDirsList(
				Mockito.anyList(), Mockito.anyList()
			);

			Mockito.doReturn(
				Collections.emptyList()
			).when(
				portalGitWorkingDirectory
			).getModifiedNonposhiModules();

			Mockito.doReturn(
				Collections.emptyList()
			).when(
				portalGitWorkingDirectory
			).getModifiedPoshiModules();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static File _writeJobPropertiesFile(Properties jobProperties) {
		try {
			File jobPropertiesFile = File.createTempFile(
				"BatchTestClassGroupTestUtil", ".properties");

			jobPropertiesFile.deleteOnExit();

			try (OutputStream outputStream = new FileOutputStream(
					jobPropertiesFile)) {

				jobProperties.store(outputStream, null);
			}

			return jobPropertiesFile;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private static PortalTestClassJob _portalTestClassJob;

}