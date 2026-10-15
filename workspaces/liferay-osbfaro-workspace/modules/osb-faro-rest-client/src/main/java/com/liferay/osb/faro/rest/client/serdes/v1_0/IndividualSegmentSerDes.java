/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.IndividualSegment;
import com.liferay.osb.faro.rest.client.json.BaseJSONParser;

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
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class IndividualSegmentSerDes {

	public static IndividualSegment toDTO(String json) {
		IndividualSegmentJSONParser individualSegmentJSONParser =
			new IndividualSegmentJSONParser();

		return individualSegmentJSONParser.parseToDTO(json);
	}

	public static IndividualSegment[] toDTOs(String json) {
		IndividualSegmentJSONParser individualSegmentJSONParser =
			new IndividualSegmentJSONParser();

		return individualSegmentJSONParser.parseToDTOs(json);
	}

	public static String toJSON(IndividualSegment individualSegment) {
		if (individualSegment == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (individualSegment.getActiveIndividualCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"activeIndividualCount\": ");

			sb.append(individualSegment.getActiveIndividualCount());
		}

		if (individualSegment.getAnonymousIndividualCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"anonymousIndividualCount\": ");

			sb.append(individualSegment.getAnonymousIndividualCount());
		}

		if (individualSegment.getChannelId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelId\": ");

			sb.append("\"");

			sb.append(_escape(individualSegment.getChannelId()));

			sb.append("\"");
		}

		if (individualSegment.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					individualSegment.getDateCreated()));

			sb.append("\"");
		}

		if (individualSegment.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					individualSegment.getDateModified()));

			sb.append("\"");
		}

		if (individualSegment.getFilter() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"filter\": ");

			sb.append("\"");

			sb.append(_escape(individualSegment.getFilter()));

			sb.append("\"");
		}

		if (individualSegment.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(individualSegment.getId()));

			sb.append("\"");
		}

		if (individualSegment.getIncludeAnonymousUsers() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"includeAnonymousUsers\": ");

			sb.append(individualSegment.getIncludeAnonymousUsers());
		}

		if (individualSegment.getIndividualCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individualCount\": ");

			sb.append(individualSegment.getIndividualCount());
		}

		if (individualSegment.getKnownIndividualCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"knownIndividualCount\": ");

			sb.append(individualSegment.getKnownIndividualCount());
		}

		if (individualSegment.getLastActivityDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastActivityDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					individualSegment.getLastActivityDate()));

			sb.append("\"");
		}

		if (individualSegment.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(individualSegment.getName()));

			sb.append("\"");
		}

		if (individualSegment.getSegmentType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"segmentType\": ");

			sb.append("\"");
			sb.append(individualSegment.getSegmentType());
			sb.append("\"");
		}

		if (individualSegment.getState() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"state\": ");

			sb.append("\"");
			sb.append(individualSegment.getState());
			sb.append("\"");
		}

		if (individualSegment.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");
			sb.append(individualSegment.getStatus());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		IndividualSegmentJSONParser individualSegmentJSONParser =
			new IndividualSegmentJSONParser();

		return individualSegmentJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		IndividualSegment individualSegment) {

		if (individualSegment == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (individualSegment.getActiveIndividualCount() == null) {
			map.put("activeIndividualCount", null);
		}
		else {
			map.put(
				"activeIndividualCount",
				String.valueOf(individualSegment.getActiveIndividualCount()));
		}

		if (individualSegment.getAnonymousIndividualCount() == null) {
			map.put("anonymousIndividualCount", null);
		}
		else {
			map.put(
				"anonymousIndividualCount",
				String.valueOf(
					individualSegment.getAnonymousIndividualCount()));
		}

		if (individualSegment.getChannelId() == null) {
			map.put("channelId", null);
		}
		else {
			map.put(
				"channelId", String.valueOf(individualSegment.getChannelId()));
		}

		if (individualSegment.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					individualSegment.getDateCreated()));
		}

		if (individualSegment.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					individualSegment.getDateModified()));
		}

		if (individualSegment.getFilter() == null) {
			map.put("filter", null);
		}
		else {
			map.put("filter", String.valueOf(individualSegment.getFilter()));
		}

		if (individualSegment.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(individualSegment.getId()));
		}

		if (individualSegment.getIncludeAnonymousUsers() == null) {
			map.put("includeAnonymousUsers", null);
		}
		else {
			map.put(
				"includeAnonymousUsers",
				String.valueOf(individualSegment.getIncludeAnonymousUsers()));
		}

		if (individualSegment.getIndividualCount() == null) {
			map.put("individualCount", null);
		}
		else {
			map.put(
				"individualCount",
				String.valueOf(individualSegment.getIndividualCount()));
		}

		if (individualSegment.getKnownIndividualCount() == null) {
			map.put("knownIndividualCount", null);
		}
		else {
			map.put(
				"knownIndividualCount",
				String.valueOf(individualSegment.getKnownIndividualCount()));
		}

		if (individualSegment.getLastActivityDate() == null) {
			map.put("lastActivityDate", null);
		}
		else {
			map.put(
				"lastActivityDate",
				liferayToJSONDateFormat.format(
					individualSegment.getLastActivityDate()));
		}

		if (individualSegment.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(individualSegment.getName()));
		}

		if (individualSegment.getSegmentType() == null) {
			map.put("segmentType", null);
		}
		else {
			map.put(
				"segmentType",
				String.valueOf(individualSegment.getSegmentType()));
		}

		if (individualSegment.getState() == null) {
			map.put("state", null);
		}
		else {
			map.put("state", String.valueOf(individualSegment.getState()));
		}

		if (individualSegment.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(individualSegment.getStatus()));
		}

		return map;
	}

	public static class IndividualSegmentJSONParser
		extends BaseJSONParser<IndividualSegment> {

		@Override
		protected IndividualSegment createDTO() {
			return new IndividualSegment();
		}

		@Override
		protected IndividualSegment[] createDTOArray(int size) {
			return new IndividualSegment[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "activeIndividualCount")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "anonymousIndividualCount")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "channelId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "filter")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "includeAnonymousUsers")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "individualCount")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "knownIndividualCount")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "lastActivityDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "segmentType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "state")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			IndividualSegment individualSegment, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "activeIndividualCount")) {
				if (jsonParserFieldValue != null) {
					individualSegment.setActiveIndividualCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "anonymousIndividualCount")) {

				if (jsonParserFieldValue != null) {
					individualSegment.setAnonymousIndividualCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "channelId")) {
				if (jsonParserFieldValue != null) {
					individualSegment.setChannelId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					individualSegment.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					individualSegment.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "filter")) {
				if (jsonParserFieldValue != null) {
					individualSegment.setFilter((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					individualSegment.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "includeAnonymousUsers")) {

				if (jsonParserFieldValue != null) {
					individualSegment.setIncludeAnonymousUsers(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "individualCount")) {
				if (jsonParserFieldValue != null) {
					individualSegment.setIndividualCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "knownIndividualCount")) {

				if (jsonParserFieldValue != null) {
					individualSegment.setKnownIndividualCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "lastActivityDate")) {
				if (jsonParserFieldValue != null) {
					individualSegment.setLastActivityDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					individualSegment.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "segmentType")) {
				if (jsonParserFieldValue != null) {
					individualSegment.setSegmentType(
						IndividualSegment.SegmentType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "state")) {
				if (jsonParserFieldValue != null) {
					individualSegment.setState(
						IndividualSegment.State.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					individualSegment.setStatus(
						IndividualSegment.Status.create(
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
// LIFERAY-REST-BUILDER-HASH:2009112086