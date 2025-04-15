/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.builder.web.internal.portlet.action;

import com.liferay.headless.builder.web.internal.constants.HeadlessBuilderPortletKeys;
import com.liferay.headless.builder.web.internal.display.context.HeadlessBuilderWebDisplayContext;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Montenegro
 */
@Component(
	property = {
		"jakarta.portlet.name=" + HeadlessBuilderPortletKeys.HEADLESS_BUILDER,
		"mvc.command.name=/headless_builder/edit_api_application"
	},
	service = MVCRenderCommand.class
)
public class EditAPIApplicationMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT,
			new HeadlessBuilderWebDisplayContext(
				_portal.getHttpServletRequest(renderRequest)));

		return "/headless_builder/edit_api_application.jsp";
	}

	@Reference
	private Portal _portal;

}