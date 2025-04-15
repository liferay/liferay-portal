/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfigurationListAccountGroup;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class ProductConfigurationListAccountGroupSerDes {

	public static ProductConfigurationListAccountGroup toDTO(String json) {
		ProductConfigurationListAccountGroupJSONParser
			productConfigurationListAccountGroupJSONParser =
				new ProductConfigurationListAccountGroupJSONParser();

		return productConfigurationListAccountGroupJSONParser.parseToDTO(json);
	}

	public static ProductConfigurationListAccountGroup[] toDTOs(String json) {
		ProductConfigurationListAccountGroupJSONParser
			productConfigurationListAccountGroupJSONParser =
				new ProductConfigurationListAccountGroupJSONParser();

		return productConfigurationListAccountGroupJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup) {

		if (productConfigurationListAccountGroup == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (productConfigurationListAccountGroup.getAccountGroup() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroup\": ");

			sb.append(
				String.valueOf(
					productConfigurationListAccountGroup.getAccountGroup()));
		}

		if (productConfigurationListAccountGroup.
				getAccountGroupExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroupExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					productConfigurationListAccountGroup.
						getAccountGroupExternalReferenceCode()));

			sb.append("\"");
		}

		if (productConfigurationListAccountGroup.getAccountGroupId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountGroupId\": ");

			sb.append(productConfigurationListAccountGroup.getAccountGroupId());
		}

		if (productConfigurationListAccountGroup.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(
				_toJSON(productConfigurationListAccountGroup.getActions()));
		}

		if (productConfigurationListAccountGroup.
				getProductConfigurationListAccountGroupId() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListAccountGroupId\": ");

			sb.append(
				productConfigurationListAccountGroup.
					getProductConfigurationListAccountGroupId());
		}

		if (productConfigurationListAccountGroup.
				getProductConfigurationListExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					productConfigurationListAccountGroup.
						getProductConfigurationListExternalReferenceCode()));

			sb.append("\"");
		}

		if (productConfigurationListAccountGroup.
				getProductConfigurationListId() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListId\": ");

			sb.append(
				productConfigurationListAccountGroup.
					getProductConfigurationListId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProductConfigurationListAccountGroupJSONParser
			productConfigurationListAccountGroupJSONParser =
				new ProductConfigurationListAccountGroupJSONParser();

		return productConfigurationListAccountGroupJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ProductConfigurationListAccountGroup
			productConfigurationListAccountGroup) {

		if (productConfigurationListAccountGroup == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (productConfigurationListAccountGroup.getAccountGroup() == null) {
			map.put("accountGroup", null);
		}
		else {
			map.put(
				"accountGroup",
				String.valueOf(
					productConfigurationListAccountGroup.getAccountGroup()));
		}

		if (productConfigurationListAccountGroup.
				getAccountGroupExternalReferenceCode() == null) {

			map.put("accountGroupExternalReferenceCode", null);
		}
		else {
			map.put(
				"accountGroupExternalReferenceCode",
				String.valueOf(
					productConfigurationListAccountGroup.
						getAccountGroupExternalReferenceCode()));
		}

		if (productConfigurationListAccountGroup.getAccountGroupId() == null) {
			map.put("accountGroupId", null);
		}
		else {
			map.put(
				"accountGroupId",
				String.valueOf(
					productConfigurationListAccountGroup.getAccountGroupId()));
		}

		if (productConfigurationListAccountGroup.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions",
				String.valueOf(
					productConfigurationListAccountGroup.getActions()));
		}

		if (productConfigurationListAccountGroup.
				getProductConfigurationListAccountGroupId() == null) {

			map.put("productConfigurationListAccountGroupId", null);
		}
		else {
			map.put(
				"productConfigurationListAccountGroupId",
				String.valueOf(
					productConfigurationListAccountGroup.
						getProductConfigurationListAccountGroupId()));
		}

		if (productConfigurationListAccountGroup.
				getProductConfigurationListExternalReferenceCode() == null) {

			map.put("productConfigurationListExternalReferenceCode", null);
		}
		else {
			map.put(
				"productConfigurationListExternalReferenceCode",
				String.valueOf(
					productConfigurationListAccountGroup.
						getProductConfigurationListExternalReferenceCode()));
		}

		if (productConfigurationListAccountGroup.
				getProductConfigurationListId() == null) {

			map.put("productConfigurationListId", null);
		}
		else {
			map.put(
				"productConfigurationListId",
				String.valueOf(
					productConfigurationListAccountGroup.
						getProductConfigurationListId()));
		}

		return map;
	}

	public static class ProductConfigurationListAccountGroupJSONParser
		extends BaseJSONParser<ProductConfigurationListAccountGroup> {

		@Override
		protected ProductConfigurationListAccountGroup createDTO() {
			return new ProductConfigurationListAccountGroup();
		}

		@Override
		protected ProductConfigurationListAccountGroup[] createDTOArray(
			int size) {

			return new ProductConfigurationListAccountGroup[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "accountGroup")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"accountGroupExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "accountGroupId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productConfigurationListAccountGroupId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productConfigurationListExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productConfigurationListId")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ProductConfigurationListAccountGroup
				productConfigurationListAccountGroup,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accountGroup")) {
				if (jsonParserFieldValue != null) {
					productConfigurationListAccountGroup.setAccountGroup(
						AccountGroupSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"accountGroupExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					productConfigurationListAccountGroup.
						setAccountGroupExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "accountGroupId")) {
				if (jsonParserFieldValue != null) {
					productConfigurationListAccountGroup.setAccountGroupId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					productConfigurationListAccountGroup.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productConfigurationListAccountGroupId")) {

				if (jsonParserFieldValue != null) {
					productConfigurationListAccountGroup.
						setProductConfigurationListAccountGroupId(
							Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productConfigurationListExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					productConfigurationListAccountGroup.
						setProductConfigurationListExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productConfigurationListId")) {

				if (jsonParserFieldValue != null) {
					productConfigurationListAccountGroup.
						setProductConfigurationListId(
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