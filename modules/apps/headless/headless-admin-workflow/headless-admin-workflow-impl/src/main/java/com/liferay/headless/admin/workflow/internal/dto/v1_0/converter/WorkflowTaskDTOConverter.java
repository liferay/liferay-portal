/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.internal.dto.v1_0.converter;

import com.liferay.headless.admin.workflow.dto.v1_0.Role;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTask;
import com.liferay.headless.admin.workflow.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.admin.workflow.internal.dto.v1_0.util.ObjectReviewedUtil;
import com.liferay.headless.admin.workflow.internal.dto.v1_0.util.RoleUtil;
import com.liferay.headless.admin.workflow.internal.resource.v1_0.WorkflowTaskResourceImpl;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowTaskAssignee;
import com.liferay.portal.kernel.workflow.WorkflowTaskManager;
import com.liferay.portal.vulcan.dto.converter.DTOConverter;
import com.liferay.portal.vulcan.dto.converter.DTOConverterContext;
import com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carolina Barbosa
 */
@Component(
	property = {
		"application.name=Liferay.Headless.Admin.Workflow", "default=true",
		"dto.class.name=com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken",
		"version=v1.0"
	},
	service = DTOConverter.class
)
public class WorkflowTaskDTOConverter
	implements DTOConverter<KaleoTaskInstanceToken, WorkflowTask> {

	@Override
	public String getContentType() {
		return KaleoTaskInstanceToken.class.getSimpleName();
	}

	@Override
	public WorkflowTask toDTO(DTOConverterContext dtoConverterContext)
		throws Exception {

		return _toWorkflowTask(
			dtoConverterContext,
			_workflowTaskManager.getWorkflowTask(
				(Long)dtoConverterContext.getId()));
	}

	@Override
	public WorkflowTask toDTO(
			DTOConverterContext dtoConverterContext,
			KaleoTaskInstanceToken kaleoTaskInstanceToken)
		throws Exception {

		return _toWorkflowTask(
			dtoConverterContext,
			_workflowTaskManager.getWorkflowTask(
				kaleoTaskInstanceToken.getKaleoTaskInstanceTokenId()));
	}

	private WorkflowTask _toWorkflowTask(
			DTOConverterContext dtoConverterContext,
			com.liferay.portal.kernel.workflow.WorkflowTask workflowTask)
		throws Exception {

		User assignedUser = _userLocalService.fetchUser(
			workflowTask.getAssigneeUserId());

		Map<String, Serializable> optionalAttributes =
			workflowTask.getOptionalAttributes();

		return new WorkflowTask() {
			{
				setActions(dtoConverterContext::getActions);
				setAssignedToMe(
					() -> {
						if (assignedUser == null) {
							return false;
						}

						return Objects.equals(
							assignedUser.getUserId(),
							dtoConverterContext.getUserId());
					});
				setAssigneePerson(
					() -> CreatorUtil.toCreator(_portal, assignedUser));
				setAssigneeRoles(
					() -> {
						List<Role> roles = new ArrayList<>();

						for (WorkflowTaskAssignee workflowTaskAssignee :
								workflowTask.getWorkflowTaskAssignees()) {

							if (!StringUtil.equals(
									workflowTaskAssignee.getAssigneeClassName(),
									com.liferay.portal.kernel.model.Role.class.
										getName())) {

								continue;
							}

							com.liferay.portal.kernel.model.Role role =
								_roleLocalService.getRole(
									workflowTaskAssignee.getAssigneeClassPK());

							roles.add(
								RoleUtil.toRole(
									dtoConverterContext.isAcceptAllLanguages(),
									dtoConverterContext.getLocale(), _portal,
									role,
									_userLocalService.fetchUser(
										role.getUserId())));
						}

						return roles.toArray(new Role[0]);
					});
				setCompleted(workflowTask::isCompleted);
				setCreator(
					() -> CreatorUtil.toCreator(
						_portal,
						_userLocalService.fetchUser(
							GetterUtil.getLong(
								optionalAttributes.get(
									WorkflowConstants.CONTEXT_USER_ID)))));
				setDateCompletion(workflowTask::getCompletionDate);
				setDateCreated(workflowTask::getCreateDate);
				setDateDue(workflowTask::getDueDate);
				setDescription(workflowTask::getDescription);
				setId(workflowTask::getWorkflowTaskId);
				setLabel(
					() -> _language.get(
						ResourceBundleUtil.getModuleAndPortalResourceBundle(
							dtoConverterContext.getLocale(),
							WorkflowTaskResourceImpl.class),
						workflowTask.getName()));
				setName(workflowTask::getName);
				setObjectReviewed(
					() -> ObjectReviewedUtil.toObjectReviewed(
						dtoConverterContext.getLocale(),
						workflowTask.getOptionalAttributes()));
				setWorkflowDefinitionId(workflowTask::getWorkflowDefinitionId);
				setWorkflowDefinitionName(
					workflowTask::getWorkflowDefinitionName);
				setWorkflowDefinitionVersion(
					() -> String.valueOf(
						workflowTask.getWorkflowDefinitionVersion()));
				setWorkflowInstanceId(workflowTask::getWorkflowInstanceId);
			}
		};
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowTaskManager _workflowTaskManager;

}