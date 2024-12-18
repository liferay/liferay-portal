/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v2_0;

import com.liferay.search.experiences.rest.client.dto.v2_0.SXPParameterContributorDefinition;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Generated;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class SXPParameterContributorDefinitionSerDes {

	public static SXPParameterContributorDefinition toDTO(String json) {
		SXPParameterContributorDefinitionJSONParser
			sxpParameterContributorDefinitionJSONParser =
				new SXPParameterContributorDefinitionJSONParser();

		return sxpParameterContributorDefinitionJSONParser.parseToDTO(json);
	}

	public static SXPParameterContributorDefinition[] toDTOs(String json) {
		SXPParameterContributorDefinitionJSONParser
			sxpParameterContributorDefinitionJSONParser =
				new SXPParameterContributorDefinitionJSONParser();

		return sxpParameterContributorDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		SXPParameterContributorDefinition sxpParameterContributorDefinition) {

		if (sxpParameterContributorDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (sxpParameterContributorDefinition.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(
				_escape(sxpParameterContributorDefinition.getClassName()));

			sb.append("\"");
		}

		if (sxpParameterContributorDefinition.getHelpText() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"helpText\": ");

			sb.append("\"");

			sb.append(_escape(sxpParameterContributorDefinition.getHelpText()));

			sb.append("\"");
		}

		if (sxpParameterContributorDefinition.getTemplateVariable() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"templateVariable\": ");

			sb.append("\"");

			sb.append(
				_escape(
					sxpParameterContributorDefinition.getTemplateVariable()));

			sb.append("\"");
		}

		if (sxpParameterContributorDefinition.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(sxpParameterContributorDefinition.getTitle()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SXPParameterContributorDefinitionJSONParser
			sxpParameterContributorDefinitionJSONParser =
				new SXPParameterContributorDefinitionJSONParser();

		return sxpParameterContributorDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SXPParameterContributorDefinition sxpParameterContributorDefinition) {

		if (sxpParameterContributorDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (sxpParameterContributorDefinition.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put(
				"className",
				String.valueOf(
					sxpParameterContributorDefinition.getClassName()));
		}

		if (sxpParameterContributorDefinition.getHelpText() == null) {
			map.put("helpText", null);
		}
		else {
			map.put(
				"helpText",
				String.valueOf(
					sxpParameterContributorDefinition.getHelpText()));
		}

		if (sxpParameterContributorDefinition.getTemplateVariable() == null) {
			map.put("templateVariable", null);
		}
		else {
			map.put(
				"templateVariable",
				String.valueOf(
					sxpParameterContributorDefinition.getTemplateVariable()));
		}

		if (sxpParameterContributorDefinition.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put(
				"title",
				String.valueOf(sxpParameterContributorDefinition.getTitle()));
		}

		return map;
	}

	public static class SXPParameterContributorDefinitionJSONParser
		extends BaseJSONParser<SXPParameterContributorDefinition> {

		@Override
		protected SXPParameterContributorDefinition createDTO() {
			return new SXPParameterContributorDefinition();
		}

		@Override
		protected SXPParameterContributorDefinition[] createDTOArray(int size) {
			return new SXPParameterContributorDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "helpText")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "templateVariable")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SXPParameterContributorDefinition sxpParameterContributorDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					sxpParameterContributorDefinition.setClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "helpText")) {
				if (jsonParserFieldValue != null) {
					sxpParameterContributorDefinition.setHelpText(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "templateVariable")) {
				if (jsonParserFieldValue != null) {
					sxpParameterContributorDefinition.setTemplateVariable(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					sxpParameterContributorDefinition.setTitle(
						(String)jsonParserFieldValue);
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