/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.change.tracking.spi.display;

import com.liferay.announcements.constants.AnnouncementsPortletKeys;
import com.liferay.announcements.kernel.model.AnnouncementsEntry;
import com.liferay.change.tracking.spi.display.BaseCTDisplayRenderer;
import com.liferay.change.tracking.spi.display.CTDisplayRenderer;
import com.liferay.change.tracking.spi.display.context.DisplayContext;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cheryl Tang
 */
@Component(service = CTDisplayRenderer.class)
public class AnnouncementsEntryCTDisplayRenderer
	extends BaseCTDisplayRenderer<AnnouncementsEntry> {

	@Override
	public String getEditURL(
		HttpServletRequest httpServletRequest,
		AnnouncementsEntry announcementsEntry) {

		return PortletURLBuilder.create(
			_portal.getControlPanelPortletURL(
				httpServletRequest,
				AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN,
				PortletRequest.RENDER_PHASE)
		).setMVCRenderCommandName(
			"/announcements/edit_entry"
		).setRedirect(
			_portal.getCurrentURL(httpServletRequest)
		).setBackURL(
			ParamUtil.getString(httpServletRequest, "backURL")
		).setParameter(
			"entryId", announcementsEntry.getEntryId()
		).buildString();
	}

	@Override
	public Class<AnnouncementsEntry> getModelClass() {
		return AnnouncementsEntry.class;
	}

	@Override
	public String getTitle(Locale locale, AnnouncementsEntry announcementsEntry)
		throws PortalException {

		return announcementsEntry.getTitle();
	}

	@Override
	public String renderPreview(
		DisplayContext<AnnouncementsEntry> displayContext) {

		AnnouncementsEntry announcementsEntry = displayContext.getModel();

		return announcementsEntry.getContent();
	}

	@Override
	protected void buildDisplay(
		DisplayBuilder<AnnouncementsEntry> displayBuilder) {

		AnnouncementsEntry announcementsEntry = displayBuilder.getModel();

		displayBuilder.display(
			"title", announcementsEntry.getTitle()
		).display(
			"created-by", announcementsEntry.getUserName()
		).display(
			"create-date", announcementsEntry.getCreateDate()
		).display(
			"display-date", announcementsEntry.getDisplayDate()
		).display(
			"expiration-date", announcementsEntry.getExpirationDate()
		).display(
			"alert", announcementsEntry.isAlert()
		).display(
			"priority", announcementsEntry.getPriority()
		).display(
			"type", announcementsEntry.getType()
		).display(
			"url", announcementsEntry.getUrl()
		).display(
			"content", announcementsEntry.getContent()
		);
	}

	@Reference
	private Portal _portal;

}