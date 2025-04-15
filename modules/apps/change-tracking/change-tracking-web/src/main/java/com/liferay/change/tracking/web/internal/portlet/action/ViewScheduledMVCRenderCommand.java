/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.web.internal.portlet.action;

import com.liferay.change.tracking.constants.CTPortletKeys;
import com.liferay.change.tracking.web.internal.constants.CTWebKeys;
import com.liferay.change.tracking.web.internal.display.context.ViewScheduledDisplayContext;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"jakarta.portlet.name=" + CTPortletKeys.PUBLICATIONS,
		"mvc.command.name=/change_tracking/view_scheduled"
	},
	service = MVCRenderCommand.class
)
public class ViewScheduledMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		ViewScheduledDisplayContext viewScheduledDisplayContext =
			new ViewScheduledDisplayContext(
				_portal.getHttpServletRequest(renderRequest), _language,
				renderRequest, renderResponse);

		renderRequest.setAttribute(
			CTWebKeys.VIEW_SCHEDULED_DISPLAY_CONTEXT,
			viewScheduledDisplayContext);

		return "/publications/view_scheduled.jsp";
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}