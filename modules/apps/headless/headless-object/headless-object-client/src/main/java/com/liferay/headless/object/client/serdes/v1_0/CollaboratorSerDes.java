/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.object.client.serdes.v1_0;

import com.liferay.headless.object.client.dto.v1_0.Collaborator;
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
public class CollaboratorSerDes {

	public static Collaborator toDTO(String json) {
		CollaboratorJSONParser collaboratorJSONParser =
			new CollaboratorJSONParser();

		return collaboratorJSONParser.parseToDTO(json);
	}

	public static Collaborator[] toDTOs(String json) {
		CollaboratorJSONParser collaboratorJSONParser =
			new CollaboratorJSONParser();

		return collaboratorJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Collaborator collaborator) {
		if (collaborator == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (collaborator.getActionIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actionIds\": ");

			sb.append("[");

			for (int i = 0; i < collaborator.getActionIds().length; i++) {
				sb.append(_toJSON(collaborator.getActionIds()[i]));

				if ((i + 1) < collaborator.getActionIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (collaborator.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(collaborator.getActions()));
		}

		if (collaborator.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(collaborator.getCreator());
		}

		if (collaborator.getDateExpired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateExpired\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(collaborator.getDateExpired()));

			sb.append("\"");
		}

		if (collaborator.getEmailAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddress\": ");

			sb.append("\"");

			sb.append(_escape(collaborator.getEmailAddress()));

			sb.append("\"");
		}

		if (collaborator.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(collaborator.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (collaborator.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(collaborator.getId());
		}

		if (collaborator.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(collaborator.getName()));

			sb.append("\"");
		}

		if (collaborator.getPortrait() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"portrait\": ");

			sb.append("\"");

			sb.append(_escape(collaborator.getPortrait()));

			sb.append("\"");
		}

		if (collaborator.getShare() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"share\": ");

			sb.append(collaborator.getShare());
		}

		if (collaborator.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(collaborator.getType()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CollaboratorJSONParser collaboratorJSONParser =
			new CollaboratorJSONParser();

		return collaboratorJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Collaborator collaborator) {
		if (collaborator == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (collaborator.getActionIds() == null) {
			map.put("actionIds", null);
		}
		else {
			map.put("actionIds", String.valueOf(collaborator.getActionIds()));
		}

		if (collaborator.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(collaborator.getActions()));
		}

		if (collaborator.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(collaborator.getCreator()));
		}

		if (collaborator.getDateExpired() == null) {
			map.put("dateExpired", null);
		}
		else {
			map.put(
				"dateExpired",
				liferayToJSONDateFormat.format(collaborator.getDateExpired()));
		}

		if (collaborator.getEmailAddress() == null) {
			map.put("emailAddress", null);
		}
		else {
			map.put(
				"emailAddress", String.valueOf(collaborator.getEmailAddress()));
		}

		if (collaborator.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(collaborator.getExternalReferenceCode()));
		}

		if (collaborator.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(collaborator.getId()));
		}

		if (collaborator.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(collaborator.getName()));
		}

		if (collaborator.getPortrait() == null) {
			map.put("portrait", null);
		}
		else {
			map.put("portrait", String.valueOf(collaborator.getPortrait()));
		}

		if (collaborator.getShare() == null) {
			map.put("share", null);
		}
		else {
			map.put("share", String.valueOf(collaborator.getShare()));
		}

		if (collaborator.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(collaborator.getType()));
		}

		return map;
	}

	public static class CollaboratorJSONParser
		extends BaseJSONParser<Collaborator> {

		@Override
		protected Collaborator createDTO() {
			return new Collaborator();
		}

		@Override
		protected Collaborator[] createDTOArray(int size) {
			return new Collaborator[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actionIds")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateExpired")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "emailAddress")) {
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
			else if (Objects.equals(jsonParserFieldName, "portrait")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "share")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Collaborator collaborator, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actionIds")) {
				if (jsonParserFieldValue != null) {
					collaborator.setActionIds(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					collaborator.setActions(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					collaborator.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateExpired")) {
				if (jsonParserFieldValue != null) {
					collaborator.setDateExpired(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				if (jsonParserFieldValue != null) {
					collaborator.setEmailAddress((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					collaborator.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					collaborator.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					collaborator.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "portrait")) {
				if (jsonParserFieldValue != null) {
					collaborator.setPortrait((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "share")) {
				if (jsonParserFieldValue != null) {
					collaborator.setShare((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					collaborator.setType((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:1147971854