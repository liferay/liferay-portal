/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.ItemValue;
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
public class ItemValueSerDes {

	public static ItemValue toDTO(String json) {
		ItemValueJSONParser itemValueJSONParser = new ItemValueJSONParser();

		return itemValueJSONParser.parseToDTO(json);
	}

	public static ItemValue[] toDTOs(String json) {
		ItemValueJSONParser itemValueJSONParser = new ItemValueJSONParser();

		return itemValueJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ItemValue itemValue) {
		if (itemValue == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (itemValue.getItemExternalReference() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemExternalReference\": ");

			sb.append(String.valueOf(itemValue.getItemExternalReference()));
		}

		if (itemValue.getTemplateReference() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"templateReference\": ");

			sb.append(String.valueOf(itemValue.getTemplateReference()));
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ItemValueJSONParser itemValueJSONParser = new ItemValueJSONParser();

		return itemValueJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ItemValue itemValue) {
		if (itemValue == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (itemValue.getItemExternalReference() == null) {
			map.put("itemExternalReference", null);
		}
		else {
			map.put(
				"itemExternalReference",
				String.valueOf(itemValue.getItemExternalReference()));
		}

		if (itemValue.getTemplateReference() == null) {
			map.put("templateReference", null);
		}
		else {
			map.put(
				"templateReference",
				String.valueOf(itemValue.getTemplateReference()));
		}

		return map;
	}

	public static class ItemValueJSONParser extends BaseJSONParser<ItemValue> {

		@Override
		protected ItemValue createDTO() {
			return new ItemValue();
		}

		@Override
		protected ItemValue[] createDTOArray(int size) {
			return new ItemValue[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "itemExternalReference")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "templateReference")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ItemValue itemValue, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "itemExternalReference")) {
				if (jsonParserFieldValue != null) {
					itemValue.setItemExternalReference(
						ItemExternalReferenceSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "templateReference")) {
				if (jsonParserFieldValue != null) {
					itemValue.setTemplateReference(
						TemplateReferenceSerDes.toDTO(
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
// LIFERAY-REST-BUILDER-HASH:-1386789412