/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.internal.graphql.servlet.v1_0;

import com.liferay.headless.admin.workflow.internal.graphql.mutation.v1_0.Mutation;
import com.liferay.headless.admin.workflow.internal.graphql.query.v1_0.Query;
import com.liferay.headless.admin.workflow.internal.resource.v1_0.AssigneeResourceImpl;
import com.liferay.headless.admin.workflow.internal.resource.v1_0.TransitionResourceImpl;
import com.liferay.headless.admin.workflow.internal.resource.v1_0.WorkflowDefinitionLinkResourceImpl;
import com.liferay.headless.admin.workflow.internal.resource.v1_0.WorkflowDefinitionResourceImpl;
import com.liferay.headless.admin.workflow.internal.resource.v1_0.WorkflowInstanceResourceImpl;
import com.liferay.headless.admin.workflow.internal.resource.v1_0.WorkflowLogResourceImpl;
import com.liferay.headless.admin.workflow.internal.resource.v1_0.WorkflowTaskAssignableUsersResourceImpl;
import com.liferay.headless.admin.workflow.internal.resource.v1_0.WorkflowTaskResourceImpl;
import com.liferay.headless.admin.workflow.internal.resource.v1_0.WorkflowTaskTransitionsResourceImpl;
import com.liferay.headless.admin.workflow.resource.v1_0.AssigneeResource;
import com.liferay.headless.admin.workflow.resource.v1_0.TransitionResource;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowDefinitionLinkResource;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowDefinitionResource;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowInstanceResource;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowLogResource;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowTaskAssignableUsersResource;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowTaskResource;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowTaskTransitionsResource;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.vulcan.graphql.servlet.ServletData;

import jakarta.annotation.Generated;

import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentServiceObjects;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceScope;

/**
 * @author Javier Gamarra
 * @generated
 */
@Component(service = ServletData.class)
@Generated("")
public class ServletDataImpl implements ServletData {

	@Activate
	public void activate(BundleContext bundleContext) {
		Mutation.setWorkflowDefinitionResourceComponentServiceObjects(
			_workflowDefinitionResourceComponentServiceObjects);
		Mutation.setWorkflowDefinitionLinkResourceComponentServiceObjects(
			_workflowDefinitionLinkResourceComponentServiceObjects);
		Mutation.setWorkflowInstanceResourceComponentServiceObjects(
			_workflowInstanceResourceComponentServiceObjects);
		Mutation.setWorkflowLogResourceComponentServiceObjects(
			_workflowLogResourceComponentServiceObjects);
		Mutation.setWorkflowTaskResourceComponentServiceObjects(
			_workflowTaskResourceComponentServiceObjects);
		Mutation.setWorkflowTaskAssignableUsersResourceComponentServiceObjects(
			_workflowTaskAssignableUsersResourceComponentServiceObjects);
		Mutation.setWorkflowTaskTransitionsResourceComponentServiceObjects(
			_workflowTaskTransitionsResourceComponentServiceObjects);

		Query.setAssigneeResourceComponentServiceObjects(
			_assigneeResourceComponentServiceObjects);
		Query.setTransitionResourceComponentServiceObjects(
			_transitionResourceComponentServiceObjects);
		Query.setWorkflowDefinitionResourceComponentServiceObjects(
			_workflowDefinitionResourceComponentServiceObjects);
		Query.setWorkflowDefinitionLinkResourceComponentServiceObjects(
			_workflowDefinitionLinkResourceComponentServiceObjects);
		Query.setWorkflowInstanceResourceComponentServiceObjects(
			_workflowInstanceResourceComponentServiceObjects);
		Query.setWorkflowLogResourceComponentServiceObjects(
			_workflowLogResourceComponentServiceObjects);
		Query.setWorkflowTaskResourceComponentServiceObjects(
			_workflowTaskResourceComponentServiceObjects);
	}

	public String getApplicationName() {
		return "Liferay.Headless.Admin.Workflow";
	}

	@Override
	public Mutation getMutation() {
		return new Mutation();
	}

	@Override
	public String getPath() {
		return "/headless-admin-workflow-graphql/v1_0";
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
						"mutation#deleteWorkflowDefinition",
						new ObjectValuePair<>(
							WorkflowDefinitionResourceImpl.class,
							"deleteWorkflowDefinition"));
					put(
						"mutation#deleteWorkflowDefinitionBatch",
						new ObjectValuePair<>(
							WorkflowDefinitionResourceImpl.class,
							"deleteWorkflowDefinitionBatch"));
					put(
						"mutation#deleteWorkflowDefinitionUndeploy",
						new ObjectValuePair<>(
							WorkflowDefinitionResourceImpl.class,
							"deleteWorkflowDefinitionUndeploy"));
					put(
						"mutation#createWorkflowDefinition",
						new ObjectValuePair<>(
							WorkflowDefinitionResourceImpl.class,
							"postWorkflowDefinition"));
					put(
						"mutation#createWorkflowDefinitionBatch",
						new ObjectValuePair<>(
							WorkflowDefinitionResourceImpl.class,
							"postWorkflowDefinitionBatch"));
					put(
						"mutation#createWorkflowDefinitionDeploy",
						new ObjectValuePair<>(
							WorkflowDefinitionResourceImpl.class,
							"postWorkflowDefinitionDeploy"));
					put(
						"mutation#createWorkflowDefinitionSave",
						new ObjectValuePair<>(
							WorkflowDefinitionResourceImpl.class,
							"postWorkflowDefinitionSave"));
					put(
						"mutation#createWorkflowDefinitionUpdateActive",
						new ObjectValuePair<>(
							WorkflowDefinitionResourceImpl.class,
							"postWorkflowDefinitionUpdateActive"));
					put(
						"mutation#createWorkflowDefinitionsPageExportBatch",
						new ObjectValuePair<>(
							WorkflowDefinitionResourceImpl.class,
							"postWorkflowDefinitionsPageExportBatch"));
					put(
						"mutation#updateWorkflowDefinition",
						new ObjectValuePair<>(
							WorkflowDefinitionResourceImpl.class,
							"putWorkflowDefinition"));
					put(
						"mutation#updateWorkflowDefinitionBatch",
						new ObjectValuePair<>(
							WorkflowDefinitionResourceImpl.class,
							"putWorkflowDefinitionBatch"));
					put(
						"mutation#createWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLink",
						new ObjectValuePair<>(
							WorkflowDefinitionLinkResourceImpl.class,
							"postWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLink"));
					put(
						"mutation#createWorkflowDefinitionWorkflowDefinitionLink",
						new ObjectValuePair<>(
							WorkflowDefinitionLinkResourceImpl.class,
							"postWorkflowDefinitionWorkflowDefinitionLink"));
					put(
						"mutation#createWorkflowDefinitionWorkflowDefinitionLinkBatch",
						new ObjectValuePair<>(
							WorkflowDefinitionLinkResourceImpl.class,
							"postWorkflowDefinitionWorkflowDefinitionLinkBatch"));
					put(
						"mutation#createWorkflowDefinitionWorkflowDefinitionLinksPageExportBatch",
						new ObjectValuePair<>(
							WorkflowDefinitionLinkResourceImpl.class,
							"postWorkflowDefinitionWorkflowDefinitionLinksPageExportBatch"));
					put(
						"mutation#updateWorkflowDefinitionLinkByExternalReferenceCode",
						new ObjectValuePair<>(
							WorkflowDefinitionLinkResourceImpl.class,
							"putWorkflowDefinitionLinkByExternalReferenceCode"));
					put(
						"mutation#deleteWorkflowInstance",
						new ObjectValuePair<>(
							WorkflowInstanceResourceImpl.class,
							"deleteWorkflowInstance"));
					put(
						"mutation#deleteWorkflowInstanceBatch",
						new ObjectValuePair<>(
							WorkflowInstanceResourceImpl.class,
							"deleteWorkflowInstanceBatch"));
					put(
						"mutation#createWorkflowInstanceChangeTransition",
						new ObjectValuePair<>(
							WorkflowInstanceResourceImpl.class,
							"postWorkflowInstanceChangeTransition"));
					put(
						"mutation#createWorkflowInstanceSubmit",
						new ObjectValuePair<>(
							WorkflowInstanceResourceImpl.class,
							"postWorkflowInstanceSubmit"));
					put(
						"mutation#createWorkflowInstancesPageExportBatch",
						new ObjectValuePair<>(
							WorkflowInstanceResourceImpl.class,
							"postWorkflowInstancesPageExportBatch"));
					put(
						"mutation#createWorkflowInstanceWorkflowLogsPageExportBatch",
						new ObjectValuePair<>(
							WorkflowLogResourceImpl.class,
							"postWorkflowInstanceWorkflowLogsPageExportBatch"));
					put(
						"mutation#createWorkflowTaskWorkflowLogsPageExportBatch",
						new ObjectValuePair<>(
							WorkflowLogResourceImpl.class,
							"postWorkflowTaskWorkflowLogsPageExportBatch"));
					put(
						"mutation#patchWorkflowTaskAssignToUser",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"patchWorkflowTaskAssignToUser"));
					put(
						"mutation#patchWorkflowTaskChangeTransition",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"patchWorkflowTaskChangeTransition"));
					put(
						"mutation#patchWorkflowTaskUpdateDueDate",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"patchWorkflowTaskUpdateDueDate"));
					put(
						"mutation#createWorkflowInstanceWorkflowTasksPageExportBatch",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"postWorkflowInstanceWorkflowTasksPageExportBatch"));
					put(
						"mutation#createWorkflowTaskAssignToMe",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"postWorkflowTaskAssignToMe"));
					put(
						"mutation#createWorkflowTaskAssignToRole",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"postWorkflowTaskAssignToRole"));
					put(
						"mutation#createWorkflowTaskAssignToUser",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"postWorkflowTaskAssignToUser"));
					put(
						"mutation#createWorkflowTaskChangeTransition",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"postWorkflowTaskChangeTransition"));
					put(
						"mutation#createWorkflowTaskUpdateDueDate",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"postWorkflowTaskUpdateDueDate"));
					put(
						"mutation#createWorkflowTasksPage",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"postWorkflowTasksPage"));
					put(
						"mutation#createWorkflowTaskAssignableUser",
						new ObjectValuePair<>(
							WorkflowTaskAssignableUsersResourceImpl.class,
							"postWorkflowTaskAssignableUser"));
					put(
						"mutation#createWorkflowTaskTransition",
						new ObjectValuePair<>(
							WorkflowTaskTransitionsResourceImpl.class,
							"postWorkflowTaskTransition"));

					put(
						"query#workflowTaskAssignableUsers",
						new ObjectValuePair<>(
							AssigneeResourceImpl.class,
							"getWorkflowTaskAssignableUsersPage"));
					put(
						"query#workflowInstanceNextTransitions",
						new ObjectValuePair<>(
							TransitionResourceImpl.class,
							"getWorkflowInstanceNextTransitionsPage"));
					put(
						"query#workflowTaskNextTransitions",
						new ObjectValuePair<>(
							TransitionResourceImpl.class,
							"getWorkflowTaskNextTransitionsPage"));
					put(
						"query#workflowDefinition",
						new ObjectValuePair<>(
							WorkflowDefinitionResourceImpl.class,
							"getWorkflowDefinition"));
					put(
						"query#workflowDefinitionByName",
						new ObjectValuePair<>(
							WorkflowDefinitionResourceImpl.class,
							"getWorkflowDefinitionByName"));
					put(
						"query#workflowDefinitions",
						new ObjectValuePair<>(
							WorkflowDefinitionResourceImpl.class,
							"getWorkflowDefinitionsPage"));
					put(
						"query#workflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinks",
						new ObjectValuePair<>(
							WorkflowDefinitionLinkResourceImpl.class,
							"getWorkflowDefinitionByExternalReferenceCodeWorkflowDefinitionLinksPage"));
					put(
						"query#workflowDefinitionWorkflowDefinitionLinks",
						new ObjectValuePair<>(
							WorkflowDefinitionLinkResourceImpl.class,
							"getWorkflowDefinitionWorkflowDefinitionLinksPage"));
					put(
						"query#workflowInstance",
						new ObjectValuePair<>(
							WorkflowInstanceResourceImpl.class,
							"getWorkflowInstance"));
					put(
						"query#workflowInstances",
						new ObjectValuePair<>(
							WorkflowInstanceResourceImpl.class,
							"getWorkflowInstancesPage"));
					put(
						"query#workflowInstanceWorkflowLogs",
						new ObjectValuePair<>(
							WorkflowLogResourceImpl.class,
							"getWorkflowInstanceWorkflowLogsPage"));
					put(
						"query#workflowLog",
						new ObjectValuePair<>(
							WorkflowLogResourceImpl.class, "getWorkflowLog"));
					put(
						"query#workflowTaskWorkflowLogs",
						new ObjectValuePair<>(
							WorkflowLogResourceImpl.class,
							"getWorkflowTaskWorkflowLogsPage"));
					put(
						"query#workflowInstanceWorkflowTasksAssignedToMe",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"getWorkflowInstanceWorkflowTasksAssignedToMePage"));
					put(
						"query#workflowInstanceWorkflowTasksAssignedToUser",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"getWorkflowInstanceWorkflowTasksAssignedToUserPage"));
					put(
						"query#workflowInstanceWorkflowTasks",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"getWorkflowInstanceWorkflowTasksPage"));
					put(
						"query#workflowTask",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class, "getWorkflowTask"));
					put(
						"query#workflowTaskHasAssignableUsers",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"getWorkflowTaskHasAssignableUsers"));
					put(
						"query#workflowTasksAssignedToMe",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"getWorkflowTasksAssignedToMePage"));
					put(
						"query#workflowTasksAssignedToMyRoles",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"getWorkflowTasksAssignedToMyRolesPage"));
					put(
						"query#workflowTasksAssignedToRole",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"getWorkflowTasksAssignedToRolePage"));
					put(
						"query#workflowTasksAssignedToUser",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"getWorkflowTasksAssignedToUserPage"));
					put(
						"query#workflowTasksAssignedToUserRoles",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"getWorkflowTasksAssignedToUserRolesPage"));
					put(
						"query#workflowTasksSubmittingUser",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"getWorkflowTasksSubmittingUserPage"));

					put(
						"query#WorkflowInstance.workflowTasks",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"getWorkflowInstanceWorkflowTasksPage"));
					put(
						"query#WorkflowInstance.workflowLogs",
						new ObjectValuePair<>(
							WorkflowLogResourceImpl.class,
							"getWorkflowInstanceWorkflowLogsPage"));
					put(
						"query#WorkflowTask.hasAssignableUsers",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"getWorkflowTaskHasAssignableUsers"));
					put(
						"query#WorkflowInstance.workflowTasksAssignedToMe",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"getWorkflowInstanceWorkflowTasksAssignedToMePage"));
					put(
						"query#WorkflowTask.workflowInstance",
						new ObjectValuePair<>(
							WorkflowInstanceResourceImpl.class,
							"getWorkflowInstance"));
					put(
						"query#WorkflowTasksBulkSelection.workflowDefinition",
						new ObjectValuePair<>(
							WorkflowDefinitionResourceImpl.class,
							"getWorkflowDefinition"));
					put(
						"query#WorkflowTaskAssignToUser.workflowTask",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class, "getWorkflowTask"));
					put(
						"query#WorkflowTask.assignableUsers",
						new ObjectValuePair<>(
							AssigneeResourceImpl.class,
							"getWorkflowTaskAssignableUsersPage"));
					put(
						"query#WorkflowTask.nextTransitions",
						new ObjectValuePair<>(
							TransitionResourceImpl.class,
							"getWorkflowTaskNextTransitionsPage"));
					put(
						"query#WorkflowDefinition.workflowDefinitionLinks",
						new ObjectValuePair<>(
							WorkflowDefinitionLinkResourceImpl.class,
							"getWorkflowDefinitionWorkflowDefinitionLinksPage"));
					put(
						"query#WorkflowInstance.workflowTasksAssignedToUser",
						new ObjectValuePair<>(
							WorkflowTaskResourceImpl.class,
							"getWorkflowInstanceWorkflowTasksAssignedToUserPage"));
					put(
						"query#WorkflowInstance.nextTransitions",
						new ObjectValuePair<>(
							TransitionResourceImpl.class,
							"getWorkflowInstanceNextTransitionsPage"));
				}
			};

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WorkflowDefinitionResource>
		_workflowDefinitionResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WorkflowDefinitionLinkResource>
		_workflowDefinitionLinkResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WorkflowInstanceResource>
		_workflowInstanceResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WorkflowLogResource>
		_workflowLogResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WorkflowTaskResource>
		_workflowTaskResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WorkflowTaskAssignableUsersResource>
		_workflowTaskAssignableUsersResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<WorkflowTaskTransitionsResource>
		_workflowTaskTransitionsResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<AssigneeResource>
		_assigneeResourceComponentServiceObjects;

	@Reference(scope = ReferenceScope.PROTOTYPE_REQUIRED)
	private ComponentServiceObjects<TransitionResource>
		_transitionResourceComponentServiceObjects;

}