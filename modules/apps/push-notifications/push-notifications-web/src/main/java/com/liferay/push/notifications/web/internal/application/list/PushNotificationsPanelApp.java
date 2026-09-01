/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.push.notifications.web.internal.application.list;

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
import com.liferay.push.notifications.constants.PushNotificationsPortletKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Andrea Di Giorgi
 */
@Component(
	property = {
		"panel.app.order:Integer=300",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_CONFIGURATION
	},
	service = PanelApp.class
)
public class PushNotificationsPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "bell-on";
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
				_language.get(LocaleUtil.ENGLISH, "devices"),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setTabs1(
					"devices"
				).buildString(),
				_language.get(themeDisplay.getLocale(), "devices")),
			new PanelAppNavigationItem(
				_language.get(LocaleUtil.ENGLISH, "test"),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setTabs1(
					"test"
				).buildString(),
				_language.get(themeDisplay.getLocale(), "test")));
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return PushNotificationsPortletKeys.PUSH_NOTIFICATIONS;
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + PushNotificationsPortletKeys.PUSH_NOTIFICATIONS + ")"
	)
	private Portlet _portlet;

}