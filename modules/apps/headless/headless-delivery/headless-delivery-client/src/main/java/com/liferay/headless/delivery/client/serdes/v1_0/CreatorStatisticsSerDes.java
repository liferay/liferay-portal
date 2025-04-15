/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.CreatorStatistics;
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
public class CreatorStatisticsSerDes {

	public static CreatorStatistics toDTO(String json) {
		CreatorStatisticsJSONParser creatorStatisticsJSONParser =
			new CreatorStatisticsJSONParser();

		return creatorStatisticsJSONParser.parseToDTO(json);
	}

	public static CreatorStatistics[] toDTOs(String json) {
		CreatorStatisticsJSONParser creatorStatisticsJSONParser =
			new CreatorStatisticsJSONParser();

		return creatorStatisticsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CreatorStatistics creatorStatistics) {
		if (creatorStatistics == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (creatorStatistics.getJoinDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"joinDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					creatorStatistics.getJoinDate()));

			sb.append("\"");
		}

		if (creatorStatistics.getLastPostDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastPostDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					creatorStatistics.getLastPostDate()));

			sb.append("\"");
		}

		if (creatorStatistics.getPostsNumber() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"postsNumber\": ");

			sb.append(creatorStatistics.getPostsNumber());
		}

		if (creatorStatistics.getRank() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rank\": ");

			sb.append("\"");

			sb.append(_escape(creatorStatistics.getRank()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CreatorStatisticsJSONParser creatorStatisticsJSONParser =
			new CreatorStatisticsJSONParser();

		return creatorStatisticsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		CreatorStatistics creatorStatistics) {

		if (creatorStatistics == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (creatorStatistics.getJoinDate() == null) {
			map.put("joinDate", null);
		}
		else {
			map.put(
				"joinDate",
				liferayToJSONDateFormat.format(
					creatorStatistics.getJoinDate()));
		}

		if (creatorStatistics.getLastPostDate() == null) {
			map.put("lastPostDate", null);
		}
		else {
			map.put(
				"lastPostDate",
				liferayToJSONDateFormat.format(
					creatorStatistics.getLastPostDate()));
		}

		if (creatorStatistics.getPostsNumber() == null) {
			map.put("postsNumber", null);
		}
		else {
			map.put(
				"postsNumber",
				String.valueOf(creatorStatistics.getPostsNumber()));
		}

		if (creatorStatistics.getRank() == null) {
			map.put("rank", null);
		}
		else {
			map.put("rank", String.valueOf(creatorStatistics.getRank()));
		}

		return map;
	}

	public static class CreatorStatisticsJSONParser
		extends BaseJSONParser<CreatorStatistics> {

		@Override
		protected CreatorStatistics createDTO() {
			return new CreatorStatistics();
		}

		@Override
		protected CreatorStatistics[] createDTOArray(int size) {
			return new CreatorStatistics[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "joinDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "lastPostDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "postsNumber")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "rank")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CreatorStatistics creatorStatistics, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "joinDate")) {
				if (jsonParserFieldValue != null) {
					creatorStatistics.setJoinDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "lastPostDate")) {
				if (jsonParserFieldValue != null) {
					creatorStatistics.setLastPostDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "postsNumber")) {
				if (jsonParserFieldValue != null) {
					creatorStatistics.setPostsNumber(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "rank")) {
				if (jsonParserFieldValue != null) {
					creatorStatistics.setRank((String)jsonParserFieldValue);
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