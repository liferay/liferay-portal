/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.Rating;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class RatingSerDes {

	public static Rating toDTO(String json) {
		RatingJSONParser ratingJSONParser = new RatingJSONParser();

		return ratingJSONParser.parseToDTO(json);
	}

	public static Rating[] toDTOs(String json) {
		RatingJSONParser ratingJSONParser = new RatingJSONParser();

		return ratingJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Rating rating) {
		if (rating == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (rating.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(rating.getActions()));
		}

		if (rating.getBestRating() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bestRating\": ");

			sb.append(rating.getBestRating());
		}

		if (rating.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(rating.getCreator()));
		}

		if (rating.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(rating.getDateCreated()));

			sb.append("\"");
		}

		if (rating.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(rating.getDateModified()));

			sb.append("\"");
		}

		if (rating.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(rating.getId());
		}

		if (rating.getRatingValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ratingValue\": ");

			sb.append(rating.getRatingValue());
		}

		if (rating.getWorstRating() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"worstRating\": ");

			sb.append(rating.getWorstRating());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		RatingJSONParser ratingJSONParser = new RatingJSONParser();

		return ratingJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Rating rating) {
		if (rating == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (rating.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(rating.getActions()));
		}

		if (rating.getBestRating() == null) {
			map.put("bestRating", null);
		}
		else {
			map.put("bestRating", String.valueOf(rating.getBestRating()));
		}

		if (rating.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(rating.getCreator()));
		}

		if (rating.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(rating.getDateCreated()));
		}

		if (rating.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(rating.getDateModified()));
		}

		if (rating.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(rating.getId()));
		}

		if (rating.getRatingValue() == null) {
			map.put("ratingValue", null);
		}
		else {
			map.put("ratingValue", String.valueOf(rating.getRatingValue()));
		}

		if (rating.getWorstRating() == null) {
			map.put("worstRating", null);
		}
		else {
			map.put("worstRating", String.valueOf(rating.getWorstRating()));
		}

		return map;
	}

	public static class RatingJSONParser extends BaseJSONParser<Rating> {

		@Override
		protected Rating createDTO() {
			return new Rating();
		}

		@Override
		protected Rating[] createDTOArray(int size) {
			return new Rating[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "bestRating")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "ratingValue")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "worstRating")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Rating rating, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					rating.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "bestRating")) {
				if (jsonParserFieldValue != null) {
					rating.setBestRating(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					rating.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					rating.setDateCreated(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					rating.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					rating.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "ratingValue")) {
				if (jsonParserFieldValue != null) {
					rating.setRatingValue(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "worstRating")) {
				if (jsonParserFieldValue != null) {
					rating.setWorstRating(
						Double.valueOf((String)jsonParserFieldValue));
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