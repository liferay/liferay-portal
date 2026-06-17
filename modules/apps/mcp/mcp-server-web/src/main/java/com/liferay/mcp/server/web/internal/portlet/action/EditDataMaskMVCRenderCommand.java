/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.web.internal.portlet.action;

import com.liferay.mcp.server.web.internal.constants.MCPServerWebPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Jorge González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MCPServerWebPortletKeys.MCP_SERVER_WEB,
		"mvc.command.name=/mcp_server/edit_data_mask"
	},
	service = MVCRenderCommand.class
)
public class EditDataMaskMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		return "/edit_data_mask.jsp";
	}

}