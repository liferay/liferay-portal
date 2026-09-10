/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.rest.client.serdes.v1_0;

import com.liferay.segments.asah.rest.client.dto.v1_0.AsahSegmentsEntry;
import com.liferay.segments.asah.rest.client.dto.v1_0.Membership;
import com.liferay.segments.asah.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
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
public class AsahSegmentsEntrySerDes {

	public static AsahSegmentsEntry toDTO(String json) {
		AsahSegmentsEntryJSONParser asahSegmentsEntryJSONParser =
			new AsahSegmentsEntryJSONParser();

		return asahSegmentsEntryJSONParser.parseToDTO(json);
	}

	public static AsahSegmentsEntry[] toDTOs(String json) {
		AsahSegmentsEntryJSONParser asahSegmentsEntryJSONParser =
			new AsahSegmentsEntryJSONParser();

		return asahSegmentsEntryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AsahSegmentsEntry asahSegmentsEntry) {
		if (asahSegmentsEntry == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (asahSegmentsEntry.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(asahSegmentsEntry.getActive());
		}

		if (asahSegmentsEntry.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(asahSegmentsEntry.getId()));

			sb.append("\"");
		}

		if (asahSegmentsEntry.getMemberships() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"memberships\": ");

			sb.append("[");

			for (int i = 0; i < asahSegmentsEntry.getMemberships().length;
				 i++) {

				sb.append(
					String.valueOf(asahSegmentsEntry.getMemberships()[i]));

				if ((i + 1) < asahSegmentsEntry.getMemberships().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (asahSegmentsEntry.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(asahSegmentsEntry.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AsahSegmentsEntryJSONParser asahSegmentsEntryJSONParser =
			new AsahSegmentsEntryJSONParser();

		return asahSegmentsEntryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		AsahSegmentsEntry asahSegmentsEntry) {

		if (asahSegmentsEntry == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (asahSegmentsEntry.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(asahSegmentsEntry.getActive()));
		}

		if (asahSegmentsEntry.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(asahSegmentsEntry.getId()));
		}

		if (asahSegmentsEntry.getMemberships() == null) {
			map.put("memberships", null);
		}
		else {
			map.put(
				"memberships",
				String.valueOf(asahSegmentsEntry.getMemberships()));
		}

		if (asahSegmentsEntry.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(asahSegmentsEntry.getName()));
		}

		return map;
	}

	public static class AsahSegmentsEntryJSONParser
		extends BaseJSONParser<AsahSegmentsEntry> {

		@Override
		protected AsahSegmentsEntry createDTO() {
			return new AsahSegmentsEntry();
		}

		@Override
		protected AsahSegmentsEntry[] createDTOArray(int size) {
			return new AsahSegmentsEntry[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "memberships")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AsahSegmentsEntry asahSegmentsEntry, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					asahSegmentsEntry.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					asahSegmentsEntry.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "memberships")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Membership[] membershipsArray =
						new Membership[jsonParserFieldValues.length];

					for (int i = 0; i < membershipsArray.length; i++) {
						membershipsArray[i] = MembershipSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					asahSegmentsEntry.setMemberships(membershipsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					asahSegmentsEntry.setName((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:2109798075