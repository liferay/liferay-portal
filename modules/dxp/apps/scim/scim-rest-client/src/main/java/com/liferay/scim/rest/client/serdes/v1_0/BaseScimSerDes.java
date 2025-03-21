/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.BaseScim;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class BaseScimSerDes {

	public static BaseScim toDTO(String json) {
		BaseScimJSONParser baseScimJSONParser = new BaseScimJSONParser();

		return baseScimJSONParser.parseToDTO(json);
	}

	public static BaseScim[] toDTOs(String json) {
		BaseScimJSONParser baseScimJSONParser = new BaseScimJSONParser();

		return baseScimJSONParser.parseToDTOs(json);
	}

	public static String toJSON(BaseScim baseScim) {
		if (baseScim == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (baseScim.getExternalId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalId\": ");

			sb.append("\"");

			sb.append(_escape(baseScim.getExternalId()));

			sb.append("\"");
		}

		if (baseScim.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(baseScim.getId()));

			sb.append("\"");
		}

		if (baseScim.getMeta() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"meta\": ");

			sb.append(String.valueOf(baseScim.getMeta()));
		}

		if (baseScim.getSchemas() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schemas\": ");

			sb.append("[");

			for (int i = 0; i < baseScim.getSchemas().length; i++) {
				sb.append(_toJSON(baseScim.getSchemas()[i]));

				if ((i + 1) < baseScim.getSchemas().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		BaseScimJSONParser baseScimJSONParser = new BaseScimJSONParser();

		return baseScimJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(BaseScim baseScim) {
		if (baseScim == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (baseScim.getExternalId() == null) {
			map.put("externalId", null);
		}
		else {
			map.put("externalId", String.valueOf(baseScim.getExternalId()));
		}

		if (baseScim.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(baseScim.getId()));
		}

		if (baseScim.getMeta() == null) {
			map.put("meta", null);
		}
		else {
			map.put("meta", String.valueOf(baseScim.getMeta()));
		}

		if (baseScim.getSchemas() == null) {
			map.put("schemas", null);
		}
		else {
			map.put("schemas", String.valueOf(baseScim.getSchemas()));
		}

		return map;
	}

	public static class BaseScimJSONParser extends BaseJSONParser<BaseScim> {

		@Override
		protected BaseScim createDTO() {
			return new BaseScim();
		}

		@Override
		protected BaseScim[] createDTOArray(int size) {
			return new BaseScim[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "meta")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "schemas")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			BaseScim baseScim, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalId")) {
				if (jsonParserFieldValue != null) {
					baseScim.setExternalId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					baseScim.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "meta")) {
				if (jsonParserFieldValue != null) {
					baseScim.setMeta(
						MetaSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "schemas")) {
				if (jsonParserFieldValue != null) {
					baseScim.setSchemas(
						toStrings((Object[])jsonParserFieldValue));
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