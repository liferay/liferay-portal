/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.server.admin.web.internal.portlet.action;

import com.liferay.friendly.url.checker.FriendlyURLPublicMappingChecker;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.server.admin.web.internal.constants.ServerAdminWebKeys;
import com.liferay.server.admin.web.internal.display.context.ServerDisplayContext;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Philip Jones
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + PortletKeys.SERVER_ADMIN,
		"mvc.command.name=/", "mvc.command.name=/server_admin/view"
	},
	service = MVCRenderCommand.class
)
public class ViewMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		renderRequest.setAttribute(
			ServerAdminWebKeys.SERVER_DISPLAY_CONTEXT,
			new ServerDisplayContext(
				_friendlyURLPublicMappingChecker, renderRequest,
				renderResponse));

		return "/view.jsp";
	}

	@Reference
	private FriendlyURLPublicMappingChecker _friendlyURLPublicMappingChecker;

}