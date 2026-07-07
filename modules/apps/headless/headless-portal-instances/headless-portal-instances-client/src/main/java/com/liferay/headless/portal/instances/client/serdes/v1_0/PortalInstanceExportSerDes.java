/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.client.serdes.v1_0;

import com.liferay.headless.portal.instances.client.dto.v1_0.PortalInstanceExport;
import com.liferay.headless.portal.instances.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alberto Chaparro
 * @generated
 */
@Generated("")
public class PortalInstanceExportSerDes {

	public static PortalInstanceExport toDTO(String json) {
		PortalInstanceExportJSONParser portalInstanceExportJSONParser =
			new PortalInstanceExportJSONParser();

		return portalInstanceExportJSONParser.parseToDTO(json);
	}

	public static PortalInstanceExport[] toDTOs(String json) {
		PortalInstanceExportJSONParser portalInstanceExportJSONParser =
			new PortalInstanceExportJSONParser();

		return portalInstanceExportJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PortalInstanceExport portalInstanceExport) {
		if (portalInstanceExport == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (portalInstanceExport.getExportedPartitionName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"exportedPartitionName\": ");

			sb.append("\"");

			sb.append(_escape(portalInstanceExport.getExportedPartitionName()));

			sb.append("\"");
		}

		if (portalInstanceExport.getSourcePartitionName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sourcePartitionName\": ");

			sb.append("\"");

			sb.append(_escape(portalInstanceExport.getSourcePartitionName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PortalInstanceExportJSONParser portalInstanceExportJSONParser =
			new PortalInstanceExportJSONParser();

		return portalInstanceExportJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PortalInstanceExport portalInstanceExport) {

		if (portalInstanceExport == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (portalInstanceExport.getExportedPartitionName() == null) {
			map.put("exportedPartitionName", null);
		}
		else {
			map.put(
				"exportedPartitionName",
				String.valueOf(
					portalInstanceExport.getExportedPartitionName()));
		}

		if (portalInstanceExport.getSourcePartitionName() == null) {
			map.put("sourcePartitionName", null);
		}
		else {
			map.put(
				"sourcePartitionName",
				String.valueOf(portalInstanceExport.getSourcePartitionName()));
		}

		return map;
	}

	public static class PortalInstanceExportJSONParser
		extends BaseJSONParser<PortalInstanceExport> {

		@Override
		protected PortalInstanceExport createDTO() {
			return new PortalInstanceExport();
		}

		@Override
		protected PortalInstanceExport[] createDTOArray(int size) {
			return new PortalInstanceExport[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "exportedPartitionName")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "sourcePartitionName")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PortalInstanceExport portalInstanceExport,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "exportedPartitionName")) {
				if (jsonParserFieldValue != null) {
					portalInstanceExport.setExportedPartitionName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "sourcePartitionName")) {

				if (jsonParserFieldValue != null) {
					portalInstanceExport.setSourcePartitionName(
						(String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:650498961