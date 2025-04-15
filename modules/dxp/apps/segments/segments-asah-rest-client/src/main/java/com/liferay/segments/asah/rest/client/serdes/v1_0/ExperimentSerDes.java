/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.rest.client.serdes.v1_0;

import com.liferay.segments.asah.rest.client.dto.v1_0.Experiment;
import com.liferay.segments.asah.rest.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ExperimentSerDes {

	public static Experiment toDTO(String json) {
		ExperimentJSONParser experimentJSONParser = new ExperimentJSONParser();

		return experimentJSONParser.parseToDTO(json);
	}

	public static Experiment[] toDTOs(String json) {
		ExperimentJSONParser experimentJSONParser = new ExperimentJSONParser();

		return experimentJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Experiment experiment) {
		if (experiment == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (experiment.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(experiment.getDateCreated()));

			sb.append("\"");
		}

		if (experiment.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(experiment.getDateModified()));

			sb.append("\"");
		}

		if (experiment.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(experiment.getDescription()));

			sb.append("\"");
		}

		if (experiment.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(experiment.getId()));

			sb.append("\"");
		}

		if (experiment.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(experiment.getName()));

			sb.append("\"");
		}

		if (experiment.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(experiment.getSiteId());
		}

		if (experiment.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(_escape(experiment.getStatus()));

			sb.append("\"");
		}

		if (experiment.getWinnerVariantId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"winnerVariantId\": ");

			sb.append(experiment.getWinnerVariantId());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ExperimentJSONParser experimentJSONParser = new ExperimentJSONParser();

		return experimentJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Experiment experiment) {
		if (experiment == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (experiment.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(experiment.getDateCreated()));
		}

		if (experiment.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(experiment.getDateModified()));
		}

		if (experiment.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(experiment.getDescription()));
		}

		if (experiment.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(experiment.getId()));
		}

		if (experiment.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(experiment.getName()));
		}

		if (experiment.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(experiment.getSiteId()));
		}

		if (experiment.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(experiment.getStatus()));
		}

		if (experiment.getWinnerVariantId() == null) {
			map.put("winnerVariantId", null);
		}
		else {
			map.put(
				"winnerVariantId",
				String.valueOf(experiment.getWinnerVariantId()));
		}

		return map;
	}

	public static class ExperimentJSONParser
		extends BaseJSONParser<Experiment> {

		@Override
		protected Experiment createDTO() {
			return new Experiment();
		}

		@Override
		protected Experiment[] createDTOArray(int size) {
			return new Experiment[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "winnerVariantId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Experiment experiment, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					experiment.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					experiment.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					experiment.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					experiment.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					experiment.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					experiment.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					experiment.setStatus((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "winnerVariantId")) {
				if (jsonParserFieldValue != null) {
					experiment.setWinnerVariantId(
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