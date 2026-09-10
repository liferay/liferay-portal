/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FragmentEditableElement;
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
public class FragmentEditableElementSerDes {

	public static FragmentEditableElement toDTO(String json) {
		FragmentEditableElementJSONParser fragmentEditableElementJSONParser =
			new FragmentEditableElementJSONParser();

		return fragmentEditableElementJSONParser.parseToDTO(json);
	}

	public static FragmentEditableElement[] toDTOs(String json) {
		FragmentEditableElementJSONParser fragmentEditableElementJSONParser =
			new FragmentEditableElementJSONParser();

		return fragmentEditableElementJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		FragmentEditableElement fragmentEditableElement) {

		if (fragmentEditableElement == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (fragmentEditableElement.getFragmentEditableElementValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentEditableElementValue\": ");

			sb.append(
				String.valueOf(
					fragmentEditableElement.getFragmentEditableElementValue()));
		}

		if (fragmentEditableElement.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(fragmentEditableElement.getId()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentEditableElementJSONParser fragmentEditableElementJSONParser =
			new FragmentEditableElementJSONParser();

		return fragmentEditableElementJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FragmentEditableElement fragmentEditableElement) {

		if (fragmentEditableElement == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fragmentEditableElement.getFragmentEditableElementValue() == null) {
			map.put("fragmentEditableElementValue", null);
		}
		else {
			map.put(
				"fragmentEditableElementValue",
				String.valueOf(
					fragmentEditableElement.getFragmentEditableElementValue()));
		}

		if (fragmentEditableElement.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(fragmentEditableElement.getId()));
		}

		return map;
	}

	public static class FragmentEditableElementJSONParser
		extends BaseJSONParser<FragmentEditableElement> {

		@Override
		protected FragmentEditableElement createDTO() {
			return new FragmentEditableElement();
		}

		@Override
		protected FragmentEditableElement[] createDTOArray(int size) {
			return new FragmentEditableElement[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "fragmentEditableElementValue")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FragmentEditableElement fragmentEditableElement,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "fragmentEditableElementValue")) {

				if (jsonParserFieldValue != null) {
					fragmentEditableElement.setFragmentEditableElementValue(
						FragmentEditableElementValueSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					fragmentEditableElement.setId((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-247929944