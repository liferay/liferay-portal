/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.DropZonePageElementDefinition;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentReference;
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
public class DropZonePageElementDefinitionSerDes {

	public static DropZonePageElementDefinition toDTO(String json) {
		DropZonePageElementDefinitionJSONParser
			dropZonePageElementDefinitionJSONParser =
				new DropZonePageElementDefinitionJSONParser();

		return dropZonePageElementDefinitionJSONParser.parseToDTO(json);
	}

	public static DropZonePageElementDefinition[] toDTOs(String json) {
		DropZonePageElementDefinitionJSONParser
			dropZonePageElementDefinitionJSONParser =
				new DropZonePageElementDefinitionJSONParser();

		return dropZonePageElementDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		DropZonePageElementDefinition dropZonePageElementDefinition) {

		if (dropZonePageElementDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (dropZonePageElementDefinition.getAddNewFragmentEntries() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"addNewFragmentEntries\": ");

			sb.append(dropZonePageElementDefinition.getAddNewFragmentEntries());
		}

		if (dropZonePageElementDefinition.getAllowedFragmentReferences() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"allowedFragmentReferences\": ");

			sb.append("[");

			for (int i = 0;
				 i < dropZonePageElementDefinition.
					 getAllowedFragmentReferences().length;
				 i++) {

				sb.append(
					String.valueOf(
						dropZonePageElementDefinition.
							getAllowedFragmentReferences()[i]));

				if ((i + 1) < dropZonePageElementDefinition.
						getAllowedFragmentReferences().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (dropZonePageElementDefinition.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(dropZonePageElementDefinition.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		DropZonePageElementDefinitionJSONParser
			dropZonePageElementDefinitionJSONParser =
				new DropZonePageElementDefinitionJSONParser();

		return dropZonePageElementDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		DropZonePageElementDefinition dropZonePageElementDefinition) {

		if (dropZonePageElementDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (dropZonePageElementDefinition.getAddNewFragmentEntries() == null) {
			map.put("addNewFragmentEntries", null);
		}
		else {
			map.put(
				"addNewFragmentEntries",
				String.valueOf(
					dropZonePageElementDefinition.getAddNewFragmentEntries()));
		}

		if (dropZonePageElementDefinition.getAllowedFragmentReferences() ==
				null) {

			map.put("allowedFragmentReferences", null);
		}
		else {
			map.put(
				"allowedFragmentReferences",
				String.valueOf(
					dropZonePageElementDefinition.
						getAllowedFragmentReferences()));
		}

		if (dropZonePageElementDefinition.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(dropZonePageElementDefinition.getType()));
		}

		return map;
	}

	public static class DropZonePageElementDefinitionJSONParser
		extends BaseJSONParser<DropZonePageElementDefinition> {

		@Override
		protected DropZonePageElementDefinition createDTO() {
			return new DropZonePageElementDefinition();
		}

		@Override
		protected DropZonePageElementDefinition[] createDTOArray(int size) {
			return new DropZonePageElementDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "addNewFragmentEntries")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "allowedFragmentReferences")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			DropZonePageElementDefinition dropZonePageElementDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "addNewFragmentEntries")) {
				if (jsonParserFieldValue != null) {
					dropZonePageElementDefinition.setAddNewFragmentEntries(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "allowedFragmentReferences")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FragmentReference[] allowedFragmentReferencesArray =
						new FragmentReference[jsonParserFieldValues.length];

					for (int i = 0; i < allowedFragmentReferencesArray.length;
						 i++) {

						allowedFragmentReferencesArray[i] =
							FragmentReferenceSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					dropZonePageElementDefinition.setAllowedFragmentReferences(
						allowedFragmentReferencesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					dropZonePageElementDefinition.setType(
						DropZonePageElementDefinition.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-668767794