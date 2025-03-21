/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.BackgroundImage;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class BackgroundImageSerDes {

	public static BackgroundImage toDTO(String json) {
		BackgroundImageJSONParser backgroundImageJSONParser =
			new BackgroundImageJSONParser();

		return backgroundImageJSONParser.parseToDTO(json);
	}

	public static BackgroundImage[] toDTOs(String json) {
		BackgroundImageJSONParser backgroundImageJSONParser =
			new BackgroundImageJSONParser();

		return backgroundImageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(BackgroundImage backgroundImage) {
		if (backgroundImage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (backgroundImage.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			if (backgroundImage.getDescription() instanceof String) {
				sb.append("\"");
				sb.append((String)backgroundImage.getDescription());
				sb.append("\"");
			}
			else {
				sb.append(backgroundImage.getDescription());
			}
		}

		if (backgroundImage.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			if (backgroundImage.getTitle() instanceof String) {
				sb.append("\"");
				sb.append((String)backgroundImage.getTitle());
				sb.append("\"");
			}
			else {
				sb.append(backgroundImage.getTitle());
			}
		}

		if (backgroundImage.getUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"url\": ");

			if (backgroundImage.getUrl() instanceof String) {
				sb.append("\"");
				sb.append((String)backgroundImage.getUrl());
				sb.append("\"");
			}
			else {
				sb.append(backgroundImage.getUrl());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		BackgroundImageJSONParser backgroundImageJSONParser =
			new BackgroundImageJSONParser();

		return backgroundImageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(BackgroundImage backgroundImage) {
		if (backgroundImage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (backgroundImage.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(backgroundImage.getDescription()));
		}

		if (backgroundImage.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(backgroundImage.getTitle()));
		}

		if (backgroundImage.getUrl() == null) {
			map.put("url", null);
		}
		else {
			map.put("url", String.valueOf(backgroundImage.getUrl()));
		}

		return map;
	}

	public static class BackgroundImageJSONParser
		extends BaseJSONParser<BackgroundImage> {

		@Override
		protected BackgroundImage createDTO() {
			return new BackgroundImage();
		}

		@Override
		protected BackgroundImage[] createDTOArray(int size) {
			return new BackgroundImage[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			BackgroundImage backgroundImage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					backgroundImage.setDescription(
						(Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					backgroundImage.setTitle((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				if (jsonParserFieldValue != null) {
					backgroundImage.setUrl((Object)jsonParserFieldValue);
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