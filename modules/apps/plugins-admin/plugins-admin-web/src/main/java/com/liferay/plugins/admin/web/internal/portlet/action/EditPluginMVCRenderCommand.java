/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.plugins.admin.web.internal.portlet.action;

import com.liferay.plugins.admin.web.internal.constants.PluginsAdminPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Peter Fellwock
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PluginsAdminPortletKeys.PLUGINS_ADMIN,
		"mvc.command.name=/plugins_admin/edit_plugin"
	},
	service = MVCRenderCommand.class
)
public class EditPluginMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		return "/edit_plugin.jsp";
	}

}