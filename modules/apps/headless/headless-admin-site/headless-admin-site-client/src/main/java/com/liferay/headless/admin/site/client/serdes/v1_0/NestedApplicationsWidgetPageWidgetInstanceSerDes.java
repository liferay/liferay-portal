/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.NestedApplicationsWidgetPageWidgetInstance;
import com.liferay.headless.admin.site.client.dto.v1_0.NestedWidgetSection;
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
public class NestedApplicationsWidgetPageWidgetInstanceSerDes {

	public static NestedApplicationsWidgetPageWidgetInstance toDTO(
		String json) {

		NestedApplicationsWidgetPageWidgetInstanceJSONParser
			nestedApplicationsWidgetPageWidgetInstanceJSONParser =
				new NestedApplicationsWidgetPageWidgetInstanceJSONParser();

		return nestedApplicationsWidgetPageWidgetInstanceJSONParser.parseToDTO(
			json);
	}

	public static NestedApplicationsWidgetPageWidgetInstance[] toDTOs(
		String json) {

		NestedApplicationsWidgetPageWidgetInstanceJSONParser
			nestedApplicationsWidgetPageWidgetInstanceJSONParser =
				new NestedApplicationsWidgetPageWidgetInstanceJSONParser();

		return nestedApplicationsWidgetPageWidgetInstanceJSONParser.parseToDTOs(
			json);
	}

	public static String toJSON(
		NestedApplicationsWidgetPageWidgetInstance
			nestedApplicationsWidgetPageWidgetInstance) {

		if (nestedApplicationsWidgetPageWidgetInstance == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (nestedApplicationsWidgetPageWidgetInstance.
				getNestedWidgetSections() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nestedWidgetSections\": ");

			sb.append("[");

			for (int i = 0;
				 i < nestedApplicationsWidgetPageWidgetInstance.
					 getNestedWidgetSections().length;
				 i++) {

				sb.append(
					String.valueOf(
						nestedApplicationsWidgetPageWidgetInstance.
							getNestedWidgetSections()[i]));

				if ((i + 1) < nestedApplicationsWidgetPageWidgetInstance.
						getNestedWidgetSections().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (nestedApplicationsWidgetPageWidgetInstance.
				getExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					nestedApplicationsWidgetPageWidgetInstance.
						getExternalReferenceCode()));

			sb.append("\"");
		}

		if (nestedApplicationsWidgetPageWidgetInstance.getParentSectionId() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentSectionId\": ");

			sb.append("\"");

			sb.append(
				_escape(
					nestedApplicationsWidgetPageWidgetInstance.
						getParentSectionId()));

			sb.append("\"");
		}

		if (nestedApplicationsWidgetPageWidgetInstance.
				getParentWidgetInstanceExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentWidgetInstanceExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					nestedApplicationsWidgetPageWidgetInstance.
						getParentWidgetInstanceExternalReferenceCode()));

			sb.append("\"");
		}

		if (nestedApplicationsWidgetPageWidgetInstance.getPosition() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"position\": ");

			sb.append(nestedApplicationsWidgetPageWidgetInstance.getPosition());
		}

		if (nestedApplicationsWidgetPageWidgetInstance.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(nestedApplicationsWidgetPageWidgetInstance.getType());
			sb.append("\"");
		}

		if (nestedApplicationsWidgetPageWidgetInstance.getWidgetConfig() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetConfig\": ");

			sb.append(
				_toJSON(
					nestedApplicationsWidgetPageWidgetInstance.
						getWidgetConfig()));
		}

		if (nestedApplicationsWidgetPageWidgetInstance.getWidgetInstanceId() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetInstanceId\": ");

			sb.append("\"");

			sb.append(
				_escape(
					nestedApplicationsWidgetPageWidgetInstance.
						getWidgetInstanceId()));

			sb.append("\"");
		}

		if (nestedApplicationsWidgetPageWidgetInstance.
				getWidgetLookAndFeelConfig() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetLookAndFeelConfig\": ");

			sb.append(
				String.valueOf(
					nestedApplicationsWidgetPageWidgetInstance.
						getWidgetLookAndFeelConfig()));
		}

		if (nestedApplicationsWidgetPageWidgetInstance.getWidgetName() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetName\": ");

			sb.append("\"");

			sb.append(
				_escape(
					nestedApplicationsWidgetPageWidgetInstance.
						getWidgetName()));

			sb.append("\"");
		}

		if (nestedApplicationsWidgetPageWidgetInstance.getWidgetPermissions() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"widgetPermissions\": ");

			sb.append("[");

			for (int i = 0;
				 i < nestedApplicationsWidgetPageWidgetInstance.
					 getWidgetPermissions().length;
				 i++) {

				sb.append(
					String.valueOf(
						nestedApplicationsWidgetPageWidgetInstance.
							getWidgetPermissions()[i]));

				if ((i + 1) < nestedApplicationsWidgetPageWidgetInstance.
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
		NestedApplicationsWidgetPageWidgetInstanceJSONParser
			nestedApplicationsWidgetPageWidgetInstanceJSONParser =
				new NestedApplicationsWidgetPageWidgetInstanceJSONParser();

		return nestedApplicationsWidgetPageWidgetInstanceJSONParser.parseToMap(
			json);
	}

	public static Map<String, String> toMap(
		NestedApplicationsWidgetPageWidgetInstance
			nestedApplicationsWidgetPageWidgetInstance) {

		if (nestedApplicationsWidgetPageWidgetInstance == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (nestedApplicationsWidgetPageWidgetInstance.
				getNestedWidgetSections() == null) {

			map.put("nestedWidgetSections", null);
		}
		else {
			map.put(
				"nestedWidgetSections",
				String.valueOf(
					nestedApplicationsWidgetPageWidgetInstance.
						getNestedWidgetSections()));
		}

		if (nestedApplicationsWidgetPageWidgetInstance.
				getExternalReferenceCode() == null) {

			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					nestedApplicationsWidgetPageWidgetInstance.
						getExternalReferenceCode()));
		}

		if (nestedApplicationsWidgetPageWidgetInstance.getParentSectionId() ==
				null) {

			map.put("parentSectionId", null);
		}
		else {
			map.put(
				"parentSectionId",
				String.valueOf(
					nestedApplicationsWidgetPageWidgetInstance.
						getParentSectionId()));
		}

		if (nestedApplicationsWidgetPageWidgetInstance.
				getParentWidgetInstanceExternalReferenceCode() == null) {

			map.put("parentWidgetInstanceExternalReferenceCode", null);
		}
		else {
			map.put(
				"parentWidgetInstanceExternalReferenceCode",
				String.valueOf(
					nestedApplicationsWidgetPageWidgetInstance.
						getParentWidgetInstanceExternalReferenceCode()));
		}

		if (nestedApplicationsWidgetPageWidgetInstance.getPosition() == null) {
			map.put("position", null);
		}
		else {
			map.put(
				"position",
				String.valueOf(
					nestedApplicationsWidgetPageWidgetInstance.getPosition()));
		}

		if (nestedApplicationsWidgetPageWidgetInstance.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(
					nestedApplicationsWidgetPageWidgetInstance.getType()));
		}

		if (nestedApplicationsWidgetPageWidgetInstance.getWidgetConfig() ==
				null) {

			map.put("widgetConfig", null);
		}
		else {
			map.put(
				"widgetConfig",
				String.valueOf(
					nestedApplicationsWidgetPageWidgetInstance.
						getWidgetConfig()));
		}

		if (nestedApplicationsWidgetPageWidgetInstance.getWidgetInstanceId() ==
				null) {

			map.put("widgetInstanceId", null);
		}
		else {
			map.put(
				"widgetInstanceId",
				String.valueOf(
					nestedApplicationsWidgetPageWidgetInstance.
						getWidgetInstanceId()));
		}

		if (nestedApplicationsWidgetPageWidgetInstance.
				getWidgetLookAndFeelConfig() == null) {

			map.put("widgetLookAndFeelConfig", null);
		}
		else {
			map.put(
				"widgetLookAndFeelConfig",
				String.valueOf(
					nestedApplicationsWidgetPageWidgetInstance.
						getWidgetLookAndFeelConfig()));
		}

		if (nestedApplicationsWidgetPageWidgetInstance.getWidgetName() ==
				null) {

			map.put("widgetName", null);
		}
		else {
			map.put(
				"widgetName",
				String.valueOf(
					nestedApplicationsWidgetPageWidgetInstance.
						getWidgetName()));
		}

		if (nestedApplicationsWidgetPageWidgetInstance.getWidgetPermissions() ==
				null) {

			map.put("widgetPermissions", null);
		}
		else {
			map.put(
				"widgetPermissions",
				String.valueOf(
					nestedApplicationsWidgetPageWidgetInstance.
						getWidgetPermissions()));
		}

		return map;
	}

	public static class NestedApplicationsWidgetPageWidgetInstanceJSONParser
		extends BaseJSONParser<NestedApplicationsWidgetPageWidgetInstance> {

		@Override
		protected NestedApplicationsWidgetPageWidgetInstance createDTO() {
			return new NestedApplicationsWidgetPageWidgetInstance();
		}

		@Override
		protected NestedApplicationsWidgetPageWidgetInstance[] createDTOArray(
			int size) {

			return new NestedApplicationsWidgetPageWidgetInstance[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "nestedWidgetSections")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

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
			NestedApplicationsWidgetPageWidgetInstance
				nestedApplicationsWidgetPageWidgetInstance,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "nestedWidgetSections")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					NestedWidgetSection[] nestedWidgetSectionsArray =
						new NestedWidgetSection[jsonParserFieldValues.length];

					for (int i = 0; i < nestedWidgetSectionsArray.length; i++) {
						nestedWidgetSectionsArray[i] =
							NestedWidgetSectionSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					nestedApplicationsWidgetPageWidgetInstance.
						setNestedWidgetSections(nestedWidgetSectionsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					nestedApplicationsWidgetPageWidgetInstance.
						setExternalReferenceCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "parentSectionId")) {
				if (jsonParserFieldValue != null) {
					nestedApplicationsWidgetPageWidgetInstance.
						setParentSectionId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentWidgetInstanceExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					nestedApplicationsWidgetPageWidgetInstance.
						setParentWidgetInstanceExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "position")) {
				if (jsonParserFieldValue != null) {
					nestedApplicationsWidgetPageWidgetInstance.setPosition(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					nestedApplicationsWidgetPageWidgetInstance.setType(
						NestedApplicationsWidgetPageWidgetInstance.Type.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "widgetConfig")) {
				if (jsonParserFieldValue != null) {
					nestedApplicationsWidgetPageWidgetInstance.setWidgetConfig(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "widgetInstanceId")) {
				if (jsonParserFieldValue != null) {
					nestedApplicationsWidgetPageWidgetInstance.
						setWidgetInstanceId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "widgetLookAndFeelConfig")) {

				if (jsonParserFieldValue != null) {
					nestedApplicationsWidgetPageWidgetInstance.
						setWidgetLookAndFeelConfig(
							WidgetLookAndFeelConfigSerDes.toDTO(
								(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "widgetName")) {
				if (jsonParserFieldValue != null) {
					nestedApplicationsWidgetPageWidgetInstance.setWidgetName(
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

					nestedApplicationsWidgetPageWidgetInstance.
						setWidgetPermissions(widgetPermissionsArray);
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
// LIFERAY-REST-BUILDER-HASH:1260984062