/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet.action;

import com.liferay.layout.admin.constants.LayoutAdminPortletKeys;
import com.liferay.layout.admin.web.internal.display.context.SelectLayoutPageTemplateEntryDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LayoutAdminPortletKeys.GROUP_PAGES,
		"mvc.command.name=/layout_admin/select_layout_utility_page_entry_master_layout"
	},
	service = MVCRenderCommand.class
)
public class SelectLayoutUtilityPageEntryMasterLayoutMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		renderRequest.setAttribute(
			SelectLayoutPageTemplateEntryDisplayContext.class.getName(),
			new SelectLayoutPageTemplateEntryDisplayContext(
				_portal.getHttpServletRequest(renderRequest),
				_portal.getLiferayPortletResponse(renderResponse)));

		return "/select_layout_utility_page_entry_master_layout.jsp";
	}

	@Reference
	private Portal _portal;

}