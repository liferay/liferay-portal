/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util;

import com.liferay.dynamic.data.mapping.model.DDMFormFieldOptions;
import com.liferay.dynamic.data.mapping.model.LocalizedValue;
import com.liferay.list.type.model.ListTypeEntry;
import com.liferay.list.type.service.ListTypeEntryLocalService;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author Pedro Leite
 */
public class DDMFormFieldTemplateContextContributorUtil {

	public static Map<String, Object> getLocaleMap(Locale defaultLocale) {
		JSONObject localeJSONObject = _getLocaleJSONObject(defaultLocale);

		return HashMapBuilder.<String, Object>put(
			"availableLocales",
			JSONUtil.toJSONArray(
				LanguageUtil.getAvailableLocales(),
				locale -> _getLocaleJSONObject(locale), _log)
		).put(
			"defaultLocale", localeJSONObject
		).put(
			"editingLocale", localeJSONObject
		).build();
	}

	public static List<Map<String, Object>> getOptions(
		DDMFormFieldOptions ddmFormFieldOptions, Long listTypeDefinitionId,
		ListTypeEntryLocalService listTypeEntryLocalService) {

		List<Map<String, Object>> options = new ArrayList<>();

		for (String optionValue : ddmFormFieldOptions.getOptionsValues()) {
			if (optionValue == null) {
				continue;
			}

			LocalizedValue localizedValue = ddmFormFieldOptions.getOptionLabels(
				optionValue);

			options.add(
				HashMapBuilder.<String, Object>put(
					"label",
					localizedValue.getString(localizedValue.getDefaultLocale())
				).put(
					"labelMap",
					() -> {
						Map<Locale, String> labeMap = _getListTypeEntryNameMap(
							optionValue, listTypeDefinitionId,
							listTypeEntryLocalService);

						if (labeMap != null) {
							return labeMap;
						}

						return localizedValue.getValues();
					}
				).put(
					"reference",
					ddmFormFieldOptions.getOptionReference(optionValue)
				).put(
					"value", optionValue
				).build());
		}

		return options;
	}

	private static Map<Locale, String> _getListTypeEntryNameMap(
		String key, long listTypeDefinitionId,
		ListTypeEntryLocalService listTypeEntryLocalService) {

		if (listTypeDefinitionId == 0) {
			return null;
		}

		ListTypeEntry listTypeEntry =
			listTypeEntryLocalService.fetchListTypeEntry(
				listTypeDefinitionId, key);

		if (listTypeEntry == null) {
			return null;
		}

		return listTypeEntry.getNameMap();
	}

	private static JSONObject _getLocaleJSONObject(Locale locale) {
		String languageId = LocaleUtil.toLanguageId(locale);

		return JSONUtil.put(
			"displayName", locale.getDisplayName(locale)
		).put(
			"icon",
			StringUtil.toLowerCase(StringUtil.replace(languageId, '_', "-"))
		).put(
			"localeId", languageId
		);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DDMFormFieldTemplateContextContributorUtil.class);

}