/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.settings.rest.client.serdes.v1_0;

import com.liferay.analytics.settings.rest.client.dto.v1_0.DataSource;
import com.liferay.analytics.settings.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Riccardo Ferrari
 * @generated
 */
@Generated("")
public class DataSourceSerDes {

	public static DataSource toDTO(String json) {
		DataSourceJSONParser dataSourceJSONParser = new DataSourceJSONParser();

		return dataSourceJSONParser.parseToDTO(json);
	}

	public static DataSource[] toDTOs(String json) {
		DataSourceJSONParser dataSourceJSONParser = new DataSourceJSONParser();

		return dataSourceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(DataSource dataSource) {
		if (dataSource == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (dataSource.getCommerceChannelIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"commerceChannelIds\": ");

			sb.append("[");

			for (int i = 0; i < dataSource.getCommerceChannelIds().length;
				 i++) {

				sb.append(dataSource.getCommerceChannelIds()[i]);

				if ((i + 1) < dataSource.getCommerceChannelIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (dataSource.getDataSourceId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataSourceId\": ");

			sb.append("\"");

			sb.append(_escape(dataSource.getDataSourceId()));

			sb.append("\"");
		}

		if (dataSource.getSiteIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteIds\": ");

			sb.append("[");

			for (int i = 0; i < dataSource.getSiteIds().length; i++) {
				sb.append(dataSource.getSiteIds()[i]);

				if ((i + 1) < dataSource.getSiteIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DataSourceJSONParser dataSourceJSONParser = new DataSourceJSONParser();

		return dataSourceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(DataSource dataSource) {
		if (dataSource == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (dataSource.getCommerceChannelIds() == null) {
			map.put("commerceChannelIds", null);
		}
		else {
			map.put(
				"commerceChannelIds",
				String.valueOf(dataSource.getCommerceChannelIds()));
		}

		if (dataSource.getDataSourceId() == null) {
			map.put("dataSourceId", null);
		}
		else {
			map.put(
				"dataSourceId", String.valueOf(dataSource.getDataSourceId()));
		}

		if (dataSource.getSiteIds() == null) {
			map.put("siteIds", null);
		}
		else {
			map.put("siteIds", String.valueOf(dataSource.getSiteIds()));
		}

		return map;
	}

	public static class DataSourceJSONParser
		extends BaseJSONParser<DataSource> {

		@Override
		protected DataSource createDTO() {
			return new DataSource();
		}

		@Override
		protected DataSource[] createDTOArray(int size) {
			return new DataSource[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "commerceChannelIds")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dataSourceId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteIds")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DataSource dataSource, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "commerceChannelIds")) {
				if (jsonParserFieldValue != null) {
					dataSource.setCommerceChannelIds(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dataSourceId")) {
				if (jsonParserFieldValue != null) {
					dataSource.setDataSourceId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteIds")) {
				if (jsonParserFieldValue != null) {
					dataSource.setSiteIds(
						toLongs((Object[])jsonParserFieldValue));
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