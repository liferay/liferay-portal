/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.serdes.v1_0;

import com.liferay.analytics.cms.rest.client.dto.v1_0.ObjectEntryTopPages;
import com.liferay.analytics.cms.rest.client.dto.v1_0.TopPage;
import com.liferay.analytics.cms.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
public class ObjectEntryTopPagesSerDes {

	public static ObjectEntryTopPages toDTO(String json) {
		ObjectEntryTopPagesJSONParser objectEntryTopPagesJSONParser =
			new ObjectEntryTopPagesJSONParser();

		return objectEntryTopPagesJSONParser.parseToDTO(json);
	}

	public static ObjectEntryTopPages[] toDTOs(String json) {
		ObjectEntryTopPagesJSONParser objectEntryTopPagesJSONParser =
			new ObjectEntryTopPagesJSONParser();

		return objectEntryTopPagesJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ObjectEntryTopPages objectEntryTopPages) {
		if (objectEntryTopPages == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (objectEntryTopPages.getTopPages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"topPages\": ");

			sb.append("[");

			for (int i = 0; i < objectEntryTopPages.getTopPages().length; i++) {
				sb.append(String.valueOf(objectEntryTopPages.getTopPages()[i]));

				if ((i + 1) < objectEntryTopPages.getTopPages().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (objectEntryTopPages.getTotalCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalCount\": ");

			sb.append(objectEntryTopPages.getTotalCount());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectEntryTopPagesJSONParser objectEntryTopPagesJSONParser =
			new ObjectEntryTopPagesJSONParser();

		return objectEntryTopPagesJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ObjectEntryTopPages objectEntryTopPages) {

		if (objectEntryTopPages == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (objectEntryTopPages.getTopPages() == null) {
			map.put("topPages", null);
		}
		else {
			map.put(
				"topPages", String.valueOf(objectEntryTopPages.getTopPages()));
		}

		if (objectEntryTopPages.getTotalCount() == null) {
			map.put("totalCount", null);
		}
		else {
			map.put(
				"totalCount",
				String.valueOf(objectEntryTopPages.getTotalCount()));
		}

		return map;
	}

	public static class ObjectEntryTopPagesJSONParser
		extends BaseJSONParser<ObjectEntryTopPages> {

		@Override
		protected ObjectEntryTopPages createDTO() {
			return new ObjectEntryTopPages();
		}

		@Override
		protected ObjectEntryTopPages[] createDTOArray(int size) {
			return new ObjectEntryTopPages[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "topPages")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "totalCount")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectEntryTopPages objectEntryTopPages, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "topPages")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					TopPage[] topPagesArray =
						new TopPage[jsonParserFieldValues.length];

					for (int i = 0; i < topPagesArray.length; i++) {
						topPagesArray[i] = TopPageSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					objectEntryTopPages.setTopPages(topPagesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "totalCount")) {
				if (jsonParserFieldValue != null) {
					objectEntryTopPages.setTotalCount(
						Long.valueOf((String)jsonParserFieldValue));
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