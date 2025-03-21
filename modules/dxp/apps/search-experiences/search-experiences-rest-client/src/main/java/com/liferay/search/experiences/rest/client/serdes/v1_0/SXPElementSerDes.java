/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.SXPElement;
import com.liferay.search.experiences.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class SXPElementSerDes {

	public static SXPElement toDTO(String json) {
		SXPElementJSONParser sxpElementJSONParser = new SXPElementJSONParser();

		return sxpElementJSONParser.parseToDTO(json);
	}

	public static SXPElement[] toDTOs(String json) {
		SXPElementJSONParser sxpElementJSONParser = new SXPElementJSONParser();

		return sxpElementJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SXPElement sxpElement) {
		if (sxpElement == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (sxpElement.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(sxpElement.getActions()));
		}

		if (sxpElement.getCreateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(sxpElement.getCreateDate()));

			sb.append("\"");
		}

		if (sxpElement.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(sxpElement.getDescription()));

			sb.append("\"");
		}

		if (sxpElement.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(sxpElement.getDescription_i18n()));
		}

		if (sxpElement.getElementDefinition() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"elementDefinition\": ");

			sb.append(String.valueOf(sxpElement.getElementDefinition()));
		}

		if (sxpElement.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(sxpElement.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (sxpElement.getFallbackDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fallbackDescription\": ");

			sb.append("\"");

			sb.append(_escape(sxpElement.getFallbackDescription()));

			sb.append("\"");
		}

		if (sxpElement.getFallbackTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fallbackTitle\": ");

			sb.append("\"");

			sb.append(_escape(sxpElement.getFallbackTitle()));

			sb.append("\"");
		}

		if (sxpElement.getHidden() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"hidden\": ");

			sb.append(sxpElement.getHidden());
		}

		if (sxpElement.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(sxpElement.getId());
		}

		if (sxpElement.getModifiedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifiedDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(sxpElement.getModifiedDate()));

			sb.append("\"");
		}

		if (sxpElement.getReadOnly() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnly\": ");

			sb.append(sxpElement.getReadOnly());
		}

		if (sxpElement.getSchemaVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schemaVersion\": ");

			sb.append("\"");

			sb.append(_escape(sxpElement.getSchemaVersion()));

			sb.append("\"");
		}

		if (sxpElement.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(sxpElement.getTitle()));

			sb.append("\"");
		}

		if (sxpElement.getTitle_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title_i18n\": ");

			sb.append(_toJSON(sxpElement.getTitle_i18n()));
		}

		if (sxpElement.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append(sxpElement.getType());
		}

		if (sxpElement.getUserName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userName\": ");

			sb.append("\"");

			sb.append(_escape(sxpElement.getUserName()));

			sb.append("\"");
		}

		if (sxpElement.getVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"version\": ");

			sb.append("\"");

			sb.append(_escape(sxpElement.getVersion()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SXPElementJSONParser sxpElementJSONParser = new SXPElementJSONParser();

		return sxpElementJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SXPElement sxpElement) {
		if (sxpElement == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (sxpElement.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(sxpElement.getActions()));
		}

		if (sxpElement.getCreateDate() == null) {
			map.put("createDate", null);
		}
		else {
			map.put(
				"createDate",
				liferayToJSONDateFormat.format(sxpElement.getCreateDate()));
		}

		if (sxpElement.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put("description", String.valueOf(sxpElement.getDescription()));
		}

		if (sxpElement.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(sxpElement.getDescription_i18n()));
		}

		if (sxpElement.getElementDefinition() == null) {
			map.put("elementDefinition", null);
		}
		else {
			map.put(
				"elementDefinition",
				String.valueOf(sxpElement.getElementDefinition()));
		}

		if (sxpElement.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(sxpElement.getExternalReferenceCode()));
		}

		if (sxpElement.getFallbackDescription() == null) {
			map.put("fallbackDescription", null);
		}
		else {
			map.put(
				"fallbackDescription",
				String.valueOf(sxpElement.getFallbackDescription()));
		}

		if (sxpElement.getFallbackTitle() == null) {
			map.put("fallbackTitle", null);
		}
		else {
			map.put(
				"fallbackTitle", String.valueOf(sxpElement.getFallbackTitle()));
		}

		if (sxpElement.getHidden() == null) {
			map.put("hidden", null);
		}
		else {
			map.put("hidden", String.valueOf(sxpElement.getHidden()));
		}

		if (sxpElement.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(sxpElement.getId()));
		}

		if (sxpElement.getModifiedDate() == null) {
			map.put("modifiedDate", null);
		}
		else {
			map.put(
				"modifiedDate",
				liferayToJSONDateFormat.format(sxpElement.getModifiedDate()));
		}

		if (sxpElement.getReadOnly() == null) {
			map.put("readOnly", null);
		}
		else {
			map.put("readOnly", String.valueOf(sxpElement.getReadOnly()));
		}

		if (sxpElement.getSchemaVersion() == null) {
			map.put("schemaVersion", null);
		}
		else {
			map.put(
				"schemaVersion", String.valueOf(sxpElement.getSchemaVersion()));
		}

		if (sxpElement.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(sxpElement.getTitle()));
		}

		if (sxpElement.getTitle_i18n() == null) {
			map.put("title_i18n", null);
		}
		else {
			map.put("title_i18n", String.valueOf(sxpElement.getTitle_i18n()));
		}

		if (sxpElement.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put("type", String.valueOf(sxpElement.getType()));
		}

		if (sxpElement.getUserName() == null) {
			map.put("userName", null);
		}
		else {
			map.put("userName", String.valueOf(sxpElement.getUserName()));
		}

		if (sxpElement.getVersion() == null) {
			map.put("version", null);
		}
		else {
			map.put("version", String.valueOf(sxpElement.getVersion()));
		}

		return map;
	}

	public static class SXPElementJSONParser
		extends BaseJSONParser<SXPElement> {

		@Override
		protected SXPElement createDTO() {
			return new SXPElement();
		}

		@Override
		protected SXPElement[] createDTOArray(int size) {
			return new SXPElement[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "elementDefinition")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "fallbackDescription")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fallbackTitle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "hidden")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "readOnly")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "schemaVersion")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "title_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "userName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SXPElement sxpElement, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					sxpElement.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				if (jsonParserFieldValue != null) {
					sxpElement.setCreateDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					sxpElement.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					sxpElement.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "elementDefinition")) {
				if (jsonParserFieldValue != null) {
					sxpElement.setElementDefinition(
						ElementDefinitionSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					sxpElement.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "fallbackDescription")) {

				if (jsonParserFieldValue != null) {
					sxpElement.setFallbackDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fallbackTitle")) {
				if (jsonParserFieldValue != null) {
					sxpElement.setFallbackTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "hidden")) {
				if (jsonParserFieldValue != null) {
					sxpElement.setHidden((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					sxpElement.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				if (jsonParserFieldValue != null) {
					sxpElement.setModifiedDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "readOnly")) {
				if (jsonParserFieldValue != null) {
					sxpElement.setReadOnly((Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "schemaVersion")) {
				if (jsonParserFieldValue != null) {
					sxpElement.setSchemaVersion((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					sxpElement.setTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title_i18n")) {
				if (jsonParserFieldValue != null) {
					sxpElement.setTitle_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					sxpElement.setType(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userName")) {
				if (jsonParserFieldValue != null) {
					sxpElement.setUserName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				if (jsonParserFieldValue != null) {
					sxpElement.setVersion((String)jsonParserFieldValue);
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