/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.CollectionDisplayListStyle;
import com.liferay.headless.admin.site.client.dto.v1_0.ListStyle;
import com.liferay.headless.admin.site.client.dto.v1_0.TemplateListStyle;
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
public class CollectionDisplayListStyleSerDes {

	public static CollectionDisplayListStyle toDTO(String json) {
		CollectionDisplayListStyleJSONParser
			collectionDisplayListStyleJSONParser =
				new CollectionDisplayListStyleJSONParser();

		return collectionDisplayListStyleJSONParser.parseToDTO(json);
	}

	public static CollectionDisplayListStyle[] toDTOs(String json) {
		CollectionDisplayListStyleJSONParser
			collectionDisplayListStyleJSONParser =
				new CollectionDisplayListStyleJSONParser();

		return collectionDisplayListStyleJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		CollectionDisplayListStyle collectionDisplayListStyle) {

		if (collectionDisplayListStyle == null) {
			return "null";
		}

		CollectionDisplayListStyle.CollectionDisplayListStyleType
			collectionDisplayListStyleType =
				collectionDisplayListStyle.getCollectionDisplayListStyleType();

		if (collectionDisplayListStyleType != null) {
			String collectionDisplayListStyleTypeString =
				collectionDisplayListStyleType.toString();

			if (collectionDisplayListStyleTypeString.equals("ListStyle")) {
				return ListStyleSerDes.toJSON(
					(ListStyle)collectionDisplayListStyle);
			}

			if (collectionDisplayListStyleTypeString.equals("Template")) {
				return TemplateListStyleSerDes.toJSON(
					(TemplateListStyle)collectionDisplayListStyle);
			}

			throw new IllegalArgumentException(
				"Unknown collectionDisplayListStyleType " +
					collectionDisplayListStyleTypeString);
		}
		else {
			throw new IllegalArgumentException(
				"Missing collectionDisplayListStyleType parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		CollectionDisplayListStyleJSONParser
			collectionDisplayListStyleJSONParser =
				new CollectionDisplayListStyleJSONParser();

		return collectionDisplayListStyleJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		CollectionDisplayListStyle collectionDisplayListStyle) {

		if (collectionDisplayListStyle == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (collectionDisplayListStyle.getCollectionDisplayListStyleType() ==
				null) {

			map.put("collectionDisplayListStyleType", null);
		}
		else {
			map.put(
				"collectionDisplayListStyleType",
				String.valueOf(
					collectionDisplayListStyle.
						getCollectionDisplayListStyleType()));
		}

		return map;
	}

	public static class CollectionDisplayListStyleJSONParser
		extends BaseJSONParser<CollectionDisplayListStyle> {

		@Override
		protected CollectionDisplayListStyle createDTO() {
			return null;
		}

		@Override
		protected CollectionDisplayListStyle[] createDTOArray(int size) {
			return new CollectionDisplayListStyle[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "collectionDisplayListStyleType")) {

				return false;
			}

			return false;
		}

		@Override
		public CollectionDisplayListStyle parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object collectionDisplayListStyleType = jsonMap.get(
				"collectionDisplayListStyleType");

			if (collectionDisplayListStyleType != null) {
				String collectionDisplayListStyleTypeString =
					collectionDisplayListStyleType.toString();

				if (collectionDisplayListStyleTypeString.equals("ListStyle")) {
					return ListStyle.toDTO(json);
				}

				if (collectionDisplayListStyleTypeString.equals("Template")) {
					return TemplateListStyle.toDTO(json);
				}

				throw new IllegalArgumentException(
					"Unknown collectionDisplayListStyleType " +
						collectionDisplayListStyleTypeString);
			}
			else {
				throw new IllegalArgumentException(
					"Missing collectionDisplayListStyleType parameter");
			}
		}

		@Override
		protected void setField(
			CollectionDisplayListStyle collectionDisplayListStyle,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "collectionDisplayListStyleType")) {

				if (jsonParserFieldValue != null) {
					collectionDisplayListStyle.
						setCollectionDisplayListStyleType(
							CollectionDisplayListStyle.
								CollectionDisplayListStyleType.create(
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
// LIFERAY-REST-BUILDER-HASH:-1563831996