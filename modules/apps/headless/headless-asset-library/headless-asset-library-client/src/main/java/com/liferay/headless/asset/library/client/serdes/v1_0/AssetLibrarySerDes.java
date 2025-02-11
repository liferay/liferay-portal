/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.client.serdes.v1_0;

import com.liferay.headless.asset.library.client.dto.v1_0.AssetLibrary;
import com.liferay.headless.asset.library.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Generated;

/**
 * @author Roberto Díaz
 * @generated
 */
@Generated("")
public class AssetLibrarySerDes {

	public static AssetLibrary toDTO(String json) {
		AssetLibraryJSONParser assetLibraryJSONParser =
			new AssetLibraryJSONParser();

		return assetLibraryJSONParser.parseToDTO(json);
	}

	public static AssetLibrary[] toDTOs(String json) {
		AssetLibraryJSONParser assetLibraryJSONParser =
			new AssetLibraryJSONParser();

		return assetLibraryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AssetLibrary assetLibrary) {
		if (assetLibrary == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (assetLibrary.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(assetLibrary.getDateCreated()));

			sb.append("\"");
		}

		if (assetLibrary.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(assetLibrary.getDateModified()));

			sb.append("\"");
		}

		if (assetLibrary.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(assetLibrary.getDescription()));

			sb.append("\"");
		}

		if (assetLibrary.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(assetLibrary.getDescription_i18n()));
		}

		if (assetLibrary.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(assetLibrary.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (assetLibrary.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(assetLibrary.getId());
		}

		if (assetLibrary.getLinkedSiteIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"linkedSiteIds\": ");

			sb.append("[");

			for (int i = 0; i < assetLibrary.getLinkedSiteIds().length; i++) {
				sb.append(assetLibrary.getLinkedSiteIds()[i]);

				if ((i + 1) < assetLibrary.getLinkedSiteIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (assetLibrary.getLinkedSitesExternalReferenceCodes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"linkedSitesExternalReferenceCodes\": ");

			sb.append("[");

			for (int i = 0;
				 i < assetLibrary.getLinkedSitesExternalReferenceCodes().length;
				 i++) {

				sb.append(
					_toJSON(
						assetLibrary.getLinkedSitesExternalReferenceCodes()
							[i]));

				if ((i + 1) < assetLibrary.
						getLinkedSitesExternalReferenceCodes().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (assetLibrary.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(assetLibrary.getName()));

			sb.append("\"");
		}

		if (assetLibrary.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(assetLibrary.getName_i18n()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AssetLibraryJSONParser assetLibraryJSONParser =
			new AssetLibraryJSONParser();

		return assetLibraryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AssetLibrary assetLibrary) {
		if (assetLibrary == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (assetLibrary.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(assetLibrary.getDateCreated()));
		}

		if (assetLibrary.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(assetLibrary.getDateModified()));
		}

		if (assetLibrary.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(assetLibrary.getDescription()));
		}

		if (assetLibrary.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(assetLibrary.getDescription_i18n()));
		}

		if (assetLibrary.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(assetLibrary.getExternalReferenceCode()));
		}

		if (assetLibrary.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(assetLibrary.getId()));
		}

		if (assetLibrary.getLinkedSiteIds() == null) {
			map.put("linkedSiteIds", null);
		}
		else {
			map.put(
				"linkedSiteIds",
				String.valueOf(assetLibrary.getLinkedSiteIds()));
		}

		if (assetLibrary.getLinkedSitesExternalReferenceCodes() == null) {
			map.put("linkedSitesExternalReferenceCodes", null);
		}
		else {
			map.put(
				"linkedSitesExternalReferenceCodes",
				String.valueOf(
					assetLibrary.getLinkedSitesExternalReferenceCodes()));
		}

		if (assetLibrary.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(assetLibrary.getName()));
		}

		if (assetLibrary.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put("name_i18n", String.valueOf(assetLibrary.getName_i18n()));
		}

		return map;
	}

	public static class AssetLibraryJSONParser
		extends BaseJSONParser<AssetLibrary> {

		@Override
		protected AssetLibrary createDTO() {
			return new AssetLibrary();
		}

		@Override
		protected AssetLibrary[] createDTOArray(int size) {
			return new AssetLibrary[size];
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
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "linkedSiteIds")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"linkedSitesExternalReferenceCodes")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			AssetLibrary assetLibrary, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					assetLibrary.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "linkedSiteIds")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setLinkedSiteIds(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"linkedSitesExternalReferenceCodes")) {

				if (jsonParserFieldValue != null) {
					assetLibrary.setLinkedSitesExternalReferenceCodes(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
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