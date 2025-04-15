/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.uad.display;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalFeed;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.user.associated.data.display.UADDisplay;

import jakarta.portlet.PortletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Balázs Sáfrány-Kovalik
 */
@Component(service = UADDisplay.class)
public class JournalFeedUADDisplay extends BaseJournalFeedUADDisplay {

	@Override
	public String getEditURL(
			JournalFeed journalFeed,
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws Exception {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				liferayPortletRequest, JournalPortletKeys.JOURNAL,
				PortletRequest.RENDER_PHASE)
		).setMVCPath(
			"/edit_feed.jsp"
		).setRedirect(
			_portal.getCurrentURL(liferayPortletRequest)
		).setParameter(
			"feedId", journalFeed.getFeedId()
		).setParameter(
			"groupId", journalFeed.getGroupId()
		).buildString();
	}

	@Reference
	private Portal _portal;

}