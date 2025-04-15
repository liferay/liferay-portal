/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.portlet.configuration.icon;

import com.liferay.announcements.constants.AnnouncementsPortletKeys;
import com.liferay.announcements.kernel.model.AnnouncementsEntry;
import com.liferay.announcements.web.internal.portlet.action.ActionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.portlet.configuration.icon.BasePortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.configuration.icon.PortletConfigurationIcon;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.ActionKeys;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portlet.announcements.service.permission.AnnouncementsEntryPermission;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletResponse;
import jakarta.portlet.PortletURL;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"jakarta.portlet.name=" + AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN,
		"path=/announcements/view_entry"
	},
	service = PortletConfigurationIcon.class
)
public class DeleteEntryPortletConfigurationIcon
	extends BasePortletConfigurationIcon {

	@Override
	public String getMessage(PortletRequest portletRequest) {
		return _language.get(getLocale(portletRequest), "delete");
	}

	@Override
	public String getURL(
		PortletRequest portletRequest, PortletResponse portletResponse) {

		PortletURL portletURL = PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				portletRequest, AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN,
				PortletRequest.ACTION_PHASE)
		).setActionName(
			"/announcements/edit_entry"
		).setCMD(
			Constants.DELETE
		).buildPortletURL();

		PortletURL redirectURL = _portal.getControlPanelPortletURL(
			portletRequest, AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN,
			PortletRequest.RENDER_PHASE);

		portletURL.setParameter("redirect", redirectURL.toString());

		AnnouncementsEntry entry = null;

		try {
			entry = ActionUtil.getEntry(portletRequest);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}

		portletURL.setParameter("entryId", String.valueOf(entry.getEntryId()));

		return portletURL.toString();
	}

	@Override
	public double getWeight() {
		return 100;
	}

	@Override
	public boolean isShow(PortletRequest portletRequest) {
		try {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)portletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			return AnnouncementsEntryPermission.contains(
				themeDisplay.getPermissionChecker(),
				ActionUtil.getEntry(portletRequest), ActionKeys.DELETE);
		}
		catch (PortalException portalException) {
			throw new RuntimeException(portalException);
		}
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}