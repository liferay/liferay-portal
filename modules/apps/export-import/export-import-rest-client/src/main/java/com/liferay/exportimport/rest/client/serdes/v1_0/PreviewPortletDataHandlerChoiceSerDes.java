/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.Choice;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerChoice;
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
public class PreviewPortletDataHandlerChoiceSerDes {

	public static PreviewPortletDataHandlerChoice toDTO(String json) {
		PreviewPortletDataHandlerChoiceJSONParser
			previewPortletDataHandlerChoiceJSONParser =
				new PreviewPortletDataHandlerChoiceJSONParser();

		return previewPortletDataHandlerChoiceJSONParser.parseToDTO(json);
	}

	public static PreviewPortletDataHandlerChoice[] toDTOs(String json) {
		PreviewPortletDataHandlerChoiceJSONParser
			previewPortletDataHandlerChoiceJSONParser =
				new PreviewPortletDataHandlerChoiceJSONParser();

		return previewPortletDataHandlerChoiceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PreviewPortletDataHandlerChoice previewPortletDataHandlerChoice) {

		if (previewPortletDataHandlerChoice == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (previewPortletDataHandlerChoice.getChoices() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"choices\": ");

			sb.append("[");

			for (int i = 0;
				 i < previewPortletDataHandlerChoice.getChoices().length; i++) {

				sb.append(
					String.valueOf(
						previewPortletDataHandlerChoice.getChoices()[i]));

				if ((i + 1) <
						previewPortletDataHandlerChoice.getChoices().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (previewPortletDataHandlerChoice.getDefaultChoice() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultChoice\": ");

			sb.append("\"");

			sb.append(
				_escape(previewPortletDataHandlerChoice.getDefaultChoice()));

			sb.append("\"");
		}

		if (previewPortletDataHandlerChoice.getDisabled() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"disabled\": ");

			sb.append(previewPortletDataHandlerChoice.getDisabled());
		}

		if (previewPortletDataHandlerChoice.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(previewPortletDataHandlerChoice.getLabel()));

			sb.append("\"");
		}

		if (previewPortletDataHandlerChoice.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(previewPortletDataHandlerChoice.getName()));

			sb.append("\"");
		}

		if (previewPortletDataHandlerChoice.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(previewPortletDataHandlerChoice.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PreviewPortletDataHandlerChoiceJSONParser
			previewPortletDataHandlerChoiceJSONParser =
				new PreviewPortletDataHandlerChoiceJSONParser();

		return previewPortletDataHandlerChoiceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PreviewPortletDataHandlerChoice previewPortletDataHandlerChoice) {

		if (previewPortletDataHandlerChoice == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (previewPortletDataHandlerChoice.getChoices() == null) {
			map.put("choices", null);
		}
		else {
			map.put(
				"choices",
				String.valueOf(previewPortletDataHandlerChoice.getChoices()));
		}

		if (previewPortletDataHandlerChoice.getDefaultChoice() == null) {
			map.put("defaultChoice", null);
		}
		else {
			map.put(
				"defaultChoice",
				String.valueOf(
					previewPortletDataHandlerChoice.getDefaultChoice()));
		}

		if (previewPortletDataHandlerChoice.getDisabled() == null) {
			map.put("disabled", null);
		}
		else {
			map.put(
				"disabled",
				String.valueOf(previewPortletDataHandlerChoice.getDisabled()));
		}

		if (previewPortletDataHandlerChoice.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put(
				"label",
				String.valueOf(previewPortletDataHandlerChoice.getLabel()));
		}

		if (previewPortletDataHandlerChoice.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name",
				String.valueOf(previewPortletDataHandlerChoice.getName()));
		}

		if (previewPortletDataHandlerChoice.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(previewPortletDataHandlerChoice.getType()));
		}

		return map;
	}

	public static class PreviewPortletDataHandlerChoiceJSONParser
		extends BaseJSONParser<PreviewPortletDataHandlerChoice> {

		@Override
		protected PreviewPortletDataHandlerChoice createDTO() {
			return new PreviewPortletDataHandlerChoice();
		}

		@Override
		protected PreviewPortletDataHandlerChoice[] createDTOArray(int size) {
			return new PreviewPortletDataHandlerChoice[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "choices")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultChoice")) {
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
			PreviewPortletDataHandlerChoice previewPortletDataHandlerChoice,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "choices")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Choice[] choicesArray =
						new Choice[jsonParserFieldValues.length];

					for (int i = 0; i < choicesArray.length; i++) {
						choicesArray[i] = ChoiceSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					previewPortletDataHandlerChoice.setChoices(choicesArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultChoice")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerChoice.setDefaultChoice(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "disabled")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerChoice.setDisabled(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerChoice.setLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerChoice.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					previewPortletDataHandlerChoice.setType(
						PreviewPortletDataHandlerChoice.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-1329095045