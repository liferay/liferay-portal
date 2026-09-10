/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.SiteTemplate;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
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
public class SiteTemplateSerDes {

	public static SiteTemplate toDTO(String json) {
		SiteTemplateJSONParser siteTemplateJSONParser =
			new SiteTemplateJSONParser();

		return siteTemplateJSONParser.parseToDTO(json);
	}

	public static SiteTemplate[] toDTOs(String json) {
		SiteTemplateJSONParser siteTemplateJSONParser =
			new SiteTemplateJSONParser();

		return siteTemplateJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SiteTemplate siteTemplate) {
		if (siteTemplate == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (siteTemplate.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(siteTemplate.getActive());
		}

		if (siteTemplate.getDefaultLanguageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultLanguageId\": ");

			sb.append("\"");

			sb.append(_escape(siteTemplate.getDefaultLanguageId()));

			sb.append("\"");
		}

		if (siteTemplate.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(siteTemplate.getDescription()));

			sb.append("\"");
		}

		if (siteTemplate.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(siteTemplate.getDescription_i18n()));
		}

		if (siteTemplate.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(siteTemplate.getId());
		}

		if (siteTemplate.getLogo() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"logo\": ");

			sb.append("\"");

			sb.append(_escape(siteTemplate.getLogo()));

			sb.append("\"");
		}

		if (siteTemplate.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(siteTemplate.getName()));

			sb.append("\"");
		}

		if (siteTemplate.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(siteTemplate.getName_i18n()));
		}

		if (siteTemplate.getPagesUpdateable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pagesUpdateable\": ");

			sb.append(siteTemplate.getPagesUpdateable());
		}

		if (siteTemplate.getPermissions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"permissions\": ");

			sb.append("[");

			for (int i = 0; i < siteTemplate.getPermissions().length; i++) {
				sb.append(siteTemplate.getPermissions()[i]);

				if ((i + 1) < siteTemplate.getPermissions().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (siteTemplate.getSiteExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(siteTemplate.getSiteExternalReferenceCode()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SiteTemplateJSONParser siteTemplateJSONParser =
			new SiteTemplateJSONParser();

		return siteTemplateJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SiteTemplate siteTemplate) {
		if (siteTemplate == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (siteTemplate.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(siteTemplate.getActive()));
		}

		if (siteTemplate.getDefaultLanguageId() == null) {
			map.put("defaultLanguageId", null);
		}
		else {
			map.put(
				"defaultLanguageId",
				String.valueOf(siteTemplate.getDefaultLanguageId()));
		}

		if (siteTemplate.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(siteTemplate.getDescription()));
		}

		if (siteTemplate.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(siteTemplate.getDescription_i18n()));
		}

		if (siteTemplate.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(siteTemplate.getId()));
		}

		if (siteTemplate.getLogo() == null) {
			map.put("logo", null);
		}
		else {
			map.put("logo", String.valueOf(siteTemplate.getLogo()));
		}

		if (siteTemplate.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(siteTemplate.getName()));
		}

		if (siteTemplate.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put("name_i18n", String.valueOf(siteTemplate.getName_i18n()));
		}

		if (siteTemplate.getPagesUpdateable() == null) {
			map.put("pagesUpdateable", null);
		}
		else {
			map.put(
				"pagesUpdateable",
				String.valueOf(siteTemplate.getPagesUpdateable()));
		}

		if (siteTemplate.getPermissions() == null) {
			map.put("permissions", null);
		}
		else {
			map.put(
				"permissions", String.valueOf(siteTemplate.getPermissions()));
		}

		if (siteTemplate.getSiteExternalReferenceCode() == null) {
			map.put("siteExternalReferenceCode", null);
		}
		else {
			map.put(
				"siteExternalReferenceCode",
				String.valueOf(siteTemplate.getSiteExternalReferenceCode()));
		}

		return map;
	}

	public static class SiteTemplateJSONParser
		extends BaseJSONParser<SiteTemplate> {

		@Override
		protected SiteTemplate createDTO() {
			return new SiteTemplate();
		}

		@Override
		protected SiteTemplate[] createDTOArray(int size) {
			return new SiteTemplate[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultLanguageId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "logo")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "pagesUpdateable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "permissions")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "siteExternalReferenceCode")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SiteTemplate siteTemplate, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					siteTemplate.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultLanguageId")) {
				if (jsonParserFieldValue != null) {
					siteTemplate.setDefaultLanguageId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					siteTemplate.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					siteTemplate.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					siteTemplate.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "logo")) {
				if (jsonParserFieldValue != null) {
					siteTemplate.setLogo((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					siteTemplate.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					siteTemplate.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pagesUpdateable")) {
				if (jsonParserFieldValue != null) {
					siteTemplate.setPagesUpdateable(
						(Boolean)jsonParserFieldValue);
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

					siteTemplate.setPermissions(permissionsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "siteExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					siteTemplate.setSiteExternalReferenceCode(
						(String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-381426555