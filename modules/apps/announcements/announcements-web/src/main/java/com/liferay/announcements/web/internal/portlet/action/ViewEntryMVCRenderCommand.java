/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.portlet.action;

import com.liferay.announcements.constants.AnnouncementsPortletKeys;
import com.liferay.announcements.kernel.model.AnnouncementsEntry;
import com.liferay.announcements.kernel.model.AnnouncementsFlagConstants;
import com.liferay.announcements.web.internal.constants.AnnouncementsWebKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Adolfo Pérez
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AnnouncementsPortletKeys.ALERTS,
		"jakarta.portlet.name=" + AnnouncementsPortletKeys.ANNOUNCEMENTS,
		"jakarta.portlet.name=" + AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN,
		"mvc.command.name=/announcements/view_entry"
	},
	service = MVCRenderCommand.class
)
public class ViewEntryMVCRenderCommand implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		try {
			AnnouncementsEntry entry = ActionUtil.getEntry(renderRequest);

			renderRequest.setAttribute(
				AnnouncementsWebKeys.ANNOUNCEMENTS_ENTRY, entry);

			renderRequest.setAttribute(
				AnnouncementsWebKeys.VIEW_ENTRY_FLAG_VALUE,
				AnnouncementsFlagConstants.NOT_HIDDEN);
		}
		catch (PortalException portalException) {

			// LPS-52675

			if (_log.isDebugEnabled()) {
				_log.debug(portalException);
			}

			return "/announcements/error.jsp";
		}

		return "/announcements/view_entry.jsp";
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ViewEntryMVCRenderCommand.class);

}