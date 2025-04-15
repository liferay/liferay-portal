/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.portlet.action;

import com.liferay.dispatch.constants.DispatchPortletKeys;
import com.liferay.dispatch.service.DispatchLogService;
import com.liferay.dispatch.web.internal.display.context.DispatchLogDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author guywandji
 * @author Alessio Antonio Rendina
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DispatchPortletKeys.DISPATCH,
		"mvc.command.name=/dispatch/view_dispatch_log"
	},
	service = MVCRenderCommand.class
)
public class ViewDispatchLogMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		DispatchLogDisplayContext dispatchLogDisplayContext =
			new DispatchLogDisplayContext(_dispatchLogService, renderRequest);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, dispatchLogDisplayContext);

		return "/view_dispatch_log.jsp";
	}

	@Reference
	private DispatchLogService _dispatchLogService;

}