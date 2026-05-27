/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.rest.client.serdes.v1_0;

import com.liferay.seo.studio.rest.client.dto.v1_0.CrawledPage;
import com.liferay.seo.studio.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Brooke Dalton
 * @generated
 */
@Generated("")
public class CrawledPageSerDes {

	public static CrawledPage toDTO(String json) {
		CrawledPageJSONParser crawledPageJSONParser =
			new CrawledPageJSONParser();

		return crawledPageJSONParser.parseToDTO(json);
	}

	public static CrawledPage[] toDTOs(String json) {
		CrawledPageJSONParser crawledPageJSONParser =
			new CrawledPageJSONParser();

		return crawledPageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CrawledPage crawledPage) {
		if (crawledPage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (crawledPage.getLinks() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"links\": ");

			sb.append("[");

			for (int i = 0; i < crawledPage.getLinks().length; i++) {
				sb.append(_toJSON(crawledPage.getLinks()[i]));

				if ((i + 1) < crawledPage.getLinks().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (crawledPage.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(crawledPage.getTitle()));

			sb.append("\"");
		}

		if (crawledPage.getUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"url\": ");

			sb.append("\"");

			sb.append(_escape(crawledPage.getUrl()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CrawledPageJSONParser crawledPageJSONParser =
			new CrawledPageJSONParser();

		return crawledPageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(CrawledPage crawledPage) {
		if (crawledPage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (crawledPage.getLinks() == null) {
			map.put("links", null);
		}
		else {
			map.put("links", String.valueOf(crawledPage.getLinks()));
		}

		if (crawledPage.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(crawledPage.getTitle()));
		}

		if (crawledPage.getUrl() == null) {
			map.put("url", null);
		}
		else {
			map.put("url", String.valueOf(crawledPage.getUrl()));
		}

		return map;
	}

	public static class CrawledPageJSONParser
		extends BaseJSONParser<CrawledPage> {

		@Override
		protected CrawledPage createDTO() {
			return new CrawledPage();
		}

		@Override
		protected CrawledPage[] createDTOArray(int size) {
			return new CrawledPage[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "links")) {
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
			CrawledPage crawledPage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "links")) {
				if (jsonParserFieldValue != null) {
					crawledPage.setLinks(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					crawledPage.setTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				if (jsonParserFieldValue != null) {
					crawledPage.setUrl((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-164810651