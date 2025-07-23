/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.exception;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.security.auth.ScreenNameValidator;
import com.liferay.portal.kernel.util.ClassUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringUtil;

/**
 * @author Brian Wing Shun Chan
 */
public class UserScreenNameException extends PortalException {

	public static class MustNotBeDuplicate extends UserScreenNameException {

		public MustNotBeDuplicate(long userId, String screenName) {
			super(
				String.format(
					"Screen name %s must not be duplicate but is already " +
						"used by user %s",
					screenName, userId));

			this.userId = userId;
			this.screenName = screenName;
		}

		public final String screenName;
		public final long userId;

	}

	public static class MustNotBeNull extends UserScreenNameException {

		public MustNotBeNull() {
			super("Screen name must not be null");
		}

		public MustNotBeNull(long userId) {
			super(
				String.format(
					"Screen name must not be null for user %s", userId));
		}

		public MustNotBeNull(String fullName) {
			super(
				String.format(
					"Screen name must not be null for the full name %s",
					fullName));
		}

	}

	public static class MustNotBeNumeric extends UserScreenNameException {

		public MustNotBeNumeric(long userId, String screenName) {
			super(
				String.format(
					"Screen name %s for user %s must not be numeric because " +
						"the portal property \"%s\" is disabled",
					screenName, userId,
					PropsKeys.USERS_SCREEN_NAME_ALLOW_NUMERIC));

			this.userId = userId;
			this.screenName = screenName;
		}

		public final String screenName;
		public final long userId;

	}

	public static class MustNotBeReserved extends UserScreenNameException {

		public MustNotBeReserved(
			long userId, String screenName, String[] reservedScreenNames) {

			super(
				String.format(
					"Screen name %s for user %s must not be a reserved name " +
						"such as: %s",
					screenName, userId, StringUtil.merge(reservedScreenNames)));

			this.userId = userId;
			this.screenName = screenName;
			this.reservedScreenNames = reservedScreenNames;
		}

		public final String[] reservedScreenNames;
		public final String screenName;
		public final long userId;

	}

	public static class MustNotBeReservedForAnonymous
		extends UserScreenNameException {

		public MustNotBeReservedForAnonymous(
			long userId, String screenName, String[] reservedScreenNames) {

			super(
				String.format(
					"Screen name %s for user %s must not be a reserved name " +
						"for anonymous users such as: %s",
					screenName, userId, StringUtil.merge(reservedScreenNames)));

			this.userId = userId;
			this.screenName = screenName;
			this.reservedScreenNames = reservedScreenNames;
		}

		public final String[] reservedScreenNames;
		public final String screenName;
		public final long userId;

	}

	public static class MustNotBeUsedByGroup extends UserScreenNameException {

		public MustNotBeUsedByGroup(
			long userId, String screenName, Group group) {

			super(
				String.format(
					"Screen name %s for user %s must not be used by group %s",
					screenName, userId, group.getGroupId()));

			this.userId = userId;
			this.screenName = screenName;
			this.group = group;
		}

		public final Group group;
		public final String screenName;
		public final long userId;

	}

	public static class MustNotExceedMaximumLength
		extends UserScreenNameException {

		public MustNotExceedMaximumLength(
			String screenName, int screenNameMaxLength) {

			super(
				StringBundler.concat(
					"Screen Name ", screenName, " must have fewer than ",
					screenNameMaxLength, " characters"));
		}

	}

	public static class MustProduceValidFriendlyURL
		extends UserScreenNameException {

		public MustProduceValidFriendlyURL(
			long userId, String screenName, int exceptionType) {

			super(
				String.format(
					"Screen name %s for user %s must produce a valid " +
						"friendly URL",
					screenName, userId),
				new GroupFriendlyURLException(exceptionType));

			this.userId = userId;
			this.screenName = screenName;
			this.exceptionType = exceptionType;
		}

		public final int exceptionType;
		public final String screenName;
		public final long userId;

	}

	public static class MustValidate extends UserScreenNameException {

		public MustValidate(
			long userId, String screenName,
			ScreenNameValidator screenNameValidator) {

			super(
				String.format(
					"Screen name %s for user %s must validate with %s: %s",
					screenName, userId,
					ClassUtil.getClassName(screenNameValidator),
					screenNameValidator.getDescription(
						LocaleUtil.getDefault())));

			this.userId = userId;
			this.screenName = screenName;
			this.screenNameValidator = screenNameValidator;
		}

		public final String screenName;
		public final ScreenNameValidator screenNameValidator;
		public final long userId;

	}

	private UserScreenNameException(String msg) {
		super(msg);
	}

	private UserScreenNameException(String msg, Throwable throwable) {
		super(msg, throwable);
	}

}