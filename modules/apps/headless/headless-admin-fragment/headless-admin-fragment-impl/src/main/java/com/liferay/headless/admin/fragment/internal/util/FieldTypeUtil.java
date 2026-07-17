/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.internal.util;

import com.liferay.headless.admin.fragment.constant.v1_0.FieldType;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.Map;
import java.util.Objects;

/**
 * @author Rubén Pulido
 */
public class FieldTypeUtil {

	public static FieldType toExternalFieldType(String internalFieldType) {
		for (Map.Entry<FieldType, String> entry :
				_externalToInternalValuesMap.entrySet()) {

			if (Objects.equals(internalFieldType, entry.getValue())) {
				return entry.getKey();
			}
		}

		throw new UnsupportedOperationException();
	}

	public static String toInternalFieldType(FieldType externalFieldType) {
		String internalFieldType = _externalToInternalValuesMap.get(
			externalFieldType);

		if (internalFieldType != null) {
			return internalFieldType;
		}

		throw new UnsupportedOperationException();
	}

	public static String[] toInternalFieldTypes(
		FieldType[] externalFieldTypes) {

		return TransformUtil.transform(
			externalFieldTypes, FieldTypeUtil::toInternalFieldType,
			String.class);
	}

	private static final Map<FieldType, String> _externalToInternalValuesMap =
		HashMapBuilder.put(
			FieldType.BOOLEAN, "boolean"
		).put(
			FieldType.CAPTCHA, "captcha"
		).put(
			FieldType.DATE, "date"
		).put(
			FieldType.DATE_TIME, "date-time"
		).put(
			FieldType.EMAIL, "email"
		).put(
			FieldType.FILE, "file"
		).put(
			FieldType.FORM_BUTTON, "formButton"
		).put(
			FieldType.FRIENDLY_URL, "friendly-url"
		).put(
			FieldType.HTML, "html"
		).put(
			FieldType.LOCALIZATION_SELECT, "localizationSelect"
		).put(
			FieldType.LONG_TEXT, "long-text"
		).put(
			FieldType.MULTISELECT, "multiselect"
		).put(
			FieldType.NUMBER, "number"
		).put(
			FieldType.PHONE_NUMBER, "phone-number"
		).put(
			FieldType.RELATIONSHIP, "relationship"
		).put(
			FieldType.SELECT, "select"
		).put(
			FieldType.STEPPER, "stepper"
		).put(
			FieldType.TEXT, "text"
		).build();

}