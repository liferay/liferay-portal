/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.SyncProcess;
import com.liferay.exportimport.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class SyncProcessSerDes {

	public static SyncProcess toDTO(String json) {
		SyncProcessJSONParser syncProcessJSONParser =
			new SyncProcessJSONParser();

		return syncProcessJSONParser.parseToDTO(json);
	}

	public static SyncProcess[] toDTOs(String json) {
		SyncProcessJSONParser syncProcessJSONParser =
			new SyncProcessJSONParser();

		return syncProcessJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SyncProcess syncProcess) {
		if (syncProcess == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (syncProcess.getSiteTemplateId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteTemplateId\": ");

			sb.append(syncProcess.getSiteTemplateId());
		}

		if (syncProcess.getSiteTemplateName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteTemplateName\": ");

			sb.append("\"");

			sb.append(_escape(syncProcess.getSiteTemplateName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SyncProcessJSONParser syncProcessJSONParser =
			new SyncProcessJSONParser();

		return syncProcessJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SyncProcess syncProcess) {
		if (syncProcess == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (syncProcess.getSiteTemplateId() == null) {
			map.put("siteTemplateId", null);
		}
		else {
			map.put(
				"siteTemplateId",
				String.valueOf(syncProcess.getSiteTemplateId()));
		}

		if (syncProcess.getSiteTemplateName() == null) {
			map.put("siteTemplateName", null);
		}
		else {
			map.put(
				"siteTemplateName",
				String.valueOf(syncProcess.getSiteTemplateName()));
		}

		return map;
	}

	public static class SyncProcessJSONParser
		extends BaseJSONParser<SyncProcess> {

		@Override
		protected SyncProcess createDTO() {
			return new SyncProcess();
		}

		@Override
		protected SyncProcess[] createDTOArray(int size) {
			return new SyncProcess[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "siteTemplateId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteTemplateName")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SyncProcess syncProcess, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "siteTemplateId")) {
				if (jsonParserFieldValue != null) {
					syncProcess.setSiteTemplateId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteTemplateName")) {
				if (jsonParserFieldValue != null) {
					syncProcess.setSiteTemplateName(
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

		if (value instanceof Collection) {
			Collection<?> collection = (Collection<?>)value;

			return _toJSON(collection.toArray());
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
// LIFERAY-REST-BUILDER-HASH:-1181799034