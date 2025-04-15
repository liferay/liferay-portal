/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.PageTemplate;
import com.liferay.headless.delivery.client.dto.v1_0.TaxonomyCategoryBrief;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

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
public class PageTemplateSerDes {

	public static PageTemplate toDTO(String json) {
		PageTemplateJSONParser pageTemplateJSONParser =
			new PageTemplateJSONParser();

		return pageTemplateJSONParser.parseToDTO(json);
	}

	public static PageTemplate[] toDTOs(String json) {
		PageTemplateJSONParser pageTemplateJSONParser =
			new PageTemplateJSONParser();

		return pageTemplateJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PageTemplate pageTemplate) {
		if (pageTemplate == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (pageTemplate.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(String.valueOf(pageTemplate.getCreator()));
		}

		if (pageTemplate.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(pageTemplate.getDateCreated()));

			sb.append("\"");
		}

		if (pageTemplate.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(pageTemplate.getDateModified()));

			sb.append("\"");
		}

		if (pageTemplate.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(pageTemplate.getId());
		}

		if (pageTemplate.getKeywords() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keywords\": ");

			sb.append("[");

			for (int i = 0; i < pageTemplate.getKeywords().length; i++) {
				sb.append(_toJSON(pageTemplate.getKeywords()[i]));

				if ((i + 1) < pageTemplate.getKeywords().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageTemplate.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(pageTemplate.getName()));

			sb.append("\"");
		}

		if (pageTemplate.getPageDefinition() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageDefinition\": ");

			sb.append(String.valueOf(pageTemplate.getPageDefinition()));
		}

		if (pageTemplate.getPageTemplateCollection() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageTemplateCollection\": ");

			sb.append(String.valueOf(pageTemplate.getPageTemplateCollection()));
		}

		if (pageTemplate.getTaxonomyCategoryBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryBriefs\": ");

			sb.append("[");

			for (int i = 0; i < pageTemplate.getTaxonomyCategoryBriefs().length;
				 i++) {

				sb.append(
					String.valueOf(
						pageTemplate.getTaxonomyCategoryBriefs()[i]));

				if ((i + 1) < pageTemplate.getTaxonomyCategoryBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageTemplate.getTaxonomyCategoryIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"taxonomyCategoryIds\": ");

			sb.append("[");

			for (int i = 0; i < pageTemplate.getTaxonomyCategoryIds().length;
				 i++) {

				sb.append(pageTemplate.getTaxonomyCategoryIds()[i]);

				if ((i + 1) < pageTemplate.getTaxonomyCategoryIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageTemplate.getUuid() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uuid\": ");

			sb.append("\"");

			sb.append(_escape(pageTemplate.getUuid()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageTemplateJSONParser pageTemplateJSONParser =
			new PageTemplateJSONParser();

		return pageTemplateJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PageTemplate pageTemplate) {
		if (pageTemplate == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (pageTemplate.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(pageTemplate.getCreator()));
		}

		if (pageTemplate.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(pageTemplate.getDateCreated()));
		}

		if (pageTemplate.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(pageTemplate.getDateModified()));
		}

		if (pageTemplate.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(pageTemplate.getId()));
		}

		if (pageTemplate.getKeywords() == null) {
			map.put("keywords", null);
		}
		else {
			map.put("keywords", String.valueOf(pageTemplate.getKeywords()));
		}

		if (pageTemplate.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(pageTemplate.getName()));
		}

		if (pageTemplate.getPageDefinition() == null) {
			map.put("pageDefinition", null);
		}
		else {
			map.put(
				"pageDefinition",
				String.valueOf(pageTemplate.getPageDefinition()));
		}

		if (pageTemplate.getPageTemplateCollection() == null) {
			map.put("pageTemplateCollection", null);
		}
		else {
			map.put(
				"pageTemplateCollection",
				String.valueOf(pageTemplate.getPageTemplateCollection()));
		}

		if (pageTemplate.getTaxonomyCategoryBriefs() == null) {
			map.put("taxonomyCategoryBriefs", null);
		}
		else {
			map.put(
				"taxonomyCategoryBriefs",
				String.valueOf(pageTemplate.getTaxonomyCategoryBriefs()));
		}

		if (pageTemplate.getTaxonomyCategoryIds() == null) {
			map.put("taxonomyCategoryIds", null);
		}
		else {
			map.put(
				"taxonomyCategoryIds",
				String.valueOf(pageTemplate.getTaxonomyCategoryIds()));
		}

		if (pageTemplate.getUuid() == null) {
			map.put("uuid", null);
		}
		else {
			map.put("uuid", String.valueOf(pageTemplate.getUuid()));
		}

		return map;
	}

	public static class PageTemplateJSONParser
		extends BaseJSONParser<PageTemplate> {

		@Override
		protected PageTemplate createDTO() {
			return new PageTemplate();
		}

		@Override
		protected PageTemplate[] createDTOArray(int size) {
			return new PageTemplate[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageDefinition")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "pageTemplateCollection")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryBriefs")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryIds")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageTemplate pageTemplate, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					pageTemplate.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					pageTemplate.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					pageTemplate.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					pageTemplate.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "keywords")) {
				if (jsonParserFieldValue != null) {
					pageTemplate.setKeywords(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					pageTemplate.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageDefinition")) {
				if (jsonParserFieldValue != null) {
					pageTemplate.setPageDefinition(
						PageDefinitionSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "pageTemplateCollection")) {

				if (jsonParserFieldValue != null) {
					pageTemplate.setPageTemplateCollection(
						PageTemplateCollectionSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryBriefs")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					TaxonomyCategoryBrief[] taxonomyCategoryBriefsArray =
						new TaxonomyCategoryBrief[jsonParserFieldValues.length];

					for (int i = 0; i < taxonomyCategoryBriefsArray.length;
						 i++) {

						taxonomyCategoryBriefsArray[i] =
							TaxonomyCategoryBriefSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					pageTemplate.setTaxonomyCategoryBriefs(
						taxonomyCategoryBriefsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "taxonomyCategoryIds")) {

				if (jsonParserFieldValue != null) {
					pageTemplate.setTaxonomyCategoryIds(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "uuid")) {
				if (jsonParserFieldValue != null) {
					pageTemplate.setUuid((String)jsonParserFieldValue);
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