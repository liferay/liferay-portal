/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerBoolean;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerChoice;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerControl;
import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerTreeSelection;
import com.liferay.exportimport.rest.client.dto.v1_0.Type;
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
public class PortletDataHandlerControlSerDes {

	public static PortletDataHandlerControl toDTO(String json) {
		PortletDataHandlerControlJSONParser
			portletDataHandlerControlJSONParser =
				new PortletDataHandlerControlJSONParser();

		return portletDataHandlerControlJSONParser.parseToDTO(json);
	}

	public static PortletDataHandlerControl[] toDTOs(String json) {
		PortletDataHandlerControlJSONParser
			portletDataHandlerControlJSONParser =
				new PortletDataHandlerControlJSONParser();

		return portletDataHandlerControlJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PortletDataHandlerControl portletDataHandlerControl) {

		if (portletDataHandlerControl == null) {
			return "null";
		}

		PortletDataHandlerControl.Type type =
			portletDataHandlerControl.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("Boolean")) {
				return PortletDataHandlerBooleanSerDes.toJSON(
					(PortletDataHandlerBoolean)portletDataHandlerControl);
			}

			if (typeString.equals("Choice")) {
				return PortletDataHandlerChoiceSerDes.toJSON(
					(PortletDataHandlerChoice)portletDataHandlerControl);
			}

			if (typeString.equals("TreeSelection")) {
				return PortletDataHandlerTreeSelectionSerDes.toJSON(
					(PortletDataHandlerTreeSelection)portletDataHandlerControl);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		PortletDataHandlerControlJSONParser
			portletDataHandlerControlJSONParser =
				new PortletDataHandlerControlJSONParser();

		return portletDataHandlerControlJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PortletDataHandlerControl portletDataHandlerControl) {

		if (portletDataHandlerControl == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (portletDataHandlerControl.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put(
				"label", String.valueOf(portletDataHandlerControl.getLabel()));
		}

		if (portletDataHandlerControl.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(portletDataHandlerControl.getName()));
		}

		if (portletDataHandlerControl.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type", String.valueOf(portletDataHandlerControl.getType()));
		}

		return map;
	}

	public static class PortletDataHandlerControlJSONParser
		extends BaseJSONParser<PortletDataHandlerControl> {

		@Override
		protected PortletDataHandlerControl createDTO() {
			return null;
		}

		@Override
		protected PortletDataHandlerControl[] createDTOArray(int size) {
			return new PortletDataHandlerControl[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "label")) {
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
		public PortletDataHandlerControl parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("Boolean")) {
					return PortletDataHandlerBoolean.toDTO(json);
				}

				if (typeString.equals("Choice")) {
					return PortletDataHandlerChoice.toDTO(json);
				}

				if (typeString.equals("TreeSelection")) {
					return PortletDataHandlerTreeSelection.toDTO(json);
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
			PortletDataHandlerControl portletDataHandlerControl,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					portletDataHandlerControl.setLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					portletDataHandlerControl.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					portletDataHandlerControl.setType(
						PortletDataHandlerControl.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-106646777