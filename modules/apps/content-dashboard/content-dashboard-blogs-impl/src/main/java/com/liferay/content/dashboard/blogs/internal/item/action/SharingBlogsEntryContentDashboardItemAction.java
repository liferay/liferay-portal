/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.content.dashboard.blogs.internal.item.action;

import com.liferay.blogs.model.BlogsEntry;
import com.liferay.content.dashboard.item.action.ContentDashboardItemAction;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

/**
 * @author Cristina González
 */
public class SharingBlogsEntryContentDashboardItemAction
	implements ContentDashboardItemAction {

	public SharingBlogsEntryContentDashboardItemAction(
		BlogsEntry blogsEntry, HttpServletRequest httpServletRequest,
		Language language, Portal portal) {

		_blogsEntry = blogsEntry;
		_httpServletRequest = httpServletRequest;
		_language = language;
		_portal = portal;
	}

	@Override
	public String getIcon() {
		return "share";
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "share");
	}

	@Override
	public String getName() {
		return "share";
	}

	@Override
	public Type getType() {
		return Type.SHARING_BUTTON;
	}

	@Override
	public String getURL() {
		try {
			PortletResponse portletResponse =
				(PortletResponse)_httpServletRequest.getAttribute(
					JavaConstants.JAKARTA_PORTLET_RESPONSE);

			return PortletURLBuilder.createRenderURL(
				_portal.getLiferayPortletResponse(portletResponse),
				"com_liferay_content_dashboard_web_portlet_" +
					"ContentDashboardAdminPortlet"
			).setMVCPath(
				"/sharing_button.jsp"
			).setParameter(
				"className", BlogsEntry.class.getName()
			).setParameter(
				"classPK", _blogsEntry.getEntryId()
			).setWindowState(
				LiferayWindowState.EXCLUSIVE
			).buildString();
		}
		catch (Exception exception) {
			_log.error(exception);

			return StringPool.BLANK;
		}
	}

	@Override
	public String getURL(Locale locale) {
		return getURL();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SharingBlogsEntryContentDashboardItemAction.class);

	private final BlogsEntry _blogsEntry;
	private final HttpServletRequest _httpServletRequest;
	private final Language _language;
	private final Portal _portal;

}