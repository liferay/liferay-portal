/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.style.book.web.internal.portlet.action;

import com.liferay.client.extension.type.manager.CETManager;
import com.liferay.frontend.token.definition.FrontendTokenDefinitionRegistry;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.style.book.constants.StyleBookPortletKeys;
import com.liferay.style.book.web.internal.display.context.StyleBookDisplayContext;
import com.liferay.style.book.web.internal.display.context.StyleBookManagementToolbarDisplayContext;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"jakarta.portlet.name=" + StyleBookPortletKeys.STYLE_BOOK,
		"mvc.command.name=/", "mvc.command.name=/style_book/view"
	},
	service = MVCRenderCommand.class
)
public class ViewMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);
		LiferayPortletRequest liferayPortletRequest =
			_portal.getLiferayPortletRequest(renderRequest);
		LiferayPortletResponse liferayPortletResponse =
			_portal.getLiferayPortletResponse(renderResponse);

		StyleBookDisplayContext styleBookDisplayContext =
			new StyleBookDisplayContext(
				_frontendTokenDefinitionRegistry, httpServletRequest,
				liferayPortletRequest, liferayPortletResponse);

		renderRequest.setAttribute(
			StyleBookDisplayContext.class.getName(), styleBookDisplayContext);

		StyleBookManagementToolbarDisplayContext
			styleBookManagementToolbarDisplayContext =
				new StyleBookManagementToolbarDisplayContext(
					_cetManager, _frontendTokenDefinitionRegistry,
					httpServletRequest, liferayPortletRequest,
					liferayPortletResponse,
					styleBookDisplayContext.
						getStyleBookEntriesSearchContainer());

		renderRequest.setAttribute(
			StyleBookManagementToolbarDisplayContext.class.getName(),
			styleBookManagementToolbarDisplayContext);

		return "/view.jsp";
	}

	@Reference
	private CETManager _cetManager;

	@Reference
	private FrontendTokenDefinitionRegistry _frontendTokenDefinitionRegistry;

	@Reference
	private Portal _portal;

}