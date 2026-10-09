/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.petra.function.UnsafeFunction;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author Mikel Lorza
 */
public class LocalizedValueUtil {

	public static JSONObject toJSONObject(Map<String, String> localizedValues) {
		return toJSONObject(localizedValues, value -> value);
	}

	public static <T, R, E extends Throwable> JSONObject toJSONObject(
			Map<String, T> localizedValues,
			UnsafeFunction<T, R, E> unsafeFunction)
		throws E {

		if (localizedValues == null) {
			return null;
		}

		JSONObject jsonObject = JSONFactoryUtil.createJSONObject();

		Set<Locale> availableLocales = LanguageUtil.getAvailableLocales();

		for (Map.Entry<String, T> entry : localizedValues.entrySet()) {
			Locale locale = LocaleUtil.fromLanguageId(
				entry.getKey(), true, false);

			if ((locale == null) || !availableLocales.contains(locale)) {
				continue;
			}

			jsonObject.put(
				LocaleUtil.toLanguageId(locale),
				unsafeFunction.apply(entry.getValue()));
		}

		return jsonObject;
	}

	public static Map<String, String> toLocalizedValues(JSONObject jsonObject) {
		return toLocalizedValues(jsonObject, key -> jsonObject.getString(key));
	}

	public static <R, E extends Throwable> Map<String, R> toLocalizedValues(
			JSONObject jsonObject, UnsafeFunction<String, R, E> unsafeFunction)
		throws E {

		if (jsonObject == null) {
			return null;
		}

		return new HashMap<>() {
			{
				Set<Locale> availableLocales =
					LanguageUtil.getAvailableLocales();

				for (String key : jsonObject.keySet()) {
					Locale locale = LocaleUtil.fromLanguageId(key, true, false);

					if ((locale == null) ||
						!availableLocales.contains(locale)) {

						continue;
					}

					put(
						LocaleUtil.toBCP47LanguageId(key),
						unsafeFunction.apply(key));
				}
			}
		};
	}

}