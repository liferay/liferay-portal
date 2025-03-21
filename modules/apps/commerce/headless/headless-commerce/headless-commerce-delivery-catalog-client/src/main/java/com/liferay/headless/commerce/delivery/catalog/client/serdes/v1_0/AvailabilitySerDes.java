/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.Availability;
import com.liferay.headless.commerce.delivery.catalog.client.json.BaseJSONParser;

import java.math.BigDecimal;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class AvailabilitySerDes {

	public static Availability toDTO(String json) {
		AvailabilityJSONParser availabilityJSONParser =
			new AvailabilityJSONParser();

		return availabilityJSONParser.parseToDTO(json);
	}

	public static Availability[] toDTOs(String json) {
		AvailabilityJSONParser availabilityJSONParser =
			new AvailabilityJSONParser();

		return availabilityJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Availability availability) {
		if (availability == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (availability.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(availability.getLabel()));

			sb.append("\"");
		}

		if (availability.getLabel_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label_i18n\": ");

			sb.append("\"");

			sb.append(_escape(availability.getLabel_i18n()));

			sb.append("\"");
		}

		if (availability.getStockQuantity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"stockQuantity\": ");

			sb.append(availability.getStockQuantity());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AvailabilityJSONParser availabilityJSONParser =
			new AvailabilityJSONParser();

		return availabilityJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Availability availability) {
		if (availability == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (availability.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put("label", String.valueOf(availability.getLabel()));
		}

		if (availability.getLabel_i18n() == null) {
			map.put("label_i18n", null);
		}
		else {
			map.put("label_i18n", String.valueOf(availability.getLabel_i18n()));
		}

		if (availability.getStockQuantity() == null) {
			map.put("stockQuantity", null);
		}
		else {
			map.put(
				"stockQuantity",
				String.valueOf(availability.getStockQuantity()));
		}

		return map;
	}

	public static class AvailabilityJSONParser
		extends BaseJSONParser<Availability> {

		@Override
		protected Availability createDTO() {
			return new Availability();
		}

		@Override
		protected Availability[] createDTOArray(int size) {
			return new Availability[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label_i18n")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "stockQuantity")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Availability availability, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					availability.setLabel((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label_i18n")) {
				if (jsonParserFieldValue != null) {
					availability.setLabel_i18n((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "stockQuantity")) {
				if (jsonParserFieldValue != null) {
					availability.setStockQuantity(
						new BigDecimal((String)jsonParserFieldValue));
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