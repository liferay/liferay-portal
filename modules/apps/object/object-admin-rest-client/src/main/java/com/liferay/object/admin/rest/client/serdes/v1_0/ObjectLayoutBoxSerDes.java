/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.serdes.v1_0;

import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutBox;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutRow;
import com.liferay.object.admin.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectLayoutBoxSerDes {

	public static ObjectLayoutBox toDTO(String json) {
		ObjectLayoutBoxJSONParser objectLayoutBoxJSONParser =
			new ObjectLayoutBoxJSONParser();

		return objectLayoutBoxJSONParser.parseToDTO(json);
	}

	public static ObjectLayoutBox[] toDTOs(String json) {
		ObjectLayoutBoxJSONParser objectLayoutBoxJSONParser =
			new ObjectLayoutBoxJSONParser();

		return objectLayoutBoxJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ObjectLayoutBox objectLayoutBox) {
		if (objectLayoutBox == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (objectLayoutBox.getCollapsable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collapsable\": ");

			sb.append(objectLayoutBox.getCollapsable());
		}

		if (objectLayoutBox.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(objectLayoutBox.getId());
		}

		if (objectLayoutBox.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(objectLayoutBox.getName()));
		}

		if (objectLayoutBox.getObjectLayoutRows() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectLayoutRows\": ");

			sb.append("[");

			for (int i = 0; i < objectLayoutBox.getObjectLayoutRows().length;
				 i++) {

				sb.append(
					String.valueOf(objectLayoutBox.getObjectLayoutRows()[i]));

				if ((i + 1) < objectLayoutBox.getObjectLayoutRows().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (objectLayoutBox.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(objectLayoutBox.getPriority());
		}

		if (objectLayoutBox.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(objectLayoutBox.getType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectLayoutBoxJSONParser objectLayoutBoxJSONParser =
			new ObjectLayoutBoxJSONParser();

		return objectLayoutBoxJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ObjectLayoutBox objectLayoutBox) {
		if (objectLayoutBox == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (objectLayoutBox.getCollapsable() == null) {
			map.put("collapsable", null);
		}
		else {
			map.put(
				"collapsable",
				String.valueOf(objectLayoutBox.getCollapsable()));
		}

		if (objectLayoutBox.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(objectLayoutBox.getId()));
		}

		if (objectLayoutBox.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(objectLayoutBox.getName()));
		}

		if (objectLayoutBox.getObjectLayoutRows() == null) {
			map.put("objectLayoutRows", null);
		}
		else {
			map.put(
				"objectLayoutRows",
				String.valueOf(objectLayoutBox.getObjectLayoutRows()));
		}

		if (objectLayoutBox.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(objectLayoutBox.getPriority()));
		}

		if (objectLayoutBox.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(objectLayoutBox.getType()));
		}

		return map;
	}

	public static class ObjectLayoutBoxJSONParser
		extends BaseJSONParser<ObjectLayoutBox> {

		@Override
		protected ObjectLayoutBox createDTO() {
			return new ObjectLayoutBox();
		}

		@Override
		protected ObjectLayoutBox[] createDTOArray(int size) {
			return new ObjectLayoutBox[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "collapsable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "objectLayoutRows")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectLayoutBox objectLayoutBox, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "collapsable")) {
				if (jsonParserFieldValue != null) {
					objectLayoutBox.setCollapsable(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					objectLayoutBox.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					objectLayoutBox.setName(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "objectLayoutRows")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ObjectLayoutRow[] objectLayoutRowsArray =
						new ObjectLayoutRow[jsonParserFieldValues.length];

					for (int i = 0; i < objectLayoutRowsArray.length; i++) {
						objectLayoutRowsArray[i] = ObjectLayoutRowSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					objectLayoutBox.setObjectLayoutRows(objectLayoutRowsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					objectLayoutBox.setPriority(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					objectLayoutBox.setType(
						ObjectLayoutBox.Type.create(
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