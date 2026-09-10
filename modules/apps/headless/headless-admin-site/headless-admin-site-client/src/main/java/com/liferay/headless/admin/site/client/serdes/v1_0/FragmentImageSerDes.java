/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FragmentImage;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentImageViewport;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

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
public class FragmentImageSerDes {

	public static FragmentImage toDTO(String json) {
		FragmentImageJSONParser fragmentImageJSONParser =
			new FragmentImageJSONParser();

		return fragmentImageJSONParser.parseToDTO(json);
	}

	public static FragmentImage[] toDTOs(String json) {
		FragmentImageJSONParser fragmentImageJSONParser =
			new FragmentImageJSONParser();

		return fragmentImageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FragmentImage fragmentImage) {
		if (fragmentImage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (fragmentImage.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(fragmentImage.getDescription_i18n()));
		}

		if (fragmentImage.getFragmentImageValue() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentImageValue\": ");

			sb.append(String.valueOf(fragmentImage.getFragmentImageValue()));
		}

		if (fragmentImage.getFragmentImageViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentImageViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < fragmentImage.getFragmentImageViewports().length; i++) {

				sb.append(
					String.valueOf(
						fragmentImage.getFragmentImageViewports()[i]));

				if ((i + 1) <
						fragmentImage.getFragmentImageViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (fragmentImage.getLazyLoading() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"lazyLoading\": ");

			sb.append(fragmentImage.getLazyLoading());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentImageJSONParser fragmentImageJSONParser =
			new FragmentImageJSONParser();

		return fragmentImageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(FragmentImage fragmentImage) {
		if (fragmentImage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fragmentImage.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(fragmentImage.getDescription_i18n()));
		}

		if (fragmentImage.getFragmentImageValue() == null) {
			map.put("fragmentImageValue", null);
		}
		else {
			map.put(
				"fragmentImageValue",
				String.valueOf(fragmentImage.getFragmentImageValue()));
		}

		if (fragmentImage.getFragmentImageViewports() == null) {
			map.put("fragmentImageViewports", null);
		}
		else {
			map.put(
				"fragmentImageViewports",
				String.valueOf(fragmentImage.getFragmentImageViewports()));
		}

		if (fragmentImage.getLazyLoading() == null) {
			map.put("lazyLoading", null);
		}
		else {
			map.put(
				"lazyLoading", String.valueOf(fragmentImage.getLazyLoading()));
		}

		return map;
	}

	public static class FragmentImageJSONParser
		extends BaseJSONParser<FragmentImage> {

		@Override
		protected FragmentImage createDTO() {
			return new FragmentImage();
		}

		@Override
		protected FragmentImage[] createDTOArray(int size) {
			return new FragmentImage[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "fragmentImageValue")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "fragmentImageViewports")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "lazyLoading")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FragmentImage fragmentImage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					fragmentImage.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "fragmentImageValue")) {

				if (jsonParserFieldValue != null) {
					fragmentImage.setFragmentImageValue(
						FragmentImageValueSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "fragmentImageViewports")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FragmentImageViewport[] fragmentImageViewportsArray =
						new FragmentImageViewport[jsonParserFieldValues.length];

					for (int i = 0; i < fragmentImageViewportsArray.length;
						 i++) {

						fragmentImageViewportsArray[i] =
							FragmentImageViewportSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					fragmentImage.setFragmentImageViewports(
						fragmentImageViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "lazyLoading")) {
				if (jsonParserFieldValue != null) {
					fragmentImage.setLazyLoading((Boolean)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:2032464196