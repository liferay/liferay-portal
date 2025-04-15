/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.client.serdes.v1_0;

import com.liferay.headless.admin.taxonomy.client.dto.v1_0.AssetLibrary;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.Keyword;
import com.liferay.headless.admin.taxonomy.client.json.BaseJSONParser;

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
public class KeywordSerDes {

	public static Keyword toDTO(String json) {
		KeywordJSONParser keywordJSONParser = new KeywordJSONParser();

		return keywordJSONParser.parseToDTO(json);
	}

	public static Keyword[] toDTOs(String json) {
		KeywordJSONParser keywordJSONParser = new KeywordJSONParser();

		return keywordJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Keyword keyword) {
		if (keyword == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (keyword.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(keyword.getActions()));
		}

		if (keyword.getAssetLibraries() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraries\": ");

			sb.append("[");

			for (int i = 0; i < keyword.getAssetLibraries().length; i++) {
				sb.append(String.valueOf(keyword.getAssetLibraries()[i]));

				if ((i + 1) < keyword.getAssetLibraries().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (keyword.getAssetLibraryKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraryKey\": ");

			sb.append("\"");

			sb.append(_escape(keyword.getAssetLibraryKey()));

			sb.append("\"");
		}

		if (keyword.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(keyword.getCreator()));
		}

		if (keyword.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(keyword.getDateCreated()));

			sb.append("\"");
		}

		if (keyword.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(keyword.getDateModified()));

			sb.append("\"");
		}

		if (keyword.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(keyword.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (keyword.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(keyword.getId());
		}

		if (keyword.getKeywordUsageCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywordUsageCount\": ");

			sb.append(keyword.getKeywordUsageCount());
		}

		if (keyword.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(keyword.getName()));

			sb.append("\"");
		}

		if (keyword.getSiteExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(keyword.getSiteExternalReferenceCode()));

			sb.append("\"");
		}

		if (keyword.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(keyword.getSiteId());
		}

		if (keyword.getSubscribed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subscribed\": ");

			sb.append(keyword.getSubscribed());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		KeywordJSONParser keywordJSONParser = new KeywordJSONParser();

		return keywordJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Keyword keyword) {
		if (keyword == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (keyword.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(keyword.getActions()));
		}

		if (keyword.getAssetLibraries() == null) {
			map.put("assetLibraries", null);
		}
		else {
			map.put(
				"assetLibraries", String.valueOf(keyword.getAssetLibraries()));
		}

		if (keyword.getAssetLibraryKey() == null) {
			map.put("assetLibraryKey", null);
		}
		else {
			map.put(
				"assetLibraryKey",
				String.valueOf(keyword.getAssetLibraryKey()));
		}

		if (keyword.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(keyword.getCreator()));
		}

		if (keyword.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(keyword.getDateCreated()));
		}

		if (keyword.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(keyword.getDateModified()));
		}

		if (keyword.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(keyword.getExternalReferenceCode()));
		}

		if (keyword.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(keyword.getId()));
		}

		if (keyword.getKeywordUsageCount() == null) {
			map.put("keywordUsageCount", null);
		}
		else {
			map.put(
				"keywordUsageCount",
				String.valueOf(keyword.getKeywordUsageCount()));
		}

		if (keyword.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(keyword.getName()));
		}

		if (keyword.getSiteExternalReferenceCode() == null) {
			map.put("siteExternalReferenceCode", null);
		}
		else {
			map.put(
				"siteExternalReferenceCode",
				String.valueOf(keyword.getSiteExternalReferenceCode()));
		}

		if (keyword.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(keyword.getSiteId()));
		}

		if (keyword.getSubscribed() == null) {
			map.put("subscribed", null);
		}
		else {
			map.put("subscribed", String.valueOf(keyword.getSubscribed()));
		}

		return map;
	}

	public static class KeywordJSONParser extends BaseJSONParser<Keyword> {

		@Override
		protected Keyword createDTO() {
			return new Keyword();
		}

		@Override
		protected Keyword[] createDTOArray(int size) {
			return new Keyword[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "assetLibraries")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "keywordUsageCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "siteExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "subscribed")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Keyword keyword, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					keyword.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetLibraries")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					AssetLibrary[] assetLibrariesArray =
						new AssetLibrary[jsonParserFieldValues.length];

					for (int i = 0; i < assetLibrariesArray.length; i++) {
						assetLibrariesArray[i] = AssetLibrarySerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					keyword.setAssetLibraries(assetLibrariesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
				if (jsonParserFieldValue != null) {
					keyword.setAssetLibraryKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					keyword.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					keyword.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					keyword.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					keyword.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					keyword.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "keywordUsageCount")) {
				if (jsonParserFieldValue != null) {
					keyword.setKeywordUsageCount(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					keyword.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "siteExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					keyword.setSiteExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					keyword.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subscribed")) {
				if (jsonParserFieldValue != null) {
					keyword.setSubscribed((Boolean)jsonParserFieldValue);
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