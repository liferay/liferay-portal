/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.client.serdes.v1_0;

import com.liferay.headless.cms.client.dto.v1_0.MissingRequiredFieldsAsset;
import com.liferay.headless.cms.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Crescenzo Rega
 * @generated
 */
@Generated("")
public class MissingRequiredFieldsAssetSerDes {

	public static MissingRequiredFieldsAsset toDTO(String json) {
		MissingRequiredFieldsAssetJSONParser
			missingRequiredFieldsAssetJSONParser =
				new MissingRequiredFieldsAssetJSONParser();

		return missingRequiredFieldsAssetJSONParser.parseToDTO(json);
	}

	public static MissingRequiredFieldsAsset[] toDTOs(String json) {
		MissingRequiredFieldsAssetJSONParser
			missingRequiredFieldsAssetJSONParser =
				new MissingRequiredFieldsAssetJSONParser();

		return missingRequiredFieldsAssetJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		MissingRequiredFieldsAsset missingRequiredFieldsAsset) {

		if (missingRequiredFieldsAsset == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (missingRequiredFieldsAsset.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(missingRequiredFieldsAsset.getActions()));
		}

		if (missingRequiredFieldsAsset.getHref() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"href\": ");

			sb.append("\"");

			sb.append(_escape(missingRequiredFieldsAsset.getHref()));

			sb.append("\"");
		}

		if (missingRequiredFieldsAsset.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(missingRequiredFieldsAsset.getId());
		}

		if (missingRequiredFieldsAsset.getMissingRequiredFieldsCount() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"missingRequiredFieldsCount\": ");

			sb.append(
				missingRequiredFieldsAsset.getMissingRequiredFieldsCount());
		}

		if (missingRequiredFieldsAsset.
				getObjectDefinitionExternalReferenceCode() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					missingRequiredFieldsAsset.
						getObjectDefinitionExternalReferenceCode()));

			sb.append("\"");
		}

		if (missingRequiredFieldsAsset.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(missingRequiredFieldsAsset.getTitle()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		MissingRequiredFieldsAssetJSONParser
			missingRequiredFieldsAssetJSONParser =
				new MissingRequiredFieldsAssetJSONParser();

		return missingRequiredFieldsAssetJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		MissingRequiredFieldsAsset missingRequiredFieldsAsset) {

		if (missingRequiredFieldsAsset == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (missingRequiredFieldsAsset.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put(
				"actions",
				String.valueOf(missingRequiredFieldsAsset.getActions()));
		}

		if (missingRequiredFieldsAsset.getHref() == null) {
			map.put("href", null);
		}
		else {
			map.put(
				"href", String.valueOf(missingRequiredFieldsAsset.getHref()));
		}

		if (missingRequiredFieldsAsset.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(missingRequiredFieldsAsset.getId()));
		}

		if (missingRequiredFieldsAsset.getMissingRequiredFieldsCount() ==
				null) {

			map.put("missingRequiredFieldsCount", null);
		}
		else {
			map.put(
				"missingRequiredFieldsCount",
				String.valueOf(
					missingRequiredFieldsAsset.
						getMissingRequiredFieldsCount()));
		}

		if (missingRequiredFieldsAsset.
				getObjectDefinitionExternalReferenceCode() == null) {

			map.put("objectDefinitionExternalReferenceCode", null);
		}
		else {
			map.put(
				"objectDefinitionExternalReferenceCode",
				String.valueOf(
					missingRequiredFieldsAsset.
						getObjectDefinitionExternalReferenceCode()));
		}

		if (missingRequiredFieldsAsset.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put(
				"title", String.valueOf(missingRequiredFieldsAsset.getTitle()));
		}

		return map;
	}

	public static class MissingRequiredFieldsAssetJSONParser
		extends BaseJSONParser<MissingRequiredFieldsAsset> {

		@Override
		protected MissingRequiredFieldsAsset createDTO() {
			return new MissingRequiredFieldsAsset();
		}

		@Override
		protected MissingRequiredFieldsAsset[] createDTOArray(int size) {
			return new MissingRequiredFieldsAsset[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "href")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "missingRequiredFieldsCount")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"objectDefinitionExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			MissingRequiredFieldsAsset missingRequiredFieldsAsset,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					missingRequiredFieldsAsset.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "href")) {
				if (jsonParserFieldValue != null) {
					missingRequiredFieldsAsset.setHref(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					missingRequiredFieldsAsset.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "missingRequiredFieldsCount")) {

				if (jsonParserFieldValue != null) {
					missingRequiredFieldsAsset.setMissingRequiredFieldsCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"objectDefinitionExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					missingRequiredFieldsAsset.
						setObjectDefinitionExternalReferenceCode(
							(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					missingRequiredFieldsAsset.setTitle(
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
// LIFERAY-REST-BUILDER-HASH:817000108