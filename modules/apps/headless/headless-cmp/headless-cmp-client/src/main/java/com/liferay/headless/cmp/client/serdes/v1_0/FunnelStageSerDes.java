/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.client.serdes.v1_0;

import com.liferay.headless.cmp.client.dto.v1_0.FunnelStage;
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
public class FunnelStageSerDes {

	public static FunnelStage toDTO(String json) {
		FunnelStageJSONParser funnelStageJSONParser =
			new FunnelStageJSONParser();

		return funnelStageJSONParser.parseToDTO(json);
	}

	public static FunnelStage[] toDTOs(String json) {
		FunnelStageJSONParser funnelStageJSONParser =
			new FunnelStageJSONParser();

		return funnelStageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FunnelStage funnelStage) {
		if (funnelStage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (funnelStage.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(funnelStage.getDescription()));

			sb.append("\"");
		}

		if (funnelStage.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(funnelStage.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (funnelStage.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(funnelStage.getId());
		}

		if (funnelStage.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(funnelStage.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FunnelStageJSONParser funnelStageJSONParser =
			new FunnelStageJSONParser();

		return funnelStageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(FunnelStage funnelStage) {
		if (funnelStage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (funnelStage.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(funnelStage.getDescription()));
		}

		if (funnelStage.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(funnelStage.getExternalReferenceCode()));
		}

		if (funnelStage.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(funnelStage.getId()));
		}

		if (funnelStage.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(funnelStage.getName()));
		}

		return map;
	}

	public static class FunnelStageJSONParser
		extends BaseJSONParser<FunnelStage> {

		@Override
		protected FunnelStage createDTO() {
			return new FunnelStage();
		}

		@Override
		protected FunnelStage[] createDTOArray(int size) {
			return new FunnelStage[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FunnelStage funnelStage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					funnelStage.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					funnelStage.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					funnelStage.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					funnelStage.setName((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:1525350866