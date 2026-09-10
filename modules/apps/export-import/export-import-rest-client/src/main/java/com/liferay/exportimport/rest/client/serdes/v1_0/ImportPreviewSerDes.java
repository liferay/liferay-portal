/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.ImportPreview;
import com.liferay.exportimport.rest.client.dto.v1_0.PreviewPortletDataHandlerSection;
import com.liferay.exportimport.rest.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

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
public class ImportPreviewSerDes {

	public static ImportPreview toDTO(String json) {
		ImportPreviewJSONParser importPreviewJSONParser =
			new ImportPreviewJSONParser();

		return importPreviewJSONParser.parseToDTO(json);
	}

	public static ImportPreview[] toDTOs(String json) {
		ImportPreviewJSONParser importPreviewJSONParser =
			new ImportPreviewJSONParser();

		return importPreviewJSONParser.parseToDTOs(json);
	}

	public static String toJSON(ImportPreview importPreview) {
		if (importPreview == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (importPreview.getAdditionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"additionCount\": ");

			sb.append(importPreview.getAdditionCount());
		}

		if (importPreview.getAuthor() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"author\": ");

			sb.append("\"");

			sb.append(_escape(importPreview.getAuthor()));

			sb.append("\"");
		}

		if (importPreview.getDeletionCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"deletionCount\": ");

			sb.append(importPreview.getDeletionCount());
		}

		if (importPreview.getExportDate() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"exportDate\": ");

			sb.append("\"");

			sb.append(
				liferayToJSONDateFormat.format(importPreview.getExportDate()));

			sb.append("\"");
		}

		if (importPreview.getFileName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileName\": ");

			sb.append("\"");

			sb.append(_escape(importPreview.getFileName()));

			sb.append("\"");
		}

		if (importPreview.getFileSize() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fileSize\": ");

			sb.append(importPreview.getFileSize());
		}

		if (importPreview.getPreviewPortletDataHandlerSections() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"previewPortletDataHandlerSections\": ");

			sb.append("[");

			for (int i = 0;
				 i <
					 importPreview.
						 getPreviewPortletDataHandlerSections().length;
				 i++) {

				sb.append(
					String.valueOf(
						importPreview.getPreviewPortletDataHandlerSections()
							[i]));

				if ((i + 1) < importPreview.
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
		ImportPreviewJSONParser importPreviewJSONParser =
			new ImportPreviewJSONParser();

		return importPreviewJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(ImportPreview importPreview) {
		if (importPreview == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssXX");

		if (importPreview.getAdditionCount() == null) {
			map.put("additionCount", null);
		}
		else {
			map.put(
				"additionCount",
				String.valueOf(importPreview.getAdditionCount()));
		}

		if (importPreview.getAuthor() == null) {
			map.put("author", null);
		}
		else {
			map.put("author", String.valueOf(importPreview.getAuthor()));
		}

		if (importPreview.getDeletionCount() == null) {
			map.put("deletionCount", null);
		}
		else {
			map.put(
				"deletionCount",
				String.valueOf(importPreview.getDeletionCount()));
		}

		if (importPreview.getExportDate() == null) {
			map.put("exportDate", null);
		}
		else {
			map.put(
				"exportDate",
				liferayToJSONDateFormat.format(importPreview.getExportDate()));
		}

		if (importPreview.getFileName() == null) {
			map.put("fileName", null);
		}
		else {
			map.put("fileName", String.valueOf(importPreview.getFileName()));
		}

		if (importPreview.getFileSize() == null) {
			map.put("fileSize", null);
		}
		else {
			map.put("fileSize", String.valueOf(importPreview.getFileSize()));
		}

		if (importPreview.getPreviewPortletDataHandlerSections() == null) {
			map.put("previewPortletDataHandlerSections", null);
		}
		else {
			map.put(
				"previewPortletDataHandlerSections",
				String.valueOf(
					importPreview.getPreviewPortletDataHandlerSections()));
		}

		return map;
	}

	public static class ImportPreviewJSONParser
		extends BaseJSONParser<ImportPreview> {

		@Override
		protected ImportPreview createDTO() {
			return new ImportPreview();
		}

		@Override
		protected ImportPreview[] createDTOArray(int size) {
			return new ImportPreview[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "additionCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "author")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "deletionCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "exportDate")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fileName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fileSize")) {
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
			ImportPreview importPreview, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "additionCount")) {
				if (jsonParserFieldValue != null) {
					importPreview.setAdditionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "author")) {
				if (jsonParserFieldValue != null) {
					importPreview.setAuthor((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "deletionCount")) {
				if (jsonParserFieldValue != null) {
					importPreview.setDeletionCount(
						Long.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "exportDate")) {
				if (jsonParserFieldValue != null) {
					importPreview.setExportDate(
						toDate((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fileName")) {
				if (jsonParserFieldValue != null) {
					importPreview.setFileName((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fileSize")) {
				if (jsonParserFieldValue != null) {
					importPreview.setFileSize(
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

					importPreview.setPreviewPortletDataHandlerSections(
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
// LIFERAY-REST-BUILDER-HASH:-1742208831