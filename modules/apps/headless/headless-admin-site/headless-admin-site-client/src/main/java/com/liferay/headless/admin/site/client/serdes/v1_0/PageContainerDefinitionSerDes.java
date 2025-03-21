/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.client.serdes.v1_0;

import com.liferay.headless.admin.site.client.dto.v1_0.CustomCSSViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.FragmentViewport;
import com.liferay.headless.admin.site.client.dto.v1_0.PageContainerDefinition;
import com.liferay.headless.admin.site.client.json.BaseJSONParser;

import jakarta.annotation.Generated;

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
public class PageContainerDefinitionSerDes {

	public static PageContainerDefinition toDTO(String json) {
		PageContainerDefinitionJSONParser pageContainerDefinitionJSONParser =
			new PageContainerDefinitionJSONParser();

		return pageContainerDefinitionJSONParser.parseToDTO(json);
	}

	public static PageContainerDefinition[] toDTOs(String json) {
		PageContainerDefinitionJSONParser pageContainerDefinitionJSONParser =
			new PageContainerDefinitionJSONParser();

		return pageContainerDefinitionJSONParser.parseToDTOs(json);
	}

	public static String toJSON(
		PageContainerDefinition pageContainerDefinition) {

		if (pageContainerDefinition == null) {
			return "null";
		}

		StringBuilder sb = new StringBuilder();

		sb.append("{");

		if (pageContainerDefinition.getBackgroundFragmentImage() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"backgroundFragmentImage\": ");

			sb.append(
				String.valueOf(
					pageContainerDefinition.getBackgroundFragmentImage()));
		}

		if (pageContainerDefinition.getContentVisibility() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentVisibility\": ");

			sb.append("\"");

			sb.append(_escape(pageContainerDefinition.getContentVisibility()));

			sb.append("\"");
		}

		if (pageContainerDefinition.getCssClasses() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"cssClasses\": ");

			sb.append("[");

			for (int i = 0; i < pageContainerDefinition.getCssClasses().length;
				 i++) {

				sb.append(_toJSON(pageContainerDefinition.getCssClasses()[i]));

				if ((i + 1) < pageContainerDefinition.getCssClasses().length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageContainerDefinition.getCustomCSS() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSS\": ");

			sb.append("\"");

			sb.append(_escape(pageContainerDefinition.getCustomCSS()));

			sb.append("\"");
		}

		if (pageContainerDefinition.getCustomCSSViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customCSSViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < pageContainerDefinition.getCustomCSSViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						pageContainerDefinition.getCustomCSSViewports()[i]));

				if ((i + 1) <
						pageContainerDefinition.
							getCustomCSSViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageContainerDefinition.getFragmentLink() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentLink\": ");

			sb.append(
				String.valueOf(pageContainerDefinition.getFragmentLink()));
		}

		if (pageContainerDefinition.getFragmentStyle() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentStyle\": ");

			sb.append(
				String.valueOf(pageContainerDefinition.getFragmentStyle()));
		}

		if (pageContainerDefinition.getFragmentViewports() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fragmentViewports\": ");

			sb.append("[");

			for (int i = 0;
				 i < pageContainerDefinition.getFragmentViewports().length;
				 i++) {

				sb.append(
					String.valueOf(
						pageContainerDefinition.getFragmentViewports()[i]));

				if ((i + 1) <
						pageContainerDefinition.getFragmentViewports().length) {

					sb.append(", ");
				}
			}

			sb.append("]");
		}

		if (pageContainerDefinition.getHtmlProperties() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"htmlProperties\": ");

			sb.append(
				String.valueOf(pageContainerDefinition.getHtmlProperties()));
		}

		if (pageContainerDefinition.getIndexed() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexed\": ");

			sb.append(pageContainerDefinition.getIndexed());
		}

		if (pageContainerDefinition.getLayout() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"layout\": ");

			sb.append(String.valueOf(pageContainerDefinition.getLayout()));
		}

		if (pageContainerDefinition.getName() != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(pageContainerDefinition.getName()));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	public static Map<String, Object> toMap(String json) {
		PageContainerDefinitionJSONParser pageContainerDefinitionJSONParser =
			new PageContainerDefinitionJSONParser();

		return pageContainerDefinitionJSONParser.parseToMap(json);
	}

	public static Map<String, String> toMap(
		PageContainerDefinition pageContainerDefinition) {

		if (pageContainerDefinition == null) {
			return null;
		}

		Map<String, String> map = new TreeMap<>();

		if (pageContainerDefinition.getBackgroundFragmentImage() == null) {
			map.put("backgroundFragmentImage", null);
		}
		else {
			map.put(
				"backgroundFragmentImage",
				String.valueOf(
					pageContainerDefinition.getBackgroundFragmentImage()));
		}

		if (pageContainerDefinition.getContentVisibility() == null) {
			map.put("contentVisibility", null);
		}
		else {
			map.put(
				"contentVisibility",
				String.valueOf(pageContainerDefinition.getContentVisibility()));
		}

		if (pageContainerDefinition.getCssClasses() == null) {
			map.put("cssClasses", null);
		}
		else {
			map.put(
				"cssClasses",
				String.valueOf(pageContainerDefinition.getCssClasses()));
		}

		if (pageContainerDefinition.getCustomCSS() == null) {
			map.put("customCSS", null);
		}
		else {
			map.put(
				"customCSS",
				String.valueOf(pageContainerDefinition.getCustomCSS()));
		}

		if (pageContainerDefinition.getCustomCSSViewports() == null) {
			map.put("customCSSViewports", null);
		}
		else {
			map.put(
				"customCSSViewports",
				String.valueOf(
					pageContainerDefinition.getCustomCSSViewports()));
		}

		if (pageContainerDefinition.getFragmentLink() == null) {
			map.put("fragmentLink", null);
		}
		else {
			map.put(
				"fragmentLink",
				String.valueOf(pageContainerDefinition.getFragmentLink()));
		}

		if (pageContainerDefinition.getFragmentStyle() == null) {
			map.put("fragmentStyle", null);
		}
		else {
			map.put(
				"fragmentStyle",
				String.valueOf(pageContainerDefinition.getFragmentStyle()));
		}

		if (pageContainerDefinition.getFragmentViewports() == null) {
			map.put("fragmentViewports", null);
		}
		else {
			map.put(
				"fragmentViewports",
				String.valueOf(pageContainerDefinition.getFragmentViewports()));
		}

		if (pageContainerDefinition.getHtmlProperties() == null) {
			map.put("htmlProperties", null);
		}
		else {
			map.put(
				"htmlProperties",
				String.valueOf(pageContainerDefinition.getHtmlProperties()));
		}

		if (pageContainerDefinition.getIndexed() == null) {
			map.put("indexed", null);
		}
		else {
			map.put(
				"indexed",
				String.valueOf(pageContainerDefinition.getIndexed()));
		}

		if (pageContainerDefinition.getLayout() == null) {
			map.put("layout", null);
		}
		else {
			map.put(
				"layout", String.valueOf(pageContainerDefinition.getLayout()));
		}

		if (pageContainerDefinition.getName() == null) {
			map.put("name", null);
		}
		else {
			map.put("name", String.valueOf(pageContainerDefinition.getName()));
		}

		return map;
	}

	public static class PageContainerDefinitionJSONParser
		extends BaseJSONParser<PageContainerDefinition> {

		@Override
		protected PageContainerDefinition createDTO() {
			return new PageContainerDefinition();
		}

		@Override
		protected PageContainerDefinition[] createDTOArray(int size) {
			return new PageContainerDefinition[size];
		}

		@Override
		protected boolean parseMaps(String jsonParserFieldName) {
			if (Objects.equals(
					jsonParserFieldName, "backgroundFragmentImage")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "contentVisibility")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "customCSS")) {
				return false;
			}
			else if (Objects.equals(
						jsonParserFieldName, "customCSSViewports")) {

				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentLink")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentViewports")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "htmlProperties")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "layout")) {
				return false;
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				return false;
			}

			return false;
		}

		@Override
		protected void setField(
			PageContainerDefinition pageContainerDefinition,
			String jsonParserFieldName, Object jsonParserFieldValue) {

			if (Objects.equals(
					jsonParserFieldName, "backgroundFragmentImage")) {

				if (jsonParserFieldValue != null) {
					pageContainerDefinition.setBackgroundFragmentImage(
						FragmentImageSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "contentVisibility")) {
				if (jsonParserFieldValue != null) {
					pageContainerDefinition.setContentVisibility(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "cssClasses")) {
				if (jsonParserFieldValue != null) {
					pageContainerDefinition.setCssClasses(
						toStrings((Object[])jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "customCSS")) {
				if (jsonParserFieldValue != null) {
					pageContainerDefinition.setCustomCSS(
						(String)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(
						jsonParserFieldName, "customCSSViewports")) {

				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					CustomCSSViewport[] customCSSViewportsArray =
						new CustomCSSViewport[jsonParserFieldValues.length];

					for (int i = 0; i < customCSSViewportsArray.length; i++) {
						customCSSViewportsArray[i] =
							CustomCSSViewportSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					pageContainerDefinition.setCustomCSSViewports(
						customCSSViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentLink")) {
				if (jsonParserFieldValue != null) {
					pageContainerDefinition.setFragmentLink(
						FragmentLinkSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentStyle")) {
				if (jsonParserFieldValue != null) {
					pageContainerDefinition.setFragmentStyle(
						FragmentStyleSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "fragmentViewports")) {
				if (jsonParserFieldValue != null) {
					Object[] jsonParserFieldValues =
						(Object[])jsonParserFieldValue;

					FragmentViewport[] fragmentViewportsArray =
						new FragmentViewport[jsonParserFieldValues.length];

					for (int i = 0; i < fragmentViewportsArray.length; i++) {
						fragmentViewportsArray[i] =
							FragmentViewportSerDes.toDTO(
								(String)jsonParserFieldValues[i]);
					}

					pageContainerDefinition.setFragmentViewports(
						fragmentViewportsArray);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "htmlProperties")) {
				if (jsonParserFieldValue != null) {
					pageContainerDefinition.setHtmlProperties(
						HtmlPropertiesSerDes.toDTO(
							(String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "indexed")) {
				if (jsonParserFieldValue != null) {
					pageContainerDefinition.setIndexed(
						(Boolean)jsonParserFieldValue);
				}
			}
			else if (Objects.equals(jsonParserFieldName, "layout")) {
				if (jsonParserFieldValue != null) {
					pageContainerDefinition.setLayout(
						LayoutSerDes.toDTO((String)jsonParserFieldValue));
				}
			}
			else if (Objects.equals(jsonParserFieldName, "name")) {
				if (jsonParserFieldValue != null) {
					pageContainerDefinition.setName(
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