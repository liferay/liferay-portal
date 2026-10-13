/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.launch.web.internal.portlet.action;

import com.liferay.item.selector.ItemSelector;
import com.liferay.launch.entry.type.registry.LaunchEntryTypeRegistry;
import com.liferay.launch.web.internal.constants.LaunchPortletKeys;
import com.liferay.launch.web.internal.constants.LaunchWebKeys;
import com.liferay.launch.web.internal.display.context.ViewLaunchDisplayContext;
import com.liferay.object.service.ObjectEntryLocalService;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + LaunchPortletKeys.LAUNCH,
		"mvc.command.name=/launch/view_launch"
	},
	service = MVCRenderCommand.class
)
public class ViewLaunchMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		HttpServletRequest httpServletRequest = _portal.getHttpServletRequest(
			renderRequest);

		ViewLaunchDisplayContext viewLaunchDisplayContext =
			new ViewLaunchDisplayContext(
				httpServletRequest, _itemSelector, _launchEntryTypeRegistry,
				_portal.getLiferayPortletResponse(renderResponse),
				_objectEntryLocalService);

		renderRequest.setAttribute(
			LaunchWebKeys.VIEW_LAUNCH_DISPLAY_CONTEXT,
			viewLaunchDisplayContext);

		return "/view_launch.jsp";
	}

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private LaunchEntryTypeRegistry _launchEntryTypeRegistry;

	@Reference
	private ObjectEntryLocalService _objectEntryLocalService;

	@Reference
	private Portal _portal;

}