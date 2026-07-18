/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.portal.instances.client.serdes.v1_0;

import com.liferay.headless.portal.instances.client.dto.v1_0.PortalInstanceCopy;
import com.liferay.headless.portal.instances.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Alberto Chaparro
 * @generated
 */
@Generated("")
public class PortalInstanceCopySerDes {

	public static PortalInstanceCopy toDTO(String json) {
		PortalInstanceCopyJSONParser portalInstanceCopyJSONParser =
			new PortalInstanceCopyJSONParser();

		return portalInstanceCopyJSONParser.parseToDTO(json);
	}

	public static PortalInstanceCopy[] toDTOs(String json) {
		PortalInstanceCopyJSONParser portalInstanceCopyJSONParser =
			new PortalInstanceCopyJSONParser();

		return portalInstanceCopyJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PortalInstanceCopy portalInstanceCopy) {
		if (portalInstanceCopy == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (portalInstanceCopy.getDestinationCompanyId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"destinationCompanyId\": ");

			sb.append(portalInstanceCopy.getDestinationCompanyId());
		}

		if (portalInstanceCopy.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(portalInstanceCopy.getName()));

			sb.append("\"");
		}

		if (portalInstanceCopy.getVirtualHost() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"virtualHost\": ");

			sb.append("\"");

			sb.append(_escape(portalInstanceCopy.getVirtualHost()));

			sb.append("\"");
		}

		if (portalInstanceCopy.getWebId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"webId\": ");

			sb.append("\"");

			sb.append(_escape(portalInstanceCopy.getWebId()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PortalInstanceCopyJSONParser portalInstanceCopyJSONParser =
			new PortalInstanceCopyJSONParser();

		return portalInstanceCopyJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PortalInstanceCopy portalInstanceCopy) {

		if (portalInstanceCopy == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (portalInstanceCopy.getDestinationCompanyId() == null) {
			map.put("destinationCompanyId", null);
		}
		else {
			map.put(
				"destinationCompanyId",
				String.valueOf(portalInstanceCopy.getDestinationCompanyId()));
		}

		if (portalInstanceCopy.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(portalInstanceCopy.getName()));
		}

		if (portalInstanceCopy.getVirtualHost() == null) {
			map.put("virtualHost", null);
		}
		else {
			map.put(
				"virtualHost",
				String.valueOf(portalInstanceCopy.getVirtualHost()));
		}

		if (portalInstanceCopy.getWebId() == null) {
			map.put("webId", null);
		}
		else {
			map.put("webId", String.valueOf(portalInstanceCopy.getWebId()));
		}

		return map;
	}

	public static class PortalInstanceCopyJSONParser
		extends BaseJSONParser<PortalInstanceCopy> {

		@Override
		protected PortalInstanceCopy createDTO() {
			return new PortalInstanceCopy();
		}

		@Override
		protected PortalInstanceCopy[] createDTOArray(int size) {
			return new PortalInstanceCopy[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "destinationCompanyId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "virtualHost")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "webId")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PortalInstanceCopy portalInstanceCopy, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "destinationCompanyId")) {
				if (jsonParserFieldValue != null) {
					portalInstanceCopy.setDestinationCompanyId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					portalInstanceCopy.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "virtualHost")) {
				if (jsonParserFieldValue != null) {
					portalInstanceCopy.setVirtualHost(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "webId")) {
				if (jsonParserFieldValue != null) {
					portalInstanceCopy.setWebId((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-305157922