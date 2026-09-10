/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.serdes.v1_0;

import com.liferay.analytics.cms.rest.client.dto.v1_0.Overview;
import com.liferay.analytics.cms.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
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
public class OverviewSerDes {

	public static Overview toDTO(String json) {
		OverviewJSONParser overviewJSONParser = new OverviewJSONParser();

		return overviewJSONParser.parseToDTO(json);
	}

	public static Overview[] toDTOs(String json) {
		OverviewJSONParser overviewJSONParser = new OverviewJSONParser();

		return overviewJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Overview overview) {
		if (overview == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (overview.getCategoriesCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"categoriesCount\": ");

			sb.append(overview.getCategoriesCount());
		}

		if (overview.getTagsCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tagsCount\": ");

			sb.append(overview.getTagsCount());
		}

		if (overview.getTotalCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalCount\": ");

			sb.append(overview.getTotalCount());
		}

		if (overview.getTrend() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"trend\": ");

			sb.append(String.valueOf(overview.getTrend()));
		}

		if (overview.getVocabulariesCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"vocabulariesCount\": ");

			sb.append(overview.getVocabulariesCount());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OverviewJSONParser overviewJSONParser = new OverviewJSONParser();

		return overviewJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Overview overview) {
		if (overview == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (overview.getCategoriesCount() == null) {
			map.put("categoriesCount", null);
		}
		else {
			map.put(
				"categoriesCount",
				String.valueOf(overview.getCategoriesCount()));
		}

		if (overview.getTagsCount() == null) {
			map.put("tagsCount", null);
		}
		else {
			map.put("tagsCount", String.valueOf(overview.getTagsCount()));
		}

		if (overview.getTotalCount() == null) {
			map.put("totalCount", null);
		}
		else {
			map.put("totalCount", String.valueOf(overview.getTotalCount()));
		}

		if (overview.getTrend() == null) {
			map.put("trend", null);
		}
		else {
			map.put("trend", String.valueOf(overview.getTrend()));
		}

		if (overview.getVocabulariesCount() == null) {
			map.put("vocabulariesCount", null);
		}
		else {
			map.put(
				"vocabulariesCount",
				String.valueOf(overview.getVocabulariesCount()));
		}

		return map;
	}

	public static class OverviewJSONParser extends BaseJSONParser<Overview> {

		@Override
		protected Overview createDTO() {
			return new Overview();
		}

		@Override
		protected Overview[] createDTOArray(int size) {
			return new Overview[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "categoriesCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "tagsCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "totalCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "trend")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "vocabulariesCount")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Overview overview, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "categoriesCount")) {
				if (jsonParserFieldValue != null) {
					overview.setCategoriesCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "tagsCount")) {
				if (jsonParserFieldValue != null) {
					overview.setTagsCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "totalCount")) {
				if (jsonParserFieldValue != null) {
					overview.setTotalCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "trend")) {
				if (jsonParserFieldValue != null) {
					overview.setTrend(
						TrendSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "vocabulariesCount")) {
				if (jsonParserFieldValue != null) {
					overview.setVocabulariesCount(
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
// LIFERAY-REST-BUILDER-HASH:-1196793333