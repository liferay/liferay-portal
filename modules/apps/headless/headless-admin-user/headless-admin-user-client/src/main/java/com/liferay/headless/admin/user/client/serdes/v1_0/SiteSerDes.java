/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.Site;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class SiteSerDes {

	public static Site toDTO(String json) {
		SiteJSONParser siteJSONParser = new SiteJSONParser();

		return siteJSONParser.parseToDTO(json);
	}

	public static Site[] toDTOs(String json) {
		SiteJSONParser siteJSONParser = new SiteJSONParser();

		return siteJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Site site) {
		if (site == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (site.getAvailableLanguages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availableLanguages\": ");

			sb.append("[");

			for (int i = 0; i < site.getAvailableLanguages().length; i++) {
				sb.append(_toJSON(site.getAvailableLanguages()[i]));

				if ((i + 1) < site.getAvailableLanguages().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (site.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(site.getCreator()));
		}

		if (site.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(site.getDescription()));

			sb.append("\"");
		}

		if (site.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(site.getDescription_i18n()));
		}

		if (site.getDescriptiveName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptiveName\": ");

			sb.append("\"");

			sb.append(_escape(site.getDescriptiveName()));

			sb.append("\"");
		}

		if (site.getDescriptiveName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptiveName_i18n\": ");

			sb.append(_toJSON(site.getDescriptiveName_i18n()));
		}

		if (site.getFriendlyUrlPath() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"friendlyUrlPath\": ");

			sb.append("\"");

			sb.append(_escape(site.getFriendlyUrlPath()));

			sb.append("\"");
		}

		if (site.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(site.getId());
		}

		if (site.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(site.getKey()));

			sb.append("\"");
		}

		if (site.getMembershipType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"membershipType\": ");

			sb.append("\"");

			sb.append(_escape(site.getMembershipType()));

			sb.append("\"");
		}

		if (site.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(site.getName()));

			sb.append("\"");
		}

		if (site.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(site.getName_i18n()));
		}

		if (site.getParentSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentSiteId\": ");

			sb.append(site.getParentSiteId());
		}

		if (site.getSites() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sites\": ");

			sb.append("[");

			for (int i = 0; i < site.getSites().length; i++) {
				sb.append(String.valueOf(site.getSites()[i]));

				if ((i + 1) < site.getSites().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SiteJSONParser siteJSONParser = new SiteJSONParser();

		return siteJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Site site) {
		if (site == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (site.getAvailableLanguages() == null) {
			map.put("availableLanguages", null);
		}
		else {
			map.put(
				"availableLanguages",
				String.valueOf(site.getAvailableLanguages()));
		}

		if (site.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(site.getCreator()));
		}

		if (site.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(site.getDescription()));
		}

		if (site.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n", String.valueOf(site.getDescription_i18n()));
		}

		if (site.getDescriptiveName() == null) {
			map.put("descriptiveName", null);
		}
		else {
			map.put(
				"descriptiveName", String.valueOf(site.getDescriptiveName()));
		}

		if (site.getDescriptiveName_i18n() == null) {
			map.put("descriptiveName_i18n", null);
		}
		else {
			map.put(
				"descriptiveName_i18n",
				String.valueOf(site.getDescriptiveName_i18n()));
		}

		if (site.getFriendlyUrlPath() == null) {
			map.put("friendlyUrlPath", null);
		}
		else {
			map.put(
				"friendlyUrlPath", String.valueOf(site.getFriendlyUrlPath()));
		}

		if (site.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(site.getId()));
		}

		if (site.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(site.getKey()));
		}

		if (site.getMembershipType() == null) {
			map.put("membershipType", null);
		}
		else {
			map.put("membershipType", String.valueOf(site.getMembershipType()));
		}

		if (site.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(site.getName()));
		}

		if (site.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put("name_i18n", String.valueOf(site.getName_i18n()));
		}

		if (site.getParentSiteId() == null) {
			map.put("parentSiteId", null);
		}
		else {
			map.put("parentSiteId", String.valueOf(site.getParentSiteId()));
		}

		if (site.getSites() == null) {
			map.put("sites", null);
		}
		else {
			map.put("sites", String.valueOf(site.getSites()));
		}

		return map;
	}

	public static class SiteJSONParser extends BaseJSONParser<Site> {

		@Override
		protected Site createDTO() {
			return new Site();
		}

		@Override
		protected Site[] createDTOArray(int size) {
			return new Site[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "availableLanguages")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "descriptiveName")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "descriptiveName_i18n")) {

				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "friendlyUrlPath")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "membershipType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "parentSiteId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sites")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Site site, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "availableLanguages")) {
				if (jsonParserFieldValue != null) {
					site.setAvailableLanguages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					site.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					site.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					site.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "descriptiveName")) {
				if (jsonParserFieldValue != null) {
					site.setDescriptiveName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "descriptiveName_i18n")) {

				if (jsonParserFieldValue != null) {
					site.setDescriptiveName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "friendlyUrlPath")) {
				if (jsonParserFieldValue != null) {
					site.setFriendlyUrlPath((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					site.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					site.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "membershipType")) {
				if (jsonParserFieldValue != null) {
					site.setMembershipType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					site.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					site.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "parentSiteId")) {
				if (jsonParserFieldValue != null) {
					site.setParentSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sites")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Site[] sitesArray = new Site[jsonParserFieldValues.length];

					for (int i = 0; i < sitesArray.length; i++) {
						sitesArray[i] = SiteSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					site.setSites(sitesArray);
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