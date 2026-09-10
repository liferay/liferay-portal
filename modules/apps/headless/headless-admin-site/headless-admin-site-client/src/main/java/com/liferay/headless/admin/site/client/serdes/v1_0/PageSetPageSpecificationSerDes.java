/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.PageSetPageSpecification;
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
public class PageSetPageSpecificationSerDes {

	public static PageSetPageSpecification toDTO(String json) {
		PageSetPageSpecificationJSONParser pageSetPageSpecificationJSONParser =
			new PageSetPageSpecificationJSONParser();

		return pageSetPageSpecificationJSONParser.parseToDTO(json);
	}

	public static PageSetPageSpecification[] toDTOs(String json) {
		PageSetPageSpecificationJSONParser pageSetPageSpecificationJSONParser =
			new PageSetPageSpecificationJSONParser();

		return pageSetPageSpecificationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PageSetPageSpecification pageSetPageSpecification) {

		if (pageSetPageSpecification == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pageSetPageSpecification.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0;
				 i < pageSetPageSpecification.getCustomFields().length; i++) {

				sb.append(pageSetPageSpecification.getCustomFields()[i]);

				if ((i + 1) <
						pageSetPageSpecification.getCustomFields().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageSetPageSpecification.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(pageSetPageSpecification.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (pageSetPageSpecification.
				getSiteTemplatePageSpecificationExternalReferenceCode() !=
					null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(
				"\"siteTemplatePageSpecificationExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					pageSetPageSpecification.
						getSiteTemplatePageSpecificationExternalReferenceCode()));

			sb.append("\"");
		}

		if (pageSetPageSpecification.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");
			sb.append(pageSetPageSpecification.getStatus());
			sb.append("\"");
		}

		if (pageSetPageSpecification.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(pageSetPageSpecification.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageSetPageSpecificationJSONParser pageSetPageSpecificationJSONParser =
			new PageSetPageSpecificationJSONParser();

		return pageSetPageSpecificationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PageSetPageSpecification pageSetPageSpecification) {

		if (pageSetPageSpecification == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageSetPageSpecification.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields",
				String.valueOf(pageSetPageSpecification.getCustomFields()));
		}

		if (pageSetPageSpecification.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					pageSetPageSpecification.getExternalReferenceCode()));
		}

		if (pageSetPageSpecification.
				getSiteTemplatePageSpecificationExternalReferenceCode() ==
					null) {

			map.put("siteTemplatePageSpecificationExternalReferenceCode", null);
		}
		else {
			map.put(
				"siteTemplatePageSpecificationExternalReferenceCode",
				String.valueOf(
					pageSetPageSpecification.
						getSiteTemplatePageSpecificationExternalReferenceCode()));
		}

		if (pageSetPageSpecification.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put(
				"status", String.valueOf(pageSetPageSpecification.getStatus()));
		}

		if (pageSetPageSpecification.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(pageSetPageSpecification.getType()));
		}

		return map;
	}

	public static class PageSetPageSpecificationJSONParser
		extends BaseJSONParser<PageSetPageSpecification> {

		@Override
		protected PageSetPageSpecification createDTO() {
			return new PageSetPageSpecification();
		}

		@Override
		protected PageSetPageSpecification[] createDTOArray(int size) {
			return new PageSetPageSpecification[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "customFields")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"siteTemplatePageSpecificationExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageSetPageSpecification pageSetPageSpecification,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.admin.site.client.custom.field.
						CustomField[] customFieldsArray = new
						com.liferay.headless.admin.site.client.custom.field.
							CustomField[jsonParserFieldValues.length];

					for (int i = 0; i < customFieldsArray.length; i++) {
						customFieldsArray[i] =
							com.liferay.headless.admin.site.client.custom.field.
								CustomField.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					pageSetPageSpecification.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					pageSetPageSpecification.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"siteTemplatePageSpecificationExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					pageSetPageSpecification.
						setSiteTemplatePageSpecificationExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					pageSetPageSpecification.setStatus(
						PageSetPageSpecification.Status.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					pageSetPageSpecification.setType(
						PageSetPageSpecification.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-282467327