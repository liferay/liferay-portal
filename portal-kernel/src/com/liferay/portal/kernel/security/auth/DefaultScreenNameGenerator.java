/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.auth;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.service.UserLocalServiceUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Brian Wing Shun Chan
 * @author Alexander Chow
 * @author Juan Fernández
 */
public class DefaultScreenNameGenerator implements ScreenNameGenerator {

	@Override
	public String generate(long companyId, long userId, String emailAddress)
		throws Exception {

		String screenName = null;

		if (Validator.isNotNull(emailAddress)) {
			screenName = StringUtil.extractFirst(emailAddress, CharPool.AT);

			screenName = StringUtil.shorten(screenName, 64, StringPool.BLANK);

			screenName = StringUtil.toLowerCase(screenName);

			for (char c : screenName.toCharArray()) {
				if (!Validator.isChar(c) && !Validator.isDigit(c) &&
					(c != CharPool.DASH) && (c != CharPool.PERIOD)) {

					screenName = StringUtil.replace(
						screenName, c, CharPool.PERIOD);
				}
			}

			if (screenName.equals(DefaultScreenNameValidator.POSTFIX)) {
				screenName += StringPool.PERIOD + userId;
			}
		}
		else {
			screenName = String.valueOf(userId);
		}

		if (!_USERS_SCREEN_NAME_ALLOW_NUMERIC &&
			Validator.isNumber(screenName)) {

			screenName = _NONNUMERICAL_PREFIX + screenName;
		}

		String[] reservedScreenNames = PrefsPropsUtil.getStringArray(
			companyId, PropsKeys.ADMIN_RESERVED_SCREEN_NAMES,
			StringPool.NEW_LINE, _ADMIN_RESERVED_SCREEN_NAMES);

		for (String reservedScreenName : reservedScreenNames) {
			if (StringUtil.equalsIgnoreCase(screenName, reservedScreenName)) {
				return getUnusedScreenName(companyId, screenName);
			}
		}

		User user = UserLocalServiceUtil.fetchUserByScreenName(
			companyId, screenName);

		if (user != null) {
			return getUnusedScreenName(companyId, screenName);
		}

		Group friendlyURLGroup = GroupLocalServiceUtil.fetchFriendlyURLGroup(
			companyId, StringPool.SLASH + screenName);

		if (friendlyURLGroup == null) {
			return screenName;
		}

		return getUnusedScreenName(companyId, screenName);
	}

	protected String getUnusedScreenName(long companyId, String screenName) {
		for (int i = 1;; i++) {
			String tempScreenName = screenName + StringPool.PERIOD + i;

			User user = UserLocalServiceUtil.fetchUserByScreenName(
				companyId, tempScreenName);

			if (user != null) {
				continue;
			}

			Group friendlyURLGroup =
				GroupLocalServiceUtil.fetchFriendlyURLGroup(
					companyId, StringPool.SLASH + tempScreenName);

			if (friendlyURLGroup == null) {
				return tempScreenName;
			}
		}
	}

	private static final String[] _ADMIN_RESERVED_SCREEN_NAMES =
		StringUtil.splitLines(
			PropsUtil.get(PropsKeys.ADMIN_RESERVED_SCREEN_NAMES));

	private static final String _NONNUMERICAL_PREFIX = "user.";

	private static final boolean _USERS_SCREEN_NAME_ALLOW_NUMERIC =
		GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.USERS_SCREEN_NAME_ALLOW_NUMERIC));

}