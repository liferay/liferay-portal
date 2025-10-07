/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.internal.graphql.query.v1_0;

import com.liferay.headless.admin.workflow.dto.v1_0.Assignee;
import com.liferay.headless.admin.workflow.dto.v1_0.Transition;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowDefinition;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowDefinitionLink;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowInstance;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowLog;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTask;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTaskAssignToUser;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTasksBulkSelection;
import com.liferay.headless.admin.workflow.resource.v1_0.AssigneeResource;
import com.liferay.headless.admin.workflow.resource.v1_0.TransitionResource;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowDefinitionLinkResource;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowDefinitionResource;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowInstanceResource;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowLogResource;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowTaskResource;
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

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import jakarta.ws.rs.core.UriInfo;

import java.util.Map;
import java.util.function.BiFunction;

import org.osgi.service.component.ComponentServiceObjects;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class Query {

	public static void setAssigneeResourceComponentServiceObjects(
		ComponentServiceObjects<AssigneeResource>
			assigneeResourceComponentServiceObjects) {

		_assigneeResourceComponentServiceObjects =
			assigneeResourceComponentServiceObjects;
	}

	public static void setTransitionResourceComponentServiceObjects(
		ComponentServiceObjects<TransitionResource>
			transitionResourceComponentServiceObjects) {

		_transitionResourceComponentServiceObjects =
			transitionResourceComponentServiceObjects;
	}

	public static void setWorkflowDefinitionResourceComponentServiceObjects(
		ComponentServiceObjects<WorkflowDefinitionResource>
			workflowDefinitionResourceComponentServiceObjects) {

		_workflowDefinitionResourceComponentServiceObjects =
			workflowDefinitionResourceComponentServiceObjects;
	}

	public static void setWorkflowDefinitionLinkResourceComponentServiceObjects(
		ComponentServiceObjects<WorkflowDefinitionLinkResource>
			workflowDefinitionLinkResourceComponentServiceObjects) {

		_workflowDefinitionLinkResourceComponentServiceObjects =
			workflowDefinitionLinkResourceComponentServiceObjects;
	}

	public static void setWorkflowInstanceResourceComponentServiceObjects(
		ComponentServiceObjects<WorkflowInstanceResource>
			workflowInstanceResourceComponentServiceObjects) {

		_workflowInstanceResourceComponentServiceObjects =
			workflowInstanceResourceComponentServiceObjects;
	}

	public static void setWorkflowLogResourceComponentServiceObjects(
		ComponentServiceObjects<WorkflowLogResource>
			workflowLogResourceComponentServiceObjects) {

		_workflowLogResourceComponentServiceObjects =
			workflowLogResourceComponentServiceObjects;
	}

	public static void setWorkflowTaskResourceComponentServiceObjects(
		ComponentServiceObjects<WorkflowTaskResource>
			workflowTaskResourceComponentServiceObjects) {

		_workflowTaskResourceComponentServiceObjects =
			workflowTaskResourceComponentServiceObjects;
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowTaskAssignableUsers(page: ___, pageSize: ___, workflowTaskId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public AssigneePage workflowTaskAssignableUsers(
			@GraphQLName("workflowTaskId") Long workflowTaskId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_assigneeResourceComponentServiceObjects,
			this::_populateResourceContext,
			assigneeResource -> new AssigneePage(
				assigneeResource.getWorkflowTaskAssignableUsersPage(
					workflowTaskId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowInstanceNextTransitions(page: ___, pageSize: ___, workflowInstanceId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TransitionPage workflowInstanceNextTransitions(
			@GraphQLName("workflowInstanceId") Long workflowInstanceId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_transitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			transitionResource -> new TransitionPage(
				transitionResource.getWorkflowInstanceNextTransitionsPage(
					workflowInstanceId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowTaskNextTransitions(page: ___, pageSize: ___, workflowTaskId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public TransitionPage workflowTaskNextTransitions(
			@GraphQLName("workflowTaskId") Long workflowTaskId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_transitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			transitionResource -> new TransitionPage(
				transitionResource.getWorkflowTaskNextTransitionsPage(
					workflowTaskId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowDefinition(workflowDefinitionId: ___){actions, active, content, creator, dateCreated, dateModified, description, externalReferenceCode, id, name, nodes, title, title_i18n, transitions, version}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowDefinition workflowDefinition(
			@GraphQLName("workflowDefinitionId") Long workflowDefinitionId)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowDefinitionResource ->
				workflowDefinitionResource.getWorkflowDefinition(
					workflowDefinitionId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowDefinitionByName(contentFormat: ___, name: ___, version: ___){actions, active, content, creator, dateCreated, dateModified, description, externalReferenceCode, id, name, nodes, title, title_i18n, transitions, version}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowDefinition workflowDefinitionByName(
			@GraphQLName("name") String name,
			@GraphQLName("contentFormat") String contentFormat,
			@GraphQLName("version") Integer version)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowDefinitionResource ->
				workflowDefinitionResource.getWorkflowDefinitionByName(
					name, contentFormat, version));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowDefinitions(active: ___, page: ___, pageSize: ___, sorts: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowDefinitionPage workflowDefinitions(
			@GraphQLName("active") Boolean active,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page,
			@GraphQLName("sort") String sortsString)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowDefinitionResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowDefinitionResource -> new WorkflowDefinitionPage(
				workflowDefinitionResource.getWorkflowDefinitionsPage(
					active, Pagination.of(page, pageSize),
					_sortsBiFunction.apply(
						workflowDefinitionResource, sortsString))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinks(externalReferenceCode: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowDefinitionLinkPage
			workflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinks(
				@GraphQLName("externalReferenceCode") String
					externalReferenceCode,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowDefinitionLinkResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowDefinitionLinkResource -> new WorkflowDefinitionLinkPage(
				workflowDefinitionLinkResource.
					getWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage(
						externalReferenceCode, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowDefinitionWorkflowDefinitionLinks(page: ___, pageSize: ___, workflowDefinitionId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowDefinitionLinkPage workflowDefinitionWorkflowDefinitionLinks(
			@GraphQLName("workflowDefinitionId") Long workflowDefinitionId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowDefinitionLinkResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowDefinitionLinkResource -> new WorkflowDefinitionLinkPage(
				workflowDefinitionLinkResource.
					getWorkflowDefinitionWorkflowDefinitionLinksPage(
						workflowDefinitionId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowInstance(workflowInstanceId: ___){actions, completed, currentNodeNames, dateCompletion, dateCreated, id, objectReviewed, workflowDefinitionName, workflowDefinitionVersion}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowInstance workflowInstance(
			@GraphQLName("workflowInstanceId") Long workflowInstanceId)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowInstanceResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowInstanceResource ->
				workflowInstanceResource.getWorkflowInstance(
					workflowInstanceId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowInstances(assetClassName: ___, assetPrimaryKey: ___, completed: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowInstancePage workflowInstances(
			@GraphQLName("assetClassName") String assetClassName,
			@GraphQLName("assetPrimaryKey") Long assetPrimaryKey,
			@GraphQLName("completed") Boolean completed,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowInstanceResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowInstanceResource -> new WorkflowInstancePage(
				workflowInstanceResource.getWorkflowInstancesPage(
					assetClassName, assetPrimaryKey, completed,
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowInstanceWorkflowLogs(page: ___, pageSize: ___, types: ___, workflowInstanceId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowLogPage workflowInstanceWorkflowLogs(
			@GraphQLName("workflowInstanceId") Long workflowInstanceId,
			@GraphQLName("types") String[] types,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowLogResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowLogResource -> new WorkflowLogPage(
				workflowLogResource.getWorkflowInstanceWorkflowLogsPage(
					workflowInstanceId, types, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowLog(workflowLogId: ___){auditPerson, commentLog, dateCreated, description, id, person, previousPerson, previousRole, previousState, previousStateLabel, role, state, stateLabel, type, workflowTaskId}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowLog workflowLog(
			@GraphQLName("workflowLogId") Long workflowLogId)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowLogResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowLogResource -> workflowLogResource.getWorkflowLog(
				workflowLogId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowTaskWorkflowLogs(page: ___, pageSize: ___, types: ___, workflowTaskId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowLogPage workflowTaskWorkflowLogs(
			@GraphQLName("workflowTaskId") Long workflowTaskId,
			@GraphQLName("types") String[] types,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowLogResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowLogResource -> new WorkflowLogPage(
				workflowLogResource.getWorkflowTaskWorkflowLogsPage(
					workflowTaskId, types, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowInstanceWorkflowTasksAssignedToMe(completed: ___, page: ___, pageSize: ___, workflowInstanceId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowTaskPage workflowInstanceWorkflowTasksAssignedToMe(
			@GraphQLName("workflowInstanceId") Long workflowInstanceId,
			@GraphQLName("completed") Boolean completed,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowTaskResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowTaskResource -> new WorkflowTaskPage(
				workflowTaskResource.
					getWorkflowInstanceWorkflowTasksAssignedToMePage(
						workflowInstanceId, completed,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowInstanceWorkflowTasksAssignedToUser(assigneeId: ___, completed: ___, page: ___, pageSize: ___, workflowInstanceId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowTaskPage workflowInstanceWorkflowTasksAssignedToUser(
			@GraphQLName("workflowInstanceId") Long workflowInstanceId,
			@GraphQLName("assigneeId") Long assigneeId,
			@GraphQLName("completed") Boolean completed,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowTaskResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowTaskResource -> new WorkflowTaskPage(
				workflowTaskResource.
					getWorkflowInstanceWorkflowTasksAssignedToUserPage(
						workflowInstanceId, assigneeId, completed,
						Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowInstanceWorkflowTasks(completed: ___, page: ___, pageSize: ___, workflowInstanceId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowTaskPage workflowInstanceWorkflowTasks(
			@GraphQLName("workflowInstanceId") Long workflowInstanceId,
			@GraphQLName("completed") Boolean completed,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowTaskResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowTaskResource -> new WorkflowTaskPage(
				workflowTaskResource.getWorkflowInstanceWorkflowTasksPage(
					workflowInstanceId, completed,
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowTask(workflowTaskId: ___){actions, assigneePerson, assigneeRoles, completed, dateCompletion, dateCreated, dateDue, description, id, label, name, objectReviewed, workflowDefinitionId, workflowDefinitionName, workflowDefinitionVersion, workflowInstanceId, workflowLogs}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowTask workflowTask(
			@GraphQLName("workflowTaskId") Long workflowTaskId)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowTaskResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowTaskResource -> workflowTaskResource.getWorkflowTask(
				workflowTaskId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowTaskHasAssignableUsers(workflowTaskId: ___){}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public Boolean workflowTaskHasAssignableUsers(
			@GraphQLName("workflowTaskId") Long workflowTaskId)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowTaskResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowTaskResource ->
				workflowTaskResource.getWorkflowTaskHasAssignableUsers(
					workflowTaskId));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowTasksAssignedToMe(page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowTaskPage workflowTasksAssignedToMe(
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowTaskResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowTaskResource -> new WorkflowTaskPage(
				workflowTaskResource.getWorkflowTasksAssignedToMePage(
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowTasksAssignedToMyRoles(page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowTaskPage workflowTasksAssignedToMyRoles(
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowTaskResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowTaskResource -> new WorkflowTaskPage(
				workflowTaskResource.getWorkflowTasksAssignedToMyRolesPage(
					Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowTasksAssignedToRole(page: ___, pageSize: ___, roleId: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowTaskPage workflowTasksAssignedToRole(
			@GraphQLName("roleId") Long roleId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowTaskResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowTaskResource -> new WorkflowTaskPage(
				workflowTaskResource.getWorkflowTasksAssignedToRolePage(
					roleId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowTasksAssignedToUser(assigneeId: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowTaskPage workflowTasksAssignedToUser(
			@GraphQLName("assigneeId") Long assigneeId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowTaskResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowTaskResource -> new WorkflowTaskPage(
				workflowTaskResource.getWorkflowTasksAssignedToUserPage(
					assigneeId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowTasksAssignedToUserRoles(assigneeId: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowTaskPage workflowTasksAssignedToUserRoles(
			@GraphQLName("assigneeId") Long assigneeId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowTaskResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowTaskResource -> new WorkflowTaskPage(
				workflowTaskResource.getWorkflowTasksAssignedToUserRolesPage(
					assigneeId, Pagination.of(page, pageSize))));
	}

	/**
	 * Invoke this method with the command line:
	 *
	 * curl -H 'Content-Type: text/plain; charset=utf-8' -X 'POST' 'http://localhost:8080/o/graphql' -d $'{"query": "query {workflowTasksSubmittingUser(creatorId: ___, page: ___, pageSize: ___){items {__}, page, pageSize, totalCount}}"}' -u 'test@liferay.com:test'
	 */
	@GraphQLField
	public WorkflowTaskPage workflowTasksSubmittingUser(
			@GraphQLName("creatorId") Long creatorId,
			@GraphQLName("pageSize") int pageSize,
			@GraphQLName("page") int page)
		throws Exception {

		return _applyComponentServiceObjects(
			_workflowTaskResourceComponentServiceObjects,
			this::_populateResourceContext,
			workflowTaskResource -> new WorkflowTaskPage(
				workflowTaskResource.getWorkflowTasksSubmittingUserPage(
					creatorId, Pagination.of(page, pageSize))));
	}

	@GraphQLTypeExtension(WorkflowInstance.class)
	public class GetWorkflowInstanceWorkflowTasksPageTypeExtension {

		public GetWorkflowInstanceWorkflowTasksPageTypeExtension(
			WorkflowInstance workflowInstance) {

			_workflowInstance = workflowInstance;
		}

		@GraphQLField
		public WorkflowTaskPage workflowTasks(
				@GraphQLName("completed") Boolean completed,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_workflowTaskResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				workflowTaskResource -> new WorkflowTaskPage(
					workflowTaskResource.getWorkflowInstanceWorkflowTasksPage(
						_workflowInstance.getId(), completed,
						Pagination.of(page, pageSize))));
		}

		private WorkflowInstance _workflowInstance;

	}

	@GraphQLTypeExtension(WorkflowInstance.class)
	public class GetWorkflowInstanceWorkflowLogsPageTypeExtension {

		public GetWorkflowInstanceWorkflowLogsPageTypeExtension(
			WorkflowInstance workflowInstance) {

			_workflowInstance = workflowInstance;
		}

		@GraphQLField
		public WorkflowLogPage workflowLogs(
				@GraphQLName("types") String[] types,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_workflowLogResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				workflowLogResource -> new WorkflowLogPage(
					workflowLogResource.getWorkflowInstanceWorkflowLogsPage(
						_workflowInstance.getId(), types,
						Pagination.of(page, pageSize))));
		}

		private WorkflowInstance _workflowInstance;

	}

	@GraphQLTypeExtension(WorkflowTask.class)
	public class GetWorkflowTaskHasAssignableUsersTypeExtension {

		public GetWorkflowTaskHasAssignableUsersTypeExtension(
			WorkflowTask workflowTask) {

			_workflowTask = workflowTask;
		}

		@GraphQLField
		public Boolean hasAssignableUsers() throws Exception {
			return _applyComponentServiceObjects(
				_workflowTaskResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				workflowTaskResource ->
					workflowTaskResource.getWorkflowTaskHasAssignableUsers(
						_workflowTask.getId()));
		}

		private WorkflowTask _workflowTask;

	}

	@GraphQLTypeExtension(WorkflowInstance.class)
	public class GetWorkflowInstanceWorkflowTasksAssignedToMePageTypeExtension {

		public GetWorkflowInstanceWorkflowTasksAssignedToMePageTypeExtension(
			WorkflowInstance workflowInstance) {

			_workflowInstance = workflowInstance;
		}

		@GraphQLField
		public WorkflowTaskPage workflowTasksAssignedToMe(
				@GraphQLName("completed") Boolean completed,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_workflowTaskResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				workflowTaskResource -> new WorkflowTaskPage(
					workflowTaskResource.
						getWorkflowInstanceWorkflowTasksAssignedToMePage(
							_workflowInstance.getId(), completed,
							Pagination.of(page, pageSize))));
		}

		private WorkflowInstance _workflowInstance;

	}

	@GraphQLTypeExtension(WorkflowTask.class)
	public class GetWorkflowInstanceTypeExtension {

		public GetWorkflowInstanceTypeExtension(WorkflowTask workflowTask) {
			_workflowTask = workflowTask;
		}

		@GraphQLField
		public WorkflowInstance workflowInstance() throws Exception {
			return _applyComponentServiceObjects(
				_workflowInstanceResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				workflowInstanceResource ->
					workflowInstanceResource.getWorkflowInstance(
						_workflowTask.getWorkflowInstanceId()));
		}

		private WorkflowTask _workflowTask;

	}

	@GraphQLTypeExtension(WorkflowTasksBulkSelection.class)
	public class GetWorkflowDefinitionTypeExtension {

		public GetWorkflowDefinitionTypeExtension(
			WorkflowTasksBulkSelection workflowTasksBulkSelection) {

			_workflowTasksBulkSelection = workflowTasksBulkSelection;
		}

		@GraphQLField
		public WorkflowDefinition workflowDefinition() throws Exception {
			return _applyComponentServiceObjects(
				_workflowDefinitionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				workflowDefinitionResource ->
					workflowDefinitionResource.getWorkflowDefinition(
						_workflowTasksBulkSelection.getWorkflowDefinitionId()));
		}

		private WorkflowTasksBulkSelection _workflowTasksBulkSelection;

	}

	@GraphQLTypeExtension(WorkflowTaskAssignToUser.class)
	public class GetWorkflowTaskTypeExtension {

		public GetWorkflowTaskTypeExtension(
			WorkflowTaskAssignToUser workflowTaskAssignToUser) {

			_workflowTaskAssignToUser = workflowTaskAssignToUser;
		}

		@GraphQLField
		public WorkflowTask workflowTask() throws Exception {
			return _applyComponentServiceObjects(
				_workflowTaskResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				workflowTaskResource -> workflowTaskResource.getWorkflowTask(
					_workflowTaskAssignToUser.getWorkflowTaskId()));
		}

		private WorkflowTaskAssignToUser _workflowTaskAssignToUser;

	}

	@GraphQLTypeExtension(WorkflowTask.class)
	public class GetWorkflowTaskAssignableUsersPageTypeExtension {

		public GetWorkflowTaskAssignableUsersPageTypeExtension(
			WorkflowTask workflowTask) {

			_workflowTask = workflowTask;
		}

		@GraphQLField
		public AssigneePage assignableUsers(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_assigneeResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				assigneeResource -> new AssigneePage(
					assigneeResource.getWorkflowTaskAssignableUsersPage(
						_workflowTask.getId(), Pagination.of(page, pageSize))));
		}

		private WorkflowTask _workflowTask;

	}

	@GraphQLTypeExtension(WorkflowTask.class)
	public class GetWorkflowTaskNextTransitionsPageTypeExtension {

		public GetWorkflowTaskNextTransitionsPageTypeExtension(
			WorkflowTask workflowTask) {

			_workflowTask = workflowTask;
		}

		@GraphQLField
		public TransitionPage nextTransitions(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_transitionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				transitionResource -> new TransitionPage(
					transitionResource.getWorkflowTaskNextTransitionsPage(
						_workflowTask.getId(), Pagination.of(page, pageSize))));
		}

		private WorkflowTask _workflowTask;

	}

	@GraphQLTypeExtension(WorkflowDefinition.class)
	public class GetWorkflowDefinitionWorkflowDefinitionLinksPageTypeExtension {

		public GetWorkflowDefinitionWorkflowDefinitionLinksPageTypeExtension(
			WorkflowDefinition workflowDefinition) {

			_workflowDefinition = workflowDefinition;
		}

		@GraphQLField
		public WorkflowDefinitionLinkPage workflowDefinitionLinks(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_workflowDefinitionLinkResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				workflowDefinitionLinkResource ->
					new WorkflowDefinitionLinkPage(
						workflowDefinitionLinkResource.
							getWorkflowDefinitionWorkflowDefinitionLinksPage(
								_workflowDefinition.getId(),
								Pagination.of(page, pageSize))));
		}

		private WorkflowDefinition _workflowDefinition;

	}

	@GraphQLTypeExtension(WorkflowInstance.class)
	public class
		GetWorkflowInstanceWorkflowTasksAssignedToUserPageTypeExtension {

		public GetWorkflowInstanceWorkflowTasksAssignedToUserPageTypeExtension(
			WorkflowInstance workflowInstance) {

			_workflowInstance = workflowInstance;
		}

		@GraphQLField
		public WorkflowTaskPage workflowTasksAssignedToUser(
				@GraphQLName("assigneeId") Long assigneeId,
				@GraphQLName("completed") Boolean completed,
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_workflowTaskResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				workflowTaskResource -> new WorkflowTaskPage(
					workflowTaskResource.
						getWorkflowInstanceWorkflowTasksAssignedToUserPage(
							_workflowInstance.getId(), assigneeId, completed,
							Pagination.of(page, pageSize))));
		}

		private WorkflowInstance _workflowInstance;

	}

	@GraphQLTypeExtension(WorkflowInstance.class)
	public class GetWorkflowInstanceNextTransitionsPageTypeExtension {

		public GetWorkflowInstanceNextTransitionsPageTypeExtension(
			WorkflowInstance workflowInstance) {

			_workflowInstance = workflowInstance;
		}

		@GraphQLField
		public TransitionPage nextTransitions(
				@GraphQLName("pageSize") int pageSize,
				@GraphQLName("page") int page)
			throws Exception {

			return _applyComponentServiceObjects(
				_transitionResourceComponentServiceObjects,
				Query.this::_populateResourceContext,
				transitionResource -> new TransitionPage(
					transitionResource.getWorkflowInstanceNextTransitionsPage(
						_workflowInstance.getId(),
						Pagination.of(page, pageSize))));
		}

		private WorkflowInstance _workflowInstance;

	}

	@GraphQLName("AssigneePage")
	public class AssigneePage {

		public AssigneePage(Page assigneePage) {
			actions = assigneePage.getActions();

			items = assigneePage.getItems();
			lastPage = assigneePage.getLastPage();
			page = assigneePage.getPage();
			pageSize = assigneePage.getPageSize();
			totalCount = assigneePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Assignee> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("TransitionPage")
	public class TransitionPage {

		public TransitionPage(Page transitionPage) {
			actions = transitionPage.getActions();

			items = transitionPage.getItems();
			lastPage = transitionPage.getLastPage();
			page = transitionPage.getPage();
			pageSize = transitionPage.getPageSize();
			totalCount = transitionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<Transition> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("WorkflowDefinitionPage")
	public class WorkflowDefinitionPage {

		public WorkflowDefinitionPage(Page workflowDefinitionPage) {
			actions = workflowDefinitionPage.getActions();

			items = workflowDefinitionPage.getItems();
			lastPage = workflowDefinitionPage.getLastPage();
			page = workflowDefinitionPage.getPage();
			pageSize = workflowDefinitionPage.getPageSize();
			totalCount = workflowDefinitionPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<WorkflowDefinition> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("WorkflowDefinitionLinkPage")
	public class WorkflowDefinitionLinkPage {

		public WorkflowDefinitionLinkPage(Page workflowDefinitionLinkPage) {
			actions = workflowDefinitionLinkPage.getActions();

			items = workflowDefinitionLinkPage.getItems();
			lastPage = workflowDefinitionLinkPage.getLastPage();
			page = workflowDefinitionLinkPage.getPage();
			pageSize = workflowDefinitionLinkPage.getPageSize();
			totalCount = workflowDefinitionLinkPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<WorkflowDefinitionLink> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("WorkflowInstancePage")
	public class WorkflowInstancePage {

		public WorkflowInstancePage(Page workflowInstancePage) {
			actions = workflowInstancePage.getActions();

			items = workflowInstancePage.getItems();
			lastPage = workflowInstancePage.getLastPage();
			page = workflowInstancePage.getPage();
			pageSize = workflowInstancePage.getPageSize();
			totalCount = workflowInstancePage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<WorkflowInstance> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("WorkflowLogPage")
	public class WorkflowLogPage {

		public WorkflowLogPage(Page workflowLogPage) {
			actions = workflowLogPage.getActions();

			items = workflowLogPage.getItems();
			lastPage = workflowLogPage.getLastPage();
			page = workflowLogPage.getPage();
			pageSize = workflowLogPage.getPageSize();
			totalCount = workflowLogPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<WorkflowLog> items;

		@GraphQLField
		protected long lastPage;

		@GraphQLField
		protected long page;

		@GraphQLField
		protected long pageSize;

		@GraphQLField
		protected long totalCount;

	}

	@GraphQLName("WorkflowTaskPage")
	public class WorkflowTaskPage {

		public WorkflowTaskPage(Page workflowTaskPage) {
			actions = workflowTaskPage.getActions();

			items = workflowTaskPage.getItems();
			lastPage = workflowTaskPage.getLastPage();
			page = workflowTaskPage.getPage();
			pageSize = workflowTaskPage.getPageSize();
			totalCount = workflowTaskPage.getTotalCount();
		}

		@GraphQLField
		protected Map<String, Map<String, String>> actions;

		@GraphQLField
		protected java.util.Collection<WorkflowTask> items;

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

	private void _populateResourceContext(AssigneeResource assigneeResource)
		throws Exception {

		assigneeResource.setContextAcceptLanguage(_acceptLanguage);
		assigneeResource.setContextCompany(_company);
		assigneeResource.setContextHttpServletRequest(_httpServletRequest);
		assigneeResource.setContextHttpServletResponse(_httpServletResponse);
		assigneeResource.setContextUriInfo(_uriInfo);
		assigneeResource.setContextUser(_user);
		assigneeResource.setGroupLocalService(_groupLocalService);
		assigneeResource.setResourceActionLocalService(
			_resourceActionLocalService);
		assigneeResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		assigneeResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(TransitionResource transitionResource)
		throws Exception {

		transitionResource.setContextAcceptLanguage(_acceptLanguage);
		transitionResource.setContextCompany(_company);
		transitionResource.setContextHttpServletRequest(_httpServletRequest);
		transitionResource.setContextHttpServletResponse(_httpServletResponse);
		transitionResource.setContextUriInfo(_uriInfo);
		transitionResource.setContextUser(_user);
		transitionResource.setGroupLocalService(_groupLocalService);
		transitionResource.setResourceActionLocalService(
			_resourceActionLocalService);
		transitionResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		transitionResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			WorkflowDefinitionResource workflowDefinitionResource)
		throws Exception {

		workflowDefinitionResource.setContextAcceptLanguage(_acceptLanguage);
		workflowDefinitionResource.setContextCompany(_company);
		workflowDefinitionResource.setContextHttpServletRequest(
			_httpServletRequest);
		workflowDefinitionResource.setContextHttpServletResponse(
			_httpServletResponse);
		workflowDefinitionResource.setContextUriInfo(_uriInfo);
		workflowDefinitionResource.setContextUser(_user);
		workflowDefinitionResource.setGroupLocalService(_groupLocalService);
		workflowDefinitionResource.setResourceActionLocalService(
			_resourceActionLocalService);
		workflowDefinitionResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		workflowDefinitionResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			WorkflowDefinitionLinkResource workflowDefinitionLinkResource)
		throws Exception {

		workflowDefinitionLinkResource.setContextAcceptLanguage(
			_acceptLanguage);
		workflowDefinitionLinkResource.setContextCompany(_company);
		workflowDefinitionLinkResource.setContextHttpServletRequest(
			_httpServletRequest);
		workflowDefinitionLinkResource.setContextHttpServletResponse(
			_httpServletResponse);
		workflowDefinitionLinkResource.setContextUriInfo(_uriInfo);
		workflowDefinitionLinkResource.setContextUser(_user);
		workflowDefinitionLinkResource.setGroupLocalService(_groupLocalService);
		workflowDefinitionLinkResource.setResourceActionLocalService(
			_resourceActionLocalService);
		workflowDefinitionLinkResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		workflowDefinitionLinkResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			WorkflowInstanceResource workflowInstanceResource)
		throws Exception {

		workflowInstanceResource.setContextAcceptLanguage(_acceptLanguage);
		workflowInstanceResource.setContextCompany(_company);
		workflowInstanceResource.setContextHttpServletRequest(
			_httpServletRequest);
		workflowInstanceResource.setContextHttpServletResponse(
			_httpServletResponse);
		workflowInstanceResource.setContextUriInfo(_uriInfo);
		workflowInstanceResource.setContextUser(_user);
		workflowInstanceResource.setGroupLocalService(_groupLocalService);
		workflowInstanceResource.setResourceActionLocalService(
			_resourceActionLocalService);
		workflowInstanceResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		workflowInstanceResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			WorkflowLogResource workflowLogResource)
		throws Exception {

		workflowLogResource.setContextAcceptLanguage(_acceptLanguage);
		workflowLogResource.setContextCompany(_company);
		workflowLogResource.setContextHttpServletRequest(_httpServletRequest);
		workflowLogResource.setContextHttpServletResponse(_httpServletResponse);
		workflowLogResource.setContextUriInfo(_uriInfo);
		workflowLogResource.setContextUser(_user);
		workflowLogResource.setGroupLocalService(_groupLocalService);
		workflowLogResource.setResourceActionLocalService(
			_resourceActionLocalService);
		workflowLogResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		workflowLogResource.setRoleLocalService(_roleLocalService);
	}

	private void _populateResourceContext(
			WorkflowTaskResource workflowTaskResource)
		throws Exception {

		workflowTaskResource.setContextAcceptLanguage(_acceptLanguage);
		workflowTaskResource.setContextCompany(_company);
		workflowTaskResource.setContextHttpServletRequest(_httpServletRequest);
		workflowTaskResource.setContextHttpServletResponse(
			_httpServletResponse);
		workflowTaskResource.setContextUriInfo(_uriInfo);
		workflowTaskResource.setContextUser(_user);
		workflowTaskResource.setGroupLocalService(_groupLocalService);
		workflowTaskResource.setResourceActionLocalService(
			_resourceActionLocalService);
		workflowTaskResource.setResourcePermissionLocalService(
			_resourcePermissionLocalService);
		workflowTaskResource.setRoleLocalService(_roleLocalService);
	}

	private static ComponentServiceObjects<AssigneeResource>
		_assigneeResourceComponentServiceObjects;
	private static ComponentServiceObjects<TransitionResource>
		_transitionResourceComponentServiceObjects;
	private static ComponentServiceObjects<WorkflowDefinitionResource>
		_workflowDefinitionResourceComponentServiceObjects;
	private static ComponentServiceObjects<WorkflowDefinitionLinkResource>
		_workflowDefinitionLinkResourceComponentServiceObjects;
	private static ComponentServiceObjects<WorkflowInstanceResource>
		_workflowInstanceResourceComponentServiceObjects;
	private static ComponentServiceObjects<WorkflowLogResource>
		_workflowLogResourceComponentServiceObjects;
	private static ComponentServiceObjects<WorkflowTaskResource>
		_workflowTaskResourceComponentServiceObjects;

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