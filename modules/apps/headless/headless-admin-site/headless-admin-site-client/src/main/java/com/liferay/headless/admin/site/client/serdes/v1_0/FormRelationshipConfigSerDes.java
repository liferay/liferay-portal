/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FormRelationshipConfig;
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
public class FormRelationshipConfigSerDes {

	public static FormRelationshipConfig toDTO(String json) {
		FormRelationshipConfigJSONParser formRelationshipConfigJSONParser =
			new FormRelationshipConfigJSONParser();

		return formRelationshipConfigJSONParser.parseToDTO(json);
	}

	public static FormRelationshipConfig[] toDTOs(String json) {
		FormRelationshipConfigJSONParser formRelationshipConfigJSONParser =
			new FormRelationshipConfigJSONParser();

		return formRelationshipConfigJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FormRelationshipConfig formRelationshipConfig) {
		if (formRelationshipConfig == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (formRelationshipConfig.getButtonLabelFragmentInlineValue() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"buttonLabelFragmentInlineValue\": ");

			sb.append(
				String.valueOf(
					formRelationshipConfig.
						getButtonLabelFragmentInlineValue()));
		}

		if (formRelationshipConfig.getContentType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentType\": ");

			sb.append("\"");

			sb.append(_escape(formRelationshipConfig.getContentType()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FormRelationshipConfigJSONParser formRelationshipConfigJSONParser =
			new FormRelationshipConfigJSONParser();

		return formRelationshipConfigJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FormRelationshipConfig formRelationshipConfig) {

		if (formRelationshipConfig == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (formRelationshipConfig.getButtonLabelFragmentInlineValue() ==
				null) {

			map.put("buttonLabelFragmentInlineValue", null);
		}
		else {
			map.put(
				"buttonLabelFragmentInlineValue",
				String.valueOf(
					formRelationshipConfig.
						getButtonLabelFragmentInlineValue()));
		}

		if (formRelationshipConfig.getContentType() == null) {
			map.put("contentType", null);
		}
		else {
			map.put(
				"contentType",
				String.valueOf(formRelationshipConfig.getContentType()));
		}

		return map;
	}

	public static class FormRelationshipConfigJSONParser
		extends BaseJSONParser<FormRelationshipConfig> {

		@Override
		protected FormRelationshipConfig createDTO() {
			return new FormRelationshipConfig();
		}

		@Override
		protected FormRelationshipConfig[] createDTOArray(int size) {
			return new FormRelationshipConfig[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "buttonLabelFragmentInlineValue")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "contentType")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FormRelationshipConfig formRelationshipConfig,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "buttonLabelFragmentInlineValue")) {

				if (jsonParserFieldValue != null) {
					formRelationshipConfig.setButtonLabelFragmentInlineValue(
						FragmentInlineValueSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentType")) {
				if (jsonParserFieldValue != null) {
					formRelationshipConfig.setContentType(
						(String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:1402973592