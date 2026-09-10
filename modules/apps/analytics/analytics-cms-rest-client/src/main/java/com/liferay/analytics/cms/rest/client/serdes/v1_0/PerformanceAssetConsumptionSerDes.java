/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.client.serdes.v1_0;

import com.liferay.analytics.cms.rest.client.dto.v1_0.PerformanceAssetConsumption;
import com.liferay.analytics.cms.rest.client.dto.v1_0.PerformanceAssetConsumptionItem;
import com.liferay.analytics.cms.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
public class PerformanceAssetConsumptionSerDes {

	public static PerformanceAssetConsumption toDTO(String json) {
		PerformanceAssetConsumptionJSONParser
			performanceAssetConsumptionJSONParser =
				new PerformanceAssetConsumptionJSONParser();

		return performanceAssetConsumptionJSONParser.parseToDTO(json);
	}

	public static PerformanceAssetConsumption[] toDTOs(String json) {
		PerformanceAssetConsumptionJSONParser
			performanceAssetConsumptionJSONParser =
				new PerformanceAssetConsumptionJSONParser();

		return performanceAssetConsumptionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PerformanceAssetConsumption performanceAssetConsumption) {

		if (performanceAssetConsumption == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (performanceAssetConsumption.getPerformanceAssetConsumptionItems() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"performanceAssetConsumptionItems\": ");

			sb.append("[");

			for (int i = 0;
				 i < performanceAssetConsumption.
					 getPerformanceAssetConsumptionItems().length;
				 i++) {

				sb.append(
					String.valueOf(
						performanceAssetConsumption.
							getPerformanceAssetConsumptionItems()[i]));

				if ((i + 1) < performanceAssetConsumption.
						getPerformanceAssetConsumptionItems().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (performanceAssetConsumption.
				getPerformanceAssetConsumptionItemsCount() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"performanceAssetConsumptionItemsCount\": ");

			sb.append(
				performanceAssetConsumption.
					getPerformanceAssetConsumptionItemsCount());
		}

		if (performanceAssetConsumption.getTotalCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalCount\": ");

			sb.append(performanceAssetConsumption.getTotalCount());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PerformanceAssetConsumptionJSONParser
			performanceAssetConsumptionJSONParser =
				new PerformanceAssetConsumptionJSONParser();

		return performanceAssetConsumptionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PerformanceAssetConsumption performanceAssetConsumption) {

		if (performanceAssetConsumption == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (performanceAssetConsumption.getPerformanceAssetConsumptionItems() ==
				null) {

			map.put("performanceAssetConsumptionItems", null);
		}
		else {
			map.put(
				"performanceAssetConsumptionItems",
				String.valueOf(
					performanceAssetConsumption.
						getPerformanceAssetConsumptionItems()));
		}

		if (performanceAssetConsumption.
				getPerformanceAssetConsumptionItemsCount() == null) {

			map.put("performanceAssetConsumptionItemsCount", null);
		}
		else {
			map.put(
				"performanceAssetConsumptionItemsCount",
				String.valueOf(
					performanceAssetConsumption.
						getPerformanceAssetConsumptionItemsCount()));
		}

		if (performanceAssetConsumption.getTotalCount() == null) {
			map.put("totalCount", null);
		}
		else {
			map.put(
				"totalCount",
				String.valueOf(performanceAssetConsumption.getTotalCount()));
		}

		return map;
	}

	public static class PerformanceAssetConsumptionJSONParser
		extends BaseJSONParser<PerformanceAssetConsumption> {

		@Override
		protected PerformanceAssetConsumption createDTO() {
			return new PerformanceAssetConsumption();
		}

		@Override
		protected PerformanceAssetConsumption[] createDTOArray(int size) {
			return new PerformanceAssetConsumption[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "performanceAssetConsumptionItems")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"performanceAssetConsumptionItemsCount")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "totalCount")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PerformanceAssetConsumption performanceAssetConsumption,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "performanceAssetConsumptionItems")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PerformanceAssetConsumptionItem[]
						performanceAssetConsumptionItemsArray =
							new PerformanceAssetConsumptionItem
								[jsonParserFieldValues.length];

					for (int i = 0;
						 i < performanceAssetConsumptionItemsArray.length;
						 i++) {

						performanceAssetConsumptionItemsArray[i] =
							PerformanceAssetConsumptionItemSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					performanceAssetConsumption.
						setPerformanceAssetConsumptionItems(
							performanceAssetConsumptionItemsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"performanceAssetConsumptionItemsCount")) {

				if (jsonParserFieldValue != null) {
					performanceAssetConsumption.
						setPerformanceAssetConsumptionItemsCount(
							Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "totalCount")) {
				if (jsonParserFieldValue != null) {
					performanceAssetConsumption.setTotalCount(
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
// LIFERAY-REST-BUILDER-HASH:1930294492