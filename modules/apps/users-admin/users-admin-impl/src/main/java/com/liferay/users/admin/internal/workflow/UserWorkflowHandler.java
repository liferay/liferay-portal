/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.internal.workflow;

import com.liferay.portal.kernel.audit.AuditRequestThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.workflow.BaseWorkflowHandler;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.kernel.workflow.WorkflowHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.Serializable;

import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(
	property = "model.class.name=com.liferay.portal.kernel.model.User",
	service = WorkflowHandler.class
)
public class UserWorkflowHandler extends BaseWorkflowHandler<User> {

	@Override
	public void contributeWorkflowContext(
		Map<String, Serializable> workflowContext) {

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			WorkflowConstants.CONTEXT_SERVICE_CONTEXT);

		HttpServletRequest httpServletRequest = serviceContext.getRequest();

		if (httpServletRequest == null) {
			return;
		}

		serviceContext.setAttribute(
			"serverName", httpServletRequest.getServerName());
		serviceContext.setAttribute(
			"serverPort", httpServletRequest.getServerPort());

		HttpSession httpSession = httpServletRequest.getSession();

		serviceContext.setAttribute("sessionId", httpSession.getId());

		serviceContext.setRequest(httpServletRequest);
	}

	@Override
	public String getClassName() {
		return User.class.getName();
	}

	@Override
	public String getType(Locale locale) {
		return ResourceActionsUtil.getModelResource(locale, getClassName());
	}

	@Override
	public boolean isScopeable() {
		return false;
	}

	@Override
	public User updateStatus(
			int status, Map<String, Serializable> workflowContext)
		throws PortalException {

		long userId = GetterUtil.getLong(
			(String)workflowContext.get(
				WorkflowConstants.CONTEXT_ENTRY_CLASS_PK));

		User user = _userLocalService.getUser(userId);

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			WorkflowConstants.CONTEXT_SERVICE_CONTEXT);

		if (((user.getStatus() == WorkflowConstants.STATUS_DRAFT) ||
			 (user.getStatus() == WorkflowConstants.STATUS_PENDING)) &&
			(status == WorkflowConstants.STATUS_APPROVED)) {

			_userLocalService.completeUserRegistration(user, serviceContext);

			_updateAuditRequestThreadLocal(workflowContext);
		}

		return _userLocalService.updateStatus(user, status, serviceContext);
	}

	private void _updateAuditRequestThreadLocal(
		Map<String, Serializable> workflowContext) {

		AuditRequestThreadLocal auditRequestThreadLocal =
			AuditRequestThreadLocal.getAuditThreadLocal();

		ServiceContext serviceContext = (ServiceContext)workflowContext.get(
			WorkflowConstants.CONTEXT_SERVICE_CONTEXT);

		auditRequestThreadLocal.setClientHost(serviceContext.getRemoteHost());
		auditRequestThreadLocal.setClientIP(serviceContext.getRemoteAddr());

		long userId = GetterUtil.getLong(
			(String)workflowContext.get(WorkflowConstants.CONTEXT_USER_ID));

		if (userId != 0) {
			auditRequestThreadLocal.setRealUserId(userId);
		}

		Serializable serverName = serviceContext.getAttribute("serverName");

		if (serverName == null) {
			return;
		}

		auditRequestThreadLocal.setServerName((String)serverName);
		auditRequestThreadLocal.setServerPort(
			(int)serviceContext.getAttribute("serverPort"));

		Serializable sessionId = serviceContext.getAttribute("sessionId");

		if (sessionId == null) {
			return;
		}

		auditRequestThreadLocal.setSessionID((String)sessionId);
	}

	@Reference
	private UserLocalService _userLocalService;

}