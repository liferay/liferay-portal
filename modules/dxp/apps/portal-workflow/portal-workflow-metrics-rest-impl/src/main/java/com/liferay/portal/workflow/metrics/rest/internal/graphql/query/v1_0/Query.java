/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.internal.graphql.query.v1_0;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLTypeExtension;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Calendar;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.HistogramMetric;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Index;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Instance;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Node;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.NodeMetric;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Process;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.ProcessMetric;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.ProcessVersion;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.ReindexStatus;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Role;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.SLA;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.SLAResult;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Task;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.TaskBulkSelection;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.TimeRange;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.CalendarResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.HistogramMetricResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.IndexResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.InstanceResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.NodeMetricResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.NodeResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.ProcessMetricResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.ProcessResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.ProcessVersionResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.ReindexStatusResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.RoleResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.SLAResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.SLAResultResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.TaskResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.TimeRangeResource;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import java.util.Date;
import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class Query {

	public static void setCalendarResourceComponentServiceObjects(
		ComponentServiceObjects<CalendarResource>
			calendarResourceComponentServiceObjects) {

		_calendarResourceComponentServiceObjects =
			calendarResourceComponentServiceObjects;
	}

	public static void setHistogramMetricResourceComponentServiceObjects(
		ComponentServiceObjects<HistogramMetricResource>
			histogramMetricResourceComponentServiceObjects) {

		_histogramMetricResourceComponentServiceObjects =
			histogramMetricResourceComponentServiceObjects;
	}

	public static void setIndexResourceComponentServiceObjects(
		ComponentServiceObjects<IndexResource>
			indexResourceComponentServiceObjects) {

		_indexResourceComponentServiceObjects =
			indexResourceComponentServiceObjects;
	}

	public static void setInstanceResourceComponentServiceObjects(
		ComponentServiceObjects<InstanceResource>
			instanceResourceComponentServiceObjects) {

		_instanceResourceComponentServiceObjects =
			instanceResourceComponentServiceObjects;
	}

	public static void setNodeResourceComponentServiceObjects(
		ComponentServiceObjects<NodeResource>
			nodeResourceComponentServiceObjects) {

		_nodeResourceComponentServiceObjects =
			nodeResourceComponentServiceObjects;
	}

	public static void setNodeMetricResourceComponentServiceObjects(
		ComponentServiceObjects<NodeMetricResource>
			nodeMetricResourceComponentServiceObjects) {

		_nodeMetricResourceComponentServiceObjects =
			nodeMetricResourceComponentServiceObjects;
	}

	public static void setProcessResourceComponentServiceObjects(
		ComponentServiceObjects<ProcessResource>
			processResourceComponentServiceObjects) {

		_processResourceComponentServiceObjects =
			processResourceComponentServiceObjects;
	}

	public static void setProcessMetricResourceComponentServiceObjects(
		ComponentServiceObjects<ProcessMetricResource>
			processMetricResourceComponentServiceObjects) {

		_processMetricResourceComponentServiceObjects =
			processMetricResourceComponentServiceObjects;
	}

	public static void setProcessVersionResourceComponentServiceObjects(
		ComponentServiceObjects<ProcessVersionResource>
			processVersionResourceComponentServiceObjects) {

		_processVersionResourceComponentServiceObjects =
			processVersionResourceComponentServiceObjects;
	}

	public static void setReindexStatusResourceComponentServiceObjects(
		ComponentServiceObjects<ReindexStatusResource>
			reindexStatusResourceComponentServiceObjects) {

		_reindexStatusResourceComponentServiceObjects =
			reindexStatusResourceComponentServiceObjects;
	}

	public static void setRoleResourceComponentServiceObjects(
		ComponentServiceObjects<RoleResource>
			roleResourceComponentServiceObjects) {

		_roleResourceComponentServiceObjects =
			roleResourceComponentServiceObjects;
	}

	public static void setSLAResourceComponentServiceObjects(
		ComponentServiceObjects<SLAResource>
			slaResourceComponentServiceObjects) {

		_slaResourceComponentServiceObjects =
			slaResourceComponentServiceObjects;
	}

	public static void setSLAResultResourceComponentServiceObjects(
		ComponentServiceObjects<SLAResultResource>
			slaResultResourceComponentServiceObjects) {

		_slaResultResourceComponentServiceObjects =
			slaResultResourceComponentServiceObjects;
	}

	public static void setTaskResourceComponentServiceObjects(
		ComponentServiceObjects<TaskResource>
			taskResourceComponentServiceObjects) {

		_taskResourceComponentServiceObjects =
			taskResourceComponentServiceObjects;
	}

	public static void setTimeRangeResourceComponentServiceObjects(
		ComponentServiceObjects<TimeRangeResource>
			timeRangeResourceComponentServiceObjects) {

		_timeRangeResourceComponentServiceObjects =
			timeRangeResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {calendars{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public CalendarPage calendars() throws Exception {
		return _applyComponentServiceObjects(
			_calendarResourceComponentServiceObjects,
			this::_populateResourceContext,
			calendarResource -> new CalendarPage(
				calendarResource.getCalendarsPage()));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {processHistogramMetric(dateEnd: ___, dateStart: ___, processId: ___, unit: ___){histograms, unit, value}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public HistogramMetric processHistogramMetric(
			@GraphQLName("processId") Long processId,
			@GraphQLName("dateEnd") Date dateEnd,
			@GraphQLName("dateStart") Date dateStart,
			@GraphQLName("unit") String unit)
		throws Exception {

		return _applyComponentServiceObjects(
			_histogramMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			histogramMetricResource ->
				histogramMetricResource.getProcessHistogramMetric(
					processId, dateEnd, dateStart, unit));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {indexes{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public IndexPage indexes() throws Exception {
		return _applyComponentServiceObjects(
			_indexResourceComponentServiceObjects,
			this::_populateResourceContext,
			indexResource -> new IndexPage(indexResource.getIndexesPage()));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {processInstance(instanceId: ___, processId: ___){active, assetTitle, assetTitle_i18n, assetType, assetType_i18n, assignees, className, classPK, completed, creator, dateCompletion, dateCreated, dateModified, duration, id, processId, processVersion, slaResults, slaStatus, taskNames, transitions}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Instance processInstance(
			@GraphQLName("processId") Long processId,
			@GraphQLName("instanceId") Long instanceId)
		throws Exception {

		return _applyComponentServiceObjects(
			_instanceResourceComponentServiceObjects,
			this::_populateResourceContext,
			instanceResource -> instanceResource.getProcessInstance(
				processId, instanceId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {processInstances(assigneeIds: ___, classPKs: ___, dateEnd: ___, dateStart: ___, page: ___, pageSize: ___, processId: ___, slaStatuses: ___, sorts: ___, statuses: ___, taskNames: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public InstancePage processInstances(
			@GraphQLName("processId") Long processId,
			@GraphQLName("assigneeIds") Long[] assigneeIds,
			@GraphQLName("classPKs") Long[] classPKs,
			@GraphQLName("dateEnd") Date dateEnd,
			@GraphQLName("dateStart") Date dateStart,
			@GraphQLName("slaStatuses") String[] slaStatuses,
			@GraphQLName("statuses") String[] statuses,
			@GraphQLName("taskNames") String[] taskNames,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_instanceResourceComponentServiceObjects,
			this::_populateResourceContext,
			instanceResource -> new InstancePage(
				instanceResource.getProcessInstancesPage(
					processId, assigneeIds, classPKs, dateEnd, dateStart,
					slaStatuses, statuses, taskNames,
					Pagination.of(page, pageSize),
					_sortsBiFunction.apply(instanceResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {processNodes(processId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public NodePage processNodes(@GraphQLName("processId") Long processId)
		throws Exception {

		return _applyComponentServiceObjects(
			_nodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			nodeResource -> new NodePage(
				nodeResource.getProcessNodesPage(processId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {processNodeMetrics(completed: ___, dateEnd: ___, dateStart: ___, key: ___, page: ___, pageSize: ___, processId: ___, processVersion: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public NodeMetricPage processNodeMetrics(
			@GraphQLName("processId") Long processId,
			@GraphQLName("completed") Boolean completed,
			@GraphQLName("dateEnd") Date dateEnd,
			@GraphQLName("dateStart") Date dateStart,
			@GraphQLName("key") String key,
			@GraphQLName("processVersion") String processVersion,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_nodeMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			nodeMetricResource -> new NodeMetricPage(
				nodeMetricResource.getProcessNodeMetricsPage(
					processId, completed, dateEnd, dateStart, key,
					processVersion, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(nodeMetricResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {process(processId: ___){active, dateCreated, dateModified, description, id, name, title, title_i18n, version}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Process process(@GraphQLName("processId") Long processId)
		throws Exception {

		return _applyComponentServiceObjects(
			_processResourceComponentServiceObjects,
			this::_populateResourceContext,
			processResource -> processResource.getProcess(processId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {processTitle(processId: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public String processTitle(@GraphQLName("processId") Long processId)
		throws Exception {

		return _applyComponentServiceObjects(
			_processResourceComponentServiceObjects,
			this::_populateResourceContext,
			processResource -> processResource.getProcessTitle(processId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {processMetric(completed: ___, dateEnd: ___, dateStart: ___, processId: ___){instanceCount, onTimeInstanceCount, overdueInstanceCount, process, untrackedInstanceCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProcessMetric processMetric(
			@GraphQLName("processId") Long processId,
			@GraphQLName("completed") Boolean completed,
			@GraphQLName("dateEnd") Date dateEnd,
			@GraphQLName("dateStart") Date dateStart)
		throws Exception {

		return _applyComponentServiceObjects(
			_processMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			processMetricResource -> processMetricResource.getProcessMetric(
				processId, completed, dateEnd, dateStart));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {processMetrics(page: ___, pageSize: ___, sorts: ___, title: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProcessMetricPage processMetrics(
			@GraphQLName("title") String title,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_processMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			processMetricResource -> new ProcessMetricPage(
				processMetricResource.getProcessMetricsPage(
					title, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						processMetricResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {processProcessVersions(processId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ProcessVersionPage processProcessVersions(
			@GraphQLName("processId") Long processId)
		throws Exception {

		return _applyComponentServiceObjects(
			_processVersionResourceComponentServiceObjects,
			this::_populateResourceContext,
			processVersionResource -> new ProcessVersionPage(
				processVersionResource.getProcessProcessVersionsPage(
					processId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {reindexStatuses{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public ReindexStatusPage reindexStatuses() throws Exception {
		return _applyComponentServiceObjects(
			_reindexStatusResourceComponentServiceObjects,
			this::_populateResourceContext,
			reindexStatusResource -> new ReindexStatusPage(
				reindexStatusResource.getReindexStatusesPage()));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {processRoles(completed: ___, processId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public RolePage processRoles(
			@GraphQLName("processId") Long processId,
			@GraphQLName("completed") Boolean completed)
		throws Exception {

		return _applyComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> new RolePage(
				roleResource.getProcessRolesPage(processId, completed)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {processSLAs(page: ___, pageSize: ___, processId: ___, status: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SLAPage processSLAs(
			@GraphQLName("processId") Long processId,
			@GraphQLName("status") Integer status,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_slaResourceComponentServiceObjects, this::_populateResourceContext,
			slaResource -> new SLAPage(
				slaResource.getProcessSLAsPage(
					processId, status, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {sLA(slaId: ___){calendarKey, dateModified, description, duration, id, name, pauseNodeKeys, processId, startNodeKeys, status, stopNodeKeys}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SLA sLA(@GraphQLName("slaId") Long slaId) throws Exception {
		return _applyComponentServiceObjects(
			_slaResourceComponentServiceObjects, this::_populateResourceContext,
			slaResource -> slaResource.getSLA(slaId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {processLastSLAResult(processId: ___){dateModified, dateOverdue, id, name, onTime, remainingTime, status}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public SLAResult processLastSLAResult(
			@GraphQLName("processId") Long processId)
		throws Exception {

		return _applyComponentServiceObjects(
			_slaResultResourceComponentServiceObjects,
			this::_populateResourceContext,
			slaResultResource -> slaResultResource.getProcessLastSLAResult(
				processId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {processTask(processId: ___, taskId: ___){assetTitle, assetTitle_i18n, assetType, assetType_i18n, assignee, className, classPK, completed, completionUserId, dateCompletion, dateCreated, dateModified, duration, id, instanceId, label, name, nodeId, processId, processVersion}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Task processTask(
			@GraphQLName("processId") Long processId,
			@GraphQLName("taskId") Long taskId)
		throws Exception {

		return _applyComponentServiceObjects(
			_taskResourceComponentServiceObjects,
			this::_populateResourceContext,
			taskResource -> taskResource.getProcessTask(processId, taskId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {processTasks(processId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TaskPage processTasks(@GraphQLName("processId") Long processId)
		throws Exception {

		return _applyComponentServiceObjects(
			_taskResourceComponentServiceObjects,
			this::_populateResourceContext,
			taskResource -> new TaskPage(
				taskResource.getProcessTasksPage(processId)));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {timeRanges{items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TimeRangePage timeRanges() throws Exception {
		return _applyComponentServiceObjects(
			_timeRangeResourceComponentServiceObjects,
			this::_populateResourceContext,
			timeRangeResource -> new TimeRangePage(
				timeRangeResource.getTimeRangesPage()));
	}

	@GraphQLTypeExtension(Process.class)
	public class GetProcessInstanceTypeExtension {

		public GetProcessInstanceTypeExtension(Process process) {
			_process = process;
		}

		@GraphQLField
		public Instance instance(@GraphQLName("instanceId") Long instanceId)
			throws Exception {

			return _applyComponentServiceObjects(
				_instanceResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				instanceResource -> instanceResource.getProcessInstance(
					_process.getId(), instanceId));
		}

		private Process _process;

	}

	@GraphQLTypeExtension(TaskBulkSelection.class)
	public class GetProcessTypeExtension {

		public GetProcessTypeExtension(TaskBulkSelection taskBulkSelection) {
			_taskBulkSelection = taskBulkSelection;
		}

		@GraphQLField
		public Process process() throws Exception {
			return _applyComponentServiceObjects(
				_processResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				processResource -> processResource.getProcess(
					_taskBulkSelection.getProcessId()));
		}

		private TaskBulkSelection _taskBulkSelection;

	}

	@GraphQLTypeExtension(Process.class)
	public class GetProcessNodesPageTypeExtension {

		public GetProcessNodesPageTypeExtension(Process process) {
			_process = process;
		}

		@GraphQLField
		public NodePage nodes() throws Exception {
			return _applyComponentServiceObjects(
				_nodeResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				nodeResource -> new NodePage(
					nodeResource.getProcessNodesPage(_process.getId())));
		}

		private Process _process;

	}

	@GraphQLTypeExtension(Process.class)
	public class GetProcessLastSLAResultTypeExtension {

		public GetProcessLastSLAResultTypeExtension(Process process) {
			_process = process;
		}

		@GraphQLField
		public SLAResult lastSLAResult() throws Exception {
			return _applyComponentServiceObjects(
				_slaResultResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				slaResultResource -> slaResultResource.getProcessLastSLAResult(
					_process.getId()));
		}

		private Process _process;

	}

	@GraphQLTypeExtension(Process.class)
	public class GetProcessHistogramMetricTypeExtension {

		public GetProcessHistogramMetricTypeExtension(Process process) {
			_process = process;
		}

		@GraphQLField
		public HistogramMetric histogramMetric(
				@GraphQLName("dateEnd") Date dateEnd,
				@GraphQLName("dateStart") Date dateStart,
				@GraphQLName("unit") String unit)
			throws Exception {

			return _applyComponentServiceObjects(
				_histogramMetricResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				histogramMetricResource ->
					histogramMetricResource.getProcessHistogramMetric(
						_process.getId(), dateEnd, dateStart, unit));
		}

		private Process _process;

	}

	@GraphQLTypeExtension(Process.class)
	public class GetProcessTasksPageTypeExtension {

		public GetProcessTasksPageTypeExtension(Process process) {
			_process = process;
		}

		@GraphQLField
		public TaskPage tasks() throws Exception {
			return _applyComponentServiceObjects(
				_taskResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				taskResource -> new TaskPage(
					taskResource.getProcessTasksPage(_process.getId())));
		}

		private Process _process;

	}

	@GraphQLTypeExtension(Process.class)
	public class GetProcessSLAsPageTypeExtension {

		public GetProcessSLAsPageTypeExtension(Process process) {
			_process = process;
		}

		@GraphQLField
		public SLAPage sLAs(
				@GraphQLName("status") Integer status,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_slaResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				slaResource -> new SLAPage(
					slaResource.getProcessSLAsPage(
						_process.getId(), status,
						Pagination.of(page, pageSize))));
		}

		private Process _process;

	}

	@GraphQLTypeExtension(Process.class)
	public class GetProcessRolesPageTypeExtension {

		public GetProcessRolesPageTypeExtension(Process process) {
			_process = process;
		}

		@GraphQLField
		public RolePage roles(@GraphQLName("completed") Boolean completed)
			throws Exception {

			return _applyComponentServiceObjects(
				_roleResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				roleResource -> new RolePage(
					roleResource.getProcessRolesPage(
						_process.getId(), completed)));
		}

		private Process _process;

	}

	@GraphQLTypeExtension(Process.class)
	public class GetProcessMetricTypeExtension {

		public GetProcessMetricTypeExtension(Process process) {
			_process = process;
		}

		@GraphQLField
		public ProcessMetric metric(
				@GraphQLName("completed") Boolean completed,
				@GraphQLName("dateEnd") Date dateEnd,
				@GraphQLName("dateStart") Date dateStart)
			throws Exception {

			return _applyComponentServiceObjects(
				_processMetricResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				processMetricResource -> processMetricResource.getProcessMetric(
					_process.getId(), completed, dateEnd, dateStart));
		}

		private Process _process;

	}

	@GraphQLTypeExtension(Process.class)
	public class GetProcessTaskTypeExtension {

		public GetProcessTaskTypeExtension(Process process) {
			_process = process;
		}

		@GraphQLField
		public Task task(@GraphQLName("taskId") Long taskId) throws Exception {
			return _applyComponentServiceObjects(
				_taskResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				taskResource -> taskResource.getProcessTask(
					_process.getId(), taskId));
		}

		private Process _process;

	}

	@GraphQLTypeExtension(Process.class)
	public class GetProcessProcessVersionsPageTypeExtension {

		public GetProcessProcessVersionsPageTypeExtension(Process process) {
			_process = process;
		}

		@GraphQLField
		public ProcessVersionPage processVersions() throws Exception {
			return _applyComponentServiceObjects(
				_processVersionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				processVersionResource -> new ProcessVersionPage(
					processVersionResource.getProcessProcessVersionsPage(
						_process.getId())));
		}

		private Process _process;

	}

	@GraphQLTypeExtension(Process.class)
	public class GetProcessNodeMetricsPageTypeExtension {

		public GetProcessNodeMetricsPageTypeExtension(Process process) {
			_process = process;
		}

		@GraphQLField
		public NodeMetricPage nodeMetrics(
				@GraphQLName("completed") Boolean completed,
				@GraphQLName("dateEnd") Date dateEnd,
				@GraphQLName("dateStart") Date dateStart,
				@GraphQLName("key") String key,
				@GraphQLName("processVersion") String processVersion,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_nodeMetricResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				nodeMetricResource -> new NodeMetricPage(
					nodeMetricResource.getProcessNodeMetricsPage(
						_process.getId(), completed, dateEnd, dateStart, key,
						processVersion, Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							nodeMetricResource, sortsString))));
		}

		private Process _process;

	}

	@GraphQLTypeExtension(Process.class)
	public class GetProcessInstancesPageTypeExtension {

		public GetProcessInstancesPageTypeExtension(Process process) {
			_process = process;
		}

		@GraphQLField
		public InstancePage instances(
				@GraphQLName("assigneeIds") Long[] assigneeIds,
				@GraphQLName("classPKs") Long[] classPKs,
				@GraphQLName("dateEnd") Date dateEnd,
				@GraphQLName("dateStart") Date dateStart,
				@GraphQLName("slaStatuses") String[] slaStatuses,
				@GraphQLName("statuses") String[] statuses,
				@GraphQLName("taskNames") String[] taskNames,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString)
			throws Exception {

			return _applyComponentServiceObjects(
				_instanceResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				instanceResource -> new InstancePage(
					instanceResource.getProcessInstancesPage(
						_process.getId(), assigneeIds, classPKs, dateEnd,
						dateStart, slaStatuses, statuses, taskNames,
						Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							instanceResource, sortsString))));
		}

		private Process _process;

	}

	@GraphQLName("CalendarPage")
	public class CalendarPage {

		public CalendarPage(Page calendarPage) {
			actions = calendarPage.getActions();

			items = calendarPage.getItems();
			lastPage = calendarPage.getLastPage();
			page = calendarPage.getPage();
			pageSize = calendarPage.getPageSize();
			totalCount = calendarPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Calendar> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("HistogramMetricPage")
	public class HistogramMetricPage {

		public HistogramMetricPage(Page histogramMetricPage) {
			actions = histogramMetricPage.getActions();

			items = histogramMetricPage.getItems();
			lastPage = histogramMetricPage.getLastPage();
			page = histogramMetricPage.getPage();
			pageSize = histogramMetricPage.getPageSize();
			totalCount = histogramMetricPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<HistogramMetric> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("IndexPage")
	public class IndexPage {

		public IndexPage(Page indexPage) {
			actions = indexPage.getActions();

			items = indexPage.getItems();
			lastPage = indexPage.getLastPage();
			page = indexPage.getPage();
			pageSize = indexPage.getPageSize();
			totalCount = indexPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Index> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("InstancePage")
	public class InstancePage {

		public InstancePage(Page instancePage) {
			actions = instancePage.getActions();

			items = instancePage.getItems();
			lastPage = instancePage.getLastPage();
			page = instancePage.getPage();
			pageSize = instancePage.getPageSize();
			totalCount = instancePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Instance> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("NodePage")
	public class NodePage {

		public NodePage(Page nodePage) {
			actions = nodePage.getActions();

			items = nodePage.getItems();
			lastPage = nodePage.getLastPage();
			page = nodePage.getPage();
			pageSize = nodePage.getPageSize();
			totalCount = nodePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Node> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("NodeMetricPage")
	public class NodeMetricPage {

		public NodeMetricPage(Page nodeMetricPage) {
			actions = nodeMetricPage.getActions();

			items = nodeMetricPage.getItems();
			lastPage = nodeMetricPage.getLastPage();
			page = nodeMetricPage.getPage();
			pageSize = nodeMetricPage.getPageSize();
			totalCount = nodeMetricPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<NodeMetric> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProcessPage")
	public class ProcessPage {

		public ProcessPage(Page processPage) {
			actions = processPage.getActions();

			items = processPage.getItems();
			lastPage = processPage.getLastPage();
			page = processPage.getPage();
			pageSize = processPage.getPageSize();
			totalCount = processPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Process> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProcessMetricPage")
	public class ProcessMetricPage {

		public ProcessMetricPage(Page processMetricPage) {
			actions = processMetricPage.getActions();

			items = processMetricPage.getItems();
			lastPage = processMetricPage.getLastPage();
			page = processMetricPage.getPage();
			pageSize = processMetricPage.getPageSize();
			totalCount = processMetricPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProcessMetric> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ProcessVersionPage")
	public class ProcessVersionPage {

		public ProcessVersionPage(Page processVersionPage) {
			actions = processVersionPage.getActions();

			items = processVersionPage.getItems();
			lastPage = processVersionPage.getLastPage();
			page = processVersionPage.getPage();
			pageSize = processVersionPage.getPageSize();
			totalCount = processVersionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ProcessVersion> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("ReindexStatusPage")
	public class ReindexStatusPage {

		public ReindexStatusPage(Page reindexStatusPage) {
			actions = reindexStatusPage.getActions();

			items = reindexStatusPage.getItems();
			lastPage = reindexStatusPage.getLastPage();
			page = reindexStatusPage.getPage();
			pageSize = reindexStatusPage.getPageSize();
			totalCount = reindexStatusPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<ReindexStatus> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("RolePage")
	public class RolePage {

		public RolePage(Page rolePage) {
			actions = rolePage.getActions();

			items = rolePage.getItems();
			lastPage = rolePage.getLastPage();
			page = rolePage.getPage();
			pageSize = rolePage.getPageSize();
			totalCount = rolePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Role> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SLAPage")
	public class SLAPage {

		public SLAPage(Page slaPage) {
			actions = slaPage.getActions();

			items = slaPage.getItems();
			lastPage = slaPage.getLastPage();
			page = slaPage.getPage();
			pageSize = slaPage.getPageSize();
			totalCount = slaPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<SLA> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("SLAResultPage")
	public class SLAResultPage {

		public SLAResultPage(Page slaResultPage) {
			actions = slaResultPage.getActions();

			items = slaResultPage.getItems();
			lastPage = slaResultPage.getLastPage();
			page = slaResultPage.getPage();
			pageSize = slaResultPage.getPageSize();
			totalCount = slaResultPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<SLAResult> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TaskPage")
	public class TaskPage {

		public TaskPage(Page taskPage) {
			actions = taskPage.getActions();

			items = taskPage.getItems();
			lastPage = taskPage.getLastPage();
			page = taskPage.getPage();
			pageSize = taskPage.getPageSize();
			totalCount = taskPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Task> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TimeRangePage")
	public class TimeRangePage {

		public TimeRangePage(Page timeRangePage) {
			actions = timeRangePage.getActions();

			items = timeRangePage.getItems();
			lastPage = timeRangePage.getLastPage();
			page = timeRangePage.getPage();
			pageSize = timeRangePage.getPageSize();
			totalCount = timeRangePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<TimeRange> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	private <T, R, E1 extends Throwable, E2 extends Throwable> R
			_applyComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeFunction<T, R, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			return unsafeFunction.apply(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(CalendarResource calendarResource)
		throws Exception {

		calendarResource.setContextAcceptLanguage(_acceptLanguage);
		calendarResource.setContextCompany(_company);
		calendarResource.setContextHttpServletRequest(_httpServletRequest);
		calendarResource.setContextHttpServletResponse(_httpServletResponse);
		calendarResource.setContextUriInfo(_uriInfo);
		calendarResource.setContextUser(_user);
		calendarResource.setGroupLocalService(_groupLocalService);
		calendarResource.setResourceActionLocalService(
			_resourceActionLocalService);
		calendarResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		calendarResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			HistogramMetricResource histogramMetricResource)
		throws Exception {

		histogramMetricResource.setContextAcceptLanguage(_acceptLanguage);
		histogramMetricResource.setContextCompany(_company);
		histogramMetricResource.setContextHttpServletRequest(
			_httpServletRequest);
		histogramMetricResource.setContextHttpServletResponse(
			_httpServletResponse);
		histogramMetricResource.setContextUriInfo(_uriInfo);
		histogramMetricResource.setContextUser(_user);
		histogramMetricResource.setGroupLocalService(_groupLocalService);
		histogramMetricResource.setResourceActionLocalService(
			_resourceActionLocalService);
		histogramMetricResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		histogramMetricResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(IndexResource indexResource)
		throws Exception {

		indexResource.setContextAcceptLanguage(_acceptLanguage);
		indexResource.setContextCompany(_company);
		indexResource.setContextHttpServletRequest(_httpServletRequest);
		indexResource.setContextHttpServletResponse(_httpServletResponse);
		indexResource.setContextUriInfo(_uriInfo);
		indexResource.setContextUser(_user);
		indexResource.setGroupLocalService(_groupLocalService);
		indexResource.setResourceActionLocalService(
			_resourceActionLocalService);
		indexResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		indexResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(InstanceResource instanceResource)
		throws Exception {

		instanceResource.setContextAcceptLanguage(_acceptLanguage);
		instanceResource.setContextCompany(_company);
		instanceResource.setContextHttpServletRequest(_httpServletRequest);
		instanceResource.setContextHttpServletResponse(_httpServletResponse);
		instanceResource.setContextUriInfo(_uriInfo);
		instanceResource.setContextUser(_user);
		instanceResource.setGroupLocalService(_groupLocalService);
		instanceResource.setResourceActionLocalService(
			_resourceActionLocalService);
		instanceResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		instanceResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(NodeResource nodeResource)
		throws Exception {

		nodeResource.setContextAcceptLanguage(_acceptLanguage);
		nodeResource.setContextCompany(_company);
		nodeResource.setContextHttpServletRequest(_httpServletRequest);
		nodeResource.setContextHttpServletResponse(_httpServletResponse);
		nodeResource.setContextUriInfo(_uriInfo);
		nodeResource.setContextUser(_user);
		nodeResource.setGroupLocalService(_groupLocalService);
		nodeResource.setResourceActionLocalService(_resourceActionLocalService);
		nodeResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		nodeResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(NodeMetricResource nodeMetricResource)
		throws Exception {

		nodeMetricResource.setContextAcceptLanguage(_acceptLanguage);
		nodeMetricResource.setContextCompany(_company);
		nodeMetricResource.setContextHttpServletRequest(_httpServletRequest);
		nodeMetricResource.setContextHttpServletResponse(_httpServletResponse);
		nodeMetricResource.setContextUriInfo(_uriInfo);
		nodeMetricResource.setContextUser(_user);
		nodeMetricResource.setGroupLocalService(_groupLocalService);
		nodeMetricResource.setResourceActionLocalService(
			_resourceActionLocalService);
		nodeMetricResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		nodeMetricResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(ProcessResource processResource)
		throws Exception {

		processResource.setContextAcceptLanguage(_acceptLanguage);
		processResource.setContextCompany(_company);
		processResource.setContextHttpServletRequest(_httpServletRequest);
		processResource.setContextHttpServletResponse(_httpServletResponse);
		processResource.setContextUriInfo(_uriInfo);
		processResource.setContextUser(_user);
		processResource.setGroupLocalService(_groupLocalService);
		processResource.setResourceActionLocalService(
			_resourceActionLocalService);
		processResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		processResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ProcessMetricResource processMetricResource)
		throws Exception {

		processMetricResource.setContextAcceptLanguage(_acceptLanguage);
		processMetricResource.setContextCompany(_company);
		processMetricResource.setContextHttpServletRequest(_httpServletRequest);
		processMetricResource.setContextHttpServletResponse(
			_httpServletResponse);
		processMetricResource.setContextUriInfo(_uriInfo);
		processMetricResource.setContextUser(_user);
		processMetricResource.setGroupLocalService(_groupLocalService);
		processMetricResource.setResourceActionLocalService(
			_resourceActionLocalService);
		processMetricResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		processMetricResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ProcessVersionResource processVersionResource)
		throws Exception {

		processVersionResource.setContextAcceptLanguage(_acceptLanguage);
		processVersionResource.setContextCompany(_company);
		processVersionResource.setContextHttpServletRequest(
			_httpServletRequest);
		processVersionResource.setContextHttpServletResponse(
			_httpServletResponse);
		processVersionResource.setContextUriInfo(_uriInfo);
		processVersionResource.setContextUser(_user);
		processVersionResource.setGroupLocalService(_groupLocalService);
		processVersionResource.setResourceActionLocalService(
			_resourceActionLocalService);
		processVersionResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		processVersionResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			ReindexStatusResource reindexStatusResource)
		throws Exception {

		reindexStatusResource.setContextAcceptLanguage(_acceptLanguage);
		reindexStatusResource.setContextCompany(_company);
		reindexStatusResource.setContextHttpServletRequest(_httpServletRequest);
		reindexStatusResource.setContextHttpServletResponse(
			_httpServletResponse);
		reindexStatusResource.setContextUriInfo(_uriInfo);
		reindexStatusResource.setContextUser(_user);
		reindexStatusResource.setGroupLocalService(_groupLocalService);
		reindexStatusResource.setResourceActionLocalService(
			_resourceActionLocalService);
		reindexStatusResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		reindexStatusResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(RoleResource roleResource)
		throws Exception {

		roleResource.setContextAcceptLanguage(_acceptLanguage);
		roleResource.setContextCompany(_company);
		roleResource.setContextHttpServletRequest(_httpServletRequest);
		roleResource.setContextHttpServletResponse(_httpServletResponse);
		roleResource.setContextUriInfo(_uriInfo);
		roleResource.setContextUser(_user);
		roleResource.setGroupLocalService(_groupLocalService);
		roleResource.setResourceActionLocalService(_resourceActionLocalService);
		roleResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		roleResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(SLAResource slaResource)
		throws Exception {

		slaResource.setContextAcceptLanguage(_acceptLanguage);
		slaResource.setContextCompany(_company);
		slaResource.setContextHttpServletRequest(_httpServletRequest);
		slaResource.setContextHttpServletResponse(_httpServletResponse);
		slaResource.setContextUriInfo(_uriInfo);
		slaResource.setContextUser(_user);
		slaResource.setGroupLocalService(_groupLocalService);
		slaResource.setResourceActionLocalService(_resourceActionLocalService);
		slaResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		slaResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(SLAResultResource slaResultResource)
		throws Exception {

		slaResultResource.setContextAcceptLanguage(_acceptLanguage);
		slaResultResource.setContextCompany(_company);
		slaResultResource.setContextHttpServletRequest(_httpServletRequest);
		slaResultResource.setContextHttpServletResponse(_httpServletResponse);
		slaResultResource.setContextUriInfo(_uriInfo);
		slaResultResource.setContextUser(_user);
		slaResultResource.setGroupLocalService(_groupLocalService);
		slaResultResource.setResourceActionLocalService(
			_resourceActionLocalService);
		slaResultResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		slaResultResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(TaskResource taskResource)
		throws Exception {

		taskResource.setContextAcceptLanguage(_acceptLanguage);
		taskResource.setContextCompany(_company);
		taskResource.setContextHttpServletRequest(_httpServletRequest);
		taskResource.setContextHttpServletResponse(_httpServletResponse);
		taskResource.setContextUriInfo(_uriInfo);
		taskResource.setContextUser(_user);
		taskResource.setGroupLocalService(_groupLocalService);
		taskResource.setResourceActionLocalService(_resourceActionLocalService);
		taskResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		taskResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(TimeRangeResource timeRangeResource)
		throws Exception {

		timeRangeResource.setContextAcceptLanguage(_acceptLanguage);
		timeRangeResource.setContextCompany(_company);
		timeRangeResource.setContextHttpServletRequest(_httpServletRequest);
		timeRangeResource.setContextHttpServletResponse(_httpServletResponse);
		timeRangeResource.setContextUriInfo(_uriInfo);
		timeRangeResource.setContextUser(_user);
		timeRangeResource.setGroupLocalService(_groupLocalService);
		timeRangeResource.setResourceActionLocalService(
			_resourceActionLocalService);
		timeRangeResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		timeRangeResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<CalendarResource>
		_calendarResourceComponentServiceObjects;
	private static ComponentServiceObjects<HistogramMetricResource>
		_histogramMetricResourceComponentServiceObjects;
	private static ComponentServiceObjects<IndexResource>
		_indexResourceComponentServiceObjects;
	private static ComponentServiceObjects<InstanceResource>
		_instanceResourceComponentServiceObjects;
	private static ComponentServiceObjects<NodeResource>
		_nodeResourceComponentServiceObjects;
	private static ComponentServiceObjects<NodeMetricResource>
		_nodeMetricResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProcessResource>
		_processResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProcessMetricResource>
		_processMetricResourceComponentServiceObjects;
	private static ComponentServiceObjects<ProcessVersionResource>
		_processVersionResourceComponentServiceObjects;
	private static ComponentServiceObjects<ReindexStatusResource>
		_reindexStatusResourceComponentServiceObjects;
	private static ComponentServiceObjects<RoleResource>
		_roleResourceComponentServiceObjects;
	private static ComponentServiceObjects<SLAResource>
		_slaResourceComponentServiceObjects;
	private static ComponentServiceObjects<SLAResultResource>
		_slaResultResourceComponentServiceObjects;
	private static ComponentServiceObjects<TaskResource>
		_taskResourceComponentServiceObjects;
	private static ComponentServiceObjects<TimeRangeResource>
		_timeRangeResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private BiFunction
		<Object, String, com.liferay.portal.kernel.search.filter.Filter>
			_filterBiFunction;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private ResourceActionLocalService _resourceActionLocalService;
	private ResourcePermissionLocalService _resourcePermissionLocalService;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;

}