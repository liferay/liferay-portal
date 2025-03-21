/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.events;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.service.CTPreferencesLocalService;
import com.liferay.change.tracking.web.internal.configuration.helper.CTSettingsConfigurationHelper;
import com.liferay.change.tracking.web.internal.helper.SandboxHelper;
import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactory;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = "key=servlet.service.events.pre", service = LifecycleAction.class
)
public class SandboxServicePreAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		try {
			User user = _portal.getUser(httpServletRequest);

			if ((user == null) ||
				!_ctSettingsConfigurationHelper.isEnabled(
					user.getCompanyId()) ||
				!_ctSettingsConfigurationHelper.isSandboxEnabled(
					user.getCompanyId())) {

				return;
			}

			PermissionChecker permissionChecker =
				PermissionThreadLocal.getPermissionChecker();

			if (permissionChecker == null) {
				permissionChecker = _permissionCheckerFactory.create(user);

				PermissionThreadLocal.setPermissionChecker(permissionChecker);
			}

			if (!PortletPermissionUtil.contains(
					permissionChecker, CTPortletKeys.PUBLICATIONS,
					ActionKeys.ACCESS_IN_CONTROL_PANEL) ||
				!PortletPermissionUtil.contains(
					permissionChecker, CTPortletKeys.PUBLICATIONS,
					ActionKeys.VIEW)) {

				return;
			}

			_sandboxHelper.sandbox(
				_ctPreferencesLocalService.getCTPreferences(
					user.getCompanyId(), user.getUserId()));
		}
		catch (Exception exception) {
			throw new ActionException(exception);
		}
	}

	@Reference
	private CTPreferencesLocalService _ctPreferencesLocalService;

	@Reference
	private CTSettingsConfigurationHelper _ctSettingsConfigurationHelper;

	@Reference
	private PermissionCheckerFactory _permissionCheckerFactory;

	@Reference
	private Portal _portal;

	@Reference
	private SandboxHelper _sandboxHelper;

}