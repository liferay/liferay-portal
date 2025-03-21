/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageSpecification;
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
public class PageSpecificationSerDes {

	public static PageSpecification toDTO(String json) {
		PageSpecificationJSONParser pageSpecificationJSONParser =
			new PageSpecificationJSONParser();

		return pageSpecificationJSONParser.parseToDTO(json);
	}

	public static PageSpecification[] toDTOs(String json) {
		PageSpecificationJSONParser pageSpecificationJSONParser =
			new PageSpecificationJSONParser();

		return pageSpecificationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PageSpecification pageSpecification) {
		if (pageSpecification == null) {
			return "null";
		}

		PageSpecification.Type type = pageSpecification.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("ContentPageSpecification")) {
				return ContentPageSpecificationSerDes.toJSON(
					(ContentPageSpecification)pageSpecification);
			}

			if (typeString.equals("WidgetPageSpecification")) {
				return WidgetPageSpecificationSerDes.toJSON(
					(WidgetPageSpecification)pageSpecification);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		PageSpecificationJSONParser pageSpecificationJSONParser =
			new PageSpecificationJSONParser();

		return pageSpecificationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PageSpecification pageSpecification) {

		if (pageSpecification == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageSpecification.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(pageSpecification.getExternalReferenceCode()));
		}

		if (pageSpecification.getSettings() == null) {
			map.put("settings", null);
		}
		else {
			map.put(
				"settings", String.valueOf(pageSpecification.getSettings()));
		}

		if (pageSpecification.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(pageSpecification.getStatus()));
		}

		if (pageSpecification.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(pageSpecification.getType()));
		}

		return map;
	}

	public static class PageSpecificationJSONParser
		extends BaseJSONParser<PageSpecification> {

		@Override
		protected PageSpecification createDTO() {
			return null;
		}

		@Override
		protected PageSpecification[] createDTOArray(int size) {
			return new PageSpecification[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "settings")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		public PageSpecification parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("ContentPageSpecification")) {
					return ContentPageSpecification.toDTO(json);
				}

				if (typeString.equals("WidgetPageSpecification")) {
					return WidgetPageSpecification.toDTO(json);
				}

				throw new IllegalArgumentException(
					"Unknown type " + typeString);
			}
			else {
				throw new IllegalArgumentException("Missing type parameter");
			}
		}

		@Override
		protected void setField(
			PageSpecification pageSpecification, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					pageSpecification.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "settings")) {
				if (jsonParserFieldValue != null) {
					pageSpecification.setSettings(
						SettingsSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					pageSpecification.setStatus(
						PageSpecification.Status.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					pageSpecification.setType(
						PageSpecification.Type.create(
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