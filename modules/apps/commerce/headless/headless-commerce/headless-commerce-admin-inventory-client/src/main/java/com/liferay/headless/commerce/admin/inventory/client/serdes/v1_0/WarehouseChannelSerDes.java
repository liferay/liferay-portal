/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.WarehouseChannel;
import com.liferay.headless.commerce.admin.inventory.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public class WarehouseChannelSerDes {

	public static WarehouseChannel toDTO(String json) {
		WarehouseChannelJSONParser warehouseChannelJSONParser =
			new WarehouseChannelJSONParser();

		return warehouseChannelJSONParser.parseToDTO(json);
	}

	public static WarehouseChannel[] toDTOs(String json) {
		WarehouseChannelJSONParser warehouseChannelJSONParser =
			new WarehouseChannelJSONParser();

		return warehouseChannelJSONParser.parseToDTOs(json);
	}

	public static String toJSON(WarehouseChannel warehouseChannel) {
		if (warehouseChannel == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (warehouseChannel.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(warehouseChannel.getActions()));
		}

		if (warehouseChannel.getChannel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channel\": ");

			sb.append(String.valueOf(warehouseChannel.getChannel()));
		}

		if (warehouseChannel.getChannelExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(warehouseChannel.getChannelExternalReferenceCode()));

			sb.append("\"");
		}

		if (warehouseChannel.getChannelId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"channelId\": ");

			sb.append(warehouseChannel.getChannelId());
		}

		if (warehouseChannel.getWarehouseChannelId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseChannelId\": ");

			sb.append(warehouseChannel.getWarehouseChannelId());
		}

		if (warehouseChannel.getWarehouseExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(warehouseChannel.getWarehouseExternalReferenceCode()));

			sb.append("\"");
		}

		if (warehouseChannel.getWarehouseId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"warehouseId\": ");

			sb.append(warehouseChannel.getWarehouseId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		WarehouseChannelJSONParser warehouseChannelJSONParser =
			new WarehouseChannelJSONParser();

		return warehouseChannelJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(WarehouseChannel warehouseChannel) {
		if (warehouseChannel == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (warehouseChannel.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(warehouseChannel.getActions()));
		}

		if (warehouseChannel.getChannel() == null) {
			map.put("channel", null);
		}
		else {
			map.put("channel", String.valueOf(warehouseChannel.getChannel()));
		}

		if (warehouseChannel.getChannelExternalReferenceCode() == null) {
			map.put("channelExternalReferenceCode", null);
		}
		else {
			map.put(
				"channelExternalReferenceCode",
				String.valueOf(
					warehouseChannel.getChannelExternalReferenceCode()));
		}

		if (warehouseChannel.getChannelId() == null) {
			map.put("channelId", null);
		}
		else {
			map.put(
				"channelId", String.valueOf(warehouseChannel.getChannelId()));
		}

		if (warehouseChannel.getWarehouseChannelId() == null) {
			map.put("warehouseChannelId", null);
		}
		else {
			map.put(
				"warehouseChannelId",
				String.valueOf(warehouseChannel.getWarehouseChannelId()));
		}

		if (warehouseChannel.getWarehouseExternalReferenceCode() == null) {
			map.put("warehouseExternalReferenceCode", null);
		}
		else {
			map.put(
				"warehouseExternalReferenceCode",
				String.valueOf(
					warehouseChannel.getWarehouseExternalReferenceCode()));
		}

		if (warehouseChannel.getWarehouseId() == null) {
			map.put("warehouseId", null);
		}
		else {
			map.put(
				"warehouseId",
				String.valueOf(warehouseChannel.getWarehouseId()));
		}

		return map;
	}

	public static class WarehouseChannelJSONParser
		extends BaseJSONParser<WarehouseChannel> {

		@Override
		protected WarehouseChannel createDTO() {
			return new WarehouseChannel();
		}

		@Override
		protected WarehouseChannel[] createDTOArray(int size) {
			return new WarehouseChannel[size];
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
			else if (Objects.equals(
						jsonParserFieldName, "warehouseChannelId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"warehouseExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "warehouseId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			WarehouseChannel warehouseChannel, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					warehouseChannel.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "channel")) {
				if (jsonParserFieldValue != null) {
					warehouseChannel.setChannel(
						ChannelSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "channelExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					warehouseChannel.setChannelExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "channelId")) {
				if (jsonParserFieldValue != null) {
					warehouseChannel.setChannelId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "warehouseChannelId")) {

				if (jsonParserFieldValue != null) {
					warehouseChannel.setWarehouseChannelId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"warehouseExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					warehouseChannel.setWarehouseExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "warehouseId")) {
				if (jsonParserFieldValue != null) {
					warehouseChannel.setWarehouseId(
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