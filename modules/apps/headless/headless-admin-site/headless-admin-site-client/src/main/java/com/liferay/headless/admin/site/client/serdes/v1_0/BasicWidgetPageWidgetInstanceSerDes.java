/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.BasicWidgetPageWidgetInstance;
import com.liferay.headless.admin.site.client.dto.v1_0.WidgetPermission;
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
public class BasicWidgetPageWidgetInstanceSerDes {

	public static BasicWidgetPageWidgetInstance toDTO(String json) {
		BasicWidgetPageWidgetInstanceJSONParser
			basicWidgetPageWidgetInstanceJSONParser =
				new BasicWidgetPageWidgetInstanceJSONParser();

		return basicWidgetPageWidgetInstanceJSONParser.parseToDTO(json);
	}

	public static BasicWidgetPageWidgetInstance[] toDTOs(String json) {
		BasicWidgetPageWidgetInstanceJSONParser
			basicWidgetPageWidgetInstanceJSONParser =
				new BasicWidgetPageWidgetInstanceJSONParser();

		return basicWidgetPageWidgetInstanceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		BasicWidgetPageWidgetInstance basicWidgetPageWidgetInstance) {

		if (basicWidgetPageWidgetInstance == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (basicWidgetPageWidgetInstance.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					basicWidgetPageWidgetInstance.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (basicWidgetPageWidgetInstance.getParentSectionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentSectionId\": ");

			sb.append("\"");

			sb.append(
				_escape(basicWidgetPageWidgetInstance.getParentSectionId()));

			sb.append("\"");
		}

		if (basicWidgetPageWidgetInstance.
				getParentWidgetInstanceExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentWidgetInstanceExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					basicWidgetPageWidgetInstance.
						getParentWidgetInstanceExternalReferenceCode()));

			sb.append("\"");
		}

		if (basicWidgetPageWidgetInstance.getPosition() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"position\": ");

			sb.append(basicWidgetPageWidgetInstance.getPosition());
		}

		if (basicWidgetPageWidgetInstance.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(basicWidgetPageWidgetInstance.getType());
			sb.append("\"");
		}

		if (basicWidgetPageWidgetInstance.getWidgetConfig() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetConfig\": ");

			sb.append(_toJSON(basicWidgetPageWidgetInstance.getWidgetConfig()));
		}

		if (basicWidgetPageWidgetInstance.getWidgetInstanceId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetInstanceId\": ");

			sb.append("\"");

			sb.append(
				_escape(basicWidgetPageWidgetInstance.getWidgetInstanceId()));

			sb.append("\"");
		}

		if (basicWidgetPageWidgetInstance.getWidgetLookAndFeelConfig() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetLookAndFeelConfig\": ");

			sb.append(
				String.valueOf(
					basicWidgetPageWidgetInstance.
						getWidgetLookAndFeelConfig()));
		}

		if (basicWidgetPageWidgetInstance.getWidgetName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetName\": ");

			sb.append("\"");

			sb.append(_escape(basicWidgetPageWidgetInstance.getWidgetName()));

			sb.append("\"");
		}

		if (basicWidgetPageWidgetInstance.getWidgetPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetPermissions\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 basicWidgetPageWidgetInstance.
						 getWidgetPermissions().length;
				 i++) {

				sb.append(
					String.valueOf(
						basicWidgetPageWidgetInstance.getWidgetPermissions()
							[i]));

				if ((i + 1) < basicWidgetPageWidgetInstance.
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
		BasicWidgetPageWidgetInstanceJSONParser
			basicWidgetPageWidgetInstanceJSONParser =
				new BasicWidgetPageWidgetInstanceJSONParser();

		return basicWidgetPageWidgetInstanceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		BasicWidgetPageWidgetInstance basicWidgetPageWidgetInstance) {

		if (basicWidgetPageWidgetInstance == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (basicWidgetPageWidgetInstance.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					basicWidgetPageWidgetInstance.getExternalReferenceCode()));
		}

		if (basicWidgetPageWidgetInstance.getParentSectionId() == null) {
			map.put("parentSectionId", null);
		}
		else {
			map.put(
				"parentSectionId",
				String.valueOf(
					basicWidgetPageWidgetInstance.getParentSectionId()));
		}

		if (basicWidgetPageWidgetInstance.
				getParentWidgetInstanceExternalReferenceCode() == null) {

			map.put("parentWidgetInstanceExternalReferenceCode", null);
		}
		else {
			map.put(
				"parentWidgetInstanceExternalReferenceCode",
				String.valueOf(
					basicWidgetPageWidgetInstance.
						getParentWidgetInstanceExternalReferenceCode()));
		}

		if (basicWidgetPageWidgetInstance.getPosition() == null) {
			map.put("position", null);
		}
		else {
			map.put(
				"position",
				String.valueOf(basicWidgetPageWidgetInstance.getPosition()));
		}

		if (basicWidgetPageWidgetInstance.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(basicWidgetPageWidgetInstance.getType()));
		}

		if (basicWidgetPageWidgetInstance.getWidgetConfig() == null) {
			map.put("widgetConfig", null);
		}
		else {
			map.put(
				"widgetConfig",
				String.valueOf(
					basicWidgetPageWidgetInstance.getWidgetConfig()));
		}

		if (basicWidgetPageWidgetInstance.getWidgetInstanceId() == null) {
			map.put("widgetInstanceId", null);
		}
		else {
			map.put(
				"widgetInstanceId",
				String.valueOf(
					basicWidgetPageWidgetInstance.getWidgetInstanceId()));
		}

		if (basicWidgetPageWidgetInstance.getWidgetLookAndFeelConfig() ==
				null) {

			map.put("widgetLookAndFeelConfig", null);
		}
		else {
			map.put(
				"widgetLookAndFeelConfig",
				String.valueOf(
					basicWidgetPageWidgetInstance.
						getWidgetLookAndFeelConfig()));
		}

		if (basicWidgetPageWidgetInstance.getWidgetName() == null) {
			map.put("widgetName", null);
		}
		else {
			map.put(
				"widgetName",
				String.valueOf(basicWidgetPageWidgetInstance.getWidgetName()));
		}

		if (basicWidgetPageWidgetInstance.getWidgetPermissions() == null) {
			map.put("widgetPermissions", null);
		}
		else {
			map.put(
				"widgetPermissions",
				String.valueOf(
					basicWidgetPageWidgetInstance.getWidgetPermissions()));
		}

		return map;
	}

	public static class BasicWidgetPageWidgetInstanceJSONParser
		extends BaseJSONParser<BasicWidgetPageWidgetInstance> {

		@Override
		protected BasicWidgetPageWidgetInstance createDTO() {
			return new BasicWidgetPageWidgetInstance();
		}

		@Override
		protected BasicWidgetPageWidgetInstance[] createDTOArray(int size) {
			return new BasicWidgetPageWidgetInstance[size];
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
			else if (Objects.equals(jsonParserFieldName, "type")) {
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
			BasicWidgetPageWidgetInstance basicWidgetPageWidgetInstance,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					basicWidgetPageWidgetInstance.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "parentSectionId")) {
				if (jsonParserFieldValue != null) {
					basicWidgetPageWidgetInstance.setParentSectionId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentWidgetInstanceExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					basicWidgetPageWidgetInstance.
						setParentWidgetInstanceExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "position")) {
				if (jsonParserFieldValue != null) {
					basicWidgetPageWidgetInstance.setPosition(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					basicWidgetPageWidgetInstance.setType(
						BasicWidgetPageWidgetInstance.Type.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "widgetConfig")) {
				if (jsonParserFieldValue != null) {
					basicWidgetPageWidgetInstance.setWidgetConfig(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "widgetInstanceId")) {
				if (jsonParserFieldValue != null) {
					basicWidgetPageWidgetInstance.setWidgetInstanceId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "widgetLookAndFeelConfig")) {

				if (jsonParserFieldValue != null) {
					basicWidgetPageWidgetInstance.setWidgetLookAndFeelConfig(
						WidgetLookAndFeelConfigSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "widgetName")) {
				if (jsonParserFieldValue != null) {
					basicWidgetPageWidgetInstance.setWidgetName(
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

					basicWidgetPageWidgetInstance.setWidgetPermissions(
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
// LIFERAY-REST-BUILDER-HASH:283324840