/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

import java.util.Properties;
import java.util.regex.Matcher;

/**
 * @author Peter Yoo
 */
public class BuildFactory {

	public static Build newBuild(String url, Build parentBuild) {
		return newBuild(url, parentBuild, null);
	}

	public static Build newBuild(
		String url, Build parentBuild, String jobVariant) {

		url = JenkinsResultsParserUtil.getLocalURL(url);

		Matcher matcher = _buildURLMultiPattern.find(url);

		if (matcher == null) {
			throw new IllegalArgumentException(
				"Invalid Jenkins build URL: " + url);
		}

		String axisVariable = matcher.group("axisVariable");

		if (jobVariant == null) {
			jobVariant = "";
		}

		if (axisVariable != null) {
			if (JenkinsResultsParserUtil.isNullOrEmpty(jobVariant) &&
				(parentBuild != null)) {

				jobVariant = parentBuild.getJobVariant();
			}

			if (JenkinsResultsParserUtil.isNullOrEmpty(jobVariant)) {
				jobVariant = JenkinsResultsParserUtil.getBuildParameter(
					url, "JOB_VARIANT", parentBuild);
			}

			if ((jobVariant != null) &&
				(jobVariant.contains("functional") ||
				 jobVariant.contains("test-portal-environment") ||
				 jobVariant.contains("test-portal-fixpack-environment"))) {

				return new PoshiAxisBuild(url, (BatchBuild)parentBuild);
			}

			return new AxisBuild(url, (BatchBuild)parentBuild);
		}

		String jobName = matcher.group("jobName");

		if (jobName.contains("-controller")) {
			return new ControllerTopLevelBuild(url, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("app-server-bundle-builder")) {
			return new AppServerBundleDownstreamBuild(
				url, (TopLevelBuild)parentBuild);
		}

		if (jobName.contains("-downstream")) {
			String queryString = matcher.group("queryString");

			if ((queryString != null) && queryString.contains("JOB_VARIANT")) {
				jobVariant = queryString.replaceAll(
					".*JOB_VARIANT=([^&]+).*", "$1");
			}

			if (JenkinsResultsParserUtil.isNullOrEmpty(jobVariant)) {
				jobVariant = JenkinsResultsParserUtil.getBuildParameter(
					url, "JOB_VARIANT", parentBuild);
			}

			if (jobVariant != null) {
				if (jobVariant.contains("functional") ||
					jobVariant.contains("test-portal-environment") ||
					jobVariant.contains("test-portal-fixpack-environment")) {

					return new PoshiJUnitDownstreamBuild(
						url, (TopLevelBuild)parentBuild);
				}
				else if (jobVariant.startsWith("integration") ||
						 jobVariant.startsWith("js-unit") ||
						 jobVariant.startsWith("modules-integration") ||
						 jobVariant.startsWith("modules-unit") ||
						 jobVariant.startsWith("playwright-js")) {

					return new JUnitDownstreamBuild(
						url, (TopLevelBuild)parentBuild);
				}
			}

			return new DefaultDownstreamBuild(url, (TopLevelBuild)parentBuild);
		}

		if (jobName.contains("-source-format")) {
			return new SourceFormatBuild(url, (TopLevelBuild)parentBuild);
		}

		if (jobName.contains("-validation")) {
			return new ValidationBuild(url, (TopLevelBuild)parentBuild);
		}

		if (jobName.contains("root-cause-analysis-tool-batch")) {
			return new FreestyleBatchBuild(url, (TopLevelBuild)parentBuild);
		}

		for (String batchToken : _TOKENS_BATCH) {
			if (jobName.contains(batchToken)) {
				if (jobName.contains("qa-websites")) {
					return new QAWebsitesBatchBuild(
						url, (TopLevelBuild)parentBuild);
				}

				return new BatchBuild(url, (TopLevelBuild)parentBuild);
			}
		}

		if (jobName.contains("legacy")) {
			return new LegacyTopLevelBuild(url, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("root-cause-analysis-tool")) {
			return new RootCauseAnalysisToolBuild(
				url, (TopLevelBuild)parentBuild);
		}

		if (jobName.startsWith("test-jenkins-acceptance-pullrequest")) {
			return new JenkinsTopLevelBuild(url, (TopLevelBuild)parentBuild);
		}

		if (jobName.startsWith("test-plugins-acceptance-pullrequest")) {
			return new PullRequestPluginsTopLevelBuild(
				url, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("test-plugins-extraapps")) {
			return new ExtraAppsPluginsTopLevelBuild(
				url, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("test-plugins-marketplaceapp")) {
			return new MarketplaceAppPluginsTopLevelBuild(
				url, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("test-portal-app-release")) {
			return new PortalAppReleaseTopLevelBuild(
				url, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("forward-pullrequest") ||
			jobName.startsWith("test-portal-acceptance-pullrequest")) {

			return new PullRequestPortalTopLevelBuild(
				url, (TopLevelBuild)parentBuild);
		}

		if (jobName.startsWith("test-portal-aws(")) {
			return new PortalAWSTopLevelBuild(url, (TopLevelBuild)parentBuild);
		}

		if (jobName.startsWith("test-portal-environment(") ||
			jobName.startsWith("test-portal-environment-release(") ||
			jobName.startsWith("test-portal-fixpack-environment(")) {

			return new PortalEnvironmentBuild(url, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("test-portal-fixpack-release")) {
			return new PortalFixpackReleasePortalTopLevelBuild(
				url, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("test-portal-hotfix-release")) {
			return new PortalHotfixReleasePortalTopLevelBuild(
				url, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("test-portal-release")) {
			return new PortalReleasePortalTopLevelBuild(
				url, (TopLevelBuild)parentBuild);
		}

		if (jobName.matches("test-subrepository-acceptance-pullrequest.*")) {
			return new PullRequestSubrepositoryTopLevelBuild(
				url, (TopLevelBuild)parentBuild);
		}

		if (jobName.contains("plugins")) {
			return new PluginsTopLevelBuild(url, (TopLevelBuild)parentBuild);
		}

		if (jobName.contains("portal")) {
			if (jobName.contains("upstream")) {
				return new UpstreamPortalTopLevelBuild(
					url, (TopLevelBuild)parentBuild);
			}

			return new PortalTopLevelBuild(url, (TopLevelBuild)parentBuild);
		}

		if (jobName.contains("qa-websites")) {
			return new QAWebsitesTopLevelBuild(url, (TopLevelBuild)parentBuild);
		}

		return new DefaultTopLevelBuild(url, (TopLevelBuild)parentBuild);
	}

	public static synchronized Build newBuildFromArchive(
		File archiveRootDir, String archiveName) {

		String originalUrlDependenciesFile =
			JenkinsResultsParserUtil.urlDependenciesFile;

		try {
			if (archiveRootDir != null) {
				JenkinsResultsParserUtil.urlDependenciesFile =
					JenkinsResultsParserUtil.combine(
						"file:", archiveRootDir.getPath(), "/");
			}

			String url = JenkinsResultsParserUtil.combine(
				Build.DEPENDENCIES_URL_TOKEN, "/", archiveName, "/",
				"archive.properties");

			Properties archiveProperties = new Properties();

			try {
				archiveProperties.load(
					new StringReader(
						JenkinsResultsParserUtil.toString(
							JenkinsResultsParserUtil.getLocalURL(url))));
			}
			catch (IOException ioException) {
				throw new RuntimeException(
					"Unable to find archive " + archiveName, ioException);
			}

			return newBuild(
				archiveProperties.getProperty("top.level.build.url"), null);
		}
		finally {
			JenkinsResultsParserUtil.urlDependenciesFile =
				originalUrlDependenciesFile;
		}
	}

	public static Build newBuildFromArchive(String archiveName) {
		return newBuildFromArchive(null, archiveName);
	}

	public static DefaultBuild newDefaultBuild(String url) {
		return new DefaultBuild(url);
	}

	private static final String _BUILD_URL_SUFFIX_REGEX =
		JenkinsResultsParserUtil.combine(
			"((?<axisVariable>AXIS_VARIABLE=[^,/]+(,[^/]+)?)|)/?",
			"((?<buildNumber>\\d+)|buildWithParameters\\?" +
				"(?<queryString>.*))/?");

	private static final String[] _TOKENS_BATCH = {
		"-batch", "-chrome", "-dist", "-edge", "-firefox", "-ie11", "-safari"
	};

	private static final MultiPattern _buildURLMultiPattern = new MultiPattern(
		JenkinsResultsParserUtil.combine(
			"\\w+://(?<master>[^/]+)/+job/+(?<jobName>[^/]+(/label=[^/]+)?)/",
			_BUILD_URL_SUFFIX_REGEX),
		JenkinsResultsParserUtil.combine(
			".*?Test/+[^/]+/+(?<master>test-[0-9]-[0-9]{1,2})/",
			"(?<jobName>[^/]+)/?", _BUILD_URL_SUFFIX_REGEX),
		JenkinsResultsParserUtil.combine(
			"file:/.*", "(?<master>test-[0-9]-[0-9]{1,2})/",
			"(?<jobName>[^/]+)/?", _BUILD_URL_SUFFIX_REGEX));

}