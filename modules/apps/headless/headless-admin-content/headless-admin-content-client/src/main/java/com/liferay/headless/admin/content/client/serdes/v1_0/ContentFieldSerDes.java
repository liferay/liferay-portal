/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.client.serdes.v1_0;

import com.liferay.headless.admin.content.client.dto.v1_0.ContentField;
import com.liferay.headless.admin.content.client.dto.v1_0.ContentFieldValue;
import com.liferay.headless.admin.content.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ContentFieldSerDes {

	public static ContentField toDTO(String json) {
		ContentFieldJSONParser contentFieldJSONParser =
			new ContentFieldJSONParser();

		return contentFieldJSONParser.parseToDTO(json);
	}

	public static ContentField[] toDTOs(String json) {
		ContentFieldJSONParser contentFieldJSONParser =
			new ContentFieldJSONParser();

		return contentFieldJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ContentField contentField) {
		if (contentField == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (contentField.getContentFieldValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentFieldValue\": ");

			sb.append(contentField.getContentFieldValue());
		}

		if (contentField.getContentFieldValue_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentFieldValue_i18n\": ");

			sb.append(_toJSON(contentField.getContentFieldValue_i18n()));
		}

		if (contentField.getDataType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataType\": ");

			sb.append("\"");

			sb.append(_escape(contentField.getDataType()));

			sb.append("\"");
		}

		if (contentField.getInputControl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inputControl\": ");

			sb.append("\"");

			sb.append(_escape(contentField.getInputControl()));

			sb.append("\"");
		}

		if (contentField.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(contentField.getLabel()));

			sb.append("\"");
		}

		if (contentField.getLabel_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label_i18n\": ");

			sb.append(_toJSON(contentField.getLabel_i18n()));
		}

		if (contentField.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(contentField.getName()));

			sb.append("\"");
		}

		if (contentField.getNestedContentFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nestedContentFields\": ");

			sb.append("[");

			for (int i = 0; i < contentField.getNestedContentFields().length;
				 i++) {

				sb.append(contentField.getNestedContentFields()[i]);

				if ((i + 1) < contentField.getNestedContentFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (contentField.getRepeatable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"repeatable\": ");

			sb.append(contentField.getRepeatable());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ContentFieldJSONParser contentFieldJSONParser =
			new ContentFieldJSONParser();

		return contentFieldJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ContentField contentField) {
		if (contentField == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (contentField.getContentFieldValue() == null) {
			map.put("contentFieldValue", null);
		}
		else {
			map.put(
				"contentFieldValue",
				String.valueOf(contentField.getContentFieldValue()));
		}

		if (contentField.getContentFieldValue_i18n() == null) {
			map.put("contentFieldValue_i18n", null);
		}
		else {
			map.put(
				"contentFieldValue_i18n",
				String.valueOf(contentField.getContentFieldValue_i18n()));
		}

		if (contentField.getDataType() == null) {
			map.put("dataType", null);
		}
		else {
			map.put("dataType", String.valueOf(contentField.getDataType()));
		}

		if (contentField.getInputControl() == null) {
			map.put("inputControl", null);
		}
		else {
			map.put(
				"inputControl", String.valueOf(contentField.getInputControl()));
		}

		if (contentField.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(contentField.getLabel()));
		}

		if (contentField.getLabel_i18n() == null) {
			map.put("label_i18n", null);
		}
		else {
			map.put("label_i18n", String.valueOf(contentField.getLabel_i18n()));
		}

		if (contentField.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(contentField.getName()));
		}

		if (contentField.getNestedContentFields() == null) {
			map.put("nestedContentFields", null);
		}
		else {
			map.put(
				"nestedContentFields",
				String.valueOf(contentField.getNestedContentFields()));
		}

		if (contentField.getRepeatable() == null) {
			map.put("repeatable", null);
		}
		else {
			map.put("repeatable", String.valueOf(contentField.getRepeatable()));
		}

		return map;
	}

	public static class ContentFieldJSONParser
		extends BaseJSONParser<ContentField> {

		@Override
		protected ContentField createDTO() {
			return new ContentField();
		}

		@Override
		protected ContentField[] createDTOArray(int size) {
			return new ContentField[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "contentFieldValue")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "contentFieldValue_i18n")) {

				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "dataType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "inputControl")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "nestedContentFields")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "repeatable")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ContentField contentField, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "contentFieldValue")) {
				if (jsonParserFieldValue != null) {
					contentField.setContentFieldValue(
						ContentFieldValueSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "contentFieldValue_i18n")) {

				if (jsonParserFieldValue != null) {
					contentField.setContentFieldValue_i18n(
						(Map<String, ContentFieldValue>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataType")) {
				if (jsonParserFieldValue != null) {
					contentField.setDataType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "inputControl")) {
				if (jsonParserFieldValue != null) {
					contentField.setInputControl((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					contentField.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label_i18n")) {
				if (jsonParserFieldValue != null) {
					contentField.setLabel_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					contentField.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "nestedContentFields")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ContentField[] nestedContentFieldsArray =
						new ContentField[jsonParserFieldValues.length];

					for (int i = 0; i < nestedContentFieldsArray.length; i++) {
						nestedContentFieldsArray[i] = ContentFieldSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					contentField.setNestedContentFields(
						nestedContentFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "repeatable")) {
				if (jsonParserFieldValue != null) {
					contentField.setRepeatable((Boolean)jsonParserFieldValue);
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