/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Element;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseParentBuild extends BaseBuild implements ParentBuild {

	@Override
	public void addCachedDownstreamBuild(Build build) {
		if ((build == null) || _downstreamBuilds.contains(build)) {
			return;
		}

		build.setBuildCached(true);

		build.saveBuildURLInBuildDatabase();

		addDownstreamBuild(build);
	}

	@Override
	public void addDownstreamBuild(Build build) {
		if (build == null) {
			return;
		}

		if (_downstreamBuilds == null) {
			getDownstreamBuilds();
		}

		if (build.isBuildCached() && _downstreamBuilds.contains(build)) {
			return;
		}

		_downstreamBuilds.add(build);
	}

	@Override
	public void addDownstreamBuilds(Map<String, String> urlAxisNames) {
		if (urlAxisNames.isEmpty()) {
			return;
		}

		final Build thisBuild = this;

		List<Callable<Build>> callables = new ArrayList<>(urlAxisNames.size());

		for (Map.Entry<String, String> urlEntry : urlAxisNames.entrySet()) {
			String url = urlEntry.getKey();

			try {
				url = JenkinsResultsParserUtil.getLocalURL(
					JenkinsResultsParserUtil.decode(url));
			}
			catch (UnsupportedEncodingException unsupportedEncodingException) {
				throw new IllegalArgumentException(
					"Unable to decode " + url, unsupportedEncodingException);
			}

			if (!hasBuildURL(url)) {
				final String axisName = urlEntry.getValue();

				final String buildURL = url;

				Matcher matcher = _buildURLPattern.matcher(buildURL);

				String hostname = null;

				if (matcher.matches()) {
					hostname = matcher.group("hostname");
				}

				ParallelExecutor.SequentialCallable<Build> callable =
					new ParallelExecutor.SequentialCallable<Build>(hostname) {

						@Override
						public Build call() {
							try {
								return BuildFactory.newBuild(
									buildURL, axisName, thisBuild);
							}
							catch (RuntimeException runtimeException) {
								if (!isFromArchive()) {
									String shortStackTrace =
										JenkinsResultsParserUtil.getStackTrace(
											runtimeException, 10);

									System.out.println(
										"WARNING: Build object failure:\n" +
											shortStackTrace);

									NotificationUtil.sendSlackNotification(
										JenkinsResultsParserUtil.combine(
											shortStackTrace, "\nBuild URL: ",
											thisBuild.getBuildURL()),
										"ci-notifications",
										"Build object failure");
								}

								return null;
							}
						}

					};

				callables.add(callable);
			}
		}

		ParallelExecutor<Build> parallelExecutor = new ParallelExecutor<>(
			callables, true, getExecutorService(), "addDownstreamBuilds");

		try {
			addDownstreamBuilds(parallelExecutor.execute(60L * 30L));
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}
	}

	@Override
	public void addDownstreamBuilds(String... urls) {
		Map<String, String> urlAxisNames = new HashMap<>();

		for (String url : urls) {
			urlAxisNames.put(url, null);
		}

		addDownstreamBuilds(urlAxisNames);
	}

	@Override
	public int getDownstreamBuildCount(String status) {
		return getDownstreamBuildCount(null, status);
	}

	@Override
	public int getDownstreamBuildCount(String result, String status) {
		List<Build> downstreamBuilds = getDownstreamBuilds(result, status);

		return downstreamBuilds.size();
	}

	@Override
	public List<Build> getDownstreamBuilds() {
		if (_downstreamBuilds != null) {
			return new ArrayList<>(_downstreamBuilds);
		}

		_downstreamBuilds = new ArrayList<>();

		return new ArrayList<>(_downstreamBuilds);
	}

	@Override
	public List<Build> getDownstreamBuilds(String status) {
		return getDownstreamBuilds(null, status);
	}

	@Override
	public List<Build> getDownstreamBuilds(String result, String status) {
		List<Build> filteredDownstreamBuilds = Collections.synchronizedList(
			new ArrayList<Build>());

		List<Build> downstreamBuilds = getDownstreamBuilds();

		if ((result == null) && (status == null)) {
			filteredDownstreamBuilds.addAll(downstreamBuilds);

			return filteredDownstreamBuilds;
		}

		for (Build downstreamBuild : downstreamBuilds) {
			if (((status == null) ||
				 status.equals(downstreamBuild.getStatus())) &&
				((result == null) ||
				 result.equals(downstreamBuild.getResult()))) {

				filteredDownstreamBuilds.add(downstreamBuild);
			}
		}

		return filteredDownstreamBuilds;
	}

	@Override
	public Long getLatestStartTimestamp() {
		Long latestStartTimestamp = getStartTime();

		if (latestStartTimestamp == null) {
			return null;
		}

		for (Build downstreamBuild : getDownstreamBuilds(null)) {
			Long downstreamBuildLatestStartTimestamp = null;

			if (downstreamBuild instanceof ParentBuild) {
				ParentBuild parentBuild = (ParentBuild)downstreamBuild;

				downstreamBuildLatestStartTimestamp =
					parentBuild.getLatestStartTimestamp();
			}
			else {
				downstreamBuildLatestStartTimestamp =
					downstreamBuild.getStartTime();
			}

			if (downstreamBuildLatestStartTimestamp == null) {
				return null;
			}

			latestStartTimestamp = Math.max(
				latestStartTimestamp, downstreamBuildLatestStartTimestamp);
		}

		return latestStartTimestamp;
	}

	@Override
	public Build getLongestDelayedDownstreamBuild() {
		List<Build> downstreamBuilds = getDownstreamBuilds(null);

		if (downstreamBuilds.isEmpty()) {
			return this;
		}

		Build longestDelayedBuild = downstreamBuilds.get(0);

		for (Build downstreamBuild : downstreamBuilds) {
			Build longestDelayedDownstreamBuild = downstreamBuild;

			if (downstreamBuild instanceof ParentBuild) {
				ParentBuild parentBuild = (ParentBuild)downstreamBuild;

				longestDelayedDownstreamBuild =
					parentBuild.getLongestDelayedDownstreamBuild();
			}

			if (downstreamBuild.getDelayTime() >
					longestDelayedDownstreamBuild.getDelayTime()) {

				longestDelayedDownstreamBuild = downstreamBuild;
			}

			if (longestDelayedDownstreamBuild.getDelayTime() >
					longestDelayedBuild.getDelayTime()) {

				longestDelayedBuild = longestDelayedDownstreamBuild;
			}
		}

		return longestDelayedBuild;
	}

	@Override
	public Build getLongestRunningDownstreamBuild() {
		Build longestRunningDownstreamBuild = null;

		for (Build downstreamBuild : getDownstreamBuilds(null)) {
			if ((longestRunningDownstreamBuild == null) ||
				(downstreamBuild.getDuration() >
					longestRunningDownstreamBuild.getDuration())) {

				longestRunningDownstreamBuild = downstreamBuild;
			}
		}

		return longestRunningDownstreamBuild;
	}

	@Override
	public List<Build> getModifiedDownstreamBuilds() {
		return getModifiedDownstreamBuildsByStatus(null);
	}

	@Override
	public List<Build> getModifiedDownstreamBuildsByStatus(String status) {
		List<Build> modifiedDownstreamBuilds = new ArrayList<>();

		for (Build downstreamBuild : getDownstreamBuilds()) {
			if (downstreamBuild.isBuildModified()) {
				modifiedDownstreamBuilds.add(downstreamBuild);

				continue;
			}

			if (!(downstreamBuild instanceof ParentBuild)) {
				continue;
			}

			ParentBuild parentBuild = (ParentBuild)downstreamBuild;

			if (parentBuild.hasModifiedDownstreamBuilds()) {
				modifiedDownstreamBuilds.add(parentBuild);
			}
		}

		if (status != null) {
			modifiedDownstreamBuilds.retainAll(getDownstreamBuilds(status));
		}

		return modifiedDownstreamBuilds;
	}

	@Override
	public List<TestResult> getTestResults(String testStatus) {
		List<TestResult> testResults = new ArrayList<>();

		for (Build downstreamBuild : getDownstreamBuilds(null)) {
			List<TestResult> downstreamTestResults =
				downstreamBuild.getTestResults(testStatus);

			if (!(downstreamTestResults == null)) {
				testResults.addAll(downstreamTestResults);
			}
		}

		return testResults;
	}

	@Override
	public long getTotalDuration() {
		long totalDuration = getDuration();

		for (Build downstreamBuild :
				JenkinsResultsParserUtil.flatten(getDownstreamBuilds(null))) {

			totalDuration += downstreamBuild.getDuration();
		}

		return totalDuration;
	}

	@Override
	public int getTotalSlavesUsedCount() {
		return getTotalSlavesUsedCount(null, false);
	}

	@Override
	public int getTotalSlavesUsedCount(
		String status, boolean modifiedBuildsOnly) {

		return getTotalSlavesUsedCount(status, modifiedBuildsOnly, false);
	}

	@Override
	public int getTotalSlavesUsedCount(
		String status, boolean modifiedBuildsOnly, boolean ignoreCurrentBuild) {

		int totalSlavesUsedCount = 1;

		if (ignoreCurrentBuild || (modifiedBuildsOnly && !isBuildModified()) ||
			((status != null) && !status.equals(getStatus()))) {

			totalSlavesUsedCount = 0;
		}

		List<Build> downstreamBuilds;

		if (modifiedBuildsOnly) {
			downstreamBuilds = getModifiedDownstreamBuildsByStatus(status);
		}
		else {
			downstreamBuilds = getDownstreamBuilds(status);
		}

		return totalSlavesUsedCount + downstreamBuilds.size();
	}

	@Override
	public List<TestResult> getUniqueFailureTestResults() {
		List<TestResult> uniqueFailureTestResults = new ArrayList<>();

		for (Build downstreamBuild : getFailedDownstreamBuilds()) {
			uniqueFailureTestResults.addAll(
				downstreamBuild.getUniqueFailureTestResults());
		}

		return uniqueFailureTestResults;
	}

	@Override
	public List<TestResult> getUpstreamJobFailureTestResults() {
		List<TestResult> upstreamFailureTestResults = new ArrayList<>();

		for (Build downstreamBuild : getFailedDownstreamBuilds()) {
			upstreamFailureTestResults.addAll(
				downstreamBuild.getUpstreamJobFailureTestResults());
		}

		return upstreamFailureTestResults;
	}

	@Override
	public boolean hasBuildURL(String buildURL) {
		if (super.hasBuildURL(buildURL)) {
			return true;
		}

		for (Build downstreamBuild : getDownstreamBuilds()) {
			if (downstreamBuild.hasBuildURL(buildURL)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean hasDownstreamBuilds() {
		if (getDownstreamBuildCount(null, null) > 0) {
			return true;
		}

		return false;
	}

	@Override
	public boolean hasModifiedDownstreamBuilds() {
		for (Build downstreamBuild : getDownstreamBuilds()) {
			if (downstreamBuild.isBuildModified()) {
				return true;
			}

			if (!(downstreamBuild instanceof ParentBuild)) {
				continue;
			}

			ParentBuild parentBuild = (ParentBuild)downstreamBuild;

			if (parentBuild.hasModifiedDownstreamBuilds()) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void removeDownstreamBuild(Build build) {
		if (_downstreamBuilds == null) {
			getDownstreamBuilds();
		}

		_downstreamBuilds.remove(build);
	}

	@Override
	public String replaceBuildURL(String text) {
		if (JenkinsResultsParserUtil.isNullOrEmpty(text)) {
			return text;
		}

		text = super.replaceBuildURL(text);

		for (Build downstreamBuild : getDownstreamBuilds("complete")) {
			Build downstreamBaseBuild = downstreamBuild;

			text = downstreamBaseBuild.replaceBuildURL(text);
		}

		return super.replaceBuildURL(text);
	}

	@Override
	public void reset() {
		super.reset();

		if (_downstreamBuilds != null) {
			_downstreamBuilds.clear();
		}
	}

	@Override
	public void update() {
		if (skipUpdate()) {
			return;
		}

		int buildThreadSpawnFrequency = 0;

		try {
			buildThreadSpawnFrequency = Integer.parseInt(
				JenkinsResultsParserUtil.getBuildProperty(
					"build.thread.spawn.frequency"));
		}
		catch (IOException ioException) {
			throw new RuntimeException(
				"Unable to parse property \"build.thread.spawn.frequency\"",
				ioException);
		}

		Map<String, Integer> callableGroupCounter = new HashMap<>();
		List<Callable<Object>> callables = new ArrayList<>();

		for (final Build downstreamBuild : getDownstreamBuilds(null)) {
			String status = downstreamBuild.getStatus();

			if (status.equals("completed")) {
				continue;
			}

			JenkinsMaster jenkinsMaster = downstreamBuild.getJenkinsMaster();

			String jenkinsMasterName = jenkinsMaster.getName();

			Integer buildCounter = callableGroupCounter.computeIfAbsent(
				jenkinsMasterName, key -> 0);

			String sequentialCallableGroupName = jenkinsMasterName;

			try {
				if (buildCounter >= buildThreadSpawnFrequency) {
					int groupNumber = buildCounter / buildThreadSpawnFrequency;

					sequentialCallableGroupName =
						JenkinsResultsParserUtil.combine(
							sequentialCallableGroupName, "_",
							String.valueOf(groupNumber));
				}

				callableGroupCounter.put(
					sequentialCallableGroupName, buildCounter + 1);

				ParallelExecutor.SequentialCallable<Object> callable =
					new ParallelExecutor.SequentialCallable<Object>(
						sequentialCallableGroupName) {

						@Override
						public Object call() {
							downstreamBuild.update();

							return null;
						}

					};

				callables.add(callable);
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		List<List<Callable<Object>>> callablesList = Lists.partition(
			callables, _getInvokedGroupSize());

		for (int i = 0; i < callablesList.size(); i++) {
			ParallelExecutor<Object> parallelExecutor = new ParallelExecutor<>(
				callablesList.get(i), getExecutorService(), "update-" + i);

			try {
				long buildUpdateTimeout = 60 * 90;

				String buildUpdateTimeoutString =
					JenkinsResultsParserUtil.getBuildProperty(
						"build.update.timeout", getBranchName(), getJobName(),
						getTestSuiteName());

				if (JenkinsResultsParserUtil.isInteger(
						buildUpdateTimeoutString)) {

					buildUpdateTimeout = Long.parseLong(
						buildUpdateTimeoutString);
				}
				else if (Objects.equals(getJobName(), "test-portal-release")) {
					buildUpdateTimeout = 60 * 240;
				}

				parallelExecutor.execute(buildUpdateTimeout);
			}
			catch (IOException | TimeoutException exception) {
				throw new RuntimeException(exception);
			}
		}

		findDownstreamBuilds();

		super.update();
	}

	protected BaseParentBuild(String buildURL) {
		super(buildURL);
	}

	protected BaseParentBuild(String buildURL, Build parentBuild) {
		super(buildURL, parentBuild);
	}

	protected void addDownstreamBuilds(Collection<Build> builds) {
		if (builds == null) {
			return;
		}

		for (Build build : builds) {
			addDownstreamBuild(build);
		}
	}

	protected void addDownstreamBuildsTimelineData(TimelineData timelineData) {
		for (Build downstreamBuild : getDownstreamBuilds(null)) {
			downstreamBuild.addTimelineData(timelineData);
		}
	}

	protected abstract void findDownstreamBuilds();

	@Override
	protected List<Callable<Object>> getArchiveCallables() {
		List<Callable<Object>> archiveCallables = super.getArchiveCallables();

		List<Build> downstreamBuilds = getDownstreamBuilds();

		if ((downstreamBuilds != null) && !downstreamBuilds.isEmpty()) {
			for (Build downstreamBuild : downstreamBuilds) {
				if (downstreamBuild instanceof BaseBuild) {
					BaseBuild downstreamBaseBuild = (BaseBuild)downstreamBuild;

					archiveCallables.addAll(
						downstreamBaseBuild.getArchiveCallables());
				}
			}
		}

		return archiveCallables;
	}

	protected int getDownstreamBuildCountByResult(String result) {
		List<Build> downstreamBuilds = getDownstreamBuilds(null);

		if (result == null) {
			return downstreamBuilds.size();
		}

		int count = 0;

		for (Build downstreamBuild : downstreamBuilds) {
			String downstreamBuildResult = downstreamBuild.getResult();

			if (Objects.equals(downstreamBuildResult, result)) {
				count++;
			}
		}

		return count;
	}

	protected List<Element> getDownstreamBuildMessageElements(
		List<Build> downstreamBuilds) {

		List<Callable<Element>> callables = new ArrayList<>();

		for (final Build downstreamBuild : downstreamBuilds) {
			JenkinsMaster jenkinsMaster = downstreamBuild.getJenkinsMaster();

			ParallelExecutor.SequentialCallable<Element> callable =
				new ParallelExecutor.SequentialCallable<Element>(
					jenkinsMaster.getName()) {

					public Element call() {
						return downstreamBuild.getGitHubMessageElement();
					}

				};

			callables.add(callable);
		}

		ParallelExecutor<Element> parallelExecutor = new ParallelExecutor<>(
			callables, getExecutorService(), "getDownstreamBuildMessages");

		try {
			return parallelExecutor.execute();
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}
	}

	protected List<Build> getFailedDownstreamBuilds() {
		List<Build> failedDownstreamBuilds = new ArrayList<>();

		failedDownstreamBuilds.addAll(getDownstreamBuilds("ABORTED", null));
		failedDownstreamBuilds.addAll(getDownstreamBuilds("MISSING", null));
		failedDownstreamBuilds.addAll(getDownstreamBuilds("FAILURE", null));
		failedDownstreamBuilds.addAll(getDownstreamBuilds("UNSTABLE", null));

		return failedDownstreamBuilds;
	}

	@Override
	protected List<Element> getJenkinsReportTableRowElements(
		String result, String status) {

		List<Element> tableRowElements = super.getJenkinsReportTableRowElements(
			result, status);

		List<Build> builds = getDownstreamBuilds(result, status);

		Collections.sort(builds, new BaseBuild.BuildDisplayNameComparator());

		String batchName = null;

		for (Build build : builds) {
			if (!(build instanceof BaseBuild)) {
				continue;
			}

			if (build instanceof DownstreamBuild) {
				DownstreamBuild downstreamBuild = (DownstreamBuild)build;

				String downstreamBatchName = downstreamBuild.getBatchName();

				if (!Objects.equals(batchName, downstreamBatchName)) {
					tableRowElements.add(
						Dom4JUtil.getNewElement(
							"th", null, downstreamBatchName));

					batchName = downstreamBatchName;
				}
			}

			BaseBuild baseBuild = (BaseBuild)build;

			tableRowElements.addAll(
				baseBuild.getJenkinsReportTableRowElements(result, status));
		}

		return tableRowElements;
	}

	@Override
	protected boolean skipUpdate() {
		boolean skipUpdate = super.skipUpdate();

		if (!skipUpdate || hasModifiedDownstreamBuilds()) {
			return false;
		}

		return true;
	}

	protected void sortDownstreamBuilds() {
		Collections.sort(
			_downstreamBuilds, new BaseBuild.BuildDisplayNameComparator());
	}

	private int _getInvokedGroupSize() {
		try {
			String invokedGroupSize = JenkinsResultsParserUtil.getBuildProperty(
				"test.batch.invoked.group.size");

			if (JenkinsResultsParserUtil.isInteger(invokedGroupSize)) {
				return Integer.parseInt(invokedGroupSize);
			}
		}
		catch (IOException ioException) {
			return _INVOKED_GROUP_SIZE_DEFAULT;
		}

		return _INVOKED_GROUP_SIZE_DEFAULT;
	}

	private static final int _INVOKED_GROUP_SIZE_DEFAULT = 500;

	private static final Pattern _buildURLPattern = Pattern.compile(
		"http[s]?\\:\\/\\/(?<hostname>[^\\/]+)\\/.*");

	private List<Build> _downstreamBuilds;

}