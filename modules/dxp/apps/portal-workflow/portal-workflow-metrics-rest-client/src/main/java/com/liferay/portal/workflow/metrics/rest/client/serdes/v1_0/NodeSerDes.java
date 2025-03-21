/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.metrics.rest.client.serdes.v1_0;

import com.liferay.portal.workflow.metrics.rest.client.dto.v1_0.Node;
import com.liferay.portal.workflow.metrics.rest.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Rafael Praxedes
 * @generated
 */
@Generated("")
public class NodeSerDes {

	public static Node toDTO(String json) {
		NodeJSONParser nodeJSONParser = new NodeJSONParser();

		return nodeJSONParser.parseToDTO(json);
	}

	public static Node[] toDTOs(String json) {
		NodeJSONParser nodeJSONParser = new NodeJSONParser();

		return nodeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Node node) {
		if (node == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (node.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(node.getDateCreated()));

			sb.append("\"");
		}

		if (node.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(node.getDateModified()));

			sb.append("\"");
		}

		if (node.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(node.getId());
		}

		if (node.getInitial() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"initial\": ");

			sb.append(node.getInitial());
		}

		if (node.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(node.getLabel()));

			sb.append("\"");
		}

		if (node.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(node.getName()));

			sb.append("\"");
		}

		if (node.getProcessId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"processId\": ");

			sb.append(node.getProcessId());
		}

		if (node.getProcessVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"processVersion\": ");

			sb.append("\"");

			sb.append(_escape(node.getProcessVersion()));

			sb.append("\"");
		}

		if (node.getTerminal() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"terminal\": ");

			sb.append(node.getTerminal());
		}

		if (node.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(node.getType()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		NodeJSONParser nodeJSONParser = new NodeJSONParser();

		return nodeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Node node) {
		if (node == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (node.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(node.getDateCreated()));
		}

		if (node.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(node.getDateModified()));
		}

		if (node.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(node.getId()));
		}

		if (node.getInitial() == null) {
			map.put("initial", null);
		}
		else {
			map.put("initial", String.valueOf(node.getInitial()));
		}

		if (node.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(node.getLabel()));
		}

		if (node.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(node.getName()));
		}

		if (node.getProcessId() == null) {
			map.put("processId", null);
		}
		else {
			map.put("processId", String.valueOf(node.getProcessId()));
		}

		if (node.getProcessVersion() == null) {
			map.put("processVersion", null);
		}
		else {
			map.put("processVersion", String.valueOf(node.getProcessVersion()));
		}

		if (node.getTerminal() == null) {
			map.put("terminal", null);
		}
		else {
			map.put("terminal", String.valueOf(node.getTerminal()));
		}

		if (node.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(node.getType()));
		}

		return map;
	}

	public static class NodeJSONParser extends BaseJSONParser<Node> {

		@Override
		protected Node createDTO() {
			return new Node();
		}

		@Override
		protected Node[] createDTOArray(int size) {
			return new Node[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "initial")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "processId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "processVersion")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "terminal")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Node node, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					node.setDateCreated(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					node.setDateModified(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					node.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "initial")) {
				if (jsonParserFieldValue != null) {
					node.setInitial((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					node.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					node.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "processId")) {
				if (jsonParserFieldValue != null) {
					node.setProcessId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "processVersion")) {
				if (jsonParserFieldValue != null) {
					node.setProcessVersion((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "terminal")) {
				if (jsonParserFieldValue != null) {
					node.setTerminal((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					node.setType((String)jsonParserFieldValue);
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