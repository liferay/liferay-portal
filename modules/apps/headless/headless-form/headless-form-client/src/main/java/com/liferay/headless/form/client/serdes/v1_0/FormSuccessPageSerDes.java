/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.form.client.serdes.v1_0;

import com.liferay.headless.form.client.dto.v1_0.FormSuccessPage;
import com.liferay.headless.form.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FormSuccessPageSerDes {

	public static FormSuccessPage toDTO(String json) {
		FormSuccessPageJSONParser formSuccessPageJSONParser =
			new FormSuccessPageJSONParser();

		return formSuccessPageJSONParser.parseToDTO(json);
	}

	public static FormSuccessPage[] toDTOs(String json) {
		FormSuccessPageJSONParser formSuccessPageJSONParser =
			new FormSuccessPageJSONParser();

		return formSuccessPageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FormSuccessPage formSuccessPage) {
		if (formSuccessPage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (formSuccessPage.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(formSuccessPage.getDescription()));

			sb.append("\"");
		}

		if (formSuccessPage.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(formSuccessPage.getDescription_i18n()));
		}

		if (formSuccessPage.getHeadline() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"headline\": ");

			sb.append("\"");

			sb.append(_escape(formSuccessPage.getHeadline()));

			sb.append("\"");
		}

		if (formSuccessPage.getHeadline_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"headline_i18n\": ");

			sb.append(_toJSON(formSuccessPage.getHeadline_i18n()));
		}

		if (formSuccessPage.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(formSuccessPage.getId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FormSuccessPageJSONParser formSuccessPageJSONParser =
			new FormSuccessPageJSONParser();

		return formSuccessPageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(FormSuccessPage formSuccessPage) {
		if (formSuccessPage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (formSuccessPage.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(formSuccessPage.getDescription()));
		}

		if (formSuccessPage.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(formSuccessPage.getDescription_i18n()));
		}

		if (formSuccessPage.getHeadline() == null) {
			map.put("headline", null);
		}
		else {
			map.put("headline", String.valueOf(formSuccessPage.getHeadline()));
		}

		if (formSuccessPage.getHeadline_i18n() == null) {
			map.put("headline_i18n", null);
		}
		else {
			map.put(
				"headline_i18n",
				String.valueOf(formSuccessPage.getHeadline_i18n()));
		}

		if (formSuccessPage.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(formSuccessPage.getId()));
		}

		return map;
	}

	public static class FormSuccessPageJSONParser
		extends BaseJSONParser<FormSuccessPage> {

		@Override
		protected FormSuccessPage createDTO() {
			return new FormSuccessPage();
		}

		@Override
		protected FormSuccessPage[] createDTOArray(int size) {
			return new FormSuccessPage[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "headline")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "headline_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FormSuccessPage formSuccessPage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					formSuccessPage.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					formSuccessPage.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "headline")) {
				if (jsonParserFieldValue != null) {
					formSuccessPage.setHeadline((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "headline_i18n")) {
				if (jsonParserFieldValue != null) {
					formSuccessPage.setHeadline_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					formSuccessPage.setId(
						Long.valueOf((String)jsonParserFieldValue));
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