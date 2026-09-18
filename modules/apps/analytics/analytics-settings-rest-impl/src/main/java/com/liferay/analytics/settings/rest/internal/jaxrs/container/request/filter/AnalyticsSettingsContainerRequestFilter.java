/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.internal.jaxrs.container.request.filter;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.RoleLocalService;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Rachael Koestartyo
 */
@Component(
	property = {
		"osgi.jaxrs.application.select=(osgi.jaxrs.name=Liferay.Analytyics.Settings.REST)",
		"osgi.jaxrs.extension=true",
		"osgi.jaxrs.name=Liferay.Analytics.Settings.REST.Container.Request.Filter"
	},
	scope = ServiceScope.PROTOTYPE, service = ContainerRequestFilter.class
)
public class AnalyticsSettingsContainerRequestFilter
	implements ContainerRequestFilter {

	@Override
	public void filter(ContainerRequestContext containerRequestContext) {
		if (_hasPermission()) {
			return;
		}

		containerRequestContext.abortWith(
			Response.status(
				Response.Status.FORBIDDEN
			).entity(
				"You do not have the required permissions"
			).type(
				MediaType.APPLICATION_JSON
			).build());
	}

	private boolean _hasPermission() {
		PermissionChecker permissionChecker =
			PermissionThreadLocal.getPermissionChecker();

		if (permissionChecker == null) {
			return false;
		}

		long companyId = permissionChecker.getCompanyId();

		if (permissionChecker.isCompanyAdmin(companyId)) {
			return true;
		}

		try {
			return _roleLocalService.hasUserRole(
				permissionChecker.getUserId(), companyId,
				RoleConstants.ANALYTICS_ADMINISTRATOR, true);
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		AnalyticsSettingsContainerRequestFilter.class);

	@Reference
	private RoleLocalService _roleLocalService;

}