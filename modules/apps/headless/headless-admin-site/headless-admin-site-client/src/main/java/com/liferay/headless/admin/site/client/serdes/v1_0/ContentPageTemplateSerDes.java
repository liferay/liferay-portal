/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ContentPageTemplate;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.Keyword;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.TaxonomyCategory;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class ContentPageTemplateSerDes {

	public static ContentPageTemplate toDTO(String json) {
		ContentPageTemplateJSONParser contentPageTemplateJSONParser =
			new ContentPageTemplateJSONParser();

		return contentPageTemplateJSONParser.parseToDTO(json);
	}

	public static ContentPageTemplate[] toDTOs(String json) {
		ContentPageTemplateJSONParser contentPageTemplateJSONParser =
			new ContentPageTemplateJSONParser();

		return contentPageTemplateJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ContentPageTemplate contentPageTemplate) {
		if (contentPageTemplate == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (contentPageTemplate.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(contentPageTemplate.getCreator());
		}

		if (contentPageTemplate.getCreatorExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creatorExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(contentPageTemplate.getCreatorExternalReferenceCode()));

			sb.append("\"");
		}

		if (contentPageTemplate.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					contentPageTemplate.getDateCreated()));

			sb.append("\"");
		}

		if (contentPageTemplate.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					contentPageTemplate.getDateModified()));

			sb.append("\"");
		}

		if (contentPageTemplate.getDatePublished() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"datePublished\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					contentPageTemplate.getDatePublished()));

			sb.append("\"");
		}

		if (contentPageTemplate.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(contentPageTemplate.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (contentPageTemplate.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(contentPageTemplate.getKey()));

			sb.append("\"");
		}

		if (contentPageTemplate.getKeywordItemExternalReferences() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywordItemExternalReferences\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 contentPageTemplate.
						 getKeywordItemExternalReferences().length;
				 i++) {

				sb.append(
					String.valueOf(
						contentPageTemplate.getKeywordItemExternalReferences()
							[i]));

				if ((i + 1) < contentPageTemplate.
						getKeywordItemExternalReferences().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (contentPageTemplate.getKeywords() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("[");

			for (int i = 0; i < contentPageTemplate.getKeywords().length; i++) {
				sb.append(contentPageTemplate.getKeywords()[i]);

				if ((i + 1) < contentPageTemplate.getKeywords().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (contentPageTemplate.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(contentPageTemplate.getName()));

			sb.append("\"");
		}

		if (contentPageTemplate.getPageSpecifications() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSpecifications\": ");

			sb.append("[");

			for (int i = 0;
				 i < contentPageTemplate.getPageSpecifications().length; i++) {

				sb.append(
					String.valueOf(
						contentPageTemplate.getPageSpecifications()[i]));

				if ((i + 1) <
						contentPageTemplate.getPageSpecifications().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (contentPageTemplate.getPageTemplateSet() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageTemplateSet\": ");

			sb.append(String.valueOf(contentPageTemplate.getPageTemplateSet()));
		}

		if (contentPageTemplate.getPageTemplateSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageTemplateSettings\": ");

			sb.append(
				String.valueOf(contentPageTemplate.getPageTemplateSettings()));
		}

		if (contentPageTemplate.getTaxonomyCategories() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategories\": ");

			sb.append("[");

			for (int i = 0;
				 i < contentPageTemplate.getTaxonomyCategories().length; i++) {

				sb.append(contentPageTemplate.getTaxonomyCategories()[i]);

				if ((i + 1) <
						contentPageTemplate.getTaxonomyCategories().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (contentPageTemplate.getTaxonomyCategoryItemExternalReferences() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryItemExternalReferences\": ");

			sb.append("[");

			for (int i = 0;
				 i < contentPageTemplate.
					 getTaxonomyCategoryItemExternalReferences().length;
				 i++) {

				sb.append(
					String.valueOf(
						contentPageTemplate.
							getTaxonomyCategoryItemExternalReferences()[i]));

				if ((i + 1) < contentPageTemplate.
						getTaxonomyCategoryItemExternalReferences().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (contentPageTemplate.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(contentPageTemplate.getType());

			sb.append("\"");
		}

		if (contentPageTemplate.getUuid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uuid\": ");

			sb.append("\"");

			sb.append(_escape(contentPageTemplate.getUuid()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ContentPageTemplateJSONParser contentPageTemplateJSONParser =
			new ContentPageTemplateJSONParser();

		return contentPageTemplateJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ContentPageTemplate contentPageTemplate) {

		if (contentPageTemplate == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (contentPageTemplate.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put(
				"creator", String.valueOf(contentPageTemplate.getCreator()));
		}

		if (contentPageTemplate.getCreatorExternalReferenceCode() == null) {
			map.put("creatorExternalReferenceCode", null);
		}
		else {
			map.put(
				"creatorExternalReferenceCode",
				String.valueOf(
					contentPageTemplate.getCreatorExternalReferenceCode()));
		}

		if (contentPageTemplate.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					contentPageTemplate.getDateCreated()));
		}

		if (contentPageTemplate.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					contentPageTemplate.getDateModified()));
		}

		if (contentPageTemplate.getDatePublished() == null) {
			map.put("datePublished", null);
		}
		else {
			map.put(
				"datePublished",
				liferayToJSONDateFormat.format(
					contentPageTemplate.getDatePublished()));
		}

		if (contentPageTemplate.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(contentPageTemplate.getExternalReferenceCode()));
		}

		if (contentPageTemplate.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(contentPageTemplate.getKey()));
		}

		if (contentPageTemplate.getKeywordItemExternalReferences() == null) {
			map.put("keywordItemExternalReferences", null);
		}
		else {
			map.put(
				"keywordItemExternalReferences",
				String.valueOf(
					contentPageTemplate.getKeywordItemExternalReferences()));
		}

		if (contentPageTemplate.getKeywords() == null) {
			map.put("keywords", null);
		}
		else {
			map.put(
				"keywords", String.valueOf(contentPageTemplate.getKeywords()));
		}

		if (contentPageTemplate.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(contentPageTemplate.getName()));
		}

		if (contentPageTemplate.getPageSpecifications() == null) {
			map.put("pageSpecifications", null);
		}
		else {
			map.put(
				"pageSpecifications",
				String.valueOf(contentPageTemplate.getPageSpecifications()));
		}

		if (contentPageTemplate.getPageTemplateSet() == null) {
			map.put("pageTemplateSet", null);
		}
		else {
			map.put(
				"pageTemplateSet",
				String.valueOf(contentPageTemplate.getPageTemplateSet()));
		}

		if (contentPageTemplate.getPageTemplateSettings() == null) {
			map.put("pageTemplateSettings", null);
		}
		else {
			map.put(
				"pageTemplateSettings",
				String.valueOf(contentPageTemplate.getPageTemplateSettings()));
		}

		if (contentPageTemplate.getTaxonomyCategories() == null) {
			map.put("taxonomyCategories", null);
		}
		else {
			map.put(
				"taxonomyCategories",
				String.valueOf(contentPageTemplate.getTaxonomyCategories()));
		}

		if (contentPageTemplate.getTaxonomyCategoryItemExternalReferences() ==
				null) {

			map.put("taxonomyCategoryItemExternalReferences", null);
		}
		else {
			map.put(
				"taxonomyCategoryItemExternalReferences",
				String.valueOf(
					contentPageTemplate.
						getTaxonomyCategoryItemExternalReferences()));
		}

		if (contentPageTemplate.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(contentPageTemplate.getType()));
		}

		if (contentPageTemplate.getUuid() == null) {
			map.put("uuid", null);
		}
		else {
			map.put("uuid", String.valueOf(contentPageTemplate.getUuid()));
		}

		return map;
	}

	public static class ContentPageTemplateJSONParser
		extends BaseJSONParser<ContentPageTemplate> {

		@Override
		protected ContentPageTemplate createDTO() {
			return new ContentPageTemplate();
		}

		@Override
		protected ContentPageTemplate[] createDTOArray(int size) {
			return new ContentPageTemplate[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "creatorExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "keywordItemExternalReferences")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "pageSpecifications")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageTemplateSet")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "pageTemplateSettings")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategories")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"taxonomyCategoryItemExternalReferences")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ContentPageTemplate contentPageTemplate, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					contentPageTemplate.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "creatorExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					contentPageTemplate.setCreatorExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					contentPageTemplate.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					contentPageTemplate.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				if (jsonParserFieldValue != null) {
					contentPageTemplate.setDatePublished(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					contentPageTemplate.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					contentPageTemplate.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "keywordItemExternalReferences")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ItemExternalReference[] keywordItemExternalReferencesArray =
						new ItemExternalReference[jsonParserFieldValues.length];

					for (int i = 0;
						 i < keywordItemExternalReferencesArray.length; i++) {

						keywordItemExternalReferencesArray[i] =
							ItemExternalReferenceSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					contentPageTemplate.setKeywordItemExternalReferences(
						keywordItemExternalReferencesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Keyword[] keywordsArray =
						new Keyword[jsonParserFieldValues.length];

					for (int i = 0; i < keywordsArray.length; i++) {
						keywordsArray[i] = KeywordSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					contentPageTemplate.setKeywords(keywordsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					contentPageTemplate.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "pageSpecifications")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PageSpecification[] pageSpecificationsArray =
						new PageSpecification[jsonParserFieldValues.length];

					for (int i = 0; i < pageSpecificationsArray.length; i++) {
						pageSpecificationsArray[i] =
							PageSpecificationSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					contentPageTemplate.setPageSpecifications(
						pageSpecificationsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageTemplateSet")) {
				if (jsonParserFieldValue != null) {
					contentPageTemplate.setPageTemplateSet(
						PageTemplateSetSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "pageTemplateSettings")) {

				if (jsonParserFieldValue != null) {
					contentPageTemplate.setPageTemplateSettings(
						PageTemplateSettingsSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategories")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					TaxonomyCategory[] taxonomyCategoriesArray =
						new TaxonomyCategory[jsonParserFieldValues.length];

					for (int i = 0; i < taxonomyCategoriesArray.length; i++) {
						taxonomyCategoriesArray[i] =
							TaxonomyCategorySerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					contentPageTemplate.setTaxonomyCategories(
						taxonomyCategoriesArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"taxonomyCategoryItemExternalReferences")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ItemExternalReference[]
						taxonomyCategoryItemExternalReferencesArray =
							new ItemExternalReference
								[jsonParserFieldValues.length];

					for (int i = 0;
						 i < taxonomyCategoryItemExternalReferencesArray.length;
						 i++) {

						taxonomyCategoryItemExternalReferencesArray[i] =
							ItemExternalReferenceSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					contentPageTemplate.
						setTaxonomyCategoryItemExternalReferences(
							taxonomyCategoryItemExternalReferencesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					contentPageTemplate.setType(
						ContentPageTemplate.Type.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				if (jsonParserFieldValue != null) {
					contentPageTemplate.setUuid((String)jsonParserFieldValue);
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