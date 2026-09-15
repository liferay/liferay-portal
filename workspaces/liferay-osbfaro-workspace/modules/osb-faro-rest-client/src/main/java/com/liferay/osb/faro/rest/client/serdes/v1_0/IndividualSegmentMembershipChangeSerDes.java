/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.IndividualSegmentMembershipChange;
import com.liferay.osb.faro.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class IndividualSegmentMembershipChangeSerDes {

	public static IndividualSegmentMembershipChange toDTO(String json) {
		IndividualSegmentMembershipChangeJSONParser
			individualSegmentMembershipChangeJSONParser =
				new IndividualSegmentMembershipChangeJSONParser();

		return individualSegmentMembershipChangeJSONParser.parseToDTO(json);
	}

	public static IndividualSegmentMembershipChange[] toDTOs(String json) {
		IndividualSegmentMembershipChangeJSONParser
			individualSegmentMembershipChangeJSONParser =
				new IndividualSegmentMembershipChangeJSONParser();

		return individualSegmentMembershipChangeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		IndividualSegmentMembershipChange individualSegmentMembershipChange) {

		if (individualSegmentMembershipChange == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (individualSegmentMembershipChange.getDateChanged() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateChanged\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					individualSegmentMembershipChange.getDateChanged()));

			sb.append("\"");
		}

		if (individualSegmentMembershipChange.getDateFirst() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateFirst\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					individualSegmentMembershipChange.getDateFirst()));

			sb.append("\"");
		}

		if (individualSegmentMembershipChange.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(individualSegmentMembershipChange.getId()));

			sb.append("\"");
		}

		if (individualSegmentMembershipChange.getIndividualEmail() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individualEmail\": ");

			sb.append("\"");

			sb.append(
				_escape(
					individualSegmentMembershipChange.getIndividualEmail()));

			sb.append("\"");
		}

		if (individualSegmentMembershipChange.getIndividualId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individualId\": ");

			sb.append("\"");

			sb.append(
				_escape(individualSegmentMembershipChange.getIndividualId()));

			sb.append("\"");
		}

		if (individualSegmentMembershipChange.getIndividualName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individualName\": ");

			sb.append("\"");

			sb.append(
				_escape(individualSegmentMembershipChange.getIndividualName()));

			sb.append("\"");
		}

		if (individualSegmentMembershipChange.getIndividualSegmentId() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individualSegmentId\": ");

			sb.append("\"");

			sb.append(
				_escape(
					individualSegmentMembershipChange.
						getIndividualSegmentId()));

			sb.append("\"");
		}

		if (individualSegmentMembershipChange.getOperation() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"operation\": ");

			sb.append("\"");

			sb.append(
				_escape(individualSegmentMembershipChange.getOperation()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		IndividualSegmentMembershipChangeJSONParser
			individualSegmentMembershipChangeJSONParser =
				new IndividualSegmentMembershipChangeJSONParser();

		return individualSegmentMembershipChangeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		IndividualSegmentMembershipChange individualSegmentMembershipChange) {

		if (individualSegmentMembershipChange == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (individualSegmentMembershipChange.getDateChanged() == null) {
			map.put("dateChanged", null);
		}
		else {
			map.put(
				"dateChanged",
				liferayToJSONDateFormat.format(
					individualSegmentMembershipChange.getDateChanged()));
		}

		if (individualSegmentMembershipChange.getDateFirst() == null) {
			map.put("dateFirst", null);
		}
		else {
			map.put(
				"dateFirst",
				liferayToJSONDateFormat.format(
					individualSegmentMembershipChange.getDateFirst()));
		}

		if (individualSegmentMembershipChange.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put(
				"id",
				String.valueOf(individualSegmentMembershipChange.getId()));
		}

		if (individualSegmentMembershipChange.getIndividualEmail() == null) {
			map.put("individualEmail", null);
		}
		else {
			map.put(
				"individualEmail",
				String.valueOf(
					individualSegmentMembershipChange.getIndividualEmail()));
		}

		if (individualSegmentMembershipChange.getIndividualId() == null) {
			map.put("individualId", null);
		}
		else {
			map.put(
				"individualId",
				String.valueOf(
					individualSegmentMembershipChange.getIndividualId()));
		}

		if (individualSegmentMembershipChange.getIndividualName() == null) {
			map.put("individualName", null);
		}
		else {
			map.put(
				"individualName",
				String.valueOf(
					individualSegmentMembershipChange.getIndividualName()));
		}

		if (individualSegmentMembershipChange.getIndividualSegmentId() ==
				null) {

			map.put("individualSegmentId", null);
		}
		else {
			map.put(
				"individualSegmentId",
				String.valueOf(
					individualSegmentMembershipChange.
						getIndividualSegmentId()));
		}

		if (individualSegmentMembershipChange.getOperation() == null) {
			map.put("operation", null);
		}
		else {
			map.put(
				"operation",
				String.valueOf(
					individualSegmentMembershipChange.getOperation()));
		}

		return map;
	}

	public static class IndividualSegmentMembershipChangeJSONParser
		extends BaseJSONParser<IndividualSegmentMembershipChange> {

		@Override
		protected IndividualSegmentMembershipChange createDTO() {
			return new IndividualSegmentMembershipChange();
		}

		@Override
		protected IndividualSegmentMembershipChange[] createDTOArray(int size) {
			return new IndividualSegmentMembershipChange[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dateChanged")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateFirst")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "individualEmail")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "individualId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "individualName")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "individualSegmentId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "operation")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			IndividualSegmentMembershipChange individualSegmentMembershipChange,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dateChanged")) {
				if (jsonParserFieldValue != null) {
					individualSegmentMembershipChange.setDateChanged(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateFirst")) {
				if (jsonParserFieldValue != null) {
					individualSegmentMembershipChange.setDateFirst(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					individualSegmentMembershipChange.setId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "individualEmail")) {
				if (jsonParserFieldValue != null) {
					individualSegmentMembershipChange.setIndividualEmail(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "individualId")) {
				if (jsonParserFieldValue != null) {
					individualSegmentMembershipChange.setIndividualId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "individualName")) {
				if (jsonParserFieldValue != null) {
					individualSegmentMembershipChange.setIndividualName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "individualSegmentId")) {

				if (jsonParserFieldValue != null) {
					individualSegmentMembershipChange.setIndividualSegmentId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "operation")) {
				if (jsonParserFieldValue != null) {
					individualSegmentMembershipChange.setOperation(
						(String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-1870012213