/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandlerTreeSelection;
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
public class PortletDataHandlerTreeSelectionSerDes {

	public static PortletDataHandlerTreeSelection toDTO(String json) {
		PortletDataHandlerTreeSelectionJSONParser
			portletDataHandlerTreeSelectionJSONParser =
				new PortletDataHandlerTreeSelectionJSONParser();

		return portletDataHandlerTreeSelectionJSONParser.parseToDTO(json);
	}

	public static PortletDataHandlerTreeSelection[] toDTOs(String json) {
		PortletDataHandlerTreeSelectionJSONParser
			portletDataHandlerTreeSelectionJSONParser =
				new PortletDataHandlerTreeSelectionJSONParser();

		return portletDataHandlerTreeSelectionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PortletDataHandlerTreeSelection portletDataHandlerTreeSelection) {

		if (portletDataHandlerTreeSelection == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (portletDataHandlerTreeSelection.getAdditionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"additionCount\": ");

			sb.append(portletDataHandlerTreeSelection.getAdditionCount());
		}

		if (portletDataHandlerTreeSelection.getDeletionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletionCount\": ");

			sb.append(portletDataHandlerTreeSelection.getDeletionCount());
		}

		if (portletDataHandlerTreeSelection.
				getExcludedItemExternalReferenceCodes() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"excludedItemExternalReferenceCodes\": ");

			sb.append("[");

			for (int i = 0;
				 i < portletDataHandlerTreeSelection.
					 getExcludedItemExternalReferenceCodes().length;
				 i++) {

				sb.append(
					_toJSON(
						portletDataHandlerTreeSelection.
							getExcludedItemExternalReferenceCodes()[i]));

				if ((i + 1) < portletDataHandlerTreeSelection.
						getExcludedItemExternalReferenceCodes().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (portletDataHandlerTreeSelection.
				getExcludedSubtreeExternalReferenceCodes() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"excludedSubtreeExternalReferenceCodes\": ");

			sb.append("[");

			for (int i = 0;
				 i < portletDataHandlerTreeSelection.
					 getExcludedSubtreeExternalReferenceCodes().length;
				 i++) {

				sb.append(
					_toJSON(
						portletDataHandlerTreeSelection.
							getExcludedSubtreeExternalReferenceCodes()[i]));

				if ((i + 1) < portletDataHandlerTreeSelection.
						getExcludedSubtreeExternalReferenceCodes().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (portletDataHandlerTreeSelection.getItemExternalReferenceCodes() !=
				null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"itemExternalReferenceCodes\": ");

			sb.append("[");

			for (int i = 0;
				 i < portletDataHandlerTreeSelection.
					 getItemExternalReferenceCodes().length;
				 i++) {

				sb.append(
					_toJSON(
						portletDataHandlerTreeSelection.
							getItemExternalReferenceCodes()[i]));

				if ((i + 1) < portletDataHandlerTreeSelection.
						getItemExternalReferenceCodes().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (portletDataHandlerTreeSelection.getSelectAll() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selectAll\": ");

			sb.append(portletDataHandlerTreeSelection.getSelectAll());
		}

		if (portletDataHandlerTreeSelection.getSelected() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"selected\": ");

			sb.append(portletDataHandlerTreeSelection.getSelected());
		}

		if (portletDataHandlerTreeSelection.
				getSubtreeExternalReferenceCodes() != null) {

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"subtreeExternalReferenceCodes\": ");

			sb.append("[");

			for (int i = 0;
				 i < portletDataHandlerTreeSelection.
					 getSubtreeExternalReferenceCodes().length;
				 i++) {

				sb.append(
					_toJSON(
						portletDataHandlerTreeSelection.
							getSubtreeExternalReferenceCodes()[i]));

				if ((i + 1) < portletDataHandlerTreeSelection.
						getSubtreeExternalReferenceCodes().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (portletDataHandlerTreeSelection.getLabel() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(portletDataHandlerTreeSelection.getLabel()));

			sb.append("\"");
		}

		if (portletDataHandlerTreeSelection.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(portletDataHandlerTreeSelection.getName()));

			sb.append("\"");
		}

		if (portletDataHandlerTreeSelection.getType() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");
			sb.append(portletDataHandlerTreeSelection.getType());
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PortletDataHandlerTreeSelectionJSONParser
			portletDataHandlerTreeSelectionJSONParser =
				new PortletDataHandlerTreeSelectionJSONParser();

		return portletDataHandlerTreeSelectionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PortletDataHandlerTreeSelection portletDataHandlerTreeSelection) {

		if (portletDataHandlerTreeSelection == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (portletDataHandlerTreeSelection.getAdditionCount() == null) {
			map.put("additionCount", null);
		}
		else {
			map.put(
				"additionCount",
				String.valueOf(
					portletDataHandlerTreeSelection.getAdditionCount()));
		}

		if (portletDataHandlerTreeSelection.getDeletionCount() == null) {
			map.put("deletionCount", null);
		}
		else {
			map.put(
				"deletionCount",
				String.valueOf(
					portletDataHandlerTreeSelection.getDeletionCount()));
		}

		if (portletDataHandlerTreeSelection.
				getExcludedItemExternalReferenceCodes() == null) {

			map.put("excludedItemExternalReferenceCodes", null);
		}
		else {
			map.put(
				"excludedItemExternalReferenceCodes",
				String.valueOf(
					portletDataHandlerTreeSelection.
						getExcludedItemExternalReferenceCodes()));
		}

		if (portletDataHandlerTreeSelection.
				getExcludedSubtreeExternalReferenceCodes() == null) {

			map.put("excludedSubtreeExternalReferenceCodes", null);
		}
		else {
			map.put(
				"excludedSubtreeExternalReferenceCodes",
				String.valueOf(
					portletDataHandlerTreeSelection.
						getExcludedSubtreeExternalReferenceCodes()));
		}

		if (portletDataHandlerTreeSelection.getItemExternalReferenceCodes() ==
				null) {

			map.put("itemExternalReferenceCodes", null);
		}
		else {
			map.put(
				"itemExternalReferenceCodes",
				String.valueOf(
					portletDataHandlerTreeSelection.
						getItemExternalReferenceCodes()));
		}

		if (portletDataHandlerTreeSelection.getSelectAll() == null) {
			map.put("selectAll", null);
		}
		else {
			map.put(
				"selectAll",
				String.valueOf(portletDataHandlerTreeSelection.getSelectAll()));
		}

		if (portletDataHandlerTreeSelection.getSelected() == null) {
			map.put("selected", null);
		}
		else {
			map.put(
				"selected",
				String.valueOf(portletDataHandlerTreeSelection.getSelected()));
		}

		if (portletDataHandlerTreeSelection.
				getSubtreeExternalReferenceCodes() == null) {

			map.put("subtreeExternalReferenceCodes", null);
		}
		else {
			map.put(
				"subtreeExternalReferenceCodes",
				String.valueOf(
					portletDataHandlerTreeSelection.
						getSubtreeExternalReferenceCodes()));
		}

		if (portletDataHandlerTreeSelection.getLabel() == null) {
			map.put("label", null);
		}
		else {
			map.put(
				"label",
				String.valueOf(portletDataHandlerTreeSelection.getLabel()));
		}

		if (portletDataHandlerTreeSelection.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put(
				"name",
				String.valueOf(portletDataHandlerTreeSelection.getName()));
		}

		if (portletDataHandlerTreeSelection.getType() == null) {
			map.put("type", null);
		}
		else {
			map.put(
				"type",
				String.valueOf(portletDataHandlerTreeSelection.getType()));
		}

		return map;
	}

	public static class PortletDataHandlerTreeSelectionJSONParser
		extends BaseJSONParser<PortletDataHandlerTreeSelection> {

		@Override
		protected PortletDataHandlerTreeSelection createDTO() {
			return new PortletDataHandlerTreeSelection();
		}

		@Override
		protected PortletDataHandlerTreeSelection[] createDTOArray(int size) {
			return new PortletDataHandlerTreeSelection[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "additionCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "deletionCount")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"excludedItemExternalReferenceCodes")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"excludedSubtreeExternalReferenceCodes")) {

				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "itemExternalReferenceCodes")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "selectAll")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "selected")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "subtreeExternalReferenceCodes")) {

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
			PortletDataHandlerTreeSelection portletDataHandlerTreeSelection,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "additionCount")) {
				if (jsonParserFieldValue != null) {
					portletDataHandlerTreeSelection.setAdditionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deletionCount")) {
				if (jsonParserFieldValue != null) {
					portletDataHandlerTreeSelection.setDeletionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"excludedItemExternalReferenceCodes")) {

				if (jsonParserFieldValue != null) {
					portletDataHandlerTreeSelection.
						setExcludedItemExternalReferenceCodes(
							toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"excludedSubtreeExternalReferenceCodes")) {

				if (jsonParserFieldValue != null) {
					portletDataHandlerTreeSelection.
						setExcludedSubtreeExternalReferenceCodes(
							toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "itemExternalReferenceCodes")) {

				if (jsonParserFieldValue != null) {
					portletDataHandlerTreeSelection.
						setItemExternalReferenceCodes(
							toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selectAll")) {
				if (jsonParserFieldValue != null) {
					portletDataHandlerTreeSelection.setSelectAll(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "selected")) {
				if (jsonParserFieldValue != null) {
					portletDataHandlerTreeSelection.setSelected(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "subtreeExternalReferenceCodes")) {

				if (jsonParserFieldValue != null) {
					portletDataHandlerTreeSelection.
						setSubtreeExternalReferenceCodes(
							toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "label")) {
				if (jsonParserFieldValue != null) {
					portletDataHandlerTreeSelection.setLabel(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					portletDataHandlerTreeSelection.setName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "type")) {
				if (jsonParserFieldValue != null) {
					portletDataHandlerTreeSelection.setType(
						PortletDataHandlerTreeSelection.Type.create(
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
// LIFERAY-REST-BUILDER-HASH:-1291551381