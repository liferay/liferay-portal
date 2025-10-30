/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.TemplateListStyle;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class TemplateListStyleSerDes {

	public static TemplateListStyle toDTO(String json) {
		TemplateListStyleJSONParser templateListStyleJSONParser =
			new TemplateListStyleJSONParser();

		return templateListStyleJSONParser.parseToDTO(json);
	}

	public static TemplateListStyle[] toDTOs(String json) {
		TemplateListStyleJSONParser templateListStyleJSONParser =
			new TemplateListStyleJSONParser();

		return templateListStyleJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TemplateListStyle templateListStyle) {
		if (templateListStyle == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (templateListStyle.getListItemStyleClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listItemStyleClassName\": ");

			sb.append("\"");

			sb.append(_escape(templateListStyle.getListItemStyleClassName()));

			sb.append("\"");
		}

		if (templateListStyle.getListStyleClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listStyleClassName\": ");

			sb.append("\"");

			sb.append(_escape(templateListStyle.getListStyleClassName()));

			sb.append("\"");
		}

		if (templateListStyle.getTemplateKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"templateKey\": ");

			sb.append("\"");

			sb.append(_escape(templateListStyle.getTemplateKey()));

			sb.append("\"");
		}

		if (templateListStyle.getCollectionDisplayListStyleType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionDisplayListStyleType\": ");

			sb.append("\"");
			sb.append(templateListStyle.getCollectionDisplayListStyleType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TemplateListStyleJSONParser templateListStyleJSONParser =
			new TemplateListStyleJSONParser();

		return templateListStyleJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		TemplateListStyle templateListStyle) {

		if (templateListStyle == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (templateListStyle.getListItemStyleClassName() == null) {
			map.put("listItemStyleClassName", null);
		}
		else {
			map.put(
				"listItemStyleClassName",
				String.valueOf(templateListStyle.getListItemStyleClassName()));
		}

		if (templateListStyle.getListStyleClassName() == null) {
			map.put("listStyleClassName", null);
		}
		else {
			map.put(
				"listStyleClassName",
				String.valueOf(templateListStyle.getListStyleClassName()));
		}

		if (templateListStyle.getTemplateKey() == null) {
			map.put("templateKey", null);
		}
		else {
			map.put(
				"templateKey",
				String.valueOf(templateListStyle.getTemplateKey()));
		}

		if (templateListStyle.getCollectionDisplayListStyleType() == null) {
			map.put("collectionDisplayListStyleType", null);
		}
		else {
			map.put(
				"collectionDisplayListStyleType",
				String.valueOf(
					templateListStyle.getCollectionDisplayListStyleType()));
		}

		return map;
	}

	public static class TemplateListStyleJSONParser
		extends BaseJSONParser<TemplateListStyle> {

		@Override
		protected TemplateListStyle createDTO() {
			return new TemplateListStyle();
		}

		@Override
		protected TemplateListStyle[] createDTOArray(int size) {
			return new TemplateListStyle[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "listItemStyleClassName")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "listStyleClassName")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "templateKey")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"collectionDisplayListStyleType")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TemplateListStyle templateListStyle, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "listItemStyleClassName")) {
				if (jsonParserFieldValue != null) {
					templateListStyle.setListItemStyleClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "listStyleClassName")) {

				if (jsonParserFieldValue != null) {
					templateListStyle.setListStyleClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "templateKey")) {
				if (jsonParserFieldValue != null) {
					templateListStyle.setTemplateKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"collectionDisplayListStyleType")) {

				if (jsonParserFieldValue != null) {
					templateListStyle.setCollectionDisplayListStyleType(
						TemplateListStyle.CollectionDisplayListStyleType.create(
							(String)jsonParserFieldValue));
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