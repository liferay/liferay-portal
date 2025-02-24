/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ClassNameReference;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionItemExternalReference;
import com.liferay.headless.admin.site.client.dto.v1_0.CollectionReference;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class CollectionReferenceSerDes {

	public static CollectionReference toDTO(String json) {
		CollectionReferenceJSONParser collectionReferenceJSONParser =
			new CollectionReferenceJSONParser();

		return collectionReferenceJSONParser.parseToDTO(json);
	}

	public static CollectionReference[] toDTOs(String json) {
		CollectionReferenceJSONParser collectionReferenceJSONParser =
			new CollectionReferenceJSONParser();

		return collectionReferenceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(CollectionReference collectionReference) {
		if (collectionReference == null) {
			return "null";
		}

		CollectionReference.CollectionType collectionType =
			collectionReference.getCollectionType();

		if (collectionType != null) {
			String collectionTypeString = collectionType.toString();

			if (collectionTypeString.equals("Collection")) {
				return CollectionItemExternalReferenceSerDes.toJSON(
					(CollectionItemExternalReference)collectionReference);
			}

			if (collectionTypeString.equals("CollectionProvider")) {
				return ClassNameReferenceSerDes.toJSON(
					(ClassNameReference)collectionReference);
			}

			throw new IllegalArgumentException(
				"Unknown collectionType " + collectionTypeString);
		}
		else {
			throw new IllegalArgumentException(
				"Missing collectionType parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		CollectionReferenceJSONParser collectionReferenceJSONParser =
			new CollectionReferenceJSONParser();

		return collectionReferenceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		CollectionReference collectionReference) {

		if (collectionReference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (collectionReference.getCollectionType() == null) {
			map.put("collectionType", null);
		}
		else {
			map.put(
				"collectionType",
				String.valueOf(collectionReference.getCollectionType()));
		}

		return map;
	}

	public static class CollectionReferenceJSONParser
		extends BaseJSONParser<CollectionReference> {

		@Override
		protected CollectionReference createDTO() {
			return null;
		}

		@Override
		protected CollectionReference[] createDTOArray(int size) {
			return new CollectionReference[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "collectionType")) {
				return false;
			}

			return false;
		}

		@Override
		public CollectionReference parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object collectionType = jsonMap.get("collectionType");

			if (collectionType != null) {
				String collectionTypeString = collectionType.toString();

				if (collectionTypeString.equals("Collection")) {
					return CollectionItemExternalReference.toDTO(json);
				}

				if (collectionTypeString.equals("CollectionProvider")) {
					return ClassNameReference.toDTO(json);
				}

				throw new IllegalArgumentException(
					"Unknown collectionType " + collectionTypeString);
			}
			else {
				throw new IllegalArgumentException(
					"Missing collectionType parameter");
			}
		}

		@Override
		protected void setField(
			CollectionReference collectionReference, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "collectionType")) {
				if (jsonParserFieldValue != null) {
					collectionReference.setCollectionType(
						CollectionReference.CollectionType.create(
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