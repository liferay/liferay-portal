/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.EmbeddedPageSpecification;
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
public class EmbeddedPageSpecificationSerDes {

	public static EmbeddedPageSpecification toDTO(String json) {
		EmbeddedPageSpecificationJSONParser
			embeddedPageSpecificationJSONParser =
				new EmbeddedPageSpecificationJSONParser();

		return embeddedPageSpecificationJSONParser.parseToDTO(json);
	}

	public static EmbeddedPageSpecification[] toDTOs(String json) {
		EmbeddedPageSpecificationJSONParser
			embeddedPageSpecificationJSONParser =
				new EmbeddedPageSpecificationJSONParser();

		return embeddedPageSpecificationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		EmbeddedPageSpecification embeddedPageSpecification) {

		if (embeddedPageSpecification == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (embeddedPageSpecification.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0;
				 i < embeddedPageSpecification.getCustomFields().length; i++) {

				sb.append(embeddedPageSpecification.getCustomFields()[i]);

				if ((i + 1) <
						embeddedPageSpecification.getCustomFields().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (embeddedPageSpecification.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(embeddedPageSpecification.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (embeddedPageSpecification.
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
					embeddedPageSpecification.
						getSiteTemplatePageSpecificationExternalReferenceCode()));

			sb.append("\"");
		}

		if (embeddedPageSpecification.getStatus() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");
			sb.append(embeddedPageSpecification.getStatus());
			sb.append("\"");
		}

		if (embeddedPageSpecification.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(embeddedPageSpecification.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		EmbeddedPageSpecificationJSONParser
			embeddedPageSpecificationJSONParser =
				new EmbeddedPageSpecificationJSONParser();

		return embeddedPageSpecificationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		EmbeddedPageSpecification embeddedPageSpecification) {

		if (embeddedPageSpecification == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (embeddedPageSpecification.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields",
				String.valueOf(embeddedPageSpecification.getCustomFields()));
		}

		if (embeddedPageSpecification.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					embeddedPageSpecification.getExternalReferenceCode()));
		}

		if (embeddedPageSpecification.
				getSiteTemplatePageSpecificationExternalReferenceCode() ==
					null) {

			map.put("siteTemplatePageSpecificationExternalReferenceCode", null);
		}
		else {
			map.put(
				"siteTemplatePageSpecificationExternalReferenceCode",
				String.valueOf(
					embeddedPageSpecification.
						getSiteTemplatePageSpecificationExternalReferenceCode()));
		}

		if (embeddedPageSpecification.getStatus() == null) {
			map.put("status", null);
		}
		else {
			map.put(
				"status",
				String.valueOf(embeddedPageSpecification.getStatus()));
		}

		if (embeddedPageSpecification.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type", String.valueOf(embeddedPageSpecification.getType()));
		}

		return map;
	}

	public static class EmbeddedPageSpecificationJSONParser
		extends BaseJSONParser<EmbeddedPageSpecification> {

		@Override
		protected EmbeddedPageSpecification createDTO() {
			return new EmbeddedPageSpecification();
		}

		@Override
		protected EmbeddedPageSpecification[] createDTOArray(int size) {
			return new EmbeddedPageSpecification[size];
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
			EmbeddedPageSpecification embeddedPageSpecification,
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

					embeddedPageSpecification.setCustomFields(
						customFieldsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					embeddedPageSpecification.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"siteTemplatePageSpecificationExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					embeddedPageSpecification.
						setSiteTemplatePageSpecificationExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "status")) {
				if (jsonParserFieldValue != null) {
					embeddedPageSpecification.setStatus(
						EmbeddedPageSpecification.Status.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					embeddedPageSpecification.setType(
						EmbeddedPageSpecification.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:1425401695