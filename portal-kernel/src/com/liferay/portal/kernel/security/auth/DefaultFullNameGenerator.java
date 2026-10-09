/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.security.auth;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.NoSuchListTypeException;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.model.UserConstants;
import com.liferay.portal.kernel.service.ListTypeServiceUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Michael C. Han
 */
public class DefaultFullNameGenerator implements FullNameGenerator {

	public static final DefaultFullNameGenerator INSTANCE =
		new DefaultFullNameGenerator();

	@Override
	public String getFullName(
		String firstName, String middleName, String lastName) {

		String fullName = buildFullName(firstName, middleName, lastName, false);

		if (!isFullNameTooLong(fullName)) {
			return fullName;
		}

		fullName = buildFullName(firstName, middleName, lastName, true);

		if (!isFullNameTooLong(fullName)) {
			return fullName;
		}

		return shortenFullName(fullName);
	}

	@Override
	public String getLocalizedFullName(
		String firstName, String middleName, String lastName, Locale locale,
		long prefixListTypeId, long suffixListTypeId) {

		String fullName = buildLocalizedFullName(
			firstName, middleName, lastName, locale, prefixListTypeId,
			suffixListTypeId, false);

		if (!isFullNameTooLong(fullName)) {
			return fullName;
		}

		fullName = buildLocalizedFullName(
			firstName, middleName, lastName, locale, prefixListTypeId,
			suffixListTypeId, true);

		if (!isFullNameTooLong(fullName)) {
			return fullName;
		}

		return shortenFullName(fullName);
	}

	@Override
	public String[] splitFullName(String fullName) {
		String firstName = StringPool.BLANK;
		String middleName = StringPool.BLANK;
		String lastName = StringPool.BLANK;

		if (Validator.isNull(fullName)) {
			return new String[] {firstName, middleName, lastName};
		}

		String[] name = StringUtil.split(fullName, CharPool.SPACE);

		firstName = name[0];

		middleName = StringPool.BLANK;
		lastName = name[name.length - 1];

		if (name.length > 2) {
			for (int i = 1; i < (name.length - 1); i++) {
				if (Validator.isNull(name[i].trim())) {
					continue;
				}

				if (i != 1) {
					middleName += StringPool.SPACE;
				}

				middleName += name[i].trim();
			}
		}

		return new String[] {firstName, middleName, lastName};
	}

	protected String buildFullName(
		String firstName, String middleName, String lastName,
		boolean useInitials) {

		StringBundler sb = new StringBundler(5);

		if (useInitials) {
			firstName = firstName.substring(0, 1);
		}

		sb.append(firstName);

		if (Validator.isNotNull(middleName)) {
			if (useInitials) {
				middleName = middleName.substring(0, 1);
			}

			sb.append(StringPool.SPACE);
			sb.append(middleName);
		}

		if (Validator.isNotNull(lastName)) {
			sb.append(StringPool.SPACE);
			sb.append(lastName);
		}

		return sb.toString();
	}

	protected String buildLocalizedFullName(
		String firstName, String middleName, String lastName, Locale locale,
		long prefixListTypeId, long suffixListTypeId, boolean useInitials) {

		Map<String, String> namesMap = new HashMap<>();

		if (Validator.isNotNull(firstName)) {
			if (useInitials) {
				firstName = firstName.substring(0, 1);
			}

			namesMap.put("first-name", firstName);
		}

		if (Validator.isNotNull(middleName)) {
			if (useInitials) {
				middleName = middleName.substring(0, 1);
			}

			namesMap.put("middle-name", middleName);
		}

		if (Validator.isNotNull(lastName)) {
			namesMap.put("last-name", lastName);
		}

		if (prefixListTypeId != 0) {
			try {
				ListType listType = ListTypeServiceUtil.getListType(
					suffixListTypeId);

				String prefix = listType.getName();

				prefix = LanguageUtil.get(locale, prefix);

				namesMap.put("prefix", prefix);
			}
			catch (NoSuchListTypeException noSuchListTypeException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Ignoring full name prefix " + prefixListTypeId,
						noSuchListTypeException);
				}
			}
			catch (PortalException portalException) {
				throw new SystemException(portalException);
			}
		}

		if (suffixListTypeId != 0) {
			try {
				ListType listType = ListTypeServiceUtil.getListType(
					suffixListTypeId);

				String suffix = listType.getName();

				suffix = LanguageUtil.get(locale, suffix);

				namesMap.put("suffix", suffix);
			}
			catch (NoSuchListTypeException noSuchListTypeException) {
				if (_log.isDebugEnabled()) {
					_log.debug(
						"Ignoring full name suffix " + suffixListTypeId,
						noSuchListTypeException);
				}
			}
			catch (PortalException portalException) {
				throw new SystemException(portalException);
			}
		}

		FullNameDefinition fullNameDefinition =
			FullNameDefinitionFactory.getInstance(locale);

		List<FullNameField> fullNameFields =
			fullNameDefinition.getFullNameFields();

		StringBundler sb = new StringBundler(2 * fullNameFields.size());

		for (FullNameField fullNameField : fullNameFields) {
			String name = namesMap.get(fullNameField.getName());

			if (name == null) {
				continue;
			}

			sb.append(StringPool.SPACE);
			sb.append(name);
		}

		return StringUtil.trim(sb.toString());
	}

	protected boolean isFullNameTooLong(String fullName) {
		if (fullName.length() > UserConstants.FULL_NAME_MAX_LENGTH) {
			return true;
		}

		return false;
	}

	protected String shortenFullName(String fullName) {
		if (_log.isInfoEnabled()) {
			_log.info(
				StringBundler.concat(
					"Full name exceeds ", UserConstants.FULL_NAME_MAX_LENGTH,
					" characters for user ", fullName,
					". Full name was shortened."));
		}

		return fullName.substring(0, UserConstants.FULL_NAME_MAX_LENGTH);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultFullNameGenerator.class);

}