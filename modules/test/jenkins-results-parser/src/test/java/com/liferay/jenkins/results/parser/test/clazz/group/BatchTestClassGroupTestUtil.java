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
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mockito.Mockito;

/**
 * @author Kenji Heigel
 */
public class BatchTestClassGroupTestUtil {

	public static PortalTestClassJob getPortalTestClassJob() {
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

		List<File> jobPropertiesFiles =
			_portalTestClassJob.getJobPropertiesFiles();

		jobPropertiesFiles.clear();

		jobPropertiesFiles.add(
			new File(
				"src/test/resources/dependencies/test/clazz/group" +
					"/BatchTestClassGroupTestUtil/test.properties"));

		return _portalTestClassJob;
	}

	public static ServiceBuilderModulesBatchTestClassGroup
		newServiceBuilderModulesBatchTestClassGroup(
			String... modifiedFilePaths) {

		PortalTestClassJob portalTestClassJob = getPortalTestClassJob();

		PortalGitWorkingDirectory portalGitWorkingDirectory =
			portalTestClassJob.getPortalGitWorkingDirectory();

		List<File> modifiedFiles = new ArrayList<>();

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

	private static PortalTestClassJob _portalTestClassJob;

}