/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saved.content.web.internal.portlet.action;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.saved.content.constants.MySavedContentPortletKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alicia García
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MySavedContentPortletKeys.MY_SAVED_CONTENT,
		"mvc.command.name=/",
		"mvc.command.name=/saved_content/view_my_saved_content"
	},
	service = MVCRenderCommand.class
)
public class ViewMySavedContentMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		return "/my_saved_content/view.jsp";
	}

}