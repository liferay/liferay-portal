/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.client.serdes.v1_0;

import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutBox;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayoutTab;
import com.liferay.object.admin.rest.client.json.BaseJSONParser;

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
public class ObjectLayoutTabSerDes {

	public static ObjectLayoutTab toDTO(String json) {
		ObjectLayoutTabJSONParser objectLayoutTabJSONParser =
			new ObjectLayoutTabJSONParser();

		return objectLayoutTabJSONParser.parseToDTO(json);
	}

	public static ObjectLayoutTab[] toDTOs(String json) {
		ObjectLayoutTabJSONParser objectLayoutTabJSONParser =
			new ObjectLayoutTabJSONParser();

		return objectLayoutTabJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ObjectLayoutTab objectLayoutTab) {
		if (objectLayoutTab == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (objectLayoutTab.getId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(objectLayoutTab.getId());
		}

		if (objectLayoutTab.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append(_toJSON(objectLayoutTab.getName()));
		}

		if (objectLayoutTab.getObjectLayoutBoxes() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectLayoutBoxes\": ");

			sb.append("[");

			for (int i = 0; i < objectLayoutTab.getObjectLayoutBoxes().length;
				 i++) {

				sb.append(
					String.valueOf(objectLayoutTab.getObjectLayoutBoxes()[i]));

				if ((i + 1) < objectLayoutTab.getObjectLayoutBoxes().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (objectLayoutTab.getObjectRelationshipExternalReferenceCode() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectRelationshipExternalReferenceCode\": ");

			sb.append("\"");

			sb.append(
				_escape(
					objectLayoutTab.
						getObjectRelationshipExternalReferenceCode()));

			sb.append("\"");
		}

		if (objectLayoutTab.getObjectRelationshipId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"objectRelationshipId\": ");

			sb.append(objectLayoutTab.getObjectRelationshipId());
		}

		if (objectLayoutTab.getPriority() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"priority\": ");

			sb.append(objectLayoutTab.getPriority());
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ObjectLayoutTabJSONParser objectLayoutTabJSONParser =
			new ObjectLayoutTabJSONParser();

		return objectLayoutTabJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ObjectLayoutTab objectLayoutTab) {
		if (objectLayoutTab == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (objectLayoutTab.getId() == null) {
			map.put("id", null);
		}
		else {
			map.put("id", String.valueOf(objectLayoutTab.getId()));
		}

		if (objectLayoutTab.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(objectLayoutTab.getName()));
		}

		if (objectLayoutTab.getObjectLayoutBoxes() == null) {
			map.put("objectLayoutBoxes", null);
		}
		else {
			map.put(
				"objectLayoutBoxes",
				String.valueOf(objectLayoutTab.getObjectLayoutBoxes()));
		}

		if (objectLayoutTab.getObjectRelationshipExternalReferenceCode() ==
				null) {

			map.put("objectRelationshipExternalReferenceCode", null);
		}
		else {
			map.put(
				"objectRelationshipExternalReferenceCode",
				String.valueOf(
					objectLayoutTab.
						getObjectRelationshipExternalReferenceCode()));
		}

		if (objectLayoutTab.getObjectRelationshipId() == null) {
			map.put("objectRelationshipId", null);
		}
		else {
			map.put(
				"objectRelationshipId",
				String.valueOf(objectLayoutTab.getObjectRelationshipId()));
		}

		if (objectLayoutTab.getPriority() == null) {
			map.put("priority", null);
		}
		else {
			map.put("priority", String.valueOf(objectLayoutTab.getPriority()));
		}

		return map;
	}

	public static class ObjectLayoutTabJSONParser
		extends BaseJSONParser<ObjectLayoutTab> {

		@Override
		protected ObjectLayoutTab createDTO() {
			return new ObjectLayoutTab();
		}

		@Override
		protected ObjectLayoutTab[] createDTOArray(int size) {
			return new ObjectLayoutTab[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "id")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return true;
			}
			else if (Objects.equals(jsonParserFieldName, "objectLayoutBoxes")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"objectRelationshipExternalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectRelationshipId")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ObjectLayoutTab objectLayoutTab, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "id")) {
				if (jsonParserFieldValue != null) {
					objectLayoutTab.setId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					objectLayoutTab.setName(
						(Map<String, String>)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "objectLayoutBoxes")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					ObjectLayoutBox[] objectLayoutBoxesArray =
						new ObjectLayoutBox[jsonParserFieldValues.length];

					for (int i = 0; i < objectLayoutBoxesArray.length; i++) {
						objectLayoutBoxesArray[i] = ObjectLayoutBoxSerDes.toDTO(
							(String)jsonParserFieldValues[i]);
					}

					objectLayoutTab.setObjectLayoutBoxes(
						objectLayoutBoxesArray);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"objectRelationshipExternalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					objectLayoutTab.setObjectRelationshipExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "objectRelationshipId")) {

				if (jsonParserFieldValue != null) {
					objectLayoutTab.setObjectRelationshipId(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "priority")) {
				if (jsonParserFieldValue != null) {
					objectLayoutTab.setPriority(
						Integer.valueOf((String)jsonParserFieldValue));
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