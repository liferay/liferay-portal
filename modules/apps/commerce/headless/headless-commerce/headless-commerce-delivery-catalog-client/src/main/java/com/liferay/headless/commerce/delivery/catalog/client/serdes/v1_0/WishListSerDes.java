/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.WishList;
import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.WishListItem;
import com.liferay.headless.commerce.delivery.catalog.client.json.BaseJSONParser;

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
public class WishListSerDes {

	public static WishList toDTO(String json) {
		WishListJSONParser wishListJSONParser = new WishListJSONParser();

		return wishListJSONParser.parseToDTO(json);
	}

	public static WishList[] toDTOs(String json) {
		WishListJSONParser wishListJSONParser = new WishListJSONParser();

		return wishListJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WishList wishList) {
		if (wishList == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (wishList.getDefaultWishList() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultWishList\": ");

			sb.append(wishList.getDefaultWishList());
		}

		if (wishList.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(wishList.getId());
		}

		if (wishList.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(wishList.getName()));

			sb.append("\"");
		}

		if (wishList.getWishListItems() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"wishListItems\": ");

			sb.append("[");

			for (int i = 0; i < wishList.getWishListItems().length; i++) {
				sb.append(String.valueOf(wishList.getWishListItems()[i]));

				if ((i + 1) < wishList.getWishListItems().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WishListJSONParser wishListJSONParser = new WishListJSONParser();

		return wishListJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(WishList wishList) {
		if (wishList == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (wishList.getDefaultWishList() == null) {
			map.put("defaultWishList", null);
		}
		else {
			map.put(
				"defaultWishList",
				String.valueOf(wishList.getDefaultWishList()));
		}

		if (wishList.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(wishList.getId()));
		}

		if (wishList.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(wishList.getName()));
		}

		if (wishList.getWishListItems() == null) {
			map.put("wishListItems", null);
		}
		else {
			map.put(
				"wishListItems", String.valueOf(wishList.getWishListItems()));
		}

		return map;
	}

	public static class WishListJSONParser extends BaseJSONParser<WishList> {

		@Override
		protected WishList createDTO() {
			return new WishList();
		}

		@Override
		protected WishList[] createDTOArray(int size) {
			return new WishList[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "defaultWishList")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "wishListItems")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WishList wishList, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "defaultWishList")) {
				if (jsonParserFieldValue != null) {
					wishList.setDefaultWishList((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					wishList.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					wishList.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "wishListItems")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					WishListItem[] wishListItemsArray =
						new WishListItem[jsonParserFieldValues.length];

					for (int i = 0; i < wishListItemsArray.length; i++) {
						wishListItemsArray[i] = WishListItemSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					wishList.setWishListItems(wishListItemsArray);
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