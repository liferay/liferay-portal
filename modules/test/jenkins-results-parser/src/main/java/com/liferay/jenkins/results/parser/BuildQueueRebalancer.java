/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser;

import com.liferay.jenkins.results.parser.aws.AWSFleetCloud;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class BuildQueueRebalancer {

	public BuildQueueRebalancer(JenkinsCohort jenkinsCohort) {
		_jenkinsCohort = jenkinsCohort;
	}

	public String getSummary() {
		StringBuilder sb = new StringBuilder();

		sb.append("Build queue rebalanced by ");
		sb.append(_getRebalanceActionCount(Type.REINVOKE));
		sb.append(" reinvocations and ");
		sb.append(_getRebalanceActionCount(Type.ABORT));
		sb.append(" aborts.");

		for (RebalanceAction rebalanceAction : _rebalanceActions) {
			sb.append("\n");
			sb.append(rebalanceAction.getSummary());
		}

		return sb.toString();
	}

	public void rebalance() {
		_generateBlacklistRebalanceActions();

		_generateAvailableRebalanceActions();

		_executeRebalanceActions();
	}

	public static enum Type {

		ABORT, REINVOKE

	}

	private static double _getRebalanceThresholdMultiplier() {
		try {
			String rebalanceThresholdMultiplier =
				JenkinsResultsParserUtil.getBuildProperty(
					"jenkins.queue.rebalance.threshold");

			if (JenkinsResultsParserUtil.isDouble(
					rebalanceThresholdMultiplier)) {

				return Double.parseDouble(rebalanceThresholdMultiplier);
			}

			return _REBALANCE_THRESHOLD_MULTIPLIER_DEFAULT;
		}
		catch (IOException ioException) {
			return _REBALANCE_THRESHOLD_MULTIPLIER_DEFAULT;
		}
	}

	private void _executeRebalanceActions() {
		for (RebalanceAction rebalanceAction : _rebalanceActions) {
			rebalanceAction.execute();
		}
	}

	private void _generateAvailableRebalanceActions() {
		Map<String, Queue> queueMap = _getQueueMap();

		for (Map.Entry<String, Queue> queueEntry : queueMap.entrySet()) {
			System.out.println(
				"Rebalancing queue items with label " + queueEntry.getKey());

			Queue queue = queueEntry.getValue();

			for (JenkinsMaster jenkinsMaster :
					_jenkinsCohort.getAvailableJenkinsMasters()) {

				List<JenkinsMaster.QueueItem> queueItems = queue.getQueueItems(
					jenkinsMaster);

				int targetQueueSize = queue.getTargetQueueSize();

				if (queueItems.size() < targetQueueSize) {
					continue;
				}

				for (int i = targetQueueSize; i < queueItems.size(); i++) {
					_rebalanceActions.add(
						new RebalanceAction(queueItems.get(i)));
				}
			}
		}
	}

	private void _generateBlacklistRebalanceActions() {
		for (JenkinsMaster jenkinsMaster :
				_jenkinsCohort.getBlacklistedJenkinsMasters()) {

			try {
				for (JenkinsMaster.QueueItem queueItem :
						jenkinsMaster.getQueueItems()) {

					_rebalanceActions.add(new RebalanceAction(queueItem));
				}
			}
			catch (Exception exception) {
				System.out.println(
					"Unable to drain queue items from " +
						jenkinsMaster.getName() + ": " + exception);
			}
		}
	}

	private Queue _getQueue(String primaryLabel) {
		Map<String, Queue> queueMap = _getQueueMap();

		return queueMap.get(primaryLabel);
	}

	private synchronized Map<String, Queue> _getQueueMap() {
		if (_queueMaps != null) {
			return _queueMaps;
		}

		List<JenkinsMaster.QueueItem> availableQueueItems = new ArrayList<>();

		List<JenkinsMaster> jenkinsMasters =
			_jenkinsCohort.getAvailableJenkinsMasters();

		for (JenkinsMaster jenkinsMaster : jenkinsMasters) {
			availableQueueItems.addAll(jenkinsMaster.getQueueItems());
		}

		_queueMaps = new HashMap<>();

		for (JenkinsMaster.QueueItem queueItem : availableQueueItems) {
			AWSFleetCloud awsFleetCloud = queueItem.getAWSFleetCloud();

			if (awsFleetCloud == null) {
				System.out.println(
					"Skipping queue item with no matching fleet " +
						queueItem.getURL());

				continue;
			}

			String primaryLabel = awsFleetCloud.getPrimaryLabel();

			Queue queue = _queueMaps.get(primaryLabel);

			if (queue == null) {
				queue = new Queue(awsFleetCloud);
			}

			queue.addQueueItem(queueItem);

			_queueMaps.put(primaryLabel, queue);
		}

		return _queueMaps;
	}

	private int _getRebalanceActionCount(Type type) {
		int count = 0;

		for (RebalanceAction rebalanceAction : _rebalanceActions) {
			if (rebalanceAction.getType() == type) {
				count++;
			}
		}

		return count;
	}

	private static final double _REBALANCE_THRESHOLD_MULTIPLIER_DEFAULT = 1.5;

	private final JenkinsCohort _jenkinsCohort;
	private Map<String, Queue> _queueMaps;
	private final List<RebalanceAction> _rebalanceActions = new ArrayList<>();

	private static class Queue {

		public void addQueueItem(JenkinsMaster.QueueItem queueItem) {
			if (_queueItems.contains(queueItem)) {
				return;
			}

			_queueItems.add(queueItem);

			JenkinsMaster jenkinsMaster = queueItem.getJenkinsMaster();

			List<JenkinsMaster.QueueItem> queueItems =
				_jenkinsMasterQueueItems.get(jenkinsMaster);

			if (queueItems == null) {
				queueItems = new ArrayList<>();
			}

			queueItems.add(queueItem);

			_jenkinsMasterQueueItems.put(jenkinsMaster, queueItems);
		}

		public int getAverageQueueSize() {
			if (_jenkinsMasters.isEmpty()) {
				return 0;
			}

			return _queueItems.size() / _jenkinsMasters.size();
		}

		public int getMaxQueueSize() {
			return _maxQueueSize;
		}

		public JenkinsMaster getMostAvailableJenkinsMaster() {
			JenkinsMaster mostAvailableJenkinsMaster = null;

			int smallestQueueSize = -1;

			for (JenkinsMaster jenkinsMaster : _jenkinsMasters) {
				int queueSize = _jenkinsMasterQueueSizes.get(jenkinsMaster);

				if ((mostAvailableJenkinsMaster == null) ||
					(queueSize < smallestQueueSize)) {

					mostAvailableJenkinsMaster = jenkinsMaster;
					smallestQueueSize = queueSize;
				}
			}

			if (mostAvailableJenkinsMaster == null) {
				return null;
			}

			_jenkinsMasterQueueSizes.put(
				mostAvailableJenkinsMaster, smallestQueueSize + 1);

			return mostAvailableJenkinsMaster;
		}

		public String getPrimaryLabel() {
			return _primaryLabel;
		}

		public List<JenkinsMaster.QueueItem> getQueueItems() {
			Collections.sort(_queueItems);

			return new ArrayList<>(_queueItems);
		}

		public List<JenkinsMaster.QueueItem> getQueueItems(
			JenkinsMaster jenkinsMaster) {

			List<JenkinsMaster.QueueItem> queueItems =
				_jenkinsMasterQueueItems.get(jenkinsMaster);

			if (queueItems == null) {
				return new ArrayList<>();
			}

			Collections.sort(queueItems);

			return queueItems;
		}

		public int getTargetQueueSize() {
			int targetQueueSize = getMaxQueueSize();

			int averageQueueSize = getAverageQueueSize();

			if (targetQueueSize < averageQueueSize) {
				targetQueueSize = averageQueueSize;
			}

			return (int)(targetQueueSize * _getRebalanceThresholdMultiplier());
		}

		private Queue(AWSFleetCloud awsFleetCloud) {
			_primaryLabel = awsFleetCloud.getPrimaryLabel();

			_maxQueueSize = awsFleetCloud.getMaxSize();

			JenkinsMaster jenkinsMaster = awsFleetCloud.getJenkinsMaster();

			JenkinsCohort jenkinsCohort = jenkinsMaster.getJenkinsCohort();

			_jenkinsMasters = jenkinsCohort.getAvailableJenkinsMasters();

			for (JenkinsMaster availableJenkinsMaster : _jenkinsMasters) {
				int queueSize = 0;

				for (JenkinsMaster.QueueItem queueItem :
						availableJenkinsMaster.getQueueItems()) {

					if (Objects.equals(
							queueItem.getPrimaryLabel(), _primaryLabel)) {

						queueSize++;
					}
				}

				_jenkinsMasterQueueSizes.put(availableJenkinsMaster, queueSize);
			}
		}

		private final Map<JenkinsMaster, List<JenkinsMaster.QueueItem>>
			_jenkinsMasterQueueItems = new HashMap<>();
		private final Map<JenkinsMaster, Integer> _jenkinsMasterQueueSizes =
			new HashMap<>();
		private final List<JenkinsMaster> _jenkinsMasters;
		private final int _maxQueueSize;
		private final String _primaryLabel;
		private final List<JenkinsMaster.QueueItem> _queueItems =
			new ArrayList<>();

	}

	private class RebalanceAction {

		public void execute() {
			try {
				JenkinsMaster currentJenkinsMaster = _getCurrentJenkinsMaster();

				if (getType() == Type.ABORT) {
					JenkinsStopBuildUtil.cancelQueueItem(
						currentJenkinsMaster, _queueItem.getId());

					_executed = true;

					return;
				}

				Queue queue = _getQueue(_queueItem.getPrimaryLabel());

				if (queue == null) {
					System.out.println(
						"Skipping rebalance for queue item with no matching " +
							"fleet " + _queueItem.getURL());

					return;
				}

				JenkinsMaster targetJenkinsMaster =
					queue.getMostAvailableJenkinsMaster();

				if ((targetJenkinsMaster == null) ||
					(targetJenkinsMaster == currentJenkinsMaster)) {

					return;
				}

				long queueId = JenkinsResultsParserUtil.invokeJenkinsBuild(
					targetJenkinsMaster, _queueItem.getTaskName(),
					_queueItem.getParameters());

				if (queueId == 0) {
					return;
				}

				JenkinsStopBuildUtil.cancelQueueItem(
					currentJenkinsMaster, _queueItem.getId());

				_executed = true;
			}
			catch (Exception exception) {
				System.out.println(
					"Unable to rebalance queue item " + _queueItem.getURL() +
						": " + exception);
			}
		}

		public JenkinsMaster.QueueItem getQueueItem() {
			return _queueItem;
		}

		public String getSummary() {
			StringBuilder sb = new StringBuilder();

			sb.append("The ");

			if (getType() == Type.ABORT) {
				sb.append("abort");
			}
			else {
				sb.append("reinvoke");
			}

			sb.append(" was");

			if (!_executed) {
				sb.append(" not");
			}

			sb.append(" executed on ");

			JenkinsMaster currentJenkinsMaster = _getCurrentJenkinsMaster();

			sb.append(currentJenkinsMaster.getName());

			sb.append(".");

			return sb.toString();
		}

		public Type getType() {
			if (_type != null) {
				return _type;
			}

			Map<String, String> parameters = _queueItem.getParameters();

			String parentBuildURL = parameters.get("PARENT_BUILD_URL");

			if (JenkinsResultsParserUtil.isNullOrEmpty(parentBuildURL)) {
				_type = Type.REINVOKE;

				return _type;
			}

			if (_isBuildInProgress(parentBuildURL)) {
				_type = Type.ABORT;
			}
			else {
				_type = Type.REINVOKE;
			}

			return _type;
		}

		private RebalanceAction(JenkinsMaster.QueueItem queueItem) {
			_queueItem = queueItem;
		}

		private JenkinsMaster _getCurrentJenkinsMaster() {
			return _queueItem.getJenkinsMaster();
		}

		private boolean _isBuildInProgress(String buildURL) {
			try {
				JSONObject jsonObject = JenkinsResultsParserUtil.toJSONObject(
					JenkinsResultsParserUtil.combine(
						JenkinsResultsParserUtil.getLocalURL(buildURL),
						"/api/json?tree=result"),
					false, 5000);

				String result = jsonObject.optString("result");

				return JenkinsResultsParserUtil.isNullOrEmpty(result);
			}
			catch (Exception exception) {
				return false;
			}
		}

		private boolean _executed;
		private final JenkinsMaster.QueueItem _queueItem;
		private Type _type;

	}

}