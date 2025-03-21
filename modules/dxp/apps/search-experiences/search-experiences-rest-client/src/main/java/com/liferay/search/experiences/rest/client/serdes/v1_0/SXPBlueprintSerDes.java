/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.serdes.v1_0;

import com.liferay.search.experiences.rest.client.dto.v1_0.ElementInstance;
import com.liferay.search.experiences.rest.client.dto.v1_0.SXPBlueprint;
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
public class SXPBlueprintSerDes {

	public static SXPBlueprint toDTO(String json) {
		SXPBlueprintJSONParser sxpBlueprintJSONParser =
			new SXPBlueprintJSONParser();

		return sxpBlueprintJSONParser.parseToDTO(json);
	}

	public static SXPBlueprint[] toDTOs(String json) {
		SXPBlueprintJSONParser sxpBlueprintJSONParser =
			new SXPBlueprintJSONParser();

		return sxpBlueprintJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SXPBlueprint sxpBlueprint) {
		if (sxpBlueprint == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (sxpBlueprint.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(sxpBlueprint.getActions()));
		}

		if (sxpBlueprint.getConfiguration() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"configuration\": ");

			sb.append(String.valueOf(sxpBlueprint.getConfiguration()));
		}

		if (sxpBlueprint.getCreateDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"createDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(sxpBlueprint.getCreateDate()));

			sb.append("\"");
		}

		if (sxpBlueprint.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(sxpBlueprint.getDescription()));

			sb.append("\"");
		}

		if (sxpBlueprint.getDescription_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description_i18n\": ");

			sb.append(_toJSON(sxpBlueprint.getDescription_i18n()));
		}

		if (sxpBlueprint.getElementInstances() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"elementInstances\": ");

			sb.append("[");

			for (int i = 0; i < sxpBlueprint.getElementInstances().length;
				 i++) {

				sb.append(
					String.valueOf(sxpBlueprint.getElementInstances()[i]));

				if ((i + 1) < sxpBlueprint.getElementInstances().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (sxpBlueprint.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(sxpBlueprint.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (sxpBlueprint.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(sxpBlueprint.getId());
		}

		if (sxpBlueprint.getModifiedDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"modifiedDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(sxpBlueprint.getModifiedDate()));

			sb.append("\"");
		}

		if (sxpBlueprint.getSchemaVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schemaVersion\": ");

			sb.append("\"");

			sb.append(_escape(sxpBlueprint.getSchemaVersion()));

			sb.append("\"");
		}

		if (sxpBlueprint.getTitle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title\": ");

			sb.append("\"");

			sb.append(_escape(sxpBlueprint.getTitle()));

			sb.append("\"");
		}

		if (sxpBlueprint.getTitle_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"title_i18n\": ");

			sb.append(_toJSON(sxpBlueprint.getTitle_i18n()));
		}

		if (sxpBlueprint.getUserName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userName\": ");

			sb.append("\"");

			sb.append(_escape(sxpBlueprint.getUserName()));

			sb.append("\"");
		}

		if (sxpBlueprint.getVersion() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"version\": ");

			sb.append("\"");

			sb.append(_escape(sxpBlueprint.getVersion()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SXPBlueprintJSONParser sxpBlueprintJSONParser =
			new SXPBlueprintJSONParser();

		return sxpBlueprintJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SXPBlueprint sxpBlueprint) {
		if (sxpBlueprint == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (sxpBlueprint.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(sxpBlueprint.getActions()));
		}

		if (sxpBlueprint.getConfiguration() == null) {
			map.put("configuration", null);
		}
		else {
			map.put(
				"configuration",
				String.valueOf(sxpBlueprint.getConfiguration()));
		}

		if (sxpBlueprint.getCreateDate() == null) {
			map.put("createDate", null);
		}
		else {
			map.put(
				"createDate",
				liferayToJSONDateFormat.format(sxpBlueprint.getCreateDate()));
		}

		if (sxpBlueprint.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description", String.valueOf(sxpBlueprint.getDescription()));
		}

		if (sxpBlueprint.getDescription_i18n() == null) {
			map.put("description_i18n", null);
		}
		else {
			map.put(
				"description_i18n",
				String.valueOf(sxpBlueprint.getDescription_i18n()));
		}

		if (sxpBlueprint.getElementInstances() == null) {
			map.put("elementInstances", null);
		}
		else {
			map.put(
				"elementInstances",
				String.valueOf(sxpBlueprint.getElementInstances()));
		}

		if (sxpBlueprint.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(sxpBlueprint.getExternalReferenceCode()));
		}

		if (sxpBlueprint.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(sxpBlueprint.getId()));
		}

		if (sxpBlueprint.getModifiedDate() == null) {
			map.put("modifiedDate", null);
		}
		else {
			map.put(
				"modifiedDate",
				liferayToJSONDateFormat.format(sxpBlueprint.getModifiedDate()));
		}

		if (sxpBlueprint.getSchemaVersion() == null) {
			map.put("schemaVersion", null);
		}
		else {
			map.put(
				"schemaVersion",
				String.valueOf(sxpBlueprint.getSchemaVersion()));
		}

		if (sxpBlueprint.getTitle() == null) {
			map.put("title", null);
		}
		else {
			map.put("title", String.valueOf(sxpBlueprint.getTitle()));
		}

		if (sxpBlueprint.getTitle_i18n() == null) {
			map.put("title_i18n", null);
		}
		else {
			map.put("title_i18n", String.valueOf(sxpBlueprint.getTitle_i18n()));
		}

		if (sxpBlueprint.getUserName() == null) {
			map.put("userName", null);
		}
		else {
			map.put("userName", String.valueOf(sxpBlueprint.getUserName()));
		}

		if (sxpBlueprint.getVersion() == null) {
			map.put("version", null);
		}
		else {
			map.put("version", String.valueOf(sxpBlueprint.getVersion()));
		}

		return map;
	}

	public static class SXPBlueprintJSONParser
		extends BaseJSONParser<SXPBlueprint> {

		@Override
		protected SXPBlueprint createDTO() {
			return new SXPBlueprint();
		}

		@Override
		protected SXPBlueprint[] createDTOArray(int size) {
			return new SXPBlueprint[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "configuration")) {
				return false;
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
			else if (Objects.equals(jsonParserFieldName, "elementInstances")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
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
			SXPBlueprint sxpBlueprint, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					sxpBlueprint.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "configuration")) {
				if (jsonParserFieldValue != null) {
					sxpBlueprint.setConfiguration(
						ConfigurationSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "createDate")) {
				if (jsonParserFieldValue != null) {
					sxpBlueprint.setCreateDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					sxpBlueprint.setDescription((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description_i18n")) {
				if (jsonParserFieldValue != null) {
					sxpBlueprint.setDescription_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "elementInstances")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ElementInstance[] elementInstancesArray =
						new ElementInstance[jsonParserFieldValues.length];

					for (int i = 0; i < elementInstancesArray.length; i++) {
						elementInstancesArray[i] = ElementInstanceSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					sxpBlueprint.setElementInstances(elementInstancesArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					sxpBlueprint.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					sxpBlueprint.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "modifiedDate")) {
				if (jsonParserFieldValue != null) {
					sxpBlueprint.setModifiedDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "schemaVersion")) {
				if (jsonParserFieldValue != null) {
					sxpBlueprint.setSchemaVersion((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title")) {
				if (jsonParserFieldValue != null) {
					sxpBlueprint.setTitle((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "title_i18n")) {
				if (jsonParserFieldValue != null) {
					sxpBlueprint.setTitle_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "userName")) {
				if (jsonParserFieldValue != null) {
					sxpBlueprint.setUserName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "version")) {
				if (jsonParserFieldValue != null) {
					sxpBlueprint.setVersion((String)jsonParserFieldValue);
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