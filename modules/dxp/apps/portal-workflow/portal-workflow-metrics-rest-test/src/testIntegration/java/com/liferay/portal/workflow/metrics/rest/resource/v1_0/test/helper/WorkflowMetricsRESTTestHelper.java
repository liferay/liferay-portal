/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.resource.v1_0.test.helper;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.document.DocumentBuilderFactory;
import com.liferay.portal.search.engine.adapter.SearchEngineAdapter;
import com.liferay.portal.search.engine.adapter.document.UpdateDocumentRequest;
import com.liferay.portal.search.engine.adapter.search.CountSearchRequest;
import com.liferay.portal.search.engine.adapter.search.CountSearchResponse;
import com.liferay.portal.search.engine.adapter.search.SearchSearchRequest;
import com.liferay.portal.search.engine.adapter.search.SearchSearchResponse;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.index.IndexNameBuilder;
import com.liferay.portal.search.query.BooleanQuery;
import com.liferay.portal.search.query.QueriesUtil;
import com.liferay.portal.vulcan.util.LocalizedMapUtil;
import com.liferay.portal.workflow.metrics.model.AddNodeRequest;
import com.liferay.portal.workflow.metrics.model.AddProcessRequest;
import com.liferay.portal.workflow.metrics.model.AddTaskRequest;
import com.liferay.portal.workflow.metrics.model.Assignment;
import com.liferay.portal.workflow.metrics.model.CompleteTaskRequest;
import com.liferay.portal.workflow.metrics.model.DeleteNodeRequest;
import com.liferay.portal.workflow.metrics.model.DeleteProcessRequest;
import com.liferay.portal.workflow.metrics.model.RoleAssignment;
import com.liferay.portal.workflow.metrics.model.UpdateProcessRequest;
import com.liferay.portal.workflow.metrics.model.UpdateTaskRequest;
import com.liferay.portal.workflow.metrics.model.UserAssignment;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Assignee;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Creator;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Instance;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Node;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.NodeMetric;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Process;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.ProcessMetric;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.SLAResult;
import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Task;
import com.liferay.portal.workflow.metrics.search.index.InstanceWorkflowMetricsIndexer;
import com.liferay.portal.workflow.metrics.search.index.NodeWorkflowMetricsIndexer;
import com.liferay.portal.workflow.metrics.search.index.ProcessWorkflowMetricsIndexer;
import com.liferay.portal.workflow.metrics.search.index.TaskWorkflowMetricsIndexer;
import com.liferay.portal.workflow.metrics.search.index.constants.WorkflowMetricsIndexNameConstants;

import java.io.Serializable;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.apache.commons.codec.digest.DigestUtils;

import org.junit.Assert;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Inácio Nery
 */
@Component(service = WorkflowMetricsRESTTestHelper.class)
public class WorkflowMetricsRESTTestHelper {

	public Instance addInstance(
			long companyId, boolean completed, long processId)
		throws Exception {

		Instance instance = new Instance();

		instance.setCreator(
			new Creator() {
				{
					id = RandomTestUtil.nextLong();
					name = RandomTestUtil.randomString();
				}
			});
		instance.setCompleted(completed);

		if (completed) {
			instance.setDateCompletion(RandomTestUtil.nextDate());
			instance.setDuration(1000L);
		}

		instance.setId(RandomTestUtil.randomLong());
		instance.setProcessId(processId);

		return addInstance(companyId, instance);
	}

	public Instance addInstance(long companyId, Instance instance)
		throws Exception {

		Date createDate = instance.getDateCreated();

		if (createDate == null) {
			createDate = new Date();
		}

		Date modifiedDate = instance.getDateModified();

		if (modifiedDate == null) {
			modifiedDate = new Date();
		}

		Creator creator = instance.getCreator();

		_instanceWorkflowMetricsIndexer.addInstance(
			_createLocalizationMap(instance.getAssetTitle()),
			_createLocalizationMap(instance.getAssetType()), StringPool.BLANK,
			GetterUtil.getLong(instance.getClassPK()), companyId, null,
			createDate, instance.getId(), modifiedDate, instance.getProcessId(),
			instance.getProcessVersion(), creator.getId(), creator.getName());

		_assertCount(
			_indexNameBuilder.getIndexName(companyId) +
				WorkflowMetricsIndexNameConstants.SUFFIX_INSTANCE,
			"active", true, "companyId", companyId, "deleted", false,
			"instanceId", instance.getId(), "processId",
			instance.getProcessId());

		return instance;
	}

	public Instance addInstance(long companyId, long processId)
		throws Exception {

		Instance instance = new Instance();

		instance.setCreator(
			new Creator() {
				{
					id = RandomTestUtil.randomLong();
					name = RandomTestUtil.randomString();
				}
			});
		instance.setId(RandomTestUtil.randomLong());
		instance.setProcessId(processId);

		return addInstance(companyId, instance);
	}

	public Node addNode(long companyId, long processId, String version)
		throws Exception {

		Node node = new Node();

		node.setId(RandomTestUtil.randomLong());
		node.setName(RandomTestUtil.randomString());
		node.setProcessVersion(version);

		return addNode(companyId, node, processId, version);
	}

	public Node addNode(
			long companyId, Node node, long processId, String version)
		throws Exception {

		AddNodeRequest.Builder builder = new AddNodeRequest.Builder();

		Date createDate = node.getDateCreated();

		if (createDate == null) {
			createDate = new Date();
		}

		Date modifiedDate = node.getDateModified();

		if (modifiedDate == null) {
			modifiedDate = new Date();
		}

		String type = node.getType();

		if (type == null) {
			type = "TASK";
		}

		_nodeWorkflowMetricsIndexer.addNode(
			builder.companyId(
				companyId
			).createDate(
				createDate
			).initial(
				false
			).modifiedDate(
				modifiedDate
			).name(
				node.getName()
			).nodeId(
				node.getId()
			).processId(
				processId
			).processVersion(
				version
			).terminal(
				false
			).type(
				type
			).build());

		_assertCount(
			_indexNameBuilder.getIndexName(companyId) +
				WorkflowMetricsIndexNameConstants.SUFFIX_NODE,
			"companyId", companyId, "deleted", false, "name", node.getName(),
			"processId", processId, "version", version);

		return node;
	}

	public NodeMetric addNodeMetric(
			Assignee assignee, long companyId,
			UnsafeSupplier<Instance, Exception> instanceUnsafeSupplier,
			long processId, String status, User user)
		throws Exception {

		String randomString = RandomTestUtil.randomString();

		return addNodeMetric(
			assignee, companyId, instanceUnsafeSupplier,
			new NodeMetric() {
				{
					durationAvg =
						Objects.equals(status, "COMPLETED") ? 1000L : 0L;
					instanceCount = 1L;
					node = new Node() {
						{
							id = RandomTestUtil.randomLong();
							label = randomString;
							name = randomString;
						}
					};
					onTimeInstanceCount = 0L;
					overdueInstanceCount = 0L;
				}
			},
			processId, status, user, "1.0");
	}

	public NodeMetric addNodeMetric(
			Assignee assignee, long companyId,
			UnsafeSupplier<Instance, Exception> instanceUnsafeSupplier,
			NodeMetric nodeMetric, long processId, String status, User user,
			String version)
		throws Exception {

		Node node = addNode(
			companyId, nodeMetric.getNode(), processId, version);

		Long onTimeInstanceCount = nodeMetric.getOnTimeInstanceCount();
		Long overdueInstanceCount = nodeMetric.getOverdueInstanceCount();

		for (int i = 0; i < nodeMetric.getInstanceCount(); i++) {
			Instance instance = instanceUnsafeSupplier.get();
			Long taskId = RandomTestUtil.nextLong();

			if (onTimeInstanceCount > 0) {
				addSLATaskResult(
					assignee.getId(), false, companyId, instance, node.getId(),
					true, status, taskId, node.getName());

				onTimeInstanceCount--;
			}
			else if (overdueInstanceCount > 0) {
				addSLATaskResult(
					assignee.getId(), true, companyId, instance, node.getId(),
					false, status, taskId, node.getName());

				overdueInstanceCount--;
			}

			List<Assignment> assignments = new ArrayList<>();

			for (long roleId : user.getRoleIds()) {
				assignments.add(
					new RoleAssignment(roleId, Collections.emptyList()));
			}

			addTask(
				assignee, assignments, companyId, nodeMetric.getDurationAvg(),
				instance, node.getName(), node.getId(), processId, version,
				taskId);

			if (instance.getCompleted()) {
				completeInstance(companyId, instance);
			}
		}

		_assertCount(
			_indexNameBuilder.getIndexName(companyId) +
				WorkflowMetricsIndexNameConstants.SUFFIX_NODE,
			"companyId", companyId, "deleted", false, "name", node.getName(),
			"processId", processId);

		return nodeMetric;
	}

	public Process addProcess(long companyId) throws Exception {
		Process process = new Process() {
			{
				id = RandomTestUtil.randomLong();
				title = RandomTestUtil.randomString();
				version = "1.0";
			}
		};

		return addProcess(companyId, process);
	}

	public Process addProcess(long companyId, Process process)
		throws Exception {

		AddProcessRequest.Builder builder = new AddProcessRequest.Builder();

		Boolean active = process.getActive();

		if (active == null) {
			active = Boolean.TRUE;
		}

		Date createDate = process.getDateCreated();

		if (createDate == null) {
			createDate = new Date();
		}

		Date modifiedDate = process.getDateModified();

		if (modifiedDate == null) {
			modifiedDate = new Date();
		}

		_processWorkflowMetricsIndexer.addProcess(
			builder.active(
				active
			).companyId(
				companyId
			).createDate(
				createDate
			).description(
				process.getDescription()
			).modifiedDate(
				modifiedDate
			).name(
				process.getName()
			).processId(
				process.getId()
			).title(
				process.getTitle()
			).titleMap(
				LocalizedMapUtil.getLocalizedMap(process.getTitle_i18n())
			).version(
				process.getVersion()
			).versions(
				new String[] {process.getVersion()}
			).build());

		_assertCount(
			_indexNameBuilder.getIndexName(companyId) +
				WorkflowMetricsIndexNameConstants.SUFFIX_PROCESS,
			"companyId", companyId, "deleted", false, "processId",
			process.getId());

		return process;
	}

	public ProcessMetric addProcessMetric(long companyId) throws Exception {
		return addProcessMetric(companyId, "1.0");
	}

	public ProcessMetric addProcessMetric(
			long companyId, ProcessMetric processMetric)
		throws Exception {

		Process process = addProcess(companyId, processMetric.getProcess());

		Long onTimeInstanceCount = processMetric.getOnTimeInstanceCount();
		Long overdueInstanceCount = processMetric.getOverdueInstanceCount();

		for (int i = 0; i < processMetric.getInstanceCount(); i++) {
			Instance instance = addInstance(companyId, false, process.getId());

			if (onTimeInstanceCount > 0) {
				addSLAInstanceResults(
					companyId, instance,
					new SLAResult() {
						{
							dateModified = new Date(
								(System.currentTimeMillis() / Time.MINUTE) *
									Time.MINUTE);
							dateOverdue = new Date(
								(System.currentTimeMillis() / Time.MINUTE) *
									Time.MINUTE);
							id = RandomTestUtil.randomLong();
							name = null;
							onTime = true;
							remainingTime = 1L;
							status = null;
						}
					});

				onTimeInstanceCount--;
			}
			else if (overdueInstanceCount > 0) {
				addSLAInstanceResults(
					companyId, instance,
					new SLAResult() {
						{
							dateModified = new Date(
								System.currentTimeMillis() / Time.SECOND *
									Time.SECOND);
							dateOverdue = new Date(
								System.currentTimeMillis() / Time.SECOND *
									Time.SECOND);
							id = RandomTestUtil.randomLong();
							name = null;
							onTime = false;
							remainingTime = -1L;
							status = null;
						}
					});

				overdueInstanceCount--;
			}
		}

		return processMetric;
	}

	public ProcessMetric addProcessMetric(long companyId, String version)
		throws Exception {

		ProcessMetric processMetric = new ProcessMetric() {
			{
				instanceCount = 0L;
				onTimeInstanceCount = 0L;
				overdueInstanceCount = 0L;
				untrackedInstanceCount = 0L;

				setProcess(
					() -> {
						Process process = new Process();

						process.setId(RandomTestUtil.randomLong());
						process.setTitle(RandomTestUtil.randomString());
						process.setVersion(version);

						return process;
					});
			}
		};

		return addProcessMetric(companyId, processMetric);
	}

	public void addSLAInstanceResults(
			long companyId, Instance instance, SLAResult... slaResults)
		throws Exception {

		for (SLAResult slaResult : slaResults) {
			_invokeAddDocument(
				_getIndexer(_CLASS_NAME_SLA_INSTANCE_RESULT_INDEXER),
				_creatWorkflowMetricsSLAInstanceResultDocument(
					companyId, instance, slaResult));

			_assertCount(
				_indexNameBuilder.getIndexName(companyId) +
					WorkflowMetricsIndexNameConstants.
						SUFFIX_SLA_INSTANCE_RESULT,
				"blocked", false, "companyId", companyId, "deleted", false,
				"instanceCompleted",
				Objects.nonNull(instance.getDateCompletion()), "instanceId",
				instance.getId(), "onTime", slaResult.getOnTime(), "processId",
				instance.getProcessId(), "remainingTime",
				slaResult.getRemainingTime(), "slaDefinitionId",
				slaResult.getId());
		}

		_updateInstance(companyId, instance, slaResults);
	}

	public void addSLATaskResult(
			long assigneeId, boolean breached, long companyId,
			Instance instance, long nodeId, boolean onTime, String status,
			long taskId, String taskName)
		throws Exception {

		long slaDefinitionId = RandomTestUtil.randomLong();

		_invokeAddDocument(
			_getIndexer(_CLASS_NAME_SLA_TASK_RESULT_INDEXER),
			_creatWorkflowMetricsSLATaskResultDocument(
				assigneeId, breached, companyId,
				Objects.nonNull(instance.getDateCompletion()), instance.getId(),
				nodeId, onTime, instance.getProcessId(), slaDefinitionId,
				status, taskId, taskName));

		_assertCount(
			_indexNameBuilder.getIndexName(companyId) +
				WorkflowMetricsIndexNameConstants.SUFFIX_SLA_TASK_RESULT,
			"breached", breached, "assigneeIds", assigneeId, "assigneeType",
			User.class.getName(), "companyId", companyId, "deleted", false,
			"instanceCompleted", Objects.nonNull(instance.getDateCompletion()),
			"instanceId", instance.getId(), "onTime", onTime, "processId",
			instance.getProcessId(), "slaDefinitionId", slaDefinitionId,
			"taskId", taskId, "taskName", taskName);
	}

	public Task addTask(
			Assignee assignee, List<Assignment> assignments, long companyId,
			Instance instance)
		throws Exception {

		return addTask(
			assignee, assignments, companyId, 0L, instance,
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
			instance.getProcessId(), "1.0", RandomTestUtil.randomLong());
	}

	public Task addTask(
			Assignee assignee, List<Assignment> assignments, long companyId,
			long durationAvg, Instance instance, String name, long nodeId,
			long processId, String processVersion, long taskId)
		throws Exception {

		if ((assignee != null) && (assignee.getId() != null) &&
			(assignee.getId() != -1L)) {

			assignments = new ArrayList<>();

			User user = _userLocalService.fetchUser(assignee.getId());

			assignments.add(
				new UserAssignment(assignee.getId(), user.getFullName()));
		}

		AddTaskRequest.Builder addTaskRequestBuilder =
			new AddTaskRequest.Builder();

		addTaskRequestBuilder.assetTitleMap(
			_createLocalizationMap(
				StringUtil.toLowerCase(RandomTestUtil.randomString()))
		).assetTypeMap(
			_createLocalizationMap(
				StringUtil.toLowerCase(RandomTestUtil.randomString()))
		).assignments(
			assignments
		).className(
			StringPool.BLANK
		).classPK(
			GetterUtil.getLong(instance.getClassPK())
		).companyId(
			companyId
		);

		if (assignments.get(0) instanceof UserAssignment) {
			addTaskRequestBuilder.completed(
				durationAvg > 0
			).completionDate(
				(durationAvg > 0) ? new Date() : null
			);

			UserAssignment userAssignment = (UserAssignment)assignments.get(0);

			addTaskRequestBuilder.completionUserId(
				() ->
					(durationAvg > 0) ? userAssignment.getAssignmentId() :
						null);
		}

		AddTaskRequest addTaskRequest = addTaskRequestBuilder.createDate(
			new Date()
		).instanceCompleted(
			instance.getCompleted()
		).instanceCompletionDate(
			instance.getDateCompletion()
		).instanceId(
			instance.getId()
		).modifiedDate(
			new Date()
		).name(
			name
		).nodeId(
			nodeId
		).processId(
			processId
		).processVersion(
			processVersion
		).taskId(
			taskId
		).userId(
			0L
		).build();

		_taskWorkflowMetricsIndexer.addTask(addTaskRequest);

		_assertCount(
			_indexNameBuilder.getIndexName(addTaskRequest.getCompanyId()) +
				WorkflowMetricsIndexNameConstants.SUFFIX_TASK,
			"active", true, "companyId", addTaskRequest.getCompanyId(),
			"deleted", false, "instanceId", instance.getId(), "processId",
			addTaskRequest.getProcessId(), "nodeId", addTaskRequest.getNodeId(),
			"name", addTaskRequest.getName(), "taskId",
			addTaskRequest.getTaskId());

		if (!addTaskRequest.isCompleted()) {
			String indexName = _indexNameBuilder.getIndexName(
				addTaskRequest.getCompanyId());

			_assertCount(
				booleanQuery -> booleanQuery.addMustQueryClauses(
					QueriesUtil.nested(
						"tasks",
						QueriesUtil.term(
							"tasks.taskId", addTaskRequest.getTaskId()))),
				1,
				indexName + WorkflowMetricsIndexNameConstants.SUFFIX_INSTANCE,
				"companyId", addTaskRequest.getCompanyId(), "deleted", false,
				"instanceId", instance.getId(), "processId",
				addTaskRequest.getProcessId());
		}

		if (ListUtil.isNotEmpty(addTaskRequest.getAssignments())) {
			UpdateTaskRequest.Builder updateTaskRequestBuilder =
				new UpdateTaskRequest.Builder();

			_taskWorkflowMetricsIndexer.updateTask(
				updateTaskRequestBuilder.assetTitleMap(
					addTaskRequest.getAssetTitleMap()
				).assetTypeMap(
					addTaskRequest.getAssetTypeMap()
				).assignments(
					addTaskRequest.getAssignments()
				).companyId(
					addTaskRequest.getCompanyId()
				).modifiedDate(
					new Date()
				).taskId(
					addTaskRequest.getTaskId()
				).userId(
					0
				).build());

			Assignment assignment = assignments.get(0);

			String assignmentType = Role.class.getName();

			if (assignment instanceof UserAssignment) {
				assignmentType = User.class.getName();
			}

			_assertCount(
				_indexNameBuilder.getIndexName(companyId) +
					WorkflowMetricsIndexNameConstants.SUFFIX_TASK,
				"assigneeIds", assignment.getAssignmentId(), "assigneeType",
				assignmentType, "companyId", addTaskRequest.getCompanyId(),
				"deleted", false, "instanceId", instance.getId(), "processId",
				addTaskRequest.getProcessId(), "nodeId",
				addTaskRequest.getNodeId(), "name", addTaskRequest.getName(),
				"taskId", addTaskRequest.getTaskId());
		}

		if (addTaskRequest.isCompleted()) {
			CompleteTaskRequest.Builder completeTaskRequestBuilder =
				new CompleteTaskRequest.Builder();

			_taskWorkflowMetricsIndexer.completeTask(
				completeTaskRequestBuilder.companyId(
					addTaskRequest.getCompanyId()
				).completionDate(
					addTaskRequest.getCompletionDate()
				).completionUserId(
					addTaskRequest.getCompletionUserId()
				).duration(
					durationAvg
				).modifiedDate(
					addTaskRequest.getModifiedDate()
				).taskId(
					addTaskRequest.getTaskId()
				).userId(
					0
				).build());

			_assertCount(
				_indexNameBuilder.getIndexName(addTaskRequest.getCompanyId()) +
					WorkflowMetricsIndexNameConstants.SUFFIX_TASK,
				"companyId", addTaskRequest.getCompanyId(), "completed", true,
				"completionUserId", addTaskRequest.getCompletionUserId(),
				"deleted", false, "duration", durationAvg, "instanceId",
				addTaskRequest.getInstanceId(), "processId",
				addTaskRequest.getProcessId(), "nodeId",
				addTaskRequest.getNodeId(), "name", addTaskRequest.getName(),
				"taskId", addTaskRequest.getTaskId());
		}

		return _toTask(addTaskRequest, durationAvg);
	}

	public Task addTask(
			Assignee assignee, long companyId, Instance instance, User user)
		throws Exception {

		List<Assignment> assignments = new ArrayList<>();

		for (long roleId : user.getRoleIds()) {
			assignments.add(
				new RoleAssignment(roleId, Collections.emptyList()));
		}

		return addTask(
			assignee, assignments, companyId, 0L, instance,
			RandomTestUtil.randomString(), RandomTestUtil.randomLong(),
			instance.getProcessId(), "1.0", RandomTestUtil.randomLong());
	}

	public Task addTask(long companyId, Instance instance, Task task, User user)
		throws Exception {

		List<Assignment> assignments = new ArrayList<>();
		String assigneeType = Role.class.getName();

		Assignee assignee = task.getAssignee();

		if ((assignee != null) && (assignee.getId() != null)) {
			assigneeType = User.class.getName();
			assignments.add(
				new UserAssignment(assignee.getId(), user.getFullName()));
		}
		else {
			for (Long roleId : user.getRoleIds()) {
				assignments.add(
					new RoleAssignment(roleId, Collections.emptyList()));
			}
		}

		AddTaskRequest.Builder addTaskRequestBuilder =
			new AddTaskRequest.Builder();

		_taskWorkflowMetricsIndexer.addTask(
			addTaskRequestBuilder.assetTitleMap(
				_createLocalizationMap(task.getAssetTitle())
			).assetTypeMap(
				_createLocalizationMap(task.getAssetType())
			).assignments(
				assignments
			).className(
				task.getClassName()
			).classPK(
				task.getClassPK()
			).companyId(
				companyId
			).createDate(
				task.getDateCreated()
			).instanceId(
				instance.getId()
			).modifiedDate(
				task.getDateModified()
			).name(
				task.getName()
			).nodeId(
				task.getNodeId()
			).processId(
				task.getProcessId()
			).processVersion(
				task.getProcessVersion()
			).taskId(
				task.getId()
			).userId(
				0
			).build());

		String indexName = _indexNameBuilder.getIndexName(companyId);

		_assertCount(
			indexName + WorkflowMetricsIndexNameConstants.SUFFIX_TASK,
			"companyId", companyId, "deleted", false, "instanceId",
			instance.getId(), "processId", task.getProcessId(), "nodeId",
			task.getNodeId(), "name", task.getName(), "taskId", task.getId());

		_assertCount(
			booleanQuery -> booleanQuery.addMustQueryClauses(
				QueriesUtil.nested(
					"tasks", QueriesUtil.term("tasks.taskId", task.getId()))),
			1, indexName + WorkflowMetricsIndexNameConstants.SUFFIX_INSTANCE,
			"companyId", companyId, "deleted", false, "instanceId",
			instance.getId(), "processId", task.getProcessId());

		if (!assignments.isEmpty()) {
			UpdateTaskRequest.Builder updateTaskRequestBuilder =
				new UpdateTaskRequest.Builder();

			_taskWorkflowMetricsIndexer.updateTask(
				updateTaskRequestBuilder.assetTitleMap(
					_createLocalizationMap(task.getAssetTitle())
				).assetTypeMap(
					_createLocalizationMap(task.getAssetType())
				).assignments(
					assignments
				).companyId(
					companyId
				).modifiedDate(
					new Date()
				).taskId(
					task.getId()
				).userId(
					0
				).build());

			Assignment assignment = assignments.get(0);

			_assertCount(
				indexName + WorkflowMetricsIndexNameConstants.SUFFIX_TASK,
				"assigneeIds", assignment.getAssignmentId(), "assigneeType",
				assigneeType, "companyId", companyId, "deleted", false,
				"instanceId", instance.getId(), "processId",
				task.getProcessId(), "nodeId", task.getNodeId(), "name",
				task.getName(), "taskId", task.getId());
		}

		if (task.getCompleted()) {
			CompleteTaskRequest.Builder completeTaskRequestBuilder =
				new CompleteTaskRequest.Builder();

			_taskWorkflowMetricsIndexer.completeTask(
				completeTaskRequestBuilder.companyId(
					companyId
				).completionDate(
					task.getDateCompletion()
				).completionUserId(
					task.getCompletionUserId()
				).duration(
					task.getDuration()
				).modifiedDate(
					task.getDateModified()
				).taskId(
					task.getId()
				).userId(
					0
				).build());

			_assertCount(
				indexName + WorkflowMetricsIndexNameConstants.SUFFIX_TASK,
				"companyId", companyId, "completed", true, "completionUserId",
				task.getCompletionUserId(), "deleted", false, "duration",
				task.getDuration(), "instanceId", instance.getId(), "processId",
				task.getProcessId(), "nodeId", task.getNodeId(), "name",
				task.getName(), "taskId", task.getId());
		}

		return task;
	}

	public void blockSLAInstanceResults(
			long companyId, long processId, long slaDefinitionId)
		throws Exception {

		Object indexer = _getIndexer(_CLASS_NAME_SLA_INSTANCE_RESULT_INDEXER);

		Class<?> indexerClass = indexer.getClass();

		Method method = null;

		while ((indexerClass != Object.class) && (method == null)) {
			try {
				method = ReflectionUtil.getDeclaredMethod(
					indexerClass, "blockDocuments", long.class, long.class,
					long.class);
			}
			catch (NoSuchMethodException noSuchMethodException) {
			}

			indexerClass = indexerClass.getSuperclass();
		}

		method.invoke(indexer, companyId, processId, slaDefinitionId);

		_assertCount(
			_indexNameBuilder.getIndexName(companyId) +
				WorkflowMetricsIndexNameConstants.SUFFIX_SLA_INSTANCE_RESULT,
			"blocked", true, "companyId", companyId, "deleted", false,
			"processId", processId, "slaDefinitionId", slaDefinitionId);
	}

	public void completeInstance(long companyId, Instance instance)
		throws Exception {

		Date completionDate = instance.getDateCompletion();

		if (completionDate == null) {
			completionDate = new Date();
		}

		Long duration = instance.getDuration();

		if (duration == null) {
			duration = 1000L;
		}

		Date modifiedDate = instance.getDateModified();

		if (modifiedDate == null) {
			modifiedDate = new Date();
		}

		_instanceWorkflowMetricsIndexer.completeInstance(
			companyId, completionDate, duration, instance.getId(),
			modifiedDate);

		_assertCount(
			_indexNameBuilder.getIndexName(companyId) +
				WorkflowMetricsIndexNameConstants.SUFFIX_INSTANCE,
			"companyId", companyId, "completed", true, "deleted", false,
			"instanceId", instance.getId(), "processId",
			instance.getProcessId());
	}

	public void deleteInstance(long companyId, Instance instance)
		throws Exception {

		_instanceWorkflowMetricsIndexer.deleteInstance(
			companyId, instance.getId());

		_assertCount(
			_indexNameBuilder.getIndexName(companyId) +
				WorkflowMetricsIndexNameConstants.SUFFIX_INSTANCE,
			"companyId", companyId, "deleted", true, "instanceId",
			instance.getId(), "processId", instance.getProcessId());
	}

	public void deleteNode(long companyId, Node node, long processId)
		throws Exception {

		DeleteNodeRequest.Builder builder = new DeleteNodeRequest.Builder();

		_nodeWorkflowMetricsIndexer.deleteNode(
			builder.companyId(
				companyId
			).nodeId(
				node.getId()
			).build());

		_assertCount(
			_indexNameBuilder.getIndexName(companyId) +
				WorkflowMetricsIndexNameConstants.SUFFIX_NODE,
			"companyId", companyId, "deleted", true, "name", node.getName(),
			"processId", processId);
	}

	public void deleteProcess(long companyId, long processId) throws Exception {
		DeleteProcessRequest.Builder builder =
			new DeleteProcessRequest.Builder();

		_processWorkflowMetricsIndexer.deleteProcess(
			builder.companyId(
				companyId
			).processId(
				processId
			).build());

		_assertCount(
			_indexNameBuilder.getIndexName(companyId) +
				WorkflowMetricsIndexNameConstants.SUFFIX_PROCESS,
			"companyId", companyId, "deleted", true, "processId", processId);
	}

	public void deleteProcess(long companyId, Process process)
		throws Exception {

		deleteProcess(companyId, process.getId());
	}

	public void deleteSLATaskResults(long companyId, long processId)
		throws Exception {

		_deleteDocuments(
			_indexNameBuilder.getIndexName(companyId) +
				WorkflowMetricsIndexNameConstants.SUFFIX_SLA_TASK_RESULT,
			"companyId", companyId, "processId", processId);
	}

	public void deleteTasks(long companyId, long processId) throws Exception {
		_deleteDocuments(
			_indexNameBuilder.getIndexName(companyId) +
				WorkflowMetricsIndexNameConstants.SUFFIX_TASK,
			"companyId", companyId, "processId", processId);
	}

	public Document[] getDocuments(long companyId) throws Exception {
		if (_searchEngineAdapter == null) {
			return new Document[0];
		}

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames(
			_indexNameBuilder.getIndexName(companyId) +
				WorkflowMetricsIndexNameConstants.SUFFIX_PROCESS);

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		searchSearchRequest.setQuery(
			booleanQuery.addMustQueryClauses(
				QueriesUtil.term("companyId", companyId),
				QueriesUtil.term("deleted", Boolean.FALSE)));

		searchSearchRequest.setSize(10000);

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		return TransformUtil.transformToArray(
			searchHits.getSearchHits(), SearchHit::getDocument, Document.class);
	}

	public void restoreProcess(Document document) throws Exception {
		AddProcessRequest.Builder builder = new AddProcessRequest.Builder();

		builder.active(
			document.getBoolean("active")
		).companyId(
			document.getLong("companyId")
		).createDate(
			_parseDate(document.getDate("createDate"))
		).description(
			document.getString("description")
		).modifiedDate(
			_parseDate(document.getDate("modifiedDate"))
		).name(
			document.getString("name")
		).processId(
			document.getLong("processId")
		).title(
			document.getString("title")
		).titleMap(
			_createLocalizationMap(document.getString("title"))
		);

		String version = StringBundler.concat(
			document.getString("version"), CharPool.PERIOD, 0);

		_processWorkflowMetricsIndexer.addProcess(
			builder.version(
				version
			).versions(
				new String[] {version}
			).build());

		_assertCount(
			_indexNameBuilder.getIndexName(document.getLong("companyId")) +
				WorkflowMetricsIndexNameConstants.SUFFIX_PROCESS,
			"companyId", document.getLong("companyId"), "deleted", false,
			"processId", document.getLong("processId"));
	}

	public void updateProcess(long companyId, long processId, String version)
		throws Exception {

		UpdateProcessRequest.Builder builder =
			new UpdateProcessRequest.Builder();

		_processWorkflowMetricsIndexer.updateProcess(
			builder.active(
				null
			).companyId(
				companyId
			).description(
				null
			).modifiedDate(
				new Date()
			).processId(
				processId
			).title(
				null
			).titleMap(
				null
			).version(
				version
			).build());

		_assertCount(
			_indexNameBuilder.getIndexName(companyId) +
				WorkflowMetricsIndexNameConstants.SUFFIX_PROCESS,
			"companyId", companyId, "deleted", false, "processId", processId,
			"version", version);
	}

	private void _assertCount(
			Consumer<BooleanQuery> booleanQueryConsumer, long expectedCount,
			String indexName, Object... parameters)
		throws Exception {

		if ((_searchEngineAdapter == null) || (parameters == null)) {
			return;
		}

		if ((parameters.length % 2) != 0) {
			throw new IllegalArgumentException(
				"Parameters length is not an even number");
		}

		CountSearchRequest countSearchRequest = new CountSearchRequest();

		countSearchRequest.setIndexNames(indexName);

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		for (int i = 0; i < parameters.length; i = i + 2) {
			booleanQuery.addMustQueryClauses(
				QueriesUtil.term(
					String.valueOf(parameters[i]), parameters[i + 1]));
		}

		booleanQueryConsumer.accept(booleanQuery);

		countSearchRequest.setQuery(booleanQuery);

		CountSearchResponse countSearchResponse = _searchEngineAdapter.execute(
			countSearchRequest);

		Assert.assertEquals(expectedCount, countSearchResponse.getCount());
	}

	private void _assertCount(
			long expectedCount, String indexName, Object... parameters)
		throws Exception {

		_assertCount(
			booleanQuery -> {
			},
			expectedCount, indexName, parameters);
	}

	private void _assertCount(String indexName, Object... parameters)
		throws Exception {

		_assertCount(1, indexName, parameters);
	}

	private Map<Locale, String> _createLocalizationMap(String value) {
		Map<Locale, String> localizationMap = new HashMap<>();

		for (Locale availableLocale : _language.getAvailableLocales()) {
			localizationMap.put(availableLocale, value);
		}

		return localizationMap;
	}

	private Document _creatWorkflowMetricsSLAInstanceResultDocument(
		long companyId, Instance instance, SLAResult slaResult) {

		DocumentBuilder documentBuilder = DocumentBuilderFactory.builder();

		documentBuilder.setValue(
			"active", true
		).setValue(
			"blocked", false
		).setValue(
			"companyId", companyId
		).setValue(
			"deleted", false
		).setValue(
			"elapsedTime", slaResult.getOnTime() ? 1000 : -1000
		).setValue(
			"instanceCompleted", Objects.nonNull(instance.getDateCompletion())
		).setValue(
			"instanceId", instance.getId()
		).setValue(
			"modifiedDate", _getDateString(slaResult.getDateModified())
		).setValue(
			"onTime", slaResult.getOnTime()
		).setValue(
			"overdueDate", _getDateString(slaResult.getDateOverdue())
		).setValue(
			"processId", instance.getProcessId()
		).setValue(
			"remainingTime", slaResult.getRemainingTime()
		).setValue(
			"slaDefinitionId", slaResult.getId()
		);

		if (slaResult.getStatus() != null) {
			SLAResult.Status status = slaResult.getStatus();

			documentBuilder.setValue("status", status.getValue());
		}
		else {
			documentBuilder.setValue(
				"status", SLAResult.Status.RUNNING.getValue());
		}

		documentBuilder.setString(
			"uid",
			_digest(
				"WorkflowMetricsSLAInstanceResult", companyId, instance.getId(),
				instance.getProcessId(), slaResult.getId()));

		return documentBuilder.build();
	}

	private Document _creatWorkflowMetricsSLATaskResultDocument(
		long assigneeId, boolean breached, long companyId,
		boolean instanceCompleted, long instanceId, long nodeId, boolean onTime,
		long processId, long slaDefinitionId, String status, long taskId,
		String taskName) {

		DocumentBuilder documentBuilder = DocumentBuilderFactory.builder();

		documentBuilder.setValue(
			"active", true
		).setValue(
			"assigneeIds", assigneeId
		).setValue(
			"assigneeType", User.class.getName()
		).setValue(
			"breached", breached
		).setValue(
			"companyId", companyId
		);

		if (Objects.equals(status, "COMPLETED")) {
			documentBuilder.setDate(
				"completionDate", _getDateString(new Date())
			).setValue(
				"completionUserId", assigneeId
			);
		}

		documentBuilder.setValue(
			"deleted", false
		).setValue(
			"elapsedTime", onTime ? 1000 : -1000
		).setValue(
			"instanceCompleted", instanceCompleted
		).setValue(
			"instanceId", instanceId
		).setValue(
			"nodeId", nodeId
		).setValue(
			"onTime", onTime
		).setValue(
			"processId", processId
		).setValue(
			"slaDefinitionId", slaDefinitionId
		).setValue(
			"status", status
		).setValue(
			"taskId", taskId
		).setValue(
			"taskName", taskName
		).setString(
			"uid",
			_digest(
				"WorkflowMetricsSLATaskResult", companyId, instanceId,
				processId, slaDefinitionId, taskId)
		);

		return documentBuilder.build();
	}

	private void _deleteDocuments(String indexName, Object... parameters)
		throws Exception {

		if (_searchEngineAdapter == null) {
			return;
		}

		SearchSearchRequest searchSearchRequest = new SearchSearchRequest();

		searchSearchRequest.setIndexNames(indexName);

		BooleanQuery booleanQuery = QueriesUtil.booleanQuery();

		for (int j = 0; j < parameters.length; j = j + 2) {
			booleanQuery.addMustQueryClauses(
				QueriesUtil.term(
					String.valueOf(parameters[j]), parameters[j + 1]));
		}

		searchSearchRequest.setQuery(booleanQuery);

		searchSearchRequest.setSize(10000);

		SearchSearchResponse searchSearchResponse =
			_searchEngineAdapter.execute(searchSearchRequest);

		SearchHits searchHits = searchSearchResponse.getSearchHits();

		for (SearchHit searchHit : searchHits.getSearchHits()) {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.builder(
				searchHit.getDocument());

			documentBuilder = documentBuilder.setValue("deleted", true);

			Document document = documentBuilder.build();

			UpdateDocumentRequest updateDocumentRequest =
				new UpdateDocumentRequest(
					indexName, document.getString("uid"), document);

			updateDocumentRequest.setRefresh(true);
			updateDocumentRequest.setUpsert(true);

			_searchEngineAdapter.execute(updateDocumentRequest);
		}

		_assertCount(
			searchSearchResponse.getCount(), indexName,
			ArrayUtil.append(new Object[] {"deleted", true}, parameters));
	}

	private String _digest(String indexNamePrefix, Serializable... parts) {
		StringBundler sb = new StringBundler();

		for (Serializable part : parts) {
			sb.append(part);
		}

		return indexNamePrefix + DigestUtils.sha256Hex(sb.toString());
	}

	private String _getDateString(Date date) {
		try {
			return DateUtil.getDate(
				date, "yyyyMMddHHmmss", LocaleUtil.getDefault());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return null;
		}
	}

	private Object _getIndexer(String className) throws Exception {
		Object indexer = _indexers.get(className);

		if (indexer != null) {
			return indexer;
		}

		Bundle bundle = FrameworkUtil.getBundle(
			WorkflowMetricsRESTTestHelper.class);

		BundleContext bundleContext = bundle.getBundleContext();

		int count = 0;

		ServiceReference<?> serviceReference = null;

		do {
			ServiceReference<?>[] serviceReferences =
				bundleContext.getServiceReferences(
					className, "(objectClass=" + className + ")");

			if (ArrayUtil.isEmpty(serviceReferences)) {
				count++;

				if (count >= 5) {
					throw new IllegalStateException(
						"Unable to get reference to " + className);
				}

				Thread.sleep(500);
			}

			serviceReference = serviceReferences[0];
		}
		while (serviceReference == null);

		indexer = bundleContext.getService(serviceReference);

		_indexers.put(className, indexer);

		return indexer;
	}

	private void _invokeAddDocument(Object indexer, Document document)
		throws Exception {

		_invokeMethod(indexer, "addDocument", document);
	}

	private void _invokeMethod(
			Object indexer, String methodName, Document document)
		throws Exception {

		Class<?> indexerClass = indexer.getClass();

		Method method = null;

		while ((indexerClass != Object.class) && (method == null)) {
			try {
				method = ReflectionUtil.getDeclaredMethod(
					indexerClass, methodName, Document.class);
			}
			catch (NoSuchMethodException noSuchMethodException) {
			}

			indexerClass = indexerClass.getSuperclass();
		}

		method.invoke(indexer, document);
	}

	private Date _parseDate(String dateString) {
		try {
			return DateUtil.parseDate(
				"yyyyMMddHHmmss", dateString, LocaleUtil.getDefault());
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(exception);
			}

			return new Date();
		}
	}

	private Task _toTask(AddTaskRequest addTaskRequest, Long duration) {
		Task task = new Task();

		task.setAssignee(
			new Assignee() {
				{
					id = -1L;
				}
			});

		List<Assignment> assignments = addTaskRequest.getAssignments();

		if (assignments.get(0) instanceof UserAssignment) {
			UserAssignment userAssignment = (UserAssignment)assignments.get(0);

			task.setAssignee(
				new Assignee() {
					{
						id = userAssignment.getAssignmentId();
						name = userAssignment.getName();
					}
				});
		}

		task.setClassName(addTaskRequest.getClassName());
		task.setClassPK(addTaskRequest.getInstanceId());
		task.setCompleted(addTaskRequest.isCompleted());
		task.setDateCompletion(addTaskRequest.getCompletionDate());
		task.setCompletionUserId(addTaskRequest.getCompletionUserId());
		task.setDateCreated(addTaskRequest.getCreateDate());
		task.setDateModified(addTaskRequest.getModifiedDate());
		task.setDuration(duration);
		task.setId(addTaskRequest.getTaskId());
		task.setInstanceId(addTaskRequest.getInstanceId());
		task.setName(addTaskRequest.getName());
		task.setNodeId(addTaskRequest.getNodeId());
		task.setProcessId(addTaskRequest.getProcessId());
		task.setProcessVersion(addTaskRequest.getProcessVersion());

		return task;
	}

	private void _updateInstance(
			long companyId, Instance instance, SLAResult... slaResults)
		throws Exception {

		DocumentBuilder documentBuilder = DocumentBuilderFactory.builder();

		String indexName = _indexNameBuilder.getIndexName(companyId);

		Document document = documentBuilder.setValue(
			"slaResults",
			TransformUtil.transform(
				slaResults,
				slaResult -> HashMapBuilder.put(
					"onTime", String.valueOf(slaResult.getOnTime())
				).put(
					"overdueDate", _getDateString(slaResult.getDateOverdue())
				).put(
					"remainingTime",
					String.valueOf(slaResult.getRemainingTime())
				).put(
					"slaDefinitionId", String.valueOf(slaResult.getId())
				).put(
					"status", slaResult.getStatusAsString()
				).build(),
				Object.class)
		).setString(
			"uid",
			_digest("WorkflowMetricsInstance", companyId, instance.getId())
		).build();

		UpdateDocumentRequest updateDocumentRequest = new UpdateDocumentRequest(
			indexName + WorkflowMetricsIndexNameConstants.SUFFIX_INSTANCE,
			document.getString("uid"), document);

		updateDocumentRequest.setRefresh(true);

		_searchEngineAdapter.execute(updateDocumentRequest);

		for (SLAResult slaResult : slaResults) {
			_assertCount(
				booleanQuery -> booleanQuery.addMustQueryClauses(
					QueriesUtil.nested(
						"slaResults",
						QueriesUtil.term(
							"slaResults.overdueDate",
							_getDateString(slaResult.getDateOverdue())))),
				1,
				indexName + WorkflowMetricsIndexNameConstants.SUFFIX_INSTANCE,
				"companyId", companyId, "deleted", false, "instanceId",
				instance.getId(), "processId", instance.getProcessId());
		}
	}

	private static final String _CLASS_NAME_SLA_INSTANCE_RESULT_INDEXER =
		"com.liferay.portal.workflow.metrics.internal.search.index." +
			"SLAInstanceResultWorkflowMetricsIndexer";

	private static final String _CLASS_NAME_SLA_TASK_RESULT_INDEXER =
		"com.liferay.portal.workflow.metrics.internal.search.index." +
			"SLATaskResultWorkflowMetricsIndexer";

	private static final Log _log = LogFactoryUtil.getLog(
		WorkflowMetricsRESTTestHelper.class);

	private final Map<String, Object> _indexers = new HashMap<>();

	@Reference
	private IndexNameBuilder _indexNameBuilder;

	@Reference
	private InstanceWorkflowMetricsIndexer _instanceWorkflowMetricsIndexer;

	@Reference
	private Language _language;

	@Reference
	private NodeWorkflowMetricsIndexer _nodeWorkflowMetricsIndexer;

	@Reference
	private ProcessWorkflowMetricsIndexer _processWorkflowMetricsIndexer;

	@Reference(
		target = "(|(search.engine.impl=Elasticsearch)(search.engine.impl=OpenSearch))"
	)
	private volatile SearchEngineAdapter _searchEngineAdapter;

	@Reference
	private TaskWorkflowMetricsIndexer _taskWorkflowMetricsIndexer;

	@Reference
	private UserLocalService _userLocalService;

}