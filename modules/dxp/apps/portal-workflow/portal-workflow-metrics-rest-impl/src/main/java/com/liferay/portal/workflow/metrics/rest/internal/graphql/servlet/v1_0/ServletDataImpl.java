/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.internal.graphql.servlet.v1_0;

import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;
import com.liferay.portal.workflow.metrics.rest.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.portal.workflow.metrics.rest.internal.graphql.query.v1_0.Query;
import com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.AssigneeMetricResourceImpl;
import com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.AssigneeResourceImpl;
import com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.CalendarResourceImpl;
import com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.HistogramMetricResourceImpl;
import com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.IndexResourceImpl;
import com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.InstanceResourceImpl;
import com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.NodeMetricResourceImpl;
import com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.NodeResourceImpl;
import com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.ProcessMetricResourceImpl;
import com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.ProcessResourceImpl;
import com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.ProcessVersionResourceImpl;
import com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.ReindexStatusResourceImpl;
import com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.RoleResourceImpl;
import com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.SLAResourceImpl;
import com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.SLAResultResourceImpl;
import com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.TaskResourceImpl;
import com.liferay.portal.workflow.metrics.rest.internal.resource.v1_0.TimeRangeResourceImpl;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.AssigneeMetricResource;
import com.liferay.portal.workflow.metrics.rest.resource.v1_0.AssigneeResource;
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

import java.util.HashMap;
import java.util.Map;

import jakarta.annotation.Generated;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setAssigneeResourceComponentServiceObjects(
			_assigneeResourceComponentServiceObjects);
		Mutation.setAssigneeMetricResourceComponentServiceObjects(
			_assigneeMetricResourceComponentServiceObjects);
		Mutation.setCalendarResourceComponentServiceObjects(
			_calendarResourceComponentServiceObjects);
		Mutation.setIndexResourceComponentServiceObjects(
			_indexResourceComponentServiceObjects);
		Mutation.setInstanceResourceComponentServiceObjects(
			_instanceResourceComponentServiceObjects);
		Mutation.setNodeResourceComponentServiceObjects(
			_nodeResourceComponentServiceObjects);
		Mutation.setProcessResourceComponentServiceObjects(
			_processResourceComponentServiceObjects);
		Mutation.setProcessMetricResourceComponentServiceObjects(
			_processMetricResourceComponentServiceObjects);
		Mutation.setProcessVersionResourceComponentServiceObjects(
			_processVersionResourceComponentServiceObjects);
		Mutation.setReindexStatusResourceComponentServiceObjects(
			_reindexStatusResourceComponentServiceObjects);
		Mutation.setRoleResourceComponentServiceObjects(
			_roleResourceComponentServiceObjects);
		Mutation.setSLAResourceComponentServiceObjects(
			_slaResourceComponentServiceObjects);
		Mutation.setTaskResourceComponentServiceObjects(
			_taskResourceComponentServiceObjects);
		Mutation.setTimeRangeResourceComponentServiceObjects(
			_timeRangeResourceComponentServiceObjects);

		Query.setCalendarResourceComponentServiceObjects(
			_calendarResourceComponentServiceObjects);
		Query.setHistogramMetricResourceComponentServiceObjects(
			_histogramMetricResourceComponentServiceObjects);
		Query.setIndexResourceComponentServiceObjects(
			_indexResourceComponentServiceObjects);
		Query.setInstanceResourceComponentServiceObjects(
			_instanceResourceComponentServiceObjects);
		Query.setNodeResourceComponentServiceObjects(
			_nodeResourceComponentServiceObjects);
		Query.setNodeMetricResourceComponentServiceObjects(
			_nodeMetricResourceComponentServiceObjects);
		Query.setProcessResourceComponentServiceObjects(
			_processResourceComponentServiceObjects);
		Query.setProcessMetricResourceComponentServiceObjects(
			_processMetricResourceComponentServiceObjects);
		Query.setProcessVersionResourceComponentServiceObjects(
			_processVersionResourceComponentServiceObjects);
		Query.setReindexStatusResourceComponentServiceObjects(
			_reindexStatusResourceComponentServiceObjects);
		Query.setRoleResourceComponentServiceObjects(
			_roleResourceComponentServiceObjects);
		Query.setSLAResourceComponentServiceObjects(
			_slaResourceComponentServiceObjects);
		Query.setSLAResultResourceComponentServiceObjects(
			_slaResultResourceComponentServiceObjects);
		Query.setTaskResourceComponentServiceObjects(
			_taskResourceComponentServiceObjects);
		Query.setTimeRangeResourceComponentServiceObjects(
			_timeRangeResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Portal.Workflow.Metrics.REST";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/portal-workflow-metrics-graphql/v1_0";
	}

	@Override
	public Query getQuery() {
		return new Query();
	}

	public ObjectValuePair<Class<?>, String> getResourceMethodObjectValuePair(
		String methodName, boolean mutation) {

		if (mutation) {
			return _resourceMethodObjectValuePairs.get(
				"mutation#" + methodName);
		}

		return _resourceMethodObjectValuePairs.get("query#" + methodName);
	}

	private static final Map<String, ObjectValuePair<Class<?>, String>>
		_resourceMethodObjectValuePairs =
			new HashMap<String, ObjectValuePair<Class<?>, String>>() {
				{
					put(
						"mutation#createProcessAssigneesPage",
						new ObjectValuePair<>(
							AssigneeResourceImpl.class,
							"postProcessAssigneesPage"));
					put(
						"mutation#createProcessAssigneeMetricsPage",
						new ObjectValuePair<>(
							AssigneeMetricResourceImpl.class,
							"postProcessAssigneeMetricsPage"));
					put(
						"mutation#createCalendarsPageExportBatch",
						new ObjectValuePair<>(
							CalendarResourceImpl.class,
							"postCalendarsPageExportBatch"));
					put(
						"mutation#createIndexesPageExportBatch",
						new ObjectValuePair<>(
							IndexResourceImpl.class,
							"postIndexesPageExportBatch"));
					put(
						"mutation#patchIndexRefresh",
						new ObjectValuePair<>(
							IndexResourceImpl.class, "patchIndexRefresh"));
					put(
						"mutation#patchIndexReindex",
						new ObjectValuePair<>(
							IndexResourceImpl.class, "patchIndexReindex"));
					put(
						"mutation#createProcessInstancesPageExportBatch",
						new ObjectValuePair<>(
							InstanceResourceImpl.class,
							"postProcessInstancesPageExportBatch"));
					put(
						"mutation#createProcessInstance",
						new ObjectValuePair<>(
							InstanceResourceImpl.class, "postProcessInstance"));
					put(
						"mutation#createProcessInstanceBatch",
						new ObjectValuePair<>(
							InstanceResourceImpl.class,
							"postProcessInstanceBatch"));
					put(
						"mutation#deleteProcessInstance",
						new ObjectValuePair<>(
							InstanceResourceImpl.class,
							"deleteProcessInstance"));
					put(
						"mutation#patchProcessInstance",
						new ObjectValuePair<>(
							InstanceResourceImpl.class,
							"patchProcessInstance"));
					put(
						"mutation#patchProcessInstanceComplete",
						new ObjectValuePair<>(
							InstanceResourceImpl.class,
							"patchProcessInstanceComplete"));
					put(
						"mutation#createProcessNodesPageExportBatch",
						new ObjectValuePair<>(
							NodeResourceImpl.class,
							"postProcessNodesPageExportBatch"));
					put(
						"mutation#createProcessNode",
						new ObjectValuePair<>(
							NodeResourceImpl.class, "postProcessNode"));
					put(
						"mutation#createProcessNodeBatch",
						new ObjectValuePair<>(
							NodeResourceImpl.class, "postProcessNodeBatch"));
					put(
						"mutation#deleteProcessNode",
						new ObjectValuePair<>(
							NodeResourceImpl.class, "deleteProcessNode"));
					put(
						"mutation#createProcess",
						new ObjectValuePair<>(
							ProcessResourceImpl.class, "postProcess"));
					put(
						"mutation#createProcessBatch",
						new ObjectValuePair<>(
							ProcessResourceImpl.class, "postProcessBatch"));
					put(
						"mutation#deleteProcess",
						new ObjectValuePair<>(
							ProcessResourceImpl.class, "deleteProcess"));
					put(
						"mutation#deleteProcessBatch",
						new ObjectValuePair<>(
							ProcessResourceImpl.class, "deleteProcessBatch"));
					put(
						"mutation#updateProcess",
						new ObjectValuePair<>(
							ProcessResourceImpl.class, "putProcess"));
					put(
						"mutation#updateProcessBatch",
						new ObjectValuePair<>(
							ProcessResourceImpl.class, "putProcessBatch"));
					put(
						"mutation#createProcessMetricsPageExportBatch",
						new ObjectValuePair<>(
							ProcessMetricResourceImpl.class,
							"postProcessMetricsPageExportBatch"));
					put(
						"mutation#createProcessProcessVersionsPageExportBatch",
						new ObjectValuePair<>(
							ProcessVersionResourceImpl.class,
							"postProcessProcessVersionsPageExportBatch"));
					put(
						"mutation#createReindexStatusesPageExportBatch",
						new ObjectValuePair<>(
							ReindexStatusResourceImpl.class,
							"postReindexStatusesPageExportBatch"));
					put(
						"mutation#createProcessRolesPageExportBatch",
						new ObjectValuePair<>(
							RoleResourceImpl.class,
							"postProcessRolesPageExportBatch"));
					put(
						"mutation#createProcessSLAsPageExportBatch",
						new ObjectValuePair<>(
							SLAResourceImpl.class,
							"postProcessSLAsPageExportBatch"));
					put(
						"mutation#createProcessSLA",
						new ObjectValuePair<>(
							SLAResourceImpl.class, "postProcessSLA"));
					put(
						"mutation#createProcessSLABatch",
						new ObjectValuePair<>(
							SLAResourceImpl.class, "postProcessSLABatch"));
					put(
						"mutation#deleteSLA",
						new ObjectValuePair<>(
							SLAResourceImpl.class, "deleteSLA"));
					put(
						"mutation#deleteSLABatch",
						new ObjectValuePair<>(
							SLAResourceImpl.class, "deleteSLABatch"));
					put(
						"mutation#updateSLA",
						new ObjectValuePair<>(SLAResourceImpl.class, "putSLA"));
					put(
						"mutation#updateSLABatch",
						new ObjectValuePair<>(
							SLAResourceImpl.class, "putSLABatch"));
					put(
						"mutation#createProcessTasksPageExportBatch",
						new ObjectValuePair<>(
							TaskResourceImpl.class,
							"postProcessTasksPageExportBatch"));
					put(
						"mutation#createProcessTask",
						new ObjectValuePair<>(
							TaskResourceImpl.class, "postProcessTask"));
					put(
						"mutation#createProcessTaskBatch",
						new ObjectValuePair<>(
							TaskResourceImpl.class, "postProcessTaskBatch"));
					put(
						"mutation#deleteProcessTask",
						new ObjectValuePair<>(
							TaskResourceImpl.class, "deleteProcessTask"));
					put(
						"mutation#patchProcessTask",
						new ObjectValuePair<>(
							TaskResourceImpl.class, "patchProcessTask"));
					put(
						"mutation#patchProcessTaskComplete",
						new ObjectValuePair<>(
							TaskResourceImpl.class,
							"patchProcessTaskComplete"));
					put(
						"mutation#createTasksPage",
						new ObjectValuePair<>(
							TaskResourceImpl.class, "postTasksPage"));
					put(
						"mutation#createTimeRangesPageExportBatch",
						new ObjectValuePair<>(
							TimeRangeResourceImpl.class,
							"postTimeRangesPageExportBatch"));

					put(
						"query#calendars",
						new ObjectValuePair<>(
							CalendarResourceImpl.class, "getCalendarsPage"));
					put(
						"query#processHistogramMetric",
						new ObjectValuePair<>(
							HistogramMetricResourceImpl.class,
							"getProcessHistogramMetric"));
					put(
						"query#indexes",
						new ObjectValuePair<>(
							IndexResourceImpl.class, "getIndexesPage"));
					put(
						"query#processInstances",
						new ObjectValuePair<>(
							InstanceResourceImpl.class,
							"getProcessInstancesPage"));
					put(
						"query#processInstance",
						new ObjectValuePair<>(
							InstanceResourceImpl.class, "getProcessInstance"));
					put(
						"query#processNodes",
						new ObjectValuePair<>(
							NodeResourceImpl.class, "getProcessNodesPage"));
					put(
						"query#processNodeMetrics",
						new ObjectValuePair<>(
							NodeMetricResourceImpl.class,
							"getProcessNodeMetricsPage"));
					put(
						"query#process",
						new ObjectValuePair<>(
							ProcessResourceImpl.class, "getProcess"));
					put(
						"query#processTitle",
						new ObjectValuePair<>(
							ProcessResourceImpl.class, "getProcessTitle"));
					put(
						"query#processMetrics",
						new ObjectValuePair<>(
							ProcessMetricResourceImpl.class,
							"getProcessMetricsPage"));
					put(
						"query#processMetric",
						new ObjectValuePair<>(
							ProcessMetricResourceImpl.class,
							"getProcessMetric"));
					put(
						"query#processProcessVersions",
						new ObjectValuePair<>(
							ProcessVersionResourceImpl.class,
							"getProcessProcessVersionsPage"));
					put(
						"query#reindexStatuses",
						new ObjectValuePair<>(
							ReindexStatusResourceImpl.class,
							"getReindexStatusesPage"));
					put(
						"query#processRoles",
						new ObjectValuePair<>(
							RoleResourceImpl.class, "getProcessRolesPage"));
					put(
						"query#processSLAs",
						new ObjectValuePair<>(
							SLAResourceImpl.class, "getProcessSLAsPage"));
					put(
						"query#sLA",
						new ObjectValuePair<>(SLAResourceImpl.class, "getSLA"));
					put(
						"query#processLastSLAResult",
						new ObjectValuePair<>(
							SLAResultResourceImpl.class,
							"getProcessLastSLAResult"));
					put(
						"query#processTasks",
						new ObjectValuePair<>(
							TaskResourceImpl.class, "getProcessTasksPage"));
					put(
						"query#processTask",
						new ObjectValuePair<>(
							TaskResourceImpl.class, "getProcessTask"));
					put(
						"query#timeRanges",
						new ObjectValuePair<>(
							TimeRangeResourceImpl.class, "getTimeRangesPage"));

					put(
						"query#Process.instance",
						new ObjectValuePair<>(
							InstanceResourceImpl.class, "getProcessInstance"));
					put(
						"query#TaskBulkSelection.process",
						new ObjectValuePair<>(
							ProcessResourceImpl.class, "getProcess"));
					put(
						"query#Process.nodes",
						new ObjectValuePair<>(
							NodeResourceImpl.class, "getProcessNodesPage"));
					put(
						"query#Process.lastSLAResult",
						new ObjectValuePair<>(
							SLAResultResourceImpl.class,
							"getProcessLastSLAResult"));
					put(
						"query#Process.histogramMetric",
						new ObjectValuePair<>(
							HistogramMetricResourceImpl.class,
							"getProcessHistogramMetric"));
					put(
						"query#Process.tasks",
						new ObjectValuePair<>(
							TaskResourceImpl.class, "getProcessTasksPage"));
					put(
						"query#Process.sLAs",
						new ObjectValuePair<>(
							SLAResourceImpl.class, "getProcessSLAsPage"));
					put(
						"query#Process.roles",
						new ObjectValuePair<>(
							RoleResourceImpl.class, "getProcessRolesPage"));
					put(
						"query#Process.metric",
						new ObjectValuePair<>(
							ProcessMetricResourceImpl.class,
							"getProcessMetric"));
					put(
						"query#Process.task",
						new ObjectValuePair<>(
							TaskResourceImpl.class, "getProcessTask"));
					put(
						"query#Process.processVersions",
						new ObjectValuePair<>(
							ProcessVersionResourceImpl.class,
							"getProcessProcessVersionsPage"));
					put(
						"query#Process.nodeMetrics",
						new ObjectValuePair<>(
							NodeMetricResourceImpl.class,
							"getProcessNodeMetricsPage"));
					put(
						"query#Process.instances",
						new ObjectValuePair<>(
							InstanceResourceImpl.class,
							"getProcessInstancesPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AssigneeResource>
		_assigneeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AssigneeMetricResource>
		_assigneeMetricResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<CalendarResource>
		_calendarResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<IndexResource>
		_indexResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<InstanceResource>
		_instanceResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<NodeResource>
		_nodeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ProcessResource>
		_processResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ProcessMetricResource>
		_processMetricResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ProcessVersionResource>
		_processVersionResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<ReindexStatusResource>
		_reindexStatusResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<RoleResource>
		_roleResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<SLAResource>
		_slaResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TaskResource>
		_taskResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TimeRangeResource>
		_timeRangeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<HistogramMetricResource>
		_histogramMetricResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<NodeMetricResource>
		_nodeMetricResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<SLAResultResource>
		_slaResultResourceComponentServiceObjects;

}