/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.expando.web.internal.portlet.action;

import com.liferay.expando.constants.ExpandoPortletKeys;
import com.liferay.expando.web.internal.display.context.ExpandoDisplayContext;
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
		"jakarta.portlet.name=" + ExpandoPortletKeys.EXPANDO,
		"mvc.command.name=/expando/view_attributes"
	},
	service = MVCRenderCommand.class
)
public class ViewAttributesMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		renderRequest.setAttribute(
			ExpandoDisplayContext.class.getName(),
			new ExpandoDisplayContext(
				_portal.getHttpServletRequest(renderRequest), renderRequest,
				renderResponse));

		return "/view_attributes.jsp";
	}

	@Reference
	private Portal _portal;

}