/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationRegistryUtil;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.users.admin.constants.UserScreenNavigationEntryConstants;
import com.liferay.users.admin.constants.UsersAdminPortletKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"panel.app.order:Integer=100",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_USERS
	},
	service = PanelApp.class
)
public class UsersPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "users";
	}

	@Override
	public List<PanelAppNavigationItem> getPanelAppNavigationItems(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return TransformUtil.unsafeTransform(
			ScreenNavigationRegistryUtil.getScreenNavigationCategories(
				UserScreenNavigationEntryConstants.
					SCREEN_NAVIGATION_KEY_USERS_AND_ORGANIZATIONS,
				themeDisplay.getUser(), null),
			screenNavigationCategory -> new PanelAppNavigationItem(
				screenNavigationCategory.getLabel(LocaleUtil.ENGLISH),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setParameter(
					"screenNavigationCategoryKey",
					screenNavigationCategory.getCategoryKey()
				).buildString(),
				screenNavigationCategory.getLabel(themeDisplay.getLocale())));
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return UsersAdminPortletKeys.USERS_ADMIN;
	}

	@Reference(
		target = "(jakarta.portlet.name=" + UsersAdminPortletKeys.USERS_ADMIN + ")"
	)
	private Portlet _portlet;

}