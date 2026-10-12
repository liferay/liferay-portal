/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.BuildDatabase;
import com.liferay.jenkins.results.parser.BuildReport;
import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.Environment;
import com.liferay.jenkins.results.parser.JenkinsMaster;
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
import com.liferay.jenkins.results.parser.test.clazz.JSUnitJUnitTestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClass;
import com.liferay.jenkins.results.parser.test.clazz.TestClassMethod;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.FunctionalAxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.JSUnitAxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.JUnitAxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.ModulesAxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.PlaywrightAxisTestClassGroup;
import com.liferay.jenkins.results.parser.test.clazz.group.WorkspacesCompileAxisTestClassGroup;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringEscapeUtils;

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

	private Element _getTestcaseElement(
		TestrayCaseResult testrayCaseResult, String testSuiteName,
		String[] warnings) {

		Element testcaseElement = Dom4JUtil.getNewElement("testcase");

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

		Element propertiesElement = testcaseElement.addElement("properties");

		if (testSuiteName.equals("upstream-dxp")) {
			if (testrayCaseResult instanceof JUnitBatchBuildTestrayCaseResult) {
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
					StringEscapeUtils.escapeHtml4(warning));
			}
		}

		Element attachmentsElement = testcaseElement.addElement("attachments");

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

		return testcaseElement;
	}

	private List<Element> _getTestcaseElements(
		AxisTestClassGroup axisTestClassGroup,
		List<TestrayCaseResult> testrayCaseResults, String testSuiteName) {

		final String[] testrayCaseResultWarnings =
			_getTestrayCaseResultWarnings(testrayCaseResults);

		List<Callable<Element>> callables = new ArrayList<>();

		for (final TestrayCaseResult testrayCaseResult : testrayCaseResults) {
			callables.add(
				new Callable<Element>() {

					@Override
					public Element call() throws Exception {
						return _getTestcaseElement(
							testrayCaseResult, testSuiteName,
							testrayCaseResultWarnings);
					}

				});
		}

		ParallelExecutor<Element> parallelExecutor = new ParallelExecutor<>(
			callables, true, _caseResultExecutorService, true,
			"recordAxisTestClassGroup:" + axisTestClassGroup.getAxisName());

		try {
			return parallelExecutor.execute(60L * 30L);
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}
	}

	private String[] _getTestrayCaseResultWarnings(
		List<TestrayCaseResult> testrayCaseResults) {

		for (TestrayCaseResult testrayCaseResult : testrayCaseResults) {
			String[] warnings = testrayCaseResult.getWarnings();

			if (warnings != null) {
				return warnings;
			}
		}

		return null;
	}

	private boolean _isTestClassFileReported(TestClass testClass) {
		if (!(testClass instanceof JSUnitJUnitTestClass)) {
			return false;
		}

		JSUnitJUnitTestClass jsUnitJUnitTestClass =
			(JSUnitJUnitTestClass)testClass;

		if (!jsUnitJUnitTestClass.isTestClassFileReported()) {
			return false;
		}

		return testClass.hasTestClassMethods();
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

		TestrayRun testrayRun = axisTestClassGroup.getTestrayRun();

		TestrayBuild testrayBuild = testrayRun.getTestrayBuild();

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		Document document = DocumentHelper.createDocument();

		Element rootElement = document.addElement("testsuite");

		rootElement.add(testrayRun.getEnvironmentsElement());

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

		propertiesMap.put("testray.run.id", testrayRun.getRunIdString());
		propertiesMap.put(
			"testray.total.cpu.use.time",
			JenkinsResultsParserUtil.toDurationString(
				_topLevelBuildReport.getTotalActualDuration()));

		_addPropertyElements(
			rootElement.addElement("properties"), propertiesMap);

		List<TestrayCaseResult> testrayCaseResults = new ArrayList<>();

		TestrayCaseResult buildTestrayCaseResult =
			TestrayFactory.newBuildTestrayCaseResult(
				axisTestClassGroup, testrayBuild, _topLevelBuildReport);

		buildTestrayCaseResult.setParentTestrayCaseResult(
			topLevelTestrayCaseResult);

		buildTestrayCaseResult.setTestrayRun(testrayRun);

		buildTestrayCaseResult.cacheTestrayCaseResultURL();

		if (buildTestrayCaseResult.getTestrayCaseResultURL() == null) {
			_uncreatedTestrayCaseResultsCount.incrementAndGet();
		}

		testrayCaseResults.add(buildTestrayCaseResult);

		if (axisTestClassGroup instanceof FunctionalAxisTestClassGroup ||
			axisTestClassGroup instanceof JSUnitAxisTestClassGroup ||
			axisTestClassGroup instanceof JUnitAxisTestClassGroup ||
			axisTestClassGroup instanceof ModulesAxisTestClassGroup ||
			axisTestClassGroup instanceof WorkspacesCompileAxisTestClassGroup) {

			PortalLogBatchBuildTestrayCaseResult
				portalLogBatchBuildTestrayCaseResult =
					TestrayFactory.newPortalLogTestrayCaseResult(
						axisTestClassGroup, testrayBuild, _topLevelBuildReport);

			if (!JenkinsResultsParserUtil.isNullOrEmpty(
					portalLogBatchBuildTestrayCaseResult.getErrors())) {

				portalLogBatchBuildTestrayCaseResult.setParentTestrayCaseResult(
					buildTestrayCaseResult);
				portalLogBatchBuildTestrayCaseResult.setTestrayRun(testrayRun);

				testrayCaseResults.add(portalLogBatchBuildTestrayCaseResult);
			}

			for (TestClass testClass : axisTestClassGroup.getTestClasses()) {
				if (_isTestClassFileReported(testClass)) {
					for (TestClassMethod testClassMethod :
							testClass.getTestClassMethods()) {

						TestrayCaseResult testClassMethodTestrayCaseResult =
							TestrayFactory.newBuildTestrayCaseResult(
								axisTestClassGroup, testClass, testClassMethod,
								testrayBuild, _topLevelBuildReport);

						testClassMethodTestrayCaseResult.
							setParentTestrayCaseResult(buildTestrayCaseResult);

						testClassMethodTestrayCaseResult.setTestrayRun(
							testrayRun);

						testrayCaseResults.add(
							testClassMethodTestrayCaseResult);
					}

					continue;
				}

				TestrayCaseResult testClassTestrayCaseResult =
					TestrayFactory.newBuildTestrayCaseResult(
						axisTestClassGroup, testClass, testrayBuild,
						_topLevelBuildReport);

				testClassTestrayCaseResult.setParentTestrayCaseResult(
					buildTestrayCaseResult);
				testClassTestrayCaseResult.setTestrayRun(testrayRun);

				testrayCaseResults.add(testClassTestrayCaseResult);
			}
		}
		else if (axisTestClassGroup instanceof PlaywrightAxisTestClassGroup) {
			for (TestClass testClass : axisTestClassGroup.getTestClasses()) {
				for (TestClassMethod testClassMethod :
						testClass.getTestClassMethods()) {

					TestrayCaseResult testClassMethodTestrayCaseResult =
						TestrayFactory.newBuildTestrayCaseResult(
							axisTestClassGroup, testClass, testClassMethod,
							testrayBuild, _topLevelBuildReport);

					testClassMethodTestrayCaseResult.setParentTestrayCaseResult(
						buildTestrayCaseResult);
					testClassMethodTestrayCaseResult.setTestrayRun(testrayRun);

					testrayCaseResults.add(testClassMethodTestrayCaseResult);
				}
			}
		}

		List<Element> testcaseElements = _getTestcaseElements(
			axisTestClassGroup, testrayCaseResults,
			_topLevelBuildReport.getTestSuiteName());

		for (Element testcaseElement : testcaseElements) {
			rootElement.add(testcaseElement);
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

	private static final ExecutorService _caseResultExecutorService =
		JenkinsResultsParserUtil.getNewThreadPoolExecutor(20, true);
	private static final ExecutorService _executorService =
		JenkinsResultsParserUtil.getNewThreadPoolExecutor(10, true);

	private final TestrayContext _testrayContext;
	private final TopLevelBuildReport _topLevelBuildReport;
	private final AtomicInteger _uncreatedTestrayCaseResultsCount =
		new AtomicInteger();

}