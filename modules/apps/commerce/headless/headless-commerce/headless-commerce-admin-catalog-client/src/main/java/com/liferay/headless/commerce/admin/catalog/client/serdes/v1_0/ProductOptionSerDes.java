/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductOption;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductOptionValue;
import com.liferay.headless.commerce.admin.catalog.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class ProductOptionSerDes {

	public static ProductOption toDTO(String json) {
		ProductOptionJSONParser productOptionJSONParser =
			new ProductOptionJSONParser();

		return productOptionJSONParser.parseToDTO(json);
	}

	public static ProductOption[] toDTOs(String json) {
		ProductOptionJSONParser productOptionJSONParser =
			new ProductOptionJSONParser();

		return productOptionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ProductOption productOption) {
		if (productOption == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (productOption.getCatalogId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"catalogId\": ");

			sb.append(productOption.getCatalogId());
		}

		if (productOption.getCustomFields() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customFields\": ");

			sb.append("[");

			for (int i = 0; i < productOption.getCustomFields().length; i++) {
				sb.append(productOption.getCustomFields()[i]);

				if ((i + 1) < productOption.getCustomFields().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (productOption.getDefinedExternally() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"definedExternally\": ");

			sb.append(productOption.getDefinedExternally());
		}

		if (productOption.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append(_toJSON(productOption.getDescription()));
		}

		if (productOption.getFacetable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"facetable\": ");

			sb.append(productOption.getFacetable());
		}

		if (productOption.getFieldType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fieldType\": ");

			sb.append("\"");

			sb.append(_escape(productOption.getFieldType()));

			sb.append("\"");
		}

		if (productOption.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(productOption.getId());
		}

		if (productOption.getInfoItemServiceKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"infoItemServiceKey\": ");

			sb.append("\"");

			sb.append(_escape(productOption.getInfoItemServiceKey()));

			sb.append("\"");
		}

		if (productOption.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(productOption.getKey()));

			sb.append("\"");
		}

		if (productOption.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(productOption.getName()));
		}

		if (productOption.getOptionExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"optionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(productOption.getOptionExternalReferenceCode()));

			sb.append("\"");
		}

		if (productOption.getOptionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"optionId\": ");

			sb.append(productOption.getOptionId());
		}

		if (productOption.getPriceType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priceType\": ");

			sb.append("\"");

			sb.append(_escape(productOption.getPriceType()));

			sb.append("\"");
		}

		if (productOption.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(productOption.getPriority());
		}

		if (productOption.getProductOptionValues() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"productOptionValues\": ");

			sb.append("[");

			for (int i = 0; i < productOption.getProductOptionValues().length;
				 i++) {

				sb.append(
					String.valueOf(productOption.getProductOptionValues()[i]));

				if ((i + 1) < productOption.getProductOptionValues().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (productOption.getRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(productOption.getRequired());
		}

		if (productOption.getSkuContributor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"skuContributor\": ");

			sb.append(productOption.getSkuContributor());
		}

		if (productOption.getTypeSettings() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"typeSettings\": ");

			sb.append("\"");

			sb.append(_escape(productOption.getTypeSettings()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ProductOptionJSONParser productOptionJSONParser =
			new ProductOptionJSONParser();

		return productOptionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ProductOption productOption) {
		if (productOption == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (productOption.getCatalogId() == null) {
			map.put("catalogId", null);
		}
		else {
			map.put("catalogId", String.valueOf(productOption.getCatalogId()));
		}

		if (productOption.getCustomFields() == null) {
			map.put("customFields", null);
		}
		else {
			map.put(
				"customFields",
				String.valueOf(productOption.getCustomFields()));
		}

		if (productOption.getDefinedExternally() == null) {
			map.put("definedExternally", null);
		}
		else {
			map.put(
				"definedExternally",
				String.valueOf(productOption.getDefinedExternally()));
		}

		if (productOption.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(productOption.getDescription()));
		}

		if (productOption.getFacetable() == null) {
			map.put("facetable", null);
		}
		else {
			map.put("facetable", String.valueOf(productOption.getFacetable()));
		}

		if (productOption.getFieldType() == null) {
			map.put("fieldType", null);
		}
		else {
			map.put("fieldType", String.valueOf(productOption.getFieldType()));
		}

		if (productOption.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(productOption.getId()));
		}

		if (productOption.getInfoItemServiceKey() == null) {
			map.put("infoItemServiceKey", null);
		}
		else {
			map.put(
				"infoItemServiceKey",
				String.valueOf(productOption.getInfoItemServiceKey()));
		}

		if (productOption.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(productOption.getKey()));
		}

		if (productOption.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(productOption.getName()));
		}

		if (productOption.getOptionExternalReferenceCode() == null) {
			map.put("optionExternalReferenceCode", null);
		}
		else {
			map.put(
				"optionExternalReferenceCode",
				String.valueOf(productOption.getOptionExternalReferenceCode()));
		}

		if (productOption.getOptionId() == null) {
			map.put("optionId", null);
		}
		else {
			map.put("optionId", String.valueOf(productOption.getOptionId()));
		}

		if (productOption.getPriceType() == null) {
			map.put("priceType", null);
		}
		else {
			map.put("priceType", String.valueOf(productOption.getPriceType()));
		}

		if (productOption.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(productOption.getPriority()));
		}

		if (productOption.getProductOptionValues() == null) {
			map.put("productOptionValues", null);
		}
		else {
			map.put(
				"productOptionValues",
				String.valueOf(productOption.getProductOptionValues()));
		}

		if (productOption.getRequired() == null) {
			map.put("required", null);
		}
		else {
			map.put("required", String.valueOf(productOption.getRequired()));
		}

		if (productOption.getSkuContributor() == null) {
			map.put("skuContributor", null);
		}
		else {
			map.put(
				"skuContributor",
				String.valueOf(productOption.getSkuContributor()));
		}

		if (productOption.getTypeSettings() == null) {
			map.put("typeSettings", null);
		}
		else {
			map.put(
				"typeSettings",
				String.valueOf(productOption.getTypeSettings()));
		}

		return map;
	}

	public static class ProductOptionJSONParser
		extends BaseJSONParser<ProductOption> {

		@Override
		protected ProductOption createDTO() {
			return new ProductOption();
		}

		@Override
		protected ProductOption[] createDTOArray(int size) {
			return new ProductOption[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "catalogId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customFields")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "definedExternally")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return true;
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
			else if (Objects.equals(
						jsonParserFieldName, "infoItemServiceKey")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "optionExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "optionId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priceType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "productOptionValues")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "skuContributor")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "typeSettings")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ProductOption productOption, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "catalogId")) {
				if (jsonParserFieldValue != null) {
					productOption.setCatalogId(
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

					productOption.setCustomFields(customFieldsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "definedExternally")) {
				if (jsonParserFieldValue != null) {
					productOption.setDefinedExternally(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					productOption.setDescription(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "facetable")) {
				if (jsonParserFieldValue != null) {
					productOption.setFacetable((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fieldType")) {
				if (jsonParserFieldValue != null) {
					productOption.setFieldType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					productOption.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "infoItemServiceKey")) {

				if (jsonParserFieldValue != null) {
					productOption.setInfoItemServiceKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					productOption.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					productOption.setName(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "optionExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					productOption.setOptionExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "optionId")) {
				if (jsonParserFieldValue != null) {
					productOption.setOptionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priceType")) {
				if (jsonParserFieldValue != null) {
					productOption.setPriceType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					productOption.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "productOptionValues")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ProductOptionValue[] productOptionValuesArray =
						new ProductOptionValue[jsonParserFieldValues.length];

					for (int i = 0; i < productOptionValuesArray.length; i++) {
						productOptionValuesArray[i] =
							ProductOptionValueSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					productOption.setProductOptionValues(
						productOptionValuesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				if (jsonParserFieldValue != null) {
					productOption.setRequired((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "skuContributor")) {
				if (jsonParserFieldValue != null) {
					productOption.setSkuContributor(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "typeSettings")) {
				if (jsonParserFieldValue != null) {
					productOption.setTypeSettings((String)jsonParserFieldValue);
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