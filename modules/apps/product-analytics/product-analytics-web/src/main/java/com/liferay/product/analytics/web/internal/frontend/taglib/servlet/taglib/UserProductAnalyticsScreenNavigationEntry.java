/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.analytics.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.layout.utility.page.kernel.provider.LayoutUtilityPageEntryLayoutProvider;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.product.analytics.web.internal.configuration.ProductAnalyticsConfiguration;
import com.liferay.product.analytics.web.internal.constants.ProductAnalyticsScreenNavigationEntryConstants;
import com.liferay.product.analytics.web.internal.constants.ProductAnalyticsWebKeys;
import com.liferay.product.analytics.web.internal.display.context.ProductAnalyticsConsentPanelDisplayContext;
import com.liferay.users.admin.constants.UserScreenNavigationEntryConstants;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.ResourceBundle;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Christopher Kian
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class UserProductAnalyticsScreenNavigationEntry
	implements ScreenNavigationEntry<User> {

	@Override
	public String getCategoryKey() {
		return ProductAnalyticsScreenNavigationEntryConstants.
			CATEGORY_KEY_DATA_AND_PRIVACY;
	}

	@Override
	public String getEntryKey() {
		return ProductAnalyticsScreenNavigationEntryConstants.
			ENTRY_KEY_PRODUCT_ANALYTICS;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(
			_getResourceBundle(locale), "product-analytics-configuration-name");
	}

	@Override
	public String getScreenNavigationKey() {
		return UserScreenNavigationEntryConstants.SCREEN_NAVIGATION_KEY_USERS;
	}

	@Override
	public boolean isVisible(User user, User selUser) {
		if (selUser == null) {
			return false;
		}

		try {
			ProductAnalyticsConfiguration productAnalyticsConfiguration =
				_configurationProvider.getCompanyConfiguration(
					ProductAnalyticsConfiguration.class,
					selUser.getCompanyId());

			if (!productAnalyticsConfiguration.enabled()) {
				return false;
			}
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to load product analytics configuration",
					portalException);
			}

			return false;
		}

		PermissionChecker permissionChecker =
			PermissionCheckerFactoryUtil.create(selUser);

		if (permissionChecker.isCompanyAdmin()) {
			return true;
		}

		for (long groupId :
				_groupLocalService.getActiveGroupIds(selUser.getUserId())) {

			if (permissionChecker.isGroupAdmin(groupId)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		ProductAnalyticsConsentPanelDisplayContext
			productAnalyticsConsentPanelDisplayContext =
				new ProductAnalyticsConsentPanelDisplayContext(
					_layoutUtilityPageEntryLayoutProvider, httpServletRequest);

		httpServletRequest.setAttribute(
			ProductAnalyticsWebKeys.
				PRODUCT_ANALYTICS_CONSENT_PANEL_DISPLAY_CONTEXT,
			productAnalyticsConsentPanelDisplayContext);

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(
				"/product_analytics_consent_panel/view.jsp");

		try {
			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (ServletException servletException) {
			throw new IOException(servletException);
		}
	}

	private ResourceBundle _getResourceBundle(Locale locale) {
		return ResourceBundleUtil.getBundle(
			"content.Language", locale, getClass());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserProductAnalyticsScreenNavigationEntry.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private Language _language;

	@Reference
	private LayoutUtilityPageEntryLayoutProvider
		_layoutUtilityPageEntryLayoutProvider;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.product.analytics.web)"
	)
	private ServletContext _servletContext;

}