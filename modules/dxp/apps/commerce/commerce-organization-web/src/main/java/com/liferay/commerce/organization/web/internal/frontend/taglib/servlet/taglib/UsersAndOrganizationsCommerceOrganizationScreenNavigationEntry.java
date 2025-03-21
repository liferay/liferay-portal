/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.organization.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.commerce.organization.constants.CommerceOrganizationScreenNavigationEntryConstants;
import com.liferay.commerce.organization.web.internal.display.context.CommerceOrganizationDisplayContext;
import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.frontend.taglib.servlet.taglib.util.JSPRenderer;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.OrganizationLocalService;
import com.liferay.portal.kernel.service.OrganizationService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.users.admin.constants.UserScreenNavigationEntryConstants;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Stefano Motta
 */
@Component(
	property = "screen.navigation.entry.order:Integer=10",
	service = ScreenNavigationEntry.class
)
public class UsersAndOrganizationsCommerceOrganizationScreenNavigationEntry
	implements ScreenNavigationEntry<User> {

	@Override
	public String getCategoryKey() {
		return CommerceOrganizationScreenNavigationEntryConstants.
			CATEGORY_KEY_COMMERCE_ORGANIZATION;
	}

	@Override
	public String getEntryKey() {
		return CommerceOrganizationScreenNavigationEntryConstants.
			CATEGORY_KEY_COMMERCE_ORGANIZATION;
	}

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "organization-chart");
	}

	@Override
	public String getScreenNavigationKey() {
		return UserScreenNavigationEntryConstants.
			SCREEN_NAVIGATION_KEY_USERS_AND_ORGANIZATIONS;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			CommerceOrganizationDisplayContext
				commerceOrganizationDisplayContext =
					new CommerceOrganizationDisplayContext(
						_configurationProvider, httpServletRequest,
						_organizationLocalService, _organizationService,
						_portal, _userLocalService);

			commerceOrganizationDisplayContext.setAdminPortlet(true);

			httpServletRequest.setAttribute(
				WebKeys.PORTLET_DISPLAY_CONTEXT,
				commerceOrganizationDisplayContext);
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		_jspRenderer.renderJSP(
			_servletContext, httpServletRequest, httpServletResponse,
			"/view.jsp");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UsersAndOrganizationsCommerceOrganizationScreenNavigationEntry.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private JSPRenderer _jspRenderer;

	@Reference
	private Language _language;

	@Reference
	private OrganizationLocalService _organizationLocalService;

	@Reference
	private OrganizationService _organizationService;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.commerce.organization.web)"
	)
	private ServletContext _servletContext;

	@Reference
	private UserLocalService _userLocalService;

}