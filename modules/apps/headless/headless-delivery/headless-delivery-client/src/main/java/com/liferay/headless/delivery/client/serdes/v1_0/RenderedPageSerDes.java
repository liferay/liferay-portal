/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.client.serdes.v1_0;

import com.liferay.headless.delivery.client.dto.v1_0.RenderedPage;
import com.liferay.headless.delivery.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class RenderedPageSerDes {

	public static RenderedPage toDTO(String json) {
		RenderedPageJSONParser renderedPageJSONParser =
			new RenderedPageJSONParser();

		return renderedPageJSONParser.parseToDTO(json);
	}

	public static RenderedPage[] toDTOs(String json) {
		RenderedPageJSONParser renderedPageJSONParser =
			new RenderedPageJSONParser();

		return renderedPageJSONParser.parseToDTOs(json);
	}

	public static String toJSON(RenderedPage renderedPage) {
		if (renderedPage == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (renderedPage.getMasterPageId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"masterPageId\": ");

			sb.append("\"");

			sb.append(_escape(renderedPage.getMasterPageId()));

			sb.append("\"");
		}

		if (renderedPage.getMasterPageName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"masterPageName\": ");

			sb.append("\"");

			sb.append(_escape(renderedPage.getMasterPageName()));

			sb.append("\"");
		}

		if (renderedPage.getPageTemplateId() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageTemplateId\": ");

			sb.append("\"");

			sb.append(_escape(renderedPage.getPageTemplateId()));

			sb.append("\"");
		}

		if (renderedPage.getPageTemplateName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"pageTemplateName\": ");

			sb.append("\"");

			sb.append(_escape(renderedPage.getPageTemplateName()));

			sb.append("\"");
		}

		if (renderedPage.getRenderedPageURL() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"renderedPageURL\": ");

			sb.append("\"");

			sb.append(_escape(renderedPage.getRenderedPageURL()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		RenderedPageJSONParser renderedPageJSONParser =
			new RenderedPageJSONParser();

		return renderedPageJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(RenderedPage renderedPage) {
		if (renderedPage == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (renderedPage.getMasterPageId() == null) {
			map.put("masterPageId", null);
		}
		else {
			map.put(
				"masterPageId", String.valueOf(renderedPage.getMasterPageId()));
		}

		if (renderedPage.getMasterPageName() == null) {
			map.put("masterPageName", null);
		}
		else {
			map.put(
				"masterPageName",
				String.valueOf(renderedPage.getMasterPageName()));
		}

		if (renderedPage.getPageTemplateId() == null) {
			map.put("pageTemplateId", null);
		}
		else {
			map.put(
				"pageTemplateId",
				String.valueOf(renderedPage.getPageTemplateId()));
		}

		if (renderedPage.getPageTemplateName() == null) {
			map.put("pageTemplateName", null);
		}
		else {
			map.put(
				"pageTemplateName",
				String.valueOf(renderedPage.getPageTemplateName()));
		}

		if (renderedPage.getRenderedPageURL() == null) {
			map.put("renderedPageURL", null);
		}
		else {
			map.put(
				"renderedPageURL",
				String.valueOf(renderedPage.getRenderedPageURL()));
		}

		return map;
	}

	public static class RenderedPageJSONParser
		extends BaseJSONParser<RenderedPage> {

		@Override
		protected RenderedPage createDTO() {
			return new RenderedPage();
		}

		@Override
		protected RenderedPage[] createDTOArray(int size) {
			return new RenderedPage[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "masterPageId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "masterPageName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageTemplateId")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "pageTemplateName")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "renderedPageURL")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			RenderedPage renderedPage, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "masterPageId")) {
				if (jsonParserFieldValue != null) {
					renderedPage.setMasterPageId((String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "masterPageName")) {
				if (jsonParserFieldValue != null) {
					renderedPage.setMasterPageName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageTemplateId")) {
				if (jsonParserFieldValue != null) {
					renderedPage.setPageTemplateId(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "pageTemplateName")) {
				if (jsonParserFieldValue != null) {
					renderedPage.setPageTemplateName(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "renderedPageURL")) {
				if (jsonParserFieldValue != null) {
					renderedPage.setRenderedPageURL(
						(String)jsonParserFieldValue);
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