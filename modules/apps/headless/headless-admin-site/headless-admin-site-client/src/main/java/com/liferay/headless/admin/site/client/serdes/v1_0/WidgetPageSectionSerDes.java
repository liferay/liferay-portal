/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageSection;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageWidgetInstance;
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
public class WidgetPageSectionSerDes {

	public static WidgetPageSection toDTO(String json) {
		WidgetPageSectionJSONParser widgetPageSectionJSONParser =
			new WidgetPageSectionJSONParser();

		return widgetPageSectionJSONParser.parseToDTO(json);
	}

	public static WidgetPageSection[] toDTOs(String json) {
		WidgetPageSectionJSONParser widgetPageSectionJSONParser =
			new WidgetPageSectionJSONParser();

		return widgetPageSectionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WidgetPageSection widgetPageSection) {
		if (widgetPageSection == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (widgetPageSection.getCustomizable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customizable\": ");

			sb.append(widgetPageSection.getCustomizable());
		}

		if (widgetPageSection.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(widgetPageSection.getId()));

			sb.append("\"");
		}

		if (widgetPageSection.getWidgetPageWidgetInstances() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetPageWidgetInstances\": ");

			sb.append("[");

			for (int i = 0;
				 i < widgetPageSection.getWidgetPageWidgetInstances().length;
				 i++) {

				sb.append(
					String.valueOf(
						widgetPageSection.getWidgetPageWidgetInstances()[i]));

				if ((i + 1) <
						widgetPageSection.
							getWidgetPageWidgetInstances().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WidgetPageSectionJSONParser widgetPageSectionJSONParser =
			new WidgetPageSectionJSONParser();

		return widgetPageSectionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		WidgetPageSection widgetPageSection) {

		if (widgetPageSection == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (widgetPageSection.getCustomizable() == null) {
			map.put("customizable", null);
		}
		else {
			map.put(
				"customizable",
				String.valueOf(widgetPageSection.getCustomizable()));
		}

		if (widgetPageSection.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(widgetPageSection.getId()));
		}

		if (widgetPageSection.getWidgetPageWidgetInstances() == null) {
			map.put("widgetPageWidgetInstances", null);
		}
		else {
			map.put(
				"widgetPageWidgetInstances",
				String.valueOf(
					widgetPageSection.getWidgetPageWidgetInstances()));
		}

		return map;
	}

	public static class WidgetPageSectionJSONParser
		extends BaseJSONParser<WidgetPageSection> {

		@Override
		protected WidgetPageSection createDTO() {
			return new WidgetPageSection();
		}

		@Override
		protected WidgetPageSection[] createDTOArray(int size) {
			return new WidgetPageSection[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "customizable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "widgetPageWidgetInstances")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WidgetPageSection widgetPageSection, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "customizable")) {
				if (jsonParserFieldValue != null) {
					widgetPageSection.setCustomizable(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					widgetPageSection.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "widgetPageWidgetInstances")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					WidgetPageWidgetInstance[] widgetPageWidgetInstancesArray =
						new WidgetPageWidgetInstance
							[jsonParserFieldValues.length];

					for (int i = 0; i < widgetPageWidgetInstancesArray.length;
						 i++) {

						widgetPageWidgetInstancesArray[i] =
							WidgetPageWidgetInstanceSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					widgetPageSection.setWidgetPageWidgetInstances(
						widgetPageWidgetInstancesArray);
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