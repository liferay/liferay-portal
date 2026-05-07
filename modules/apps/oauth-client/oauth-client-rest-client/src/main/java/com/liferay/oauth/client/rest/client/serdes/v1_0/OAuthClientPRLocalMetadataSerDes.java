/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.client.serdes.v1_0;

import com.liferay.oauth.client.rest.client.dto.v1_0.OAuthClientPRLocalMetadata;
import com.liferay.oauth.client.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Manuele Castro
 * @generated
 */
@Generated("")
public class OAuthClientPRLocalMetadataSerDes {

	public static OAuthClientPRLocalMetadata toDTO(String json) {
		OAuthClientPRLocalMetadataJSONParser
			oAuthClientPRLocalMetadataJSONParser =
				new OAuthClientPRLocalMetadataJSONParser();

		return oAuthClientPRLocalMetadataJSONParser.parseToDTO(json);
	}

	public static OAuthClientPRLocalMetadata[] toDTOs(String json) {
		OAuthClientPRLocalMetadataJSONParser
			oAuthClientPRLocalMetadataJSONParser =
				new OAuthClientPRLocalMetadataJSONParser();

		return oAuthClientPRLocalMetadataJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		if (oAuthClientPRLocalMetadata == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (oAuthClientPRLocalMetadata.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(oAuthClientPRLocalMetadata.getCreator());
		}

		if (oAuthClientPRLocalMetadata.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					oAuthClientPRLocalMetadata.getDateCreated()));

			sb.append("\"");
		}

		if (oAuthClientPRLocalMetadata.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					oAuthClientPRLocalMetadata.getDateModified()));

			sb.append("\"");
		}

		if (oAuthClientPRLocalMetadata.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(oAuthClientPRLocalMetadata.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (oAuthClientPRLocalMetadata.getLocalWellKnownEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"localWellKnownEnabled\": ");

			sb.append(oAuthClientPRLocalMetadata.getLocalWellKnownEnabled());
		}

		if (oAuthClientPRLocalMetadata.getLocalWellKnownURI() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"localWellKnownURI\": ");

			sb.append("\"");

			sb.append(
				_escape(oAuthClientPRLocalMetadata.getLocalWellKnownURI()));

			sb.append("\"");
		}

		if (oAuthClientPRLocalMetadata.getMetadataJSON() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metadataJSON\": ");

			sb.append("\"");

			sb.append(_escape(oAuthClientPRLocalMetadata.getMetadataJSON()));

			sb.append("\"");
		}

		if (oAuthClientPRLocalMetadata.getResource() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"resource\": ");

			sb.append("\"");

			sb.append(_escape(oAuthClientPRLocalMetadata.getResource()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OAuthClientPRLocalMetadataJSONParser
			oAuthClientPRLocalMetadataJSONParser =
				new OAuthClientPRLocalMetadataJSONParser();

		return oAuthClientPRLocalMetadataJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		if (oAuthClientPRLocalMetadata == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (oAuthClientPRLocalMetadata.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put(
				"creator",
				String.valueOf(oAuthClientPRLocalMetadata.getCreator()));
		}

		if (oAuthClientPRLocalMetadata.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					oAuthClientPRLocalMetadata.getDateCreated()));
		}

		if (oAuthClientPRLocalMetadata.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					oAuthClientPRLocalMetadata.getDateModified()));
		}

		if (oAuthClientPRLocalMetadata.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					oAuthClientPRLocalMetadata.getExternalReferenceCode()));
		}

		if (oAuthClientPRLocalMetadata.getLocalWellKnownEnabled() == null) {
			map.put("localWellKnownEnabled", null);
		}
		else {
			map.put(
				"localWellKnownEnabled",
				String.valueOf(
					oAuthClientPRLocalMetadata.getLocalWellKnownEnabled()));
		}

		if (oAuthClientPRLocalMetadata.getLocalWellKnownURI() == null) {
			map.put("localWellKnownURI", null);
		}
		else {
			map.put(
				"localWellKnownURI",
				String.valueOf(
					oAuthClientPRLocalMetadata.getLocalWellKnownURI()));
		}

		if (oAuthClientPRLocalMetadata.getMetadataJSON() == null) {
			map.put("metadataJSON", null);
		}
		else {
			map.put(
				"metadataJSON",
				String.valueOf(oAuthClientPRLocalMetadata.getMetadataJSON()));
		}

		if (oAuthClientPRLocalMetadata.getResource() == null) {
			map.put("resource", null);
		}
		else {
			map.put(
				"resource",
				String.valueOf(oAuthClientPRLocalMetadata.getResource()));
		}

		return map;
	}

	public static class OAuthClientPRLocalMetadataJSONParser
		extends BaseJSONParser<OAuthClientPRLocalMetadata> {

		@Override
		protected OAuthClientPRLocalMetadata createDTO() {
			return new OAuthClientPRLocalMetadata();
		}

		@Override
		protected OAuthClientPRLocalMetadata[] createDTOArray(int size) {
			return new OAuthClientPRLocalMetadata[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "localWellKnownEnabled")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "localWellKnownURI")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "metadataJSON")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "resource")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					oAuthClientPRLocalMetadata.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					oAuthClientPRLocalMetadata.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					oAuthClientPRLocalMetadata.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					oAuthClientPRLocalMetadata.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "localWellKnownEnabled")) {

				if (jsonParserFieldValue != null) {
					oAuthClientPRLocalMetadata.setLocalWellKnownEnabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "localWellKnownURI")) {
				if (jsonParserFieldValue != null) {
					oAuthClientPRLocalMetadata.setLocalWellKnownURI(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "metadataJSON")) {
				if (jsonParserFieldValue != null) {
					oAuthClientPRLocalMetadata.setMetadataJSON(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "resource")) {
				if (jsonParserFieldValue != null) {
					oAuthClientPRLocalMetadata.setResource(
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
// LIFERAY-REST-BUILDER-HASH:-1352484749