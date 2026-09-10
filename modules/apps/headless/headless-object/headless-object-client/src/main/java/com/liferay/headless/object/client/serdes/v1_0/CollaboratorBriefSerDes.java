/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.client.serdes.v1_0;

import com.liferay.headless.object.client.dto.v1_0.CollaboratorBrief;
import com.liferay.headless.object.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alicia García
 * @generated
 */
@Generated("")
public class CollaboratorBriefSerDes {

	public static CollaboratorBrief toDTO(String json) {
		CollaboratorBriefJSONParser collaboratorBriefJSONParser =
			new CollaboratorBriefJSONParser();

		return collaboratorBriefJSONParser.parseToDTO(json);
	}

	public static CollaboratorBrief[] toDTOs(String json) {
		CollaboratorBriefJSONParser collaboratorBriefJSONParser =
			new CollaboratorBriefJSONParser();

		return collaboratorBriefJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CollaboratorBrief collaboratorBrief) {
		if (collaboratorBrief == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (collaboratorBrief.getActionIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actionIds\": ");

			sb.append("[");

			for (int i = 0; i < collaboratorBrief.getActionIds().length; i++) {
				sb.append(_toJSON(collaboratorBrief.getActionIds()[i]));

				if ((i + 1) < collaboratorBrief.getActionIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (collaboratorBrief.getDateExpired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateExpired\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					collaboratorBrief.getDateExpired()));

			sb.append("\"");
		}

		if (collaboratorBrief.getShare() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"share\": ");

			sb.append(collaboratorBrief.getShare());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CollaboratorBriefJSONParser collaboratorBriefJSONParser =
			new CollaboratorBriefJSONParser();

		return collaboratorBriefJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		CollaboratorBrief collaboratorBrief) {

		if (collaboratorBrief == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (collaboratorBrief.getActionIds() == null) {
			map.put("actionIds", null);
		}
		else {
			map.put(
				"actionIds", String.valueOf(collaboratorBrief.getActionIds()));
		}

		if (collaboratorBrief.getDateExpired() == null) {
			map.put("dateExpired", null);
		}
		else {
			map.put(
				"dateExpired",
				liferayToJSONDateFormat.format(
					collaboratorBrief.getDateExpired()));
		}

		if (collaboratorBrief.getShare() == null) {
			map.put("share", null);
		}
		else {
			map.put("share", String.valueOf(collaboratorBrief.getShare()));
		}

		return map;
	}

	public static class CollaboratorBriefJSONParser
		extends BaseJSONParser<CollaboratorBrief> {

		@Override
		protected CollaboratorBrief createDTO() {
			return new CollaboratorBrief();
		}

		@Override
		protected CollaboratorBrief[] createDTOArray(int size) {
			return new CollaboratorBrief[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actionIds")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateExpired")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "share")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CollaboratorBrief collaboratorBrief, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actionIds")) {
				if (jsonParserFieldValue != null) {
					collaboratorBrief.setActionIds(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateExpired")) {
				if (jsonParserFieldValue != null) {
					collaboratorBrief.setDateExpired(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "share")) {
				if (jsonParserFieldValue != null) {
					collaboratorBrief.setShare((Boolean)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-547147768