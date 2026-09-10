/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.RepeatableFieldsCollectionProviderReference;
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
public class RepeatableFieldsCollectionProviderReferenceSerDes {

	public static RepeatableFieldsCollectionProviderReference toDTO(
		String json) {

		RepeatableFieldsCollectionProviderReferenceJSONParser
			repeatableFieldsCollectionProviderReferenceJSONParser =
				new RepeatableFieldsCollectionProviderReferenceJSONParser();

		return repeatableFieldsCollectionProviderReferenceJSONParser.parseToDTO(
			json);
	}

	public static RepeatableFieldsCollectionProviderReference[] toDTOs(
		String json) {

		RepeatableFieldsCollectionProviderReferenceJSONParser
			repeatableFieldsCollectionProviderReferenceJSONParser =
				new RepeatableFieldsCollectionProviderReferenceJSONParser();

		return repeatableFieldsCollectionProviderReferenceJSONParser.
			parseToDTOs(json);
	}

	public static String toJSON(
		RepeatableFieldsCollectionProviderReference
			repeatableFieldsCollectionProviderReference) {

		if (repeatableFieldsCollectionProviderReference == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (repeatableFieldsCollectionProviderReference.getClassName() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"className\": ");

			sb.append("\"");

			sb.append(
				_escape(
					repeatableFieldsCollectionProviderReference.
						getClassName()));

			sb.append("\"");
		}

		if (repeatableFieldsCollectionProviderReference.getFieldName() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fieldName\": ");

			sb.append("\"");

			sb.append(
				_escape(
					repeatableFieldsCollectionProviderReference.
						getFieldName()));

			sb.append("\"");
		}

		if (repeatableFieldsCollectionProviderReference.
				getSubTypeExternalReference() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subTypeExternalReference\": ");

			sb.append(
				String.valueOf(
					repeatableFieldsCollectionProviderReference.
						getSubTypeExternalReference()));
		}

		if (repeatableFieldsCollectionProviderReference.getCollectionType() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"collectionType\": ");

			sb.append("\"");
			sb.append(
				repeatableFieldsCollectionProviderReference.
					getCollectionType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		RepeatableFieldsCollectionProviderReferenceJSONParser
			repeatableFieldsCollectionProviderReferenceJSONParser =
				new RepeatableFieldsCollectionProviderReferenceJSONParser();

		return repeatableFieldsCollectionProviderReferenceJSONParser.parseToMap(
			json);
	}

	public static Map<String, String> toMap(
		RepeatableFieldsCollectionProviderReference
			repeatableFieldsCollectionProviderReference) {

		if (repeatableFieldsCollectionProviderReference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (repeatableFieldsCollectionProviderReference.getClassName() ==
				null) {

			map.put("className", null);
		}
		else {
			map.put(
				"className",
				String.valueOf(
					repeatableFieldsCollectionProviderReference.
						getClassName()));
		}

		if (repeatableFieldsCollectionProviderReference.getFieldName() ==
				null) {

			map.put("fieldName", null);
		}
		else {
			map.put(
				"fieldName",
				String.valueOf(
					repeatableFieldsCollectionProviderReference.
						getFieldName()));
		}

		if (repeatableFieldsCollectionProviderReference.
				getSubTypeExternalReference() == null) {

			map.put("subTypeExternalReference", null);
		}
		else {
			map.put(
				"subTypeExternalReference",
				String.valueOf(
					repeatableFieldsCollectionProviderReference.
						getSubTypeExternalReference()));
		}

		if (repeatableFieldsCollectionProviderReference.getCollectionType() ==
				null) {

			map.put("collectionType", null);
		}
		else {
			map.put(
				"collectionType",
				String.valueOf(
					repeatableFieldsCollectionProviderReference.
						getCollectionType()));
		}

		return map;
	}

	public static class RepeatableFieldsCollectionProviderReferenceJSONParser
		extends BaseJSONParser<RepeatableFieldsCollectionProviderReference> {

		@Override
		protected RepeatableFieldsCollectionProviderReference createDTO() {
			return new RepeatableFieldsCollectionProviderReference();
		}

		@Override
		protected RepeatableFieldsCollectionProviderReference[] createDTOArray(
			int size) {

			return new RepeatableFieldsCollectionProviderReference[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "className")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fieldName")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "subTypeExternalReference")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "collectionType")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			RepeatableFieldsCollectionProviderReference
				repeatableFieldsCollectionProviderReference,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "className")) {
				if (jsonParserFieldValue != null) {
					repeatableFieldsCollectionProviderReference.setClassName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fieldName")) {
				if (jsonParserFieldValue != null) {
					repeatableFieldsCollectionProviderReference.setFieldName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "subTypeExternalReference")) {

				if (jsonParserFieldValue != null) {
					repeatableFieldsCollectionProviderReference.
						setSubTypeExternalReference(
							ItemExternalReferenceSerDes.toDTO(
								(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "collectionType")) {
				if (jsonParserFieldValue != null) {
					repeatableFieldsCollectionProviderReference.
						setCollectionType(
							RepeatableFieldsCollectionProviderReference.
								CollectionType.create(
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
// LIFERAY-REST-BUILDER-HASH:-2117031350