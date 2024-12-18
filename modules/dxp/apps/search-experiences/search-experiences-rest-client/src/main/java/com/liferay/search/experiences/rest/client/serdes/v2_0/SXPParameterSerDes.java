/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v2_0;

import com.liferay.search.experiences.rest.client.dto.v2_0.SXPParameter;
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
public class SXPParameterSerDes {

	public static SXPParameter toDTO(String json) {
		SXPParameterJSONParser sxpParameterJSONParser =
			new SXPParameterJSONParser();

		return sxpParameterJSONParser.parseToDTO(json);
	}

	public static SXPParameter[] toDTOs(String json) {
		SXPParameterJSONParser sxpParameterJSONParser =
			new SXPParameterJSONParser();

		return sxpParameterJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SXPParameter sxpParameter) {
		if (sxpParameter == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (sxpParameter.getDefaultValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultValue\": ");

			if (sxpParameter.getDefaultValue() instanceof String) {
				sb.append("\"");
				sb.append((String)sxpParameter.getDefaultValue());
				sb.append("\"");
			}
			else {
				sb.append(sxpParameter.getDefaultValue());
			}
		}

		if (sxpParameter.getFormat() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"format\": ");

			sb.append("\"");

			sb.append(_escape(sxpParameter.getFormat()));

			sb.append("\"");
		}

		if (sxpParameter.getMax() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"max\": ");

			if (sxpParameter.getMax() instanceof String) {
				sb.append("\"");
				sb.append((String)sxpParameter.getMax());
				sb.append("\"");
			}
			else {
				sb.append(sxpParameter.getMax());
			}
		}

		if (sxpParameter.getMin() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"min\": ");

			if (sxpParameter.getMin() instanceof String) {
				sb.append("\"");
				sb.append((String)sxpParameter.getMin());
				sb.append("\"");
			}
			else {
				sb.append(sxpParameter.getMin());
			}
		}

		if (sxpParameter.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(sxpParameter.getType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SXPParameterJSONParser sxpParameterJSONParser =
			new SXPParameterJSONParser();

		return sxpParameterJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SXPParameter sxpParameter) {
		if (sxpParameter == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (sxpParameter.getDefaultValue() == null) {
			map.put("defaultValue", null);
		}
		else {
			map.put(
				"defaultValue", String.valueOf(sxpParameter.getDefaultValue()));
		}

		if (sxpParameter.getFormat() == null) {
			map.put("format", null);
		}
		else {
			map.put("format", String.valueOf(sxpParameter.getFormat()));
		}

		if (sxpParameter.getMax() == null) {
			map.put("max", null);
		}
		else {
			map.put("max", String.valueOf(sxpParameter.getMax()));
		}

		if (sxpParameter.getMin() == null) {
			map.put("min", null);
		}
		else {
			map.put("min", String.valueOf(sxpParameter.getMin()));
		}

		if (sxpParameter.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(sxpParameter.getType()));
		}

		return map;
	}

	public static class SXPParameterJSONParser
		extends BaseJSONParser<SXPParameter> {

		@Override
		protected SXPParameter createDTO() {
			return new SXPParameter();
		}

		@Override
		protected SXPParameter[] createDTOArray(int size) {
			return new SXPParameter[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "defaultValue")) {
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
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SXPParameter sxpParameter, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "defaultValue")) {
				if (jsonParserFieldValue != null) {
					sxpParameter.setDefaultValue((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "format")) {
				if (jsonParserFieldValue != null) {
					sxpParameter.setFormat((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "max")) {
				if (jsonParserFieldValue != null) {
					sxpParameter.setMax((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "min")) {
				if (jsonParserFieldValue != null) {
					sxpParameter.setMin((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					sxpParameter.setType(
						SXPParameter.Type.create((String)jsonParserFieldValue));
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