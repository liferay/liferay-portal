/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.HrefURLValue;
import com.liferay.headless.admin.site.client.dto.v1_0.SitePageURLValue;
import com.liferay.headless.admin.site.client.dto.v1_0.URLValue;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class URLValueSerDes {

	public static URLValue toDTO(String json) {
		URLValueJSONParser urlValueJSONParser = new URLValueJSONParser();

		return urlValueJSONParser.parseToDTO(json);
	}

	public static URLValue[] toDTOs(String json) {
		URLValueJSONParser urlValueJSONParser = new URLValueJSONParser();

		return urlValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(URLValue urlValue) {
		if (urlValue == null) {
			return "null";
		}

		URLValue.UrlType urlType = urlValue.getUrlType();

		if (urlType != null) {
			String urlTypeString = urlType.toString();

			if (urlTypeString.equals("Href")) {
				return HrefURLValueSerDes.toJSON((HrefURLValue)urlValue);
			}

			if (urlTypeString.equals("SitePage")) {
				return SitePageURLValueSerDes.toJSON(
					(SitePageURLValue)urlValue);
			}

			throw new IllegalArgumentException(
				"Unknown urlType " + urlTypeString);
		}
		else {
			throw new IllegalArgumentException("Missing urlType parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		URLValueJSONParser urlValueJSONParser = new URLValueJSONParser();

		return urlValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(URLValue urlValue) {
		if (urlValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (urlValue.getUrlType() == null) {
			map.put("urlType", null);
		}
		else {
			map.put("urlType", String.valueOf(urlValue.getUrlType()));
		}

		return map;
	}

	public static class URLValueJSONParser extends BaseJSONParser<URLValue> {

		@Override
		protected URLValue createDTO() {
			return null;
		}

		@Override
		protected URLValue[] createDTOArray(int size) {
			return new URLValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "urlType")) {
				return false;
			}

			return false;
		}

		@Override
		public URLValue parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object urlType = jsonMap.get("urlType");

			if (urlType != null) {
				String urlTypeString = urlType.toString();

				if (urlTypeString.equals("Href")) {
					return HrefURLValue.toDTO(json);
				}

				if (urlTypeString.equals("SitePage")) {
					return SitePageURLValue.toDTO(json);
				}

				throw new IllegalArgumentException(
					"Unknown urlType " + urlTypeString);
			}
			else {
				throw new IllegalArgumentException("Missing urlType parameter");
			}
		}

		@Override
		protected void setField(
			URLValue urlValue, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "urlType")) {
				if (jsonParserFieldValue != null) {
					urlValue.setUrlType(
						URLValue.UrlType.create((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:1281407845