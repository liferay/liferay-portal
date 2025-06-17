/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageSection;
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
public class WidgetPageSpecificationSerDes {

	public static WidgetPageSpecification toDTO(String json) {
		WidgetPageSpecificationJSONParser widgetPageSpecificationJSONParser =
			new WidgetPageSpecificationJSONParser();

		return widgetPageSpecificationJSONParser.parseToDTO(json);
	}

	public static WidgetPageSpecification[] toDTOs(String json) {
		WidgetPageSpecificationJSONParser widgetPageSpecificationJSONParser =
			new WidgetPageSpecificationJSONParser();

		return widgetPageSpecificationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		WidgetPageSpecification widgetPageSpecification) {

		if (widgetPageSpecification == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (widgetPageSpecification.getWidgetPageSections() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetPageSections\": ");

			sb.append("[");

			for (int i = 0;
				 i < widgetPageSpecification.getWidgetPageSections().length;
				 i++) {

				sb.append(
					String.valueOf(
						widgetPageSpecification.getWidgetPageSections()[i]));

				if ((i + 1) <
						widgetPageSpecification.
							getWidgetPageSections().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (widgetPageSpecification.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(widgetPageSpecification.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (widgetPageSpecification.getSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"settings\": ");

			sb.append(String.valueOf(widgetPageSpecification.getSettings()));
		}

		if (widgetPageSpecification.
				getSiteTemplatePageSpecificationExternalReferenceCode() !=
					null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(
				"\"siteTemplatePageSpecificationExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					widgetPageSpecification.
						getSiteTemplatePageSpecificationExternalReferenceCode()));

			sb.append("\"");
		}

		if (widgetPageSpecification.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(widgetPageSpecification.getStatus());

			sb.append("\"");
		}

		if (widgetPageSpecification.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(widgetPageSpecification.getType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WidgetPageSpecificationJSONParser widgetPageSpecificationJSONParser =
			new WidgetPageSpecificationJSONParser();

		return widgetPageSpecificationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		WidgetPageSpecification widgetPageSpecification) {

		if (widgetPageSpecification == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (widgetPageSpecification.getWidgetPageSections() == null) {
			map.put("widgetPageSections", null);
		}
		else {
			map.put(
				"widgetPageSections",
				String.valueOf(
					widgetPageSpecification.getWidgetPageSections()));
		}

		if (widgetPageSpecification.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					widgetPageSpecification.getExternalReferenceCode()));
		}

		if (widgetPageSpecification.getSettings() == null) {
			map.put("settings", null);
		}
		else {
			map.put(
				"settings",
				String.valueOf(widgetPageSpecification.getSettings()));
		}

		if (widgetPageSpecification.
				getSiteTemplatePageSpecificationExternalReferenceCode() ==
					null) {

			map.put("siteTemplatePageSpecificationExternalReferenceCode", null);
		}
		else {
			map.put(
				"siteTemplatePageSpecificationExternalReferenceCode",
				String.valueOf(
					widgetPageSpecification.
						getSiteTemplatePageSpecificationExternalReferenceCode()));
		}

		if (widgetPageSpecification.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put(
				"status", String.valueOf(widgetPageSpecification.getStatus()));
		}

		if (widgetPageSpecification.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(widgetPageSpecification.getType()));
		}

		return map;
	}

	public static class WidgetPageSpecificationJSONParser
		extends BaseJSONParser<WidgetPageSpecification> {

		@Override
		protected WidgetPageSpecification createDTO() {
			return new WidgetPageSpecification();
		}

		@Override
		protected WidgetPageSpecification[] createDTOArray(int size) {
			return new WidgetPageSpecification[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "widgetPageSections")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "settings")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"siteTemplatePageSpecificationExternalReferenceCode")) {

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
		protected void setField(
			WidgetPageSpecification widgetPageSpecification,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "widgetPageSections")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					WidgetPageSection[] widgetPageSectionsArray =
						new WidgetPageSection[jsonParserFieldValues.length];

					for (int i = 0; i < widgetPageSectionsArray.length; i++) {
						widgetPageSectionsArray[i] =
							WidgetPageSectionSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					widgetPageSpecification.setWidgetPageSections(
						widgetPageSectionsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					widgetPageSpecification.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "settings")) {
				if (jsonParserFieldValue != null) {
					widgetPageSpecification.setSettings(
						SettingsSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"siteTemplatePageSpecificationExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					widgetPageSpecification.
						setSiteTemplatePageSpecificationExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					widgetPageSpecification.setStatus(
						WidgetPageSpecification.Status.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					widgetPageSpecification.setType(
						WidgetPageSpecification.Type.create(
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