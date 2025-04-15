/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.personal.menu.web.internal.control.menu;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.control.menu.BaseJSPProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.ProductNavigationControlMenuEntry;
import com.liferay.product.navigation.control.menu.constants.ProductNavigationControlMenuCategoryKeys;
import com.liferay.product.navigation.personal.menu.configuration.PersonalMenuConfiguration;
import com.liferay.product.navigation.personal.menu.configuration.PersonalMenuConfigurationRegistry;
import com.liferay.product.navigation.personal.menu.web.internal.constants.PersonalMenuWebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Samuel Trong Tran
 */
@Component(
	property = {
		"product.navigation.control.menu.category.key=" + ProductNavigationControlMenuCategoryKeys.USER,
		"product.navigation.control.menu.entry.order:Integer=700"
	},
	service = ProductNavigationControlMenuEntry.class
)
public class PersonalMenuProductNavigationControlMenuEntry
	extends BaseJSPProductNavigationControlMenuEntry
	implements ProductNavigationControlMenuEntry {

	@Override
	public String getIconJspPath() {
		return "/control_menu/personal_menu_icon.jsp";
	}

	@Override
	public boolean includeIcon(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		PersonalMenuConfiguration personalMenuConfiguration =
			_personalMenuConfigurationRegistry.
				getCompanyPersonalMenuConfiguration(
					themeDisplay.getCompanyId());

		if (personalMenuConfiguration.showNotificationBadgeInPersonalMenu() &&
			!user.isGuestUser() &&
			(_userNotificationEventLocalService != null)) {

			httpServletRequest.setAttribute(
				PersonalMenuWebKeys.NOTIFICATIONS_COUNT,
				_userNotificationEventLocalService.
					getUserNotificationEventsCount(
						themeDisplay.getUserId(),
						UserNotificationDeliveryConstants.TYPE_WEBSITE, true,
						false));
		}

		return super.includeIcon(httpServletRequest, httpServletResponse);
	}

	@Override
	public boolean isShow(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		if (user.isOnDemandUser()) {
			return false;
		}

		PersonalMenuConfiguration personalMenuConfiguration =
			_personalMenuConfigurationRegistry.
				getCompanyPersonalMenuConfiguration(
					themeDisplay.getCompanyId());

		if (personalMenuConfiguration.showInControlMenu()) {
			return true;
		}

		Layout layout = themeDisplay.getLayout();

		if (!layout.isTypeControlPanel()) {
			return false;
		}

		return super.isShow(httpServletRequest);
	}

	@Override
	protected ServletContext getServletContext() {
		return _servletContext;
	}

	@Reference
	private PersonalMenuConfigurationRegistry
		_personalMenuConfigurationRegistry;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.product.navigation.personal.menu.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}