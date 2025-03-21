/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.CollectionItemExternalReference;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class CollectionItemExternalReferenceSerDes {

	public static CollectionItemExternalReference toDTO(String json) {
		CollectionItemExternalReferenceJSONParser
			collectionItemExternalReferenceJSONParser =
				new CollectionItemExternalReferenceJSONParser();

		return collectionItemExternalReferenceJSONParser.parseToDTO(json);
	}

	public static CollectionItemExternalReference[] toDTOs(String json) {
		CollectionItemExternalReferenceJSONParser
			collectionItemExternalReferenceJSONParser =
				new CollectionItemExternalReferenceJSONParser();

		return collectionItemExternalReferenceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		CollectionItemExternalReference collectionItemExternalReference) {

		if (collectionItemExternalReference == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (collectionItemExternalReference.getClassName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(_escape(collectionItemExternalReference.getClassName()));

			sb.append("\"");
		}

		if (collectionItemExternalReference.getExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					collectionItemExternalReference.
						getExternalReferenceCode()));

			sb.append("\"");
		}

		if (collectionItemExternalReference.getScope() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"scope\": ");

			sb.append(
				String.valueOf(collectionItemExternalReference.getScope()));
		}

		if (collectionItemExternalReference.getCollectionType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionType\": ");

			sb.append("\"");

			sb.append(collectionItemExternalReference.getCollectionType());

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		CollectionItemExternalReferenceJSONParser
			collectionItemExternalReferenceJSONParser =
				new CollectionItemExternalReferenceJSONParser();

		return collectionItemExternalReferenceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		CollectionItemExternalReference collectionItemExternalReference) {

		if (collectionItemExternalReference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (collectionItemExternalReference.getClassName() == null) {
			map.put("className", null);
		}
		else {
			map.put(
				"className",
				String.valueOf(collectionItemExternalReference.getClassName()));
		}

		if (collectionItemExternalReference.getExternalReferenceCode() ==
				null) {

			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					collectionItemExternalReference.
						getExternalReferenceCode()));
		}

		if (collectionItemExternalReference.getScope() == null) {
			map.put("scope", null);
		}
		else {
			map.put(
				"scope",
				String.valueOf(collectionItemExternalReference.getScope()));
		}

		if (collectionItemExternalReference.getCollectionType() == null) {
			map.put("collectionType", null);
		}
		else {
			map.put(
				"collectionType",
				String.valueOf(
					collectionItemExternalReference.getCollectionType()));
		}

		return map;
	}

	public static class CollectionItemExternalReferenceJSONParser
		extends BaseJSONParser<CollectionItemExternalReference> {

		@Override
		protected CollectionItemExternalReference createDTO() {
			return new CollectionItemExternalReference();
		}

		@Override
		protected CollectionItemExternalReference[] createDTOArray(int size) {
			return new CollectionItemExternalReference[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "scope")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "collectionType")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			CollectionItemExternalReference collectionItemExternalReference,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					collectionItemExternalReference.setClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					collectionItemExternalReference.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "scope")) {
				if (jsonParserFieldValue != null) {
					collectionItemExternalReference.setScope(
						ScopeSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "collectionType")) {
				if (jsonParserFieldValue != null) {
					collectionItemExternalReference.setCollectionType(
						CollectionItemExternalReference.CollectionType.create(
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