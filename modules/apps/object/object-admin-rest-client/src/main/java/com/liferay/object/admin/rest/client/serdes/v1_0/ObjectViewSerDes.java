/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.serdes.v1_0;

import com.liferay.object.admin.rest.client.dto.v1_0.ObjectView;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectViewColumn;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectViewFilterColumn;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectViewSortColumn;
import com.liferay.object.admin.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ObjectViewSerDes {

	public static ObjectView toDTO(String json) {
		ObjectViewJSONParser objectViewJSONParser = new ObjectViewJSONParser();

		return objectViewJSONParser.parseToDTO(json);
	}

	public static ObjectView[] toDTOs(String json) {
		ObjectViewJSONParser objectViewJSONParser = new ObjectViewJSONParser();

		return objectViewJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ObjectView objectView) {
		if (objectView == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (objectView.getActions() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"actions\": ");

			sb.append(_toJSON(objectView.getActions()));
		}

		if (objectView.getDateCreated() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateCreated\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(objectView.getDateCreated()));

			sb.append("\"");
		}

		if (objectView.getDateModified() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateModified\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(objectView.getDateModified()));

			sb.append("\"");
		}

		if (objectView.getDefaultObjectView() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultObjectView\": ");

			sb.append(objectView.getDefaultObjectView());
		}

		if (objectView.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(objectView.getId());
		}

		if (objectView.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(objectView.getName()));
		}

		if (objectView.getObjectDefinitionExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(objectView.getObjectDefinitionExternalReferenceCode()));

			sb.append("\"");
		}

		if (objectView.getObjectDefinitionId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectDefinitionId\": ");

			sb.append(objectView.getObjectDefinitionId());
		}

		if (objectView.getObjectViewColumns() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectViewColumns\": ");

			sb.append("[");

			for (int i = 0; i < objectView.getObjectViewColumns().length; i++) {
				sb.append(String.valueOf(objectView.getObjectViewColumns()[i]));

				if ((i + 1) < objectView.getObjectViewColumns().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (objectView.getObjectViewFilterColumns() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectViewFilterColumns\": ");

			sb.append("[");

			for (int i = 0; i < objectView.getObjectViewFilterColumns().length;
				 i++) {

				sb.append(
					String.valueOf(objectView.getObjectViewFilterColumns()[i]));

				if ((i + 1) < objectView.getObjectViewFilterColumns().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (objectView.getObjectViewSortColumns() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectViewSortColumns\": ");

			sb.append("[");

			for (int i = 0; i < objectView.getObjectViewSortColumns().length;
				 i++) {

				sb.append(
					String.valueOf(objectView.getObjectViewSortColumns()[i]));

				if ((i + 1) < objectView.getObjectViewSortColumns().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectViewJSONParser objectViewJSONParser = new ObjectViewJSONParser();

		return objectViewJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ObjectView objectView) {
		if (objectView == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (objectView.getActions() == null) {
			map.put("actions", null);
		}
		else {
			map.put("actions", String.valueOf(objectView.getActions()));
		}

		if (objectView.getDateCreated() == null) {
			map.put("dateCreated", null);
		}
		else {
			map.put(
				"dateCreated",
				liferayToJSONDateFormat.format(objectView.getDateCreated()));
		}

		if (objectView.getDateModified() == null) {
			map.put("dateModified", null);
		}
		else {
			map.put(
				"dateModified",
				liferayToJSONDateFormat.format(objectView.getDateModified()));
		}

		if (objectView.getDefaultObjectView() == null) {
			map.put("defaultObjectView", null);
		}
		else {
			map.put(
				"defaultObjectView",
				String.valueOf(objectView.getDefaultObjectView()));
		}

		if (objectView.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(objectView.getId()));
		}

		if (objectView.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(objectView.getName()));
		}

		if (objectView.getObjectDefinitionExternalReferenceCode() == null) {
			map.put("objectDefinitionExternalReferenceCode", null);
		}
		else {
			map.put(
				"objectDefinitionExternalReferenceCode",
				String.valueOf(
					objectView.getObjectDefinitionExternalReferenceCode()));
		}

		if (objectView.getObjectDefinitionId() == null) {
			map.put("objectDefinitionId", null);
		}
		else {
			map.put(
				"objectDefinitionId",
				String.valueOf(objectView.getObjectDefinitionId()));
		}

		if (objectView.getObjectViewColumns() == null) {
			map.put("objectViewColumns", null);
		}
		else {
			map.put(
				"objectViewColumns",
				String.valueOf(objectView.getObjectViewColumns()));
		}

		if (objectView.getObjectViewFilterColumns() == null) {
			map.put("objectViewFilterColumns", null);
		}
		else {
			map.put(
				"objectViewFilterColumns",
				String.valueOf(objectView.getObjectViewFilterColumns()));
		}

		if (objectView.getObjectViewSortColumns() == null) {
			map.put("objectViewSortColumns", null);
		}
		else {
			map.put(
				"objectViewSortColumns",
				String.valueOf(objectView.getObjectViewSortColumns()));
		}

		return map;
	}

	public static class ObjectViewJSONParser
		extends BaseJSONParser<ObjectView> {

		@Override
		protected ObjectView createDTO() {
			return new ObjectView();
		}

		@Override
		protected ObjectView[] createDTOArray(int size) {
			return new ObjectView[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "actions")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "defaultObjectView")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"objectDefinitionExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "objectViewColumns")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectViewFilterColumns")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectViewSortColumns")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectView objectView, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "actions")) {
				if (jsonParserFieldValue != null) {
					objectView.setActions(
						(Map<String, Map<String, String>>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateCreated")) {
				if (jsonParserFieldValue != null) {
					objectView.setDateCreated(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "dateModified")) {
				if (jsonParserFieldValue != null) {
					objectView.setDateModified(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "defaultObjectView")) {
				if (jsonParserFieldValue != null) {
					objectView.setDefaultObjectView(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					objectView.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					objectView.setName(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"objectDefinitionExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					objectView.setObjectDefinitionExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectDefinitionId")) {

				if (jsonParserFieldValue != null) {
					objectView.setObjectDefinitionId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "objectViewColumns")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ObjectViewColumn[] objectViewColumnsArray =
						new ObjectViewColumn[jsonParserFieldValues.length];

					for (int i = 0; i < objectViewColumnsArray.length; i++) {
						objectViewColumnsArray[i] =
							ObjectViewColumnSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					objectView.setObjectViewColumns(objectViewColumnsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectViewFilterColumns")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ObjectViewFilterColumn[] objectViewFilterColumnsArray =
						new ObjectViewFilterColumn
							[jsonParserFieldValues.length];

					for (int i = 0; i < objectViewFilterColumnsArray.length;
						 i++) {

						objectViewFilterColumnsArray[i] =
							ObjectViewFilterColumnSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					objectView.setObjectViewFilterColumns(
						objectViewFilterColumnsArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectViewSortColumns")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ObjectViewSortColumn[] objectViewSortColumnsArray =
						new ObjectViewSortColumn[jsonParserFieldValues.length];

					for (int i = 0; i < objectViewSortColumnsArray.length;
						 i++) {

						objectViewSortColumnsArray[i] =
							ObjectViewSortColumnSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					objectView.setObjectViewSortColumns(
						objectViewSortColumnsArray);
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