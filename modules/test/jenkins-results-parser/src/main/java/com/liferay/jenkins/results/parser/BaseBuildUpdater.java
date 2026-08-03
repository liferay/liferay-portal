/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public abstract class BaseBuildUpdater implements BuildUpdater {

	@Override
	public Build getBuild() {
		return _build;
	}

	@Override
	public void reset() {
	}

	@Override
	public void update() {
		String status = _build.getStatus();

		if (status.equals("completed")) {
			runCompleted();
		}
		else if (status.equals("missing")) {
			runMissing();
		}
		else if (status.equals("queued")) {
			runQueued();
		}
		else if (status.equals("reporting")) {
			runReporting();
		}
		else if (status.equals("running")) {
			runRunning();
		}
		else if (status.equals("starting")) {
			runStarting();
		}
	}

	protected BaseBuildUpdater(Build build) {
		_build = build;
	}

	protected abstract boolean isBuildCompleted();

	protected abstract boolean isBuildFailing();

	protected abstract boolean isBuildQueued();

	protected abstract boolean isBuildRunning();

	protected void runCompleted() {
		_build.setStatus("completed");
	}

	protected void runMissing() {
		if (isBuildRunning()) {
			_missingTickCount = 0;

			_build.setStatus("running");

			return;
		}

		if (isBuildQueued()) {
			_missingTickCount = 0;

			_build.setStatus("queued");

			return;
		}

		if (isBuildCompleted()) {
			_missingTickCount = 0;

			_build.setStatus("completed");

			return;
		}

		_missingTickCount++;

		if (_missingTickCount < _getMissingReinvokeTickCount()) {
			return;
		}

		if (_missingReinvocationCount < _getMissingMaximumReinvocationCount()) {
			_missingTickCount = 0;

			if (!_reattachMatchingBuild()) {
				_missingReinvocationCount++;

				_build.reset();

				reinvoke();
			}

			_build.setStatus("queued");

			return;
		}

		_missingTickCount = 0;

		_build.setStatus("reporting");
	}

	protected void runQueued() {
		if (isBuildRunning()) {
			_build.setStatus("running");

			return;
		}

		if (isBuildQueued()) {
			return;
		}

		if (isBuildCompleted()) {
			_build.setStatus("completed");

			return;
		}

		_build.setStatus("missing");
	}

	protected void runReporting() {
		if (isBuildFailing()) {
			_isApplySlaveOfflineRules();

			if (_isApplyReinvokeRules()) {
				_build.setStatus("queued");

				return;
			}

			if (_build instanceof AppServerBundleDownstreamBuild) {
				AppServerBundleDownstreamBuild appServerBundleDownstreamBuild =
					(AppServerBundleDownstreamBuild)_build;

				try {
					appServerBundleDownstreamBuild.
						createBuildFailureObjectRef();
				}
				catch (IOException ioException) {
					throw new RuntimeException(ioException);
				}
			}
		}

		_build.setStatus("completed");

		if (_build instanceof DownstreamBuild) {
			DownstreamBuild downstreamBuild = (DownstreamBuild)_build;

			downstreamBuild.generateBuildReport();
		}
	}

	protected void runRunning() {
		if (!isBuildCompleted()) {
			_build.setStatus("running");

			return;
		}

		_build.setStatus("reporting");
	}

	protected void runStarting() {
		Build.Invocation previousInvocation = _build.getPreviousInvocation();

		if (previousInvocation != null) {
			reinvoke();
		}
		else {
			invoke();
		}

		_build.setStatus("queued");
	}

	private int _getMissingMaximumReinvocationCount() {
		try {
			String missingMaxReinvocationCount =
				JenkinsResultsParserUtil.getBuildProperty(
					"build.missing.max.reinvocation.count");

			if (JenkinsResultsParserUtil.isInteger(
					missingMaxReinvocationCount)) {

				return Integer.parseInt(missingMaxReinvocationCount);
			}
		}
		catch (IOException ioException) {
		}

		return _MISSING_MAXIMUM_REINVOCATION_COUNT_DEFAULT;
	}

	private int _getMissingReinvokeTickCount() {
		try {
			String missingReinvokeTickCount =
				JenkinsResultsParserUtil.getBuildProperty(
					"build.missing.reinvoke.tick.count");

			if (JenkinsResultsParserUtil.isInteger(missingReinvokeTickCount)) {
				return Integer.parseInt(missingReinvokeTickCount);
			}
		}
		catch (IOException ioException) {
		}

		return _MISSING_REINVOKE_TICK_COUNT_DEFAULT;
	}

	private boolean _hasMaximumInvocationCount() {
		Build build = getBuild();

		if ((isBuildCompleted() && !isBuildFailing()) || !isBuildCompleted() ||
			build.isFromArchive()) {

			return false;
		}

		_setCurrentReinvokeRule();

		return build.hasMaximumInvocationCount();
	}

	private boolean _isApplyReinvokeRules() {
		Build build = getBuild();

		if (build instanceof ParentBuild) {
			return false;
		}

		if ((isBuildCompleted() && !isBuildFailing()) || !isBuildCompleted() ||
			build.isFromArchive() || _hasMaximumInvocationCount()) {

			return false;
		}

		for (ReinvokeRule reinvokeRule : ReinvokeRule.getReinvokeRules()) {
			if (!reinvokeRule.matches(build)) {
				continue;
			}

			_reinvoke(reinvokeRule);

			return true;
		}

		return false;
	}

	private boolean _isApplySlaveOfflineRules() {
		Build build = getBuild();

		if ((isBuildCompleted() && !isBuildFailing()) || !isBuildCompleted() ||
			build.isFromArchive()) {

			return false;
		}

		JenkinsSlave jenkinsSlave = build.getJenkinsSlave();

		if (jenkinsSlave == null) {
			return false;
		}

		jenkinsSlave.update();

		if (jenkinsSlave.isOffline()) {
			return false;
		}

		List<SlaveOfflineRule> slaveOfflineRules = new ArrayList<>(
			SlaveOfflineRule.getSlaveOfflineRules());

		for (SlaveOfflineRule slaveOfflineRule : slaveOfflineRules) {
			if (!slaveOfflineRule.matches(build)) {
				continue;
			}

			_takeSlaveOffline(slaveOfflineRule);

			return true;
		}

		return false;
	}

	private boolean _reattachMatchingBuild() {
		Build build = getBuild();

		Build.Invocation currentInvocation = build.getCurrentInvocation();

		if (currentInvocation == null) {
			return false;
		}

		JenkinsMaster jenkinsMaster = currentInvocation.getJenkinsMaster();

		if (jenkinsMaster == null) {
			return false;
		}

		Map<String, String> parameters = build.getParameters();

		if (parameters.isEmpty()) {
			return false;
		}

		String jobName = build.getJobName();

		JSONObject buildJSONObject = jenkinsMaster.getInProgressBuildJSONObject(
			jobName, parameters);

		if (buildJSONObject != null) {
			currentInvocation.setQueueId(buildJSONObject.getLong("queueId"));

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"[", build.getBuildName(),
					"] Reattached to the running build ",
					buildJSONObject.getString("url")));

			return true;
		}

		JSONObject queueItemJSONObject = jenkinsMaster.getQueuedBuildJSONObject(
			jobName, parameters);

		if (queueItemJSONObject != null) {
			currentInvocation.setQueueId(queueItemJSONObject.getLong("id"));

			System.out.println(
				JenkinsResultsParserUtil.combine(
					"[", build.getBuildName(),
					"] Reattached to the queued build"));

			return true;
		}

		return false;
	}

	private void _reinvoke(ReinvokeRule reinvokeRule) {
		Build build = getBuild();

		if ((build instanceof ParentBuild) || _hasMaximumInvocationCount()) {
			return;
		}

		Build parentBuild = build.getParentBuild();

		if (parentBuild == null) {
			return;
		}

		String parentBuildStatus = parentBuild.getStatus();

		if (!parentBuildStatus.equals("running") ||
			!JenkinsResultsParserUtil.isCINode() ||
			build.isFromCompletedBuild()) {

			return;
		}

		if ((reinvokeRule != null) && !build.isFromArchive()) {
			String message = JenkinsResultsParserUtil.combine(
				reinvokeRule.getName(), " failure detected at ",
				build.getBuildURL(), ". This build will be reinvoked.\n\n",
				reinvokeRule.toString(), "\n\n");

			System.out.println(message);

			TopLevelBuild topLevelBuild = build.getTopLevelBuild();

			if (topLevelBuild != null) {
				message = JenkinsResultsParserUtil.combine(
					message, "Top Level Build URL: ",
					topLevelBuild.getBuildURL());
			}

			String notificationRecipients =
				reinvokeRule.getNotificationRecipients();

			if ((notificationRecipients != null) &&
				!notificationRecipients.isEmpty()) {

				List<String> invalidNotificationRecipients = new ArrayList<>();

				for (String notificationRecipient :
						notificationRecipients.split(",")) {

					notificationRecipient = notificationRecipient.trim();

					Matcher matcher = _notificationRecipentsPattern.matcher(
						notificationRecipient);

					if (matcher.find()) {
						String slack = matcher.group("slack");

						if (!JenkinsResultsParserUtil.isNullOrEmpty(slack)) {
							NotificationUtil.sendSlackNotification(
								message, slack, "Build Reinvoked");

							continue;
						}

						String email = matcher.group("slack");

						if (!JenkinsResultsParserUtil.isNullOrEmpty(email)) {
							NotificationUtil.sendEmail(
								message, "jenkins", "Build Reinvoked", email);
						}
					}
					else {
						invalidNotificationRecipients.add(
							notificationRecipient);
					}
				}

				if (!invalidNotificationRecipients.isEmpty()) {
					String invalidNotificationRecipientsString =
						JenkinsResultsParserUtil.join(
							",", invalidNotificationRecipients);

					System.out.println(
						"WARNING: Invalid notification recipients found: " +
							invalidNotificationRecipientsString);
				}
			}

			String reinvokeBuildPriority =
				reinvokeRule.getReinvokeBuildPriority();

			if ((reinvokeBuildPriority != null) &&
				!reinvokeBuildPriority.isEmpty()) {

				Map<String, String> reinvokeBuildParameters = new HashMap<>();

				reinvokeBuildParameters.put(
					"BUILD_PRIORITY", reinvokeBuildPriority);

				reinvoke(reinvokeBuildParameters);

				return;
			}
		}

		reinvoke();
	}

	private void _setCurrentReinvokeRule() {
		Build build = getBuild();

		if (build instanceof ParentBuild) {
			return;
		}

		if ((isBuildCompleted() && !isBuildFailing()) || !isBuildCompleted() ||
			build.isFromArchive()) {

			return;
		}

		Build.Invocation currentInvocation = build.getCurrentInvocation();

		if (_reinvokeRulesMap.containsKey(currentInvocation)) {
			return;
		}

		for (ReinvokeRule reinvokeRule : ReinvokeRule.getReinvokeRules()) {
			if (!reinvokeRule.matches(build)) {
				continue;
			}

			_reinvokeRulesMap.put(currentInvocation, reinvokeRule);

			currentInvocation.setReinvokeRule(reinvokeRule);

			break;
		}

		_reinvokeRulesMap.put(currentInvocation, null);
	}

	private void _takeSlaveOffline(SlaveOfflineRule slaveOfflineRule) {
		Build build = getBuild();

		if ((slaveOfflineRule == null) || build.isFromArchive()) {
			return;
		}

		slaveOfflineRule.takeSlaveOffline(build);
	}

	private static final int _MISSING_MAXIMUM_REINVOCATION_COUNT_DEFAULT = 2;

	private static final int _MISSING_REINVOKE_TICK_COUNT_DEFAULT = 3;

	private static final Pattern _notificationRecipentsPattern =
		Pattern.compile(
			"slack:(?:<@)?(?<slack>[\\w-]+)>?|(?<email>[\\w-]+@[\\w.-]+)");

	private final Build _build;
	private int _missingReinvocationCount;
	private int _missingTickCount;
	private final Map<Build.Invocation, ReinvokeRule> _reinvokeRulesMap =
		new HashMap<>();

}