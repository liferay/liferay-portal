/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.client.serdes.v1_0;

import com.liferay.analytics.settings.rest.client.dto.v1_0.CommerceChannel;
import com.liferay.analytics.settings.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class CommerceChannelSerDes {

	public static CommerceChannel toDTO(String json) {
		CommerceChannelJSONParser commerceChannelJSONParser =
			new CommerceChannelJSONParser();

		return commerceChannelJSONParser.parseToDTO(json);
	}

	public static CommerceChannel[] toDTOs(String json) {
		CommerceChannelJSONParser commerceChannelJSONParser =
			new CommerceChannelJSONParser();

		return commerceChannelJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CommerceChannel commerceChannel) {
		if (commerceChannel == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (commerceChannel.getChannelName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelName\": ");

			sb.append("\"");

			sb.append(_escape(commerceChannel.getChannelName()));

			sb.append("\"");
		}

		if (commerceChannel.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(commerceChannel.getId());
		}

		if (commerceChannel.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(commerceChannel.getName()));

			sb.append("\"");
		}

		if (commerceChannel.getSiteName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteName\": ");

			sb.append("\"");

			sb.append(_escape(commerceChannel.getSiteName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CommerceChannelJSONParser commerceChannelJSONParser =
			new CommerceChannelJSONParser();

		return commerceChannelJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(CommerceChannel commerceChannel) {
		if (commerceChannel == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (commerceChannel.getChannelName() == null) {
			map.put("channelName", null);
		}
		else {
			map.put(
				"channelName",
				String.valueOf(commerceChannel.getChannelName()));
		}

		if (commerceChannel.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(commerceChannel.getId()));
		}

		if (commerceChannel.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(commerceChannel.getName()));
		}

		if (commerceChannel.getSiteName() == null) {
			map.put("siteName", null);
		}
		else {
			map.put("siteName", String.valueOf(commerceChannel.getSiteName()));
		}

		return map;
	}

	public static class CommerceChannelJSONParser
		extends BaseJSONParser<CommerceChannel> {

		@Override
		protected CommerceChannel createDTO() {
			return new CommerceChannel();
		}

		@Override
		protected CommerceChannel[] createDTOArray(int size) {
			return new CommerceChannel[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "channelName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteName")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CommerceChannel commerceChannel, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "channelName")) {
				if (jsonParserFieldValue != null) {
					commerceChannel.setChannelName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					commerceChannel.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					commerceChannel.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteName")) {
				if (jsonParserFieldValue != null) {
					commerceChannel.setSiteName((String)jsonParserFieldValue);
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