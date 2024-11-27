/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.testray;

import com.liferay.jenkins.results.parser.Build;
import com.liferay.jenkins.results.parser.Dom4JUtil;
import com.liferay.jenkins.results.parser.DownstreamBuild;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.Job;
import com.liferay.jenkins.results.parser.QAWebsitesGitRepositoryJob;
import com.liferay.jenkins.results.parser.TestResult;
import com.liferay.jenkins.results.parser.TopLevelBuild;
import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.job.property.JobPropertyFactory;
import com.liferay.jenkins.results.parser.test.clazz.group.AxisTestClassGroup;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.WordUtils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

/**
 * @author Michael Hashimoto
 */
public class BatchBuildTestrayCaseResult extends BuildTestrayCaseResult {

	public BatchBuildTestrayCaseResult(
		TestrayBuild testrayBuild, TopLevelBuild topLevelBuild,
		AxisTestClassGroup axisTestClassGroup) {

		super(testrayBuild, topLevelBuild);

		_axisTestClassGroup = axisTestClassGroup;
	}

	public String getAxisName() {
		return _axisTestClassGroup.getAxisName();
	}

	public String getBatchName() {
		return _axisTestClassGroup.getBatchName();
	}

	@Override
	public Build getBuild() {
		TopLevelBuild topLevelBuild = getTopLevelBuild();

		DownstreamBuild downstreamBuild = topLevelBuild.getDownstreamBuild(
			getAxisName());

		if (downstreamBuild != null) {
			return downstreamBuild;
		}

		return topLevelBuild.getDownstreamAxisBuild(getAxisName());
	}

	@Override
	public String getComponentName() {
		try {
			return JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getBuildProperties(),
				"testray.case.component", getBatchName());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public String getErrors() {
		Build build = getBuild();

		if (build == null) {
			return "Unable to run on CI";
		}

		if (!build.isFailing()) {
			return null;
		}

		String result = build.getResult();

		if (result == null) {
			return "Unable to finish build on CI";
		}

		if (result.equals("ABORTED")) {
			return build.getJobName() + " timed out after 2 hours";
		}

		String errorMessage = build.getFailureMessage();

		if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
			return "Failed for unknown reason";
		}

		if (errorMessage.contains("\n")) {
			errorMessage = errorMessage.substring(
				0, errorMessage.indexOf("\n"));
		}

		errorMessage = errorMessage.trim();

		if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
			return "Failed for unknown reason";
		}

		return errorMessage;
	}

	@Override
	public String getName() {
		return getAxisName();
	}

	@Override
	public int getPriority() {
		try {
			String testrayCasePriority = JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getBuildProperties(),
				"testray.case.priority", getBatchName());

			if ((testrayCasePriority != null) &&
				testrayCasePriority.matches("\\d+")) {

				return Integer.parseInt(testrayCasePriority);
			}

			return 5;
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public String getTeamName() {
		JobProperty teamNamesJobProperty = _getJobProperty(
			"testray.team.names");

		String teamNames = teamNamesJobProperty.getValue();

		if (JenkinsResultsParserUtil.isNullOrEmpty(teamNames)) {
			try {
				return JenkinsResultsParserUtil.getProperty(
					JenkinsResultsParserUtil.getBuildProperties(),
					"testray.case.team", getBatchName());
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		String componentName = getComponentName();

		for (String teamName : teamNames.split(",")) {
			JobProperty teamComponentNamesJobProperty = _getJobProperty(
				"testray.team." + teamName + ".component.names");

			if (teamComponentNamesJobProperty == null) {
				continue;
			}

			String teamComponentNames =
				teamComponentNamesJobProperty.getValue();

			if (JenkinsResultsParserUtil.isNullOrEmpty(teamComponentNames)) {
				continue;
			}

			for (String teamComponentName : teamComponentNames.split(",")) {
				if (teamComponentName.equals(componentName)) {
					teamName = teamName.replace("-", " ");

					return WordUtils.capitalize(teamName);
				}
			}
		}

		try {
			return JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getBuildProperties(),
				"testray.case.team", getBatchName());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public List<TestrayAttachment> getTestrayAttachments() {
		List<TestrayAttachment> testrayAttachments = new ArrayList<>();

		testrayAttachments.addAll(_getDockerLogsTestrayAttachments());
		testrayAttachments.addAll(_getGCLogsTestrayAttachments());
		testrayAttachments.addAll(_getJStacksTestrayAttachments());

		testrayAttachments.add(_getGradlePluginsAttachment());
		testrayAttachments.add(_getJenkinsConsoleTestrayAttachment());
		testrayAttachments.add(getTopLevelBuildReportTestrayAttachment());
		testrayAttachments.add(getTopLevelJenkinsConsoleTestrayAttachment());
		testrayAttachments.add(getTopLevelJenkinsReportTestrayAttachment());
		testrayAttachments.add(getTopLevelJobSummaryTestrayAttachment());
		testrayAttachments.add(_getWarningsTestrayAttachment());

		testrayAttachments.removeAll(Collections.singleton(null));

		return testrayAttachments;
	}

	@Override
	public String getType() {
		try {
			return JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getBuildProperties(),
				"testray.case.type", getBatchName());
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	public String[] getWarnings() {
		TestrayAttachment testrayAttachment = _getWarningsTestrayAttachment();

		if (testrayAttachment == null) {
			return null;
		}

		String testrayAttachmentValue = testrayAttachment.getValue();

		if (JenkinsResultsParserUtil.isNullOrEmpty(testrayAttachmentValue)) {
			return null;
		}

		try {
			Document document = Dom4JUtil.parse(testrayAttachmentValue);

			Element rootElement = document.getRootElement();

			List<String> warnings = new ArrayList<>();

			for (Element valueElement : rootElement.elements()) {
				String warning = valueElement.getText();

				warning = warning.trim();

				if (JenkinsResultsParserUtil.isNullOrEmpty(warning)) {
					continue;
				}

				warnings.add(warning);
			}

			if (!warnings.isEmpty()) {
				return warnings.toArray(new String[0]);
			}
		}
		catch (DocumentException documentException) {
			return null;
		}

		return null;
	}

	protected String getAxisBuildURLPath() {
		return JenkinsResultsParserUtil.combine(
			getTopLevelBuildURLPath(), "/", getAxisName());
	}

	protected AxisTestClassGroup getAxisTestClassGroup() {
		return _axisTestClassGroup;
	}

	protected List<TestrayAttachment> getLiferayLogTestrayAttachments() {
		List<TestrayAttachment> testrayAttachments = new ArrayList<>();

		TestrayAttachment testrayAttachment = getTestrayAttachment(
			getBuild(), "Liferay Log",
			getAxisBuildURLPath() + "/liferay-log.txt.gz");

		if (testrayAttachment == null) {
			return testrayAttachments;
		}

		testrayAttachments.add(testrayAttachment);

		for (int i = 1; i <= 5; i++) {
			TestrayAttachment liferayLogTestrayAttachment =
				getTestrayAttachment(
					getBuild(), "Liferay Log (" + i + ")",
					JenkinsResultsParserUtil.combine(
						getAxisBuildURLPath(), "/liferay-log-",
						String.valueOf(i), ".txt.gz"));

			if (liferayLogTestrayAttachment == null) {
				break;
			}

			testrayAttachments.add(liferayLogTestrayAttachment);
		}

		return testrayAttachments;
	}

	protected List<TestrayAttachment> getLiferayOSGiLogTestrayAttachments() {
		List<TestrayAttachment> testrayAttachments = new ArrayList<>();

		TestrayAttachment testrayAttachment = getTestrayAttachment(
			getBuild(), "Liferay OSGi Log",
			getAxisBuildURLPath() + "/liferay-osgi-log.txt.gz");

		if (testrayAttachment == null) {
			return testrayAttachments;
		}

		testrayAttachments.add(testrayAttachment);

		for (int i = 1; i <= 5; i++) {
			TestrayAttachment liferayOSGiLogTestrayAttachment =
				getTestrayAttachment(
					getBuild(), "Liferay OSGi Log (" + i + ")",
					JenkinsResultsParserUtil.combine(
						getAxisBuildURLPath(), "/liferay-osgi-log-",
						String.valueOf(i), ".txt.gz"));

			if (liferayOSGiLogTestrayAttachment == null) {
				break;
			}

			testrayAttachments.add(liferayOSGiLogTestrayAttachment);
		}

		return testrayAttachments;
	}

	protected TestResult getTestResult() {
		return null;
	}

	protected long getTestResultDuration() {
		TestResult testResult = getTestResult();

		if (testResult == null) {
			return 0;
		}

		return testResult.getDuration();
	}

	protected String getTestResultErrors() {
		TestResult testResult = getTestResult();

		Build build = getBuild();

		if (testResult == null) {
			String failureMessage = "Failed prior to running test";

			if (build == null) {
				failureMessage = "Unable to run build on CI";
			}

			String result = build.getResult();

			if (result == null) {
				failureMessage = "Unable to finish build on CI";
			}

			if (result.equals("ABORTED")) {
				failureMessage =
					build.getJobName() + " timed out after 2 hours";
			}

			if (result.equals("SUCCESS") || result.equals("UNSTABLE")) {
				failureMessage = "Unable to run test on CI";
			}

			String buildFailureMessage = build.getFailureMessage();

			if (buildFailureMessage == null) {
				return failureMessage;
			}

			return failureMessage + ": " + buildFailureMessage;
		}

		if (testResult.isSkipped()) {
			return "Failed to run test on CI";
		}

		if (!testResult.isFailing()) {
			return null;
		}

		String errorMessage = testResult.getErrorDetails();

		if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
			errorMessage = build.getFailureMessage();
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
			return "Failed for unknown reason";
		}

		if (errorMessage.contains("\n")) {
			errorMessage = errorMessage.substring(
				0, errorMessage.indexOf("\n"));
		}

		errorMessage = errorMessage.trim();

		if (JenkinsResultsParserUtil.isNullOrEmpty(errorMessage)) {
			return "Failed for unknown reason";
		}

		return errorMessage;
	}

	protected Status getTestResultStatus() {
		Build build = getBuild();

		if (build == null) {
			return Status.UNTESTED;
		}

		TestResult testResult = getTestResult();

		if (testResult == null) {
			String result = build.getResult();

			if ((result == null) || result.equals("SUCCESS") ||
				result.equals("UNSTABLE")) {

				return Status.UNTESTED;
			}

			return Status.FAILED;
		}

		if (testResult.isFailing()) {
			return Status.FAILED;
		}
		else if (testResult.isSkipped()) {
			return Status.UNTESTED;
		}

		return Status.PASSED;
	}

	@Override
	protected TestrayAttachment getTopLevelBuildReportTestrayAttachment() {
		TopLevelBuildTestrayCaseResult topLevelBuildTestrayCaseResult =
			getTopLevelBuildTestrayCaseResult();

		if (topLevelBuildTestrayCaseResult == null) {
			return null;
		}

		return topLevelBuildTestrayCaseResult.
			getTopLevelBuildReportTestrayAttachment();
	}

	protected TopLevelBuildTestrayCaseResult
		getTopLevelBuildTestrayCaseResult() {

		if (_topLevelBuildTestrayCaseResult != null) {
			return _topLevelBuildTestrayCaseResult;
		}

		_topLevelBuildTestrayCaseResult =
			TestrayFactory.newTopLevelBuildTestrayCaseResult(
				getTestrayBuild(), getTopLevelBuild());

		return _topLevelBuildTestrayCaseResult;
	}

	@Override
	protected TestrayAttachment getTopLevelJenkinsConsoleTestrayAttachment() {
		TopLevelBuildTestrayCaseResult topLevelBuildTestrayCaseResult =
			getTopLevelBuildTestrayCaseResult();

		if (topLevelBuildTestrayCaseResult == null) {
			return null;
		}

		return topLevelBuildTestrayCaseResult.
			getTopLevelJenkinsConsoleTestrayAttachment();
	}

	@Override
	protected TestrayAttachment getTopLevelJenkinsReportTestrayAttachment() {
		TopLevelBuildTestrayCaseResult topLevelBuildTestrayCaseResult =
			getTopLevelBuildTestrayCaseResult();

		if (topLevelBuildTestrayCaseResult == null) {
			return null;
		}

		return topLevelBuildTestrayCaseResult.
			getTopLevelJenkinsReportTestrayAttachment();
	}

	@Override
	protected TestrayAttachment getTopLevelJobSummaryTestrayAttachment() {
		TopLevelBuildTestrayCaseResult topLevelBuildTestrayCaseResult =
			getTopLevelBuildTestrayCaseResult();

		if (topLevelBuildTestrayCaseResult == null) {
			return null;
		}

		return topLevelBuildTestrayCaseResult.
			getTopLevelJobSummaryTestrayAttachment();
	}

	private List<TestrayAttachment> _getDockerLogsTestrayAttachments() {
		List<TestrayAttachment> testrayAttachments = new ArrayList<>();

		Build build = getBuild();

		if (build == null) {
			return testrayAttachments;
		}

		for (URL testrayAttachmentURL : build.getTestrayAttachmentURLs()) {
			Matcher matcher = _dockerLogsURLPattern.matcher(
				String.valueOf(testrayAttachmentURL));

			if (!matcher.find()) {
				continue;
			}

			testrayAttachments.add(
				getTestrayAttachment(
					build, "Docker Log (" + matcher.group("fileName") + ")",
					getAxisBuildURLPath() + "/" + matcher.group("key")));
		}

		return testrayAttachments;
	}

	private List<TestrayAttachment> _getGCLogsTestrayAttachments() {
		List<TestrayAttachment> testrayAttachments = new ArrayList<>();

		Build build = getBuild();

		if (build == null) {
			return testrayAttachments;
		}

		for (URL testrayAttachmentURL : build.getTestrayAttachmentURLs()) {
			Matcher matcher = _gcLogsURLPattern.matcher(
				String.valueOf(testrayAttachmentURL));

			if (!matcher.find()) {
				continue;
			}

			testrayAttachments.add(
				getTestrayAttachment(
					build, "GC Log (" + matcher.group("fileName") + ")",
					getAxisBuildURLPath() + "/" + matcher.group("key")));
		}

		return testrayAttachments;
	}

	private TestrayAttachment _getGradlePluginsAttachment() {
		return getTestrayAttachment(
			getBuild(), "Gradle Plugins Test Report",
			getAxisBuildURLPath() + "/gradle_plugins.tar.gz");
	}

	private TestrayAttachment _getJenkinsConsoleTestrayAttachment() {
		String name = "Jenkins Console";
		String key = getAxisBuildURLPath() + "/jenkins-console.txt.gz";

		TestrayAttachment testrayAttachment = getTestrayAttachment(
			getBuild(), name, key);

		if (testrayAttachment != null) {
			return testrayAttachment;
		}

		final Build build = getBuild();

		if (build == null) {
			return null;
		}

		return uploadTestrayAttachment(
			name, key,
			new Callable<File>() {

				@Override
				public File call() throws Exception {
					File jenkinsConsoleFile = new File(
						getTestrayUploadBaseDir(), "jenkins-console.txt");
					File jenkinsConsoleGzFile = new File(
						getTestrayUploadBaseDir(), "jenkins-console.txt.gz");

					try {
						JenkinsResultsParserUtil.write(
							jenkinsConsoleFile, build.getConsoleText());

						JenkinsResultsParserUtil.gzip(
							jenkinsConsoleFile, jenkinsConsoleGzFile);
					}
					catch (IOException ioException) {
						throw new RuntimeException(ioException);
					}
					finally {
						JenkinsResultsParserUtil.delete(jenkinsConsoleFile);
					}

					if (jenkinsConsoleGzFile.exists()) {
						return jenkinsConsoleGzFile;
					}

					return null;
				}

			});
	}

	private JobProperty _getJobProperty(String basePropertyName) {
		TopLevelBuild topLevelBuild = getTopLevelBuild();

		Job job = topLevelBuild.getJob();

		if (job instanceof QAWebsitesGitRepositoryJob) {
			AxisTestClassGroup axisTestClassGroup = getAxisTestClassGroup();

			return JobPropertyFactory.newJobProperty(
				basePropertyName, job, axisTestClassGroup.getTestBaseDir(),
				JobProperty.Type.QA_WEBSITES_TEST_DIR);
		}

		return JobPropertyFactory.newJobProperty(basePropertyName, job);
	}

	private List<TestrayAttachment> _getJStacksTestrayAttachments() {
		List<TestrayAttachment> testrayAttachments = new ArrayList<>();

		Build build = getBuild();

		if (build == null) {
			return testrayAttachments;
		}

		for (URL testrayAttachmentURL : build.getTestrayAttachmentURLs()) {
			Matcher matcher = _jStacksURLPattern.matcher(
				String.valueOf(testrayAttachmentURL));

			if (!matcher.find()) {
				continue;
			}

			testrayAttachments.add(
				getTestrayAttachment(
					build, "Docker Log (" + matcher.group("fileName") + ")",
					getAxisBuildURLPath() + "/" + matcher.group("key")));
		}

		return testrayAttachments;
	}

	private TestrayAttachment _getWarningsTestrayAttachment() {
		return getTestrayAttachment(
			getBuild(), "Warnings",
			getAxisBuildURLPath() + "/warnings.html.gz");
	}

	private static final Pattern _dockerLogsURLPattern = Pattern.compile(
		"https?://.+/(?<key>docker-logs/(?<fileName>[^/]+.log).txt.gz)");
	private static final Pattern _gcLogsURLPattern = Pattern.compile(
		"https?://.+/(?<key>gc/(?<fileName>[^/]+.log).txt.gz)");
	private static final Pattern _jStacksURLPattern = Pattern.compile(
		"https?://.+/(?<key>jstacks/(?<fileName>[^/]+.log).txt.gz)");

	private final AxisTestClassGroup _axisTestClassGroup;
	private TopLevelBuildTestrayCaseResult _topLevelBuildTestrayCaseResult;

}