/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.PageFragmentCompositionInstanceDefinition;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class PageFragmentCompositionInstanceDefinitionSerDes {

	public static PageFragmentCompositionInstanceDefinition toDTO(String json) {
		PageFragmentCompositionInstanceDefinitionJSONParser
			pageFragmentCompositionInstanceDefinitionJSONParser =
				new PageFragmentCompositionInstanceDefinitionJSONParser();

		return pageFragmentCompositionInstanceDefinitionJSONParser.parseToDTO(
			json);
	}

	public static PageFragmentCompositionInstanceDefinition[] toDTOs(
		String json) {

		PageFragmentCompositionInstanceDefinitionJSONParser
			pageFragmentCompositionInstanceDefinitionJSONParser =
				new PageFragmentCompositionInstanceDefinitionJSONParser();

		return pageFragmentCompositionInstanceDefinitionJSONParser.parseToDTOs(
			json);
	}

	public static String toJSON(
		PageFragmentCompositionInstanceDefinition
			pageFragmentCompositionInstanceDefinition) {

		if (pageFragmentCompositionInstanceDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pageFragmentCompositionInstanceDefinition.
				getFragmentComposition() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentComposition\": ");

			sb.append(
				String.valueOf(
					pageFragmentCompositionInstanceDefinition.
						getFragmentComposition()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageFragmentCompositionInstanceDefinitionJSONParser
			pageFragmentCompositionInstanceDefinitionJSONParser =
				new PageFragmentCompositionInstanceDefinitionJSONParser();

		return pageFragmentCompositionInstanceDefinitionJSONParser.parseToMap(
			json);
	}

	public static Map<String, String> toMap(
		PageFragmentCompositionInstanceDefinition
			pageFragmentCompositionInstanceDefinition) {

		if (pageFragmentCompositionInstanceDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageFragmentCompositionInstanceDefinition.
				getFragmentComposition() == null) {

			map.put("fragmentComposition", null);
		}
		else {
			map.put(
				"fragmentComposition",
				String.valueOf(
					pageFragmentCompositionInstanceDefinition.
						getFragmentComposition()));
		}

		return map;
	}

	public static class PageFragmentCompositionInstanceDefinitionJSONParser
		extends BaseJSONParser<PageFragmentCompositionInstanceDefinition> {

		@Override
		protected PageFragmentCompositionInstanceDefinition createDTO() {
			return new PageFragmentCompositionInstanceDefinition();
		}

		@Override
		protected PageFragmentCompositionInstanceDefinition[] createDTOArray(
			int size) {

			return new PageFragmentCompositionInstanceDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "fragmentComposition")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageFragmentCompositionInstanceDefinition
				pageFragmentCompositionInstanceDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "fragmentComposition")) {
				if (jsonParserFieldValue != null) {
					pageFragmentCompositionInstanceDefinition.
						setFragmentComposition(
							ItemExternalReferenceSerDes.toDTO(
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