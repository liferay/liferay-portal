/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.Location;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class LocationSerDes {

	public static Location toDTO(String json) {
		LocationJSONParser locationJSONParser = new LocationJSONParser();

		return locationJSONParser.parseToDTO(json);
	}

	public static Location[] toDTOs(String json) {
		LocationJSONParser locationJSONParser = new LocationJSONParser();

		return locationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Location location) {
		if (location == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (location.getAddressCountry() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressCountry\": ");

			sb.append("\"");

			sb.append(_escape(location.getAddressCountry()));

			sb.append("\"");
		}

		if (location.getAddressCountryCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressCountryCode\": ");

			sb.append("\"");

			sb.append(_escape(location.getAddressCountryCode()));

			sb.append("\"");
		}

		if (location.getAddressCountry_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressCountry_i18n\": ");

			sb.append(_toJSON(location.getAddressCountry_i18n()));
		}

		if (location.getAddressRegion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressRegion\": ");

			sb.append("\"");

			sb.append(_escape(location.getAddressRegion()));

			sb.append("\"");
		}

		if (location.getAddressRegionCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addressRegionCode\": ");

			sb.append("\"");

			sb.append(_escape(location.getAddressRegionCode()));

			sb.append("\"");
		}

		if (location.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(location.getId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		LocationJSONParser locationJSONParser = new LocationJSONParser();

		return locationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Location location) {
		if (location == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (location.getAddressCountry() == null) {
			map.put("addressCountry", null);
		}
		else {
			map.put(
				"addressCountry", String.valueOf(location.getAddressCountry()));
		}

		if (location.getAddressCountryCode() == null) {
			map.put("addressCountryCode", null);
		}
		else {
			map.put(
				"addressCountryCode",
				String.valueOf(location.getAddressCountryCode()));
		}

		if (location.getAddressCountry_i18n() == null) {
			map.put("addressCountry_i18n", null);
		}
		else {
			map.put(
				"addressCountry_i18n",
				String.valueOf(location.getAddressCountry_i18n()));
		}

		if (location.getAddressRegion() == null) {
			map.put("addressRegion", null);
		}
		else {
			map.put(
				"addressRegion", String.valueOf(location.getAddressRegion()));
		}

		if (location.getAddressRegionCode() == null) {
			map.put("addressRegionCode", null);
		}
		else {
			map.put(
				"addressRegionCode",
				String.valueOf(location.getAddressRegionCode()));
		}

		if (location.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(location.getId()));
		}

		return map;
	}

	public static class LocationJSONParser extends BaseJSONParser<Location> {

		@Override
		protected Location createDTO() {
			return new Location();
		}

		@Override
		protected Location[] createDTOArray(int size) {
			return new Location[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "addressCountry")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "addressCountryCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "addressCountry_i18n")) {

				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "addressRegion")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "addressRegionCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Location location, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "addressCountry")) {
				if (jsonParserFieldValue != null) {
					location.setAddressCountry((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "addressCountryCode")) {

				if (jsonParserFieldValue != null) {
					location.setAddressCountryCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "addressCountry_i18n")) {

				if (jsonParserFieldValue != null) {
					location.setAddressCountry_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "addressRegion")) {
				if (jsonParserFieldValue != null) {
					location.setAddressRegion((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "addressRegionCode")) {
				if (jsonParserFieldValue != null) {
					location.setAddressRegionCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					location.setId(Long.valueOf((String)jsonParserFieldValue));
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