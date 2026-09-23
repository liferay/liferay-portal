/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.rest.client.serdes.v1_0;

import com.liferay.exportimport.rest.client.dto.v1_0.PreviewSite;
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
public class PreviewSiteSerDes {

	public static PreviewSite toDTO(String json) {
		PreviewSiteJSONParser previewSiteJSONParser =
			new PreviewSiteJSONParser();

		return previewSiteJSONParser.parseToDTO(json);
	}

	public static PreviewSite[] toDTOs(String json) {
		PreviewSiteJSONParser previewSiteJSONParser =
			new PreviewSiteJSONParser();

		return previewSiteJSONParser.parseToDTOs(json);
	}

	public static String toJSON(PreviewSite previewSite) {
		if (previewSite == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (previewSite.getChildSitesCount() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"childSitesCount\": ");

			sb.append(previewSite.getChildSitesCount());
		}

		if (previewSite.getDescriptiveName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"descriptiveName\": ");

			sb.append("\"");

			sb.append(_escape(previewSite.getDescriptiveName()));

			sb.append("\"");
		}

		if (previewSite.getExistsInInstance() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"existsInInstance\": ");

			sb.append(previewSite.getExistsInInstance());
		}

		if (previewSite.getExternalReferenceCode() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(previewSite.getExternalReferenceCode()));

			sb.append("\"");
		}

		if (previewSite.getPath() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"path\": ");

			sb.append("\"");

			sb.append(_escape(previewSite.getPath()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PreviewSiteJSONParser previewSiteJSONParser =
			new PreviewSiteJSONParser();

		return previewSiteJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(PreviewSite previewSite) {
		if (previewSite == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (previewSite.getChildSitesCount() == null) {
			map.put("childSitesCount", null);
		}
		else {
			map.put(
				"childSitesCount",
				String.valueOf(previewSite.getChildSitesCount()));
		}

		if (previewSite.getDescriptiveName() == null) {
			map.put("descriptiveName", null);
		}
		else {
			map.put(
				"descriptiveName",
				String.valueOf(previewSite.getDescriptiveName()));
		}

		if (previewSite.getExistsInInstance() == null) {
			map.put("existsInInstance", null);
		}
		else {
			map.put(
				"existsInInstance",
				String.valueOf(previewSite.getExistsInInstance()));
		}

		if (previewSite.getExternalReferenceCode() == null) {
			map.put("externalReferenceCode", null);
		}
		else {
			map.put(
				"externalReferenceCode",
				String.valueOf(previewSite.getExternalReferenceCode()));
		}

		if (previewSite.getPath() == null) {
			map.put("path", null);
		}
		else {
			map.put("path", String.valueOf(previewSite.getPath()));
		}

		return map;
	}

	public static class PreviewSiteJSONParser
		extends BaseJSONParser<PreviewSite> {

		@Override
		protected PreviewSite createDTO() {
			return new PreviewSite();
		}

		@Override
		protected PreviewSite[] createDTOArray(int size) {
			return new PreviewSite[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "childSitesCount")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "descriptiveName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "existsInInstance")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "path")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PreviewSite previewSite, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "childSitesCount")) {
				if (jsonParserFieldValue != null) {
					previewSite.setChildSitesCount(
						Integer.valueOf((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "descriptiveName")) {
				if (jsonParserFieldValue != null) {
					previewSite.setDescriptiveName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "existsInInstance")) {
				if (jsonParserFieldValue != null) {
					previewSite.setExistsInInstance(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "externalReferenceCode")) {

				if (jsonParserFieldValue != null) {
					previewSite.setExternalReferenceCode(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "path")) {
				if (jsonParserFieldValue != null) {
					previewSite.setPath((String)jsonParserFieldValue);
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
// LIFERAY-REST-BUILDER-HASH:-449639162