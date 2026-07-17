/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.entry.util;

import com.liferay.document.library.kernel.model.DLFileEntry;
import com.liferay.document.library.kernel.service.DLFileEntryLocalServiceUtil;
import com.liferay.object.constants.ObjectFieldConstants;
import com.liferay.object.model.ObjectField;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HtmlParserUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;

import java.util.Map;

/**
 * @author Feliphe Marinho
 */
public class ObjectEntryValuesUtil {

	public static Object getTitleFieldValue(
		String businessType, Map<String, Object> modelAttributes,
		ObjectField objectField, User user, Map<String, ?> values) {

		Map.Entry<String, ?> valueEntry = null;

		if (values != null) {
			valueEntry = MapUtil.getEntry(values, objectField.getName());
		}

		if (valueEntry == null) {
			return modelAttributes.get(objectField.getDBColumnName());
		}

		Object value = valueEntry.getValue();

		if (StringUtil.equals(
				businessType, ObjectFieldConstants.BUSINESS_TYPE_BOOLEAN)) {

			return GetterUtil.getBoolean(value);
		}

		if (!(value instanceof Map)) {
			return value;
		}

		Map<String, Object> localizedValues = (Map<String, Object>)value;

		String siteDefaultLanguageId = LanguageUtil.getLanguageId(
			LocaleUtil.getSiteDefault());

		Object localizedValue = localizedValues.get(siteDefaultLanguageId);

		if (localizedValue != null) {
			return localizedValue;
		}

		if (user != null) {
			localizedValue = localizedValues.get(user.getLanguageId());

			if (localizedValue != null) {
				return localizedValue;
			}
		}

		return localizedValues.get(
			LanguageUtil.getLanguageId(LocaleUtil.getDefault()));
	}

	public static Object getValue(
		String languageId, ObjectField objectField, Map<String, ?> values) {

		if (objectField == null) {
			return null;
		}

		if (StringUtil.equals(objectField.getName(), "creator")) {
			return values.get("userName");
		}
		else if (StringUtil.equals(objectField.getName(), "id")) {
			return values.get("objectEntryId");
		}

		Object value = values.get(objectField.getName());

		if ((languageId != null) && objectField.isLocalized()) {
			Map<String, Object> localizedValues =
				(Map<String, Object>)values.get(
					objectField.getI18nObjectFieldName());

			if (MapUtil.isNotEmpty(localizedValues)) {
				value = localizedValues.get(languageId);
			}
		}

		if (objectField.compareBusinessType(
				ObjectFieldConstants.BUSINESS_TYPE_ATTACHMENT)) {

			long dlFileEntryId = GetterUtil.getLong(value);

			if (dlFileEntryId == 0) {
				return StringPool.BLANK;
			}

			DLFileEntry dlFileEntry =
				DLFileEntryLocalServiceUtil.fetchDLFileEntry(dlFileEntryId);

			if (dlFileEntry != null) {
				return dlFileEntry.getFileName();
			}

			return StringPool.BLANK;
		}
		else if (objectField.compareBusinessType(
					ObjectFieldConstants.BUSINESS_TYPE_RICH_TEXT)) {

			return HtmlParserUtil.extractText(GetterUtil.getString(value));
		}

		return value;
	}

	public static String getValueString(
		ObjectField objectField, Map<String, ?> values) {

		return String.valueOf(getValue(null, objectField, values));
	}

}