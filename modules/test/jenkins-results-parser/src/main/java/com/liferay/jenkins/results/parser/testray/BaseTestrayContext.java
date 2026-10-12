/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.Environment;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Job;
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
import com.liferay.jenkins.results.parser.Workspace;
import com.liferay.jenkins.results.parser.WorkspaceGitRepository;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.job.property.JobPropertyFactory;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseTestrayContext implements TestrayContext {

	@Override
	public BuildDatabase getBuildDatabase() {
		return _buildDatabase;
	}

	@Override
	public JobProperty getJobProperty(
		String basePropertyName, File testBaseDir) {

		Job job = _getJob();

		if (job == null) {
			throw new RuntimeException(
				"Unable to get job property " + basePropertyName);
		}

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

	@Override
	public synchronized List<Job> getJobs() {
		if (_jobs != null) {
			return _jobs;
		}

		_jobs = _buildDatabase.getJobs();

		for (Job job : _jobs) {
			job.setTestrayContext(this);
		}

		return _jobs;
	}

	@Override
	public PortalFixpackRelease getPortalFixpackRelease() {
		if (_portalFixpackReleases.isEmpty()) {
			return null;
		}

		return _portalFixpackReleases.get(0);
	}

	@Override
	public PortalHotfixRelease getPortalHotfixRelease() {
		if (_portalHotfixReleases.isEmpty()) {
			return null;
		}

		return _portalHotfixReleases.get(0);
	}

	@Override
	public PortalRelease getPortalRelease() {
		if (_portalReleases.isEmpty()) {
			return null;
		}

		return _portalReleases.get(0);
	}

	@Override
	public PullRequest getPullRequest() {
		if (_pullRequests.isEmpty()) {
			return null;
		}

		if (_pullRequests.size() == 1) {
			return _pullRequests.get(0);
		}

		Map<String, String> topLevelBuildParameters =
			getTopLevelBuildParameters();

		String gitHubReceiverUsername = topLevelBuildParameters.get(
			"GITHUB_RECEIVER_USERNAME");
		String pullRequestNumber = topLevelBuildParameters.get(
			"GITHUB_PULL_REQUEST_NUMBER");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(gitHubReceiverUsername) &&
			!JenkinsResultsParserUtil.isNullOrEmpty(pullRequestNumber)) {

			for (PullRequest pullRequest : _pullRequests) {
				if (Objects.equals(
						pullRequest.getReceiverUsername(),
						gitHubReceiverUsername) &&
					Objects.equals(
						pullRequest.getNumber(), pullRequestNumber)) {

					return pullRequest;
				}
			}
		}

		return _pullRequests.get(0);
	}

	@Override
	public TestrayBuild getTestrayBuild() {
		return getTestrayBuild(null);
	}

	@Override
	public synchronized TestrayBuild getTestrayBuild(File testBaseDir) {
		TestrayBuild testrayBuild = _testrayBuilds.get(testBaseDir);

		if (testrayBuild != null) {
			return testrayBuild;
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		try {
			String testrayBuildId = Environment.get("TESTRAY_BUILD_ID");

			TestrayRoutine testrayRoutine = getTestrayRoutine(testBaseDir);

			if ((testrayBuildId != null) && testrayBuildId.matches("\\d+")) {
				testrayBuild = TestrayFactory.newTestrayBuild(
					testrayRoutine, Long.parseLong(testrayBuildId));
			}

			String testrayBuildName = Environment.get("TESTRAY_BUILD_NAME");

			Date testrayBuildDate = getTestrayBuildDate();
			String testrayBuildDescription = getTestrayBuildDescription();
			String testrayBuildSHA = getTestrayBuildSHA();
			TestrayProductVersion testrayProductVersion =
				getTestrayProductVersion(testBaseDir);

			if ((testrayBuild == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(testrayBuildName)) {

				testrayBuild = testrayRoutine.createTestrayBuild(
					testrayProductVersion, replace(testrayBuildName),
					testrayBuildDate, testrayBuildDescription, testrayBuildSHA);
			}

			testrayBuildId = _getBuildParameter("TESTRAY_BUILD_ID");

			if ((testrayBuild == null) && (testrayBuildId != null) &&
				testrayBuildId.matches("\\d+")) {

				testrayBuild = TestrayFactory.newTestrayBuild(
					testrayRoutine, Long.parseLong(testrayBuildId));
			}

			testrayBuildName = _getBuildParameter("TESTRAY_BUILD_NAME");

			if ((testrayBuild == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(testrayBuildName)) {

				testrayBuild = testrayRoutine.createTestrayBuild(
					testrayProductVersion, replace(testrayBuildName),
					testrayBuildDate, testrayBuildDescription, testrayBuildSHA);
			}

			if (testrayBuild == null) {
				JobProperty jobProperty = getJobProperty(
					"testray.build.id", testBaseDir);

				testrayBuildId = jobProperty.getValue();

				if ((testrayBuildId != null) &&
					testrayBuildId.matches("\\d+")) {

					testrayBuild = TestrayFactory.newTestrayBuild(
						testrayRoutine, Long.parseLong(testrayBuildId));
				}
			}

			if (testrayBuild == null) {
				JobProperty jobProperty = getJobProperty(
					"testray.build.name", testBaseDir);

				testrayBuildName = jobProperty.getValue();

				if (!JenkinsResultsParserUtil.isNullOrEmpty(testrayBuildName)) {
					testrayBuild = testrayRoutine.createTestrayBuild(
						testrayProductVersion, replace(testrayBuildName),
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

	@Override
	public Date getTestrayBuildDate() {
		if (hasControllerBuild()) {
			return getControllerStartDate();
		}

		return getTopLevelStartDate();
	}

	@Override
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

		URL testrayAttachmentURL = getTopLevelTestrayAttachmentURLBySuffix(
			"jenkins-report.html.gz");

		if (testrayAttachmentURL != null) {
			sb.append(testrayAttachmentURL);
			sb.append("?authuser=0");
		}
		else {
			sb.append(_getJenkinsReportURL());
		}

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

	@Override
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

	@Override
	public synchronized Set<TestrayBuild> getTestrayBuilds() {
		return new HashSet<>(_testrayBuilds.values());
	}

	@Override
	public synchronized Map<File, TestrayBuild> getTestrayBuildsMap() {
		return new HashMap<>(_testrayBuilds);
	}

	@Override
	public TestrayProductVersion getTestrayProductVersion() {
		return getTestrayProductVersion(null);
	}

	@Override
	public synchronized TestrayProductVersion getTestrayProductVersion(
		File testBaseDir) {

		TestrayProductVersion testrayProductVersion =
			_testrayProductVersions.get(testBaseDir);

		if (testrayProductVersion != null) {
			return testrayProductVersion;
		}

		long start = System.currentTimeMillis();

		try {
			String testrayProductVersionId = Environment.get(
				"TESTRAY_PRODUCT_VERSION_ID");

			TestrayProject testrayProject = getTestrayProject(testBaseDir);

			if ((testrayProductVersionId != null) &&
				testrayProductVersionId.matches("\\d+")) {

				testrayProductVersion =
					testrayProject.getTestrayProductVersionById(
						Long.parseLong(testrayProductVersionId));
			}

			String testrayProductVersionName = Environment.get(
				"TESTRAY_PRODUCT_VERSION_NAME");

			if ((testrayProductVersion == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(
					testrayProductVersionName)) {

				testrayProductVersion =
					testrayProject.createTestrayProductVersion(
						replace(testrayProductVersionName));
			}

			testrayProductVersionId = _getBuildParameter(
				"TESTRAY_PRODUCT_VERSION_ID");

			if ((testrayProductVersion == null) &&
				(testrayProductVersionId != null) &&
				testrayProductVersionId.matches("\\d+")) {

				testrayProductVersion =
					testrayProject.getTestrayProductVersionById(
						Long.parseLong(testrayProductVersionId));
			}

			testrayProductVersionName = _getBuildParameter(
				"TESTRAY_PRODUCT_VERSION_NAME");

			if ((testrayProductVersion == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(
					testrayProductVersionName)) {

				testrayProductVersion =
					testrayProject.createTestrayProductVersion(
						replace(testrayProductVersionName));
			}

			if (testrayProductVersion == null) {
				JobProperty jobProperty = getJobProperty(
					"testray.product.version.id", testBaseDir);

				testrayProductVersionId = jobProperty.getValue();

				if ((testrayProductVersionId != null) &&
					testrayProductVersionId.matches("\\d+")) {

					testrayProductVersion =
						testrayProject.getTestrayProductVersionById(
							Long.parseLong(testrayProductVersionId));
				}
			}

			String topLevelJobName = getTopLevelJobName();

			if ((testrayProductVersion == null) &&
				(topLevelJobName.equals("test-qa-websites-functional-daily") ||
				 topLevelJobName.equals(
					 "test-qa-websites-functional-weekly"))) {

				testrayProductVersion =
					testrayProject.createTestrayProductVersion(replace("1.x"));
			}

			if (testrayProductVersion == null) {
				JobProperty jobProperty = getJobProperty(
					"testray.product.version.name", testBaseDir);

				testrayProductVersionName = jobProperty.getValue();

				if (!JenkinsResultsParserUtil.isNullOrEmpty(
						testrayProductVersionName)) {

					testrayProductVersion =
						testrayProject.createTestrayProductVersion(
							replace(testrayProductVersionName));
				}
			}

			PortalRelease portalRelease = getPortalRelease();

			if (portalRelease != null) {
				String portalReleaseVersion = portalRelease.getPortalVersion();

				testrayProductVersion =
					testrayProject.createTestrayProductVersion(
						replace(portalReleaseVersion));
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

	@Override
	public TestrayProject getTestrayProject() {
		return getTestrayProject(null);
	}

	@Override
	public synchronized TestrayProject getTestrayProject(File testBaseDir) {
		TestrayProject testrayProject = _testrayProjects.get(testBaseDir);

		if (testrayProject != null) {
			return testrayProject;
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		try {
			String testrayProjectId = Environment.get("TESTRAY_PROJECT_ID");

			TestrayServer testrayServer = getTestrayServer(testBaseDir);

			if ((testrayProjectId != null) &&
				testrayProjectId.matches("\\d+")) {

				testrayProject = testrayServer.getTestrayProjectById(
					Long.parseLong(testrayProjectId));
			}

			String testrayProjectName = Environment.get("TESTRAY_PROJECT_NAME");

			if ((testrayProject == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(testrayProjectName)) {

				testrayProject = testrayServer.getTestrayProjectByName(
					replace(testrayProjectName));
			}

			if ((testrayProject == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(testrayProjectName)) {

				testrayProject = testrayServer.createTestrayProject(
					replace(testrayProjectName));
			}

			testrayProjectId = _getBuildParameter("TESTRAY_PROJECT_ID");

			if ((testrayProject == null) && (testrayProjectId != null) &&
				testrayProjectId.matches("\\d+")) {

				testrayProject = testrayServer.getTestrayProjectById(
					Long.parseLong(testrayProjectId));
			}

			testrayProjectName = _getBuildParameter("TESTRAY_PROJECT_NAME");

			if ((testrayProject == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(testrayProjectName)) {

				testrayProject = testrayServer.getTestrayProjectByName(
					replace(testrayProjectName));
			}

			if (testrayProject == null) {
				JobProperty jobProperty = getJobProperty(
					"testray.project.id", testBaseDir);

				testrayProjectId = jobProperty.getValue();

				if ((testrayProjectId != null) &&
					testrayProjectId.matches("\\d+")) {

					testrayProject = testrayServer.getTestrayProjectById(
						Long.parseLong(testrayProjectId));
				}
			}

			if (testrayProject == null) {
				JobProperty jobProperty = getJobProperty(
					"testray.project.name", testBaseDir);

				testrayProjectName = jobProperty.getValue();

				if (!JenkinsResultsParserUtil.isNullOrEmpty(
						testrayProjectName)) {

					testrayProject = testrayServer.getTestrayProjectByName(
						replace(testrayProjectName));
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
							replace(testrayProjectName));
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
						replace(testrayProjectName));
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

	@Override
	public TestrayRoutine getTestrayRoutine() {
		return getTestrayRoutine(null);
	}

	@Override
	public synchronized TestrayRoutine getTestrayRoutine(File testBaseDir) {
		TestrayRoutine testrayRoutine = _testrayRoutines.get(testBaseDir);

		if (testrayRoutine != null) {
			return testrayRoutine;
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		try {
			String testrayRoutineId = Environment.get("TESTRAY_ROUTINE_ID");

			TestrayProject testrayProject = getTestrayProject(testBaseDir);

			if ((testrayRoutineId != null) &&
				testrayRoutineId.matches("\\d+")) {

				testrayRoutine = testrayProject.getTestrayRoutineById(
					Long.parseLong(testrayRoutineId));
			}

			String testrayRoutineName = Environment.get("TESTRAY_ROUTINE_NAME");

			if ((testrayRoutine == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(testrayRoutineName)) {

				testrayRoutine = testrayProject.createTestrayRoutine(
					replace(testrayRoutineName));
			}

			testrayRoutineId = _getBuildParameter("TESTRAY_ROUTINE_ID");

			if ((testrayRoutine == null) && (testrayRoutineId != null) &&
				testrayRoutineId.matches("\\d+")) {

				testrayRoutine = testrayProject.getTestrayRoutineById(
					Long.parseLong(testrayRoutineId));
			}

			testrayRoutineName = _getBuildParameter("TESTRAY_ROUTINE_NAME");

			if ((testrayRoutine == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(testrayRoutineName)) {

				testrayRoutine = testrayProject.createTestrayRoutine(
					replace(testrayRoutineName));
			}

			testrayRoutineName = _getBuildParameter("TESTRAY_BUILD_TYPE");

			if ((testrayRoutine == null) &&
				!JenkinsResultsParserUtil.isNullOrEmpty(testrayRoutineName)) {

				testrayRoutine = testrayProject.createTestrayRoutine(
					replace(testrayRoutineName));
			}

			if (testrayRoutine == null) {
				JobProperty jobProperty = getJobProperty(
					"testray.routine.id", testBaseDir);

				testrayRoutineId = jobProperty.getValue();

				if ((testrayRoutineId != null) &&
					testrayRoutineId.matches("\\d+")) {

					testrayRoutine = testrayProject.getTestrayRoutineById(
						Long.parseLong(testrayRoutineId));
				}
			}

			if (testrayRoutine == null) {
				JobProperty jobProperty = getJobProperty(
					"testray.routine.name", testBaseDir);

				testrayRoutineName = jobProperty.getValue();

				if (!JenkinsResultsParserUtil.isNullOrEmpty(
						testrayRoutineName)) {

					testrayRoutine = testrayProject.createTestrayRoutine(
						replace(testrayRoutineName));
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
						replace(testrayRoutineName));
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

	@Override
	public TestrayServer getTestrayServer() {
		return getTestrayServer(null);
	}

	@Override
	public synchronized TestrayServer getTestrayServer(File testBaseDir) {
		TestrayServer testrayServer = _testrayServers.get(testBaseDir);

		if (testrayServer != null) {
			return testrayServer;
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		try {
			String testrayServerURL = Environment.get("TESTRAY_SERVER_URL");

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
				JobProperty jobProperty = getJobProperty(
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

	@Override
	public String replace(String string) {
		if (string == null) {
			return null;
		}

		string = _replace(string);

		if (!JenkinsResultsParserUtil.isNullOrEmpty(string) &&
			(string.length() > 150)) {

			string = string.substring(string.length() - 150);
		}

		return string;
	}

	@Override
	public String replaceSlack(String string, TestrayBuild testrayBuild) {
		if (string == null) {
			return null;
		}

		string = _replace(string);

		string = _replaceSlackTestrayInformation(string, testrayBuild);
		string = _replaceSlackTestrayImporter(string);

		return string;
	}

	protected BaseTestrayContext(BuildDatabase buildDatabase) {
		_buildDatabase = buildDatabase;

		_portalFixpackReleases = buildDatabase.getPortalFixpackReleases();
		_portalHotfixReleases = buildDatabase.getPortalHotfixReleases();
		_portalReleases = buildDatabase.getPortalReleases();
		_pullRequests = buildDatabase.getPullRequests();
		_workspaces = buildDatabase.getWorkspaces();
	}

	protected abstract int getControllerBuildNumber();

	protected abstract Map<String, String> getControllerBuildParameters();

	protected abstract JenkinsMaster getControllerJenkinsMaster();

	protected abstract String getControllerJobName();

	protected abstract Date getControllerStartDate();

	protected abstract String getTestSuiteName();

	protected abstract int getTopLevelBuildNumber();

	protected abstract Map<String, String> getTopLevelBuildParameters();

	protected abstract JenkinsMaster getTopLevelJenkinsMaster();

	protected abstract String getTopLevelJobName();

	protected abstract Date getTopLevelStartDate();

	protected abstract URL getTopLevelTestrayAttachmentURLBySuffix(
		String suffix);

	protected abstract boolean hasControllerBuild();

	private String _fixSlackString(String string) {
		string = string.replace("*", "&#42;");
		string = string.replace(">", "&gt;");
		string = string.replace("<", "&lt;");

		return string.replace("|", "&vert;");
	}

	private String _getBuildParameter(String buildParameterName) {
		Map<String, String> buildParameters = new HashMap<>();

		if (hasControllerBuild()) {
			buildParameters.putAll(getControllerBuildParameters());
		}

		buildParameters.putAll(getTopLevelBuildParameters());

		return buildParameters.get(buildParameterName);
	}

	private String _getBuildURL(
		int buildNumber, JenkinsMaster jenkinsMaster, String jobName) {

		return JenkinsResultsParserUtil.combine(
			"https://", jenkinsMaster.getName(), ".liferay.com/job/", jobName,
			"/", String.valueOf(buildNumber));
	}

	private String _getJenkinsReportURL() {
		JenkinsMaster topLevelJenkinsMaster = getTopLevelJenkinsMaster();

		return JenkinsResultsParserUtil.combine(
			"https://", topLevelJenkinsMaster.getName(), ".liferay.com/",
			"userContent/jobs/", getTopLevelJobName(), "/builds/",
			String.valueOf(getTopLevelBuildNumber()), "/jenkins-report.html");
	}

	private Job _getJob() {
		List<Job> jobs = getJobs();

		if (jobs.isEmpty()) {
			return null;
		}

		String testSuiteName = getTestSuiteName();

		for (Job job : jobs) {
			if (!(job instanceof TestSuiteJob)) {
				continue;
			}

			TestSuiteJob testSuiteJob = (TestSuiteJob)job;

			if (Objects.equals(
					testSuiteJob.getTestSuiteName(), testSuiteName)) {

				return job;
			}
		}

		return jobs.get(0);
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

	private Job.BuildProfile _getPortalBuildProfile() {
		Map<String, String> topLevelBuildParameters =
			getTopLevelBuildParameters();

		Job.BuildProfile buildProfile = Job.BuildProfile.getByString(
			topLevelBuildParameters.get("TEST_PORTAL_BUILD_PROFILE"));

		if (buildProfile != null) {
			return buildProfile;
		}

		return Job.BuildProfile.DXP;
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

	private String _replace(String string) {
		string = _replaceControllerBuild(string);
		string = _replacePluginsBranchInformationBuild(string);
		string = _replacePluginsTopLevelBuild(string);
		string = _replacePortalAppReleaseTopLevelBuild(string);
		string = _replacePortalBranchInformationBuild(string);
		string = _replacePortalRelease(string);
		string = _replacePullRequestBuild(string);
		string = _replaceQAWebsitesTopLevelBuild(string);
		string = _replaceTopLevelBuild(string);

		String topLevelJobName = getTopLevelJobName();

		if (topLevelJobName.contains("subrepository")) {
			string = _replaceSubrepository(string);
		}

		return string;
	}

	private String _replaceControllerBuild(String string) {
		if (!hasControllerBuild()) {
			return string;
		}

		JenkinsMaster controllerJenkinsMaster = getControllerJenkinsMaster();
		String controllerJobName = getControllerJobName();
		int controllerBuildNumber = getControllerBuildNumber();

		string = string.replace(
			"$(jenkins.controller.build.url)",
			_getBuildURL(
				controllerBuildNumber, controllerJenkinsMaster,
				controllerJobName));
		string = string.replace(
			"$(jenkins.controller.build.number)",
			String.valueOf(controllerBuildNumber));
		string = string.replace(
			"$(jenkins.controller.build.start)",
			JenkinsResultsParserUtil.toDateString(
				getControllerStartDate(), "yyyy-MM-dd HH:mm:ss",
				"America/Los_Angeles"));
		string = string.replace(
			"$(jenkins.controller.job.name)", controllerJobName);

		return string.replace(
			"$(jenkins.controller.master.hostname)",
			controllerJenkinsMaster.getName());
	}

	private String _replacePluginsBranchInformationBuild(String string) {
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

	private String _replacePluginsTopLevelBuild(String string) {
		Map<String, String> topLevelBuildParameters =
			getTopLevelBuildParameters();

		String pluginName = topLevelBuildParameters.get("TEST_PLUGIN_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(pluginName)) {
			string = string.replace("$(plugin.name)", pluginName);
		}

		return string;
	}

	private String _replacePortalAppReleaseTopLevelBuild(String string) {
		Map<String, String> topLevelBuildParameters =
			getTopLevelBuildParameters();

		String portalAppName = topLevelBuildParameters.get(
			"TEST_PORTAL_APP_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(portalAppName)) {
			string = string.replace("$(portal.app.name)", portalAppName);
		}

		return string;
	}

	private String _replacePortalBranchInformationBuild(String string) {
		Job.BuildProfile buildProfile = _getPortalBuildProfile();

		string = string.replace(
			"$(portal.profile)", buildProfile.toDisplayString());

		if (buildProfile == Job.BuildProfile.PORTAL) {
			string = string.replace("$(portal.type)", "CE");
		}
		else {
			string = string.replace("$(portal.type)", "EE");
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

	private String _replacePortalRelease(String string) {
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

			Map<String, String> topLevelBuildParameters =
				getTopLevelBuildParameters();

			String portalReleaseBuildVersion = topLevelBuildParameters.get(
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

	private String _replacePullRequestBuild(String string) {
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

	private String _replaceQAWebsitesTopLevelBuild(String string) {
		Map<String, String> topLevelBuildParameters =
			getTopLevelBuildParameters();

		String projectNames = topLevelBuildParameters.get("PROJECT_NAMES");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(projectNames)) {
			string = string.replace(
				"$(qa.websites.project.name)", projectNames);
		}

		return string;
	}

	private String _replaceSlackTestrayImporter(String string) {
		String buildNumber = Environment.get("BUILD_NUMBER");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(buildNumber)) {
			string = string.replace(
				"$(testray.importer.build.number)", buildNumber);
		}

		String buildURL = Environment.get("BUILD_URL");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(buildURL)) {
			string = string.replace("$(testray.importer.build.url)", buildURL);
		}

		String jobName = Environment.get("JOB_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(jobName)) {
			string = string.replace("$(testray.importer.job.name)", jobName);
		}

		return string;
	}

	private String _replaceSlackTestrayInformation(
		String string, TestrayBuild testrayBuild) {

		if (testrayBuild == null) {
			return string;
		}

		TestrayServer testrayServer = testrayBuild.getTestrayServer();

		if (testrayServer != null) {
			string = string.replace(
				"$(testray.server.url)",
				String.valueOf(testrayServer.getURL()));
		}

		TestrayProject testrayProject = testrayBuild.getTestrayProject();

		if (testrayProject != null) {
			string = string.replace(
				"$(testray.project.name)",
				_fixSlackString(testrayProject.getName()));

			string = string.replace(
				"$(testray.project.url)",
				String.valueOf(testrayProject.getURL()));
		}

		TestrayProductVersion testrayProductVersion =
			testrayBuild.getTestrayProductVersion();

		if (testrayProductVersion != null) {
			string = string.replace(
				"$(testray.product.version.name)",
				_fixSlackString(testrayProductVersion.getName()));
		}

		TestrayRoutine testrayRoutine = testrayBuild.getTestrayRoutine();

		if (testrayRoutine != null) {
			string = string.replace(
				"$(testray.routine.name)",
				_fixSlackString(testrayRoutine.getName()));
			string = string.replace(
				"$(testray.routine.url)",
				String.valueOf(testrayRoutine.getURL()));
		}

		string = string.replace(
			"$(testray.build.name)", _fixSlackString(testrayBuild.getName()));

		return string.replace(
			"$(testray.build.url)", String.valueOf(testrayBuild.getURL()));
	}

	private String _replaceSubrepository(String string) {
		Map<String, String> topLevelBuildParameters =
			getTopLevelBuildParameters();

		String gitHubUpstreamBranchName = topLevelBuildParameters.get(
			"GITHUB_UPSTREAM_BRANCH_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(gitHubUpstreamBranchName)) {
			string = string.replace(
				"$(github.upstream.branch.name)", gitHubUpstreamBranchName);
		}

		String repositoryName = topLevelBuildParameters.get("REPOSITORY_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(repositoryName)) {
			string = string.replace("$(repository.name)", repositoryName);
		}

		return string;
	}

	private String _replaceTopLevelBuild(String string) {
		string = string.replace("$(ci.test.suite)", getTestSuiteName());

		int topLevelBuildNumber = getTopLevelBuildNumber();

		string = string.replace(
			"$(jenkins.build.number)", String.valueOf(topLevelBuildNumber));
		string = string.replace(
			"$(jenkins.build.start)",
			JenkinsResultsParserUtil.toDateString(
				getTopLevelStartDate(), "yyyy-MM-dd[HH:mm:ss]",
				"America/Los_Angeles"));

		JenkinsMaster topLevelJenkinsMaster = getTopLevelJenkinsMaster();
		String topLevelJobName = getTopLevelJobName();

		string = string.replace(
			"$(jenkins.build.url)",
			_getBuildURL(
				topLevelBuildNumber, topLevelJenkinsMaster, topLevelJobName));
		string = string.replace("$(jenkins.job.name)", topLevelJobName);
		string = string.replace(
			"$(jenkins.master.hostname)", topLevelJenkinsMaster.getName());

		return string.replace("$(jenkins.report.url)", _getJenkinsReportURL());
	}

	private static final Pattern _quarterlyReleaseVersionPattern =
		Pattern.compile("(?<year>\\d{4}).(?<quarter>[Qq]\\d+).\\d+");
	private static final Pattern _releaseArtifactURLPattern = Pattern.compile(
		"https?://.+/(?<releaseName>[^/]+)(.7z|.tar.gz|.war|.zip)");
	private static final Pattern _releaseBranchPattern = Pattern.compile(
		"release-(?<year>\\d{4})\\.q(?<quarter>[1-4])");

	private final BuildDatabase _buildDatabase;
	private List<Job> _jobs;
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
	private final List<Workspace> _workspaces;

}