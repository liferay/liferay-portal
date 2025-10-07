/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.failure.message.generator.CIFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.CITestSuiteValidationFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.CompileFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.DownstreamFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.FailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.FormatFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.GenericFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.GitLPushFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.GradleTaskFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.InvalidGitCommitSHAFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.InvalidSenderSHAFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.JenkinsRegenFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.JenkinsSourceFormatFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.PoshiTestFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.PoshiValidationFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.RebaseFailureMessageGenerator;
import com.liferay.jenkins.results.parser.failure.message.generator.RelevantRuleValidationFailureMessageGenerator;
import com.liferay.jenkins.results.parser.testray.TestrayBuild;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Kevin Yen
 */
public abstract class BaseTopLevelBuild
	extends BaseParentBuild implements TopLevelBuild {

	@Override
	public void addTestrayAttachmentURL(URL testrayAttachmentURL) {
		if (_testrayAttachmentURLs.contains(testrayAttachmentURL)) {
			return;
		}

		_testrayAttachmentURLs.add(testrayAttachmentURL);
	}

	@Override
	public void addTimelineData(TimelineData timelineData) {
		timelineData.addTimelineData(this);

		if (getTopLevelBuild() == this) {
			addDownstreamBuildsTimelineData(timelineData);
		}
	}

	@Override
	public String getAcceptanceUpstreamJobName() {
		String jobName = getJobName();

		if (jobName.contains("pullrequest")) {
			String branchName = getBranchName();

			if (branchName.startsWith("ee-")) {
				return jobName.replace("pullrequest", "upstream");
			}

			return jobName.replace("pullrequest", "upstream-dxp");
		}

		return "";
	}

	@Override
	public String getAcceptanceUpstreamJobURL() {
		String upstreamAcceptanceJenkinsMaster = null;

		try {
			upstreamAcceptanceJenkinsMaster =
				JenkinsResultsParserUtil.getBuildProperty(
					"upstream.acceptance.jenkins.master");
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get upstream acceptance Jenkins master property");
		}

		String jobName = getJobName();

		if (jobName.contains("pullrequest")) {
			String acceptanceUpstreamJobURL = JenkinsResultsParserUtil.combine(
				"https://", upstreamAcceptanceJenkinsMaster,
				".liferay.com/job/", getAcceptanceUpstreamJobName());

			try {
				JenkinsResultsParserUtil.toString(
					JenkinsResultsParserUtil.getLocalURL(
						acceptanceUpstreamJobURL),
					false, 0, 0, 0);
			}
			catch (IOException ioException) {
				return null;
			}

			return acceptanceUpstreamJobURL;
		}

		return null;
	}

	@Override
	public URL getArtifactsBaseURL() {
		StringBuilder sb = new StringBuilder();

		try {
			URL buildBaseArtifactURL = new URL(
				JenkinsResultsParserUtil.getBuildProperty(
					"build.base.artifact.url"));

			sb.append(buildBaseArtifactURL);
		}
		catch (IOException ioException) {
			return null;
		}

		TopLevelBuild topLevelBuild = getTopLevelBuild();

		sb.append("/");

		sb.append(
			JenkinsResultsParserUtil.toDateString(
				new Date(topLevelBuild.getStartTime()), "yyyy-MM",
				"America/Los_Angeles"));

		JenkinsMaster jenkinsMaster = topLevelBuild.getJenkinsMaster();

		sb.append("/");
		sb.append(jenkinsMaster.getName());
		sb.append("/");
		sb.append(topLevelBuild.getJobName());
		sb.append("/");
		sb.append(topLevelBuild.getBuildNumber());

		try {
			return new URL(sb.toString());
		}
		catch (MalformedURLException malformedURLException) {
		}

		return null;
	}

	@Override
	public long getAverageDelayTime() {
		if (getDownstreamBuildCount(null) == 0) {
			return 0;
		}

		List<Build> allDownstreamBuilds = JenkinsResultsParserUtil.flatten(
			getDownstreamBuilds(null));

		if (allDownstreamBuilds.isEmpty()) {
			return 0;
		}

		long totalDelayTime = 0;

		for (Build downstreamBuild : allDownstreamBuilds) {
			totalDelayTime += downstreamBuild.getDelayTime();
		}

		return totalDelayTime / allDownstreamBuilds.size();
	}

	@Override
	public Map<String, String> getBaseGitRepositoryDetailsTempMap() {
		String gitRepositoryType = getBaseGitRepositoryType();

		String tempMapName = "git." + gitRepositoryType + ".properties";

		return getTempMap(tempMapName);
	}

	@Override
	public String getBuildName() {
		String jenkinsJobVariant = getParameterValue("JENKINS_JOB_VARIANT");

		if (!JenkinsResultsParserUtil.isNullOrEmpty(jenkinsJobVariant)) {
			return getJobName() + "/" + jenkinsJobVariant;
		}

		return "top-level";
	}

	@Override
	public JSONObject getBuildReportJSONObject() {
		JSONObject buildReportJSONObject = super.getBuildReportJSONObject();

		buildReportJSONObject.put("buildParameters", getParameters());

		if (!(this instanceof ControllerTopLevelBuild)) {
			buildReportJSONObject.put(
				"testSuiteName", getTestSuiteName()
			).put(
				"totalDuration", getTotalDuration()
			);
		}

		return buildReportJSONObject;
	}

	@Override
	public Build getControllerBuild() {
		if (_controllerBuild != null) {
			return _controllerBuild;
		}

		String controllerBuildURL = getParameterValue("CONTROLLER_BUILD_URL");

		if ((controllerBuildURL == null) ||
			!controllerBuildURL.matches("https?://.*")) {

			return null;
		}

		_controllerBuild = BuildFactory.newBuild(controllerBuildURL, null);

		return _controllerBuild;
	}

	@Override
	public String getDisplayName() {
		StringBuilder sb = new StringBuilder(super.getDisplayName());

		String jenkinsJobVariant = getParameterValue("JENKINS_JOB_VARIANT");

		if ((getParentBuild() != null) && (jenkinsJobVariant != null) &&
			!jenkinsJobVariant.isEmpty()) {

			sb.append("/");
			sb.append(jenkinsJobVariant);
		}

		return sb.toString();
	}

	@Override
	public AxisBuild getDownstreamAxisBuild(String axisName) {
		AxisBuild targetAxisBuild = _downstreamAxisBuilds.get(axisName);

		if (targetAxisBuild != null) {
			return targetAxisBuild;
		}

		for (AxisBuild axisBuild : getDownstreamAxisBuilds()) {
			if (axisName.equals(axisBuild.getAxisName())) {
				return axisBuild;
			}
		}

		return null;
	}

	@Override
	public List<AxisBuild> getDownstreamAxisBuilds() {
		if (_downstreamAxisBuildsPopulated &&
			!_downstreamAxisBuilds.isEmpty()) {

			List<AxisBuild> downstreamAxisBuilds = new ArrayList<>(
				_downstreamAxisBuilds.values());

			Collections.sort(
				downstreamAxisBuilds,
				new BaseBuild.BuildDisplayNameComparator());

			return downstreamAxisBuilds;
		}

		List<AxisBuild> downstreamAxisBuilds = new ArrayList<>();

		for (BatchBuild downstreamBatchBuild : getDownstreamBatchBuilds()) {
			downstreamAxisBuilds.addAll(
				downstreamBatchBuild.getDownstreamAxisBuilds());
		}

		synchronized (_downstreamAxisBuilds) {
			if (isCompleted() && !_downstreamAxisBuildsPopulated) {
				for (AxisBuild downstreamAxisBuild : downstreamAxisBuilds) {
					_downstreamAxisBuilds.put(
						downstreamAxisBuild.getAxisName(), downstreamAxisBuild);
				}

				_downstreamAxisBuildsPopulated = true;
			}
		}

		Collections.sort(
			downstreamAxisBuilds, new BaseBuild.BuildDisplayNameComparator());

		return downstreamAxisBuilds;
	}

	@Override
	public BatchBuild getDownstreamBatchBuild(String jobVariant) {
		BatchBuild targetBatchBuild = _downstreamBatchBuilds.get(jobVariant);

		if (targetBatchBuild != null) {
			return targetBatchBuild;
		}

		for (BatchBuild batchBuild : getDownstreamBatchBuilds()) {
			if (jobVariant.equals(batchBuild.getJobVariant())) {
				return batchBuild;
			}
		}

		return null;
	}

	@Override
	public List<BatchBuild> getDownstreamBatchBuilds() {
		if (_downstreamBatchBuildsPopulated &&
			!_downstreamBatchBuilds.isEmpty()) {

			List<BatchBuild> downstreamBatchBuilds = new ArrayList<>(
				_downstreamBatchBuilds.values());

			Collections.sort(
				downstreamBatchBuilds,
				new BaseBuild.BuildDisplayNameComparator());

			return downstreamBatchBuilds;
		}

		List<BatchBuild> downstreamBatchBuilds = new ArrayList<>();

		List<Build> downstreamBuilds = getDownstreamBuilds(null);

		for (Build downstreamBuild : downstreamBuilds) {
			if (!(downstreamBuild instanceof BatchBuild)) {
				continue;
			}

			downstreamBatchBuilds.add((BatchBuild)downstreamBuild);
		}

		synchronized (_downstreamBatchBuilds) {
			if (isCompleted() && !_downstreamBatchBuildsPopulated) {
				for (BatchBuild downstreamBatchBuild : downstreamBatchBuilds) {
					String jobVariant = downstreamBatchBuild.getJobVariant();

					if (JenkinsResultsParserUtil.isNullOrEmpty(jobVariant)) {
						continue;
					}

					_downstreamBatchBuilds.put(
						jobVariant, downstreamBatchBuild);
				}

				_downstreamBatchBuildsPopulated = true;
			}
		}

		Collections.sort(
			downstreamBatchBuilds, new BaseBuild.BuildDisplayNameComparator());

		return downstreamBatchBuilds;
	}

	@Override
	public DownstreamBuild getDownstreamBuild(String axisName) {
		for (Build downstreamBuild : getDownstreamBuilds(null)) {
			String downstreamAxisName = downstreamBuild.getParameterValue(
				"JOB_VARIANT");

			if (JenkinsResultsParserUtil.isNullOrEmpty(downstreamAxisName)) {
				continue;
			}

			String downstreamAxisVariable = downstreamBuild.getParameterValue(
				"AXIS_VARIABLE");

			if (JenkinsResultsParserUtil.isNullOrEmpty(
					downstreamAxisVariable)) {

				continue;
			}

			downstreamAxisName += "/" + downstreamAxisVariable;

			if (!axisName.equals(downstreamAxisName) ||
				!(downstreamBuild instanceof DownstreamBuild)) {

				continue;
			}

			return (DownstreamBuild)downstreamBuild;
		}

		return null;
	}

	@Override
	public Element getGitHubMessageElement() {
		sortDownstreamBuilds();

		if (getParentBuild() == null) {
			return getTopGitHubMessageElement();
		}

		return super.getGitHubMessageElement();
	}

	@Override
	public JenkinsCohort getJenkinsCohort() {
		if (_jenkinsCohort != null) {
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
	public String getJenkinsReport() {
		try {
			return JenkinsResultsParserUtil.toString(
				JenkinsResultsParserUtil.getLocalURL(getJenkinsReportURL()));
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get Jenkins report", ioException);
		}
	}

	@Override
	public synchronized Element getJenkinsReportElement() {
		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		try {
			return Dom4JUtil.getNewElement(
				"html", null, getJenkinsReportHeadElement(),
				getJenkinsReportBodyElement());
		}
		finally {
			String duration = JenkinsResultsParserUtil.toDurationString(
				JenkinsResultsParserUtil.getCurrentTimeMillis() - start);

			System.out.println("Jenkins reported generated in " + duration);
		}
	}

	@Override
	public String getJenkinsReportURL() {
		if (fromArchive) {
			return getBuildURL() + "/jenkins-report.html";
		}

		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		return JenkinsResultsParserUtil.combine(
			"https://", jenkinsMaster.getName(), ".liferay.com/",
			"userContent/jobs/", getJobName(), "/builds/",
			String.valueOf(getBuildNumber()), "/jenkins-report.html");
	}

	@Override
	public File getJobSummaryDir() {
		File jobSummaryDir = new File(getBuildDirPath(), "job-summary");

		if (!jobSummaryDir.exists()) {
			try {
				CIJobSummaryReportUtil.writeJobSummaryReport(
					jobSummaryDir, getJob());
			}
			catch (IOException ioException) {
				throw new RuntimeException(ioException);
			}
		}

		return jobSummaryDir;
	}

	@Override
	public int getJobVariantsDownstreamBuildCount(
		List<String> jobVariants, String result, String status) {

		List<Build> jobVariantsDownstreamBuilds =
			getJobVariantsDownstreamBuilds(jobVariants, result, status);

		return jobVariantsDownstreamBuilds.size();
	}

	@Override
	public List<Build> getJobVariantsDownstreamBuilds(
		Iterable<String> jobVariants, String result, String status) {

		List<Build> jobVariantsDownstreamBuilds = new ArrayList<>();

		List<Build> downstreamBuilds = getDownstreamBuilds(result, status);

		for (Build downstreamBuild : downstreamBuilds) {
			String downstreamBuildJobVariant = downstreamBuild.getJobVariant();

			for (String jobVariant : jobVariants) {
				if (downstreamBuildJobVariant.startsWith(jobVariant)) {
					jobVariantsDownstreamBuilds.add(downstreamBuild);

					break;
				}
			}
		}

		return jobVariantsDownstreamBuilds;
	}

	@Override
	public Map<String, String> getMetricLabels() {
		Map<String, String> metricLabels = new TreeMap<>();

		metricLabels.put("job_type", "top-level");
		metricLabels.put("top_level_job_name", getJobName());

		return metricLabels;
	}

	@Override
	public List<String> getProjectNames() {
		return Collections.emptyList();
	}

	@Override
	public String getStatusSummary() {
		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		if ((currentTimeMillis - _MILLIS_DOWNSTREAM_BUILDS_LISTING_INTERVAL) >=
				_lastDownstreamBuildsListingTimestamp) {

			StringBuilder sb = new StringBuilder(_getStatusSummary());

			sb.append("\nRunning Builds: ");

			_lastDownstreamBuildsListingTimestamp =
				JenkinsResultsParserUtil.getCurrentTimeMillis();

			for (Build downstreamBuild : getDownstreamBuilds("running")) {
				sb.append("\n");
				sb.append(downstreamBuild.getBuildURL());
			}

			return sb.toString();
		}

		return _getStatusSummary();
	}

	@Override
	public synchronized List<URL> getTestrayAttachmentURLs() {
		return _testrayAttachmentURLs;
	}

	@Override
	public JSONObject getTestReportJSONObject(boolean cache) {
		return null;
	}

	@Override
	public String getTestSuiteName() {
		String testSuiteName = getParameterValue("CI_TEST_SUITE");

		if (testSuiteName == null) {
			testSuiteName = "default";
		}

		return testSuiteName;
	}

	public TimelineData getTimelineData() {
		return new TimelineData(500, this);
	}

	@Override
	public TopLevelBuildReport getTopLevelBuildReport() {
		if (JenkinsResultsParserUtil.isNullOrEmpty(getBuildURL())) {
			return null;
		}

		if (_topLevelBuildReport != null) {
			return _topLevelBuildReport;
		}

		_topLevelBuildReport = BuildReportFactory.newTopLevelBuildReport(this);

		return _topLevelBuildReport;
	}

	public URL getUserContentURL() {
		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		try {
			return new URL(
				JenkinsResultsParserUtil.combine(
					"https://", jenkinsMaster.getName(),
					".liferay.com/userContent/jobs/", getJobName(), "/builds/",
					String.valueOf(getBuildNumber())));
		}
		catch (MalformedURLException malformedURLException) {
			throw new RuntimeException(malformedURLException);
		}
	}

	@Override
	public Element getValidationGitHubMessageElement() {
		ValidationBuild validationBuild = null;

		for (Build downstreamBuild : getDownstreamBuilds()) {
			if (downstreamBuild instanceof ValidationBuild) {
				validationBuild = (ValidationBuild)downstreamBuild;
			}
		}

		if (validationBuild == null) {
			throw new RuntimeException("Unable to find a validation build");
		}

		return validationBuild.getGitHubMessageElement();
	}

	@Override
	public boolean isCompareToUpstream() {
		return _compareToUpstream;
	}

	@Override
	public boolean isFromCompletedBuild() {
		if (fromCompletedBuild) {
			return fromCompletedBuild;
		}

		Build parentBuild = getParentBuild();

		if (parentBuild != null) {
			return parentBuild.isFromCompletedBuild();
		}

		String consoleText = getConsoleText();

		if (JenkinsResultsParserUtil.isNullOrEmpty(consoleText)) {
			return false;
		}

		if (consoleText.contains("stop-current-job:") ||
			consoleText.contains(
				"com.liferay.jenkins.results.parser.BuildLauncher teardown")) {

			fromCompletedBuild = true;

			return fromCompletedBuild;
		}

		String buildURL = getBuildURL();

		if (!JenkinsResultsParserUtil.isURL(buildURL)) {
			return false;
		}

		try {
			JSONObject buildJSONObject = JenkinsResultsParserUtil.toJSONObject(
				buildURL + "/api/json?tree=result");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(
					buildJSONObject.optString("result", null))) {

				fromCompletedBuild = true;

				return fromCompletedBuild;
			}
		}
		catch (IOException | JSONException exception) {
			return false;
		}

		return false;
	}

	@Override
	public boolean isUniqueFailure() {
		return true;
	}

	@Override
	public void setCompareToUpstream(boolean compareToUpstream) {
		_compareToUpstream = compareToUpstream;
	}

	@Override
	public void takeSlaveOffline(SlaveOfflineRule slaveOfflineRule) {
	}

	@Override
	public synchronized void update() {
		if (skipUpdate()) {
			return;
		}

		super.update();

		if (_sendBuildMetrics && !fromArchive && (getParentBuild() == null)) {
			if (!fromCompletedBuild) {
				sendBuildMetricsOnModifiedBuilds();
			}
			else {
				sendBuildMetrics(
					StatsDMetricsUtil.generateGaugeDeltaMetric(
						"build_slave_usage_gauge", -1, getMetricLabels()));
			}
		}
	}

	public class WorkspaceBranchInformation implements BranchInformation {

		@Override
		public String getCachedRemoteGitRefName() {
			return _workspaceGitRepository.getGitHubDevBranchName();
		}

		@Override
		public String getOriginName() {
			return _workspaceGitRepository.getSenderBranchUsername();
		}

		@Override
		public Integer getPullRequestNumber() {
			Matcher matcher = _gitHubURLPattern.matcher(
				_workspaceGitRepository.getGitHubURL());

			if (!matcher.find()) {
				return 0;
			}

			return Integer.parseInt(matcher.group("pullNumber"));
		}

		@Override
		public String getReceiverUsername() {
			Matcher matcher = _gitHubURLPattern.matcher(
				_workspaceGitRepository.getGitHubURL());

			if (!matcher.find()) {
				return "liferay";
			}

			return matcher.group("username");
		}

		@Override
		public String getRepositoryName() {
			return _workspaceGitRepository.getName();
		}

		@Override
		public String getSenderBranchName() {
			return _workspaceGitRepository.getSenderBranchName();
		}

		@Override
		public String getSenderBranchSHA() {
			return _workspaceGitRepository.getSenderBranchSHA();
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
			String remoteURL = null;

			if (isReleaseBuild()) {
				remoteURL = JenkinsResultsParserUtil.combine(
					"git@github.com:", getSenderUsername(), "/",
					getReleaseRepositoryName(), ".git");
			}
			else {
				remoteURL = JenkinsResultsParserUtil.combine(
					"git@github.com:", getSenderUsername(), "/",
					getRepositoryName(), ".git");
			}

			return GitUtil.getRemoteGitRef(
				getSenderBranchName(), new File("."), remoteURL);
		}

		@Override
		public String getSenderUsername() {
			return _workspaceGitRepository.getSenderBranchUsername();
		}

		@Override
		public String getUpstreamBranchName() {
			return _workspaceGitRepository.getUpstreamBranchName();
		}

		@Override
		public String getUpstreamBranchSHA() {
			return _workspaceGitRepository.getBaseBranchSHA();
		}

		protected WorkspaceBranchInformation(
			WorkspaceGitRepository workspaceGitRepository) {

			_workspaceGitRepository = workspaceGitRepository;
		}

		private final WorkspaceGitRepository _workspaceGitRepository;

	}

	protected BaseTopLevelBuild(String url) {
		this(url, null);
	}

	protected BaseTopLevelBuild(String url, TopLevelBuild topLevelBuild) {
		super(url, topLevelBuild);

		Properties buildProperties = null;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get build.properties", ioException);
		}

		_sendBuildMetrics = Boolean.valueOf(
			buildProperties.getProperty("build.metrics.send"));

		if (_sendBuildMetrics) {
			_metricsHostName = buildProperties.getProperty(
				"build.metrics.host.name");

			String metricsHostPortString = buildProperties.getProperty(
				"build.metrics.host.port");

			if ((_metricsHostName == null) || (metricsHostPortString == null)) {
				throw new IllegalArgumentException(
					"Properties \"build.metrics.host.name\" and " +
						"\"build.metrics.host.port\" must be set to send " +
							"build metrics");
			}

			try {
				_metricsHostPort = Integer.parseInt(metricsHostPortString);
			}
			catch (NumberFormatException numberFormatException) {
				throw new IllegalArgumentException(
					"Please set \"build.metrics.host.port\" to an integer");
			}

			if (topLevelBuild == null) {
				sendBuildMetrics(
					StatsDMetricsUtil.generateGaugeDeltaMetric(
						"build_slave_usage_gauge", 1, getMetricLabels()));
			}
		}
	}

	@Override
	protected void findDownstreamBuilds() {
		if (getParentBuild() != null) {
			return;
		}

		BuildDatabase buildDatabase = getBuildDatabase();

		Properties properties = buildDatabase.getProperties(
			BUILD_URLS_PROPERTIES_KEY);

		Map<String, String> urlAxisNames = new HashMap<>();

		List<String> badBuildURLs = getBadBuildURLs();

		for (String propertyName : properties.stringPropertyNames()) {
			if (Objects.equals(propertyName, getJobVariant())) {
				continue;
			}

			String buildURL = properties.getProperty(propertyName);

			if (badBuildURLs.contains(buildURL)) {
				continue;
			}

			urlAxisNames.put(buildURL, propertyName);
		}

		if (!urlAxisNames.isEmpty()) {
			addDownstreamBuilds(urlAxisNames);

			return;
		}

		System.out.println(
			"Unable to find downstream builds in build-database.json");

		_findDownstreamBuildsInConsoleText();
	}

	@Override
	protected List<Callable<Object>> getArchiveCallables() {
		List<Callable<Object>> archiveCallables = super.getArchiveCallables();

		archiveCallables.add(
			new Callable<Object>() {

				@Override
				public Object call() {
					_archiveBuildDatabase();

					return null;
				}

			});
		archiveCallables.add(
			new Callable<Object>() {

				@Override
				public Object call() {
					_archiveJenkinsReport();

					return null;
				}

			});
		archiveCallables.add(
			new Callable<Object>() {

				@Override
				public Object call() {
					_archiveProperties();

					return null;
				}

			});

		return archiveCallables;
	}

	protected Element getBaseBranchDetailsElement() {
		String baseBranchURL = JenkinsResultsParserUtil.combine(
			"https://github.com/liferay/", getBaseGitRepositoryName(), "/tree/",
			getBranchName());

		String baseGitRepositoryName = getBaseGitRepositoryName();

		String baseGitRepositorySHA = null;

		if ((this instanceof WorkspaceBuild) && !fromArchive) {
			WorkspaceBuild workspaceBuild = (WorkspaceBuild)this;

			Workspace workspace = workspaceBuild.getWorkspace();

			WorkspaceGitRepository workspaceGitRepository =
				workspace.getPrimaryWorkspaceGitRepository();

			baseGitRepositorySHA = workspaceGitRepository.getBaseBranchSHA();
		}
		else if (!baseGitRepositoryName.equals("liferay-jenkins-ee") &&
				 baseGitRepositoryName.endsWith("-ee")) {

			baseGitRepositorySHA = getBaseGitRepositorySHA(
				baseGitRepositoryName.substring(
					0, baseGitRepositoryName.length() - 3));
		}
		else {
			baseGitRepositorySHA = getBaseGitRepositorySHA(
				baseGitRepositoryName);
		}

		Element baseGitBranchDetailsElement = Dom4JUtil.getNewElement(
			"p", null, "Branch Name: ",
			Dom4JUtil.getNewAnchorElement(baseBranchURL, getBranchName()));

		if (baseGitRepositorySHA != null) {
			String baseGitRepositoryCommitURL =
				"https://github.com/liferay/" + baseGitRepositoryName +
					"/commit/" + baseGitRepositorySHA;

			Dom4JUtil.addToElement(
				baseGitBranchDetailsElement, Dom4JUtil.getNewElement("br"),
				"Branch GIT ID: ",
				Dom4JUtil.getNewAnchorElement(
					baseGitRepositoryCommitURL, baseGitRepositorySHA));
		}

		return baseGitBranchDetailsElement;
	}

	protected Element[] getBuildFailureElements() {
		List<Build> failedDownstreamBuilds = getFailedDownstreamBuilds();

		if (failedDownstreamBuilds != null) {
			StringBuilder sb = new StringBuilder();

			sb.append("\nUnique Failures:");

			for (Build failedDownstreamBuild : failedDownstreamBuilds) {
				if (failedDownstreamBuild.isUniqueFailure()) {
					sb.append("\n");
					sb.append(failedDownstreamBuild.getDisplayName());

					for (TestResult testResult :
							failedDownstreamBuild.
								getUniqueFailureTestResults()) {

						sb.append("\n\t");
						sb.append(testResult.getDisplayName());
					}
				}
			}

			sb.append("\n\nUpstream Failures:");

			for (Build failedDownstreamBuild : failedDownstreamBuilds) {
				if (!failedDownstreamBuild.isUniqueFailure()) {
					sb.append("\n");
					sb.append(failedDownstreamBuild.getDisplayName());

					for (TestResult testResult :
							failedDownstreamBuild.
								getUpstreamJobFailureTestResults()) {

						sb.append("\n\t");
						sb.append(testResult.getDisplayName());
					}
				}
			}

			System.out.println(sb.toString());
		}

		List<Element> downstreamBuildMessageElements =
			getDownstreamBuildMessageElements(failedDownstreamBuilds);

		System.out.println(
			"Collected " + downstreamBuildMessageElements.size() +
				" downstream failure messages");

		List<Element> allCurrentBuildFailureElements = new ArrayList<>();
		List<Element> upstreamBuildFailureElements = new ArrayList<>();

		int maxFailureCount = 5;

		for (Build failedDownstreamBuild : failedDownstreamBuilds) {
			Element gitHubMessageElement =
				failedDownstreamBuild.getGitHubMessageElement();

			if (gitHubMessageElement == null) {
				continue;
			}

			if (failedDownstreamBuild.isUniqueFailure()) {
				allCurrentBuildFailureElements.add(gitHubMessageElement);
			}
			else {
				upstreamBuildFailureElements.add(gitHubMessageElement);
			}
		}

		List<Element> buildFailureElements = new ArrayList<>();

		buildFailureElements.add(Dom4JUtil.getNewElement("hr"));

		if (allCurrentBuildFailureElements.isEmpty() &&
			upstreamBuildFailureElements.isEmpty()) {

			allCurrentBuildFailureElements.add(
				0, super.getGitHubMessageElement());
		}

		if (allCurrentBuildFailureElements.isEmpty() &&
			!upstreamBuildFailureElements.isEmpty()) {

			String uniqueFailureMessage =
				"This pull contains no unique failures.";

			if (this instanceof PullRequestPortalTopLevelBuild) {
				PullRequestPortalTopLevelBuild pullRequestPortalTopLevelBuild =
					(PullRequestPortalTopLevelBuild)this;

				String stableJobResult =
					pullRequestPortalTopLevelBuild.getStableJobResult();

				if ((stableJobResult != null) &&
					!stableJobResult.equals("SUCCESS")) {

					uniqueFailureMessage = JenkinsResultsParserUtil.combine(
						"This pull contains no unique failures. However, the ",
						"stable suite failed.");
				}
			}

			buildFailureElements.add(
				Dom4JUtil.getNewElement("h4", null, uniqueFailureMessage));
		}
		else {
			String failureTitle = "Failures unique to this pull:";

			if (!UpstreamFailureUtil.isUpstreamComparisonAvailable(this) &&
				isCompareToUpstream()) {

				failureTitle =
					"Failures (upstream comparison is not available):";
			}

			buildFailureElements.add(
				Dom4JUtil.getNewElement("h4", null, failureTitle));

			buildFailureElements.add(
				Dom4JUtil.getOrderedListElement(
					allCurrentBuildFailureElements, maxFailureCount));
		}

		String acceptanceUpstreamJobURL = getAcceptanceUpstreamJobURL();

		if ((allCurrentBuildFailureElements.size() < maxFailureCount) &&
			!upstreamBuildFailureElements.isEmpty()) {

			Element acceptanceUpstreamJobLinkElement =
				Dom4JUtil.getNewAnchorElement(
					acceptanceUpstreamJobURL, "acceptance upstream results");

			Element upstreamJobFailureElement = Dom4JUtil.getNewElement(
				"details", null,
				Dom4JUtil.getNewElement(
					"summary", null,
					Dom4JUtil.getNewElement(
						"strong", null, "Failures in common with ",
						acceptanceUpstreamJobLinkElement, " at ",
						UpstreamFailureUtil.getUpstreamJobFailuresSHA(this),
						":")));

			int remainingFailureCount =
				maxFailureCount - allCurrentBuildFailureElements.size();

			Dom4JUtil.getOrderedListElement(
				upstreamBuildFailureElements, upstreamJobFailureElement,
				remainingFailureCount);

			buildFailureElements.add(Dom4JUtil.getNewElement("hr"));

			buildFailureElements.add(upstreamJobFailureElement);
		}

		String jobName = getJobName();

		if (jobName.contains("pullrequest") &&
			upstreamBuildFailureElements.isEmpty() &&
			(acceptanceUpstreamJobURL != null)) {

			Element upstreamResultElement = Dom4JUtil.getNewElement("h4");

			Dom4JUtil.addToElement(
				upstreamResultElement, "For upstream results, click ",
				Dom4JUtil.getNewAnchorElement(acceptanceUpstreamJobURL, "here"),
				".");

			buildFailureElements.add(upstreamResultElement);

			Map<String, String> startPropertiesTempMap =
				getStartPropertiesTempMap();

			String subrepositoryMergePullMentionUsers =
				startPropertiesTempMap.get(
					"SUBREPOSITORY_MERGE_PULL_MENTION_USERS");

			if (subrepositoryMergePullMentionUsers != null) {
				StringBuilder sb = new StringBuilder();

				sb.append("cc");

				for (String subrepositoryMergePullMentionUser :
						subrepositoryMergePullMentionUsers.split(",")) {

					sb.append(" @");
					sb.append(subrepositoryMergePullMentionUser);
				}

				buildFailureElements.add(
					Dom4JUtil.getNewElement("div", null, sb.toString()));
			}
		}

		return buildFailureElements.toArray(new Element[0]);
	}

	protected Element getDownstreamGitHubMessageElement() {
		String status = getStatus();

		if (!status.equals("completed") && (getParentBuild() != null)) {
			return null;
		}

		String result = getResult();

		if (Objects.equals(result, "SUCCESS")) {
			return null;
		}

		Element messageElement = Dom4JUtil.getNewElement(
			"div", null,
			Dom4JUtil.getNewAnchorElement(
				getBuildURL(), null, getDisplayName()));

		if (Objects.equals(result, "ABORTED")) {
			messageElement.add(
				Dom4JUtil.toCodeSnippetElement("Build was aborted"));
		}

		if (Objects.equals(result, "FAILURE")) {
			Element failureMessageElement = getFailureMessageElement();

			if (failureMessageElement != null) {
				messageElement.add(failureMessageElement);
			}
		}

		if (Objects.equals(result, "MISSING")) {
			messageElement.add(
				Dom4JUtil.toCodeSnippetElement("Build was missing"));
		}

		return messageElement;
	}

	@Override
	protected ExecutorService getExecutorService() {
		return _executorService;
	}

	protected Element getFailedJobSummaryElement() {
		Element jobSummaryListElement = getJobSummaryListElement(false, null);

		int failCount =
			getDownstreamBuildCount(null) -
				getDownstreamBuildCountByResult("SUCCESS") + 1;

		return Dom4JUtil.getNewElement(
			"div", null,
			Dom4JUtil.getNewElement(
				"h4", null, String.valueOf(failCount), " Failed Jobs:"),
			jobSummaryListElement);
	}

	@Override
	protected FailureMessageGenerator[] getFailureMessageGenerators() {
		return _FAILURE_MESSAGE_GENERATORS;
	}

	@Override
	protected Element getGitHubMessageJobResultsElement() {
		int successCount = getDownstreamBuildCountByResult("SUCCESS");

		int failCount = getDownstreamBuildCount(null) - successCount + 1;

		return Dom4JUtil.getNewElement(
			"div", null, Dom4JUtil.getNewElement("h6", null, "Job Results:"),
			Dom4JUtil.getNewElement(
				"p", null, String.valueOf(successCount),
				JenkinsResultsParserUtil.getNounForm(
					successCount, " Jobs", " Job"),
				" Passed.", Dom4JUtil.getNewElement("br"),
				String.valueOf(failCount),
				JenkinsResultsParserUtil.getNounForm(
					failCount, " Jobs", " Job"),
				" Failed."));
	}

	protected String getGitRepositoryDetailsPropertiesTempMapURL(
		String gitRepositoryType) {

		if (fromArchive) {
			return JenkinsResultsParserUtil.combine(
				getBuildURL(), "git.", gitRepositoryType, ".properties.json");
		}

		TopLevelBuild topLevelBuild = getTopLevelBuild();

		JenkinsMaster topLevelBuildJenkinsMaster =
			topLevelBuild.getJenkinsMaster();

		return JenkinsResultsParserUtil.combine(
			JenkinsResultsParserUtil.getJenkinsTempMapURL(), "/",
			topLevelBuildJenkinsMaster.getName(), "/",
			topLevelBuild.getJobName(), "/",
			String.valueOf(topLevelBuild.getBuildNumber()), "/",
			topLevelBuild.getJobName(), "/git.", gitRepositoryType,
			".properties");
	}

	protected Element getJenkinsReportBodyElement() {
		Element subheadingElement = null;

		JSONObject jobJSONObject = getBuildJSONObject();

		String description = jobJSONObject.optString("description");

		if (!description.isEmpty()) {
			subheadingElement = Dom4JUtil.getNewElement("h2");

			try {
				Dom4JUtil.addRawXMLToElement(subheadingElement, description);
			}
			catch (DocumentException documentException) {
				throw new RuntimeException(
					"Unable to parse description HTML " + description,
					documentException);
			}
		}

		String buildURL = getBuildURL();

		Element headingElement = Dom4JUtil.getNewElement(
			"h1", null, "Jenkins report for ",
			Dom4JUtil.getNewAnchorElement(buildURL, buildURL));

		return Dom4JUtil.getNewElement(
			"body", null, headingElement, subheadingElement,
			getJenkinsReportCommitElement(), getJenkinsReportSummaryElement(),
			getJenkinsReportTimelineElement(),
			getJenkinsReportTopLevelTableElement(),
			getJenkinsReportDownstreamElement());
	}

	@Override
	protected String getJenkinsReportBuildInfoCellElementTagName() {
		return "th";
	}

	protected Element getJenkinsReportChartJsScriptElement(
		String xData, String y1Data, String y2Data) {

		String resourceFileContent = null;

		try {
			resourceFileContent =
				JenkinsResultsParserUtil.getResourceFileContent(
					"dependencies/chart_template.js");
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to load resource chart_template.js", ioException);
		}

		resourceFileContent = resourceFileContent.replace("'xData'", xData);

		resourceFileContent = resourceFileContent.replace("'y1Data'", y1Data);

		resourceFileContent = resourceFileContent.replace("'y2Data'", y2Data);

		Element scriptElement = Dom4JUtil.getNewElement("script");

		scriptElement.addText(resourceFileContent);

		return scriptElement;
	}

	protected Element getJenkinsReportCommitElement() {
		if (!(this instanceof WorkspaceBuild)) {
			return null;
		}

		WorkspaceBuild workspaceBuild = (WorkspaceBuild)this;

		Workspace workspace = workspaceBuild.getWorkspace();

		WorkspaceGitRepository workspaceGitRepository =
			workspace.getPrimaryWorkspaceGitRepository();

		WorkspaceBranchInformation workspaceBranchInformation =
			new WorkspaceBranchInformation(workspaceGitRepository);

		String senderBranchSHA =
			workspaceBranchInformation.getSenderBranchSHA();

		GitHubRemoteGitCommit gitHubRemoteGitCommit = null;

		if (isReleaseBuild()) {
			gitHubRemoteGitCommit = GitCommitFactory.newGitHubRemoteGitCommit(
				workspaceBranchInformation.getSenderUsername(),
				getReleaseRepositoryName(), senderBranchSHA);
		}
		else {
			gitHubRemoteGitCommit = GitCommitFactory.newGitHubRemoteGitCommit(
				workspaceBranchInformation.getSenderUsername(),
				workspaceBranchInformation.getRepositoryName(),
				senderBranchSHA);
		}

		return Dom4JUtil.getNewElement(
			"div", null,
			Dom4JUtil.getNewElement(
				"p", null, "Sender Branch Name: ",
				workspaceBranchInformation.getSenderBranchName()),
			Dom4JUtil.getNewElement(
				"p", null, "Sender Branch SHA: ", senderBranchSHA),
			Dom4JUtil.getNewElement(
				"p", null, "Commit Message: ",
				gitHubRemoteGitCommit.getMessage()),
			Dom4JUtil.getNewElement(
				"p", null, "Commit Date: ",
				toJenkinsReportDateString(
					gitHubRemoteGitCommit.getCommitDate(),
					getJenkinsReportTimeZoneName())));
	}

	protected Element getJenkinsReportDownstreamElement() {
		return Dom4JUtil.getNewElement(
			"div", null,
			getJenkinsReportDownstreamTableElement(null, "queued", "Queued: "),
			getJenkinsReportDownstreamTableElement(
				null, "starting", "Starting: "),
			getJenkinsReportDownstreamTableElement(
				null, "running", "Running: "),
			getJenkinsReportDownstreamTableElement(
				null, "missing", "Missing: "),
			Dom4JUtil.getNewElement("h2", null, "Completed: "),
			getJenkinsReportDownstreamTableElement(
				"ABORTED", "completed", "---- Aborted: "),
			getJenkinsReportDownstreamTableElement(
				"FAILURE", "completed", "---- Failure: "),
			getJenkinsReportDownstreamTableElement(
				"MISSING", "completed", "---- Missing: "),
			getJenkinsReportDownstreamTableElement(
				"UNSTABLE", "completed", "---- Unstable: "),
			getJenkinsReportDownstreamTableElement(
				"SUCCESS", "completed", "---- Success: "));
	}

	protected Element getJenkinsReportDownstreamTableElement(
		String result, String status, String captionText) {

		List<Element> tableRowElements = getJenkinsReportTableRowElements(
			result, status);

		if (tableRowElements.isEmpty()) {
			return null;
		}

		return Dom4JUtil.getNewElement(
			"table", null,
			Dom4JUtil.getNewElement(
				"caption", null, captionText,
				String.valueOf(getDownstreamBuildCount(result, status))),
			getJenkinsReportTableColumnHeadersElement(),
			tableRowElements.toArray(new Element[0]));
	}

	protected Element getJenkinsReportHeadElement() {
		Element headElement = Dom4JUtil.getNewElement("head");

		getResourceFileContentAsElement(
			"style", headElement, "dependencies/jenkins_report.css");

		Element scriptElement = getResourceFileContentAsElement(
			"script", headElement, "dependencies/jenkins_report.js");

		scriptElement.addAttribute("language", "javascript");

		return headElement;
	}

	protected Element getJenkinsReportSummaryElement() {
		Element summaryElement = Dom4JUtil.getNewElement(
			"div", null,
			Dom4JUtil.getNewElement(
				"p", null,
				Dom4JUtil.getNewAnchorElement(
					_URL_CI_SYSTEM_STATUS, "CI System Status")),
			Dom4JUtil.getNewElement(
				"p", null, "Start Time: ",
				toJenkinsReportDateString(
					new Date(getStartTime()), getJenkinsReportTimeZoneName())),
			Dom4JUtil.getNewElement(
				"p", null, "Invocation Delay Time: ",
				JenkinsResultsParserUtil.toDurationString(
					getQueuingDuration())),
			Dom4JUtil.getNewElement(
				"p", null, "Build Time: ",
				JenkinsResultsParserUtil.toDurationString(getDuration())),
			Dom4JUtil.getNewElement(
				"p", null, "Total CPU Usage Time: ",
				JenkinsResultsParserUtil.toDurationString(getTotalDuration())),
			Dom4JUtil.getNewElement(
				"p", null, "Total number of Jenkins slaves used: ",
				String.valueOf(getTotalSlavesUsedCount())),
			Dom4JUtil.getNewElement(
				"p", null, "Total number of reinvocations: ",
				String.valueOf(_getTotalReinvocationCount())),
			Dom4JUtil.getNewElement(
				"p", null, "Average delay time for invoked build to start: ",
				JenkinsResultsParserUtil.toDurationString(
					getAverageDelayTime())));

		Build longestDelayedDownstreamBuild =
			getLongestDelayedDownstreamBuild();

		if (longestDelayedDownstreamBuild != null) {
			Dom4JUtil.getNewElement(
				"p", summaryElement,
				"Longest delay time for invoked build to start: ",
				Dom4JUtil.getNewAnchorElement(
					longestDelayedDownstreamBuild.getBuildURL(),
					longestDelayedDownstreamBuild.getDisplayName()),
				" in: ",
				JenkinsResultsParserUtil.toDurationString(
					longestDelayedDownstreamBuild.getDelayTime()));
		}

		Build longestRunningDownstreamBuild =
			getLongestRunningDownstreamBuild();

		if (longestRunningDownstreamBuild != null) {
			Dom4JUtil.getNewElement(
				"p", summaryElement, "Longest Running Downstream Build: ",
				Dom4JUtil.getNewAnchorElement(
					longestRunningDownstreamBuild.getBuildURL(),
					longestRunningDownstreamBuild.getDisplayName()),
				" in: ",
				JenkinsResultsParserUtil.toDurationString(
					longestRunningDownstreamBuild.getDuration()));
		}

		try {
			Properties buildProperties =
				JenkinsResultsParserUtil.getBuildProperties();

			String longestRunningTestEnabled = buildProperties.getProperty(
				"jenkins.report.longest.running.test.enabled", "false");

			if (longestRunningTestEnabled.equals("true")) {
				TestResult longestRunningTest = getLongestRunningTest();

				if (longestRunningTest != null) {
					Dom4JUtil.getNewElement(
						"p", summaryElement, "Longest Running Test: ",
						Dom4JUtil.getNewAnchorElement(
							longestRunningTest.getTestReportURL(),
							longestRunningTest.getDisplayName()),
						" in: ",
						JenkinsResultsParserUtil.toDurationString(
							longestRunningTest.getDuration()));
				}
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to get build properties", ioException);
		}

		return summaryElement;
	}

	protected Element getJenkinsReportTableColumnHeadersElement() {
		Element nameElement = Dom4JUtil.getNewElement("th", null, "Name");

		Element consoleElement = Dom4JUtil.getNewElement("th", null, "Console");

		Element testReportElement = Dom4JUtil.getNewElement(
			"th", null, "Test Report");

		Element startTimeElement = Dom4JUtil.getNewElement(
			"th", null, "Start Time");

		Element buildTimeElement = Dom4JUtil.getNewElement(
			"th", null, "Build Time");

		Element estimatedBuildTimeElement = null;
		Element diffBuildTimeElement = null;

		if (buildDurationsEnabled()) {
			estimatedBuildTimeElement = Dom4JUtil.getNewElement(
				"th", null, "Build Time (est)");
			diffBuildTimeElement = Dom4JUtil.getNewElement(
				"th", null, "Build Time (+/-)");
		}

		Element statusElement = Dom4JUtil.getNewElement("th", null, "Status");

		Element resultElement = Dom4JUtil.getNewElement("th", null, "Result");

		Element tableColumnHeaderElement = Dom4JUtil.getNewElement("tr");

		Dom4JUtil.addToElement(
			tableColumnHeaderElement, nameElement, consoleElement,
			testReportElement, startTimeElement, buildTimeElement,
			estimatedBuildTimeElement, diffBuildTimeElement, statusElement,
			resultElement);

		return tableColumnHeaderElement;
	}

	protected Element getJenkinsReportTimelineElement() {
		Element canvasElement = Dom4JUtil.getNewElement("canvas");

		canvasElement.addAttribute("height", "300");
		canvasElement.addAttribute("id", "timeline");

		Element scriptElement = Dom4JUtil.getNewElement("script");

		scriptElement.addAttribute("src", _URL_CHART_JS);
		scriptElement.addText("");

		TimelineData timelineData = getTimelineData();

		Element chartJSScriptElement = getJenkinsReportChartJsScriptElement(
			Arrays.toString(timelineData.getIndexData()),
			Arrays.toString(timelineData.getSlaveUsageData()),
			Arrays.toString(timelineData.getInvocationsData()));

		return Dom4JUtil.getNewElement(
			"div", null, canvasElement, scriptElement, chartJSScriptElement);
	}

	protected Element getJenkinsReportTopLevelTableElement() {
		Element topLevelTableElement = Dom4JUtil.getNewElement("table");

		String result = getResult();

		if (result != null) {
			Dom4JUtil.getNewElement(
				"caption", topLevelTableElement, "Top Level Build - ",
				Dom4JUtil.getNewElement("strong", null, getResult()));
		}
		else {
			Dom4JUtil.getNewElement(
				"caption", topLevelTableElement, "Top Level Build - ",
				Dom4JUtil.getNewElement(
					"strong", null, StringUtils.upperCase(getStatus())));
		}

		Dom4JUtil.addToElement(
			topLevelTableElement, getJenkinsReportTableColumnHeadersElement(),
			getJenkinsReportTableRowElement());

		List<Element> jenkinsReportStopWatchRecordElements =
			getJenkinsReportStopWatchRecordElements();

		Dom4JUtil.addToElement(
			topLevelTableElement,
			jenkinsReportStopWatchRecordElements.toArray());

		return topLevelTableElement;
	}

	protected Element getJobSummaryElement() {
		int successCount = getDownstreamBuildCountByResult("SUCCESS");

		String result = getResult();

		if (Objects.equals(result, "SUCCESS")) {
			successCount++;
		}

		Element detailsElement = Dom4JUtil.getNewElement(
			"details", null,
			Dom4JUtil.getNewElement(
				"summary", null,
				Dom4JUtil.getNewElement(
					"strong", null, "ci:test:", getTestSuiteName(), " - ",
					String.valueOf(successCount), " out of ",
					String.valueOf(getDownstreamBuildCount(null) + 1),
					" jobs PASSED")));

		if ((result != null) && !result.equals("SUCCESS")) {
			Dom4JUtil.addToElement(
				detailsElement, getFailedJobSummaryElement());
		}

		if (getDownstreamBuildCountByResult("SUCCESS") > 0) {
			Dom4JUtil.addToElement(
				detailsElement, getSuccessfulJobSummaryElement());
		}

		return detailsElement;
	}

	protected Element getJobSummaryListElement() {
		Element jobSummaryListElement = Dom4JUtil.getNewElement("ul");

		List<Build> builds = new ArrayList<>();

		builds.add(this);

		builds.addAll(getDownstreamBuilds(null));

		for (Build build : builds) {
			Element jobSummaryListItemElement = Dom4JUtil.getNewElement(
				"li", jobSummaryListElement);

			jobSummaryListItemElement.add(
				build.getGitHubMessageBuildAnchorElement());
		}

		return jobSummaryListElement;
	}

	protected Element getJobSummaryListElement(
		boolean success, List<String> jobVariants) {

		Element batchListElement = null;
		String batchName = null;

		List<Build> builds = new ArrayList<>();

		if (jobVariants != null) {
			builds.addAll(
				getJobVariantsDownstreamBuilds(jobVariants, null, null));
		}
		else {
			builds.add(this);

			builds.addAll(getDownstreamBuilds(null));
		}

		int count = 0;
		Element jobSummaryListElement = Dom4JUtil.getNewElement("ul");

		for (Build build : builds) {
			if (Objects.equals(getResult(), "SUCCESS") == success) {
				count++;

				if (count > _MAX_JOB_SUMMARY_LIST_SIZE) {
					Element jobSummaryListItemElement = Dom4JUtil.getNewElement(
						"li", jobSummaryListElement);

					jobSummaryListItemElement.addText("...");

					break;
				}

				if (build instanceof DownstreamBuild) {
					DownstreamBuild downstreamBuild = (DownstreamBuild)build;

					if (!Objects.equals(
							batchName, downstreamBuild.getBatchName())) {

						batchName = downstreamBuild.getBatchName();

						Element batchListItemElement = Dom4JUtil.getNewElement(
							"li", jobSummaryListElement);

						batchListItemElement.addText(batchName);

						batchListElement = Dom4JUtil.getNewElement(
							"ul", batchListItemElement);
					}

					Element batchListItemElement = Dom4JUtil.getNewElement(
						"li", batchListElement);

					batchListItemElement.add(
						build.getGitHubMessageBuildAnchorElement());

					continue;
				}

				Element jobSummaryListItemElement = Dom4JUtil.getNewElement(
					"li", jobSummaryListElement);

				jobSummaryListItemElement.add(
					build.getGitHubMessageBuildAnchorElement());
			}
		}

		return jobSummaryListElement;
	}

	protected Element getMoreDetailsElement() {
		return Dom4JUtil.getNewElement(
			"h5", null, "For more details click ",
			Dom4JUtil.getNewAnchorElement(getJenkinsReportURL(), "here"), ".");
	}

	protected Element getReevaluationDetailsElement(
		TopLevelBuildReport upstreamTopLevelBuildReport) {

		Element growURLElement = Dom4JUtil.getNewAnchorElement(
			"https://grow.liferay.com/share" +
				"/CI+liferay-continuous-integration+GitHub+Commands#" +
					"General-Commands",
			"reevaluation");

		String buildID = JenkinsResultsParserUtil.getBuildID(getBuildURL());

		Element preElement = Dom4JUtil.getNewElement(
			"pre", null, "ci:reevaluate:" + buildID);

		return Dom4JUtil.getNewElement(
			"p", null, "This pull is eligible for ", growURLElement,
			". When this ",
			Dom4JUtil.getNewAnchorElement(
				String.valueOf(upstreamTopLevelBuildReport.getBuildURL()),
				"upstream build"),
			" has completed, using the following CI command will compare ",
			"this pull request result against a more recent upstream result:",
			preElement);
	}

	protected String getReleaseRepositoryName() {
		if (!Objects.equals(getBranchName(), "master")) {
			return "liferay-portal-ee";
		}

		return "liferay-portal";
	}

	protected Element getResourceFileContentAsElement(
		String tagName, Element parentElement, String resourceName) {

		String resourceFileContent = null;

		try {
			resourceFileContent =
				JenkinsResultsParserUtil.getResourceFileContent(resourceName);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to load resource " + resourceName, ioException);
		}

		return Dom4JUtil.getNewElement(
			tagName, parentElement, resourceFileContent);
	}

	protected Element getResultElement() {
		StringBuilder sb = new StringBuilder();

		String result = getResult();

		int successCount = getDownstreamBuildCountByResult("SUCCESS");

		if ((result != null) && result.matches("(APPROVED|SUCCESS)")) {
			successCount++;

			sb.append(":heavy_check_mark: ");
		}
		else {
			sb.append(":x: ");
		}

		sb.append("ci:test:");
		sb.append(getTestSuiteName());
		sb.append(" - ");
		sb.append(String.valueOf(successCount));
		sb.append(" out of ");
		sb.append(String.valueOf(getDownstreamBuildCountByResult(null) + 1));
		sb.append(" jobs passed in ");
		sb.append(JenkinsResultsParserUtil.toDurationString(getDuration()));

		return Dom4JUtil.getNewElement("h3", null, sb.toString());
	}

	@Override
	protected String getStartPropertiesTempMapURL() {
		if (fromArchive) {
			return getBuildURL() + "/start.properties.json";
		}

		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		return JenkinsResultsParserUtil.combine(
			JenkinsResultsParserUtil.getJenkinsTempMapURL(), "/",
			jenkinsMaster.getName(), "/", getJobName(), "/",
			String.valueOf(getBuildNumber()), "/", getJobName(), "/",
			"start.properties");
	}

	@Override
	protected String getStopPropertiesTempMapURL() {
		if (fromArchive) {
			return getBuildURL() + "/stop.properties.json";
		}

		JenkinsMaster jenkinsMaster = getJenkinsMaster();

		return JenkinsResultsParserUtil.combine(
			JenkinsResultsParserUtil.getJenkinsTempMapURL(), "/",
			jenkinsMaster.getName(), "/", getJobName(), "/",
			String.valueOf(getBuildNumber()), "/", getJobName(), "/",
			"stop.properties");
	}

	protected Element getSuccessfulJobSummaryElement() {
		Element jobSummaryListElement = getJobSummaryListElement(true, null);

		int successCount = getDownstreamBuildCountByResult("SUCCESS");

		if (Objects.equals(getResult(), "SUCCESS")) {
			successCount++;
		}

		return Dom4JUtil.getNewElement(
			"details", null,
			Dom4JUtil.getNewElement(
				"summary", null,
				Dom4JUtil.getNewElement(
					"strong", null, String.valueOf(successCount),
					" Successful Jobs:")),
			jobSummaryListElement);
	}

	@Override
	protected String getTempMapURL(String tempMapName) {
		String tempMapURL = super.getTempMapURL(tempMapName);

		if (tempMapURL != null) {
			return tempMapURL;
		}

		Matcher matcher = gitRepositoryTempMapNamePattern.matcher(tempMapName);

		if (matcher.find()) {
			return getGitRepositoryDetailsPropertiesTempMapURL(
				matcher.group("gitRepositoryType"));
		}

		return null;
	}

	@Override
	protected int getTestCountByStatus(String status) {
		int testCount = 0;

		for (Build downstreamBuild : getDownstreamBuilds(null)) {
			if (!(downstreamBuild instanceof BaseBuild)) {
				continue;
			}

			BaseBuild downstreamBaseBuild = (BaseBuild)downstreamBuild;

			testCount += downstreamBaseBuild.getTestCountByStatus(status);
		}

		return testCount;
	}

	protected Element getTopGitHubMessageElement() {
		update();

		Element rootElement = Dom4JUtil.getNewElement("html");

		rootElement.add(getResultElement());

		Element detailsElement = Dom4JUtil.getNewElement(
			"details", rootElement,
			Dom4JUtil.getNewElement(
				"summary", null, "Click here for more details."));

		String result = getResult();

		if (isCompareToUpstream() &&
			UpstreamFailureUtil.isUpstreamComparisonAvailable(this)) {

			String upstreamBranchSHA = getUpstreamBranchSHA();

			TopLevelBuildReport upstreamTopLevelBuildReport =
				UpstreamFailureUtil.getUpstreamTopLevelBuildReport(
					this, upstreamBranchSHA);

			if ((upstreamTopLevelBuildReport != null) &&
				isEligibleForReevaluation(result, upstreamBranchSHA)) {

				Dom4JUtil.addToElement(
					detailsElement, Dom4JUtil.getNewElement("br"),
					getReevaluationDetailsElement(upstreamTopLevelBuildReport));
			}
		}

		Dom4JUtil.addToElement(
			detailsElement, Dom4JUtil.getNewElement("h4", null, "Base Branch:"),
			getBaseBranchDetailsElement());

		if (isCompareToUpstream() &&
			UpstreamFailureUtil.isUpstreamComparisonAvailable(this)) {

			Dom4JUtil.addToElement(
				detailsElement,
				Dom4JUtil.getNewElement("h4", null, "Upstream Comparison:"),
				getUpstreamComparisonDetailsElement());
		}

		Dom4JUtil.addToElement(
			detailsElement, getJobSummaryElement(), getMoreDetailsElement());

		if ((result != null) && !result.equals("SUCCESS")) {
			Dom4JUtil.addToElement(
				detailsElement, (Object[])getBuildFailureElements());
		}

		return rootElement;
	}

	protected String getUpstreamBranchSHA() {
		String upstreamBranchSHA = getParameterValue(
			"GITHUB_UPSTREAM_BRANCH_SHA");

		if ((upstreamBranchSHA == null) || upstreamBranchSHA.isEmpty()) {
			Map<String, String> startPropertiesTempMap =
				getStartPropertiesTempMap();

			upstreamBranchSHA = startPropertiesTempMap.get(
				"GITHUB_UPSTREAM_BRANCH_SHA");
		}

		return upstreamBranchSHA;
	}

	protected Element getUpstreamComparisonDetailsElement() {
		String upstreamJobFailuresSHA =
			UpstreamFailureUtil.getUpstreamJobFailuresSHA(this);

		String upstreamCommitURL =
			"https://github.com/liferay/" + getBaseGitRepositoryName() +
				"/commit/" + upstreamJobFailuresSHA;

		Element upstreamComparisonDetailsElement = Dom4JUtil.getNewElement(
			"p", null, "Branch GIT ID: ",
			Dom4JUtil.getNewAnchorElement(
				upstreamCommitURL, upstreamJobFailuresSHA));

		TestrayBuild testrayBuild = UpstreamFailureUtil.getUpstreamTestrayBuild(
			this);

		Dom4JUtil.addToElement(
			upstreamComparisonDetailsElement, Dom4JUtil.getNewElement("br"),
			"Jenkins Build URL: ",
			Dom4JUtil.getNewAnchorElement(
				String.valueOf(testrayBuild.getURL()), testrayBuild.getName()));

		return upstreamComparisonDetailsElement;
	}

	protected boolean isEligibleForReevaluation(
		String result, String upstreamBranchSHA) {

		if (JenkinsResultsParserUtil.isNullOrEmpty(upstreamBranchSHA)) {
			return false;
		}

		if ((result != null) && !result.matches("(APPROVED|SUCCESS)") &&
			hasDownstreamBuilds() &&
			!upstreamBranchSHA.equals(
				UpstreamFailureUtil.getUpstreamJobFailuresSHA(this))) {

			return true;
		}

		return false;
	}

	protected boolean isReleaseBuild() {
		return false;
	}

	protected void sendBuildMetrics(String message) {
		if (_sendBuildMetrics) {
			DatagramRequestUtil.send(
				message.trim(), _metricsHostName, _metricsHostPort);
		}
	}

	protected void sendBuildMetricsOnModifiedBuilds() {
		StringBuilder sb = new StringBuilder();

		Map<Map<String, String>, Integer> slaveUsages =
			_getSlaveUsageByLabels();

		for (Map.Entry<Map<String, String>, Integer> slaveUsageEntry :
				slaveUsages.entrySet()) {

			Map<String, String> metricLabels = slaveUsageEntry.getKey();
			Integer slaveUsage = slaveUsageEntry.getValue();

			String buildMetricMessage =
				StatsDMetricsUtil.generateGaugeDeltaMetric(
					"build_slave_usage_gauge", slaveUsage, metricLabels);

			if (buildMetricMessage != null) {
				sb.append(buildMetricMessage);
				sb.append("\n");
			}
		}

		if (sb.length() > 0) {
			sendBuildMetrics(sb.toString());
		}

		sendBuildMetricsOnModifiedCompletedBuilds();
	}

	protected void sendBuildMetricsOnModifiedCompletedBuilds() {
		List<Build> modifiedCompletedBuilds =
			getModifiedDownstreamBuildsByStatus("completed");

		for (Build modifiedCompletedBuild : modifiedCompletedBuilds) {
			if (modifiedCompletedBuild instanceof BatchBuild) {
				continue;
			}

			sendBuildMetrics(
				StatsDMetricsUtil.generateTimerMetric(
					"jenkins_job_build_duration",
					modifiedCompletedBuild.getDuration(),
					modifiedCompletedBuild.getMetricLabels()));
		}
	}

	protected static final Pattern gitRepositoryTempMapNamePattern =
		Pattern.compile("git\\.(?<gitRepositoryType>.*)\\.properties");

	private void _archiveBuildDatabase() {
		String status = getStatus();

		String urlSuffix = "build-database.json";

		File archiveFile = getArchiveFile(urlSuffix);

		if (!status.equals("completed")) {
			if (archiveFile.exists()) {
				JenkinsResultsParserUtil.delete(archiveFile);
			}

			return;
		}

		if (archiveFile.exists()) {
			return;
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		BuildDatabase buildDatabase = getBuildDatabase();

		try {
			writeArchiveFile(
				String.valueOf(buildDatabase.getJSONObject()),
				getArchivePath() + "/" + urlSuffix);
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to archive " + urlSuffix, ioException);
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

	private void _archiveJenkinsReport() {
		String status = getStatus();

		String urlSuffix = "jenkins-report.html";

		File archiveFile = getArchiveFile(urlSuffix);

		if (!status.equals("completed")) {
			if (archiveFile.exists()) {
				JenkinsResultsParserUtil.delete(archiveFile);
			}

			return;
		}

		if (archiveFile.exists()) {
			return;
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		File jenkinsReportFile = new File(getBuildDirPath(), urlSuffix);

		try {
			if (jenkinsReportFile.exists()) {
				JenkinsResultsParserUtil.copy(jenkinsReportFile, archiveFile);

				return;
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to copy the Jenkins report", ioException);
		}
		finally {
			if (JenkinsResultsParserUtil.debug) {
				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Archived ", String.valueOf(archiveFile), " in ",
						JenkinsResultsParserUtil.toDurationString(
							JenkinsResultsParserUtil.getCurrentTimeMillis() -
								start)));
			}
		}

		try {
			writeArchiveFile(
				getJenkinsReport(), getArchivePath() + "/jenkins-report.html");
		}
		catch (Exception exception) {
			System.out.println("Unable to archive Jenkins report");
		}
		finally {
			if (JenkinsResultsParserUtil.debug) {
				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Archived ", String.valueOf(archiveFile), " in ",
						JenkinsResultsParserUtil.toDurationString(
							JenkinsResultsParserUtil.getCurrentTimeMillis() -
								start)));
			}
		}
	}

	private void _archiveProperties() {
		String status = getStatus();

		File archiveFile = new File(
			getArchiveRootDir(), getArchiveName() + "/archive.properties");

		if (!status.equals("completed")) {
			if (archiveFile.exists()) {
				JenkinsResultsParserUtil.delete(archiveFile);
			}

			return;
		}

		if (archiveFile.exists()) {
			return;
		}

		long start = JenkinsResultsParserUtil.getCurrentTimeMillis();

		Properties archiveProperties = new Properties();

		archiveProperties.setProperty(
			"top.level.build.url", replaceBuildURL(getBuildURL()));

		StringWriter sw = new StringWriter();

		try {
			archiveProperties.store(sw, null);

			JenkinsResultsParserUtil.write(archiveFile, sw.toString());
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to archive properties", ioException);
		}
		finally {
			if (JenkinsResultsParserUtil.debug) {
				System.out.println(
					JenkinsResultsParserUtil.combine(
						"Archived ", String.valueOf(archiveFile), " in ",
						JenkinsResultsParserUtil.toDurationString(
							JenkinsResultsParserUtil.getCurrentTimeMillis() -
								start)));
			}
		}
	}

	private void _findDownstreamBuildsInConsoleText() {
		if ((getBuildURL() == null) || (getParentBuild() != null)) {
			return;
		}

		String consoleText = getConsoleText();

		if (JenkinsResultsParserUtil.isNullOrEmpty(consoleText)) {
			return;
		}

		Set<String> downstreamBuildURLs = new HashSet<>();

		for (Build downstreamBuild : getDownstreamBuilds(null)) {
			String downstreamBuildURL = downstreamBuild.getBuildURL();

			if (downstreamBuildURL != null) {
				downstreamBuildURLs.add(downstreamBuildURL);
			}

			List<String> downstreamBadBuildURLs =
				downstreamBuild.getBadBuildURLs();

			if (downstreamBadBuildURLs != null) {
				downstreamBuildURLs.addAll(downstreamBadBuildURLs);
			}
		}

		Map<String, String> urlAxisNames = new HashMap<>();

		int i = consoleText.lastIndexOf("\nstop-current-job:");

		if (i != -1) {
			consoleText = consoleText.substring(0, i);
		}

		Matcher downstreamBuildURLMatcher = _downstreamBuildURLPattern.matcher(
			consoleText.substring(consoleReadCursor));

		consoleReadCursor = consoleText.length();

		while (downstreamBuildURLMatcher.find()) {
			String url = downstreamBuildURLMatcher.group("url");

			Pattern reinvocationPattern = Pattern.compile(
				Pattern.quote(url) + " restarted at (?<url>[^\\s]*)\\.");

			Matcher reinvocationMatcher = reinvocationPattern.matcher(
				consoleText);

			while (reinvocationMatcher.find()) {
				url = reinvocationMatcher.group("url");
			}

			if (downstreamBuildURLs.contains(url) ||
				urlAxisNames.containsKey(url)) {

				continue;
			}

			String jobVariant = downstreamBuildURLMatcher.group("jobVariant");

			if (!JenkinsResultsParserUtil.isNullOrEmpty(jobVariant)) {
				String jobName = downstreamBuildURLMatcher.group("jobName");

				if (!JenkinsResultsParserUtil.isNullOrEmpty(jobName) &&
					jobVariant.contains(jobName + "/")) {

					jobVariant = jobVariant.replaceAll(jobName + "/", "");
				}
			}

			urlAxisNames.put(url, jobVariant);
		}

		addDownstreamBuilds(urlAxisNames);
	}

	private Map<Map<String, String>, Integer> _getSlaveUsageByLabels() {
		Map<Map<String, String>, Integer> slaveUsages = new HashMap<>();

		List<Build> modifiedDownstreamBuilds = getModifiedDownstreamBuilds();

		for (Build modifiedDownstreamBuild : modifiedDownstreamBuilds) {
			Map<String, String> metricLabels =
				modifiedDownstreamBuild.getMetricLabels();

			Integer slaveUsage = slaveUsages.get(metricLabels);

			if (slaveUsage == null) {
				slaveUsage = 0;
			}

			if (modifiedDownstreamBuild instanceof ParentBuild) {
				ParentBuild parentBuild = (ParentBuild)modifiedDownstreamBuild;

				slaveUsage += parentBuild.getTotalSlavesUsedCount(
					"running", true);
				slaveUsage -= parentBuild.getTotalSlavesUsedCount(
					"completed", true);
			}

			slaveUsages.put(metricLabels, slaveUsage);
		}

		return slaveUsages;
	}

	private String _getStatusSummary() {
		return JenkinsResultsParserUtil.combine(
			String.valueOf(getDownstreamBuildCount("starting")), " Starting / ",
			String.valueOf(getDownstreamBuildCount("missing")), " Missing / ",
			String.valueOf(getDownstreamBuildCount("queued")), " Queued / ",
			String.valueOf(getDownstreamBuildCount("running")), " Running / ",
			String.valueOf(getDownstreamBuildCount("reporting")),
			" Reporting / ",
			String.valueOf(getDownstreamBuildCount("completed")),
			" Completed / ", String.valueOf(getDownstreamBuildCount(null)),
			" Total ");
	}

	private int _getTotalReinvocationCount() {
		BuildDatabase buildDatabase = getBuildDatabase();

		Properties properties = buildDatabase.getProperties(
			BAD_BUILD_URLS_PROPERTIES_KEY);

		int totalReinvocationCount = 0;

		for (String propertyName : properties.stringPropertyNames()) {
			String badBuildURLsString = properties.getProperty(propertyName);

			if (!badBuildURLsString.isEmpty()) {
				String[] badBuildURLs = badBuildURLsString.split(",");

				totalReinvocationCount += badBuildURLs.length;
			}
		}

		return totalReinvocationCount;
	}

	private static final FailureMessageGenerator[] _FAILURE_MESSAGE_GENERATORS =
		{
			new CITestSuiteValidationFailureMessageGenerator(),
			new CompileFailureMessageGenerator(),
			new FormatFailureMessageGenerator(),
			new GitLPushFailureMessageGenerator(),
			new JenkinsRegenFailureMessageGenerator(),
			new JenkinsSourceFormatFailureMessageGenerator(),
			new InvalidGitCommitSHAFailureMessageGenerator(),
			new InvalidSenderSHAFailureMessageGenerator(),
			new RebaseFailureMessageGenerator(),
			new RelevantRuleValidationFailureMessageGenerator(),
			//
			new PoshiValidationFailureMessageGenerator(),
			new PoshiTestFailureMessageGenerator(),
			//
			new GradleTaskFailureMessageGenerator(),
			//
			new DownstreamFailureMessageGenerator(),
			//
			new CIFailureMessageGenerator(),
			//
			new GenericFailureMessageGenerator()
		};

	private static final int _MAX_JOB_SUMMARY_LIST_SIZE = 500;

	private static final long _MILLIS_DOWNSTREAM_BUILDS_LISTING_INTERVAL =
		1000 * 60 * 5;

	private static final String _URL_CHART_JS =
		"https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.5.0/Chart.min.js";

	private static final String _URL_CI_SYSTEM_STATUS =
		"http://test-1-0.liferay.com/userContent/reports/ci-system-status" +
			"/index.html";

	private static final Pattern _downstreamBuildURLPattern = Pattern.compile(
		"[\\'\\\"](?<jobVariant>[^\\'\\\"]+)[\\'\\\"] (completed|started) at " +
			"(?<url>.+/job/(?<jobName>[^/]+)/.+)\\.");
	private static final ExecutorService _executorService =
		JenkinsResultsParserUtil.getNewThreadPoolExecutor(10, true);
	private static final Pattern _gitHubURLPattern = Pattern.compile(
		"https://github.com/(?<username>[^/]+)/[^/]/pull/" +
			"(?<pullNumber>\\d+)");

	private boolean _compareToUpstream;
	private Build _controllerBuild;
	private final Map<String, AxisBuild> _downstreamAxisBuilds =
		new ConcurrentHashMap<>();
	private boolean _downstreamAxisBuildsPopulated;
	private final Map<String, BatchBuild> _downstreamBatchBuilds =
		new ConcurrentHashMap<>();
	private boolean _downstreamBatchBuildsPopulated;
	private JenkinsCohort _jenkinsCohort;
	private long _lastDownstreamBuildsListingTimestamp = -1L;
	private String _metricsHostName;
	private int _metricsHostPort;
	private final boolean _sendBuildMetrics;
	private final List<URL> _testrayAttachmentURLs = new ArrayList<>();
	private TopLevelBuildReport _topLevelBuildReport;

}