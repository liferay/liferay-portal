/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfiguration;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfigurationList;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class ProductConfigurationListSerDes {

	public static ProductConfigurationList toDTO(String json) {
		ProductConfigurationListJSONParser productConfigurationListJSONParser =
			new ProductConfigurationListJSONParser();

		return productConfigurationListJSONParser.parseToDTO(json);
	}

	public static ProductConfigurationList[] toDTOs(String json) {
		ProductConfigurationListJSONParser productConfigurationListJSONParser =
			new ProductConfigurationListJSONParser();

		return productConfigurationListJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ProductConfigurationList productConfigurationList) {

		if (productConfigurationList == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (productConfigurationList.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(productConfigurationList.getActions()));
		}

		if (productConfigurationList.getCatalogExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					productConfigurationList.
						getCatalogExternalReferenceCode()));

			sb.append("\"");
		}

		if (productConfigurationList.getCatalogId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogId\": ");

			sb.append(productConfigurationList.getCatalogId());
		}

		if (productConfigurationList.getCreateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					productConfigurationList.getCreateDate()));

			sb.append("\"");
		}

		if (productConfigurationList.getDisplayDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					productConfigurationList.getDisplayDate()));

			sb.append("\"");
		}

		if (productConfigurationList.getExpirationDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expirationDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					productConfigurationList.getExpirationDate()));

			sb.append("\"");
		}

		if (productConfigurationList.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(productConfigurationList.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (productConfigurationList.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(productConfigurationList.getId());
		}

		if (productConfigurationList.getMaster() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"master\": ");

			sb.append(productConfigurationList.getMaster());
		}

		if (productConfigurationList.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(productConfigurationList.getName()));

			sb.append("\"");
		}

		if (productConfigurationList.getNeverExpire() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"neverExpire\": ");

			sb.append(productConfigurationList.getNeverExpire());
		}

		if (productConfigurationList.getParentProductConfigurationListId() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentProductConfigurationListId\": ");

			sb.append(
				productConfigurationList.getParentProductConfigurationListId());
		}

		if (productConfigurationList.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(productConfigurationList.getPriority());
		}

		if (productConfigurationList.getProductConfigurations() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurations\": ");

			sb.append("[");

			for (int i = 0;
				 i < productConfigurationList.getProductConfigurations().length;
				 i++) {

				sb.append(
					String.valueOf(
						productConfigurationList.getProductConfigurations()
							[i]));

				if ((i + 1) < productConfigurationList.
						getProductConfigurations().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProductConfigurationListJSONParser productConfigurationListJSONParser =
			new ProductConfigurationListJSONParser();

		return productConfigurationListJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ProductConfigurationList productConfigurationList) {

		if (productConfigurationList == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (productConfigurationList.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions",
				String.valueOf(productConfigurationList.getActions()));
		}

		if (productConfigurationList.getCatalogExternalReferenceCode() ==
				null) {

			map.put("catalogExternalReferenceCode", null);
		}
		else {
			map.put(
				"catalogExternalReferenceCode",
				String.valueOf(
					productConfigurationList.
						getCatalogExternalReferenceCode()));
		}

		if (productConfigurationList.getCatalogId() == null) {
			map.put("catalogId", null);
		}
		else {
			map.put(
				"catalogId",
				String.valueOf(productConfigurationList.getCatalogId()));
		}

		if (productConfigurationList.getCreateDate() == null) {
			map.put("createDate", null);
		}
		else {
			map.put(
				"createDate",
				liferayToJSONDateFormat.format(
					productConfigurationList.getCreateDate()));
		}

		if (productConfigurationList.getDisplayDate() == null) {
			map.put("displayDate", null);
		}
		else {
			map.put(
				"displayDate",
				liferayToJSONDateFormat.format(
					productConfigurationList.getDisplayDate()));
		}

		if (productConfigurationList.getExpirationDate() == null) {
			map.put("expirationDate", null);
		}
		else {
			map.put(
				"expirationDate",
				liferayToJSONDateFormat.format(
					productConfigurationList.getExpirationDate()));
		}

		if (productConfigurationList.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					productConfigurationList.getExternalReferenceCode()));
		}

		if (productConfigurationList.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(productConfigurationList.getId()));
		}

		if (productConfigurationList.getMaster() == null) {
			map.put("master", null);
		}
		else {
			map.put(
				"master", String.valueOf(productConfigurationList.getMaster()));
		}

		if (productConfigurationList.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(productConfigurationList.getName()));
		}

		if (productConfigurationList.getNeverExpire() == null) {
			map.put("neverExpire", null);
		}
		else {
			map.put(
				"neverExpire",
				String.valueOf(productConfigurationList.getNeverExpire()));
		}

		if (productConfigurationList.getParentProductConfigurationListId() ==
				null) {

			map.put("parentProductConfigurationListId", null);
		}
		else {
			map.put(
				"parentProductConfigurationListId",
				String.valueOf(
					productConfigurationList.
						getParentProductConfigurationListId()));
		}

		if (productConfigurationList.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put(
				"priority",
				String.valueOf(productConfigurationList.getPriority()));
		}

		if (productConfigurationList.getProductConfigurations() == null) {
			map.put("productConfigurations", null);
		}
		else {
			map.put(
				"productConfigurations",
				String.valueOf(
					productConfigurationList.getProductConfigurations()));
		}

		return map;
	}

	public static class ProductConfigurationListJSONParser
		extends BaseJSONParser<ProductConfigurationList> {

		@Override
		protected ProductConfigurationList createDTO() {
			return new ProductConfigurationList();
		}

		@Override
		protected ProductConfigurationList[] createDTOArray(int size) {
			return new ProductConfigurationList[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "catalogExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "catalogId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "displayDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "master")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentProductConfigurationListId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productConfigurations")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ProductConfigurationList productConfigurationList,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					productConfigurationList.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "catalogExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					productConfigurationList.setCatalogExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "catalogId")) {
				if (jsonParserFieldValue != null) {
					productConfigurationList.setCatalogId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				if (jsonParserFieldValue != null) {
					productConfigurationList.setCreateDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayDate")) {
				if (jsonParserFieldValue != null) {
					productConfigurationList.setDisplayDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				if (jsonParserFieldValue != null) {
					productConfigurationList.setExpirationDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					productConfigurationList.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					productConfigurationList.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "master")) {
				if (jsonParserFieldValue != null) {
					productConfigurationList.setMaster(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					productConfigurationList.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "neverExpire")) {
				if (jsonParserFieldValue != null) {
					productConfigurationList.setNeverExpire(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"parentProductConfigurationListId")) {

				if (jsonParserFieldValue != null) {
					productConfigurationList.
						setParentProductConfigurationListId(
							Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					productConfigurationList.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productConfigurations")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ProductConfiguration[] productConfigurationsArray =
						new ProductConfiguration[jsonParserFieldValues.length];

					for (int i = 0; i < productConfigurationsArray.length;
						 i++) {

						productConfigurationsArray[i] =
							ProductConfigurationSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					productConfigurationList.setProductConfigurations(
						productConfigurationsArray);
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