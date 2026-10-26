/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.AssetLibrary;
import com.liferay.exportimport.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class AssetLibrarySerDes {

	public static AssetLibrary toDTO(String json) {
		AssetLibraryJSONParser assetLibraryJSONParser =
			new AssetLibraryJSONParser();

		return assetLibraryJSONParser.parseToDTO(json);
	}

	public static AssetLibrary[] toDTOs(String json) {
		AssetLibraryJSONParser assetLibraryJSONParser =
			new AssetLibraryJSONParser();

		return assetLibraryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(AssetLibrary assetLibrary) {
		if (assetLibrary == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (assetLibrary.getDescriptiveName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptiveName\": ");

			sb.append("\"");

			sb.append(_escape(assetLibrary.getDescriptiveName()));

			sb.append("\"");
		}

		if (assetLibrary.getExistsInInstance() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"existsInInstance\": ");

			sb.append(assetLibrary.getExistsInInstance());
		}

		if (assetLibrary.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(assetLibrary.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (assetLibrary.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(assetLibrary.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		AssetLibraryJSONParser assetLibraryJSONParser =
			new AssetLibraryJSONParser();

		return assetLibraryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(AssetLibrary assetLibrary) {
		if (assetLibrary == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (assetLibrary.getDescriptiveName() == null) {
			map.put("descriptiveName", null);
		}
		else {
			map.put(
				"descriptiveName",
				String.valueOf(assetLibrary.getDescriptiveName()));
		}

		if (assetLibrary.getExistsInInstance() == null) {
			map.put("existsInInstance", null);
		}
		else {
			map.put(
				"existsInInstance",
				String.valueOf(assetLibrary.getExistsInInstance()));
		}

		if (assetLibrary.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(assetLibrary.getExternalReferenceCode()));
		}

		if (assetLibrary.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(assetLibrary.getType()));
		}

		return map;
	}

	public static class AssetLibraryJSONParser
		extends BaseJSONParser<AssetLibrary> {

		@Override
		protected AssetLibrary createDTO() {
			return new AssetLibrary();
		}

		@Override
		protected AssetLibrary[] createDTOArray(int size) {
			return new AssetLibrary[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "descriptiveName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "existsInInstance")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			AssetLibrary assetLibrary, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "descriptiveName")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setDescriptiveName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "existsInInstance")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setExistsInInstance(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					assetLibrary.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					assetLibrary.setType(
						AssetLibrary.Type.create((String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:502391889