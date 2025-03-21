/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.TaxonomyCategory;
import com.liferay.headless.admin.site.client.dto.v1_0.TaxonomyCategoryProperty;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class TaxonomyCategorySerDes {

	public static TaxonomyCategory toDTO(String json) {
		TaxonomyCategoryJSONParser taxonomyCategoryJSONParser =
			new TaxonomyCategoryJSONParser();

		return taxonomyCategoryJSONParser.parseToDTO(json);
	}

	public static TaxonomyCategory[] toDTOs(String json) {
		TaxonomyCategoryJSONParser taxonomyCategoryJSONParser =
			new TaxonomyCategoryJSONParser();

		return taxonomyCategoryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TaxonomyCategory taxonomyCategory) {
		if (taxonomyCategory == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (taxonomyCategory.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(taxonomyCategory.getActions()));
		}

		if (taxonomyCategory.getAvailableLanguages() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"availableLanguages\": ");

			sb.append("[");

			for (int i = 0; i < taxonomyCategory.getAvailableLanguages().length;
				 i++) {

				sb.append(_toJSON(taxonomyCategory.getAvailableLanguages()[i]));

				if ((i + 1) < taxonomyCategory.getAvailableLanguages().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (taxonomyCategory.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(taxonomyCategory.getCreator());
		}

		if (taxonomyCategory.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					taxonomyCategory.getDateCreated()));

			sb.append("\"");
		}

		if (taxonomyCategory.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					taxonomyCategory.getDateModified()));

			sb.append("\"");
		}

		if (taxonomyCategory.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyCategory.getDescription()));

			sb.append("\"");
		}

		if (taxonomyCategory.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(taxonomyCategory.getDescription_i18n()));
		}

		if (taxonomyCategory.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyCategory.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (taxonomyCategory.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyCategory.getId()));

			sb.append("\"");
		}

		if (taxonomyCategory.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyCategory.getName()));

			sb.append("\"");
		}

		if (taxonomyCategory.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(taxonomyCategory.getName_i18n()));
		}

		if (taxonomyCategory.getNumberOfTaxonomyCategories() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"numberOfTaxonomyCategories\": ");

			sb.append(taxonomyCategory.getNumberOfTaxonomyCategories());
		}

		if (taxonomyCategory.getParentTaxonomyCategory() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentTaxonomyCategory\": ");

			sb.append(taxonomyCategory.getParentTaxonomyCategory());
		}

		if (taxonomyCategory.getParentTaxonomyVocabulary() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"parentTaxonomyVocabulary\": ");

			sb.append(taxonomyCategory.getParentTaxonomyVocabulary());
		}

		if (taxonomyCategory.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < taxonomyCategory.getPermissions().length; i++) {
				sb.append(taxonomyCategory.getPermissions()[i]);

				if ((i + 1) < taxonomyCategory.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (taxonomyCategory.getSiteExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(taxonomyCategory.getSiteExternalReferenceCode()));

			sb.append("\"");
		}

		if (taxonomyCategory.getSiteId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteId\": ");

			sb.append(taxonomyCategory.getSiteId());
		}

		if (taxonomyCategory.getTaxonomyCategoryProperties() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryProperties\": ");

			sb.append("[");

			for (int i = 0;
				 i < taxonomyCategory.getTaxonomyCategoryProperties().length;
				 i++) {

				sb.append(taxonomyCategory.getTaxonomyCategoryProperties()[i]);

				if ((i + 1) <
						taxonomyCategory.
							getTaxonomyCategoryProperties().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (taxonomyCategory.getTaxonomyCategoryUsageCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryUsageCount\": ");

			sb.append(taxonomyCategory.getTaxonomyCategoryUsageCount());
		}

		if (taxonomyCategory.getTaxonomyVocabularyId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyVocabularyId\": ");

			sb.append(taxonomyCategory.getTaxonomyVocabularyId());
		}

		if (taxonomyCategory.getViewableBy() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"viewableBy\": ");

			sb.append("\"");

			sb.append(taxonomyCategory.getViewableBy());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TaxonomyCategoryJSONParser taxonomyCategoryJSONParser =
			new TaxonomyCategoryJSONParser();

		return taxonomyCategoryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(TaxonomyCategory taxonomyCategory) {
		if (taxonomyCategory == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (taxonomyCategory.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(taxonomyCategory.getActions()));
		}

		if (taxonomyCategory.getAvailableLanguages() == null) {
			map.put("availableLanguages", null);
		}
		else {
			map.put(
				"availableLanguages",
				String.valueOf(taxonomyCategory.getAvailableLanguages()));
		}

		if (taxonomyCategory.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(taxonomyCategory.getCreator()));
		}

		if (taxonomyCategory.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					taxonomyCategory.getDateCreated()));
		}

		if (taxonomyCategory.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					taxonomyCategory.getDateModified()));
		}

		if (taxonomyCategory.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(taxonomyCategory.getDescription()));
		}

		if (taxonomyCategory.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(taxonomyCategory.getDescription_i18n()));
		}

		if (taxonomyCategory.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(taxonomyCategory.getExternalReferenceCode()));
		}

		if (taxonomyCategory.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(taxonomyCategory.getId()));
		}

		if (taxonomyCategory.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(taxonomyCategory.getName()));
		}

		if (taxonomyCategory.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put(
				"name_i18n", String.valueOf(taxonomyCategory.getName_i18n()));
		}

		if (taxonomyCategory.getNumberOfTaxonomyCategories() == null) {
			map.put("numberOfTaxonomyCategories", null);
		}
		else {
			map.put(
				"numberOfTaxonomyCategories",
				String.valueOf(
					taxonomyCategory.getNumberOfTaxonomyCategories()));
		}

		if (taxonomyCategory.getParentTaxonomyCategory() == null) {
			map.put("parentTaxonomyCategory", null);
		}
		else {
			map.put(
				"parentTaxonomyCategory",
				String.valueOf(taxonomyCategory.getParentTaxonomyCategory()));
		}

		if (taxonomyCategory.getParentTaxonomyVocabulary() == null) {
			map.put("parentTaxonomyVocabulary", null);
		}
		else {
			map.put(
				"parentTaxonomyVocabulary",
				String.valueOf(taxonomyCategory.getParentTaxonomyVocabulary()));
		}

		if (taxonomyCategory.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions",
				String.valueOf(taxonomyCategory.getPermissions()));
		}

		if (taxonomyCategory.getSiteExternalReferenceCode() == null) {
			map.put("siteExternalReferenceCode", null);
		}
		else {
			map.put(
				"siteExternalReferenceCode",
				String.valueOf(
					taxonomyCategory.getSiteExternalReferenceCode()));
		}

		if (taxonomyCategory.getSiteId() == null) {
			map.put("siteId", null);
		}
		else {
			map.put("siteId", String.valueOf(taxonomyCategory.getSiteId()));
		}

		if (taxonomyCategory.getTaxonomyCategoryProperties() == null) {
			map.put("taxonomyCategoryProperties", null);
		}
		else {
			map.put(
				"taxonomyCategoryProperties",
				String.valueOf(
					taxonomyCategory.getTaxonomyCategoryProperties()));
		}

		if (taxonomyCategory.getTaxonomyCategoryUsageCount() == null) {
			map.put("taxonomyCategoryUsageCount", null);
		}
		else {
			map.put(
				"taxonomyCategoryUsageCount",
				String.valueOf(
					taxonomyCategory.getTaxonomyCategoryUsageCount()));
		}

		if (taxonomyCategory.getTaxonomyVocabularyId() == null) {
			map.put("taxonomyVocabularyId", null);
		}
		else {
			map.put(
				"taxonomyVocabularyId",
				String.valueOf(taxonomyCategory.getTaxonomyVocabularyId()));
		}

		if (taxonomyCategory.getViewableBy() == null) {
			map.put("viewableBy", null);
		}
		else {
			map.put(
				"viewableBy", String.valueOf(taxonomyCategory.getViewableBy()));
		}

		return map;
	}

	public static class TaxonomyCategoryJSONParser
		extends BaseJSONParser<TaxonomyCategory> {

		@Override
		protected TaxonomyCategory createDTO() {
			return new TaxonomyCategory();
		}

		@Override
		protected TaxonomyCategory[] createDTOArray(int size) {
			return new TaxonomyCategory[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
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
			else if (Objects.equals(
						jsonParserFieldName, "parentTaxonomyCategory")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentTaxonomyVocabulary")) {

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
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryProperties")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryUsageCount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyVocabularyId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TaxonomyCategory taxonomyCategory, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategory.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "availableLanguages")) {

				if (jsonParserFieldValue != null) {
					taxonomyCategory.setAvailableLanguages(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategory.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategory.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategory.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategory.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategory.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					taxonomyCategory.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategory.setId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategory.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategory.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "numberOfTaxonomyCategories")) {

				if (jsonParserFieldValue != null) {
					taxonomyCategory.setNumberOfTaxonomyCategories(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentTaxonomyCategory")) {

				if (jsonParserFieldValue != null) {
					taxonomyCategory.setParentTaxonomyCategory(
						ParentTaxonomyCategorySerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "parentTaxonomyVocabulary")) {

				if (jsonParserFieldValue != null) {
					taxonomyCategory.setParentTaxonomyVocabulary(
						ParentTaxonomyVocabularySerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.admin.site.client.permission.
						Permission[] permissionsArray = new
						com.liferay.headless.admin.site.client.permission.
							Permission[jsonParserFieldValues.length];

					for (int i = 0; i < permissionsArray.length; i++) {
						permissionsArray[i] =
							com.liferay.headless.admin.site.client.permission.
								Permission.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					taxonomyCategory.setPermissions(permissionsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "siteExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					taxonomyCategory.setSiteExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "siteId")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategory.setSiteId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryProperties")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					TaxonomyCategoryProperty[] taxonomyCategoryPropertiesArray =
						new TaxonomyCategoryProperty
							[jsonParserFieldValues.length];

					for (int i = 0; i < taxonomyCategoryPropertiesArray.length;
						 i++) {

						taxonomyCategoryPropertiesArray[i] =
							TaxonomyCategoryPropertySerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					taxonomyCategory.setTaxonomyCategoryProperties(
						taxonomyCategoryPropertiesArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryUsageCount")) {

				if (jsonParserFieldValue != null) {
					taxonomyCategory.setTaxonomyCategoryUsageCount(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyVocabularyId")) {

				if (jsonParserFieldValue != null) {
					taxonomyCategory.setTaxonomyVocabularyId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "viewableBy")) {
				if (jsonParserFieldValue != null) {
					taxonomyCategory.setViewableBy(
						TaxonomyCategory.ViewableBy.create(
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