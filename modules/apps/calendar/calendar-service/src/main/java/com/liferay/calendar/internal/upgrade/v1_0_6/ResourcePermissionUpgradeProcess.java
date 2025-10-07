/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.calendar.internal.upgrade.v1_0_6;

import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeException;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.security.permission.ResourceActionsImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

/**
 * @author José María Muñoz
 * @author Alberto Chaparro
 */
public class ResourcePermissionUpgradeProcess extends UpgradeProcess {

	public ResourcePermissionUpgradeProcess(
		ResourceActionLocalService resourceActionLocalService,
		ResourcePermissionLocalService resourcePermissionLocalService,
		RoleLocalService roleLocalService) {

		_resourceActionLocalService = resourceActionLocalService;
		_resourcePermissionLocalService = resourcePermissionLocalService;
		_roleLocalService = roleLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		_upgradeGuestResourceBlockPermissions();
	}

	private List<String> _getCalendarResourceUnsupportedActionIds()
		throws Exception {

		List<String> actionIds = new ArrayList<>();

		List<String> guestUnsupportedActions =
			_getModelResourceGuestUnsupportedActions();

		for (String resourceActionId : _NEW_UNSUPPORTED_ACTION_IDS) {
			if (guestUnsupportedActions.contains(resourceActionId)) {
				ResourceAction resourceAction =
					_resourceActionLocalService.getResourceAction(
						_CALENDAR_RESOURCE_NAME, resourceActionId);

				actionIds.add(resourceAction.getActionId());
			}
		}

		return actionIds;
	}

	private List<String> _getModelResourceGuestUnsupportedActions()
		throws Exception {

		try {
			ResourceActionsImpl resourceActionsImpl = new ResourceActionsImpl();

			Class<?> clazz = getClass();

			ClassLoader classLoader = clazz.getClassLoader();

			for (String config : PropsValues.RESOURCE_ACTIONS_CONFIGS) {
				resourceActionsImpl.readModelResources(classLoader, config);
			}

			return resourceActionsImpl.getModelResourceGuestUnsupportedActions(
				_CALENDAR_RESOURCE_NAME);
		}
		catch (Exception exception) {
			throw new UpgradeException(exception);
		}
	}

	private void _upgradeGuestResourceBlockPermissions() throws Exception {
		List<String> unsupportedActionIds =
			_getCalendarResourceUnsupportedActionIds();

		if (unsupportedActionIds.isEmpty()) {
			return;
		}

		try (PreparedStatement preparedStatement = connection.prepareStatement(
				"select calendarResourceId, companyId from CalendarResource");
			ResultSet resultSet = preparedStatement.executeQuery()) {

			while (resultSet.next()) {
				long calendarResourceId = resultSet.getLong(1);

				long companyId = resultSet.getLong(2);

				Role guestRole = _roleLocalService.getRole(
					companyId, RoleConstants.GUEST);

				for (String unsupportedActionId : unsupportedActionIds) {
					_resourcePermissionLocalService.removeResourcePermission(
						companyId, _CALENDAR_RESOURCE_NAME,
						ResourceConstants.SCOPE_INDIVIDUAL,
						String.valueOf(calendarResourceId),
						guestRole.getRoleId(), unsupportedActionId);
				}
			}
		}
	}

	private static final String _CALENDAR_RESOURCE_NAME =
		"com.liferay.calendar.model.CalendarResource";

	private static final String[] _NEW_UNSUPPORTED_ACTION_IDS = {
		ActionKeys.PERMISSIONS, ActionKeys.VIEW
	};

	private final ResourceActionLocalService _resourceActionLocalService;
	private final ResourcePermissionLocalService
		_resourcePermissionLocalService;
	private final RoleLocalService _roleLocalService;

}