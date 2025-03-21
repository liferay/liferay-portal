/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.AuthenticationScheme;
import com.liferay.scim.rest.client.dto.v1_0.ServiceProviderConfig;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class ServiceProviderConfigSerDes {

	public static ServiceProviderConfig toDTO(String json) {
		ServiceProviderConfigJSONParser serviceProviderConfigJSONParser =
			new ServiceProviderConfigJSONParser();

		return serviceProviderConfigJSONParser.parseToDTO(json);
	}

	public static ServiceProviderConfig[] toDTOs(String json) {
		ServiceProviderConfigJSONParser serviceProviderConfigJSONParser =
			new ServiceProviderConfigJSONParser();

		return serviceProviderConfigJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ServiceProviderConfig serviceProviderConfig) {
		if (serviceProviderConfig == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (serviceProviderConfig.getAuthenticationSchemes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"authenticationSchemes\": ");

			sb.append("[");

			for (int i = 0;
				 i < serviceProviderConfig.getAuthenticationSchemes().length;
				 i++) {

				sb.append(
					String.valueOf(
						serviceProviderConfig.getAuthenticationSchemes()[i]));

				if ((i + 1) <
						serviceProviderConfig.
							getAuthenticationSchemes().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (serviceProviderConfig.getBulk() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"bulk\": ");

			sb.append(String.valueOf(serviceProviderConfig.getBulk()));
		}

		if (serviceProviderConfig.getChangePassword() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"changePassword\": ");

			sb.append(
				String.valueOf(serviceProviderConfig.getChangePassword()));
		}

		if (serviceProviderConfig.getDocumentationUri() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"documentationUri\": ");

			sb.append("\"");

			sb.append(_escape(serviceProviderConfig.getDocumentationUri()));

			sb.append("\"");
		}

		if (serviceProviderConfig.getEtag() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"etag\": ");

			sb.append(String.valueOf(serviceProviderConfig.getEtag()));
		}

		if (serviceProviderConfig.getFilter() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"filter\": ");

			sb.append(String.valueOf(serviceProviderConfig.getFilter()));
		}

		if (serviceProviderConfig.getMeta() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"meta\": ");

			sb.append(String.valueOf(serviceProviderConfig.getMeta()));
		}

		if (serviceProviderConfig.getPatch() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"patch\": ");

			sb.append(String.valueOf(serviceProviderConfig.getPatch()));
		}

		if (serviceProviderConfig.getSchemas() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schemas\": ");

			sb.append("[");

			for (int i = 0; i < serviceProviderConfig.getSchemas().length;
				 i++) {

				sb.append(_toJSON(serviceProviderConfig.getSchemas()[i]));

				if ((i + 1) < serviceProviderConfig.getSchemas().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (serviceProviderConfig.getSort() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sort\": ");

			sb.append(String.valueOf(serviceProviderConfig.getSort()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ServiceProviderConfigJSONParser serviceProviderConfigJSONParser =
			new ServiceProviderConfigJSONParser();

		return serviceProviderConfigJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		ServiceProviderConfig serviceProviderConfig) {

		if (serviceProviderConfig == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (serviceProviderConfig.getAuthenticationSchemes() == null) {
			map.put("authenticationSchemes", null);
		}
		else {
			map.put(
				"authenticationSchemes",
				String.valueOf(
					serviceProviderConfig.getAuthenticationSchemes()));
		}

		if (serviceProviderConfig.getBulk() == null) {
			map.put("bulk", null);
		}
		else {
			map.put("bulk", String.valueOf(serviceProviderConfig.getBulk()));
		}

		if (serviceProviderConfig.getChangePassword() == null) {
			map.put("changePassword", null);
		}
		else {
			map.put(
				"changePassword",
				String.valueOf(serviceProviderConfig.getChangePassword()));
		}

		if (serviceProviderConfig.getDocumentationUri() == null) {
			map.put("documentationUri", null);
		}
		else {
			map.put(
				"documentationUri",
				String.valueOf(serviceProviderConfig.getDocumentationUri()));
		}

		if (serviceProviderConfig.getEtag() == null) {
			map.put("etag", null);
		}
		else {
			map.put("etag", String.valueOf(serviceProviderConfig.getEtag()));
		}

		if (serviceProviderConfig.getFilter() == null) {
			map.put("filter", null);
		}
		else {
			map.put(
				"filter", String.valueOf(serviceProviderConfig.getFilter()));
		}

		if (serviceProviderConfig.getMeta() == null) {
			map.put("meta", null);
		}
		else {
			map.put("meta", String.valueOf(serviceProviderConfig.getMeta()));
		}

		if (serviceProviderConfig.getPatch() == null) {
			map.put("patch", null);
		}
		else {
			map.put("patch", String.valueOf(serviceProviderConfig.getPatch()));
		}

		if (serviceProviderConfig.getSchemas() == null) {
			map.put("schemas", null);
		}
		else {
			map.put(
				"schemas", String.valueOf(serviceProviderConfig.getSchemas()));
		}

		if (serviceProviderConfig.getSort() == null) {
			map.put("sort", null);
		}
		else {
			map.put("sort", String.valueOf(serviceProviderConfig.getSort()));
		}

		return map;
	}

	public static class ServiceProviderConfigJSONParser
		extends BaseJSONParser<ServiceProviderConfig> {

		@Override
		protected ServiceProviderConfig createDTO() {
			return new ServiceProviderConfig();
		}

		@Override
		protected ServiceProviderConfig[] createDTOArray(int size) {
			return new ServiceProviderConfig[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "authenticationSchemes")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "bulk")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "changePassword")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "documentationUri")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "etag")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "filter")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "meta")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "patch")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "schemas")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "sort")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ServiceProviderConfig serviceProviderConfig,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "authenticationSchemes")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					AuthenticationScheme[] authenticationSchemesArray =
						new AuthenticationScheme[jsonParserFieldValues.length];

					for (int i = 0; i < authenticationSchemesArray.length;
						 i++) {

						authenticationSchemesArray[i] =
							AuthenticationSchemeSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					serviceProviderConfig.setAuthenticationSchemes(
						authenticationSchemesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "bulk")) {
				if (jsonParserFieldValue != null) {
					serviceProviderConfig.setBulk(
						BulkSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "changePassword")) {
				if (jsonParserFieldValue != null) {
					serviceProviderConfig.setChangePassword(
						ChangePasswordSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "documentationUri")) {
				if (jsonParserFieldValue != null) {
					serviceProviderConfig.setDocumentationUri(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "etag")) {
				if (jsonParserFieldValue != null) {
					serviceProviderConfig.setEtag(
						EtagSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "filter")) {
				if (jsonParserFieldValue != null) {
					serviceProviderConfig.setFilter(
						FilterSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "meta")) {
				if (jsonParserFieldValue != null) {
					serviceProviderConfig.setMeta(
						MetaSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "patch")) {
				if (jsonParserFieldValue != null) {
					serviceProviderConfig.setPatch(
						PatchSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "schemas")) {
				if (jsonParserFieldValue != null) {
					serviceProviderConfig.setSchemas(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "sort")) {
				if (jsonParserFieldValue != null) {
					serviceProviderConfig.setSort(
						SortSerDes.toDTO((String)jsonParserFieldValue));
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