/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Pin;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class PinSerDes {

	public static Pin toDTO(String json) {
		PinJSONParser pinJSONParser = new PinJSONParser();

		return pinJSONParser.parseToDTO(json);
	}

	public static Pin[] toDTOs(String json) {
		PinJSONParser pinJSONParser = new PinJSONParser();

		return pinJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Pin pin) {
		if (pin == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pin.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(pin.getId());
		}

		if (pin.getMappedProduct() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mappedProduct\": ");

			sb.append(String.valueOf(pin.getMappedProduct()));
		}

		if (pin.getPositionX() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"positionX\": ");

			sb.append(pin.getPositionX());
		}

		if (pin.getPositionY() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"positionY\": ");

			sb.append(pin.getPositionY());
		}

		if (pin.getSequence() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sequence\": ");

			sb.append("\"");

			sb.append(_escape(pin.getSequence()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PinJSONParser pinJSONParser = new PinJSONParser();

		return pinJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Pin pin) {
		if (pin == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pin.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(pin.getId()));
		}

		if (pin.getMappedProduct() == null) {
			map.put("mappedProduct", null);
		}
		else {
			map.put("mappedProduct", String.valueOf(pin.getMappedProduct()));
		}

		if (pin.getPositionX() == null) {
			map.put("positionX", null);
		}
		else {
			map.put("positionX", String.valueOf(pin.getPositionX()));
		}

		if (pin.getPositionY() == null) {
			map.put("positionY", null);
		}
		else {
			map.put("positionY", String.valueOf(pin.getPositionY()));
		}

		if (pin.getSequence() == null) {
			map.put("sequence", null);
		}
		else {
			map.put("sequence", String.valueOf(pin.getSequence()));
		}

		return map;
	}

	public static class PinJSONParser extends BaseJSONParser<Pin> {

		@Override
		protected Pin createDTO() {
			return new Pin();
		}

		@Override
		protected Pin[] createDTOArray(int size) {
			return new Pin[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "mappedProduct")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "positionX")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "positionY")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sequence")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Pin pin, String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					pin.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "mappedProduct")) {
				if (jsonParserFieldValue != null) {
					pin.setMappedProduct(
						MappedProductSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "positionX")) {
				if (jsonParserFieldValue != null) {
					pin.setPositionX(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "positionY")) {
				if (jsonParserFieldValue != null) {
					pin.setPositionY(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sequence")) {
				if (jsonParserFieldValue != null) {
					pin.setSequence((String)jsonParserFieldValue);
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