/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.client.serdes.v1_0;

import com.liferay.oauth.client.rest.client.dto.v1_0.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Collection;
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
public class OAuthClientASLocalMetadataSerDes {

	public static OAuthClientASLocalMetadata toDTO(String json) {
		OAuthClientASLocalMetadataJSONParser
			oAuthClientASLocalMetadataJSONParser =
				new OAuthClientASLocalMetadataJSONParser();

		return oAuthClientASLocalMetadataJSONParser.parseToDTO(json);
	}

	public static OAuthClientASLocalMetadata[] toDTOs(String json) {
		OAuthClientASLocalMetadataJSONParser
			oAuthClientASLocalMetadataJSONParser =
				new OAuthClientASLocalMetadataJSONParser();

		return oAuthClientASLocalMetadataJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		if (oAuthClientASLocalMetadata == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (oAuthClientASLocalMetadata.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(oAuthClientASLocalMetadata.getCreator());
		}

		if (oAuthClientASLocalMetadata.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					oAuthClientASLocalMetadata.getDateCreated()));

			sb.append("\"");
		}

		if (oAuthClientASLocalMetadata.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					oAuthClientASLocalMetadata.getDateModified()));

			sb.append("\"");
		}

		if (oAuthClientASLocalMetadata.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(oAuthClientASLocalMetadata.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (oAuthClientASLocalMetadata.getIssuer() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"issuer\": ");

			sb.append("\"");

			sb.append(_escape(oAuthClientASLocalMetadata.getIssuer()));

			sb.append("\"");
		}

		if (oAuthClientASLocalMetadata.getLocalWellKnownEnabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"localWellKnownEnabled\": ");

			sb.append(oAuthClientASLocalMetadata.getLocalWellKnownEnabled());
		}

		if (oAuthClientASLocalMetadata.getLocalWellKnownURI() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"localWellKnownURI\": ");

			sb.append("\"");

			sb.append(
				_escape(oAuthClientASLocalMetadata.getLocalWellKnownURI()));

			sb.append("\"");
		}

		if (oAuthClientASLocalMetadata.getMetadataJSON() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metadataJSON\": ");

			sb.append("\"");

			sb.append(_escape(oAuthClientASLocalMetadata.getMetadataJSON()));

			sb.append("\"");
		}

		if (oAuthClientASLocalMetadata.getOAuthASLocalWellKnownURI() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"oAuthASLocalWellKnownURI\": ");

			sb.append("\"");

			sb.append(
				_escape(
					oAuthClientASLocalMetadata.getOAuthASLocalWellKnownURI()));

			sb.append("\"");
		}

		if (oAuthClientASLocalMetadata.getOAuthASMetadataJSON() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"oAuthASMetadataJSON\": ");

			sb.append("\"");

			sb.append(
				_escape(oAuthClientASLocalMetadata.getOAuthASMetadataJSON()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OAuthClientASLocalMetadataJSONParser
			oAuthClientASLocalMetadataJSONParser =
				new OAuthClientASLocalMetadataJSONParser();

		return oAuthClientASLocalMetadataJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		if (oAuthClientASLocalMetadata == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (oAuthClientASLocalMetadata.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put(
				"creator",
				String.valueOf(oAuthClientASLocalMetadata.getCreator()));
		}

		if (oAuthClientASLocalMetadata.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					oAuthClientASLocalMetadata.getDateCreated()));
		}

		if (oAuthClientASLocalMetadata.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					oAuthClientASLocalMetadata.getDateModified()));
		}

		if (oAuthClientASLocalMetadata.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(
					oAuthClientASLocalMetadata.getExternalReferenceCode()));
		}

		if (oAuthClientASLocalMetadata.getIssuer() == null) {
			map.put("issuer", null);
		}
		else {
			map.put(
				"issuer",
				String.valueOf(oAuthClientASLocalMetadata.getIssuer()));
		}

		if (oAuthClientASLocalMetadata.getLocalWellKnownEnabled() == null) {
			map.put("localWellKnownEnabled", null);
		}
		else {
			map.put(
				"localWellKnownEnabled",
				String.valueOf(
					oAuthClientASLocalMetadata.getLocalWellKnownEnabled()));
		}

		if (oAuthClientASLocalMetadata.getLocalWellKnownURI() == null) {
			map.put("localWellKnownURI", null);
		}
		else {
			map.put(
				"localWellKnownURI",
				String.valueOf(
					oAuthClientASLocalMetadata.getLocalWellKnownURI()));
		}

		if (oAuthClientASLocalMetadata.getMetadataJSON() == null) {
			map.put("metadataJSON", null);
		}
		else {
			map.put(
				"metadataJSON",
				String.valueOf(oAuthClientASLocalMetadata.getMetadataJSON()));
		}

		if (oAuthClientASLocalMetadata.getOAuthASLocalWellKnownURI() == null) {
			map.put("oAuthASLocalWellKnownURI", null);
		}
		else {
			map.put(
				"oAuthASLocalWellKnownURI",
				String.valueOf(
					oAuthClientASLocalMetadata.getOAuthASLocalWellKnownURI()));
		}

		if (oAuthClientASLocalMetadata.getOAuthASMetadataJSON() == null) {
			map.put("oAuthASMetadataJSON", null);
		}
		else {
			map.put(
				"oAuthASMetadataJSON",
				String.valueOf(
					oAuthClientASLocalMetadata.getOAuthASMetadataJSON()));
		}

		return map;
	}

	public static class OAuthClientASLocalMetadataJSONParser
		extends BaseJSONParser<OAuthClientASLocalMetadata> {

		@Override
		protected OAuthClientASLocalMetadata createDTO() {
			return new OAuthClientASLocalMetadata();
		}

		@Override
		protected OAuthClientASLocalMetadata[] createDTOArray(int size) {
			return new OAuthClientASLocalMetadata[size];
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
			else if (Objects.equals(jsonParserFieldName, "issuer")) {
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
			else if (Objects.equals(
						jsonParserFieldName, "oAuthASLocalWellKnownURI")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "oAuthASMetadataJSON")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			OAuthClientASLocalMetadata oAuthClientASLocalMetadata,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					oAuthClientASLocalMetadata.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					oAuthClientASLocalMetadata.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					oAuthClientASLocalMetadata.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					oAuthClientASLocalMetadata.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "issuer")) {
				if (jsonParserFieldValue != null) {
					oAuthClientASLocalMetadata.setIssuer(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "localWellKnownEnabled")) {

				if (jsonParserFieldValue != null) {
					oAuthClientASLocalMetadata.setLocalWellKnownEnabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "localWellKnownURI")) {
				if (jsonParserFieldValue != null) {
					oAuthClientASLocalMetadata.setLocalWellKnownURI(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "metadataJSON")) {
				if (jsonParserFieldValue != null) {
					oAuthClientASLocalMetadata.setMetadataJSON(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "oAuthASLocalWellKnownURI")) {

				if (jsonParserFieldValue != null) {
					oAuthClientASLocalMetadata.setOAuthASLocalWellKnownURI(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "oAuthASMetadataJSON")) {

				if (jsonParserFieldValue != null) {
					oAuthClientASLocalMetadata.setOAuthASMetadataJSON(
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
// LIFERAY-REST-BUILDER-HASH:-759609169