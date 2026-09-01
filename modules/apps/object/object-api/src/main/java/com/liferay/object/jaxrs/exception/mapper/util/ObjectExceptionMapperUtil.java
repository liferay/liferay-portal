/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.jaxrs.exception.mapper.util;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;

import java.util.List;

/**
 * @author Murilo Stodolni
 */
public class ObjectExceptionMapperUtil {

	public static String getTitle(
		AcceptLanguage acceptLanguage, List<Object> arguments,
		Language language, String message, String messageKey) {

		if (Validator.isNull(messageKey)) {
			return message;
		}

		if (arguments == null) {
			return language.get(
				acceptLanguage.getPreferredLocale(), messageKey);
		}

		return language.format(
			acceptLanguage.getPreferredLocale(), messageKey,
			arguments.toArray(), false);
	}

}