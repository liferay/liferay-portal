/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.user.personal.bar.web.internal.portlet;

import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserNotificationDeliveryConstants;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.service.UserNotificationEventLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.product.navigation.personal.menu.configuration.PersonalMenuConfiguration;
import com.liferay.product.navigation.personal.menu.configuration.PersonalMenuConfigurationRegistry;
import com.liferay.product.navigation.user.personal.bar.web.internal.constants.ProductNavigationUserPersonalBarPortletKeys;
import com.liferay.product.navigation.user.personal.bar.web.internal.constants.ProductNavigationUserPersonalBarWebKeys;
import com.liferay.site.manager.RecentGroupManager;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-user-personal-bar",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.use-default-template=false",
		"jakarta.portlet.display-name=User Personal Bar",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + ProductNavigationUserPersonalBarPortletKeys.PRODUCT_NAVIGATION_USER_PERSONAL_BAR,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class ProductNavigationUserPersonalBarPortlet extends MVCPortlet {

	@Override
	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		PersonalMenuConfiguration personalMenuConfiguration =
			_personalMenuConfigurationRegistry.
				getCompanyPersonalMenuConfiguration(
					themeDisplay.getCompanyId());

		if (personalMenuConfiguration.showNotificationBadgeInPersonalMenu() &&
			!user.isGuestUser()) {

			renderRequest.setAttribute(
				ProductNavigationUserPersonalBarWebKeys.NOTIFICATIONS_COUNT,
				_getNotificationsCount(themeDisplay));
		}

		_recentGroupManager.addRecentGroup(
			_portal.getHttpServletRequest(renderRequest),
			themeDisplay.getScopeGroupId());

		super.doDispatch(renderRequest, renderResponse);
	}

	private int _getNotificationsCount(ThemeDisplay themeDisplay) {
		if (_userNotificationEventLocalService == null) {
			return 0;
		}

		return _userNotificationEventLocalService.
			getUserNotificationEventsCount(
				themeDisplay.getUserId(),
				UserNotificationDeliveryConstants.TYPE_WEBSITE, true, false);
	}

	@Reference
	private PersonalMenuConfigurationRegistry
		_personalMenuConfigurationRegistry;

	@Reference
	private Portal _portal;

	@Reference
	private RecentGroupManager _recentGroupManager;

	@Reference
	private UserNotificationEventLocalService
		_userNotificationEventLocalService;

}