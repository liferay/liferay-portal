/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.client.serdes.v1_0;

import com.liferay.headless.commerce.machine.learning.client.dto.v1_0.Product;
import com.liferay.headless.commerce.machine.learning.client.dto.v1_0.ProductOption;
import com.liferay.headless.commerce.machine.learning.client.dto.v1_0.ProductSpecification;
import com.liferay.headless.commerce.machine.learning.client.dto.v1_0.Sku;
import com.liferay.headless.commerce.machine.learning.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class ProductSerDes {

	public static Product toDTO(String json) {
		ProductJSONParser productJSONParser = new ProductJSONParser();

		return productJSONParser.parseToDTO(json);
	}

	public static Product[] toDTOs(String json) {
		ProductJSONParser productJSONParser = new ProductJSONParser();

		return productJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Product product) {
		if (product == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (product.getCatalogId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogId\": ");

			sb.append(product.getCatalogId());
		}

		if (product.getCategoryIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"categoryIds\": ");

			sb.append("[");

			for (int i = 0; i < product.getCategoryIds().length; i++) {
				sb.append(product.getCategoryIds()[i]);

				if ((i + 1) < product.getCategoryIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getCreateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(product.getCreateDate()));

			sb.append("\"");
		}

		if (product.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append(_toJSON(product.getCustomFields()));
		}

		if (product.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append(_toJSON(product.getDescription()));
		}

		if (product.getDisplayDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"displayDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(product.getDisplayDate()));

			sb.append("\"");
		}

		if (product.getExpirationDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expirationDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(product.getExpirationDate()));

			sb.append("\"");
		}

		if (product.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(product.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (product.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(product.getId());
		}

		if (product.getMetaDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metaDescription\": ");

			sb.append(_toJSON(product.getMetaDescription()));
		}

		if (product.getMetaKeyword() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metaKeyword\": ");

			sb.append(_toJSON(product.getMetaKeyword()));
		}

		if (product.getMetaTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metaTitle\": ");

			sb.append(_toJSON(product.getMetaTitle()));
		}

		if (product.getModifiedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifiedDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(product.getModifiedDate()));

			sb.append("\"");
		}

		if (product.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(product.getName()));
		}

		if (product.getProductChannelIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productChannelIds\": ");

			sb.append("[");

			for (int i = 0; i < product.getProductChannelIds().length; i++) {
				sb.append(product.getProductChannelIds()[i]);

				if ((i + 1) < product.getProductChannelIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getProductId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(product.getProductId());
		}

		if (product.getProductOptions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productOptions\": ");

			sb.append("[");

			for (int i = 0; i < product.getProductOptions().length; i++) {
				sb.append(String.valueOf(product.getProductOptions()[i]));

				if ((i + 1) < product.getProductOptions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getProductSpecifications() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productSpecifications\": ");

			sb.append("[");

			for (int i = 0; i < product.getProductSpecifications().length;
				 i++) {

				sb.append(
					String.valueOf(product.getProductSpecifications()[i]));

				if ((i + 1) < product.getProductSpecifications().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getProductType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productType\": ");

			sb.append("\"");

			sb.append(_escape(product.getProductType()));

			sb.append("\"");
		}

		if (product.getSkus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skus\": ");

			sb.append("[");

			for (int i = 0; i < product.getSkus().length; i++) {
				sb.append(String.valueOf(product.getSkus()[i]));

				if ((i + 1) < product.getSkus().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(product.getStatus());
		}

		if (product.getSubscriptionEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subscriptionEnabled\": ");

			sb.append(product.getSubscriptionEnabled());
		}

		if (product.getTags() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tags\": ");

			sb.append("[");

			for (int i = 0; i < product.getTags().length; i++) {
				sb.append(_toJSON(product.getTags()[i]));

				if ((i + 1) < product.getTags().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (product.getUrls() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"urls\": ");

			sb.append(_toJSON(product.getUrls()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProductJSONParser productJSONParser = new ProductJSONParser();

		return productJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Product product) {
		if (product == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (product.getCatalogId() == null) {
			map.put("catalogId", null);
		}
		else {
			map.put("catalogId", String.valueOf(product.getCatalogId()));
		}

		if (product.getCategoryIds() == null) {
			map.put("categoryIds", null);
		}
		else {
			map.put("categoryIds", String.valueOf(product.getCategoryIds()));
		}

		if (product.getCreateDate() == null) {
			map.put("createDate", null);
		}
		else {
			map.put(
				"createDate",
				liferayToJSONDateFormat.format(product.getCreateDate()));
		}

		if (product.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put("customFields", String.valueOf(product.getCustomFields()));
		}

		if (product.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(product.getDescription()));
		}

		if (product.getDisplayDate() == null) {
			map.put("displayDate", null);
		}
		else {
			map.put(
				"displayDate",
				liferayToJSONDateFormat.format(product.getDisplayDate()));
		}

		if (product.getExpirationDate() == null) {
			map.put("expirationDate", null);
		}
		else {
			map.put(
				"expirationDate",
				liferayToJSONDateFormat.format(product.getExpirationDate()));
		}

		if (product.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(product.getExternalReferenceCode()));
		}

		if (product.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(product.getId()));
		}

		if (product.getMetaDescription() == null) {
			map.put("metaDescription", null);
		}
		else {
			map.put(
				"metaDescription",
				String.valueOf(product.getMetaDescription()));
		}

		if (product.getMetaKeyword() == null) {
			map.put("metaKeyword", null);
		}
		else {
			map.put("metaKeyword", String.valueOf(product.getMetaKeyword()));
		}

		if (product.getMetaTitle() == null) {
			map.put("metaTitle", null);
		}
		else {
			map.put("metaTitle", String.valueOf(product.getMetaTitle()));
		}

		if (product.getModifiedDate() == null) {
			map.put("modifiedDate", null);
		}
		else {
			map.put(
				"modifiedDate",
				liferayToJSONDateFormat.format(product.getModifiedDate()));
		}

		if (product.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(product.getName()));
		}

		if (product.getProductChannelIds() == null) {
			map.put("productChannelIds", null);
		}
		else {
			map.put(
				"productChannelIds",
				String.valueOf(product.getProductChannelIds()));
		}

		if (product.getProductId() == null) {
			map.put("productId", null);
		}
		else {
			map.put("productId", String.valueOf(product.getProductId()));
		}

		if (product.getProductOptions() == null) {
			map.put("productOptions", null);
		}
		else {
			map.put(
				"productOptions", String.valueOf(product.getProductOptions()));
		}

		if (product.getProductSpecifications() == null) {
			map.put("productSpecifications", null);
		}
		else {
			map.put(
				"productSpecifications",
				String.valueOf(product.getProductSpecifications()));
		}

		if (product.getProductType() == null) {
			map.put("productType", null);
		}
		else {
			map.put("productType", String.valueOf(product.getProductType()));
		}

		if (product.getSkus() == null) {
			map.put("skus", null);
		}
		else {
			map.put("skus", String.valueOf(product.getSkus()));
		}

		if (product.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(product.getStatus()));
		}

		if (product.getSubscriptionEnabled() == null) {
			map.put("subscriptionEnabled", null);
		}
		else {
			map.put(
				"subscriptionEnabled",
				String.valueOf(product.getSubscriptionEnabled()));
		}

		if (product.getTags() == null) {
			map.put("tags", null);
		}
		else {
			map.put("tags", String.valueOf(product.getTags()));
		}

		if (product.getUrls() == null) {
			map.put("urls", null);
		}
		else {
			map.put("urls", String.valueOf(product.getUrls()));
		}

		return map;
	}

	public static class ProductJSONParser extends BaseJSONParser<Product> {

		@Override
		protected Product createDTO() {
			return new Product();
		}

		@Override
		protected Product[] createDTOArray(int size) {
			return new Product[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "catalogId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "categoryIds")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return true;
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
			else if (Objects.equals(jsonParserFieldName, "metaDescription")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "metaKeyword")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "metaTitle")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "productChannelIds")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productOptions")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productSpecifications")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "skus")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "subscriptionEnabled")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "tags")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "urls")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			Product product, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "catalogId")) {
				if (jsonParserFieldValue != null) {
					product.setCatalogId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "categoryIds")) {
				if (jsonParserFieldValue != null) {
					product.setCategoryIds(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				if (jsonParserFieldValue != null) {
					product.setCreateDate(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					product.setCustomFields(
						(Map<String, ?>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					product.setDescription(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "displayDate")) {
				if (jsonParserFieldValue != null) {
					product.setDisplayDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expirationDate")) {
				if (jsonParserFieldValue != null) {
					product.setExpirationDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					product.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					product.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "metaDescription")) {
				if (jsonParserFieldValue != null) {
					product.setMetaDescription(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "metaKeyword")) {
				if (jsonParserFieldValue != null) {
					product.setMetaKeyword(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "metaTitle")) {
				if (jsonParserFieldValue != null) {
					product.setMetaTitle(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				if (jsonParserFieldValue != null) {
					product.setModifiedDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					product.setName((Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productChannelIds")) {
				if (jsonParserFieldValue != null) {
					product.setProductChannelIds(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				if (jsonParserFieldValue != null) {
					product.setProductId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productOptions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ProductOption[] productOptionsArray =
						new ProductOption[jsonParserFieldValues.length];

					for (int i = 0; i < productOptionsArray.length; i++) {
						productOptionsArray[i] = ProductOptionSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					product.setProductOptions(productOptionsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productSpecifications")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ProductSpecification[] productSpecificationsArray =
						new ProductSpecification[jsonParserFieldValues.length];

					for (int i = 0; i < productSpecificationsArray.length;
						 i++) {

						productSpecificationsArray[i] =
							ProductSpecificationSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					product.setProductSpecifications(
						productSpecificationsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productType")) {
				if (jsonParserFieldValue != null) {
					product.setProductType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "skus")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Sku[] skusArray = new Sku[jsonParserFieldValues.length];

					for (int i = 0; i < skusArray.length; i++) {
						skusArray[i] = SkuSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					product.setSkus(skusArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					product.setStatus(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "subscriptionEnabled")) {

				if (jsonParserFieldValue != null) {
					product.setSubscriptionEnabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "tags")) {
				if (jsonParserFieldValue != null) {
					product.setTags(toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "urls")) {
				if (jsonParserFieldValue != null) {
					product.setUrls((Map<String, String>)jsonParserFieldValue);
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