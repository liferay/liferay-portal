/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cmp.client.serdes.v1_0;

import com.liferay.headless.cmp.client.dto.v1_0.ContentCoverage;
import com.liferay.headless.cmp.client.dto.v1_0.ContentCoverageEntry;
import com.liferay.headless.cmp.client.dto.v1_0.FunnelStage;
import com.liferay.headless.cmp.client.dto.v1_0.Persona;
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
public class ContentCoverageSerDes {

	public static ContentCoverage toDTO(String json) {
		ContentCoverageJSONParser contentCoverageJSONParser =
			new ContentCoverageJSONParser();

		return contentCoverageJSONParser.parseToDTO(json);
	}

	public static ContentCoverage[] toDTOs(String json) {
		ContentCoverageJSONParser contentCoverageJSONParser =
			new ContentCoverageJSONParser();

		return contentCoverageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ContentCoverage contentCoverage) {
		if (contentCoverage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (contentCoverage.getAssetCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetCount\": ");

			sb.append(contentCoverage.getAssetCount());
		}

		if (contentCoverage.getContentCoverageEntries() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentCoverageEntries\": ");

			sb.append("[");

			for (int i = 0;
				 i < contentCoverage.getContentCoverageEntries().length; i++) {

				sb.append(
					String.valueOf(
						contentCoverage.getContentCoverageEntries()[i]));

				if ((i + 1) <
						contentCoverage.getContentCoverageEntries().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (contentCoverage.getFunnelStages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"funnelStages\": ");

			sb.append("[");

			for (int i = 0; i < contentCoverage.getFunnelStages().length; i++) {
				sb.append(String.valueOf(contentCoverage.getFunnelStages()[i]));

				if ((i + 1) < contentCoverage.getFunnelStages().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (contentCoverage.getPersonas() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"personas\": ");

			sb.append("[");

			for (int i = 0; i < contentCoverage.getPersonas().length; i++) {
				sb.append(String.valueOf(contentCoverage.getPersonas()[i]));

				if ((i + 1) < contentCoverage.getPersonas().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ContentCoverageJSONParser contentCoverageJSONParser =
			new ContentCoverageJSONParser();

		return contentCoverageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ContentCoverage contentCoverage) {
		if (contentCoverage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (contentCoverage.getAssetCount() == null) {
			map.put("assetCount", null);
		}
		else {
			map.put(
				"assetCount", String.valueOf(contentCoverage.getAssetCount()));
		}

		if (contentCoverage.getContentCoverageEntries() == null) {
			map.put("contentCoverageEntries", null);
		}
		else {
			map.put(
				"contentCoverageEntries",
				String.valueOf(contentCoverage.getContentCoverageEntries()));
		}

		if (contentCoverage.getFunnelStages() == null) {
			map.put("funnelStages", null);
		}
		else {
			map.put(
				"funnelStages",
				String.valueOf(contentCoverage.getFunnelStages()));
		}

		if (contentCoverage.getPersonas() == null) {
			map.put("personas", null);
		}
		else {
			map.put("personas", String.valueOf(contentCoverage.getPersonas()));
		}

		return map;
	}

	public static class ContentCoverageJSONParser
		extends BaseJSONParser<ContentCoverage> {

		@Override
		protected ContentCoverage createDTO() {
			return new ContentCoverage();
		}

		@Override
		protected ContentCoverage[] createDTOArray(int size) {
			return new ContentCoverage[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "assetCount")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "contentCoverageEntries")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "funnelStages")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "personas")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ContentCoverage contentCoverage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "assetCount")) {
				if (jsonParserFieldValue != null) {
					contentCoverage.setAssetCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "contentCoverageEntries")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ContentCoverageEntry[] contentCoverageEntriesArray =
						new ContentCoverageEntry[jsonParserFieldValues.length];

					for (int i = 0; i < contentCoverageEntriesArray.length;
						 i++) {

						contentCoverageEntriesArray[i] =
							ContentCoverageEntrySerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					contentCoverage.setContentCoverageEntries(
						contentCoverageEntriesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "funnelStages")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FunnelStage[] funnelStagesArray =
						new FunnelStage[jsonParserFieldValues.length];

					for (int i = 0; i < funnelStagesArray.length; i++) {
						funnelStagesArray[i] = FunnelStageSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					contentCoverage.setFunnelStages(funnelStagesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "personas")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Persona[] personasArray =
						new Persona[jsonParserFieldValues.length];

					for (int i = 0; i < personasArray.length; i++) {
						personasArray[i] = PersonaSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					contentCoverage.setPersonas(personasArray);
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
// LIFERAY-REST-BUILDER-HASH:1339366572