/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfigurationListChannel;
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
public class ProductConfigurationListChannelSerDes {

	public static ProductConfigurationListChannel toDTO(String json) {
		ProductConfigurationListChannelJSONParser
			productConfigurationListChannelJSONParser =
				new ProductConfigurationListChannelJSONParser();

		return productConfigurationListChannelJSONParser.parseToDTO(json);
	}

	public static ProductConfigurationListChannel[] toDTOs(String json) {
		ProductConfigurationListChannelJSONParser
			productConfigurationListChannelJSONParser =
				new ProductConfigurationListChannelJSONParser();

		return productConfigurationListChannelJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ProductConfigurationListChannel productConfigurationListChannel) {

		if (productConfigurationListChannel == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (productConfigurationListChannel.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(productConfigurationListChannel.getActions()));
		}

		if (productConfigurationListChannel.getChannel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channel\": ");

			sb.append(
				String.valueOf(productConfigurationListChannel.getChannel()));
		}

		if (productConfigurationListChannel.getChannelExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					productConfigurationListChannel.
						getChannelExternalReferenceCode()));

			sb.append("\"");
		}

		if (productConfigurationListChannel.getChannelId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelId\": ");

			sb.append(productConfigurationListChannel.getChannelId());
		}

		if (productConfigurationListChannel.getOrder() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"order\": ");

			sb.append(productConfigurationListChannel.getOrder());
		}

		if (productConfigurationListChannel.
				getProductConfigurationListChannelId() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListChannelId\": ");

			sb.append(
				productConfigurationListChannel.
					getProductConfigurationListChannelId());
		}

		if (productConfigurationListChannel.
				getProductConfigurationListExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					productConfigurationListChannel.
						getProductConfigurationListExternalReferenceCode()));

			sb.append("\"");
		}

		if (productConfigurationListChannel.getProductConfigurationListId() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productConfigurationListId\": ");

			sb.append(
				productConfigurationListChannel.
					getProductConfigurationListId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProductConfigurationListChannelJSONParser
			productConfigurationListChannelJSONParser =
				new ProductConfigurationListChannelJSONParser();

		return productConfigurationListChannelJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ProductConfigurationListChannel productConfigurationListChannel) {

		if (productConfigurationListChannel == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (productConfigurationListChannel.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions",
				String.valueOf(productConfigurationListChannel.getActions()));
		}

		if (productConfigurationListChannel.getChannel() == null) {
			map.put("channel", null);
		}
		else {
			map.put(
				"channel",
				String.valueOf(productConfigurationListChannel.getChannel()));
		}

		if (productConfigurationListChannel.getChannelExternalReferenceCode() ==
				null) {

			map.put("channelExternalReferenceCode", null);
		}
		else {
			map.put(
				"channelExternalReferenceCode",
				String.valueOf(
					productConfigurationListChannel.
						getChannelExternalReferenceCode()));
		}

		if (productConfigurationListChannel.getChannelId() == null) {
			map.put("channelId", null);
		}
		else {
			map.put(
				"channelId",
				String.valueOf(productConfigurationListChannel.getChannelId()));
		}

		if (productConfigurationListChannel.getOrder() == null) {
			map.put("order", null);
		}
		else {
			map.put(
				"order",
				String.valueOf(productConfigurationListChannel.getOrder()));
		}

		if (productConfigurationListChannel.
				getProductConfigurationListChannelId() == null) {

			map.put("productConfigurationListChannelId", null);
		}
		else {
			map.put(
				"productConfigurationListChannelId",
				String.valueOf(
					productConfigurationListChannel.
						getProductConfigurationListChannelId()));
		}

		if (productConfigurationListChannel.
				getProductConfigurationListExternalReferenceCode() == null) {

			map.put("productConfigurationListExternalReferenceCode", null);
		}
		else {
			map.put(
				"productConfigurationListExternalReferenceCode",
				String.valueOf(
					productConfigurationListChannel.
						getProductConfigurationListExternalReferenceCode()));
		}

		if (productConfigurationListChannel.getProductConfigurationListId() ==
				null) {

			map.put("productConfigurationListId", null);
		}
		else {
			map.put(
				"productConfigurationListId",
				String.valueOf(
					productConfigurationListChannel.
						getProductConfigurationListId()));
		}

		return map;
	}

	public static class ProductConfigurationListChannelJSONParser
		extends BaseJSONParser<ProductConfigurationListChannel> {

		@Override
		protected ProductConfigurationListChannel createDTO() {
			return new ProductConfigurationListChannel();
		}

		@Override
		protected ProductConfigurationListChannel[] createDTOArray(int size) {
			return new ProductConfigurationListChannel[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "channel")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "channelExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "channelId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "order")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productConfigurationListChannelId")) {

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
			ProductConfigurationListChannel productConfigurationListChannel,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					productConfigurationListChannel.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "channel")) {
				if (jsonParserFieldValue != null) {
					productConfigurationListChannel.setChannel(
						ChannelSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "channelExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					productConfigurationListChannel.
						setChannelExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "channelId")) {
				if (jsonParserFieldValue != null) {
					productConfigurationListChannel.setChannelId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "order")) {
				if (jsonParserFieldValue != null) {
					productConfigurationListChannel.setOrder(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productConfigurationListChannelId")) {

				if (jsonParserFieldValue != null) {
					productConfigurationListChannel.
						setProductConfigurationListChannelId(
							Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"productConfigurationListExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					productConfigurationListChannel.
						setProductConfigurationListExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productConfigurationListId")) {

				if (jsonParserFieldValue != null) {
					productConfigurationListChannel.
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