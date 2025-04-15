/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0;

import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Specification;
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
public class SpecificationSerDes {

	public static Specification toDTO(String json) {
		SpecificationJSONParser specificationJSONParser =
			new SpecificationJSONParser();

		return specificationJSONParser.parseToDTO(json);
	}

	public static Specification[] toDTOs(String json) {
		SpecificationJSONParser specificationJSONParser =
			new SpecificationJSONParser();

		return specificationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Specification specification) {
		if (specification == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (specification.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append(_toJSON(specification.getDescription()));
		}

		if (specification.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(specification.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (specification.getFacetable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"facetable\": ");

			sb.append(specification.getFacetable());
		}

		if (specification.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(specification.getId());
		}

		if (specification.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(specification.getKey()));

			sb.append("\"");
		}

		if (specification.getListTypeDefinitionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listTypeDefinitionId\": ");

			sb.append(specification.getListTypeDefinitionId());
		}

		if (specification.getListTypeDefinitionIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"listTypeDefinitionIds\": ");

			sb.append("[");

			for (int i = 0; i < specification.getListTypeDefinitionIds().length;
				 i++) {

				sb.append(specification.getListTypeDefinitionIds()[i]);

				if ((i + 1) < specification.getListTypeDefinitionIds().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (specification.getOptionCategory() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"optionCategory\": ");

			sb.append(String.valueOf(specification.getOptionCategory()));
		}

		if (specification.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(specification.getPriority());
		}

		if (specification.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append(_toJSON(specification.getTitle()));
		}

		if (specification.getVisible() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"visible\": ");

			sb.append(specification.getVisible());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SpecificationJSONParser specificationJSONParser =
			new SpecificationJSONParser();

		return specificationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Specification specification) {
		if (specification == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (specification.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(specification.getDescription()));
		}

		if (specification.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(specification.getExternalReferenceCode()));
		}

		if (specification.getFacetable() == null) {
			map.put("facetable", null);
		}
		else {
			map.put("facetable", String.valueOf(specification.getFacetable()));
		}

		if (specification.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(specification.getId()));
		}

		if (specification.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(specification.getKey()));
		}

		if (specification.getListTypeDefinitionId() == null) {
			map.put("listTypeDefinitionId", null);
		}
		else {
			map.put(
				"listTypeDefinitionId",
				String.valueOf(specification.getListTypeDefinitionId()));
		}

		if (specification.getListTypeDefinitionIds() == null) {
			map.put("listTypeDefinitionIds", null);
		}
		else {
			map.put(
				"listTypeDefinitionIds",
				String.valueOf(specification.getListTypeDefinitionIds()));
		}

		if (specification.getOptionCategory() == null) {
			map.put("optionCategory", null);
		}
		else {
			map.put(
				"optionCategory",
				String.valueOf(specification.getOptionCategory()));
		}

		if (specification.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(specification.getPriority()));
		}

		if (specification.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(specification.getTitle()));
		}

		if (specification.getVisible() == null) {
			map.put("visible", null);
		}
		else {
			map.put("visible", String.valueOf(specification.getVisible()));
		}

		return map;
	}

	public static class SpecificationJSONParser
		extends BaseJSONParser<Specification> {

		@Override
		protected Specification createDTO() {
			return new Specification();
		}

		@Override
		protected Specification[] createDTOArray(int size) {
			return new Specification[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "description")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "facetable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "listTypeDefinitionId")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "listTypeDefinitionIds")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "optionCategory")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "visible")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Specification specification, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					specification.setDescription(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					specification.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "facetable")) {
				if (jsonParserFieldValue != null) {
					specification.setFacetable((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					specification.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					specification.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "listTypeDefinitionId")) {

				if (jsonParserFieldValue != null) {
					specification.setListTypeDefinitionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "listTypeDefinitionIds")) {

				if (jsonParserFieldValue != null) {
					specification.setListTypeDefinitionIds(
						toLongs((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "optionCategory")) {
				if (jsonParserFieldValue != null) {
					specification.setOptionCategory(
						OptionCategorySerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					specification.setPriority(
						Double.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					specification.setTitle(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "visible")) {
				if (jsonParserFieldValue != null) {
					specification.setVisible((Boolean)jsonParserFieldValue);
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