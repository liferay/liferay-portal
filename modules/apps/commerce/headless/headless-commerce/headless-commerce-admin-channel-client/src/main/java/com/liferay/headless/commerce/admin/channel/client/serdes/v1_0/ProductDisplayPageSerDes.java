/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.channel.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.channel.client.dto.v1_0.ProductDisplayPage;
import com.liferay.headless.commerce.admin.channel.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class ProductDisplayPageSerDes {

	public static ProductDisplayPage toDTO(String json) {
		ProductDisplayPageJSONParser productDisplayPageJSONParser =
			new ProductDisplayPageJSONParser();

		return productDisplayPageJSONParser.parseToDTO(json);
	}

	public static ProductDisplayPage[] toDTOs(String json) {
		ProductDisplayPageJSONParser productDisplayPageJSONParser =
			new ProductDisplayPageJSONParser();

		return productDisplayPageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ProductDisplayPage productDisplayPage) {
		if (productDisplayPage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (productDisplayPage.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(productDisplayPage.getActions()));
		}

		if (productDisplayPage.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(productDisplayPage.getId());
		}

		if (productDisplayPage.getPageTemplateUuid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageTemplateUuid\": ");

			sb.append("\"");

			sb.append(_escape(productDisplayPage.getPageTemplateUuid()));

			sb.append("\"");
		}

		if (productDisplayPage.getPageUuid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageUuid\": ");

			sb.append("\"");

			sb.append(_escape(productDisplayPage.getPageUuid()));

			sb.append("\"");
		}

		if (productDisplayPage.getProductExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(productDisplayPage.getProductExternalReferenceCode()));

			sb.append("\"");
		}

		if (productDisplayPage.getProductId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(productDisplayPage.getProductId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProductDisplayPageJSONParser productDisplayPageJSONParser =
			new ProductDisplayPageJSONParser();

		return productDisplayPageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ProductDisplayPage productDisplayPage) {

		if (productDisplayPage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (productDisplayPage.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(productDisplayPage.getActions()));
		}

		if (productDisplayPage.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(productDisplayPage.getId()));
		}

		if (productDisplayPage.getPageTemplateUuid() == null) {
			map.put("pageTemplateUuid", null);
		}
		else {
			map.put(
				"pageTemplateUuid",
				String.valueOf(productDisplayPage.getPageTemplateUuid()));
		}

		if (productDisplayPage.getPageUuid() == null) {
			map.put("pageUuid", null);
		}
		else {
			map.put(
				"pageUuid", String.valueOf(productDisplayPage.getPageUuid()));
		}

		if (productDisplayPage.getProductExternalReferenceCode() == null) {
			map.put("productExternalReferenceCode", null);
		}
		else {
			map.put(
				"productExternalReferenceCode",
				String.valueOf(
					productDisplayPage.getProductExternalReferenceCode()));
		}

		if (productDisplayPage.getProductId() == null) {
			map.put("productId", null);
		}
		else {
			map.put(
				"productId", String.valueOf(productDisplayPage.getProductId()));
		}

		return map;
	}

	public static class ProductDisplayPageJSONParser
		extends BaseJSONParser<ProductDisplayPage> {

		@Override
		protected ProductDisplayPage createDTO() {
			return new ProductDisplayPage();
		}

		@Override
		protected ProductDisplayPage[] createDTOArray(int size) {
			return new ProductDisplayPage[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageTemplateUuid")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageUuid")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ProductDisplayPage productDisplayPage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					productDisplayPage.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					productDisplayPage.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageTemplateUuid")) {
				if (jsonParserFieldValue != null) {
					productDisplayPage.setPageTemplateUuid(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageUuid")) {
				if (jsonParserFieldValue != null) {
					productDisplayPage.setPageUuid(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					productDisplayPage.setProductExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				if (jsonParserFieldValue != null) {
					productDisplayPage.setProductId(
						Long.valueOf((String)jsonParserFieldValue));
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