/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.FormFragmentInstancePageElementDefinition;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
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
public class FormFragmentInstancePageElementDefinitionSerDes {

	public static FormFragmentInstancePageElementDefinition toDTO(String json) {
		FormFragmentInstancePageElementDefinitionJSONParser
			formFragmentInstancePageElementDefinitionJSONParser =
				new FormFragmentInstancePageElementDefinitionJSONParser();

		return formFragmentInstancePageElementDefinitionJSONParser.parseToDTO(
			json);
	}

	public static FormFragmentInstancePageElementDefinition[] toDTOs(
		String json) {

		FormFragmentInstancePageElementDefinitionJSONParser
			formFragmentInstancePageElementDefinitionJSONParser =
				new FormFragmentInstancePageElementDefinitionJSONParser();

		return formFragmentInstancePageElementDefinitionJSONParser.parseToDTOs(
			json);
	}

	public static String toJSON(
		FormFragmentInstancePageElementDefinition
			formFragmentInstancePageElementDefinition) {

		if (formFragmentInstancePageElementDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (formFragmentInstancePageElementDefinition.getFieldKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fieldKey\": ");

			sb.append("\"");

			sb.append(
				_escape(
					formFragmentInstancePageElementDefinition.getFieldKey()));

			sb.append("\"");
		}

		if (formFragmentInstancePageElementDefinition.getFragmentInstance() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentInstance\": ");

			sb.append(
				String.valueOf(
					formFragmentInstancePageElementDefinition.
						getFragmentInstance()));
		}

		if (formFragmentInstancePageElementDefinition.getHelpText_i18n() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"helpText_i18n\": ");

			sb.append(
				_toJSON(
					formFragmentInstancePageElementDefinition.
						getHelpText_i18n()));
		}

		if (formFragmentInstancePageElementDefinition.getLabel_i18n() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label_i18n\": ");

			sb.append(
				_toJSON(
					formFragmentInstancePageElementDefinition.getLabel_i18n()));
		}

		if (formFragmentInstancePageElementDefinition.getMarkAsRequired() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"markAsRequired\": ");

			sb.append(
				formFragmentInstancePageElementDefinition.getMarkAsRequired());
		}

		if (formFragmentInstancePageElementDefinition.getReadOnlyField() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnlyField\": ");

			sb.append(
				formFragmentInstancePageElementDefinition.getReadOnlyField());
		}

		if (formFragmentInstancePageElementDefinition.getShowHelpText() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showHelpText\": ");

			sb.append(
				formFragmentInstancePageElementDefinition.getShowHelpText());
		}

		if (formFragmentInstancePageElementDefinition.getShowLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showLabel\": ");

			sb.append(formFragmentInstancePageElementDefinition.getShowLabel());
		}

		if (formFragmentInstancePageElementDefinition.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(formFragmentInstancePageElementDefinition.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FormFragmentInstancePageElementDefinitionJSONParser
			formFragmentInstancePageElementDefinitionJSONParser =
				new FormFragmentInstancePageElementDefinitionJSONParser();

		return formFragmentInstancePageElementDefinitionJSONParser.parseToMap(
			json);
	}

	public static Map<String, String> toMap(
		FormFragmentInstancePageElementDefinition
			formFragmentInstancePageElementDefinition) {

		if (formFragmentInstancePageElementDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (formFragmentInstancePageElementDefinition.getFieldKey() == null) {
			map.put("fieldKey", null);
		}
		else {
			map.put(
				"fieldKey",
				String.valueOf(
					formFragmentInstancePageElementDefinition.getFieldKey()));
		}

		if (formFragmentInstancePageElementDefinition.getFragmentInstance() ==
				null) {

			map.put("fragmentInstance", null);
		}
		else {
			map.put(
				"fragmentInstance",
				String.valueOf(
					formFragmentInstancePageElementDefinition.
						getFragmentInstance()));
		}

		if (formFragmentInstancePageElementDefinition.getHelpText_i18n() ==
				null) {

			map.put("helpText_i18n", null);
		}
		else {
			map.put(
				"helpText_i18n",
				String.valueOf(
					formFragmentInstancePageElementDefinition.
						getHelpText_i18n()));
		}

		if (formFragmentInstancePageElementDefinition.getLabel_i18n() == null) {
			map.put("label_i18n", null);
		}
		else {
			map.put(
				"label_i18n",
				String.valueOf(
					formFragmentInstancePageElementDefinition.getLabel_i18n()));
		}

		if (formFragmentInstancePageElementDefinition.getMarkAsRequired() ==
				null) {

			map.put("markAsRequired", null);
		}
		else {
			map.put(
				"markAsRequired",
				String.valueOf(
					formFragmentInstancePageElementDefinition.
						getMarkAsRequired()));
		}

		if (formFragmentInstancePageElementDefinition.getReadOnlyField() ==
				null) {

			map.put("readOnlyField", null);
		}
		else {
			map.put(
				"readOnlyField",
				String.valueOf(
					formFragmentInstancePageElementDefinition.
						getReadOnlyField()));
		}

		if (formFragmentInstancePageElementDefinition.getShowHelpText() ==
				null) {

			map.put("showHelpText", null);
		}
		else {
			map.put(
				"showHelpText",
				String.valueOf(
					formFragmentInstancePageElementDefinition.
						getShowHelpText()));
		}

		if (formFragmentInstancePageElementDefinition.getShowLabel() == null) {
			map.put("showLabel", null);
		}
		else {
			map.put(
				"showLabel",
				String.valueOf(
					formFragmentInstancePageElementDefinition.getShowLabel()));
		}

		if (formFragmentInstancePageElementDefinition.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(
					formFragmentInstancePageElementDefinition.getType()));
		}

		return map;
	}

	public static class FormFragmentInstancePageElementDefinitionJSONParser
		extends BaseJSONParser<FormFragmentInstancePageElementDefinition> {

		@Override
		protected FormFragmentInstancePageElementDefinition createDTO() {
			return new FormFragmentInstancePageElementDefinition();
		}

		@Override
		protected FormFragmentInstancePageElementDefinition[] createDTOArray(
			int size) {

			return new FormFragmentInstancePageElementDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "fieldKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentInstance")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "helpText_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "label_i18n")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "markAsRequired")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "readOnlyField")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "showHelpText")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "showLabel")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FormFragmentInstancePageElementDefinition
				formFragmentInstancePageElementDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "fieldKey")) {
				if (jsonParserFieldValue != null) {
					formFragmentInstancePageElementDefinition.setFieldKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentInstance")) {
				if (jsonParserFieldValue != null) {
					formFragmentInstancePageElementDefinition.
						setFragmentInstance(
							FragmentInstanceSerDes.toDTO(
								(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "helpText_i18n")) {
				if (jsonParserFieldValue != null) {
					formFragmentInstancePageElementDefinition.setHelpText_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label_i18n")) {
				if (jsonParserFieldValue != null) {
					formFragmentInstancePageElementDefinition.setLabel_i18n(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "markAsRequired")) {
				if (jsonParserFieldValue != null) {
					formFragmentInstancePageElementDefinition.setMarkAsRequired(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "readOnlyField")) {
				if (jsonParserFieldValue != null) {
					formFragmentInstancePageElementDefinition.setReadOnlyField(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "showHelpText")) {
				if (jsonParserFieldValue != null) {
					formFragmentInstancePageElementDefinition.setShowHelpText(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "showLabel")) {
				if (jsonParserFieldValue != null) {
					formFragmentInstancePageElementDefinition.setShowLabel(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					formFragmentInstancePageElementDefinition.setType(
						FormFragmentInstancePageElementDefinition.Type.create(
							(String)jsonParserFieldValue));
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
// LIFERAY-REST-BUILDER-HASH:-875529166