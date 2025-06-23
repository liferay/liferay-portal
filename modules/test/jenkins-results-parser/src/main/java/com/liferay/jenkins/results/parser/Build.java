/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.List;
import java.util.Map;

import org.dom4j.Element;

import org.json.JSONObject;

/**
 * @author Kevin Yen
 */
public interface Build {

	public static final String DEPENDENCIES_URL_TOKEN = "${dependencies.url}";

	public void addInvocation(Invocation invocation);

	public void addTimelineData(TimelineData timelineData);

	public void archive();

	public void archive(String archiveName);

	public String getArchiveName();

	public String getArchivePath();

	public File getArchiveRootDir();

	public URL getArtifactsBaseURL();

	public List<String> getBadBuildURLs();

	public String getBaseGitRepositoryName();

	public String getBaseGitRepositorySHA(String gitRepositoryName);

	public String getBatchName(String jobVariant);

	public String getBranchName();

	public BuildDatabase getBuildDatabase();

	public String getBuildDescription();

	public String getBuildDirPath();

	public JSONObject getBuildJSONObject();

	public JSONObject getBuildJSONObject(String tree);

	public String getBuildName();

	public int getBuildNumber();

	public Job.BuildProfile getBuildProfile();

	public JSONObject getBuildReportJSONObject();

	public String getBuildURL();

	public String getBuildURLRegex();

	public String getConsoleText();

	public Invocation getCurrentInvocation();

	public Long getDelayTime();

	public int getDepth();

	public String getDisplayName();

	public long getDuration();

	public String getFailureMessage();

	public Element getGitHubMessageBuildAnchorElement();

	public Element getGitHubMessageElement();

	public Element getGitHubMessageUpstreamJobFailureElement();

	public Map<String, String> getInjectedEnvironmentVariablesMap()
		throws IOException;

	public String getInvocationURL();

	public int getInvokedBatchSize();

	public Long getInvokedTime();

	public JenkinsCohort getJenkinsCohort();

	public JenkinsMaster getJenkinsMaster();

	public JenkinsSlave getJenkinsSlave();

	public Job getJob();

	public String getJobName();

	public String getJobURL();

	public String getJobVariant();

	public TestResult getLongestRunningTest();

	public int getMaximumSlavesPerHost();

	public Map<String, String> getMetricLabels();

	public int getMinimumSlaveRAM();

	public Map<String, String> getParameters();

	public String getParameterValue(String name);

	public Build getParentBuild();

	public Invocation getPreviousInvocation();

	public String getResult();

	public Map<String, String> getStartPropertiesTempMap();

	public Long getStartTime();

	public String getStatus();

	public long getStatusAge();

	public long getStatusDuration(String status);

	public Map<String, String> getStopPropertiesTempMap();

	public StopWatchRecordsGroup getStopWatchRecordsGroup();

	public TestClassResult getTestClassResult(String testClassName);

	public List<TestClassResult> getTestClassResults();

	public List<URL> getTestrayAttachmentURLs();

	public String getTestrayBuildDateString();

	public JSONObject getTestReportJSONObject(boolean checkCache);

	public List<TestResult> getTestResults();

	public List<TestResult> getTestResults(String testStatus);

	public String getTestSuiteName();

	public TopLevelBuild getTopLevelBuild();

	public List<TestResult> getUniqueFailureTestResults();

	public List<TestResult> getUpstreamJobFailureTestResults();

	public boolean hasBuildURL(String buildURL);

	public boolean hasDownstreamBuilds();

	public boolean hasGenericCIFailure();

	public boolean hasMaximumInvocationCount();

	public boolean isBuildModified();

	public boolean isCompareToUpstream();

	public boolean isCompleted();

	public boolean isFailing();

	public boolean isFromArchive();

	public boolean isFromCompletedBuild();

	public boolean isUniqueFailure();

	public String replaceBuildURL(String text);

	public void reset();

	public void saveBuildURLInBuildDatabase();

	public void setArchiveName(String archiveName);

	public void setArchiveRootDir(File archiveRootDir);

	public void setBuildURL(String buildURL);

	public void setCompareToUpstream(boolean compareToUpstream);

	public void setJenkinsCohort(JenkinsCohort jenkinsCohort);

	public void setJenkinsMaster(JenkinsMaster jenkinsMaster);

	public void setResult(String result);

	public void setStatus(String status);

	public void takeSlaveOffline(SlaveOfflineRule slaveOfflineRule);

	public void update();

	public interface BranchInformation {

		public String getCachedRemoteGitRefName();

		public String getOriginName();

		public Integer getPullRequestNumber();

		public String getReceiverUsername();

		public String getRepositoryName();

		public String getSenderBranchName();

		public String getSenderBranchSHA();

		public String getSenderBranchSHAShort();

		public RemoteGitRef getSenderRemoteGitRef();

		public String getSenderUsername();

		public String getUpstreamBranchName();

		public String getUpstreamBranchSHA();

	}

	public class Invocation {

		public Invocation(Build build) {
			_build = build;
		}

		public Invocation(Build build, JenkinsMaster jenkinsMaster) {
			_build = build;
			_jenkinsMaster = jenkinsMaster;
		}

		public Invocation(
			Build build, JenkinsMaster jenkinsMaster, long queueId) {

			_build = build;
			_jenkinsMaster = jenkinsMaster;
			_queueId = queueId;
		}

		public String getBuildURL() {
			if (JenkinsResultsParserUtil.isURL(_buildURL)) {
				return _buildURL;
			}

			_buildURL = JenkinsResultsParserUtil.getBuildURL(
				_build.getJobName(), getJenkinsMaster(), getQueueId());

			return _buildURL;
		}

		public JenkinsMaster getJenkinsMaster() {
			return _jenkinsMaster;
		}

		public long getQueueId() {
			return _queueId;
		}

		public ReinvokeRule getReinvokeRule() {
			return _reinvokeRule;
		}

		public void setBuildURL(String buildURL) {
			_buildURL = buildURL;
		}

		public void setJenkinsMaster(JenkinsMaster jenkinsMaster) {
			_jenkinsMaster = jenkinsMaster;
		}

		public void setQueueId(long queueId) {
			_queueId = queueId;
		}

		public void setReinvokeRule(ReinvokeRule reinvokeRule) {
			_reinvokeRule = reinvokeRule;
		}

		private final Build _build;
		private String _buildURL;
		private JenkinsMaster _jenkinsMaster;
		private long _queueId;
		private ReinvokeRule _reinvokeRule;

	}

	public class TimelineData {

		protected TimelineData(int size, TopLevelBuild topLevelBuild) {
			if (topLevelBuild != topLevelBuild.getTopLevelBuild()) {
				throw new IllegalArgumentException(
					"Nested top level builds are invalid");
			}

			if (size < 1) {
				throw new IllegalArgumentException("Invalid size " + size);
			}

			_duration = topLevelBuild.getDuration();
			_startTime = topLevelBuild.getStartTime();

			_timeline = new TimelineDataPoint[size];

			for (int i = 0; i < size; i++) {
				_timeline[i] = new TimelineDataPoint(
					(int)(i * (_duration / _timeline.length)));
			}

			topLevelBuild.addTimelineData(this);
		}

		protected void addTimelineData(BaseBuild build) {
			Long buildInvokedTime = build.getInvokedTime();

			if (buildInvokedTime == null) {
				return;
			}

			_timeline[_getIndex(buildInvokedTime)]._invocationsCount++;

			Long buildStartTime = build.getStartTime();

			if (buildStartTime == null) {
				return;
			}

			int endIndex = _getIndex(buildStartTime + build.getDuration());
			int startIndex = _getIndex(buildStartTime);

			for (int i = startIndex; i <= endIndex; i++) {
				_timeline[i]._slaveUsageCount++;
			}
		}

		protected int[] getIndexData() {
			int[] indexes = new int[_timeline.length];

			for (int i = 0; i < _timeline.length; i++) {
				indexes[i] = _timeline[i]._index;
			}

			return indexes;
		}

		protected int[] getInvocationsData() {
			int[] invocationsData = new int[_timeline.length];

			for (int i = 0; i < _timeline.length; i++) {
				invocationsData[i] = _timeline[i]._invocationsCount;
			}

			return invocationsData;
		}

		protected int[] getSlaveUsageData() {
			int[] slaveUsageData = new int[_timeline.length];

			for (int i = 0; i < _timeline.length; i++) {
				slaveUsageData[i] = _timeline[i]._slaveUsageCount;
			}

			return slaveUsageData;
		}

		private int _getIndex(long timestamp) {
			int index =
				(int)((timestamp - _startTime) * _timeline.length / _duration);

			if (index >= _timeline.length) {
				return _timeline.length - 1;
			}

			if (index < 0) {
				return 0;
			}

			return index;
		}

		private final long _duration;
		private final long _startTime;
		private final TimelineDataPoint[] _timeline;

		private static class TimelineDataPoint {

			private TimelineDataPoint(int index) {
				_index = index;
			}

			private final int _index;
			private int _invocationsCount;
			private int _slaveUsageCount;

		}

	}

}