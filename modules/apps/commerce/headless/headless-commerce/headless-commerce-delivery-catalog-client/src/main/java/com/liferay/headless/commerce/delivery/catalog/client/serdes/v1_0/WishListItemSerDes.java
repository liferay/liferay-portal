/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.WishListItem;
import com.liferay.headless.commerce.delivery.catalog.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public class WishListItemSerDes {

	public static WishListItem toDTO(String json) {
		WishListItemJSONParser wishListItemJSONParser =
			new WishListItemJSONParser();

		return wishListItemJSONParser.parseToDTO(json);
	}

	public static WishListItem[] toDTOs(String json) {
		WishListItemJSONParser wishListItemJSONParser =
			new WishListItemJSONParser();

		return wishListItemJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WishListItem wishListItem) {
		if (wishListItem == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (wishListItem.getFinalPrice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"finalPrice\": ");

			sb.append("\"");

			sb.append(_escape(wishListItem.getFinalPrice()));

			sb.append("\"");
		}

		if (wishListItem.getFriendlyURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyURL\": ");

			sb.append("\"");

			sb.append(_escape(wishListItem.getFriendlyURL()));

			sb.append("\"");
		}

		if (wishListItem.getIcon() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"icon\": ");

			sb.append("\"");

			sb.append(_escape(wishListItem.getIcon()));

			sb.append("\"");
		}

		if (wishListItem.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(wishListItem.getId());
		}

		if (wishListItem.getProductId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(wishListItem.getProductId());
		}

		if (wishListItem.getProductName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productName\": ");

			sb.append("\"");

			sb.append(_escape(wishListItem.getProductName()));

			sb.append("\"");
		}

		if (wishListItem.getSkuId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuId\": ");

			sb.append(wishListItem.getSkuId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WishListItemJSONParser wishListItemJSONParser =
			new WishListItemJSONParser();

		return wishListItemJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(WishListItem wishListItem) {
		if (wishListItem == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (wishListItem.getFinalPrice() == null) {
			map.put("finalPrice", null);
		}
		else {
			map.put("finalPrice", String.valueOf(wishListItem.getFinalPrice()));
		}

		if (wishListItem.getFriendlyURL() == null) {
			map.put("friendlyURL", null);
		}
		else {
			map.put(
				"friendlyURL", String.valueOf(wishListItem.getFriendlyURL()));
		}

		if (wishListItem.getIcon() == null) {
			map.put("icon", null);
		}
		else {
			map.put("icon", String.valueOf(wishListItem.getIcon()));
		}

		if (wishListItem.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(wishListItem.getId()));
		}

		if (wishListItem.getProductId() == null) {
			map.put("productId", null);
		}
		else {
			map.put("productId", String.valueOf(wishListItem.getProductId()));
		}

		if (wishListItem.getProductName() == null) {
			map.put("productName", null);
		}
		else {
			map.put(
				"productName", String.valueOf(wishListItem.getProductName()));
		}

		if (wishListItem.getSkuId() == null) {
			map.put("skuId", null);
		}
		else {
			map.put("skuId", String.valueOf(wishListItem.getSkuId()));
		}

		return map;
	}

	public static class WishListItemJSONParser
		extends BaseJSONParser<WishListItem> {

		@Override
		protected WishListItem createDTO() {
			return new WishListItem();
		}

		@Override
		protected WishListItem[] createDTOArray(int size) {
			return new WishListItem[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "finalPrice")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "friendlyURL")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "icon")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "skuId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WishListItem wishListItem, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "finalPrice")) {
				if (jsonParserFieldValue != null) {
					wishListItem.setFinalPrice((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "friendlyURL")) {
				if (jsonParserFieldValue != null) {
					wishListItem.setFriendlyURL((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "icon")) {
				if (jsonParserFieldValue != null) {
					wishListItem.setIcon((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					wishListItem.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				if (jsonParserFieldValue != null) {
					wishListItem.setProductId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productName")) {
				if (jsonParserFieldValue != null) {
					wishListItem.setProductName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "skuId")) {
				if (jsonParserFieldValue != null) {
					wishListItem.setSkuId(
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