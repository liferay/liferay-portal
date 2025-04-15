/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.web.internal.struts;

import com.liferay.message.boards.constants.MBPortletKeys;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.struts.StrutsAction;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletMode;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Brian Wing Shun Chan
 */
@Component(
	property = {
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS,
		"jakarta.portlet.name=" + MBPortletKeys.MESSAGE_BOARDS_ADMIN,
		"path=/message_boards/find_recent_posts"
	},
	service = StrutsAction.class
)
public class FindRecentPostsStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		try {
			long plid = ParamUtil.getLong(httpServletRequest, "p_l_id");

			httpServletResponse.sendRedirect(
				PortletURLBuilder.create(
					PortletURLFactoryUtil.create(
						httpServletRequest, MBPortletKeys.MESSAGE_BOARDS, plid,
						PortletRequest.RENDER_PHASE)
				).setMVCRenderCommandName(
					"/message_boards/view_recent_posts"
				).setPortletMode(
					PortletMode.VIEW
				).setWindowState(
					WindowState.NORMAL
				).buildString());

			return null;
		}
		catch (Exception exception) {
			_portal.sendError(
				exception, httpServletRequest, httpServletResponse);

			return null;
		}
	}

	@Reference
	private Portal _portal;

}