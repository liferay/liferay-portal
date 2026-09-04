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

	public static Build newBuild(
		DownstreamBuildReport cachedDownstreamBuildReport, Build parentBuild) {

		return newBuild(
			String.valueOf(cachedDownstreamBuildReport.getBuildURL()),
			cachedDownstreamBuildReport,
			cachedDownstreamBuildReport.getJobVariant(), parentBuild);
	}

	public static Build newBuild(String buildURL, Build parentBuild) {
		return newBuild(buildURL, null, null, parentBuild);
	}

	public static Build newBuild(
		String buildURL, DownstreamBuildReport cachedDownstreamBuildReport,
		String jobVariant, Build parentBuild) {

		buildURL = JenkinsResultsParserUtil.getLocalURL(buildURL);

		Matcher matcher = _buildURLMultiPattern.find(buildURL);

		if (matcher == null) {
			throw new IllegalArgumentException(
				"Invalid Jenkins build URL: " + buildURL);
		}

		String jobName = matcher.group("jobName");

		if (jobName.contains("-controller")) {
			return new ControllerTopLevelBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("app-server-bundle-builder")) {
			return new AppServerBundleDownstreamBuild(
				buildURL, cachedDownstreamBuildReport,
				(TopLevelBuild)parentBuild);
		}

		if (jobVariant == null) {
			if (cachedDownstreamBuildReport != null) {
				jobVariant = cachedDownstreamBuildReport.getJobVariant();
			}
			else {
				jobVariant = "";
			}
		}

		if (jobName.contains("-downstream")) {
			String queryString = matcher.group("queryString");

			if ((queryString != null) && queryString.contains("JOB_VARIANT")) {
				jobVariant = queryString.replaceAll(
					".*JOB_VARIANT=([^&]+).*", "$1");
			}

			if (JenkinsResultsParserUtil.isNullOrEmpty(jobVariant)) {
				jobVariant = JenkinsResultsParserUtil.getBuildParameter(
					buildURL, "JOB_VARIANT", parentBuild);
			}

			if (jobVariant != null) {
				if (jobVariant.contains("functional") ||
					jobVariant.contains("test-portal-environment") ||
					jobVariant.contains("test-portal-fixpack-environment")) {

					return new PoshiJUnitDownstreamBuild(
						buildURL, cachedDownstreamBuildReport,
						(TopLevelBuild)parentBuild);
				}
				else if (jobVariant.startsWith("integration") ||
						 jobVariant.startsWith("js-unit") ||
						 jobVariant.startsWith("modules-compile") ||
						 jobVariant.startsWith("modules-semantic-versioning") ||
						 jobVariant.startsWith("playwright-js") ||
						 jobVariant.startsWith("rest-builder") ||
						 jobVariant.startsWith(
							 "rest-builder-and-service-builder") ||
						 jobVariant.startsWith("semantic-versioning") ||
						 jobVariant.startsWith("service-builder") ||
						 jobVariant.startsWith("workspaces-js-unit")) {

					return new JUnitDownstreamBuild(
						buildURL, cachedDownstreamBuildReport,
						(TopLevelBuild)parentBuild);
				}
				else if (jobVariant.startsWith("modules-integration") ||
						 jobVariant.startsWith("modules-unit") ||
						 jobVariant.startsWith("workspaces-unit")) {

					return new ModulesJUnitDownstreamBuild(
						buildURL, cachedDownstreamBuildReport,
						(TopLevelBuild)parentBuild);
				}
			}

			return new DefaultDownstreamBuild(
				buildURL, cachedDownstreamBuildReport,
				(TopLevelBuild)parentBuild);
		}

		if (jobName.contains("-source-format")) {
			return new SourceFormatBuild(buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.contains("-validation")) {
			return new ValidationBuild(buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.contains("root-cause-analysis-tool-batch")) {
			return new FreestyleBatchBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.contains("legacy")) {
			return new LegacyTopLevelBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("root-cause-analysis-tool")) {
			return new RootCauseAnalysisToolBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("sanitize-language")) {
			return new SanitizeLanguageTopLevelBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("test-docker-release-pullrequest")) {
			return new DockerReleaseTopLevelBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.startsWith("test-jenkins-acceptance-pullrequest")) {
			return new JenkinsTopLevelBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.startsWith("test-plugins-acceptance-pullrequest")) {
			return new PullRequestPluginsTopLevelBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("test-plugins-extraapps")) {
			return new ExtraAppsPluginsTopLevelBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("test-plugins-marketplaceapp")) {
			return new MarketplaceAppPluginsTopLevelBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("test-portal-app-release")) {
			return new PortalAppReleaseTopLevelBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("forward-pullrequest") ||
			jobName.startsWith("test-portal-acceptance-pullrequest")) {

			return new PullRequestPortalTopLevelBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("test-portal-fixpack-release")) {
			return new PortalFixpackReleasePortalTopLevelBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("test-portal-hotfix-release")) {
			return new PortalHotfixReleasePortalTopLevelBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.equals("test-portal-release")) {
			return new PortalReleasePortalTopLevelBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.matches("test-subrepository-acceptance-pullrequest.*")) {
			return new PullRequestSubrepositoryTopLevelBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.contains("plugins")) {
			return new PluginsTopLevelBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.contains("portal")) {
			if (jobName.contains("upstream")) {
				return new UpstreamPortalTopLevelBuild(
					buildURL, (TopLevelBuild)parentBuild);
			}

			return new PortalTopLevelBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		if (jobName.contains("qa-websites")) {
			return new QAWebsitesTopLevelBuild(
				buildURL, (TopLevelBuild)parentBuild);
		}

		return new DefaultTopLevelBuild(buildURL, (TopLevelBuild)parentBuild);
	}

	public static Build newBuild(
		String buildURL, String jobVariant, Build parentBuild) {

		return newBuild(buildURL, null, jobVariant, parentBuild);
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

			String buildURL = JenkinsResultsParserUtil.combine(
				Build.DEPENDENCIES_URL_TOKEN, "/", archiveName, "/",
				"archive.properties");

			Properties archiveProperties = new Properties();

			try {
				archiveProperties.load(
					new StringReader(
						JenkinsResultsParserUtil.toString(
							JenkinsResultsParserUtil.getLocalURL(buildURL))));
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

	public static DefaultBuild newDefaultBuild(String buildURL) {
		return new DefaultBuild(buildURL);
	}

	private static final String _BUILD_URL_SUFFIX_REGEX =
		JenkinsResultsParserUtil.combine(
			"((?<axisVariable>AXIS_VARIABLE=[^,/]+(,[^/]+)?)|)/?",
			"((?<buildNumber>\\d+)|buildWithParameters\\?" +
				"(?<queryString>.*))/?");

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