/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.testray.TestrayBuild;
import com.liferay.jenkins.results.parser.testray.TestrayFactory;
import com.liferay.jenkins.results.parser.testray.TestrayRoutine;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import org.json.JSONObject;

/**
 * @author Kenji Heigel
 */
public class UpstreamFailureUtil {

	public static synchronized List<String> getUpstreamJobFailures(
		String type, TopLevelBuild topLevelBuild) {

		if (_upstreamFailures.containsKey(type)) {
			return _upstreamFailures.get(type);
		}

		List<String> upstreamFailures = new ArrayList<>();

		_upstreamFailures.put(type, upstreamFailures);

		if (!isUpstreamComparisonAvailable(topLevelBuild)) {
			return upstreamFailures;
		}

		TopLevelBuildReport topLevelBuildReport =
			getUpstreamTopLevelBuildReport(topLevelBuild);

		if ((topLevelBuildReport == null) ||
			(topLevelBuildReport.getDownstreamBuildReports() == null)) {

			return upstreamFailures;
		}

		for (DownstreamBuildReport downstreamBuildReport :
				topLevelBuildReport.getDownstreamBuildReports()) {

			String result = downstreamBuildReport.getResult();

			if (!result.equals("FAILURE") && !result.equals("REGRESSION") &&
				!result.equals("UNSTABLE")) {

				continue;
			}

			String batchName = _getBatchName(
				downstreamBuildReport.getBatchName());

			if (type.equals("build")) {
				upstreamFailures.add(
					_formatUpstreamBuildFailure(batchName, result));
			}
			else if (type.equals("test")) {
				for (TestReport testReport :
						downstreamBuildReport.getTestReports()) {

					String testReportStatus = testReport.getStatus();

					if (!testReportStatus.equals("PASSED")) {
						upstreamFailures.add(
							_formatUpstreamTestFailure(
								batchName, testReport.getTestName()));
					}
				}
			}
		}

		return upstreamFailures;
	}

	public static String getUpstreamJobFailuresSHA(
		TopLevelBuild topLevelBuild) {

		if (isUpstreamComparisonAvailable(topLevelBuild)) {
			return _upstreamJobFailuresSHA;
		}

		return "";
	}

	public static TestrayBuild getUpstreamTestrayBuild(
		TopLevelBuild topLevelBuild) {

		if (isUpstreamComparisonAvailable(topLevelBuild)) {
			return _upstreamTestrayBuild;
		}

		return null;
	}

	public static TestrayBuild getUpstreamTestrayBuild(
		TopLevelBuild topLevelBuild, String upstreamBranchSHA) {

		TestrayRoutine testrayRoutine = _upstreamTestrayRoutine;

		if (testrayRoutine == null) {
			return null;
		}

		for (TestrayBuild testrayBuild : testrayRoutine.getTestrayBuilds(25)) {
			if (!Objects.equals(
					upstreamBranchSHA, testrayBuild.getPortalSHA())) {

				continue;
			}

			return testrayBuild;
		}

		return null;
	}

	public static TopLevelBuildReport getUpstreamTopLevelBuildReport(
		TopLevelBuild topLevelBuild) {

		if (isUpstreamComparisonAvailable(topLevelBuild)) {
			return _upstreamTopLevelBuildReport;
		}

		return null;
	}

	public static TopLevelBuildReport getUpstreamTopLevelBuildReport(
		TopLevelBuild topLevelBuild, String upstreamBranchSHA) {

		if (upstreamBranchSHA == null) {
			return getUpstreamTopLevelBuildReport(topLevelBuild);
		}

		JobReport jobReport = JobReport.getInstance(
			topLevelBuild.getAcceptanceUpstreamJobURL());

		for (TopLevelBuildReport topLevelBuildReport :
				jobReport.getTopLevelBuildReports(25)) {

			String portalGitCommit = JenkinsResultsParserUtil.getBuildParameter(
				String.valueOf(topLevelBuildReport.getBuildURL()),
				"PORTAL_GIT_COMMIT");

			if (!Objects.equals(upstreamBranchSHA, portalGitCommit)) {
				continue;
			}

			return topLevelBuildReport;
		}

		return null;
	}

	public static boolean isUpstreamComparisonAvailable(
		TopLevelBuild topLevelBuild) {

		try {
			_init(topLevelBuild);
		}
		catch (Exception exception) {
			System.out.println("Unable to initialize upstream comparison");

			exception.printStackTrace();

			_upstreamComparisonAvailable = false;
		}

		return _upstreamComparisonAvailable;
	}

	public static void reset() {
		_upstreamComparisonAvailable = null;
		_upstreamJobFailuresSHA = null;
		_upstreamTestrayBuild = null;
		_upstreamTestrayRoutine = null;
		_upstreamTopLevelBuildReport = null;
	}

	private static String _formatUpstreamBuildFailure(
		String batchName, String testResult) {

		return JenkinsResultsParserUtil.combine(batchName, ",", testResult);
	}

	private static String _formatUpstreamTestFailure(
		String jobVariant, String testName) {

		return JenkinsResultsParserUtil.combine(testName, ",", jobVariant);
	}

	private static String _getBatchName(String jobVariant) {
		jobVariant = jobVariant.replaceAll("(.*)/.*", "$1");

		return jobVariant.replaceAll("_stable$", "");
	}

	private static String _getUpstreamComparison(String jobName) {
		try {
			Properties buildProperties =
				JenkinsResultsParserUtil.getBuildProperties();

			return buildProperties.getProperty(
				"upstream.comparison[" + jobName + "]", "true");
		}
		catch (Exception exception) {
			exception.printStackTrace();

			return "true";
		}
	}

	private static void _init(TopLevelBuild topLevelBuild) {
		if (_upstreamComparisonAvailable != null) {
			return;
		}

		if (!(topLevelBuild instanceof PortalBranchInformationBuild) ||
			Objects.equals(
				_getUpstreamComparison(topLevelBuild.getJobName()), "false")) {

			_upstreamComparisonAvailable = false;

			System.out.println(
				"Upstream comparison is disabled for " +
					topLevelBuild.getJobName());

			return;
		}

		_setUpstreamTestrayRoutine(topLevelBuild);

		if (_upstreamTestrayRoutine == null) {
			_upstreamComparisonAvailable = false;

			System.out.println("Unable to get upstream Testray routine");

			return;
		}

		_setUpstreamTestrayBuild(topLevelBuild);

		if (_upstreamTestrayBuild == null) {
			_upstreamComparisonAvailable = false;

			System.out.println("Unable to get upstream Testray build");

			return;
		}

		_setUpstreamTopLevelBuildReport();

		if (_upstreamTopLevelBuildReport == null) {
			_upstreamComparisonAvailable = false;

			System.out.println("Unable to get upstream top level build report");

			return;
		}

		_setUpstreamJobFailuresSHA();

		if (JenkinsResultsParserUtil.isNullOrEmpty(_upstreamJobFailuresSHA)) {
			_upstreamComparisonAvailable = false;

			System.out.println("Unable to get upstream acceptance build SHA");

			return;
		}

		_upstreamComparisonAvailable = true;
	}

	private static void _setUpstreamJobFailuresSHA() {
		TopLevelBuildReport upstreamTopLevelBuildReport =
			_upstreamTopLevelBuildReport;

		if (upstreamTopLevelBuildReport == null) {
			System.out.println(
				"Unable to get upstream acceptance failure data");

			_upstreamJobFailuresSHA = "";
		}

		Map<String, String> buildParameters =
			upstreamTopLevelBuildReport.getBuildParameters();

		_upstreamJobFailuresSHA = buildParameters.get("PORTAL_GIT_COMMIT");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(_upstreamJobFailuresSHA)) {
			return;
		}

		_upstreamJobFailuresSHA = JenkinsResultsParserUtil.getBuildParameter(
			String.valueOf(upstreamTopLevelBuildReport.getBuildURL()),
			"PORTAL_GIT_COMMIT");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(_upstreamJobFailuresSHA)) {
			return;
		}

		File testResultsJSONFile = new File(
			System.getenv("WORKSPACE"), "test.results.json");

		try {
			JenkinsResultsParserUtil.toFile(
				upstreamTopLevelBuildReport.getTestResultsJSONUserContentURL(),
				testResultsJSONFile);

			JSONObject upstreamJobFailuresJSONObject = new JSONObject(
				JenkinsResultsParserUtil.read(testResultsJSONFile));

			_upstreamJobFailuresSHA = upstreamJobFailuresJSONObject.getString(
				"SHA");
		}
		catch (Exception exception) {
			System.out.println(
				"Unable to get upstream acceptance failure data");

			_upstreamJobFailuresSHA = "";
		}
		finally {
			if (testResultsJSONFile.exists()) {
				JenkinsResultsParserUtil.delete(testResultsJSONFile);
			}
		}
	}

	private static void _setUpstreamTestrayBuild(TopLevelBuild topLevelBuild) {
		int buildCount = 0;

		String upstreamBranchName = topLevelBuild.getBranchName();

		if (topLevelBuild instanceof PullRequestSubrepositoryTopLevelBuild) {
			PullRequestSubrepositoryTopLevelBuild
				pullRequestSubrepositoryTopLevelBuild =
					(PullRequestSubrepositoryTopLevelBuild)topLevelBuild;

			upstreamBranchName =
				pullRequestSubrepositoryTopLevelBuild.
					getPortalUpstreamBranchName();
		}

		GitWorkingDirectory gitWorkingDirectory =
			GitWorkingDirectoryFactory.newGitWorkingDirectory(
				upstreamBranchName, (File)null, "liferay-portal");

		TestrayRoutine testrayRoutine = _upstreamTestrayRoutine;

		for (TestrayBuild testrayBuild : testrayRoutine.getTestrayBuilds(25)) {
			if (buildCount > 25) {
				break;
			}

			buildCount++;

			String portalSHA = testrayBuild.getPortalSHA();

			if (JenkinsResultsParserUtil.isNullOrEmpty(portalSHA) ||
				!gitWorkingDirectory.refContainsSHA("HEAD", portalSHA)) {

				continue;
			}

			TopLevelBuildReport topLevelBuildReport =
				testrayBuild.getTopLevelBuildReport();

			if (topLevelBuildReport == null) {
				continue;
			}

			List<DownstreamBuildReport> downstreamBuildReports =
				topLevelBuildReport.getDownstreamBuildReports();

			if ((downstreamBuildReports == null) ||
				downstreamBuildReports.isEmpty()) {

				continue;
			}

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Comparing with test results from ",
					String.valueOf(topLevelBuildReport.getBuildURL()),
					" at SHA ", portalSHA));

			_upstreamTestrayBuild = testrayBuild;

			break;
		}
	}

	private static void _setUpstreamTestrayRoutine(
		TopLevelBuild topLevelBuild) {

		PortalBranchInformationBuild portalBranchInformationBuild =
			(PortalBranchInformationBuild)topLevelBuild;

		Build.BranchInformation branchInformation =
			portalBranchInformationBuild.getPortalBranchInformation();

		try {
			String testHistoryRoutineURL = JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getBuildProperties(),
				"test.history.routine.url",
				branchInformation.getUpstreamBranchName());

			if (JenkinsResultsParserUtil.isNullOrEmpty(testHistoryRoutineURL)) {
				return;
			}

			_upstreamTestrayRoutine = TestrayFactory.newTestrayRoutine(
				testHistoryRoutineURL);
		}
		catch (IOException ioException) {
			System.out.println("Unable to set upstream Testray routine");
		}
	}

	private static void _setUpstreamTopLevelBuildReport() {
		TestrayBuild upstreamTestrayBuild = _upstreamTestrayBuild;

		if (_upstreamTestrayBuild == null) {
			return;
		}

		_upstreamTopLevelBuildReport =
			upstreamTestrayBuild.getTopLevelBuildReport();
	}

	private static Boolean _upstreamComparisonAvailable;
	private static final Map<String, List<String>> _upstreamFailures =
		new HashMap<>();
	private static String _upstreamJobFailuresSHA;
	private static TestrayBuild _upstreamTestrayBuild;
	private static TestrayRoutine _upstreamTestrayRoutine;
	private static TopLevelBuildReport _upstreamTopLevelBuildReport;

}