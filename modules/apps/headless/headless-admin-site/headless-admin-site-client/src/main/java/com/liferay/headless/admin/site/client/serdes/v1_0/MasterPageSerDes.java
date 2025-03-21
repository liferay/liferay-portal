/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.Keyword;
import com.liferay.headless.admin.site.client.dto.v1_0.MasterPage;
import com.liferay.headless.admin.site.client.dto.v1_0.PageSpecification;
import com.liferay.headless.admin.site.client.dto.v1_0.TaxonomyCategory;
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
public class MasterPageSerDes {

	public static MasterPage toDTO(String json) {
		MasterPageJSONParser masterPageJSONParser = new MasterPageJSONParser();

		return masterPageJSONParser.parseToDTO(json);
	}

	public static MasterPage[] toDTOs(String json) {
		MasterPageJSONParser masterPageJSONParser = new MasterPageJSONParser();

		return masterPageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(MasterPage masterPage) {
		if (masterPage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (masterPage.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(masterPage.getCreator());
		}

		if (masterPage.getCreatorExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creatorExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(masterPage.getCreatorExternalReferenceCode()));

			sb.append("\"");
		}

		if (masterPage.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(masterPage.getDateCreated()));

			sb.append("\"");
		}

		if (masterPage.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(masterPage.getDateModified()));

			sb.append("\"");
		}

		if (masterPage.getDatePublished() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"datePublished\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(masterPage.getDatePublished()));

			sb.append("\"");
		}

		if (masterPage.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(masterPage.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (masterPage.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(masterPage.getKey()));

			sb.append("\"");
		}

		if (masterPage.getKeywordItemExternalReferences() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywordItemExternalReferences\": ");

			sb.append("[");

			for (int i = 0;
				 i < masterPage.getKeywordItemExternalReferences().length;
				 i++) {

				sb.append(
					String.valueOf(
						masterPage.getKeywordItemExternalReferences()[i]));

				if ((i + 1) <
						masterPage.getKeywordItemExternalReferences().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (masterPage.getKeywords() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("[");

			for (int i = 0; i < masterPage.getKeywords().length; i++) {
				sb.append(masterPage.getKeywords()[i]);

				if ((i + 1) < masterPage.getKeywords().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (masterPage.getMarkedAsDefault() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"markedAsDefault\": ");

			sb.append(masterPage.getMarkedAsDefault());
		}

		if (masterPage.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(masterPage.getName()));

			sb.append("\"");
		}

		if (masterPage.getPageSpecifications() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageSpecifications\": ");

			sb.append("[");

			for (int i = 0; i < masterPage.getPageSpecifications().length;
				 i++) {

				sb.append(
					String.valueOf(masterPage.getPageSpecifications()[i]));

				if ((i + 1) < masterPage.getPageSpecifications().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (masterPage.getTaxonomyCategories() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategories\": ");

			sb.append("[");

			for (int i = 0; i < masterPage.getTaxonomyCategories().length;
				 i++) {

				sb.append(masterPage.getTaxonomyCategories()[i]);

				if ((i + 1) < masterPage.getTaxonomyCategories().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (masterPage.getTaxonomyCategoryItemExternalReferences() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryItemExternalReferences\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 masterPage.
						 getTaxonomyCategoryItemExternalReferences().length;
				 i++) {

				sb.append(
					String.valueOf(
						masterPage.getTaxonomyCategoryItemExternalReferences()
							[i]));

				if ((i + 1) < masterPage.
						getTaxonomyCategoryItemExternalReferences().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (masterPage.getThumbnail() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"thumbnail\": ");

			sb.append(String.valueOf(masterPage.getThumbnail()));
		}

		if (masterPage.getUuid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uuid\": ");

			sb.append("\"");

			sb.append(_escape(masterPage.getUuid()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MasterPageJSONParser masterPageJSONParser = new MasterPageJSONParser();

		return masterPageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(MasterPage masterPage) {
		if (masterPage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (masterPage.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(masterPage.getCreator()));
		}

		if (masterPage.getCreatorExternalReferenceCode() == null) {
			map.put("creatorExternalReferenceCode", null);
		}
		else {
			map.put(
				"creatorExternalReferenceCode",
				String.valueOf(masterPage.getCreatorExternalReferenceCode()));
		}

		if (masterPage.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(masterPage.getDateCreated()));
		}

		if (masterPage.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(masterPage.getDateModified()));
		}

		if (masterPage.getDatePublished() == null) {
			map.put("datePublished", null);
		}
		else {
			map.put(
				"datePublished",
				liferayToJSONDateFormat.format(masterPage.getDatePublished()));
		}

		if (masterPage.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(masterPage.getExternalReferenceCode()));
		}

		if (masterPage.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(masterPage.getKey()));
		}

		if (masterPage.getKeywordItemExternalReferences() == null) {
			map.put("keywordItemExternalReferences", null);
		}
		else {
			map.put(
				"keywordItemExternalReferences",
				String.valueOf(masterPage.getKeywordItemExternalReferences()));
		}

		if (masterPage.getKeywords() == null) {
			map.put("keywords", null);
		}
		else {
			map.put("keywords", String.valueOf(masterPage.getKeywords()));
		}

		if (masterPage.getMarkedAsDefault() == null) {
			map.put("markedAsDefault", null);
		}
		else {
			map.put(
				"markedAsDefault",
				String.valueOf(masterPage.getMarkedAsDefault()));
		}

		if (masterPage.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(masterPage.getName()));
		}

		if (masterPage.getPageSpecifications() == null) {
			map.put("pageSpecifications", null);
		}
		else {
			map.put(
				"pageSpecifications",
				String.valueOf(masterPage.getPageSpecifications()));
		}

		if (masterPage.getTaxonomyCategories() == null) {
			map.put("taxonomyCategories", null);
		}
		else {
			map.put(
				"taxonomyCategories",
				String.valueOf(masterPage.getTaxonomyCategories()));
		}

		if (masterPage.getTaxonomyCategoryItemExternalReferences() == null) {
			map.put("taxonomyCategoryItemExternalReferences", null);
		}
		else {
			map.put(
				"taxonomyCategoryItemExternalReferences",
				String.valueOf(
					masterPage.getTaxonomyCategoryItemExternalReferences()));
		}

		if (masterPage.getThumbnail() == null) {
			map.put("thumbnail", null);
		}
		else {
			map.put("thumbnail", String.valueOf(masterPage.getThumbnail()));
		}

		if (masterPage.getUuid() == null) {
			map.put("uuid", null);
		}
		else {
			map.put("uuid", String.valueOf(masterPage.getUuid()));
		}

		return map;
	}

	public static class MasterPageJSONParser
		extends BaseJSONParser<MasterPage> {

		@Override
		protected MasterPage createDTO() {
			return new MasterPage();
		}

		@Override
		protected MasterPage[] createDTOArray(int size) {
			return new MasterPage[size];
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
			else if (Objects.equals(jsonParserFieldName, "markedAsDefault")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "pageSpecifications")) {

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
			else if (Objects.equals(jsonParserFieldName, "thumbnail")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			MasterPage masterPage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					masterPage.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "creatorExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					masterPage.setCreatorExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					masterPage.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					masterPage.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "datePublished")) {
				if (jsonParserFieldValue != null) {
					masterPage.setDatePublished(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					masterPage.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					masterPage.setKey((String)jsonParserFieldValue);
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

					masterPage.setKeywordItemExternalReferences(
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

					masterPage.setKeywords(keywordsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "markedAsDefault")) {
				if (jsonParserFieldValue != null) {
					masterPage.setMarkedAsDefault(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					masterPage.setName((String)jsonParserFieldValue);
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

					masterPage.setPageSpecifications(pageSpecificationsArray);
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

					masterPage.setTaxonomyCategories(taxonomyCategoriesArray);
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

					masterPage.setTaxonomyCategoryItemExternalReferences(
						taxonomyCategoryItemExternalReferencesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "thumbnail")) {
				if (jsonParserFieldValue != null) {
					masterPage.setThumbnail(
						ItemExternalReferenceSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				if (jsonParserFieldValue != null) {
					masterPage.setUuid((String)jsonParserFieldValue);
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