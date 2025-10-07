/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.typeconverter;

import com.liferay.portal.kernel.util.LocaleUtil;

import java.util.Locale;

import jodd.typeconverter.TypeConverter;

/**
 * @author Eric Yan
 */
public class LocaleTypeConverter implements TypeConverter<Locale> {

	@Override
	public Locale convert(Object object) {
		if (object == null) {
			return null;
		}

		if (object instanceof Locale) {
			return (Locale)object;
		}

		return LocaleUtil.fromLanguageId(String.valueOf(object), false);
	}

}