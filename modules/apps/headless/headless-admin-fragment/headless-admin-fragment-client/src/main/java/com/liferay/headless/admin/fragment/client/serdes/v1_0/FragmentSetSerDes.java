/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.client.serdes.v1_0;

import com.liferay.headless.admin.fragment.client.dto.v1_0.FragmentSet;
import com.liferay.headless.admin.fragment.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public class FragmentSetSerDes {

	public static FragmentSet toDTO(String json) {
		FragmentSetJSONParser fragmentSetJSONParser =
			new FragmentSetJSONParser();

		return fragmentSetJSONParser.parseToDTO(json);
	}

	public static FragmentSet[] toDTOs(String json) {
		FragmentSetJSONParser fragmentSetJSONParser =
			new FragmentSetJSONParser();

		return fragmentSetJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FragmentSet fragmentSet) {
		if (fragmentSet == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (fragmentSet.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(fragmentSet.getActions()));
		}

		if (fragmentSet.getCreator() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"creator\": ");

			sb.append(fragmentSet.getCreator());
		}

		if (fragmentSet.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(fragmentSet.getDateCreated()));

			sb.append("\"");
		}

		if (fragmentSet.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(fragmentSet.getDateModified()));

			sb.append("\"");
		}

		if (fragmentSet.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(fragmentSet.getDescription()));

			sb.append("\"");
		}

		if (fragmentSet.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(fragmentSet.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (fragmentSet.getKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"key\": ");

			sb.append("\"");

			sb.append(_escape(fragmentSet.getKey()));

			sb.append("\"");
		}

		if (fragmentSet.getMarketplace() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"marketplace\": ");

			sb.append(fragmentSet.getMarketplace());
		}

		if (fragmentSet.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(fragmentSet.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentSetJSONParser fragmentSetJSONParser =
			new FragmentSetJSONParser();

		return fragmentSetJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(FragmentSet fragmentSet) {
		if (fragmentSet == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (fragmentSet.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(fragmentSet.getActions()));
		}

		if (fragmentSet.getCreator() == null) {
			map.put("creator", null);
		}
		else {
			map.put("creator", String.valueOf(fragmentSet.getCreator()));
		}

		if (fragmentSet.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(fragmentSet.getDateCreated()));
		}

		if (fragmentSet.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(fragmentSet.getDateModified()));
		}

		if (fragmentSet.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(fragmentSet.getDescription()));
		}

		if (fragmentSet.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(fragmentSet.getExternalReferenceCode()));
		}

		if (fragmentSet.getKey() == null) {
			map.put("key", null);
		}
		else {
			map.put("key", String.valueOf(fragmentSet.getKey()));
		}

		if (fragmentSet.getMarketplace() == null) {
			map.put("marketplace", null);
		}
		else {
			map.put(
				"marketplace", String.valueOf(fragmentSet.getMarketplace()));
		}

		if (fragmentSet.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(fragmentSet.getName()));
		}

		return map;
	}

	public static class FragmentSetJSONParser
		extends BaseJSONParser<FragmentSet> {

		@Override
		protected FragmentSet createDTO() {
			return new FragmentSet();
		}

		@Override
		protected FragmentSet[] createDTOArray(int size) {
			return new FragmentSet[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "marketplace")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FragmentSet fragmentSet, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					fragmentSet.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "creator")) {
				if (jsonParserFieldValue != null) {
					fragmentSet.setCreator(
						CreatorSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					fragmentSet.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					fragmentSet.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					fragmentSet.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					fragmentSet.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "key")) {
				if (jsonParserFieldValue != null) {
					fragmentSet.setKey((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "marketplace")) {
				if (jsonParserFieldValue != null) {
					fragmentSet.setMarketplace((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					fragmentSet.setName((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-631635417