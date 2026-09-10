/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerBoolean;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerControl;
import com.liferay.exportimport.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public class PreviewPortletDataHandlerBooleanSerDes {

	public static PreviewPortletDataHandlerBoolean toDTO(String json) {
		PreviewPortletDataHandlerBooleanJSONParser
			previewPortletDataHandlerBooleanJSONParser =
				new PreviewPortletDataHandlerBooleanJSONParser();

		return previewPortletDataHandlerBooleanJSONParser.parseToDTO(json);
	}

	public static PreviewPortletDataHandlerBoolean[] toDTOs(String json) {
		PreviewPortletDataHandlerBooleanJSONParser
			previewPortletDataHandlerBooleanJSONParser =
				new PreviewPortletDataHandlerBooleanJSONParser();

		return previewPortletDataHandlerBooleanJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PreviewPortletDataHandlerBoolean previewPortletDataHandlerBoolean) {

		if (previewPortletDataHandlerBoolean == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (previewPortletDataHandlerBoolean.getAdditionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"additionCount\": ");

			sb.append(previewPortletDataHandlerBoolean.getAdditionCount());
		}

		if (previewPortletDataHandlerBoolean.getDefaultState() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultState\": ");

			sb.append(previewPortletDataHandlerBoolean.getDefaultState());
		}

		if (previewPortletDataHandlerBoolean.getDeletionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletionCount\": ");

			sb.append(previewPortletDataHandlerBoolean.getDeletionCount());
		}

		if (previewPortletDataHandlerBoolean.
				getPreviewPortletDataHandlerControls() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"previewPortletDataHandlerControls\": ");

			sb.append("[");

			for (int i = 0;
				 i < previewPortletDataHandlerBoolean.
					 getPreviewPortletDataHandlerControls().length;
				 i++) {

				sb.append(
					String.valueOf(
						previewPortletDataHandlerBoolean.
							getPreviewPortletDataHandlerControls()[i]));

				if ((i + 1) < previewPortletDataHandlerBoolean.
						getPreviewPortletDataHandlerControls().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (previewPortletDataHandlerBoolean.getDisabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"disabled\": ");

			sb.append(previewPortletDataHandlerBoolean.getDisabled());
		}

		if (previewPortletDataHandlerBoolean.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(previewPortletDataHandlerBoolean.getLabel()));

			sb.append("\"");
		}

		if (previewPortletDataHandlerBoolean.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(previewPortletDataHandlerBoolean.getName()));

			sb.append("\"");
		}

		if (previewPortletDataHandlerBoolean.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(previewPortletDataHandlerBoolean.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PreviewPortletDataHandlerBooleanJSONParser
			previewPortletDataHandlerBooleanJSONParser =
				new PreviewPortletDataHandlerBooleanJSONParser();

		return previewPortletDataHandlerBooleanJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PreviewPortletDataHandlerBoolean previewPortletDataHandlerBoolean) {

		if (previewPortletDataHandlerBoolean == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (previewPortletDataHandlerBoolean.getAdditionCount() == null) {
			map.put("additionCount", null);
		}
		else {
			map.put(
				"additionCount",
				String.valueOf(
					previewPortletDataHandlerBoolean.getAdditionCount()));
		}

		if (previewPortletDataHandlerBoolean.getDefaultState() == null) {
			map.put("defaultState", null);
		}
		else {
			map.put(
				"defaultState",
				String.valueOf(
					previewPortletDataHandlerBoolean.getDefaultState()));
		}

		if (previewPortletDataHandlerBoolean.getDeletionCount() == null) {
			map.put("deletionCount", null);
		}
		else {
			map.put(
				"deletionCount",
				String.valueOf(
					previewPortletDataHandlerBoolean.getDeletionCount()));
		}

		if (previewPortletDataHandlerBoolean.
				getPreviewPortletDataHandlerControls() == null) {

			map.put("previewPortletDataHandlerControls", null);
		}
		else {
			map.put(
				"previewPortletDataHandlerControls",
				String.valueOf(
					previewPortletDataHandlerBoolean.
						getPreviewPortletDataHandlerControls()));
		}

		if (previewPortletDataHandlerBoolean.getDisabled() == null) {
			map.put("disabled", null);
		}
		else {
			map.put(
				"disabled",
				String.valueOf(previewPortletDataHandlerBoolean.getDisabled()));
		}

		if (previewPortletDataHandlerBoolean.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put(
				"label",
				String.valueOf(previewPortletDataHandlerBoolean.getLabel()));
		}

		if (previewPortletDataHandlerBoolean.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name",
				String.valueOf(previewPortletDataHandlerBoolean.getName()));
		}

		if (previewPortletDataHandlerBoolean.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(previewPortletDataHandlerBoolean.getType()));
		}

		return map;
	}

	public static class PreviewPortletDataHandlerBooleanJSONParser
		extends BaseJSONParser<PreviewPortletDataHandlerBoolean> {

		@Override
		protected PreviewPortletDataHandlerBoolean createDTO() {
			return new PreviewPortletDataHandlerBoolean();
		}

		@Override
		protected PreviewPortletDataHandlerBoolean[] createDTOArray(int size) {
			return new PreviewPortletDataHandlerBoolean[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "additionCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultState")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "deletionCount")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"previewPortletDataHandlerControls")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "disabled")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PreviewPortletDataHandlerBoolean previewPortletDataHandlerBoolean,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "additionCount")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerBoolean.setAdditionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultState")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerBoolean.setDefaultState(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deletionCount")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerBoolean.setDeletionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"previewPortletDataHandlerControls")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PreviewPortletDataHandlerControl[]
						previewPortletDataHandlerControlsArray =
							new PreviewPortletDataHandlerControl
								[jsonParserFieldValues.length];

					for (int i = 0;
						 i < previewPortletDataHandlerControlsArray.length;
						 i++) {

						previewPortletDataHandlerControlsArray[i] =
							PreviewPortletDataHandlerControlSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					previewPortletDataHandlerBoolean.
						setPreviewPortletDataHandlerControls(
							previewPortletDataHandlerControlsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "disabled")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerBoolean.setDisabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerBoolean.setLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerBoolean.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerBoolean.setType(
						PreviewPortletDataHandlerBoolean.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:1291877025