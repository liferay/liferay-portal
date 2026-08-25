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
import com.liferay.jenkins.results.parser.ReflectionTestUtil;
import com.liferay.jenkins.results.parser.job.property.JobPropertyFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.mockito.Mockito;

/**
 * @author Kenji Heigel
 */
public class BatchTestClassGroupTestUtil {

	public static PortalTestClassJob getPortalTestClassJob() {
		return _setJobPropertiesFiles();
	}

	public static PortalTestClassJob getPortalTestClassJob(
		Properties jobProperties) {

		return _setJobPropertiesFiles(_writeJobPropertiesFile(jobProperties));
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

	public static void resetCaches() {
		Map<String, ?> jobProperties = ReflectionTestUtil.getFieldValue(
			JobPropertyFactory.class, "_jobProperties");

		jobProperties.clear();
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

		try {
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

		_portalTestClassJob = (PortalTestClassJob)JobFactory.newJob(
			Job.BuildProfile.DXP, "test-portal-acceptance-pullrequest(master)",
			null, portalGitWorkingDirectory, upstreamBranchName, null,
			repositoryName, "relevant", upstreamBranchName);

		return _portalTestClassJob;
	}

	private static PortalTestClassJob _setJobPropertiesFiles(
		File... overrideJobPropertiesFiles) {

		PortalTestClassJob portalTestClassJob = _getPortalTestClassJob();

		List<File> jobPropertiesFiles =
			portalTestClassJob.getJobPropertiesFiles();

		jobPropertiesFiles.clear();

		Collections.addAll(jobPropertiesFiles, overrideJobPropertiesFiles);

		jobPropertiesFiles.add(new File(_JOB_PROPERTIES_FILE_PATH));

		return portalTestClassJob;
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

	private static final String _JOB_PROPERTIES_FILE_PATH =
		"src/test/resources/dependencies/test/clazz/group" +
			"/BatchTestClassGroupTestUtil/test.properties";

	private static PortalTestClassJob _portalTestClassJob;

}