/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.elasticsearch8.internal.util;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.Result;
import co.elastic.clients.elasticsearch._types.aggregations.FieldDateMath;
import co.elastic.clients.json.JsonData;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Petteri Karttunen
 */
public class ConversionUtil {

	public static Double toDouble(Object object) {
		if (object == null) {
			return null;
		}

		if (object instanceof Float) {
			Float value = (Float)object;

			return value.doubleValue();
		}
		else if (object instanceof Integer) {
			Integer value = (Integer)object;

			return value.doubleValue();
		}
		else if (object instanceof Long) {
			Long value = (Long)object;

			return value.doubleValue();
		}

		throw new IllegalArgumentException(
			_getClassName(object) + " cannot be cast to double");
	}

	public static List<Double> toDoubleList(double[] array) {
		if (array.length == 0) {
			return Collections.emptyList();
		}

		return ListUtil.fromArray(array);
	}

	public static FieldDateMath toFieldDateMath(
		String stringValue, Double value) {

		if (!Validator.isBlank(stringValue)) {
			return FieldDateMath.of(
				fieldDateMath -> fieldDateMath.expr(stringValue));
		}
		else if (value != null) {
			return FieldDateMath.of(
				fieldDateMath -> fieldDateMath.value(value.longValue()));
		}

		return null;
	}

	public static FieldValue toFieldValue(Object object) {
		if (object == null) {
			return FieldValue.of(fieldValue -> fieldValue.nullValue());
		}
		else if (object instanceof Boolean) {
			return FieldValue.of(
				fieldValue -> fieldValue.booleanValue((Boolean)object));
		}
		else if (object instanceof Double) {
			return FieldValue.of(
				fieldValue -> fieldValue.doubleValue((Double)object));
		}
		else if (object instanceof Integer) {
			Integer value = (Integer)object;

			return FieldValue.of(
				fieldValue -> fieldValue.stringValue(value.toString()));
		}
		else if (object instanceof Long) {
			return FieldValue.of(
				fieldValue -> fieldValue.longValue((Long)object));
		}
		else if (object instanceof String) {
			return FieldValue.of(
				fieldValue -> fieldValue.stringValue((String)object));
		}

		throw new IllegalArgumentException(
			_getClassName(object) + " cannot be converted to field value");
	}

	public static List<FieldValue> toFieldValues(Boolean... values) {
		List<FieldValue> fieldValues = new ArrayList<>();

		ArrayUtil.isNotEmptyForEach(
			values, value -> fieldValues.add(FieldValue.of(value)));

		return fieldValues;
	}

	public static List<FieldValue> toFieldValues(Double... values) {
		List<FieldValue> fieldValues = new ArrayList<>();

		ArrayUtil.isNotEmptyForEach(
			values, value -> fieldValues.add(FieldValue.of(value)));

		return fieldValues;
	}

	public static List<FieldValue> toFieldValues(Long... values) {
		List<FieldValue> fieldValues = new ArrayList<>();

		ArrayUtil.isNotEmptyForEach(
			values, value -> fieldValues.add(FieldValue.of(value)));

		return fieldValues;
	}

	public static List<FieldValue> toFieldValues(String... values) {
		List<FieldValue> fieldValues = new ArrayList<>();

		ArrayUtil.isNotEmptyForEach(
			values, value -> fieldValues.add(FieldValue.of(value)));

		return fieldValues;
	}

	public static Float toFloat(Object object) {
		return toFloat(object, null);
	}

	public static Float toFloat(Object object, Float defaultValue) {
		if (object == null) {
			return defaultValue;
		}

		if (object instanceof Double) {
			Double value = (Double)object;

			return value.floatValue();
		}
		else if (object instanceof Integer) {
			Integer value = (Integer)object;

			return value.floatValue();
		}
		else if (object instanceof Long) {
			Long value = (Long)object;

			return value.floatValue();
		}

		throw new IllegalArgumentException(
			_getClassName(object) + " cannot be converted to float");
	}

	public static int toHttpStatusCode(Result result) {
		if ((result == Result.Created) || (result == Result.Deleted) ||
			(result == Result.Updated)) {

			return 200;
		}
		else if (result == Result.NoOp) {
			return 204;
		}
		else if (result == Result.NotFound) {
			return 404;
		}

		return -1;
	}

	public static int toInt(Number number) {
		return number.intValue();
	}

	public static Map<String, JsonData> toJsonDataMap(Map<String, Object> map) {
		Map<String, JsonData> jsonDatas = new HashMap<>();

		if (map == null) {
			return jsonDatas;
		}

		MapUtil.isNotEmptyForEach(
			map, (key, value) -> jsonDatas.put(key, JsonData.of(value)));

		return jsonDatas;
	}

	private static String _getClassName(Object object) {
		Class<?> clazz = object.getClass();

		return clazz.getName();
	}

}