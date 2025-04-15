/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.product.navigation.personal.menu;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.product.navigation.personal.menu.PersonalMenuEntry;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = {
		"product.navigation.personal.menu.entry.order:Integer=200",
		"product.navigation.personal.menu.group:Integer=100"
	},
	service = PersonalMenuEntry.class
)
public class MyProfilePersonalMenuEntry implements PersonalMenuEntry {

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "my-profile");
	}

	@Override
	public String getPortletURL(HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		return user.getDisplayURL(themeDisplay, false);
	}

	@Override
	public boolean isActive(PortletRequest portletRequest, String portletId)
		throws PortalException {

		if (Validator.isNotNull(portletId)) {
			return false;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		String displayURL = layout.getRegularURL(
			_portal.getHttpServletRequest(portletRequest));

		User user = themeDisplay.getUser();

		return displayURL.startsWith(user.getDisplayURL(themeDisplay, false));
	}

	@Override
	public boolean isShow(
			PortletRequest portletRequest, PermissionChecker permissionChecker)
		throws PortalException {

		if (!PrefsPropsUtil.getBoolean(
				_portal.getCompanyId(portletRequest),
				PropsKeys.LAYOUT_USER_PUBLIC_LAYOUTS_ENABLED)) {

			return false;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		User user = themeDisplay.getUser();

		if (Validator.isNull(user.getDisplayURL(themeDisplay, false)) ||
			(PropsValues.LAYOUT_USER_PUBLIC_LAYOUTS_POWER_USER_REQUIRED &&
			 !roleLocalService.hasUserRole(
				 themeDisplay.getUserId(), themeDisplay.getCompanyId(),
				 RoleConstants.POWER_USER, true))) {

			return false;
		}

		return true;
	}

	@Reference
	protected RoleLocalService roleLocalService;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

}