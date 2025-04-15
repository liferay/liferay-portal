/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.PortraitMobile;
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
public class PortraitMobileSerDes {

	public static PortraitMobile toDTO(String json) {
		PortraitMobileJSONParser portraitMobileJSONParser =
			new PortraitMobileJSONParser();

		return portraitMobileJSONParser.parseToDTO(json);
	}

	public static PortraitMobile[] toDTOs(String json) {
		PortraitMobileJSONParser portraitMobileJSONParser =
			new PortraitMobileJSONParser();

		return portraitMobileJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PortraitMobile portraitMobile) {
		if (portraitMobile == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (portraitMobile.getModulesPerRow() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modulesPerRow\": ");

			sb.append(portraitMobile.getModulesPerRow());
		}

		if (portraitMobile.getReverseOrder() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"reverseOrder\": ");

			sb.append(portraitMobile.getReverseOrder());
		}

		if (portraitMobile.getVerticalAlignment() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"verticalAlignment\": ");

			sb.append("\"");

			sb.append(_escape(portraitMobile.getVerticalAlignment()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PortraitMobileJSONParser portraitMobileJSONParser =
			new PortraitMobileJSONParser();

		return portraitMobileJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PortraitMobile portraitMobile) {
		if (portraitMobile == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (portraitMobile.getModulesPerRow() == null) {
			map.put("modulesPerRow", null);
		}
		else {
			map.put(
				"modulesPerRow",
				String.valueOf(portraitMobile.getModulesPerRow()));
		}

		if (portraitMobile.getReverseOrder() == null) {
			map.put("reverseOrder", null);
		}
		else {
			map.put(
				"reverseOrder",
				String.valueOf(portraitMobile.getReverseOrder()));
		}

		if (portraitMobile.getVerticalAlignment() == null) {
			map.put("verticalAlignment", null);
		}
		else {
			map.put(
				"verticalAlignment",
				String.valueOf(portraitMobile.getVerticalAlignment()));
		}

		return map;
	}

	public static class PortraitMobileJSONParser
		extends BaseJSONParser<PortraitMobile> {

		@Override
		protected PortraitMobile createDTO() {
			return new PortraitMobile();
		}

		@Override
		protected PortraitMobile[] createDTOArray(int size) {
			return new PortraitMobile[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "modulesPerRow")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "reverseOrder")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "verticalAlignment")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PortraitMobile portraitMobile, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "modulesPerRow")) {
				if (jsonParserFieldValue != null) {
					portraitMobile.setModulesPerRow(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "reverseOrder")) {
				if (jsonParserFieldValue != null) {
					portraitMobile.setReverseOrder(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "verticalAlignment")) {
				if (jsonParserFieldValue != null) {
					portraitMobile.setVerticalAlignment(
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