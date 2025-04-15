/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.OptionValue;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class OptionValueSerDes {

	public static OptionValue toDTO(String json) {
		OptionValueJSONParser optionValueJSONParser =
			new OptionValueJSONParser();

		return optionValueJSONParser.parseToDTO(json);
	}

	public static OptionValue[] toDTOs(String json) {
		OptionValueJSONParser optionValueJSONParser =
			new OptionValueJSONParser();

		return optionValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(OptionValue optionValue) {
		if (optionValue == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (optionValue.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(optionValue.getActions()));
		}

		if (optionValue.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < optionValue.getCustomFields().length; i++) {
				sb.append(optionValue.getCustomFields()[i]);

				if ((i + 1) < optionValue.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (optionValue.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(optionValue.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (optionValue.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(optionValue.getId());
		}

		if (optionValue.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(optionValue.getKey()));

			sb.append("\"");
		}

		if (optionValue.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(optionValue.getName()));
		}

		if (optionValue.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(optionValue.getPriority());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OptionValueJSONParser optionValueJSONParser =
			new OptionValueJSONParser();

		return optionValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(OptionValue optionValue) {
		if (optionValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (optionValue.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(optionValue.getActions()));
		}

		if (optionValue.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields", String.valueOf(optionValue.getCustomFields()));
		}

		if (optionValue.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(optionValue.getExternalReferenceCode()));
		}

		if (optionValue.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(optionValue.getId()));
		}

		if (optionValue.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(optionValue.getKey()));
		}

		if (optionValue.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(optionValue.getName()));
		}

		if (optionValue.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(optionValue.getPriority()));
		}

		return map;
	}

	public static class OptionValueJSONParser
		extends BaseJSONParser<OptionValue> {

		@Override
		protected OptionValue createDTO() {
			return new OptionValue();
		}

		@Override
		protected OptionValue[] createDTOArray(int size) {
			return new OptionValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			OptionValue optionValue, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					optionValue.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.commerce.admin.catalog.client.custom.
						field.CustomField[] customFieldsArray = new
						com.liferay.headless.commerce.admin.catalog.client.
							custom.field.CustomField
							[jsonParserFieldValues.length];

					for (int i = 0; i < customFieldsArray.length; i++) {
						customFieldsArray[i] =
							com.liferay.headless.commerce.admin.catalog.client.
								custom.field.CustomField.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					optionValue.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					optionValue.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					optionValue.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					optionValue.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					optionValue.setName(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					optionValue.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
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