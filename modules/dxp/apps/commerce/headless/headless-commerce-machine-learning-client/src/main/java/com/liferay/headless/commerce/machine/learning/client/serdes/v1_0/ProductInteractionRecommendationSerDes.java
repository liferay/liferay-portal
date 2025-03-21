/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.machine.learning.client.serdes.v1_0;

import com.liferay.headless.commerce.machine.learning.client.dto.v1_0.ProductInteractionRecommendation;
import com.liferay.headless.commerce.machine.learning.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class ProductInteractionRecommendationSerDes {

	public static ProductInteractionRecommendation toDTO(String json) {
		ProductInteractionRecommendationJSONParser
			productInteractionRecommendationJSONParser =
				new ProductInteractionRecommendationJSONParser();

		return productInteractionRecommendationJSONParser.parseToDTO(json);
	}

	public static ProductInteractionRecommendation[] toDTOs(String json) {
		ProductInteractionRecommendationJSONParser
			productInteractionRecommendationJSONParser =
				new ProductInteractionRecommendationJSONParser();

		return productInteractionRecommendationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		ProductInteractionRecommendation productInteractionRecommendation) {

		if (productInteractionRecommendation == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (productInteractionRecommendation.getCreateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					productInteractionRecommendation.getCreateDate()));

			sb.append("\"");
		}

		if (productInteractionRecommendation.getJobId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"jobId\": ");

			sb.append("\"");

			sb.append(_escape(productInteractionRecommendation.getJobId()));

			sb.append("\"");
		}

		if (productInteractionRecommendation.getProductId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productId\": ");

			sb.append(productInteractionRecommendation.getProductId());
		}

		if (productInteractionRecommendation.getRank() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rank\": ");

			sb.append(productInteractionRecommendation.getRank());
		}

		if (productInteractionRecommendation.getRecommendedProductId() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"recommendedProductId\": ");

			sb.append(
				productInteractionRecommendation.getRecommendedProductId());
		}

		if (productInteractionRecommendation.getScore() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"score\": ");

			sb.append(productInteractionRecommendation.getScore());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProductInteractionRecommendationJSONParser
			productInteractionRecommendationJSONParser =
				new ProductInteractionRecommendationJSONParser();

		return productInteractionRecommendationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ProductInteractionRecommendation productInteractionRecommendation) {

		if (productInteractionRecommendation == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (productInteractionRecommendation.getCreateDate() == null) {
			map.put("createDate", null);
		}
		else {
			map.put(
				"createDate",
				liferayToJSONDateFormat.format(
					productInteractionRecommendation.getCreateDate()));
		}

		if (productInteractionRecommendation.getJobId() == null) {
			map.put("jobId", null);
		}
		else {
			map.put(
				"jobId",
				String.valueOf(productInteractionRecommendation.getJobId()));
		}

		if (productInteractionRecommendation.getProductId() == null) {
			map.put("productId", null);
		}
		else {
			map.put(
				"productId",
				String.valueOf(
					productInteractionRecommendation.getProductId()));
		}

		if (productInteractionRecommendation.getRank() == null) {
			map.put("rank", null);
		}
		else {
			map.put(
				"rank",
				String.valueOf(productInteractionRecommendation.getRank()));
		}

		if (productInteractionRecommendation.getRecommendedProductId() ==
				null) {

			map.put("recommendedProductId", null);
		}
		else {
			map.put(
				"recommendedProductId",
				String.valueOf(
					productInteractionRecommendation.
						getRecommendedProductId()));
		}

		if (productInteractionRecommendation.getScore() == null) {
			map.put("score", null);
		}
		else {
			map.put(
				"score",
				String.valueOf(productInteractionRecommendation.getScore()));
		}

		return map;
	}

	public static class ProductInteractionRecommendationJSONParser
		extends BaseJSONParser<ProductInteractionRecommendation> {

		@Override
		protected ProductInteractionRecommendation createDTO() {
			return new ProductInteractionRecommendation();
		}

		@Override
		protected ProductInteractionRecommendation[] createDTOArray(int size) {
			return new ProductInteractionRecommendation[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "createDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "jobId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "rank")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "recommendedProductId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "score")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ProductInteractionRecommendation productInteractionRecommendation,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "createDate")) {
				if (jsonParserFieldValue != null) {
					productInteractionRecommendation.setCreateDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "jobId")) {
				if (jsonParserFieldValue != null) {
					productInteractionRecommendation.setJobId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "productId")) {
				if (jsonParserFieldValue != null) {
					productInteractionRecommendation.setProductId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "rank")) {
				if (jsonParserFieldValue != null) {
					productInteractionRecommendation.setRank(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "recommendedProductId")) {

				if (jsonParserFieldValue != null) {
					productInteractionRecommendation.setRecommendedProductId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "score")) {
				if (jsonParserFieldValue != null) {
					productInteractionRecommendation.setScore(
						Float.valueOf((String)jsonParserFieldValue));
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