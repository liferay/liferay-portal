/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.client.serdes.v1_0;

import com.liferay.scim.rest.client.dto.v1_0.Operation;
import com.liferay.scim.rest.client.dto.v1_0.PatchOp;
import com.liferay.scim.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
public class PatchOpSerDes {

	public static PatchOp toDTO(String json) {
		PatchOpJSONParser patchOpJSONParser = new PatchOpJSONParser();

		return patchOpJSONParser.parseToDTO(json);
	}

	public static PatchOp[] toDTOs(String json) {
		PatchOpJSONParser patchOpJSONParser = new PatchOpJSONParser();

		return patchOpJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PatchOp patchOp) {
		if (patchOp == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (patchOp.getOperations() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"Operations\": ");

			sb.append("[");

			for (int i = 0; i < patchOp.getOperations().length; i++) {
				sb.append(String.valueOf(patchOp.getOperations()[i]));

				if ((i + 1) < patchOp.getOperations().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (patchOp.getSchemas() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"schemas\": ");

			sb.append("[");

			for (int i = 0; i < patchOp.getSchemas().length; i++) {
				sb.append(_toJSON(patchOp.getSchemas()[i]));

				if ((i + 1) < patchOp.getSchemas().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PatchOpJSONParser patchOpJSONParser = new PatchOpJSONParser();

		return patchOpJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PatchOp patchOp) {
		if (patchOp == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (patchOp.getOperations() == null) {
			map.put("Operations", null);
		}
		else {
			map.put("Operations", String.valueOf(patchOp.getOperations()));
		}

		if (patchOp.getSchemas() == null) {
			map.put("schemas", null);
		}
		else {
			map.put("schemas", String.valueOf(patchOp.getSchemas()));
		}

		return map;
	}

	public static class PatchOpJSONParser extends BaseJSONParser<PatchOp> {

		@Override
		protected PatchOp createDTO() {
			return new PatchOp();
		}

		@Override
		protected PatchOp[] createDTOArray(int size) {
			return new PatchOp[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "Operations")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "schemas")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PatchOp patchOp, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "Operations")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					Operation[] OperationsArray =
						new Operation[jsonParserFieldValues.length];

					for (int i = 0; i < OperationsArray.length; i++) {
						OperationsArray[i] = OperationSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					patchOp.setOperations(OperationsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "schemas")) {
				if (jsonParserFieldValue != null) {
					patchOp.setSchemas(
						toStrings((Object[])jsonParserFieldValue));
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