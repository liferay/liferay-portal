/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.service.impl;

import com.liferay.dispatch.model.DispatchLog;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.base.DispatchLogServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.util.OrderByComparator;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=dispatch",
		"json.web.service.context.path=DispatchLog"
	},
	service = AopService.class
)
public class DispatchLogServiceImpl extends DispatchLogServiceBaseImpl {

	@Override
	public void deleteDispatchLog(long dispatchLogId) throws PortalException {
		DispatchLog dispatchLog = dispatchLogPersistence.findByPrimaryKey(
			dispatchLogId);

		_dispatchTriggerModelResourcePermission.check(
			getPermissionChecker(), dispatchLog.getDispatchTriggerId(),
			ActionKeys.UPDATE);

		dispatchLogLocalService.deleteDispatchLog(dispatchLogId);
	}

	@Override
	public DispatchLog getDispatchLog(long dispatchLogId)
		throws PortalException {

		DispatchLog dispatchLog = dispatchLogPersistence.findByPrimaryKey(
			dispatchLogId);

		_dispatchTriggerModelResourcePermission.check(
			getPermissionChecker(), dispatchLog.getDispatchTriggerId(),
			ActionKeys.VIEW);

		return dispatchLog;
	}

	@Override
	public List<DispatchLog> getDispatchLogs(
			long dispatchTriggerId, int start, int end)
		throws PortalException {

		_dispatchTriggerModelResourcePermission.check(
			getPermissionChecker(), dispatchTriggerId, ActionKeys.VIEW);

		return dispatchLogPersistence.findByDispatchTriggerId(
			dispatchTriggerId, start, end);
	}

	@Override
	public List<DispatchLog> getDispatchLogs(
			long dispatchTriggerId, int start, int end,
			OrderByComparator<DispatchLog> orderByComparator)
		throws PortalException {

		_dispatchTriggerModelResourcePermission.check(
			getPermissionChecker(), dispatchTriggerId, ActionKeys.VIEW);

		return dispatchLogPersistence.findByDispatchTriggerId(
			dispatchTriggerId, start, end, orderByComparator);
	}

	@Override
	public int getDispatchLogsCount(long dispatchTriggerId)
		throws PortalException {

		_dispatchTriggerModelResourcePermission.check(
			getPermissionChecker(), dispatchTriggerId, ActionKeys.VIEW);

		return dispatchLogPersistence.countByDispatchTriggerId(
			dispatchTriggerId);
	}

	@Reference(
		target = "(model.class.name=com.liferay.dispatch.model.DispatchTrigger)"
	)
	private ModelResourcePermission<DispatchTrigger>
		_dispatchTriggerModelResourcePermission;

}