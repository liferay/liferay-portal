/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandler;
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
public class PreviewPortletDataHandlerSerDes {

	public static PreviewPortletDataHandler toDTO(String json) {
		PreviewPortletDataHandlerJSONParser
			previewPortletDataHandlerJSONParser =
				new PreviewPortletDataHandlerJSONParser();

		return previewPortletDataHandlerJSONParser.parseToDTO(json);
	}

	public static PreviewPortletDataHandler[] toDTOs(String json) {
		PreviewPortletDataHandlerJSONParser
			previewPortletDataHandlerJSONParser =
				new PreviewPortletDataHandlerJSONParser();

		return previewPortletDataHandlerJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PreviewPortletDataHandler previewPortletDataHandler) {

		if (previewPortletDataHandler == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (previewPortletDataHandler.getAdditionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"additionCount\": ");

			sb.append(previewPortletDataHandler.getAdditionCount());
		}

		if (previewPortletDataHandler.getDeletionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletionCount\": ");

			sb.append(previewPortletDataHandler.getDeletionCount());
		}

		if (previewPortletDataHandler.getDescription() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"description\": ");

			sb.append("\"");

			sb.append(_escape(previewPortletDataHandler.getDescription()));

			sb.append("\"");
		}

		if (previewPortletDataHandler.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(previewPortletDataHandler.getLabel()));

			sb.append("\"");
		}

		if (previewPortletDataHandler.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(previewPortletDataHandler.getName()));

			sb.append("\"");
		}

		if (previewPortletDataHandler.getPreviewPortletDataHandlerControls() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"previewPortletDataHandlerControls\": ");

			sb.append("[");

			for (int i = 0;
				 i < previewPortletDataHandler.
					 getPreviewPortletDataHandlerControls().length;
				 i++) {

				sb.append(
					String.valueOf(
						previewPortletDataHandler.
							getPreviewPortletDataHandlerControls()[i]));

				if ((i + 1) < previewPortletDataHandler.
						getPreviewPortletDataHandlerControls().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (previewPortletDataHandler.getTag() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tag\": ");

			sb.append("\"");

			sb.append(_escape(previewPortletDataHandler.getTag()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PreviewPortletDataHandlerJSONParser
			previewPortletDataHandlerJSONParser =
				new PreviewPortletDataHandlerJSONParser();

		return previewPortletDataHandlerJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PreviewPortletDataHandler previewPortletDataHandler) {

		if (previewPortletDataHandler == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (previewPortletDataHandler.getAdditionCount() == null) {
			map.put("additionCount", null);
		}
		else {
			map.put(
				"additionCount",
				String.valueOf(previewPortletDataHandler.getAdditionCount()));
		}

		if (previewPortletDataHandler.getDeletionCount() == null) {
			map.put("deletionCount", null);
		}
		else {
			map.put(
				"deletionCount",
				String.valueOf(previewPortletDataHandler.getDeletionCount()));
		}

		if (previewPortletDataHandler.getDescription() == null) {
			map.put("description", null);
		}
		else {
			map.put(
				"description",
				String.valueOf(previewPortletDataHandler.getDescription()));
		}

		if (previewPortletDataHandler.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put(
				"label", String.valueOf(previewPortletDataHandler.getLabel()));
		}

		if (previewPortletDataHandler.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name", String.valueOf(previewPortletDataHandler.getName()));
		}

		if (previewPortletDataHandler.getPreviewPortletDataHandlerControls() ==
				null) {

			map.put("previewPortletDataHandlerControls", null);
		}
		else {
			map.put(
				"previewPortletDataHandlerControls",
				String.valueOf(
					previewPortletDataHandler.
						getPreviewPortletDataHandlerControls()));
		}

		if (previewPortletDataHandler.getTag() == null) {
			map.put("tag", null);
		}
		else {
			map.put("tag", String.valueOf(previewPortletDataHandler.getTag()));
		}

		return map;
	}

	public static class PreviewPortletDataHandlerJSONParser
		extends BaseJSONParser<PreviewPortletDataHandler> {

		@Override
		protected PreviewPortletDataHandler createDTO() {
			return new PreviewPortletDataHandler();
		}

		@Override
		protected PreviewPortletDataHandler[] createDTOArray(int size) {
			return new PreviewPortletDataHandler[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "additionCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "deletionCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"previewPortletDataHandlerControls")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "tag")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PreviewPortletDataHandler previewPortletDataHandler,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "additionCount")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandler.setAdditionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deletionCount")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandler.setDeletionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "description")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandler.setDescription(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandler.setLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandler.setName(
						(String)jsonParserFieldValue);
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

					previewPortletDataHandler.
						setPreviewPortletDataHandlerControls(
							previewPortletDataHandlerControlsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "tag")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandler.setTag(
						(String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:1269518921