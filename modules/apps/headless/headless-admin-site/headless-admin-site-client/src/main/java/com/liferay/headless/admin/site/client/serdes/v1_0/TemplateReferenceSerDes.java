/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.TemplateReference;
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
public class TemplateReferenceSerDes {

	public static TemplateReference toDTO(String json) {
		TemplateReferenceJSONParser templateReferenceJSONParser =
			new TemplateReferenceJSONParser();

		return templateReferenceJSONParser.parseToDTO(json);
	}

	public static TemplateReference[] toDTOs(String json) {
		TemplateReferenceJSONParser templateReferenceJSONParser =
			new TemplateReferenceJSONParser();

		return templateReferenceJSONParser.parseToDTOs(json);
	}

	public static String toJSON(TemplateReference templateReference) {
		if (templateReference == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (templateReference.getRendererKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"rendererKey\": ");

			sb.append("\"");

			sb.append(_escape(templateReference.getRendererKey()));

			sb.append("\"");
		}

		if (templateReference.getTemplateKey() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"templateKey\": ");

			sb.append("\"");

			sb.append(_escape(templateReference.getTemplateKey()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		TemplateReferenceJSONParser templateReferenceJSONParser =
			new TemplateReferenceJSONParser();

		return templateReferenceJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		TemplateReference templateReference) {

		if (templateReference == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (templateReference.getRendererKey() == null) {
			map.put("rendererKey", null);
		}
		else {
			map.put(
				"rendererKey",
				String.valueOf(templateReference.getRendererKey()));
		}

		if (templateReference.getTemplateKey() == null) {
			map.put("templateKey", null);
		}
		else {
			map.put(
				"templateKey",
				String.valueOf(templateReference.getTemplateKey()));
		}

		return map;
	}

	public static class TemplateReferenceJSONParser
		extends BaseJSONParser<TemplateReference> {

		@Override
		protected TemplateReference createDTO() {
			return new TemplateReference();
		}

		@Override
		protected TemplateReference[] createDTOArray(int size) {
			return new TemplateReference[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(jsonParserFieldName, "rendererKey")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "templateKey")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			TemplateReference templateReference, String jsonParserFieldName,
			Object jsonParserFieldValue) {

			if (Objects.equals(jsonParserFieldName, "rendererKey")) {
				if (jsonParserFieldValue != null) {
					templateReference.setRendererKey(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "templateKey")) {
				if (jsonParserFieldValue != null) {
					templateReference.setTemplateKey(
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
// LIFERAY-REST-BUILDER-HASH:641962354