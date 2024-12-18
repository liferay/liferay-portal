/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v2_0;

import com.liferay.search.experiences.rest.client.dto.v2_0.Condition;
import com.liferay.search.experiences.rest.client.dto.v2_0.ConditionConfiguration;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class ConditionConfigurationSerDes {

	public static ConditionConfiguration toDTO(String json) {
		ConditionConfigurationJSONParser conditionConfigurationJSONParser =
			new ConditionConfigurationJSONParser();

		return conditionConfigurationJSONParser.parseToDTO(json);
	}

	public static ConditionConfiguration[] toDTOs(String json) {
		ConditionConfigurationJSONParser conditionConfigurationJSONParser =
			new ConditionConfigurationJSONParser();

		return conditionConfigurationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ConditionConfiguration conditionConfiguration) {
		if (conditionConfiguration == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (conditionConfiguration.getConditions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"conditions\": ");

			sb.append("[");

			for (int i = 0; i < conditionConfiguration.getConditions().length;
				 i++) {

				sb.append(
					String.valueOf(conditionConfiguration.getConditions()[i]));

				if ((i + 1) < conditionConfiguration.getConditions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ConditionConfigurationJSONParser conditionConfigurationJSONParser =
			new ConditionConfigurationJSONParser();

		return conditionConfigurationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ConditionConfiguration conditionConfiguration) {

		if (conditionConfiguration == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (conditionConfiguration.getConditions() == null) {
			map.put("conditions", null);
		}
		else {
			map.put(
				"conditions",
				String.valueOf(conditionConfiguration.getConditions()));
		}

		return map;
	}

	public static class ConditionConfigurationJSONParser
		extends BaseJSONParser<ConditionConfiguration> {

		@Override
		protected ConditionConfiguration createDTO() {
			return new ConditionConfiguration();
		}

		@Override
		protected ConditionConfiguration[] createDTOArray(int size) {
			return new ConditionConfiguration[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "conditions")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ConditionConfiguration conditionConfiguration,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "conditions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Condition[] conditionsArray =
						new Condition[jsonParserFieldValues.length];

					for (int i = 0; i < conditionsArray.length; i++) {
						conditionsArray[i] = ConditionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					conditionConfiguration.setConditions(conditionsArray);
				}
			}
		}

	}

	private static String _escape(Object object) {
		String string = String.valueOf(object);

		for (String[] strings : BaseJSONParser.JSON_ESCAPE_STRINGS) {
			string = string.replace(strings[0], strings[1]);
		}

		return string;
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(entry.getKey());
			sb.append("\": ");

			Object value = entry.getValue();

			sb.append(_toJSON(value));

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static String _toJSON(Object value) {
		if (value instanceof Map) {
			return _toJSON((Map)value);
		}

		Class<?> clazz = value.getClass();

		if (clazz.isArray()) {
			StringBuilder sb = new StringBuilder("[");

			Object[] values = (Object[])value;

			for (int i = 0; i < values.length; i++) {
				sb.append(_toJSON(values[i]));

				if ((i + 1) < values.length) {
					sb.append(", ");
				}
			}

			sb.append("]");

			return sb.toString();
		}

		if (value instanceof String) {
			return "\"" + _escape(value) + "\"";
		}

		return String.valueOf(value);
	}

}