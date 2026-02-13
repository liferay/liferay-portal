/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.BuildReport;
import com.liferay.jenkins.results.parser.ControllerBuildReport;
import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.NotificationUtil;
import com.liferay.jenkins.results.parser.ParallelExecutor;
import com.liferay.jenkins.results.parser.PluginsWorkspaceGitRepository;
import com.liferay.jenkins.results.parser.PortalFixpackRelease;
import com.liferay.jenkins.results.parser.PortalHotfixRelease;
import com.liferay.jenkins.results.parser.PortalRelease;
import com.liferay.jenkins.results.parser.PortalWorkspace;
import com.liferay.jenkins.results.parser.PortalWorkspaceGitRepository;
import com.liferay.jenkins.results.parser.PullRequest;
import com.liferay.jenkins.results.parser.QAWebsitesGitRepositoryJob;
import com.liferay.jenkins.results.parser.QAWebsitesWorkspaceGitRepository;
import com.liferay.jenkins.results.parser.TestSuiteJob;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.Workspace;
import com.liferay.jenkins.results.parser.WorkspaceGitRepository;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.job.property.JobPropertyFactory;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.FunctionalAxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.JSUnitAxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.JUnitAxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.ModulesAxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.PlaywrightAxisTestClassGroup;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * @author Michael Hashimoto
 */
public class TestrayImporter {

	public TestrayImporter(
		BuildDatabase buildDatabase, TopLevelBuildReport topLevelBuildReport) {

		if (topLevelBuildReport == null) {
			throw new RuntimeException(
				"Please provide a valid top level build report");
		}

		_topLevelBuildReport = topLevelBuildReport;

		_jobs = buildDatabase.getJobs();
		_portalFixpackReleases = buildDatabase.getPortalFixpackReleases();
		_portalHotfixReleases = buildDatabase.getPortalHotfixReleases();
		_portalReleases = buildDatabase.getPortalReleases();
		_pullRequests = buildDatabase.getPullRequests();
		_workspaces = buildDatabase.getWorkspaces();
	}

	public String getJenkinsBuildDescription() {
		Document document = DocumentHelper.createDocument();

		Element rootElement = document.addElement("div");

		Dom4JUtil.addToElement(
			rootElement,
			_getJenkinsBuildDescriptionElement(
				"Jenkins Build",
				JenkinsResultsParserUtil.combine(
					_topLevelBuildReport.getJobName(), "#",
					String.valueOf(_topLevelBuildReport.getBuildNumber())),
				String.valueOf(_topLevelBuildReport.getBuildURL())),
			_getJenkinsBuildDescriptionElement(
				"Jenkins Report", "jenkins-report.html",
				String.valueOf(_topLevelBuildReport.getJenkinsReportURL())),
			_getJenkinsBuildDescriptionElement(
				"Jenkins Suite", _topLevelBuildReport.getTestSuiteName()));

		PullRequest pullRequest = getPullRequest();

		if (pullRequest != null) {
			Dom4JUtil.addToElement(
				rootElement,
				_getJenkinsBuildDescriptionElement(
					"Pull Request",
					JenkinsResultsParserUtil.combine(
						pullRequest.getReceiverUsername(), "#",
						pullRequest.getNumber()),
					pullRequest.getHtmlURL()));
		}

		Map<Long, TestrayBuild> testrayBuildMap = new HashMap<>();

		for (TestrayBuild testrayBuild : _testrayBuilds.values()) {
			testrayBuildMap.put(testrayBuild.getID(), testrayBuild);
		}

		int i = 0;

		for (Map.Entry<Long, TestrayBuild> testrayBuildEntry :
				testrayBuildMap.entrySet()) {

			String testrayRoutineTitle = "Testray Routine";

			if (i > 0) {
				testrayRoutineTitle = JenkinsResultsParserUtil.combine(
					testrayRoutineTitle, " (", String.valueOf(i), ")");
			}

			TestrayBuild testrayBuild = testrayBuildEntry.getValue();

			TestrayRoutine testrayRoutine = testrayBuild.getTestrayRoutine();

			String testrayBuildTitle = "Testray Build";

			if (i > 0) {
				testrayBuildTitle = JenkinsResultsParserUtil.combine(
					testrayBuildTitle, " (", String.valueOf(i), ")");
			}

			Dom4JUtil.addToElement(
				rootElement,
				_getJenkinsBuildDescriptionElement(
					testrayRoutineTitle, testrayRoutine.getName(),
					String.valueOf(testrayRoutine.getURL())),
				_getJenkinsBuildDescriptionElement(
					testrayBuildTitle, testrayBuild.getName(),
					String.valueOf(testrayBuild.getURL())),
				_getJenkinsBuildDescriptionCodeElement(
					"Testray Build ID", String.valueOf(testrayBuild.getID())));

			i++;
		}

		String currentJobName = System.getenv("JOB_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(currentJobName)) {
			Dom4JUtil.addToElement(
				rootElement,
				_getJenkinsBuildDescriptionElement(
					"Testray Importer",
					JenkinsResultsParserUtil.combine(
						currentJobName, "#", System.getenv("BUILD_NUMBER")),
					System.getenv("BUILD_URL")));
		}

		try {
			String buildDescription = Dom4JUtil.format(rootElement, false);

			return buildDescription.replaceAll("\n", "<br />");
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public PortalFixpackRelease getPortalFixpackRelease() {
		if (_portalFixpackReleases.isEmpty()) {
			return null;
		}

		return _portalFixpackReleases.get(0);
	}

	public PortalHotfixRelease getPortalHotfixRelease() {
		if (_portalHotfixReleases.isEmpty()) {
			return null;
		}

		return _portalHotfixReleases.get(0);
	}

	public PortalRelease getPortalRelease() {
		if (_portalReleases.isEmpty()) {
			return null;
		}

		return _portalReleases.get(0);
	}

	public PullRequest getPullRequest() {
		if (_pullRequests.isEmpty()) {
			return null;
		}

		if (_pullRequests.size() == 1) {
			return _pullRequests.get(0);
		}

		Map<String, String> buildParameters =
			_topLevelBuildReport.getBuildParameters();

		String githubReceiverUsername = buildParameters.get(
			"GITHUB_RECEIVER_USERNAME");

		String pullRequestNumber = buildParameters.get(
			"GITHUB_PULL_REQUEST_NUMBER");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(githubReceiverUsername) &&
			!JenkinsResultsParserUtil.isNullOrEmpty(pullRequestNumber)) {

			for (PullRequest pullRequest : _pullRequests) {
				if (Objects.equals(
						pullRequest.getReceiverUsername(),
						githubReceiverUsername) &&
					Objects.equals(
						pullRequest.getNumber(), pullRequestNumber)) {

					return pullRequest;
				}
			}
		}

		return _pullRequests.get(0);
	}

	public synchronized TestrayBuild getTestrayBuild(File testBaseDir) {
		TestrayBuild testrayBuild = _testrayBuilds.get(testBaseDir);

		if (testrayBuild != null) {
			return testrayBuild;
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		Date testrayBuildDate = getTestrayBuildDate();
		String testrayBuildDescription = getTestrayBuildDescription();
		String testrayBuildSHA = getTestrayBuildSHA();

		try {
			String testrayBuildID = System.getenv("TESTRAY_BUILD_ID");

			TestrayRoutine testrayRoutine = getTestrayRoutine(testBaseDir);
			TestrayProductVersion testrayProductVersion =
				getTestrayProductVersion(testBaseDir);

			if ((testrayBuildID != null) && testrayBuildID.matches("\\d+")) {
				testrayBuild = testrayRoutine.getTestrayBuildByID(
					Long.parseLong(testrayBuildID));
			}

			String testrayBuildName = System.getenv("TESTRAY_BUILD_NAME");

			if ((testrayBuild == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(testrayBuildName)) {

				testrayBuild = testrayRoutine.createTestrayBuild(
					testrayProductVersion,
					_replaceEnvVars(testrayBuildName, true), testrayBuildDate,
					testrayBuildDescription, testrayBuildSHA);
			}

			testrayBuildID = _getBuildParameter("TESTRAY_BUILD_ID");

			if ((testrayBuild == null) && (testrayBuildID != null) &&
				testrayBuildID.matches("\\d+")) {

				testrayBuild = testrayRoutine.getTestrayBuildByID(
					Long.parseLong(testrayBuildID));
			}

			testrayBuildName = _getBuildParameter("TESTRAY_BUILD_NAME");

			if ((testrayBuild == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(testrayBuildName)) {

				testrayBuild = testrayRoutine.createTestrayBuild(
					testrayProductVersion,
					_replaceEnvVars(testrayBuildName, true), testrayBuildDate,
					testrayBuildDescription, testrayBuildSHA);
			}

			if (testrayBuild == null) {
				JobProperty jobProperty = _getJobProperty(
					"testray.build.id", testBaseDir);

				testrayBuildID = jobProperty.getValue();

				if ((testrayBuildID != null) &&
					testrayBuildID.matches("\\d+")) {

					testrayBuild = testrayRoutine.getTestrayBuildByID(
						Long.parseLong(testrayBuildID));
				}
			}

			if (testrayBuild == null) {
				JobProperty jobProperty = _getJobProperty(
					"testray.build.name", testBaseDir);

				testrayBuildName = jobProperty.getValue();

				if (!JenkinsResultsParserUtil.isNullOrEmpty(testrayBuildName)) {
					testrayBuild = testrayRoutine.createTestrayBuild(
						testrayProductVersion,
						_replaceEnvVars(testrayBuildName, true),
						testrayBuildDate, testrayBuildDescription,
						testrayBuildSHA);
				}
			}
		}
		finally {
			if (testrayBuild != null) {
				_testrayBuilds.put(testBaseDir, testrayBuild);

				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Testray Build ", String.valueOf(testrayBuild.getURL()),
						" created in ",
						JenkinsResultsParserUtil.toDurationString(
							JenkinsResultsParserUtil.getCurrentTimeMillis() -
								start)));

				return testrayBuild;
			}
		}

		throw new RuntimeException("Please set TESTRAY_BUILD_NAME");
	}

	public Date getTestrayBuildDate() {
		ControllerBuildReport controllerBuildReport =
			_topLevelBuildReport.getControllerBuildReport();

		if (controllerBuildReport != null) {
			return controllerBuildReport.getStartDate();
		}

		return _topLevelBuildReport.getStartDate();
	}

	public String getTestrayBuildDescription() {
		StringBuilder sb = new StringBuilder();

		PortalRelease portalRelease = getPortalRelease();

		if (portalRelease != null) {
			sb.append("Portal Release: ");
			sb.append(portalRelease.getPortalVersion());
			sb.append("; ");
		}

		PortalFixpackRelease portalFixpackRelease = getPortalFixpackRelease();

		if (portalFixpackRelease != null) {
			sb.append("Portal Fixpack: ");
			sb.append(portalFixpackRelease.getPortalFixpackVersion());
			sb.append("; ");
		}

		PortalHotfixRelease portalHotfixRelease = getPortalHotfixRelease();

		if (portalHotfixRelease != null) {
			sb.append("Portal Hotfix: ");
			sb.append(portalHotfixRelease.getPortalHotfixReleaseVersion());
			sb.append("; ");
		}

		sb.append("<a href=\"");
		sb.append(_topLevelBuildReport.getJenkinsReportURL());
		sb.append("\">Jenkins Report</a>");
		sb.append("; ");

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_getPortalWorkspaceGitRepository();

		if (portalWorkspaceGitRepository != null) {
			sb.append("Portal Branch: ");
			sb.append(portalWorkspaceGitRepository.getUpstreamBranchName());
			sb.append("; ");

			sb.append("Portal SHA: ");
			sb.append(portalWorkspaceGitRepository.getSenderBranchSHAShort());
			sb.append("; ");
		}

		PluginsWorkspaceGitRepository pluginsWorkspaceGitRepository =
			_getPluginsWorkspaceGitRepository();

		if (pluginsWorkspaceGitRepository != null) {
			sb.append("Plugins Branch: ");
			sb.append(pluginsWorkspaceGitRepository.getUpstreamBranchName());
			sb.append("; ");

			sb.append("Plugins SHA: ");
			sb.append(pluginsWorkspaceGitRepository.getSenderBranchSHAShort());
			sb.append("; ");
		}

		QAWebsitesWorkspaceGitRepository qaWebsitesWorkspaceGitRepository =
			_getQAWebsitesWorkspaceGitRepository();

		if (qaWebsitesWorkspaceGitRepository != null) {
			sb.append("QA Websites Branch: ");
			sb.append(qaWebsitesWorkspaceGitRepository.getUpstreamBranchName());
			sb.append("; ");

			sb.append("QA Websites SHA: ");
			sb.append(
				qaWebsitesWorkspaceGitRepository.getSenderBranchSHAShort());
			sb.append("; ");
		}

		return sb.toString();
	}

	public String getTestrayBuildSHA() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_getPortalWorkspaceGitRepository();

		if (portalWorkspaceGitRepository != null) {
			return portalWorkspaceGitRepository.getSenderBranchSHA();
		}

		PluginsWorkspaceGitRepository pluginsWorkspaceGitRepository =
			_getPluginsWorkspaceGitRepository();

		if (pluginsWorkspaceGitRepository != null) {
			return pluginsWorkspaceGitRepository.getSenderBranchSHA();
		}

		QAWebsitesWorkspaceGitRepository qaWebsitesWorkspaceGitRepository =
			_getQAWebsitesWorkspaceGitRepository();

		if (qaWebsitesWorkspaceGitRepository != null) {
			return qaWebsitesWorkspaceGitRepository.getSenderBranchSHA();
		}

		return null;
	}

	public synchronized TestrayProductVersion getTestrayProductVersion(
		File testBaseDir) {

		TestrayProductVersion testrayProductVersion =
			_testrayProductVersions.get(testBaseDir);

		if (testrayProductVersion != null) {
			return testrayProductVersion;
		}

		long start = System.currentTimeMillis();

		try {
			TestrayProject testrayProject = getTestrayProject(testBaseDir);

			String testrayProductVersionID = System.getenv(
				"TESTRAY_PRODUCT_VERSION_ID");

			if ((testrayProductVersionID != null) &&
				testrayProductVersionID.matches("\\d+")) {

				testrayProductVersion =
					testrayProject.getTestrayProductVersionByID(
						Long.parseLong(testrayProductVersionID));
			}

			String testrayProductVersionName = System.getenv(
				"TESTRAY_PRODUCT_VERSION_NAME");

			if ((testrayProductVersion == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(
					testrayProductVersionName)) {

				testrayProductVersion =
					testrayProject.createTestrayProductVersion(
						_replaceEnvVars(testrayProductVersionName, true));
			}

			testrayProductVersionID = _getBuildParameter(
				"TESTRAY_PRODUCT_VERSION_ID");

			if ((testrayProductVersion == null) &&
				(testrayProductVersionID != null) &&
				testrayProductVersionID.matches("\\d+")) {

				testrayProductVersion =
					testrayProject.getTestrayProductVersionByID(
						Long.parseLong(testrayProductVersionID));
			}

			testrayProductVersionName = _getBuildParameter(
				"TESTRAY_PRODUCT_VERSION_NAME");

			if ((testrayProductVersion == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(
					testrayProductVersionName)) {

				testrayProductVersion =
					testrayProject.createTestrayProductVersion(
						_replaceEnvVars(testrayProductVersionName, true));
			}

			if (testrayProductVersion == null) {
				JobProperty jobProperty = _getJobProperty(
					"testray.product.version.id", testBaseDir);

				testrayProductVersionID = jobProperty.getValue();

				if ((testrayProductVersionID != null) &&
					testrayProductVersionID.matches("\\d+")) {

					testrayProductVersion =
						testrayProject.getTestrayProductVersionByID(
							Long.parseLong(testrayProductVersionID));
				}
			}

			String jobName = _topLevelBuildReport.getJobName();

			if ((testrayProductVersion == null) &&
				(jobName.equals("test-qa-websites-functional-daily") ||
				 jobName.equals("test-qa-websites-functional-weekly"))) {

				testrayProductVersion =
					testrayProject.createTestrayProductVersion(
						_replaceEnvVars("1.x", true));
			}

			if (testrayProductVersion == null) {
				JobProperty jobProperty = _getJobProperty(
					"testray.product.version.name", testBaseDir);

				testrayProductVersionName = jobProperty.getValue();

				if (!JenkinsResultsParserUtil.isNullOrEmpty(
						testrayProductVersionName)) {

					testrayProductVersion =
						testrayProject.createTestrayProductVersion(
							_replaceEnvVars(testrayProductVersionName, true));
				}
			}

			PortalRelease portalRelease = getPortalRelease();

			if (portalRelease != null) {
				String portalReleaseVersion = portalRelease.getPortalVersion();

				testrayProductVersion =
					testrayProject.createTestrayProductVersion(
						_replaceEnvVars(portalReleaseVersion, true));
			}
		}
		finally {
			if (testrayProductVersion != null) {
				_testrayProductVersions.put(testBaseDir, testrayProductVersion);

				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Testray Product Version '",
						testrayProductVersion.getName(), "' created in ",
						JenkinsResultsParserUtil.toDurationString(
							System.currentTimeMillis() - start)));

				return testrayProductVersion;
			}
		}

		return null;
	}

	public synchronized TestrayProject getTestrayProject(File testBaseDir) {
		TestrayProject testrayProject = _testrayProjects.get(testBaseDir);

		if (testrayProject != null) {
			return testrayProject;
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		try {
			String testrayProjectID = System.getenv("TESTRAY_PROJECT_ID");

			TestrayServer testrayServer = getTestrayServer(testBaseDir);

			if ((testrayProjectID != null) &&
				testrayProjectID.matches("\\d+")) {

				testrayProject = testrayServer.getTestrayProjectByID(
					Long.parseLong(testrayProjectID));
			}

			String testrayProjectName = System.getenv("TESTRAY_PROJECT_NAME");

			if ((testrayProject == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(testrayProjectName)) {

				testrayProject = testrayServer.getTestrayProjectByName(
					_replaceEnvVars(testrayProjectName, true));
			}

			if ((testrayProject == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(testrayProjectName)) {

				testrayProject = testrayServer.createTestrayProject(
					_replaceEnvVars(testrayProjectName, true));
			}

			testrayProjectID = _getBuildParameter("TESTRAY_PROJECT_ID");

			if ((testrayProject == null) && (testrayProjectID != null) &&
				testrayProjectID.matches("\\d+")) {

				testrayProject = testrayServer.getTestrayProjectByID(
					Long.parseLong(testrayProjectID));
			}

			testrayProjectName = _getBuildParameter("TESTRAY_PROJECT_NAME");

			if ((testrayProject == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(testrayProjectName)) {

				testrayProject = testrayServer.getTestrayProjectByName(
					_replaceEnvVars(testrayProjectName, true));
			}

			if (testrayProject == null) {
				JobProperty jobProperty = _getJobProperty(
					"testray.project.id", testBaseDir);

				testrayProjectID = jobProperty.getValue();

				if ((testrayProjectID != null) &&
					testrayProjectID.matches("\\d+")) {

					testrayProject = testrayServer.getTestrayProjectByID(
						Long.parseLong(testrayProjectID));
				}
			}

			if (testrayProject == null) {
				JobProperty jobProperty = _getJobProperty(
					"testray.project.name", testBaseDir);

				testrayProjectName = jobProperty.getValue();

				if (!JenkinsResultsParserUtil.isNullOrEmpty(
						testrayProjectName)) {

					testrayProject = testrayServer.getTestrayProjectByName(
						_replaceEnvVars(testrayProjectName, true));
				}
			}

			PortalRelease portalRelease = getPortalRelease();

			if (portalRelease != null) {
				String portalVersion = portalRelease.getPortalVersion();

				if (PortalRelease.isQuarterlyRelease(portalVersion)) {
					Matcher quarterlyReleaseVersionMatcher =
						_quarterlyReleaseVersionPattern.matcher(portalVersion);

					if (quarterlyReleaseVersionMatcher.find()) {
						String year = quarterlyReleaseVersionMatcher.group(
							"year");
						String quarter = quarterlyReleaseVersionMatcher.group(
							"quarter");

						testrayProjectName = JenkinsResultsParserUtil.combine(
							"Liferay Portal ", year, " ",
							quarter.toUpperCase());

						testrayProject = testrayServer.getTestrayProjectByName(
							_replaceEnvVars(testrayProjectName, true));
					}
				}
			}

			try {
				Properties buildProperties =
					JenkinsResultsParserUtil.getBuildProperties();

				if (buildProperties.containsKey(
						"testray.override.project.name")) {

					testrayProjectName = buildProperties.getProperty(
						"testray.override.project.name");

					testrayProject = testrayServer.getTestrayProjectByName(
						_replaceEnvVars(testrayProjectName, true));
				}
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}
		finally {
			if (testrayProject != null) {
				_testrayProjects.put(testBaseDir, testrayProject);

				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Testray Project ",
						String.valueOf(testrayProject.getURL()), " created in ",
						JenkinsResultsParserUtil.toDurationString(
							JenkinsResultsParserUtil.getCurrentTimeMillis() -
								start)));

				return testrayProject;
			}
		}

		throw new RuntimeException("Please set TESTRAY_PROJECT_NAME");
	}

	public synchronized TestrayRoutine getTestrayRoutine(File testBaseDir) {
		TestrayRoutine testrayRoutine = _testrayRoutines.get(testBaseDir);

		if (testrayRoutine != null) {
			return testrayRoutine;
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		try {
			String testrayRoutineID = System.getenv("TESTRAY_ROUTINE_ID");

			TestrayProject testrayProject = getTestrayProject(testBaseDir);

			if ((testrayRoutineID != null) &&
				testrayRoutineID.matches("\\d+")) {

				testrayRoutine = testrayProject.getTestrayRoutineByID(
					Long.parseLong(testrayRoutineID));
			}

			String testrayRoutineName = System.getenv("TESTRAY_ROUTINE_NAME");

			if ((testrayRoutine == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(testrayRoutineName)) {

				testrayRoutine = testrayProject.createTestrayRoutine(
					_replaceEnvVars(testrayRoutineName, true));
			}

			testrayRoutineID = _getBuildParameter("TESTRAY_ROUTINE_ID");

			if ((testrayRoutine == null) && (testrayRoutineID != null) &&
				testrayRoutineID.matches("\\d+")) {

				testrayRoutine = testrayProject.getTestrayRoutineByID(
					Long.parseLong(testrayRoutineID));
			}

			testrayRoutineName = _getBuildParameter("TESTRAY_ROUTINE_NAME");

			if ((testrayRoutine == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(testrayRoutineName)) {

				testrayRoutine = testrayProject.createTestrayRoutine(
					_replaceEnvVars(testrayRoutineName, true));
			}

			testrayRoutineName = _getBuildParameter("TESTRAY_BUILD_TYPE");

			if ((testrayRoutine == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(testrayRoutineName)) {

				testrayRoutine = testrayProject.createTestrayRoutine(
					_replaceEnvVars(testrayRoutineName, true));
			}

			if (testrayRoutine == null) {
				JobProperty jobProperty = _getJobProperty(
					"testray.routine.id", testBaseDir);

				testrayRoutineID = jobProperty.getValue();

				if ((testrayRoutineID != null) &&
					testrayRoutineID.matches("\\d+")) {

					testrayRoutine = testrayProject.getTestrayRoutineByID(
						Long.parseLong(testrayRoutineID));
				}
			}

			if (testrayRoutine == null) {
				JobProperty jobProperty = _getJobProperty(
					"testray.routine.name", testBaseDir);

				testrayRoutineName = jobProperty.getValue();

				if (!JenkinsResultsParserUtil.isNullOrEmpty(
						testrayRoutineName)) {

					testrayRoutine = testrayProject.createTestrayRoutine(
						_replaceEnvVars(testrayRoutineName, true));
				}
			}

			try {
				Properties buildProperties =
					JenkinsResultsParserUtil.getBuildProperties();

				if (buildProperties.containsKey(
						"testray.override.routine.name")) {

					testrayRoutineName = buildProperties.getProperty(
						"testray.override.routine.name");

					testrayRoutine = testrayProject.createTestrayRoutine(
						_replaceEnvVars(testrayRoutineName, true));
				}
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}
		finally {
			if (testrayRoutine != null) {
				_testrayRoutines.put(testBaseDir, testrayRoutine);

				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Testray Routine ",
						String.valueOf(testrayRoutine.getURL()), " created in ",
						JenkinsResultsParserUtil.toDurationString(
							JenkinsResultsParserUtil.getCurrentTimeMillis() -
								start)));

				return testrayRoutine;
			}
		}

		throw new RuntimeException("Please set TESTRAY_ROUTINE_NAME");
	}

	public synchronized TestrayServer getTestrayServer(File testBaseDir) {
		TestrayServer testrayServer = _testrayServers.get(testBaseDir);

		if (testrayServer != null) {
			return testrayServer;
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		try {
			String testrayServerURL = System.getenv("TESTRAY_SERVER_URL");

			if ((testrayServerURL != null) &&
				testrayServerURL.matches("https?://.*")) {

				testrayServer = TestrayFactory.newTestrayServer(
					testrayServerURL);
			}

			testrayServerURL = _getBuildParameter("TESTRAY_SERVER_URL");

			if ((testrayServer == null) && (testrayServerURL != null) &&
				testrayServerURL.matches("https?://.*")) {

				testrayServer = TestrayFactory.newTestrayServer(
					testrayServerURL);
			}

			if (testrayServer == null) {
				JobProperty jobProperty = _getJobProperty(
					"testray.server.url", testBaseDir);

				testrayServerURL = jobProperty.getValue();

				if ((testrayServerURL != null) &&
					testrayServerURL.matches("https?://.*")) {

					testrayServer = TestrayFactory.newTestrayServer(
						testrayServerURL);
				}
			}
		}
		finally {
			if (testrayServer != null) {
				_testrayServers.put(testBaseDir, testrayServer);

				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Testray Server ",
						String.valueOf(testrayServer.getURL()), " created in ",
						JenkinsResultsParserUtil.toDurationString(
							JenkinsResultsParserUtil.getCurrentTimeMillis() -
								start)));

				return testrayServer;
			}
		}

		throw new RuntimeException("Please set TESTRAY_SERVER_URL");
	}

	public void postSlackNotification() {
		List<Long> testrayBuildIDs = new ArrayList<>();

		for (Map.Entry<File, TestrayBuild> testrayBuildEntry :
				_testrayBuilds.entrySet()) {

			File testBaseDir = testrayBuildEntry.getKey();

			TestrayBuild testrayBuild = testrayBuildEntry.getValue();

			if (testrayBuildIDs.contains(testrayBuild.getID())) {
				continue;
			}

			testrayBuildIDs.add(testrayBuild.getID());

			String slackChannels = _getSlackChannels(testBaseDir);

			if (JenkinsResultsParserUtil.isNullOrEmpty(slackChannels)) {
				continue;
			}

			for (String slackChannel : slackChannels.split(",")) {
				NotificationUtil.sendSlackNotification(
					_getSlackBody(testBaseDir), slackChannel,
					_getSlackIconEmoji(testBaseDir),
					_getSlackSubject(testBaseDir),
					_getSlackUsername(testBaseDir));
			}
		}
	}

	public void recordTestrayCaseResults() {
		List<AxisTestClassGroup> axisTestClassGroups = new ArrayList<>();
		List<Callable<Void>> callables = new ArrayList<>();

		for (Job job : _jobs) {
			if (job instanceof TestSuiteJob) {
				TestSuiteJob testSuiteJob = (TestSuiteJob)job;

				if (!Objects.equals(
						_topLevelBuildReport.getTestSuiteName(),
						testSuiteJob.getTestSuiteName())) {

					continue;
				}
			}

			axisTestClassGroups.addAll(job.getAxisTestClassGroups());
			axisTestClassGroups.addAll(job.getDependentAxisTestClassGroups());

			File testBaseDir = null;

			if ((job instanceof QAWebsitesGitRepositoryJob) &&
				!axisTestClassGroups.isEmpty()) {

				AxisTestClassGroup axisTestClassGroup = axisTestClassGroups.get(
					0);

				testBaseDir = axisTestClassGroup.getTestBaseDir();
			}

			TestrayBuild testrayBuild = getTestrayBuild(testBaseDir);

			TopLevelStandaloneBuildTestrayCaseResult
				topLevelStandaloneBuildTestrayCaseResult =
					TestrayFactory.newTopLevelStandaloneBuildTestrayCaseResult(
						testrayBuild, _topLevelBuildReport);

			topLevelStandaloneBuildTestrayCaseResult.recordTestrayCaseResult(
				job);

			AppServerBundleStandaloneBuildTestrayCaseResult
				portalAppServerBundleStandaloneBuildTestrayCaseResult =
					new AppServerBundleStandaloneBuildTestrayCaseResult(
						"portal", testrayBuild, _topLevelBuildReport);

			BuildReport portalAppServerBundleBuildReport =
				portalAppServerBundleStandaloneBuildTestrayCaseResult.
					getBuildReport();

			if (portalAppServerBundleBuildReport != null) {
				portalAppServerBundleStandaloneBuildTestrayCaseResult.
					recordTestrayCaseResult(job);
			}

			AppServerBundleStandaloneBuildTestrayCaseResult
				analyticsCloudAppServerBundleStandaloneBuildTestrayCaseResult =
					new AppServerBundleStandaloneBuildTestrayCaseResult(
						"analytics.cloud", testrayBuild, _topLevelBuildReport);

			BuildReport analyticsCloudAppServerBundleBuildReport =
				analyticsCloudAppServerBundleStandaloneBuildTestrayCaseResult.
					getBuildReport();

			if (analyticsCloudAppServerBundleBuildReport != null) {
				analyticsCloudAppServerBundleStandaloneBuildTestrayCaseResult.
					recordTestrayCaseResult(job);
			}

			for (final AxisTestClassGroup axisTestClassGroup :
					axisTestClassGroups) {

				callables.add(
					new Callable<Void>() {

						@Override
						public Void call() throws Exception {
							_recordAxisTestClassGroup(axisTestClassGroup);

							return null;
						}

					});
			}
		}

		ParallelExecutor<Void> parallelExecutor = new ParallelExecutor<>(
			callables, _executorService, "recordTestrayCaseResults");

		try {
			parallelExecutor.execute(60L * 300L);
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}

		List<Long> testrayBuildIDs = new ArrayList<>();

		for (TestrayBuild testrayBuild : _testrayBuilds.values()) {
			if (testrayBuildIDs.contains(testrayBuild.getID())) {
				continue;
			}

			testrayBuildIDs.add(testrayBuild.getID());

			TestrayServer testrayServer = testrayBuild.getTestrayServer();

			testrayServer.importCaseResults(_topLevelBuildReport);
		}

		_sendPullRequestNotification();
	}

	private void _addDetailsElements(
		Element propertiesElement,
		JUnitBatchBuildTestrayCaseResult testrayCaseResult) {

		Element detailsElement = propertiesElement.addElement("details");

		for (String methodName : testrayCaseResult.getMethodNames()) {
			if (JenkinsResultsParserUtil.isNullOrEmpty(
					testrayCaseResult.getMethodIssues(methodName))) {

				continue;
			}

			Element detailElement = detailsElement.addElement("detail");

			Element issuesPropertyElement = detailElement.addElement(
				"property");

			issuesPropertyElement.addAttribute("name", "testray.jira.issues");
			issuesPropertyElement.addAttribute(
				"value", testrayCaseResult.getMethodIssues(methodName));

			Element namePropertyElement = detailElement.addElement("property");

			namePropertyElement.addAttribute(
				"name", "testray.testcase.detail.name");
			namePropertyElement.addAttribute("value", methodName);

			Element statusPropertyElement = detailElement.addElement(
				"property");

			statusPropertyElement.addAttribute(
				"name", "testray.testcase.detail.status");
			statusPropertyElement.addAttribute(
				"value", testrayCaseResult.getMethodStatus(methodName));
		}
	}

	private void _addPropertyElements(
		Element propertiesElement, Map<String, String> propertiesMap) {

		for (Map.Entry<String, String> propertyEntry :
				propertiesMap.entrySet()) {

			Element propertyElement = propertiesElement.addElement("property");

			String propertyName = propertyEntry.getKey();
			String propertyValue = propertyEntry.getValue();

			if (JenkinsResultsParserUtil.isNullOrEmpty(propertyName) ||
				JenkinsResultsParserUtil.isNullOrEmpty(propertyValue)) {

				continue;
			}

			propertyElement.addAttribute("name", propertyName);
			propertyElement.addAttribute("value", propertyValue);
		}
	}

	private String _fixSlackString(String string) {
		string = string.replace("*", "&#42;");
		string = string.replace(">", "&gt;");
		string = string.replace("<", "&lt;");

		return string.replace("|", "&vert;");
	}

	private String _getBuildParameter(String buildParameterName) {
		Map<String, String> buildParameters = new HashMap<>();

		ControllerBuildReport controllerBuildReport =
			_topLevelBuildReport.getControllerBuildReport();

		if (controllerBuildReport != null) {
			buildParameters.putAll(controllerBuildReport.getBuildParameters());
		}

		buildParameters.putAll(_topLevelBuildReport.getBuildParameters());

		return buildParameters.get(buildParameterName);
	}

	private Element _getJenkinsBuildDescriptionCodeElement(
		String title, String name) {

		Document document = DocumentHelper.createDocument();

		Element element = document.addElement("div");

		Element titleElement = element.addElement("strong");

		titleElement.addText(title + ":");

		Element spaceElement = element.addElement("span");

		spaceElement.addText(" ");

		Element codeElement = element.addElement("code");

		codeElement.addText(name);

		element.addElement("br");

		return element;
	}

	private Element _getJenkinsBuildDescriptionElement(
		String title, String name) {

		return _getJenkinsBuildDescriptionElement(title, name, null);
	}

	private Element _getJenkinsBuildDescriptionElement(
		String title, String name, String url) {

		Document document = DocumentHelper.createDocument();

		Element element = document.addElement("div");

		Element titleElement = element.addElement("strong");

		titleElement.addText(title + ":");

		Element spaceElement = element.addElement("span");

		spaceElement.addText(" ");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(url)) {
			Element linkElement = element.addElement("a");

			linkElement.addAttribute("href", url);
			linkElement.addText(name);
		}
		else {
			element.addText(name);
		}

		element.addElement("br");

		return element;
	}

	private JobProperty _getJobProperty(
		String basePropertyName, File testBaseDir) {

		for (Job job : _jobs) {
			if (job instanceof QAWebsitesGitRepositoryJob) {
				JobProperty jobProperty = JobPropertyFactory.newJobProperty(
					basePropertyName, job, testBaseDir,
					JobProperty.Type.QA_WEBSITES_TEST_DIR);

				if (!JenkinsResultsParserUtil.isNullOrEmpty(
						jobProperty.getValue())) {

					return jobProperty;
				}
			}

			return JobPropertyFactory.newJobProperty(basePropertyName, job);
		}

		return null;
	}

	private String _getMajorPortalVersion() {
		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_getPortalWorkspaceGitRepository();

		if (portalWorkspaceGitRepository == null) {
			return "7.4";
		}

		File releasePropertiesFile = new File(
			portalWorkspaceGitRepository.getDirectory(), "release.properties");

		Properties releaseProperties = JenkinsResultsParserUtil.getProperties(
			releasePropertiesFile);

		String majorPortalVersion = JenkinsResultsParserUtil.getProperty(
			releaseProperties, "lp.version.major");

		if (JenkinsResultsParserUtil.isNullOrEmpty(majorPortalVersion)) {
			return "7.4";
		}

		return majorPortalVersion;
	}

	private PluginsWorkspaceGitRepository _getPluginsWorkspaceGitRepository() {
		for (Workspace workspace : _workspaces) {
			if (!(workspace instanceof PortalWorkspace)) {
				continue;
			}

			PortalWorkspace portalWorkspace = (PortalWorkspace)workspace;

			return portalWorkspace.getPluginsWorkspaceGitRepository();
		}

		return null;
	}

	private PortalWorkspaceGitRepository _getPortalWorkspaceGitRepository() {
		for (Workspace workspace : _workspaces) {
			if (!(workspace instanceof PortalWorkspace)) {
				continue;
			}

			PortalWorkspace portalWorkspace = (PortalWorkspace)workspace;

			return portalWorkspace.getPortalWorkspaceGitRepository();
		}

		return null;
	}

	private QAWebsitesWorkspaceGitRepository
		_getQAWebsitesWorkspaceGitRepository() {

		for (Workspace workspace : _workspaces) {
			WorkspaceGitRepository workspaceGitRepository =
				workspace.getWorkspaceGitRepository("liferay-qa-websites-ee");

			if (!(workspaceGitRepository instanceof
					QAWebsitesWorkspaceGitRepository)) {

				return null;
			}

			return (QAWebsitesWorkspaceGitRepository)workspaceGitRepository;
		}

		return null;
	}

	private String _getSlackBody(File testBaseDir) {
		JobProperty jobProperty = _getJobProperty(
			"testray.slack.body", testBaseDir);

		String slackBody = jobProperty.getValue();

		if (JenkinsResultsParserUtil.isNullOrEmpty(slackBody)) {
			StringBuilder sb = new StringBuilder();

			sb.append("*Jenkins Testray Importer:* ");
			sb.append("<$(testray.importer.build.url)|");
			sb.append("$(testray.importer.job.name)#");
			sb.append("$(testray.importer.build.number)>\n");

			sb.append("*Testray Build:* ");
			sb.append("<$(testray.build.url)|$(testray.build.name)>");

			slackBody = sb.toString();
		}

		return _replaceSlackEnvVars(slackBody, testBaseDir);
	}

	private String _getSlackChannels(File testBaseDir) {
		String slackChannels = System.getenv("TESTRAY_SLACK_CHANNELS");

		if (JenkinsResultsParserUtil.isNullOrEmpty(slackChannels)) {
			JobProperty jobProperty = _getJobProperty(
				"testray.slack.channels", testBaseDir);

			slackChannels = jobProperty.getValue();
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(slackChannels)) {
			slackChannels = "testray-reports";
		}

		return _replaceSlackEnvVars(slackChannels, testBaseDir);
	}

	private String _getSlackIconEmoji(File testBaseDir) {
		String slackIconEmoji = System.getenv("TESTRAY_SLACK_ICON_EMOJI");

		if (JenkinsResultsParserUtil.isNullOrEmpty(slackIconEmoji)) {
			JobProperty jobProperty = _getJobProperty(
				"testray.slack.icon.emoji", testBaseDir);

			slackIconEmoji = jobProperty.getValue();
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(slackIconEmoji)) {
			return _replaceSlackEnvVars(slackIconEmoji, testBaseDir);
		}

		return ":liferay-ci:";
	}

	private String _getSlackSubject(File testBaseDir) {
		JobProperty jobProperty = _getJobProperty(
			"testray.slack.subject", testBaseDir);

		String slackSubject = jobProperty.getValue();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(slackSubject)) {
			return _replaceSlackEnvVars(slackSubject, testBaseDir);
		}

		return JenkinsResultsParserUtil.combine(
			_topLevelBuildReport.getJobName(), "#",
			String.valueOf(_topLevelBuildReport.getBuildNumber()));
	}

	private String _getSlackUsername(File testBaseDir) {
		String slackUsername = System.getenv("TESTRAY_SLACK_USERNAME");

		if (JenkinsResultsParserUtil.isNullOrEmpty(slackUsername)) {
			JobProperty jobProperty = _getJobProperty(
				"testray.slack.username", testBaseDir);

			slackUsername = jobProperty.getValue();
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(slackUsername)) {
			return _replaceSlackEnvVars(slackUsername, testBaseDir);
		}

		return "Liferay CI";
	}

	private void _recordAxisTestClassGroup(
		AxisTestClassGroup axisTestClassGroup) {

		Job job = axisTestClassGroup.getJob();

		TestrayBuild testrayBuild = getTestrayBuild(
			axisTestClassGroup.getTestBaseDir());

		TestrayRun testrayRun;

		if (axisTestClassGroup instanceof FunctionalAxisTestClassGroup) {
			testrayRun = TestrayFactory.newTestrayRun(
				testrayBuild, axisTestClassGroup, job.getJobPropertiesFiles());
		}
		else {
			testrayRun = TestrayFactory.newTestrayRun(
				testrayBuild, axisTestClassGroup.getBatchName(),
				job.getJobPropertiesFiles());
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		Document document = DocumentHelper.createDocument();

		Element rootElement = document.addElement("testsuite");

		Element environmentsElement = rootElement.addElement("environments");

		for (TestrayRun.Factor factor : testrayRun.getFactors()) {
			Element environmentElement = environmentsElement.addElement(
				"environment");

			environmentElement.addAttribute("type", factor.getName());
			environmentElement.addAttribute("option", factor.getValue());
		}

		Map<String, String> propertiesMap = new HashMap<>();

		propertiesMap.put(
			"testray.build.date",
			_topLevelBuildReport.getTestrayBuildDateString());
		propertiesMap.put("testray.build.name", testrayBuild.getName());
		propertiesMap.put(
			"testray.build.time",
			JenkinsResultsParserUtil.toDurationString(
				_topLevelBuildReport.getDuration()));

		TestrayRoutine testrayRoutine = testrayBuild.getTestrayRoutine();

		propertiesMap.put("testray.build.type", testrayRoutine.getName());

		TestrayProductVersion testrayProductVersion =
			testrayBuild.getTestrayProductVersion();

		if (testrayProductVersion != null) {
			propertiesMap.put(
				"testray.product.version", testrayProductVersion.getName());
		}

		TestrayProject testrayProject = testrayBuild.getTestrayProject();

		propertiesMap.put("testray.project.name", testrayProject.getName());

		propertiesMap.put("testray.run.id", testrayRun.getRunIDString());
		propertiesMap.put(
			"testray.total.cpu.use.time",
			JenkinsResultsParserUtil.toDurationString(
				_topLevelBuildReport.getTotalActualDuration()));

		_addPropertyElements(
			rootElement.addElement("properties"), propertiesMap);

		String[] warnings = null;

		List<TestrayCaseResult> testrayCaseResults = new ArrayList<>();

		if (axisTestClassGroup instanceof FunctionalAxisTestClassGroup ||
			axisTestClassGroup instanceof JSUnitAxisTestClassGroup ||
			axisTestClassGroup instanceof JUnitAxisTestClassGroup ||
			axisTestClassGroup instanceof ModulesAxisTestClassGroup) {

			PortalLogBatchBuildTestrayCaseResult
				portalLogBatchBuildTestrayCaseResult =
					TestrayFactory.newPortalLogTestrayCaseResult(
						axisTestClassGroup, testrayBuild, _topLevelBuildReport);

			if (!JenkinsResultsParserUtil.isNullOrEmpty(
					portalLogBatchBuildTestrayCaseResult.getErrors())) {

				testrayCaseResults.add(portalLogBatchBuildTestrayCaseResult);
			}

			for (TestClass testClass : axisTestClassGroup.getTestClasses()) {
				testrayCaseResults.add(
					TestrayFactory.newBuildTestrayCaseResult(
						axisTestClassGroup, testClass, testrayBuild,
						_topLevelBuildReport));
			}
		}
		else if (axisTestClassGroup instanceof PlaywrightAxisTestClassGroup) {
			for (TestClass testClass : axisTestClassGroup.getTestClasses()) {
				for (TestClassMethod testClassMethod :
						testClass.getTestClassMethods()) {

					testrayCaseResults.add(
						TestrayFactory.newBuildTestrayCaseResult(
							axisTestClassGroup, testClass, testClassMethod,
							testrayBuild, _topLevelBuildReport));
				}
			}
		}
		else {
			testrayCaseResults.add(
				TestrayFactory.newBuildTestrayCaseResult(
					axisTestClassGroup, testrayBuild, _topLevelBuildReport));
		}

		for (TestrayCaseResult testrayCaseResult : testrayCaseResults) {
			Element testcaseElement = rootElement.addElement("testcase");

			Map<String, String> testcasePropertiesMap = new HashMap<>();

			testcasePropertiesMap.put(
				"testray.case.type.name", testrayCaseResult.getType());
			testcasePropertiesMap.put(
				"testray.component.names",
				testrayCaseResult.getSubcomponentNames());
			testcasePropertiesMap.put(
				"testray.main.component.name",
				testrayCaseResult.getComponentName());
			testcasePropertiesMap.put(
				"testray.team.name", testrayCaseResult.getTeamName());
			testcasePropertiesMap.put(
				"testray.testcase.duration",
				String.valueOf(testrayCaseResult.getDuration()));

			String testrayCaseName = testrayCaseResult.getName();

			if (testrayCaseName.length() > 150) {
				testrayCaseName = testrayCaseName.substring(0, 150);
			}

			testcasePropertiesMap.put("testray.testcase.name", testrayCaseName);

			testcasePropertiesMap.put(
				"testray.testcase.priority",
				String.valueOf(testrayCaseResult.getPriority()));

			TestrayCaseResult.Status testrayCaseStatus =
				testrayCaseResult.getStatus();

			testcasePropertiesMap.put(
				"testray.testcase.status", testrayCaseStatus.getName());

			Element propertiesElement = testcaseElement.addElement(
				"properties");

			String testSuiteName = _topLevelBuildReport.getTestSuiteName();

			if (testSuiteName.equals("upstream-dxp")) {
				if (testrayCaseResult instanceof
						JUnitBatchBuildTestrayCaseResult) {

					_addDetailsElements(
						propertiesElement,
						(JUnitBatchBuildTestrayCaseResult)testrayCaseResult);
				}
				else {
					testcasePropertiesMap.put(
						"testray.jira.issues", testrayCaseResult.getIssues());
				}
			}

			_addPropertyElements(propertiesElement, testcasePropertiesMap);

			if (warnings == null) {
				warnings = testrayCaseResult.getWarnings();
			}

			if ((warnings != null) && (warnings.length > 0)) {
				Element warningsPropertyElement = propertiesElement.addElement(
					"property");

				warningsPropertyElement.addAttribute(
					"name", "testray.testcase.warnings");
				warningsPropertyElement.addAttribute(
					"value", String.valueOf(warnings.length));

				for (String warning : warnings) {
					Element warningPropertyElement =
						warningsPropertyElement.addElement("value");

					warningPropertyElement.addText(
						StringEscapeUtils.escapeHtml(warning));
				}
			}

			Element attachmentsElement = testcaseElement.addElement(
				"attachments");

			for (TestrayAttachment testrayAttachment :
					testrayCaseResult.getTestrayAttachments()) {

				Element attachmentFileElement = attachmentsElement.addElement(
					"file");

				attachmentFileElement.addAttribute(
					"name", testrayAttachment.getName());
				attachmentFileElement.addAttribute(
					"url", testrayAttachment.getURL() + "?authuser=0");
				attachmentFileElement.addAttribute(
					"value", testrayAttachment.getKey() + "?authuser=0");
			}

			String errors = testrayCaseResult.getErrors();

			if (!JenkinsResultsParserUtil.isNullOrEmpty(errors)) {
				Element failureElement = testcaseElement.addElement("failure");

				failureElement.addAttribute("message", errors);
			}
		}

		TestrayServer testrayServer = testrayBuild.getTestrayServer();

		JenkinsMaster jenkinsMaster = _topLevelBuildReport.getJenkinsMaster();

		try {
			String axisName = axisTestClassGroup.getAxisName();

			testrayServer.writeCaseResult(
				JenkinsResultsParserUtil.combine(
					"TESTS-", jenkinsMaster.getName(), "_",
					_topLevelBuildReport.getJobName(), "_",
					String.valueOf(_topLevelBuildReport.getBuildNumber()), "_",
					axisName.replace("/", "_"), ".xml"),
				Dom4JUtil.format(rootElement));
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		System.out.println(
			JenkinsResultsParserUtil.combine(
				"Recorded ", String.valueOf(testrayCaseResults.size()),
				" case results for ", axisTestClassGroup.getAxisName(), " in ",
				JenkinsResultsParserUtil.toDurationString(
					currentTimeMillis - start)));
	}

	private String _replaceEnvVars(String string, boolean truncate) {
		string = _replaceEnvVarsControllerBuild(string);
		string = _replaceEnvVarsPluginsBranchInformationBuild(string);
		string = _replaceEnvVarsPluginsTopLevelBuild(string);
		string = _replaceEnvVarsPortalAppReleaseTopLevelBuild(string);
		string = _replaceEnvVarsPortalBranchInformationBuild(string);
		string = _replaceEnvVarsPortalRelease(string);
		string = _replaceEnvVarsPullRequestBuild(string);
		string = _replaceEnvVarsQAWebsitesTopLevelBuild(string);
		string = _replaceEnvVarsTopLevelBuild(string);

		String jobName = _topLevelBuildReport.getJobName();

		if (jobName.contains("subrepository")) {
			string = _replaceEnvVarsSubrepository(string);
		}

		if (truncate && !JenkinsResultsParserUtil.isNullOrEmpty(string) &&
			(string.length() > 150)) {

			string = string.substring(string.length() - 150);
		}

		return string;
	}

	private String _replaceEnvVarsControllerBuild(String string) {
		ControllerBuildReport controllerBuildReport =
			_topLevelBuildReport.getControllerBuildReport();

		if (controllerBuildReport == null) {
			return string;
		}

		string = string.replace(
			"$(jenkins.controller.build.url)",
			String.valueOf(controllerBuildReport.getBuildURL()));
		string = string.replace(
			"$(jenkins.controller.build.number)",
			String.valueOf(controllerBuildReport.getBuildNumber()));
		string = string.replace(
			"$(jenkins.controller.build.start)",
			controllerBuildReport.getTestrayBuildDateString());
		string = string.replace(
			"$(jenkins.controller.job.name)",
			controllerBuildReport.getJobName());

		JenkinsMaster jenkinsMaster = controllerBuildReport.getJenkinsMaster();

		return string.replace(
			"$(jenkins.controller.master.hostname)", jenkinsMaster.getName());
	}

	private String _replaceEnvVarsPluginsBranchInformationBuild(String string) {
		PluginsWorkspaceGitRepository pluginsWorkspaceGitRepository =
			_getPluginsWorkspaceGitRepository();

		if (pluginsWorkspaceGitRepository == null) {
			return string;
		}

		string = string.replace(
			"$(plugins.branch.name)",
			pluginsWorkspaceGitRepository.getUpstreamBranchName());
		string = string.replace(
			"$(plugins.custom.branch.name)",
			pluginsWorkspaceGitRepository.getSenderBranchName());
		string = string.replace(
			"$(plugins.custom.branch.username)",
			pluginsWorkspaceGitRepository.getSenderBranchUsername());
		string = string.replace(
			"$(plugins.repository)", pluginsWorkspaceGitRepository.getName());

		return string.replace(
			"$(plugins.sha)",
			pluginsWorkspaceGitRepository.getSenderBranchSHA());
	}

	private String _replaceEnvVarsPluginsTopLevelBuild(String string) {
		Map<String, String> buildParameters =
			_topLevelBuildReport.getBuildParameters();

		String pluginName = buildParameters.get("TEST_PLUGIN_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(pluginName)) {
			string = string.replace("$(plugin.name)", pluginName);
		}

		return string;
	}

	private String _replaceEnvVarsPortalAppReleaseTopLevelBuild(String string) {
		Map<String, String> buildParameters =
			_topLevelBuildReport.getBuildParameters();

		String portalAppName = buildParameters.get("TEST_PORTAL_APP_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(portalAppName)) {
			string = string.replace("$(portal.app.name)", portalAppName);
		}

		return string;
	}

	private String _replaceEnvVarsPortalBranchInformationBuild(String string) {
		Job.BuildProfile buildProfile = _topLevelBuildReport.getBuildProfile();

		if (buildProfile != null) {
			string = string.replace(
				"$(portal.profile)", buildProfile.toDisplayString());

			if (buildProfile == Job.BuildProfile.PORTAL) {
				string = string.replace("$(portal.type)", "CE");
			}
			else {
				string = string.replace("$(portal.type)", "EE");
			}
		}

		String majorPortalVersion = _getMajorPortalVersion();

		string = string.replace("$(portal.version)", majorPortalVersion);

		string = string.replace(
			"$(portal.product.version)", majorPortalVersion + ".x");

		PortalWorkspaceGitRepository portalWorkspaceGitRepository =
			_getPortalWorkspaceGitRepository();

		if (portalWorkspaceGitRepository == null) {
			return string;
		}

		String portalUpstreamBranchName =
			portalWorkspaceGitRepository.getUpstreamBranchName();

		string = string.replace(
			"$(portal.branch.name)", portalUpstreamBranchName);

		Matcher releaseBranchMatcher = _releaseBranchPattern.matcher(
			portalUpstreamBranchName);

		if (releaseBranchMatcher.find()) {
			string = string.replace(
				"$(portal.branch.display.name)",
				JenkinsResultsParserUtil.combine(
					releaseBranchMatcher.group("year"), " Q",
					releaseBranchMatcher.group("quarter")));
		}
		else {
			string = string.replace(
				"$(portal.branch.display.name)", majorPortalVersion);
		}

		string = string.replace(
			"$(portal.repository)", portalWorkspaceGitRepository.getName());

		return string.replace(
			"$(portal.sha)", portalWorkspaceGitRepository.getSenderBranchSHA());
	}

	private String _replaceEnvVarsPortalRelease(String string) {
		PortalRelease portalRelease = getPortalRelease();

		if (portalRelease != null) {
			String portalBundleTomcatURLString = String.valueOf(
				portalRelease.getPortalBundleTomcatURL());

			string = string.replace(
				"$(portal.product.version)", portalRelease.getPortalVersion());
			string = string.replace(
				"$(portal.release.tomcat.url)", portalBundleTomcatURLString);
			string = string.replace(
				"$(portal.release.version)", portalRelease.getPortalVersion());

			Matcher matcher = _releaseArtifactURLPattern.matcher(
				portalBundleTomcatURLString);

			if (matcher.find()) {
				string = string.replace(
					"$(portal.release.tomcat.name)",
					matcher.group("releaseName"));
			}

			Map<String, String> buildParameters =
				_topLevelBuildReport.getBuildParameters();

			String portalReleaseBuildVersion = buildParameters.get(
				"TEST_PORTAL_RELEASE_VERSION");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(
					portalReleaseBuildVersion)) {

				string = string.replace(
					"$(portal.release.build.version)",
					portalReleaseBuildVersion);
			}
		}

		PortalFixpackRelease portalFixpackRelease = getPortalFixpackRelease();

		if (portalFixpackRelease != null) {
			String portalFixpackURL = String.valueOf(
				portalFixpackRelease.getPortalFixpackURL());

			string = string.replace(
				"$(portal.fixpack.release.url)", portalFixpackURL);

			string = string.replace(
				"$(portal.fixpack.release.version)",
				portalFixpackRelease.getPortalFixpackVersion());

			Matcher matcher = _releaseArtifactURLPattern.matcher(
				portalFixpackURL);

			if (matcher.find()) {
				string = string.replace(
					"$(portal.fixpack.release.name)",
					matcher.group("releaseName"));
			}
		}

		PortalHotfixRelease portalHotfixRelease = getPortalHotfixRelease();

		if (portalHotfixRelease != null) {
			String portalHotfixURL = String.valueOf(
				portalHotfixRelease.getPortalHotfixReleaseURL());

			string = string.replace(
				"$(portal.hotfix.release.url)", portalHotfixURL);

			string = string.replace(
				"$(portal.hotfix.release.version)",
				portalHotfixRelease.getPortalHotfixReleaseVersion());

			if (portalRelease != null) {
				string = string.replace(
					"$(portal.product.version)",
					portalRelease.getPortalVersion());
			}

			Matcher matcher = _releaseArtifactURLPattern.matcher(
				portalHotfixURL);

			if (matcher.find()) {
				string = string.replace(
					"$(portal.hotfix.release.name)",
					matcher.group("releaseName"));
			}
		}

		StringBuilder sb = new StringBuilder();

		if (portalRelease == null) {
			sb.append(_getMajorPortalVersion());
			sb.append(".x");

			string = string.replace("$(portal.product.version)", sb.toString());
		}
		else {
			sb.append(portalRelease.getPortalVersion());

			string = string.replace(
				"$(portal.product.version)", portalRelease.getPortalVersion());

			if (portalFixpackRelease != null) {
				sb.append(" FP");
				sb.append(portalFixpackRelease.getPortalFixpackVersion());
			}

			if (portalHotfixRelease != null) {
				sb.append(" HF");
				sb.append(portalHotfixRelease.getPortalHotfixReleaseVersion());
			}
		}

		return string.replace("$(portal.release.name)", sb.toString());
	}

	private String _replaceEnvVarsPullRequestBuild(String string) {
		PullRequest pullRequest = getPullRequest();

		if (pullRequest == null) {
			return string;
		}

		string = string.replace(
			"$(pull.request.number)", pullRequest.getNumber());
		string = string.replace(
			"$(pull.request.url)", pullRequest.getHtmlURL());
		string = string.replace(
			"$(pull.request.receiver.username)",
			pullRequest.getReceiverUsername());

		return string.replace(
			"$(pull.request.sender.username)", pullRequest.getSenderUsername());
	}

	private String _replaceEnvVarsQAWebsitesTopLevelBuild(String string) {
		Map<String, String> buildParameters =
			_topLevelBuildReport.getBuildParameters();

		String projectNames = buildParameters.get("PROJECT_NAMES");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(projectNames)) {
			string = string.replace(
				"$(qa.websites.project.name)", projectNames);
		}

		return string;
	}

	private String _replaceEnvVarsSubrepository(String string) {
		Map<String, String> buildParameters =
			_topLevelBuildReport.getBuildParameters();

		String githubUpstreamBranchName = buildParameters.get(
			"GITHUB_UPSTREAM_BRANCH_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(githubUpstreamBranchName)) {
			string = string.replace(
				"$(github.upstream.branch.name)", githubUpstreamBranchName);
		}

		String repositoryName = buildParameters.get("REPOSITORY_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(repositoryName)) {
			string = string.replace("$(repository.name)", repositoryName);
		}

		return string;
	}

	private String _replaceEnvVarsTopLevelBuild(String string) {
		string = string.replace(
			"$(ci.test.suite)", _topLevelBuildReport.getTestSuiteName());
		string = string.replace(
			"$(jenkins.build.number)",
			String.valueOf(_topLevelBuildReport.getBuildNumber()));
		string = string.replace(
			"$(jenkins.build.start)",
			JenkinsResultsParserUtil.toDateString(
				_topLevelBuildReport.getStartDate(), "yyyy-MM-dd[HH:mm:ss]",
				"America/Los_Angeles"));
		string = string.replace(
			"$(jenkins.build.url)",
			String.valueOf(_topLevelBuildReport.getBuildURL()));
		string = string.replace(
			"$(jenkins.job.name)", _topLevelBuildReport.getJobName());

		JenkinsMaster jenkinsMaster = _topLevelBuildReport.getJenkinsMaster();

		string = string.replace(
			"$(jenkins.master.hostname)", jenkinsMaster.getName());

		return string.replace(
			"$(jenkins.report.url)",
			String.valueOf(_topLevelBuildReport.getJenkinsReportURL()));
	}

	private String _replaceSlackEnvVars(String string, File testBaseDir) {
		string = _replaceEnvVars(string, false);

		string = _replaceSlackEnvVarsTestrayInformation(string, testBaseDir);
		string = _replaceSlackEnvVarsTestrayImporter(string);

		return string;
	}

	private String _replaceSlackEnvVarsTestrayImporter(String string) {
		String buildNumber = System.getenv("BUILD_NUMBER");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(buildNumber)) {
			string = string.replace(
				"$(testray.importer.build.number)", buildNumber);
		}

		String buildURL = System.getenv("BUILD_URL");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(buildURL)) {
			string = string.replace("$(testray.importer.build.url)", buildURL);
		}

		String jobName = System.getenv("JOB_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(jobName)) {
			string = string.replace("$(testray.importer.job.name)", jobName);
		}

		return string;
	}

	private String _replaceSlackEnvVarsTestrayInformation(
		String string, File testBaseDir) {

		TestrayServer testrayServer = getTestrayServer(testBaseDir);

		if (testrayServer != null) {
			string = string.replace(
				"$(testray.server.url)",
				String.valueOf(testrayServer.getURL()));
		}

		TestrayProject testrayProject = getTestrayProject(testBaseDir);

		if (testrayProject != null) {
			string = string.replace(
				"$(testray.project.name)",
				_fixSlackString(testrayProject.getName()));

			string = string.replace(
				"$(testray.project.url)",
				String.valueOf(testrayProject.getURL()));
		}

		TestrayProductVersion testrayProductVersion = getTestrayProductVersion(
			testBaseDir);

		if (testrayProductVersion != null) {
			string = string.replace(
				"$(testray.product.version.name)",
				_fixSlackString(testrayProductVersion.getName()));
		}

		TestrayRoutine testrayRoutine = getTestrayRoutine(testBaseDir);

		if (testrayRoutine != null) {
			string = string.replace(
				"$(testray.routine.name)",
				_fixSlackString(testrayRoutine.getName()));
			string = string.replace(
				"$(testray.routine.url)",
				String.valueOf(testrayRoutine.getURL()));
		}

		TestrayBuild testrayBuild = getTestrayBuild(testBaseDir);

		if (testrayBuild != null) {
			string = string.replace(
				"$(testray.build.name)",
				_fixSlackString(testrayBuild.getName()));
			string = string.replace(
				"$(testray.build.url)", String.valueOf(testrayBuild.getURL()));
		}

		return string;
	}

	private void _sendPullRequestNotification() {
		PullRequest pullRequest = getPullRequest();

		if (pullRequest == null) {
			return;
		}

		pullRequest.addComment(getJenkinsBuildDescription());
	}

	private static final ExecutorService _executorService =
		JenkinsResultsParserUtil.getNewThreadPoolExecutor(10, true);
	private static final Pattern _quarterlyReleaseVersionPattern =
		Pattern.compile("(?<year>\\d{4}).(?<quarter>[Qq]\\d+).\\d+");
	private static final Pattern _releaseArtifactURLPattern = Pattern.compile(
		"https?://.+/(?<releaseName>[^/]+)(.7z|.tar.gz|.war|.zip)");
	private static final Pattern _releaseBranchPattern = Pattern.compile(
		"release-(?<year>\\d{4})\\.q(?<quarter>[1-4])");

	private final List<Job> _jobs;
	private final List<PortalFixpackRelease> _portalFixpackReleases;
	private final List<PortalHotfixRelease> _portalHotfixReleases;
	private final List<PortalRelease> _portalReleases;
	private final List<PullRequest> _pullRequests;
	private final Map<File, TestrayBuild> _testrayBuilds =
		Collections.synchronizedMap(new HashMap<File, TestrayBuild>());
	private final Map<File, TestrayProductVersion> _testrayProductVersions =
		Collections.synchronizedMap(new HashMap<File, TestrayProductVersion>());
	private final Map<File, TestrayProject> _testrayProjects =
		Collections.synchronizedMap(new HashMap<File, TestrayProject>());
	private final Map<File, TestrayRoutine> _testrayRoutines =
		Collections.synchronizedMap(new HashMap<File, TestrayRoutine>());
	private final Map<File, TestrayServer> _testrayServers =
		Collections.synchronizedMap(new HashMap<File, TestrayServer>());
	private final TopLevelBuildReport _topLevelBuildReport;
	private final List<Workspace> _workspaces;

}