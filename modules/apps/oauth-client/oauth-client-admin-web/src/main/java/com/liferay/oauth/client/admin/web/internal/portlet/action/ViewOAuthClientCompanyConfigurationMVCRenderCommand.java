/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.portlet.action;

import com.liferay.oauth.client.constants.OAuthClientAdminPortletKeys;
import com.liferay.oauth.client.persistence.configuration.OAuthClientCompanyConfiguration;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alvaro Saugar
 */
@Component(
	property = {
		"jakarta.portlet.name=" + OAuthClientAdminPortletKeys.OAUTH_CLIENT_ADMIN,
		"mvc.command.name=/oauth_client_admin/view_oauth_client_company_configuration"
	},
	service = MVCRenderCommand.class
)
public class ViewOAuthClientCompanyConfigurationMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)renderRequest.getAttribute(WebKeys.THEME_DISPLAY);

			PermissionChecker permissionChecker =
				themeDisplay.getPermissionChecker();

			if (!permissionChecker.isCompanyAdmin(
					themeDisplay.getCompanyId())) {

				throw new PrincipalException();
			}

			renderRequest.setAttribute(
				OAuthClientCompanyConfiguration.class.getName(),
				_configurationProvider.getCompanyConfiguration(
					OAuthClientCompanyConfiguration.class,
					themeDisplay.getCompanyId()));
		}
		catch (PrincipalException principalException) {
			SessionErrors.add(renderRequest, principalException.getClass());

			return "/admin/error.jsp";
		}
		catch (PortalException portalException) {
			throw new PortletException(portalException);
		}

		return "/admin/view.jsp";
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

}