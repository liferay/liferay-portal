/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.OrganizationBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class OrganizationBriefSerDes {

	public static OrganizationBrief toDTO(String json) {
		OrganizationBriefJSONParser organizationBriefJSONParser =
			new OrganizationBriefJSONParser();

		return organizationBriefJSONParser.parseToDTO(json);
	}

	public static OrganizationBrief[] toDTOs(String json) {
		OrganizationBriefJSONParser organizationBriefJSONParser =
			new OrganizationBriefJSONParser();

		return organizationBriefJSONParser.parseToDTOs(json);
	}

	public static String toJSON(OrganizationBrief organizationBrief) {
		if (organizationBrief == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (organizationBrief.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(organizationBrief.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (organizationBrief.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(organizationBrief.getId());
		}

		if (organizationBrief.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(organizationBrief.getName()));

			sb.append("\"");
		}

		if (organizationBrief.getRoleBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleBriefs\": ");

			sb.append("[");

			for (int i = 0; i < organizationBrief.getRoleBriefs().length; i++) {
				sb.append(String.valueOf(organizationBrief.getRoleBriefs()[i]));

				if ((i + 1) < organizationBrief.getRoleBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		OrganizationBriefJSONParser organizationBriefJSONParser =
			new OrganizationBriefJSONParser();

		return organizationBriefJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		OrganizationBrief organizationBrief) {

		if (organizationBrief == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (organizationBrief.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(organizationBrief.getExternalReferenceCode()));
		}

		if (organizationBrief.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(organizationBrief.getId()));
		}

		if (organizationBrief.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(organizationBrief.getName()));
		}

		if (organizationBrief.getRoleBriefs() == null) {
			map.put("roleBriefs", null);
		}
		else {
			map.put(
				"roleBriefs",
				String.valueOf(organizationBrief.getRoleBriefs()));
		}

		return map;
	}

	public static class OrganizationBriefJSONParser
		extends BaseJSONParser<OrganizationBrief> {

		@Override
		protected OrganizationBrief createDTO() {
			return new OrganizationBrief();
		}

		@Override
		protected OrganizationBrief[] createDTOArray(int size) {
			return new OrganizationBrief[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "roleBriefs")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			OrganizationBrief organizationBrief, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "externalReferenceCode")) {
				if (jsonParserFieldValue != null) {
					organizationBrief.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					organizationBrief.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					organizationBrief.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "roleBriefs")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					RoleBrief[] roleBriefsArray =
						new RoleBrief[jsonParserFieldValues.length];

					for (int i = 0; i < roleBriefsArray.length; i++) {
						roleBriefsArray[i] = RoleBriefSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					organizationBrief.setRoleBriefs(roleBriefsArray);
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