/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerSection;
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
public class PreviewPortletDataHandlerSectionSerDes {

	public static PreviewPortletDataHandlerSection toDTO(String json) {
		PreviewPortletDataHandlerSectionJSONParser
			previewPortletDataHandlerSectionJSONParser =
				new PreviewPortletDataHandlerSectionJSONParser();

		return previewPortletDataHandlerSectionJSONParser.parseToDTO(json);
	}

	public static PreviewPortletDataHandlerSection[] toDTOs(String json) {
		PreviewPortletDataHandlerSectionJSONParser
			previewPortletDataHandlerSectionJSONParser =
				new PreviewPortletDataHandlerSectionJSONParser();

		return previewPortletDataHandlerSectionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PreviewPortletDataHandlerSection previewPortletDataHandlerSection) {

		if (previewPortletDataHandlerSection == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (previewPortletDataHandlerSection.getAdditionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"additionCount\": ");

			sb.append(previewPortletDataHandlerSection.getAdditionCount());
		}

		if (previewPortletDataHandlerSection.getDeletionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletionCount\": ");

			sb.append(previewPortletDataHandlerSection.getDeletionCount());
		}

		if (previewPortletDataHandlerSection.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(previewPortletDataHandlerSection.getLabel()));

			sb.append("\"");
		}

		if (previewPortletDataHandlerSection.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(previewPortletDataHandlerSection.getName()));

			sb.append("\"");
		}

		if (previewPortletDataHandlerSection.getPreviewPortletDataHandlers() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"previewPortletDataHandlers\": ");

			sb.append("[");

			for (int i = 0;
				 i < previewPortletDataHandlerSection.
					 getPreviewPortletDataHandlers().length;
				 i++) {

				sb.append(
					String.valueOf(
						previewPortletDataHandlerSection.
							getPreviewPortletDataHandlers()[i]));

				if ((i + 1) < previewPortletDataHandlerSection.
						getPreviewPortletDataHandlers().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PreviewPortletDataHandlerSectionJSONParser
			previewPortletDataHandlerSectionJSONParser =
				new PreviewPortletDataHandlerSectionJSONParser();

		return previewPortletDataHandlerSectionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PreviewPortletDataHandlerSection previewPortletDataHandlerSection) {

		if (previewPortletDataHandlerSection == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (previewPortletDataHandlerSection.getAdditionCount() == null) {
			map.put("additionCount", null);
		}
		else {
			map.put(
				"additionCount",
				String.valueOf(
					previewPortletDataHandlerSection.getAdditionCount()));
		}

		if (previewPortletDataHandlerSection.getDeletionCount() == null) {
			map.put("deletionCount", null);
		}
		else {
			map.put(
				"deletionCount",
				String.valueOf(
					previewPortletDataHandlerSection.getDeletionCount()));
		}

		if (previewPortletDataHandlerSection.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put(
				"label",
				String.valueOf(previewPortletDataHandlerSection.getLabel()));
		}

		if (previewPortletDataHandlerSection.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name",
				String.valueOf(previewPortletDataHandlerSection.getName()));
		}

		if (previewPortletDataHandlerSection.getPreviewPortletDataHandlers() ==
				null) {

			map.put("previewPortletDataHandlers", null);
		}
		else {
			map.put(
				"previewPortletDataHandlers",
				String.valueOf(
					previewPortletDataHandlerSection.
						getPreviewPortletDataHandlers()));
		}

		return map;
	}

	public static class PreviewPortletDataHandlerSectionJSONParser
		extends BaseJSONParser<PreviewPortletDataHandlerSection> {

		@Override
		protected PreviewPortletDataHandlerSection createDTO() {
			return new PreviewPortletDataHandlerSection();
		}

		@Override
		protected PreviewPortletDataHandlerSection[] createDTOArray(int size) {
			return new PreviewPortletDataHandlerSection[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "additionCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "deletionCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "previewPortletDataHandlers")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PreviewPortletDataHandlerSection previewPortletDataHandlerSection,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "additionCount")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerSection.setAdditionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deletionCount")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerSection.setDeletionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerSection.setLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerSection.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "previewPortletDataHandlers")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PreviewPortletDataHandler[]
						previewPortletDataHandlersArray =
							new PreviewPortletDataHandler
								[jsonParserFieldValues.length];

					for (int i = 0; i < previewPortletDataHandlersArray.length;
						 i++) {

						previewPortletDataHandlersArray[i] =
							PreviewPortletDataHandlerSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					previewPortletDataHandlerSection.
						setPreviewPortletDataHandlers(
							previewPortletDataHandlersArray);
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
// LIFERAY-REST-BUILDER-HASH:892653584