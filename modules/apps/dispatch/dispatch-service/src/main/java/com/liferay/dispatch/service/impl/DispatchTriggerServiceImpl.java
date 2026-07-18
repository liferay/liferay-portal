/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.service.impl;

import com.liferay.dispatch.constants.DispatchActionKeys;
import com.liferay.dispatch.constants.DispatchConstants;
import com.liferay.dispatch.executor.DispatchTaskClusterMode;
import com.liferay.dispatch.model.DispatchTrigger;
import com.liferay.dispatch.service.base.DispatchTriggerServiceBaseImpl;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.UnicodeProperties;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"json.web.service.context.name=dispatch",
		"json.web.service.context.path=DispatchTrigger"
	},
	service = AopService.class
)
public class DispatchTriggerServiceImpl extends DispatchTriggerServiceBaseImpl {

	@Override
	public DispatchTrigger addDispatchTrigger(
			String externalReferenceCode, long userId,
			String dispatchTaskExecutorType,
			UnicodeProperties dispatchTaskSettingsUnicodeProperties,
			String name)
		throws PortalException {

		_portletResourcePermission.check(
			getPermissionChecker(), GroupConstants.DEFAULT_LIVE_GROUP_ID,
			DispatchActionKeys.ADD_DISPATCH_TRIGGER);

		return dispatchTriggerLocalService.addDispatchTrigger(
			externalReferenceCode, userId, dispatchTaskExecutorType,
			dispatchTaskSettingsUnicodeProperties, name, false);
	}

	@Override
	public void deleteDispatchTrigger(long dispatchTriggerId)
		throws PortalException {

		_dispatchTriggerModelResourcePermission.check(
			getPermissionChecker(), dispatchTriggerId, ActionKeys.DELETE);

		dispatchTriggerLocalService.deleteDispatchTrigger(dispatchTriggerId);
	}

	@Override
	public DispatchTrigger getDispatchTrigger(long dispatchTriggerId)
		throws PortalException {

		_dispatchTriggerModelResourcePermission.check(
			getPermissionChecker(), dispatchTriggerId, ActionKeys.VIEW);

		return dispatchTriggerLocalService.getDispatchTrigger(
			dispatchTriggerId);
	}

	@Override
	public List<DispatchTrigger> getDispatchTriggers(int start, int end)
		throws PortalException {

		PermissionChecker permissionChecker = getPermissionChecker();

		if (permissionChecker.isCompanyAdmin()) {
			return dispatchTriggerPersistence.findByCompanyId(
				permissionChecker.getCompanyId(), start, end);
		}

		return dispatchTriggerPersistence.findByC_U(
			permissionChecker.getCompanyId(), permissionChecker.getUserId(),
			start, end);
	}

	@Override
	public int getDispatchTriggersCount() throws PortalException {
		PermissionChecker permissionChecker = getPermissionChecker();

		if (permissionChecker.isCompanyAdmin()) {
			return dispatchTriggerPersistence.countByCompanyId(
				permissionChecker.getCompanyId());
		}

		return dispatchTriggerPersistence.countByC_U(
			permissionChecker.getCompanyId(), permissionChecker.getUserId());
	}

	@Override
	public DispatchTrigger updateDispatchTrigger(
			long dispatchTriggerId, boolean active, String cronExpression,
			DispatchTaskClusterMode dispatchTaskClusterMode, int endDateMonth,
			int endDateDay, int endDateYear, int endDateHour, int endDateMinute,
			boolean neverEnd, boolean overlapAllowed, int startDateMonth,
			int startDateDay, int startDateYear, int startDateHour,
			int startDateMinute, String timeZoneId)
		throws PortalException {

		_dispatchTriggerModelResourcePermission.check(
			getPermissionChecker(), dispatchTriggerId, ActionKeys.UPDATE);

		return dispatchTriggerLocalService.updateDispatchTrigger(
			dispatchTriggerId, active, cronExpression, dispatchTaskClusterMode,
			endDateMonth, endDateDay, endDateYear, endDateHour, endDateMinute,
			neverEnd, overlapAllowed, startDateMonth, startDateDay,
			startDateYear, startDateHour, startDateMinute, timeZoneId);
	}

	@Override
	public DispatchTrigger updateDispatchTrigger(
			long dispatchTriggerId,
			UnicodeProperties dispatchTaskSettingsUnicodeProperties,
			String name)
		throws PortalException {

		_dispatchTriggerModelResourcePermission.check(
			getPermissionChecker(), dispatchTriggerId, ActionKeys.UPDATE);

		return dispatchTriggerLocalService.updateDispatchTrigger(
			dispatchTriggerId, dispatchTaskSettingsUnicodeProperties, name);
	}

	@Reference(
		target = "(model.class.name=com.liferay.dispatch.model.DispatchTrigger)"
	)
	private ModelResourcePermission<DispatchTrigger>
		_dispatchTriggerModelResourcePermission;

	@Reference(
		target = "(resource.name=" + DispatchConstants.RESOURCE_NAME + ")"
	)
	private PortletResourcePermission _portletResourcePermission;

}