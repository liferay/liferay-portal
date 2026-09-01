/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.announcements.web.internal.application.list;

import com.liferay.announcements.constants.AnnouncementsPortletKeys;
import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Roberto Díaz
 */
@Component(
	property = {
		"panel.app.order:Integer=1100",
		"panel.category.key=" + PanelCategoryKeys.APPLICATIONS_MENU_APPLICATIONS
	},
	service = PanelApp.class
)
public class AnnouncementsPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "megaphone";
	}

	@Override
	public List<PanelAppNavigationItem> getPanelAppNavigationItems(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return List.of(
			new PanelAppNavigationItem(
				_language.get(LocaleUtil.ENGLISH, "announcements"),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setNavigation(
					"announcements"
				).buildString(),
				_language.get(themeDisplay.getLocale(), "announcements")),
			new PanelAppNavigationItem(
				_language.get(LocaleUtil.ENGLISH, "alerts"),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setNavigation(
					"alerts"
				).buildString(),
				_language.get(themeDisplay.getLocale(), "alerts")));
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN;
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + AnnouncementsPortletKeys.ANNOUNCEMENTS_ADMIN + ")"
	)
	private Portlet _portlet;

}