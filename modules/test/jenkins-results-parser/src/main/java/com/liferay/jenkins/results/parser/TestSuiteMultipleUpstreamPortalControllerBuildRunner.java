/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class TestSuiteMultipleUpstreamPortalControllerBuildRunner
	<S extends ControllerPortalTopLevelBuildData>
		extends BaseUpstreamPortalControllerBuildRunner<S> {

	@Override
	public void run() {
		List<String> testSuiteNames = _getSelectedTestSuiteNames();

		if (testSuiteNames.isEmpty()) {
			System.out.println("There are no test suites to run at this time.");

			keepJenkinsBuild(false);

			return;
		}

		S buildData = getBuildData();

		String portalBranchSHA = buildData.getPortalBranchSHA();

		for (String testSuiteName : testSuiteNames) {
			JSONObject previousBuildJSONObject =
				_getPreviousTestSuiteBuildJSONObject(testSuiteName);

			if ((previousBuildJSONObject != null) &&
				_previousBuildHasCurrentSHA(
					previousBuildJSONObject, portalBranchSHA)) {

				System.out.println(
					testSuiteName + " was invoked on this SHA already: " +
						portalBranchSHA);

				continue;
			}

			String invocationJobURL = getInvocationJobURL(testSuiteName);

			Map<String, String> invocationParameters = new HashMap<>();

			invocationParameters.putAll(buildData.getBuildParameters());

			invocationParameters.put("CI_TEST_SUITE", testSuiteName);
			invocationParameters.put(
				"JENKINS_GITHUB_BRANCH_NAME",
				buildData.getJenkinsGitHubBranchName());
			invocationParameters.put(
				"JENKINS_GITHUB_BRANCH_USERNAME",
				buildData.getJenkinsGitHubUsername());
			invocationParameters.put(
				"PARENT_BUILD_URL", buildData.getBuildURL());
			invocationParameters.put("PORTAL_GIT_COMMIT", portalBranchSHA);
			invocationParameters.put(
				"PORTAL_GITHUB_URL", buildData.getPortalGitHubURL());
			invocationParameters.put(
				"SLAVE_LABEL", getSlaveLabel(testSuiteName));
			invocationParameters.put(
				"TEST_PORTAL_BUILD_PROFILE",
				getTestPortalBuildProfile(testSuiteName));

			String testrayProjectName = getTestrayProjectName(testSuiteName);

			if (testrayProjectName != null) {
				String testrayRoutineName = JenkinsResultsParserUtil.combine(
					"[", buildData.getPortalUpstreamBranchName(), "] ci:test:",
					testSuiteName);

				String testrayBuildName = JenkinsResultsParserUtil.combine(
					testrayRoutineName, " - ",
					String.valueOf(buildData.getBuildNumber()), " - ",
					JenkinsResultsParserUtil.toDateString(
						new Date(buildData.getStartTime()),
						"yyyy-MM-dd[HH:mm:ss]", "America/Los_Angeles"));

				if (getTestrayRoutineName(testSuiteName) != null) {
					testrayRoutineName = getTestrayRoutineName(testSuiteName);
				}

				invocationParameters.put(
					"TESTRAY_BUILD_NAME", testrayBuildName);
				invocationParameters.put(
					"TESTRAY_PROJECT_NAME", testrayProjectName);
				invocationParameters.put(
					"TESTRAY_ROUTINE_NAME", testrayRoutineName);
			}

			invocationParameters.put(
				"TESTRAY_SLACK_CHANNELS",
				getTestraySlackChannels(testSuiteName));
			invocationParameters.put(
				"TESTRAY_SLACK_ICON_EMOJI",
				getTestraySlackIconEmoji(testSuiteName));
			invocationParameters.put(
				"TESTRAY_SLACK_USERNAME",
				getTestraySlackUsername(testSuiteName));

			try {
				long queueId = JenkinsResultsParserUtil.invokeJenkinsBuild(
					invocationJobURL, invocationParameters);

				if (queueId == 0) {
					throw new RuntimeException(
						"Unable to invoke " + invocationJobURL);
				}

				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Job for '", testSuiteName, "' was invoked at ",
						invocationJobURL));

				_invokedTestSuiteNames.add(testSuiteName);
			}
			catch (Exception exception) {
				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Unable to invoke a new build for test suite, '",
						testSuiteName, "'"));

				exception.printStackTrace();
			}
		}

		boolean keepLogs = true;

		if (_invokedTestSuiteNames.isEmpty()) {
			keepLogs = false;
		}

		keepJenkinsBuild(keepLogs);

		StringBuilder sb = new StringBuilder();

		sb.append(JenkinsResultsParserUtil.join(", ", _invokedTestSuiteNames));
		sb.append(",");
		sb.append(" <strong>GIT ID</strong> - ");
		sb.append("<a href=\"https://github.com/");
		sb.append(buildData.getPortalGitHubUsername());
		sb.append("/");
		sb.append(buildData.getPortalGitHubRepositoryName());
		sb.append("/commit/");

		sb.append(portalBranchSHA);

		sb.append("\">");

		sb.append(getPortalBranchAbbreviatedSHA());

		sb.append("</a>");

		buildData.setBuildDescription(sb.toString());

		updateBuildDescription();
	}

	@Override
	public void tearDown() {
	}

	protected TestSuiteMultipleUpstreamPortalControllerBuildRunner(
		S buildData) {

		super(buildData);
	}

	@Override
	protected String getInvocationJobName() {
		S buildData = getBuildData();

		return JenkinsResultsParserUtil.combine(
			"test-portal-testsuite-upstream(",
			buildData.getPortalUpstreamBranchName(), ")");
	}

	@Override
	protected void invokeBuild() {
		throw new UnsupportedOperationException();
	}

	private List<Build> _getBuildHistory() {
		S buildData = getBuildData();

		Build build = BuildFactory.newBuild(buildData.getBuildURL(), null);

		Job job = JobFactory.newJob(buildData.getJobName());

		return job.getBuildHistory(build.getJenkinsMaster());
	}

	private List<String> _getBuildTestSuiteNames(Build build) {
		String buildDescription = build.getBuildDescription();

		if ((buildDescription == null) || buildDescription.isEmpty()) {
			return Collections.emptyList();
		}

		return Arrays.asList(buildDescription.split("\\s*,\\s*"));
	}

	private Map<String, Long> _getCandidateTestSuiteStaleDurations() {
		Properties buildProperties;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		S buildData = getBuildData();

		String upstreamBranchName = buildData.getPortalUpstreamBranchName();

		Map<String, Long> candidateTestSuiteStaleDurations =
			new LinkedHashMap<>();

		for (String testSuiteName : _getTestSuiteNames()) {
			String suiteStaleDuration = buildProperties.getProperty(
				JenkinsResultsParserUtil.combine(
					"portal.testsuite.upstream.stale.duration[",
					upstreamBranchName, "][", testSuiteName, "]"));

			if (suiteStaleDuration == null) {
				continue;
			}

			candidateTestSuiteStaleDurations.put(
				testSuiteName, Long.parseLong(suiteStaleDuration) * 60 * 1000);
		}

		return candidateTestSuiteStaleDurations;
	}

	private Map<String, Long> _getLatestTestSuiteStartTimes() {
		Map<String, Long> latestTestSuiteStartTimes = new LinkedHashMap<>();

		List<Build> builds = _getBuildHistory();

		BuildData buildData = getBuildData();

		Build currentBuild = BuildFactory.newBuild(
			buildData.getBuildURL(), null);

		builds.remove(currentBuild);

		for (String testSuiteName : _getTestSuiteNames()) {
			for (Build build : builds) {
				List<String> buildTestSuiteNames = _getBuildTestSuiteNames(
					build);

				if (buildTestSuiteNames.contains(testSuiteName)) {
					latestTestSuiteStartTimes.put(
						testSuiteName, build.getStartTime());

					break;
				}
			}
		}

		return latestTestSuiteStartTimes;
	}

	private JSONObject _getPreviousTestSuiteBuildJSONObject(
		String testSuiteName) {

		for (JSONObject previousBuildJSONObject :
				getPreviousBuildJSONObjects()) {

			String description = previousBuildJSONObject.optString(
				"description", "");

			if (description.contains("EXPIRE") ||
				description.contains("SKIPPED")) {

				continue;
			}

			if (description.contains(testSuiteName)) {
				return previousBuildJSONObject;
			}
		}

		return null;
	}

	private List<String> _getSelectedTestSuiteNames() {
		if (_selectedTestSuiteNames != null) {
			return _selectedTestSuiteNames;
		}

		_selectedTestSuiteNames = new ArrayList<>();

		Map<String, Long> candidateTestSuiteStaleDurations =
			_getCandidateTestSuiteStaleDurations();

		Map<String, Long> latestTestSuiteStartTimes =
			_getLatestTestSuiteStartTimes();

		S buildData = getBuildData();

		Long startTime = buildData.getStartTime();

		for (Map.Entry<String, Long> entry :
				candidateTestSuiteStaleDurations.entrySet()) {

			String testSuiteName = entry.getKey();

			if (!latestTestSuiteStartTimes.containsKey(testSuiteName)) {
				_selectedTestSuiteNames.add(testSuiteName);

				continue;
			}

			Long testSuiteIdleDuration =
				startTime - latestTestSuiteStartTimes.get(testSuiteName);

			if (testSuiteIdleDuration > entry.getValue()) {
				_selectedTestSuiteNames.add(testSuiteName);
			}
		}

		return _selectedTestSuiteNames;
	}

	private List<String> _getTestSuiteNames() {
		S buildData = getBuildData();

		try {
			return JenkinsResultsParserUtil.getBuildPropertyAsList(
				true,
				JenkinsResultsParserUtil.combine(
					"portal.testsuite.upstream.suites[",
					buildData.getPortalUpstreamBranchName(), "]"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	private boolean _previousBuildHasCurrentSHA(
		JSONObject previousBuildJSONObject, String portalBranchSHA) {

		if (previousBuildJSONObject == null) {
			return false;
		}

		String description = previousBuildJSONObject.optString(
			"description", "");

		Matcher matcher = _portalBranchSHAPattern.matcher(description);

		if (!matcher.find()) {
			return false;
		}

		String previousPortalBranchSHA = matcher.group("branchSHA");

		return portalBranchSHA.equals(previousPortalBranchSHA);
	}

	private static final Pattern _portalBranchSHAPattern = Pattern.compile(
		"<strong>GIT ID</strong> - <a href=\"https://github.com/[^/]+/[^/]+/" +
			"commit/(?<branchSHA>[0-9a-f]{40})\">[0-9a-f]{7}</a>");

	private final List<String> _invokedTestSuiteNames = new ArrayList<>();
	private List<String> _selectedTestSuiteNames;

}