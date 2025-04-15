/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.uad.display;

import com.liferay.announcements.kernel.model.AnnouncementsEntry;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.user.associated.data.display.UADDisplay;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Noah Sherrill
 */
@Component(service = UADDisplay.class)
public class AnnouncementsEntryUADDisplay
	extends BaseAnnouncementsEntryUADDisplay {

	@Override
	public String getEditURL(
			AnnouncementsEntry announcementsEntry,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		return PortletURLBuilder.createLiferayPortletURL(
			liferayPortletResponse,
			portal.getControlPanelPlid(liferayPortletRequest),
			PortletProviderUtil.getPortletId(
				AnnouncementsEntry.class.getName(),
				PortletProvider.Action.VIEW),
			PortletRequest.RENDER_PHASE
		).setMVCRenderCommandName(
			"/announcements/edit_entry"
		).setRedirect(
			portal.getCurrentURL(liferayPortletRequest)
		).setParameter(
			"entryId", announcementsEntry.getEntryId()
		).buildString();
	}

	@Reference
	protected Portal portal;

}