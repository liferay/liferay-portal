/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerBoolean;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerChoice;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerControl;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerSetting;
import com.liferay.exportimport.rest.client.dto.v1_0.Type;
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
public class PreviewPortletDataHandlerControlSerDes {

	public static PreviewPortletDataHandlerControl toDTO(String json) {
		PreviewPortletDataHandlerControlJSONParser
			previewPortletDataHandlerControlJSONParser =
				new PreviewPortletDataHandlerControlJSONParser();

		return previewPortletDataHandlerControlJSONParser.parseToDTO(json);
	}

	public static PreviewPortletDataHandlerControl[] toDTOs(String json) {
		PreviewPortletDataHandlerControlJSONParser
			previewPortletDataHandlerControlJSONParser =
				new PreviewPortletDataHandlerControlJSONParser();

		return previewPortletDataHandlerControlJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PreviewPortletDataHandlerControl previewPortletDataHandlerControl) {

		if (previewPortletDataHandlerControl == null) {
			return "null";
		}

		PreviewPortletDataHandlerControl.Type type =
			previewPortletDataHandlerControl.getType();

		if (type != null) {
			String typeString = type.toString();

			if (typeString.equals("Boolean")) {
				return PreviewPortletDataHandlerBooleanSerDes.toJSON(
					(PreviewPortletDataHandlerBoolean)
						previewPortletDataHandlerControl);
			}

			if (typeString.equals("Choice")) {
				return PreviewPortletDataHandlerChoiceSerDes.toJSON(
					(PreviewPortletDataHandlerChoice)
						previewPortletDataHandlerControl);
			}

			if (typeString.equals("Setting")) {
				return PreviewPortletDataHandlerSettingSerDes.toJSON(
					(PreviewPortletDataHandlerSetting)
						previewPortletDataHandlerControl);
			}

			throw new IllegalArgumentException("Unknown type " + typeString);
		}
		else {
			throw new IllegalArgumentException("Missing type parameter");
		}
	}

	public static Map<String, Object> toMap(String json) {
		PreviewPortletDataHandlerControlJSONParser
			previewPortletDataHandlerControlJSONParser =
				new PreviewPortletDataHandlerControlJSONParser();

		return previewPortletDataHandlerControlJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PreviewPortletDataHandlerControl previewPortletDataHandlerControl) {

		if (previewPortletDataHandlerControl == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (previewPortletDataHandlerControl.getDisabled() == null) {
			map.put("disabled", null);
		}
		else {
			map.put(
				"disabled",
				String.valueOf(previewPortletDataHandlerControl.getDisabled()));
		}

		if (previewPortletDataHandlerControl.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put(
				"label",
				String.valueOf(previewPortletDataHandlerControl.getLabel()));
		}

		if (previewPortletDataHandlerControl.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name",
				String.valueOf(previewPortletDataHandlerControl.getName()));
		}

		if (previewPortletDataHandlerControl.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(previewPortletDataHandlerControl.getType()));
		}

		return map;
	}

	public static class PreviewPortletDataHandlerControlJSONParser
		extends BaseJSONParser<PreviewPortletDataHandlerControl> {

		@Override
		protected PreviewPortletDataHandlerControl createDTO() {
			return null;
		}

		@Override
		protected PreviewPortletDataHandlerControl[] createDTOArray(int size) {
			return new PreviewPortletDataHandlerControl[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "disabled")) {
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
		public PreviewPortletDataHandlerControl parseToDTO(String json) {
			Map<String, Object> jsonMap = parseToMap(json);

			Object type = jsonMap.get("type");

			if (type != null) {
				String typeString = type.toString();

				if (typeString.equals("Boolean")) {
					return PreviewPortletDataHandlerBoolean.toDTO(json);
				}

				if (typeString.equals("Choice")) {
					return PreviewPortletDataHandlerChoice.toDTO(json);
				}

				if (typeString.equals("Setting")) {
					return PreviewPortletDataHandlerSetting.toDTO(json);
				}

				throw new IllegalArgumentException(
					"Unknown type " + typeString);
			}
			else {
				throw new IllegalArgumentException("Missing type parameter");
			}
		}

		@Override
		protected void setField(
			PreviewPortletDataHandlerControl previewPortletDataHandlerControl,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "disabled")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerControl.setDisabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerControl.setLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerControl.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerControl.setType(
						PreviewPortletDataHandlerControl.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-1225805541