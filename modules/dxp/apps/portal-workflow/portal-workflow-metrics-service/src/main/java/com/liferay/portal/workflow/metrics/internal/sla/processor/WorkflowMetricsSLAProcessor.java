/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.internal.sla.processor;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.petra.string.StringUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.workflow.metrics.model.WorkflowMetricsSLADefinitionVersion;
import com.liferay.portal.workflow.metrics.sla.calendar.WorkflowMetricsSLACalendar;
import com.liferay.portal.workflow.metrics.sla.calendar.WorkflowMetricsSLACalendarRegistry;
import com.liferay.portal.workflow.metrics.sla.processor.WorkflowMetricsSLAStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

/**
 * @author Rafael Praxedes
 */
public class WorkflowMetricsSLAProcessor {

	public WorkflowMetricsSLAInstanceResult process(
		LocalDateTime completionLocalDateTime,
		LocalDateTime createLocalDateTime, List<Document> documents,
		long instanceId, LocalDateTime nowLocalDateTime, long startNodeId,
		WorkflowMetricsSLACalendarRegistry workflowMetricsSLACalendarRegistry,
		WorkflowMetricsSLADefinitionVersion workflowMetricsSLADefinitionVersion,
		WorkflowMetricsSLAInstanceResult workflowMetricsSLAInstanceResult) {

		long elapsedTime = 0;
		LocalDateTime lastCheckLocalDateTime = null;
		WorkflowMetricsSLAStatus workflowMetricsSLAStatus =
			WorkflowMetricsSLAStatus.NEW;

		if (workflowMetricsSLAInstanceResult != null) {
			elapsedTime = workflowMetricsSLAInstanceResult.getElapsedTime();

			lastCheckLocalDateTime =
				workflowMetricsSLAInstanceResult.getModifiedLocalDateTime();
			workflowMetricsSLAStatus =
				workflowMetricsSLAInstanceResult.getWorkflowMetricsSLAStatus();

			if (lastCheckLocalDateTime.isAfter(nowLocalDateTime) ||
				Objects.equals(
					workflowMetricsSLAStatus,
					WorkflowMetricsSLAStatus.COMPLETED)) {

				return null;
			}
		}

		WorkflowMetricsSLACalendar workflowMetricsSLACalendar =
			workflowMetricsSLACalendarRegistry.getWorkflowMetricsSLACalendar(
				workflowMetricsSLADefinitionVersion.getCalendarKey());
		WorkflowMetricsSLAStopwatch workflowMetricsSLAStopwatch =
			_createWorkflowMetricsSLAStopwatch(
				createLocalDateTime, documents, lastCheckLocalDateTime,
				startNodeId, workflowMetricsSLADefinitionVersion,
				workflowMetricsSLAStatus);

		LocalDateTime endLocalDateTime = nowLocalDateTime;

		if (completionLocalDateTime != null) {
			endLocalDateTime = completionLocalDateTime;
		}

		if (!workflowMetricsSLAStopwatch.isEmpty()) {
			List<TaskInterval> taskIntervals = _toTaskIntervals(
				documents, lastCheckLocalDateTime, nowLocalDateTime);

			for (TaskInterval taskInterval : taskIntervals) {
				endLocalDateTime = taskInterval.getEndLocalDateTime();

				elapsedTime += _computeElapsedTime(
					endLocalDateTime, taskInterval.getStartLocalDateTime(),
					workflowMetricsSLACalendar, workflowMetricsSLAStopwatch);
			}

			workflowMetricsSLAStatus =
				workflowMetricsSLAStopwatch.getWorkflowMetricsSLAStatus();

			if (completionLocalDateTime != null) {
				workflowMetricsSLAStatus = WorkflowMetricsSLAStatus.COMPLETED;
			}

			if (Objects.equals(
					workflowMetricsSLAStatus,
					WorkflowMetricsSLAStatus.RUNNING)) {

				Duration duration = workflowMetricsSLACalendar.getDuration(
					endLocalDateTime, nowLocalDateTime);

				elapsedTime += duration.toMillis();

				endLocalDateTime = nowLocalDateTime;
			}
			else if (Objects.equals(
						workflowMetricsSLAStatus,
						WorkflowMetricsSLAStatus.COMPLETED)) {

				Duration duration = workflowMetricsSLACalendar.getDuration(
					endLocalDateTime, completionLocalDateTime);

				elapsedTime += duration.toMillis();

				endLocalDateTime = completionLocalDateTime;
			}
		}

		return _createWorkflowMetricsSLAInstanceResult(
			workflowMetricsSLADefinitionVersion.getCompanyId(),
			completionLocalDateTime, documents, elapsedTime, endLocalDateTime,
			instanceId, nowLocalDateTime, workflowMetricsSLACalendar,
			workflowMetricsSLADefinitionVersion, workflowMetricsSLAStatus);
	}

	protected boolean isBreached(
		Document document, LocalDateTime nowLocalDateTime,
		LocalDateTime overdueLocalDateTime) {

		if (nowLocalDateTime.isBefore(overdueLocalDateTime)) {
			return false;
		}

		LocalDateTime createDateLocalDateTime = LocalDateTime.parse(
			document.getDate("createDate"), _dateTimeFormatter);

		if (createDateLocalDateTime.isAfter(overdueLocalDateTime)) {
			return false;
		}

		if (Validator.isNull(document.getDate("completionDate"))) {
			return true;
		}

		LocalDateTime completionDateLocalDateTime = LocalDateTime.parse(
			document.getDate("completionDate"), _dateTimeFormatter);

		return completionDateLocalDateTime.isAfter(overdueLocalDateTime);
	}

	protected boolean isOnTime(
		Document document, LocalDateTime nowLocalDateTime,
		LocalDateTime overdueLocalDateTime) {

		if (nowLocalDateTime.isBefore(overdueLocalDateTime)) {
			return true;
		}

		LocalDateTime createDateLocalDateTime = LocalDateTime.parse(
			document.getDate("createDate"), _dateTimeFormatter);

		if (createDateLocalDateTime.isAfter(overdueLocalDateTime) ||
			Validator.isNull(document.getDate("completionDate"))) {

			return false;
		}

		LocalDateTime completionDateLocalDateTime = LocalDateTime.parse(
			document.getDate("completionDate"), _dateTimeFormatter);

		if (completionDateLocalDateTime.isBefore(overdueLocalDateTime) ||
			Objects.equals(completionDateLocalDateTime, overdueLocalDateTime)) {

			return true;
		}

		return false;
	}

	protected static class TaskInterval {

		public LocalDateTime getEndLocalDateTime() {
			return _endLocalDateTime;
		}

		public LocalDateTime getStartLocalDateTime() {
			return _startLocalDateTime;
		}

		public void setEndLocalDateTime(LocalDateTime endLocalDateTime) {
			_endLocalDateTime = endLocalDateTime;
		}

		public void setStartLocalDateTime(LocalDateTime startLocalDateTime) {
			_startLocalDateTime = startLocalDateTime;
		}

		private LocalDateTime _endLocalDateTime;
		private LocalDateTime _startLocalDateTime;

	}

	private long _computeElapsedTime(
		LocalDateTime endLocalDateTime, LocalDateTime starLocalDateTime,
		WorkflowMetricsSLACalendar workflowMetricsSLACalendar,
		WorkflowMetricsSLAStopwatch workflowMetricsSLAStopwatch) {

		long elapsedTime = 0;

		for (TaskInterval taskInterval :
				workflowMetricsSLAStopwatch.getTaskIntervals()) {

			if (endLocalDateTime.isBefore(taskInterval._startLocalDateTime) ||
				starLocalDateTime.isAfter(taskInterval._endLocalDateTime)) {

				continue;
			}

			Duration duration = workflowMetricsSLACalendar.getDuration(
				_getMaxLocalDateTime(
					starLocalDateTime, taskInterval._startLocalDateTime),
				_getMinLocalDateTime(
					endLocalDateTime, taskInterval._endLocalDateTime));

			elapsedTime += duration.toMillis();
		}

		return elapsedTime;
	}

	private WorkflowMetricsSLAInstanceResult
		_createWorkflowMetricsSLAInstanceResult(
			long companyId, LocalDateTime completionLocalDateTime,
			List<Document> documents, long elapsedTime,
			LocalDateTime endLocalDateTime, long instanceId,
			LocalDateTime nowLocalDateTime,
			WorkflowMetricsSLACalendar workflowMetricsSLACalendar,
			WorkflowMetricsSLADefinitionVersion
				workflowMetricsSLADefinitionVersion,
			WorkflowMetricsSLAStatus workflowMetricsSLAStatus) {

		WorkflowMetricsSLAInstanceResult workflowMetricsSLAInstanceResult =
			new WorkflowMetricsSLAInstanceResult() {
				{
					setCompanyId(companyId);

					if (completionLocalDateTime != null) {
						setCompletionLocalDateTime(completionLocalDateTime);
					}

					setElapsedTime(elapsedTime);
					setInstanceId(instanceId);
					setModifiedLocalDateTime(nowLocalDateTime);
					setOnTime(
						elapsedTime <=
							workflowMetricsSLADefinitionVersion.getDuration());

					long remainingTime =
						workflowMetricsSLADefinitionVersion.getDuration() -
							elapsedTime;

					setOverdueLocalDateTime(
						workflowMetricsSLACalendar.getOverdueLocalDateTime(
							endLocalDateTime,
							Duration.ofMillis(remainingTime)));

					setProcessId(
						workflowMetricsSLADefinitionVersion.getProcessId());
					setRemainingTime(remainingTime);
					setSLADefinitionId(
						workflowMetricsSLADefinitionVersion.
							getWorkflowMetricsSLADefinitionId());
					setWorkflowMetricsSLAStatus(workflowMetricsSLAStatus);
				}
			};

		workflowMetricsSLAInstanceResult.setWorkflowMetricsSLATaskResults(
			_createWorkflowMetricsSLATaskResults(
				documents, completionLocalDateTime != null,
				completionLocalDateTime, nowLocalDateTime,
				workflowMetricsSLAInstanceResult));

		return workflowMetricsSLAInstanceResult;
	}

	private WorkflowMetricsSLAStopwatch _createWorkflowMetricsSLAStopwatch(
		LocalDateTime createDateLocalDateTime, List<Document> documents,
		LocalDateTime lastCheckLocalDateTime, long startNodeId,
		WorkflowMetricsSLADefinitionVersion workflowMetricsSLADefinitionVersion,
		WorkflowMetricsSLAStatus workflowMetricsSLAStatus) {

		WorkflowMetricsSLAStopwatch workflowMetricsSLAStopwatch =
			new WorkflowMetricsSLAStopwatch(workflowMetricsSLAStatus);

		if (Objects.equals(
				workflowMetricsSLAStatus, WorkflowMetricsSLAStatus.STOPPED)) {

			return workflowMetricsSLAStopwatch;
		}

		Map<Long, String> startTimeMarkers = _getTimeMarkers(
			StringUtil.split(
				workflowMetricsSLADefinitionVersion.getStartNodeKeys()));

		if (Objects.equals(
				workflowMetricsSLAStatus, WorkflowMetricsSLAStatus.NEW) &&
			startTimeMarkers.containsKey(startNodeId)) {

			workflowMetricsSLAStopwatch.run(createDateLocalDateTime);
		}
		else if (Objects.equals(
					workflowMetricsSLAStatus,
					WorkflowMetricsSLAStatus.RUNNING)) {

			workflowMetricsSLAStopwatch.run(lastCheckLocalDateTime);
		}

		if (ListUtil.isEmpty(documents)) {
			return workflowMetricsSLAStopwatch;
		}

		Map<Long, String> pauseTimeMarkers = _getTimeMarkers(
			StringUtil.split(
				workflowMetricsSLADefinitionVersion.getPauseNodeKeys()));
		Map<Long, String> stopTimeMarkers = _getTimeMarkers(
			StringUtil.split(
				workflowMetricsSLADefinitionVersion.getStopNodeKeys()));

		Iterator<Document> iterator = documents.iterator();

		while (iterator.hasNext() && !workflowMetricsSLAStopwatch.isStopped()) {
			Document document = iterator.next();

			long nodeId = document.getLong("nodeId");

			TaskInterval taskInterval = _toTaskInterval(
				document, lastCheckLocalDateTime, null);

			if (pauseTimeMarkers.containsKey(nodeId) &&
				!stopTimeMarkers.containsKey(nodeId)) {

				workflowMetricsSLAStopwatch.pause(
					taskInterval._startLocalDateTime);

				if (taskInterval._endLocalDateTime != null) {
					workflowMetricsSLAStopwatch.run(
						taskInterval._endLocalDateTime);
				}
			}

			String startTimeMarker = startTimeMarkers.get(nodeId);

			if (startTimeMarker != null) {
				if (Objects.equals(startTimeMarker, "enter")) {
					workflowMetricsSLAStopwatch.run(
						taskInterval._startLocalDateTime);
				}
				else if (Objects.equals(startTimeMarker, "leave") &&
						 (taskInterval._endLocalDateTime != null)) {

					workflowMetricsSLAStopwatch.run(
						taskInterval._endLocalDateTime);
				}
			}

			String stopTimeMarker = stopTimeMarkers.get(nodeId);

			if (stopTimeMarker != null) {
				if (Objects.equals(stopTimeMarker, "enter")) {
					workflowMetricsSLAStopwatch.stop(
						taskInterval._startLocalDateTime);
				}
				else if (Objects.equals(stopTimeMarker, "leave") &&
						 (taskInterval._endLocalDateTime != null)) {

					workflowMetricsSLAStopwatch.stop(
						taskInterval._endLocalDateTime);
				}
			}
		}

		return workflowMetricsSLAStopwatch;
	}

	private WorkflowMetricsSLATaskResult _createWorkflowMetricsSLATaskResult(
		Document document, boolean instanceCompleted,
		LocalDateTime instanceCompletionLocalDateTime,
		LocalDateTime nowLocalDateTime,
		WorkflowMetricsSLAInstanceResult workflowMetricsSLAInstanceResult) {

		return new WorkflowMetricsSLATaskResult() {
			{
				List<Long> assigneeIds = document.getLongs("assigneeIds");

				if (assigneeIds != null) {
					setAssigneeIds(assigneeIds.toArray(new Long[0]));
					setAssigneeType(document.getString("assigneeType"));
				}

				setBreached(
					WorkflowMetricsSLAProcessor.this.isBreached(
						document, nowLocalDateTime,
						workflowMetricsSLAInstanceResult.
							getOverdueLocalDateTime()));
				setCompanyId(workflowMetricsSLAInstanceResult.getCompanyId());

				if (Validator.isNotNull(document.getDate("completionDate"))) {
					setCompletionLocalDateTime(
						LocalDateTime.parse(
							document.getDate("completionDate"),
							_dateTimeFormatter));
				}

				if (Validator.isNotNull(document.getLong("completionUserId"))) {
					setCompletionUserId(document.getLong("completionUserId"));
				}

				setInstanceCompleted(instanceCompleted);
				setInstanceCompletionLocalDateTime(
					instanceCompletionLocalDateTime);
				setInstanceId(workflowMetricsSLAInstanceResult.getInstanceId());
				setModifiedLocalDateTime(
					workflowMetricsSLAInstanceResult.
						getModifiedLocalDateTime());
				setNodeId(document.getLong("nodeId"));
				setOnTime(
					WorkflowMetricsSLAProcessor.this.isOnTime(
						document, nowLocalDateTime,
						workflowMetricsSLAInstanceResult.
							getOverdueLocalDateTime()));
				setProcessId(workflowMetricsSLAInstanceResult.getProcessId());
				setSLADefinitionId(
					workflowMetricsSLAInstanceResult.getSLADefinitionId());
				setTaskId(document.getLong("taskId"));
				setTaskName(document.getString("name"));
				setWorkflowMetricsSLAStatus(
					_getWorkflowMetricsSLAStatus(
						document, workflowMetricsSLAInstanceResult));
			}
		};
	}

	private List<WorkflowMetricsSLATaskResult>
		_createWorkflowMetricsSLATaskResults(
			List<Document> documents, boolean instanceCompleted,
			LocalDateTime instanceCompletionLocalDateTime,
			LocalDateTime nowLocalDateTime,
			WorkflowMetricsSLAInstanceResult workflowMetricsSLAInstanceResult) {

		if (ListUtil.isEmpty(documents)) {
			return Collections.emptyList();
		}

		return TransformUtil.transform(
			documents,
			document -> _createWorkflowMetricsSLATaskResult(
				document, instanceCompleted, instanceCompletionLocalDateTime,
				nowLocalDateTime, workflowMetricsSLAInstanceResult));
	}

	private LocalDateTime _getMaxLocalDateTime(
		LocalDateTime localDateTime1, LocalDateTime localDateTime2) {

		if (localDateTime1.isAfter(localDateTime2)) {
			return localDateTime1;
		}

		return localDateTime2;
	}

	private LocalDateTime _getMinLocalDateTime(
		LocalDateTime localDateTime1, LocalDateTime localDateTime2) {

		if (localDateTime1.isBefore(localDateTime2)) {
			return localDateTime1;
		}

		return localDateTime2;
	}

	private Map<Long, String> _getTimeMarkers(List<String> nodeKeys) {
		Map<Long, String> map = new HashMap<>();

		for (String nodeKey : nodeKeys) {
			List<String> parts = StringUtil.split(nodeKey, CharPool.COLON);

			long nodeId = GetterUtil.getLong(parts.get(0));

			if (parts.size() == 1) {
				map.put(nodeId, StringPool.BLANK);
			}
			else {
				map.put(nodeId, parts.get(1));
			}
		}

		return map;
	}

	private WorkflowMetricsSLAStatus _getWorkflowMetricsSLAStatus(
		Document document,
		WorkflowMetricsSLAInstanceResult workflowMetricsSLAInstanceResult) {

		if (Objects.equals(
				workflowMetricsSLAInstanceResult.getWorkflowMetricsSLAStatus(),
				WorkflowMetricsSLAStatus.NEW)) {

			return WorkflowMetricsSLAStatus.NEW;
		}

		if (GetterUtil.getBoolean(document.getBoolean("completed")) ||
			(workflowMetricsSLAInstanceResult.getCompletionLocalDateTime() !=
				null) ||
			Objects.equals(
				workflowMetricsSLAInstanceResult.getWorkflowMetricsSLAStatus(),
				WorkflowMetricsSLAStatus.COMPLETED)) {

			return WorkflowMetricsSLAStatus.COMPLETED;
		}

		return WorkflowMetricsSLAStatus.RUNNING;
	}

	private TaskInterval _toTaskInterval(
		Document document, LocalDateTime lastCheckLocalDateTime,
		LocalDateTime nowLocalDateTime) {

		TaskInterval taskInterval = new TaskInterval();

		if (Validator.isNull(document.getDate("completionDate"))) {
			taskInterval._endLocalDateTime = nowLocalDateTime;
		}
		else {
			taskInterval._endLocalDateTime = LocalDateTime.parse(
				document.getDate("completionDate"), _dateTimeFormatter);
		}

		LocalDateTime createDateLocalDateTime = LocalDateTime.parse(
			document.getDate("createDate"), _dateTimeFormatter);

		if ((lastCheckLocalDateTime != null) &&
			lastCheckLocalDateTime.isAfter(createDateLocalDateTime)) {

			taskInterval._startLocalDateTime = lastCheckLocalDateTime;
		}
		else {
			taskInterval._startLocalDateTime = createDateLocalDateTime;
		}

		return taskInterval;
	}

	private List<TaskInterval> _toTaskIntervals(
		List<Document> documents, LocalDateTime lastCheckLocalDateTime,
		LocalDateTime nowLocalDateTime) {

		if (ListUtil.isEmpty(documents)) {
			return Collections.emptyList();
		}

		Stack<TaskInterval> taskIntervals = new Stack<>();

		taskIntervals.push(
			_toTaskInterval(
				documents.get(0), lastCheckLocalDateTime, nowLocalDateTime));

		for (Document document : documents) {
			TaskInterval topTaskInterval = taskIntervals.peek();

			TaskInterval taskInterval = _toTaskInterval(
				document, lastCheckLocalDateTime, nowLocalDateTime);

			LocalDateTime topEndLocalDateTime =
				topTaskInterval.getEndLocalDateTime();

			if (topEndLocalDateTime.isBefore(
					taskInterval.getStartLocalDateTime())) {

				taskIntervals.push(taskInterval);
			}
			else if (topEndLocalDateTime.isBefore(
						taskInterval.getEndLocalDateTime())) {

				topTaskInterval._endLocalDateTime =
					taskInterval.getEndLocalDateTime();
			}
		}

		return taskIntervals;
	}

	private final DateTimeFormatter _dateTimeFormatter =
		DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

}