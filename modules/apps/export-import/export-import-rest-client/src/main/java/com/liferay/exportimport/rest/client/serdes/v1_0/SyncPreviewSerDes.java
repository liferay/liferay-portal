/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.PortletDataHandler;
import com.liferay.exportimport.rest.client.dto.v1_0.SyncPreview;
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
public class SyncPreviewSerDes {

	public static SyncPreview toDTO(String json) {
		SyncPreviewJSONParser syncPreviewJSONParser =
			new SyncPreviewJSONParser();

		return syncPreviewJSONParser.parseToDTO(json);
	}

	public static SyncPreview[] toDTOs(String json) {
		SyncPreviewJSONParser syncPreviewJSONParser =
			new SyncPreviewJSONParser();

		return syncPreviewJSONParser.parseToDTOs(json);
	}

	public static String toJSON(SyncPreview syncPreview) {
		if (syncPreview == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (syncPreview.getAdditionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"additionCount\": ");

			sb.append(syncPreview.getAdditionCount());
		}

		if (syncPreview.getDeletionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletionCount\": ");

			sb.append(syncPreview.getDeletionCount());
		}

		if (syncPreview.getPortletDataHandlers() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"portletDataHandlers\": ");

			sb.append("[");

			for (int i = 0; i < syncPreview.getPortletDataHandlers().length;
				 i++) {

				sb.append(
					String.valueOf(syncPreview.getPortletDataHandlers()[i]));

				if ((i + 1) < syncPreview.getPortletDataHandlers().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		SyncPreviewJSONParser syncPreviewJSONParser =
			new SyncPreviewJSONParser();

		return syncPreviewJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(SyncPreview syncPreview) {
		if (syncPreview == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (syncPreview.getAdditionCount() == null) {
			map.put("additionCount", null);
		}
		else {
			map.put(
				"additionCount",
				String.valueOf(syncPreview.getAdditionCount()));
		}

		if (syncPreview.getDeletionCount() == null) {
			map.put("deletionCount", null);
		}
		else {
			map.put(
				"deletionCount",
				String.valueOf(syncPreview.getDeletionCount()));
		}

		if (syncPreview.getPortletDataHandlers() == null) {
			map.put("portletDataHandlers", null);
		}
		else {
			map.put(
				"portletDataHandlers",
				String.valueOf(syncPreview.getPortletDataHandlers()));
		}

		return map;
	}

	public static class SyncPreviewJSONParser
		extends BaseJSONParser<SyncPreview> {

		@Override
		protected SyncPreview createDTO() {
			return new SyncPreview();
		}

		@Override
		protected SyncPreview[] createDTOArray(int size) {
			return new SyncPreview[size];
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
						jsonParserFieldName, "portletDataHandlers")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			SyncPreview syncPreview, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "additionCount")) {
				if (jsonParserFieldValue != null) {
					syncPreview.setAdditionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deletionCount")) {
				if (jsonParserFieldValue != null) {
					syncPreview.setDeletionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "portletDataHandlers")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PortletDataHandler[] portletDataHandlersArray =
						new PortletDataHandler[jsonParserFieldValues.length];

					for (int i = 0; i < portletDataHandlersArray.length; i++) {
						portletDataHandlersArray[i] =
							PortletDataHandlerSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					syncPreview.setPortletDataHandlers(
						portletDataHandlersArray);
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
// LIFERAY-REST-BUILDER-HASH:768516500