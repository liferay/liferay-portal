/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.rest.builder.test.client.serdes.v1_0;

import com.liferay.portal.tools.rest.builder.test.client.dto.v1_0.BatchTestEntity;
import com.liferay.portal.tools.rest.builder.test.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alejandro Tardín
 * @generated
 */
@Generated("")
public class BatchTestEntitySerDes {

	public static BatchTestEntity toDTO(String json) {
		BatchTestEntityJSONParser batchTestEntityJSONParser =
			new BatchTestEntityJSONParser();

		return batchTestEntityJSONParser.parseToDTO(json);
	}

	public static BatchTestEntity[] toDTOs(String json) {
		BatchTestEntityJSONParser batchTestEntityJSONParser =
			new BatchTestEntityJSONParser();

		return batchTestEntityJSONParser.parseToDTOs(json);
	}

	public static String toJSON(BatchTestEntity batchTestEntity) {
		if (batchTestEntity == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (batchTestEntity.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < batchTestEntity.getCustomFields().length; i++) {
				sb.append(batchTestEntity.getCustomFields()[i]);

				if ((i + 1) < batchTestEntity.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (batchTestEntity.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(batchTestEntity.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (batchTestEntity.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(batchTestEntity.getId());
		}

		if (batchTestEntity.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(batchTestEntity.getName()));

			sb.append("\"");
		}

		if (batchTestEntity.getNestedField() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nestedField\": ");

			sb.append("\"");

			sb.append(_escape(batchTestEntity.getNestedField()));

			sb.append("\"");
		}

		if (batchTestEntity.getRelatedCompanyTestEntity() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"relatedCompanyTestEntity\": ");

			sb.append(
				String.valueOf(batchTestEntity.getRelatedCompanyTestEntity()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		BatchTestEntityJSONParser batchTestEntityJSONParser =
			new BatchTestEntityJSONParser();

		return batchTestEntityJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(BatchTestEntity batchTestEntity) {
		if (batchTestEntity == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (batchTestEntity.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields",
				String.valueOf(batchTestEntity.getCustomFields()));
		}

		if (batchTestEntity.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(batchTestEntity.getExternalReferenceCode()));
		}

		if (batchTestEntity.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(batchTestEntity.getId()));
		}

		if (batchTestEntity.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(batchTestEntity.getName()));
		}

		if (batchTestEntity.getNestedField() == null) {
			map.put("nestedField", null);
		}
		else {
			map.put(
				"nestedField",
				String.valueOf(batchTestEntity.getNestedField()));
		}

		if (batchTestEntity.getRelatedCompanyTestEntity() == null) {
			map.put("relatedCompanyTestEntity", null);
		}
		else {
			map.put(
				"relatedCompanyTestEntity",
				String.valueOf(batchTestEntity.getRelatedCompanyTestEntity()));
		}

		return map;
	}

	public static class BatchTestEntityJSONParser
		extends BaseJSONParser<BatchTestEntity> {

		@Override
		protected BatchTestEntity createDTO() {
			return new BatchTestEntity();
		}

		@Override
		protected BatchTestEntity[] createDTOArray(int size) {
			return new BatchTestEntity[size];
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
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "nestedField")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "relatedCompanyTestEntity")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			BatchTestEntity batchTestEntity, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.portal.tools.rest.builder.test.client.custom.
						field.CustomField[] customFieldsArray = new
						com.liferay.portal.tools.rest.builder.test.client.
							custom.field.CustomField
							[jsonParserFieldValues.length];

					for (int i = 0; i < customFieldsArray.length; i++) {
						customFieldsArray[i] =
							com.liferay.portal.tools.rest.builder.test.client.
								custom.field.CustomField.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					batchTestEntity.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					batchTestEntity.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					batchTestEntity.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					batchTestEntity.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "nestedField")) {
				if (jsonParserFieldValue != null) {
					batchTestEntity.setNestedField(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "relatedCompanyTestEntity")) {

				if (jsonParserFieldValue != null) {
					batchTestEntity.setRelatedCompanyTestEntity(
						CompanyTestEntitySerDes.toDTO(
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