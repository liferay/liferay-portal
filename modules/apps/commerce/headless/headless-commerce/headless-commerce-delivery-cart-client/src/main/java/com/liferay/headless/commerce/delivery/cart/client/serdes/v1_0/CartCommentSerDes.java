/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.cart.client.serdes.v1_0;

import com.liferay.headless.commerce.delivery.cart.client.dto.v1_0.CartComment;
import com.liferay.headless.commerce.delivery.cart.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class CartCommentSerDes {

	public static CartComment toDTO(String json) {
		CartCommentJSONParser cartCommentJSONParser =
			new CartCommentJSONParser();

		return cartCommentJSONParser.parseToDTO(json);
	}

	public static CartComment[] toDTOs(String json) {
		CartCommentJSONParser cartCommentJSONParser =
			new CartCommentJSONParser();

		return cartCommentJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CartComment cartComment) {
		if (cartComment == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (cartComment.getAuthor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"author\": ");

			sb.append("\"");

			sb.append(_escape(cartComment.getAuthor()));

			sb.append("\"");
		}

		if (cartComment.getAuthorId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"authorId\": ");

			sb.append(cartComment.getAuthorId());
		}

		if (cartComment.getAuthorPortraitURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"authorPortraitURL\": ");

			sb.append("\"");

			sb.append(_escape(cartComment.getAuthorPortraitURL()));

			sb.append("\"");
		}

		if (cartComment.getContent() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"content\": ");

			sb.append("\"");

			sb.append(_escape(cartComment.getContent()));

			sb.append("\"");
		}

		if (cartComment.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(cartComment.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (cartComment.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(cartComment.getId());
		}

		if (cartComment.getModifiedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifiedDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(cartComment.getModifiedDate()));

			sb.append("\"");
		}

		if (cartComment.getOrderId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"orderId\": ");

			sb.append(cartComment.getOrderId());
		}

		if (cartComment.getRestricted() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"restricted\": ");

			sb.append(cartComment.getRestricted());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CartCommentJSONParser cartCommentJSONParser =
			new CartCommentJSONParser();

		return cartCommentJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(CartComment cartComment) {
		if (cartComment == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (cartComment.getAuthor() == null) {
			map.put("author", null);
		}
		else {
			map.put("author", String.valueOf(cartComment.getAuthor()));
		}

		if (cartComment.getAuthorId() == null) {
			map.put("authorId", null);
		}
		else {
			map.put("authorId", String.valueOf(cartComment.getAuthorId()));
		}

		if (cartComment.getAuthorPortraitURL() == null) {
			map.put("authorPortraitURL", null);
		}
		else {
			map.put(
				"authorPortraitURL",
				String.valueOf(cartComment.getAuthorPortraitURL()));
		}

		if (cartComment.getContent() == null) {
			map.put("content", null);
		}
		else {
			map.put("content", String.valueOf(cartComment.getContent()));
		}

		if (cartComment.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(cartComment.getExternalReferenceCode()));
		}

		if (cartComment.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(cartComment.getId()));
		}

		if (cartComment.getModifiedDate() == null) {
			map.put("modifiedDate", null);
		}
		else {
			map.put(
				"modifiedDate",
				liferayToJSONDateFormat.format(cartComment.getModifiedDate()));
		}

		if (cartComment.getOrderId() == null) {
			map.put("orderId", null);
		}
		else {
			map.put("orderId", String.valueOf(cartComment.getOrderId()));
		}

		if (cartComment.getRestricted() == null) {
			map.put("restricted", null);
		}
		else {
			map.put("restricted", String.valueOf(cartComment.getRestricted()));
		}

		return map;
	}

	public static class CartCommentJSONParser
		extends BaseJSONParser<CartComment> {

		@Override
		protected CartComment createDTO() {
			return new CartComment();
		}

		@Override
		protected CartComment[] createDTOArray(int size) {
			return new CartComment[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "author")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "authorId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "authorPortraitURL")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "content")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "orderId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "restricted")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CartComment cartComment, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "author")) {
				if (jsonParserFieldValue != null) {
					cartComment.setAuthor((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "authorId")) {
				if (jsonParserFieldValue != null) {
					cartComment.setAuthorId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "authorPortraitURL")) {
				if (jsonParserFieldValue != null) {
					cartComment.setAuthorPortraitURL(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "content")) {
				if (jsonParserFieldValue != null) {
					cartComment.setContent((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					cartComment.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					cartComment.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				if (jsonParserFieldValue != null) {
					cartComment.setModifiedDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "orderId")) {
				if (jsonParserFieldValue != null) {
					cartComment.setOrderId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "restricted")) {
				if (jsonParserFieldValue != null) {
					cartComment.setRestricted((Boolean)jsonParserFieldValue);
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