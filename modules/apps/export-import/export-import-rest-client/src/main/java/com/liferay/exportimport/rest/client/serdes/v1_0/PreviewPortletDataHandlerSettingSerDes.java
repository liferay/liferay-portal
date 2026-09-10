/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerControl;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerSetting;
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
public class PreviewPortletDataHandlerSettingSerDes {

	public static PreviewPortletDataHandlerSetting toDTO(String json) {
		PreviewPortletDataHandlerSettingJSONParser
			previewPortletDataHandlerSettingJSONParser =
				new PreviewPortletDataHandlerSettingJSONParser();

		return previewPortletDataHandlerSettingJSONParser.parseToDTO(json);
	}

	public static PreviewPortletDataHandlerSetting[] toDTOs(String json) {
		PreviewPortletDataHandlerSettingJSONParser
			previewPortletDataHandlerSettingJSONParser =
				new PreviewPortletDataHandlerSettingJSONParser();

		return previewPortletDataHandlerSettingJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PreviewPortletDataHandlerSetting previewPortletDataHandlerSetting) {

		if (previewPortletDataHandlerSetting == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (previewPortletDataHandlerSetting.getDefaultState() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultState\": ");

			sb.append(previewPortletDataHandlerSetting.getDefaultState());
		}

		if (previewPortletDataHandlerSetting.
				getPreviewPortletDataHandlerControls() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"previewPortletDataHandlerControls\": ");

			sb.append("[");

			for (int i = 0;
				 i < previewPortletDataHandlerSetting.
					 getPreviewPortletDataHandlerControls().length;
				 i++) {

				sb.append(
					String.valueOf(
						previewPortletDataHandlerSetting.
							getPreviewPortletDataHandlerControls()[i]));

				if ((i + 1) < previewPortletDataHandlerSetting.
						getPreviewPortletDataHandlerControls().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (previewPortletDataHandlerSetting.getDisabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"disabled\": ");

			sb.append(previewPortletDataHandlerSetting.getDisabled());
		}

		if (previewPortletDataHandlerSetting.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(previewPortletDataHandlerSetting.getLabel()));

			sb.append("\"");
		}

		if (previewPortletDataHandlerSetting.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(previewPortletDataHandlerSetting.getName()));

			sb.append("\"");
		}

		if (previewPortletDataHandlerSetting.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(previewPortletDataHandlerSetting.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PreviewPortletDataHandlerSettingJSONParser
			previewPortletDataHandlerSettingJSONParser =
				new PreviewPortletDataHandlerSettingJSONParser();

		return previewPortletDataHandlerSettingJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PreviewPortletDataHandlerSetting previewPortletDataHandlerSetting) {

		if (previewPortletDataHandlerSetting == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (previewPortletDataHandlerSetting.getDefaultState() == null) {
			map.put("defaultState", null);
		}
		else {
			map.put(
				"defaultState",
				String.valueOf(
					previewPortletDataHandlerSetting.getDefaultState()));
		}

		if (previewPortletDataHandlerSetting.
				getPreviewPortletDataHandlerControls() == null) {

			map.put("previewPortletDataHandlerControls", null);
		}
		else {
			map.put(
				"previewPortletDataHandlerControls",
				String.valueOf(
					previewPortletDataHandlerSetting.
						getPreviewPortletDataHandlerControls()));
		}

		if (previewPortletDataHandlerSetting.getDisabled() == null) {
			map.put("disabled", null);
		}
		else {
			map.put(
				"disabled",
				String.valueOf(previewPortletDataHandlerSetting.getDisabled()));
		}

		if (previewPortletDataHandlerSetting.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put(
				"label",
				String.valueOf(previewPortletDataHandlerSetting.getLabel()));
		}

		if (previewPortletDataHandlerSetting.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name",
				String.valueOf(previewPortletDataHandlerSetting.getName()));
		}

		if (previewPortletDataHandlerSetting.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(previewPortletDataHandlerSetting.getType()));
		}

		return map;
	}

	public static class PreviewPortletDataHandlerSettingJSONParser
		extends BaseJSONParser<PreviewPortletDataHandlerSetting> {

		@Override
		protected PreviewPortletDataHandlerSetting createDTO() {
			return new PreviewPortletDataHandlerSetting();
		}

		@Override
		protected PreviewPortletDataHandlerSetting[] createDTOArray(int size) {
			return new PreviewPortletDataHandlerSetting[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "defaultState")) {
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
			PreviewPortletDataHandlerSetting previewPortletDataHandlerSetting,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "defaultState")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerSetting.setDefaultState(
						(Boolean)jsonParserFieldValue);
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

					previewPortletDataHandlerSetting.
						setPreviewPortletDataHandlerControls(
							previewPortletDataHandlerControlsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "disabled")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerSetting.setDisabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerSetting.setLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerSetting.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerSetting.setType(
						PreviewPortletDataHandlerSetting.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:721979239