/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.defaultpermissions.web.internal.portlet.action;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.defaultpermissions.web.internal.constants.PortalDefaultPermissionsWebKeys;
import com.liferay.portal.kernel.defaultpermissions.configuration.manager.PortalDefaultPermissionsConfigurationManager;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.constants.MVCRenderConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.roles.admin.constants.RolesAdminWebKeys;
import com.liferay.roles.admin.role.type.contributor.provider.RoleTypeContributorProvider;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = {
		"jakarta.portlet.name=com_liferay_portlet_configuration_web_portlet_PortletConfigurationPortlet",
		"mvc.command.name=/configuration/edit_portal_default_permissions_configuration"
	},
	service = MVCRenderCommand.class
)
public class EditPortalDefaultPermissionsConfigurationMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(
				"/configuration" +
					"/edit_portal_default_permissions_configuration.jsp");

		try {
			HttpServletRequest httpServletRequest =
				_portal.getHttpServletRequest(renderRequest);
			HttpServletResponse httpServletResponse =
				_portal.getHttpServletResponse(renderResponse);

			String scope = httpServletRequest.getParameter("scope");

			if (scope.equals(
					ExtendedObjectClassDefinition.Scope.GROUP.toString())) {

				renderRequest.setAttribute(
					PortalDefaultPermissionsWebKeys.
						PORTAL_DEFAULT_PERMISSIONS_CONFIGURATION_MANAGER,
					_groupPortalDefaultPermissionsConfigurationManager);
			}
			else {
				renderRequest.setAttribute(
					PortalDefaultPermissionsWebKeys.
						PORTAL_DEFAULT_PERMISSIONS_CONFIGURATION_MANAGER,
					_companyPortalDefaultPermissionsConfigurationManager);
			}

			renderRequest.setAttribute(
				RolesAdminWebKeys.ROLE_TYPE_CONTRIBUTOR_PROVIDER,
				_roleTypeContributorProvider);

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new PortletException(exception);
		}

		return MVCRenderConstants.MVC_PATH_VALUE_SKIP_DISPATCH;
	}

	@Reference(target = "(portal.default.permissions.scope=company)")
	private PortalDefaultPermissionsConfigurationManager
		_companyPortalDefaultPermissionsConfigurationManager;

	@Reference(target = "(portal.default.permissions.scope=group)")
	private PortalDefaultPermissionsConfigurationManager
		_groupPortalDefaultPermissionsConfigurationManager;

	@Reference
	private Portal _portal;

	@Reference
	private RoleTypeContributorProvider _roleTypeContributorProvider;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.defaultpermissions.web)"
	)
	private ServletContext _servletContext;

}