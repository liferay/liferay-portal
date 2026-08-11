/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.client.serdes.v1_0;

import com.liferay.headless.cms.client.dto.v1_0.AssetStatistics;
import com.liferay.headless.cms.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Crescenzo Rega
 * @generated
 */
@Generated("")
public class AssetStatisticsSerDes {

	public static AssetStatistics toDTO(String json) {
		AssetStatisticsJSONParser assetStatisticsJSONParser =
			new AssetStatisticsJSONParser();

		return assetStatisticsJSONParser.parseToDTO(json);
	}

	public static AssetStatistics[] toDTOs(String json) {
		AssetStatisticsJSONParser assetStatisticsJSONParser =
			new AssetStatisticsJSONParser();

		return assetStatisticsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AssetStatistics assetStatistics) {
		if (assetStatistics == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (assetStatistics.getApprovedCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"approvedCount\": ");

			sb.append(assetStatistics.getApprovedCount());
		}

		if (assetStatistics.getBrokenLinksCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"brokenLinksCount\": ");

			sb.append(assetStatistics.getBrokenLinksCount());
		}

		if (assetStatistics.getExpiredCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expiredCount\": ");

			sb.append(assetStatistics.getExpiredCount());
		}

		if (assetStatistics.getExpiringSoonCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"expiringSoonCount\": ");

			sb.append(assetStatistics.getExpiringSoonCount());
		}

		if (assetStatistics.getInDraftCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inDraftCount\": ");

			sb.append(assetStatistics.getInDraftCount());
		}

		if (assetStatistics.getPendingCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pendingCount\": ");

			sb.append(assetStatistics.getPendingCount());
		}

		if (assetStatistics.getReviewDateOverdueCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reviewDateOverdueCount\": ");

			sb.append(assetStatistics.getReviewDateOverdueCount());
		}

		if (assetStatistics.getScheduledCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scheduledCount\": ");

			sb.append(assetStatistics.getScheduledCount());
		}

		if (assetStatistics.getTotalCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"totalCount\": ");

			sb.append(assetStatistics.getTotalCount());
		}

		if (assetStatistics.getUpcomingReviewCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"upcomingReviewCount\": ");

			sb.append(assetStatistics.getUpcomingReviewCount());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AssetStatisticsJSONParser assetStatisticsJSONParser =
			new AssetStatisticsJSONParser();

		return assetStatisticsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AssetStatistics assetStatistics) {
		if (assetStatistics == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (assetStatistics.getApprovedCount() == null) {
			map.put("approvedCount", null);
		}
		else {
			map.put(
				"approvedCount",
				String.valueOf(assetStatistics.getApprovedCount()));
		}

		if (assetStatistics.getBrokenLinksCount() == null) {
			map.put("brokenLinksCount", null);
		}
		else {
			map.put(
				"brokenLinksCount",
				String.valueOf(assetStatistics.getBrokenLinksCount()));
		}

		if (assetStatistics.getExpiredCount() == null) {
			map.put("expiredCount", null);
		}
		else {
			map.put(
				"expiredCount",
				String.valueOf(assetStatistics.getExpiredCount()));
		}

		if (assetStatistics.getExpiringSoonCount() == null) {
			map.put("expiringSoonCount", null);
		}
		else {
			map.put(
				"expiringSoonCount",
				String.valueOf(assetStatistics.getExpiringSoonCount()));
		}

		if (assetStatistics.getInDraftCount() == null) {
			map.put("inDraftCount", null);
		}
		else {
			map.put(
				"inDraftCount",
				String.valueOf(assetStatistics.getInDraftCount()));
		}

		if (assetStatistics.getPendingCount() == null) {
			map.put("pendingCount", null);
		}
		else {
			map.put(
				"pendingCount",
				String.valueOf(assetStatistics.getPendingCount()));
		}

		if (assetStatistics.getReviewDateOverdueCount() == null) {
			map.put("reviewDateOverdueCount", null);
		}
		else {
			map.put(
				"reviewDateOverdueCount",
				String.valueOf(assetStatistics.getReviewDateOverdueCount()));
		}

		if (assetStatistics.getScheduledCount() == null) {
			map.put("scheduledCount", null);
		}
		else {
			map.put(
				"scheduledCount",
				String.valueOf(assetStatistics.getScheduledCount()));
		}

		if (assetStatistics.getTotalCount() == null) {
			map.put("totalCount", null);
		}
		else {
			map.put(
				"totalCount", String.valueOf(assetStatistics.getTotalCount()));
		}

		if (assetStatistics.getUpcomingReviewCount() == null) {
			map.put("upcomingReviewCount", null);
		}
		else {
			map.put(
				"upcomingReviewCount",
				String.valueOf(assetStatistics.getUpcomingReviewCount()));
		}

		return map;
	}

	public static class AssetStatisticsJSONParser
		extends BaseJSONParser<AssetStatistics> {

		@Override
		protected AssetStatistics createDTO() {
			return new AssetStatistics();
		}

		@Override
		protected AssetStatistics[] createDTOArray(int size) {
			return new AssetStatistics[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "approvedCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "brokenLinksCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "expiredCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "expiringSoonCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "inDraftCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pendingCount")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "reviewDateOverdueCount")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "scheduledCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "totalCount")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "upcomingReviewCount")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AssetStatistics assetStatistics, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "approvedCount")) {
				if (jsonParserFieldValue != null) {
					assetStatistics.setApprovedCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "brokenLinksCount")) {
				if (jsonParserFieldValue != null) {
					assetStatistics.setBrokenLinksCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expiredCount")) {
				if (jsonParserFieldValue != null) {
					assetStatistics.setExpiredCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "expiringSoonCount")) {
				if (jsonParserFieldValue != null) {
					assetStatistics.setExpiringSoonCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "inDraftCount")) {
				if (jsonParserFieldValue != null) {
					assetStatistics.setInDraftCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pendingCount")) {
				if (jsonParserFieldValue != null) {
					assetStatistics.setPendingCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "reviewDateOverdueCount")) {

				if (jsonParserFieldValue != null) {
					assetStatistics.setReviewDateOverdueCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scheduledCount")) {
				if (jsonParserFieldValue != null) {
					assetStatistics.setScheduledCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "totalCount")) {
				if (jsonParserFieldValue != null) {
					assetStatistics.setTotalCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "upcomingReviewCount")) {

				if (jsonParserFieldValue != null) {
					assetStatistics.setUpcomingReviewCount(
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
// LIFERAY-REST-BUILDER-HASH:1512915642