/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.pim.client.serdes.v1_0;

import com.liferay.headless.pim.client.dto.v1_0.LinkReference;
import com.liferay.headless.pim.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Stefano Motta
 * @generated
 */
@Generated("")
public class LinkReferenceSerDes {

	public static LinkReference toDTO(String json) {
		LinkReferenceJSONParser linkReferenceJSONParser =
			new LinkReferenceJSONParser();

		return linkReferenceJSONParser.parseToDTO(json);
	}

	public static LinkReference[] toDTOs(String json) {
		LinkReferenceJSONParser linkReferenceJSONParser =
			new LinkReferenceJSONParser();

		return linkReferenceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(LinkReference linkReference) {
		if (linkReference == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (linkReference.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(linkReference.getActions()));
		}

		if (linkReference.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(linkReference.getClassName()));

			sb.append("\"");
		}

		if (linkReference.getCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"code\": ");

			sb.append("\"");

			sb.append(_escape(linkReference.getCode()));

			sb.append("\"");
		}

		if (linkReference.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(linkReference.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (linkReference.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(linkReference.getId());
		}

		if (linkReference.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(linkReference.getName()));

			sb.append("\"");
		}

		if (linkReference.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(String.valueOf(linkReference.getStatus()));
		}

		if (linkReference.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(linkReference.getType()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		LinkReferenceJSONParser linkReferenceJSONParser =
			new LinkReferenceJSONParser();

		return linkReferenceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(LinkReference linkReference) {
		if (linkReference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (linkReference.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(linkReference.getActions()));
		}

		if (linkReference.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put("className", String.valueOf(linkReference.getClassName()));
		}

		if (linkReference.getCode() == null) {
			map.put("code", null);
		}
		else {
			map.put("code", String.valueOf(linkReference.getCode()));
		}

		if (linkReference.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(linkReference.getExternalReferenceCode()));
		}

		if (linkReference.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(linkReference.getId()));
		}

		if (linkReference.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(linkReference.getName()));
		}

		if (linkReference.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(linkReference.getStatus()));
		}

		if (linkReference.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(linkReference.getType()));
		}

		return map;
	}

	public static class LinkReferenceJSONParser
		extends BaseJSONParser<LinkReference> {

		@Override
		protected LinkReference createDTO() {
			return new LinkReference();
		}

		@Override
		protected LinkReference[] createDTOArray(int size) {
			return new LinkReference[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "code")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
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
			LinkReference linkReference, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					linkReference.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					linkReference.setClassName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "code")) {
				if (jsonParserFieldValue != null) {
					linkReference.setCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					linkReference.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					linkReference.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					linkReference.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					linkReference.setStatus(
						StatusSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					linkReference.setType((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-147779517