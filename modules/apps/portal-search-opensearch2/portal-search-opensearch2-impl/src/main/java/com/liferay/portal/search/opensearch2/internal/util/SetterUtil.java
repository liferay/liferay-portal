/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.util;

import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.opensearch2.internal.script.ScriptTranslator;
import com.liferay.portal.search.script.Script;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch._types.FieldValue;

/**
 * @author Petteri Karttunen
 */
public class SetterUtil {

	public static void setNotBlankString(
		Consumer<String> consumer, String value) {

		if (!Validator.isBlank(value)) {
			consumer.accept(value);
		}
	}

	public static void setNotEmptyStringList(
		Consumer<List<String>> consumer, List<String> value) {

		if (ListUtil.isNotEmpty(value)) {
			consumer.accept(value);
		}
	}

	public static void setNotNullBoolean(
		Consumer<Boolean> consumer, Boolean value) {

		if (value != null) {
			consumer.accept(value);
		}
	}

	public static void setNotNullDouble(
		Consumer<Double> consumer, Double value) {

		if (value != null) {
			consumer.accept(value);
		}
	}

	public static void setNotNullEmptyStringArrayAsList(
		Consumer<List<String>> consumer, String[] values) {

		if (ArrayUtil.isNotEmpty(values)) {
			consumer.accept(Arrays.asList(values));
		}
	}

	public static void setNotNullFieldValue(
		Consumer<FieldValue> consumer, Object value) {

		if (value == null) {
			return;
		}

		if (value instanceof Boolean) {
			consumer.accept(FieldValue.of((boolean)value));
		}
		else if (value instanceof Double) {
			consumer.accept(FieldValue.of((double)value));
		}
		else if (value instanceof Integer) {
			Integer integerValue = (Integer)value;

			consumer.accept(FieldValue.of(integerValue.longValue()));
		}
		else if (value instanceof Long) {
			consumer.accept(FieldValue.of((long)value));
		}
		else if (value instanceof String) {
			consumer.accept(FieldValue.of((String)value));
		}
		else {
			Class<?> clazz = value.getClass();

			throw new IllegalArgumentException(
				clazz.getName() + " cannot be converted to field value");
		}
	}

	public static void setNotNullFloat(Consumer<Float> consumer, Float value) {
		if (value != null) {
			consumer.accept(value);
		}
	}

	public static void setNotNullFloatAsDouble(
		Consumer<Double> consumer, Float value) {

		if (value != null) {
			consumer.accept(value.doubleValue());
		}
	}

	public static void setNotNullInteger(
		Consumer<Integer> consumer, Integer value) {

		if (value != null) {
			consumer.accept(value);
		}
	}

	public static void setNotNullIntegerAsDouble(
		Consumer<Double> consumer, Integer value) {

		if (value != null) {
			consumer.accept(value.doubleValue());
		}
	}

	public static void setNotNullIntegerAsFloat(
		Consumer<Float> consumer, Integer value) {

		if (value != null) {
			consumer.accept(value.floatValue());
		}
	}

	public static void setNotNullJsonData(
		Consumer<JsonData> consumer, Object value) {

		if (value != null) {
			consumer.accept(JsonData.of(value));
		}
	}

	public static void setNotNullLong(Consumer<Long> consumer, Long value) {
		if (value != null) {
			consumer.accept(value);
		}
	}

	public static void setNotNullScript(
		Consumer<org.opensearch.client.opensearch._types.Script> consumer,
		Script script) {

		if (script != null) {
			consumer.accept(scriptTranslator.translate(script));
		}
	}

	public static void setNotNullValueAsString(
		Consumer<String> consumer, Object value) {

		if (value != null) {
			consumer.accept(String.valueOf(value));
		}
	}

	protected static final ScriptTranslator scriptTranslator =
		new ScriptTranslator();

}