/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.seo.studio.rest.client.serdes.v1_0;

import com.liferay.seo.studio.rest.client.dto.v1_0.CrawlHit;
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
public class CrawlHitSerDes {

	public static CrawlHit toDTO(String json) {
		CrawlHitJSONParser crawlHitJSONParser = new CrawlHitJSONParser();

		return crawlHitJSONParser.parseToDTO(json);
	}

	public static CrawlHit[] toDTOs(String json) {
		CrawlHitJSONParser crawlHitJSONParser = new CrawlHitJSONParser();

		return crawlHitJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CrawlHit crawlHit) {
		if (crawlHit == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (crawlHit.getCanonicalUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"canonicalUrl\": ");

			sb.append("\"");

			sb.append(_escape(crawlHit.getCanonicalUrl()));

			sb.append("\"");
		}

		if (crawlHit.getLinks() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"links\": ");

			sb.append("[");

			for (int i = 0; i < crawlHit.getLinks().length; i++) {
				sb.append(_toJSON(crawlHit.getLinks()[i]));

				if ((i + 1) < crawlHit.getLinks().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (crawlHit.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(crawlHit.getTitle()));

			sb.append("\"");
		}

		if (crawlHit.getUrl() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"url\": ");

			sb.append("\"");

			sb.append(_escape(crawlHit.getUrl()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CrawlHitJSONParser crawlHitJSONParser = new CrawlHitJSONParser();

		return crawlHitJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(CrawlHit crawlHit) {
		if (crawlHit == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (crawlHit.getCanonicalUrl() == null) {
			map.put("canonicalUrl", null);
		}
		else {
			map.put("canonicalUrl", String.valueOf(crawlHit.getCanonicalUrl()));
		}

		if (crawlHit.getLinks() == null) {
			map.put("links", null);
		}
		else {
			map.put("links", String.valueOf(crawlHit.getLinks()));
		}

		if (crawlHit.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(crawlHit.getTitle()));
		}

		if (crawlHit.getUrl() == null) {
			map.put("url", null);
		}
		else {
			map.put("url", String.valueOf(crawlHit.getUrl()));
		}

		return map;
	}

	public static class CrawlHitJSONParser extends BaseJSONParser<CrawlHit> {

		@Override
		protected CrawlHit createDTO() {
			return new CrawlHit();
		}

		@Override
		protected CrawlHit[] createDTOArray(int size) {
			return new CrawlHit[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "canonicalUrl")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "links")) {
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
			CrawlHit crawlHit, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "canonicalUrl")) {
				if (jsonParserFieldValue != null) {
					crawlHit.setCanonicalUrl((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "links")) {
				if (jsonParserFieldValue != null) {
					crawlHit.setLinks(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					crawlHit.setTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "url")) {
				if (jsonParserFieldValue != null) {
					crawlHit.setUrl((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:751220138