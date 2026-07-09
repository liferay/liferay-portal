/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.client.serdes.v1_0;

import com.liferay.headless.cmp.client.dto.v1_0.ContentCoverageEntry;
import com.liferay.headless.cmp.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Carolina Barbosa
 * @generated
 */
@Generated("")
public class ContentCoverageEntrySerDes {

	public static ContentCoverageEntry toDTO(String json) {
		ContentCoverageEntryJSONParser contentCoverageEntryJSONParser =
			new ContentCoverageEntryJSONParser();

		return contentCoverageEntryJSONParser.parseToDTO(json);
	}

	public static ContentCoverageEntry[] toDTOs(String json) {
		ContentCoverageEntryJSONParser contentCoverageEntryJSONParser =
			new ContentCoverageEntryJSONParser();

		return contentCoverageEntryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ContentCoverageEntry contentCoverageEntry) {
		if (contentCoverageEntry == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (contentCoverageEntry.getAssetCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetCount\": ");

			sb.append(contentCoverageEntry.getAssetCount());
		}

		if (contentCoverageEntry.getFunnelStageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"funnelStageId\": ");

			sb.append(contentCoverageEntry.getFunnelStageId());
		}

		if (contentCoverageEntry.getPersonaId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"personaId\": ");

			sb.append(contentCoverageEntry.getPersonaId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ContentCoverageEntryJSONParser contentCoverageEntryJSONParser =
			new ContentCoverageEntryJSONParser();

		return contentCoverageEntryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ContentCoverageEntry contentCoverageEntry) {

		if (contentCoverageEntry == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (contentCoverageEntry.getAssetCount() == null) {
			map.put("assetCount", null);
		}
		else {
			map.put(
				"assetCount",
				String.valueOf(contentCoverageEntry.getAssetCount()));
		}

		if (contentCoverageEntry.getFunnelStageId() == null) {
			map.put("funnelStageId", null);
		}
		else {
			map.put(
				"funnelStageId",
				String.valueOf(contentCoverageEntry.getFunnelStageId()));
		}

		if (contentCoverageEntry.getPersonaId() == null) {
			map.put("personaId", null);
		}
		else {
			map.put(
				"personaId",
				String.valueOf(contentCoverageEntry.getPersonaId()));
		}

		return map;
	}

	public static class ContentCoverageEntryJSONParser
		extends BaseJSONParser<ContentCoverageEntry> {

		@Override
		protected ContentCoverageEntry createDTO() {
			return new ContentCoverageEntry();
		}

		@Override
		protected ContentCoverageEntry[] createDTOArray(int size) {
			return new ContentCoverageEntry[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "assetCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "funnelStageId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "personaId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ContentCoverageEntry contentCoverageEntry,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "assetCount")) {
				if (jsonParserFieldValue != null) {
					contentCoverageEntry.setAssetCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "funnelStageId")) {
				if (jsonParserFieldValue != null) {
					contentCoverageEntry.setFunnelStageId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "personaId")) {
				if (jsonParserFieldValue != null) {
					contentCoverageEntry.setPersonaId(
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
// LIFERAY-REST-BUILDER-HASH:-101981393