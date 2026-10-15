/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerBoolean;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerControl;
import com.liferay.exportimport.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class PortletDataHandlerBooleanSerDes {

	public static PortletDataHandlerBoolean toDTO(String json) {
		PortletDataHandlerBooleanJSONParser
			portletDataHandlerBooleanJSONParser =
				new PortletDataHandlerBooleanJSONParser();

		return portletDataHandlerBooleanJSONParser.parseToDTO(json);
	}

	public static PortletDataHandlerBoolean[] toDTOs(String json) {
		PortletDataHandlerBooleanJSONParser
			portletDataHandlerBooleanJSONParser =
				new PortletDataHandlerBooleanJSONParser();

		return portletDataHandlerBooleanJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PortletDataHandlerBoolean portletDataHandlerBoolean) {

		if (portletDataHandlerBoolean == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (portletDataHandlerBoolean.getAdditionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"additionCount\": ");

			sb.append(portletDataHandlerBoolean.getAdditionCount());
		}

		if (portletDataHandlerBoolean.getDeletionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletionCount\": ");

			sb.append(portletDataHandlerBoolean.getDeletionCount());
		}

		if (portletDataHandlerBoolean.getPortletDataHandlerControls() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"portletDataHandlerControls\": ");

			sb.append("[");

			for (int i = 0;
				 i < portletDataHandlerBoolean.
					 getPortletDataHandlerControls().length;
				 i++) {

				sb.append(
					String.valueOf(
						portletDataHandlerBoolean.
							getPortletDataHandlerControls()[i]));

				if ((i + 1) < portletDataHandlerBoolean.
						getPortletDataHandlerControls().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (portletDataHandlerBoolean.getSelected() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selected\": ");

			sb.append(portletDataHandlerBoolean.getSelected());
		}

		if (portletDataHandlerBoolean.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(portletDataHandlerBoolean.getLabel()));

			sb.append("\"");
		}

		if (portletDataHandlerBoolean.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(portletDataHandlerBoolean.getName()));

			sb.append("\"");
		}

		if (portletDataHandlerBoolean.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(portletDataHandlerBoolean.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PortletDataHandlerBooleanJSONParser
			portletDataHandlerBooleanJSONParser =
				new PortletDataHandlerBooleanJSONParser();

		return portletDataHandlerBooleanJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PortletDataHandlerBoolean portletDataHandlerBoolean) {

		if (portletDataHandlerBoolean == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (portletDataHandlerBoolean.getAdditionCount() == null) {
			map.put("additionCount", null);
		}
		else {
			map.put(
				"additionCount",
				String.valueOf(portletDataHandlerBoolean.getAdditionCount()));
		}

		if (portletDataHandlerBoolean.getDeletionCount() == null) {
			map.put("deletionCount", null);
		}
		else {
			map.put(
				"deletionCount",
				String.valueOf(portletDataHandlerBoolean.getDeletionCount()));
		}

		if (portletDataHandlerBoolean.getPortletDataHandlerControls() == null) {
			map.put("portletDataHandlerControls", null);
		}
		else {
			map.put(
				"portletDataHandlerControls",
				String.valueOf(
					portletDataHandlerBoolean.getPortletDataHandlerControls()));
		}

		if (portletDataHandlerBoolean.getSelected() == null) {
			map.put("selected", null);
		}
		else {
			map.put(
				"selected",
				String.valueOf(portletDataHandlerBoolean.getSelected()));
		}

		if (portletDataHandlerBoolean.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put(
				"label", String.valueOf(portletDataHandlerBoolean.getLabel()));
		}

		if (portletDataHandlerBoolean.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(portletDataHandlerBoolean.getName()));
		}

		if (portletDataHandlerBoolean.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type", String.valueOf(portletDataHandlerBoolean.getType()));
		}

		return map;
	}

	public static class PortletDataHandlerBooleanJSONParser
		extends BaseJSONParser<PortletDataHandlerBoolean> {

		@Override
		protected PortletDataHandlerBoolean createDTO() {
			return new PortletDataHandlerBoolean();
		}

		@Override
		protected PortletDataHandlerBoolean[] createDTOArray(int size) {
			return new PortletDataHandlerBoolean[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "additionCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "deletionCount")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "portletDataHandlerControls")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "selected")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PortletDataHandlerBoolean portletDataHandlerBoolean,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "additionCount")) {
				if (jsonParserFieldValue != null) {
					portletDataHandlerBoolean.setAdditionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deletionCount")) {
				if (jsonParserFieldValue != null) {
					portletDataHandlerBoolean.setDeletionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "portletDataHandlerControls")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PortletDataHandlerControl[]
						portletDataHandlerControlsArray =
							new PortletDataHandlerControl
								[jsonParserFieldValues.length];

					for (int i = 0; i < portletDataHandlerControlsArray.length;
						 i++) {

						portletDataHandlerControlsArray[i] =
							PortletDataHandlerControlSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					portletDataHandlerBoolean.setPortletDataHandlerControls(
						portletDataHandlerControlsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selected")) {
				if (jsonParserFieldValue != null) {
					portletDataHandlerBoolean.setSelected(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					portletDataHandlerBoolean.setLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					portletDataHandlerBoolean.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					portletDataHandlerBoolean.setType(
						PortletDataHandlerBoolean.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:1116731642