/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.Campaign;
import com.liferay.osb.faro.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collection;
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
public class CampaignSerDes {

	public static Campaign toDTO(String json) {
		CampaignJSONParser campaignJSONParser = new CampaignJSONParser();

		return campaignJSONParser.parseToDTO(json);
	}

	public static Campaign[] toDTOs(String json) {
		CampaignJSONParser campaignJSONParser = new CampaignJSONParser();

		return campaignJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Campaign campaign) {
		if (campaign == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (campaign.getAccountsTouched() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountsTouched\": ");

			sb.append(campaign.getAccountsTouched());
		}

		if (campaign.getCampaignType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"campaignType\": ");

			sb.append("\"");

			sb.append(_escape(campaign.getCampaignType()));

			sb.append("\"");
		}

		if (campaign.getEndDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"endDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(campaign.getEndDate()));

			sb.append("\"");
		}

		if (campaign.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(campaign.getId()));

			sb.append("\"");
		}

		if (campaign.getIndividualsTouched() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"individualsTouched\": ");

			sb.append(campaign.getIndividualsTouched());
		}

		if (campaign.getLastTouchDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastTouchDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(campaign.getLastTouchDate()));

			sb.append("\"");
		}

		if (campaign.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(campaign.getName()));

			sb.append("\"");
		}

		if (campaign.getOrigin() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"origin\": ");

			sb.append("\"");

			sb.append(_escape(campaign.getOrigin()));

			sb.append("\"");
		}

		if (campaign.getStartDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"startDate\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(campaign.getStartDate()));

			sb.append("\"");
		}

		if (campaign.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");

			sb.append(_escape(campaign.getStatus()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CampaignJSONParser campaignJSONParser = new CampaignJSONParser();

		return campaignJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Campaign campaign) {
		if (campaign == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (campaign.getAccountsTouched() == null) {
			map.put("accountsTouched", null);
		}
		else {
			map.put(
				"accountsTouched",
				String.valueOf(campaign.getAccountsTouched()));
		}

		if (campaign.getCampaignType() == null) {
			map.put("campaignType", null);
		}
		else {
			map.put("campaignType", String.valueOf(campaign.getCampaignType()));
		}

		if (campaign.getEndDate() == null) {
			map.put("endDate", null);
		}
		else {
			map.put(
				"endDate",
				liferayToJSONDateFormat.format(campaign.getEndDate()));
		}

		if (campaign.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(campaign.getId()));
		}

		if (campaign.getIndividualsTouched() == null) {
			map.put("individualsTouched", null);
		}
		else {
			map.put(
				"individualsTouched",
				String.valueOf(campaign.getIndividualsTouched()));
		}

		if (campaign.getLastTouchDate() == null) {
			map.put("lastTouchDate", null);
		}
		else {
			map.put(
				"lastTouchDate",
				liferayToJSONDateFormat.format(campaign.getLastTouchDate()));
		}

		if (campaign.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(campaign.getName()));
		}

		if (campaign.getOrigin() == null) {
			map.put("origin", null);
		}
		else {
			map.put("origin", String.valueOf(campaign.getOrigin()));
		}

		if (campaign.getStartDate() == null) {
			map.put("startDate", null);
		}
		else {
			map.put(
				"startDate",
				liferayToJSONDateFormat.format(campaign.getStartDate()));
		}

		if (campaign.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put("status", String.valueOf(campaign.getStatus()));
		}

		return map;
	}

	public static class CampaignJSONParser extends BaseJSONParser<Campaign> {

		@Override
		protected Campaign createDTO() {
			return new Campaign();
		}

		@Override
		protected Campaign[] createDTOArray(int size) {
			return new Campaign[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "accountsTouched")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "campaignType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "endDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "individualsTouched")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "lastTouchDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "origin")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "startDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Campaign campaign, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accountsTouched")) {
				if (jsonParserFieldValue != null) {
					campaign.setAccountsTouched(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "campaignType")) {
				if (jsonParserFieldValue != null) {
					campaign.setCampaignType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "endDate")) {
				if (jsonParserFieldValue != null) {
					campaign.setEndDate(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					campaign.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "individualsTouched")) {

				if (jsonParserFieldValue != null) {
					campaign.setIndividualsTouched(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "lastTouchDate")) {
				if (jsonParserFieldValue != null) {
					campaign.setLastTouchDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					campaign.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "origin")) {
				if (jsonParserFieldValue != null) {
					campaign.setOrigin((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "startDate")) {
				if (jsonParserFieldValue != null) {
					campaign.setStartDate(toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					campaign.setStatus((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:904247092