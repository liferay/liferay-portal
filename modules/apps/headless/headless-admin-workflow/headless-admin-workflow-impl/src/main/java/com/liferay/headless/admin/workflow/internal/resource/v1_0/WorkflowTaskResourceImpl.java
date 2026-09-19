/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.internal.resource.v1_0;

import com.liferay.headless.admin.workflow.dto.v1_0.ChangeTransition;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTask;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTaskAssignToMe;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTaskAssignToRole;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTaskAssignToUser;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTasksBulkSelection;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowTaskResource;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowException;
import com.liferay.portal.kernel.workflow.WorkflowInstance;
import com.liferay.portal.kernel.workflow.WorkflowInstanceManager;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.kernel.workflow.WorkflowTransition;
import com.liferay.portal.kernel.workflow.search.WorkflowModelSearchResult;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DefaultDTOConverterContext;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.workflow.comparator.WorkflowComparatorFactory;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/workflow-task.properties",
	scope = ServiceScope.PROTOTYPE, service = WorkflowTaskResource.class
)
@CTAware
public class WorkflowTaskResourceImpl extends BaseWorkflowTaskResourceImpl {

	@Override
	public Page<WorkflowTask> getWorkflowInstanceWorkflowTasksAssignedToMePage(
			Long workflowInstanceId, Boolean completed, Pagination pagination)
		throws Exception {

		return Page.of(
			transform(
				_workflowTaskManager.getWorkflowTasksByWorkflowInstance(
					contextCompany.getCompanyId(), contextUser.getUserId(),
					workflowInstanceId, completed,
					pagination.getStartPosition(), pagination.getEndPosition(),
					null),
				this::_toWorkflowTask),
			pagination,
			_workflowTaskManager.getWorkflowTaskCountByWorkflowInstance(
				contextCompany.getCompanyId(), contextUser.getUserId(),
				workflowInstanceId, completed));
	}

	@Override
	public Page<WorkflowTask>
			getWorkflowInstanceWorkflowTasksAssignedToUserPage(
				Long workflowInstanceId, Long assigneeId, Boolean completed,
				Pagination pagination)
		throws Exception {

		return Page.of(
			_getActions(),
			transform(
				_workflowTaskManager.getWorkflowTasksByWorkflowInstance(
					contextCompany.getCompanyId(), assigneeId,
					workflowInstanceId, completed,
					pagination.getStartPosition(), pagination.getEndPosition(),
					null),
				this::_toWorkflowTask),
			pagination,
			_workflowTaskManager.getWorkflowTaskCountByWorkflowInstance(
				contextCompany.getCompanyId(), assigneeId, workflowInstanceId,
				completed));
	}

	@Override
	public Page<WorkflowTask> getWorkflowInstanceWorkflowTasksPage(
			Long workflowInstanceId, Boolean completed, Pagination pagination)
		throws Exception {

		return Page.of(
			_getActions(),
			transform(
				_workflowTaskManager.getWorkflowTasksByWorkflowInstance(
					contextCompany.getCompanyId(), null, workflowInstanceId,
					completed, pagination.getStartPosition(),
					pagination.getEndPosition(), null),
				this::_toWorkflowTask),
			pagination,
			_workflowTaskManager.getWorkflowTaskCountByWorkflowInstance(
				contextCompany.getCompanyId(), null, workflowInstanceId,
				completed));
	}

	@Override
	public WorkflowTask getWorkflowTask(Long workflowTaskId) throws Exception {
		try {
			return _toWorkflowTask(
				_workflowTaskManager.getWorkflowTask(workflowTaskId));
		}
		catch (WorkflowException workflowException) {
			Throwable throwable = workflowException.getCause();

			if (throwable instanceof NoSuchModelException) {
				throw (NoSuchModelException)throwable;
			}

			throw workflowException;
		}
	}

	@Override
	public Boolean getWorkflowTaskHasAssignableUsers(Long workflowTaskId)
		throws Exception {

		return _workflowTaskManager.hasAssignableUsers(workflowTaskId);
	}

	@Override
	public Page<WorkflowTask> getWorkflowTasksAssignedToMePage(
			Pagination pagination)
		throws Exception {

		return Page.of(
			_getActions(),
			transform(
				_workflowTaskManager.getWorkflowTasksByUser(
					contextCompany.getCompanyId(), contextUser.getUserId(),
					null, pagination.getStartPosition(),
					pagination.getEndPosition(), null),
				this::_toWorkflowTask),
			pagination,
			_workflowTaskManager.getWorkflowTaskCountByUser(
				contextCompany.getCompanyId(), contextUser.getUserId(), null));
	}

	@Override
	public Page<WorkflowTask> getWorkflowTasksAssignedToMyRolesPage(
			Pagination pagination)
		throws Exception {

		return Page.of(
			_getActions(),
			transform(
				_workflowTaskManager.getWorkflowTasksByUserRoles(
					contextCompany.getCompanyId(), contextUser.getUserId(),
					null, pagination.getStartPosition(),
					pagination.getEndPosition(), null),
				this::_toWorkflowTask),
			pagination,
			_workflowTaskManager.getWorkflowTaskCountByUserRoles(
				contextCompany.getCompanyId(), contextUser.getUserId(), null));
	}

	@Override
	public Page<WorkflowTask> getWorkflowTasksAssignedToRolePage(
			Long roleId, Pagination pagination)
		throws Exception {

		return Page.of(
			_getActions(),
			transform(
				_workflowTaskManager.getWorkflowTasksByRole(
					contextCompany.getCompanyId(), roleId, null,
					pagination.getStartPosition(), pagination.getEndPosition(),
					null),
				this::_toWorkflowTask),
			pagination,
			_workflowTaskManager.getWorkflowTaskCountByRole(
				contextCompany.getCompanyId(), roleId, null));
	}

	@Override
	public Page<WorkflowTask> getWorkflowTasksAssignedToUserPage(
			Long assigneeId, Pagination pagination)
		throws Exception {

		return Page.of(
			_getActions(),
			transform(
				_workflowTaskManager.getWorkflowTasksByUser(
					contextCompany.getCompanyId(), assigneeId, null,
					pagination.getStartPosition(), pagination.getEndPosition(),
					null),
				this::_toWorkflowTask),
			pagination,
			_workflowTaskManager.getWorkflowTaskCountByUser(
				contextCompany.getCompanyId(), assigneeId, null));
	}

	@Override
	public Page<WorkflowTask> getWorkflowTasksAssignedToUserRolesPage(
			Long assigneeId, Pagination pagination)
		throws Exception {

		return Page.of(
			_getActions(),
			transform(
				_workflowTaskManager.getWorkflowTasksByUserRoles(
					contextCompany.getCompanyId(), assigneeId, null,
					pagination.getStartPosition(), pagination.getEndPosition(),
					null),
				this::_toWorkflowTask),
			pagination,
			_workflowTaskManager.getWorkflowTaskCountByUserRoles(
				contextCompany.getCompanyId(), assigneeId, null));
	}

	@Override
	public Page<WorkflowTask> getWorkflowTasksSubmittingUserPage(
			Long creatorId, Pagination pagination)
		throws Exception {

		return Page.of(
			_getActions(),
			transform(
				_workflowTaskManager.getWorkflowTasksBySubmittingUser(
					contextCompany.getCompanyId(), creatorId, null,
					pagination.getStartPosition(), pagination.getEndPosition(),
					null),
				this::_toWorkflowTask),
			pagination,
			_workflowTaskManager.getWorkflowTaskCountBySubmittingUser(
				contextCompany.getCompanyId(), creatorId, null));
	}

	@Override
	public void patchWorkflowTaskAssignToUser(
			WorkflowTaskAssignToUser[] workflowTaskAssignToUsers)
		throws Exception {

		try {
			for (WorkflowTaskAssignToUser workflowTaskAssignToUser :
					workflowTaskAssignToUsers) {

				_workflowTaskManager.assignWorkflowTaskToUser(
					contextCompany.getCompanyId(), contextUser.getUserId(),
					workflowTaskAssignToUser.getWorkflowTaskId(),
					workflowTaskAssignToUser.getAssigneeId(),
					workflowTaskAssignToUser.getComment(),
					workflowTaskAssignToUser.getDueDate(),
					_getWorkflowContext(
						workflowTaskAssignToUser.getWorkflowTaskId()));
			}
		}
		catch (WorkflowException workflowException) {
			Throwable throwable = workflowException.getCause();

			if (throwable instanceof NoSuchModelException) {
				throw (NoSuchModelException)throwable;
			}

			throw workflowException;
		}
	}

	@Override
	public void patchWorkflowTaskChangeTransition(
			ChangeTransition[] changeTransitions)
		throws Exception {

		try {
			for (ChangeTransition changeTransition : changeTransitions) {
				_workflowTaskManager.completeWorkflowTask(
					contextCompany.getCompanyId(), contextUser.getUserId(),
					changeTransition.getWorkflowTaskId(),
					changeTransition.getTransitionName(),
					changeTransition.getComment(),
					_getWorkflowContext(changeTransition.getWorkflowTaskId()));
			}
		}
		catch (WorkflowException workflowException) {
			Throwable throwable = workflowException.getCause();

			if (throwable instanceof NoSuchModelException) {
				throw (NoSuchModelException)throwable;
			}

			throw workflowException;
		}
	}

	@Override
	public void patchWorkflowTaskUpdateDueDate(
			WorkflowTaskAssignToMe[] workflowTaskAssignToMes)
		throws Exception {

		try {
			for (WorkflowTaskAssignToMe workflowTaskAssignToMe :
					workflowTaskAssignToMes) {

				_workflowTaskManager.updateDueDate(
					contextCompany.getCompanyId(), contextUser.getUserId(),
					workflowTaskAssignToMe.getWorkflowTaskId(),
					workflowTaskAssignToMe.getComment(),
					workflowTaskAssignToMe.getDueDate());
			}
		}
		catch (WorkflowException workflowException) {
			Throwable throwable = workflowException.getCause();

			if (throwable instanceof NoSuchModelException) {
				throw (NoSuchModelException)throwable;
			}

			throw workflowException;
		}
	}

	@Override
	public WorkflowTask postWorkflowTaskAssignToMe(
			Long workflowTaskId, WorkflowTaskAssignToMe workflowTaskAssignToMe)
		throws Exception {

		return _toWorkflowTask(
			_workflowTaskManager.assignWorkflowTaskToUser(
				contextCompany.getCompanyId(), contextUser.getUserId(),
				workflowTaskId, contextUser.getUserId(),
				workflowTaskAssignToMe.getComment(),
				workflowTaskAssignToMe.getDueDate(),
				_getWorkflowContext(workflowTaskId)));
	}

	@Override
	public WorkflowTask postWorkflowTaskAssignToRole(
			Long workflowTaskId,
			WorkflowTaskAssignToRole workflowTaskAssignToRole)
		throws Exception {

		return _toWorkflowTask(
			_workflowTaskManager.assignWorkflowTaskToRole(
				contextCompany.getCompanyId(), contextUser.getUserId(),
				workflowTaskId, workflowTaskAssignToRole.getRoleId(),
				workflowTaskAssignToRole.getComment(),
				workflowTaskAssignToRole.getDueDate(),
				_getWorkflowContext(workflowTaskId)));
	}

	@Override
	public WorkflowTask postWorkflowTaskAssignToUser(
			Long workflowTaskId,
			WorkflowTaskAssignToUser workflowTaskAssignToUser)
		throws Exception {

		return _toWorkflowTask(
			_workflowTaskManager.assignWorkflowTaskToUser(
				contextCompany.getCompanyId(), contextUser.getUserId(),
				workflowTaskId, workflowTaskAssignToUser.getAssigneeId(),
				workflowTaskAssignToUser.getComment(),
				workflowTaskAssignToUser.getDueDate(),
				_getWorkflowContext(workflowTaskId)));
	}

	@Override
	public WorkflowTask postWorkflowTaskChangeTransition(
			Long workflowTaskId, ChangeTransition changeTransition)
		throws Exception {

		return _toWorkflowTask(
			_workflowTaskManager.completeWorkflowTask(
				contextCompany.getCompanyId(), contextUser.getUserId(),
				workflowTaskId, changeTransition.getTransitionName(),
				changeTransition.getComment(),
				_getWorkflowContext(workflowTaskId)));
	}

	@Override
	public WorkflowTask postWorkflowTaskUpdateDueDate(
			Long workflowTaskId, WorkflowTaskAssignToMe workflowTaskAssignToMe)
		throws Exception {

		return _toWorkflowTask(
			_workflowTaskManager.updateDueDate(
				contextCompany.getCompanyId(), contextUser.getUserId(),
				workflowTaskId, workflowTaskAssignToMe.getComment(),
				workflowTaskAssignToMe.getDueDate()));
	}

	@Override
	public Page<WorkflowTask> postWorkflowTasksPage(
			Pagination pagination, Sort[] sorts,
			WorkflowTasksBulkSelection workflowTasksBulkSelection)
		throws Exception {

		String assigneeClassName = null;

		if (GetterUtil.getBoolean(
				workflowTasksBulkSelection.getSearchByRoles())) {

			assigneeClassName = Role.class.getName();
		}

		WorkflowModelSearchResult
			<com.liferay.portal.kernel.workflow.WorkflowTask> workflowTasks =
				_workflowTaskManager.searchWorkflowTasks(
					contextCompany.getCompanyId(), contextUser.getUserId(),
					workflowTasksBulkSelection.getAssetTitle(),
					workflowTasksBulkSelection.getWorkflowTaskNames(),
					workflowTasksBulkSelection.getAssetTypes(),
					workflowTasksBulkSelection.getAssetPrimaryKeys(),
					assigneeClassName,
					workflowTasksBulkSelection.getAssigneeIds(),
					workflowTasksBulkSelection.getDateDueStart(),
					workflowTasksBulkSelection.getDateDueEnd(),
					workflowTasksBulkSelection.getCompleted(), false,
					workflowTasksBulkSelection.getSearchByUserRoles(),
					workflowTasksBulkSelection.getWorkflowDefinitionId(),
					workflowTasksBulkSelection.getWorkflowInstanceIds(),
					GetterUtil.getBoolean(
						workflowTasksBulkSelection.getAndOperator(), true),
					pagination.getStartPosition(), pagination.getEndPosition(),
					_toOrderByComparator((Sort)ArrayUtil.getValue(sorts, 0)));

		return Page.of(
			transform(workflowTasks.getWorkflowModels(), this::_toWorkflowTask),
			pagination, workflowTasks.getLength());
	}

	private Map<String, Map<String, String>> _getActions() {
		return HashMapBuilder.<String, Map<String, String>>put(
			"assignedToMe",
			addAction(
				ActionKeys.VIEW, "getWorkflowTasksAssignedToMePage",
				WorkflowConstants.RESOURCE_NAME, null)
		).put(
			"assignedToRole",
			addAction(
				ActionKeys.VIEW, "getWorkflowTasksAssignedToRolePage",
				WorkflowConstants.RESOURCE_NAME, null)
		).put(
			"assignedToUser",
			addAction(
				ActionKeys.VIEW, "getWorkflowTasksAssignedToUserPage",
				WorkflowConstants.RESOURCE_NAME, null)
		).put(
			"assignedToUserRoles",
			addAction(
				ActionKeys.VIEW, "getWorkflowTasksAssignedToUserRolesPage",
				WorkflowConstants.RESOURCE_NAME, null)
		).build();
	}

	private Map<String, Serializable> _getWorkflowContext(long workflowTaskId)
		throws Exception {

		com.liferay.portal.kernel.workflow.WorkflowTask workflowTask =
			_workflowTaskManager.getWorkflowTask(workflowTaskId);

		WorkflowInstance workflowInstance =
			_workflowInstanceManager.getWorkflowInstance(
				contextCompany.getCompanyId(),
				workflowTask.getWorkflowInstanceId());

		Map<String, Serializable> workflowContext =
			workflowInstance.getWorkflowContext();

		workflowContext.put(
			WorkflowConstants.CONTEXT_USER_ID,
			String.valueOf(contextUser.getUserId()));

		return workflowContext;
	}

	private OrderByComparator<com.liferay.portal.kernel.workflow.WorkflowTask>
		_toOrderByComparator(Sort sort) {

		if (sort == null) {
			return null;
		}

		boolean ascending = !sort.isReverse();

		String sortFieldName = sort.getFieldName();

		if (StringUtil.startsWith(sortFieldName, "dateCompletion")) {
			return _workflowComparatorFactory.getTaskCompletionDateComparator(
				ascending);
		}
		else if (StringUtil.startsWith(sortFieldName, "dateCreated")) {
			return _workflowComparatorFactory.getTaskCreateDateComparator(
				ascending);
		}
		else if (StringUtil.startsWith(sortFieldName, "dateDue")) {
			return _workflowComparatorFactory.getTaskDueDateComparator(
				ascending);
		}
		else if (StringUtil.startsWith(sortFieldName, "name")) {
			return _workflowComparatorFactory.getTaskNameComparator(ascending);
		}

		return _workflowComparatorFactory.getTaskInstanceIdComparator(
			ascending);
	}

	private WorkflowTask _toWorkflowTask(
			Map<String, Map<String, String>> actions,
			com.liferay.portal.kernel.workflow.WorkflowTask workflowTask)
		throws Exception {

		return _workflowTaskDTOConverter.toDTO(
			new DefaultDTOConverterContext(
				contextAcceptLanguage.isAcceptAllLanguages(), actions, null,
				contextHttpServletRequest, workflowTask.getWorkflowTaskId(),
				contextAcceptLanguage.getPreferredLocale(), contextUriInfo,
				contextUser));
	}

	private WorkflowTask _toWorkflowTask(
			com.liferay.portal.kernel.workflow.WorkflowTask workflowTask)
		throws Exception {

		Map<String, Map<String, String>> actions =
			HashMapBuilder.<String, Map<String, String>>put(
				"assignToMe",
				addAction(
					ActionKeys.UPDATE, workflowTask.getWorkflowTaskId(),
					"postWorkflowTaskAssignToMe",
					_kaleoTaskInstanceTokenModelResourcePermission)
			).put(
				"assignToRole",
				addAction(
					ActionKeys.UPDATE, workflowTask.getWorkflowTaskId(),
					"postWorkflowTaskAssignToRole",
					_kaleoTaskInstanceTokenModelResourcePermission)
			).put(
				"assignToUser",
				addAction(
					ActionKeys.UPDATE, workflowTask.getWorkflowTaskId(),
					"postWorkflowTaskAssignToUser",
					_kaleoTaskInstanceTokenModelResourcePermission)
			).put(
				"changeTransition",
				addAction(
					ActionKeys.UPDATE, workflowTask.getWorkflowTaskId(),
					"postWorkflowTaskChangeTransition",
					_kaleoTaskInstanceTokenModelResourcePermission)
			).put(
				"get",
				addAction(
					ActionKeys.VIEW, workflowTask.getWorkflowTaskId(),
					"getWorkflowTask",
					_kaleoTaskInstanceTokenModelResourcePermission)
			).put(
				"updateDueDate",
				addAction(
					ActionKeys.UPDATE, workflowTask.getWorkflowTaskId(),
					"patchWorkflowTaskUpdateDueDate",
					_kaleoTaskInstanceTokenModelResourcePermission)
			).build();

		User assignedUser = _userLocalService.fetchUser(
			workflowTask.getAssigneeUserId());

		if ((assignedUser == null) ||
			!Objects.equals(
				assignedUser.getUserId(), contextUser.getUserId())) {

			return _toWorkflowTask(actions, workflowTask);
		}

		for (WorkflowTransition workflowTransition :
				_workflowTaskManager.getNextWorkflowTransitions(
					workflowTask.getWorkflowTaskId())) {

			actions.put(
				"workflow_" + workflowTransition.getName(),
				HashMapBuilder.put(
					"label",
					workflowTransition.getLabel(
						contextAcceptLanguage.getPreferredLocale())
				).put(
					"name", workflowTransition.getName()
				).putAll(
					addAction(
						ActionKeys.UPDATE, workflowTask.getWorkflowTaskId(),
						"postWorkflowTaskChangeTransition",
						_kaleoTaskInstanceTokenModelResourcePermission)
				).build());
		}

		return _toWorkflowTask(actions, workflowTask);
	}

	@Reference(
		target = "(model.class.name=com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken)"
	)
	private ModelResourcePermission<?>
		_kaleoTaskInstanceTokenModelResourcePermission;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowComparatorFactory _workflowComparatorFactory;

	@Reference
	private WorkflowInstanceManager _workflowInstanceManager;

	@Reference(
		target = "(component.name=com.liferay.headless.admin.workflow.internal.dto.v1_0.converter.WorkflowTaskDTOConverter)"
	)
	private DTOConverter<KaleoTaskInstanceToken, WorkflowTask>
		_workflowTaskDTOConverter;

	@Reference
	private WorkflowTaskManager _workflowTaskManager;

}