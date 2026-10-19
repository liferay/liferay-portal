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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * @author Brittney Nguyen
 */
public class RunningBuildDurationMonitor extends BaseMonitor {

	public RunningBuildDurationMonitor(MonitorConfig monitorConfig) {
		super(monitorConfig);

		_cohortName = getRequiredParameter(
			"cohort.name", monitorConfig.getParameters());
		_buildDurationMaximumSeconds = getLongValue(
			"threshold", 24 * 60 * 60, "build.duration.maximum",
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
				MonitorResult.Status.UNKNOWN, currentTimeMillis);
		}

		Map<String, String> exceptionMessages = new ConcurrentHashMap<>();
		int longRunningBuildsCount = 0;
		List<String> messages = new ArrayList<>();
		int runningBuildsCount = 0;
		List<MonitorResult.Status> statuses = new ArrayList<>();

		List<List<JenkinsMaster.RunningBuild>> runningBuildsList =
			_readRunningBuilds(exceptionMessages, jenkinsMasters);

		for (int i = 0; i < jenkinsMasters.size(); i++) {
			JenkinsMaster jenkinsMaster = jenkinsMasters.get(i);

			List<JenkinsMaster.RunningBuild> runningBuilds =
				runningBuildsList.get(i);

			if (runningBuilds == null) {
				messages.add(
					JenkinsResultsParserUtil.combine(
						"Unable to read running builds on ",
						jenkinsMaster.getName(), ": ",
						exceptionMessages.get(jenkinsMaster.getName())));

				statuses.add(MonitorResult.Status.UNKNOWN);

				continue;
			}

			for (JenkinsMaster.RunningBuild runningBuild : runningBuilds) {
				if (!runningBuild.isBuilding()) {
					continue;
				}

				runningBuildsCount++;

				long duration = runningBuild.getDuration(currentTimeMillis);

				if (duration <= (_buildDurationMaximumSeconds * 1000)) {
					continue;
				}

				longRunningBuildsCount++;

				messages.add(
					JenkinsResultsParserUtil.combine(
						jenkinsMaster.getName(), " ",
						runningBuild.getFullDisplayName(),
						" has been running for ",
						JenkinsResultsParserUtil.toDurationString(duration),
						" at ", runningBuild.getURL()));

				statuses.add(MonitorResult.Status.CRITICAL);
			}
		}

		if (longRunningBuildsCount > 0) {
			messages.add(
				0,
				JenkinsResultsParserUtil.combine(
					String.valueOf(longRunningBuildsCount), " ",
					JenkinsResultsParserUtil.getNounForm(
						longRunningBuildsCount, "builds", "build"),
					" in cohort ", _cohortName, " over the ceiling of ",
					JenkinsResultsParserUtil.toDurationString(
						_buildDurationMaximumSeconds * 1000)));
		}

		Map<String, String> metrics = new LinkedHashMap<>();

		metrics.put(
			"long.running.builds.count",
			String.valueOf(longRunningBuildsCount));
		metrics.put("running.builds.count", String.valueOf(runningBuildsCount));

		if (statuses.isEmpty()) {
			return new MonitorResult(
				JenkinsResultsParserUtil.combine(
					"No build in cohort ", _cohortName, " has run longer than ",
					JenkinsResultsParserUtil.toDurationString(
						_buildDurationMaximumSeconds * 1000)),
				metrics, MonitorResult.Status.OK, currentTimeMillis);
		}

		return new MonitorResult(
			JenkinsResultsParserUtil.join(". ", messages), metrics,
			MonitorResult.Status.getMostSevere(statuses), currentTimeMillis);
	}

	@Override
	protected int getReadsCount() {
		return 1;
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

		return new ArrayList<>(jenkinsMastersMap.values());
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

		ParallelExecutor<List<JenkinsMaster.RunningBuild>> parallelExecutor =
			new ParallelExecutor<>(
				callables,
				JenkinsResultsParserUtil.getNewThreadPoolExecutor(
					jenkinsMasters.size(), true),
				"readRunningBuilds");

		try {
			return parallelExecutor.execute();
		}
		catch (TimeoutException timeoutException) {
			throw new RuntimeException(timeoutException);
		}
	}

	private static final int _RETRIES_SIZE_MAX = 1;

	private static final int _SECONDS_RETRY_PERIOD = 1;

	private final long _buildDurationMaximumSeconds;
	private final String _cohortName;

}