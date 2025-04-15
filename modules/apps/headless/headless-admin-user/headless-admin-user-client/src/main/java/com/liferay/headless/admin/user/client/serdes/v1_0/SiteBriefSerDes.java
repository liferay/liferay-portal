/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.client.serdes.v1_0;

import com.liferay.headless.admin.user.client.dto.v1_0.RoleBrief;
import com.liferay.headless.admin.user.client.dto.v1_0.SiteBrief;
import com.liferay.headless.admin.user.client.json.BaseJSONParser;

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
public class SiteBriefSerDes {

	public static SiteBrief toDTO(String json) {
		SiteBriefJSONParser siteBriefJSONParser = new SiteBriefJSONParser();

		return siteBriefJSONParser.parseToDTO(json);
	}

	public static SiteBrief[] toDTOs(String json) {
		SiteBriefJSONParser siteBriefJSONParser = new SiteBriefJSONParser();

		return siteBriefJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SiteBrief siteBrief) {
		if (siteBrief == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (siteBrief.getDescriptiveName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptiveName\": ");

			sb.append("\"");

			sb.append(_escape(siteBrief.getDescriptiveName()));

			sb.append("\"");
		}

		if (siteBrief.getDescriptiveName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptiveName_i18n\": ");

			sb.append(_toJSON(siteBrief.getDescriptiveName_i18n()));
		}

		if (siteBrief.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(siteBrief.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (siteBrief.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(siteBrief.getId());
		}

		if (siteBrief.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(siteBrief.getName()));

			sb.append("\"");
		}

		if (siteBrief.getName_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(siteBrief.getName_i18n()));
		}

		if (siteBrief.getRoleBriefs() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"roleBriefs\": ");

			sb.append("[");

			for (int i = 0; i < siteBrief.getRoleBriefs().length; i++) {
				sb.append(String.valueOf(siteBrief.getRoleBriefs()[i]));

				if ((i + 1) < siteBrief.getRoleBriefs().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SiteBriefJSONParser siteBriefJSONParser = new SiteBriefJSONParser();

		return siteBriefJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SiteBrief siteBrief) {
		if (siteBrief == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (siteBrief.getDescriptiveName() == null) {
			map.put("descriptiveName", null);
		}
		else {
			map.put(
				"descriptiveName",
				String.valueOf(siteBrief.getDescriptiveName()));
		}

		if (siteBrief.getDescriptiveName_i18n() == null) {
			map.put("descriptiveName_i18n", null);
		}
		else {
			map.put(
				"descriptiveName_i18n",
				String.valueOf(siteBrief.getDescriptiveName_i18n()));
		}

		if (siteBrief.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(siteBrief.getExternalReferenceCode()));
		}

		if (siteBrief.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(siteBrief.getId()));
		}

		if (siteBrief.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(siteBrief.getName()));
		}

		if (siteBrief.getName_i18n() == null) {
			map.put("name_i18n", null);
		}
		else {
			map.put("name_i18n", String.valueOf(siteBrief.getName_i18n()));
		}

		if (siteBrief.getRoleBriefs() == null) {
			map.put("roleBriefs", null);
		}
		else {
			map.put("roleBriefs", String.valueOf(siteBrief.getRoleBriefs()));
		}

		return map;
	}

	public static class SiteBriefJSONParser extends BaseJSONParser<SiteBrief> {

		@Override
		protected SiteBrief createDTO() {
			return new SiteBrief();
		}

		@Override
		protected SiteBrief[] createDTOArray(int size) {
			return new SiteBrief[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "descriptiveName")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "descriptiveName_i18n")) {

				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "roleBriefs")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SiteBrief siteBrief, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "descriptiveName")) {
				if (jsonParserFieldValue != null) {
					siteBrief.setDescriptiveName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "descriptiveName_i18n")) {

				if (jsonParserFieldValue != null) {
					siteBrief.setDescriptiveName_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					siteBrief.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					siteBrief.setId(Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					siteBrief.setName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name_i18n")) {
				if (jsonParserFieldValue != null) {
					siteBrief.setName_i18n(
						(Map<String, String>)jsonParserFieldValue);
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

					siteBrief.setRoleBriefs(roleBriefsArray);
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