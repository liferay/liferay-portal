/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.util.Map;

/**
 * @author Kenji Heigel
 */
public class AppServerBundleDownstreamBuild extends BaseDownstreamBuild {

	protected AppServerBundleDownstreamBuild(
		String buildURL, DownstreamBuildReport cachedDownstreamBuildReport,
		TopLevelBuild topLevelBuild) {

		super(buildURL, cachedDownstreamBuildReport, topLevelBuild);
	}

	protected void createBuildFailureObjectRef() throws IOException {
		boolean bundleBuilderFailureCachingEnabled = Boolean.parseBoolean(
			JenkinsResultsParserUtil.getBuildProperty(
				"bundle.builder.failure.caching.enabled"));

		if (!bundleBuilderFailureCachingEnabled) {
			return;
		}

		Map<String, String> startPropertiesTempMap =
			getStartPropertiesTempMap();

		String axisVariable = getAxisVariable();

		String filePath = axisVariable + "/" + _FILE_NAME_BUILD_FAILURE;

		String s3ObjectPath =
			startPropertiesTempMap.get("S3_BUCKET_DIST_PATH") + "/" + filePath;

		if (CloudBucketUtil.isS3ObjectRefAvailable(s3ObjectPath)) {
			return;
		}

		PortalWorkspaceBuild portalWorkspaceBuild =
			(PortalWorkspaceBuild)getTopLevelBuild();

		Workspace workspace = portalWorkspaceBuild.getWorkspace();

		if (!(workspace instanceof PortalWorkspace)) {
			return;
		}

		PortalWorkspace portalWorkspace = (PortalWorkspace)workspace;

		WorkspaceGitRepository workspaceGitRepository =
			portalWorkspace.getPortalWorkspaceGitRepository();

		if (axisVariable.equals("analytics.cloud")) {
			workspaceGitRepository = portalWorkspace.getWorkspaceGitRepository(
				"com-liferay-osb-asah-private");
		}

		File directory = new File(
			workspaceGitRepository.getDirectory(), axisVariable);

		directory.mkdirs();

		File buildFailureFile = new File(directory, _FILE_NAME_BUILD_FAILURE);

		buildFailureFile.createNewFile();

		CloudBucketUtil.uploadS3File(s3ObjectPath, buildFailureFile);

		StringBuilder sb = new StringBuilder();

		sb.append(
			JenkinsResultsParserUtil.getBuildProperty(
				"cloud.ci.s3.bucket.bundles.path"));
		sb.append("/");

		if (axisVariable.equals("analytics.cloud")) {
			sb.append(workspaceGitRepository.getName());
			sb.append("/");
			sb.append(workspaceGitRepository.getBaseBranchSHA());
			sb.append("/");
			sb.append(workspaceGitRepository.getSenderBranchSHA());
		}
		else {
			sb.append(portalWorkspace.getS3ObjectPathName());
		}

		sb.append("/");
		sb.append(filePath);

		CloudBucketUtil.createS3ObjectRef(s3ObjectPath, sb.toString());

		NotificationUtil.sendSlackNotification(
			getBuildURL(), "#ci-aws-notifications", ":ci:",
			"Bundle Builder Failure (" + getAxisVariable() + ")", "Liferay CI");
	}

	private static final String _FILE_NAME_BUILD_FAILURE = "build-failure";

}