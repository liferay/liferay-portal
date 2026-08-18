/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Calum Ragan
 */
public class StaleBuildReaper {

	public StaleBuildReaper(boolean dryRun, JenkinsCohort jenkinsCohort) {
		_dryRun = dryRun;
		_jenkinsCohort = jenkinsCohort;
	}

	public int getReapedBuildsCount() {
		int reapedBuildsCount = 0;

		for (ReapAction reapAction : _reapActions) {
			if (reapAction.isExecuted()) {
				reapedBuildsCount++;
			}
		}

		return reapedBuildsCount;
	}

	public int getStaleBuildsCount() {
		return _reapActions.size();
	}

	public String getSummary() {
		StringBuilder sb = new StringBuilder();

		int staleBuildsCount = getStaleBuildsCount();

		String nounForm = JenkinsResultsParserUtil.getNounForm(
			staleBuildsCount, "stale builds", "stale build");

		if (_dryRun) {
			sb.append("Found ");
			sb.append(staleBuildsCount);
			sb.append(" ");
			sb.append(nounForm);
			sb.append(". No build was aborted because DRY_RUN is enabled.");
		}
		else {
			sb.append("Reaped ");
			sb.append(getReapedBuildsCount());
			sb.append(" of ");
			sb.append(staleBuildsCount);
			sb.append(" ");
			sb.append(nounForm);
			sb.append(".");
		}

		for (ReapAction reapAction : _reapActions) {
			sb.append("\n");
			sb.append(reapAction.getSummary());
		}

		return sb.toString();
	}

	public void reap() {
		_generateReapActions();

		_executeReapActions();

		String summary = getSummary();

		System.out.println(summary);

		_sendSlackNotification(summary);
	}

	public static enum Reason {

		JENKINS_SLAVE_BEING_REMOVED("its node is being removed"),
		JENKINS_SLAVE_OFFLINE("its node has been offline"),
		LIKELY_STUCK("its executor reports likelyStuck");

		public String getDescription() {
			return _description;
		}

		private Reason(String description) {
			_description = description;
		}

		private final String _description;

	}

	private void _executeReapActions() {
		if (_dryRun) {
			return;
		}

		for (ReapAction reapAction : _reapActions) {
			reapAction.execute();
		}
	}

	private void _generateReapActions() {
		long currentTimeMillis =
			JenkinsResultsParserUtil.getCurrentTimeMillis();

		for (JenkinsMaster jenkinsMaster : _getJenkinsMasters()) {
			jenkinsMaster.update(false);

			List<JenkinsMaster.RunningBuild> runningBuilds =
				jenkinsMaster.getRunningBuilds();

			int maxRunningBuildsCount =
				jenkinsMaster.getMaxRunningBuildsCount();

			System.out.println(
				JenkinsResultsParserUtil.combine(
					jenkinsMaster.getName(), " reports at most ",
					String.valueOf(maxRunningBuildsCount), " ",
					JenkinsResultsParserUtil.getNounForm(
						maxRunningBuildsCount, "running builds",
						"running build"),
					". Enumerated ", String.valueOf(runningBuilds.size()),
					"."));

			for (JenkinsMaster.RunningBuild runningBuild : runningBuilds) {
				List<Reason> reasons = _getReasons(
					currentTimeMillis, runningBuild);

				if (reasons.isEmpty()) {
					continue;
				}

				_reapActions.add(
					new ReapAction(
						runningBuild.getDuration(currentTimeMillis), reasons,
						runningBuild));
			}
		}
	}

	private List<JenkinsMaster> _getJenkinsMasters() {
		List<JenkinsMaster> jenkinsMasters = new ArrayList<>(
			_jenkinsCohort.getAvailableJenkinsMasters());

		jenkinsMasters.addAll(_jenkinsCohort.getBlacklistedJenkinsMasters());

		return jenkinsMasters;
	}

	private List<Reason> _getJenkinsSlaveReasons(
		long currentTimeMillis, JenkinsMaster.RunningBuild runningBuild) {

		List<Reason> reasons = new ArrayList<>();

		if (runningBuild.isJenkinsSlaveBeingRemoved()) {
			reasons.add(Reason.JENKINS_SLAVE_BEING_REMOVED);

			return reasons;
		}

		if (!runningBuild.isJenkinsSlaveOfflineUnexpectedly()) {
			return reasons;
		}

		long jenkinsSlaveOfflineDuration =
			runningBuild.getJenkinsSlaveOfflineDuration(currentTimeMillis);

		if (jenkinsSlaveOfflineDuration >= _MINIMUM_OFFLINE_DURATION) {
			reasons.add(Reason.JENKINS_SLAVE_OFFLINE);
		}

		return reasons;
	}

	private List<Reason> _getReasons(
		long currentTimeMillis, JenkinsMaster.RunningBuild runningBuild) {

		List<Reason> reasons = new ArrayList<>();

		if (!runningBuild.isBuilding()) {
			return reasons;
		}

		long duration = runningBuild.getDuration(currentTimeMillis);

		if (runningBuild.isLikelyStuck() &&
			(duration >= _MINIMUM_LIKELY_STUCK_DURATION)) {

			reasons.add(Reason.LIKELY_STUCK);
		}

		reasons.addAll(
			_getJenkinsSlaveReasons(currentTimeMillis, runningBuild));

		return reasons;
	}

	private void _sendSlackNotification(String summary) {
		if (_reapActions.isEmpty()) {
			return;
		}

		String subject = "Stale builds reaped";

		if (_dryRun) {
			subject = "Stale builds detected";
		}

		NotificationUtil.sendSlackNotification(
			summary, "ci-notifications", subject);
	}

	private static final long _MINIMUM_LIKELY_STUCK_DURATION = 60 * 60 * 1000L;

	private static final long _MINIMUM_OFFLINE_DURATION = 15 * 60 * 1000L;

	private final boolean _dryRun;
	private final JenkinsCohort _jenkinsCohort;
	private final List<ReapAction> _reapActions = new ArrayList<>();

	private class ReapAction {

		public void execute() {
			String buildURL = _runningBuild.getURL();

			try {
				_abortResult = JenkinsStopBuildUtil.abortBuild(buildURL);
			}
			catch (Exception exception) {
				System.out.println("Unable to reap " + buildURL);

				exception.printStackTrace();
			}
		}

		public String getSummary() {
			StringBuilder sb = new StringBuilder();

			JenkinsMaster jenkinsMaster = _runningBuild.getJenkinsMaster();

			sb.append(jenkinsMaster.getName());

			sb.append(" ");
			sb.append(_runningBuild.getFullDisplayName());
			sb.append(" on ");
			sb.append(_runningBuild.getJenkinsSlaveName());
			sb.append(" has been running for ");
			sb.append(JenkinsResultsParserUtil.toDurationString(_duration));

			long estimatedDuration = _runningBuild.getEstimatedDuration();

			if (estimatedDuration > 0) {
				sb.append(" against an estimate of ");
				sb.append(
					JenkinsResultsParserUtil.toDurationString(
						estimatedDuration));
			}

			sb.append(". Flagged because ");

			List<String> descriptions = new ArrayList<>();

			for (Reason reason : _reasons) {
				descriptions.add(reason.getDescription());
			}

			sb.append(JenkinsResultsParserUtil.join(", and ", descriptions));

			sb.append(". ");
			sb.append(_getOutcome());
			sb.append(" ");
			sb.append(_runningBuild.getURL());

			return sb.toString();
		}

		public boolean isExecuted() {
			if (_abortResult == JenkinsStopBuildUtil.AbortResult.STOPPED) {
				return true;
			}

			return false;
		}

		private ReapAction(
			long duration, List<Reason> reasons,
			JenkinsMaster.RunningBuild runningBuild) {

			_duration = duration;
			_reasons = reasons;
			_runningBuild = runningBuild;
		}

		private String _getOutcome() {
			if (_dryRun) {
				return "Not aborted (DRY_RUN).";
			}

			if (_abortResult ==
					JenkinsStopBuildUtil.AbortResult.ALREADY_FINISHED) {

				return "Already finished.";
			}

			if (_abortResult ==
					JenkinsStopBuildUtil.AbortResult.STILL_RUNNING) {

				return "Ignored the abort, still holding its executor.";
			}

			if (_abortResult == JenkinsStopBuildUtil.AbortResult.STOPPED) {
				return "Aborted.";
			}

			return "Abort failed.";
		}

		private JenkinsStopBuildUtil.AbortResult _abortResult;
		private final long _duration;
		private final List<Reason> _reasons;
		private final JenkinsMaster.RunningBuild _runningBuild;

	}

}