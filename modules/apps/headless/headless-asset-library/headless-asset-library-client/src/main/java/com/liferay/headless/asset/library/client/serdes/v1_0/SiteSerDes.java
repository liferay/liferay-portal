/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.client.serdes.v1_0;

import com.liferay.headless.asset.library.client.dto.v1_0.Site;
import com.liferay.headless.asset.library.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Roberto Díaz
 * @generated
 */
@Generated("")
public class SiteSerDes {

	public static Site toDTO(String json) {
		SiteJSONParser siteJSONParser = new SiteJSONParser();

		return siteJSONParser.parseToDTO(json);
	}

	public static Site[] toDTOs(String json) {
		SiteJSONParser siteJSONParser = new SiteJSONParser();

		return siteJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Site site) {
		if (site == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (site.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(site.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (site.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(site.getId());
		}

		if (site.getLogo() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logo\": ");

			sb.append("\"");

			sb.append(_escape(site.getLogo()));

			sb.append("\"");
		}

		if (site.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(site.getName()));

			sb.append("\"");
		}

		if (site.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(site.getName_i18n()));
		}

		if (site.getSearchable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"searchable\": ");

			sb.append(site.getSearchable());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SiteJSONParser siteJSONParser = new SiteJSONParser();

		return siteJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Site site) {
		if (site == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (site.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(site.getExternalReferenceCode()));
		}

		if (site.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(site.getId()));
		}

		if (site.getLogo() == null) {
			map.put("logo", null);
		}
		else {
			map.put("logo", String.valueOf(site.getLogo()));
		}

		if (site.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(site.getName()));
		}

		if (site.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put("name_i18n", String.valueOf(site.getName_i18n()));
		}

		if (site.getSearchable() == null) {
			map.put("searchable", null);
		}
		else {
			map.put("searchable", String.valueOf(site.getSearchable()));
		}

		return map;
	}

	public static class SiteJSONParser extends BaseJSONParser<Site> {

		@Override
		protected Site createDTO() {
			return new Site();
		}

		@Override
		protected Site[] createDTOArray(int size) {
			return new Site[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "logo")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "searchable")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Site site, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					site.setExternalReferenceCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					site.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "logo")) {
				if (jsonParserFieldValue != null) {
					site.setLogo((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					site.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					site.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "searchable")) {
				if (jsonParserFieldValue != null) {
					site.setSearchable((Boolean)jsonParserFieldValue);
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