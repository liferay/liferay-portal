/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.Collapse;
import com.liferay.search.experiences.rest.client.dto.v1_0.InnerHit;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class CollapseSerDes {

	public static Collapse toDTO(String json) {
		CollapseJSONParser collapseJSONParser = new CollapseJSONParser();

		return collapseJSONParser.parseToDTO(json);
	}

	public static Collapse[] toDTOs(String json) {
		CollapseJSONParser collapseJSONParser = new CollapseJSONParser();

		return collapseJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Collapse collapse) {
		if (collapse == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (collapse.getField() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"field\": ");

			sb.append("\"");

			sb.append(_escape(collapse.getField()));

			sb.append("\"");
		}

		if (collapse.getInnerHits() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"innerHits\": ");

			sb.append("[");

			for (int i = 0; i < collapse.getInnerHits().length; i++) {
				sb.append(String.valueOf(collapse.getInnerHits()[i]));

				if ((i + 1) < collapse.getInnerHits().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (collapse.getMaxConcurrentGroupRequests() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"maxConcurrentGroupRequests\": ");

			sb.append(collapse.getMaxConcurrentGroupRequests());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CollapseJSONParser collapseJSONParser = new CollapseJSONParser();

		return collapseJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Collapse collapse) {
		if (collapse == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (collapse.getField() == null) {
			map.put("field", null);
		}
		else {
			map.put("field", String.valueOf(collapse.getField()));
		}

		if (collapse.getInnerHits() == null) {
			map.put("innerHits", null);
		}
		else {
			map.put("innerHits", String.valueOf(collapse.getInnerHits()));
		}

		if (collapse.getMaxConcurrentGroupRequests() == null) {
			map.put("maxConcurrentGroupRequests", null);
		}
		else {
			map.put(
				"maxConcurrentGroupRequests",
				String.valueOf(collapse.getMaxConcurrentGroupRequests()));
		}

		return map;
	}

	public static class CollapseJSONParser extends BaseJSONParser<Collapse> {

		@Override
		protected Collapse createDTO() {
			return new Collapse();
		}

		@Override
		protected Collapse[] createDTOArray(int size) {
			return new Collapse[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "field")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "innerHits")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "maxConcurrentGroupRequests")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Collapse collapse, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "field")) {
				if (jsonParserFieldValue != null) {
					collapse.setField((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "innerHits")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					InnerHit[] innerHitsArray =
						new InnerHit[jsonParserFieldValues.length];

					for (int i = 0; i < innerHitsArray.length; i++) {
						innerHitsArray[i] = InnerHitSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					collapse.setInnerHits(innerHitsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "maxConcurrentGroupRequests")) {

				if (jsonParserFieldValue != null) {
					collapse.setMaxConcurrentGroupRequests(
						Integer.valueOf((String)jsonParserFieldValue));
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