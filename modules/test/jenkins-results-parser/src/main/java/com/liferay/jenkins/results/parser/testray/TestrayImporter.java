/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.BuildReport;
import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.Environment;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.NotificationUtil;
import com.liferay.jenkins.results.parser.ParallelExecutor;
import com.liferay.jenkins.results.parser.PullRequest;
import com.liferay.jenkins.results.parser.QAWebsitesGitRepositoryJob;
import com.liferay.jenkins.results.parser.TestSuiteJob;
import com.liferay.jenkins.results.parser.TopLevelBuildReport;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.persistent.resource.PersistentResource;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

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

		_testrayContext = TestrayFactory.newTestrayContext(
			buildDatabase, topLevelBuildReport);
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

		PullRequest pullRequest = _testrayContext.getPullRequest();

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

		int i = 0;

		for (TestrayBuild testrayBuild : _testrayContext.getTestrayBuilds()) {
			String testrayRoutineTitle = "Testray Routine";

			if (i > 0) {
				testrayRoutineTitle = JenkinsResultsParserUtil.combine(
					testrayRoutineTitle, " (", String.valueOf(i), ")");
			}

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
					"Testray Build ID", String.valueOf(testrayBuild.getId())));

			i++;
		}

		String currentJobName = Environment.get("JOB_NAME");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(currentJobName)) {
			Dom4JUtil.addToElement(
				rootElement,
				_getJenkinsBuildDescriptionElement(
					"Testray Importer",
					JenkinsResultsParserUtil.combine(
						currentJobName, "#", Environment.get("BUILD_NUMBER")),
					Environment.get("BUILD_URL")));
		}

		try {
			String buildDescription = Dom4JUtil.format(rootElement, false);

			return buildDescription.replaceAll("\n", "<br />");
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	public TestrayBuild getTestrayBuild(File testBaseDir) {
		return _testrayContext.getTestrayBuild(testBaseDir);
	}

	public void postSlackNotification() {
		List<Long> testrayBuildIds = new ArrayList<>();

		Map<File, TestrayBuild> testrayBuildMap =
			_testrayContext.getTestrayBuildsMap();

		for (Map.Entry<File, TestrayBuild> testrayBuildEntry :
				testrayBuildMap.entrySet()) {

			File testBaseDir = testrayBuildEntry.getKey();

			TestrayBuild testrayBuild = testrayBuildEntry.getValue();

			if (testrayBuildIds.contains(testrayBuild.getId())) {
				continue;
			}

			testrayBuildIds.add(testrayBuild.getId());

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

		for (Job job : _testrayContext.getJobs()) {
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

			TestrayCaseResult topLevelTestrayCaseResult =
				_recordTopLevelTestrayCaseResult(job, testBaseDir);

			_recordAppServerTestrayCaseResult(
				job, PersistentResource.Type.ASAH_BUNDLE, testBaseDir,
				topLevelTestrayCaseResult);
			_recordAppServerTestrayCaseResult(
				job, PersistentResource.Type.FARO_BUNDLE, testBaseDir,
				topLevelTestrayCaseResult);
			_recordAppServerTestrayCaseResult(
				job, PersistentResource.Type.PORTAL_BUNDLE, testBaseDir,
				topLevelTestrayCaseResult);

			for (AxisTestClassGroup axisTestClassGroup : axisTestClassGroups) {
				callables.add(
					new Callable<Void>() {

						@Override
						public Void call() throws Exception {
							_recordAxisTestClassGroup(
								axisTestClassGroup, topLevelTestrayCaseResult);

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

		int failedTaskCount = parallelExecutor.getFailedTaskCount();

		if (failedTaskCount > 0) {
			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Unable to record ", String.valueOf(failedTaskCount),
					" of ", String.valueOf(callables.size()), " Testray axes"));
		}

		int uncreatedTestrayCaseResultsCount =
			_uncreatedTestrayCaseResultsCount.get();

		if (uncreatedTestrayCaseResultsCount > 0) {
			System.out.println(
				JenkinsResultsParserUtil.combine(
					"Unable to create ",
					String.valueOf(uncreatedTestrayCaseResultsCount), " of ",
					String.valueOf(callables.size()), " Testray case results"));
		}

		List<Long> testrayBuildIds = new ArrayList<>();

		for (TestrayBuild testrayBuild : _testrayContext.getTestrayBuilds()) {
			if (testrayBuildIds.contains(testrayBuild.getId())) {
				continue;
			}

			testrayBuildIds.add(testrayBuild.getId());

			TestrayServer testrayServer = testrayBuild.getTestrayServer();

			testrayServer.importCaseResults(_topLevelBuildReport);
		}

		_sendPullRequestNotification();
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

	private String _getSlackBody(File testBaseDir) {
		JobProperty jobProperty = _testrayContext.getJobProperty(
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
		String slackChannels = Environment.get("TESTRAY_SLACK_CHANNELS");

		if (JenkinsResultsParserUtil.isNullOrEmpty(slackChannels)) {
			JobProperty jobProperty = _testrayContext.getJobProperty(
				"testray.slack.channels", testBaseDir);

			slackChannels = jobProperty.getValue();
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(slackChannels)) {
			slackChannels = "testray-reports";
		}

		return _replaceSlackEnvVars(slackChannels, testBaseDir);
	}

	private String _getSlackIconEmoji(File testBaseDir) {
		String slackIconEmoji = Environment.get("TESTRAY_SLACK_ICON_EMOJI");

		if (JenkinsResultsParserUtil.isNullOrEmpty(slackIconEmoji)) {
			JobProperty jobProperty = _testrayContext.getJobProperty(
				"testray.slack.icon.emoji", testBaseDir);

			slackIconEmoji = jobProperty.getValue();
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(slackIconEmoji)) {
			return _replaceSlackEnvVars(slackIconEmoji, testBaseDir);
		}

		return ":liferay-ci:";
	}

	private String _getSlackSubject(File testBaseDir) {
		JobProperty jobProperty = _testrayContext.getJobProperty(
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
		String slackUsername = Environment.get("TESTRAY_SLACK_USERNAME");

		if (JenkinsResultsParserUtil.isNullOrEmpty(slackUsername)) {
			JobProperty jobProperty = _testrayContext.getJobProperty(
				"testray.slack.username", testBaseDir);

			slackUsername = jobProperty.getValue();
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(slackUsername)) {
			return _replaceSlackEnvVars(slackUsername, testBaseDir);
		}

		return "Liferay CI";
	}

	private TestrayCaseResult _recordAppServerTestrayCaseResult(
		Job job, PersistentResource.Type persistentResourceType,
		File testBaseDir, TestrayCaseResult topLevelTestrayCaseResult) {

		TestrayBuild testrayBuild = _testrayContext.getTestrayBuild(
			testBaseDir);

		AppServerBundleStandaloneBuildTestrayCaseResult
			appServerBundleStandaloneBuildTestrayCaseResult =
				new AppServerBundleStandaloneBuildTestrayCaseResult(
					String.valueOf(persistentResourceType), testrayBuild,
					_topLevelBuildReport);

		BuildReport buildReport =
			appServerBundleStandaloneBuildTestrayCaseResult.getBuildReport();

		if (buildReport == null) {
			return null;
		}

		appServerBundleStandaloneBuildTestrayCaseResult.
			setParentTestrayCaseResult(topLevelTestrayCaseResult);

		appServerBundleStandaloneBuildTestrayCaseResult.recordTestrayCaseResult(
			job);

		return appServerBundleStandaloneBuildTestrayCaseResult;
	}

	private void _recordAxisTestClassGroup(
		AxisTestClassGroup axisTestClassGroup,
		TestrayCaseResult topLevelTestrayCaseResult) {

		Job job = axisTestClassGroup.getJob();

		if ((job != null) && (job.getTestrayContext() == null)) {
			job.setTestrayContext(_testrayContext);
		}

		TestrayRun testrayRun = axisTestClassGroup.getTestrayRun();

		if (testrayRun == null) {
			throw new RuntimeException(
				"Unable to find a Testray run for " +
					axisTestClassGroup.getAxisName());
		}

		if (_testrayContext.hasTestrayCaseResultFile(axisTestClassGroup)) {
			_testrayContext.updateTestrayCaseResultFileDurations(
				axisTestClassGroup);

			return;
		}

		TestrayCaseResult buildTestrayCaseResult =
			TestrayFactory.newBuildTestrayCaseResult(
				axisTestClassGroup, testrayRun.getTestrayBuild(),
				_topLevelBuildReport);

		buildTestrayCaseResult.setParentTestrayCaseResult(
			topLevelTestrayCaseResult);

		buildTestrayCaseResult.setTestrayRun(testrayRun);

		buildTestrayCaseResult.cacheTestrayCaseResultURL();

		if (buildTestrayCaseResult.getTestrayCaseResultURL() == null) {
			_uncreatedTestrayCaseResultsCount.incrementAndGet();
		}

		_testrayContext.recordTestrayCaseResultFile(
			axisTestClassGroup, buildTestrayCaseResult);
	}

	private TestrayCaseResult _recordTopLevelTestrayCaseResult(
		Job job, File testBaseDir) {

		TopLevelStandaloneBuildTestrayCaseResult
			topLevelStandaloneBuildTestrayCaseResult =
				TestrayFactory.newTopLevelStandaloneBuildTestrayCaseResult(
					_testrayContext.getTestrayBuild(testBaseDir),
					_topLevelBuildReport);

		topLevelStandaloneBuildTestrayCaseResult.recordTestrayCaseResult(job);

		topLevelStandaloneBuildTestrayCaseResult.cacheTestrayCaseResultURL();

		URL testrayCaseResultURL =
			topLevelStandaloneBuildTestrayCaseResult.getTestrayCaseResultURL();

		if (testrayCaseResultURL == null) {
			_uncreatedTestrayCaseResultsCount.incrementAndGet();
		}

		return topLevelStandaloneBuildTestrayCaseResult;
	}

	private String _replaceSlackEnvVars(String string, File testBaseDir) {
		return _testrayContext.replaceSlack(
			string, _testrayContext.getTestrayBuild(testBaseDir));
	}

	private void _sendPullRequestNotification() {
		PullRequest pullRequest = _testrayContext.getPullRequest();

		if (pullRequest == null) {
			return;
		}

		pullRequest.addComment(getJenkinsBuildDescription());
	}

	private static final ExecutorService _executorService =
		JenkinsResultsParserUtil.getNewThreadPoolExecutor(10, true);

	private final TestrayContext _testrayContext;
	private final TopLevelBuildReport _topLevelBuildReport;
	private final AtomicInteger _uncreatedTestrayCaseResultsCount =
		new AtomicInteger();

}