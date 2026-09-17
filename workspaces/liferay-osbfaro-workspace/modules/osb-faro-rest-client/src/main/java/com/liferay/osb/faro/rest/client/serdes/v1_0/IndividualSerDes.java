/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.client.serdes.v1_0;

import com.liferay.osb.faro.rest.client.dto.v1_0.Individual;
import com.liferay.osb.faro.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class IndividualSerDes {

	public static Individual toDTO(String json) {
		IndividualJSONParser individualJSONParser = new IndividualJSONParser();

		return individualJSONParser.parseToDTO(json);
	}

	public static Individual[] toDTOs(String json) {
		IndividualJSONParser individualJSONParser = new IndividualJSONParser();

		return individualJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Individual individual) {
		if (individual == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (individual.getAccountName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"accountName\": ");

			sb.append("\"");

			sb.append(_escape(individual.getAccountName()));

			sb.append("\"");
		}

		if (individual.getActivitiesCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"activitiesCount\": ");

			sb.append(individual.getActivitiesCount());
		}

		if (individual.getActivityStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"activityStatus\": ");

			sb.append("\"");

			sb.append(_escape(individual.getActivityStatus()));

			sb.append("\"");
		}

		if (individual.getAverageSessionDuration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"averageSessionDuration\": ");

			sb.append(individual.getAverageSessionDuration());
		}

		if (individual.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(individual.getDateCreated()));

			sb.append("\"");
		}

		if (individual.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(individual.getDateModified()));

			sb.append("\"");
		}

		if (individual.getDemographics() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"demographics\": ");

			if (individual.getDemographics() instanceof String) {
				sb.append("\"");
				sb.append((String)individual.getDemographics());
				sb.append("\"");
			}
			else {
				sb.append(individual.getDemographics());
			}
		}

		if (individual.getEmailAddress() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"emailAddress\": ");

			sb.append("\"");

			sb.append(_escape(individual.getEmailAddress()));

			sb.append("\"");
		}

		if (individual.getFirstActivityDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"firstActivityDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					individual.getFirstActivityDate()));

			sb.append("\"");
		}

		if (individual.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(individual.getId()));

			sb.append("\"");
		}

		if (individual.getKnownSinceDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"knownSinceDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(individual.getKnownSinceDate()));

			sb.append("\"");
		}

		if (individual.getLastActivityDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastActivityDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					individual.getLastActivityDate()));

			sb.append("\"");
		}

		if (individual.getLastSessionCountry() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lastSessionCountry\": ");

			sb.append("\"");

			sb.append(_escape(individual.getLastSessionCountry()));

			sb.append("\"");
		}

		if (individual.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(individual.getName()));

			sb.append("\"");
		}

		if (individual.getProfileType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"profileType\": ");

			sb.append("\"");
			sb.append(individual.getProfileType());
			sb.append("\"");
		}

		if (individual.getSessionsCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sessionsCount\": ");

			sb.append(individual.getSessionsCount());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		IndividualJSONParser individualJSONParser = new IndividualJSONParser();

		return individualJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Individual individual) {
		if (individual == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (individual.getAccountName() == null) {
			map.put("accountName", null);
		}
		else {
			map.put("accountName", String.valueOf(individual.getAccountName()));
		}

		if (individual.getActivitiesCount() == null) {
			map.put("activitiesCount", null);
		}
		else {
			map.put(
				"activitiesCount",
				String.valueOf(individual.getActivitiesCount()));
		}

		if (individual.getActivityStatus() == null) {
			map.put("activityStatus", null);
		}
		else {
			map.put(
				"activityStatus",
				String.valueOf(individual.getActivityStatus()));
		}

		if (individual.getAverageSessionDuration() == null) {
			map.put("averageSessionDuration", null);
		}
		else {
			map.put(
				"averageSessionDuration",
				String.valueOf(individual.getAverageSessionDuration()));
		}

		if (individual.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(individual.getDateCreated()));
		}

		if (individual.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(individual.getDateModified()));
		}

		if (individual.getDemographics() == null) {
			map.put("demographics", null);
		}
		else {
			map.put(
				"demographics", String.valueOf(individual.getDemographics()));
		}

		if (individual.getEmailAddress() == null) {
			map.put("emailAddress", null);
		}
		else {
			map.put(
				"emailAddress", String.valueOf(individual.getEmailAddress()));
		}

		if (individual.getFirstActivityDate() == null) {
			map.put("firstActivityDate", null);
		}
		else {
			map.put(
				"firstActivityDate",
				liferayToJSONDateFormat.format(
					individual.getFirstActivityDate()));
		}

		if (individual.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(individual.getId()));
		}

		if (individual.getKnownSinceDate() == null) {
			map.put("knownSinceDate", null);
		}
		else {
			map.put(
				"knownSinceDate",
				liferayToJSONDateFormat.format(individual.getKnownSinceDate()));
		}

		if (individual.getLastActivityDate() == null) {
			map.put("lastActivityDate", null);
		}
		else {
			map.put(
				"lastActivityDate",
				liferayToJSONDateFormat.format(
					individual.getLastActivityDate()));
		}

		if (individual.getLastSessionCountry() == null) {
			map.put("lastSessionCountry", null);
		}
		else {
			map.put(
				"lastSessionCountry",
				String.valueOf(individual.getLastSessionCountry()));
		}

		if (individual.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(individual.getName()));
		}

		if (individual.getProfileType() == null) {
			map.put("profileType", null);
		}
		else {
			map.put("profileType", String.valueOf(individual.getProfileType()));
		}

		if (individual.getSessionsCount() == null) {
			map.put("sessionsCount", null);
		}
		else {
			map.put(
				"sessionsCount", String.valueOf(individual.getSessionsCount()));
		}

		return map;
	}

	public static class IndividualJSONParser
		extends BaseJSONParser<Individual> {

		@Override
		protected Individual createDTO() {
			return new Individual();
		}

		@Override
		protected Individual[] createDTOArray(int size) {
			return new Individual[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "accountName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "activitiesCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "activityStatus")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "averageSessionDuration")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "demographics")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "firstActivityDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "knownSinceDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "lastActivityDate")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "lastSessionCountry")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "profileType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sessionsCount")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Individual individual, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "accountName")) {
				if (jsonParserFieldValue != null) {
					individual.setAccountName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "activitiesCount")) {
				if (jsonParserFieldValue != null) {
					individual.setActivitiesCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "activityStatus")) {
				if (jsonParserFieldValue != null) {
					individual.setActivityStatus((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "averageSessionDuration")) {

				if (jsonParserFieldValue != null) {
					individual.setAverageSessionDuration(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					individual.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					individual.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "demographics")) {
				if (jsonParserFieldValue != null) {
					individual.setDemographics((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "emailAddress")) {
				if (jsonParserFieldValue != null) {
					individual.setEmailAddress((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "firstActivityDate")) {
				if (jsonParserFieldValue != null) {
					individual.setFirstActivityDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					individual.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "knownSinceDate")) {
				if (jsonParserFieldValue != null) {
					individual.setKnownSinceDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "lastActivityDate")) {
				if (jsonParserFieldValue != null) {
					individual.setLastActivityDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "lastSessionCountry")) {

				if (jsonParserFieldValue != null) {
					individual.setLastSessionCountry(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					individual.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "profileType")) {
				if (jsonParserFieldValue != null) {
					individual.setProfileType(
						Individual.ProfileType.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sessionsCount")) {
				if (jsonParserFieldValue != null) {
					individual.setSessionsCount(
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
// LIFERAY-REST-BUILDER-HASH:-1289614337