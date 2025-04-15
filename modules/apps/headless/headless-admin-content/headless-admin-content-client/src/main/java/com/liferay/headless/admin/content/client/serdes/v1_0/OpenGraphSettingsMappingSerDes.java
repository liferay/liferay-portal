/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.client.serdes.v1_0;

import com.liferay.headless.admin.content.client.dto.v1_0.OpenGraphSettingsMapping;
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
public class OpenGraphSettingsMappingSerDes {

	public static OpenGraphSettingsMapping toDTO(String json) {
		OpenGraphSettingsMappingJSONParser openGraphSettingsMappingJSONParser =
			new OpenGraphSettingsMappingJSONParser();

		return openGraphSettingsMappingJSONParser.parseToDTO(json);
	}

	public static OpenGraphSettingsMapping[] toDTOs(String json) {
		OpenGraphSettingsMappingJSONParser openGraphSettingsMappingJSONParser =
			new OpenGraphSettingsMappingJSONParser();

		return openGraphSettingsMappingJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		OpenGraphSettingsMapping openGraphSettingsMapping) {

		if (openGraphSettingsMapping == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (openGraphSettingsMapping.getDescriptionMappingFieldKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptionMappingFieldKey\": ");

			sb.append("\"");

			sb.append(
				_escape(
					openGraphSettingsMapping.getDescriptionMappingFieldKey()));

			sb.append("\"");
		}

		if (openGraphSettingsMapping.getImageAltMappingFieldKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageAltMappingFieldKey\": ");

			sb.append("\"");

			sb.append(
				_escape(openGraphSettingsMapping.getImageAltMappingFieldKey()));

			sb.append("\"");
		}

		if (openGraphSettingsMapping.getImageMappingFieldKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"imageMappingFieldKey\": ");

			sb.append("\"");

			sb.append(
				_escape(openGraphSettingsMapping.getImageMappingFieldKey()));

			sb.append("\"");
		}

		if (openGraphSettingsMapping.getTitleMappingFieldKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"titleMappingFieldKey\": ");

			sb.append("\"");

			sb.append(
				_escape(openGraphSettingsMapping.getTitleMappingFieldKey()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OpenGraphSettingsMappingJSONParser openGraphSettingsMappingJSONParser =
			new OpenGraphSettingsMappingJSONParser();

		return openGraphSettingsMappingJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		OpenGraphSettingsMapping openGraphSettingsMapping) {

		if (openGraphSettingsMapping == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (openGraphSettingsMapping.getDescriptionMappingFieldKey() == null) {
			map.put("descriptionMappingFieldKey", null);
		}
		else {
			map.put(
				"descriptionMappingFieldKey",
				String.valueOf(
					openGraphSettingsMapping.getDescriptionMappingFieldKey()));
		}

		if (openGraphSettingsMapping.getImageAltMappingFieldKey() == null) {
			map.put("imageAltMappingFieldKey", null);
		}
		else {
			map.put(
				"imageAltMappingFieldKey",
				String.valueOf(
					openGraphSettingsMapping.getImageAltMappingFieldKey()));
		}

		if (openGraphSettingsMapping.getImageMappingFieldKey() == null) {
			map.put("imageMappingFieldKey", null);
		}
		else {
			map.put(
				"imageMappingFieldKey",
				String.valueOf(
					openGraphSettingsMapping.getImageMappingFieldKey()));
		}

		if (openGraphSettingsMapping.getTitleMappingFieldKey() == null) {
			map.put("titleMappingFieldKey", null);
		}
		else {
			map.put(
				"titleMappingFieldKey",
				String.valueOf(
					openGraphSettingsMapping.getTitleMappingFieldKey()));
		}

		return map;
	}

	public static class OpenGraphSettingsMappingJSONParser
		extends BaseJSONParser<OpenGraphSettingsMapping> {

		@Override
		protected OpenGraphSettingsMapping createDTO() {
			return new OpenGraphSettingsMapping();
		}

		@Override
		protected OpenGraphSettingsMapping[] createDTOArray(int size) {
			return new OpenGraphSettingsMapping[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "descriptionMappingFieldKey")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "imageAltMappingFieldKey")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "imageMappingFieldKey")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "titleMappingFieldKey")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			OpenGraphSettingsMapping openGraphSettingsMapping,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "descriptionMappingFieldKey")) {

				if (jsonParserFieldValue != null) {
					openGraphSettingsMapping.setDescriptionMappingFieldKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "imageAltMappingFieldKey")) {

				if (jsonParserFieldValue != null) {
					openGraphSettingsMapping.setImageAltMappingFieldKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "imageMappingFieldKey")) {

				if (jsonParserFieldValue != null) {
					openGraphSettingsMapping.setImageMappingFieldKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "titleMappingFieldKey")) {

				if (jsonParserFieldValue != null) {
					openGraphSettingsMapping.setTitleMappingFieldKey(
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