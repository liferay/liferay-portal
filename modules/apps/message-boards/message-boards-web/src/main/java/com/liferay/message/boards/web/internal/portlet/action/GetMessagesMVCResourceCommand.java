/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.portlet.action;

import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletRequestDispatcher;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Sergio González
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN,
		"mvc.command.name=/message_boards/get_messages"
	},
	service = MVCResourceCommand.class
)
public class GetMessagesMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		resourceRequest.setAttribute(
			WebKeys.MESSAGE_BOARDS_MESSAGE_DISPLAY,
			ActionUtil.getMessageDisplay(resourceRequest));

		int index = ParamUtil.getInteger(resourceRequest, "index");

		resourceRequest.setAttribute(WebKeys.MESSAGE_BOARDS_TREE_INDEX, index);

		PortletRequestDispatcher portletRequestDispatcher =
			getPortletRequestDispatcher(
				resourceRequest,
				"/message_boards/view_message_content_resources.jsp");

		portletRequestDispatcher.include(resourceRequest, resourceResponse);
	}

}