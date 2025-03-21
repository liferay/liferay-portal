/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.FragmentFieldAction;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class FragmentFieldActionSerDes {

	public static FragmentFieldAction toDTO(String json) {
		FragmentFieldActionJSONParser fragmentFieldActionJSONParser =
			new FragmentFieldActionJSONParser();

		return fragmentFieldActionJSONParser.parseToDTO(json);
	}

	public static FragmentFieldAction[] toDTOs(String json) {
		FragmentFieldActionJSONParser fragmentFieldActionJSONParser =
			new FragmentFieldActionJSONParser();

		return fragmentFieldActionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(FragmentFieldAction fragmentFieldAction) {
		if (fragmentFieldAction == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (fragmentFieldAction.getAction() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"action\": ");

			if (fragmentFieldAction.getAction() instanceof String) {
				sb.append("\"");
				sb.append((String)fragmentFieldAction.getAction());
				sb.append("\"");
			}
			else {
				sb.append(fragmentFieldAction.getAction());
			}
		}

		if (fragmentFieldAction.getOnError() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"onError\": ");

			sb.append(String.valueOf(fragmentFieldAction.getOnError()));
		}

		if (fragmentFieldAction.getOnSuccess() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"onSuccess\": ");

			sb.append(String.valueOf(fragmentFieldAction.getOnSuccess()));
		}

		if (fragmentFieldAction.getText() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"text\": ");

			if (fragmentFieldAction.getText() instanceof String) {
				sb.append("\"");
				sb.append((String)fragmentFieldAction.getText());
				sb.append("\"");
			}
			else {
				sb.append(fragmentFieldAction.getText());
			}
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		FragmentFieldActionJSONParser fragmentFieldActionJSONParser =
			new FragmentFieldActionJSONParser();

		return fragmentFieldActionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		FragmentFieldAction fragmentFieldAction) {

		if (fragmentFieldAction == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (fragmentFieldAction.getAction() == null) {
			map.put("action", null);
		}
		else {
			map.put("action", String.valueOf(fragmentFieldAction.getAction()));
		}

		if (fragmentFieldAction.getOnError() == null) {
			map.put("onError", null);
		}
		else {
			map.put(
				"onError", String.valueOf(fragmentFieldAction.getOnError()));
		}

		if (fragmentFieldAction.getOnSuccess() == null) {
			map.put("onSuccess", null);
		}
		else {
			map.put(
				"onSuccess",
				String.valueOf(fragmentFieldAction.getOnSuccess()));
		}

		if (fragmentFieldAction.getText() == null) {
			map.put("text", null);
		}
		else {
			map.put("text", String.valueOf(fragmentFieldAction.getText()));
		}

		return map;
	}

	public static class FragmentFieldActionJSONParser
		extends BaseJSONParser<FragmentFieldAction> {

		@Override
		protected FragmentFieldAction createDTO() {
			return new FragmentFieldAction();
		}

		@Override
		protected FragmentFieldAction[] createDTOArray(int size) {
			return new FragmentFieldAction[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "action")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "onError")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "onSuccess")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "text")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			FragmentFieldAction fragmentFieldAction, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "action")) {
				if (jsonParserFieldValue != null) {
					fragmentFieldAction.setAction((Object)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "onError")) {
				if (jsonParserFieldValue != null) {
					fragmentFieldAction.setOnError(
						ActionExecutionResultSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "onSuccess")) {
				if (jsonParserFieldValue != null) {
					fragmentFieldAction.setOnSuccess(
						ActionExecutionResultSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "text")) {
				if (jsonParserFieldValue != null) {
					fragmentFieldAction.setText((Object)jsonParserFieldValue);
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