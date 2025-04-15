/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.iframe.web.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.RoleLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.PortletRequest;

/**
 * @author Amos Fong
 */
public class IFrameUtil {

	public static String getPassword(
			PortletRequest portletRequest, String password)
		throws PortalException {

		if (Validator.isNotNull(password) && password.equals("@password@") &&
			isPasswordTokenResolutionEnabled(portletRequest)) {

			password = PortalUtil.getUserPassword(portletRequest);
		}

		if (password == null) {
			password = StringPool.BLANK;
		}

		return password;
	}

	public static String getUserName(
			PortletRequest portletRequest, String userName)
		throws PortalException {

		User user = PortalUtil.getUser(portletRequest);

		if (user == null) {
			return userName;
		}

		if (Validator.isNull(userName) || userName.equals("@user_id@")) {
			userName = portletRequest.getRemoteUser();
		}
		else if (userName.equals("@email_address@")) {
			userName = user.getEmailAddress();
		}
		else if (userName.equals("@screen_name@")) {
			userName = user.getScreenName();
		}

		return userName;
	}

	public static boolean isPasswordTokenEnabled(PortletRequest portletRequest)
		throws PortalException {

		if (!PropsValues.SESSION_STORE_PASSWORD) {
			return false;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		Group layoutGroup = layout.getGroup();

		if (layout.isPrivateLayout() && layoutGroup.isUser() &&
			(themeDisplay.getRealUserId() == layoutGroup.getClassPK())) {

			return true;
		}

		String roleName = PropsValues.IFRAME_PASSWORD_PASSWORD_TOKEN_ROLE;

		if (Validator.isNull(roleName)) {
			return false;
		}

		try {
			Role role = RoleLocalServiceUtil.getRole(
				themeDisplay.getCompanyId(), roleName);

			if (UserLocalServiceUtil.hasRoleUser(
					role.getRoleId(), themeDisplay.getUserId())) {

				return true;
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					StringBundler.concat(
						"Error getting role ", roleName,
						". The password token will be disabled."),
					exception);
			}
		}

		return false;
	}

	public static boolean isPasswordTokenResolutionEnabled(
			PortletRequest portletRequest)
		throws PortalException {

		if (!PropsValues.SESSION_STORE_PASSWORD) {
			return false;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		Layout layout = themeDisplay.getLayout();

		Group layoutGroup = layout.getGroup();

		if (layout.isPrivateLayout() && layoutGroup.isUser() &&
			(themeDisplay.getRealUserId() != layoutGroup.getClassPK())) {

			return false;
		}

		return true;
	}

	private static final Log _log = LogFactoryUtil.getLog(IFrameUtil.class);

}