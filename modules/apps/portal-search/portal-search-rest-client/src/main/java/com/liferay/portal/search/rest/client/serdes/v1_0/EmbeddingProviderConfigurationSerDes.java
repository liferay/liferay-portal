/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.client.serdes.v1_0;

import com.liferay.portal.search.rest.client.dto.v1_0.EmbeddingProviderConfiguration;
import com.liferay.portal.search.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class EmbeddingProviderConfigurationSerDes {

	public static EmbeddingProviderConfiguration toDTO(String json) {
		EmbeddingProviderConfigurationJSONParser
			embeddingProviderConfigurationJSONParser =
				new EmbeddingProviderConfigurationJSONParser();

		return embeddingProviderConfigurationJSONParser.parseToDTO(json);
	}

	public static EmbeddingProviderConfiguration[] toDTOs(String json) {
		EmbeddingProviderConfigurationJSONParser
			embeddingProviderConfigurationJSONParser =
				new EmbeddingProviderConfigurationJSONParser();

		return embeddingProviderConfigurationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		EmbeddingProviderConfiguration embeddingProviderConfiguration) {

		if (embeddingProviderConfiguration == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (embeddingProviderConfiguration.getAttributes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"attributes\": ");

			if (embeddingProviderConfiguration.getAttributes() instanceof
					String) {

				sb.append("\"");
				sb.append(
					(String)embeddingProviderConfiguration.getAttributes());
				sb.append("\"");
			}
			else {
				sb.append(embeddingProviderConfiguration.getAttributes());
			}
		}

		if (embeddingProviderConfiguration.getEmbeddingVectorDimensions() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"embeddingVectorDimensions\": ");

			sb.append(
				embeddingProviderConfiguration.getEmbeddingVectorDimensions());
		}

		if (embeddingProviderConfiguration.getLanguageIds() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"languageIds\": ");

			sb.append("[");

			for (int i = 0;
				 i < embeddingProviderConfiguration.getLanguageIds().length;
				 i++) {

				sb.append(
					_toJSON(
						embeddingProviderConfiguration.getLanguageIds()[i]));

				if ((i + 1) <
						embeddingProviderConfiguration.
							getLanguageIds().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (embeddingProviderConfiguration.getModelClassNames() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modelClassNames\": ");

			sb.append("[");

			for (int i = 0;
				 i < embeddingProviderConfiguration.getModelClassNames().length;
				 i++) {

				sb.append(
					_toJSON(
						embeddingProviderConfiguration.getModelClassNames()
							[i]));

				if ((i + 1) < embeddingProviderConfiguration.
						getModelClassNames().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (embeddingProviderConfiguration.getProviderName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"providerName\": ");

			sb.append("\"");

			sb.append(
				_escape(embeddingProviderConfiguration.getProviderName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		EmbeddingProviderConfigurationJSONParser
			embeddingProviderConfigurationJSONParser =
				new EmbeddingProviderConfigurationJSONParser();

		return embeddingProviderConfigurationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		EmbeddingProviderConfiguration embeddingProviderConfiguration) {

		if (embeddingProviderConfiguration == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (embeddingProviderConfiguration.getAttributes() == null) {
			map.put("attributes", null);
		}
		else {
			map.put(
				"attributes",
				String.valueOf(embeddingProviderConfiguration.getAttributes()));
		}

		if (embeddingProviderConfiguration.getEmbeddingVectorDimensions() ==
				null) {

			map.put("embeddingVectorDimensions", null);
		}
		else {
			map.put(
				"embeddingVectorDimensions",
				String.valueOf(
					embeddingProviderConfiguration.
						getEmbeddingVectorDimensions()));
		}

		if (embeddingProviderConfiguration.getLanguageIds() == null) {
			map.put("languageIds", null);
		}
		else {
			map.put(
				"languageIds",
				String.valueOf(
					embeddingProviderConfiguration.getLanguageIds()));
		}

		if (embeddingProviderConfiguration.getModelClassNames() == null) {
			map.put("modelClassNames", null);
		}
		else {
			map.put(
				"modelClassNames",
				String.valueOf(
					embeddingProviderConfiguration.getModelClassNames()));
		}

		if (embeddingProviderConfiguration.getProviderName() == null) {
			map.put("providerName", null);
		}
		else {
			map.put(
				"providerName",
				String.valueOf(
					embeddingProviderConfiguration.getProviderName()));
		}

		return map;
	}

	public static class EmbeddingProviderConfigurationJSONParser
		extends BaseJSONParser<EmbeddingProviderConfiguration> {

		@Override
		protected EmbeddingProviderConfiguration createDTO() {
			return new EmbeddingProviderConfiguration();
		}

		@Override
		protected EmbeddingProviderConfiguration[] createDTOArray(int size) {
			return new EmbeddingProviderConfiguration[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "attributes")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "embeddingVectorDimensions")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "languageIds")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "modelClassNames")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "providerName")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			EmbeddingProviderConfiguration embeddingProviderConfiguration,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "attributes")) {
				if (jsonParserFieldValue != null) {
					embeddingProviderConfiguration.setAttributes(
						(Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "embeddingVectorDimensions")) {

				if (jsonParserFieldValue != null) {
					embeddingProviderConfiguration.setEmbeddingVectorDimensions(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "languageIds")) {
				if (jsonParserFieldValue != null) {
					embeddingProviderConfiguration.setLanguageIds(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modelClassNames")) {
				if (jsonParserFieldValue != null) {
					embeddingProviderConfiguration.setModelClassNames(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "providerName")) {
				if (jsonParserFieldValue != null) {
					embeddingProviderConfiguration.setProviderName(
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