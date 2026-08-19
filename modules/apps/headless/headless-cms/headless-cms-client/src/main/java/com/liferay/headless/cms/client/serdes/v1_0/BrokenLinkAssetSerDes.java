/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.client.serdes.v1_0;

import com.liferay.headless.cms.client.dto.v1_0.BrokenLinkAsset;
import com.liferay.headless.cms.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class BrokenLinkAssetSerDes {

	public static BrokenLinkAsset toDTO(String json) {
		BrokenLinkAssetJSONParser brokenLinkAssetJSONParser =
			new BrokenLinkAssetJSONParser();

		return brokenLinkAssetJSONParser.parseToDTO(json);
	}

	public static BrokenLinkAsset[] toDTOs(String json) {
		BrokenLinkAssetJSONParser brokenLinkAssetJSONParser =
			new BrokenLinkAssetJSONParser();

		return brokenLinkAssetJSONParser.parseToDTOs(json);
	}

	public static String toJSON(BrokenLinkAsset brokenLinkAsset) {
		if (brokenLinkAsset == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (brokenLinkAsset.getBrokenLinkTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"brokenLinkTitle\": ");

			sb.append("\"");

			sb.append(_escape(brokenLinkAsset.getBrokenLinkTitle()));

			sb.append("\"");
		}

		if (brokenLinkAsset.getBrokenLinksCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"brokenLinksCount\": ");

			sb.append(brokenLinkAsset.getBrokenLinksCount());
		}

		if (brokenLinkAsset.getHref() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"href\": ");

			sb.append("\"");

			sb.append(_escape(brokenLinkAsset.getHref()));

			sb.append("\"");
		}

		if (brokenLinkAsset.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(brokenLinkAsset.getId());
		}

		if (brokenLinkAsset.getObjectDefinitionExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					brokenLinkAsset.
						getObjectDefinitionExternalReferenceCode()));

			sb.append("\"");
		}

		if (brokenLinkAsset.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(brokenLinkAsset.getTitle()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		BrokenLinkAssetJSONParser brokenLinkAssetJSONParser =
			new BrokenLinkAssetJSONParser();

		return brokenLinkAssetJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(BrokenLinkAsset brokenLinkAsset) {
		if (brokenLinkAsset == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (brokenLinkAsset.getBrokenLinkTitle() == null) {
			map.put("brokenLinkTitle", null);
		}
		else {
			map.put(
				"brokenLinkTitle",
				String.valueOf(brokenLinkAsset.getBrokenLinkTitle()));
		}

		if (brokenLinkAsset.getBrokenLinksCount() == null) {
			map.put("brokenLinksCount", null);
		}
		else {
			map.put(
				"brokenLinksCount",
				String.valueOf(brokenLinkAsset.getBrokenLinksCount()));
		}

		if (brokenLinkAsset.getHref() == null) {
			map.put("href", null);
		}
		else {
			map.put("href", String.valueOf(brokenLinkAsset.getHref()));
		}

		if (brokenLinkAsset.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(brokenLinkAsset.getId()));
		}

		if (brokenLinkAsset.getObjectDefinitionExternalReferenceCode() ==
				null) {

			map.put("objectDefinitionExternalReferenceCode", null);
		}
		else {
			map.put(
				"objectDefinitionExternalReferenceCode",
				String.valueOf(
					brokenLinkAsset.
						getObjectDefinitionExternalReferenceCode()));
		}

		if (brokenLinkAsset.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(brokenLinkAsset.getTitle()));
		}

		return map;
	}

	public static class BrokenLinkAssetJSONParser
		extends BaseJSONParser<BrokenLinkAsset> {

		@Override
		protected BrokenLinkAsset createDTO() {
			return new BrokenLinkAsset();
		}

		@Override
		protected BrokenLinkAsset[] createDTOArray(int size) {
			return new BrokenLinkAsset[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "brokenLinkTitle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "brokenLinksCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "href")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
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
			BrokenLinkAsset brokenLinkAsset, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "brokenLinkTitle")) {
				if (jsonParserFieldValue != null) {
					brokenLinkAsset.setBrokenLinkTitle(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "brokenLinksCount")) {
				if (jsonParserFieldValue != null) {
					brokenLinkAsset.setBrokenLinksCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "href")) {
				if (jsonParserFieldValue != null) {
					brokenLinkAsset.setHref((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					brokenLinkAsset.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"objectDefinitionExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					brokenLinkAsset.setObjectDefinitionExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					brokenLinkAsset.setTitle((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-792535952