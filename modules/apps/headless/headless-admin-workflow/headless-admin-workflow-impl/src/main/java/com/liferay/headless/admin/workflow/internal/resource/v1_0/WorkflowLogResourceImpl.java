/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.internal.resource.v1_0;

import com.liferay.headless.admin.workflow.dto.v1_0.Role;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowLog;
import com.liferay.headless.admin.workflow.dto.v1_0.WorkflowTask;
import com.liferay.headless.admin.workflow.internal.dto.v1_0.util.CreatorUtil;
import com.liferay.headless.admin.workflow.internal.dto.v1_0.util.RoleUtil;
import com.liferay.headless.admin.workflow.internal.dto.v1_0.util.WorkflowLogUtil;
import com.liferay.headless.admin.workflow.resource.v1_0.WorkflowLogResource;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.fields.NestedFieldId;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.portal.workflow.comparator.WorkflowComparatorFactory;
import com.liferay.portal.workflow.kaleo.KaleoWorkflowModelConverter;
import com.liferay.portal.workflow.kaleo.definition.LogType;
import com.liferay.portal.workflow.kaleo.definition.util.KaleoLogUtil;
import com.liferay.portal.workflow.kaleo.service.KaleoLogLocalService;
import com.liferay.portal.workflow.manager.WorkflowLogManager;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Javier Gamarra
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/workflow-log.properties",
	property = "nested.field.support=true", scope = ServiceScope.PROTOTYPE,
	service = WorkflowLogResource.class
)
@CTAware
public class WorkflowLogResourceImpl extends BaseWorkflowLogResourceImpl {

	@Override
	public Page<WorkflowLog> getWorkflowInstanceWorkflowLogsPage(
			Long workflowInstanceId, String[] types, Pagination pagination)
		throws Exception {

		return Page.of(
			transform(
				_workflowLogManager.getWorkflowLogsByWorkflowInstance(
					contextCompany.getCompanyId(), workflowInstanceId,
					_toLogTypes(types), pagination.getStartPosition(),
					pagination.getEndPosition(),
					_workflowComparatorFactory.getLogCreateDateComparator(
						false)),
				this::_toWorkflowLog),
			pagination,
			_workflowLogManager.getWorkflowLogCountByWorkflowInstance(
				contextCompany.getCompanyId(), workflowInstanceId,
				_toLogTypes(types)));
	}

	@Override
	public WorkflowLog getWorkflowLog(Long workflowLogId) throws Exception {
		return _toWorkflowLog(
			_kaleoWorkflowModelConverter.toWorkflowLog(
				_kaleoLogLocalService.getKaleoLog(workflowLogId)));
	}

	@NestedField(parentClass = WorkflowTask.class, value = "workflowLogs")
	@Override
	public Page<WorkflowLog> getWorkflowTaskWorkflowLogsPage(
			@NestedFieldId(value = "id") Long workflowTaskId, String[] types,
			Pagination pagination)
		throws Exception {

		return Page.of(
			transform(
				_workflowLogManager.getWorkflowLogsByWorkflowTask(
					contextCompany.getCompanyId(), workflowTaskId,
					_toLogTypes(types), pagination.getStartPosition(),
					pagination.getEndPosition(),
					_workflowComparatorFactory.getLogCreateDateComparator(
						false)),
				this::_toWorkflowLog),
			pagination,
			_workflowLogManager.getWorkflowLogCountByWorkflowTask(
				contextCompany.getCompanyId(), workflowTaskId,
				_toLogTypes(types)));
	}

	private String _toLogTypeName(WorkflowLog.Type type) {
		if (type == WorkflowLog.Type.INSTANCE_FAIL) {
			return LogType.INSTANCE_FAIL.name();
		}
		else if (type == WorkflowLog.Type.NODE_ENTRY) {
			return LogType.NODE_ENTRY.name();
		}
		else if (type == WorkflowLog.Type.TASK_ASSIGN) {
			return LogType.TASK_ASSIGNMENT.name();
		}
		else if (type == WorkflowLog.Type.TASK_COMPLETION) {
			return LogType.TASK_COMPLETION.name();
		}
		else if (type == WorkflowLog.Type.TASK_UPDATE) {
			return LogType.TASK_UPDATE.name();
		}
		else if (type == WorkflowLog.Type.TRANSITION) {
			return LogType.NODE_EXIT.name();
		}

		return null;
	}

	private List<Integer> _toLogTypes(String[] types) {
		List<Integer> logTypes = transformToList(
			types,
			type -> KaleoLogUtil.convert(
				_toLogTypeName(WorkflowLog.Type.create(type))));

		ListUtil.distinct(logTypes);

		return logTypes;
	}

	private Role _toRole(long roleId) throws Exception {
		com.liferay.portal.kernel.model.Role role = _roleLocalService.fetchRole(
			roleId);

		if (role == null) {
			return null;
		}

		return RoleUtil.toRole(
			contextAcceptLanguage.isAcceptAllLanguages(),
			contextAcceptLanguage.getPreferredLocale(), _portal, role,
			_userLocalService.fetchUser(role.getUserId()));
	}

	private WorkflowLog _toWorkflowLog(
			com.liferay.portal.kernel.workflow.WorkflowLog workflowLog)
		throws Exception {

		return new WorkflowLog() {
			{
				setAuditPerson(
					() -> CreatorUtil.toCreator(
						_portal,
						_userLocalService.fetchUser(
							workflowLog.getAuditUserId())));
				setCommentLog(
					() -> _language.get(
						ResourceBundleUtil.getBundle(
							"content.Language",
							contextAcceptLanguage.getPreferredLocale(),
							getClass()),
						workflowLog.getComment()));
				setDateCreated(workflowLog::getCreateDate);
				setDescription(
					() -> WorkflowLogUtil.getDescription(
						_language, contextAcceptLanguage.getPreferredLocale(),
						_portal, _roleLocalService::fetchRole,
						_userLocalService::fetchUser, workflowLog));
				setId(workflowLog::getWorkflowLogId);
				setPerson(
					() -> CreatorUtil.toCreator(
						_portal,
						_userLocalService.fetchUser(workflowLog.getUserId())));
				setPreviousPerson(
					() -> CreatorUtil.toCreator(
						_portal,
						_userLocalService.fetchUser(
							workflowLog.getPreviousUserId())));
				setPreviousRole(() -> _toRole(workflowLog.getPreviousRoleId()));
				setPreviousState(workflowLog::getPreviousWorkflowNodeName);
				setPreviousStateLabel(
					() -> workflowLog.getPreviousWorkflowNodeLabel(
						contextAcceptLanguage.getPreferredLocale()));
				setRole(() -> _toRole(workflowLog.getRoleId()));
				setState(workflowLog::getCurrentWorkflowNodeName);
				setStateLabel(
					() -> workflowLog.getCurrentWorkflowNodeLabel(
						contextAcceptLanguage.getPreferredLocale()));
				setType(
					() -> _toWorkflowLogType(
						KaleoLogUtil.convert(workflowLog.getType())));
				setWorkflowTaskId(workflowLog::getWorkflowTaskId);
			}
		};
	}

	private WorkflowLog.Type _toWorkflowLogType(String type) {
		if (Objects.equals(type, LogType.INSTANCE_FAIL.name())) {
			return WorkflowLog.Type.INSTANCE_FAIL;
		}
		else if (Objects.equals(type, LogType.NODE_ENTRY.name())) {
			return WorkflowLog.Type.NODE_ENTRY;
		}
		else if (Objects.equals(type, LogType.NODE_EXIT.name())) {
			return WorkflowLog.Type.TRANSITION;
		}
		else if (Objects.equals(type, LogType.TASK_ASSIGNMENT.name())) {
			return WorkflowLog.Type.TASK_ASSIGN;
		}
		else if (Objects.equals(type, LogType.TASK_COMPLETION.name())) {
			return WorkflowLog.Type.TASK_COMPLETION;
		}
		else if (Objects.equals(type, LogType.TASK_UPDATE.name())) {
			return WorkflowLog.Type.TASK_UPDATE;
		}

		return null;
	}

	@Reference
	private KaleoLogLocalService _kaleoLogLocalService;

	@Reference
	private KaleoWorkflowModelConverter _kaleoWorkflowModelConverter;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference
	private UserLocalService _userLocalService;

	@Reference
	private WorkflowComparatorFactory _workflowComparatorFactory;

	@Reference
	private WorkflowLogManager _workflowLogManager;

}