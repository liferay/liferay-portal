/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.ElementInstance;
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
public class ElementInstanceSerDes {

	public static ElementInstance toDTO(String json) {
		ElementInstanceJSONParser elementInstanceJSONParser =
			new ElementInstanceJSONParser();

		return elementInstanceJSONParser.parseToDTO(json);
	}

	public static ElementInstance[] toDTOs(String json) {
		ElementInstanceJSONParser elementInstanceJSONParser =
			new ElementInstanceJSONParser();

		return elementInstanceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ElementInstance elementInstance) {
		if (elementInstance == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (elementInstance.getConfigurationEntry() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"configurationEntry\": ");

			sb.append(String.valueOf(elementInstance.getConfigurationEntry()));
		}

		if (elementInstance.getSxpElement() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sxpElement\": ");

			sb.append(String.valueOf(elementInstance.getSxpElement()));
		}

		if (elementInstance.getSxpElementId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sxpElementId\": ");

			sb.append(elementInstance.getSxpElementId());
		}

		if (elementInstance.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append(elementInstance.getType());
		}

		if (elementInstance.getUiConfigurationValues() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uiConfigurationValues\": ");

			sb.append(_toJSON(elementInstance.getUiConfigurationValues()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ElementInstanceJSONParser elementInstanceJSONParser =
			new ElementInstanceJSONParser();

		return elementInstanceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ElementInstance elementInstance) {
		if (elementInstance == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (elementInstance.getConfigurationEntry() == null) {
			map.put("configurationEntry", null);
		}
		else {
			map.put(
				"configurationEntry",
				String.valueOf(elementInstance.getConfigurationEntry()));
		}

		if (elementInstance.getSxpElement() == null) {
			map.put("sxpElement", null);
		}
		else {
			map.put(
				"sxpElement", String.valueOf(elementInstance.getSxpElement()));
		}

		if (elementInstance.getSxpElementId() == null) {
			map.put("sxpElementId", null);
		}
		else {
			map.put(
				"sxpElementId",
				String.valueOf(elementInstance.getSxpElementId()));
		}

		if (elementInstance.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(elementInstance.getType()));
		}

		if (elementInstance.getUiConfigurationValues() == null) {
			map.put("uiConfigurationValues", null);
		}
		else {
			map.put(
				"uiConfigurationValues",
				String.valueOf(elementInstance.getUiConfigurationValues()));
		}

		return map;
	}

	public static class ElementInstanceJSONParser
		extends BaseJSONParser<ElementInstance> {

		@Override
		protected ElementInstance createDTO() {
			return new ElementInstance();
		}

		@Override
		protected ElementInstance[] createDTOArray(int size) {
			return new ElementInstance[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "configurationEntry")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sxpElement")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sxpElementId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "uiConfigurationValues")) {

				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			ElementInstance elementInstance, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "configurationEntry")) {
				if (jsonParserFieldValue != null) {
					elementInstance.setConfigurationEntry(
						ConfigurationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sxpElement")) {
				if (jsonParserFieldValue != null) {
					elementInstance.setSxpElement(
						SXPElementSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sxpElementId")) {
				if (jsonParserFieldValue != null) {
					elementInstance.setSxpElementId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					elementInstance.setType(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "uiConfigurationValues")) {

				if (jsonParserFieldValue != null) {
					elementInstance.setUiConfigurationValues(
						(Map<String, Object>)jsonParserFieldValue);
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