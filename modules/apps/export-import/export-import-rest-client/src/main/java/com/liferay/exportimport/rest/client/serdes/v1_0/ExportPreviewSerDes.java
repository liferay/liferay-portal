/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.ExportPreview;
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
public class ExportPreviewSerDes {

	public static ExportPreview toDTO(String json) {
		ExportPreviewJSONParser exportPreviewJSONParser =
			new ExportPreviewJSONParser();

		return exportPreviewJSONParser.parseToDTO(json);
	}

	public static ExportPreview[] toDTOs(String json) {
		ExportPreviewJSONParser exportPreviewJSONParser =
			new ExportPreviewJSONParser();

		return exportPreviewJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ExportPreview exportPreview) {
		if (exportPreview == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (exportPreview.getAdditionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"additionCount\": ");

			sb.append(exportPreview.getAdditionCount());
		}

		if (exportPreview.getDeletionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletionCount\": ");

			sb.append(exportPreview.getDeletionCount());
		}

		if (exportPreview.getPreviewPortletDataHandlerSections() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"previewPortletDataHandlerSections\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 exportPreview.
						 getPreviewPortletDataHandlerSections().length;
				 i++) {

				sb.append(
					String.valueOf(
						exportPreview.getPreviewPortletDataHandlerSections()
							[i]));

				if ((i + 1) < exportPreview.
						getPreviewPortletDataHandlerSections().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		ExportPreviewJSONParser exportPreviewJSONParser =
			new ExportPreviewJSONParser();

		return exportPreviewJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ExportPreview exportPreview) {
		if (exportPreview == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (exportPreview.getAdditionCount() == null) {
			map.put("additionCount", null);
		}
		else {
			map.put(
				"additionCount",
				String.valueOf(exportPreview.getAdditionCount()));
		}

		if (exportPreview.getDeletionCount() == null) {
			map.put("deletionCount", null);
		}
		else {
			map.put(
				"deletionCount",
				String.valueOf(exportPreview.getDeletionCount()));
		}

		if (exportPreview.getPreviewPortletDataHandlerSections() == null) {
			map.put("previewPortletDataHandlerSections", null);
		}
		else {
			map.put(
				"previewPortletDataHandlerSections",
				String.valueOf(
					exportPreview.getPreviewPortletDataHandlerSections()));
		}

		return map;
	}

	public static class ExportPreviewJSONParser
		extends BaseJSONParser<ExportPreview> {

		@Override
		protected ExportPreview createDTO() {
			return new ExportPreview();
		}

		@Override
		protected ExportPreview[] createDTOArray(int size) {
			return new ExportPreview[size];
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
						"previewPortletDataHandlerSections")) {

				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			ExportPreview exportPreview, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "additionCount")) {
				if (jsonParserFieldValue != null) {
					exportPreview.setAdditionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deletionCount")) {
				if (jsonParserFieldValue != null) {
					exportPreview.setDeletionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(
						jsonParserFieldName,
						"previewPortletDataHandlerSections")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					PreviewPortletDataHandlerSection[]
						previewPortletDataHandlerSectionsArray =
							new PreviewPortletDataHandlerSection
								[jsonParserFieldValues.length];

					for (int i = 0;
						 i < previewPortletDataHandlerSectionsArray.length;
						 i++) {

						previewPortletDataHandlerSectionsArray[i] =
							PreviewPortletDataHandlerSectionSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					exportPreview.setPreviewPortletDataHandlerSections(
						previewPortletDataHandlerSectionsArray);
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
// LIFERAY-REST-BUILDER-HASH:2057316559