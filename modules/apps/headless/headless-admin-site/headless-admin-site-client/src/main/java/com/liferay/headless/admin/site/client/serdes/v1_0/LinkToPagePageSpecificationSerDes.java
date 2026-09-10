/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.LinkToPagePageSpecification;
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
public class LinkToPagePageSpecificationSerDes {

	public static LinkToPagePageSpecification toDTO(String json) {
		LinkToPagePageSpecificationJSONParser
			linkToPagePageSpecificationJSONParser =
				new LinkToPagePageSpecificationJSONParser();

		return linkToPagePageSpecificationJSONParser.parseToDTO(json);
	}

	public static LinkToPagePageSpecification[] toDTOs(String json) {
		LinkToPagePageSpecificationJSONParser
			linkToPagePageSpecificationJSONParser =
				new LinkToPagePageSpecificationJSONParser();

		return linkToPagePageSpecificationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		LinkToPagePageSpecification linkToPagePageSpecification) {

		if (linkToPagePageSpecification == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (linkToPagePageSpecification.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0;
				 i < linkToPagePageSpecification.getCustomFields().length;
				 i++) {

				sb.append(linkToPagePageSpecification.getCustomFields()[i]);

				if ((i + 1) <
						linkToPagePageSpecification.getCustomFields().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (linkToPagePageSpecification.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					linkToPagePageSpecification.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (linkToPagePageSpecification.
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
					linkToPagePageSpecification.
						getSiteTemplatePageSpecificationExternalReferenceCode()));

			sb.append("\"");
		}

		if (linkToPagePageSpecification.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");
			sb.append(linkToPagePageSpecification.getStatus());
			sb.append("\"");
		}

		if (linkToPagePageSpecification.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(linkToPagePageSpecification.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		LinkToPagePageSpecificationJSONParser
			linkToPagePageSpecificationJSONParser =
				new LinkToPagePageSpecificationJSONParser();

		return linkToPagePageSpecificationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		LinkToPagePageSpecification linkToPagePageSpecification) {

		if (linkToPagePageSpecification == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (linkToPagePageSpecification.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields",
				String.valueOf(linkToPagePageSpecification.getCustomFields()));
		}

		if (linkToPagePageSpecification.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					linkToPagePageSpecification.getExternalReferenceCode()));
		}

		if (linkToPagePageSpecification.
				getSiteTemplatePageSpecificationExternalReferenceCode() ==
					null) {

			map.put("siteTemplatePageSpecificationExternalReferenceCode", null);
		}
		else {
			map.put(
				"siteTemplatePageSpecificationExternalReferenceCode",
				String.valueOf(
					linkToPagePageSpecification.
						getSiteTemplatePageSpecificationExternalReferenceCode()));
		}

		if (linkToPagePageSpecification.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put(
				"status",
				String.valueOf(linkToPagePageSpecification.getStatus()));
		}

		if (linkToPagePageSpecification.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type", String.valueOf(linkToPagePageSpecification.getType()));
		}

		return map;
	}

	public static class LinkToPagePageSpecificationJSONParser
		extends BaseJSONParser<LinkToPagePageSpecification> {

		@Override
		protected LinkToPagePageSpecification createDTO() {
			return new LinkToPagePageSpecification();
		}

		@Override
		protected LinkToPagePageSpecification[] createDTOArray(int size) {
			return new LinkToPagePageSpecification[size];
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
			LinkToPagePageSpecification linkToPagePageSpecification,
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

					linkToPagePageSpecification.setCustomFields(
						customFieldsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					linkToPagePageSpecification.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"siteTemplatePageSpecificationExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					linkToPagePageSpecification.
						setSiteTemplatePageSpecificationExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					linkToPagePageSpecification.setStatus(
						LinkToPagePageSpecification.Status.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					linkToPagePageSpecification.setType(
						LinkToPagePageSpecification.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-562632731