/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.serdes.v1_0;

import com.liferay.headless.admin.fragment.client.dto.v1_0.Dependency;
import com.liferay.headless.admin.fragment.client.dto.v1_0.TargetCollectionDisplayTypeOptions;
import com.liferay.headless.admin.fragment.client.json.BaseJSONParser;

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
public class TargetCollectionDisplayTypeOptionsSerDes {

	public static TargetCollectionDisplayTypeOptions toDTO(String json) {
		TargetCollectionDisplayTypeOptionsJSONParser
			targetCollectionDisplayTypeOptionsJSONParser =
				new TargetCollectionDisplayTypeOptionsJSONParser();

		return targetCollectionDisplayTypeOptionsJSONParser.parseToDTO(json);
	}

	public static TargetCollectionDisplayTypeOptions[] toDTOs(String json) {
		TargetCollectionDisplayTypeOptionsJSONParser
			targetCollectionDisplayTypeOptionsJSONParser =
				new TargetCollectionDisplayTypeOptionsJSONParser();

		return targetCollectionDisplayTypeOptionsJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		TargetCollectionDisplayTypeOptions targetCollectionDisplayTypeOptions) {

		if (targetCollectionDisplayTypeOptions == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (targetCollectionDisplayTypeOptions.getDependency() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dependency\": ");

			sb.append(
				_toJSON(targetCollectionDisplayTypeOptions.getDependency()));
		}

		if (targetCollectionDisplayTypeOptions.
				getEnableCompatibleCollections() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enableCompatibleCollections\": ");

			sb.append(
				targetCollectionDisplayTypeOptions.
					getEnableCompatibleCollections());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TargetCollectionDisplayTypeOptionsJSONParser
			targetCollectionDisplayTypeOptionsJSONParser =
				new TargetCollectionDisplayTypeOptionsJSONParser();

		return targetCollectionDisplayTypeOptionsJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		TargetCollectionDisplayTypeOptions targetCollectionDisplayTypeOptions) {

		if (targetCollectionDisplayTypeOptions == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (targetCollectionDisplayTypeOptions.getDependency() == null) {
			map.put("dependency", null);
		}
		else {
			map.put(
				"dependency",
				String.valueOf(
					targetCollectionDisplayTypeOptions.getDependency()));
		}

		if (targetCollectionDisplayTypeOptions.
				getEnableCompatibleCollections() == null) {

			map.put("enableCompatibleCollections", null);
		}
		else {
			map.put(
				"enableCompatibleCollections",
				String.valueOf(
					targetCollectionDisplayTypeOptions.
						getEnableCompatibleCollections()));
		}

		return map;
	}

	public static class TargetCollectionDisplayTypeOptionsJSONParser
		extends BaseJSONParser<TargetCollectionDisplayTypeOptions> {

		@Override
		protected TargetCollectionDisplayTypeOptions createDTO() {
			return new TargetCollectionDisplayTypeOptions();
		}

		@Override
		protected TargetCollectionDisplayTypeOptions[] createDTOArray(
			int size) {

			return new TargetCollectionDisplayTypeOptions[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "dependency")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "enableCompatibleCollections")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TargetCollectionDisplayTypeOptions
				targetCollectionDisplayTypeOptions,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "dependency")) {
				if (jsonParserFieldValue != null) {
					targetCollectionDisplayTypeOptions.setDependency(
						(Map<String, Dependency>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "enableCompatibleCollections")) {

				if (jsonParserFieldValue != null) {
					targetCollectionDisplayTypeOptions.
						setEnableCompatibleCollections(
							(Boolean)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:1412622003