/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.client.serdes.v1_0;

import com.liferay.headless.admin.taxonomy.client.dto.v1_0.AssetLibrary;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.AssetType;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.TaxonomyVocabulary;
import com.liferay.headless.admin.taxonomy.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class TaxonomyVocabularySerDes {

	public static TaxonomyVocabulary toDTO(String json) {
		TaxonomyVocabularyJSONParser taxonomyVocabularyJSONParser =
			new TaxonomyVocabularyJSONParser();

		return taxonomyVocabularyJSONParser.parseToDTO(json);
	}

	public static TaxonomyVocabulary[] toDTOs(String json) {
		TaxonomyVocabularyJSONParser taxonomyVocabularyJSONParser =
			new TaxonomyVocabularyJSONParser();

		return taxonomyVocabularyJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TaxonomyVocabulary taxonomyVocabulary) {
		if (taxonomyVocabulary == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (taxonomyVocabulary.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(taxonomyVocabulary.getActions()));
		}

		if (taxonomyVocabulary.getAssetLibraries() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraries\": ");

			sb.append("[");

			for (int i = 0; i < taxonomyVocabulary.getAssetLibraries().length;
				 i++) {

				sb.append(
					String.valueOf(taxonomyVocabulary.getAssetLibraries()[i]));

				if ((i + 1) < taxonomyVocabulary.getAssetLibraries().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (taxonomyVocabulary.getAssetLibraryKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetLibraryKey\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyVocabulary.getAssetLibraryKey()));

			sb.append("\"");
		}

		if (taxonomyVocabulary.getAssetTypes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTypes\": ");

			sb.append("[");

			for (int i = 0; i < taxonomyVocabulary.getAssetTypes().length;
				 i++) {

				sb.append(
					String.valueOf(taxonomyVocabulary.getAssetTypes()[i]));

				if ((i + 1) < taxonomyVocabulary.getAssetTypes().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (taxonomyVocabulary.getAvailableLanguages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availableLanguages\": ");

			sb.append("[");

			for (int i = 0;
				 i < taxonomyVocabulary.getAvailableLanguages().length; i++) {

				sb.append(
					_toJSON(taxonomyVocabulary.getAvailableLanguages()[i]));

				if ((i + 1) <
						taxonomyVocabulary.getAvailableLanguages().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (taxonomyVocabulary.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(taxonomyVocabulary.getCreator()));
		}

		if (taxonomyVocabulary.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					taxonomyVocabulary.getDateCreated()));

			sb.append("\"");
		}

		if (taxonomyVocabulary.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					taxonomyVocabulary.getDateModified()));

			sb.append("\"");
		}

		if (taxonomyVocabulary.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyVocabulary.getDescription()));

			sb.append("\"");
		}

		if (taxonomyVocabulary.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(taxonomyVocabulary.getDescription_i18n()));
		}

		if (taxonomyVocabulary.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyVocabulary.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (taxonomyVocabulary.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(taxonomyVocabulary.getId());
		}

		if (taxonomyVocabulary.getMultiValued() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"multiValued\": ");

			sb.append(taxonomyVocabulary.getMultiValued());
		}

		if (taxonomyVocabulary.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyVocabulary.getName()));

			sb.append("\"");
		}

		if (taxonomyVocabulary.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(taxonomyVocabulary.getName_i18n()));
		}

		if (taxonomyVocabulary.getNumberOfTaxonomyCategories() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfTaxonomyCategories\": ");

			sb.append(taxonomyVocabulary.getNumberOfTaxonomyCategories());
		}

		if (taxonomyVocabulary.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < taxonomyVocabulary.getPermissions().length;
				 i++) {

				sb.append(taxonomyVocabulary.getPermissions()[i]);

				if ((i + 1) < taxonomyVocabulary.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (taxonomyVocabulary.getSiteExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(taxonomyVocabulary.getSiteExternalReferenceCode()));

			sb.append("\"");
		}

		if (taxonomyVocabulary.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(taxonomyVocabulary.getSiteId());
		}

		if (taxonomyVocabulary.getViewableBy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewableBy\": ");

			sb.append("\"");

			sb.append(taxonomyVocabulary.getViewableBy());

			sb.append("\"");
		}

		if (taxonomyVocabulary.getVisibilityType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"visibilityType\": ");

			sb.append("\"");

			sb.append(taxonomyVocabulary.getVisibilityType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TaxonomyVocabularyJSONParser taxonomyVocabularyJSONParser =
			new TaxonomyVocabularyJSONParser();

		return taxonomyVocabularyJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		TaxonomyVocabulary taxonomyVocabulary) {

		if (taxonomyVocabulary == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (taxonomyVocabulary.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(taxonomyVocabulary.getActions()));
		}

		if (taxonomyVocabulary.getAssetLibraries() == null) {
			map.put("assetLibraries", null);
		}
		else {
			map.put(
				"assetLibraries",
				String.valueOf(taxonomyVocabulary.getAssetLibraries()));
		}

		if (taxonomyVocabulary.getAssetLibraryKey() == null) {
			map.put("assetLibraryKey", null);
		}
		else {
			map.put(
				"assetLibraryKey",
				String.valueOf(taxonomyVocabulary.getAssetLibraryKey()));
		}

		if (taxonomyVocabulary.getAssetTypes() == null) {
			map.put("assetTypes", null);
		}
		else {
			map.put(
				"assetTypes",
				String.valueOf(taxonomyVocabulary.getAssetTypes()));
		}

		if (taxonomyVocabulary.getAvailableLanguages() == null) {
			map.put("availableLanguages", null);
		}
		else {
			map.put(
				"availableLanguages",
				String.valueOf(taxonomyVocabulary.getAvailableLanguages()));
		}

		if (taxonomyVocabulary.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(taxonomyVocabulary.getCreator()));
		}

		if (taxonomyVocabulary.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					taxonomyVocabulary.getDateCreated()));
		}

		if (taxonomyVocabulary.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					taxonomyVocabulary.getDateModified()));
		}

		if (taxonomyVocabulary.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(taxonomyVocabulary.getDescription()));
		}

		if (taxonomyVocabulary.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(taxonomyVocabulary.getDescription_i18n()));
		}

		if (taxonomyVocabulary.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(taxonomyVocabulary.getExternalReferenceCode()));
		}

		if (taxonomyVocabulary.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(taxonomyVocabulary.getId()));
		}

		if (taxonomyVocabulary.getMultiValued() == null) {
			map.put("multiValued", null);
		}
		else {
			map.put(
				"multiValued",
				String.valueOf(taxonomyVocabulary.getMultiValued()));
		}

		if (taxonomyVocabulary.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(taxonomyVocabulary.getName()));
		}

		if (taxonomyVocabulary.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put(
				"name_i18n", String.valueOf(taxonomyVocabulary.getName_i18n()));
		}

		if (taxonomyVocabulary.getNumberOfTaxonomyCategories() == null) {
			map.put("numberOfTaxonomyCategories", null);
		}
		else {
			map.put(
				"numberOfTaxonomyCategories",
				String.valueOf(
					taxonomyVocabulary.getNumberOfTaxonomyCategories()));
		}

		if (taxonomyVocabulary.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(taxonomyVocabulary.getPermissions()));
		}

		if (taxonomyVocabulary.getSiteExternalReferenceCode() == null) {
			map.put("siteExternalReferenceCode", null);
		}
		else {
			map.put(
				"siteExternalReferenceCode",
				String.valueOf(
					taxonomyVocabulary.getSiteExternalReferenceCode()));
		}

		if (taxonomyVocabulary.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(taxonomyVocabulary.getSiteId()));
		}

		if (taxonomyVocabulary.getViewableBy() == null) {
			map.put("viewableBy", null);
		}
		else {
			map.put(
				"viewableBy",
				String.valueOf(taxonomyVocabulary.getViewableBy()));
		}

		if (taxonomyVocabulary.getVisibilityType() == null) {
			map.put("visibilityType", null);
		}
		else {
			map.put(
				"visibilityType",
				String.valueOf(taxonomyVocabulary.getVisibilityType()));
		}

		return map;
	}

	public static class TaxonomyVocabularyJSONParser
		extends BaseJSONParser<TaxonomyVocabulary> {

		@Override
		protected TaxonomyVocabulary createDTO() {
			return new TaxonomyVocabulary();
		}

		@Override
		protected TaxonomyVocabulary[] createDTOArray(int size) {
			return new TaxonomyVocabulary[size];
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
			else if (Objects.equals(jsonParserFieldName, "assetTypes")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "availableLanguages")) {

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
			else if (Objects.equals(jsonParserFieldName, "multiValued")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfTaxonomyCategories")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "siteExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "visibilityType")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TaxonomyVocabulary taxonomyVocabulary, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setActions(
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

					taxonomyVocabulary.setAssetLibraries(assetLibrariesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetLibraryKey")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setAssetLibraryKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetTypes")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					AssetType[] assetTypesArray =
						new AssetType[jsonParserFieldValues.length];

					for (int i = 0; i < assetTypesArray.length; i++) {
						assetTypesArray[i] = AssetTypeSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					taxonomyVocabulary.setAssetTypes(assetTypesArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "availableLanguages")) {

				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setAvailableLanguages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "multiValued")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setMultiValued(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfTaxonomyCategories")) {

				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setNumberOfTaxonomyCategories(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.admin.taxonomy.client.permission.
						Permission[] permissionsArray = new
						com.liferay.headless.admin.taxonomy.client.permission.
							Permission[jsonParserFieldValues.length];

					for (int i = 0; i < permissionsArray.length; i++) {
						permissionsArray[i] =
							com.liferay.headless.admin.taxonomy.client.
								permission.Permission.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					taxonomyVocabulary.setPermissions(permissionsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "siteExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setSiteExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setViewableBy(
						TaxonomyVocabulary.ViewableBy.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "visibilityType")) {
				if (jsonParserFieldValue != null) {
					taxonomyVocabulary.setVisibilityType(
						TaxonomyVocabulary.VisibilityType.create(
							(String)jsonParserFieldValue));
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