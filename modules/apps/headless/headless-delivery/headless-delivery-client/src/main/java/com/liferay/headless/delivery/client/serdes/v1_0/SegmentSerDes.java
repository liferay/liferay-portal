/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.Segment;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class SegmentSerDes {

	public static Segment toDTO(String json) {
		SegmentJSONParser segmentJSONParser = new SegmentJSONParser();

		return segmentJSONParser.parseToDTO(json);
	}

	public static Segment[] toDTOs(String json) {
		SegmentJSONParser segmentJSONParser = new SegmentJSONParser();

		return segmentJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Segment segment) {
		if (segment == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (segment.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(segment.getActive());
		}

		if (segment.getCriteria() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"criteria\": ");

			sb.append("\"");

			sb.append(_escape(segment.getCriteria()));

			sb.append("\"");
		}

		if (segment.getCriteriaValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"criteriaValue\": ");

			sb.append(_toJSON(segment.getCriteriaValue()));
		}

		if (segment.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(segment.getDateCreated()));

			sb.append("\"");
		}

		if (segment.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(segment.getDateModified()));

			sb.append("\"");
		}

		if (segment.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(segment.getId());
		}

		if (segment.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(segment.getName()));

			sb.append("\"");
		}

		if (segment.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(segment.getSiteId());
		}

		if (segment.getSource() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"source\": ");

			sb.append("\"");

			sb.append(_escape(segment.getSource()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SegmentJSONParser segmentJSONParser = new SegmentJSONParser();

		return segmentJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Segment segment) {
		if (segment == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (segment.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(segment.getActive()));
		}

		if (segment.getCriteria() == null) {
			map.put("criteria", null);
		}
		else {
			map.put("criteria", String.valueOf(segment.getCriteria()));
		}

		if (segment.getCriteriaValue() == null) {
			map.put("criteriaValue", null);
		}
		else {
			map.put(
				"criteriaValue", String.valueOf(segment.getCriteriaValue()));
		}

		if (segment.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(segment.getDateCreated()));
		}

		if (segment.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(segment.getDateModified()));
		}

		if (segment.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(segment.getId()));
		}

		if (segment.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(segment.getName()));
		}

		if (segment.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(segment.getSiteId()));
		}

		if (segment.getSource() == null) {
			map.put("source", null);
		}
		else {
			map.put("source", String.valueOf(segment.getSource()));
		}

		return map;
	}

	public static class SegmentJSONParser extends BaseJSONParser<Segment> {

		@Override
		protected Segment createDTO() {
			return new Segment();
		}

		@Override
		protected Segment[] createDTOArray(int size) {
			return new Segment[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "criteria")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "criteriaValue")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "source")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Segment segment, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					segment.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "criteria")) {
				if (jsonParserFieldValue != null) {
					segment.setCriteria((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "criteriaValue")) {
				if (jsonParserFieldValue != null) {
					segment.setCriteriaValue(
						(Map<String, Object>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					segment.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					segment.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					segment.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					segment.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					segment.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "source")) {
				if (jsonParserFieldValue != null) {
					segment.setSource((String)jsonParserFieldValue);
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