/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dispatch.web.internal.portlet.action;

import com.liferay.dispatch.constants.DispatchPortletKeys;
import com.liferay.dispatch.scheduler.SchedulerResponseManager;
import com.liferay.dispatch.web.internal.display.context.SchedulerResponseDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Matija Petanjek
 */
@Component(
	property = {
		"jakarta.portlet.name=" + DispatchPortletKeys.DISPATCH,
		"mvc.command.name=/dispatch/edit_scheduler_response"
	},
	service = MVCRenderCommand.class
)
public class EditSchedulerResponseMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		SchedulerResponseDisplayContext schedulerResponseDisplayContext =
			new SchedulerResponseDisplayContext(
				renderRequest, _schedulerResponseManager);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, schedulerResponseDisplayContext);

		return "/view_scheduler_response.jsp";
	}

	@Reference
	private SchedulerResponseManager _schedulerResponseManager;

}