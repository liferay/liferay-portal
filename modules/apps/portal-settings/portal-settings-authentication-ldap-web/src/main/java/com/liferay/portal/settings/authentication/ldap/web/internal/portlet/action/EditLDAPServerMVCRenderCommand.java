/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.settings.authentication.ldap.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

import org.osgi.service.component.annotations.Component;

/**
 * @author Philip Jones
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"mvc.command.name=/portal_settings_authentication_ldap/edit_ldap_server"
	},
	service = MVCRenderCommand.class
)
public class EditLDAPServerMVCRenderCommand
	extends BasePortalSettingsMVCRenderCommand {

	@Override
	protected String getJspPath() {
		return _JSP_PATH;
	}

	private static final String _JSP_PATH =
		"/com.liferay.portal.settings.web/edit_ldap_server.jsp";

}