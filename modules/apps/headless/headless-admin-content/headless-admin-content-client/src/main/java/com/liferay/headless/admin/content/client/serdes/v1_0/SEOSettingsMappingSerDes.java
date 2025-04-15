/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.client.serdes.v1_0;

import com.liferay.headless.admin.content.client.dto.v1_0.SEOSettingsMapping;
import com.liferay.headless.admin.content.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class SEOSettingsMappingSerDes {

	public static SEOSettingsMapping toDTO(String json) {
		SEOSettingsMappingJSONParser seoSettingsMappingJSONParser =
			new SEOSettingsMappingJSONParser();

		return seoSettingsMappingJSONParser.parseToDTO(json);
	}

	public static SEOSettingsMapping[] toDTOs(String json) {
		SEOSettingsMappingJSONParser seoSettingsMappingJSONParser =
			new SEOSettingsMappingJSONParser();

		return seoSettingsMappingJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SEOSettingsMapping seoSettingsMapping) {
		if (seoSettingsMapping == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (seoSettingsMapping.getDescriptionMappingFieldKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptionMappingFieldKey\": ");

			sb.append("\"");

			sb.append(
				_escape(seoSettingsMapping.getDescriptionMappingFieldKey()));

			sb.append("\"");
		}

		if (seoSettingsMapping.getHtmlTitleMappingFieldKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"htmlTitleMappingFieldKey\": ");

			sb.append("\"");

			sb.append(
				_escape(seoSettingsMapping.getHtmlTitleMappingFieldKey()));

			sb.append("\"");
		}

		if (seoSettingsMapping.getRobots() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"robots\": ");

			sb.append("\"");

			sb.append(_escape(seoSettingsMapping.getRobots()));

			sb.append("\"");
		}

		if (seoSettingsMapping.getRobots_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"robots_i18n\": ");

			sb.append(_toJSON(seoSettingsMapping.getRobots_i18n()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SEOSettingsMappingJSONParser seoSettingsMappingJSONParser =
			new SEOSettingsMappingJSONParser();

		return seoSettingsMappingJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		SEOSettingsMapping seoSettingsMapping) {

		if (seoSettingsMapping == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (seoSettingsMapping.getDescriptionMappingFieldKey() == null) {
			map.put("descriptionMappingFieldKey", null);
		}
		else {
			map.put(
				"descriptionMappingFieldKey",
				String.valueOf(
					seoSettingsMapping.getDescriptionMappingFieldKey()));
		}

		if (seoSettingsMapping.getHtmlTitleMappingFieldKey() == null) {
			map.put("htmlTitleMappingFieldKey", null);
		}
		else {
			map.put(
				"htmlTitleMappingFieldKey",
				String.valueOf(
					seoSettingsMapping.getHtmlTitleMappingFieldKey()));
		}

		if (seoSettingsMapping.getRobots() == null) {
			map.put("robots", null);
		}
		else {
			map.put("robots", String.valueOf(seoSettingsMapping.getRobots()));
		}

		if (seoSettingsMapping.getRobots_i18n() == null) {
			map.put("robots_i18n", null);
		}
		else {
			map.put(
				"robots_i18n",
				String.valueOf(seoSettingsMapping.getRobots_i18n()));
		}

		return map;
	}

	public static class SEOSettingsMappingJSONParser
		extends BaseJSONParser<SEOSettingsMapping> {

		@Override
		protected SEOSettingsMapping createDTO() {
			return new SEOSettingsMapping();
		}

		@Override
		protected SEOSettingsMapping[] createDTOArray(int size) {
			return new SEOSettingsMapping[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "descriptionMappingFieldKey")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "htmlTitleMappingFieldKey")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "robots")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "robots_i18n")) {
				return true;
			}

			return false;
		}

		@Override
		protected void setField(
			SEOSettingsMapping seoSettingsMapping, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "descriptionMappingFieldKey")) {

				if (jsonParserFieldValue != null) {
					seoSettingsMapping.setDescriptionMappingFieldKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "htmlTitleMappingFieldKey")) {

				if (jsonParserFieldValue != null) {
					seoSettingsMapping.setHtmlTitleMappingFieldKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "robots")) {
				if (jsonParserFieldValue != null) {
					seoSettingsMapping.setRobots((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "robots_i18n")) {
				if (jsonParserFieldValue != null) {
					seoSettingsMapping.setRobots_i18n(
						(Map<String, String>)jsonParserFieldValue);
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