/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.internal.graphql.mutation.v1_0;

import com.liferay.petra.function.UnsafeConsumer;
import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineExportTaskResource;
import com.liferay.portal.vulcan.batch.engine.resource.VulcanBatchEngineImportTaskResource;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Assignee;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.AssigneeBulkSelection;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.AssigneeMetric;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.AssigneeMetricBulkSelection;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Index;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Instance;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Node;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Process;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.SLA;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.Task;
import com.liferay.portal.workflow.metrics.rest.dto.v1_0.TaskBulkSelection;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.AssigneeMetricResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.AssigneeResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.CalendarResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.IndexResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.InstanceResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.NodeResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.ProcessMetricResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.ProcessResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.ProcessVersionResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.ReindexStatusResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.RoleResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.SLAResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.TaskResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.TimeRangeResource;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.Date;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class Mutation {

	public static void setAssigneeResourceComponentServiceObjects(
		ComponentServiceObjects<AssigneeResource>
			assigneeResourceComponentServiceObjects) {

		_assigneeResourceComponentServiceObjects =
			assigneeResourceComponentServiceObjects;
	}

	public static void setAssigneeMetricResourceComponentServiceObjects(
		ComponentServiceObjects<AssigneeMetricResource>
			assigneeMetricResourceComponentServiceObjects) {

		_assigneeMetricResourceComponentServiceObjects =
			assigneeMetricResourceComponentServiceObjects;
	}

	public static void setCalendarResourceComponentServiceObjects(
		ComponentServiceObjects<CalendarResource>
			calendarResourceComponentServiceObjects) {

		_calendarResourceComponentServiceObjects =
			calendarResourceComponentServiceObjects;
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

	@GraphQLField
	public java.util.Collection<Assignee> createProcessAssigneesPage(
			@GraphQLName("processId") Long processId,
			@GraphQLName("assigneeBulkSelection") AssigneeBulkSelection
				assigneeBulkSelection)
		throws Exception {

		return _applyComponentServiceObjects(
			_assigneeResourceComponentServiceObjects,
			this::_populateResourceContext,
			assigneeResource -> {
				Page paginationPage = assigneeResource.postProcessAssigneesPage(
					processId, assigneeBulkSelection);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public java.util.Collection<AssigneeMetric>
			createProcessAssigneeMetricsPage(
				@GraphQLName("processId") Long processId,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page,
				@GraphQLName("sort") String sortsString,
				@GraphQLName("assigneeMetricBulkSelection")
					AssigneeMetricBulkSelection assigneeMetricBulkSelection)
		throws Exception {

		return _applyComponentServiceObjects(
			_assigneeMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			assigneeMetricResource -> {
				Page paginationPage =
					assigneeMetricResource.postProcessAssigneeMetricsPage(
						processId, Pagination.of(page, pageSize),
						_sortsBiFunction.apply(
							assigneeMetricResource, sortsString),
						assigneeMetricBulkSelection);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createCalendarsPageExportBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_calendarResourceComponentServiceObjects,
			this::_populateResourceContext,
			calendarResource -> calendarResource.postCalendarsPageExportBatch(
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createIndexesPageExportBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_indexResourceComponentServiceObjects,
			this::_populateResourceContext,
			indexResource -> indexResource.postIndexesPageExportBatch(
				callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public boolean patchIndexRefresh(@GraphQLName("index") Index index)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_indexResourceComponentServiceObjects,
			this::_populateResourceContext,
			indexResource -> indexResource.patchIndexRefresh(index));

		return true;
	}

	@GraphQLField
	public boolean patchIndexReindex(@GraphQLName("index") Index index)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_indexResourceComponentServiceObjects,
			this::_populateResourceContext,
			indexResource -> indexResource.patchIndexReindex(index));

		return true;
	}

	@GraphQLField
	public Response createProcessInstancesPageExportBatch(
			@GraphQLName("processId") Long processId,
			@GraphQLName("assigneeIds") Long[] assigneeIds,
			@GraphQLName("classPKs") Long[] classPKs,
			@GraphQLName("dateEnd") Date dateEnd,
			@GraphQLName("dateStart") Date dateStart,
			@GraphQLName("slaStatuses") String[] slaStatuses,
			@GraphQLName("statuses") String[] statuses,
			@GraphQLName("taskNames") String[] taskNames,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_instanceResourceComponentServiceObjects,
			this::_populateResourceContext,
			instanceResource ->
				instanceResource.postProcessInstancesPageExportBatch(
					processId, assigneeIds, classPKs, dateEnd, dateStart,
					slaStatuses, statuses, taskNames,
					_sortsBiFunction.apply(instanceResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Instance createProcessInstance(
			@GraphQLName("processId") Long processId,
			@GraphQLName("instance") Instance instance)
		throws Exception {

		return _applyComponentServiceObjects(
			_instanceResourceComponentServiceObjects,
			this::_populateResourceContext,
			instanceResource -> instanceResource.postProcessInstance(
				processId, instance));
	}

	@GraphQLField
	public Response createProcessInstanceBatch(
			@GraphQLName("processId") Long processId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_instanceResourceComponentServiceObjects,
			this::_populateResourceContext,
			instanceResource -> instanceResource.postProcessInstanceBatch(
				processId, callbackURL, object));
	}

	@GraphQLField
	public boolean deleteProcessInstance(
			@GraphQLName("processId") Long processId,
			@GraphQLName("instanceId") Long instanceId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_instanceResourceComponentServiceObjects,
			this::_populateResourceContext,
			instanceResource -> instanceResource.deleteProcessInstance(
				processId, instanceId));

		return true;
	}

	@GraphQLField
	public boolean patchProcessInstance(
			@GraphQLName("processId") Long processId,
			@GraphQLName("instanceId") Long instanceId,
			@GraphQLName("instance") Instance instance)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_instanceResourceComponentServiceObjects,
			this::_populateResourceContext,
			instanceResource -> instanceResource.patchProcessInstance(
				processId, instanceId, instance));

		return true;
	}

	@GraphQLField
	public boolean patchProcessInstanceComplete(
			@GraphQLName("processId") Long processId,
			@GraphQLName("instanceId") Long instanceId,
			@GraphQLName("instance") Instance instance)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_instanceResourceComponentServiceObjects,
			this::_populateResourceContext,
			instanceResource -> instanceResource.patchProcessInstanceComplete(
				processId, instanceId, instance));

		return true;
	}

	@GraphQLField
	public Response createProcessNodesPageExportBatch(
			@GraphQLName("processId") Long processId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_nodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			nodeResource -> nodeResource.postProcessNodesPageExportBatch(
				processId, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Node createProcessNode(
			@GraphQLName("processId") Long processId,
			@GraphQLName("node") Node node)
		throws Exception {

		return _applyComponentServiceObjects(
			_nodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			nodeResource -> nodeResource.postProcessNode(processId, node));
	}

	@GraphQLField
	public Response createProcessNodeBatch(
			@GraphQLName("processId") Long processId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_nodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			nodeResource -> nodeResource.postProcessNodeBatch(
				processId, callbackURL, object));
	}

	@GraphQLField
	public boolean deleteProcessNode(
			@GraphQLName("processId") Long processId,
			@GraphQLName("nodeId") Long nodeId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_nodeResourceComponentServiceObjects,
			this::_populateResourceContext,
			nodeResource -> nodeResource.deleteProcessNode(processId, nodeId));

		return true;
	}

	@GraphQLField
	public Process createProcess(@GraphQLName("process") Process process)
		throws Exception {

		return _applyComponentServiceObjects(
			_processResourceComponentServiceObjects,
			this::_populateResourceContext,
			processResource -> processResource.postProcess(process));
	}

	@GraphQLField
	public Response createProcessBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_processResourceComponentServiceObjects,
			this::_populateResourceContext,
			processResource -> processResource.postProcessBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean deleteProcess(@GraphQLName("processId") Long processId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_processResourceComponentServiceObjects,
			this::_populateResourceContext,
			processResource -> processResource.deleteProcess(processId));

		return true;
	}

	@GraphQLField
	public Response deleteProcessBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_processResourceComponentServiceObjects,
			this::_populateResourceContext,
			processResource -> processResource.deleteProcessBatch(
				callbackURL, object));
	}

	@GraphQLField
	public boolean updateProcess(
			@GraphQLName("processId") Long processId,
			@GraphQLName("process") Process process)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_processResourceComponentServiceObjects,
			this::_populateResourceContext,
			processResource -> processResource.putProcess(processId, process));

		return true;
	}

	@GraphQLField
	public Response updateProcessBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_processResourceComponentServiceObjects,
			this::_populateResourceContext,
			processResource -> processResource.putProcessBatch(
				callbackURL, object));
	}

	@GraphQLField
	public Response createProcessMetricsPageExportBatch(
			@GraphQLName("title") String title,
			@GraphQLName("sort") String sortsString,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_processMetricResourceComponentServiceObjects,
			this::_populateResourceContext,
			processMetricResource ->
				processMetricResource.postProcessMetricsPageExportBatch(
					title,
					_sortsBiFunction.apply(processMetricResource, sortsString),
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createProcessProcessVersionsPageExportBatch(
			@GraphQLName("processId") Long processId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_processVersionResourceComponentServiceObjects,
			this::_populateResourceContext,
			processVersionResource ->
				processVersionResource.
					postProcessProcessVersionsPageExportBatch(
						processId, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createReindexStatusesPageExportBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_reindexStatusResourceComponentServiceObjects,
			this::_populateResourceContext,
			reindexStatusResource ->
				reindexStatusResource.postReindexStatusesPageExportBatch(
					callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createProcessRolesPageExportBatch(
			@GraphQLName("processId") Long processId,
			@GraphQLName("completed") Boolean completed,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_roleResourceComponentServiceObjects,
			this::_populateResourceContext,
			roleResource -> roleResource.postProcessRolesPageExportBatch(
				processId, completed, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Response createProcessSLAsPageExportBatch(
			@GraphQLName("processId") Long processId,
			@GraphQLName("status") Integer status,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_slaResourceComponentServiceObjects, this::_populateResourceContext,
			slaResource -> slaResource.postProcessSLAsPageExportBatch(
				processId, status, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public SLA createProcessSLA(
			@GraphQLName("processId") Long processId,
			@GraphQLName("sla") SLA sla)
		throws Exception {

		return _applyComponentServiceObjects(
			_slaResourceComponentServiceObjects, this::_populateResourceContext,
			slaResource -> slaResource.postProcessSLA(processId, sla));
	}

	@GraphQLField
	public Response createProcessSLABatch(
			@GraphQLName("processId") Long processId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_slaResourceComponentServiceObjects, this::_populateResourceContext,
			slaResource -> slaResource.postProcessSLABatch(
				processId, callbackURL, object));
	}

	@GraphQLField
	public boolean deleteSLA(@GraphQLName("slaId") Long slaId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_slaResourceComponentServiceObjects, this::_populateResourceContext,
			slaResource -> slaResource.deleteSLA(slaId));

		return true;
	}

	@GraphQLField
	public Response deleteSLABatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_slaResourceComponentServiceObjects, this::_populateResourceContext,
			slaResource -> slaResource.deleteSLABatch(callbackURL, object));
	}

	@GraphQLField
	public SLA updateSLA(
			@GraphQLName("slaId") Long slaId, @GraphQLName("sla") SLA sla)
		throws Exception {

		return _applyComponentServiceObjects(
			_slaResourceComponentServiceObjects, this::_populateResourceContext,
			slaResource -> slaResource.putSLA(slaId, sla));
	}

	@GraphQLField
	public Response updateSLABatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_slaResourceComponentServiceObjects, this::_populateResourceContext,
			slaResource -> slaResource.putSLABatch(callbackURL, object));
	}

	@GraphQLField
	public Response createProcessTasksPageExportBatch(
			@GraphQLName("processId") Long processId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_taskResourceComponentServiceObjects,
			this::_populateResourceContext,
			taskResource -> taskResource.postProcessTasksPageExportBatch(
				processId, callbackURL, contentType, fieldNames));
	}

	@GraphQLField
	public Task createProcessTask(
			@GraphQLName("processId") Long processId,
			@GraphQLName("task") Task task)
		throws Exception {

		return _applyComponentServiceObjects(
			_taskResourceComponentServiceObjects,
			this::_populateResourceContext,
			taskResource -> taskResource.postProcessTask(processId, task));
	}

	@GraphQLField
	public Response createProcessTaskBatch(
			@GraphQLName("processId") Long processId,
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("object") Object object)
		throws Exception {

		return _applyComponentServiceObjects(
			_taskResourceComponentServiceObjects,
			this::_populateResourceContext,
			taskResource -> taskResource.postProcessTaskBatch(
				processId, callbackURL, object));
	}

	@GraphQLField
	public boolean deleteProcessTask(
			@GraphQLName("processId") Long processId,
			@GraphQLName("taskId") Long taskId)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_taskResourceComponentServiceObjects,
			this::_populateResourceContext,
			taskResource -> taskResource.deleteProcessTask(processId, taskId));

		return true;
	}

	@GraphQLField
	public boolean patchProcessTask(
			@GraphQLName("processId") Long processId,
			@GraphQLName("taskId") Long taskId, @GraphQLName("task") Task task)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_taskResourceComponentServiceObjects,
			this::_populateResourceContext,
			taskResource -> taskResource.patchProcessTask(
				processId, taskId, task));

		return true;
	}

	@GraphQLField
	public boolean patchProcessTaskComplete(
			@GraphQLName("processId") Long processId,
			@GraphQLName("taskId") Long taskId, @GraphQLName("task") Task task)
		throws Exception {

		_applyVoidComponentServiceObjects(
			_taskResourceComponentServiceObjects,
			this::_populateResourceContext,
			taskResource -> taskResource.patchProcessTaskComplete(
				processId, taskId, task));

		return true;
	}

	@GraphQLField
	public java.util.Collection<Task> createTasksPage(
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("taskBulkSelection") TaskBulkSelection
				taskBulkSelection)
		throws Exception {

		return _applyComponentServiceObjects(
			_taskResourceComponentServiceObjects,
			this::_populateResourceContext,
			taskResource -> {
				Page paginationPage = taskResource.postTasksPage(
					Pagination.of(page, pageSize), taskBulkSelection);

				return paginationPage.getItems();
			});
	}

	@GraphQLField
	public Response createTimeRangesPageExportBatch(
			@GraphQLName("callbackURL") String callbackURL,
			@GraphQLName("contentType") String contentType,
			@GraphQLName("fieldNames") String fieldNames)
		throws Exception {

		return _applyComponentServiceObjects(
			_timeRangeResourceComponentServiceObjects,
			this::_populateResourceContext,
			timeRangeResource ->
				timeRangeResource.postTimeRangesPageExportBatch(
					callbackURL, contentType, fieldNames));
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

	private <T, E1 extends Throwable, E2 extends Throwable> void
			_applyVoidComponentServiceObjects(
				ComponentServiceObjects<T> componentServiceObjects,
				UnsafeConsumer<T, E1> unsafeConsumer,
				UnsafeConsumer<T, E2> unsafeFunction)
		throws E1, E2 {

		T resource = componentServiceObjects.getService();

		try {
			unsafeConsumer.accept(resource);

			unsafeFunction.accept(resource);
		}
		finally {
			componentServiceObjects.ungetService(resource);
		}
	}

	private void _populateResourceContext(AssigneeResource assigneeResource)
		throws Exception {

		assigneeResource.setContextAcceptLanguage(_acceptLanguage);
		assigneeResource.setContextCompany(_company);
		assigneeResource.setContextHttpServletRequest(_httpServletRequest);
		assigneeResource.setContextHttpServletResponse(_httpServletResponse);
		assigneeResource.setContextUriInfo(_uriInfo);
		assigneeResource.setContextUser(_user);
		assigneeResource.setGroupLocalService(_groupLocalService);
		assigneeResource.setRoleLocalService(_roleLocalService);

		assigneeResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		assigneeResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private void _populateResourceContext(
			AssigneeMetricResource assigneeMetricResource)
		throws Exception {

		assigneeMetricResource.setContextAcceptLanguage(_acceptLanguage);
		assigneeMetricResource.setContextCompany(_company);
		assigneeMetricResource.setContextHttpServletRequest(
			_httpServletRequest);
		assigneeMetricResource.setContextHttpServletResponse(
			_httpServletResponse);
		assigneeMetricResource.setContextUriInfo(_uriInfo);
		assigneeMetricResource.setContextUser(_user);
		assigneeMetricResource.setGroupLocalService(_groupLocalService);
		assigneeMetricResource.setRoleLocalService(_roleLocalService);

		assigneeMetricResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		assigneeMetricResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		calendarResource.setRoleLocalService(_roleLocalService);

		calendarResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		calendarResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		indexResource.setRoleLocalService(_roleLocalService);

		indexResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		indexResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		instanceResource.setRoleLocalService(_roleLocalService);

		instanceResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		instanceResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		nodeResource.setRoleLocalService(_roleLocalService);

		nodeResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		nodeResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		processResource.setRoleLocalService(_roleLocalService);

		processResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		processResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		processMetricResource.setRoleLocalService(_roleLocalService);

		processMetricResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		processMetricResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		processVersionResource.setRoleLocalService(_roleLocalService);

		processVersionResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		processVersionResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		reindexStatusResource.setRoleLocalService(_roleLocalService);

		reindexStatusResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		reindexStatusResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		roleResource.setRoleLocalService(_roleLocalService);

		roleResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		roleResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		slaResource.setRoleLocalService(_roleLocalService);

		slaResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		slaResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		taskResource.setRoleLocalService(_roleLocalService);

		taskResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		taskResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
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
		timeRangeResource.setRoleLocalService(_roleLocalService);

		timeRangeResource.setVulcanBatchEngineExportTaskResource(
			_vulcanBatchEngineExportTaskResource);

		timeRangeResource.setVulcanBatchEngineImportTaskResource(
			_vulcanBatchEngineImportTaskResource);
	}

	private static ComponentServiceObjects<AssigneeResource>
		_assigneeResourceComponentServiceObjects;
	private static ComponentServiceObjects<AssigneeMetricResource>
		_assigneeMetricResourceComponentServiceObjects;
	private static ComponentServiceObjects<CalendarResource>
		_calendarResourceComponentServiceObjects;
	private static ComponentServiceObjects<IndexResource>
		_indexResourceComponentServiceObjects;
	private static ComponentServiceObjects<InstanceResource>
		_instanceResourceComponentServiceObjects;
	private static ComponentServiceObjects<NodeResource>
		_nodeResourceComponentServiceObjects;
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
	private static ComponentServiceObjects<TaskResource>
		_taskResourceComponentServiceObjects;
	private static ComponentServiceObjects<TimeRangeResource>
		_timeRangeResourceComponentServiceObjects;

	private AcceptLanguage _acceptLanguage;
	private com.liferay.portal.kernel.model.Company _company;
	private GroupLocalService _groupLocalService;
	private HttpServletRequest _httpServletRequest;
	private HttpServletResponse _httpServletResponse;
	private RoleLocalService _roleLocalService;
	private BiFunction<Object, String, com.liferay.portal.kernel.search.Sort[]>
		_sortsBiFunction;
	private UriInfo _uriInfo;
	private com.liferay.portal.kernel.model.User _user;
	private VulcanBatchEngineExportTaskResource
		_vulcanBatchEngineExportTaskResource;
	private VulcanBatchEngineImportTaskResource
		_vulcanBatchEngineImportTaskResource;

}