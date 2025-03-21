/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Catalog;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class CatalogSerDes {

	public static Catalog toDTO(String json) {
		CatalogJSONParser catalogJSONParser = new CatalogJSONParser();

		return catalogJSONParser.parseToDTO(json);
	}

	public static Catalog[] toDTOs(String json) {
		CatalogJSONParser catalogJSONParser = new CatalogJSONParser();

		return catalogJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Catalog catalog) {
		if (catalog == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (catalog.getAccountId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountId\": ");

			sb.append(catalog.getAccountId());
		}

		if (catalog.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(catalog.getActions()));
		}

		if (catalog.getCurrencyCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyCode\": ");

			sb.append("\"");

			sb.append(_escape(catalog.getCurrencyCode()));

			sb.append("\"");
		}

		if (catalog.getCurrencyExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(catalog.getCurrencyExternalReferenceCode()));

			sb.append("\"");
		}

		if (catalog.getCurrencyId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"currencyId\": ");

			sb.append(catalog.getCurrencyId());
		}

		if (catalog.getDefaultLanguageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultLanguageId\": ");

			sb.append("\"");

			sb.append(_escape(catalog.getDefaultLanguageId()));

			sb.append("\"");
		}

		if (catalog.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(catalog.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (catalog.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(catalog.getId());
		}

		if (catalog.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(catalog.getName()));

			sb.append("\"");
		}

		if (catalog.getSystem() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"system\": ");

			sb.append(catalog.getSystem());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CatalogJSONParser catalogJSONParser = new CatalogJSONParser();

		return catalogJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Catalog catalog) {
		if (catalog == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (catalog.getAccountId() == null) {
			map.put("accountId", null);
		}
		else {
			map.put("accountId", String.valueOf(catalog.getAccountId()));
		}

		if (catalog.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(catalog.getActions()));
		}

		if (catalog.getCurrencyCode() == null) {
			map.put("currencyCode", null);
		}
		else {
			map.put("currencyCode", String.valueOf(catalog.getCurrencyCode()));
		}

		if (catalog.getCurrencyExternalReferenceCode() == null) {
			map.put("currencyExternalReferenceCode", null);
		}
		else {
			map.put(
				"currencyExternalReferenceCode",
				String.valueOf(catalog.getCurrencyExternalReferenceCode()));
		}

		if (catalog.getCurrencyId() == null) {
			map.put("currencyId", null);
		}
		else {
			map.put("currencyId", String.valueOf(catalog.getCurrencyId()));
		}

		if (catalog.getDefaultLanguageId() == null) {
			map.put("defaultLanguageId", null);
		}
		else {
			map.put(
				"defaultLanguageId",
				String.valueOf(catalog.getDefaultLanguageId()));
		}

		if (catalog.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(catalog.getExternalReferenceCode()));
		}

		if (catalog.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(catalog.getId()));
		}

		if (catalog.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(catalog.getName()));
		}

		if (catalog.getSystem() == null) {
			map.put("system", null);
		}
		else {
			map.put("system", String.valueOf(catalog.getSystem()));
		}

		return map;
	}

	public static class CatalogJSONParser extends BaseJSONParser<Catalog> {

		@Override
		protected Catalog createDTO() {
			return new Catalog();
		}

		@Override
		protected Catalog[] createDTOArray(int size) {
			return new Catalog[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "accountId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "currencyCode")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "currencyExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "currencyId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultLanguageId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "system")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Catalog catalog, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accountId")) {
				if (jsonParserFieldValue != null) {
					catalog.setAccountId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					catalog.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "currencyCode")) {
				if (jsonParserFieldValue != null) {
					catalog.setCurrencyCode((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "currencyExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					catalog.setCurrencyExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "currencyId")) {
				if (jsonParserFieldValue != null) {
					catalog.setCurrencyId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultLanguageId")) {
				if (jsonParserFieldValue != null) {
					catalog.setDefaultLanguageId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					catalog.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					catalog.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					catalog.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "system")) {
				if (jsonParserFieldValue != null) {
					catalog.setSystem((Boolean)jsonParserFieldValue);
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