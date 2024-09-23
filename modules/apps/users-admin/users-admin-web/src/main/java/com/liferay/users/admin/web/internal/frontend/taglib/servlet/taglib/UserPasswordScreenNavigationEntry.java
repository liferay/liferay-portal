/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.servlet.taglib.ScreenNavigationEntry;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.model.User;
import com.liferay.users.admin.constants.UserScreenNavigationEntryConstants;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pei-Jung Lan
 */
@Component(
	property = "screen.navigation.entry.order:Integer=60",
	service = ScreenNavigationEntry.class
)
public class UserPasswordScreenNavigationEntry
	extends BaseUserScreenNavigationEntry {

	@Override
	public String getActionCommandName() {
		return "/users_admin/update_password";
	}

	@Override
	public String getCategoryKey() {
		return UserScreenNavigationEntryConstants.CATEGORY_KEY_GENERAL;
	}

	@Override
	public String getEntryKey() {
		return UserScreenNavigationEntryConstants.ENTRY_KEY_PASSWORD;
	}

	@Override
	public String getJspPath() {
		return "/user/password.jsp";
	}

	@Override
	public boolean isVisible(User user, User selUser) {
		if ((selUser == null) || selUser.isServiceAccountUser()) {
			return false;
		}

		try {
			PasswordPolicy passwordPolicy = selUser.getPasswordPolicy();

			if (passwordPolicy == null) {
				return false;
			}

			return passwordPolicy.isChangeable();
		}
		catch (PortalException portalException) {
			_log.error(portalException);

			return false;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		UserPasswordScreenNavigationEntry.class);

}