/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.Option;
import com.liferay.search.experiences.rest.client.dto.v1_0.TypeOptions;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class TypeOptionsSerDes {

	public static TypeOptions toDTO(String json) {
		TypeOptionsJSONParser typeOptionsJSONParser =
			new TypeOptionsJSONParser();

		return typeOptionsJSONParser.parseToDTO(json);
	}

	public static TypeOptions[] toDTOs(String json) {
		TypeOptionsJSONParser typeOptionsJSONParser =
			new TypeOptionsJSONParser();

		return typeOptionsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TypeOptions typeOptions) {
		if (typeOptions == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (typeOptions.getBoost() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"boost\": ");

			sb.append(typeOptions.getBoost());
		}

		if (typeOptions.getFormat() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"format\": ");

			sb.append("\"");

			sb.append(_escape(typeOptions.getFormat()));

			sb.append("\"");
		}

		if (typeOptions.getMax() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"max\": ");

			if (typeOptions.getMax() instanceof String) {
				sb.append("\"");
				sb.append((String)typeOptions.getMax());
				sb.append("\"");
			}
			else {
				sb.append(typeOptions.getMax());
			}
		}

		if (typeOptions.getMin() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"min\": ");

			if (typeOptions.getMin() instanceof String) {
				sb.append("\"");
				sb.append((String)typeOptions.getMin());
				sb.append("\"");
			}
			else {
				sb.append(typeOptions.getMin());
			}
		}

		if (typeOptions.getNullable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nullable\": ");

			sb.append(typeOptions.getNullable());
		}

		if (typeOptions.getOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"options\": ");

			sb.append("[");

			for (int i = 0; i < typeOptions.getOptions().length; i++) {
				sb.append(String.valueOf(typeOptions.getOptions()[i]));

				if ((i + 1) < typeOptions.getOptions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (typeOptions.getRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(typeOptions.getRequired());
		}

		if (typeOptions.getStep() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"step\": ");

			if (typeOptions.getStep() instanceof String) {
				sb.append("\"");
				sb.append((String)typeOptions.getStep());
				sb.append("\"");
			}
			else {
				sb.append(typeOptions.getStep());
			}
		}

		if (typeOptions.getUnit() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unit\": ");

			sb.append("\"");

			sb.append(_escape(typeOptions.getUnit()));

			sb.append("\"");
		}

		if (typeOptions.getUnitSuffix() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unitSuffix\": ");

			sb.append("\"");

			sb.append(_escape(typeOptions.getUnitSuffix()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TypeOptionsJSONParser typeOptionsJSONParser =
			new TypeOptionsJSONParser();

		return typeOptionsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(TypeOptions typeOptions) {
		if (typeOptions == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (typeOptions.getBoost() == null) {
			map.put("boost", null);
		}
		else {
			map.put("boost", String.valueOf(typeOptions.getBoost()));
		}

		if (typeOptions.getFormat() == null) {
			map.put("format", null);
		}
		else {
			map.put("format", String.valueOf(typeOptions.getFormat()));
		}

		if (typeOptions.getMax() == null) {
			map.put("max", null);
		}
		else {
			map.put("max", String.valueOf(typeOptions.getMax()));
		}

		if (typeOptions.getMin() == null) {
			map.put("min", null);
		}
		else {
			map.put("min", String.valueOf(typeOptions.getMin()));
		}

		if (typeOptions.getNullable() == null) {
			map.put("nullable", null);
		}
		else {
			map.put("nullable", String.valueOf(typeOptions.getNullable()));
		}

		if (typeOptions.getOptions() == null) {
			map.put("options", null);
		}
		else {
			map.put("options", String.valueOf(typeOptions.getOptions()));
		}

		if (typeOptions.getRequired() == null) {
			map.put("required", null);
		}
		else {
			map.put("required", String.valueOf(typeOptions.getRequired()));
		}

		if (typeOptions.getStep() == null) {
			map.put("step", null);
		}
		else {
			map.put("step", String.valueOf(typeOptions.getStep()));
		}

		if (typeOptions.getUnit() == null) {
			map.put("unit", null);
		}
		else {
			map.put("unit", String.valueOf(typeOptions.getUnit()));
		}

		if (typeOptions.getUnitSuffix() == null) {
			map.put("unitSuffix", null);
		}
		else {
			map.put("unitSuffix", String.valueOf(typeOptions.getUnitSuffix()));
		}

		return map;
	}

	public static class TypeOptionsJSONParser
		extends BaseJSONParser<TypeOptions> {

		@Override
		protected TypeOptions createDTO() {
			return new TypeOptions();
		}

		@Override
		protected TypeOptions[] createDTOArray(int size) {
			return new TypeOptions[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "boost")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "format")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "max")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "min")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "nullable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "step")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "unit")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "unitSuffix")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TypeOptions typeOptions, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "boost")) {
				if (jsonParserFieldValue != null) {
					typeOptions.setBoost((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "format")) {
				if (jsonParserFieldValue != null) {
					typeOptions.setFormat((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "max")) {
				if (jsonParserFieldValue != null) {
					typeOptions.setMax((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "min")) {
				if (jsonParserFieldValue != null) {
					typeOptions.setMin((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "nullable")) {
				if (jsonParserFieldValue != null) {
					typeOptions.setNullable((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "options")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Option[] optionsArray =
						new Option[jsonParserFieldValues.length];

					for (int i = 0; i < optionsArray.length; i++) {
						optionsArray[i] = OptionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					typeOptions.setOptions(optionsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				if (jsonParserFieldValue != null) {
					typeOptions.setRequired((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "step")) {
				if (jsonParserFieldValue != null) {
					typeOptions.setStep((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "unit")) {
				if (jsonParserFieldValue != null) {
					typeOptions.setUnit((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "unitSuffix")) {
				if (jsonParserFieldValue != null) {
					typeOptions.setUnitSuffix((String)jsonParserFieldValue);
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
		if (value == null) {
			return "null";
		}

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