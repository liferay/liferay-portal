/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.FragmentImageConfiguration;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

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
public class FragmentImageConfigurationSerDes {

	public static FragmentImageConfiguration toDTO(String json) {
		FragmentImageConfigurationJSONParser
			fragmentImageConfigurationJSONParser =
				new FragmentImageConfigurationJSONParser();

		return fragmentImageConfigurationJSONParser.parseToDTO(json);
	}

	public static FragmentImageConfiguration[] toDTOs(String json) {
		FragmentImageConfigurationJSONParser
			fragmentImageConfigurationJSONParser =
				new FragmentImageConfigurationJSONParser();

		return fragmentImageConfigurationJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		FragmentImageConfiguration fragmentImageConfiguration) {

		if (fragmentImageConfiguration == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (fragmentImageConfiguration.getLandscapeMobile() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"landscapeMobile\": ");

			sb.append("\"");

			sb.append(_escape(fragmentImageConfiguration.getLandscapeMobile()));

			sb.append("\"");
		}

		if (fragmentImageConfiguration.getPortraitMobile() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"portraitMobile\": ");

			sb.append("\"");

			sb.append(_escape(fragmentImageConfiguration.getPortraitMobile()));

			sb.append("\"");
		}

		if (fragmentImageConfiguration.getTablet() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tablet\": ");

			sb.append("\"");

			sb.append(_escape(fragmentImageConfiguration.getTablet()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentImageConfigurationJSONParser
			fragmentImageConfigurationJSONParser =
				new FragmentImageConfigurationJSONParser();

		return fragmentImageConfigurationJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FragmentImageConfiguration fragmentImageConfiguration) {

		if (fragmentImageConfiguration == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fragmentImageConfiguration.getLandscapeMobile() == null) {
			map.put("landscapeMobile", null);
		}
		else {
			map.put(
				"landscapeMobile",
				String.valueOf(
					fragmentImageConfiguration.getLandscapeMobile()));
		}

		if (fragmentImageConfiguration.getPortraitMobile() == null) {
			map.put("portraitMobile", null);
		}
		else {
			map.put(
				"portraitMobile",
				String.valueOf(fragmentImageConfiguration.getPortraitMobile()));
		}

		if (fragmentImageConfiguration.getTablet() == null) {
			map.put("tablet", null);
		}
		else {
			map.put(
				"tablet",
				String.valueOf(fragmentImageConfiguration.getTablet()));
		}

		return map;
	}

	public static class FragmentImageConfigurationJSONParser
		extends BaseJSONParser<FragmentImageConfiguration> {

		@Override
		protected FragmentImageConfiguration createDTO() {
			return new FragmentImageConfiguration();
		}

		@Override
		protected FragmentImageConfiguration[] createDTOArray(int size) {
			return new FragmentImageConfiguration[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "landscapeMobile")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "portraitMobile")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "tablet")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FragmentImageConfiguration fragmentImageConfiguration,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "landscapeMobile")) {
				if (jsonParserFieldValue != null) {
					fragmentImageConfiguration.setLandscapeMobile(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "portraitMobile")) {
				if (jsonParserFieldValue != null) {
					fragmentImageConfiguration.setPortraitMobile(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "tablet")) {
				if (jsonParserFieldValue != null) {
					fragmentImageConfiguration.setTablet(
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