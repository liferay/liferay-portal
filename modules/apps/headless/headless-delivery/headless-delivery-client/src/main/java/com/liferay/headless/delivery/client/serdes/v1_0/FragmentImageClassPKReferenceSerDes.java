/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.ClassPKReference;
import com.liferay.headless.delivery.client.dto.v1_0.FragmentImageClassPKReference;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FragmentImageClassPKReferenceSerDes {

	public static FragmentImageClassPKReference toDTO(String json) {
		FragmentImageClassPKReferenceJSONParser
			fragmentImageClassPKReferenceJSONParser =
				new FragmentImageClassPKReferenceJSONParser();

		return fragmentImageClassPKReferenceJSONParser.parseToDTO(json);
	}

	public static FragmentImageClassPKReference[] toDTOs(String json) {
		FragmentImageClassPKReferenceJSONParser
			fragmentImageClassPKReferenceJSONParser =
				new FragmentImageClassPKReferenceJSONParser();

		return fragmentImageClassPKReferenceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		FragmentImageClassPKReference fragmentImageClassPKReference) {

		if (fragmentImageClassPKReference == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (fragmentImageClassPKReference.getClassPKReferences() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"classPKReferences\": ");

			sb.append(
				_toJSON(fragmentImageClassPKReference.getClassPKReferences()));
		}

		if (fragmentImageClassPKReference.getFragmentImageConfiguration() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentImageConfiguration\": ");

			sb.append(
				String.valueOf(
					fragmentImageClassPKReference.
						getFragmentImageConfiguration()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentImageClassPKReferenceJSONParser
			fragmentImageClassPKReferenceJSONParser =
				new FragmentImageClassPKReferenceJSONParser();

		return fragmentImageClassPKReferenceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FragmentImageClassPKReference fragmentImageClassPKReference) {

		if (fragmentImageClassPKReference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fragmentImageClassPKReference.getClassPKReferences() == null) {
			map.put("classPKReferences", null);
		}
		else {
			map.put(
				"classPKReferences",
				String.valueOf(
					fragmentImageClassPKReference.getClassPKReferences()));
		}

		if (fragmentImageClassPKReference.getFragmentImageConfiguration() ==
				null) {

			map.put("fragmentImageConfiguration", null);
		}
		else {
			map.put(
				"fragmentImageConfiguration",
				String.valueOf(
					fragmentImageClassPKReference.
						getFragmentImageConfiguration()));
		}

		return map;
	}

	public static class FragmentImageClassPKReferenceJSONParser
		extends BaseJSONParser<FragmentImageClassPKReference> {

		@Override
		protected FragmentImageClassPKReference createDTO() {
			return new FragmentImageClassPKReference();
		}

		@Override
		protected FragmentImageClassPKReference[] createDTOArray(int size) {
			return new FragmentImageClassPKReference[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "classPKReferences")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "fragmentImageConfiguration")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FragmentImageClassPKReference fragmentImageClassPKReference,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "classPKReferences")) {
				if (jsonParserFieldValue != null) {
					fragmentImageClassPKReference.setClassPKReferences(
						(Map<String, ClassPKReference>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "fragmentImageConfiguration")) {

				if (jsonParserFieldValue != null) {
					fragmentImageClassPKReference.setFragmentImageConfiguration(
						FragmentImageConfigurationSerDes.toDTO(
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