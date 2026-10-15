/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.Event;
import com.liferay.osb.faro.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class EventSerDes {

	public static Event toDTO(String json) {
		EventJSONParser eventJSONParser = new EventJSONParser();

		return eventJSONParser.parseToDTO(json);
	}

	public static Event[] toDTOs(String json) {
		EventJSONParser eventJSONParser = new EventJSONParser();

		return eventJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Event event) {
		if (event == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (event.getApplicationId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"applicationId\": ");

			sb.append("\"");

			sb.append(_escape(event.getApplicationId()));

			sb.append("\"");
		}

		if (event.getAssetTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTitle\": ");

			sb.append("\"");

			sb.append(_escape(event.getAssetTitle()));

			sb.append("\"");
		}

		if (event.getAttributes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attributes\": ");

			sb.append(_toJSON(event.getAttributes()));
		}

		if (event.getCanonicalUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"canonicalUrl\": ");

			sb.append("\"");

			sb.append(_escape(event.getCanonicalUrl()));

			sb.append("\"");
		}

		if (event.getCreateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(event.getCreateDate()));

			sb.append("\"");
		}

		if (event.getIndividualId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individualId\": ");

			sb.append("\"");

			sb.append(_escape(event.getIndividualId()));

			sb.append("\"");
		}

		if (event.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(event.getName()));

			sb.append("\"");
		}

		if (event.getPageDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageDescription\": ");

			sb.append("\"");

			sb.append(_escape(event.getPageDescription()));

			sb.append("\"");
		}

		if (event.getPageKeywords() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageKeywords\": ");

			sb.append("\"");

			sb.append(_escape(event.getPageKeywords()));

			sb.append("\"");
		}

		if (event.getPageTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageTitle\": ");

			sb.append("\"");

			sb.append(_escape(event.getPageTitle()));

			sb.append("\"");
		}

		if (event.getReferrer() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"referrer\": ");

			sb.append("\"");

			sb.append(_escape(event.getReferrer()));

			sb.append("\"");
		}

		if (event.getUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"url\": ");

			sb.append("\"");

			sb.append(_escape(event.getUrl()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		EventJSONParser eventJSONParser = new EventJSONParser();

		return eventJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Event event) {
		if (event == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (event.getApplicationId() == null) {
			map.put("applicationId", null);
		}
		else {
			map.put("applicationId", String.valueOf(event.getApplicationId()));
		}

		if (event.getAssetTitle() == null) {
			map.put("assetTitle", null);
		}
		else {
			map.put("assetTitle", String.valueOf(event.getAssetTitle()));
		}

		if (event.getAttributes() == null) {
			map.put("attributes", null);
		}
		else {
			map.put("attributes", String.valueOf(event.getAttributes()));
		}

		if (event.getCanonicalUrl() == null) {
			map.put("canonicalUrl", null);
		}
		else {
			map.put("canonicalUrl", String.valueOf(event.getCanonicalUrl()));
		}

		if (event.getCreateDate() == null) {
			map.put("createDate", null);
		}
		else {
			map.put(
				"createDate",
				liferayToJSONDateFormat.format(event.getCreateDate()));
		}

		if (event.getIndividualId() == null) {
			map.put("individualId", null);
		}
		else {
			map.put("individualId", String.valueOf(event.getIndividualId()));
		}

		if (event.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(event.getName()));
		}

		if (event.getPageDescription() == null) {
			map.put("pageDescription", null);
		}
		else {
			map.put(
				"pageDescription", String.valueOf(event.getPageDescription()));
		}

		if (event.getPageKeywords() == null) {
			map.put("pageKeywords", null);
		}
		else {
			map.put("pageKeywords", String.valueOf(event.getPageKeywords()));
		}

		if (event.getPageTitle() == null) {
			map.put("pageTitle", null);
		}
		else {
			map.put("pageTitle", String.valueOf(event.getPageTitle()));
		}

		if (event.getReferrer() == null) {
			map.put("referrer", null);
		}
		else {
			map.put("referrer", String.valueOf(event.getReferrer()));
		}

		if (event.getUrl() == null) {
			map.put("url", null);
		}
		else {
			map.put("url", String.valueOf(event.getUrl()));
		}

		return map;
	}

	public static class EventJSONParser extends BaseJSONParser<Event> {

		@Override
		protected Event createDTO() {
			return new Event();
		}

		@Override
		protected Event[] createDTOArray(int size) {
			return new Event[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "applicationId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "attributes")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "canonicalUrl")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "individualId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageDescription")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageKeywords")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageTitle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "referrer")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Event event, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "applicationId")) {
				if (jsonParserFieldValue != null) {
					event.setApplicationId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				if (jsonParserFieldValue != null) {
					event.setAssetTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "attributes")) {
				if (jsonParserFieldValue != null) {
					event.setAttributes(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "canonicalUrl")) {
				if (jsonParserFieldValue != null) {
					event.setCanonicalUrl((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				if (jsonParserFieldValue != null) {
					event.setCreateDate(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "individualId")) {
				if (jsonParserFieldValue != null) {
					event.setIndividualId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					event.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageDescription")) {
				if (jsonParserFieldValue != null) {
					event.setPageDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageKeywords")) {
				if (jsonParserFieldValue != null) {
					event.setPageKeywords((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageTitle")) {
				if (jsonParserFieldValue != null) {
					event.setPageTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "referrer")) {
				if (jsonParserFieldValue != null) {
					event.setReferrer((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				if (jsonParserFieldValue != null) {
					event.setUrl((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-371656595