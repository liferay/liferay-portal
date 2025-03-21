/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.AdaptedImage;
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
public class AdaptedImageSerDes {

	public static AdaptedImage toDTO(String json) {
		AdaptedImageJSONParser adaptedImageJSONParser =
			new AdaptedImageJSONParser();

		return adaptedImageJSONParser.parseToDTO(json);
	}

	public static AdaptedImage[] toDTOs(String json) {
		AdaptedImageJSONParser adaptedImageJSONParser =
			new AdaptedImageJSONParser();

		return adaptedImageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AdaptedImage adaptedImage) {
		if (adaptedImage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (adaptedImage.getContentUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentUrl\": ");

			sb.append("\"");

			sb.append(_escape(adaptedImage.getContentUrl()));

			sb.append("\"");
		}

		if (adaptedImage.getContentValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentValue\": ");

			sb.append("\"");

			sb.append(_escape(adaptedImage.getContentValue()));

			sb.append("\"");
		}

		if (adaptedImage.getHeight() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"height\": ");

			sb.append(adaptedImage.getHeight());
		}

		if (adaptedImage.getResolutionName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"resolutionName\": ");

			sb.append("\"");

			sb.append(_escape(adaptedImage.getResolutionName()));

			sb.append("\"");
		}

		if (adaptedImage.getSizeInBytes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sizeInBytes\": ");

			sb.append(adaptedImage.getSizeInBytes());
		}

		if (adaptedImage.getWidth() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"width\": ");

			sb.append(adaptedImage.getWidth());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AdaptedImageJSONParser adaptedImageJSONParser =
			new AdaptedImageJSONParser();

		return adaptedImageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AdaptedImage adaptedImage) {
		if (adaptedImage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (adaptedImage.getContentUrl() == null) {
			map.put("contentUrl", null);
		}
		else {
			map.put("contentUrl", String.valueOf(adaptedImage.getContentUrl()));
		}

		if (adaptedImage.getContentValue() == null) {
			map.put("contentValue", null);
		}
		else {
			map.put(
				"contentValue", String.valueOf(adaptedImage.getContentValue()));
		}

		if (adaptedImage.getHeight() == null) {
			map.put("height", null);
		}
		else {
			map.put("height", String.valueOf(adaptedImage.getHeight()));
		}

		if (adaptedImage.getResolutionName() == null) {
			map.put("resolutionName", null);
		}
		else {
			map.put(
				"resolutionName",
				String.valueOf(adaptedImage.getResolutionName()));
		}

		if (adaptedImage.getSizeInBytes() == null) {
			map.put("sizeInBytes", null);
		}
		else {
			map.put(
				"sizeInBytes", String.valueOf(adaptedImage.getSizeInBytes()));
		}

		if (adaptedImage.getWidth() == null) {
			map.put("width", null);
		}
		else {
			map.put("width", String.valueOf(adaptedImage.getWidth()));
		}

		return map;
	}

	public static class AdaptedImageJSONParser
		extends BaseJSONParser<AdaptedImage> {

		@Override
		protected AdaptedImage createDTO() {
			return new AdaptedImage();
		}

		@Override
		protected AdaptedImage[] createDTOArray(int size) {
			return new AdaptedImage[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "contentUrl")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "contentValue")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "height")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "resolutionName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sizeInBytes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "width")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AdaptedImage adaptedImage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "contentUrl")) {
				if (jsonParserFieldValue != null) {
					adaptedImage.setContentUrl((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentValue")) {
				if (jsonParserFieldValue != null) {
					adaptedImage.setContentValue((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "height")) {
				if (jsonParserFieldValue != null) {
					adaptedImage.setHeight(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "resolutionName")) {
				if (jsonParserFieldValue != null) {
					adaptedImage.setResolutionName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sizeInBytes")) {
				if (jsonParserFieldValue != null) {
					adaptedImage.setSizeInBytes(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "width")) {
				if (jsonParserFieldValue != null) {
					adaptedImage.setWidth(
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