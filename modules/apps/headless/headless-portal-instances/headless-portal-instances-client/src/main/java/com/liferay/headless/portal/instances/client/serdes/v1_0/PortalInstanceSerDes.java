/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.client.serdes.v1_0;

import com.liferay.headless.portal.instances.client.dto.v1_0.PortalInstance;
import com.liferay.headless.portal.instances.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Alberto Chaparro
 * @generated
 */
@Generated("")
public class PortalInstanceSerDes {

	public static PortalInstance toDTO(String json) {
		PortalInstanceJSONParser portalInstanceJSONParser =
			new PortalInstanceJSONParser();

		return portalInstanceJSONParser.parseToDTO(json);
	}

	public static PortalInstance[] toDTOs(String json) {
		PortalInstanceJSONParser portalInstanceJSONParser =
			new PortalInstanceJSONParser();

		return portalInstanceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PortalInstance portalInstance) {
		if (portalInstance == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (portalInstance.getActive() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"active\": ");

			sb.append(portalInstance.getActive());
		}

		if (portalInstance.getAdmin() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"admin\": ");

			sb.append(String.valueOf(portalInstance.getAdmin()));
		}

		if (portalInstance.getCompanyId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"companyId\": ");

			sb.append(portalInstance.getCompanyId());
		}

		if (portalInstance.getDomain() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"domain\": ");

			sb.append("\"");

			sb.append(_escape(portalInstance.getDomain()));

			sb.append("\"");
		}

		if (portalInstance.getPortalInstanceId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"portalInstanceId\": ");

			sb.append("\"");

			sb.append(_escape(portalInstance.getPortalInstanceId()));

			sb.append("\"");
		}

		if (portalInstance.getSiteInitializerKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"siteInitializerKey\": ");

			sb.append("\"");

			sb.append(_escape(portalInstance.getSiteInitializerKey()));

			sb.append("\"");
		}

		if (portalInstance.getVirtualHost() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"virtualHost\": ");

			sb.append("\"");

			sb.append(_escape(portalInstance.getVirtualHost()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PortalInstanceJSONParser portalInstanceJSONParser =
			new PortalInstanceJSONParser();

		return portalInstanceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PortalInstance portalInstance) {
		if (portalInstance == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (portalInstance.getActive() == null) {
			map.put("active", null);
		}
		else {
			map.put("active", String.valueOf(portalInstance.getActive()));
		}

		if (portalInstance.getAdmin() == null) {
			map.put("admin", null);
		}
		else {
			map.put("admin", String.valueOf(portalInstance.getAdmin()));
		}

		if (portalInstance.getCompanyId() == null) {
			map.put("companyId", null);
		}
		else {
			map.put("companyId", String.valueOf(portalInstance.getCompanyId()));
		}

		if (portalInstance.getDomain() == null) {
			map.put("domain", null);
		}
		else {
			map.put("domain", String.valueOf(portalInstance.getDomain()));
		}

		if (portalInstance.getPortalInstanceId() == null) {
			map.put("portalInstanceId", null);
		}
		else {
			map.put(
				"portalInstanceId",
				String.valueOf(portalInstance.getPortalInstanceId()));
		}

		if (portalInstance.getSiteInitializerKey() == null) {
			map.put("siteInitializerKey", null);
		}
		else {
			map.put(
				"siteInitializerKey",
				String.valueOf(portalInstance.getSiteInitializerKey()));
		}

		if (portalInstance.getVirtualHost() == null) {
			map.put("virtualHost", null);
		}
		else {
			map.put(
				"virtualHost", String.valueOf(portalInstance.getVirtualHost()));
		}

		return map;
	}

	public static class PortalInstanceJSONParser
		extends BaseJSONParser<PortalInstance> {

		@Override
		protected PortalInstance createDTO() {
			return new PortalInstance();
		}

		@Override
		protected PortalInstance[] createDTOArray(int size) {
			return new PortalInstance[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "active")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "admin")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "companyId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "domain")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "portalInstanceId")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "siteInitializerKey")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "virtualHost")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PortalInstance portalInstance, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "active")) {
				if (jsonParserFieldValue != null) {
					portalInstance.setActive((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "admin")) {
				if (jsonParserFieldValue != null) {
					portalInstance.setAdmin(
						AdminSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "companyId")) {
				if (jsonParserFieldValue != null) {
					portalInstance.setCompanyId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "domain")) {
				if (jsonParserFieldValue != null) {
					portalInstance.setDomain((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "portalInstanceId")) {
				if (jsonParserFieldValue != null) {
					portalInstance.setPortalInstanceId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "siteInitializerKey")) {

				if (jsonParserFieldValue != null) {
					portalInstance.setSiteInitializerKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "virtualHost")) {
				if (jsonParserFieldValue != null) {
					portalInstance.setVirtualHost((String)jsonParserFieldValue);
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