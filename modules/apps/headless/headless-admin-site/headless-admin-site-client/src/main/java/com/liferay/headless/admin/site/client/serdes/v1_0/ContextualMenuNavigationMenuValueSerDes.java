/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ContextualMenuNavigationMenuValue;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ContextualMenuNavigationMenuValueSerDes {

	public static ContextualMenuNavigationMenuValue toDTO(String json) {
		ContextualMenuNavigationMenuValueJSONParser
			contextualMenuNavigationMenuValueJSONParser =
				new ContextualMenuNavigationMenuValueJSONParser();

		return contextualMenuNavigationMenuValueJSONParser.parseToDTO(json);
	}

	public static ContextualMenuNavigationMenuValue[] toDTOs(String json) {
		ContextualMenuNavigationMenuValueJSONParser
			contextualMenuNavigationMenuValueJSONParser =
				new ContextualMenuNavigationMenuValueJSONParser();

		return contextualMenuNavigationMenuValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ContextualMenuNavigationMenuValue contextualMenuNavigationMenuValue) {

		if (contextualMenuNavigationMenuValue == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (contextualMenuNavigationMenuValue.getContextualMenuType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contextualMenuType\": ");

			sb.append("\"");
			sb.append(
				contextualMenuNavigationMenuValue.getContextualMenuType());
			sb.append("\"");
		}

		if (contextualMenuNavigationMenuValue.getNavigationMenuType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"navigationMenuType\": ");

			sb.append("\"");
			sb.append(
				contextualMenuNavigationMenuValue.getNavigationMenuType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ContextualMenuNavigationMenuValueJSONParser
			contextualMenuNavigationMenuValueJSONParser =
				new ContextualMenuNavigationMenuValueJSONParser();

		return contextualMenuNavigationMenuValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ContextualMenuNavigationMenuValue contextualMenuNavigationMenuValue) {

		if (contextualMenuNavigationMenuValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (contextualMenuNavigationMenuValue.getContextualMenuType() == null) {
			map.put("contextualMenuType", null);
		}
		else {
			map.put(
				"contextualMenuType",
				String.valueOf(
					contextualMenuNavigationMenuValue.getContextualMenuType()));
		}

		if (contextualMenuNavigationMenuValue.getNavigationMenuType() == null) {
			map.put("navigationMenuType", null);
		}
		else {
			map.put(
				"navigationMenuType",
				String.valueOf(
					contextualMenuNavigationMenuValue.getNavigationMenuType()));
		}

		return map;
	}

	public static class ContextualMenuNavigationMenuValueJSONParser
		extends BaseJSONParser<ContextualMenuNavigationMenuValue> {

		@Override
		protected ContextualMenuNavigationMenuValue createDTO() {
			return new ContextualMenuNavigationMenuValue();
		}

		@Override
		protected ContextualMenuNavigationMenuValue[] createDTOArray(int size) {
			return new ContextualMenuNavigationMenuValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "contextualMenuType")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "navigationMenuType")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ContextualMenuNavigationMenuValue contextualMenuNavigationMenuValue,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "contextualMenuType")) {
				if (jsonParserFieldValue != null) {
					contextualMenuNavigationMenuValue.setContextualMenuType(
						ContextualMenuNavigationMenuValue.ContextualMenuType.
							create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "navigationMenuType")) {

				if (jsonParserFieldValue != null) {
					contextualMenuNavigationMenuValue.setNavigationMenuType(
						ContextualMenuNavigationMenuValue.NavigationMenuType.
							create((String)jsonParserFieldValue));
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:-1172197145