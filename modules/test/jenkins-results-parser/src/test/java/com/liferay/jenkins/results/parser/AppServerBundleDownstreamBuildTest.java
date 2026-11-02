/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.Collections;
import java.util.Properties;

import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Michael Hashimoto
 */
public class AppServerBundleDownstreamBuildTest
	extends com.liferay.jenkins.results.parser.Test {

	@Test
	public void testCreateBuildFailureObjectRef() throws Exception {
		String bundlesPath = RandomTestUtil.randomString();

		Properties buildProperties = new Properties();

		buildProperties.setProperty(
			"bundle.builder.failure.caching.enabled", "true");
		buildProperties.setProperty(
			"cloud.ci.s3.bucket.bundles.path", bundlesPath);

		JenkinsResultsParserUtil.setBuildProperties(buildProperties);

		PortalWorkspace portalWorkspace = Mockito.mock(PortalWorkspace.class);
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			Mockito.mock(PortalWorkspaceGitRepository.class);

		Path portalDirPath = Files.createTempDirectory("portal-");

		Mockito.doReturn(
			portalDirPath.toFile()
		).when(
			portalWorkspaceGitRepository
		).getDirectory();

		Mockito.doReturn(
			portalWorkspaceGitRepository
		).when(
			portalWorkspace
		).getPortalWorkspaceGitRepository();

		String s3ObjectPathName = RandomTestUtil.randomString();

		Mockito.doReturn(
			s3ObjectPathName
		).when(
			portalWorkspace
		).getS3ObjectPathName();

		_testCreateBuildFailureObjectRef(
			"portal",
			JenkinsResultsParserUtil.combine(
				bundlesPath, "/", s3ObjectPathName, "/portal/build-failure"),
			portalWorkspace);

		WorkspaceGitRepository workspaceGitRepository = Mockito.mock(
			WorkspaceGitRepository.class);

		String baseBranchSHA = RandomTestUtil.randomSHA();

		Mockito.doReturn(
			baseBranchSHA
		).when(
			workspaceGitRepository
		).getBaseBranchSHA();

		Path asahDirPath = Files.createTempDirectory("asah-");

		Mockito.doReturn(
			asahDirPath.toFile()
		).when(
			workspaceGitRepository
		).getDirectory();

		Mockito.doReturn(
			"com-liferay-osb-asah-private"
		).when(
			workspaceGitRepository
		).getName();

		String senderBranchSHA = RandomTestUtil.randomSHA();

		Mockito.doReturn(
			senderBranchSHA
		).when(
			workspaceGitRepository
		).getSenderBranchSHA();

		portalWorkspace = Mockito.mock(PortalWorkspace.class);

		Mockito.doReturn(
			portalWorkspaceGitRepository
		).when(
			portalWorkspace
		).getPortalWorkspaceGitRepository();

		Mockito.doReturn(
			workspaceGitRepository
		).when(
			portalWorkspace
		).getWorkspaceGitRepository(
			"com-liferay-osb-asah-private"
		);

		_testCreateBuildFailureObjectRef(
			"analytics.cloud",
			JenkinsResultsParserUtil.combine(
				bundlesPath, "/com-liferay-osb-asah-private/", baseBranchSHA,
				"/", senderBranchSHA, "/analytics.cloud/build-failure"),
			portalWorkspace);

		Mockito.verify(
			portalWorkspace, Mockito.never()
		).getS3ObjectPathName();
	}

	private void _testCreateBuildFailureObjectRef(
			String axisVariable, String expectedS3ObjectRefPath,
			PortalWorkspace portalWorkspace)
		throws Exception {

		TopLevelBuild topLevelBuild = Mockito.mock(
			TopLevelBuild.class,
			Mockito.withSettings(
			).extraInterfaces(
				PortalWorkspaceBuild.class
			));

		Mockito.doReturn(
			portalWorkspace
		).when(
			(PortalWorkspaceBuild)topLevelBuild
		).getWorkspace();

		AppServerBundleDownstreamBuild appServerBundleDownstreamBuild =
			Mockito.mock(AppServerBundleDownstreamBuild.class);

		Mockito.doCallRealMethod(
		).when(
			appServerBundleDownstreamBuild
		).createBuildFailureObjectRef();

		Mockito.doReturn(
			axisVariable
		).when(
			appServerBundleDownstreamBuild
		).getAxisVariable();

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			appServerBundleDownstreamBuild
		).getBuildURL();

		String s3BucketDistPath = RandomTestUtil.randomString();

		Mockito.doReturn(
			Collections.singletonMap("S3_BUCKET_DIST_PATH", s3BucketDistPath)
		).when(
			appServerBundleDownstreamBuild
		).getStartPropertiesTempMap();

		Mockito.doReturn(
			topLevelBuild
		).when(
			appServerBundleDownstreamBuild
		).getTopLevelBuild();

		try (MockedStatic<CloudBucketUtil> cloudBucketUtilMockedStatic =
				Mockito.mockStatic(CloudBucketUtil.class);
			MockedStatic<NotificationUtil> notificationUtilMockedStatic =
				Mockito.mockStatic(NotificationUtil.class)) {

			appServerBundleDownstreamBuild.createBuildFailureObjectRef();

			cloudBucketUtilMockedStatic.verify(
				() -> CloudBucketUtil.createS3ObjectRef(
					JenkinsResultsParserUtil.combine(
						s3BucketDistPath, "/", axisVariable, "/build-failure"),
					expectedS3ObjectRefPath));
		}
	}

}