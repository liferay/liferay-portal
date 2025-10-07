/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.typeconverter;

import java.util.Date;
import java.util.Locale;

import jodd.typeconverter.TypeConverterManager;

/**
 * @author Eric Yan
 */
public class TypeConverterUtil {

	public static <T> T convertType(Object value, Class<T> destinationType) {
		return _typeConverterManager.convertType(value, destinationType);
	}

	public static <T> T convertType(
		Object value, Class<T> destinationType, T defaultValue) {

		T result = convertType(value, destinationType);

		if (result == null) {
			return defaultValue;
		}

		return result;
	}

	private static final TypeConverterManager _typeConverterManager;

	static {
		_typeConverterManager = TypeConverterManager.get();

		_typeConverterManager.register(Date.class, new DateTypeConverter());
		_typeConverterManager.register(Date[].class, new DateArrayConverter());
		_typeConverterManager.register(Locale.class, new LocaleTypeConverter());
		_typeConverterManager.register(Number.class, new NumberConverter());
		_typeConverterManager.register(
			Number[].class, new NumberArrayConverter());
	}

}