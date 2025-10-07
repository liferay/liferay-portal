/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.typeconverter;

import java.util.Date;

import jodd.typeconverter.TypeConverter;

import jodd.util.CsvUtil;

/**
 * @author Raymond Augé
 */
public class DateArrayConverter implements TypeConverter<Date[]> {

	@Override
	public Date[] convert(Object value) {
		if (value == null) {
			return null;
		}

		Class<?> type = value.getClass();

		if (type.isArray() == false) {
			if (type == String.class) {
				String[] values = CsvUtil.toStringArray(value.toString());

				return convertArray(values);
			}

			return new Date[] {_dateTypeConverter.convert(value)};
		}

		Class<?> componentType = type.getComponentType();

		if (componentType.isPrimitive() && (type == long[].class)) {
			long[] values = (long[])value;

			Date[] results = new Date[values.length];

			for (int i = 0; i < values.length; i++) {
				results[i] = _dateTypeConverter.convert(values[i]);
			}

			return results;
		}

		return convertArray((Object[])value);
	}

	protected Date[] convertArray(Object[] values) {
		Date[] results = new Date[values.length];

		for (int i = 0; i < values.length; i++) {
			results[i] = _dateTypeConverter.convert(values[i]);
		}

		return results;
	}

	private final DateTypeConverter _dateTypeConverter =
		new DateTypeConverter();

}