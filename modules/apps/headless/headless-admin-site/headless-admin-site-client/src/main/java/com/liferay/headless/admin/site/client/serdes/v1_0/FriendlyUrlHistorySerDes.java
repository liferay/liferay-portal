/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FriendlyUrlHistory;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class FriendlyUrlHistorySerDes {

	public static FriendlyUrlHistory toDTO(String json) {
		FriendlyUrlHistoryJSONParser friendlyUrlHistoryJSONParser =
			new FriendlyUrlHistoryJSONParser();

		return friendlyUrlHistoryJSONParser.parseToDTO(json);
	}

	public static FriendlyUrlHistory[] toDTOs(String json) {
		FriendlyUrlHistoryJSONParser friendlyUrlHistoryJSONParser =
			new FriendlyUrlHistoryJSONParser();

		return friendlyUrlHistoryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FriendlyUrlHistory friendlyUrlHistory) {
		if (friendlyUrlHistory == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (friendlyUrlHistory.getFriendlyUrlPath_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath_i18n\": ");

			if (friendlyUrlHistory.getFriendlyUrlPath_i18n() instanceof
					String) {

				sb.append("\"");
				sb.append((String)friendlyUrlHistory.getFriendlyUrlPath_i18n());
				sb.append("\"");
			}
			else {
				sb.append(friendlyUrlHistory.getFriendlyUrlPath_i18n());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FriendlyUrlHistoryJSONParser friendlyUrlHistoryJSONParser =
			new FriendlyUrlHistoryJSONParser();

		return friendlyUrlHistoryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FriendlyUrlHistory friendlyUrlHistory) {

		if (friendlyUrlHistory == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (friendlyUrlHistory.getFriendlyUrlPath_i18n() == null) {
			map.put("friendlyUrlPath_i18n", null);
		}
		else {
			map.put(
				"friendlyUrlPath_i18n",
				String.valueOf(friendlyUrlHistory.getFriendlyUrlPath_i18n()));
		}

		return map;
	}

	public static class FriendlyUrlHistoryJSONParser
		extends BaseJSONParser<FriendlyUrlHistory> {

		@Override
		protected FriendlyUrlHistory createDTO() {
			return new FriendlyUrlHistory();
		}

		@Override
		protected FriendlyUrlHistory[] createDTOArray(int size) {
			return new FriendlyUrlHistory[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "friendlyUrlPath_i18n")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FriendlyUrlHistory friendlyUrlHistory, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "friendlyUrlPath_i18n")) {
				if (jsonParserFieldValue != null) {
					friendlyUrlHistory.setFriendlyUrlPath_i18n(
						(Object)jsonParserFieldValue);
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