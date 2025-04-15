/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.workflow.client.serdes.v1_0;

import com.liferay.headless.admin.workflow.client.dto.v1_0.ObjectReviewed;
import com.liferay.headless.admin.workflow.client.json.BaseJSONParser;

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
public class ObjectReviewedSerDes {

	public static ObjectReviewed toDTO(String json) {
		ObjectReviewedJSONParser objectReviewedJSONParser =
			new ObjectReviewedJSONParser();

		return objectReviewedJSONParser.parseToDTO(json);
	}

	public static ObjectReviewed[] toDTOs(String json) {
		ObjectReviewedJSONParser objectReviewedJSONParser =
			new ObjectReviewedJSONParser();

		return objectReviewedJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ObjectReviewed objectReviewed) {
		if (objectReviewed == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (objectReviewed.getAssetTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetTitle\": ");

			sb.append("\"");

			sb.append(_escape(objectReviewed.getAssetTitle()));

			sb.append("\"");
		}

		if (objectReviewed.getAssetType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assetType\": ");

			sb.append("\"");

			sb.append(_escape(objectReviewed.getAssetType()));

			sb.append("\"");
		}

		if (objectReviewed.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(objectReviewed.getId());
		}

		if (objectReviewed.getResourceType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"resourceType\": ");

			sb.append("\"");

			sb.append(_escape(objectReviewed.getResourceType()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectReviewedJSONParser objectReviewedJSONParser =
			new ObjectReviewedJSONParser();

		return objectReviewedJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ObjectReviewed objectReviewed) {
		if (objectReviewed == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (objectReviewed.getAssetTitle() == null) {
			map.put("assetTitle", null);
		}
		else {
			map.put(
				"assetTitle", String.valueOf(objectReviewed.getAssetTitle()));
		}

		if (objectReviewed.getAssetType() == null) {
			map.put("assetType", null);
		}
		else {
			map.put("assetType", String.valueOf(objectReviewed.getAssetType()));
		}

		if (objectReviewed.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(objectReviewed.getId()));
		}

		if (objectReviewed.getResourceType() == null) {
			map.put("resourceType", null);
		}
		else {
			map.put(
				"resourceType",
				String.valueOf(objectReviewed.getResourceType()));
		}

		return map;
	}

	public static class ObjectReviewedJSONParser
		extends BaseJSONParser<ObjectReviewed> {

		@Override
		protected ObjectReviewed createDTO() {
			return new ObjectReviewed();
		}

		@Override
		protected ObjectReviewed[] createDTOArray(int size) {
			return new ObjectReviewed[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "assetType")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "resourceType")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectReviewed objectReviewed, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "assetTitle")) {
				if (jsonParserFieldValue != null) {
					objectReviewed.setAssetTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "assetType")) {
				if (jsonParserFieldValue != null) {
					objectReviewed.setAssetType((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					objectReviewed.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "resourceType")) {
				if (jsonParserFieldValue != null) {
					objectReviewed.setResourceType(
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