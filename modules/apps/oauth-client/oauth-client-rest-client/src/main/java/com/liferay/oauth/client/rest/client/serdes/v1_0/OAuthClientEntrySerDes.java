/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.rest.client.serdes.v1_0;

import com.liferay.oauth.client.rest.client.dto.v1_0.OAuthClientEntry;
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
public class OAuthClientEntrySerDes {

	public static OAuthClientEntry toDTO(String json) {
		OAuthClientEntryJSONParser oAuthClientEntryJSONParser =
			new OAuthClientEntryJSONParser();

		return oAuthClientEntryJSONParser.parseToDTO(json);
	}

	public static OAuthClientEntry[] toDTOs(String json) {
		OAuthClientEntryJSONParser oAuthClientEntryJSONParser =
			new OAuthClientEntryJSONParser();

		return oAuthClientEntryJSONParser.parseToDTOs(json);
	}

	public static String toJSON(OAuthClientEntry oAuthClientEntry) {
		if (oAuthClientEntry == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (oAuthClientEntry.getAuthRequestParametersJSON() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"authRequestParametersJSON\": ");

			sb.append("\"");

			sb.append(_escape(oAuthClientEntry.getAuthRequestParametersJSON()));

			sb.append("\"");
		}

		if (oAuthClientEntry.getAuthServerWellKnownURI() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"authServerWellKnownURI\": ");

			sb.append("\"");

			sb.append(_escape(oAuthClientEntry.getAuthServerWellKnownURI()));

			sb.append("\"");
		}

		if (oAuthClientEntry.getClientId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clientId\": ");

			sb.append("\"");

			sb.append(_escape(oAuthClientEntry.getClientId()));

			sb.append("\"");
		}

		if (oAuthClientEntry.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(oAuthClientEntry.getCreator());
		}

		if (oAuthClientEntry.getCustomClaims() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customClaims\": ");

			sb.append("\"");

			sb.append(_escape(oAuthClientEntry.getCustomClaims()));

			sb.append("\"");
		}

		if (oAuthClientEntry.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					oAuthClientEntry.getDateCreated()));

			sb.append("\"");
		}

		if (oAuthClientEntry.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(
					oAuthClientEntry.getDateModified()));

			sb.append("\"");
		}

		if (oAuthClientEntry.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(oAuthClientEntry.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (oAuthClientEntry.getInfoJSON() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"infoJSON\": ");

			sb.append("\"");

			sb.append(_escape(oAuthClientEntry.getInfoJSON()));

			sb.append("\"");
		}

		if (oAuthClientEntry.getMatcherField() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"matcherField\": ");

			sb.append("\"");

			sb.append(_escape(oAuthClientEntry.getMatcherField()));

			sb.append("\"");
		}

		if (oAuthClientEntry.getMetadataCacheTime() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"metadataCacheTime\": ");

			sb.append(oAuthClientEntry.getMetadataCacheTime());
		}

		if (oAuthClientEntry.getOAuthClientASLocalMetadata() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"oAuthClientASLocalMetadata\": ");

			sb.append(
				String.valueOf(
					oAuthClientEntry.getOAuthClientASLocalMetadata()));
		}

		if (oAuthClientEntry.getOidcUserInfoMapperJSON() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"oidcUserInfoMapperJSON\": ");

			sb.append("\"");

			sb.append(_escape(oAuthClientEntry.getOidcUserInfoMapperJSON()));

			sb.append("\"");
		}

		if (oAuthClientEntry.getTokenRequestParametersJSON() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tokenRequestParametersJSON\": ");

			sb.append("\"");

			sb.append(
				_escape(oAuthClientEntry.getTokenRequestParametersJSON()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OAuthClientEntryJSONParser oAuthClientEntryJSONParser =
			new OAuthClientEntryJSONParser();

		return oAuthClientEntryJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(OAuthClientEntry oAuthClientEntry) {
		if (oAuthClientEntry == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (oAuthClientEntry.getAuthRequestParametersJSON() == null) {
			map.put("authRequestParametersJSON", null);
		}
		else {
			map.put(
				"authRequestParametersJSON",
				String.valueOf(
					oAuthClientEntry.getAuthRequestParametersJSON()));
		}

		if (oAuthClientEntry.getAuthServerWellKnownURI() == null) {
			map.put("authServerWellKnownURI", null);
		}
		else {
			map.put(
				"authServerWellKnownURI",
				String.valueOf(oAuthClientEntry.getAuthServerWellKnownURI()));
		}

		if (oAuthClientEntry.getClientId() == null) {
			map.put("clientId", null);
		}
		else {
			map.put("clientId", String.valueOf(oAuthClientEntry.getClientId()));
		}

		if (oAuthClientEntry.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(oAuthClientEntry.getCreator()));
		}

		if (oAuthClientEntry.getCustomClaims() == null) {
			map.put("customClaims", null);
		}
		else {
			map.put(
				"customClaims",
				String.valueOf(oAuthClientEntry.getCustomClaims()));
		}

		if (oAuthClientEntry.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(
					oAuthClientEntry.getDateCreated()));
		}

		if (oAuthClientEntry.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(
					oAuthClientEntry.getDateModified()));
		}

		if (oAuthClientEntry.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(oAuthClientEntry.getExternalReferenceCode()));
		}

		if (oAuthClientEntry.getInfoJSON() == null) {
			map.put("infoJSON", null);
		}
		else {
			map.put("infoJSON", String.valueOf(oAuthClientEntry.getInfoJSON()));
		}

		if (oAuthClientEntry.getMatcherField() == null) {
			map.put("matcherField", null);
		}
		else {
			map.put(
				"matcherField",
				String.valueOf(oAuthClientEntry.getMatcherField()));
		}

		if (oAuthClientEntry.getMetadataCacheTime() == null) {
			map.put("metadataCacheTime", null);
		}
		else {
			map.put(
				"metadataCacheTime",
				String.valueOf(oAuthClientEntry.getMetadataCacheTime()));
		}

		if (oAuthClientEntry.getOAuthClientASLocalMetadata() == null) {
			map.put("oAuthClientASLocalMetadata", null);
		}
		else {
			map.put(
				"oAuthClientASLocalMetadata",
				String.valueOf(
					oAuthClientEntry.getOAuthClientASLocalMetadata()));
		}

		if (oAuthClientEntry.getOidcUserInfoMapperJSON() == null) {
			map.put("oidcUserInfoMapperJSON", null);
		}
		else {
			map.put(
				"oidcUserInfoMapperJSON",
				String.valueOf(oAuthClientEntry.getOidcUserInfoMapperJSON()));
		}

		if (oAuthClientEntry.getTokenRequestParametersJSON() == null) {
			map.put("tokenRequestParametersJSON", null);
		}
		else {
			map.put(
				"tokenRequestParametersJSON",
				String.valueOf(
					oAuthClientEntry.getTokenRequestParametersJSON()));
		}

		return map;
	}

	public static class OAuthClientEntryJSONParser
		extends BaseJSONParser<OAuthClientEntry> {

		@Override
		protected OAuthClientEntry createDTO() {
			return new OAuthClientEntry();
		}

		@Override
		protected OAuthClientEntry[] createDTOArray(int size) {
			return new OAuthClientEntry[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "authRequestParametersJSON")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "authServerWellKnownURI")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "clientId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customClaims")) {
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
			else if (Objects.equals(jsonParserFieldName, "infoJSON")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "matcherField")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "metadataCacheTime")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "oAuthClientASLocalMetadata")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "oidcUserInfoMapperJSON")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "tokenRequestParametersJSON")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			OAuthClientEntry oAuthClientEntry, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "authRequestParametersJSON")) {

				if (jsonParserFieldValue != null) {
					oAuthClientEntry.setAuthRequestParametersJSON(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "authServerWellKnownURI")) {

				if (jsonParserFieldValue != null) {
					oAuthClientEntry.setAuthServerWellKnownURI(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "clientId")) {
				if (jsonParserFieldValue != null) {
					oAuthClientEntry.setClientId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					oAuthClientEntry.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customClaims")) {
				if (jsonParserFieldValue != null) {
					oAuthClientEntry.setCustomClaims(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					oAuthClientEntry.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					oAuthClientEntry.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					oAuthClientEntry.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "infoJSON")) {
				if (jsonParserFieldValue != null) {
					oAuthClientEntry.setInfoJSON((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "matcherField")) {
				if (jsonParserFieldValue != null) {
					oAuthClientEntry.setMatcherField(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "metadataCacheTime")) {
				if (jsonParserFieldValue != null) {
					oAuthClientEntry.setMetadataCacheTime(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "oAuthClientASLocalMetadata")) {

				if (jsonParserFieldValue != null) {
					oAuthClientEntry.setOAuthClientASLocalMetadata(
						OAuthClientASLocalMetadataSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "oidcUserInfoMapperJSON")) {

				if (jsonParserFieldValue != null) {
					oAuthClientEntry.setOidcUserInfoMapperJSON(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "tokenRequestParametersJSON")) {

				if (jsonParserFieldValue != null) {
					oAuthClientEntry.setTokenRequestParametersJSON(
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
// LIFERAY-REST-BUILDER-HASH:-695384484