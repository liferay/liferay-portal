/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.Attribute;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class AttributeSerDes {

	public static Attribute toDTO(String json) {
		AttributeJSONParser attributeJSONParser = new AttributeJSONParser();

		return attributeJSONParser.parseToDTO(json);
	}

	public static Attribute[] toDTOs(String json) {
		AttributeJSONParser attributeJSONParser = new AttributeJSONParser();

		return attributeJSONParser.parseToDTOs(json);
	}

	public static String toJSON(Attribute attribute) {
		if (attribute == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (attribute.getCanonicalValues() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"canonicalValues\": ");

			sb.append("[");

			for (int i = 0; i < attribute.getCanonicalValues().length; i++) {
				sb.append(_toJSON(attribute.getCanonicalValues()[i]));

				if ((i + 1) < attribute.getCanonicalValues().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (attribute.getCaseExact() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"caseExact\": ");

			sb.append(attribute.getCaseExact());
		}

		if (attribute.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(attribute.getDescription()));

			sb.append("\"");
		}

		if (attribute.getMultiValued() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"multiValued\": ");

			sb.append(attribute.getMultiValued());
		}

		if (attribute.getMutability() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"mutability\": ");

			sb.append("\"");

			sb.append(attribute.getMutability());

			sb.append("\"");
		}

		if (attribute.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(attribute.getName()));

			sb.append("\"");
		}

		if (attribute.getReferenceTypes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"referenceTypes\": ");

			sb.append("[");

			for (int i = 0; i < attribute.getReferenceTypes().length; i++) {
				sb.append(_toJSON(attribute.getReferenceTypes()[i]));

				if ((i + 1) < attribute.getReferenceTypes().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (attribute.getRequired() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(attribute.getRequired());
		}

		if (attribute.getReturned() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"returned\": ");

			sb.append("\"");

			sb.append(attribute.getReturned());

			sb.append("\"");
		}

		if (attribute.getSubAttributes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subAttributes\": ");

			sb.append("[");

			for (int i = 0; i < attribute.getSubAttributes().length; i++) {
				sb.append(String.valueOf(attribute.getSubAttributes()[i]));

				if ((i + 1) < attribute.getSubAttributes().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (attribute.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(attribute.getType());

			sb.append("\"");
		}

		if (attribute.getUniqueness() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uniqueness\": ");

			sb.append("\"");

			sb.append(attribute.getUniqueness());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AttributeJSONParser attributeJSONParser = new AttributeJSONParser();

		return attributeJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(Attribute attribute) {
		if (attribute == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (attribute.getCanonicalValues() == null) {
			map.put("canonicalValues", null);
		}
		else {
			map.put(
				"canonicalValues",
				String.valueOf(attribute.getCanonicalValues()));
		}

		if (attribute.getCaseExact() == null) {
			map.put("caseExact", null);
		}
		else {
			map.put("caseExact", String.valueOf(attribute.getCaseExact()));
		}

		if (attribute.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(attribute.getDescription()));
		}

		if (attribute.getMultiValued() == null) {
			map.put("multiValued", null);
		}
		else {
			map.put("multiValued", String.valueOf(attribute.getMultiValued()));
		}

		if (attribute.getMutability() == null) {
			map.put("mutability", null);
		}
		else {
			map.put("mutability", String.valueOf(attribute.getMutability()));
		}

		if (attribute.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(attribute.getName()));
		}

		if (attribute.getReferenceTypes() == null) {
			map.put("referenceTypes", null);
		}
		else {
			map.put(
				"referenceTypes",
				String.valueOf(attribute.getReferenceTypes()));
		}

		if (attribute.getRequired() == null) {
			map.put("required", null);
		}
		else {
			map.put("required", String.valueOf(attribute.getRequired()));
		}

		if (attribute.getReturned() == null) {
			map.put("returned", null);
		}
		else {
			map.put("returned", String.valueOf(attribute.getReturned()));
		}

		if (attribute.getSubAttributes() == null) {
			map.put("subAttributes", null);
		}
		else {
			map.put(
				"subAttributes", String.valueOf(attribute.getSubAttributes()));
		}

		if (attribute.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(attribute.getType()));
		}

		if (attribute.getUniqueness() == null) {
			map.put("uniqueness", null);
		}
		else {
			map.put("uniqueness", String.valueOf(attribute.getUniqueness()));
		}

		return map;
	}

	public static class AttributeJSONParser extends BaseJSONParser<Attribute> {

		@Override
		protected Attribute createDTO() {
			return new Attribute();
		}

		@Override
		protected Attribute[] createDTOArray(int size) {
			return new Attribute[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "canonicalValues")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "caseExact")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "multiValued")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "mutability")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "referenceTypes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "returned")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "subAttributes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "uniqueness")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			Attribute attribute, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "canonicalValues")) {
				if (jsonParserFieldValue != null) {
					attribute.setCanonicalValues(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "caseExact")) {
				if (jsonParserFieldValue != null) {
					attribute.setCaseExact((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					attribute.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "multiValued")) {
				if (jsonParserFieldValue != null) {
					attribute.setMultiValued((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "mutability")) {
				if (jsonParserFieldValue != null) {
					attribute.setMutability(
						Attribute.Mutability.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					attribute.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "referenceTypes")) {
				if (jsonParserFieldValue != null) {
					attribute.setReferenceTypes(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "required")) {
				if (jsonParserFieldValue != null) {
					attribute.setRequired((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "returned")) {
				if (jsonParserFieldValue != null) {
					attribute.setReturned(
						Attribute.Returned.create(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "subAttributes")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Attribute[] subAttributesArray =
						new Attribute[jsonParserFieldValues.length];

					for (int i = 0; i < subAttributesArray.length; i++) {
						subAttributesArray[i] = AttributeSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					attribute.setSubAttributes(subAttributesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					attribute.setType(
						Attribute.Type.create((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "uniqueness")) {
				if (jsonParserFieldValue != null) {
					attribute.setUniqueness(
						Attribute.Uniqueness.create(
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