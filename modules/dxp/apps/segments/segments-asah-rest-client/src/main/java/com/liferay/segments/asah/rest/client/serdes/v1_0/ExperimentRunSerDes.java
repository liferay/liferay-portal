/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.asah.rest.client.serdes.v1_0;

import com.liferay.segments.asah.rest.client.dto.v1_0.ExperimentRun;
import com.liferay.segments.asah.rest.client.dto.v1_0.ExperimentVariant;
import com.liferay.segments.asah.rest.client.json.BaseJSONParser;

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
public class ExperimentRunSerDes {

	public static ExperimentRun toDTO(String json) {
		ExperimentRunJSONParser experimentRunJSONParser =
			new ExperimentRunJSONParser();

		return experimentRunJSONParser.parseToDTO(json);
	}

	public static ExperimentRun[] toDTOs(String json) {
		ExperimentRunJSONParser experimentRunJSONParser =
			new ExperimentRunJSONParser();

		return experimentRunJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ExperimentRun experimentRun) {
		if (experimentRun == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (experimentRun.getConfidenceLevel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"confidenceLevel\": ");

			sb.append(experimentRun.getConfidenceLevel());
		}

		if (experimentRun.getExperimentVariants() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"experimentVariants\": ");

			sb.append("[");

			for (int i = 0; i < experimentRun.getExperimentVariants().length;
				 i++) {

				sb.append(
					String.valueOf(experimentRun.getExperimentVariants()[i]));

				if ((i + 1) < experimentRun.getExperimentVariants().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (experimentRun.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(_escape(experimentRun.getStatus()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ExperimentRunJSONParser experimentRunJSONParser =
			new ExperimentRunJSONParser();

		return experimentRunJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ExperimentRun experimentRun) {
		if (experimentRun == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (experimentRun.getConfidenceLevel() == null) {
			map.put("confidenceLevel", null);
		}
		else {
			map.put(
				"confidenceLevel",
				String.valueOf(experimentRun.getConfidenceLevel()));
		}

		if (experimentRun.getExperimentVariants() == null) {
			map.put("experimentVariants", null);
		}
		else {
			map.put(
				"experimentVariants",
				String.valueOf(experimentRun.getExperimentVariants()));
		}

		if (experimentRun.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(experimentRun.getStatus()));
		}

		return map;
	}

	public static class ExperimentRunJSONParser
		extends BaseJSONParser<ExperimentRun> {

		@Override
		protected ExperimentRun createDTO() {
			return new ExperimentRun();
		}

		@Override
		protected ExperimentRun[] createDTOArray(int size) {
			return new ExperimentRun[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "confidenceLevel")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "experimentVariants")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ExperimentRun experimentRun, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "confidenceLevel")) {
				if (jsonParserFieldValue != null) {
					experimentRun.setConfidenceLevel(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "experimentVariants")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ExperimentVariant[] experimentVariantsArray =
						new ExperimentVariant[jsonParserFieldValues.length];

					for (int i = 0; i < experimentVariantsArray.length; i++) {
						experimentVariantsArray[i] =
							ExperimentVariantSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					experimentRun.setExperimentVariants(
						experimentVariantsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					experimentRun.setStatus((String)jsonParserFieldValue);
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