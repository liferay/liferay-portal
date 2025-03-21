/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.portlet.action;

import com.liferay.announcements.constants.AnnouncementsPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.util.Objects;

import org.osgi.service.component.annotations.Component;

/**
 * @author Thiago Moreira
 * @author Raymond Augé
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AnnouncementsPortletKeys.ALERTS,
		"jakarta.portlet.name=" + AnnouncementsPortletKeys.ANNOUNCEMENTS,
		"jakarta.portlet.name=" + AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN,
		"mvc.command.name=/", "mvc.command.name=/alerts/view",
		"mvc.command.name=/announcements/view"
	},
	service = MVCRenderCommand.class
)
public class ViewMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		if (Objects.equals(
				_getPortletId(renderRequest),
				AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN)) {

			return "/announcements_admin/view.jsp";
		}

		return "/announcements/view.jsp";
	}

	private String _getPortletId(RenderRequest renderRequest) {
		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletDisplay portletDisplay = themeDisplay.getPortletDisplay();

		return portletDisplay.getPortletName();
	}

}