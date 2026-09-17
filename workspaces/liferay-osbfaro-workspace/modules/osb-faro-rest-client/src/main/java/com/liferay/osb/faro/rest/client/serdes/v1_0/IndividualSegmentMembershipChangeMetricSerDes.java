/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.IndividualSegmentMembershipChangeMetric;
import com.liferay.osb.faro.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Leslie Wong
 * @generated
 */
@Generated("")
public class IndividualSegmentMembershipChangeMetricSerDes {

	public static IndividualSegmentMembershipChangeMetric toDTO(String json) {
		IndividualSegmentMembershipChangeMetricJSONParser
			individualSegmentMembershipChangeMetricJSONParser =
				new IndividualSegmentMembershipChangeMetricJSONParser();

		return individualSegmentMembershipChangeMetricJSONParser.parseToDTO(
			json);
	}

	public static IndividualSegmentMembershipChangeMetric[] toDTOs(
		String json) {

		IndividualSegmentMembershipChangeMetricJSONParser
			individualSegmentMembershipChangeMetricJSONParser =
				new IndividualSegmentMembershipChangeMetricJSONParser();

		return individualSegmentMembershipChangeMetricJSONParser.parseToDTOs(
			json);
	}

	public static String toJSON(
		IndividualSegmentMembershipChangeMetric
			individualSegmentMembershipChangeMetric) {

		if (individualSegmentMembershipChangeMetric == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (individualSegmentMembershipChangeMetric.getAddedIndividuals() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addedIndividuals\": ");

			sb.append(
				String.valueOf(
					individualSegmentMembershipChangeMetric.
						getAddedIndividuals()));
		}

		if (individualSegmentMembershipChangeMetric.getIndividuals() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individuals\": ");

			sb.append(
				String.valueOf(
					individualSegmentMembershipChangeMetric.getIndividuals()));
		}

		if (individualSegmentMembershipChangeMetric.getKnownIndividuals() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"knownIndividuals\": ");

			sb.append(
				String.valueOf(
					individualSegmentMembershipChangeMetric.
						getKnownIndividuals()));
		}

		if (individualSegmentMembershipChangeMetric.getRemovedIndividuals() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"removedIndividuals\": ");

			sb.append(
				String.valueOf(
					individualSegmentMembershipChangeMetric.
						getRemovedIndividuals()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		IndividualSegmentMembershipChangeMetricJSONParser
			individualSegmentMembershipChangeMetricJSONParser =
				new IndividualSegmentMembershipChangeMetricJSONParser();

		return individualSegmentMembershipChangeMetricJSONParser.parseToMap(
			json);
	}

	public static Map<String, String> toMap(
		IndividualSegmentMembershipChangeMetric
			individualSegmentMembershipChangeMetric) {

		if (individualSegmentMembershipChangeMetric == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (individualSegmentMembershipChangeMetric.getAddedIndividuals() ==
				null) {

			map.put("addedIndividuals", null);
		}
		else {
			map.put(
				"addedIndividuals",
				String.valueOf(
					individualSegmentMembershipChangeMetric.
						getAddedIndividuals()));
		}

		if (individualSegmentMembershipChangeMetric.getIndividuals() == null) {
			map.put("individuals", null);
		}
		else {
			map.put(
				"individuals",
				String.valueOf(
					individualSegmentMembershipChangeMetric.getIndividuals()));
		}

		if (individualSegmentMembershipChangeMetric.getKnownIndividuals() ==
				null) {

			map.put("knownIndividuals", null);
		}
		else {
			map.put(
				"knownIndividuals",
				String.valueOf(
					individualSegmentMembershipChangeMetric.
						getKnownIndividuals()));
		}

		if (individualSegmentMembershipChangeMetric.getRemovedIndividuals() ==
				null) {

			map.put("removedIndividuals", null);
		}
		else {
			map.put(
				"removedIndividuals",
				String.valueOf(
					individualSegmentMembershipChangeMetric.
						getRemovedIndividuals()));
		}

		return map;
	}

	public static class IndividualSegmentMembershipChangeMetricJSONParser
		extends BaseJSONParser<IndividualSegmentMembershipChangeMetric> {

		@Override
		protected IndividualSegmentMembershipChangeMetric createDTO() {
			return new IndividualSegmentMembershipChangeMetric();
		}

		@Override
		protected IndividualSegmentMembershipChangeMetric[] createDTOArray(
			int size) {

			return new IndividualSegmentMembershipChangeMetric[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "addedIndividuals")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "individuals")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "knownIndividuals")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "removedIndividuals")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			IndividualSegmentMembershipChangeMetric
				individualSegmentMembershipChangeMetric,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "addedIndividuals")) {
				if (jsonParserFieldValue != null) {
					individualSegmentMembershipChangeMetric.setAddedIndividuals(
						MetricSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "individuals")) {
				if (jsonParserFieldValue != null) {
					individualSegmentMembershipChangeMetric.setIndividuals(
						MetricSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "knownIndividuals")) {
				if (jsonParserFieldValue != null) {
					individualSegmentMembershipChangeMetric.setKnownIndividuals(
						MetricSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "removedIndividuals")) {

				if (jsonParserFieldValue != null) {
					individualSegmentMembershipChangeMetric.
						setRemovedIndividuals(
							MetricSerDes.toDTO((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:1474058962