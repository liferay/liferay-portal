/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.InnerHit;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class InnerHitSerDes {

	public static InnerHit toDTO(String json) {
		InnerHitJSONParser innerHitJSONParser = new InnerHitJSONParser();

		return innerHitJSONParser.parseToDTO(json);
	}

	public static InnerHit[] toDTOs(String json) {
		InnerHitJSONParser innerHitJSONParser = new InnerHitJSONParser();

		return innerHitJSONParser.parseToDTOs(json);
	}

	public static String toJSON(InnerHit innerHit) {
		if (innerHit == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (innerHit.getInnerCollapse() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"innerCollapse\": ");

			sb.append(String.valueOf(innerHit.getInnerCollapse()));
		}

		if (innerHit.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(innerHit.getName()));

			sb.append("\"");
		}

		if (innerHit.getSize() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"size\": ");

			sb.append(innerHit.getSize());
		}

		if (innerHit.getSorts() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sorts\": ");

			sb.append("[");

			for (int i = 0; i < innerHit.getSorts().length; i++) {
				sb.append(_toJSON(innerHit.getSorts()[i]));

				if ((i + 1) < innerHit.getSorts().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		InnerHitJSONParser innerHitJSONParser = new InnerHitJSONParser();

		return innerHitJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(InnerHit innerHit) {
		if (innerHit == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (innerHit.getInnerCollapse() == null) {
			map.put("innerCollapse", null);
		}
		else {
			map.put(
				"innerCollapse", String.valueOf(innerHit.getInnerCollapse()));
		}

		if (innerHit.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(innerHit.getName()));
		}

		if (innerHit.getSize() == null) {
			map.put("size", null);
		}
		else {
			map.put("size", String.valueOf(innerHit.getSize()));
		}

		if (innerHit.getSorts() == null) {
			map.put("sorts", null);
		}
		else {
			map.put("sorts", String.valueOf(innerHit.getSorts()));
		}

		return map;
	}

	public static class InnerHitJSONParser extends BaseJSONParser<InnerHit> {

		@Override
		protected InnerHit createDTO() {
			return new InnerHit();
		}

		@Override
		protected InnerHit[] createDTOArray(int size) {
			return new InnerHit[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "innerCollapse")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "size")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sorts")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			InnerHit innerHit, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "innerCollapse")) {
				if (jsonParserFieldValue != null) {
					innerHit.setInnerCollapse(
						InnerCollapseSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					innerHit.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "size")) {
				if (jsonParserFieldValue != null) {
					innerHit.setSize(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sorts")) {
				if (jsonParserFieldValue != null) {
					innerHit.setSorts((Object[])jsonParserFieldValue);
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