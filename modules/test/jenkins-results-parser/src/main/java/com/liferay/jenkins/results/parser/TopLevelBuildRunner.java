/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.google.common.collect.Lists;

import com.liferay.jenkins.results.parser.job.property.JobProperty;
import com.liferay.jenkins.results.parser.job.property.JobPropertyFactory;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import org.dom4j.Element;

/**
 * @author Michael Hashimoto
 */
public abstract class TopLevelBuildRunner<T extends TopLevelBuildData>
	extends BaseBuildRunner<T> {

	@Override
	public void run() {
		publishJenkinsReport();

		updateBuildDescription();

		setUpWorkspace();

		validateBuildParameters();

		prepareInvocationBuildDataList();

		if (JenkinsResultsParserUtil.isCloudCINode()) {
			BuildDatabase buildDatabase = BuildDatabaseUtil.getBuildDatabase();

			TopLevelBuildData topLevelBuildData = getBuildData();

			buildDatabase.uploadBuildDatabaseFileToCloudBucket(
				topLevelBuildData.getS3BucketDistPath() + "/" +
					BuildDatabase.FILE_NAME_BUILD_DATABASE_JSON);
		}
		else {
			propagateBuildDatabaseToDistNodes();
		}

		invokeDownstreamBuilds();

		propagateBuildDatabaseToUserContent();

		waitForDownstreamBuildsToComplete();

		publishJenkinsReport();
	}

	@Override
	public void tearDown() {
		super.tearDown();

		publishJenkinsReport();
	}

	protected TopLevelBuildRunner(T topLevelBuildData) {
		super(topLevelBuildData);

		Build build = BuildFactory.newBuild(
			topLevelBuildData.getBuildURL(), null);

		if (!(build instanceof TopLevelBuild)) {
			throw new RuntimeException(
				"Invalid build URL " + topLevelBuildData.getBuildURL());
		}

		_topLevelBuild = (TopLevelBuild)build;

		topLevelBuildData.setInvocationTime(topLevelBuildData.getStartTime());
	}

	protected void addInvocationBuildData(BuildData buildData) {
		_downstreamBuildDataList.add(buildData);
	}

	protected void failBuildRunner(String message) {
		failBuildRunner(message, null);
	}

	protected void failBuildRunner(String message, Exception exception) {
		TopLevelBuildData topLevelBuildData = getBuildData();

		topLevelBuildData.setBuildDescription("<b>ERROR:</b> " + message);

		updateBuildDescription();

		if (exception == null) {
			throw new RuntimeException(message);
		}

		throw new RuntimeException(message, exception);
	}

	protected String getBaseInvocationURL(String cohortName, String jobName) {
		try {
			String baseInvocationURL =
				JenkinsResultsParserUtil.getBuildProperty(
					"jenkins.osb.jenkins.web.master.url", jobName);

			if (!JenkinsResultsParserUtil.isNullOrEmpty(baseInvocationURL)) {
				return baseInvocationURL;
			}
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		return JenkinsResultsParserUtil.getMostAvailableMasterURL(
			"http://" + cohortName + ".liferay.com", null, 1, jobName);
	}

	protected String getBuildParameter(String key) {
		TopLevelBuildData topLevelBuildData = getBuildData();

		return topLevelBuildData.getBuildParameter(key);
	}

	protected Element getJenkinsReportElement() {
		return _topLevelBuild.getJenkinsReportElement();
	}

	protected String getJobPropertyValue(String key) {
		JobProperty jobProperty = JobPropertyFactory.newJobProperty(
			key, getJob());

		return jobProperty.getValue();
	}

	protected TopLevelBuild getTopLevelBuild() {
		return _topLevelBuild;
	}

	protected void invokeDownstreamBuilds() {
		for (BuildData downstreamBuildData : _downstreamBuildDataList) {
			_invokeDownstreamBuild(downstreamBuildData);
		}
	}

	protected abstract void prepareInvocationBuildDataList();

	protected void propagateBuildDatabaseToDistNodes() {
		if (!JenkinsResultsParserUtil.isCINode()) {
			return;
		}

		TopLevelBuildData topLevelBuildData = getBuildData();

		BuildDatabase buildDatabase = BuildDatabaseUtil.getBuildDatabase();

		FilePropagator filePropagator = buildDatabase.rsyncBuildDatabaseFile(
			topLevelBuildData.getDistNodes(), topLevelBuildData.getDistPath(),
			_COMMAND_FILE_PROPAGATOR_PRE_DIST_COMMAND, null,
			_THREADS_FILE_PROPAGATOR_THREAD_SIZE);

		List<String> distNodes = Lists.newArrayList(
			topLevelBuildData.getDistNodes());

		distNodes.removeAll(filePropagator.getErrorSlaves());

		topLevelBuildData.setDistNodes(distNodes);
	}

	protected void propagateBuildDatabaseToUserContent() {
		if (!JenkinsResultsParserUtil.isCINode()) {
			return;
		}

		BuildData buildData = getBuildData();

		BuildDatabase buildDatabase = BuildDatabaseUtil.getBuildDatabase();

		buildDatabase.rsyncBuildDatabaseFileToJenkinsMaster(
			"/opt/java/jenkins/userContent/" +
				buildData.getUserContentRelativePath(),
			JenkinsMaster.getInstance(buildData.getTopLevelMasterHostname()));
	}

	protected void publishJenkinsReport() {
		_updateBuildData();

		try {
			String jenkinsReportString = StringEscapeUtils.unescapeXml(
				Dom4JUtil.format(getJenkinsReportElement(), true));

			TopLevelBuildData topLevelBuildData = getBuildData();

			File jenkinsReportFile = new File(
				topLevelBuildData.getWorkspaceDir(), "jenkins-report.html");

			JenkinsResultsParserUtil.write(
				jenkinsReportFile, jenkinsReportString);

			publishToUserContentDir(jenkinsReportFile);
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}
	}

	@Override
	protected void setUpWorkspace() {
		Workspace workspace = getWorkspace();

		workspace.setUp();

		if (!JenkinsResultsParserUtil.isCloudCINode()) {
			workspace.synchronizeToGitHubDev();
		}
	}

	protected void updateJenkinsReport() {
		if (!_allBuildsAreRunning()) {
			_lastGeneratedReportTime = -1;

			return;
		}

		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		if (_lastGeneratedReportTime == -1) {
			_lastGeneratedReportTime = currentTimeMillis;

			publishJenkinsReport();

			return;
		}

		if ((_lastGeneratedReportTime + _MILLIS_REPORT_GENERATION_INTERVAL) >
				currentTimeMillis) {

			return;
		}

		_lastGeneratedReportTime = currentTimeMillis;

		publishJenkinsReport();
	}

	protected abstract void validateBuildParameters();

	protected void waitForDownstreamBuildsToComplete() {
		while (true) {
			_topLevelBuild.update();

			updateJenkinsReport();

			System.out.println(_topLevelBuild.getStatusSummary());

			int completed = _topLevelBuild.getDownstreamBuildCount("completed");
			int total = _topLevelBuild.getDownstreamBuildCount(null);

			if (completed >= total) {
				break;
			}

			JenkinsResultsParserUtil.sleep(
				_SECONDS_WAIT_FOR_INVOKED_JOB_DURATION * 1000);
		}
	}

	private boolean _allBuildsAreRunning() {
		List<Build> runningBuilds = new ArrayList<>();

		runningBuilds.addAll(_topLevelBuild.getDownstreamBuilds("running"));
		runningBuilds.addAll(_topLevelBuild.getDownstreamBuilds("completed"));

		List<Build> totalBuilds = _topLevelBuild.getDownstreamBuilds(null);

		if (runningBuilds.size() >= totalBuilds.size()) {
			return true;
		}

		return false;
	}

	private BuildData _getDownstreamBuildData(Build downstreamBuild) {
		if (_downstreamBuildDataList.isEmpty()) {
			return null;
		}

		String buildURL = downstreamBuild.getBuildURL();

		if (buildURL == null) {
			return null;
		}

		String runId = downstreamBuild.getParameterValue("RUN_ID");

		for (BuildData downstreamBuildData : _downstreamBuildDataList) {
			if (runId.equals(downstreamBuildData.getRunId())) {
				return downstreamBuildData;
			}
		}

		return null;
	}

	private void _invokeBuild(
		String cohortName, String jobName,
		Map<String, String> invocationParameters) {

		Properties buildProperties;

		try {
			buildProperties = JenkinsResultsParserUtil.getBuildProperties();
		}
		catch (IOException ioException) {
			throw new RuntimeException(ioException);
		}

		invocationParameters.put(
			"PARENT_BUILD_URL", _topLevelBuild.getBuildURL());

		StringBuilder sb = new StringBuilder();

		sb.append(getBaseInvocationURL(cohortName, jobName));
		sb.append("/job/");
		sb.append(jobName);
		sb.append("/buildWithParameters?token=");
		sb.append(buildProperties.getProperty("jenkins.authentication.token"));

		for (Map.Entry<String, String> invocationParameter :
				invocationParameters.entrySet()) {

			sb.append("&");
			sb.append(
				JenkinsResultsParserUtil.fixURL(invocationParameter.getKey()));
			sb.append("=");
			sb.append(
				JenkinsResultsParserUtil.fixURL(
					invocationParameter.getValue()));
		}

		_topLevelBuild.addDownstreamBuilds(sb.toString());
	}

	private void _invokeDownstreamBuild(BuildData buildData) {
		TopLevelBuildData topLevelBuildData = getBuildData();

		topLevelBuildData.addDownstreamBuildData(buildData);

		Map<String, String> invocationParameters = new HashMap<>();

		invocationParameters.put(
			"DIST_NODES",
			StringUtils.join(topLevelBuildData.getDistNodes(), ","));
		invocationParameters.put("DIST_PATH", topLevelBuildData.getDistPath());
		invocationParameters.put(
			"JENKINS_GITHUB_URL", topLevelBuildData.getJenkinsGitHubURL());
		invocationParameters.put("RUN_ID", buildData.getRunId());
		invocationParameters.put(
			"S3_BUCKET_DIST_PATH", topLevelBuildData.getS3BucketDistPath());
		invocationParameters.put(
			"TOP_LEVEL_RUN_ID", topLevelBuildData.getRunId());

		buildData.setInvocationTime(
			JenkinsResultsParserUtil.getCurrentTimeMillis());

		_invokeBuild(
			topLevelBuildData.getCohortName(), buildData.getJobName(),
			invocationParameters);
	}

	private void _updateBuildData() {
		TopLevelBuildData topLevelBuildData = getBuildData();

		topLevelBuildData.setBuildDuration(
			JenkinsResultsParserUtil.getCurrentTimeMillis() -
				topLevelBuildData.getStartTime());
		topLevelBuildData.setBuildResult(_topLevelBuild.getResult());
		topLevelBuildData.setBuildStatus(_topLevelBuild.getStatus());

		for (Build downstreamBuild : _topLevelBuild.getDownstreamBuilds(null)) {
			String buildURL = downstreamBuild.getBuildURL();

			if (buildURL == null) {
				continue;
			}

			BuildData downstreamBuildData = _getDownstreamBuildData(
				downstreamBuild);

			if (downstreamBuildData == null) {
				continue;
			}

			downstreamBuildData.setBuildDuration(downstreamBuild.getDuration());
			downstreamBuildData.setBuildResult(downstreamBuild.getResult());
			downstreamBuildData.setBuildStatus(downstreamBuild.getStatus());
			downstreamBuildData.setBuildURL(buildURL);
		}
	}

	private static final String _COMMAND_FILE_PROPAGATOR_PRE_DIST_COMMAND =
		JenkinsResultsParserUtil.combine(
			"find ", JenkinsResultsParserUtil.getJenkinsDistRootPath(),
			"/*/* -maxdepth 1 -type d -mmin +",
			String.valueOf(
				TopLevelBuildRunner._MILLIS_FILE_PROPAGATOR_EXPIRATION),
			" -exec rm -frv {} \\;");

	private static final int _MILLIS_FILE_PROPAGATOR_EXPIRATION = 1440;

	private static final long _MILLIS_REPORT_GENERATION_INTERVAL =
		1000 * 60 * 5;

	private static final int _SECONDS_WAIT_FOR_INVOKED_JOB_DURATION = 30;

	private static final int _THREADS_FILE_PROPAGATOR_THREAD_SIZE = 1;

	private final List<BuildData> _downstreamBuildDataList = new ArrayList<>();
	private long _lastGeneratedReportTime = -1;
	private final TopLevelBuild _topLevelBuild;

}