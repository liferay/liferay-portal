/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.failure.message.generator.FailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.GenericFailureMessageGenerator;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Kevin Yen
 */
public abstract class BaseBuild implements Build {

	@Override
	public void addInvocation(Invocation invocation) {
		_invocations.add(invocation);
	}

	@Override
	public void addTestrayAttachmentURL(URL testrayAttachmentURL) {
		if (_testrayAttachmentURLs.contains(testrayAttachmentURL)) {
			return;
		}

		_testrayAttachmentURLs.add(testrayAttachmentURL);
	}

	@Override
	public void archive() {
		archive(getArchiveName());
	}

	@Override
	public void archive(String archiveName) {
		setArchiveName(archiveName);

		if (fromArchive) {
			return;
		}

		File archiveDir = new File(getArchiveRootDir(), getArchivePath());

		if (!archiveDir.exists()) {
			archiveDir.mkdirs();
		}

		ParallelExecutor<Object> parallelExecutor = new ParallelExecutor<>(
			getArchiveCallables(), getExecutorService(), "archive");

		try {
			parallelExecutor.execute();
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof BaseBuild)) {
			return false;
		}

		BaseBuild baseBuild = (BaseBuild)object;

		return Objects.equals(getBuildURL(), baseBuild.getBuildURL());
	}

	@Override
	public String getArchiveName() {
		if (getParentBuild() == null) {
			return _archiveName;
		}

		Build topLevelBuild = getTopLevelBuild();

		if (this == topLevelBuild) {
			return _archiveName;
		}

		return topLevelBuild.getArchiveName();
	}

	@Override
	public String getArchivePath() {
		String archiveName = getArchiveName();

		StringBuilder sb = new StringBuilder(archiveName);

		if (!archiveName.endsWith("/")) {
			sb.append("/");
		}

		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		sb.append(jenkinsMaster.getName());

		sb.append("/");
		sb.append(getJobName());
		sb.append("/");
		sb.append(getBuildNumber());

		return sb.toString();
	}

	@Override
	public File getArchiveRootDir() {
		Build parentBuild = getParentBuild();

		if (parentBuild == null) {
			return _archiveRootDir;
		}

		if (equals(parentBuild)) {
			System.out.println("STACKOVERFLOW CATCH");

			return _archiveRootDir;
		}

		return parentBuild.getArchiveRootDir();
	}

	@Override
	public List<String> getBadBuildURLs() {
		if (_invocations.size() <= 1) {
			return Collections.emptyList();
		}

		List<String> badBuildURLs = new ArrayList<>();

		for (Invocation invocation :
				_invocations.subList(0, _invocations.size() - 1)) {

			badBuildURLs.add(invocation.getBuildURL());
		}

		return badBuildURLs;
	}

	@Override
	public String getBaseGitRepositoryName() {
		if (gitRepositoryName == null) {
			Properties buildProperties = null;

			try {
				buildProperties = JenkinsResultsParserUtil.getBuildProperties();
			}
			catch (IOException ioException) {
				throw new RuntimeException(
					"Unable to get build.properties", ioException);
			}

			TopLevelBuild topLevelBuild = getTopLevelBuild();

			gitRepositoryName = topLevelBuild.getParameterValue(
				"REPOSITORY_NAME");

			if ((gitRepositoryName != null) && !gitRepositoryName.isEmpty()) {
				return gitRepositoryName;
			}

			gitRepositoryName = buildProperties.getProperty(
				JenkinsResultsParserUtil.combine(
					"repository[", topLevelBuild.getJobName(), "]"));

			if (gitRepositoryName == null) {
				throw new RuntimeException(
					"Unable to get Git repository name for job " +
						topLevelBuild.getJobName());
			}
		}

		return gitRepositoryName;
	}

	@Override
	public String getBaseGitRepositorySHA(String gitRepositoryName) {
		TopLevelBuild topLevelBuild = getTopLevelBuild();

		if ((topLevelBuild instanceof WorkspaceBuild) && !fromArchive) {
			WorkspaceBuild workspaceBuild = (WorkspaceBuild)topLevelBuild;

			Workspace workspace = workspaceBuild.getWorkspace();

			WorkspaceGitRepository workspaceGitRepository =
				workspace.getPrimaryWorkspaceGitRepository();

			return workspaceGitRepository.getBaseBranchSHA();
		}

		if (gitRepositoryName.equals("liferay-jenkins-ee")) {
			Map<String, String> topLevelBuildStartPropertiesTempMap =
				topLevelBuild.getStartPropertiesTempMap();

			return topLevelBuildStartPropertiesTempMap.get(
				"JENKINS_GITHUB_UPSTREAM_BRANCH_SHA");
		}

		Map<String, String> gitRepositoryGitDetailsTempMap =
			topLevelBuild.getBaseGitRepositoryDetailsTempMap();

		return gitRepositoryGitDetailsTempMap.get("github.upstream.branch.sha");
	}

	@Override
	public String getBatchName(String jobVariant) {
		jobVariant = jobVariant.replaceAll("(.*)/.*", "$1");

		return jobVariant.replaceAll("_stable$", "");
	}

	@Override
	public String getBranchName() {
		return _branchName;
	}

	@Override
	public BuildDatabase getBuildDatabase() {
		if (_buildDatabase != null) {
			return _buildDatabase;
		}

		TopLevelBuild topLevelBuild = getTopLevelBuild();

		if ((topLevelBuild != null) && (topLevelBuild != this)) {
			_buildDatabase = topLevelBuild.getBuildDatabase();
		}
		else {
			_buildDatabase = BuildDatabaseUtil.getBuildDatabase(this);
		}

		return _buildDatabase;
	}

	@Override
	public String getBuildDescription() {
		if ((_buildDescription == null) && (getBuildURL() != null)) {
			JSONObject descriptionJSONObject = getBuildJSONObject(
				"description");

			if (descriptionJSONObject == null) {
				return null;
			}

			String description = descriptionJSONObject.optString("description");

			if (description.equals("")) {
				description = null;
			}

			_buildDescription = description;
		}

		return _buildDescription;
	}

	@Override
	public String getBuildDirPath() {
		StringBuilder sb = new StringBuilder();

		if (JenkinsResultsParserUtil.isWindows()) {
			sb.append("C:");
		}

		Properties buildProperties = null;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		sb.append(buildProperties.getProperty("jenkins.tmp.dir"));

		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		sb.append(jenkinsMaster.getName());

		sb.append("/");
		sb.append(getJobName());

		if (this instanceof AxisBuild) {
			sb.append("/");

			AxisBuild axisBuild = (AxisBuild)this;

			sb.append(axisBuild.getAxisNumber());
		}

		sb.append("/");
		sb.append(getBuildNumber());

		return sb.toString();
	}

	@Override
	public JSONObject getBuildJSONObject() {
		if (_buildJSONObject != null) {
			return _buildJSONObject;
		}

		String archiveFileContent = getArchiveFileContent("api/json");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(archiveFileContent)) {
			_buildJSONObject = new JSONObject(archiveFileContent);

			return _buildJSONObject;
		}

		try {
			JSONObject buildJSONObject = JenkinsResultsParserUtil.toJSONObject(
				JenkinsResultsParserUtil.getLocalURL(
					getBuildURL() + "api/json"),
				false);

			if (!isCompleted()) {
				return buildJSONObject;
			}

			_buildJSONObject = buildJSONObject;

			return _buildJSONObject;
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get build JSON object", ioException);
		}
	}

	@Override
	public JSONObject getBuildJSONObject(String tree) {
		String archiveFileContent = getArchiveFileContent("api/json");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(archiveFileContent)) {
			return new JSONObject(archiveFileContent);
		}

		String buildURL = getBuildURL();

		if (JenkinsResultsParserUtil.isNullOrEmpty(buildURL)) {
			return null;
		}

		return JenkinsAPIUtil.getAPIJSONObject(buildURL, tree);
	}

	@Override
	public int getBuildNumber() {
		String buildURL = getBuildURL();

		if (!JenkinsResultsParserUtil.isURL(buildURL)) {
			return -1;
		}

		MultiPattern buildURLMultiPattern = getBuildURLMultiPattern();

		Matcher matcher = buildURLMultiPattern.find(buildURL);

		if (matcher == null) {
			return -1;
		}

		return Integer.parseInt(matcher.group("buildNumber"));
	}

	@Override
	public Job.BuildProfile getBuildProfile() {
		String buildProfile = getParameterValue("TEST_PORTAL_BUILD_PROFILE");

		if (JenkinsResultsParserUtil.isNullOrEmpty(buildProfile)) {
			buildProfile = System.getenv("TEST_PORTAL_BUILD_PROFILE");
		}

		if (!JenkinsResultsParserUtil.isNullOrEmpty(buildProfile)) {
			if (buildProfile.equals("dxp")) {
				return Job.BuildProfile.DXP;
			}

			return Job.BuildProfile.PORTAL;
		}

		String branchName = getBranchName();

		if (!branchName.equals("master") && !branchName.startsWith("ee-")) {
			return Job.BuildProfile.DXP;
		}

		return Job.BuildProfile.PORTAL;
	}

	@Override
	public JSONObject getBuildReportJSONObject() {
		JSONObject buildReportJSONObject = new JSONObject();

		buildReportJSONObject.put(
			"buildURL", getBuildURL()
		).put(
			"duration", getDuration()
		);

		if (isFailing()) {
			buildReportJSONObject.put("failureMessage", getFailureMessage());
		}

		buildReportJSONObject.put(
			"result", getResult()
		).put(
			"startTime", getStartTime()
		);

		StopWatchRecordsGroup stopWatchRecordsGroup =
			getStopWatchRecordsGroup();

		if (stopWatchRecordsGroup != null) {
			buildReportJSONObject.put(
				"stopWatchRecords", stopWatchRecordsGroup.getJSONArray());
		}

		buildReportJSONObject.put(
			"testrayAttachmentURLs", getTestrayAttachmentURLs());

		return buildReportJSONObject;
	}

	@Override
	public String getBuildURL() {
		return _buildURL;
	}

	@Override
	public String getBuildURLRegex() {
		StringBuffer sb = new StringBuffer();

		sb.append("http[s]*:\\/\\/");

		JenkinsMaster jenkinsMaster = getJenkinsMaster();

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
		sb.append(getBuildNumber());
		sb.append("[\\/]*");

		return sb.toString();
	}

	@Override
	public String getConsoleText() {
		String archiveFileContent = getArchiveFileContent("consoleText");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(archiveFileContent)) {
			return archiveFileContent;
		}

		String buildURL = getBuildURL();

		if (buildURL == null) {
			return "";
		}

		if (_jenkinsConsoleTextLoader == null) {
			_jenkinsConsoleTextLoader = JenkinsConsoleTextLoader.getInstance(
				getBuildURL());
		}

		return _jenkinsConsoleTextLoader.getConsoleText();
	}

	@Override
	public Invocation getCurrentInvocation() {
		if (_invocations.isEmpty()) {
			return null;
		}

		return _invocations.get(_invocations.size() - 1);
	}

	@Override
	public Long getDelayTime() {
		Long startTime = getStartTime();

		long currentTime = JenkinsResultsParserUtil.getCurrentTimeMillis();

		if (startTime == null) {
			startTime = currentTime;
		}

		Long invokedTime = getInvokedTime();

		if (invokedTime == null) {
			invokedTime = currentTime;
		}

		return startTime - invokedTime + getQueuingDuration();
	}

	@Override
	public int getDepth() {
		Build parentBuild = getParentBuild();

		if (parentBuild == null) {
			return 0;
		}

		return parentBuild.getDepth() + 1;
	}

	@Override
	public String getDisplayName() {
		StringBuilder sb = new StringBuilder();

		sb.append(getJobName());

		String jobVariant = getParameterValue("JOB_VARIANT");

		if ((jobVariant != null) && !jobVariant.isEmpty()) {
			sb.append("/");
			sb.append(jobVariant);
		}

		return sb.toString();
	}

	@Override
	public long getDuration() {
		if (_duration != null) {
			return _duration;
		}

		JSONObject buildJSONObject = getBuildJSONObject("duration,timestamp");

		if (buildJSONObject == null) {
			return 0;
		}

		long duration = buildJSONObject.getLong("duration");

		if (duration == 0) {
			long timestamp = buildJSONObject.getLong("timestamp");

			return JenkinsResultsParserUtil.getCurrentTimeMillis() - timestamp;
		}

		_duration = duration;

		return _duration;
	}

	@Override
	public String getFailureMessage() {
		for (FailureMessageGenerator failureMessageGenerator :
				getFailureMessageGenerators()) {

			try {
				String failureMessage = failureMessageGenerator.getMessage(
					this);

				if (failureMessage != null) {
					return failureMessage;
				}
			}
			catch (Exception exception) {
				exception.printStackTrace();

				Class<?> clazz = failureMessageGenerator.getClass();

				String className = clazz.getName();

				StringBuilder sb = new StringBuilder();

				sb.append("Unable to get failure message from " + className);
				sb.append(className);
				sb.append("\n");
				sb.append(getBuildURL());

				NotificationUtil.sendEmail(
					sb.toString(), "jenkins", "Unable to get failure message",
					"calum.ragan@liferay.com");
			}
		}

		return null;
	}

	@Override
	public Element getGitHubMessageBuildAnchorElement() {
		getResult();

		int i = 0;

		String result = getResult();

		while (result == null) {
			if (i == 2) {
				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Unable to create build anchor element. The process ",
						"timed out while waiting for a build result for ",
						getBuildURL(), "."));

				break;
			}

			JenkinsResultsParserUtil.sleep(1000 * 5);

			result = getResult();

			i++;
		}

		if (Objects.equals(result, "SUCCESS")) {
			return Dom4JUtil.getNewAnchorElement(
				getBuildURL(), getDisplayName());
		}

		return Dom4JUtil.getNewAnchorElement(
			getBuildURL(), null,
			Dom4JUtil.getNewElement("strike", null, getDisplayName()));
	}

	@Override
	public Element getGitHubMessageElement() {
		return getGitHubMessageElement(false);
	}

	public Element getGitHubMessageElement(boolean showCommonFailuresCount) {
		if (_gitHubMessageElement != null) {
			return _gitHubMessageElement;
		}

		if (!Objects.equals(getStatus(), "completed") &&
			(getParentBuild() != null)) {

			return null;
		}

		String result = getResult();

		if (result.equals("SUCCESS")) {
			return null;
		}

		Element messageElement = Dom4JUtil.getNewElement("div");

		Dom4JUtil.addToElement(
			messageElement,
			Dom4JUtil.getNewElement(
				"h5", null,
				Dom4JUtil.getNewAnchorElement(
					getBuildURL(), getDisplayName())));

		if (showCommonFailuresCount) {
			Dom4JUtil.addToElement(
				messageElement,
				getGitHubMessageJobResultsElement(showCommonFailuresCount));
		}
		else {
			Dom4JUtil.addToElement(
				messageElement, getGitHubMessageJobResultsElement());
		}

		if (result.equals("ABORTED") && !hasDownstreamBuilds()) {
			messageElement.add(
				Dom4JUtil.toCodeSnippetElement("Build was aborted"));

			return messageElement;
		}

		Element failureMessageElement = getFailureMessageElement();

		if (failureMessageElement != null) {
			messageElement.add(failureMessageElement);
		}

		_gitHubMessageElement = messageElement;

		return _gitHubMessageElement;
	}

	@Override
	public Map<String, String> getInjectedEnvironmentVariablesMap()
		throws IOException {

		Map<String, String> injectedEnvironmentVariablesMap = new HashMap<>();

		String localBuildURL = JenkinsResultsParserUtil.getLocalURL(
			getBuildURL());

		JSONObject jsonObject = JenkinsResultsParserUtil.toJSONObject(
			localBuildURL + "/injectedEnvVars/api/json", false);

		JSONObject envMapJSONObject = jsonObject.getJSONObject("envMap");

		Set<String> envMapJSONObjectKeySet = envMapJSONObject.keySet();

		for (String key : envMapJSONObjectKeySet) {
			injectedEnvironmentVariablesMap.put(
				key, envMapJSONObject.getString(key));
		}

		return injectedEnvironmentVariablesMap;
	}

	@Override
	public String getInvocationURL() {
		String jobURL = getJobURL();

		if (jobURL == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer(jobURL);

		sb.append("/buildWithParameters?");

		Map<String, String> parameters = new HashMap<>(getParameters());

		try {
			parameters.put(
				"token",
				JenkinsResultsParserUtil.getBuildProperty(
					"jenkins.authentication.token"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get Jenkins authentication token", ioException);
		}

		for (Map.Entry<String, String> parameter : parameters.entrySet()) {
			sb.append(parameter.getKey());
			sb.append("=");
			sb.append(parameter.getValue());
			sb.append("&");
		}

		sb.deleteCharAt(sb.length() - 1);

		return JenkinsResultsParserUtil.fixURL(sb.toString());
	}

	@Override
	public int getInvokedBatchSize() {
		if (_invokedBatchSize > 0) {
			return _invokedBatchSize;
		}

		String invokedJobBatchSize = getParameterValue(
			"INVOKED_JOB_BATCH_SIZE");

		if (JenkinsResultsParserUtil.isInteger(invokedJobBatchSize)) {
			_invokedBatchSize = Integer.parseInt(invokedJobBatchSize);

			return _invokedBatchSize;
		}

		String testBatchSize = getParameterValue("TEST_BATCH_SIZE");

		if (JenkinsResultsParserUtil.isInteger(testBatchSize)) {
			_invokedBatchSize = Integer.parseInt(testBatchSize);
		}
		else {
			_invokedBatchSize = _INVOKED_BATCH_SIZE_DEFAULT;
		}

		return _invokedBatchSize;
	}

	@Override
	public Long getInvokedTime() {
		if (invokedTime != null) {
			return invokedTime;
		}

		invokedTime = getStartTime();

		return invokedTime;
	}

	@Override
	public JenkinsCohort getJenkinsCohort() {
		if (_jenkinsCohort != null) {
			return _jenkinsCohort;
		}

		TopLevelBuild topLevelBuild = getTopLevelBuild();

		if (topLevelBuild != null) {
			_jenkinsCohort = topLevelBuild.getJenkinsCohort();

			return _jenkinsCohort;
		}

		String cohortName = JenkinsResultsParserUtil.getCohortName();

		if (!JenkinsResultsParserUtil.isNullOrEmpty(cohortName)) {
			_jenkinsCohort = JenkinsCohort.getInstance(cohortName);

			return _jenkinsCohort;
		}

		return null;
	}

	@Override
	public JenkinsMaster getJenkinsMaster() {
		if (_jenkinsMaster != null) {
			return _jenkinsMaster;
		}

		Invocation currentInvocation = getCurrentInvocation();

		if (currentInvocation != null) {
			_jenkinsMaster = currentInvocation.getJenkinsMaster();

			return _jenkinsMaster;
		}

		String buildURL = getBuildURL();

		if (!JenkinsResultsParserUtil.isURL(buildURL)) {
			return null;
		}

		MultiPattern buildURLMultiPattern = getBuildURLMultiPattern();

		Matcher matcher = buildURLMultiPattern.find(buildURL);

		if (matcher == null) {
			return null;
		}

		_jenkinsMaster = JenkinsMaster.getInstance(matcher.group("master"));

		return _jenkinsMaster;
	}

	@Override
	public JenkinsSlave getJenkinsSlave() {
		if (_jenkinsSlave != null) {
			return _jenkinsSlave;
		}

		String buildURL = getBuildURL();
		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		if ((buildURL == null) || (jenkinsMaster == null)) {
			return null;
		}

		JSONObject builtOnJSONObject = getBuildJSONObject("builtOn");

		if (builtOnJSONObject == null) {
			return null;
		}

		String slaveName = builtOnJSONObject.optString("builtOn");

		if (slaveName.equals("")) {
			slaveName = "master";
		}

		_jenkinsSlave = jenkinsMaster.getJenkinsSlave(slaveName);

		return _jenkinsSlave;
	}

	@Override
	public Job getJob() {
		if (_job != null) {
			return _job;
		}

		_job = JobFactory.newJob(this);

		return _job;
	}

	@Override
	public String getJobName() {
		return _jobName;
	}

	@Override
	public String getJobURL() {
		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		if ((jenkinsMaster == null) || (_jobName == null)) {
			return null;
		}

		if (fromArchive) {
			return JenkinsResultsParserUtil.combine(
				Build.DEPENDENCIES_URL_TOKEN, "/", getArchiveName(), "/",
				jenkinsMaster.getName(), "/", _jobName);
		}

		String jobURL = JenkinsResultsParserUtil.combine(
			"https://", jenkinsMaster.getName(), ".liferay.com/job/", _jobName);

		try {
			return JenkinsResultsParserUtil.encode(jobURL);
		}
		catch (MalformedURLException | URISyntaxException exception) {
			throw new RuntimeException(
				"Unable to encode job URL " + jobURL, exception);
		}
	}

	@Override
	public String getJobVariant() {
		String jobVariant = getParameterValue("JOB_VARIANT");

		if ((jobVariant == null) || jobVariant.isEmpty()) {
			jobVariant = getParameterValue("JENKINS_JOB_VARIANT");
		}

		return jobVariant;
	}

	@Override
	public TestResult getLongestRunningTest() {
		TestResult longestRunningTest = null;

		List<TestResult> testResults = getTestResults(null);

		long longestTestDuration = 0;

		for (TestResult testResult : testResults) {
			long testDuration = testResult.getDuration();

			if (testDuration > longestTestDuration) {
				longestTestDuration = testDuration;

				longestRunningTest = testResult;
			}
		}

		return longestRunningTest;
	}

	@Override
	public int getMaximumSlavesPerHost() {
		if (_maximumSlavesPerHost > 0) {
			return _maximumSlavesPerHost;
		}

		String maximumSlavesPerHost = getParameterValue(
			"MAXIMUM_SLAVES_PER_HOST");

		if (JenkinsResultsParserUtil.isInteger(maximumSlavesPerHost)) {
			_maximumSlavesPerHost = Integer.parseInt(maximumSlavesPerHost);
		}
		else {
			_maximumSlavesPerHost = _MAXIMUM_SLAVES_PER_HOST;
		}

		return _maximumSlavesPerHost;
	}

	@Override
	public Map<String, String> getMetricLabels() {
		if (_parentBuild != null) {
			return _parentBuild.getMetricLabels();
		}

		return new TreeMap<>();
	}

	@Override
	public int getMinimumSlaveRAM() {
		if (_minimumSlaveRAM > 0) {
			return _minimumSlaveRAM;
		}

		String minimumSlaveRAM = getParameterValue("MINIMUM_SLAVE_RAM");

		if (JenkinsResultsParserUtil.isInteger(minimumSlaveRAM)) {
			_minimumSlaveRAM = Integer.parseInt(minimumSlaveRAM);
		}
		else {
			_minimumSlaveRAM = _MINIMUM_SLAVE_RAM_DEFAULT;
		}

		return _minimumSlaveRAM;
	}

	@Override
	public Map<String, String> getParameters() {
		return new HashMap<>(_parameters);
	}

	@Override
	public String getParameterValue(String name) {
		return _parameters.get(name);
	}

	@Override
	public Build getParentBuild() {
		return _parentBuild;
	}

	@Override
	public Invocation getPreviousInvocation() {
		if (_invocations.size() <= 1) {
			return null;
		}

		return _invocations.get(_invocations.size() - 2);
	}

	public long getQueuingDuration() {
		JSONObject buildJSONObject = getBuildJSONObject(
			"actions[queuingDurationMillis]");

		if (buildJSONObject == null) {
			return 0;
		}

		JSONArray actionsJSONArray = buildJSONObject.getJSONArray("actions");

		for (int i = 0; i < actionsJSONArray.length(); i++) {
			Object actions = actionsJSONArray.get(i);

			if (actions == JSONObject.NULL) {
				continue;
			}

			JSONObject actionJSONObject = actionsJSONArray.getJSONObject(i);

			if (actionJSONObject.has("queuingDurationMillis")) {
				return actionJSONObject.getLong("queuingDurationMillis");
			}
		}

		return 0;
	}

	@Override
	public String getResult() {
		if (!JenkinsResultsParserUtil.isNullOrEmpty(_result)) {
			return _result;
		}

		String status = getStatus();

		if (!Objects.equals(status, "reported") &&
			!Objects.equals(status, "completed")) {

			return null;
		}

		JSONObject buildJSONObject = getBuildJSONObject("result");

		if (buildJSONObject == null) {
			return "MISSING";
		}

		String result = buildJSONObject.optString("result");

		if (JenkinsResultsParserUtil.isNullOrEmpty(result)) {
			return "MISSING";
		}

		_result = result;

		return _result;
	}

	@Override
	public Map<String, String> getStartPropertiesTempMap() {
		return getTempMap("start.properties");
	}

	@Override
	public Long getStartTime() {
		if (startTime != null) {
			return startTime;
		}

		JSONObject buildJSONObject = getBuildJSONObject("timestamp");

		if (buildJSONObject == null) {
			return null;
		}

		long timestamp = buildJSONObject.getLong("timestamp");

		if (timestamp != 0) {
			startTime = timestamp;
		}

		return startTime;
	}

	@Override
	public String getStatus() {
		return _status;
	}

	@Override
	public long getStatusAge() {
		return JenkinsResultsParserUtil.getCurrentTimeMillis() -
			_statusModifiedTime;
	}

	@Override
	public long getStatusDuration(String status) {
		if (_statusDurations.containsKey(status)) {
			return _statusDurations.get(status);
		}

		return 0;
	}

	@Override
	public Map<String, String> getStopPropertiesTempMap() {
		return getTempMap("stop.properties");
	}

	@Override
	public StopWatchRecordsGroup getStopWatchRecordsGroup() {
		if (!(this instanceof TopLevelBuild)) {
			if (!Objects.equals(getStatus(), "completed")) {
				_stopWatchRecordsGroup = null;

				return new StopWatchRecordsGroup();
			}

			if (_stopWatchRecordsGroup != null) {
				return _stopWatchRecordsGroup;
			}
		}

		String consoleText = getConsoleText();

		if (JenkinsResultsParserUtil.isNullOrEmpty(consoleText)) {
			System.out.println(
				"Console text for " + getBuildName() + " is null or empty");

			return new StopWatchRecordsGroup();
		}

		_stopWatchRecordsGroup = new StopWatchRecordsGroup();

		for (String line : consoleText.split("\n")) {
			Matcher matcher = stopWatchStartTimestampPattern.matcher(line);

			if (matcher.matches()) {
				Date timestamp = null;

				try {
					timestamp = stopWatchTimestampSimpleDateFormat.parse(
						matcher.group("timestamp"));
				}
				catch (NumberFormatException numberFormatException) {
					System.out.println(
						"Unable to parse " + matcher.group("timestamp"));

					continue;
				}
				catch (ParseException parseException) {
					throw new RuntimeException(
						"Unable to parse timestamp in " + line, parseException);
				}

				String stopWatchName = matcher.group("name");

				_stopWatchRecordsGroup.add(
					new StopWatchRecord(stopWatchName, timestamp.getTime()));

				continue;
			}

			matcher = stopWatchPattern.matcher(line);

			if (matcher.matches()) {
				long duration = Long.parseLong(matcher.group("milliseconds"));

				String seconds = matcher.group("seconds");

				if (seconds != null) {
					duration += Long.parseLong(seconds) * 1000L;
				}

				String minutes = matcher.group("minutes");

				if (minutes != null) {
					duration += Long.parseLong(minutes) * 60L * 1000L;
				}

				String stopWatchName = matcher.group("name");

				StopWatchRecord stopWatchRecord = _stopWatchRecordsGroup.get(
					stopWatchName);

				if (stopWatchRecord != null) {
					stopWatchRecord.setDuration(duration);
				}
			}
		}

		return _stopWatchRecordsGroup;
	}

	@Override
	public TestClassResult getTestClassResult(String testClassName) {
		if (!isCompleted()) {
			return null;
		}

		_initTestClassResults();

		if (_testClassResults == null) {
			return null;
		}

		return _testClassResults.get(testClassName);
	}

	@Override
	public List<TestClassResult> getTestClassResults() {
		if (!isCompleted()) {
			return new ArrayList<>();
		}

		_initTestClassResults();

		if (_testClassResults == null) {
			return new ArrayList<>();
		}

		return new ArrayList<>(_testClassResults.values());
	}

	@Override
	public synchronized List<URL> getTestrayAttachmentURLs() {
		if (!Objects.equals(getStatus(), "completed") ||
			_testrayAttachmentURLsFound) {

			return _testrayAttachmentURLs;
		}

		String consoleText = getConsoleText();

		for (String line : consoleText.split("\\n")) {
			Matcher matcher = _testrayCloudObjectURLPattern.matcher(line);

			if (!matcher.find()) {
				continue;
			}

			try {
				addTestrayAttachmentURL(new URL(matcher.group("url")));
			}
			catch (MalformedURLException malformedURLException) {
				throw new RuntimeException(malformedURLException);
			}
		}

		_testrayAttachmentURLsFound = true;

		return _testrayAttachmentURLs;
	}

	@Override
	public String getTestrayBuildDateString() {
		return JenkinsResultsParserUtil.toDateString(
			new Date(getStartTime()), "yyyy-MM-dd HH:mm:ss",
			"America/Los_Angeles");
	}

	@Override
	public JSONObject getTestReportJSONObject(boolean checkCache) {
		if (_testReportJSONObject != null) {
			return _testReportJSONObject;
		}

		String result = getResult();

		if (result == null) {
			return null;
		}

		String urlSuffix = "testReport/api/json";

		String archiveFileContent = getArchiveFileContent(urlSuffix);

		if (!JenkinsResultsParserUtil.isNullOrEmpty(archiveFileContent)) {
			_testReportJSONObject = new JSONObject(archiveFileContent);

			return _testReportJSONObject;
		}

		try {
			_testReportJSONObject = JenkinsResultsParserUtil.toJSONObject(
				JenkinsResultsParserUtil.getLocalURL(getBuildURL() + urlSuffix),
				checkCache, 5000);

			return _testReportJSONObject;
		}
		catch (Exception exception) {
			_testReportJSONObject = new JSONObject();
		}

		return _testReportJSONObject;
	}

	@Override
	public List<TestResult> getTestResults() {
		if (!isCompleted()) {
			return new ArrayList<>();
		}

		List<TestResult> testResults = new ArrayList<>();

		for (TestClassResult testClassResult : getTestClassResults()) {
			testResults.addAll(testClassResult.getTestResults());
		}

		return testResults;
	}

	@Override
	public List<TestResult> getTestResults(String testStatus) {
		return Collections.emptyList();
	}

	@Override
	public String getTestSuiteName() {
		Build parentBuild = getParentBuild();

		if (parentBuild == null) {
			return "default";
		}

		return parentBuild.getTestSuiteName();
	}

	@Override
	public TopLevelBuild getTopLevelBuild() {
		Build topLevelBuild = this;

		Build parentBuild = topLevelBuild.getParentBuild();

		if (parentBuild instanceof JenkinsTopLevelBuild) {
			return (TopLevelBuild)parentBuild;
		}

		while ((topLevelBuild != null) &&
			   !(topLevelBuild instanceof TopLevelBuild)) {

			topLevelBuild = topLevelBuild.getParentBuild();
		}

		return (TopLevelBuild)topLevelBuild;
	}

	@Override
	public List<TestResult> getUniqueFailureTestResults() {
		return Collections.emptyList();
	}

	@Override
	public List<TestResult> getUpstreamJobFailureTestResults() {
		return Collections.emptyList();
	}

	@Override
	public boolean hasBuildURL(String buildURL) {
		try {
			buildURL = JenkinsResultsParserUtil.decode(buildURL);
		}
		catch (UnsupportedEncodingException unsupportedEncodingException) {
			throw new RuntimeException(
				"Unable to decode " + buildURL, unsupportedEncodingException);
		}

		buildURL = JenkinsResultsParserUtil.getLocalURL(buildURL);

		String thisBuildURL = getBuildURL();

		if (thisBuildURL != null) {
			thisBuildURL = JenkinsResultsParserUtil.getLocalURL(thisBuildURL);

			try {
				if (URLCompareUtil.matches(
						new URL(buildURL), new URL(thisBuildURL))) {

					return true;
				}
			}
			catch (MalformedURLException malformedURLException) {
				throw new RuntimeException(
					JenkinsResultsParserUtil.combine(
						"Unable to compare urls ", buildURL, " and ",
						thisBuildURL),
					malformedURLException);
			}
		}

		return false;
	}

	@Override
	public boolean hasDownstreamBuilds() {
		return false;
	}

	@Override
	public boolean hasGenericCIFailure() {
		for (FailureMessageGenerator failureMessageGenerator :
				getFailureMessageGenerators()) {

			Element failureMessage = failureMessageGenerator.getMessageElement(
				this);

			if (failureMessage != null) {
				return failureMessageGenerator.isGenericCIFailure();
			}
		}

		return false;
	}

	@Override
	public int hashCode() {
		String key = getBuildURL();

		if (key != null) {
			return key.hashCode();
		}

		return super.hashCode();
	}

	@Override
	public boolean hasMaximumInvocationCount() {
		Map<ReinvokeRule, Integer> invocationCounts = new HashMap<>();

		for (Invocation invocation : _invocations) {
			ReinvokeRule reinvokeRule = invocation.getReinvokeRule();

			if (reinvokeRule == null) {
				continue;
			}

			Integer invocationCount = invocationCounts.getOrDefault(
				reinvokeRule, 0);

			invocationCount++;

			invocationCounts.put(reinvokeRule, invocationCount);
		}

		for (Map.Entry<ReinvokeRule, Integer> entry :
				invocationCounts.entrySet()) {

			ReinvokeRule reinvokeRule = entry.getKey();

			Integer invocationCount = entry.getValue();

			if (invocationCount > reinvokeRule.getMaximumInvocationCount()) {
				return true;
			}

			return false;
		}

		if (_invocations.size() >= _getMaximumInvocationCount()) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isBuildCachingEnabled() {
		Job job = getJob();

		if (job == null) {
			return false;
		}

		return job.isBuildCachingEnabled();
	}

	@Override
	public boolean isBuildModified() {
		return _isDifferent(_status, _previousStatus);
	}

	@Override
	public boolean isCompareToUpstream() {
		TopLevelBuild topLevelBuild = getTopLevelBuild();

		return topLevelBuild.isCompareToUpstream();
	}

	@Override
	public boolean isCompleted() {
		String status = getStatus();

		if (!Objects.equals(status, "completed") &&
			!Objects.equals(status, "reporting")) {

			return false;
		}

		return true;
	}

	@Override
	public boolean isFailing() {
		return !Objects.equals(getResult(), "SUCCESS");
	}

	@Override
	public boolean isFromArchive() {
		return fromArchive;
	}

	@Override
	public boolean isFromCompletedBuild() {
		Build parentBuild = getParentBuild();

		if (parentBuild != null) {
			return parentBuild.isFromCompletedBuild();
		}

		return fromCompletedBuild;
	}

	@Override
	public boolean isUniqueFailure() {
		return false;
	}

	@Override
	public String replaceBuildURL(String text) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(text)) {
			return text;
		}

		text = text.replaceAll(
			getBuildURLRegex(),
			Matcher.quoteReplacement(
				JenkinsResultsParserUtil.combine(
					Build.DEPENDENCIES_URL_TOKEN, "/", getArchivePath())));

		Build parentBuild = getParentBuild();

		while (parentBuild != null) {
			text = text.replaceAll(
				parentBuild.getBuildURLRegex(),
				Matcher.quoteReplacement(
					Build.DEPENDENCIES_URL_TOKEN +
						parentBuild.getArchivePath()));

			parentBuild = parentBuild.getParentBuild();
		}

		return text;
	}

	@Override
	public void reset() {
		consoleReadCursor = 0;
		_duration = null;

		if (_jenkinsConsoleTextLoader != null) {
			_jenkinsConsoleTextLoader.deleteCacheFile();
		}

		_jenkinsConsoleTextLoader = null;

		_buildJSONObject = null;
		_jenkinsMaster = null;
		_jenkinsSlave = null;
		_result = null;
		_statusModifiedTime = JenkinsResultsParserUtil.getCurrentTimeMillis();
		_testReportJSONObject = null;

		if (_buildUpdater != null) {
			_buildUpdater.reset();
		}
	}

	@Override
	public void saveBuildURLInBuildDatabase() {
		BuildDatabase buildDatabase = getBuildDatabase();

		buildDatabase.putProperty(
			BUILD_URLS_PROPERTIES_KEY, getJobVariant(), getBuildURL(), false);
	}

	@Override
	public void setArchiveName(String archiveName) {
		_archiveName = archiveName;
	}

	@Override
	public void setArchiveRootDir(File archiveRootDir) {
		if (archiveRootDir == null) {
			archiveRootDir = new File(
				JenkinsResultsParserUtil.urlDependenciesFile.substring(
					"file:".length()));
		}

		if (!archiveRootDir.exists()) {
			throw new IllegalArgumentException(
				archiveRootDir.getPath() + " does not exist");
		}

		_archiveRootDir = archiveRootDir;
	}

	@Override
	public void setBuildURL(String buildURL) {
		_buildURL = buildURL;
	}

	@Override
	public void setCompareToUpstream(boolean compareToUpstream) {
	}

	@Override
	public void setJenkinsCohort(JenkinsCohort jenkinsCohort) {
		_jenkinsCohort = jenkinsCohort;
	}

	@Override
	public void setJenkinsMaster(JenkinsMaster jenkinsMaster) {
		_jenkinsMaster = jenkinsMaster;
	}

	@Override
	public void setResult(String result) {
		_result = result;
	}

	@Override
	public void setStatus(String status) {
		boolean different = _isDifferent(status, _status);

		_previousStatus = _status;

		_status = status;

		long previousStatusModifiedTime = _statusModifiedTime;

		_statusModifiedTime = JenkinsResultsParserUtil.getCurrentTimeMillis();

		_statusDurations.put(
			_previousStatus, _statusModifiedTime - previousStatusModifiedTime);

		if (different && isParentBuildRoot()) {
			System.out.println(getBuildMessage());
		}
	}

	@Override
	public void takeSlaveOffline(SlaveOfflineRule slaveOfflineRule) {
		if ((slaveOfflineRule == null) || fromArchive) {
			return;
		}

		slaveOfflineRule.takeSlaveOffline(this);
	}

	@Override
	public synchronized void update() {
		if (skipUpdate()) {
			System.out.println("Skipping build status: " + getStatus());

			return;
		}

		_buildUpdater.update();
	}

	public static class BuildDisplayNameComparator
		implements Comparator<Build> {

		@Override
		public int compare(Build build1, Build build2) {
			String axisName1 = _getAxisName(build1);
			String axisName2 = _getAxisName(build2);

			if (JenkinsResultsParserUtil.isNullOrEmpty(axisName1) ||
				JenkinsResultsParserUtil.isNullOrEmpty(axisName2)) {

				String displayName1 = build1.getDisplayName();
				String displayName2 = build2.getDisplayName();

				return displayName1.compareTo(displayName2);
			}

			Matcher matcher1 = _pattern.matcher(axisName1);
			Matcher matcher2 = _pattern.matcher(axisName2);

			if (!matcher1.find() || !matcher2.find()) {
				String displayName1 = build1.getDisplayName();
				String displayName2 = build2.getDisplayName();

				return displayName1.compareTo(displayName2);
			}

			String batchName1 = matcher1.group("batchName");
			String batchName2 = matcher2.group("batchName");

			if (!batchName1.equals(batchName2)) {
				return batchName1.compareTo(batchName2);
			}

			Integer segment1 = Integer.valueOf(matcher1.group("segment"));
			Integer segment2 = Integer.valueOf(matcher2.group("segment"));

			if (!segment1.equals(segment2)) {
				return segment1.compareTo(segment2);
			}

			String axisString1 = matcher1.group("axis");
			String axisString2 = matcher2.group("axis");

			if (JenkinsResultsParserUtil.isNullOrEmpty(axisString1) ||
				JenkinsResultsParserUtil.isNullOrEmpty(axisString2)) {

				String displayName1 = build1.getDisplayName();
				String displayName2 = build2.getDisplayName();

				return displayName1.compareTo(displayName2);
			}

			Integer axis1 = Integer.valueOf(axisString1);
			Integer axis2 = Integer.valueOf(axisString2);

			return axis1.compareTo(axis2);
		}

		private String _getAxisName(Build build) {
			if (build instanceof AxisBuild) {
				AxisBuild axisBuild = (AxisBuild)build;

				return axisBuild.getAxisNumber();
			}

			if (build instanceof DownstreamBuild) {
				DownstreamBuild downstreamBuild = (DownstreamBuild)build;

				return downstreamBuild.getAxisName();
			}

			return build.getJobVariant();
		}

		private static final Pattern _pattern = Pattern.compile(
			"(?<batchName>[^/]+)/(?<segment>\\d+)(/(?<axis>\\d+))?");

	}

	public static class DefaultBranchInformation implements BranchInformation {

		@Override
		public String getCachedRemoteGitRefName() {
			return JenkinsResultsParserUtil.combine(
				"cache-", getReceiverUsername(), "-", getUpstreamBranchSHA(),
				"-", getOriginName(), "-", getSenderBranchSHA());
		}

		@Override
		public String getOriginName() {
			String branchInformationString = _getBranchInformationString();

			String regex = "[\\S\\s]*github.origin.name=(.+)\\n[\\S\\s]*";

			if (branchInformationString.matches(regex)) {
				return branchInformationString.replaceAll(regex, "$1");
			}

			return null;
		}

		@Override
		public Integer getPullRequestNumber() {
			String branchInformationString = _getBranchInformationString();

			String regex =
				"[\\S\\s]*github.pull.request.number=(\\d+)\\n[\\S\\s]*";

			if (branchInformationString.matches(regex)) {
				return Integer.valueOf(
					branchInformationString.replaceAll(regex, "$1"));
			}

			return 0;
		}

		@Override
		public String getReceiverUsername() {
			String branchInformationString = _getBranchInformationString();

			String regex = "[\\S\\s]*github.receiver.username=(.+)\\n[\\S\\s]*";

			if (branchInformationString.matches(regex)) {
				return branchInformationString.replaceAll(regex, "$1");
			}

			return null;
		}

		@Override
		public String getRepositoryName() {
			Properties buildProperties;

			try {
				buildProperties = JenkinsResultsParserUtil.getBuildProperties();
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}

			String repositoryType = _repositoryType;

			if (repositoryType.equals("portal.base") ||
				repositoryType.equals("portal.ee")) {

				repositoryType = "portal";
			}

			return JenkinsResultsParserUtil.getProperty(
				buildProperties, repositoryType + ".repository",
				getUpstreamBranchName());
		}

		@Override
		public String getSenderBranchName() {
			String branchInformationString = _getBranchInformationString();

			String regex =
				"[\\S\\s]*github.sender.branch.name=(.+)\\n[\\S\\s]*";

			if (branchInformationString.matches(regex)) {
				return branchInformationString.replaceAll(regex, "$1");
			}

			return null;
		}

		@Override
		public String getSenderBranchSHA() {
			String branchInformationString = _getBranchInformationString();

			String regex = "[\\S\\s]*github.sender.branch.sha=(.+)\\n[\\S\\s]*";

			if (branchInformationString.matches(regex)) {
				return branchInformationString.replaceAll(regex, "$1");
			}

			return null;
		}

		@Override
		public String getSenderBranchSHAShort() {
			String senderBranchSHA = getSenderBranchSHA();

			if (senderBranchSHA == null) {
				return null;
			}

			if (senderBranchSHA.length() >= 7) {
				senderBranchSHA = senderBranchSHA.substring(0, 7);
			}

			return senderBranchSHA;
		}

		@Override
		public RemoteGitRef getSenderRemoteGitRef() {
			String remoteURL = JenkinsResultsParserUtil.combine(
				"git@github.com:", getSenderUsername(), "/",
				getRepositoryName(), ".git");

			return GitUtil.getRemoteGitRef(
				getSenderBranchName(), new File("."), remoteURL);
		}

		@Override
		public String getSenderUsername() {
			String branchInformationString = _getBranchInformationString();

			String regex = "[\\S\\s]*github.sender.username=(.+)\\n[\\S\\s]*";

			if (branchInformationString.matches(regex)) {
				return branchInformationString.replaceAll(regex, "$1");
			}

			return null;
		}

		@Override
		public String getUpstreamBranchName() {
			String branchInformationString = _getBranchInformationString();

			String regex =
				"[\\S\\s]*github.upstream.branch.name=(.+)\\n[\\S\\s]*";

			if (branchInformationString.matches(regex)) {
				return branchInformationString.replaceAll(regex, "$1");
			}

			return null;
		}

		@Override
		public String getUpstreamBranchSHA() {
			String branchInformationString = _getBranchInformationString();

			String regex =
				"[\\S\\s]*github.upstream.branch.sha=(.+)\\n[\\S\\s]*";

			if (branchInformationString.matches(regex)) {
				return branchInformationString.replaceAll(regex, "$1");
			}

			return null;
		}

		protected DefaultBranchInformation(Build build, String repositoryType) {
			_build = build;
			_repositoryType = repositoryType;
		}

		private String _getBranchInformationString() {
			if (_branchInformationString != null) {
				return _branchInformationString;
			}

			String consoleText = _build.getConsoleText();

			int x = -1;

			Pattern pattern = Pattern.compile(
				JenkinsResultsParserUtil.combine(
					"## (http://cloud-.*/)?git.", _repositoryType,
					".properties"));

			Matcher matcher = pattern.matcher(consoleText);

			if (matcher.find()) {
				x = matcher.start();
			}

			if (x == -1) {
				return "";
			}

			int y = consoleText.indexOf("prepare.repositories.", x);

			if (y == -1) {
				y = consoleText.indexOf("Deleting:", x);
			}

			y = consoleText.indexOf("\n", y);

			if (y == -1) {
				return "";
			}

			_branchInformationString = consoleText.substring(x, y);

			return _branchInformationString;
		}

		private String _branchInformationString;
		private final Build _build;
		private final String _repositoryType;

	}

	protected static boolean isHighPriorityBuildFailureElement(
		Element gitHubMessage) {

		String content = null;

		try {
			content = Dom4JUtil.format(gitHubMessage, false);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to format github message", ioException);
		}

		for (String highPriorityContentToken : _TOKENS_HIGH_PRIORITY_CONTENT) {
			if (content.contains(highPriorityContentToken)) {
				return true;
			}
		}

		return false;
	}

	protected BaseBuild(String url) {
		this(url, null);
	}

	protected BaseBuild(String url, Build parentBuild) {
		_parentBuild = parentBuild;

		if (url.contains("buildWithParameters")) {
			_setInvocationURL(url);
		}
		else {
			_setBuildURL(url);
		}

		if (!fromArchive && JenkinsResultsParserUtil.isCINode()) {
			TopLevelBuild topLevelBuild = getTopLevelBuild();

			if (topLevelBuild != null) {
				_archiveRootDir = new File(topLevelBuild.getBuildDirPath());
			}
			else {
				_archiveRootDir = new File(getBuildDirPath());
			}
		}

		_buildUpdater = BuildUpdaterFactory.newBuildUpdater(this);

		if (fromArchive || isFromCompletedBuild()) {
			update();
		}
	}

	protected void archiveFileElements(
		String urlSuffix, List<Element> elements) {

		Element rootElement = Dom4JUtil.getNewElement("root");

		for (Element element : elements) {
			rootElement.add(element);
		}

		try {
			_archive(Dom4JUtil.format(rootElement), true, urlSuffix);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	protected boolean archiveFileExists(String urlSuffix) {
		File archiveFile = getArchiveFile(urlSuffix);

		if (archiveFile == null) {
			return false;
		}

		return archiveFile.exists();
	}

	protected boolean buildDurationsEnabled() {
		if (_buildDurationsEnabled != null) {
			return _buildDurationsEnabled;
		}

		String buildDurationsEnabled = null;

		try {
			TopLevelBuild topLevelBuild = getTopLevelBuild();

			String topLevelBranchName = null;
			String topLevelJobName = null;
			String topLevelTestSuiteName = null;

			if (topLevelBuild != null) {
				topLevelBranchName = topLevelBuild.getBranchName();
				topLevelJobName = topLevelBuild.getJobName();
				topLevelTestSuiteName = topLevelBuild.getTestSuiteName();
			}

			buildDurationsEnabled = JenkinsResultsParserUtil.getProperty(
				JenkinsResultsParserUtil.getBuildProperties(),
				"build.durations.enabled", topLevelBranchName, topLevelJobName,
				topLevelTestSuiteName);

			if (Objects.equals(buildDurationsEnabled, "true")) {
				_buildDurationsEnabled = true;

				return _buildDurationsEnabled;
			}
		}
		catch (IOException ioException) {
		}

		_buildDurationsEnabled = false;

		return _buildDurationsEnabled;
	}

	protected Pattern getArchiveBuildURLPattern() {
		return Pattern.compile(
			JenkinsResultsParserUtil.combine(
				"(", Pattern.quote(Build.DEPENDENCIES_URL_TOKEN), "|",
				Pattern.quote(JenkinsResultsParserUtil.urlDependenciesFile),
				"|",
				Pattern.quote(JenkinsResultsParserUtil.urlDependenciesHttp),
				")/*(?<archiveName>.*)/(?<master>[^/]+)/+(?<jobName>[^/]+)",
				".*/(?<buildNumber>\\d+)/?"));
	}

	protected List<Callable<Object>> getArchiveCallables() {
		List<Callable<Object>> archiveCallables = new ArrayList<>();

		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		archiveCallables.add(
			new ParallelExecutor.SequentialCallable<Object>(
				jenkinsMaster.getName()) {

				@Override
				public Object call() {
					_archiveBuildJSON();

					return null;
				}

			});
		archiveCallables.add(
			new ParallelExecutor.SequentialCallable<Object>(
				jenkinsMaster.getName()) {

				@Override
				public Object call() {
					_archiveConsoleLog();

					return null;
				}

			});
		archiveCallables.add(
			new ParallelExecutor.SequentialCallable<Object>(
				jenkinsMaster.getName()) {

				@Override
				public Object call() {
					_archiveMarkerFile();

					return null;
				}

			});
		archiveCallables.add(
			new ParallelExecutor.SequentialCallable<Object>(
				jenkinsMaster.getName()) {

				@Override
				public Object call() {
					_archiveTestReportJSON();

					return null;
				}

			});

		return archiveCallables;
	}

	protected File getArchiveFile(String urlSuffix) {
		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		if (jenkinsMaster == null) {
			return null;
		}

		return new File(
			getArchiveRootDir(), getArchivePath() + "/" + urlSuffix);
	}

	protected String getArchiveFileContent(String urlSuffix) {
		if (!Objects.equals(getStatus(), "completed")) {
			return null;
		}

		File archiveFile = getArchiveFile(urlSuffix);

		if ((archiveFile == null) || !archiveFile.exists()) {
			return null;
		}

		try {
			return JenkinsResultsParserUtil.read(archiveFile);
		}
		catch (IOException ioException) {
			return null;
		}
	}

	protected List<Element> getArchiveFileElements(String urlSuffix) {
		String archiveFileContent = getArchiveFileContent(urlSuffix);

		if (JenkinsResultsParserUtil.isNullOrEmpty(archiveFileContent)) {
			return new ArrayList<>();
		}

		try {
			Document document = Dom4JUtil.parse(archiveFileContent);

			Element rootElement = document.getRootElement();

			List<Element> elements = new ArrayList<>();

			for (Element element : rootElement.elements()) {
				element.detach();

				elements.add(element);
			}

			return elements;
		}
		catch (DocumentException documentException) {
			throw new RuntimeException(documentException);
		}
	}

	protected String getBaseGitRepositoryType() {
		if (_jobName.startsWith("test-subrepository-acceptance-pullrequest")) {
			return getBaseGitRepositoryName();
		}

		if (_jobName.contains("portal")) {
			return "portal";
		}

		if (_jobName.contains("plugins")) {
			return "plugins";
		}

		return "jenkins";
	}

	protected BranchInformation getBranchInformation(String repositoryType) {
		BranchInformation branchInformation = _branchInformationMap.get(
			repositoryType);

		if (branchInformation == null) {
			branchInformation = new DefaultBranchInformation(
				this, repositoryType);

			String repositoryName = branchInformation.getRepositoryName();

			if (repositoryName == null) {
				return null;
			}

			_branchInformationMap.put(repositoryType, branchInformation);
		}

		return _branchInformationMap.get(repositoryType);
	}

	protected String getBuildMessage() {
		if (_jobName != null) {
			String status = getStatus();

			StringBuilder sb = new StringBuilder();

			sb.append("Build \"");
			sb.append(getBuildName());
			sb.append("\"");

			if (status.equals("completed")) {
				sb.append(" completed at ");
				sb.append(getBuildURL());
				sb.append(". ");
				sb.append(getResult());

				return sb.toString();
			}

			if (status.equals("missing")) {
				sb.append(" is missing ");
				sb.append(getJobURL());
				sb.append("/.");

				return sb.toString();
			}

			if (status.equals("queued")) {
				sb.append(" is queued at ");
				sb.append(getJobURL());
				sb.append("/.");

				return sb.toString();
			}

			if (status.equals("reporting")) {
				sb.append(" reporting at ");
				sb.append(getBuildURL());
				sb.append(".");

				return sb.toString();
			}

			if (status.equals("running")) {
				Invocation previousInvocation = getPreviousInvocation();

				if (previousInvocation != null) {
					sb.append(" ");

					sb.append(previousInvocation.getBuildURL());

					sb.append(" restarted at ");
				}
				else {
					sb.append(" started at ");
				}

				sb.append(getBuildURL());
				sb.append(".");

				return sb.toString();
			}

			if (status.equals("starting")) {
				String jobURL = getJobURL();

				if (JenkinsResultsParserUtil.isNullOrEmpty(jobURL)) {
					JenkinsCohort jenkinsCohort = getJenkinsCohort();

					jobURL = JenkinsResultsParserUtil.combine(
						"https://", jenkinsCohort.getName(),
						".liferay.com/job/", getJobName());
				}

				sb.append(" invoked at ");
				sb.append(jobURL);
				sb.append("/.");

				return sb.toString();
			}

			throw new RuntimeException("Unknown status: " + status);
		}

		return "";
	}

	protected Element getBuildTimeElement() {
		return Dom4JUtil.getNewElement(
			"p", null, "Build Time: ",
			JenkinsResultsParserUtil.toDurationString(getDuration()));
	}

	protected MultiPattern getBuildURLMultiPattern() {
		return _buildURLMultiPattern;
	}

	protected String getDiffDurationString(long diffDuration) {
		String diffDurationPrefix = "";

		if (diffDuration < 0) {
			diffDurationPrefix = "-";

			diffDuration *= -1;
		}
		else if (diffDuration > 0) {
			diffDurationPrefix = "+";
		}

		return JenkinsResultsParserUtil.combine(
			diffDurationPrefix,
			JenkinsResultsParserUtil.toDurationString(diffDuration));
	}

	protected ExecutorService getExecutorService() {
		return null;
	}

	protected Element getExpanderAnchorElement(
		String expanderName, String namespace) {

		Element expanderAnchorElement = Dom4JUtil.getNewAnchorElement("", "+ ");

		expanderAnchorElement.addAttribute(
			"id",
			JenkinsResultsParserUtil.combine(
				namespace, "-expander-anchor-", expanderName));
		expanderAnchorElement.addAttribute(
			"onClick",
			JenkinsResultsParserUtil.combine(
				"return toggleStopWatchRecordExpander(\'", namespace, "\', \'",
				expanderName, "\')"));
		expanderAnchorElement.addAttribute(
			"style",
			"font-family: monospace, monospace; text-decoration: none");

		return expanderAnchorElement;
	}

	protected Element getFailureMessageElement() {
		for (FailureMessageGenerator failureMessageGenerator :
				getFailureMessageGenerators()) {

			Element failureMessage = failureMessageGenerator.getMessageElement(
				this);

			if (failureMessage != null) {
				return failureMessage;
			}
		}

		return null;
	}

	protected FailureMessageGenerator[] getFailureMessageGenerators() {
		return _FAILURE_MESSAGE_GENERATORS;
	}

	protected Element getFullConsoleClickHereElement() {
		return Dom4JUtil.getNewElement(
			"h5", null, "For full console, click ",
			Dom4JUtil.getNewAnchorElement(
				getBuildURL() + "/consoleText", "here"),
			".");
	}

	protected abstract Element getGitHubMessageJobResultsElement();

	protected Element getGitHubMessageJobResultsElement(
		boolean showCommonFailuresCount) {

		return getGitHubMessageJobResultsElement();
	}

	protected List<Element> getJenkinsReportBuildDurationsElements() {
		return new ArrayList<>();
	}

	protected String getJenkinsReportBuildInfoCellElementTagName() {
		return "td";
	}

	protected List<Element> getJenkinsReportStopWatchRecordElements() {
		String urlSuffix = "stopWatchRecordElements";

		if (archiveFileExists(urlSuffix)) {
			return getArchiveFileElements(urlSuffix);
		}

		List<Element> jenkinsReportStopWatchRecordTableRowElements =
			new ArrayList<>();

		Element stopWatchRecordHeaderRowElement = Dom4JUtil.getNewElement("tr");

		stopWatchRecordHeaderRowElement.addAttribute(
			"id", hashCode() + "-stop-watch-record-header");
		stopWatchRecordHeaderRowElement.addAttribute("style", "display: none");

		Element headerDataElement = Dom4JUtil.getNewElement(
			"td", stopWatchRecordHeaderRowElement,
			getExpanderAnchorElement(
				"stop-watch-record-header", String.valueOf(hashCode())),
			Dom4JUtil.getNewElement("u", null, "Stop Watch Record"));

		headerDataElement.addAttribute(
			"style",
			JenkinsResultsParserUtil.combine(
				"text-indent: ",
				String.valueOf(getDepth() * PIXELS_WIDTH_INDENT), "px"));

		jenkinsReportStopWatchRecordTableRowElements.add(
			stopWatchRecordHeaderRowElement);

		StopWatchRecordsGroup stopWatchRecordsGroup =
			getStopWatchRecordsGroup();

		if (!stopWatchRecordsGroup.isEmpty()) {
			List<String> childStopWatchRecordNames = new ArrayList<>(
				stopWatchRecordsGroup.size());

			for (StopWatchRecord stopWatchRecord : stopWatchRecordsGroup) {
				childStopWatchRecordNames.add(stopWatchRecord.getName());
			}

			stopWatchRecordHeaderRowElement.addAttribute(
				"child-stopwatch-rows",
				JenkinsResultsParserUtil.join(",", childStopWatchRecordNames));
		}

		for (StopWatchRecord stopWatchRecord : getStopWatchRecordsGroup()) {
			jenkinsReportStopWatchRecordTableRowElements.addAll(
				_getStopWatchRecordTableRowElements(stopWatchRecord));
		}

		archiveFileElements(
			urlSuffix, jenkinsReportStopWatchRecordTableRowElements);

		return jenkinsReportStopWatchRecordTableRowElements;
	}

	protected Element getJenkinsReportTableRowElement() {
		String cellElementTagName =
			getJenkinsReportBuildInfoCellElementTagName();

		Element stopWatchRecordsExpanderAnchorElement =
			getStopWatchRecordsExpanderAnchorElement();

		Element nameCellElement = Dom4JUtil.getNewElement(
			cellElementTagName, null, stopWatchRecordsExpanderAnchorElement,
			Dom4JUtil.getNewAnchorElement(
				getBuildURL(), null, getDisplayName()));

		int indent = getDepth() * PIXELS_WIDTH_INDENT;

		if (stopWatchRecordsExpanderAnchorElement != null) {
			indent -= _PIXELS_WIDTH_EXPANDER;
		}

		nameCellElement.addAttribute("style", "text-indent: " + indent);

		Element buildInfoElement = Dom4JUtil.getNewElement(
			"tr", null, nameCellElement,
			Dom4JUtil.getNewElement(
				cellElementTagName, null,
				Dom4JUtil.getNewAnchorElement(
					getBuildURL() + "console", null, "Console")),
			Dom4JUtil.getNewElement(
				cellElementTagName, null,
				Dom4JUtil.getNewAnchorElement(
					getBuildURL() + "testReport", "Test Report")));

		List<String> childStopWatchRows = new ArrayList<>();

		if (buildDurationsEnabled()) {
			childStopWatchRows.add("build-durations-header");
			childStopWatchRows.add("test-durations-header");
		}

		childStopWatchRows.add("stop-watch-record-header");

		buildInfoElement.addAttribute(
			"child-stopwatch-rows",
			JenkinsResultsParserUtil.join(",", childStopWatchRows));

		buildInfoElement.addAttribute("id", String.valueOf(hashCode()) + "-");

		getStartTime();

		if (startTime == null) {
			Dom4JUtil.addToElement(
				buildInfoElement,
				Dom4JUtil.getNewElement(
					cellElementTagName, null, "",
					getJenkinsReportTimeZoneName()));
		}
		else {
			Dom4JUtil.addToElement(
				buildInfoElement,
				Dom4JUtil.getNewElement(
					cellElementTagName, null,
					toJenkinsReportDateString(
						new Date(startTime), getJenkinsReportTimeZoneName())));
		}

		long duration = getDuration();

		Dom4JUtil.addToElement(
			buildInfoElement,
			Dom4JUtil.getNewElement(
				cellElementTagName, null,
				JenkinsResultsParserUtil.toDurationString(duration)));

		Element estimatedDurationElement = null;
		Element diffDurationElement = null;

		if (buildDurationsEnabled()) {
			String estimatedDurationString = "n/a";
			String diffDurationString = "n/a";

			if (this instanceof DownstreamBuild) {
				DownstreamBuild downstreamBuild = (DownstreamBuild)this;

				long averageDuration = downstreamBuild.getAverageDuration();

				estimatedDurationString =
					JenkinsResultsParserUtil.toDurationString(averageDuration);
				diffDurationString = getDiffDurationString(
					duration - averageDuration);
			}

			estimatedDurationElement = Dom4JUtil.getNewElement(
				cellElementTagName, null, estimatedDurationString);
			diffDurationElement = Dom4JUtil.getNewElement(
				cellElementTagName, null, diffDurationString);
		}

		Dom4JUtil.addToElement(buildInfoElement, estimatedDurationElement);

		Dom4JUtil.addToElement(buildInfoElement, diffDurationElement);

		String currentStatus = getStatus();

		if (currentStatus != null) {
			currentStatus = StringUtils.upperCase(currentStatus);
		}
		else {
			currentStatus = "";
		}

		Dom4JUtil.getNewElement(
			cellElementTagName, buildInfoElement, currentStatus);

		String result = getResult();

		if (result == null) {
			result = "";
		}

		Dom4JUtil.getNewElement(cellElementTagName, buildInfoElement, result);

		return buildInfoElement;
	}

	protected List<Element> getJenkinsReportTableRowElements(
		String result, String status) {

		List<Element> tableRowElements = new ArrayList<>();

		if ((getParentBuild() != null) &&
			((result == null) || result.equals(getResult())) &&
			((status == null) || status.equals(getStatus()))) {

			tableRowElements.add(getJenkinsReportTableRowElement());

			if (buildDurationsEnabled()) {
				tableRowElements.addAll(
					getJenkinsReportBuildDurationsElements());
				tableRowElements.addAll(
					getJenkinsReportTestDurationsElements());
			}

			tableRowElements.addAll(getJenkinsReportStopWatchRecordElements());
		}

		return tableRowElements;
	}

	protected List<Element> getJenkinsReportTestDurationsElements() {
		return new ArrayList<>();
	}

	protected String getJenkinsReportTimeZoneName() {
		return _NAME_JENKINS_REPORT_TIME_ZONE;
	}

	protected Map<String, String> getParameters(JSONArray jsonArray) {
		Map<String, String> parameters = new HashMap<>(jsonArray.length());

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);

			parameters.put(
				jsonObject.getString("name"), jsonObject.optString("value"));
		}

		return parameters;
	}

	protected Map<String, String> getParameters(JSONObject buildJSONObject) {
		JSONArray actionsJSONArray = buildJSONObject.getJSONArray("actions");

		if (actionsJSONArray.length() == 0) {
			return new HashMap<>();
		}

		JSONObject parametersActionsJSONObject = null;

		for (int i = 0; i < actionsJSONArray.length(); i++) {
			JSONObject actionsJSONObject = actionsJSONArray.getJSONObject(i);

			if (!Objects.equals(
					actionsJSONObject.optString("_class"),
					"hudson.model.ParametersAction")) {

				continue;
			}

			parametersActionsJSONObject = actionsJSONObject;

			break;
		}

		if ((parametersActionsJSONObject != null) &&
			parametersActionsJSONObject.has("parameters")) {

			JSONArray parametersJSONArray =
				parametersActionsJSONObject.getJSONArray("parameters");

			return getParameters(parametersJSONArray);
		}

		return new HashMap<>();
	}

	protected String getStartPropertiesTempMapURL() {
		if (fromArchive) {
			return getBuildURL() + "/start.properties.json";
		}

		return getParameterValue("JSON_MAP_URL");
	}

	protected String getStopPropertiesTempMapURL() {
		return null;
	}

	protected Element getStopWatchRecordExpanderAnchorElement(
		StopWatchRecord stopWatchRecord, String namespace) {

		Set<StopWatchRecord> childStopWatchRecords =
			stopWatchRecord.getChildStopWatchRecords();

		if (childStopWatchRecords == null) {
			return null;
		}

		return getExpanderAnchorElement(stopWatchRecord.getName(), namespace);
	}

	protected Element getStopWatchRecordsExpanderAnchorElement() {
		StopWatchRecordsGroup stopWatchRecordsGroup =
			getStopWatchRecordsGroup();

		if (stopWatchRecordsGroup.isEmpty()) {
			return null;
		}

		Element stopWatchRecordsExpanderAnchorElement =
			Dom4JUtil.getNewAnchorElement("", "+ ");

		String hashCode = String.valueOf(hashCode());

		stopWatchRecordsExpanderAnchorElement.addAttribute(
			"id",
			JenkinsResultsParserUtil.combine(hashCode, "-expander-anchor-"));

		stopWatchRecordsExpanderAnchorElement.addAttribute(
			"onClick",
			JenkinsResultsParserUtil.combine(
				"return toggleStopWatchRecordExpander(\'", hashCode,
				"\', \'\')"));

		stopWatchRecordsExpanderAnchorElement.addAttribute(
			"style",
			"font-family: monospace, monospace; text-decoration: none");

		return stopWatchRecordsExpanderAnchorElement;
	}

	protected Map<String, String> getTempMap(String tempMapName) {
		String tempMapURL = getTempMapURL(tempMapName);

		if (tempMapURL == null) {
			return getTempMapFromBuildDatabase(tempMapName);
		}

		JSONObject tempMapJSONObject = null;

		try {
			tempMapJSONObject = JenkinsResultsParserUtil.toJSONObject(
				JenkinsResultsParserUtil.getLocalURL(tempMapURL), false, 0, 0,
				0);
		}
		catch (IOException ioException) {
		}

		if ((tempMapJSONObject == null) ||
			!tempMapJSONObject.has("properties")) {

			return getTempMapFromBuildDatabase(tempMapName);
		}

		JSONArray propertiesJSONArray = tempMapJSONObject.getJSONArray(
			"properties");

		Map<String, String> tempMap = new HashMap<>(
			propertiesJSONArray.length());

		for (int i = 0; i < propertiesJSONArray.length(); i++) {
			JSONObject propertyJSONObject = propertiesJSONArray.getJSONObject(
				i);

			String key = propertyJSONObject.getString("name");
			String value = propertyJSONObject.optString("value");

			if ((value != null) && !value.isEmpty()) {
				tempMap.put(key, value);
			}
		}

		return tempMap;
	}

	protected Map<String, String> getTempMapFromBuildDatabase(
		String tempMapName) {

		Map<String, String> tempMap = new HashMap<>();

		if (!fromArchive) {
			BuildDatabase buildDatabase = getBuildDatabase();

			Properties properties = buildDatabase.getProperties(tempMapName);

			for (String propertyName : properties.stringPropertyNames()) {
				tempMap.put(propertyName, properties.getProperty(propertyName));
			}
		}

		return tempMap;
	}

	protected String getTempMapURL(String tempMapName) {
		if (tempMapName.equals("start.properties")) {
			return getStartPropertiesTempMapURL();
		}

		if (tempMapName.equals("stop.properties")) {
			return getStopPropertiesTempMapURL();
		}

		return null;
	}

	protected int getTestCountByStatus(String status) {
		JSONObject testReportJSONObject = getTestReportJSONObject(false);

		if ((testReportJSONObject == null) || testReportJSONObject.isEmpty()) {
			return 0;
		}

		if (status.equals("FAILURE")) {
			return testReportJSONObject.getInt("failCount");
		}

		if (status.equals("SUCCESS")) {
			return testReportJSONObject.getInt("passCount");
		}

		throw new IllegalArgumentException("Invalid status: " + status);
	}

	protected List<TestResult> getTestResults(
		Build build, JSONArray suitesJSONArray, String testStatus) {

		List<TestResult> testResults = new ArrayList<>();

		for (int i = 0; i < suitesJSONArray.length(); i++) {
			JSONObject suiteJSONObject = suitesJSONArray.getJSONObject(i);

			JSONArray casesJSONArray = suiteJSONObject.getJSONArray("cases");

			for (int j = 0; j < casesJSONArray.length(); j++) {
				TestResult testResult = TestResultFactory.newTestResult(
					build, casesJSONArray.getJSONObject(j));

				if ((testStatus == null) ||
					testStatus.equals(testResult.getStatus())) {

					testResults.add(testResult);
				}
			}
		}

		return testResults;
	}

	protected boolean isParentBuildRoot() {
		if (_parentBuild == null) {
			return false;
		}

		if ((_parentBuild.getParentBuild() == null) &&
			(_parentBuild instanceof TopLevelBuild)) {

			return true;
		}

		return false;
	}

	protected void loadParametersFromBuildJSONObject() {
		if (getBuildURL() == null) {
			return;
		}

		JSONObject buildJSONObject = getBuildJSONObject(
			"actions[parameters[*]]");

		JSONArray actionsJSONArray = buildJSONObject.getJSONArray("actions");

		if (actionsJSONArray.length() == 0) {
			_parameters = new HashMap<>();

			return;
		}

		for (int i = 0; i < actionsJSONArray.length(); i++) {
			JSONObject actionJSONObject = actionsJSONArray.getJSONObject(i);

			if (!actionJSONObject.has("parameters")) {
				continue;
			}

			JSONArray parametersJSONArray = actionJSONObject.getJSONArray(
				"parameters");

			_parameters = new HashMap<>(parametersJSONArray.length());

			for (int j = 0; j < parametersJSONArray.length(); j++) {
				JSONObject parameterJSONObject =
					parametersJSONArray.getJSONObject(j);

				Object value = parameterJSONObject.opt("value");

				if (value instanceof String) {
					String valueString = value.toString();

					if (!valueString.isEmpty()) {
						_parameters.put(
							parameterJSONObject.getString("name"),
							value.toString());
					}
				}
			}

			return;
		}

		_parameters = Collections.emptyMap();
	}

	protected void loadParametersFromQueryString(String queryString) {
		for (String parameter : queryString.split("&")) {
			if (!parameter.contains("=")) {
				continue;
			}

			String[] nameValueArray = parameter.split("=");

			if (nameValueArray.length == 2) {
				_parameters.put(nameValueArray[0], nameValueArray[1]);
			}
			else if (nameValueArray.length == 1) {
				_parameters.put(nameValueArray[0], "");
			}
		}
	}

	protected void setJobName(String jobName) {
		_jobName = jobName;

		Matcher matcher = jobNamePattern.matcher(jobName);

		if (matcher.find()) {
			_branchName = matcher.group("branchName");

			return;
		}

		_branchName = "master";
	}

	protected boolean skipUpdate() {
		if (isBuildModified() || !Objects.equals(getStatus(), "completed")) {
			return false;
		}

		return true;
	}

	protected String toJenkinsReportDateString(Date date, String timeZoneName) {
		Properties buildProperties = null;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get build properties", ioException);
		}

		return JenkinsResultsParserUtil.toDateString(
			date, buildProperties.getProperty("jenkins.report.date.format"),
			timeZoneName);
	}

	protected void writeArchiveFile(String content, String path)
		throws IOException {

		JenkinsResultsParserUtil.write(
			new File(getArchiveRootDir(), path),
			JenkinsResultsParserUtil.redact(replaceBuildURL(content)));
	}

	protected static final String BAD_BUILD_URLS_PROPERTIES_KEY =
		"bad-build-urls.properties";

	protected static final String BUILD_URLS_PROPERTIES_KEY =
		"build-urls.properties";

	protected static final int PIXELS_WIDTH_INDENT = 35;

	protected static final Pattern jobNamePattern = Pattern.compile(
		"(?<baseJob>[^\\(]+)\\((?<branchName>[^\\)]+)\\)");
	protected static final Pattern stopWatchPattern = Pattern.compile(
		JenkinsResultsParserUtil.combine(
			"\\s*(\\[beanshell\\])?\\s*\\[stopwatch\\]\\s*\\[(?<name>[^:]+): ",
			"((?<minutes>\\d+):)?((?<seconds>\\d+))?\\.",
			"(?<milliseconds>\\d+) sec\\]"));
	protected static final Pattern stopWatchStartTimestampPattern =
		Pattern.compile(
			JenkinsResultsParserUtil.combine(
				"\\s*(\\[beanshell\\])?\\s*\\[echo\\] (?<name>.*)" +
					"\\.start\\.timestamp: (?<timestamp>.*)$"));
	protected static final SimpleDateFormat stopWatchTimestampSimpleDateFormat =
		new SimpleDateFormat("MM-dd-yyyy HH:mm:ss:SSS z");

	protected int consoleReadCursor;
	protected boolean fromArchive;
	protected boolean fromCompletedBuild;
	protected String gitRepositoryName;
	protected Long invokedTime;
	protected Long startTime;

	private void _archive(String content, boolean required, String urlSuffix) {
		boolean readyToArchive = true;

		if (!Objects.equals(getStatus(), "completed")) {
			readyToArchive = false;
		}
		else if (!(this instanceof TopLevelBuild)) {
			JSONObject buildJSONObject = JenkinsAPIUtil.getAPIJSONObject(
				getBuildURL(), "duration");

			if (buildJSONObject != null) {
				long duration = buildJSONObject.optLong("duration", 0L);

				if (duration == 0) {
					readyToArchive = false;
				}
			}
			else {
				readyToArchive = false;
			}
		}

		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		if (jenkinsMaster == null) {
			return;
		}

		File archiveFile = getArchiveFile(urlSuffix);

		if (!readyToArchive) {
			if (archiveFile.exists()) {
				JenkinsResultsParserUtil.delete(archiveFile);
			}

			return;
		}

		if (archiveFile.exists()) {
			return;
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		String urlString = getBuildURL() + urlSuffix;

		if (urlString.endsWith("json")) {
			urlString += "?pretty";
		}

		urlSuffix = JenkinsResultsParserUtil.fixFileName(urlSuffix);

		if (JenkinsResultsParserUtil.isNullOrEmpty(content)) {
			try {
				int maxRetries = 0;
				int retryPeriodSeconds = 0;

				if (required) {
					maxRetries = 2;
					retryPeriodSeconds = 5;
				}

				content = JenkinsResultsParserUtil.toString(
					JenkinsResultsParserUtil.getLocalURL(urlString), false,
					maxRetries, retryPeriodSeconds, 0, true);
			}
			catch (IOException ioException) {
				if (required) {
					throw new RuntimeException(
						"Unable to archive " + urlString, ioException);
				}

				return;
			}
		}

		if (JenkinsResultsParserUtil.isNullOrEmpty(content)) {
			return;
		}

		try {
			writeArchiveFile(content, getArchivePath() + "/" + urlSuffix);
		}
		catch (IOException ioException) {
			throw new RuntimeException("Unable to write file", ioException);
		}
		finally {
			if (JenkinsResultsParserUtil.debug) {
				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Archived ", String.valueOf(getArchiveFile(urlSuffix)),
						" in ",
						JenkinsResultsParserUtil.toDurationString(
							JenkinsResultsParserUtil.getCurrentTimeMillis() -
								start)));
			}
		}
	}

	private void _archiveBuildJSON() {
		_archive(null, true, "api/json");
	}

	private void _archiveConsoleLog() {
		if (!Objects.equals(getStatus(), "completed") ||
			JenkinsResultsParserUtil.isNullOrEmpty(getConsoleText())) {

			return;
		}

		File archiveFile = getArchiveFile("consoleText");

		if (archiveFile.exists()) {
			return;
		}

		_jenkinsConsoleTextLoader.moveCacheFileToArchiveFile(archiveFile);
	}

	private void _archiveMarkerFile() {
		_archive(
			String.valueOf(JenkinsResultsParserUtil.getCurrentTimeMillis()),
			true, "archive-marker");
	}

	private void _archiveTestReportJSON() {
		_archive(null, false, "testReport/api/json");
	}

	private int _getMaximumInvocationCount() {
		try {
			Properties properties =
				JenkinsResultsParserUtil.getBuildProperties();

			String propertyName = "build.max.invocation.count";

			if (properties.containsKey(propertyName)) {
				return Integer.parseInt(properties.getProperty(propertyName));
			}
		}
		catch (IOException ioException) {
			System.out.println("Unable to load \"build.max.invocation.count\"");
		}

		return _MAXIMUM_INVOCATION_COUNT;
	}

	private List<Element> _getStopWatchRecordTableRowElements(
		StopWatchRecord stopWatchRecord) {

		Element buildInfoElement = Dom4JUtil.getNewElement("tr", null);

		String buildHashCode = String.valueOf(hashCode());

		buildInfoElement.addAttribute(
			"id", buildHashCode + "-" + stopWatchRecord.getName());

		buildInfoElement.addAttribute("style", "display: none");

		Element expanderAnchorElement = getStopWatchRecordExpanderAnchorElement(
			stopWatchRecord, buildHashCode);

		Element nameElement = Dom4JUtil.getNewElement(
			"td", buildInfoElement, expanderAnchorElement,
			stopWatchRecord.getShortName());

		int indent =
			(getDepth() + stopWatchRecord.getDepth() + 1) * PIXELS_WIDTH_INDENT;

		if (expanderAnchorElement != null) {
			indent -= _PIXELS_WIDTH_EXPANDER;
		}

		nameElement.addAttribute(
			"style",
			JenkinsResultsParserUtil.combine(
				"text-indent: ", String.valueOf(indent), "px"));

		Dom4JUtil.getNewElement("td", buildInfoElement, "&nbsp;");

		Dom4JUtil.getNewElement("td", buildInfoElement, "&nbsp;");

		Dom4JUtil.getNewElement(
			"td", buildInfoElement,
			toJenkinsReportDateString(
				new Date(stopWatchRecord.getStartTimestamp()),
				getJenkinsReportTimeZoneName()));

		Long duration = stopWatchRecord.getDuration();

		if (duration == null) {
			Dom4JUtil.getNewElement("td", buildInfoElement, "&nbsp;");
		}
		else {
			Dom4JUtil.getNewElement(
				"td", buildInfoElement,
				JenkinsResultsParserUtil.toDurationString(
					stopWatchRecord.getDuration()));
		}

		Dom4JUtil.getNewElement("td", buildInfoElement, "&nbsp;");

		Dom4JUtil.getNewElement("td", buildInfoElement, "&nbsp;");

		List<Element> jenkinsReportTableRowElements = new ArrayList<>();

		jenkinsReportTableRowElements.add(buildInfoElement);

		Set<StopWatchRecord> childStopWatchRecords =
			stopWatchRecord.getChildStopWatchRecords();

		if (childStopWatchRecords != null) {
			List<String> childStopWatchRecordNames = new ArrayList<>(
				childStopWatchRecords.size());

			for (StopWatchRecord childStopWatchRecord : childStopWatchRecords) {
				childStopWatchRecordNames.add(childStopWatchRecord.getName());

				List<Element> childJenkinsReportTableRowElements =
					_getStopWatchRecordTableRowElements(childStopWatchRecord);

				for (Element childJenkinsReportTableRowElement :
						childJenkinsReportTableRowElements) {

					childJenkinsReportTableRowElement.addAttribute(
						"style", "display: none");
				}

				jenkinsReportTableRowElements.addAll(
					childJenkinsReportTableRowElements);
			}

			buildInfoElement.addAttribute(
				"child-stopwatch-rows",
				JenkinsResultsParserUtil.join(",", childStopWatchRecordNames));
		}

		return jenkinsReportTableRowElements;
	}

	private synchronized void _initTestClassResults() {
		if (!isCompleted() || (_testClassResults != null)) {
			return;
		}

		JSONObject testReportJSONObject = null;

		try {
			testReportJSONObject = getTestReportJSONObject(true);
		}
		catch (RuntimeException runtimeException) {
			_testClassResults = new ConcurrentHashMap<>();

			return;
		}

		_testClassResults = new ConcurrentHashMap<>();

		if ((testReportJSONObject == null) || testReportJSONObject.isEmpty()) {
			return;
		}

		List<JSONArray> suitesJSONArrays = new ArrayList<>();

		if (testReportJSONObject.has("suites")) {
			suitesJSONArrays.add(testReportJSONObject.getJSONArray("suites"));
		}
		else if (testReportJSONObject.has("childReports")) {
			JSONArray childReportsJSONArray = testReportJSONObject.getJSONArray(
				"childReports");

			for (int i = 0; i < childReportsJSONArray.length(); i++) {
				JSONObject childReportJSONObject =
					childReportsJSONArray.getJSONObject(i);

				if (!childReportJSONObject.has("result")) {
					continue;
				}

				JSONObject resultJSONObject =
					childReportJSONObject.getJSONObject("result");

				if (!resultJSONObject.has("suites")) {
					continue;
				}

				suitesJSONArrays.add(resultJSONObject.getJSONArray("suites"));
			}
		}

		for (JSONArray suitesJSONArray : suitesJSONArrays) {
			for (int i = 0; i < suitesJSONArray.length(); i++) {
				JSONObject suiteJSONObject = suitesJSONArray.getJSONObject(i);

				TestClassResult testClassResult =
					TestClassResultFactory.newTestClassResult(
						this, suiteJSONObject);

				_testClassResults.put(
					testClassResult.getClassName(), testClassResult);
			}
		}
	}

	private boolean _isDifferent(String newValue, String oldValue) {
		if (oldValue == null) {
			if (newValue != null) {
				return true;
			}

			return false;
		}

		return !oldValue.equals(newValue);
	}

	private void _setBuildURL(String buildURL) {
		try {
			buildURL = JenkinsResultsParserUtil.decode(buildURL);
		}
		catch (UnsupportedEncodingException unsupportedEncodingException) {
			throw new IllegalArgumentException(
				"Unable to decode " + buildURL, unsupportedEncodingException);
		}

		Build parentBuild = getParentBuild();

		try {
			if (parentBuild != null) {
				fromArchive = parentBuild.isFromArchive();
			}
			else {
				String archiveMarkerContent = JenkinsResultsParserUtil.toString(
					buildURL + "/archive-marker", false, 0, 0, 0);

				fromArchive =
					(archiveMarkerContent != null) &&
					!archiveMarkerContent.isEmpty();
			}
		}
		catch (IOException ioException) {
			fromArchive = false;
		}

		MultiPattern buildURLMultiPattern = getBuildURLMultiPattern();

		Matcher matcher = buildURLMultiPattern.find(buildURL);

		if (matcher == null) {
			Pattern archiveBuildURLPattern = getArchiveBuildURLPattern();

			matcher = archiveBuildURLPattern.matcher(buildURL);

			if (!matcher.find()) {
				throw new IllegalArgumentException(
					"Invalid build URL " + buildURL);
			}

			setArchiveName(matcher.group("archiveName"));
		}

		setBuildURL(JenkinsResultsParserUtil.getRemoteURL(buildURL));

		JenkinsMaster jenkinsMaster = JenkinsMaster.getInstance(
			matcher.group("master"));

		setJenkinsMaster(jenkinsMaster);

		setJenkinsCohort(jenkinsMaster.getJenkinsCohort());

		setJobName(matcher.group("jobName"));

		loadParametersFromBuildJSONObject();

		reset();

		JSONObject buildJSONObject = getBuildJSONObject("result,queueId,url");

		Invocation invocation = new Invocation(
			this, jenkinsMaster, buildJSONObject.getLong("queueId"));

		invocation.setBuildURL(buildJSONObject.getString("url"));

		addInvocation(invocation);

		String result = buildJSONObject.optString("result");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(result)) {
			fromCompletedBuild = isFromCompletedBuild();

			setResult(result);
			setStatus("completed");
		}
		else {
			setStatus("running");
		}
	}

	private void _setInvocationURL(String invocationURL) {
		if (getBuildURL() != null) {
			return;
		}

		try {
			invocationURL = JenkinsResultsParserUtil.decode(invocationURL);
		}
		catch (UnsupportedEncodingException unsupportedEncodingException) {
			throw new IllegalArgumentException(
				"Unable to decode " + invocationURL,
				unsupportedEncodingException);
		}

		Matcher invocationURLMatcher = _invocationURLPattern.matcher(
			invocationURL);

		if (!invocationURLMatcher.find()) {
			throw new RuntimeException("Invalid invocation URL");
		}

		setJobName(invocationURLMatcher.group("jobName"));

		JenkinsCohort jenkinsCohort = JenkinsCohort.getInstance(
			invocationURLMatcher.group("cohortName"));

		loadParametersFromQueryString(invocationURL);

		String masterId = invocationURLMatcher.group("masterId");

		if (JenkinsResultsParserUtil.isInteger(masterId)) {
			setJenkinsMaster(
				JenkinsMaster.getInstance(
					jenkinsCohort.getName() + "-" + masterId));
		}

		setStatus("starting");
	}

	private static final FailureMessageGenerator[] _FAILURE_MESSAGE_GENERATORS =
		{new GenericFailureMessageGenerator()};

	private static final Integer _INVOKED_BATCH_SIZE_DEFAULT = 1;

	private static final int _MAXIMUM_INVOCATION_COUNT = 2;

	private static final Integer _MAXIMUM_SLAVES_PER_HOST = 2;

	private static final Integer _MINIMUM_SLAVE_RAM_DEFAULT = 12;

	private static final String _NAME_JENKINS_REPORT_TIME_ZONE;

	private static final int _PIXELS_WIDTH_EXPANDER = 20;

	private static final String[] _TOKENS_HIGH_PRIORITY_CONTENT = {
		"compileJSP", "SourceFormatter.format", "Unable to compile JSPs"
	};

	private static final MultiPattern _buildURLMultiPattern = new MultiPattern(
		JenkinsResultsParserUtil.combine(
			"\\w+://(?<master>[^/]+)/+job/+(?<jobName>[^/]+(/label=[^/]+)?)/",
			"(?<buildNumber>\\d+)/?"));
	private static final Pattern _invocationURLPattern = Pattern.compile(
		JenkinsResultsParserUtil.combine(
			"\\w+://(?<cohortName>test-\\d+)(-(?<masterId>\\d+))?",
			"(\\.liferay\\.com)?/+job\\/+(?<jobName>[^\\/]+).*\\/",
			"buildWithParameters\\?(?<queryString>.*)"));
	private static final Pattern _testrayCloudObjectURLPattern =
		Pattern.compile(
			JenkinsResultsParserUtil.combine(
				"\\[(beanshell|exec)\\] Created Cloud Object (?<url>",
				"https://storage.cloud.google.com/[^\\s?]+).*"));

	static {
		Properties properties = null;

		try {
			properties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get build properties", ioException);
		}

		_NAME_JENKINS_REPORT_TIME_ZONE = properties.getProperty(
			"jenkins.report.time.zone");
	}

	private String _archiveName = "archive";
	private File _archiveRootDir = new File(
		JenkinsResultsParserUtil.urlDependenciesFile.substring(
			"file:".length()));
	private final Map<String, BranchInformation> _branchInformationMap =
		new HashMap<>();
	private String _branchName;
	private BuildDatabase _buildDatabase;
	private String _buildDescription;
	private Boolean _buildDurationsEnabled;
	private JSONObject _buildJSONObject;
	private final BuildUpdater _buildUpdater;
	private String _buildURL;
	private Long _duration;
	private Element _gitHubMessageElement;
	private final List<Invocation> _invocations = new ArrayList<>();
	private int _invokedBatchSize;
	private JenkinsCohort _jenkinsCohort;
	private JenkinsConsoleTextLoader _jenkinsConsoleTextLoader;
	private JenkinsMaster _jenkinsMaster;
	private JenkinsSlave _jenkinsSlave;
	private Job _job;
	private String _jobName;
	private int _maximumSlavesPerHost;
	private int _minimumSlaveRAM;
	private Map<String, String> _parameters = new HashMap<>();
	private final Build _parentBuild;
	private String _previousStatus;
	private String _result;
	private String _status;
	private final Map<String, Long> _statusDurations = new HashMap<>();
	private long _statusModifiedTime;
	private StopWatchRecordsGroup _stopWatchRecordsGroup;
	private Map<String, TestClassResult> _testClassResults;
	private final List<URL> _testrayAttachmentURLs = new ArrayList<>();
	private boolean _testrayAttachmentURLsFound;
	private JSONObject _testReportJSONObject;

}