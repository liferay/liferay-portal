/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.failure.message.generator.CIFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.CompileFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.FailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.GenericFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.GradleTaskFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.IntegrationTestTimeoutFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.JSUnitTestFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.LocalGitMirrorFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.ModulesCompilationFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.PMDFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.PluginGitIDFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.SemanticVersioningFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.ServiceBuilderFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.SourceFormatFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.StartupFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.UpgradeFailureMessageGenerator;

import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang.StringEscapeUtils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

/**
 * @author Peter Yoo
 */
public class AxisBuild extends BaseBuild {

	@Override
	public void addTimelineData(TimelineData timelineData) {
		timelineData.addTimelineData(this);
	}

	@Override
	public String getArchivePath() {
		String archiveName = getArchiveName();

		if (archiveName == null) {
			System.out.println(
				"Build URL " + getBuildURL() + " has a null archive name");
		}

		StringBuilder sb = new StringBuilder(archiveName);

		if (!archiveName.endsWith("/")) {
			sb.append("/");
		}

		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		sb.append(jenkinsMaster.getName());

		sb.append("/");
		sb.append(getJobName());
		sb.append("/");
		sb.append(getAxisVariable());
		sb.append("/");
		sb.append(getBuildNumber());

		return sb.toString();
	}

	@Override
	public URL getArtifactsBaseURL() {
		BatchBuild batchBuild = getParentBatchBuild();

		StringBuilder sb = new StringBuilder();

		sb.append(batchBuild.getArtifactsBaseURL());
		sb.append("/");
		sb.append(getAxisNumber());

		try {
			return new URL(sb.toString());
		}
		catch (MalformedURLException malformedURLException) {
			return null;
		}
	}

	public String getAxisName() {
		return JenkinsResultsParserUtil.combine(
			getJobVariant(), "/", getAxisNumber());
	}

	public String getAxisNumber() {
		Matcher matcher = _axisVariablePattern.matcher(getAxisVariable());

		if (matcher.find()) {
			return matcher.group("axisNumber");
		}

		throw new RuntimeException(
			"Invalid axis variable: " + getAxisVariable());
	}

	public String getAxisVariable() {
		if (_axisVariable != null) {
			return _axisVariable;
		}

		String buildURL = getBuildURL();

		Matcher matcher = buildURLMultiPattern.find(buildURL);

		if (matcher == null) {
			throw new IllegalArgumentException("Invalid build URL " + buildURL);
		}

		_axisVariable = matcher.group("axisVariable");

		return _axisVariable;
	}

	public String getBatchName() {
		BatchBuild parentBatchBuild = getParentBatchBuild();

		return parentBatchBuild.getBatchName();
	}

	public String getBuildDescriptionTestrayReports() {
		Element unorderedListElement = Dom4JUtil.getNewElement("ul");

		for (TestResult testResult : getTestResults(null)) {
			if (!(testResult instanceof PoshiJUnitTestResult)) {
				continue;
			}

			Element listItemElement = Dom4JUtil.getNewElement(
				"li", unorderedListElement);

			Dom4JUtil.getNewElement(
				"strong", listItemElement, testResult.getDisplayName());

			Element reportLinksUnorderedListElement = Dom4JUtil.getNewElement(
				"ul", listItemElement);

			Element poshiReportListItemElement = Dom4JUtil.getNewElement(
				"li", reportLinksUnorderedListElement);

			PoshiJUnitTestResult poshiJUnitTestResult =
				(PoshiJUnitTestResult)testResult;

			Dom4JUtil.getNewAnchorElement(
				poshiJUnitTestResult.getPoshiReportURL(),
				poshiReportListItemElement, "Poshi Report");

			Element poshiSummaryListItemElement = Dom4JUtil.getNewElement(
				"li", reportLinksUnorderedListElement);

			Dom4JUtil.getNewAnchorElement(
				poshiJUnitTestResult.getPoshiSummaryURL(),
				poshiSummaryListItemElement, "Poshi Summary");

			Element poshiConsoleListItemElement = Dom4JUtil.getNewElement(
				"li", reportLinksUnorderedListElement);

			Dom4JUtil.getNewAnchorElement(
				poshiJUnitTestResult.getPoshiConsoleURL(),
				poshiConsoleListItemElement, "Poshi Console");
		}

		Dom4JUtil.addToElement(
			unorderedListElement, Dom4JUtil.getNewElement("br"));

		try {
			return Dom4JUtil.format(unorderedListElement, false);
		}
		catch (IOException ioException) {
			throw new RuntimeException("Unable to generate html", ioException);
		}
	}

	@Override
	public String getBuildName() {
		return JenkinsResultsParserUtil.combine(
			getJobVariant(), "/", getAxisVariable());
	}

	@Override
	public String getBuildURLRegex() {
		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		StringBuffer sb = new StringBuffer();

		sb.append("http[s]*:\\/\\/");
		sb.append(
			JenkinsResultsParserUtil.getRegexLiteral(jenkinsMaster.getName()));
		sb.append("[^\\/]*");
		sb.append("[\\/]+job[\\/]+");

		String jobNameRegexLiteral = JenkinsResultsParserUtil.getRegexLiteral(
			getJobName());

		jobNameRegexLiteral = jobNameRegexLiteral.replace("\\(", "(\\(|%28)");
		jobNameRegexLiteral = jobNameRegexLiteral.replace("\\)", "(\\)|%29)");

		sb.append(jobNameRegexLiteral);

		sb.append("[\\/]+");
		sb.append(JenkinsResultsParserUtil.getRegexLiteral(getAxisVariable()));
		sb.append("[\\/]+");
		sb.append(getBuildNumber());
		sb.append("[\\/]*");

		return sb.toString();
	}

	@Override
	public String getDisplayName() {
		return JenkinsResultsParserUtil.combine(
			getAxisVariable(), " #", String.valueOf(getBuildNumber()));
	}

	@Override
	public Element getGitHubMessageElement() {
		if (_gitHubMessageElement != null) {
			return _gitHubMessageElement;
		}

		String status = getStatus();

		if (!status.equals("completed") && (getParentBuild() != null)) {
			return null;
		}

		String result = getResult();

		if (result.equals("SUCCESS")) {
			return null;
		}

		Element messageElement = Dom4JUtil.getNewElement(
			"div", null,
			Dom4JUtil.getNewAnchorElement(
				getBuildURL() + "/consoleText", null, getDisplayName()));

		if (result.equals("ABORTED")) {
			messageElement.add(
				Dom4JUtil.toCodeSnippetElement("Build was aborted"));
		}

		if (result.equals("FAILURE")) {
			Element failureMessageElement = getFailureMessageElement();

			if (failureMessageElement != null) {
				messageElement.add(failureMessageElement);
			}
		}

		if (result.equals("UNSTABLE")) {
			List<Element> failureElements = getTestResultGitHubElements(
				getUniqueFailureTestResults());

			Dom4JUtil.getOrderedListElement(failureElements, messageElement, 3);

			if (failureElements.isEmpty()) {
				return null;
			}
		}

		_gitHubMessageElement = messageElement;

		return _gitHubMessageElement;
	}

	@Override
	public Long getInvokedTime() {
		if (invokedTime != null) {
			return invokedTime;
		}

		Build parentBuild = getParentBuild();

		invokedTime = parentBuild.getStartTime();

		return invokedTime;
	}

	public BatchBuild getParentBatchBuild() {
		Build parentBuild = getParentBuild();

		if (parentBuild instanceof BatchBuild) {
			return (BatchBuild)parentBuild;
		}

		return null;
	}

	@Override
	public Long getStartTime() {
		if (startTime != null) {
			return startTime;
		}

		String consoleText = getConsoleText();

		for (String line : consoleText.split("\n")) {
			Matcher matcher = _axisStartTimestampPattern.matcher(line);

			if (!matcher.find()) {
				continue;
			}

			Properties buildProperties = null;

			try {
				buildProperties = JenkinsResultsParserUtil.getBuildProperties();
			}
			catch (IOException ioException) {
				throw new RuntimeException("Unable to get build properties");
			}

			SimpleDateFormat sdf = new SimpleDateFormat(
				buildProperties.getProperty("jenkins.report.date.format"));

			Date date = null;

			try {
				date = sdf.parse(matcher.group("startTime"));
			}
			catch (ParseException parseException) {
				throw new RuntimeException(
					"Unable to get start time", parseException);
			}

			startTime = date.getTime();

			break;
		}

		return startTime;
	}

	@Override
	public List<TestResult> getTestResults(String testStatus) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(testStatus)) {
			return getTestResults();
		}

		List<TestResult> testResults = new ArrayList<>();

		for (TestResult testResult : getTestResults()) {
			if (testStatus.equals(testResult.getStatus())) {
				testResults.add(testResult);
			}
		}

		return testResults;
	}

	@Override
	public List<TestResult> getUniqueFailureTestResults() {
		List<TestResult> uniqueFailureTestResults = new ArrayList<>();

		for (TestResult testResult : getTestResults(null)) {
			if (!testResult.isFailing()) {
				continue;
			}

			if (testResult.isUniqueFailure()) {
				uniqueFailureTestResults.add(testResult);
			}
		}

		return uniqueFailureTestResults;
	}

	@Override
	public List<TestResult> getUpstreamJobFailureTestResults() {
		List<TestResult> upstreamFailureTestResults = new ArrayList<>();

		for (TestResult testResult : getTestResults(null)) {
			if (!testResult.isFailing()) {
				continue;
			}

			if (!testResult.isUniqueFailure()) {
				upstreamFailureTestResults.add(testResult);
			}
		}

		return upstreamFailureTestResults;
	}

	public List<String> getWarningMessages() {
		List<String> warningMessages = new ArrayList<>();

		URL poshiWarningsURL = null;

		try {
			poshiWarningsURL = new URL(
				getArtifactsBaseURL() + "/poshi-warnings.xml.gz");
		}
		catch (IOException ioException) {
			return warningMessages;
		}

		StringBuilder sb = new StringBuilder();

		try (InputStream inputStream = poshiWarningsURL.openStream();
			GZIPInputStream gzipInputStream = new GZIPInputStream(
				inputStream)) {

			int i = 0;

			while ((i = gzipInputStream.read()) > 0) {
				sb.append((char)i);
			}
		}
		catch (IOException ioException) {
			return warningMessages;
		}

		try {
			Document document = Dom4JUtil.parse(sb.toString());

			Element rootElement = document.getRootElement();

			for (Element valueElement : rootElement.elements("value")) {
				String liferayErrorText = "LIFERAY_ERROR: ";

				String valueElementText = StringEscapeUtils.escapeHtml(
					valueElement.getText());

				if (valueElementText.startsWith(liferayErrorText)) {
					valueElementText = valueElementText.substring(
						liferayErrorText.length());
				}

				warningMessages.add(valueElementText);
			}
		}
		catch (DocumentException documentException) {
			warningMessages.add("Unable to parse Poshi warnings");
		}

		return warningMessages;
	}

	@Override
	public void saveBuildURLInBuildDatabase() {
		BuildDatabase buildDatabase = getBuildDatabase();

		buildDatabase.putProperty(
			BUILD_URLS_PROPERTIES_KEY, getAxisName(), getBuildURL(), false);
	}

	protected AxisBuild(String url) {
		this(url, null);
	}

	protected AxisBuild(String url, BatchBuild parentBatchBuild) {
		super(JenkinsResultsParserUtil.getLocalURL(url), parentBatchBuild);
	}

	@Override
	protected Pattern getArchiveBuildURLPattern() {
		return archiveBuildURLPattern;
	}

	@Override
	protected MultiPattern getBuildURLMultiPattern() {
		return buildURLMultiPattern;
	}

	@Override
	protected FailureMessageGenerator[] getFailureMessageGenerators() {
		return _FAILURE_MESSAGE_GENERATORS;
	}

	@Override
	protected Element getGitHubMessageJobResultsElement() {
		return null;
	}

	@Override
	protected String getStopPropertiesTempMapURL() {
		if (fromArchive) {
			return getBuildURL() + "/stop-properties.json";
		}

		TopLevelBuild topLevelBuild = getTopLevelBuild();

		JenkinsMaster topLevelBuildJenkinsMaster =
			topLevelBuild.getJenkinsMaster();

		return JenkinsResultsParserUtil.combine(
			JenkinsResultsParserUtil.getJenkinsTempMapURL(), "/",
			topLevelBuildJenkinsMaster.getName(), "/",
			topLevelBuild.getJobName(), "/",
			String.valueOf(topLevelBuild.getBuildNumber()), "/", getJobName(),
			"/", getAxisVariable(), "/", getParameterValue("JOB_VARIANT"), "/",
			"stop.properties");
	}

	protected List<Element> getTestResultGitHubElements(
		List<TestResult> testResults) {

		List<Element> testResultGitHubElements = new ArrayList<>();

		for (TestResult testResult : testResults) {
			testResultGitHubElements.add(testResult.getGitHubElement());
		}

		return testResultGitHubElements;
	}

	protected static final Pattern archiveBuildURLPattern = Pattern.compile(
		JenkinsResultsParserUtil.combine(
			"(", Pattern.quote(Build.DEPENDENCIES_URL_TOKEN), "|",
			Pattern.quote(JenkinsResultsParserUtil.urlDependenciesFile), "|",
			Pattern.quote(JenkinsResultsParserUtil.urlDependenciesHttp),
			")/*(?<archiveName>.*)/(?<master>[^/]+)/+(?<jobName>[^/]+)/",
			"(?<axisVariable>" + AxisBuild._AXIS_VARIABLE_REGEX + ")/",
			"(?<buildNumber>\\d+)/?"));
	protected static final MultiPattern buildURLMultiPattern = new MultiPattern(
		JenkinsResultsParserUtil.combine(
			"\\w+://(?<master>[^/]+)/+job/+(?<jobName>[^/]+)/",
			"(?<buildNumber>\\d+)/",
			"(?<axisVariable>" + AxisBuild._AXIS_VARIABLE_REGEX + ")/?"),
		JenkinsResultsParserUtil.combine(
			"\\w+://(?<master>[^/]+)/+job/+(?<jobName>[^/]+)/",
			"(?<axisVariable>" + AxisBuild._AXIS_VARIABLE_REGEX + ")/",
			"(?<buildNumber>\\d+)/?"));
	protected static final String defaultLogBaseURL =
		"https://storage.cloud.google.com/testray-results";

	private static final String _AXIS_VARIABLE_REGEX =
		"AXIS_VARIABLE=(?<axisNumber>[^,/]+)(,[^/]+)?";

	private static final FailureMessageGenerator[] _FAILURE_MESSAGE_GENERATORS =
		{
			new ModulesCompilationFailureMessageGenerator(),
			//
			new CompileFailureMessageGenerator(),
			new IntegrationTestTimeoutFailureMessageGenerator(),
			new JSUnitTestFailureMessageGenerator(),
			new LocalGitMirrorFailureMessageGenerator(),
			new PMDFailureMessageGenerator(),
			new PluginGitIDFailureMessageGenerator(),
			new SemanticVersioningFailureMessageGenerator(),
			new ServiceBuilderFailureMessageGenerator(),
			new SourceFormatFailureMessageGenerator(),
			new StartupFailureMessageGenerator(),
			new UpgradeFailureMessageGenerator(),
			//
			new GradleTaskFailureMessageGenerator(),
			//
			new CIFailureMessageGenerator(),
			new GenericFailureMessageGenerator()
		};

	private static final Pattern _axisStartTimestampPattern = Pattern.compile(
		"\\s*\\[echo\\] startTime: (?<startTime>[^\\n]+)");
	private static final Pattern _axisVariablePattern = Pattern.compile(
		_AXIS_VARIABLE_REGEX);

	private String _axisVariable;
	private Element _gitHubMessageElement;

}