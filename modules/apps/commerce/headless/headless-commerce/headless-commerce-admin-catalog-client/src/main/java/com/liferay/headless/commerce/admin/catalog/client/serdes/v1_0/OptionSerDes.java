/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Option;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.OptionValue;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class OptionSerDes {

	public static Option toDTO(String json) {
		OptionJSONParser optionJSONParser = new OptionJSONParser();

		return optionJSONParser.parseToDTO(json);
	}

	public static Option[] toDTOs(String json) {
		OptionJSONParser optionJSONParser = new OptionJSONParser();

		return optionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Option option) {
		if (option == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (option.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(option.getActions()));
		}

		if (option.getCatalogId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogId\": ");

			sb.append(option.getCatalogId());
		}

		if (option.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < option.getCustomFields().length; i++) {
				sb.append(option.getCustomFields()[i]);

				if ((i + 1) < option.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (option.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append(_toJSON(option.getDescription()));
		}

		if (option.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(option.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (option.getFacetable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"facetable\": ");

			sb.append(option.getFacetable());
		}

		if (option.getFieldType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fieldType\": ");

			sb.append("\"");

			sb.append(option.getFieldType());

			sb.append("\"");
		}

		if (option.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(option.getId());
		}

		if (option.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(option.getKey()));

			sb.append("\"");
		}

		if (option.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(option.getName()));
		}

		if (option.getOptionValues() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"optionValues\": ");

			sb.append("[");

			for (int i = 0; i < option.getOptionValues().length; i++) {
				sb.append(String.valueOf(option.getOptionValues()[i]));

				if ((i + 1) < option.getOptionValues().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (option.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(option.getPriority());
		}

		if (option.getRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(option.getRequired());
		}

		if (option.getSkuContributor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuContributor\": ");

			sb.append(option.getSkuContributor());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OptionJSONParser optionJSONParser = new OptionJSONParser();

		return optionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Option option) {
		if (option == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (option.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(option.getActions()));
		}

		if (option.getCatalogId() == null) {
			map.put("catalogId", null);
		}
		else {
			map.put("catalogId", String.valueOf(option.getCatalogId()));
		}

		if (option.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put("customFields", String.valueOf(option.getCustomFields()));
		}

		if (option.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(option.getDescription()));
		}

		if (option.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(option.getExternalReferenceCode()));
		}

		if (option.getFacetable() == null) {
			map.put("facetable", null);
		}
		else {
			map.put("facetable", String.valueOf(option.getFacetable()));
		}

		if (option.getFieldType() == null) {
			map.put("fieldType", null);
		}
		else {
			map.put("fieldType", String.valueOf(option.getFieldType()));
		}

		if (option.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(option.getId()));
		}

		if (option.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(option.getKey()));
		}

		if (option.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(option.getName()));
		}

		if (option.getOptionValues() == null) {
			map.put("optionValues", null);
		}
		else {
			map.put("optionValues", String.valueOf(option.getOptionValues()));
		}

		if (option.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(option.getPriority()));
		}

		if (option.getRequired() == null) {
			map.put("required", null);
		}
		else {
			map.put("required", String.valueOf(option.getRequired()));
		}

		if (option.getSkuContributor() == null) {
			map.put("skuContributor", null);
		}
		else {
			map.put(
				"skuContributor", String.valueOf(option.getSkuContributor()));
		}

		return map;
	}

	public static class OptionJSONParser extends BaseJSONParser<Option> {

		@Override
		protected Option createDTO() {
			return new Option();
		}

		@Override
		protected Option[] createDTOArray(int size) {
			return new Option[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "catalogId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "facetable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fieldType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "optionValues")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "skuContributor")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Option option, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					option.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "catalogId")) {
				if (jsonParserFieldValue != null) {
					option.setCatalogId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					com.liferay.headless.commerce.admin.catalog.client.custom.
						field.CustomField[] customFieldsArray = new
						com.liferay.headless.commerce.admin.catalog.client.
							custom.field.CustomField
							[jsonParserFieldValues.length];

					for (int i = 0; i < customFieldsArray.length; i++) {
						customFieldsArray[i] =
							com.liferay.headless.commerce.admin.catalog.client.
								custom.field.CustomField.toDTO(
									(String)jsonParserFieldValues[i]);
					}

					option.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					option.setDescription(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					option.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "facetable")) {
				if (jsonParserFieldValue != null) {
					option.setFacetable((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fieldType")) {
				if (jsonParserFieldValue != null) {
					option.setFieldType(
						Option.FieldType.create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					option.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					option.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					option.setName((Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "optionValues")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					OptionValue[] optionValuesArray =
						new OptionValue[jsonParserFieldValues.length];

					for (int i = 0; i < optionValuesArray.length; i++) {
						optionValuesArray[i] = OptionValueSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					option.setOptionValues(optionValuesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					option.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				if (jsonParserFieldValue != null) {
					option.setRequired((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "skuContributor")) {
				if (jsonParserFieldValue != null) {
					option.setSkuContributor((Boolean)jsonParserFieldValue);
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