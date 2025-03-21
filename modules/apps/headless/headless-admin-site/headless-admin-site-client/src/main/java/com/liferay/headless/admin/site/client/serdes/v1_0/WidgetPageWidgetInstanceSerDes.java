/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.WidgetLookAndFeelConfig;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPageWidgetInstance;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPermission;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class WidgetPageWidgetInstanceSerDes {

	public static WidgetPageWidgetInstance toDTO(String json) {
		WidgetPageWidgetInstanceJSONParser widgetPageWidgetInstanceJSONParser =
			new WidgetPageWidgetInstanceJSONParser();

		return widgetPageWidgetInstanceJSONParser.parseToDTO(json);
	}

	public static WidgetPageWidgetInstance[] toDTOs(String json) {
		WidgetPageWidgetInstanceJSONParser widgetPageWidgetInstanceJSONParser =
			new WidgetPageWidgetInstanceJSONParser();

		return widgetPageWidgetInstanceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		WidgetPageWidgetInstance widgetPageWidgetInstance) {

		if (widgetPageWidgetInstance == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (widgetPageWidgetInstance.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(widgetPageWidgetInstance.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (widgetPageWidgetInstance.getParentSectionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentSectionId\": ");

			sb.append("\"");

			sb.append(_escape(widgetPageWidgetInstance.getParentSectionId()));

			sb.append("\"");
		}

		if (widgetPageWidgetInstance.
				getParentWidgetInstanceExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentWidgetInstanceExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					widgetPageWidgetInstance.
						getParentWidgetInstanceExternalReferenceCode()));

			sb.append("\"");
		}

		if (widgetPageWidgetInstance.getPosition() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"position\": ");

			sb.append(widgetPageWidgetInstance.getPosition());
		}

		if (widgetPageWidgetInstance.getWidgetConfig() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetConfig\": ");

			sb.append(_toJSON(widgetPageWidgetInstance.getWidgetConfig()));
		}

		if (widgetPageWidgetInstance.getWidgetInstanceId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetInstanceId\": ");

			sb.append("\"");

			sb.append(_escape(widgetPageWidgetInstance.getWidgetInstanceId()));

			sb.append("\"");
		}

		if (widgetPageWidgetInstance.getWidgetLookAndFeelConfig() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetLookAndFeelConfig\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 widgetPageWidgetInstance.
						 getWidgetLookAndFeelConfig().length;
				 i++) {

				sb.append(
					String.valueOf(
						widgetPageWidgetInstance.getWidgetLookAndFeelConfig()
							[i]));

				if ((i + 1) < widgetPageWidgetInstance.
						getWidgetLookAndFeelConfig().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (widgetPageWidgetInstance.getWidgetName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetName\": ");

			sb.append("\"");

			sb.append(_escape(widgetPageWidgetInstance.getWidgetName()));

			sb.append("\"");
		}

		if (widgetPageWidgetInstance.getWidgetPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetPermissions\": ");

			sb.append("[");

			for (int i = 0;
				 i < widgetPageWidgetInstance.getWidgetPermissions().length;
				 i++) {

				sb.append(
					String.valueOf(
						widgetPageWidgetInstance.getWidgetPermissions()[i]));

				if ((i + 1) <
						widgetPageWidgetInstance.
							getWidgetPermissions().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WidgetPageWidgetInstanceJSONParser widgetPageWidgetInstanceJSONParser =
			new WidgetPageWidgetInstanceJSONParser();

		return widgetPageWidgetInstanceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		WidgetPageWidgetInstance widgetPageWidgetInstance) {

		if (widgetPageWidgetInstance == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (widgetPageWidgetInstance.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					widgetPageWidgetInstance.getExternalReferenceCode()));
		}

		if (widgetPageWidgetInstance.getParentSectionId() == null) {
			map.put("parentSectionId", null);
		}
		else {
			map.put(
				"parentSectionId",
				String.valueOf(widgetPageWidgetInstance.getParentSectionId()));
		}

		if (widgetPageWidgetInstance.
				getParentWidgetInstanceExternalReferenceCode() == null) {

			map.put("parentWidgetInstanceExternalReferenceCode", null);
		}
		else {
			map.put(
				"parentWidgetInstanceExternalReferenceCode",
				String.valueOf(
					widgetPageWidgetInstance.
						getParentWidgetInstanceExternalReferenceCode()));
		}

		if (widgetPageWidgetInstance.getPosition() == null) {
			map.put("position", null);
		}
		else {
			map.put(
				"position",
				String.valueOf(widgetPageWidgetInstance.getPosition()));
		}

		if (widgetPageWidgetInstance.getWidgetConfig() == null) {
			map.put("widgetConfig", null);
		}
		else {
			map.put(
				"widgetConfig",
				String.valueOf(widgetPageWidgetInstance.getWidgetConfig()));
		}

		if (widgetPageWidgetInstance.getWidgetInstanceId() == null) {
			map.put("widgetInstanceId", null);
		}
		else {
			map.put(
				"widgetInstanceId",
				String.valueOf(widgetPageWidgetInstance.getWidgetInstanceId()));
		}

		if (widgetPageWidgetInstance.getWidgetLookAndFeelConfig() == null) {
			map.put("widgetLookAndFeelConfig", null);
		}
		else {
			map.put(
				"widgetLookAndFeelConfig",
				String.valueOf(
					widgetPageWidgetInstance.getWidgetLookAndFeelConfig()));
		}

		if (widgetPageWidgetInstance.getWidgetName() == null) {
			map.put("widgetName", null);
		}
		else {
			map.put(
				"widgetName",
				String.valueOf(widgetPageWidgetInstance.getWidgetName()));
		}

		if (widgetPageWidgetInstance.getWidgetPermissions() == null) {
			map.put("widgetPermissions", null);
		}
		else {
			map.put(
				"widgetPermissions",
				String.valueOf(
					widgetPageWidgetInstance.getWidgetPermissions()));
		}

		return map;
	}

	public static class WidgetPageWidgetInstanceJSONParser
		extends BaseJSONParser<WidgetPageWidgetInstance> {

		@Override
		protected WidgetPageWidgetInstance createDTO() {
			return new WidgetPageWidgetInstance();
		}

		@Override
		protected WidgetPageWidgetInstance[] createDTOArray(int size) {
			return new WidgetPageWidgetInstance[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "parentSectionId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentWidgetInstanceExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "position")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "widgetConfig")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "widgetInstanceId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "widgetLookAndFeelConfig")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "widgetName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "widgetPermissions")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WidgetPageWidgetInstance widgetPageWidgetInstance,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					widgetPageWidgetInstance.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "parentSectionId")) {
				if (jsonParserFieldValue != null) {
					widgetPageWidgetInstance.setParentSectionId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentWidgetInstanceExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					widgetPageWidgetInstance.
						setParentWidgetInstanceExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "position")) {
				if (jsonParserFieldValue != null) {
					widgetPageWidgetInstance.setPosition(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "widgetConfig")) {
				if (jsonParserFieldValue != null) {
					widgetPageWidgetInstance.setWidgetConfig(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "widgetInstanceId")) {
				if (jsonParserFieldValue != null) {
					widgetPageWidgetInstance.setWidgetInstanceId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "widgetLookAndFeelConfig")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					WidgetLookAndFeelConfig[] widgetLookAndFeelConfigArray =
						new WidgetLookAndFeelConfig
							[jsonParserFieldValues.length];

					for (int i = 0; i < widgetLookAndFeelConfigArray.length;
						 i++) {

						widgetLookAndFeelConfigArray[i] =
							WidgetLookAndFeelConfigSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					widgetPageWidgetInstance.setWidgetLookAndFeelConfig(
						widgetLookAndFeelConfigArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "widgetName")) {
				if (jsonParserFieldValue != null) {
					widgetPageWidgetInstance.setWidgetName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "widgetPermissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					WidgetPermission[] widgetPermissionsArray =
						new WidgetPermission[jsonParserFieldValues.length];

					for (int i = 0; i < widgetPermissionsArray.length; i++) {
						widgetPermissionsArray[i] =
							WidgetPermissionSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					widgetPageWidgetInstance.setWidgetPermissions(
						widgetPermissionsArray);
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