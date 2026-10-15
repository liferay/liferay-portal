/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandler;
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
public class PortletDataHandlerSerDes {

	public static PortletDataHandler toDTO(String json) {
		PortletDataHandlerJSONParser portletDataHandlerJSONParser =
			new PortletDataHandlerJSONParser();

		return portletDataHandlerJSONParser.parseToDTO(json);
	}

	public static PortletDataHandler[] toDTOs(String json) {
		PortletDataHandlerJSONParser portletDataHandlerJSONParser =
			new PortletDataHandlerJSONParser();

		return portletDataHandlerJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PortletDataHandler portletDataHandler) {
		if (portletDataHandler == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (portletDataHandler.getAdditionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"additionCount\": ");

			sb.append(portletDataHandler.getAdditionCount());
		}

		if (portletDataHandler.getDeletionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletionCount\": ");

			sb.append(portletDataHandler.getDeletionCount());
		}

		if (portletDataHandler.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(portletDataHandler.getDescription()));

			sb.append("\"");
		}

		if (portletDataHandler.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(portletDataHandler.getLabel()));

			sb.append("\"");
		}

		if (portletDataHandler.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(portletDataHandler.getName()));

			sb.append("\"");
		}

		if (portletDataHandler.getPortletDataHandlerControls() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"portletDataHandlerControls\": ");

			sb.append("[");

			for (int i = 0;
				 i < portletDataHandler.getPortletDataHandlerControls().length;
				 i++) {

				sb.append(
					String.valueOf(
						portletDataHandler.getPortletDataHandlerControls()[i]));

				if ((i + 1) <
						portletDataHandler.
							getPortletDataHandlerControls().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (portletDataHandler.getSectionKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sectionKey\": ");

			sb.append("\"");

			sb.append(_escape(portletDataHandler.getSectionKey()));

			sb.append("\"");
		}

		if (portletDataHandler.getSectionLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sectionLabel\": ");

			sb.append("\"");

			sb.append(_escape(portletDataHandler.getSectionLabel()));

			sb.append("\"");
		}

		if (portletDataHandler.getSelected() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selected\": ");

			sb.append(portletDataHandler.getSelected());
		}

		if (portletDataHandler.getTag() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tag\": ");

			sb.append("\"");

			sb.append(_escape(portletDataHandler.getTag()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PortletDataHandlerJSONParser portletDataHandlerJSONParser =
			new PortletDataHandlerJSONParser();

		return portletDataHandlerJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PortletDataHandler portletDataHandler) {

		if (portletDataHandler == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (portletDataHandler.getAdditionCount() == null) {
			map.put("additionCount", null);
		}
		else {
			map.put(
				"additionCount",
				String.valueOf(portletDataHandler.getAdditionCount()));
		}

		if (portletDataHandler.getDeletionCount() == null) {
			map.put("deletionCount", null);
		}
		else {
			map.put(
				"deletionCount",
				String.valueOf(portletDataHandler.getDeletionCount()));
		}

		if (portletDataHandler.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(portletDataHandler.getDescription()));
		}

		if (portletDataHandler.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(portletDataHandler.getLabel()));
		}

		if (portletDataHandler.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(portletDataHandler.getName()));
		}

		if (portletDataHandler.getPortletDataHandlerControls() == null) {
			map.put("portletDataHandlerControls", null);
		}
		else {
			map.put(
				"portletDataHandlerControls",
				String.valueOf(
					portletDataHandler.getPortletDataHandlerControls()));
		}

		if (portletDataHandler.getSectionKey() == null) {
			map.put("sectionKey", null);
		}
		else {
			map.put(
				"sectionKey",
				String.valueOf(portletDataHandler.getSectionKey()));
		}

		if (portletDataHandler.getSectionLabel() == null) {
			map.put("sectionLabel", null);
		}
		else {
			map.put(
				"sectionLabel",
				String.valueOf(portletDataHandler.getSectionLabel()));
		}

		if (portletDataHandler.getSelected() == null) {
			map.put("selected", null);
		}
		else {
			map.put(
				"selected", String.valueOf(portletDataHandler.getSelected()));
		}

		if (portletDataHandler.getTag() == null) {
			map.put("tag", null);
		}
		else {
			map.put("tag", String.valueOf(portletDataHandler.getTag()));
		}

		return map;
	}

	public static class PortletDataHandlerJSONParser
		extends BaseJSONParser<PortletDataHandler> {

		@Override
		protected PortletDataHandler createDTO() {
			return new PortletDataHandler();
		}

		@Override
		protected PortletDataHandler[] createDTOArray(int size) {
			return new PortletDataHandler[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "additionCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "deletionCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "portletDataHandlerControls")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sectionKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sectionLabel")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "selected")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "tag")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PortletDataHandler portletDataHandler, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "additionCount")) {
				if (jsonParserFieldValue != null) {
					portletDataHandler.setAdditionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deletionCount")) {
				if (jsonParserFieldValue != null) {
					portletDataHandler.setDeletionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					portletDataHandler.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					portletDataHandler.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					portletDataHandler.setName((String)jsonParserFieldValue);
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

					portletDataHandler.setPortletDataHandlerControls(
						portletDataHandlerControlsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sectionKey")) {
				if (jsonParserFieldValue != null) {
					portletDataHandler.setSectionKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sectionLabel")) {
				if (jsonParserFieldValue != null) {
					portletDataHandler.setSectionLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selected")) {
				if (jsonParserFieldValue != null) {
					portletDataHandler.setSelected(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "tag")) {
				if (jsonParserFieldValue != null) {
					portletDataHandler.setTag((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-637385354