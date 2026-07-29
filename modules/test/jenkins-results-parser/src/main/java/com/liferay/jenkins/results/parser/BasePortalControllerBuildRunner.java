/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BasePortalControllerBuildRunner
	<S extends PortalTopLevelBuildData>
		extends BaseBuildRunner<S> {

	@Override
	public Workspace getWorkspace() {
		if (_workspace != null) {
			return _workspace;
		}

		_workspace = WorkspaceFactory.newWorkspace();

		return _workspace;
	}

	@Override
	public void run() {
		retirePreviousBuilds();

		if (allowConcurrentBuildsUniqueSHA()) {
			if (previousBuildHasCurrentSHA()) {
				_updateBuildDescription();

				return;
			}

			invokeBuild();

			return;
		}

		if (allowConcurrentBuilds() || expirePreviousBuild()) {
			invokeBuild();

			return;
		}

		if (previousBuildHasCurrentSHA()) {
			_updateBuildDescription();

			return;
		}

		S buildData = getBuildData();

		if (previousBuildHasExistingInvocation()) {
			buildData.setBuildDescription(
				"<strong>SKIPPED</strong> - Job was already invoked");

			super.updateBuildDescription();

			return;
		}

		if (previousBuildHasRunningInvocation()) {
			buildData.setBuildDescription(
				"<strong>SKIPPED</strong> - Job is already running");

			super.updateBuildDescription();

			return;
		}

		invokeBuild();
	}

	protected BasePortalControllerBuildRunner(S buildData) {
		super(buildData);
	}

	protected boolean allowConcurrentBuilds() {
		String allowConcurrentBuildsString = Environment.get(
			"ALLOW_CONCURRENT_BUILDS");

		if (allowConcurrentBuildsString == null) {
			return false;
		}

		allowConcurrentBuildsString = allowConcurrentBuildsString.toLowerCase();
		allowConcurrentBuildsString = allowConcurrentBuildsString.trim();

		return allowConcurrentBuildsString.equals("true");
	}

	protected boolean allowConcurrentBuildsUniqueSHA() {
		String allowConcurrentBuildsUniqueSHAString = Environment.get(
			"ALLOW_CONCURRENT_BUILDS_UNIQUE_SHA");

		if (allowConcurrentBuildsUniqueSHAString == null) {
			return false;
		}

		allowConcurrentBuildsUniqueSHAString =
			allowConcurrentBuildsUniqueSHAString.toLowerCase();
		allowConcurrentBuildsUniqueSHAString =
			allowConcurrentBuildsUniqueSHAString.trim();

		return allowConcurrentBuildsUniqueSHAString.equals("true");
	}

	protected boolean expirePreviousBuild() {
		for (JSONObject previousBuildJSONObject :
				getPreviousBuildJSONObjects()) {

			String description = previousBuildJSONObject.optString(
				"description", "");

			if (!description.contains("IN PROGRESS") &&
				!description.contains("IN QUEUE")) {

				continue;
			}

			String controllerBuildURL = previousBuildJSONObject.getString(
				"url");

			Matcher buildURLMatcher = _buildURLPattern.matcher(
				controllerBuildURL);

			if (!buildURLMatcher.find()) {
				continue;
			}

			Matcher jobURLMatcher = _jobURLPattern.matcher(description);

			if (!jobURLMatcher.find()) {
				continue;
			}

			Map<String, String> parameters = new HashMap<>();

			parameters.put("CONTROLLER_BUILD_URL", controllerBuildURL);

			JenkinsMaster jenkinsMaster = JenkinsMaster.getInstance(
				jobURLMatcher.group("masterHostname"));

			String jobName = jobURLMatcher.group("jobName");

			if (jenkinsMaster.isBuildQueued(jobName, parameters) ||
				jenkinsMaster.isBuildInProgress(jobName, parameters)) {

				long timestamp = previousBuildJSONObject.optLong(
					"timestamp", 0);

				if (timestamp == 0) {
					continue;
				}

				long inProgressBuildDuration =
					JenkinsResultsParserUtil.getCurrentTimeMillis() - timestamp;

				System.out.println(
					JenkinsResultsParserUtil.combine(
						"In progress build started ",
						JenkinsResultsParserUtil.toDurationString(
							inProgressBuildDuration),
						" ago"));

				if (inProgressBuildDuration < _getControllerBuildTimeout()) {
					return false;
				}
			}

			description = description.replace("IN PROGRESS", "EXPIRE");
			description = description.replace("IN QUEUE", "EXPIRE");

			JenkinsResultsParserUtil.updateBuildDescription(
				description, previousBuildJSONObject.getInt("number"),
				buildURLMatcher.group("jobName"),
				buildURLMatcher.group("masterHostname"));

			return true;
		}

		return false;
	}

	protected String getInvocationCohortName() {
		String invocationCohortName = Environment.get("INVOCATION_COHORT_NAME");

		if ((invocationCohortName != null) && !invocationCohortName.isEmpty()) {
			return invocationCohortName;
		}

		BuildData buildData = getBuildData();

		return buildData.getCohortName();
	}

	protected String getPortalBranchAbbreviatedSHA() {
		S buildData = getBuildData();

		String portalBranchSHA = buildData.getPortalBranchSHA();

		return portalBranchSHA.substring(0, 7);
	}

	protected String getPortalGitHubCompareURL() {
		S buildData = getBuildData();

		return buildData.getPortalGitHubCompareURL(
			getPreviousBuildPortalBranchSHA());
	}

	protected String getPreviousBuildPortalBranchSHA() {
		S buildData = getBuildData();

		String currentPortalBranchSHA = buildData.getPortalBranchSHA();

		for (JSONObject previousBuildJSONObject :
				getPreviousBuildJSONObjects()) {

			String description = previousBuildJSONObject.optString(
				"description", "");

			Matcher matcher = _portalBranchSHAPattern.matcher(description);

			if (!matcher.find()) {
				continue;
			}

			String previousPortalBranchSHA = matcher.group("branchSHA");

			if (currentPortalBranchSHA.equals(previousPortalBranchSHA)) {
				continue;
			}

			return previousPortalBranchSHA;
		}

		return null;
	}

	protected abstract void invokeBuild();

	protected boolean previousBuildHasCurrentSHA() {
		String portalBranchSHA = getPortalBranchAbbreviatedSHA();

		for (JSONObject previousBuildJSONObject :
				getPreviousBuildJSONObjects()) {

			String description = previousBuildJSONObject.optString(
				"description", "");

			if (description.contains("EXPIRE") ||
				description.contains("SKIPPED")) {

				continue;
			}

			if (description.contains(portalBranchSHA)) {
				return true;
			}
		}

		return false;
	}

	protected boolean previousBuildHasExistingInvocation() {
		for (JSONObject previousBuildJSONObject :
				getPreviousBuildJSONObjects()) {

			String description = previousBuildJSONObject.optString(
				"description", "");

			if (description.contains("IN QUEUE")) {
				return true;
			}
		}

		return false;
	}

	protected boolean previousBuildHasRunningInvocation() {
		for (JSONObject previousBuildJSONObject :
				getPreviousBuildJSONObjects()) {

			String description = previousBuildJSONObject.optString(
				"description", "");

			if (!description.contains("IN PROGRESS")) {
				continue;
			}

			Matcher buildURLMatcher = _buildURLPattern.matcher(description);

			if (!buildURLMatcher.find()) {
				continue;
			}

			String buildURL = buildURLMatcher.group();

			try {
				JSONObject jsonObject = JenkinsResultsParserUtil.toJSONObject(
					JenkinsResultsParserUtil.getLocalURL(
						buildURL + "/api/json?tree=result"));

				Object result = jsonObject.get("result");

				if (result.equals(JSONObject.NULL)) {
					return true;
				}

				JSONObject injectedEnvVarsJSONObject =
					JenkinsResultsParserUtil.toJSONObject(
						JenkinsResultsParserUtil.getLocalURL(
							previousBuildJSONObject.getString("url") +
								"/injectedEnvVars/api/json"));

				JSONObject envMapJSONObject =
					injectedEnvVarsJSONObject.getJSONObject("envMap");

				StringBuilder sb = new StringBuilder();

				sb.append("<strong style=\"color: red\">FAILURE</strong> - ");
				sb.append(buildURLMatcher.group());

				Matcher portalBranchSHAMatcher =
					_portalBranchSHAPattern.matcher(description);
				Matcher portalGitHubCompareURLMatcher =
					_portalGitHubCompareURLPattern.matcher(description);

				if (portalBranchSHAMatcher.find() ||
					portalGitHubCompareURLMatcher.find()) {

					sb.append("<ul>");

					if (portalBranchSHAMatcher.find()) {
						sb.append("<li>");
						sb.append(portalBranchSHAMatcher.group());
						sb.append("</li>");
					}

					if (portalGitHubCompareURLMatcher.find()) {
						sb.append("<li>");
						sb.append(portalGitHubCompareURLMatcher.group());
						sb.append("</li>");
					}

					sb.append("</ul>");
				}

				JenkinsResultsParserUtil.updateBuildDescription(
					sb.toString(),
					Integer.valueOf(envMapJSONObject.getString("BUILD_NUMBER")),
					envMapJSONObject.getString("JOB_NAME"),
					envMapJSONObject.getString("HOSTNAME"));
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		return false;
	}

	private long _getControllerBuildTimeout() {
		try {
			S buildData = getBuildData();

			String controllerBuildTimeout =
				JenkinsResultsParserUtil.getProperty(
					JenkinsResultsParserUtil.getBuildProperties(),
					"controller.build.timeout", buildData.getJobName());

			if (!JenkinsResultsParserUtil.isNullOrEmpty(
					controllerBuildTimeout)) {

				return Long.parseLong(controllerBuildTimeout) * 1000;
			}

			return _CONTROLLER_BUILD_TIMEOUT_DEFAULT;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private void _updateBuildDescription() {
		S buildData = getBuildData();

		buildData.setBuildDescription(
			JenkinsResultsParserUtil.combine(
				"<strong>SKIPPED</strong> - <a href=\"https://github.com/",
				"liferay/", buildData.getPortalGitHubRepositoryName(),
				"/commit/", buildData.getPortalBranchSHA(), "\">",
				getPortalBranchAbbreviatedSHA(), "</a> was already ran"));

		super.updateBuildDescription();
	}

	private static final Integer _CONTROLLER_BUILD_TIMEOUT_DEFAULT =
		1000 * 60 * 60 * 24;

	private static final Pattern _buildURLPattern = Pattern.compile(
		"https://(?<masterHostname>test-\\d+-\\d+)(-aws)?\\.liferay\\.com/" +
			"job/(?<jobName>[^/]+)/(?<buildNumber>\\d+)/?");
	private static final Pattern _jobURLPattern = Pattern.compile(
		"https://(?<masterHostname>test-\\d+-\\d+)\\.liferay\\.com/job/" +
			"(?<jobName>[^/\"]+)/?");
	private static final Pattern _portalBranchSHAPattern = Pattern.compile(
		"<strong>Git ID:</strong> <a href=\"https://github.com/[^/]+/[^/]+/" +
			"commit/(?<branchSHA>[0-9a-f]{40})\">[0-9a-f]{7}</a>");
	private static final Pattern _portalGitHubCompareURLPattern =
		Pattern.compile(
			"<strong>Git Compare:</strong> <a href=\"[^\"]+\">[^<]+</a>");

	private Workspace _workspace;

}