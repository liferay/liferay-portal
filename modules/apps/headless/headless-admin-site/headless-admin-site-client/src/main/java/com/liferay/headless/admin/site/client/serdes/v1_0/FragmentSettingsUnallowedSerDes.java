/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FragmentSettingsUnallowed;
import com.liferay.headless.admin.site.client.dto.v1_0.ItemExternalReference;
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
public class FragmentSettingsUnallowedSerDes {

	public static FragmentSettingsUnallowed toDTO(String json) {
		FragmentSettingsUnallowedJSONParser
			fragmentSettingsUnallowedJSONParser =
				new FragmentSettingsUnallowedJSONParser();

		return fragmentSettingsUnallowedJSONParser.parseToDTO(json);
	}

	public static FragmentSettingsUnallowed[] toDTOs(String json) {
		FragmentSettingsUnallowedJSONParser
			fragmentSettingsUnallowedJSONParser =
				new FragmentSettingsUnallowedJSONParser();

		return fragmentSettingsUnallowedJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		FragmentSettingsUnallowed fragmentSettingsUnallowed) {

		if (fragmentSettingsUnallowed == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (fragmentSettingsUnallowed.
				getUnallowedFragmentItemExternalReferences() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"unallowedFragmentItemExternalReferences\": ");

			sb.append("[");

			for (int i = 0;
				 i < fragmentSettingsUnallowed.
					 getUnallowedFragmentItemExternalReferences().length;
				 i++) {

				sb.append(
					String.valueOf(
						fragmentSettingsUnallowed.
							getUnallowedFragmentItemExternalReferences()[i]));

				if ((i + 1) < fragmentSettingsUnallowed.
						getUnallowedFragmentItemExternalReferences().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentSettingsUnallowedJSONParser
			fragmentSettingsUnallowedJSONParser =
				new FragmentSettingsUnallowedJSONParser();

		return fragmentSettingsUnallowedJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FragmentSettingsUnallowed fragmentSettingsUnallowed) {

		if (fragmentSettingsUnallowed == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fragmentSettingsUnallowed.
				getUnallowedFragmentItemExternalReferences() == null) {

			map.put("unallowedFragmentItemExternalReferences", null);
		}
		else {
			map.put(
				"unallowedFragmentItemExternalReferences",
				String.valueOf(
					fragmentSettingsUnallowed.
						getUnallowedFragmentItemExternalReferences()));
		}

		return map;
	}

	public static class FragmentSettingsUnallowedJSONParser
		extends BaseJSONParser<FragmentSettingsUnallowed> {

		@Override
		protected FragmentSettingsUnallowed createDTO() {
			return new FragmentSettingsUnallowed();
		}

		@Override
		protected FragmentSettingsUnallowed[] createDTOArray(int size) {
			return new FragmentSettingsUnallowed[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName,
					"unallowedFragmentItemExternalReferences")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FragmentSettingsUnallowed fragmentSettingsUnallowed,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName,
					"unallowedFragmentItemExternalReferences")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ItemExternalReference[]
						unallowedFragmentItemExternalReferencesArray =
							new ItemExternalReference
								[jsonParserFieldValues.length];

					for (int i = 0;
						 i <
							 unallowedFragmentItemExternalReferencesArray.
								 length;
						 i++) {

						unallowedFragmentItemExternalReferencesArray[i] =
							ItemExternalReferenceSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					fragmentSettingsUnallowed.
						setUnallowedFragmentItemExternalReferences(
							unallowedFragmentItemExternalReferencesArray);
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