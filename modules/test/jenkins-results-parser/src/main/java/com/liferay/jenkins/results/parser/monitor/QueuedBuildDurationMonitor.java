/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.monitor;

import com.liferay.jenkins.results.parser.JenkinsCohort;
import com.liferay.jenkins.results.parser.JenkinsMaster;
import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;
import com.liferay.jenkins.results.parser.ParallelExecutor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * @author Brittney Nguyen
 */
public class QueuedBuildDurationMonitor extends BaseMonitor {

	public QueuedBuildDurationMonitor(MonitorConfig monitorConfig) {
		super(monitorConfig);

		_buildStartGapMaximumSeconds = getLongValue(
			"threshold", 60 * 60, "build.start.gap.maximum",
			monitorConfig.getThresholds());
		_cohortName = getRequiredParameter(
			"cohort.name", monitorConfig.getParameters());
		_excludedJenkinsMasterNames = _getExcludedJenkinsMasterNames(
			monitorConfig.getParameters());
		_queueDurationMaximumSeconds = getLongValue(
			"threshold", 3 * 60 * 60, "queue.duration.maximum",
			monitorConfig.getThresholds());
	}

	@Override
	public MonitorResult execute() {
		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		List<JenkinsMaster> jenkinsMasters = _getJenkinsMasters();

		if (jenkinsMasters.isEmpty()) {
			return new MonitorResult(
				"Unable to find a master in cohort " + _cohortName, null,
				MonitorResult.Status.CRITICAL, currentTimeMillis);
		}

		Map<String, String> queueExceptionMessages = new ConcurrentHashMap<>();
		Map<String, String> runningBuildExceptionMessages =
			new ConcurrentHashMap<>();

		List<List<JenkinsMaster.QueueItem>> queueItemsList = _readQueueItems(
			queueExceptionMessages, jenkinsMasters);
		List<List<JenkinsMaster.RunningBuild>> runningBuildsList =
			_readRunningBuilds(runningBuildExceptionMessages, jenkinsMasters);

		List<String> messages = new ArrayList<>();
		int queuedBuildsCount = 0;
		List<MonitorResult.Status> statuses = new ArrayList<>();
		List<JenkinsMaster.QueueItem> stuckQueueItems = new ArrayList<>();

		for (int i = 0; i < jenkinsMasters.size(); i++) {
			JenkinsMaster jenkinsMaster = jenkinsMasters.get(i);

			List<JenkinsMaster.QueueItem> queueItems = queueItemsList.get(i);
			List<JenkinsMaster.RunningBuild> runningBuilds =
				runningBuildsList.get(i);

			if (((queueItems == null) || (runningBuilds == null)) &&
				jenkinsMaster.isBlacklisted()) {

				continue;
			}

			if (queueItems == null) {
				messages.add(
					JenkinsResultsParserUtil.combine(
						"Unable to read the queue on ",
						jenkinsMaster.getRemoteURL(), ": ",
						queueExceptionMessages.get(jenkinsMaster.getName())));

				statuses.add(MonitorResult.Status.CRITICAL);
			}

			if (runningBuilds == null) {
				messages.add(
					JenkinsResultsParserUtil.combine(
						"Unable to read running builds on ",
						jenkinsMaster.getRemoteURL(), ": ",
						runningBuildExceptionMessages.get(
							jenkinsMaster.getName())));

				statuses.add(MonitorResult.Status.CRITICAL);
			}

			if ((queueItems == null) || (runningBuilds == null)) {
				continue;
			}

			List<JenkinsMaster.QueueItem> longQueueItems = new ArrayList<>();

			for (JenkinsMaster.QueueItem queueItem : queueItems) {
				if (queueItem.isBlocked()) {
					continue;
				}

				queuedBuildsCount++;

				if ((currentTimeMillis - queueItem.getInQueueSince()) <=
						(_queueDurationMaximumSeconds * 1000)) {

					continue;
				}

				longQueueItems.add(queueItem);
			}

			if (longQueueItems.isEmpty() ||
				_hasRecentBuildStart(currentTimeMillis, runningBuilds)) {

				continue;
			}

			stuckQueueItems.addAll(longQueueItems);

			statuses.add(MonitorResult.Status.CRITICAL);
		}

		Collections.sort(stuckQueueItems);

		if (!stuckQueueItems.isEmpty()) {
			messages.add(
				0,
				_getStuckQueueItemsMessage(currentTimeMillis, stuckQueueItems));
		}

		Map<String, String> metrics = new LinkedHashMap<>();

		metrics.put("queued.builds.count", String.valueOf(queuedBuildsCount));
		metrics.put(
			"stuck.queued.builds.count",
			String.valueOf(stuckQueueItems.size()));

		if (statuses.isEmpty()) {
			return new MonitorResult(
				JenkinsResultsParserUtil.combine(
					"No master in cohort ", _cohortName,
					" has queued builds stuck longer than ",
					JenkinsResultsParserUtil.toDurationString(
						_queueDurationMaximumSeconds * 1000)),
				metrics, MonitorResult.Status.OK, currentTimeMillis);
		}

		return new MonitorResult(
			JenkinsResultsParserUtil.join(". ", messages), metrics,
			MonitorResult.Status.getMostSevere(statuses), currentTimeMillis);
	}

	@Override
	protected int getReadsCount() {
		return 2;
	}

	private <T> List<T> _execute(
		List<Callable<T>> callables, String name, int size) {

		ParallelExecutor<T> parallelExecutor = new ParallelExecutor<>(
			callables,
			JenkinsResultsParserUtil.getNewThreadPoolExecutor(size, true),
			name);

		try {
			return parallelExecutor.execute();
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}
	}

	private Set<String> _getExcludedJenkinsMasterNames(
		Map<String, String> parameters) {

		Set<String> excludedJenkinsMasterNames = new HashSet<>();

		String excludedMasterNames = parameters.get("excluded.master.names");

		if (JenkinsResultsParserUtil.isNullOrEmpty(excludedMasterNames)) {
			return excludedJenkinsMasterNames;
		}

		for (String excludedMasterName : excludedMasterNames.split(",")) {
			excludedJenkinsMasterNames.add(excludedMasterName.trim());
		}

		return excludedJenkinsMasterNames;
	}

	private List<JenkinsMaster> _getJenkinsMasters() {
		Map<String, JenkinsMaster> jenkinsMastersMap = new TreeMap<>();

		JenkinsCohort jenkinsCohort = JenkinsCohort.getInstance(_cohortName);

		for (JenkinsMaster jenkinsMaster : jenkinsCohort.getJenkinsMasters()) {
			jenkinsMastersMap.put(jenkinsMaster.getName(), jenkinsMaster);
		}

		for (JenkinsMaster jenkinsMaster :
				jenkinsCohort.getBlacklistedJenkinsMasters()) {

			jenkinsMastersMap.put(jenkinsMaster.getName(), jenkinsMaster);
		}

		for (String excludedJenkinsMasterName : _excludedJenkinsMasterNames) {
			jenkinsMastersMap.remove(excludedJenkinsMasterName);
		}

		return new ArrayList<>(jenkinsMastersMap.values());
	}

	private String _getStuckQueueItemsMessage(
		long currentTimeMillis, List<JenkinsMaster.QueueItem> stuckQueueItems) {

		List<String> stuckQueueItemMessages = new ArrayList<>();

		if (stuckQueueItems.size() <= _STUCK_QUEUE_ITEMS_SIZE_MAX) {
			for (JenkinsMaster.QueueItem stuckQueueItem : stuckQueueItems) {
				JenkinsMaster jenkinsMaster = stuckQueueItem.getJenkinsMaster();

				stuckQueueItemMessages.add(
					JenkinsResultsParserUtil.combine(
						stuckQueueItem.getTaskName(), " on ",
						jenkinsMaster.getRemoteURL(), " (",
						JenkinsResultsParserUtil.toDurationString(
							currentTimeMillis -
								stuckQueueItem.getInQueueSince()),
						")"));
			}
		}
		else {
			Map<String, List<JenkinsMaster.QueueItem>> stuckQueueItemsMap =
				new LinkedHashMap<>();

			for (JenkinsMaster.QueueItem stuckQueueItem : stuckQueueItems) {
				JenkinsMaster jenkinsMaster = stuckQueueItem.getJenkinsMaster();

				List<JenkinsMaster.QueueItem> jenkinsMasterStuckQueueItems =
					stuckQueueItemsMap.get(jenkinsMaster.getName());

				if (jenkinsMasterStuckQueueItems == null) {
					jenkinsMasterStuckQueueItems = new ArrayList<>();

					stuckQueueItemsMap.put(
						jenkinsMaster.getName(), jenkinsMasterStuckQueueItems);
				}

				jenkinsMasterStuckQueueItems.add(stuckQueueItem);
			}

			for (List<JenkinsMaster.QueueItem> jenkinsMasterStuckQueueItems :
					stuckQueueItemsMap.values()) {

				JenkinsMaster.QueueItem oldestStuckQueueItem =
					jenkinsMasterStuckQueueItems.get(0);

				JenkinsMaster jenkinsMaster =
					oldestStuckQueueItem.getJenkinsMaster();

				stuckQueueItemMessages.add(
					JenkinsResultsParserUtil.combine(
						String.valueOf(jenkinsMasterStuckQueueItems.size()),
						" on ", jenkinsMaster.getRemoteURL(), " (oldest ",
						JenkinsResultsParserUtil.toDurationString(
							currentTimeMillis -
								oldestStuckQueueItem.getInQueueSince()),
						")"));
			}
		}

		return JenkinsResultsParserUtil.combine(
			String.valueOf(stuckQueueItems.size()), " ",
			JenkinsResultsParserUtil.getNounForm(
				stuckQueueItems.size(), "builds", "build"),
			" in cohort ", _cohortName, " queued over ",
			JenkinsResultsParserUtil.toDurationString(
				_queueDurationMaximumSeconds * 1000),
			": ", JenkinsResultsParserUtil.join(", ", stuckQueueItemMessages));
	}

	private boolean _hasRecentBuildStart(
		long currentTimeMillis,
		List<JenkinsMaster.RunningBuild> runningBuilds) {

		for (JenkinsMaster.RunningBuild runningBuild : runningBuilds) {
			String jenkinsSlaveName = runningBuild.getJenkinsSlaveName();

			if (jenkinsSlaveName.equals("Built-In Node") ||
				!runningBuild.isBuilding()) {

				continue;
			}

			if (runningBuild.getDuration(currentTimeMillis) <=
					(_buildStartGapMaximumSeconds * 1000)) {

				return true;
			}
		}

		return false;
	}

	private List<List<JenkinsMaster.QueueItem>> _readQueueItems(
		Map<String, String> exceptionMessages,
		List<JenkinsMaster> jenkinsMasters) {

		List<Callable<List<JenkinsMaster.QueueItem>>> callables =
			new ArrayList<>();

		for (JenkinsMaster jenkinsMaster : jenkinsMasters) {
			callables.add(
				new Callable<List<JenkinsMaster.QueueItem>>() {

					@Override
					public List<JenkinsMaster.QueueItem> call() {
						try {
							return jenkinsMaster.readQueueItems(
								_RETRIES_SIZE_MAX, _SECONDS_RETRY_PERIOD,
								getAttemptTimeoutMillis(_RETRIES_SIZE_MAX));
						}
						catch (Exception exception) {
							exceptionMessages.put(
								jenkinsMaster.getName(),
								JenkinsResultsParserUtil.getMessage(exception));

							return null;
						}
					}

				});
		}

		return _execute(callables, "readQueueItems", jenkinsMasters.size());
	}

	private List<List<JenkinsMaster.RunningBuild>> _readRunningBuilds(
		Map<String, String> exceptionMessages,
		List<JenkinsMaster> jenkinsMasters) {

		List<Callable<List<JenkinsMaster.RunningBuild>>> callables =
			new ArrayList<>();

		for (JenkinsMaster jenkinsMaster : jenkinsMasters) {
			callables.add(
				new Callable<List<JenkinsMaster.RunningBuild>>() {

					@Override
					public List<JenkinsMaster.RunningBuild> call() {
						try {
							return jenkinsMaster.readRunningBuilds(
								_RETRIES_SIZE_MAX, _SECONDS_RETRY_PERIOD,
								getAttemptTimeoutMillis(_RETRIES_SIZE_MAX));
						}
						catch (Exception exception) {
							exceptionMessages.put(
								jenkinsMaster.getName(),
								JenkinsResultsParserUtil.getMessage(exception));

							return null;
						}
					}

				});
		}

		return _execute(callables, "readRunningBuilds", jenkinsMasters.size());
	}

	private static final int _RETRIES_SIZE_MAX = 1;

	private static final int _SECONDS_RETRY_PERIOD = 1;

	private static final int _STUCK_QUEUE_ITEMS_SIZE_MAX = 3;

	private final long _buildStartGapMaximumSeconds;
	private final String _cohortName;
	private final Set<String> _excludedJenkinsMasterNames;
	private final long _queueDurationMaximumSeconds;

}